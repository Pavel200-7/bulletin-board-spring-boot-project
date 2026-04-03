<!-- src/views/chat/components/messages/ImageMessage.vue -->
<template>
  <div class="image-message" :class="{ 'own-message': isOwn }">
    <div class="image-container">
      <img 
        :src="imageUrl" 
        :alt="`Изображение от ${formatTime(message.createdAt)}`" 
        @click="openImage"
        @error="handleImageError"
        class="message-image"
        loading="lazy"
      />
    </div>
    <div class="message-footer">
      <span class="message-time" :title="formatFullDate(message.createdAt)">
        {{ formatTime(message.createdAt) }}
      </span>
      <span v-if="message.updated" class="edited-badge" title="Изменено">(изменено)</span>
    </div>
    
    <!-- Модальное окно для просмотра изображения -->
    <ImageViewer
      v-if="showViewer"
      :image-url="imageUrl"
      :created-at="message.createdAt"
      @close="closeImageViewer"
    />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import ImageViewer from '../ImageViewer.vue'

const props = defineProps({
  message: {
    type: Object,
    required: true
  },
  isOwn: {
    type: Boolean,
    default: false
  }
})

const showViewer = ref(false)

/**
 * Получить URL изображения из MinIO
 * @param {string} imageId - ID изображения
 * @returns {string|null} - URL изображения
 */
const getImageUrl = (imageId) => {
  if (!imageId) return null
  const MINIO_URL = import.meta.env.VITE_MINIO_URL || 'http://localhost:9001'
  const BUCKET = import.meta.env.VITE_MINIO_BUCKET || 'bulletins'
  return `${MINIO_URL}/${BUCKET}/${imageId}`
}

/**
 * Форматирование времени
 * @param {string} dateStr - строка с датой (ISO format)
 * @returns {string} - отформатированное время
 */
const formatTime = (dateStr) => {
  if (!dateStr) return ''
  
  try {
    const date = new Date(dateStr)
    if (isNaN(date.getTime())) return ''
    
    return date.toLocaleTimeString('ru-RU', {
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch (err) {
    console.error('Error formatting date:', err)
    return ''
  }
}

/**
 * Форматирование полной даты для всплывающей подсказки
 * @param {string} dateStr - строка с датой (ISO format)
 * @returns {string} - отформатированная дата и время
 */
const formatFullDate = (dateStr) => {
  if (!dateStr) return ''
  
  try {
    const date = new Date(dateStr)
    if (isNaN(date.getTime())) return ''
    
    return date.toLocaleString('ru-RU', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch (err) {
    return ''
  }
}

const imageUrl = computed(() => getImageUrl(props.message.content))

const openImage = () => {
  showViewer.value = true
}

const closeImageViewer = () => {
  showViewer.value = false
}

const handleImageError = (event) => {
  console.error('Failed to load image:', imageUrl.value)
  event.target.src = '/placeholder-image.png'
  event.target.alt = 'Изображение не загрузилось'
}
</script>

<style scoped>
.image-message {
  width: 100%;
}

.image-container {
  margin-bottom: 0.25rem;
}

.message-image {
  max-width: 200px;
  max-height: 200px;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.2s;
  object-fit: cover;
}

.message-image:hover {
  transform: scale(1.02);
}

.message-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 0.25rem;
}

.message-time {
  font-size: 0.65rem;
  color: #a0aec0;
  cursor: help;
}

.own-message .message-time {
  color: rgba(255, 255, 255, 0.7);
}

.edited-badge {
  font-size: 0.65rem;
  margin-left: 0.25rem;
  opacity: 0.7;
  cursor: help;
}
</style>