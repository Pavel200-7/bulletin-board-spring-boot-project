<template>
  <div v-if="show" class="modal-overlay" @click.self="$emit('close')">
    <div class="error-modal">
      <div class="error-modal-header">
        <span class="error-icon">⚠️</span>
        <h3>Ошибка валидации</h3>
      </div>
      <div class="error-modal-body">
        <p>{{ message }}</p>
        <ul v-if="details.length">
          <li v-for="detail in details" :key="detail">
            {{ detail }}
          </li>
        </ul>
      </div>
      <div class="error-modal-footer">
        <button @click="$emit('close')" class="btn-close">Закрыть</button>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  show: Boolean,
  message: String,
  details: {
    type: Array,
    default: () => []
  }
})

defineEmits(['close'])
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

.error-modal {
  background: white;
  width: 400px;
  max-width: 90%;
  border-radius: 8px;
  overflow: hidden;
}

.error-modal-header {
  background: #fed7d7;
  padding: 1rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  border-bottom: 1px solid #feb2b2;
}

.error-modal-header .error-icon {
  font-size: 1.25rem;
  margin: 0;
}

.error-modal-header h3 {
  margin: 0;
  color: #c53030;
  font-size: 1.125rem;
}

.error-modal-body {
  padding: 1rem;
}

.error-modal-body p {
  margin: 0 0 0.5rem 0;
  color: #4a5568;
}

.error-modal-body ul {
  margin: 0;
  padding-left: 1.5rem;
  color: #e53e3e;
}

.error-modal-body li {
  margin: 0.25rem 0;
}

.error-modal-footer {
  padding: 1rem;
  border-top: 1px solid #e2e8f0;
  display: flex;
  justify-content: flex-end;
}

.btn-close {
  padding: 0.5rem 1rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-close:hover {
  background: #5a67d8;
}
</style>