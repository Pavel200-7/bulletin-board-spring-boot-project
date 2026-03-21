<!-- src/components/layout/AppHeader.vue -->
<template>
  <header class="app-header">
    <div class="header-left">
      <h1>Главная страница</h1>
    </div>
    <div class="header-right">
      <!-- Бейдж статуса -->
      <AdminBadge v-if="isAdmin" />
      <AuthBadge v-else-if="isAuthenticated" />
      <AnonymousBadge v-else-if="isAnonymous" />
      
      <!-- Кнопки действий -->
      <AdminButton v-if="isAdmin" @click="goToAdmin" />
      <LogoutButton v-if="isAuthenticated" @click="handleLogout" />
      <LoginButton v-if="isAnonymous" @click="handleLogin" />
    </div>
  </header>
</template>

<script setup>
import { useAuth } from '@/composables/useAuth'
import { useRouter } from 'vue-router'
import AdminBadge from '@/components/badges/AdminBadge.vue'
import AuthBadge from '@/components/badges/AuthBadge.vue'
import AnonymousBadge from '@/components/badges/AnonymousBadge.vue'
import AdminButton from '@/components/buttons/AdminButton.vue'
import LogoutButton from '@/components/buttons/LogoutButton.vue'
import LoginButton from '@/components/buttons/LoginButton.vue'

const { isAuthenticated, isAnonymous, isAdmin, logout, login } = useAuth()
const router = useRouter()

const handleLogout = async () => {
  console.log('Logout clicked')
  await logout()
  router.push('/')
}

const handleLogin = async () => {
  console.log('Logout clicked')
  await logout()
  router.push('/')
}

const goToAdmin = () => {
  console.log('Admin button clicked')
  router.push('/admin')
}
</script>

<style scoped>
.app-header {
  background: white;
  padding: 1rem 2rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left h1 {
  margin: 0;
  color: #333;
  font-size: 1.5rem;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 1rem;
}
</style>