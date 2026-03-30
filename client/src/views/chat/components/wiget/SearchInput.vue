<!-- src/views/chat/components/wiget/SearchInput.vue -->
<template>
  <div class="search-input">
    <input
      :value="modelValue"
      type="text"
      placeholder="Поиск по имени пользователя..."
      class="search-field"
      @input="handleInput"
    />
    <button 
      v-if="modelValue" 
      class="clear-btn" 
      @click="clearSearch"
    >
      ✕
    </button>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue', 'search'])

let debounceTimer = null

const handleInput = (event) => {
  const value = event.target.value
  emit('update:modelValue', value)
  
  if (debounceTimer) clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => {
    emit('search', value)
  }, 500)
}

const clearSearch = () => {
  emit('update:modelValue', '')
  emit('search', '')
}
</script>

<style scoped>
.search-input {
  position: relative;
  width: 100%;
}

.search-field {
  width: 100%;
  padding: 0.75rem 2rem 0.75rem 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.search-field:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

.clear-btn {
  position: absolute;
  right: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  color: #a0aec0;
  font-size: 1rem;
  padding: 0.25rem;
  border-radius: 50%;
  transition: all 0.2s;
}

.clear-btn:hover {
  color: #4a5568;
  background: #edf2f7;
}
</style>