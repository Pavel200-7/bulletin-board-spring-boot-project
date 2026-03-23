<template>
  <div class="error-container">
    <div class="error-icon">⚠️</div>
    <div class="error-message">{{ error }}</div>
    <button @click="$emit('retry')" class="retry-btn">Повторить</button>
  </div>
</template>

<script setup>
defineProps({
  error: String
})

defineEmits(['retry'])
</script>

<style scoped>
.error-container {
  text-align: center;
  padding: 2rem;
}

.error-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.error-message {
  color: #e53e3e;
  margin-bottom: 1rem;
}

.retry-btn {
  padding: 0.5rem 1rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.retry-btn:hover {
  background: #5a67d8;
}
</style>