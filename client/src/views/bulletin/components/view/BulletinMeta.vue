<!-- src/views/bulletin/components/view/BulletinMeta.vue -->
<template>
  <div class="meta-section">
    <div class="meta-item">
      <span class="meta-label">Дата публикации:</span>
      <span class="meta-value">{{ formatDate(createdAt) }}</span>
    </div>
    <div class="meta-item">
      <span class="meta-label">Просмотров:</span>
      <span class="meta-value">{{ views || 0 }}</span>
    </div>
    <div class="meta-item">
      <span class="meta-label">ID объявления:</span>
      <span class="meta-value">{{ bulletinId?.slice(0, 8) }}...</span>
    </div>
  </div>
</template>

<script setup>
defineProps({
  createdAt: {
    type: String,
    default: null
  },
  views: {
    type: Number,
    default: 0
  },
  bulletinId: {
    type: String,
    default: ''
  }
})

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('ru-RU', {
    day: 'numeric',
    month: 'long',
    year: 'numeric'
  })
}
</script>

<style scoped>
.meta-section {
  display: flex;
  gap: 2rem;
  padding: 1rem;
  background: white;
  border-radius: 12px;
  font-size: 0.75rem;
  color: #6c757d;
}

.meta-item {
  display: flex;
  gap: 0.5rem;
}

.meta-label {
  font-weight: 500;
}
</style>