<!-- src/views/bulletin/components/buttons/SubscribeButton.vue -->
<template>
  <div class="subscribe-button">
    <button 
      class="btn-subscribe"
      :class="{ 
        'is-subscribed': isSubscribedStatus,
        'loading': loading 
      }"
      @click="toggleSubscribe"
      :disabled="loading"
    >
      <span v-if="loading" class="spinner"></span>
      <span v-else>
        {{ isSubscribedStatus ? '✅ Отписаться' : '🔔 Подписаться' }}
      </span>
    </button>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useSubscription } from '@/composables/useSubscription'
import { NOTIFICATION_TYPE } from '@/services/subscription/types'

const props = defineProps({
  ownerId: {
    type: String,
    required: true
  }
})

const { checkExists, toggleSubscription, loading: subscriptionLoading } = useSubscription()

const isSubscribedStatus = ref(false)
const loading = ref(false)

// Проверка существования подписки
const checkSubscription = async () => {
  if (!props.ownerId) return
  
  try {
    const { exists } = await checkExists(NOTIFICATION_TYPE.BULLETIN_PUBLISHED, props.ownerId)
    isSubscribedStatus.value = exists
  } catch (err) {
    console.error('Ошибка проверки подписки:', err)
  }
}

// Переключение подписки
const toggleSubscribe = async () => {
  if (!props.ownerId) return
  
  loading.value = true
  
  try {
    const newStatus = await toggleSubscription(NOTIFICATION_TYPE.BULLETIN_PUBLISHED, props.ownerId)
    isSubscribedStatus.value = newStatus
  } catch (err) {
    console.error('Ошибка при переключении подписки:', err)
    alert(err.response?.data?.message || 'Произошла ошибка')
  } finally {
    loading.value = false
  }
}

// Следим за изменением ownerId
watch(() => props.ownerId, () => {
  checkSubscription()
}, { immediate: true })

onMounted(() => {
  checkSubscription()
})
</script>

<style scoped>
.subscribe-button {
  display: inline-block;
  width: 100%;
  margin-top: 0.5rem;
}

.btn-subscribe {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  width: 100%;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 8px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  margin-bottom: 10px;
}

.btn-subscribe:not(.is-subscribed) {
  background: #667eea;
  color: white;
}

.btn-subscribe:not(.is-subscribed):hover:not(:disabled) {
  background: #5a67d8;
  transform: translateY(-1px);
}

.btn-subscribe.is-subscribed {
  background: #e2e8f0;
  color: #4a5568;
}

.btn-subscribe.is-subscribed:hover:not(:disabled) {
  background: #cbd5e0;
  transform: translateY(-1px);
}

.btn-subscribe:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

.btn-subscribe.is-subscribed .spinner {
  border: 2px solid rgba(0, 0, 0, 0.1);
  border-top-color: #4a5568;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>