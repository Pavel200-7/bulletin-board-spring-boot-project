// src/services/minio/minioService.js
import minioClient from '@/utils/minioClient'

const BUCKET = import.meta.env.VITE_MINIO_BUCKET || 'bulletins'
const MINIO_URL = import.meta.env.VITE_MINIO_URL || 'http://localhost:9001'

export const minioService = {
  /**
   * Загрузить файл в MinIO
   * @param {File} file - файл для загрузки
   * @param {string} objectId - UUID для файла
   */
  async uploadFile(file, objectId) {
    const response = await minioClient.put(`/${objectId}`, file, {
      headers: {
        'Content-Type': file.type,
        'Content-Disposition': `inline; filename="${objectId}"`
      }
    })
    
    return {
      id: objectId,
      url: `${MINIO_URL}/${BUCKET}/${objectId}`,
      size: file.size,
      type: file.type,
      name: file.name
    }
  },

  /**
   * Получить URL файла по ID
   */
  getFileUrl(objectId) {
    return `${MINIO_URL}/${BUCKET}/${objectId}`
  },

  /**
   * Удалить файл из MinIO
   */
  async deleteFile(objectId) {
    return minioClient.delete(`/${objectId}`)
  },

  /**
   * Получить метаданные файла
   */
  async getFileMetadata(objectId) {
    const response = await minioClient.head(`/${objectId}`)
    return {
      id: objectId,
      url: this.getFileUrl(objectId),
      contentType: response.headers['content-type'],
      contentLength: response.headers['content-length'],
      lastModified: response.headers['last-modified']
    }
  },

  /**
   * Проверить существование файла
   */
  async fileExists(objectId) {
    try {
      await minioClient.head(`/${objectId}`)
      return true
    } catch {
      return false
    }
  },

  /**
   * Генерирует UUID для нового файла
   */
  generateFileId() {
    return crypto.randomUUID ? crypto.randomUUID() : 
      'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        const r = Math.random() * 16 | 0
        const v = c === 'x' ? r : (r & 0x3 | 0x8)
        return v.toString(16)
      })
  },

  /**
   * Проверка валидности файла
   */
  validateFile(file, options = {}) {
    const {
      maxSize = 5 * 1024 * 1024, // 5MB
      allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp']
    } = options

    if (!allowedTypes.includes(file.type)) {
      return { 
        valid: false, 
        error: `Недопустимый формат. Разрешены: ${allowedTypes.map(t => t.split('/')[1]).join(', ')}` 
      }
    }
    
    if (file.size > maxSize) {
      return { 
        valid: false, 
        error: `Максимальный размер файла: ${maxSize / 1024 / 1024}MB` 
      }
    }
    
    return { valid: true, error: null }
  }
}