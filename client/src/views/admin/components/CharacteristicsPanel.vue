<!-- src/views/admin/components/CharacteristicsPanel.vue -->
<template>
  <div class="characteristics-panel">
    <div class="panel-header">
      <h3>Характеристики</h3>
      <button 
        v-if="selectedCategory" 
        @click="handleAddCharacteristic" 
        class="btn-add"
      >
        + Характеристика
      </button>
    </div>
    
    <div class="panel-content">
      <div v-if="!selectedCategory" class="placeholder">
        Выберите категорию
      </div>
      <div v-else-if="loading" class="loading">
        Загрузка...
      </div>
      <div v-else-if="characteristics.length === 0" class="empty">
        У этой категории нет характеристик
      </div>
      <CharacteristicItem 
        v-for="char in characteristics" 
        :key="char.id"
        :characteristic="char"
        @rename="handleRename"
        @delete="handleDelete"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import CharacteristicItem from './CharacteristicItem.vue'
import { useCharacteristic } from '@/composables/useCharacteristic'

const props = defineProps({
  selectedCategory: {
    type: Object,
    default: null
  }
})

const { characteristics, loading, fetchCategoryCharacteristics, createCharacteristic } = useCharacteristic()

watch(() => props.selectedCategory, async (category) => {
  if (category) {
    await fetchCategoryCharacteristics(category.id)
  }
}, { immediate: true })

const handleAddCharacteristic = async () => {
  const name = prompt('Введите название характеристики:')
  if (name && name.trim()) {
    await createCharacteristic(props.selectedCategory.id, name.trim())
    await fetchCategoryCharacteristics(props.selectedCategory.id)
  }
}

const handleRename = async (id, newName) => {
  // будет реализовано
  console.log('Rename characteristic:', id, newName)
}

const handleDelete = async (id) => {
  // будет реализовано
  console.log('Delete characteristic:', id)
}
</script>

<style scoped>
.characteristics-panel {
  background: white;
  border-radius: 8px;
  padding: 1rem;
  height: 100%;
  overflow-y: auto;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid #e2e8f0;
}

.panel-header h3 {
  margin: 0;
  color: #333;
}

.btn-add {
  padding: 0.25rem 0.75rem;
  background: #48bb78;
  color: white;
  border: none;
  cursor: pointer;
  font-size: 0.875rem;
}

.btn-add:hover {
  background: #38a169;
}

.placeholder, .loading, .empty {
  padding: 2rem;
  text-align: center;
  color: #a0aec0;
}
</style>