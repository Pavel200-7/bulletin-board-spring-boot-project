<!-- src/views/bulletin/components/BulletinForm.vue -->
<template>
  <form @submit.prevent="handleSubmit" class="bulletin-form">
    <TextField
      v-model="formData.title"
      label="Название"
      placeholder="Введите название объявления"
      required
      :error="errors.title"
    />
    
    <TextareaField
      v-model="formData.description"
      label="Описание"
      rows="6"
      placeholder="Подробно опишите товар или услугу"
      required
      :error="errors.description"
    />
    
    <TextField
      v-model="formData.price"
      label="Цена"
      type="number"
      placeholder="0"
      required
      :error="errors.price"
    />
    
    <CategorySelector
      v-model="formData.categoryId"
      :error="errors.categoryId"
    />
    
    <CharacteristicsEditor
      v-model="formData.characteristics"
      :category-id="formData.categoryId"
      :error="errors.characteristics"
    />
    
    <ImageUploader
      :key="uploaderKey"
      :bulletin-id="bulletinId"
      :existing-files="formData.images"
      @upload="handleImageUpload"
      @delete="handleImageDelete"
      @set-main="handleSetMainImage"
      @files-updated="handleFilesUpdated"
    />
    
    <div class="form-actions">
      <button type="button" class="btn-cancel" @click="$emit('cancel')">
        Отмена
      </button>
      <button type="submit" class="btn-submit" :disabled="saving">
        {{ saving ? 'Сохранение...' : 'Сохранить' }}
      </button>
    </div>
  </form>
</template>

<script setup>
import { ref, watch, computed, nextTick } from 'vue'
import { useCategory } from '@/composables/useCategory'
import { useBulletin } from '@/composables/useBulletin'
import TextField from './wigets/TextField.vue'
import TextareaField from './wigets/TextareaField.vue'
import CategorySelector from './category/CategorySelector.vue'
import CharacteristicsEditor from './characteristic/CharacteristicsEditor.vue'
import ImageUploader from './image/ImageUploader.vue'

const props = defineProps({
  bulletin: {
    type: Object,
    default: null
  },
  isNew: {
    type: Boolean,
    default: false
  },
  saving: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['submit', 'cancel'])

const { fetchCategory } = useCategory()
const { addImage, removeImage, setMainImage } = useBulletin()

const bulletinId = computed(() => props.bulletin?.id || null)
const uploaderKey = ref(0)

const formData = ref({
  title: '',
  description: '',
  price: '',
  categoryId: '',
  characteristics: [],
  images: []
})

const errors = ref({
  title: '',
  description: '',
  price: '',
  categoryId: '',
  characteristics: ''
})

const isUpdatingFromProps = ref(false)

const validateCategoryIsLeaf = async (categoryId) => {
  if (!categoryId) return false
  
  try {
    const response = await fetchCategory(categoryId)
    const category = response.data?.categoryResponse
    return category?.leaf === true
  } catch (err) {
    console.error('Ошибка проверки категории:', err)
    return false
  }
}

const handleImageUpload = async ({ fileId, bulletinId: id }) => {
  if (id) {
    await addImage(id, fileId)
  }
}

const handleImageDelete = async ({ fileId, bulletinId: id }) => {
  if (id) {
    await removeImage(id, fileId)
  }
}

const handleSetMainImage = async ({ imageId, bulletinId: id }) => {
  if (id) {
    await setMainImage(id, imageId)
  }
}

const handleFilesUpdated = (files) => {
  if (!isUpdatingFromProps.value) {
    formData.value.images = files
  }
}

const transformBulletinToForm = (bulletinData) => {
  if (!bulletinData) return null
  
  const characteristics = (bulletinData.characteristics || []).map(char => ({
    characteristicId: char.name?.id || char.characteristicId,
    characteristicValueId: char.value?.id || char.characteristicValueId
  }))
  
  const images = (bulletinData.images || []).map(img => ({
    id: img.id,              // ← ID в БД (bulletin_image.id)
    minioId: img.imageId,    // ← ID в MinIO (для URL)
    main: img.main || false
  }))
  
  return {
    title: bulletinData.title || '',
    description: bulletinData.description || '',
    price: bulletinData.price || '',
    categoryId: bulletinData.category?.id || '',
    characteristics: characteristics,
    images: images
  }
}

watch(() => props.bulletin, (newBulletin) => {
  if (newBulletin) {
    isUpdatingFromProps.value = true
    const transformed = transformBulletinToForm(newBulletin)
    if (transformed) {
      formData.value = transformed
      uploaderKey.value++
    }
    nextTick(() => {
      isUpdatingFromProps.value = false
    })
  }
}, { immediate: true, deep: true })

const validate = async () => {
  let isValid = true
  errors.value = {
    title: '',
    description: '',
    price: '',
    categoryId: '',
    characteristics: ''
  }
  
  if (!formData.value.title?.trim()) {
    errors.value.title = 'Название обязательно'
    isValid = false
  }
  
  if (!formData.value.description?.trim()) {
    errors.value.description = 'Описание обязательно'
    isValid = false
  }
  
  if (!formData.value.price || formData.value.price <= 0) {
    errors.value.price = 'Цена должна быть больше 0'
    isValid = false
  }
  
  if (!formData.value.categoryId) {
    errors.value.categoryId = 'Выберите категорию'
    isValid = false
  } else {
    const isLeaf = await validateCategoryIsLeaf(formData.value.categoryId)
    if (!isLeaf) {
      errors.value.categoryId = 'Выберите листовую категорию (конечную)'
      isValid = false
    }
  }
  
  return isValid
}

const handleSubmit = async () => {
  if (await validate()) {
    const submitData = {
      title: formData.value.title,
      description: formData.value.description,
      price: parseFloat(formData.value.price),
      categoryId: formData.value.categoryId,
      characteristics: formData.value.characteristics.filter(c => c.characteristicId && c.characteristicValueId),
      images: formData.value.images
    }
    
    emit('submit', submitData)
  }
}
</script>

<style scoped>
.bulletin-form {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #e2e8f0;
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

.btn-submit {
  padding: 0.5rem 1rem;
  background: #48bb78;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-submit:hover:not(:disabled) {
  background: #38a169;
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>