<!-- src/views/admin/AdminLayout.vue -->
<template>
  <div class="admin-layout">
    <aside class="admin-sidebar">
      <div class="sidebar-header">
        <h2>Админ панель</h2>
      </div>
      <nav class="sidebar-nav">
        <router-link to="/admin" class="nav-link" exact-active-class="active">
          📊 Дашборд
        </router-link>
        <router-link to="/admin/categories" class="nav-link" active-class="active">
          📁 Категории
        </router-link>
      </nav>
      <div class="sidebar-footer">
        <button @click="goHome" class="home-btn">🏠 На главную</button>
      </div>
    </aside>
    
    <main class="admin-main">
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
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: #f5f5f5;
}

.admin-sidebar {
  width: 260px;
  background: white;
  box-shadow: 2px 0 4px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 1.5rem;
  border-bottom: 1px solid #e2e8f0;
}

.sidebar-header h2 {
  margin: 0;
  color: #333;
  font-size: 1.25rem;
}

.sidebar-nav {
  flex: 1;
  padding: 1rem 0;
}

.nav-link {
  display: block;
  padding: 0.75rem 1.5rem;
  color: #4a5568;
  text-decoration: none;
  transition: all 0.2s;
}

.nav-link:hover {
  background: #f7fafc;
  color: #667eea;
}

.nav-link.active {
  background: #ebf4ff;
  color: #667eea;
  border-right: 3px solid #667eea;
}

.sidebar-footer {
  padding: 1rem;
  border-top: 1px solid #e2e8f0;
}

.home-btn {
  width: 100%;
  padding: 0.5rem;
  background: #667eea;
  color: white;
  border: none;
  cursor: pointer;
}

.home-btn:hover {
  background: #5a67d8;
}

.admin-main {
  flex: 1;
  overflow-y: auto;
  padding: 1.5rem;
}
</style>