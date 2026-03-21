<!-- src/views/admin/components/CharacteristicItem.vue -->
<template>
  <div class="characteristic-item">
    <div class="item-header">
      <span class="char-name">{{ characteristic.name }}</span>
      <div class="item-actions">
        <button class="action-btn" @click="handleAddValue">+ значение</button>
        <button class="action-btn" @click="handleRename">✏️</button>
        <button class="action-btn" @click="handleDelete">🗑️</button>
      </div>
    </div>
    
    <div v-if="isExpanded" class="item-values">
      <div v-if="values.length === 0" class="no-values">
        Нет значений. Добавьте первое!
      </div>
      <div v-for="value in values" :key="value.id" class="value-item">
        <span>{{ value.name }}</span>
        <div class="value-actions">
          <button class="value-action" @click="handleRenameValue(value)">✏️</button>
          <button class="value-action" @click="handleDeleteValue(value)">🗑️</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useCharacteristicValue } from '@/composables/useCharacteristicValue'

const props = defineProps({
  characteristic: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['rename', 'delete'])

const isExpanded = ref(true)
const { values, fetchCharacteristicValues, createCharacteristicValue } = useCharacteristicValue()

const handleAddValue = async () => {
  const name = prompt('Введите значение:')
  if (name && name.trim()) {
    await createCharacteristicValue(props.characteristic.id, name.trim())
    await fetchCharacteristicValues(props.characteristic.id)
  }
}

const handleRename = () => {
  const newName = prompt('Новое название:', props.characteristic.name)
  if (newName && newName.trim() && newName !== props.characteristic.name) {
    emit('rename', props.characteristic.id, newName.trim())
  }
}

const handleDelete = () => {
  if (confirm(`Удалить характеристику "${props.characteristic.name}"?`)) {
    emit('delete', props.characteristic.id)
  }
}

const handleRenameValue = (value) => {
  // будет реализовано
  console.log('Rename value:', value)
}

const handleDeleteValue = (value) => {
  // будет реализовано
  console.log('Delete value:', value)
}
</script>

<style scoped>
.characteristic-item {
  border-bottom: 1px solid #e2e8f0;
  padding: 0.75rem 0;
}

.characteristic-item:last-child {
  border-bottom: none;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.char-name {
  font-weight: 500;
  color: #2d3748;
  cursor: pointer;
}

.item-actions {
  display: none;
  gap: 0.5rem;
}

.characteristic-item:hover .item-actions {
  display: flex;
}

.action-btn {
  padding: 0.125rem 0.375rem;
  background: transparent;
  border: none;
  cursor: pointer;
  font-size: 0.75rem;
  color: #718096;
}

.action-btn:hover {
  color: #4a5568;
}

.item-values {
  margin-top: 0.5rem;
  margin-left: 1rem;
  padding-left: 0.5rem;
  border-left: 2px solid #e2e8f0;
}

.value-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.25rem 0;
  font-size: 0.875rem;
  color: #4a5568;
}

.value-actions {
  display: none;
  gap: 0.25rem;
}

.value-item:hover .value-actions {
  display: flex;
}

.value-action {
  padding: 0.125rem 0.25rem;
  background: transparent;
  border: none;
  cursor: pointer;
  font-size: 0.7rem;
  color: #a0aec0;
}

.value-action:hover {
  color: #4a5568;
}

.no-values {
  font-size: 0.75rem;
  color: #a0aec0;
  padding: 0.25rem 0;
}
</style>