<!-- src/views/bulletin/BulletinEditPage.vue -->
<template>
  <div class="bulletin-edit-page">
    <div class="page-header">
      <div class="header-left">
        <h1>{{ isNew ? 'Создание объявления' : 'Редактирование объявления' }}</h1>
        <span v-if="!isNew && bulletin" class="status-badge" :class="statusClass">
          {{ statusText }}
        </span>
      </div>
      <div class="header-right">
        <button 
          v-if="!isNew && bulletin && bulletin.state === 'MODIFIABLE'" 
          class="btn-approve" 
          @click="handleApprove"
          :disabled="approving"
        >
          {{ approving ? 'Проверка...' : 'Проверить и подтвердить' }}
        </button>
        <button 
          v-if="!isNew && bulletin && bulletin.state === 'APPROVED'" 
          class="btn-publish" 
          @click="handlePublish"
          :disabled="publishing"
        >
          {{ publishing ? 'Публикация...' : 'Опубликовать' }}
        </button>
        <button v-if="!isNew && bulletin" class="btn-preview" @click="preview">
          Предпросмотр
        </button>
      </div>
    </div>

    <LoadingState v-if="loading" />
    
    <ErrorState 
      v-else-if="error" 
      :error="error" 
      @retry="loadData" 
    />
    
    <BulletinForm
      v-else
      ref="formRef"
      :bulletin="bulletin"
      :is-new="isNew"
      :saving="saving"
      @submit="handleSubmit"
      @cancel="goBack"
    />
    
    <!-- Модальное окно подтверждения -->
    <div v-if="showApproveModal" class="modal-overlay" @click.self="showApproveModal = false">
      <div class="modal">
        <div class="modal-header">
          <h3>Подтверждение объявления</h3>
        </div>
        <div class="modal-body">
          <p>Проверить объявление на валидность?</p>
          <p>После подтверждения объявление получит статус "Готов к публикации".</p>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showApproveModal = false">Отмена</button>
          <button class="btn-confirm" @click="confirmApprove" :disabled="approving">
            {{ approving ? 'Проверка...' : 'Подтвердить' }}
          </button>
        </div>
      </div>
    </div>
    
    <!-- Модальное окно публикации -->
    <div v-if="showPublishModal" class="modal-overlay" @click.self="showPublishModal = false">
      <div class="modal">
        <div class="modal-header">
          <h3>Публикация объявления</h3>
        </div>
        <div class="modal-body">
          <p>Вы уверены, что хотите опубликовать это объявление?</p>
          <p>После публикации оно станет доступно всем пользователям.</p>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showPublishModal = false">Отмена</button>
          <button class="btn-confirm" @click="confirmPublish" :disabled="publishing">
            {{ publishing ? 'Публикация...' : 'Опубликовать' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useBulletin } from '@/composables/useBulletin'
import LoadingState from './components/LoadingState.vue'
import ErrorState from './components/ErrorState.vue'
import BulletinForm from './components/BulletinForm.vue'

const route = useRoute()
const router = useRouter()
const { 
  bulletin, 
  loading, 
  error, 
  fetchEditableBulletin, 
  createDraft, 
  updateBulletin,
  approve,
  publishBulletin
} = useBulletin()

const formRef = ref(null)
const saving = ref(false)
const approving = ref(false)
const publishing = ref(false)
const createdDraftId = ref(null)
const showApproveModal = ref(false)
const showPublishModal = ref(false)

const bulletinId = computed(() => route.params.id)
const isNew = computed(() => {
  if (route.name === 'bulletin-create') return true
  if (route.path.includes('/edit/new')) return true
  return bulletinId.value === 'new' || bulletinId.value === 'create'
})

const statusText = computed(() => {
  const state = bulletin.value?.state
  if (state === 'MODIFIABLE') return '📝 Черновик'
  if (state === 'APPROVED') return '✅ Готов к публикации'
  if (state === 'PUBLISHED') return '📢 Опубликовано'
  if (state === 'COMPLETED') return '✔️ Завершено'
  return state || ''
})

const statusClass = computed(() => {
  const state = bulletin.value?.state
  if (state === 'MODIFIABLE') return 'status-draft'
  if (state === 'APPROVED') return 'status-approved'
  if (state === 'PUBLISHED') return 'status-published'
  if (state === 'COMPLETED') return 'status-completed'
  return ''
})

const loadData = async () => {
  if (!isNew.value && bulletinId.value) {
    await fetchEditableBulletin(bulletinId.value)
  }
}

const initializeDraft = async () => {
  if (isNew.value && !createdDraftId.value) {
    try {
      console.log('Создаем новый черновик при инициализации...')
      const response = await createDraft()
      
      const newId = response.data?.bulletinResponse?.id || response.data?.id
      if (newId) {
        createdDraftId.value = newId
        await fetchEditableBulletin(createdDraftId.value)
      }
    } catch (err) {
      console.error('Ошибка при создании черновика:', err)
    }
  }
}

const handleSubmit = async (formData) => {
  saving.value = true
  try {
    let bulletinIdToSave = null
    
    if (isNew.value) {
      if (!createdDraftId.value) {
        await initializeDraft()
      }
      bulletinIdToSave = createdDraftId.value
    } else {
      bulletinIdToSave = bulletinId.value
    }
    
    if (!bulletinIdToSave) {
      throw new Error('Не удалось получить ID объявления')
    }
    
    const characteristics = (formData.characteristics || [])
      .filter(c => c.characteristicId && c.characteristicValueId)
      .map(c => ({
        characteristicId: c.characteristicId,
        characteristicValueId: c.characteristicValueId
      }))
    
    const bulletinRequest = {
      id: bulletinIdToSave,
      title: formData.title,
      description: formData.description,
      price: parseFloat(formData.price),
      categoryId: formData.categoryId,
      characteristics: characteristics
    }
    
    await updateBulletin({ bulletinRequest })
    
    // Перезагружаем данные
    await fetchEditableBulletin(bulletinIdToSave)
    
    alert('Объявление успешно сохранено!')
    
  } catch (err) {
    console.error('Ошибка сохранения:', err)
    
    if (err.response?.data?.validationErrors) {
      const errors = err.response.data.validationErrors
      alert('Ошибка валидации:\n' + Object.entries(errors).map(([k, v]) => `${k}: ${v}`).join('\n'))
    } else if (err.response?.data?.message) {
      alert(`Ошибка: ${err.response.data.message}`)
    } else {
      alert('Произошла ошибка при сохранении объявления')
    }
  } finally {
    saving.value = false
  }
}

const handleApprove = () => {
  showApproveModal.value = true
}

const confirmApprove = async () => {
  approving.value = true
  try {
    await approve(bulletin.value.id)
    // Перезагружаем данные
    await fetchEditableBulletin(bulletin.value.id)
    alert('Объявление успешно подтверждено! Теперь его можно опубликовать.')
    showApproveModal.value = false
  } catch (err) {
    console.error('Ошибка подтверждения:', err)
    alert(err.response?.data?.message || 'Ошибка при подтверждении объявления')
  } finally {
    approving.value = false
  }
}

const handlePublish = () => {
  showPublishModal.value = true
}

const confirmPublish = async () => {
  publishing.value = true
  try {
    await publishBulletin(bulletin.value.id)
    // Перезагружаем данные
    await fetchEditableBulletin(bulletin.value.id)
    alert('Объявление успешно опубликовано!')
    showPublishModal.value = false
  } catch (err) {
    console.error('Ошибка публикации:', err)
    alert(err.response?.data?.message || 'Ошибка при публикации объявления')
  } finally {
    publishing.value = false
  }
}

const preview = () => {
  if (bulletin.value?.id) {
    router.push(`/bulletin/view/${bulletin.value.id}`)
  }
}

const goBack = () => {
  router.back()
}

onMounted(async () => {
  await loadData()
  if (isNew.value) {
    await initializeDraft()
  }
})
</script>

<style scoped>
.bulletin-edit-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 2rem;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 1rem;
  flex-wrap: wrap;
}

