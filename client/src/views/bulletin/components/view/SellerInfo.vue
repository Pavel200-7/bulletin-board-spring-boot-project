<!-- src/views/bulletin/components/view/SellerInfo.vue -->
<template>
  <div>
    <div class="seller-info" @click="openModal">
      <div class="seller-avatar">
        <img 
          v-if="avatarUrl" 
          :src="avatarUrl" 
          alt="Аватар"
          class="avatar-image"
        />
        <span v-else>👤</span>
      </div>
      <div class="seller-details">
        <div class="seller-name">{{ sellerName || 'Продавец' }}</div>
        <div class="seller-id">ID: {{ ownerId?.slice(0, 8) }}...</div>
      </div>
      <div class="seller-arrow">▶</div>
    </div>
    
    <!-- Кнопка подписки (только для авторизованных) -->
    <SubscribeButton 
      v-if="isAuthenticated" 
      :owner-id="ownerId"
    />
    
    <SellerModal
      :show="showModal"
      :owner-id="ownerId"
      @close="showModal = false"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuth } from '@/composables/useAuth'
import { useTradeAccount } from '@/composables/useTradeAccount'
import SellerModal from '../modals/SellerModal.vue'
import SubscribeButton from '../buttons/SubscribeButton.vue'

const props = defineProps({
  ownerId: {
    type: String,
    required: true
  }
})

const { isAuthenticated } = useAuth()
const { fetchTradeAccountByUserId, account } = useTradeAccount()
const showModal = ref(false)
const sellerName = ref('')
const avatarUrl = ref(null)

const getImageUrl = (imageId) => {
  if (!imageId) return null
  const MINIO_URL = import.meta.env.VITE_MINIO_URL || 'http://localhost:9001'
  const BUCKET = import.meta.env.VITE_MINIO_BUCKET || 'bulletins'
  return `${MINIO_URL}/${BUCKET}/${imageId}`
}

const loadSellerData = async () => {
  if (props.ownerId) {
    try {
      await fetchTradeAccountByUserId(props.ownerId)
      if (account.value) {
        sellerName.value = account.value.name
        avatarUrl.value = getImageUrl(account.value.imageId)
      }
    } catch (err) {
      console.error('Ошибка загрузки данных продавца:', err)
    }
  }
}

const openModal = () => {
  showModal.value = true
}

onMounted(() => {
  loadSellerData()
})
</script>

<style scoped>
.seller-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 12px;
  margin-bottom: 1rem;
  cursor: pointer;
  transition: all 0.2s;
}

.seller-info:hover {
  background: #edf2f7;
  transform: translateX(2px);
}

.seller-avatar {
  width: 48px;
  height: 48px;
  background: #e2e8f0;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  overflow: hidden;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.seller-details {
  flex: 1;
}

.seller-name {
  font-weight: 600;
  color: #212529;
  margin-bottom: 0.25rem;
}

.seller-id {
  font-size: 0.75rem;
  color: #6c757d;
}

.seller-arrow {
  color: #a0aec0;
  font-size: 0.75rem;
  transition: transform 0.2s;
}

.seller-info:hover .seller-arrow {
  transform: translateX(2px);
  color: #667eea;
}
</style>