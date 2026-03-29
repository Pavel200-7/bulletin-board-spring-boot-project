<!-- src/views/bulletin/SearchResultsPage.vue -->
<template>
  <div class="search-results">

    
    <Breadcrumbs :title="`Результаты поиска${searchQuery ? `: ${searchQuery}` : ''}`" />

    <div class="search-layout">
      <aside class="filters-sidebar">
        <FilterSidebar
          v-model="filters"
          :category-id="selectedCategoryId"
          @apply="handleFiltersApply"
        />
      </aside>

      <main class="results-main">
        <div class="search-form">
          <SearchBar v-model="searchQuery" @search="handleSearch" />
          <CategorySelector v-model="selectedCategoryId" @update:model-value="handleCategoryChange" />
          <div class="sort-section">
            <select v-model="sortBy" class="sort-select" @change="handleSearch">
              <option :value="ORDER_BY.TITLE">По названию</option>
              <option :value="ORDER_BY.PRICE">По цене</option>
            </select>
            <select v-model="sortDirection" class="sort-direction" @change="handleSearch">
              <option :value="DIRECTION.ASC">По возрастанию</option>
              <option :value="DIRECTION.DESC">По убыванию</option>
            </select>
          </div>
        </div>

        <LoadingState v-if="loading" text="Идёт поиск объявлений..." />
        <ErrorState v-else-if="error" :error="error" @retry="loadResults" />

        <div v-else-if="bulletins.length === 0" class="empty-state">
          <div class="icon">🔍</div>
          <h3>Ничего не найдено</h3>
          <p>Попробуйте изменить параметры поиска</p>
        </div>

        <div v-else class="results-grid">
          <BulletinCard
            v-for="bulletin in bulletins"
            :key="bulletin.id"
            :bulletin="bulletin"
          />
        </div>

        <Pagination
          v-if="pagination.totalPages > 1"
          :current-page="pagination.page"
          :total-pages="pagination.totalPages"
          @page-change="handlePageChange"
        />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useBulletin } from '@/composables/useBulletin'
import { BULLETIN_ORDER_BY, SORT_DIRECTION } from '@/services/bulletin/types'
import BulletinCard from './components/card/BulletinCard.vue'
import Breadcrumbs from './components/view/Breadcrumbs.vue'
import LoadingState from './components/wigets/LoadingState.vue'
import ErrorState from './components/wigets/ErrorState.vue'
import Pagination from './components/wigets/Pagination.vue'
import SearchBar from '@/views/trade/components/SearchBar.vue'
import CategorySelector from './components/category/CategorySelector.vue'
import FilterSidebar from './components/view/FilterSidebar.vue'

const route = useRoute()
const router = useRouter()
const { fetchPublishedBulletins, bulletins, loading, error, pagination } = useBulletin()

const ORDER_BY = BULLETIN_ORDER_BY
const DIRECTION = SORT_DIRECTION

const searchQuery = ref(route.query.title || '')
const selectedCategoryId = ref(route.query.categoryId || '')
const sortBy = ref(route.query.orderBy || ORDER_BY.TITLE)
const sortDirection = ref(route.query.direction || DIRECTION.ASC)

const filters = ref({
  minPrice: route.query.minPrice ? Number(route.query.minPrice) : null,
  maxPrice: route.query.maxPrice ? Number(route.query.maxPrice) : null,
  characteristicValueIds: route.query.characteristicValueIds 
    ? (typeof route.query.characteristicValueIds === 'string' 
        ? route.query.characteristicValueIds.split(',').filter(Boolean)
        : route.query.characteristicValueIds)
    : null
})