.header-left h1 {
  margin: 0;
  color: #333;
}

.header-right {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.status-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.875rem;
  font-weight: 500;
}

.status-draft {
  background: #edf2f7;
  color: #4a5568;
}

.status-approved {
  background: #fef3c7;
  color: #d97706;
}

.status-published {
  background: #c6f6d5;
  color: #2f855a;
}

.status-completed {
  background: #fed7d7;
  color: #c53030;
}

.btn-preview, .btn-approve, .btn-publish {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.btn-preview {
  background: #667eea;
  color: white;
}

.btn-preview:hover {
  background: #5a67d8;
}

.btn-approve {
  background: #f59e0b;
  color: white;
}

.btn-approve:hover:not(:disabled) {
  background: #d97706;
}

.btn-publish {
  background: #48bb78;
  color: white;
}

.btn-publish:hover:not(:disabled) {
  background: #38a169;
}

.btn-approve:disabled, .btn-publish:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* Модальное окно */
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

.modal {
  background: white;
  width: 400px;
  max-width: 90%;
  border-radius: 8px;
  overflow: hidden;
}

.modal-header {
  padding: 1rem;
  background: #f7fafc;
  border-bottom: 1px solid #e2e8f0;
}

.modal-header h3 {
  margin: 0;
  color: #333;
}

.modal-body {
  padding: 1rem;
}

.modal-body p {
  margin: 0.5rem 0;
  color: #4a5568;
}

.modal-footer {
  padding: 1rem;
  border-top: 1px solid #e2e8f0;
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
}

.btn-cancel {
  padding: 0.5rem 1rem;
  background: #e2e8f0;
  color: #4a5568;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-cancel:hover {
  background: #cbd5e0;
}

.btn-confirm {
  padding: 0.5rem 1rem;
  background: #48bb78;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-confirm:hover:not(:disabled) {
  background: #38a169;
}

.btn-confirm:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>