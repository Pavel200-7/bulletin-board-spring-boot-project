<!-- src/components/buttons/AdminButton.vue -->
<template>
  <button @click="$emit('click')" class="admin-btn">
    ⚙️ Админ панель
  </button>
</template>

<script setup>
defineEmits(['click'])
</script>

<style scoped>
.admin-btn {
  padding: 0.5rem 1rem;
  background: #667eea;
  color: white;
  border: none;
  cursor: pointer;
  font-size: 0.875rem;
  transition: background 0.2s;
}

.admin-btn:hover {
  background: #5a67d8;
}
</style>