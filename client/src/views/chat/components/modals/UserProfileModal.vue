<!-- src/views/chat/components/modals/UserProfileModal.vue -->
<template>
  <div v-if="show && profileId" class="modal-overlay" @click.self="close">
    <div class="user-modal">
      <div class="modal-header">
        <h3>Информация о пользователе</h3>
        <button class="modal-close" @click="close">✕</button>
      </div>
      
      <div class="modal-body" v-if="loading">
        <div class="loading-spinner"></div>
        <p>Загрузка данных...</p>
      </div>
      
      <div v-else-if="error" class="modal-error">
        <p>{{ error }}</p>
        <button class="btn-retry" @click="loadData">Повторить</button>
      </div>
      
      <div v-else-if="!profileData" class="modal-empty">
        <p>Данные пользователя не найдены</p>
      </div>
      
      <div v-else class="modal-content">
        <!-- Аватар -->
        <div class="user-avatar-large">
          <img 
            v-if="avatarUrl" 
            :src="avatarUrl" 
            alt="Аватар"
            class="avatar-image"
          />
          <div v-else class="avatar-placeholder">
            <span>👤</span>
          </div>
        </div>
        
        <!-- Основная информация с редактированием имени -->
        <div class="user-info-block">
          <div v-if="!isEditingName" class="name-display">
            <h2 class="user-name">{{ displayName }}</h2>
            <button 
              v-if="contactId" 
              class="edit-name-btn" 
              @click="startEditName"
              title="Редактировать имя контакта"
            >
              ✏️
            </button>
          </div>
          <div v-else class="name-edit">
            <input
              ref="nameInputRef"
              v-model="editNameValue"
              type="text"
              class="name-input"
              maxlength="100"
              @keyup.enter="saveName"
              @keyup.esc="cancelEditName"
            />
            <div class="name-edit-actions">
              <button class="save-name-btn" @click="saveName">✓</button>
              <button class="cancel-name-btn" @click="cancelEditName">✕</button>
            </div>
          </div>
          <p v-if="contactId" class="contact-hint">Имя отображается только у вас</p>
        </div>
        
        <!-- Описание -->
        <div class="info-section">
          <h4>Описание</h4>
          <p class="description-text">{{ profileData.description || 'Нет описания' }}</p>
        </div>
        
        <!-- ID пользователя -->
        <div class="info-section">
          <h4>ID пользователя</h4>
          <p class="user-id">{{ profileData.ownerId || 'Не указан' }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue'
import { useProfile } from '@/composables/useProfile'
import { useContact } from '@/composables/useContact'

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  profileId: {
    type: String,
    default: null
  },
  contactName: {
    type: String,
    default: ''
  },
  profileData: {
    type: Object,
    default: null
  },
  contactId: {
    type: String,
    default: null
  }
})

const emit = defineEmits(['close', 'name-updated'])

const { fetchProfile, profile: fetchedProfile } = useProfile()
const { renameContact } = useContact()
const loading = ref(false)
const error = ref(null)
const localProfileData = ref(null)
const localContactName = ref('')
const isEditingName = ref(false)
const editNameValue = ref('')
const nameInputRef = ref(null)

const getImageUrl = (imageId) => {
  if (!imageId) return null
  const MINIO_URL = import.meta.env.VITE_MINIO_URL || 'http://localhost:9001'
  const BUCKET = import.meta.env.VITE_MINIO_BUCKET || 'bulletins'
  return `${MINIO_URL}/${BUCKET}/${imageId}`
}

const avatarUrl = computed(() => {
  return getImageUrl(localProfileData.value?.imageId)
})

const displayName = computed(() => {
  // Сначала показываем кастомное имя контакта, если есть
  if (localContactName.value) return localContactName.value
  // Затем имя из пропса (из контакта)
  if (props.contactName) return props.contactName
  // Затем публичное имя из профиля
  return localProfileData.value?.publicName || 'Пользователь'
})

const loadData = async () => {
  if (!props.profileId) return
  
  loading.value = true
  error.value = null
  
  try {
    await fetchProfile(props.profileId)
    if (fetchedProfile.value) {
      localProfileData.value = fetchedProfile.value
    }
    
    // Загружаем текущее имя контакта
    localContactName.value = props.contactName || ''
  } catch (err) {
    console.error('Ошибка загрузки профиля:', err)
    error.value = err.message || 'Не удалось загрузить данные'
  } finally {
    loading.value = false
  }
}

const startEditName = () => {
  editNameValue.value = displayName.value
  isEditingName.value = true
  nextTick(() => {
    nameInputRef.value?.focus()
  })
}

