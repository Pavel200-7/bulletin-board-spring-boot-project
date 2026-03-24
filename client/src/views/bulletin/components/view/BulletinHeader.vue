<!-- src/views/bulletin/components/view/BulletinHeader.vue -->
<template>
  <div class="price-block">
    <div class="price">{{ formatPrice(price) }} ₽</div>
    <div class="status-badge" :class="statusClass">
      {{ statusText }}
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  price: {
    type: Number,
    required: true
  },
  state: {
    type: String,
    default: ''
  }
})

const formatPrice = (price) => {
  return new Intl.NumberFormat('ru-RU').format(price)
}

const statusText = computed(() => {
  const state = props.state
  if (state === 'PUBLISHED') return 'Активно'
  if (state === 'COMPLETED') return 'Завершено'
  if (state === 'MODIFIABLE') return 'Черновик'
  if (state === 'APPROVED') return 'Готов к публикации'
  return state || ''
})

const statusClass = computed(() => {
  const state = props.state
  if (state === 'PUBLISHED') return 'status-active'
  if (state === 'COMPLETED') return 'status-completed'
  if (state === 'MODIFIABLE') return 'status-draft'
  if (state === 'APPROVED') return 'status-approved'
  return ''
})
</script>

<style scoped>
.price-block {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 1rem;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.price {
  font-size: 2rem;
  font-weight: 700;
  color: #212529;
}

.status-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 500;
}

.status-active {
  background: #c6f6d5;
  color: #2f855a;
}

.status-completed {
  background: #fed7d7;
  color: #c53030;
}

.status-draft {
  background: #edf2f7;
  color: #4a5568;
}

.status-approved {
  background: #fef3c7;
  color: #d97706;
}
</style>