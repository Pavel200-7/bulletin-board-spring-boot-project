<!-- src/views/trade/TradeSettings.vue -->
<template>
  <div class="trade-settings">
    <div class="settings-card">
      <h2>Данные торгового аккаунта</h2>
      
      <LoadingState v-if="loading" />
      
      <ErrorState 
        v-else-if="error" 
        :error="error" 
        @retry="loadData" 
      />
      
      <EmptyState v-else-if="!account" />
      
      <SettingsForm 
        v-else
        :account="account"
        :saving="saving"
        :approving="approving"
        @submit="handleSubmit"
        @approve="handleApprove"
        @image-upload="handleImageUpload"
        @image-delete="handleImageDelete"
      />
    </div>
    
    <ValidationErrorModal
      :show="showErrorModal"
      :message="errorModalMessage"
      :details="errorModalDetails"
      @close="showErrorModal = false"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useTradeAccount } from '@/composables/useTradeAccount'
import LoadingState from './components/wiget/LoadingState.vue'
import ErrorState from './components/wiget/ErrorState.vue'
import EmptyState from './components/wiget/EmptyState.vue'
import SettingsForm from './components/SettingsForm.vue'
import ValidationErrorModal from './components/ValidationErrorModal.vue'

const { 
  account, 
  loading, 
  error, 
  fetchMyTradeAccount, 
  updateName, 
  updatePhone, 
  updateContacts, 
  updateDescription,
  updateImage,
  setApproximateLocation,
  setExactLocation,
  approveAccount 
} = useTradeAccount()

const saving = ref(false)
const approving = ref(false)
const showErrorModal = ref(false)
const errorModalMessage = ref('')
const errorModalDetails = ref([])

onMounted(() => {
  loadData()
})

const loadData = async () => {
  try {
    await fetchMyTradeAccount()
  } catch (err) {
    console.error('Ошибка загрузки:', err)
  }
}

const handleImageUpload = async ({ imageId }) => {
  try {
    await updateImage(imageId)
    await loadData()
  } catch (err) {
    console.error('Ошибка загрузки изображения:', err)
    alert('Не удалось сохранить изображение')
  }
}

const handleImageDelete = async ({ imageId }) => {
  try {
    await updateImage(null)
    await loadData()
  } catch (err) {
    console.error('Ошибка удаления изображения:', err)
    alert('Не удалось удалить изображение')
  }
}

const handleSubmit = async (formData) => {
  saving.value = true
  
  try {
    if (formData.name !== account.value.name) {
      await updateName(formData.name)
    }
    
    if (formData.phone !== account.value.phone) {
      await updatePhone(formData.phone)
    }
    
    if (formData.contacts !== account.value.contacts) {
      await updateContacts(formData.contacts)
    }
    
    if (formData.description !== account.value.description) {
      await updateDescription(formData.description)
    }
    
    // Обработка изображения уже через handleImageUpload
    
    const hasLocationData = formData.townName || 
                           formData.latitude || 
                           formData.longitude
                           
    if (hasLocationData && formData.locationName) {
      await setExactLocation({
        latitude: parseFloat(formData.latitude),
        longitude: parseFloat(formData.longitude),
        townName: formData.townName,
        locationName: formData.locationName
      })
    } else if (hasLocationData) {
      await setApproximateLocation({
        latitude: formData.latitude ? parseFloat(formData.latitude) : null,
        longitude: formData.longitude ? parseFloat(formData.longitude) : null,
        townName: formData.townName
      })
    }
    
    await loadData()
    alert('Данные успешно сохранены!')
  } catch (err) {
    console.error('Ошибка сохранения:', err)
    
    const response = err.response?.data
    if (response?.validationErrors) {
      errorModalMessage.value = 'Ошибка валидации данных'
      errorModalDetails.value = Object.entries(response.validationErrors).map(
        ([field, message]) => `${field}: ${message}`
      )
      showErrorModal.value = true
    } else if (response?.message) {
      errorModalMessage.value = 'Ошибка'
      errorModalDetails.value = [response.message]
      showErrorModal.value = true
    } else {
      alert('Произошла ошибка при сохранении данных')
    }
  } finally {
    saving.value = false
  }
}

const handleApprove = async () => {
  if (confirm('Подтвердить аккаунт? После подтверждения вы сможете публиковать объявления.')) {
    approving.value = true
    try {
      await approveAccount()
      await loadData()
      alert('Аккаунт успешно подтвержден!')
    } catch (err) {
      console.error('Ошибка подтверждения:', err)
      const response = err.response?.data
      alert(response?.message || 'Произошла ошибка при подтверждении аккаунта')
    } finally {
      approving.value = false
    }
  }
}
</script>

<style scoped>
.trade-settings {
  max-width: 800px;
  margin: 0 auto;
}

.settings-card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.settings-card h2 {
  margin-top: 0;
  margin-bottom: 1.5rem;
  color: #333;
}
</style>