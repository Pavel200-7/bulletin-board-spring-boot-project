<!-- src/views/bulletin/components/buttons/ApproveButton.vue -->
<template>
  <button 
    class="btn-approve" 
    :disabled="disabled || loading"
    @click="$emit('click')"
  >
    {{ loading ? 'Проверка...' : 'Проверить и подтвердить' }}
  </button>
</template>

<script setup>
defineProps({
  disabled: {
    type: Boolean,
    default: false
  },
  loading: {
    type: Boolean,
    default: false
  }
})

defineEmits(['click'])
</script>

<style scoped>
.btn-approve {
  padding: 0.5rem 1rem;
  background: #f59e0b;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.btn-approve:hover:not(:disabled) {
  background: #d97706;
}

.btn-approve:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>