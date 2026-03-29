<!-- src/views/trade/components/BulletinCard.vue -->
<template>
  <div class="bulletin-card" @click="handleClick">
    <div class="card-header">
      <h3 class="card-title">{{ bulletin.title }}</h3>
      <span class="card-state" :class="stateClass">{{ stateText }}</span>
    </div>
    <div class="card-footer">
      <span class="card-price">{{ formatPrice(bulletin.price) }} ₽</span>
      <span class="card-date">{{ formatDate(bulletin.createdAt) }}</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  bulletin: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['click'])

const stateText = computed(() => {
  const state = props.bulletin.state
  if (state === 'MODIFIABLE') return 'Черновик'
  if (state === 'PUBLISHED') return 'Опубликовано'
  if (state === 'COMPLETED') return 'Завершено'
  return state
})

const stateClass = computed(() => {
  const state = props.bulletin.state
  if (state === 'MODIFIABLE') return 'state-draft'
  if (state === 'PUBLISHED') return 'state-published'
  if (state === 'COMPLETED') return 'state-completed'
  return ''
})

const formatPrice = (price) => {
  return new Intl.NumberFormat('ru-RU').format(price)
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('ru-RU')
}

const handleClick = () => {
  emit('click', props.bulletin)
}
</script>

<style scoped>
.bulletin-card {
  background: white;
  padding: 1rem;
  border: 1px solid #e2e8f0;
  cursor: pointer;
  transition: all 0.2s;
}

.bulletin-card:hover {
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
}

.card-state {
  font-size: 0.75rem;
  padding: 0.125rem 0.5rem;
  border-radius: 4px;
}

.state-draft {
  background: #edf2f7;
  color: #4a5568;
}

.state-published {
  background: #c6f6d5;
  color: #2f855a;
}

.state-completed {
  background: #fed7d7;
  color: #c53030;
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
}

.card-price {
  font-weight: 600;
  color: #667eea;
}

.card-date {
  color: #a0aec0;
}
</style>