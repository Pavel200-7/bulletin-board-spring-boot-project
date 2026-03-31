<!-- src/views/chat/components/ChatHeader.vue -->
<template>
  <div class="chat-header">
    <button class="back-btn" @click="$emit('back')">←</button>
    <div class="chat-info" @click="$emit('open-profile')" v-if="profileId">
      <div class="chat-avatar">
        <img 
          v-if="avatarUrl" 
          :src="avatarUrl" 
          :alt="chatName"
          class="avatar-image"
        />
        <div v-else class="avatar-placeholder">
          <span>👤</span>
        </div>
      </div>
      <div class="chat-details">
        <h3 class="chat-name">{{ chatName }}</h3>
        <div class="chat-status">{{ status || 'Пользователь' }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  chatName: {
    type: String,
    default: ''
  },
  status: {
    type: String,
    default: ''
  },
  avatarUrl: {
    type: String,
    default: null
  },
  profileId: {
    type: String,
    default: null
  }
})

defineEmits(['back', 'open-profile'])

const getImageUrl = (imageId) => {
  if (!imageId) return null
  const MINIO_URL = import.meta.env.VITE_MINIO_URL || 'http://localhost:9001'
  const BUCKET = import.meta.env.VITE_MINIO_BUCKET || 'bulletins'
  return `${MINIO_URL}/${BUCKET}/${imageId}`
}
</script>

<style scoped>
.chat-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: white;
  border-bottom: 1px solid #e2e8f0;
}

.back-btn {
  width: 36px;
  height: 36px;
  background: #f7fafc;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  font-size: 1.25rem;
  transition: all 0.2s;
}

.back-btn:hover {
  background: #edf2f7;
}

.chat-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  cursor: pointer;
  flex: 1;
}

.chat-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  overflow: hidden;
  background: #f7fafc;
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
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  color: #a0aec0;
}

.chat-details {
  flex: 1;
}

.chat-name {
  margin: 0;
  font-size: 1rem;
  font-weight: 600;
  color: #2d3748;
}

.chat-status {
  font-size: 0.75rem;
  color: #a0aec0;
}
</style>