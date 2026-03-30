<template>
  <div class="profile-avatar">
    <div class="avatar-container" @click="triggerFileSelect">
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
      
      <button 
        v-if="imageUrl && !readonly" 
        class="delete-btn" 
        @click.stop="handleDelete"
        title="Удалить фото"
      >
        ✕
      </button>
      <div v-if="!readonly" class="upload-overlay">
        <span>✏️</span>
      </div>
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
    </div>
    
    <div v-if="uploadError" class="upload-error">
      ⚠️ {{ uploadError }}
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useMinio } from '@/composables/useMinio'

const props = defineProps({
  imageId: {
    type: String,
    default: null
  },
  readonly: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['upload', 'delete'])

const { uploadFile, deleteFile, getFileUrl, uploading, uploadProgress, uploadError } = useMinio()

const fileInput = ref(null)
const previewUrl = ref(null)
const currentImageId = ref(props.imageId)

const imageUrl = computed(() => {
  if (previewUrl.value) return previewUrl.value
  if (currentImageId.value) return getFileUrl(currentImageId.value)
  return null
})

watch(() => props.imageId, (newId) => {
  if (newId !== currentImageId.value) {
    currentImageId.value = newId
    previewUrl.value = null
  }
})

const triggerFileSelect = () => {
  if (!props.readonly && !uploading.value) {
    fileInput.value?.click()
  }
}

const handleFileSelect = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  const validation = validateFile(file)
  if (!validation.valid) {
    uploadError.value = validation.error
    return
  }
  
  try {
    previewUrl.value = URL.createObjectURL(file)
    const uploadedFile = await uploadFile(file)
    const newImageId = uploadedFile.id
    
    currentImageId.value = newImageId
    emit('upload', newImageId)
    
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

const handleDelete = async () => {
  if (!currentImageId.value) return
  
  try {
    await deleteFile(currentImageId.value)
    currentImageId.value = null
    emit('delete')
  } catch (err) {
    console.error('Ошибка удаления:', err)
  }
}

const validateFile = (file) => {
  const maxSize = 5 * 1024 * 1024
  const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp']
  
  if (!allowedTypes.includes(file.type)) {
    return { valid: false, error: 'Недопустимый формат. Разрешены: JPEG, PNG, GIF, WEBP' }
  }
  
  if (file.size > maxSize) {
    return { valid: false, error: 'Максимальный размер файла: 5MB' }
  }
  
  return { valid: true, error: null }
}
</script>

<style scoped>
.profile-avatar {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
}

.avatar-container {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  background: #f7fafc;
  border: 2px solid #e2e8f0;
  transition: all 0.2s;
}

.avatar-container:hover {
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

.avatar-container:hover .delete-btn {
  opacity: 1;
}

.delete-btn:hover {
  background: #e53e3e;
}

.upload-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  text-align: center;
  padding: 0.25rem;
  font-size: 0.75rem;
  opacity: 0;
  transition: opacity 0.2s;
}

.avatar-container:hover .upload-overlay {
  opacity: 1;
}

.file-input {
  display: none;
}

.upload-progress {
  width: 120px;
  height: 4px;
  background: #edf2f7;
  border-radius: 2px;
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  background: #48bb78;
  transition: width 0.3s;
}

.upload-error {
  font-size: 0.7rem;
  color: #e53e3e;
  text-align: center;
  max-width: 150px;
}
</style>