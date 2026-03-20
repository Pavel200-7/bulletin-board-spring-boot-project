// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import { tokenManager } from '@/services/auth/tokenManager'
import { authService } from '@/services/auth/authService'

const routes = [
  {
    path: '/',
    name: 'welcome',
    component: () => import('@/views/WelcomePage.vue'),
    meta: { requiresGuest: true }
  },
  {
    path: '/callback',
    name: 'callback',
    component: () => import('@/views/CallbackPage.vue')
  },
  {
    path: '/home',
    name: 'home',
    component: () => import('@/views/HomePage.vue'),
    meta: { requiresAuth: false }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const isAuthenticated = tokenManager.isAuthenticated()
  const isAnonymous = authService.isAnonymous()
  const hasSession = isAuthenticated || isAnonymous

  // Callback всегда доступен
  if (to.path === '/callback') {
    next()
    return
  }

  // Гостевые страницы (главная)
  if (to.meta.requiresGuest && hasSession) {
    next('/home')
    return
  }

  next()
})

export default router