<!-- src/views/bulletin/components/view/Breadcrumbs.vue -->
<template>
  <div class="breadcrumbs">
    <router-link to="/" class="breadcrumb-link">Главная</router-link>
    <span class="separator">/</span>
    <router-link to="/home" class="breadcrumb-link">Объявления</router-link>
    <span class="separator">/</span>
    <span class="breadcrumb-current">{{ title || 'Объявление' }}</span>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    default: 'Объявление'
  }
})
</script>

<style scoped>
.breadcrumbs {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
  margin-bottom: 1.5rem;
  color: #6c757d;
}

.breadcrumb-link {
  color: #6c757d;
  text-decoration: none;
  transition: color 0.2s;
}

.breadcrumb-link:hover {
  color: #667eea;
}

.separator {
  color: #dee2e6;
}

.breadcrumb-current {
  color: #212529;
  font-weight: 500;
}
</style>