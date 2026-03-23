<!-- src/views/bulletin/BulletinEditPage.vue -->
<template>
  <div class="bulletin-edit-page">
    <div class="page-header">
      <button @click="goBack" class="back-btn">← Назад</button>
      <h1>Редактирование объявления</h1>
    </div>
    
    <div class="edit-content">
      <p class="placeholder">Страница редактирования объявления в разработке</p>
      <p>ID объявления: {{ bulletinId }}</p>
    </div>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const bulletinId = route.params.id

const goBack = () => {
  router.push('/trade/drafts')
}
</script>

<style scoped>
.bulletin-edit-page {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.back-btn {
  padding: 0.5rem 1rem;
  background: #e2e8f0;
  border: none;
  cursor: pointer;
}

.back-btn:hover {
  background: #cbd5e0;
}

.edit-content {
  text-align: center;
  padding: 3rem;
}

.placeholder {
  color: #a0aec0;
}
</style>