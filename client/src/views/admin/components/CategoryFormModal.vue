<!-- src/views/admin/components/CategoryFormModal.vue -->
<template>
  <div v-if="visible" class="modal-overlay" @click.self="handleClose">
    <div class="modal-container">
      <div class="modal-header">
        <h3>{{ title }}</h3>
        <button class="close-btn" @click="handleClose">✕</button>
      </div>
      
      <div class="modal-body">
        <div class="form-group">
          <label :for="inputId">{{ label }}</label>
          <input
            :id="inputId"
            v-model="formData.name"
            type="text"
            class="form-input"
            :placeholder="placeholder"
            @keyup.enter="handleSubmit"
          />
        </div>
        
        <div v-if="error" class="error-message">
          {{ error }}
        </div>
      </div>
      
      <div class="modal-footer">
        <button class="btn btn-secondary" @click="handleClose">Отмена</button>
        <button class="btn btn-primary" @click="handleSubmit" :disabled="loading">
          {{ loading ? 'Сохранение...' : submitText }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  mode: {
    type: String,
    default: 'create'
  },
  title: {
    type: String,
    default: ''
  },
  label: {
    type: String,
    default: 'Название'
  },
  placeholder: {
    type: String,
    default: 'Введите название'
  },
  submitText: {
    type: String,
    default: 'Создать'
  },
  initialName: {
    type: String,
    default: ''
  },
  onSubmit: {
    type: Function,
    required: true
  }
})

const emit = defineEmits(['close'])

const formData = ref({ name: '' })
const loading = ref(false)
const error = ref('')
const inputId = `input-${Math.random().toString(36).substr(2, 9)}`

watch(() => props.visible, (newVal) => {
  if (newVal) {
    formData.value.name = props.initialName || ''
    error.value = ''
    loading.value = false
  }
})

const validate = () => {
  if (!formData.value.name.trim()) {
    error.value = 'Название не может быть пустым'
    return false
  }
  if (formData.value.name.length > 100) {
    error.value = 'Название не должно превышать 100 символов'
    return false
  }
  return true
}

const handleSubmit = async () => {
  if (!validate()) return
  
  loading.value = true
  error.value = ''
  
  try {
    // Вызываем переданный колбэк
    await props.onSubmit(formData.value.name.trim())
    // Если успешно — закрываем
    handleClose()
  } catch (err) {
    // Если ошибка — показываем в модальном окне
    console.error('Submit error:', err)
    
    const response = err.response?.data
    if (response?.validationErrors) {
      const firstError = Object.values(response.validationErrors)[0]
      error.value = firstError
    } else if (response?.message) {
      error.value = response.message
    } else {
      error.value = err.message || 'Произошла ошибка'
    }
  } finally {
    loading.value = false
  }
}

const handleClose = () => {
  formData.value.name = ''
  error.value = ''
  loading.value = false
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

.modal-container {
  background: white;
  width: 400px;
  max-width: 90%;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  border-bottom: 1px solid #e2e8f0;
}

.modal-header h3 {
  margin: 0;
  color: #333;
  font-size: 1.125rem;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.25rem;
  cursor: pointer;
  color: #a0aec0;
  padding: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  color: #4a5568;
}

.modal-body {
  padding: 1rem;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #4a5568;
  font-size: 0.875rem;
  font-weight: 500;
}

.form-input {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #e2e8f0;
  font-size: 0.875rem;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
}

.error-message {
  color: #e53e3e;
  font-size: 0.75rem;
  margin-top: 0.5rem;
}

.modal-footer {
  padding: 1rem;
  border-top: 1px solid #e2e8f0;
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
}

.btn {
  padding: 0.5rem 1rem;
  border: none;
  cursor: pointer;
  font-size: 0.875rem;
}

.btn-primary {
  background: #667eea;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #5a67d8;
}

.btn-secondary {
  background: #e2e8f0;
  color: #4a5568;
}

.btn-secondary:hover {
  background: #cbd5e0;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>