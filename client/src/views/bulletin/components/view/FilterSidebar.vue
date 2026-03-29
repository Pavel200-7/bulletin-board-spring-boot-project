<!-- src/views/bulletin/components/view/sections/FilterSidebar.vue -->
<template>
  <div class="filter-sidebar">
    <h3 class="filter-title">Фильтры</h3>
    
    <!-- Фильтр по цене -->
    <div class="filter-section">
      <h4 class="filter-section-title">Цена</h4>
      <div class="price-filters">
        <input
          v-model.number="localMinPrice"
          type="number"
          placeholder="от"
          class="filter-input"
          @input="handlePriceChange"
        />
        <span>—</span>
        <input
          v-model.number="localMaxPrice"
          type="number"
          placeholder="до"
          class="filter-input"
          @input="handlePriceChange"
        />
      </div>
    </div>

    <!-- Фильтр по характеристикам -->
    <div v-if="characteristics.length > 0" class="filter-section">
      <h4 class="filter-section-title">Характеристики</h4>
      <div class="characteristics-filters">
        <div
          v-for="char in characteristics"
          :key="char.id"
          class="characteristic-filter"
        >
          <div class="char-header" @click="toggleChar(char.id)">
            <span class="char-name">{{ char.name }}</span>
            <span class="expand-icon">{{ expandedChars[char.id] ? '▼' : '▶' }}</span>
          </div>
          <div v-if="expandedChars[char.id]" class="char-values">
            <label
              v-for="value in charValues[char.id]"
              :key="value.id"
              class="char-value-label"
            >
              <input
                type="checkbox"
                :value="value.id"
                v-model="selectedValues[char.id]"
                @change="handleCharChange"
              />
              <span>{{ value.name }}</span>
            </label>
          </div>
        </div>
      </div>
    </div>

    <!-- Кнопки действий -->
    <div class="filter-actions">
      <button class="btn-reset" @click="resetFilters">Сбросить</button>
      <button class="btn-apply" @click="applyFilters">Применить</button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useCharacteristic } from '@/composables/useCharacteristic'
import { useCharacteristicValue } from '@/composables/useCharacteristicValue'

const props = defineProps({
  categoryId: {
    type: String,
    default: ''
  },
  modelValue: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['update:modelValue', 'apply'])

const { characteristics, fetchCategoryCharacteristics } = useCharacteristic()
const { fetchCharacteristicValues } = useCharacteristicValue()

const localMinPrice = ref(props.modelValue.minPrice ?? '')
const localMaxPrice = ref(props.modelValue.maxPrice ?? '')
const selectedValues = ref({})
const expandedChars = ref({})
const charValues = ref({})

// Загружаем характеристики категории
watch(() => props.categoryId, async (newCategoryId) => {
  if (!newCategoryId) return
  
  try {
    await fetchCategoryCharacteristics(newCategoryId)
    
    // Инициализируем выбранные значения из modelValue
    const characteristicValueIds = props.modelValue.characteristicValueIds || []
    const idsArray = Array.isArray(characteristicValueIds) 
      ? characteristicValueIds 
      : (characteristicValueIds ? [characteristicValueIds] : [])
    
    // Загружаем значения для каждой характеристики
    for (const char of characteristics.value) {
      const response = await fetchCharacteristicValues(char.id)
      charValues.value[char.id] = response.data?.characteristicValueResponse || []
      expandedChars.value[char.id] = false
      
      // Устанавливаем выбранные значения
      selectedValues.value[char.id] = idsArray.filter(id => 
        charValues.value[char.id]?.some(v => v.id === id)
      )
    }
  } catch (err) {
    console.error('Ошибка загрузки характеристик:', err)
  }
}, { immediate: true })

const handlePriceChange = () => {
  const timeout = setTimeout(() => {
    applyFilters()
  }, 500)
  return () => clearTimeout(timeout)
}

const handleCharChange = () => {
  applyFilters()
}

const toggleChar = (charId) => {
  expandedChars.value[charId] = !expandedChars.value[charId]
}

const resetFilters = () => {
  localMinPrice.value = ''
  localMaxPrice.value = ''
  selectedValues.value = {}
  expandedChars.value = {}
  
  applyFilters()
}

const applyFilters = () => {
  const characteristicValueIds = Object.values(selectedValues.value)
    .flat()
    .filter(Boolean)
  
  const filters = {
    minPrice: localMinPrice.value ? Number(localMinPrice.value) : null,
    maxPrice: localMaxPrice.value ? Number(localMaxPrice.value) : null,
    characteristicValueIds: characteristicValueIds.length > 0 ? characteristicValueIds : null
  }
  
  emit('update:modelValue', filters)
  emit('apply', filters)
}
</script>

<style scoped>
.filter-sidebar {
  background: white;
  border-radius: 12px;
  padding: 1rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 1rem;
}

.filter-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: #2d3748;
  margin: 0 0 1rem 0;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid #e2e8f0;
}

.filter-section {
  margin-bottom: 1.5rem;
}

.filter-section-title {
  font-size: 0.875rem;
  font-weight: 600;
  color: #4a5568;
  margin: 0 0 0.5rem 0;
}

.price-filters {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.filter-input {
  flex: 1;
  padding: 0.5rem;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 0.875rem;
}

.filter-input:focus {
  outline: none;
  border-color: #667eea;
}

.characteristics-filters {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.characteristic-filter {
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  overflow: hidden;
}

.char-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem;
  background: #f7fafc;
  cursor: pointer;
  font-size: 0.875rem;
  font-weight: 500;
  color: #2d3748;
}

.char-header:hover {
  background: #edf2f7;
}

.expand-icon {
  color: #a0aec0;
  font-size: 0.75rem;
}

.char-values {
  padding: 0.5rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.char-value-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
  color: #4a5568;
  cursor: pointer;
}

.char-value-label input {
  cursor: pointer;
}

.char-value-label:hover {
  color: #2d3748;
}

.filter-actions {
  display: flex;
  gap: 0.5rem;
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #e2e8f0;
}

.btn-reset,
.btn-apply {
  flex: 1;
  padding: 0.5rem;
  border: none;
  border-radius: 6px;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-reset {
  background: #e2e8f0;
  color: #4a5568;
}

.btn-reset:hover {
  background: #cbd5e0;
}

.btn-apply {
  background: #667eea;
  color: white;
}

.btn-apply:hover {
  background: #5a67d8;
}
</style>