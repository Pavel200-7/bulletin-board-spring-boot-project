<!-- src/views/chat/components/EditMessageModal.vue -->
<template>
  <div v-if="show" class="modal-overlay" @click.self="close">
    <div class="edit-modal">
      <div class="modal-header">
        <h3>Редактирование сообщения</h3>
        <button class="close-btn" @click="close">✕</button>
      </div>
      <div class="modal-body">
        <textarea
          ref="textareaRef"
          v-model="editedText"
          class="edit-textarea"
          rows="3"
          @keyup.enter.prevent="save"
        ></textarea>
      </div>
      <div class="modal-footer">
        <button class="btn-cancel" @click="close">Отмена</button>
        <button class="btn-save" @click="save" :disabled="!editedText.trim()">
          Сохранить
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue'

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  currentText: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['save', 'close'])

const editedText = ref('')
const textareaRef = ref(null)

watch(() => props.currentText, (newText) => {
  editedText.value = newText
}, { immediate: true })

watch(() => props.show, async (newVal) => {
  if (newVal) {
    await nextTick()
    textareaRef.value?.focus()
  }
})

const save = () => {
  if (!editedText.value.trim()) return
  console.log('Sending 111111111111111111111')
  emit('save', editedText.value)
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

.edit-modal {
  background: white;
  width: 500px;
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  background: #f7fafc;
  border-bottom: 1px solid #e2e8f0;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.125rem;
  font-weight: 600;
  color: #2d3748;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.25rem;
  cursor: pointer;
  color: #a0aec0;
  padding: 0.25rem;
  border-radius: 4px;
  transition: all 0.2s;
}

.close-btn:hover {
  background: #edf2f7;
  color: #4a5568;
}

.modal-body {
  padding: 1.5rem;
}

.edit-textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.875rem;
  font-family: inherit;
  resize: vertical;
}

.edit-textarea:focus {
  outline: none;
  border-color: #667eea;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  padding: 1rem 1.5rem;
  border-top: 1px solid #e2e8f0;
}

.btn-cancel,
.btn-save {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.btn-cancel {
  background: #e2e8f0;
  color: #4a5568;
}

.btn-cancel:hover {
  background: #cbd5e0;
}

.btn-save {
  background: #48bb78;
  color: white;
}

.btn-save:hover:not(:disabled) {
  background: #38a169;
}

.btn-save:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>