<!-- src/components/badges/AnonymousBadge.vue -->
<template>
  <div class="anonymous-badge">
    <span class="anonymous-icon">👤</span>
    <span class="anonymous-text">Анонимный пользователь</span>
  </div>
</template>

<style scoped>
.anonymous-badge {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: #edf2f7;
  padding: 0.4rem 0.8rem;
}

.anonymous-icon {
  font-size: 1rem;
}

.anonymous-text {
  font-size: 0.875rem;
  font-weight: 500;
  color: #718096;
}
</style>