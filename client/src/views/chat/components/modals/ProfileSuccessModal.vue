<template>
  <div v-if="show" class="modal-overlay" @click.self="close">
    <div class="success-modal">
      <div class="modal-icon">✅</div>
      <h3>{{ title }}</h3>
      <p>{{ message }}</p>
      <button class="btn-close" @click="close">Закрыть</button>
    </div>
  </div>
</template>

<script setup>
defineProps({
  show: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: 'Успешно!'
  },
  message: {
    type: String,
    default: 'Данные сохранены'
  }
})

const emit = defineEmits(['close'])

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

.success-modal {
  background: white;
  width: 320px;
  max-width: 90%;
  padding: 2rem;
  border-radius: 16px;
  text-align: center;
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

.modal-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.success-modal h3 {
  margin: 0 0 0.5rem 0;
  color: #2d3748;
}

.success-modal p {
  margin: 0 0 1.5rem 0;
  color: #718096;
}

.btn-close {
  padding: 0.5rem 1.5rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.btn-close:hover {
  background: #5a67d8;
}
</style>