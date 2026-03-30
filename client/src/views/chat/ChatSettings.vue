<!-- src/views/chat/ChatSettings.vue -->
<template>
  <div class="chat-settings">
    <div class="settings-card">
      <h2>{{ profileExists ? 'Настройки профиля чата' : 'Создание профиля чата' }}</h2>
      
      <!-- Состояние загрузки -->
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Загрузка...</p>
      </div>
      
      <!-- Состояние ошибки -->
      <div v-else-if="error" class="error-state">
        <p>{{ error }}</p>
        <button class="retry-btn" @click="loadProfile">Повторить</button>
      </div>
      
      <!-- Форма профиля -->
      <div v-else class="profile-content">
        <ProfileAvatar
          :image-id="profileData.imageId"
          @upload="handleImageUpload"
          @delete="handleImageDelete"
        />
        
        <ProfileForm
          :initial-data="profileData"
          :loading="saving"
          :is-new="!profileExists"
          @submit="handleSubmit"
        />
      </div>
    </div>
    
    <ProfileSuccessModal
      :show="showSuccessModal"
      :title="successTitle"
      :message="successMessage"
      @close="showSuccessModal = false"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useProfile } from '@/composables/useProfile'
import ProfileAvatar from './components/wiget/ProfileAvatar.vue'
import ProfileForm from './components/wiget/ProfileForm.vue'
import ProfileSuccessModal from './components/modals/ProfileSuccessModal.vue'

const { 
  checkMyProfileExists, 
  createMyProfile, 
  updatePublicName, 
  updateDescription, 
  updateImage,
  fetchMyProfile
} = useProfile()

const profileExists = ref(false)
const profileData = ref({
  publicName: '',
  description: '',
  imageId: null
})
const loading = ref(true)
const saving = ref(false)
const error = ref(null)
const showSuccessModal = ref(false)
const successTitle = ref('')
const successMessage = ref('')

const loadProfile = async () => {
  loading.value = true
  error.value = null
  
  try {
    const { exists, profile } = await checkMyProfileExists()
    profileExists.value = exists
    
    if (exists && profile) {
      profileData.value = {
        publicName: profile.publicName || '',
        description: profile.description || '',
        imageId: profile.imageId || null
      }
    } else {
      profileData.value = {
        publicName: '',
        description: '',
        imageId: null
      }
    }
  } catch (err) {
    console.error('Ошибка загрузки профиля:', err)
    error.value = err.message || 'Не удалось загрузить профиль'
  } finally {
    loading.value = false
  }
}

const handleSubmit = async (formData) => {
  saving.value = true
  error.value = null
  
  try {
    if (!profileExists.value) {
      // Создание нового профиля
      await createMyProfile(formData.publicName)
      if (formData.description) {
        await updateDescription(formData.description)
      }
      successTitle.value = 'Профиль создан!'
      successMessage.value = 'Ваш профиль чата успешно создан'
    } else {
      // Обновление существующего
      if (formData.publicName !== profileData.value.publicName) {
        await updatePublicName(formData.publicName)
      }
      if (formData.description !== profileData.value.description) {
        await updateDescription(formData.description)
      }
      successTitle.value = 'Профиль обновлён!'
      successMessage.value = 'Изменения сохранены'
    }
    
    // Обновляем локальные данные
    await loadProfile()
    showSuccessModal.value = true
    
  } catch (err) {
    console.error('Ошибка сохранения профиля:', err)
    error.value = err.response?.data?.message || err.message || 'Не удалось сохранить изменения'
  } finally {
    saving.value = false
  }
}

const handleImageUpload = async (imageId) => {
  try {
    if (!profileExists.value) {
      // Если профиля нет, сохраняем ID для последующего создания
      profileData.value.imageId = imageId
    } else {
      await updateImage(imageId)
      await loadProfile()
    }
  } catch (err) {
    console.error('Ошибка загрузки изображения:', err)
    error.value = 'Не удалось загрузить изображение'
  }
}

const handleImageDelete = async () => {
  try {
    if (profileExists.value) {
      await updateImage(null)
      await loadProfile()
    } else {
      profileData.value.imageId = null
    }
  } catch (err) {
    console.error('Ошибка удаления изображения:', err)
    error.value = 'Не удалось удалить изображение'
  }
}

onMounted(() => {
  loadProfile()
})
</script>

<style scoped>
.chat-settings {
  max-width: 800px;
  margin: 0 auto;
}

.settings-card {
  background: white;
  padding: 2rem;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.settings-card h2 {
  margin-top: 0;
  margin-bottom: 1.5rem;
  color: #333;
  font-size: 1.25rem;
}

.loading-state,
.error-state {
  text-align: center;
  padding: 2rem;
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

.profile-content {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}
</style>