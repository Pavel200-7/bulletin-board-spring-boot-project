<!-- src/components/badges/AdminBadge.vue -->
<template>
  <div class="admin-badge">
    <span class="admin-icon">👑</span>
    <span class="admin-text">Администратор</span>
  </div>
</template>

<style scoped>
.admin-badge {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: #fef3c7;
  padding: 0.4rem 0.8rem;
}

.admin-icon {
  font-size: 1rem;
}

.admin-text {
  font-size: 0.875rem;
  font-weight: 500;
  color: #b45309;
}
</style>