const saveName = async () => {
  if (!editNameValue.value.trim()) {
    cancelEditName()
    return
  }
  
  if (!props.contactId) {
    console.error('No contactId provided')
    cancelEditName()
    return
  }
  
  try {
    await renameContact(props.contactId, editNameValue.value.trim())
    localContactName.value = editNameValue.value.trim()
    emit('name-updated', {
      contactId: props.contactId,
      newName: editNameValue.value.trim()
    })
    isEditingName.value = false
  } catch (err) {
    console.error('Failed to rename contact:', err)
    error.value = 'Не удалось изменить имя контакта'
  }
}

const cancelEditName = () => {
  isEditingName.value = false
  editNameValue.value = ''
}

// Используем данные из пропса, если они переданы
watch(() => props.profileData, (newData) => {
  if (newData) {
    localProfileData.value = newData
  }
}, { immediate: true })

// Обновляем локальное имя при изменении пропса
watch(() => props.contactName, (newName) => {
  localContactName.value = newName || ''
})

// Загружаем данные при открытии модалки
watch(() => props.show, (newVal) => {
  if (newVal && props.profileId && !localProfileData.value) {
    loadData()
  }
})

const close = () => {
  if (isEditingName.value) {
    cancelEditName()
  }
  emit('close')
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.user-modal {
  background: white;
  width: 400px;
  max-width: 90%;
  max-height: 80vh;
  border-radius: 16px;
  overflow: hidden;
  animation: modalFadeIn 0.2s ease;
}

@keyframes modalFadeIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  background: #f7fafc;
  border-bottom: 1px solid #e2e8f0;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.125rem;
  font-weight: 600;
  color: #2d3748;
}

.modal-close {
  background: none;
  border: none;
  font-size: 1.25rem;
  cursor: pointer;
  color: #a0aec0;
  padding: 0.25rem;
  border-radius: 4px;
  transition: all 0.2s;
}

.modal-close:hover {
  background: #edf2f7;
  color: #4a5568;
}

.modal-body {
  padding: 1.5rem;
  overflow-y: auto;
  max-height: calc(80vh - 60px);
}

.loading-spinner {
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

.modal-error,
.modal-empty {
  text-align: center;
  padding: 2rem;
  color: #718096;
}

.btn-retry {
  margin-top: 1rem;
  padding: 0.5rem 1rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.btn-retry:hover {
  background: #5a67d8;
}

.modal-content {
  padding: 1.5rem;
}

.user-avatar-large {
  width: 100px;
  height: 100px;
  margin: 0 auto 1rem;
  border-radius: 50%;
  overflow: hidden;
  background: #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 3rem;
  color: #a0aec0;
}

.user-info-block {
  text-align: center;
  margin-bottom: 1.5rem;
}

.name-display {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.user-name {
  font-size: 1.25rem;
  font-weight: 600;
  color: #2d3748;
  margin: 0;
}

.edit-name-btn {
  background: none;
  border: none;
  font-size: 1rem;
  cursor: pointer;
  padding: 0.25rem;
  border-radius: 4px;
  opacity: 0.6;
  transition: all 0.2s;
}

.edit-name-btn:hover {
  opacity: 1;
  background: #edf2f7;
}

.name-edit {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  align-items: center;
}

.name-input {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #667eea;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  text-align: center;
  outline: none;
}

.name-input:focus {
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.name-edit-actions {
  display: flex;
  gap: 0.5rem;
}

.save-name-btn, .cancel-name-btn {
  padding: 0.25rem 0.75rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.875rem;
}

.save-name-btn {
  background: #48bb78;
  color: white;
}

.save-name-btn:hover {
  background: #38a169;
}

.cancel-name-btn {
  background: #e2e8f0;
  color: #4a5568;
}

.cancel-name-btn:hover {
  background: #cbd5e0;
}

.contact-hint {
  font-size: 0.7rem;
  color: #a0aec0;
  margin-top: 0.5rem;
  margin-bottom: 0;
}

.info-section {
  margin-bottom: 1.5rem;
}

.info-section h4 {
  font-size: 0.875rem;
  font-weight: 600;
  color: #4a5568;
  margin: 0 0 0.75rem 0;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid #e2e8f0;
}

.description-text {
  font-size: 0.875rem;
  line-height: 1.5;
  color: #4a5568;
  margin: 0;
  white-space: pre-wrap;
}

.user-id {
  font-size: 0.75rem;
  color: #a0aec0;
  word-break: break-all;
  font-family: monospace;
}
</style>