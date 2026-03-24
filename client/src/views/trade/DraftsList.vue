<!-- src/views/trade/DraftsList.vue -->
<template>
  <div class="drafts-list">
    <div class="list-header">
      <h2>Мои объявления</h2>
      <div class="header-actions">
        <div class="tabs">
          <button 
            class="tab" 
            :class="{ active: activeTab === 'drafts' }"
            @click="switchTab('drafts')"
          >
            Черновики
          </button>
          <button 
            class="tab" 
            :class="{ active: activeTab === 'approved' }"
            @click="switchTab('approved')"
          >
            Готовы к публикации
          </button>
        </div>
        <SearchBar @search="handleSearch" />
        <button class="btn-create" @click="createNew">
          + Создать объявление
        </button>
      </div>
    </div>
    
    <div v-if="loading" class="loading">Загрузка...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else-if="bulletins.length === 0" class="empty">
      {{ activeTab === 'drafts' ? 'Нет черновиков' : 'Нет объявлений, готовых к публикации' }}
    </div>
    <div v-else class="list-content">
      <BulletinCard 
        v-for="item in bulletins" 
        :key="item.id"
        :bulletin="item"
        :highlight="item.state === 'APPROVED'"
        @click="goToEdit(item.id)"
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
import { useRouter } from 'vue-router'
import { useBulletin } from '@/composables/useBulletin'
import SearchBar from './components/SearchBar.vue'
import BulletinCard from './components/BulletinCard.vue'

const router = useRouter()
const { bulletins, loading, error, pagination, fetchMyDrafts, fetchMyApproved } = useBulletin()
const searchQuery = ref('')
const activeTab = ref('drafts')

const loadData = async (page = 0, title = null) => {
  if (activeTab.value === 'drafts') {
    await fetchMyDrafts(page, 20, title)
  } else {
    await fetchMyApproved(page, 20)
  }
}

const loadPage = (page) => {
  loadData(page, searchQuery.value)
}

const handleSearch = (query) => {
  searchQuery.value = query
  loadData(0, query)
}

const goToEdit = (id) => {
  router.push(`/trade/bulletin/edit/${id}`)
}

const createNew = () => {
  router.push('/trade/bulletin/edit/new')
}

const switchTab = (tab) => {
  activeTab.value = tab
  searchQuery.value = ''
  loadData(0, null)
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.drafts-list {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.list-header h2 {
  margin: 0;
  color: #333;
}

.header-actions {
  display: flex;
  gap: 1rem;
  align-items: center;
  flex-wrap: wrap;
}

.tabs {
  display: flex;
  gap: 0.5rem;
  background: #f7fafc;
  padding: 0.25rem;
  border-radius: 8px;
}

.tab {
  padding: 0.5rem 1rem;
  border: none;
  background: transparent;
  cursor: pointer;
  border-radius: 6px;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.tab.active {
  background: white;
  color: #667eea;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.tab:hover:not(.active) {
  background: #edf2f7;
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

.btn-create {
  padding: 0.5rem 1rem;
  background: #48bb78;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.875rem;
}

.btn-create:hover {
  background: #38a169;
}
</style>