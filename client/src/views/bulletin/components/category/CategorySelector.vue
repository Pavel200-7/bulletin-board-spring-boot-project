<!-- src/views/bulletin/components/category/CategorySelector.vue -->
<template>
  <div class="category-selector" :class="{ 'has-error': error }">
    <label>Категория *</label>
    
    <div class="category-info" v-if="selectedCategoryName">
      <div class="selected-category">
        Выбрана категория: <strong>{{ selectedCategoryName }}</strong>
      </div>
    </div>
    
    <div class="category-tree">
      <div class="tree-header">
        <span class="hint-text">Выберите листовую категорию (обозначены зеленым)</span>
      </div>
      <CategoryNode
        v-for="root in rootCategories"
        :key="root.id"
        :category="root"
        :selected-id="modelValue"
        @select="selectCategory"
      />
    </div>
    
    <div v-if="error" class="error">{{ error }}</div>
    <div class="hint">✅ Выбирать можно только листовые категории (помечены как "Листовая")</div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useCategory } from '@/composables/useCategory'
import CategoryNode from './CategoryNode.vue'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  error: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue'])

const { rootCategories, fetchRootCategories, fetchCategory } = useCategory()
const loading = ref(false)
const selectedCategory = ref(null)

const selectedCategoryName = computed(() => {
  return selectedCategory.value?.name || ''
})

const loadRoots = async () => {
  loading.value = true
  try {
    await fetchRootCategories()
  } catch (err) {
    console.error('Ошибка загрузки категорий:', err)
  } finally {
    loading.value = false
  }
}

const loadSelectedCategory = async () => {
  if (props.modelValue) {
    try {
      const response = await fetchCategory(props.modelValue)
      selectedCategory.value = response.data?.categoryResponse
    } catch (err) {
      console.error('Ошибка загрузки выбранной категории:', err)
    }
  } else {
    selectedCategory.value = null
  }
}

const selectCategory = (categoryId) => {
  console.log('CategorySelector: выбрана категория', categoryId)
  // Отправляем событие обновления
  emit('update:modelValue', categoryId)
  loadSelectedCategory()
}

// Следим за изменением modelValue
watch(() => props.modelValue, (newValue, oldValue) => {
  console.log('CategorySelector: modelValue изменился', { oldValue, newValue })
  loadSelectedCategory()
})

onMounted(() => {
  loadRoots()
  loadSelectedCategory()
})
</script>

<style scoped>
.category-selector {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.category-selector label {
  font-weight: 500;
  color: #4a5568;
}

.category-info {
  padding: 0.5rem;
  background: #f0f9ff;
  border-radius: 4px;
  border-left: 3px solid #4299e1;
}

.selected-category {
  font-size: 0.875rem;
  color: #2c5282;
}

.selected-category strong {
  color: #2b6cb0;
}

.category-tree {
  border: 1px solid #e2e8f0;
  border-radius: 4px;
  overflow: hidden;
}

.tree-header {
  padding: 0.5rem;
  background: #f7fafc;
  border-bottom: 1px solid #e2e8f0;
}

.hint-text {
  font-size: 0.75rem;
  color: #718096;
}

.category-selector.has-error label {
  color: #e53e3e;
}

.category-selector.has-error .category-tree {
  border-color: #e53e3e;
}

.error {
  color: #e53e3e;
  font-size: 0.75rem;
}

.hint {
  color: #a0aec0;
  font-size: 0.7rem;
}
</style>