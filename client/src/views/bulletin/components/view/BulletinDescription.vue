<!-- src/views/bulletin/components/view/BulletinDescription.vue -->
<template>
  <div class="description-section">
    <h3>Описание</h3>
    <div class="description-text">
      {{ description || 'Описание отсутствует' }}
    </div>
  </div>
</template>

<script setup>
defineProps({
  description: {
    type: String,
    default: ''
  }
})
</script>

<style scoped>
.description-section {
  background: white;
  border-radius: 16px;
  padding: 1.5rem;
  margin-bottom: 1rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.description-section h3 {
  font-size: 1rem;
  font-weight: 600;
  color: #212529;
  margin-bottom: 1rem;
}

.description-text {
  font-size: 0.9375rem;
  line-height: 1.6;
  color: #4a5568;
  white-space: pre-wrap;
}
</style>