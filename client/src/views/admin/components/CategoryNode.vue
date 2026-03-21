<!-- src/views/admin/components/CategoryNode.vue -->
<template>
  <div class="category-node">
    <div 
      class="node-content"
      :class="{ selected: isSelected }"
      @click="handleSelect"
    >
      <span 
        v-if="!isLeaf && hasChildren" 
        class="expand-icon" 
        @click.stop="toggleExpand"
      >
        {{ isExpanded ? '▼' : '▶' }}
      </span>
      <span v-else class="expand-icon-placeholder"></span>
      
      <span class="category-name">{{ category.name }}</span>
      <span v-if="isLeaf" class="leaf-badge">лист</span>
      
      <div class="node-actions">
        <button v-if="!isLeaf" class="action-btn" @click.stop="openAddChildModal">+</button>
        <button v-if="!isLeaf" class="action-btn" @click.stop="openAddLeafModal">🌿</button>
        <button class="action-btn" @click.stop="openRenameModal">✏️</button>
        <button class="action-btn" @click.stop="handleDelete">🗑️</button>
      </div>
    </div>
    
    <div v-if="!isLeaf && isExpanded" class="node-children">
      <div v-if="loading" class="loading-children">Загрузка...</div>
      <div v-else-if="error" class="error-children">{{ error }}</div>
      <template v-else-if="children.length > 0">
        <CategoryNode 
          v-for="child in children" 
          :key="child.id"
          :category="child"
          :is-leaf="child.leaf"
          :selected-category-id="selectedCategoryId"
          @select="handleChildSelect"
          @refresh-parent="handleRefreshParent"
        />
      </template>
      <div v-else class="no-children">Нет дочерних категорий</div>
    </div>
    
    <CategoryFormModal
      :visible="modalVisible"
      :mode="modalMode"
      :title="modalTitle"
      :label="modalLabel"
      :placeholder="modalPlaceholder"
      :submit-text="modalSubmitText"
      :initial-name="modalInitialName"
      :on-submit="modalOnSubmit || (async () => {})"
      @close="modalVisible = false"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useCategory } from '@/composables/useCategory'
import CategoryFormModal from './CategoryFormModal.vue'

const props = defineProps({
  category: {
    type: Object,
    required: true
  },
  isLeaf: {
    type: Boolean,
    default: false
  },
  selectedCategoryId: {
    type: String,
    default: null
  }
})

const emit = defineEmits(['select', 'refresh-parent'])

const { 
  fetchCategoryWithChildren, 
  createChildCategory, 
  createLeafCategory, 
  renameCategory, 
  deleteChildCategory,
  deleteRootCategory
} = useCategory()

// Состояние модального окна
const modalVisible = ref(false)
const modalMode = ref('create')
const modalInitialName = ref('')
const modalOnSubmit = ref(null)

const modalTitle = computed(() => {
  switch (modalMode.value) {
    case 'create-child': return 'Создание дочерней категории'
    case 'create-leaf': return 'Создание листовой категории'
    case 'rename': return 'Переименование категории'
    default: return ''
  }
})
const modalLabel = computed(() => 'Название категории')
const modalPlaceholder = computed(() => {
  switch (modalMode.value) {
    case 'create-child': return 'Введите название дочерней категории'
    case 'create-leaf': return 'Введите название листовой категории'
    case 'rename': return 'Новое название'
    default: return 'Введите название'
  }
})
const modalSubmitText = computed(() => {
  return modalMode.value === 'rename' ? 'Сохранить' : 'Создать'
})

const isExpanded = ref(false)
const children = ref([])
const loading = ref(false)
const error = ref(null)
const loaded = ref(false)

const isSelected = computed(() => props.selectedCategoryId === props.category.id)
const hasChildren = computed(() => children.value.length > 0)

const handleApiError = (err) => {
  const response = err.response?.data
  if (response?.message) {
    return response.message
  }
  return err.message || 'Произошла ошибка'
}

