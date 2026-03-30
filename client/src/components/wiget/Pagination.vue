<!-- src/views/bulletin/components/wiget/Pagination.vue -->
<template>
  <div class="pagination">
    <!-- Кнопка "В начало" -->
    <button 
      class="pagination-btn" 
      :disabled="currentPage === 0"
      @click="goToPage(0)"
      title="Первая страница"
    >
      ⏮
    </button>

    <!-- Кнопка "На 10 назад" -->
    <button 
      class="pagination-btn" 
      :disabled="currentPage < 10"
      @click="goToPage(currentPage - 10)"
      title="На 10 страниц назад"
    >
      ⬅️ 10
    </button>

    <!-- Кнопка "На 5 назад" -->
    <button 
      class="pagination-btn" 
      :disabled="currentPage === 0"
      @click="goToPage(currentPage - 5)"
      title="На 5 страниц назад"
    >
      ⬅️ 5
    </button>

    <!-- Кнопка "На 1 назад" -->
    <button 
      class="pagination-btn" 
      :disabled="currentPage === 0"
      @click="goToPage(currentPage - 1)"
      title="Предыдущая страница"
    >
      ←
    </button>

    <!-- Номера страниц (до 5 вперед и 5 назад) -->
    <template v-for="page in visiblePages" :key="page">
      <button 
        v-if="page >= 0 && page < totalPages"
        class="pagination-btn"
        :class="{ active: page === currentPage }"
        @click="goToPage(page)"
      >
        {{ page + 1 }}
      </button>
      <span v-else-if="page === -1" class="pagination-dots">...</span>
    </template>

    <!-- Кнопка "На 1 вперед" -->
    <button 
      class="pagination-btn" 
      :disabled="currentPage === totalPages - 1"
      @click="goToPage(currentPage + 1)"
      title="Следующая страница"
    >
      →
    </button>

    <!-- Кнопка "На 5 вперед" -->
    <button 
      class="pagination-btn" 
      :disabled="currentPage >= totalPages - 5"
      @click="goToPage(currentPage + 5)"
      title="На 5 страниц вперед"
    >
      5 ➡️
    </button>

    <!-- Кнопка "На 10 вперед" -->
    <button 
      class="pagination-btn" 
      :disabled="currentPage >= totalPages - 10"
      @click="goToPage(currentPage + 10)"
      title="На 10 страниц вперед"
    >
      10 ➡️
    </button>

    <!-- Кнопка "В конец" -->
    <button 
      class="pagination-btn" 
      :disabled="currentPage === totalPages - 1"
      @click="goToPage(totalPages - 1)"
      title="Последняя страница"
    >
      ⏭
    </button>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  currentPage: {
    type: Number,
    required: true
  },
  totalPages: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['page-change'])

// Вычисляем видимые страницы (текущая + 5 назад + 5 вперед)
const visiblePages = computed(() => {
  const total = props.totalPages
  const current = props.currentPage
  const range = 5 // количество страниц в каждую сторону
  
  let start = Math.max(0, current - range)
  let end = Math.min(total - 1, current + range)
  
  // Если страниц меньше чем нужно, расширяем
  if (end - start < range * 2) {
    if (start === 0) {
      end = Math.min(total - 1, start + range * 2)
    } else if (end === total - 1) {
      start = Math.max(0, end - range * 2)
    }
  }
  
  const pages = []
  
  // Добавляем первую страницу и многоточие, если нужно
  if (start > 0) {
    pages.push(0)
    if (start > 1) pages.push(-1) // многоточие
  }
  
  // Добавляем страницы из диапазона
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  
  // Добавляем последнюю страницу и многоточие, если нужно
  if (end < total - 1) {
    if (end < total - 2) pages.push(-1) // многоточие
    pages.push(total - 1)
  }
  
  return pages
})

const goToPage = (page) => {
  if (page >= 0 && page < props.totalPages && page !== props.currentPage) {
    emit('page-change', page)
  }
}
</script>

<style scoped>
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-top: 2rem;
  padding: 1rem;
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.pagination-btn {
  min-width: 40px;
  height: 40px;
  padding: 0 0.75rem;
  background: #f7fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.875rem;
  font-weight: 500;
  color: #4a5568;
  cursor: pointer;
  transition: all 0.2s ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.pagination-btn:hover:not(:disabled):not(.active) {
  background: #edf2f7;
  border-color: #cbd5e0;
  transform: translateY(-1px);
}

.pagination-btn:active:not(:disabled):not(.active) {
  transform: translateY(0);
}

.pagination-btn.active {
  background: #667eea;
  border-color: #667eea;
  color: white;
  cursor: default;
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-dots {
  min-width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #a0aec0;
  font-size: 1rem;
}

/* Адаптивность */
@media (max-width: 768px) {
  .pagination {
    gap: 0.25rem;
  }
  
  .pagination-btn {
    min-width: 32px;
    height: 32px;
    padding: 0 0.5rem;
    font-size: 0.75rem;
  }
  
  .pagination-dots {
    min-width: 32px;
    height: 32px;
  }
}

@media (max-width: 640px) {
  .pagination-btn:nth-child(1),
  .pagination-btn:nth-child(2),
  .pagination-btn:nth-child(3),
  .pagination-btn:nth-child(10),
  .pagination-btn:nth-child(11),
  .pagination-btn:nth-child(12) {
    display: none;
  }
}
</style>