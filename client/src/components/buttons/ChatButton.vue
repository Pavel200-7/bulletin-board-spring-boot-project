<!-- src/components/buttons/ChatButton.vue -->
<template>
  <button class="chat-btn" @click="$emit('click')">
    💬 Сообщения
  </button>
</template>

<script setup>
defineEmits(['click'])
</script>

<style scoped>
.chat-btn {
  padding: 0.5rem 1rem;
  background: #48bb78;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.875rem;
  font-weight: 500;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
}

.chat-btn:hover {
  background: #38a169;
  transform: translateY(-1px);
}
</style>