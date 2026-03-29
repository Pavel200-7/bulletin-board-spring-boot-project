<!-- src/views/bulletin/components/category/CategorySelector.vue -->
<template>
  <div class="category-selector" :class="{ 'has-error': error }">
    <label>Категория *</label>

    <!-- Отображаем выбранную категорию или placeholder -->
    <div 
      class="selector-dropdown"
      @click="isDropdownOpen = !isDropdownOpen"
    >
      <span class="selected-text">
        {{ selectedCategoryName || 'Не выбрано' }}
      </span>
      <span class="dropdown-icon">{{ isDropdownOpen ? '▲' : '▼' }}</span>
    </div>

    <!-- Выпадающий список с деревом категорий (расширенный) -->
    <div v-if="isDropdownOpen" class="dropdown-menu">
      <div class="dropdown-content">
        <!-- Опция "Не выбрано" -->
        <div class="clear-option" @click="handleClearSelection">
          <span class="clear-icon">✕</span>
          <span>Не выбрано</span>
        </div>
        
        <div class="tree-header">
          <span class="hint-text">Выберите листовую категорию</span>
        </div>
        <div class="categories-scroll-container">
          <CategoryNode
            v-for="root in rootCategories"
            :key="root.id"
            :category="root"
            :selected-id="modelValue"
            @select="handleSelectCategory"
          />
        </div>
      </div>
    </div>

    <!-- Сообщения об ошибках и подсказки -->
    <div v-if="error" class="error">{{ error }}</div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
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

// Состояние компонента
const isDropdownOpen = ref(false)
const selectedCategory = ref(null)

// Вычисляемое свойство для имени выбранной категории
const selectedCategoryName = computed(() => {
  return selectedCategory.value?.name || ''
})

// Загружаем выбранную категорию из API
const loadSelectedCategory = async () => {
  if (props.modelValue) {
    try {
      const response = await fetchCategory(props.modelValue)
      selectedCategory.value = response.data?.categoryResponse
    } catch (err) {
      console.error('Ошибка загрузки выбранной категории:', err)
      selectedCategory.value = null
    }
  } else {
    selectedCategory.value = null
  }
}

// Обработчик выбора категории
const handleSelectCategory = (categoryId) => {
  isDropdownOpen.value = false
  emit('update:modelValue', categoryId)
  loadSelectedCategory()
}

// Обработчик очистки выбора
const handleClearSelection = () => {
  isDropdownOpen.value = false
  emit('update:modelValue', null)
  selectedCategory.value = null
}

// Закрываем выпадающий список при клике вне его
const closeDropdown = (event) => {
  const selector = document.querySelector('.category-selector')
  if (selector && !selector.contains(event.target)) {
    isDropdownOpen.value = false
  }
}

watch(() => props.modelValue, (newValue) => {
  if (newValue) {
    loadSelectedCategory()
  } else {
    selectedCategory.value = null
  }
})

onMounted(() => {
  fetchRootCategories()
  loadSelectedCategory()
  document.addEventListener('click', closeDropdown)
})

onUnmounted(() => {
  document.removeEventListener('click', closeDropdown)
})
</script>

<style scoped>
.category-selector {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  position: relative;
}

.category-selector label {
  font-weight: 500;
  color: #4a5568;
}

/* Стили для выпадающего списка */
.selector-dropdown {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 0.75rem;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  background: white;
  cursor: pointer;
  transition: all 0.2s;
}

.selector-dropdown:hover {
  border-color: #667eea;
}

.selector-dropdown:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

.selected-text {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dropdown-icon {
  margin-left: 0.5rem;
  font-size: 0.875rem;
  color: #718096;
}

/* Стили для выпадающего меню - расширенного */
.dropdown-menu {
  position: absolute;
  top: 100%;
  left: 0;
  min-width: 100%;
  width: max-content;
  min-width: 400px;

  max-width: 600px;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  z-index: 10;
  max-height: 320px;
  overflow: hidden;
  margin-top: 4px;
}

/* Если нужно расширение влево при недостатке места справа */
.dropdown-menu {
  left: auto;
  right: 0;
}

.dropdown-content {
  display: flex;
  flex-direction: column;
}

/* Опция "Не выбрано" */
.clear-option {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 0.75rem;
  cursor: pointer;
  border-bottom: 1px solid #e2e8f0;
  transition: background 0.2s;
  color: #e53e3e;
  font-size: 0.875rem;
}

.clear-option:hover {
  background: #fed7d7;
}

.clear-icon {
  font-size: 1rem;
  font-weight: bold;
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

/* Контейнер с прокруткой для дерева категорий */
.categories-scroll-container {
  flex: 1;
  overflow-y: auto;
  max-height: 250px;
  padding: 0.25rem;
}

/* Стили для сообщений */
.category-selector.has-error .selector-dropdown {
  border-color: #e53e3e;
}

.error {
  color: #e53e3e;
  font-size: 0.75rem;
}
</style>