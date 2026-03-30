<template>
  <form @submit.prevent="handleSubmit" class="profile-form">
    <div class="form-group">
      <label>Публичное имя *</label>
      <input
        v-model="formData.publicName"
        type="text"
        placeholder="Как вас будут видеть другие пользователи"
        class="form-input"
        :class="{ 'has-error': errors.publicName }"
      />
      <div v-if="errors.publicName" class="field-error">{{ errors.publicName }}</div>
      <div class="field-hint">Только буквы (русские/английские) и цифры</div>
    </div>

    <div class="form-group">
      <label>Описание</label>
      <textarea
        v-model="formData.description"
        rows="4"
        placeholder="Расскажите о себе"
        class="form-textarea"
        :class="{ 'has-error': errors.description }"
      ></textarea>
      <div v-if="errors.description" class="field-error">{{ errors.description }}</div>
      <div class="field-hint">Максимум 500 символов</div>
    </div>

    <div class="form-actions">
      <button type="submit" class="btn-submit" :disabled="loading">
        {{ loading ? 'Сохранение...' : (isNew ? 'Создать профиль' : 'Сохранить изменения') }}
      </button>
    </div>
  </form>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  initialData: {
    type: Object,
    default: () => ({
      publicName: '',
      description: ''
    })
  },
  loading: {
    type: Boolean,
    default: false
  },
  isNew: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['submit'])

const formData = ref({
  publicName: props.initialData.publicName || '',
  description: props.initialData.description || ''
})

const errors = ref({
  publicName: '',
  description: ''
})

watch(() => props.initialData, (newData) => {
  formData.value = {
    publicName: newData.publicName || '',
    description: newData.description || ''
  }
}, { deep: true })

const validate = () => {
  let isValid = true
  errors.value = { publicName: '', description: '' }
  
  const nameRegex = /^[ a-zA-Zа-яА-ЯёЁ0-9]+$/
  if (!formData.value.publicName?.trim()) {
    errors.value.publicName = 'Публичное имя обязательно'
    isValid = false
  } else if (!nameRegex.test(formData.value.publicName)) {
    errors.value.publicName = 'Только буквы (русские/английские) и цифры'
    isValid = false
  }
  
  if (formData.value.description?.length > 500) {
    errors.value.description = 'Описание не должно превышать 500 символов'
    isValid = false
  }
  
  return isValid
}

const handleSubmit = () => {
  if (validate()) {
    emit('submit', {
      publicName: formData.value.publicName.trim(),
      description: formData.value.description?.trim() || ''
    })
  }
}
</script>

<style scoped>
.profile-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group label {
  font-weight: 500;
  color: #4a5568;
}

.form-input,
.form-textarea {
  padding: 0.5rem 0.75rem;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.875rem;
  transition: all 0.2s;
  font-family: inherit;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

.form-input.has-error,
.form-textarea.has-error {
  border-color: #e53e3e;
}

.field-error {
  color: #e53e3e;
  font-size: 0.75rem;
}

.field-hint {
  color: #a0aec0;
  font-size: 0.7rem;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #e2e8f0;
}

.btn-submit {
  padding: 0.5rem 1.5rem;
  background: #48bb78;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.875rem;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-submit:hover:not(:disabled) {
  background: #38a169;
  transform: translateY(-1px);
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>