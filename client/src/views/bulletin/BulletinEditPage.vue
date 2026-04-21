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
        <ApproveButton 
          v-if="!isNew && bulletin && bulletin.state === 'MODIFIABLE'" 
          :loading="approving"
          @click="handleApprove"
        />
        <PublishButton 
          v-if="!isNew && bulletin && bulletin.state === 'APPROVED'" 
          :loading="publishing"
          @click="handlePublish"
        />
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
    
    <ApproveModal
      :show="showApproveModal"
      :loading="approving"
      @confirm="confirmApprove"
      @cancel="showApproveModal = false"
      @close="showApproveModal = false"
    />
    
    <PublishModal
      :show="showPublishModal"
      :loading="publishing"
      @confirm="confirmPublish"
      @cancel="showPublishModal = false"
      @close="showPublishModal = false"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useBulletin } from '@/composables/useBulletin'
import LoadingState from './components/wigets/LoadingState.vue'
import ErrorState from './components/wigets/ErrorState.vue'
import BulletinForm from './components/BulletinForm.vue'
import ApproveButton from './components/buttons/ApproveButton.vue'
import PublishButton from './components/buttons/PublishButton.vue'
import PreviewButton from './components/buttons/PreviewButton.vue'
import ApproveModal from './components/modals/ApproveModal.vue'
import PublishModal from './components/modals/PublishModal.vue'

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
</style>