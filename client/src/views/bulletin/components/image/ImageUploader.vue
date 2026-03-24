<!-- src/views/bulletin/components/image/ImageUploader.vue -->
<template>
  <div class="image-uploader">
    <label class="upload-label">Изображения</label>
    
    <UploadArea @files-selected="handleFilesSelected" />
    
    <ProgressBar v-if="uploading" :progress="uploadProgress" />
    
    <div v-if="uploadError" class="upload-error">
      ⚠️ {{ uploadError }}
    </div>
    
    <ImageGrid
      :images="localFiles"
      :get-image-url="getImageUrl"
      @set-main="setAsMain"
      @delete="deleteImage"
      @error="handleImageError"
    />
    
    <div v-if="localFiles.length === 0 && !uploading" class="image-hint">
      <small>Добавьте хотя бы одно изображение для публикации</small>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue'
import { useMinio } from '@/composables/useMinio'
import UploadArea from './UploadArea.vue'
import ProgressBar from './ProgressBar.vue'
import ImageGrid from './ImageGrid.vue'

const props = defineProps({
  existingFiles: {
    type: Array,
    default: () => []
  },
  bulletinId: {
    type: String,
    default: null
  }
})

const emit = defineEmits(['upload', 'delete', 'set-main', 'files-updated'])

const { uploadFile, deleteFile, getFileUrl, uploading, uploadProgress, uploadError } = useMinio()

const localFiles = ref([])
const isInitializing = ref(true)

const getImageUrl = (image) => {
  if (image.isNew && image.previewUrl) {
    return image.previewUrl
  }
  if (image.minioId) {
    return getFileUrl(image.minioId)
  }
  return null
}

// Инициализация из props
watch(() => props.existingFiles, (newFiles) => {
  if (newFiles && newFiles.length) {
    localFiles.value = newFiles.map(file => ({
      bulletinImageId: file.id,
      minioId: file.minioId || file.id,
      main: file.main || false,
      isNew: false
    }))
  } else if (!isInitializing.value) {
    localFiles.value = []
  }
}, { immediate: true })

// Отправляем изменения наружу
watch(localFiles, (newFiles) => {
  if (!isInitializing.value) {
    emit('files-updated', newFiles.map(f => ({
      id: f.bulletinImageId,
      minioId: f.minioId,
      main: f.main
    })))
  }
}, { deep: true })

nextTick(() => {
  isInitializing.value = false
})

const handleImageError = (image) => {
  console.warn('Image load error:', image.minioId)
}

const setAsMain = (image) => {
  localFiles.value = localFiles.value.map(img => ({
    ...img,
    main: img.minioId === image.minioId
  }))
  
  if (image.bulletinImageId) {
    emit('set-main', { imageId: image.bulletinImageId, bulletinId: props.bulletinId })
  }
}

const deleteImage = async (image) => {
  try {
    if (image.minioId && !image.previewUrl) {
      await deleteFile(image.minioId)
    }
    
    localFiles.value = localFiles.value.filter(img => img.minioId !== image.minioId)
    
    if (image.bulletinImageId) {
      emit('delete', { 
        fileId: image.bulletinImageId,
        bulletinId: props.bulletinId 
      })
    }
  } catch (err) {
    console.error('Error deleting image:', err)
  }
}

const addImageToLocal = (imageData, previewUrl = null) => {
  const newImage = {
    bulletinImageId: null,
    minioId: imageData.id,
    main: localFiles.value.length === 0,
    isNew: true,
    previewUrl: previewUrl
  }
  localFiles.value = [...localFiles.value, newImage]
  
  setTimeout(() => {
    const idx = localFiles.value.findIndex(i => i.minioId === newImage.minioId)
    if (idx !== -1 && localFiles.value[idx].previewUrl) {
      URL.revokeObjectURL(localFiles.value[idx].previewUrl)
      localFiles.value[idx].previewUrl = null
    }
  }, 1000)
  
  return newImage
}

const handleFilesSelected = async (files) => {
  for (const file of files) {
    try {
      const previewUrl = URL.createObjectURL(file)
      const uploadedFile = await uploadFile(file)
      const newImage = addImageToLocal(uploadedFile, previewUrl)
      
      emit('upload', {
        fileId: newImage.minioId,
        bulletinId: props.bulletinId
      })
    } catch (err) {
      console.error('Upload error:', err)
    }
  }
}
</script>

<style scoped>
.image-uploader {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.upload-label {
  font-weight: 500;
  color: #4a5568;
}

.upload-error {
  padding: 0.5rem;
  background: #fed7d7;
  color: #c53030;
  border-radius: 4px;
  font-size: 0.875rem;
}

.image-hint {
  text-align: center;
  color: #a0aec0;
  font-size: 0.75rem;
  padding: 0.5rem;
}
</style>