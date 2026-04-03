<!-- src/views/chat/components/messages/TextMessage.vue -->
<template>
  <div class="text-message" :class="{ 'own-message': isOwn }">
    <template v-if="isEditing">
      <textarea
        ref="editTextareaRef"
        v-model="localEditText"
        class="edit-input"
        :class="{ 'own-edit': isOwn }"
        @keyup.enter.prevent="save"
        @keyup.esc="cancel"
      ></textarea>
      <div class="edit-actions">
        <button @click="save" class="save-btn">✓</button>
        <button @click="cancel" class="cancel-btn">✕</button>
      </div>
    </template>
    <template v-else>
      <div class="message-content">
        {{ message.content }}
        <span v-if="message.updated" class="edited-badge">(изменено)</span>
      </div>
      <div class="message-footer">
        <span class="message-time">{{ formatTime(message.createdAt) }}</span>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue'

const props = defineProps({
  message: {
    type: Object,
    required: true
  },
  isOwn: {
    type: Boolean,
    default: false
  },
  editingId: {
    type: String,
    default: null
  },
  editText: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['start-edit', 'save-edit', 'cancel-edit', 'update-edit-text'])

const isEditing = ref(false)
const localEditText = ref('')
const editTextareaRef = ref(null)

watch(() => props.editingId, (newId) => {
  isEditing.value = newId === props.message.id
  if (isEditing.value) {
    localEditText.value = props.editText
    nextTick(() => {
      editTextareaRef.value?.focus()
    })
  }
})

watch(() => props.editText, (newText) => {
  if (isEditing.value) {
    localEditText.value = newText
  }
})

/**
 * Форматирование времени
 * @param {string} dateStr - строка с датой (ISO format)
 * @returns {string} - отформатированное время
 */
const formatTime = (dateStr) => {
  if (!dateStr) return ''
  
  try {
    const date = new Date(dateStr)
    // Проверяем, что дата валидная
    if (isNaN(date.getTime())) return ''
    
    return date.toLocaleTimeString('ru-RU', {
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch (err) {
    console.error('Error formatting date:', err)
    return ''
  }
}

/**
 * Форматирование полной даты для всплывающей подсказки
 * @param {string} dateStr - строка с датой (ISO format)
 * @returns {string} - отформатированная дата и время
 */
const formatFullDate = (dateStr) => {
  if (!dateStr) return ''
  
  try {
    const date = new Date(dateStr)
    if (isNaN(date.getTime())) return ''
    
    return date.toLocaleString('ru-RU', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch (err) {
    return ''
  }
}

const save = () => {
  if (localEditText.value.trim()) {
    emit('save-edit', props.message.id, localEditText.value.trim())
  }
}

const cancel = () => {
  emit('cancel-edit')
}

const startEdit = () => {
  emit('start-edit', props.message.id, props.message.content)
}
</script>

<style scoped>
.text-message {
  width: 100%;
}

.message-content {
  font-size: 0.875rem;
  line-height: 1.4;
  white-space: pre-wrap;
  margin-bottom: 0.25rem;
}

.message-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 0.25rem;
}

.message-time {
  font-size: 0.65rem;
  color: #a0aec0;
  cursor: help;
}

.own-message .message-time {
  color: rgba(255, 255, 255, 0.7);
}

.edited-badge {
  font-size: 0.65rem;
  margin-left: 0.25rem;
  opacity: 0.7;
}

.edit-input {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.875rem;
  font-family: inherit;
  resize: vertical;
}

.edit-input.own-edit {
  background: #f8f9fa;
  color: #2d3748;
}

.edit-actions {
  display: flex;
  gap: 0.5rem;
  margin-top: 0.5rem;
  justify-content: flex-end;
}

.save-btn, .cancel-btn {
  padding: 0.25rem 0.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.75rem;
}

.save-btn {
  background: #48bb78;
  color: white;
}

.save-btn:hover {
  background: #38a169;
}

.cancel-btn {
  background: #e2e8f0;
  color: #4a5568;
}

.cancel-btn:hover {
  background: #cbd5e0;
}
</style>