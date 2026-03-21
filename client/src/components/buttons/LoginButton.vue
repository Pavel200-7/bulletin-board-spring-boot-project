<!-- src/components/buttons/LoginButton.vue -->
<template>
  <button @click="$emit('click')" class="login-btn">
    Войти
  </button>
</template>

<script setup>
defineEmits(['click'])
</script>

<style scoped>
.login-btn {
  padding: 0.5rem 1rem;
  background: #48bb78;
  color: white;
  border: none;
  cursor: pointer;
  font-size: 0.875rem;
  transition: background 0.2s;
}

.login-btn:hover {
  background: #38a169;
}
</style>