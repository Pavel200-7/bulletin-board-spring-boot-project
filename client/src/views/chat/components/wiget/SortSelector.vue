<!-- src/views/chat/components/wiget/SortSelector.vue -->
<template>
  <div class="sort-selector">
    <select v-model="localOrderBy" class="sort-select" @change="handleChange">
      <option :value="ORDER_BY.PUBLIC_NAME">По имени</option>
      <option :value="ORDER_BY.CREATED_AT">По дате создания</option>
      <option :value="ORDER_BY.UPDATED_AT">По дате обновления</option>
    </select>
    <select v-model="localDirection" class="sort-direction" @change="handleChange">
      <option :value="DIRECTION.ASC">По возрастанию</option>
      <option :value="DIRECTION.DESC">По убыванию</option>
    </select>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { PROFILE_ORDER_BY, SORT_DIRECTION } from '@/services/profile/types'

const ORDER_BY = PROFILE_ORDER_BY
const DIRECTION = SORT_DIRECTION

const props = defineProps({
  orderBy: {
    type: String,
    default: PROFILE_ORDER_BY.PUBLIC_NAME
  },
  direction: {
    type: String,
    default: SORT_DIRECTION.ASC
  }
})

const emit = defineEmits(['update:orderBy', 'update:direction', 'change'])

const localOrderBy = ref(props.orderBy)
const localDirection = ref(props.direction)

const handleChange = () => {
  emit('update:orderBy', localOrderBy.value)
  emit('update:direction', localDirection.value)
  emit('change')
}

watch(() => props.orderBy, (val) => {
  localOrderBy.value = val
})

watch(() => props.direction, (val) => {
  localDirection.value = val
})
</script>

<style scoped>
.sort-selector {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.sort-select,
.sort-direction {
  padding: 0.5rem 0.75rem;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.875rem;
  background: white;
  cursor: pointer;
  transition: all 0.2s;
  flex: 1;
  min-width: 0;
}

.sort-select:focus,
.sort-direction:focus {
  outline: none;
  border-color: #667eea;
}
</style>