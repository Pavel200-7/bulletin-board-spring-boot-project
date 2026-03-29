<!-- src/views/trade/components/SettingsForm.vue -->
<template>
  <form @submit.prevent="handleSubmit" class="settings-form">
    <!-- Аватар -->
    <AvatarUploader
      :existing-image-id="formData.imageId"
      :account-id="account.id"
      @upload="handleImageUpload"
      @delete="handleImageDelete"
      @image-updated="handleImageUpdated"
    />
    
    <TextField
      v-model="formData.name"
      label="Название"
      placeholder="Введите название вашей торговой точки"
      required
    />
    
    <TextField
      v-model="formData.phone"
      label="Телефон"
      type="tel"
      placeholder="+7 (999) 123-45-67"
      required
      :error="validationErrors.phone"
      hint="Формат: 10-15 цифр, может начинаться с +"
    />
    
    <TextareaField
      v-model="formData.contacts"
      label="Контактные данные"
      placeholder="Email, Telegram, WhatsApp, Viber и т.д."
      :error="validationErrors.contacts"
      :max-length="300"
      hint="Максимум 300 символов"
    />
    
    <TextareaField
      v-model="formData.description"
      label="Описание"
      rows="5"
      placeholder="Расскажите о себе и своем бизнесе"
      :error="validationErrors.description"
      :max-length="1000"
      hint="Максимум 1000 символов"
    />
    
    <LocationSection
      v-model:town-name="formData.townName"
      v-model:latitude="formData.latitude"
      v-model:longitude="formData.longitude"
      v-model:location-name="formData.locationName"
      :location-error="validationErrors.location"
      :coordinates-error="validationErrors.coordinates"
    />
    
    <FormActions
      :saving="saving"
      :approving="approving"
      :is-approved="account.approved"
      @approve="$emit('approve')"
    />
  </form>
</template>

<script setup>
import { ref, watch } from 'vue'
import TextField from './wiget/TextField.vue'
import TextareaField from './wiget/TextareaField.vue'
import LocationSection from './wiget/LocationSection.vue'
import FormActions from './FormActions.vue'
import AvatarUploader from './image/AvatarUploader.vue'

const props = defineProps({
  account: {
    type: Object,
    required: true
  },
  saving: Boolean,
  approving: Boolean
})

const emit = defineEmits(['submit', 'approve', 'image-upload', 'image-delete'])

const formData = ref({
  name: '',
  phone: '',
  contacts: '',
  description: '',
  townName: '',
  latitude: null,
  longitude: null,
  locationName: '',
  imageId: null
})

const validationErrors = ref({
  phone: '',
  contacts: '',
  description: '',
  location: '',
  coordinates: ''
})

watch(() => props.account, (newAccount) => {
  if (newAccount) {
    formData.value = {
      name: newAccount.name || '',
      phone: newAccount.phone || '',
      contacts: newAccount.contacts || '',
      description: newAccount.description || '',
      townName: newAccount.townName || '',
      latitude: newAccount.latitude || null,
      longitude: newAccount.longitude || null,
      locationName: newAccount.locationName || '',
      imageId: newAccount.imageId || null
    }
  }
}, { immediate: true })

const handleImageUpload = ({ imageId, accountId }) => {
  emit('image-upload', { imageId, accountId })
  formData.value.imageId = imageId
}

const handleImageDelete = ({ imageId, accountId }) => {
  emit('image-delete', { imageId, accountId })
  formData.value.imageId = null
}

const handleImageUpdated = (imageId) => {
  formData.value.imageId = imageId
}

const validatePhone = () => {
  const phoneRegex = /^\+?[0-9]{10,15}$/
  if (formData.value.phone && !phoneRegex.test(formData.value.phone)) {
    validationErrors.value.phone = 'Телефон должен содержать 10-15 цифр и может начинаться с +'
    return false
  }
  validationErrors.value.phone = ''
  return true
}

const validateCoordinates = () => {
  const hasLat = formData.value.latitude !== null && formData.value.latitude !== ''
  const hasLng = formData.value.longitude !== null && formData.value.longitude !== ''
  
  if ((hasLat && !hasLng) || (!hasLat && hasLng)) {
    validationErrors.value.coordinates = 'Необходимо указать и широту, и долготу'
    return false
  }
  
  if (hasLat && hasLng) {
    const lat = parseFloat(formData.value.latitude)
    const lng = parseFloat(formData.value.longitude)
    
    if (isNaN(lat) || lat < -90 || lat > 90) {
      validationErrors.value.coordinates = 'Широта должна быть в диапазоне от -90 до 90'
      return false
    }
    
    if (isNaN(lng) || lng < -180 || lng > 180) {
      validationErrors.value.coordinates = 'Долгота должна быть в диапазоне от -180 до 180'
      return false
    }
  }
  
  validationErrors.value.coordinates = ''
  return true
}

const validateForm = () => {
  const isPhoneValid = validatePhone()
  const isCoordinatesValid = validateCoordinates()
  return isPhoneValid && isCoordinatesValid
}

const handleSubmit = () => {
  if (!validateForm()) {
    return
  }
  emit('submit', formData.value)
}
</script>

<style scoped>
.settings-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}
</style>