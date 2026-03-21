<!-- src/views/admin/components/CategoryNode.vue -->
<template>
  <div class="category-node">
    <div 
      class="node-content"
      :class="{ selected: isSelected }"
      @click="handleSelect"
    >
      <!-- Значок раскрытия показываем только для не-листовых категорий -->
      <span 
        v-if="!category.leaf" 
        class="expand-icon" 
        @click.stop="toggleExpand"
      >
        {{ isExpanded ? '▼' : '▶' }}
      </span>
      <span v-else class="expand-icon-placeholder"></span>
      
      <span class="category-name">{{ category.name }}</span>
      <span v-if="category.leaf" class="leaf-badge">лист</span>
      <div class="node-actions">
        <button class="action-btn" @click.stop="handleAddChild">+</button>
        <button v-if="!category.leaf" class="action-btn" @click.stop="handleAddLeaf">🌿</button>
        <button class="action-btn" @click.stop="handleRename">✏️</button>
        <button class="action-btn" @click.stop="handleDelete">🗑️</button>
      </div>
    </div>
    
    <!-- Дети отображаем только для не-листовых и когда раскрыто -->
    <div v-if="!category.leaf && isExpanded" class="node-children">
      <div v-if="childrenLoading" class="loading-children">Загрузка...</div>
      <template v-else>
        <CategoryNode 
          v-for="child in children" 
          :key="child.id"
          :category="child"
          :selected-category-id="selectedCategoryId"
          @select="$emit('select', $event)"
          @create-child="$emit('create-child', $event)"
          @create-leaf="$emit('create-leaf', $event)"
          @rename="$emit('rename', $event)"
          @delete="$emit('delete', $event.id, $event.parentId)"
          @refresh-self="$emit('refresh-self')"
        />
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useCategory } from '@/composables/useCategory'

const props = defineProps({
  category: {
    type: Object,
    required: true
  },
  selectedCategoryId: {
    type: String,
    default: null
  }
})

const emit = defineEmits(['select', 'create-child', 'create-leaf', 'rename', 'delete', 'refresh-self'])

const { fetchCategoryWithFamily } = useCategory()

const isExpanded = ref(false)
const children = ref([])
const childrenLoading = ref(false)
const childrenLoaded = ref(false)

const isSelected = computed(() => props.selectedCategoryId === props.category.id)

const toggleExpand = async () => {
  if (isExpanded.value) {
    isExpanded.value = false
  } else {
    isExpanded.value = true
    
    if (!childrenLoaded.value && !props.category.leaf) {
      await loadChildren()
    }
  }
}

const loadChildren = async () => {
  childrenLoading.value = true
  try {
        console.log(response);

    const response = await fetchCategoryWithFamily(props.category.id)
    console.log(response);
    const categoryData = response.data?.categoryFamilyResponse
    if (categoryData && categoryData.children) {
      if (categoryData.id == props.category.id) {
        children.value = categoryData.children
      } else {
        console.log(categoryData.children)
        for (child in categoryData.children) {
          if (child.id == props.category.id) {
            children.value = categoryData.children
          }
        }
      }

      for (child in categoryData.children) {
        if (child.id == props.category.id) {
          children.value = categoryData.children

        }
      }
    }
    childrenLoaded.value = true
  } catch (err) {
    console.error('Ошибка загрузки детей категории:', err)
  } finally {
    childrenLoading.value = false
  }
}

// Обновить только детей текущей категории
const refreshChildren = async () => {
  if (childrenLoaded.value) {
    await loadChildren()
  }
}

const handleSelect = () => {
  emit('select', props.category)
}

const handleAddChild = async () => {
  const name = prompt('Введите название дочерней категории:')
  if (name && name.trim()) {
    emit('create-child', { parentId: props.category.id, name: name.trim() })
    // Обновляем детей после создания
    await refreshChildren()
  }
}

const handleAddLeaf = async () => {
  const name = prompt('Введите название листовой категории:')
  if (name && name.trim()) {
    emit('create-leaf', { parentId: props.category.id, name: name.trim() })
    await refreshChildren()
  }
}

const handleRename = () => {
  const newName = prompt('Новое название:', props.category.name)
  if (newName && newName.trim() && newName !== props.category.name) {
    emit('rename', { id: props.category.id, newName: newName.trim() })
    // Обновляем локальное имя
    props.category.name = newName
  }
}

const handleDelete = () => {
  const confirmMessage = children.value.length > 0
    ? `Удалить категорию "${props.category.name}" и все её подкатегории?` 
    : `Удалить категорию "${props.category.name}"?`
  
  if (confirm(confirmMessage)) {
    emit('delete', props.category.id, props.category.parentId)
  }
}

// Выставляем метод для внешнего вызова
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
  border-radius: 2px;
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
  transition: color 0.2s;
}

.action-btn:hover {
  color: #4a5568;
}

.node-children {
  margin-left: 1.5rem;
}

.loading-children {
  padding: 0.5rem;
  font-size: 0.75rem;
  color: #a0aec0;
  text-align: center;
}
</style>