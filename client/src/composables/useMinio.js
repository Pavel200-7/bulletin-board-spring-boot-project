// src/composables/useMinio.js
import { ref } from 'vue'
import { minioService } from '@/services/minio/minioService'

export function useMinio() {
  const uploading = ref(false)
  const uploadProgress = ref(0)
  const uploadError = ref(null)
  const files = ref([])

  /**
   * Загрузить файл в MinIO
   * @param {File} file - файл для загрузки
   * @param {string} objectId - UUID для файла (опционально, если не указан - генерируется)
   * @returns {Promise<{id: string, url: string, size: number, type: string}>}
   */
  const uploadFile = async (file, objectId = null) => {
    // Валидация
    const validation = minioService.validateFile(file)
    if (!validation.valid) {
      uploadError.value = validation.error
      throw new Error(validation.error)
    }

    uploading.value = true
    uploadProgress.value = 0
    uploadError.value = null

    try {
      // Генерируем ID если не указан
      const fileId = objectId || minioService.generateFileId()
      
      // Имитация прогресса
      const interval = setInterval(() => {
        if (uploadProgress.value < 90) {
          uploadProgress.value += 10
        }
      }, 100)
      
      // Загружаем в MinIO
      const uploadedFile = await minioService.uploadFile(file, fileId)

      clearInterval(interval)
      uploadProgress.value = 100
      
      // Добавляем в локальный список
      const newFile = {
        id: fileId,
        url: uploadedFile.url,
        size: uploadedFile.size,
        type: uploadedFile.type,
        name: uploadedFile.name
      }
      
      files.value.push(newFile)
  
      return newFile
    } catch (err) {
      uploadError.value = err.response?.data?.message || err.message
      throw err
    } finally {
      uploading.value = false
      setTimeout(() => {
        uploadProgress.value = 0
      }, 1000)
    }
  }

  /**
   * Удалить файл из MinIO
   * @param {string} objectId - ID файла
   */
  const deleteFile = async (objectId) => {
    try {
      await minioService.deleteFile(objectId)
      files.value = files.value.filter(f => f.id !== objectId)
      return true
    } catch (err) {
      console.error('Ошибка удаления файла:', err)
      throw err
    }
  }

  /**
   * Получить URL файла
   * @param {string} objectId - ID файла
   */
  const getFileUrl = (objectId) => {
    return minioService.getFileUrl(objectId)
  }

  /**
   * Получить метаданные файла
   * @param {string} objectId - ID файла
   */
  const getFileMetadata = async (objectId) => {
    return minioService.getFileMetadata(objectId)
  }

  /**
   * Проверить существование файла
   * @param {string} objectId - ID файла
   */
  const fileExists = async (objectId) => {
    return minioService.fileExists(objectId)
  }

  /**
   * Сгенерировать новый ID для файла
   */
  const generateFileId = () => {
    return minioService.generateFileId()
  }

  /**
   * Установить список файлов (для инициализации)
   * @param {Array} fileList - список файлов
   */
  const setFiles = (fileList) => {
    files.value = fileList.map(f => ({
      id: f.id,
      url: f.url || minioService.getFileUrl(f.id),
      main: f.main || false
    }))
  }

  /**
   * Очистить список файлов
   */
  const clearFiles = () => {
    files.value = []
    uploadError.value = null
  }

  return {
    // state
    files,
    uploading,
    uploadProgress,
    uploadError,
    
    // actions
    uploadFile,
    deleteFile,
    getFileUrl,
    getFileMetadata,
    fileExists,
    generateFileId,
    setFiles,
    clearFiles
  }
}