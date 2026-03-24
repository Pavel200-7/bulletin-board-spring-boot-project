<!-- src/views/bulletin/components/modals/BaseModal.vue -->
<template>
  <div v-if="show" class="modal-overlay" @click.self="handleClose">
    <div class="modal">
      <div class="modal-header">
        <h3>{{ title }}</h3>
        <button v-if="showClose" class="modal-close" @click="handleClose">✕</button>
      </div>
      <div class="modal-body">
        <slot name="body">
          <p>{{ message }}</p>
        </slot>
      </div>
      <div class="modal-footer">
        <button class="btn-cancel" @click="handleCancel">
          {{ cancelText }}
        </button>
        <button 
          class="btn-confirm" 
          :disabled="confirmDisabled"
          @click="handleConfirm"
        >
          {{ confirmLoading ? confirmLoadingText : confirmText }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: 'Подтверждение'
  },
  message: {
    type: String,
    default: ''
  },
  cancelText: {
    type: String,
    default: 'Отмена'
  },
  confirmText: {
    type: String,
    default: 'Подтвердить'
  },
  confirmLoadingText: {
    type: String,
    default: 'Загрузка...'
  },
  confirmLoading: {
    type: Boolean,
    default: false
  },
  confirmDisabled: {
    type: Boolean,
    default: false
  },
  showClose: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['confirm', 'cancel', 'close'])

const handleConfirm = () => {
  emit('confirm')
}

const handleCancel = () => {
  emit('cancel')
}

const handleClose = () => {
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

.modal {
  background: white;
  width: 400px;
  max-width: 90%;
  border-radius: 8px;
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
  padding: 1rem;
  background: #f7fafc;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  color: #333;
  font-size: 1.125rem;
}

.modal-close {
  background: none;
  border: none;
  font-size: 1.25rem;
  cursor: pointer;
  color: #a0aec0;
  padding: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: all 0.2s;
}

.modal-close:hover {
  background: #edf2f7;
  color: #4a5568;
}

.modal-body {
  padding: 1rem;
}

.modal-body p {
  margin: 0.5rem 0;
  color: #4a5568;
  line-height: 1.5;
}

.modal-footer {
  padding: 1rem;
  border-top: 1px solid #e2e8f0;
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
}

.btn-cancel {
  padding: 0.5rem 1rem;
  background: #e2e8f0;
  color: #4a5568;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.btn-cancel:hover {
  background: #cbd5e0;
}

.btn-confirm {
  padding: 0.5rem 1rem;
  background: #48bb78;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.btn-confirm:hover:not(:disabled) {
  background: #38a169;
}

.btn-confirm:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>