<!-- src/views/chat/components/card/ProfileCard.vue -->
<template>
  <div class="profile-card" @click="$emit('click', profileData)">
    <div class="profile-avatar">
      <img 
        v-if="avatarUrl" 
        :src="avatarUrl" 
        :alt="publicName"
        class="avatar-image"
      />
      <div v-else class="avatar-placeholder">
        <span>👤</span>
      </div>
    </div>
    <div class="profile-info">
      <div class="profile-header">
        <h3 class="profile-name">{{ publicName || 'Без имени' }}</h3>
        <div v-if="contact" class="contact-badge" title="В ваших контактах">
          ✓
        </div>
      </div>
      <p class="profile-description">{{ description || 'Нет описания' }}</p>
      <div class="profile-footer">
        <div class="profile-id">ID: {{ profileId?.slice(0, 8) }}...</div>
        <button 
          v-if="!contact && !isCurrentUser" 
          class="add-contact-btn" 
          @click.stop="handleAddContact"
        >
          + Добавить
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  profileId: {
    type: String,
    required: true
  },
  ownerId: {
    type: String,
    default: null
  },
  publicName: {
    type: String,
    default: ''
  },
  description: {
    type: String,
    default: ''
  },
  imageId: {
    type: String,
    default: null
  },
  contact: {
    type: Boolean,
    default: false
  },
  isCurrentUser: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['click', 'add-contact'])

// Собираем данные профиля в объект для передачи
const profileData = computed(() => ({
  id: props.profileId,
  ownerId: props.ownerId,
  publicName: props.publicName,
  description: props.description,
  imageId: props.imageId,
  contact: props.contact
}))

const avatarUrl = computed(() => {
  if (!props.imageId) return null
  const MINIO_URL = import.meta.env.VITE_MINIO_URL || 'http://localhost:9001'
  const BUCKET = import.meta.env.VITE_MINIO_BUCKET || 'bulletins'
  return `${MINIO_URL}/${BUCKET}/${props.imageId}`
})

const handleAddContact = () => {
  emit('add-contact', profileData.value)
}
</script>

<style scoped>
.profile-card {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.profile-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-color: #cbd5e0;
}

.profile-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  overflow: hidden;
  background: #f7fafc;
  flex-shrink: 0;
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
  font-size: 2rem;
  color: #a0aec0;
  background: #edf2f7;
}

.profile-info {
  flex: 1;
  min-width: 0;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.25rem;
}

.profile-name {
  margin: 0;
  font-size: 1rem;
  font-weight: 600;
  color: #2d3748;
}

.contact-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  background: #48bb78;
  color: white;
  border-radius: 50%;
  font-size: 0.75rem;
  font-weight: bold;
  flex-shrink: 0;
}

.profile-description {
  margin: 0 0 0.5rem 0;
  font-size: 0.875rem;
  color: #718096;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.profile-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-id {
  font-size: 0.7rem;
  color: #a0aec0;
}

.add-contact-btn {
  padding: 0.25rem 0.75rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 16px;
  font-size: 0.7rem;
  cursor: pointer;
  transition: all 0.2s;
}

.add-contact-btn:hover {
  background: #5a67d8;
  transform: translateY(-1px);
}
</style>