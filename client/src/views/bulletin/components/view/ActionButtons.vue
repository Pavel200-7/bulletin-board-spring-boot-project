<!-- src/views/bulletin/components/view/ActionButtons.vue -->
<template>
  <div class="action-buttons">
    <button class="btn-chat" @click="$emit('chat')">
      💬 Написать продавцу
    </button>
  </div>
</template>

<script setup>
defineEmits(['chat'])
</script>

<style scoped>
.action-buttons {
  display: flex;
  gap: 0.75rem;
  margin-bottom: 1.5rem;
}

.btn-chat {
  width: 100%;
  padding: 0.75rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-chat:hover {
  background: #5a67d8;
  transform: translateY(-2px);
}
</style>