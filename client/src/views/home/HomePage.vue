<!-- src/views/HomePage.vue -->
<template>
  <div class="home-container">
    <AppHeader />
    
    <main class="home-content">
      <div class="hero-section">
        <h1 class="hero-title">Найдите то, что ищете</h1>
        <p class="hero-subtitle">Тысячи объявлений от проверенных продавцов</p>
        
        <SearchForm />
      </div>
    </main>
  </div>
</template>

<script setup>
import AppHeader from '@/components/layout/AppHeader.vue'
import SearchForm from './components/SearchForm.vue'
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f8f9fa;
}

.home-content {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}

.hero-section {
  text-align: center;
  padding: 2rem 1rem;
}

.hero-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 1rem;
}

.hero-subtitle {
  font-size: 1.125rem;
  color: #718096;
  margin-bottom: 2rem;
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 1.75rem;
  }
  
  .hero-subtitle {
    font-size: 1rem;
  }
}
</style>