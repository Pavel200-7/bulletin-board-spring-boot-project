<!-- src/views/trade/components/card/ClosedBulletinCard.vue -->
<template>
  <div class="closed-bulletin-card">
    <div class="card-header">
      <h3 class="card-title">{{ bulletin.title }}</h3>
      <span class="card-state state-completed">✔️ Завершено</span>
    </div>
    <div class="card-footer">
      <span class="card-price">{{ formatPrice(bulletin.price) }} ₽</span>
      <span class="card-date">{{ formatDate(bulletin.createdAt) }}</span>
    </div>
  </div>
</template>

<script setup>
defineProps({
  bulletin: {
    type: Object,
    required: true
  }
})

const formatPrice = (price) => {
  return new Intl.NumberFormat('ru-RU').format(price)
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('ru-RU')
}
</script>

<style scoped>
.closed-bulletin-card {
  background: #f7fafc;
  padding: 1rem;
  border: 1px solid #e2e8f0;
  transition: all 0.2s;
  opacity: 0.7;
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
  color: #718096;
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

.state-completed {
  background: #fed7d7;
  color: #c53030;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.75rem;
}

.card-price {
  font-weight: 600;
  color: #a0aec0;
}

.card-date {
  color: #cbd5e0;
}
</style>