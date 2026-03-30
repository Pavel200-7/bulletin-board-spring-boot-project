<template>
  <div class="chat-layout">
    <header class="chat-header">
      <h1>Сообщения</h1>
      <button @click="goHome" class="home-btn">🏠 На главную</button>
    </header>
    
    <nav class="chat-nav">
      <router-link 
        to="/chat/settings" 
        class="nav-tab"
        active-class="active"
      >
        ⚙️ Настройки
      </router-link>
      <router-link 
        to="/chat/contacts" 
        class="nav-tab"
        active-class="active"
      >
        👥 Контакты
      </router-link>
      <router-link 
        to="/chat/search" 
        class="nav-tab"
        active-class="active"
      >
        🔍 Поиск
      </router-link>
    </nav>
    
    <main class="chat-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/home')
}
</script>

<style scoped>
.chat-layout {
  min-height: 100vh;
  background: #f5f5f5;
}

.chat-header {
  background: white;
  padding: 1rem 2rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-header h1 {
  margin: 0;
  color: #333;
  font-size: 1.5rem;
}

.home-btn {
  padding: 0.5rem 1rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.home-btn:hover {
  background: #5a67d8;
  transform: translateY(-1px);
}

.chat-nav {
  display: flex;
  gap: 0.5rem;
  padding: 1rem 2rem;
  background: white;
  border-bottom: 1px solid #e2e8f0;
}

.nav-tab {
  padding: 0.5rem 1rem;
  color: #4a5568;
  text-decoration: none;
  border-radius: 8px;
  transition: all 0.2s;
}

.nav-tab:hover {
  background: #f7fafc;
  color: #667eea;
}

.nav-tab.active {
  background: #667eea;
  color: white;
}

.chat-content {
  padding: 2rem;
}
</style>