<!-- src/views/bulletin/components/image/ImageItem.vue -->
<template>
  <div class="image-item" :class="{ 'main-image': isMain }">
    <FallbackImage
      :src="imageUrl"
      :alt="`Изображение`"
      fallback-text="Не удалось загрузить"
      @error="$emit('error')"
    />
    
    <ImageActions
      :show-set-main="showSetMain"
      :is-main="isMain"
      :total-images="totalImages"
      @set-main="$emit('set-main')"
      @delete="$emit('delete')"
    />
    
    <div v-if="isMain" class="main-badge">Главное</div>
  </div>
</template>

<script setup>
import FallbackImage from '@/components/image/FallbackImage.vue'
import ImageActions from './ImageActions.vue'

defineProps({
  imageUrl: {
    type: String,
    default: null
  },
  isMain: {
    type: Boolean,
    default: false
  },
  showSetMain: {
    type: Boolean,
    default: true
  },
  totalImages: {
    type: Number,
    default: 0
  }
})

defineEmits(['set-main', 'delete', 'error'])
</script>

<style scoped>
.image-item {
  position: relative;
  aspect-ratio: 1;
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid #e2e8f0;
  transition: all 0.2s;
  background: #f7fafc;
}

.image-item.main-image {
  border-color: #f59e0b;
  box-shadow: 0 0 0 2px #fef3c7;
}

.image-item:hover .image-actions {
  opacity: 1;
}

.main-badge {
  position: absolute;
  bottom: 0.25rem;
  left: 0.25rem;
  background: #f59e0b;
  color: white;
  font-size: 0.7rem;
  padding: 0.125rem 0.375rem;
  border-radius: 4px;
  z-index: 10;
}
</style>