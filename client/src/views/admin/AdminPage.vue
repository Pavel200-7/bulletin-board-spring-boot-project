<!-- src/views/AdminPage.vue -->
<template>
  <div class="admin-container">
    <header class="admin-header">
      <h1>Админ панель</h1>
      <button @click="goHome" class="home-btn">На главную</button>
    </header>
    
    <div class="admin-content">
      <div class="categories-section">
        <CategoryTree @category-selected="handleCategorySelected" />
      </div>
      <div class="characteristics-section">
        <CharacteristicsPanel :selected-category="selectedCategory" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import CategoryTree from './components/CategoryTree.vue'
import CharacteristicsPanel from './components/CharacteristicsPanel.vue'

const router = useRouter()
const selectedCategory = ref(null)

const goHome = () => {
  router.push('/home')
}

const handleCategorySelected = (category) => {
  selectedCategory.value = category
}
</script>

<style scoped>
.admin-container {
  min-height: 100vh;
  background: #f5f5f5;
}

.admin-header {
  background: white;
  padding: 1rem 2rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.admin-header h1 {
  margin: 0;
  color: #333;
}

.home-btn {
  padding: 0.5rem 1rem;
  background: #667eea;
  color: white;
  border: none;
  cursor: pointer;
}

.home-btn:hover {
  background: #5a67d8;
}

.admin-content {
  display: grid;
  grid-template-columns: 1fr 1.5fr;
  gap: 1.5rem;
  padding: 1.5rem;
  height: calc(100vh - 80px);
}

.categories-section,
.characteristics-section {
  overflow-y: auto;
}
</style>