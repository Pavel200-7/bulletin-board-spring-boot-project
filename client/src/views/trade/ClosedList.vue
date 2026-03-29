<!-- src/views/trade/ClosedList.vue -->
<template>
  <div class="closed-list">
    <div class="list-header">
      <h2>Закрытые объявления</h2>
      <SearchBar @search="handleSearch" />
    </div>
    
    <div v-if="loading" class="loading">Загрузка...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else-if="bulletins.length === 0" class="empty">
      Нет закрытых объявлений
    </div>
    <div v-else class="list-content">
      <ClosedBulletinCard 
        v-for="item in bulletins" 
        :key="item.id"
        :bulletin="item"
      />
    </div>
    
    <div v-if="pagination.totalPages > 1" class="pagination">
      <button 
        :disabled="pagination.page === 0" 
        @click="loadPage(pagination.page - 1)"
        class="page-btn"
      >
        ←
      </button>
      <span>Страница {{ pagination.page + 1 }} из {{ pagination.totalPages }}</span>
      <button 
        :disabled="pagination.page >= pagination.totalPages - 1" 
        @click="loadPage(pagination.page + 1)"
        class="page-btn"
      >
        →
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useBulletin } from '@/composables/useBulletin'
import SearchBar from './components/SearchBar.vue'
import ClosedBulletinCard from './components/ClosedBulletinCard.vue'

const { bulletins, loading, error, pagination, fetchMyCompleted } = useBulletin()
const searchQuery = ref('')

const loadData = async (page = 0, title = null) => {
  await fetchMyCompleted(page, 20, title)
}

const loadPage = (page) => {
  loadData(page, searchQuery.value)
}

const handleSearch = (query) => {
  searchQuery.value = query
  loadData(0, query)
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.closed-list {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.list-header h2 {
  margin: 0;
  color: #333;
}

.loading, .error, .empty {
  text-align: center;
  padding: 2rem;
  color: #a0aec0;
}

.error {
  color: #e53e3e;
}

.list-content {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 1.5rem;
  padding-top: 1rem;
  border-top: 1px solid #e2e8f0;
}

.page-btn {
  padding: 0.25rem 0.5rem;
  background: #667eea;
  color: white;
  border: none;
  cursor: pointer;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>