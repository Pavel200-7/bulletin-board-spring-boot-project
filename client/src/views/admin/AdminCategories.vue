<!-- src/views/admin/AdminCategories.vue -->
<template>
  <div class="admin-categories">
    <div class="page-header">
      <h1>Управление категориями</h1>
      <button @click="handleCreateRoot" class="btn-create">+ Корневая категория</button>
    </div>
    
    <div class="categories-content">
      <div class="tree-section">
        <h3>Дерево категорий</h3>
        <CategoryTree 
          ref="categoryTreeRef"
          @category-selected="handleCategorySelected" 
        />
      </div>
      
      <div class="characteristics-section">
        <h3>Характеристики категории</h3>
        <CharacteristicsPanel :selected-category="selectedCategory" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import CategoryTree from './components/CategoryTree.vue'
import CharacteristicsPanel from './components/CharacteristicsPanel.vue'

const categoryTreeRef = ref(null)
const selectedCategory = ref(null)

const handleCategorySelected = (category) => {
  selectedCategory.value = category
}

const handleCreateRoot = async () => {
  if (categoryTreeRef.value) {
    await categoryTreeRef.value.createRoot()
  }
}
</script>

<style scoped>
.admin-categories {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #e2e8f0;
}

.page-header h1 {
  margin: 0;
  color: #333;
  font-size: 1.5rem;
}

.btn-create {
  padding: 0.5rem 1rem;
  background: #48bb78;
  color: white;
  border: none;
  cursor: pointer;
}

.btn-create:hover {
  background: #38a169;
}

.categories-content {
  display: grid;
  grid-template-columns: 1fr 1.5fr;
  gap: 1.5rem;
  min-height: 500px;
}

.tree-section h3,
.characteristics-section h3 {
  margin: 0 0 1rem 0;
  color: #4a5568;
  font-size: 1rem;
}

.tree-section {
  border-right: 1px solid #e2e8f0;
  padding-right: 1rem;
}
</style>