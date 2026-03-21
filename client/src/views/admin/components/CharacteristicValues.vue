<!-- src/views/admin/components/CharacteristicValues.vue -->
<template>
  <div class="characteristic-values">
    <div class="values-header">
      <span class="values-title">Значения</span>
      <button class="add-value-btn" @click="openAddModal">+</button>
    </div>
    
    <div class="values-content">
      <div v-if="loading" class="values-loading">
        Загрузка...
      </div>
      <div v-else-if="values.length === 0" class="values-empty">
        Нет значений
      </div>
      <div v-else class="values-list">
        <div v-for="value in values" :key="value.id" class="value-item">
          <span class="value-name">{{ value.name }}</span>
          <div class="value-actions">
            <button class="value-action" @click="openRenameModal(value)">✏️</button>
            <button class="value-action" @click="handleDelete(value)">🗑️</button>
          </div>
        </div>
      </div>
    </div>
    
    <CategoryFormModal
      :visible="modalVisible"
      :mode="modalMode"
      title="Значение характеристики"
      label="Название значения"
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
import { useCharacteristicValue } from '@/composables/useCharacteristicValue'
import CategoryFormModal from './CategoryFormModal.vue'

const props = defineProps({
  characteristic: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['refresh'])

const { 
  values, 
  loading, 
  fetchCharacteristicValues, 
  createCharacteristicValue, 
  renameCharacteristicValue, 
  deleteCharacteristicValue 
} = useCharacteristicValue()

// Состояние модального окна
const modalVisible = ref(false)
const modalMode = ref('create')
const modalInitialName = ref('')
const modalOnSubmit = ref(null)
const currentValue = ref(null)

const modalPlaceholder = computed(() => 
  modalMode.value === 'create' ? 'Введите значение' : 'Новое название'
)
const modalSubmitText = computed(() => 
  modalMode.value === 'create' ? 'Создать' : 'Сохранить'
)

// Загрузка значений при изменении характеристики
watch(() => props.characteristic, async (newChar) => {
  if (newChar && newChar.id) {
    try {
      await fetchCharacteristicValues(newChar.id)
    } catch (err) {
      console.error('Ошибка загрузки значений:', err)
    }
  }
}, { immediate: true })

const openAddModal = () => {
  modalMode.value = 'create'
  modalInitialName.value = ''
  currentValue.value = null
  modalOnSubmit.value = async (name) => {
    await createCharacteristicValue(props.characteristic.id, name)
    await fetchCharacteristicValues(props.characteristic.id)
  }
  modalVisible.value = true
}

const openRenameModal = (value) => {
  modalMode.value = 'edit'
  modalInitialName.value = value.name
  currentValue.value = value
  modalOnSubmit.value = async (name) => {
    await renameCharacteristicValue(currentValue.value.id, name)
    currentValue.value.name = name
  }
  modalVisible.value = true
}

const handleDelete = async (value) => {
  if (confirm(`Удалить значение "${value.name}"?`)) {
    try {
      await deleteCharacteristicValue(props.characteristic.id, value.id)
      await fetchCharacteristicValues(props.characteristic.id)
      emit('refresh')
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
</script>

<style scoped>
.characteristic-values {
  margin-top: 0.5rem;
  margin-left: 1rem;
  padding-left: 0.5rem;
  border-left: 2px solid #e2e8f0;
}

.values-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

.values-title {
  font-size: 0.75rem;
  color: #a0aec0;
  font-weight: 500;
}

.add-value-btn {
  padding: 0.125rem 0.375rem;
  background: #48bb78;
  color: white;
  border: none;
  cursor: pointer;
  font-size: 0.7rem;
}

.add-value-btn:hover {
  background: #38a169;
}

.values-content {
  min-height: 30px;
}

.values-loading, .values-empty {
  font-size: 0.7rem;
  padding: 0.25rem 0;
  color: #a0aec0;
}

.values-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.value-item {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  background: #f7fafc;
  padding: 0.25rem 0.5rem;
  font-size: 0.75rem;
  color: #4a5568;
}

.value-name {
  font-size: 0.75rem;
}

.value-actions {
  display: flex;
  gap: 0.25rem;
}

.value-action {
  padding: 0.125rem 0.25rem;
  background: transparent;
  border: none;
  cursor: pointer;
  font-size: 0.65rem;
  color: #a0aec0;
}

.value-action:hover {
  color: #4a5568;
}
</style>