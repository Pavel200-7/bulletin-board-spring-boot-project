<!-- src/views/bulletin/components/category/CategoryNode.vue -->
<template>
  <div class="category-node">
    <div 
      class="category-item"
      :class="{ 
        selected: isSelected,
        leaf: category.leaf,
        nonleaf: !category.leaf
      }"
      @click="selectCategory"
    >
      <span 
        class="expand-icon" 
        :class="{ 'has-children': hasChildren }"
        @click.stop="toggleExpand"
      >
        {{ expandIcon }}
      </span>
      <span class="category-name">{{ category.name }}</span>
      <span v-if="category.leaf" class="leaf-badge">Листовая</span>
      <span v-else-if="hasChildren" class="nonleaf-badge">Категория</span>
      <span v-else class="empty-badge">Пустая</span>
    </div>
    
    <div v-if="expanded && children.length" class="children">
      <CategoryNode
        v-for="child in children"
        :key="child.id"
        :category="child"
        :selected-id="selectedId"
        @select="$emit('select', $event)"
      />
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
  selectedId: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['select'])

const { fetchCategoryWithChildren } = useCategory()
const children = ref([])
const expanded = ref(false)
const loaded = ref(false)
const loading = ref(false)

const hasChildren = computed(() => {
  if (props.category.childrenCount !== undefined) {
    return props.category.childrenCount > 0
  }
  if (loaded.value) {
    return children.value.length > 0
  }
  return !props.category.leaf
})

const expandIcon = computed(() => {
  if (!hasChildren.value) return '•'
  return expanded.value ? '▼' : '▶'
})

const isSelected = computed(() => {
  return props.selectedId === props.category.id
})

const canSelect = computed(() => {
  return props.category.leaf === true
})

const loadChildren = async () => {
  if (loaded.value || loading.value) return
  
  loading.value = true
  try {
    const response = await fetchCategoryWithChildren(props.category.id)
    children.value = response.data?.categoryWithChildrenResponse?.children || []
    loaded.value = true
  } catch (err) {
    console.error('Ошибка загрузки детей:', err)
  } finally {
    loading.value = false
  }
}

const toggleExpand = async (event) => {
  event.stopPropagation()
  
  if (!hasChildren.value) return
  
  if (!expanded.value) {
    await loadChildren()
    expanded.value = true
  } else {
    expanded.value = false
  }
}

const selectCategory = () => {
  if (canSelect.value) {
    emit('select', props.category.id)
  }
}
</script>

<style scoped>
.category-node {
  margin-left: 0.5rem;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 0.5rem;
  border-radius: 4px;
  transition: all 0.2s;
}

.category-item:hover {
  background: #f7fafc;
}

.category-item.selected {
  background: #e6f0ff;
  color: #667eea;
  border-left: 3px solid #667eea;
}

.category-item.leaf {
  cursor: pointer;
}

.category-item.nonleaf {
  cursor: default;
  color: #4a5568;
}

.expand-icon {
  width: 20px;
  display: inline-block;
  text-align: center;
  font-size: 0.75rem;
  user-select: none;
}

.expand-icon.has-children {
  cursor: pointer;
  color: #667eea;
}

.expand-icon.has-children:hover {
  color: #4299e1;
}

.category-name {
  flex: 1;
  font-size: 0.875rem;
}

.leaf-badge {
  font-size: 0.7rem;
  padding: 0.125rem 0.375rem;
  background: #c6f6d5;
  color: #2f855a;
  border-radius: 4px;
}

.nonleaf-badge {
  font-size: 0.7rem;
  padding: 0.125rem 0.375rem;
  background: #bee3f8;
  color: #2c5282;
  border-radius: 4px;
}

.empty-badge {
  font-size: 0.7rem;
  padding: 0.125rem 0.375rem;
  background: #edf2f7;
  color: #a0aec0;
  border-radius: 4px;
}

.children {
  margin-left: 1.5rem;
  border-left: 1px dashed #e2e8f0;
}
</style>