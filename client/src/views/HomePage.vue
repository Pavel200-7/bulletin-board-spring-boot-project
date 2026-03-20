<!-- src/views/HomePage.vue -->
<template>
  <div class="home-container">
    <header class="home-header">
      <h1>Главная страница</h1>
      <button @click="handleLogout" class="logout-btn">Выйти</button>
    </header>
    <main class="home-content">
      <p>Добро пожаловать!</p>
      <p>Статус: 
        <span v-if="isAuthenticated">✅ Авторизован</span>
        <span v-else-if="isAnonymous">👤 Анонимный пользователь</span>
      </p>
    </main>
  </div>
</template>

<script setup>
import { useAuth } from '@/composables/useAuth'
import { useRouter } from 'vue-router'

const { isAuthenticated, isAnonymous, logout } = useAuth()
const router = useRouter()

const handleLogout = async () => {
  await logout()
  router.push('/')
}
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background: #f5f5f5;
}

.home-header {
  background: white;
  padding: 1rem 2rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.home-header h1 {
  margin: 0;
  color: #333;
}

.logout-btn {
  padding: 0.5rem 1rem;
  background: #f56565;
  color: white;
  border: none;
  border-radius: 0.25rem;
  cursor: pointer;
}

.logout-btn:hover {
  background: #c53030;
}

.home-content {
  padding: 2rem;
  text-align: center;
}
</style>