// Функция загрузки результатов
const loadResults = async () => {
  let characteristicValueIds = route.query.characteristicValueIds || null
  if (characteristicValueIds && typeof characteristicValueIds === 'string') {
    characteristicValueIds = characteristicValueIds.split(',').filter(Boolean)
  }
  
  const page = route.query.page ? Number(route.query.page) : 0
  
  const criteria = {
    title: route.query.title || null,
    categoryId: route.query.categoryId || null,
    minPrice: route.query.minPrice ? Number(route.query.minPrice) : null,
    maxPrice: route.query.maxPrice ? Number(route.query.maxPrice) : null,
    characteristicValueIds: characteristicValueIds,
    orderBy: route.query.orderBy || ORDER_BY.TITLE,
    direction: route.query.direction || DIRECTION.ASC
  }
  
  await fetchPublishedBulletins({ page, size: 15, criteria })
}

// Обновление URL
const updateUrl = (page = 0) => {
  const query = {}
  
  if (searchQuery.value) query.title = searchQuery.value
  if (selectedCategoryId.value) query.categoryId = selectedCategoryId.value
  if (sortBy.value) query.orderBy = sortBy.value
  if (sortDirection.value) query.direction = sortDirection.value
  if (page > 0) query.page = page
  if (filters.value.minPrice) query.minPrice = filters.value.minPrice
  if (filters.value.maxPrice) query.maxPrice = filters.value.maxPrice
  if (filters.value.characteristicValueIds?.length) {
    query.characteristicValueIds = filters.value.characteristicValueIds.join(',')
  }
  
  router.push({
    name: 'bulletin-search',
    query
  })
}

const handleSearch = () => {
  updateUrl(0)
}

const handleCategoryChange = () => {
  filters.value = {
    minPrice: null,
    maxPrice: null,
    characteristicValueIds: null
  }
  updateUrl(0)
}

const handleFiltersApply = (newFilters) => {
  filters.value = newFilters
  updateUrl(0) // ← здесь передаем 0 явно
}

const handlePageChange = (page) => {
  updateUrl(page)
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// Синхронизация с URL
watch(
  () => route.query,
  () => {
    searchQuery.value = route.query.title || ''
    selectedCategoryId.value = route.query.categoryId || ''
    sortBy.value = route.query.orderBy || ORDER_BY.TITLE
    sortDirection.value = route.query.direction || DIRECTION.ASC
    
    let characteristicValueIds = route.query.characteristicValueIds || null
    if (characteristicValueIds && typeof characteristicValueIds === 'string') {
      characteristicValueIds = characteristicValueIds.split(',').filter(Boolean)
    }
    
    filters.value = {
      minPrice: route.query.minPrice ? Number(route.query.minPrice) : null,
      maxPrice: route.query.maxPrice ? Number(route.query.maxPrice) : null,
      characteristicValueIds: characteristicValueIds
    }
    loadResults()
  },
  { deep: true }
)

onMounted(() => {
  loadResults()
})
</script>

<style scoped>
.search-results {
  max-width: 1440px;
  margin: 0 auto;
  padding: 2rem;
}

.search-layout {
  display: flex;
  gap: 2rem;
}

.filters-sidebar {
  flex: 0 0 280px;
}

.results-main {
  flex: 1;
  min-width: 0;
}

.search-form {
  background: white;
  padding: 1.5rem;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  margin-bottom: 2rem;
  align-items: flex-end;
}

.search-form .search-bar {
  flex: 2;
  min-width: 200px;
}

.search-form .category-selector {
  flex: 1;
  min-width: 180px;
}

.sort-section {
  display: flex;
  gap: 0.5rem;
  min-width: 200px;
}

.sort-select,
.sort-direction {
  padding: 0.5rem 0.75rem;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.875rem;
  background: white;
  cursor: pointer;
}

.sort-select:focus,
.sort-direction:focus {
  outline: none;
  border-color: #667eea;
}

.results-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
  margin-top: 1rem;
}

.empty-state {
  text-align: center;
  padding: 4rem;
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  color: #718096;
}

.icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

@media (max-width: 768px) {
  .search-layout {
    flex-direction: column;
  }
  
  .filters-sidebar {
    flex: auto;
  }
}
</style>