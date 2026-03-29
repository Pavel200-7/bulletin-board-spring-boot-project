<!-- src/views/trade/components/image/AvatarUploader.vue -->
<template>
  <div class="avatar-uploader">
    <label class="upload-label">Аватар</label>
    
    <!-- Превью изображения -->
    <div class="avatar-preview" @click="triggerFileSelect">
      <img 
        v-if="imageUrl" 
        :src="imageUrl" 
        alt="Аватар"
        class="avatar-image"
      />
      <div v-else class="avatar-placeholder">
        <span class="placeholder-icon">📸</span>
        <span class="placeholder-text">Загрузить фото</span>
      </div>
      
      <!-- Кнопка удаления -->
      <button 
        v-if="imageUrl" 
        class="delete-btn" 
        @click.stop="handleDelete"
        title="Удалить фото"
      >
        ✕
      </button>
    </div>
    
    <input
      ref="fileInput"
      type="file"
      accept="image/jpeg,image/jpg,image/png,image/gif,image/webp"
      class="file-input"
      @change="handleFileSelect"
    />
    
    <div v-if="uploading" class="upload-progress">
      <div class="progress-bar" :style="{ width: uploadProgress + '%' }"></div>
      <span>{{ uploadProgress }}%</span>
    </div>
    
    <div v-if="uploadError" class="upload-error">
      ⚠️ {{ uploadError }}
    </div>
    <div class="upload-hint">
      <small>Рекомендуемый размер: 200x200px. Форматы: JPEG, PNG, GIF, WEBP (до 5MB)</small>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useMinio } from '@/composables/useMinio'

const props = defineProps({
  existingImageId: {
    type: String,
    default: null
  },
  accountId: {
    type: String,
    default: null
  }
})

const emit = defineEmits(['upload', 'delete', 'image-updated'])

const { uploadFile, deleteFile, getFileUrl, uploading, uploadProgress, uploadError } = useMinio()

const fileInput = ref(null)
const imageId = ref(props.existingImageId)
const previewUrl = ref(null)
const isDeleting = ref(false)

// Получаем URL изображения
const imageUrl = computed(() => {
  if (previewUrl.value) return previewUrl.value
  if (imageId.value) return getFileUrl(imageId.value)
  return null
})

// Следим за изменением existingImageId из пропсов
watch(() => props.existingImageId, (newId) => {
  if (newId && newId !== imageId.value) {
    imageId.value = newId
    previewUrl.value = null
  }
}, { immediate: true })

// Выбор файла
const triggerFileSelect = () => {
  if (!uploading.value && !isDeleting.value) {
    fileInput.value?.click()
  }
}

// Обработка выбора файла
const handleFileSelect = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  // Валидация
  const MINIO_URL = import.meta.env.VITE_MINIO_URL || 'http://localhost:9001'
  const BUCKET = import.meta.env.VITE_MINIO_BUCKET || 'bulletins'
  const validation = validateFile(file)
  if (!validation.valid) {
    uploadError.value = validation.error
    return
  }
  
  try {
    // Создаем локальное превью
    previewUrl.value = URL.createObjectURL(file)
    
    // Загружаем в MinIO
    const uploadedFile = await uploadFile(file)
    const newImageId = uploadedFile.id
    
    // Сохраняем ID
    imageId.value = newImageId
    
    // Отправляем событие родителю
    emit('upload', { imageId: newImageId, accountId: props.accountId })
    emit('image-updated', newImageId)
    
    // Убираем локальное превью через секунду
    setTimeout(() => {
      if (previewUrl.value) {
        URL.revokeObjectURL(previewUrl.value)
        previewUrl.value = null
      }
    }, 1000)
    
  } catch (err) {
    console.error('Ошибка загрузки:', err)
    previewUrl.value = null
  }
  
  fileInput.value.value = ''
}

// Удаление изображения
const handleDelete = async () => {
  if (!imageId.value) return
  
  if (confirm('Вы уверены, что хотите удалить фото?')) {
    isDeleting.value = true
    try {
      await deleteFile(imageId.value)
      
      const oldImageId = imageId.value
      imageId.value = null
      previewUrl.value = null
      
      emit('delete', { imageId: oldImageId, accountId: props.accountId })
      emit('image-updated', null)
      
    } catch (err) {
      console.error('Ошибка удаления:', err)
      alert('Не удалось удалить изображение')
    } finally {
      isDeleting.value = false
    }
  }
}

// Валидация файла
const validateFile = (file) => {
  const maxSize = 5 * 1024 * 1024 // 5MB
  const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp']
  
  if (!allowedTypes.includes(file.type)) {
    return { 
      valid: false, 
      error: `Недопустимый формат. Разрешены: JPEG, PNG, GIF, WEBP` 
    }
  }
  
  if (file.size > maxSize) {
    return { 
      valid: false, 
      error: `Максимальный размер файла: 5MB` 
    }
  }
  
  return { valid: true, error: null }
}
</script>

<style scoped>
.avatar-uploader {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.upload-label {
  font-weight: 500;
  color: #4a5568;
}

.avatar-preview {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  background: #f7fafc;
  border: 2px dashed #e2e8f0;
  transition: all 0.2s;
}

.avatar-preview:hover {
  border-color: #667eea;
  transform: scale(1.02);
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #a0aec0;
}

.placeholder-icon {
  font-size: 2rem;
  margin-bottom: 0.25rem;
}

.placeholder-text {
  font-size: 0.7rem;
}

.delete-btn {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 24px;
  height: 24px;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  border: none;
  border-radius: 50%;
  color: white;
  font-size: 0.75rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  opacity: 0;
}

.avatar-preview:hover .delete-btn {
  opacity: 1;
}

.delete-btn:hover {
  background: #e53e3e;
  transform: scale(1.05);
}

.file-input {
  display: none;
}

.upload-progress {
  position: relative;
  height: 20px;
  background: #edf2f7;
  border-radius: 10px;
  overflow: hidden;
  width: 120px;
}

.progress-bar {
  height: 100%;
  background: #48bb78;
  transition: width 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 0.7rem;
}

.upload-error {
  padding: 0.5rem;
  background: #fed7d7;
  color: #c53030;
  border-radius: 4px;
  font-size: 0.75rem;
  width: fit-content;
}

.upload-hint {
  color: #a0aec0;
  font-size: 0.7rem;
}
</style>