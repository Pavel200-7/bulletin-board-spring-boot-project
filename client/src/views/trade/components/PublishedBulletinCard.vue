<!-- src/views/trade/components/PublishedBulletinCard.vue -->
<template>
  <div class="published-bulletin-card" @click="goToDetail">
    <div class="card-header">
      <h3 class="card-title">{{ bulletin.title }}</h3>
      <span class="card-state state-published">📢 Опубликовано</span>
    </div>
    <p class="card-description">{{ bulletin.description || 'Нет описания' }}</p>
    <div class="card-footer">
      <span class="card-price">{{ formatPrice(bulletin.price) }} ₽</span>
      <span class="card-date">{{ formatDate(bulletin.createdAt) }}</span>
    </div>
    
    <!-- Кнопка завершения -->
    <div class="card-actions">
      <button 
        class="btn-complete" 
        @click.stop="handleComplete"
        :disabled="completing"
      >
        {{ completing ? 'Завершение...' : '✔️ Завершить сделку' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  bulletin: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['complete'])

const router = useRouter()
const completing = ref(false)

const formatPrice = (price) => {
  return new Intl.NumberFormat('ru-RU').format(price)
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('ru-RU')
}

const goToDetail = () => {
  router.push(`/bulletin/view/${props.bulletin.id}`)
}

const handleComplete = () => {
  if (confirm('Вы уверены, что хотите завершить это объявление? После завершения оно больше не будет отображаться в поиске.')) {
    completing.value = true
    emit('complete', props.bulletin.id)
  }
}
</script>

<style scoped>
.published-bulletin-card {
  background: white;
  padding: 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.published-bulletin-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.card-title {
  margin: 0;
  font-size: 1rem;
  font-weight: 500;
  color: #2d3748;
  flex: 1;
  margin-right: 0.5rem;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-state {
  font-size: 0.75rem;
  padding: 0.125rem 0.5rem;
  border-radius: 4px;
  flex-shrink: 0;
}

.state-published {
  background: #c6f6d5;
  color: #2f855a;
}

.card-description {
  font-size: 0.875rem;
  color: #718096;
  margin-bottom: 0.5rem;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.75rem;
  margin-bottom: 0.75rem;
}

.card-price {
  font-weight: 600;
  color: #667eea;
}

.card-date {
  color: #a0aec0;
}

.card-actions {
  padding-top: 0.5rem;
  border-top: 1px solid #e2e8f0;
}

.btn-complete {
  width: 100%;
  padding: 0.5rem;
  background: #e53e3e;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.75rem;
  transition: all 0.2s;
}

.btn-complete:hover:not(:disabled) {
  background: #c53030;
  transform: translateY(-1px);
}

.btn-complete:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>