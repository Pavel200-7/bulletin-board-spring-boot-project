<!-- src/views/bulletin/components/CharacteristicsEditor.vue -->
<template>
  <div class="characteristics-editor">
    <label>Характеристики</label>
    
    <div v-if="!categoryId" class="empty-hint">
      Сначала выберите категорию
    </div>
    
    <div v-else-if="loading" class="loading">
      Загрузка характеристик...
    </div>
    
    <div v-else-if="characteristics.length === 0" class="empty-hint">
      Для выбранной категории нет характеристик
    </div>
    
    <div v-else class="characteristics-list">
      <div
        v-for="char in characteristics"
        :key="char.id"
        class="characteristic-item"
      >
        <label class="char-label">{{ char.name }}</label>
        <select
          :value="getSelectedValue(char.id)"
          @change="updateCharacteristic(char.id, $event.target.value)"
          class="char-select"
        >
          <option value="">Выберите значение</option>
          <option
            v-for="value in charValues[char.id]"
            :key="value.id"
            :value="value.id"
          >
            {{ value.name }}
          </option>
        </select>
      </div>
    </div>
    
    <div v-if="error" class="error">{{ error }}</div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useCharacteristic } from '@/composables/useCharacteristic'
import { useCharacteristicValue } from '@/composables/useCharacteristicValue'

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  },
  categoryId: {
    type: String,
    default: ''
  },
  error: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue'])

const { characteristics, fetchCategoryCharacteristics, loading: charsLoading } = useCharacteristic()
const { fetchCharacteristicValues } = useCharacteristicValue()

const loading = ref(false)
const charValues = ref({})

// Сохраняем предыдущий categoryId для отслеживания изменений
const previousCategoryId = ref('')

// Загружаем характеристики категории
watch(() => props.categoryId, async (newCategoryId, oldCategoryId) => {
  console.log('CharacteristicsEditor: categoryId изменился', { old: oldCategoryId, new: newCategoryId })
  
  // Если категория изменилась, сбрасываем выбранные значения
  if (oldCategoryId && newCategoryId !== oldCategoryId) {
    console.log('Категория изменилась, сбрасываем характеристики')
    emit('update:modelValue', [])
  }
  
  if (!newCategoryId) return
  
  console.log('Загрузка характеристик для категории:', newCategoryId)
  loading.value = true
  try {
    await fetchCategoryCharacteristics(newCategoryId)
    console.log('Загружены характеристики:', characteristics.value)
    
    // Загружаем значения для каждой характеристики
    for (const char of characteristics.value) {
      console.log('Загрузка значений для характеристики:', char.id)
      const response = await fetchCharacteristicValues(char.id)
      charValues.value[char.id] = response.data?.characteristicValueResponse || []
      console.log('Загружены значения:', charValues.value[char.id])
    }
  } catch (err) {
    console.error('Ошибка загрузки характеристик:', err)
  } finally {
    loading.value = false
  }
  
  previousCategoryId.value = newCategoryId
}, { immediate: true })

const getSelectedValue = (characteristicId) => {
  const found = props.modelValue.find(
    item => item.characteristicId === characteristicId
  )
  return found?.characteristicValueId || ''
}

const updateCharacteristic = (characteristicId, valueId) => {
  console.log('updateCharacteristic:', { characteristicId, valueId })
  
  const current = [...props.modelValue]
  const index = current.findIndex(item => item.characteristicId === characteristicId)
  
  if (valueId) {
    const newItem = {
      characteristicId: characteristicId,
      characteristicValueId: valueId
    }
    
    if (index >= 0) {
      current[index] = newItem
    } else {
      current.push(newItem)
    }
  } else {
    if (index >= 0) {
      current.splice(index, 1)
    }
  }
  
  console.log('Updated characteristics:', current)
  emit('update:modelValue', current)
}
</script>

<style scoped>
.characteristics-editor {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.characteristics-editor label {
  font-weight: 500;
  color: #4a5568;
}

.characteristics-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
  padding: 1rem;
}

.characteristic-item {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.char-label {
  width: 150px;
  font-weight: 500;
  color: #4a5568;
  margin: 0;
}

.char-select {
  flex: 1;
  padding: 0.5rem;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
  font-size: 0.875rem;
}

.char-select:focus {
  outline: none;
  border-color: #667eea;
}

.empty-hint, .loading {
  padding: 1rem;
  text-align: center;
  color: #a0aec0;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
}

.error {
  color: #e53e3e;
  font-size: 0.75rem;
}
</style>