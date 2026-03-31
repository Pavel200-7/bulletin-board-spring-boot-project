<!-- src/views/chat/ChatContacts.vue -->
<template>
  <div class="chat-contacts">
    <div class="contacts-card">
      <h2>Контакты</h2>
      
      <!-- Поиск -->
      <div class="search-wrapper">
        <SearchInput 
          v-model="searchQuery" 
          @search="handleSearch"
          placeholder="Поиск по имени контакта..."
        />
      </div>

      <!-- Состояние загрузки -->
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Загрузка контактов...</p>
      </div>

      <!-- Состояние ошибки -->
      <div v-else-if="error" class="error-state">
        <p>{{ error }}</p>
        <button class="retry-btn" @click="loadContacts">Повторить</button>
      </div>

      <!-- Результаты -->
      <div v-else-if="filteredContacts.length === 0" class="empty-state">
        <div class="empty-icon">👥</div>
        <p v-if="searchQuery">Ничего не найдено по запросу "{{ searchQuery }}"</p>
        <p v-else>У вас пока нет контактов</p>
        <p class="empty-hint">Найдите пользователей в разделе "Поиск" и добавьте их в контакты</p>
        <button class="search-btn" @click="goToSearch">🔍 Найти пользователей</button>
      </div>

      <div v-else class="contacts-list">
        <ContactCard
          v-for="contact in filteredContacts"
          :key="contact.id"
          :contact-id="contact.id"
          :profile-id="getContactProfileId(contact)"
          :contact-name="contact.contactName"
          :chat-id="contact.chatId"
          :is-owner="contact.ownerProfileId === myProfile?.id"
          @click="goToChat(contact.chatId)"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useContact } from '@/composables/useContact'
import { useProfile } from '@/composables/useProfile'
import SearchInput from './components/wiget/SearchInput.vue'
import ContactCard from './components/card/ContactCard.vue'

const router = useRouter()
const { contacts, loading, error, fetchContacts } = useContact()
const { fetchMyProfile, profile: myProfile } = useProfile()

const searchQuery = ref('')

const filteredContacts = computed(() => {
  if (!searchQuery.value) return contacts.value
  return contacts.value.filter(contact =>
    contact.contactName?.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

// Определяем ID собеседника
const getContactProfileId = (contact) => {
  if (!myProfile.value) return contact.contactProfileId
  
  // Если текущий профиль является владельцем контакта
  if (myProfile.value.id === contact.ownerProfileId) {
    return contact.contactProfileId // собеседник
  }
  // Если текущий профиль является контактом
  return contact.ownerProfileId // владелец контакта
}

const loadContacts = async () => {
  await Promise.all([
    fetchContacts(),
    fetchMyProfile()
  ])
}

const handleSearch = (query) => {
  searchQuery.value = query
}

const goToChat = (chatId) => {
  if (chatId) {
    router.push(`/chat/room/${chatId}`)
  }
}

const goToSearch = () => {
  router.push('/chat/search')
}

onMounted(() => {
  loadContacts()
})
</script>


<style scoped>
.chat-contacts {
  max-width: 920px;
  margin: 0 auto;
}

.contacts-card {
  background: white;
  padding: 2rem;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.contacts-card h2 {
  margin-top: 0;
  margin-bottom: 1.5rem;
  color: #333;
  font-size: 1.25rem;
}

.search-wrapper {
  margin-bottom: 1.5rem;
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

.retry-btn,
.search-btn {
  padding: 0.5rem 1rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.retry-btn:hover,
.search-btn:hover {
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
  margin-bottom: 1rem;
}

.contacts-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  margin-top: 1rem;
}
</style>