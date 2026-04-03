<!-- src/views/chat/components/ChatImageUpload.vue -->
<template>
  <div class="chat-image-upload">
    <button 
      type="button"
      class="upload-btn"
      :class="{ uploading }"
      :disabled="uploading"
      @click="triggerFileSelect"
      title="Отправить изображение"
    >
      <span v-if="!uploading">📷</span>
      <div v-else class="spinner-small"></div>
    </button>
    
    <input
      ref="fileInput"
      type="file"
      accept="image/jpeg,image/jpg,image/png,image/gif,image/webp"
      class="file-input"
      @change="handleFileSelect"
    />
    
    <!-- Модальное окно предпросмотра -->
    <div v-if="previewImage" class="preview-modal" @click="closePreview">
      <div class="preview-content" @click.stop>
        <img :src="previewImage" alt="Preview" />
        <div class="preview-actions">
          <button @click="confirmSend" class="send-btn">📤 Отправить</button>
          <button @click="closePreview" class="cancel-btn">❌ Отмена</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useMinio } from '@/composables/useMinio'

const props = defineProps({
  chatId: {
    type: String,
    required: true
  }
})

const emit = defineEmits(['send-image'])

const { uploadFile, uploading, uploadProgress, uploadError } = useMinio()
const fileInput = ref(null)
const previewImage = ref(null)
const selectedFile = ref(null)

const triggerFileSelect = () => {
  if (!uploading.value) {
    fileInput.value?.click()
  }
}

const handleFileSelect = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  const validation = validateFile(file)
  if (!validation.valid) {
    alert(validation.error)
    return
  }
  
  selectedFile.value = file
  previewImage.value = URL.createObjectURL(file)
  
  // Очищаем input
  fileInput.value.value = ''
}

const closePreview = () => {
  if (previewImage.value) {
    URL.revokeObjectURL(previewImage.value)
    previewImage.value = null
  }
  selectedFile.value = null
}

const confirmSend = async () => {
  if (!selectedFile.value) return
  
  try {
    // Загружаем изображение в MinIO
    const uploadedFile = await uploadFile(selectedFile.value)
    const imageId = uploadedFile.id
    
    // Отправляем image сообщение в чат
    emit('send-image', imageId)
    
    // Закрываем превью
    closePreview()
    
  } catch (err) {
    console.error('Error uploading image:', err)
    alert('Не удалось загрузить изображение')
  }
}

const validateFile = (file) => {
  const maxSize = 10 * 1024 * 1024 // 10MB
  const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp']
  
  if (!allowedTypes.includes(file.type)) {
    return { valid: false, error: 'Недопустимый формат. Разрешены: JPEG, PNG, GIF, WEBP' }
  }
  
  if (file.size > maxSize) {
    return { valid: false, error: 'Максимальный размер файла: 10MB' }
  }
  
  return { valid: true, error: null }
}
</script>

<style scoped>
.chat-image-upload {
  display: inline-block;
}

.upload-btn {
  width: 40px;
  height: 40px;
  border: none;
  background: transparent;
  border-radius: 50%;
  cursor: pointer;
  font-size: 1.25rem;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  color: #667eea;
}

.upload-btn:hover:not(:disabled) {
  background: #edf2f7;
  transform: scale(1.05);
}

.upload-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.upload-btn.uploading {
  cursor: wait;
}

.file-input {
  display: none;
}

.preview-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  z-index: 10000;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.preview-content {
  max-width: 90vw;
  max-height: 90vh;
  background: white;
  border-radius: 12px;
  padding: 1rem;
  cursor: default;
}

.preview-content img {
  max-width: calc(90vw - 2rem);
  max-height: calc(90vh - 100px);
  object-fit: contain;
  border-radius: 8px;
}

.preview-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  margin-top: 1rem;
}

.send-btn, .cancel-btn {
  padding: 0.5rem 1.5rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.2s;
}

.send-btn {
  background: #667eea;
  color: white;
}

.send-btn:hover {
  background: #5a67d8;
  transform: scale(1.02);
}

.cancel-btn {
  background: #e2e8f0;
  color: #4a5568;
}

.cancel-btn:hover {
  background: #cbd5e0;
}

.spinner-small {
  width: 16px;
  height: 16px;
  border: 2px solid #e2e8f0;
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>