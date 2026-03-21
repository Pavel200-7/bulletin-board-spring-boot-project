<!-- src/views/HomePage.vue -->
<template>
  <div class="home-container">
    <AppHeader />
    
    <main class="home-content">
      <div class="welcome-card">
        <p class="welcome-text">Добро пожаловать!</p>
        <p class="status-text">
          Вы вошли как:
          <span v-if="isAdmin" class="status-value admin">Администратор</span>
          <span v-else-if="isAuthenticated" class="status-value auth">Авторизованный пользователь</span>
          <span v-else-if="isAnonymous" class="status-value anon">Анонимный пользователь</span>
        </p>
      </div>
    </main>
  </div>
</template>

<script setup>
import { useAuth } from '@/composables/useAuth'
import AppHeader from '@/components/layout/AppHeader.vue'

const { isAuthenticated, isAnonymous, isAdmin } = useAuth()
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

.home-content {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 2rem;
}

.welcome-card {
  background: white;
  padding: 2rem 3rem;
  text-align: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  max-width: 500px;
  width: 100%;
}

.welcome-text {
  font-size: 1.5rem;
  color: #333;
  margin-bottom: 1rem;
}

.status-text {
  font-size: 1rem;
  color: #666;
  margin-bottom: 0;
}

.status-value {
  display: inline-block;
  margin-left: 0.5rem;
  font-weight: 500;
}

.status-value.admin {
  color: #b45309;
}

.status-value.auth {
  color: #38a169;
}

.status-value.anon {
  color: #718096;
}
</style>