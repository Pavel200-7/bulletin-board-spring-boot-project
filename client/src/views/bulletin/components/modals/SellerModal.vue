<!-- src/views/bulletin/components/modals/SellerModal.vue -->
<template>
  <div v-if="show" class="modal-overlay" @click.self="close">
    <div class="seller-modal">
      <div class="modal-header">
        <h3>Информация о продавце</h3>
        <button class="modal-close" @click="close">✕</button>
      </div>
      
      <div class="modal-body" v-if="loading">
        <div class="loading-spinner"></div>
        <p>Загрузка данных...</p>
      </div>
      
      <div v-else-if="error" class="modal-error">
        <p>{{ error }}</p>
        <button class="btn-retry" @click="loadData">Повторить</button>
      </div>
      
      <div v-else-if="!account" class="modal-empty">
        <p>Данные продавца не найдены</p>
      </div>
      
      <div v-else class="modal-content">
        <!-- Аватар -->
        <div class="seller-avatar-large">
          <img 
            v-if="account.imageId" 
            :src="getImageUrl(account.imageId)" 
            alt="Аватар"
            class="avatar-image"
          />
          <div v-else class="avatar-placeholder">
            <span>👤</span>
          </div>
        </div>
        
        <!-- Основная информация -->
        <div class="seller-info-block">
          <h2 class="seller-name-large">{{ account.name || 'Не указано' }}</h2>
          <div class="seller-status" :class="{ approved: account.approved }">
            {{ account.approved ? '✅ Верифицирован' : '⏳ Не верифицирован' }}
          </div>
        </div>
        
        <!-- Контактные данные -->
        <div class="info-section">
          <h4>Контактные данные</h4>
          <div class="info-row">
            <span class="info-label">Телефон:</span>
            <span class="info-value">{{ account.phone || 'Не указан' }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">Контакты:</span>
            <span class="info-value">{{ account.contacts || 'Не указаны' }}</span>
          </div>
        </div>
        
        <!-- Описание -->
        <div class="info-section">
          <h4>Описание</h4>
          <p class="description-text">{{ account.description || 'Нет описания' }}</p>
        </div>
        
        <!-- Локация -->
        <div class="info-section" v-if="account.townName">
          <h4>Местоположение</h4>
          <div class="info-row">
            <span class="info-label">Город:</span>
            <span class="info-value">{{ account.townName }}</span>
          </div>
          <div class="info-row" v-if="account.locationName">
            <span class="info-label">Адрес:</span>
            <span class="info-value">{{ account.locationName }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useTradeAccount } from '@/composables/useTradeAccount'

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  ownerId: {
    type: String,
    required: true
  }
})

const emit = defineEmits(['close'])

const { fetchTradeAccountByUserId, account, loading, error } = useTradeAccount()

const getImageUrl = (imageId) => {
  if (!imageId) return null
  const MINIO_URL = import.meta.env.VITE_MINIO_URL || 'http://localhost:9001'
  const BUCKET = import.meta.env.VITE_MINIO_BUCKET || 'bulletins'
  return `${MINIO_URL}/${BUCKET}/${imageId}`
}

const loadData = async () => {
  if (props.ownerId) {
    await fetchTradeAccountByUserId(props.ownerId)
  }
}

const close = () => {
  emit('close')
}

watch(() => props.show, (newVal) => {
  if (newVal && props.ownerId) {
    loadData()
  }
})
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.seller-modal {
  background: white;
  width: 500px;
  max-width: 90%;
  max-height: 80vh;
  border-radius: 16px;
  overflow: hidden;
  animation: modalFadeIn 0.2s ease;
}

@keyframes modalFadeIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  background: #f7fafc;
  border-bottom: 1px solid #e2e8f0;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.125rem;
  font-weight: 600;
  color: #2d3748;
}

.modal-close {
  background: none;
  border: none;
  font-size: 1.25rem;
  cursor: pointer;
  color: #a0aec0;
  padding: 0.25rem;
  border-radius: 4px;
  transition: all 0.2s;
}

.modal-close:hover {
  background: #edf2f7;
  color: #4a5568;
}

.modal-body {
  padding: 1.5rem;
  overflow-y: auto;
  max-height: calc(80vh - 60px);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #e2e8f0;
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 1rem;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.modal-error,
.modal-empty {
  text-align: center;
  padding: 2rem;
  color: #718096;
}

.btn-retry {
  margin-top: 1rem;
  padding: 0.5rem 1rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.btn-retry:hover {
  background: #5a67d8;
}

.modal-content {
  padding: 1.5rem;
}

.seller-avatar-large {
  width: 100px;
  height: 100px;
  margin: 0 auto 1rem;
  border-radius: 50%;
  overflow: hidden;
  background: #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: center;
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
  font-size: 3rem;
  color: #a0aec0;
}

.seller-info-block {
  text-align: center;
  margin-bottom: 1.5rem;
}

.seller-name-large {
  font-size: 1.25rem;
  font-weight: 600;
  color: #2d3748;
  margin: 0 0 0.25rem 0;
}

.seller-status {
  font-size: 0.75rem;
  padding: 0.25rem 0.75rem;
  display: inline-block;
  border-radius: 20px;
  background: #edf2f7;
  color: #4a5568;
}

.seller-status.approved {
  background: #c6f6d5;
  color: #2f855a;
}

.info-section {
  margin-bottom: 1.5rem;
}

.info-section h4 {
  font-size: 0.875rem;
  font-weight: 600;
  color: #4a5568;
  margin: 0 0 0.75rem 0;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid #e2e8f0;
}

.info-row {
  display: flex;
  margin-bottom: 0.5rem;
  font-size: 0.875rem;
}

.info-label {
  width: 80px;
  color: #718096;
  flex-shrink: 0;
}

.info-value {
  flex: 1;
  color: #2d3748;
  word-break: break-word;
}

.description-text {
  font-size: 0.875rem;
  line-height: 1.5;
  color: #4a5568;
  margin: 0;
  white-space: pre-wrap;
}
</style>