<!-- src/views/admin/components/CategoryTree.vue -->
<template>
  <div class="category-tree">
    <div class="tree-header">
      <h3>Категории</h3>
      <button @click="openCreateRootModal" class="btn-add-root">+ Корневая категория</button>
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
        :category="category"
        :is-leaf="category.leaf"
        :selected-category-id="selectedCategoryId"
        @select="selectCategory"
        @refresh-parent="refreshTree"
      />
    </div>
    
    <CategoryFormModal
      :visible="modalVisible"
      mode="create"
      title="Создание корневой категории"
      label="Название категории"
      placeholder="Введите название корневой категории"
      submit-text="Создать"
      :on-submit="handleCreateRoot"
      @close="modalVisible = false"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import CategoryNode from './CategoryNode.vue'
import CategoryFormModal from './CategoryFormModal.vue'
import { useCategory } from '@/composables/useCategory'

const { fetchRootCategories, createRootCategory } = useCategory()

const rootCategories = ref([])
const loading = ref(false)
const error = ref(null)
const selectedCategoryId = ref(null)
const modalVisible = ref(false)

const emit = defineEmits(['category-selected'])

const handleApiError = (err) => {
  const response = err.response?.data
  if (response?.message) {
    return response.message
  }
  return err.message || 'Произошла ошибка'
}

const loadRoots = async () => {
  loading.value = true
  error.value = null
  try {
    const response = await fetchRootCategories()
    rootCategories.value = response.data?.categoryResponse || []
  } catch (err) {
    error.value = handleApiError(err)
    console.error('Ошибка загрузки корневых категорий:', err)
  } finally {
    loading.value = false
  }
}

const refreshTree = () => {
  loadRoots()
}

onMounted(() => {
  loadRoots()
})

const selectCategory = (category) => {
  selectedCategoryId.value = category.id
  emit('category-selected', category)
}

const openCreateRootModal = () => {
  modalVisible.value = true
}

const handleCreateRoot = async (name) => {
  await createRootCategory(name)
  await refreshTree()
}

defineExpose({
  refreshTree,
  createRoot: openCreateRootModal
})
</script>

<style scoped>
.category-tree {
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
}

.loading, .empty {
  color: #a0aec0;
}

.error {
  color: #e53e3e;
}
</style>