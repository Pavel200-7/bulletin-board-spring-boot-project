<!-- src/views/chat/ChatSearch.vue -->
<template>
  <div class="chat-search">
    <div class="search-card">
      <h2>Поиск пользователей</h2>
      
      <!-- Поиск и сортировка -->
      <div class="search-controls">
        <SearchInput 
          v-model="searchQuery" 
          @search="handleSearch"
        />
        <SortSelector
          :order-by="orderBy"
          :direction="direction"
          @update:order-by="orderBy = $event"
          @update:direction="direction = $event"
          @change="handleSearch"
        />
      </div>

      <!-- Состояние загрузки -->
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Поиск пользователей...</p>
      </div>

      <!-- Состояние ошибки -->
      <div v-else-if="error" class="error-state">
        <p>{{ error }}</p>
        <button class="retry-btn" @click="loadProfiles">Повторить</button>
      </div>

      <!-- Результаты поиска -->
      <div v-else-if="profiles.length === 0" class="empty-state">
        <div class="empty-icon">🔍</div>
        <p>Пользователи не найдены</p>
        <p class="empty-hint">Попробуйте изменить параметры поиска</p>
      </div>

      <div v-else class="profiles-list">
        <ProfileCard
          v-for="profile in profiles"
          :key="profile.id"
          :profile-id="profile.id"
          :owner-id="profile.ownerId"
          :public-name="profile.publicName"
          :description="profile.description"
          :image-id="profile.imageId"
          :contact="profile.contact"
          :is-current-user="profile.ownerId === currentUserId"
          @click="goToChat(profile.id, profile.contact)"
          @add-contact="handleAddContact"
        />
      </div>

      <!-- Пагинация -->
      <Pagination
        v-if="totalPages > 1"
        :current-page="currentPage"
        :total-pages="totalPages"
        @page-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useProfile } from '@/composables/useProfile'
import { useAuth } from '@/composables/useAuth'
import { useContact } from '@/composables/useContact'
import { PROFILE_ORDER_BY, SORT_DIRECTION } from '@/services/profile/types'
import Pagination from '../../components/wiget/Pagination.vue'
import ProfileCard from './components/card/ProfileCard.vue'
import SearchInput from './components/wiget/SearchInput.vue'
import SortSelector from './components/wiget/SortSelector.vue'

const router = useRouter()
const route = useRoute()
const { searchProfiles, profiles, loading, error, pagination } = useProfile()
const { getUserId } = useAuth()
const { addContact: addContactToContacts } = useContact()

const currentUserId = ref(null)
const searchQuery = ref('')
const orderBy = ref(PROFILE_ORDER_BY.PUBLIC_NAME)
const direction = ref(SORT_DIRECTION.ASC)
const currentPage = ref(0)
const totalPages = ref(0)

const loadProfiles = async (page = 0) => {
  const criteria = {
    publicName: searchQuery.value || null,
    orderBy: orderBy.value,
    direction: direction.value
  }
  
  await searchProfiles({ page, size: 12, criteria })
  currentPage.value = page
  totalPages.value = pagination.value.totalPages || 0
}

const handleSearch = () => {
  // Обновляем query параметр в URL
  if (searchQuery.value) {
    router.replace({ query: { ...route.query, q: searchQuery.value } })
  } else {
    const { q, ...rest } = route.query
    router.replace({ query: rest })
  }
  loadProfiles(0)
}

const handlePageChange = (page) => {
  loadProfiles(page)
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const goToChat = (profileId, isContact) => {
  if (isContact) {
    router.push(`/chat/room/${profileId}`)
  } else {
    alert('Чтобы начать чат, сначала добавьте пользователя в контакты')
  }
}

const handleAddContact = async (profile) => {
  try {
    console.log('Добавляем пользователя в контакты:', profile)
    await addContactToContacts(profile.id)
    await loadProfiles(currentPage.value)
    alert(`Пользователь ${profile.publicName} добавлен в контакты`)
  } catch (err) {
    console.error('Ошибка добавления в контакты:', err)
    alert(err.response?.data?.message || 'Не удалось добавить пользователя в контакты')
  }
}

// Читаем параметр поиска из URL при монтировании
onMounted(async () => {
  currentUserId.value = getUserId()
  
  // Проверяем query параметр q
  const querySearch = route.query.q
  if (querySearch) {
    searchQuery.value = querySearch
  }
  
  await loadProfiles()
})

// Следим за изменением query параметров (если пользователь нажимает назад/вперед)
watch(() => route.query.q, (newQuery) => {
  if (newQuery !== undefined && newQuery !== searchQuery.value) {
    searchQuery.value = newQuery || ''
    loadProfiles(0)
  }
})
</script>

<style scoped>
.chat-search {
  max-width: 920px;
  margin: 0 auto;
}

.search-card {
  background: white;
  padding: 2rem;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.search-card h2 {
  margin-top: 0;
  margin-bottom: 1.5rem;
  color: #333;
  font-size: 1.25rem;
}

.search-controls {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
}

.search-controls .search-input {
  flex: 2;
  min-width: 200px;
}

.search-controls .sort-selector {
  flex: 1;
  min-width: 300px;
}

.loading-state,
.error-state,
.empty-state {
  text-align: center;
  padding: 3rem;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #e2e8f0;
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 1rem;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.error-state p {
  color: #e53e3e;
  margin-bottom: 1rem;
}

.retry-btn {
  padding: 0.5rem 1rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.retry-btn:hover {
  background: #5a67d8;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
  color: #a0aec0;
}

.empty-state p {
  margin: 0;
  color: #718096;
}

.empty-hint {
  font-size: 0.75rem;
  color: #a0aec0;
  margin-top: 0.5rem;
}

.profiles-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  margin-top: 1rem;
}

@media (max-width: 768px) {
  .chat-search {
    max-width: 100%;
    padding: 0 1rem;
  }
  
  .search-controls {
    flex-direction: column;
  }
  
  .search-controls .sort-selector {
    width: 100%;
    min-width: unset;
  }
}
</style>