<!-- src/views/bulletin/components/buttons/PublishButton.vue -->
<template>
  <button 
    class="btn-publish" 
    :disabled="disabled || loading"
    @click="$emit('click')"
  >
    {{ loading ? 'Публикация...' : 'Опубликовать' }}
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
.btn-publish {
  padding: 0.5rem 1rem;
  background: #48bb78;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.btn-publish:hover:not(:disabled) {
  background: #38a169;
}

.btn-publish:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>