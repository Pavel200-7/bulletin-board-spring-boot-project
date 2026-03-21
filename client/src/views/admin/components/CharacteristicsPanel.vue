<!-- src/views/admin/components/CharacteristicsPanel.vue -->
<template>
  <div class="characteristics-panel">
    <div class="panel-header">
      <h3>Характеристики</h3>
      <button 
        v-if="selectedCategory" 
        @click="openAddModal" 
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
      <div v-else class="characteristics-list">
        <div v-for="char in characteristics" :key="char.id" class="characteristic-item">
          <div class="char-header">
            <span class="char-name">{{ char.name }}</span>
            <div class="char-actions">
              <button class="action-btn" @click="openRenameModal(char)">✏️</button>
              <button class="action-btn" @click="handleDelete(char)">🗑️</button>
            </div>
          </div>
          
          <CharacteristicValues 
            :characteristic="char" 
            @refresh="handleRefreshValues"
          />
        </div>
      </div>
    </div>
    
    <CategoryFormModal
      :visible="modalVisible"
      :mode="modalMode"
      :title="modalTitle"
      :label="modalLabel"
      :placeholder="modalPlaceholder"
      :submit-text="modalSubmitText"
      :initial-name="modalInitialName"
      :on-submit="modalOnSubmit || (async () => {})"
      @close="modalVisible = false"
    />
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { useCharacteristic } from '@/composables/useCharacteristic'
import CharacteristicValues from './CharacteristicValues.vue'
import CategoryFormModal from './CategoryFormModal.vue'

const props = defineProps({
  selectedCategory: {
    type: Object,
    default: null
  }
})

const { 
  characteristics, 
  loading, 
  fetchCategoryCharacteristics, 
  createCharacteristic, 
  renameCharacteristic, 
  deleteCharacteristic 
} = useCharacteristic()

// Состояние модального окна
const modalVisible = ref(false)
const modalMode = ref('create')
const modalInitialName = ref('')
const modalOnSubmit = ref(null)
const currentCharacteristic = ref(null)

const modalTitle = computed(() => 
  modalMode.value === 'create' ? 'Создание характеристики' : 'Редактирование характеристики'
)
const modalLabel = computed(() => 'Название характеристики')
const modalPlaceholder = computed(() => 
  modalMode.value === 'create' ? 'Введите название характеристики' : 'Новое название'
)
const modalSubmitText = computed(() => 
  modalMode.value === 'create' ? 'Создать' : 'Сохранить'
)

// Загрузка характеристик при выборе категории
watch(() => props.selectedCategory, async (category) => {
  if (category && category.id) {
    try {
      await fetchCategoryCharacteristics(category.id)
    } catch (err) {
      console.error('Ошибка загрузки:', err)
    }
  }
}, { immediate: true })

const openAddModal = () => {
  modalMode.value = 'create'
  modalInitialName.value = ''
  currentCharacteristic.value = null
  modalOnSubmit.value = async (name) => {
    await createCharacteristic(props.selectedCategory.id, name)
    await fetchCategoryCharacteristics(props.selectedCategory.id)
  }
  modalVisible.value = true
}

const openRenameModal = (characteristic) => {
  modalMode.value = 'edit'
  modalInitialName.value = characteristic.name
  currentCharacteristic.value = characteristic
  modalOnSubmit.value = async (name) => {
    await renameCharacteristic(currentCharacteristic.value.id, name)
    currentCharacteristic.value.name = name
  }
  modalVisible.value = true
}

const handleDelete = async (characteristic) => {
  if (confirm(`Удалить характеристику "${characteristic.name}"?`)) {
    try {
      await deleteCharacteristic(props.selectedCategory.id, characteristic.id)
      await fetchCategoryCharacteristics(props.selectedCategory.id)
    } catch (err) {
      const response = err.response?.data
      if (response?.validationErrors) {
        const firstError = Object.values(response.validationErrors)[0]
        alert(firstError)
      } else if (response?.message) {
        alert(response.message)
      } else {
        alert('Ошибка при удалении')
      }
    }
  }
}

const handleRefreshValues = () => {}
</script>

<style scoped>
.characteristics-panel {
  background: white;
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

.characteristics-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.characteristic-item {
  border: 1px solid #e2e8f0;
  padding: 0.5rem;
}

.char-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.char-name {
  font-weight: 500;
  color: #2d3748;
}

.char-actions {
  display: flex;
  gap: 0.5rem;
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
</style>