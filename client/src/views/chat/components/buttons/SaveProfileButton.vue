<template>
  <button class="btn-save" :disabled="disabled || loading" @click="$emit('click')">
    {{ loading ? 'Сохранение...' : text }}
  </button>
</template>

<script setup>
defineProps({
  text: {
    type: String,
    default: 'Сохранить'
  },
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
.btn-save {
  padding: 0.5rem 1.5rem;
  background: #48bb78;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.875rem;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-save:hover:not(:disabled) {
  background: #38a169;
  transform: translateY(-1px);
}

.btn-save:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>