const loadChildren = async () => {
  loading.value = true
  error.value = null
  try {
    const response = await fetchCategoryWithChildren(props.category.id)
    children.value = response.data?.categoryWithChildrenResponse?.children || []
    loaded.value = true
  } catch (err) {
    error.value = handleApiError(err)
    console.error('Ошибка загрузки детей:', err)
  } finally {
    loading.value = false
  }
}

const refreshChildren = async () => {
  if (loaded.value) {
    await loadChildren()
  }
}

const handleRefreshParent = () => {
  refreshChildren()
}

onMounted(() => {
  if (!props.isLeaf) {
    loadChildren()
  }
})

const toggleExpand = async () => {
  if (isExpanded.value) {
    isExpanded.value = false
  } else {
    isExpanded.value = true
    if (!loaded.value) {
      await loadChildren()
    }
  }
}

const handleSelect = () => {
  emit('select', props.category)
}

const handleChildSelect = (category) => {
  emit('select', category)
}

const openAddChildModal = () => {
  modalMode.value = 'create-child'
  modalInitialName.value = ''
  modalOnSubmit.value = async (name) => {
    await createChildCategory(props.category.id, name)
    await refreshChildren()
  }
  modalVisible.value = true
}

const openAddLeafModal = () => {
  modalMode.value = 'create-leaf'
  modalInitialName.value = ''
  modalOnSubmit.value = async (name) => {
    await createLeafCategory(props.category.id, name)
    await refreshChildren()
  }
  modalVisible.value = true
}

const openRenameModal = () => {
  modalMode.value = 'rename'
  modalInitialName.value = props.category.name
  modalOnSubmit.value = async (name) => {
    await renameCategory(props.category.id, name)
    props.category.name = name
  }
  modalVisible.value = true
}

const handleDelete = async () => {
  const msg = children.value.length > 0 
    ? `Удалить категорию "${props.category.name}" и все её подкатегории?`
    : `Удалить категорию "${props.category.name}"?`
  
  if (confirm(msg)) {
    try {
      if (props.category.parentId === null) {
        await deleteRootCategory(props.category.id)
        emit('refresh-parent')
      } else {
        await deleteChildCategory(props.category.parentId, props.category.id)
        emit('refresh-parent')
      }
    } catch (err) {
      const errorMsg = handleApiError(err)
      alert(errorMsg)
    }
  }
}

defineExpose({
  refreshChildren
})
</script>

<style scoped>
.category-node {
  margin-left: 0;
}

.node-content {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem;
  cursor: pointer;
  border: 1px solid transparent;
  transition: background 0.2s;
}

.node-content:hover {
  background: #f7fafc;
}

.node-content.selected {
  background: #ebf4ff;
  border-color: #667eea;
}

.expand-icon {
  font-size: 0.75rem;
  color: #a0aec0;
  cursor: pointer;
  min-width: 16px;
}

.expand-icon:hover {
  color: #4a5568;
}

.expand-icon-placeholder {
  min-width: 16px;
}

.category-name {
  flex: 1;
  font-size: 0.875rem;
  color: #2d3748;
}

.leaf-badge {
  font-size: 0.7rem;
  padding: 0.125rem 0.375rem;
  background: #edf2f7;
  color: #4a5568;
}

.node-actions {
  display: none;
  gap: 0.25rem;
}

.node-content:hover .node-actions {
  display: flex;
}

.action-btn {
  padding: 0.125rem 0.375rem;
  background: transparent;
  border: none;
  cursor: pointer;
  font-size: 0.75rem;
  color: #718096;
}

.action-btn:hover {
  color: #4a5568;
}

.node-children {
  margin-left: 1.5rem;
}

.loading-children, .error-children, .no-children {
  padding: 0.5rem;
  font-size: 0.75rem;
  text-align: center;
}

.loading-children {
  color: #a0aec0;
}

.error-children {
  color: #e53e3e;
}

.no-children {
  color: #a0aec0;
}
</style>