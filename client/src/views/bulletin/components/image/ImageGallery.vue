<!-- src/views/bulletin/components/image/ImageGallery.vue -->
<template>
  <div class="image-gallery" :class="`gallery-${size}`">
    <!-- Главное изображение -->
    <div class="main-image-container">
      <img 
        :src="currentImage" 
        :alt="title"
        class="main-image"
        @click="openLightbox"
      />
      <button 
        v-if="hasMultipleImages" 
        class="gallery-btn prev" 
        @click="prevImage"
        :disabled="currentIndex === 0"
      >
        ←
      </button>
      <button 
        v-if="hasMultipleImages" 
        class="gallery-btn next" 
        @click="nextImage"
        :disabled="currentIndex === images.length - 1"
      >
        →
      </button>
      <div class="image-counter" v-if="hasMultipleImages">
        {{ currentIndex + 1 }} / {{ images.length }}
      </div>
    </div>

    <!-- Миниатюры -->
    <div v-if="hasMultipleImages" class="thumbnails">
      <div 
        v-for="(image, index) in images" 
        :key="image.id"
        class="thumbnail"
        :class="{ active: currentIndex === index }"
        @click="currentIndex = index"
      >
        <img :src="getImageUrl(image)" :alt="`Миниатюра ${index + 1}`" />
      </div>
    </div>

    <!-- Лайтбокс -->
    <Lightbox
      v-if="lightboxOpen"
      :images="imageUrls"
      :initial-index="currentIndex"
      @close="closeLightbox"
    />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import Lightbox from './Lightbox.vue'

const props = defineProps({
  images: {
    type: Array,
    default: () => []
  },
  title: {
    type: String,
    default: 'Изображение'
  },
  size: {
    type: String,
    default: 'medium',
    validator: (value) => ['small', 'medium', 'large'].includes(value)
  }
})

const currentIndex = ref(0)
const lightboxOpen = ref(false)

const hasMultipleImages = computed(() => props.images.length > 1)

const getImageUrl = (image) => {
  if (!image) return '/images/image-placeholder.svg'
  const imageId = image.imageId || image.minioId || image.id
  if (!imageId) return '/images/image-placeholder.svg'
  const MINIO_URL = import.meta.env.VITE_MINIO_URL || 'http://localhost:9001'
  const BUCKET = import.meta.env.VITE_MINIO_BUCKET || 'bulletins'
  return `${MINIO_URL}/${BUCKET}/${imageId}`
}

const currentImage = computed(() => {
  if (!props.images.length) return '/images/image-placeholder.svg'
  const image = props.images[currentIndex.value]
  return getImageUrl(image)
})

const imageUrls = computed(() => {
  return props.images.map(img => getImageUrl(img))
})

const prevImage = () => {
  if (currentIndex.value > 0) {
    currentIndex.value--
  }
}

const nextImage = () => {
  if (currentIndex.value < props.images.length - 1) {
    currentIndex.value++
  }
}

const openLightbox = () => {
  if (props.images.length > 0) {
    lightboxOpen.value = true
  }
}

const closeLightbox = () => {
  lightboxOpen.value = false
}
</script>

<style scoped>
.image-gallery {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

/* Размеры галереи */
.gallery-small .main-image-container {
  max-width: 300px;
  margin: 0 auto;
}

.gallery-medium .main-image-container {
  max-width: 400px;
  margin: 0 auto;
}

.gallery-large .main-image-container {
  max-width: 500px;
  margin: 0 auto;
}

.main-image-container {
  position: relative;
  aspect-ratio: 1;
  background: #f8f9fa;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.main-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  transition: transform 0.2s;
}

.main-image:hover {
  transform: scale(1.02);
}

.gallery-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 40px;
  height: 40px;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  border: none;
  border-radius: 50%;
  color: white;
  font-size: 1.25rem;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.gallery-btn:hover:not(:disabled) {
  background: rgba(0, 0, 0, 0.7);
  transform: translateY(-50%) scale(1.05);
}

.gallery-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.prev {
  left: 1rem;
}

.next {
  right: 1rem;
}

.image-counter {
  position: absolute;
  bottom: 1rem;
  right: 1rem;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  padding: 0.25rem 0.5rem;
  border-radius: 20px;
  font-size: 0.75rem;
  color: white;
}

.thumbnails {
  display: flex;
  gap: 0.5rem;
  padding: 1rem;
  overflow-x: auto;
  background: white;
  border-top: 1px solid #e2e8f0;
}

.thumbnail {
  width: 70px;
  height: 70px;
  flex-shrink: 0;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.2s;
}

.thumbnail.active {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

.thumbnail:hover {
  transform: scale(1.05);
}

.thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumbnails::-webkit-scrollbar {
  height: 4px;
}

.thumbnails::-webkit-scrollbar-track {
  background: #e2e8f0;
  border-radius: 2px;
}

.thumbnails::-webkit-scrollbar-thumb {
  background: #cbd5e0;
  border-radius: 2px;
}

.thumbnails::-webkit-scrollbar-thumb:hover {
  background: #a0aec0;
}
</style>