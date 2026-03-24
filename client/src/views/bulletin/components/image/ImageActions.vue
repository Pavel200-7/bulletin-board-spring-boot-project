<!-- src/views/bulletin/components/image/ImageActions.vue -->
<template>
  <div class="image-actions">
    <button 
      v-if="showSetMain && !isMain && totalImages > 1" 
      class="btn-set-main" 
      @click.stop="$emit('set-main')"
      title="Сделать главным"
    >
      ⭐
    </button>
    <button 
      class="btn-delete" 
      @click.stop="$emit('delete')"
      title="Удалить"
    >
      ✕
    </button>
  </div>
</template>

<script setup>
defineProps({
  showSetMain: {
    type: Boolean,
    default: true
  },
  isMain: {
    type: Boolean,
    default: false
  },
  totalImages: {
    type: Number,
    default: 0
  }
})

defineEmits(['set-main', 'delete'])
</script>

<style scoped>
.image-actions {
  position: absolute;
  top: 0.25rem;
  right: 0.25rem;
  display: flex;
  gap: 0.25rem;
  opacity: 0;
  transition: opacity 0.2s;
  z-index: 10;
}

.image-actions:hover {
  opacity: 1;
}

.btn-set-main, .btn-delete {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  border: none;
  cursor: pointer;
  font-size: 0.875rem;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.btn-set-main {
  background: #f59e0b;
  color: white;
}

.btn-set-main:hover {
  background: #d97706;
  transform: scale(1.05);
}

.btn-delete {
  background: #e53e3e;
  color: white;
}

.btn-delete:hover {
  background: #c53030;
  transform: scale(1.05);
}
</style>