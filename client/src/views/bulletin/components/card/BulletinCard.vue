<!-- src/views/bulletin/components/card/BulletinCard.vue -->
<template>
  <div class="bulletin-card" @click="goToDetail">
    <div class="bulletin-card__image-wrapper">
      <img
        v-if="imageUrl"
        :src="imageUrl"
        :alt="bulletin.title"
        class="bulletin-card__image"
        @error="handleImageError"
      />
      <div v-else class="bulletin-card__image placeholder">
        <span class="placeholder-icon">🖼️</span>
        <span class="placeholder-text">Нет фото</span>
      </div>
    </div>

    <div class="bulletin-card__content">
      <h3 class="bulletin-card__title">{{ bulletin.title }}</h3>
      <p class="bulletin-card__price">{{ formatPrice(bulletin.price) }} ₽</p>
      <p v-if="bulletin.state === 'COMPLETED'" class="bulletin-card__completed">
        ✔️ Завершено
      </p>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const props = defineProps({
  bulletin: {
    type: Object,
    required: true
  }
})

const imageError = ref(false)

// Получение URL изображения
const getImageUrl = (imageId) => {
  if (!imageId) return null
  const MINIO_URL = import.meta.env.VITE_MINIO_URL || 'http://localhost:9001'
  const BUCKET = import.meta.env.VITE_MINIO_BUCKET || 'bulletins'
  return `${MINIO_URL}/${BUCKET}/${imageId}`
}

const imageUrl = computed(() => {
  if (imageError.value) return null

  const imageId = props.bulletin.image || null
  if (imageId === null) return null
  
  return getImageUrl(imageId)
})

const handleImageError = () => {
  imageError.value = true
}

const formatPrice = (price) => {
  return new Intl.NumberFormat('ru-RU').format(price)
}

const goToDetail = () => {
  router.push({ name: 'bulletin-view', params: { id: props.bulletin.id } })
}
</script>

<style scoped>
.bulletin-card {
  display: flex;
  flex-direction: column;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 280px;
}

.bulletin-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.bulletin-card__image-wrapper {
  flex: 0 0 auto;
  height: 200px;
  overflow: hidden;
  background: #f8f9fa;
  position: relative;
}

.bulletin-card__image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.bulletin-card:hover .bulletin-card__image {
  transform: scale(1.05);
}

.placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background: #f7fafc;
  color: #a0aec0;
}

.placeholder-icon {
  font-size: 2rem;
  margin-bottom: 0.25rem;
}

.placeholder-text {
  font-size: 0.75rem;
}

.bulletin-card__content {
  padding: 0.75rem;
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  background: white;
}

.bulletin-card__title {
  margin: 0;
  font-size: 0.875rem;
  font-weight: 600;
  color: #2d3748;
  line-height: 1.4;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  white-space: normal;
}

.bulletin-card__price {
  margin: 0;
  font-size: 1rem;
  font-weight: 700;
  color: #667eea;
}

.bulletin-card__completed {
  margin: 0;
  font-size: 0.7rem;
  color: #c53030;
  background: #fed7d7;
  display: inline-block;
  padding: 0.125rem 0.5rem;
  border-radius: 20px;
  width: fit-content;
}
</style>