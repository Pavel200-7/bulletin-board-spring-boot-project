<!-- src/views/chat/components/ChatInput.vue -->
<template>
  <div class="chat-input-container">
    <div class="input-wrapper">
      <textarea
        v-model="localMessage"
        placeholder="Сообщение..."
        class="message-input"
        rows="1"
        @keyup.enter.prevent="handleSend"
      ></textarea>
      <button class="send-btn" @click="handleSend" :disabled="!localMessage.trim()">
        📤
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue', 'send'])

const localMessage = ref(props.modelValue)

watch(() => props.modelValue, (newVal) => {
  localMessage.value = newVal
})

watch(localMessage, (newVal) => {
  emit('update:modelValue', newVal)
})

const handleSend = () => {
  if (!localMessage.value.trim()) return
  emit('send', localMessage.value)
  localMessage.value = ''
}
</script>

<style scoped>
.chat-input-container {
  background: white;
  border-top: 1px solid #e2e8f0;
  padding: 1rem;
}

.input-wrapper {
  display: flex;
  gap: 0.5rem;
  align-items: flex-end;
}

.message-input {
  flex: 1;
  padding: 0.75rem;
  border: 1px solid #e2e8f0;
  border-radius: 24px;
  font-size: 0.875rem;
  resize: none;
  font-family: inherit;
  max-height: 120px;
}

.message-input:focus {
  outline: none;
  border-color: #667eea;
}

.send-btn {
  width: 40px;
  height: 40px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.send-btn:hover:not(:disabled) {
  background: #5a67d8;
  transform: scale(1.05);
}

.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>