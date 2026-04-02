<!-- src/views/chat/components/DeleteConfirmModal.vue -->
<template>
  <div v-if="show" class="modal-overlay" @click.self="close">
    <div class="delete-modal">
      <div class="modal-header">
        <h3>Удаление сообщения</h3>
      </div>
      <div class="modal-body">
        <p>Вы уверены, что хотите удалить это сообщение?</p>
        <p class="warning">Это действие нельзя отменить.</p>
      </div>
      <div class="modal-footer">
        <button class="btn-cancel" @click="close">Отмена</button>
        <button class="btn-delete" @click="confirm">Удалить</button>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  show: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['confirm', 'close'])

const confirm = () => {
  emit('confirm')
  emit('close')
}

const close = () => {
  emit('close')
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.delete-modal {
  background: white;
  width: 400px;
  max-width: 90%;
  border-radius: 12px;
  overflow: hidden;
  animation: modalFadeIn 0.2s ease;
}

@keyframes modalFadeIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.modal-header {
  padding: 1rem 1.5rem;
  background: #fed7d7;
  border-bottom: 1px solid #feb2b2;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.125rem;
  font-weight: 600;
  color: #c53030;
}

.modal-body {
  padding: 1.5rem;
}

.modal-body p {
  margin: 0 0 0.5rem 0;
  color: #4a5568;
}

.warning {
  font-size: 0.875rem;
  color: #e53e3e;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  padding: 1rem 1.5rem;
  border-top: 1px solid #e2e8f0;
}

.btn-cancel {
  padding: 0.5rem 1rem;
  background: #e2e8f0;
  color: #4a5568;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.btn-cancel:hover {
  background: #cbd5e0;
}

.btn-delete {
  padding: 0.5rem 1rem;
  background: #e53e3e;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.btn-delete:hover {
  background: #c53030;
}
</style>