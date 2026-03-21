<!-- src/components/badges/AuthBadge.vue -->
<template>
  <div class="auth-badge">
    <span class="auth-icon">✅</span>
    <span class="auth-text">Авторизован</span>
  </div>
</template>

<style scoped>
.auth-badge {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: #e6fffa;
  padding: 0.4rem 0.8rem;
}

.auth-icon {
  font-size: 1rem;
}

.auth-text {
  font-size: 0.875rem;
  font-weight: 500;
  color: #2c7a4b;
}
</style>