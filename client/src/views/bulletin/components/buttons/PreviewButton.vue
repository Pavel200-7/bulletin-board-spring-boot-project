<!-- src/views/bulletin/components/buttons/PreviewButton.vue -->
<template>
  <button 
    class="btn-preview" 
    @click="$emit('click')"
  >
    Предпросмотр
  </button>
</template>

<script setup>
defineEmits(['click'])
</script>

<style scoped>
.btn-preview {
  padding: 0.5rem 1rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.btn-preview:hover {
  background: #5a67d8;
}
</style>