<!-- src/views/chat/components/MessagesContainer.vue -->
<template>
  <div class="messages-container" ref="container">
    <div v-if="messages.length === 0" class="empty-state">
      <div class="empty-icon">💬</div>
      <p>Нет сообщений</p>
      <p class="empty-hint">Напишите первое сообщение</p>
    </div>
    
    <div v-else class="messages-list">
      <div
        v-for="message in messages"
        :key="message.id"
        class="message-item"
        :class="{ 'message-own': message.senderId === currentUserId }"
      >
        <div class="message-bubble">
          <div v-if="message.type === 'TEXT'" class="message-text">
            {{ message.content }}
          </div>
          <div v-else-if="message.type === 'IMAGE'" class="message-image">
            <img :src="getImageUrl(message.content)" alt="Изображение" />
          </div>
          <div class="message-time">
            {{ formatTime(message.createdAt) }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

defineProps({
  messages: {
    type: Array,
    default: () => []
  },
  currentUserId: {
    type: String,
    default: null
  }
})

const container = ref(null)

const getImageUrl = (imageId) => {
  if (!imageId) return null
  const MINIO_URL = import.meta.env.VITE_MINIO_URL || 'http://localhost:9001'
  const BUCKET = import.meta.env.VITE_MINIO_BUCKET || 'bulletins'
  return `${MINIO_URL}/${BUCKET}/${imageId}`
}

const formatTime = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleTimeString('ru-RU', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

defineExpose({
  container
})
</script>

<style scoped>
.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 1rem;
  background: #f8f9fa;
}

.empty-state {
  text-align: center;
  padding: 2rem;
  color: #a0aec0;
  min-height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.empty-hint {
  font-size: 0.75rem;
  margin-top: 0.5rem;
}

.messages-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.message-item {
  display: flex;
  margin-bottom: 0.5rem;
}

.message-own {
  justify-content: flex-end;
}

.message-bubble {
  max-width: 70%;
  padding: 0.5rem 0.75rem;
  border-radius: 18px;
  background: white;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.message-own .message-bubble {
  background: #667eea;
  color: white;
}

.message-text {
  font-size: 0.875rem;
  line-height: 1.4;
  word-break: break-word;
}

.message-image img {
  max-width: 200px;
  max-height: 200px;
  border-radius: 8px;
  cursor: pointer;
}

.message-time {
  font-size: 0.65rem;
  color: #a0aec0;
  margin-top: 0.25rem;
  text-align: right;
}

.message-own .message-time {
  color: rgba(255, 255, 255, 0.7);
}
</style>