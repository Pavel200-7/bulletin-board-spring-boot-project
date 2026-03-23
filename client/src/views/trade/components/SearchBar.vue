<!-- src/views/trade/components/SearchBar.vue -->
<template>
  <div class="search-bar">
    <input
      v-model="searchQuery"
      type="text"
      placeholder="Поиск по названию..."
      class="search-input"
      @input="handleInput"
    />
    <button v-if="searchQuery" class="clear-btn" @click="clearSearch">✕</button>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const emit = defineEmits(['search'])
const searchQuery = ref('')

let debounceTimer = null

const handleInput = () => {
  if (debounceTimer) clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => {
    emit('search', searchQuery.value)
  }, 500)
}

const clearSearch = () => {
  searchQuery.value = ''
  emit('search', '')
}
</script>

<style scoped>
.search-bar {
  position: relative;
  max-width: 400px;
}

.search-input {
  width: 100%;
  padding: 0.5rem 2rem 0.5rem 0.75rem;
  border: 1px solid #e2e8f0;
  font-size: 0.875rem;
}

.search-input:focus {
  outline: none;
  border-color: #667eea;
}

.clear-btn {
  position: absolute;
  right: 0.5rem;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  color: #a0aec0;
  font-size: 0.875rem;
}

.clear-btn:hover {
  color: #4a5568;
}
</style>