<!-- src/views/WelcomePage.vue -->
<template>
  <div class="welcome-container">
    <div class="welcome-card">
      <h1 class="welcome-title">Добро пожаловать!</h1>
      
      <button @click="handleLogin" class="btn btn-primary">
        Войти через Keycloak
      </button>
      
      <button @click="handleAnonymous" class="btn btn-outline">
        Продолжить анонимно
      </button>
    </div>
  </div>
</template>

<script setup>
import { useAuth } from '@/composables/useAuth'
import { useRouter } from 'vue-router'

const { login, anonymousLogin } = useAuth()
const router = useRouter()

const handleLogin = () => {
  login()
}

const handleAnonymous = () => {
  anonymousLogin()
  router.push('/home')
}
</script>

<style scoped>
.welcome-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-family: sans-serif;
}

.welcome-card {
  background: white;
  padding: 2.5rem;
  border-radius: 1rem;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  text-align: center;
  max-width: 400px;
  width: 90%;
}

.welcome-title {
  font-size: 2rem;
  color: #333;
  margin-bottom: 2rem;
}

.btn {
  display: block;
  width: 100%;
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 0.5rem;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  margin-bottom: 1rem;
}

.btn:last-child {
  margin-bottom: 0;
}

.btn-primary {
  background: #667eea;
  color: white;
}

.btn-outline {
  background: transparent;
  border: 2px solid #667eea;
  color: #667eea;
}

.btn-outline:hover {
  background: #667eea;
  color: white;
}
</style>