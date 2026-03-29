<!-- src/views/home/components/SearchForm.vue -->
<template>
  <div class="search-form">
    <div class="search-input-wrapper">
      <input
        v-model="localTitle"
        type="text"
        placeholder="Поиск объявлений..."
        class="search-input"
        @keyup.enter="handleSearch"
      />
      <button v-if="localTitle" class="clear-btn" @click="clearTitle">✕</button>
    </div>
    
    <CategorySelector
      v-model="localCategoryId"
    />
    
    <button class="search-btn" @click="handleSearch">
      🔍 Найти
    </button>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import CategorySelector from '@/views/bulletin/components/category/CategorySelector.vue'

const router = useRouter()

const localTitle = ref('')
const localCategoryId = ref('')

const handleSearch = () => {
  const query = {}
  
  if (localTitle.value) query.title = localTitle.value
  if (localCategoryId.value) query.categoryId = localCategoryId.value
  
  router.push({
    name: 'bulletin-search',
    query
  })
}

const clearTitle = () => {
  localTitle.value = ''
}
</script>

<style scoped>
.search-form {
  background: white;
  border-radius: 48px;
  padding: 0.5rem;
  display: flex;
  gap: 0.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  max-width: 800px;
  width: 100%;
  margin: 0 auto;
}

.search-input-wrapper {
  flex: 2;
  position: relative;
}

.search-input {
  width: 100%;
  padding: 0.75rem 2rem 0.75rem 1rem;
  border: none;
  background: #f8f9fa;
  border-radius: 40px;
  font-size: 1rem;
  transition: all 0.2s;
}

.search-input:focus {
  outline: none;
  background: white;
  box-shadow: 0 0 0 2px #667eea;
}

.clear-btn {
  position: absolute;
  right: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  color: #a0aec0;
  font-size: 1rem;
  padding: 0.25rem;
  border-radius: 50%;
  transition: all 0.2s;
}

.clear-btn:hover {
  color: #4a5568;
  background: #edf2f7;
}

.category-selector {
  flex: 1;
  min-width: 180px;
}

.search-btn {
  padding: 0 1.5rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 40px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.search-btn:hover {
  background: #5a67d8;
  transform: translateY(-1px);
}

@media (max-width: 768px) {
  .search-form {
    flex-direction: column;
    border-radius: 16px;
    background: transparent;
    gap: 0.75rem;
    padding: 0;
  }
  
  .search-input-wrapper {
    width: 100%;
  }
  
  .search-input {
    background: white;
    border: 1px solid #e2e8f0;
  }
  
  .search-input:focus {
    border-color: #667eea;
  }
  
  .category-selector {
    width: 100%;
  }
  
  .search-btn {
    width: 100%;
    padding: 0.75rem;
  }
}
</style>