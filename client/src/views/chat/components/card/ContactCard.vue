<!-- src/views/chat/components/card/ContactCard.vue -->
<template>
  <div class="contact-card" @click="$emit('click')">
    <div class="contact-avatar">
      <img 
        v-if="avatarUrl" 
        :src="avatarUrl" 
        :alt="contactName"
        class="avatar-image"
      />
      <div v-else class="avatar-placeholder">
        <span>👤</span>
      </div>
    </div>
    <div class="contact-info">
      <div class="contact-header">
        <h3 class="contact-name">{{ contactName || 'Без имени' }}</h3>
        <div v-if="unreadCount > 0" class="unread-badge">
          {{ unreadCount > 99 ? '99+' : unreadCount }}
        </div>
      </div>
      <div class="contact-footer">
        <div class="contact-id">ID: {{ profileId?.slice(0, 8) }}...</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useProfile } from '@/composables/useProfile'
import { useChat } from '@/composables/chat/useChat'

const props = defineProps({
  contactId: {
    type: String,
    required: true
  },
  profileId: {
    type: String,
    required: true
  },
  contactName: {
    type: String,
    default: ''
  },
  chatId: {
    type: String,
    default: null
  }
})

defineEmits(['click'])

const { fetchProfile, profile } = useProfile()
const { fetchUnreadCount, unreadCount } = useChat()
const imageId = ref(null)
const loading = ref(true)

const getImageUrl = (id) => {
  if (!id) return null
  const MINIO_URL = import.meta.env.VITE_MINIO_URL || 'http://localhost:9001'
  const BUCKET = import.meta.env.VITE_MINIO_BUCKET || 'bulletins'
  return `${MINIO_URL}/${BUCKET}/${id}`
}

const avatarUrl = computed(() => {
  return getImageUrl(imageId.value)
})

const loadProfile = async () => {
  if (!props.profileId) return
  
  loading.value = true
  try {
    await fetchProfile(props.profileId)
    if (profile.value) {
      imageId.value = profile.value.imageId
    }
  } catch (err) {
    console.error('Ошибка загрузки профиля:', err)
  } finally {
    loading.value = false
  }
}

const loadUnreadCount = async () => {
  if (props.chatId) {
    const res = await fetchUnreadCount(props.chatId)
  }
}

onMounted(() => {
  loadProfile()
  loadUnreadCount()
})

watch(() => props.chatId, () => {
  loadUnreadCount()
})
</script>

<style scoped>
.contact-card {
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

.contact-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-color: #cbd5e0;
}

.contact-avatar {
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

.contact-info {
  flex: 1;
  min-width: 0;
}

.contact-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.5rem;
  margin-bottom: 0.25rem;
}

.contact-name {
  margin: 0;
  font-size: 1rem;
  font-weight: 600;
  color: #2d3748;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.unread-badge {
  background: #e53e3e;
  color: white;
  border-radius: 12px;
  padding: 0.15rem 0.5rem;
  font-size: 0.7rem;
  font-weight: 600;
  min-width: 20px;
  text-align: center;
  flex-shrink: 0;
}

.contact-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.contact-id {
  font-size: 0.7rem;
  color: #a0aec0;
}
</style>