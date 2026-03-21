<!-- src/components/buttons/LogoutButton.vue -->
<template>
  <button @click="$emit('click')" class="logout-btn">
    Выйти
  </button>
</template>

<script setup>
defineEmits(['click'])
</script>

<style scoped>
.logout-btn {
  padding: 0.5rem 1rem;
  background: #f56565;
  color: white;
  border: none;
  cursor: pointer;
  font-size: 0.875rem;
  transition: background 0.2s;
}

.logout-btn:hover {
  background: #c53030;
}
</style>