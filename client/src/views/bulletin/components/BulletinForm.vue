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
      @update:model-value="handleCategoryChange"
    />
    
    <CharacteristicsEditor
      v-model="formData.characteristics"
      :category-id="formData.categoryId"
      :error="errors.characteristics"
      :key="editorKey"
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
import { ref, watch } from 'vue'
import { useCategory } from '@/composables/useCategory'
import TextField from './TextField.vue'
import TextareaField from './TextareaField.vue'
import CategorySelector from './CategorySelector.vue'
import CharacteristicsEditor from './CharacteristicsEditor.vue'

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

const formData = ref({
  title: '',
  description: '',
  price: '',
  categoryId: '',
  characteristics: []
})

const errors = ref({
  title: '',
  description: '',
  price: '',
  categoryId: '',
  characteristics: ''
})

// Ключ для принудительного обновления CharacteristicsEditor
const editorKey = ref(0)

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

// Обработчик изменения категории
const handleCategoryChange = (newCategoryId) => {
  console.log('Категория изменена на:', newCategoryId)
  
  // Если категория изменилась (и это не пустое значение)
  if (newCategoryId !== formData.value.categoryId) {
    // Сбрасываем характеристики
    formData.value.characteristics = []
    // Принудительно обновляем CharacteristicsEditor
    editorKey.value++
    console.log('Характеристики сброшены')
  }
}

// Преобразуем данные из бэкенда в формат формы
const transformBulletinToForm = (bulletinData) => {
  if (!bulletinData) return null
  
  console.log('Преобразование данных из бэкенда:', bulletinData)
  
  // Преобразуем характеристики
  const characteristics = (bulletinData.characteristics || []).map(char => {
    return {
      characteristicId: char.name?.id || char.characteristicId,
      characteristicValueId: char.value?.id || char.characteristicValueId
    }
  })
  
  console.log('Преобразованные характеристики:', characteristics)
  
  return {
    title: bulletinData.title || '',
    description: bulletinData.description || '',
    price: bulletinData.price || '',
    categoryId: bulletinData.category?.id || '',
    characteristics: characteristics
  }
}

// Следим за изменением bulletin и обновляем форму
watch(() => props.bulletin, (newBulletin) => {
  if (newBulletin) {
    const transformed = transformBulletinToForm(newBulletin)
    if (transformed) {
      formData.value = transformed
      console.log('Форма обновлена:', formData.value)
      // Сбрасываем ключ редактора
      editorKey.value++
    }
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
  console.log('Форма отправлена, данные:', formData.value)
  
  if (await validate()) {
    const submitData = {
      title: formData.value.title,
      description: formData.value.description,
      price: parseFloat(formData.value.price),
      categoryId: formData.value.categoryId,
      characteristics: formData.value.characteristics.filter(c => c.characteristicId && c.characteristicValueId)
    }
    
    console.log('Отправляем данные:', submitData)
    emit('submit', submitData)
  } else {
    console.log('Валидация не пройдена')
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