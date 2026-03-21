<!-- src/views/admin/components/CategoryTree.vue -->
<template>
  <div class="category-tree">
    <div class="tree-header">
      <h3>Категории</h3>
      <button @click="handleCreateRoot" class="btn-add-root">+ Корневая категория</button>
    </div>
    
    <div class="tree-content">
      <div v-if="loading" class="loading">Загрузка...</div>
      <div v-else-if="error" class="error">{{ error }}</div>
      <div v-else-if="rootCategories.length === 0" class="empty">
        Нет категорий. Создайте первую!
      </div>
      <CategoryNode 
        v-for="category in rootCategories" 
        :key="category.id"
        :ref="(el) => setCategoryRef(category.id, el)"
        :category="category"
        :selected-category-id="selectedCategoryId"
        @select="selectCategory"
        @create-child="handleCreateChild"
        @create-leaf="handleCreateLeaf"
        @rename="handleRename"
        @delete="handleDelete"
        @refresh-self="handleRefreshSelf"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import CategoryNode from './CategoryNode.vue'
import { useCategory } from '@/composables/useCategory'

const { 
  fetchRootCategories, 
  createRootCategory, 
  createChildCategory, 
  createLeafCategory, 
  renameCategory, 
  deleteChildCategory,
  deleteRootCategory
} = useCategory()

const rootCategories = ref([])
const loading = ref(false)
const error = ref(null)
const selectedCategoryId = ref(null)
const categoryRefs = ref({})

const emit = defineEmits(['category-selected'])

// Сохраняем ссылки на компоненты по ID категории
const setCategoryRef = (categoryId, el) => {
  if (el) {
    categoryRefs.value[categoryId] = el
  }
}

// Загружаем только корневые категории
const loadRoots = async () => {
  loading.value = true
  error.value = null
  try {
    const response = await fetchRootCategories()
    rootCategories.value = response.data?.categoryResponse || []
  } catch (err) {
    error.value = 'Ошибка загрузки категорий'
    console.error(err)
  } finally {
    loading.value = false
  }
}

const refreshRoots = () => {
  loadRoots()
}

// Обновление конкретной категории (если нужно перезагрузить её детей)
const handleRefreshSelf = () => {
  // Ничего не делаем, так как CategoryNode сам обновляет своих детей
}

onMounted(() => {
  loadRoots()
})

const selectCategory = (category) => {
  selectedCategoryId.value = category.id
  emit('category-selected', category)
}

const handleCreateRoot = async () => {
  const name = prompt('Введите название корневой категории:')
  if (name && name.trim()) {
    await createRootCategory(name.trim())
    await refreshRoots()
  }
}

const handleCreateChild = async ({ parentId, name }) => {
  await createChildCategory(parentId, name)
  // Находим родительскую категорию по ID и обновляем её детей
  const parentNode = categoryRefs.value[parentId]
  if (parentNode && parentNode.refreshChildren) {
    await parentNode.refreshChildren()
  }
}

const handleCreateLeaf = async ({ parentId, name }) => {
  await createLeafCategory(parentId, name)
  const parentNode = categoryRefs.value[parentId]
  if (parentNode && parentNode.refreshChildren) {
    await parentNode.refreshChildren()
  }
}

const handleRename = async ({ id, newName }) => {
  await renameCategory(id, newName)
  // Переименование не требует перезагрузки детей
}

const handleDelete = async (id, parentId) => {
  try {
    if (parentId === null) {
      await deleteRootCategory(id)
      await refreshRoots()
    } else {
      await deleteChildCategory(parentId, id)
      const parentNode = categoryRefs.value[parentId]
      if (parentNode && parentNode.refreshChildren) {
        await parentNode.refreshChildren()
      }
    }
  } catch (err) {
    console.error('Ошибка удаления категории:', err)
    alert('Ошибка при удалении категории')
  }
}
</script>

<style scoped>
.category-tree {
  background: white;
  border-radius: 8px;
  padding: 1rem;
  height: 100%;
  overflow-y: auto;
}

.tree-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid #e2e8f0;
}

.tree-header h3 {
  margin: 0;
  color: #333;
}

.btn-add-root {
  padding: 0.25rem 0.75rem;
  background: #48bb78;
  color: white;
  border: none;
  cursor: pointer;
  font-size: 0.875rem;
}

.btn-add-root:hover {
  background: #38a169;
}

.loading, .error, .empty {
  padding: 1rem;
  text-align: center;
  color: #666;
}

.error {
  color: #e53e3e;
}

.tree-content {
  min-height: 200px;
}
</style>