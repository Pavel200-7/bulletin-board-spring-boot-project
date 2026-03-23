<!-- src/views/trade/TradeProfileLayout.vue -->
<template>
  <div class="trade-profile-layout">
    <header class="trade-header">
      <h1>Торговый профиль</h1>
      <button @click="goHome" class="home-btn">🏠 На главную</button>
    </header>
    
    <nav class="trade-nav">
      <router-link 
        to="/trade/settings" 
        class="nav-tab"
        active-class="active"
      >
        ⚙️ Настройки
      </router-link>
      <router-link 
        to="/trade/drafts" 
        class="nav-tab"
        active-class="active"
      >
        📝 Черновики
      </router-link>
      <router-link 
        to="/trade/published" 
        class="nav-tab"
        active-class="active"
      >
        📢 Действующие
      </router-link>
      <router-link 
        to="/trade/closed" 
        class="nav-tab"
        active-class="active"
      >
        🔒 Закрытые
      </router-link>
    </nav>
    
    <main class="trade-content">
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
.trade-profile-layout {
  min-height: 100vh;
  background: #f5f5f5;
}

.trade-header {
  background: white;
  padding: 1rem 2rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.trade-header h1 {
  margin: 0;
  color: #333;
  font-size: 1.5rem;
}

.home-btn {
  padding: 0.5rem 1rem;
  background: #667eea;
  color: white;
  border: none;
  cursor: pointer;
}

.home-btn:hover {
  background: #5a67d8;
}

.trade-nav {
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

.trade-content {
  padding: 2rem;
}
</style>