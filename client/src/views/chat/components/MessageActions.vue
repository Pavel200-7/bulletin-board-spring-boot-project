<!-- src/views/chat/components/MessageActions.vue -->
<template>
  <div v-if="show" class="context-menu" :style="menuPosition" @click.stop>
    <div class="context-menu-item" @click="handleEdit">
      ✏️ Изменить
    </div>
    <div class="context-menu-item delete" @click="handleDelete">
      🗑️ Удалить
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  position: {
    type: Object,
    default: () => ({ x: 0, y: 0 })
  }
})

const emit = defineEmits(['edit', 'delete', 'close'])

const menuPosition = computed(() => ({
  left: `${props.position.x}px`,
  top: `${props.position.y}px`
}))

const handleEdit = () => {
  emit('edit')
  emit('close')
}

const handleDelete = () => {
  emit('delete')
  emit('close')
}
</script>

<style scoped>
.context-menu {
  position: fixed;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  min-width: 150px;
  overflow: hidden;
  animation: fadeIn 0.1s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.context-menu-item {
  padding: 10px 16px;
  cursor: pointer;
  transition: background 0.2s;
  font-size: 14px;
  color: #2d3748;
}

.context-menu-item:hover {
  background: #f7fafc;
}

.context-menu-item.delete {
  color: #e53e3e;
}

.context-menu-item.delete:hover {
  background: #fed7d7;
}
</style>