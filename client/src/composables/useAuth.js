// src/composables/useAuth.js
import { ref, computed, onUnmounted } from 'vue'
import { authService } from '@/services/auth/authService'
import { tokenManager } from '@/services/auth/tokenManager'

export function useAuth() {
  const isAuthenticated = ref(tokenManager.isAuthenticated())
  const isAnonymous = ref(authService.isAnonymous())
  const hasSession = computed(() => isAuthenticated.value || isAnonymous.value)
  const isAdmin = computed(() => authService.isAdmin())
  
  let refreshTimeout = null

  const login = () => {
    authService.redirectToLogin()
  }

  const handleCallback = () => {
    const success = authService.handleCallback()
    if (success) {
      isAuthenticated.value = true
      startTokenRefreshTimer()
    }
    return success
  }

  const logout = async () => {
    stopTokenRefreshTimer()
    await authService.logout()
    isAuthenticated.value = false
    isAnonymous.value = false
  }

  const anonymousLogin = () => {
    authService.anonymousLogin()
    isAnonymous.value = true
  }

  const refreshToken = async () => {
    try {
      await authService.refreshToken()
      isAuthenticated.value = true
      return true
    } catch (e) {
      isAuthenticated.value = false
      return false
    }
  }

  const initAuth = () => {
    isAuthenticated.value = tokenManager.isAuthenticated()
    if (window.location.hash.includes('access_token')) {
      const success = handleCallback()
      if (success) {
        startTokenRefreshTimer()
      }
      return success
    }
    
    if (isAuthenticated.value) {
      startTokenRefreshTimer()
    }
    
    return false
  }

  const startTokenRefreshTimer = () => {
    stopTokenRefreshTimer()
    
    const expirationTime = tokenManager.getTokenExpirationTime()
    if (!expirationTime) {
      console.log('Нет времени истечения токена')
      return
    }
    
    const now = Date.now()
    const timeUntilExpiry = expirationTime - now
    
    // Если токен уже истек — сразу обновляем
    if (timeUntilExpiry <= 0) {
      console.log('Токен уже истек, обновляем...')
      refreshToken().catch(console.error)
      return
    }
    
    // Обновляем за 5 минут до истечения (или сразу, если до истечения меньше 5 минут)
    const refreshTime = Math.max(timeUntilExpiry - 5 * 60 * 1000, 0)
    
    console.log(`Токен истекает через ${Math.floor(timeUntilExpiry / 1000)} сек. Обновим через ${Math.floor(refreshTime / 1000)} сек.`)
    
    refreshTimeout = setTimeout(async () => {
      console.log('Время обновить токен...')
      try {
        await refreshToken()
        // После успешного обновления запускаем новый таймер
        startTokenRefreshTimer()
      } catch (err) {
        console.error('Не удалось обновить токен:', err)
        // При ошибке обновления — выходим из системы
        await logout()
      }
    }, refreshTime)
  }

  const stopTokenRefreshTimer = () => {
    if (refreshTimeout) {
      clearTimeout(refreshTimeout)
      refreshTimeout = null
    }
  }

  const getUserInfo = () => {
    return authService.getUserInfo()
  }

  const getUserRoles = () => {
    return authService.getUserRoles()
  }

  onUnmounted(() => {
    stopTokenRefreshTimer()
  })

  return {
    isAuthenticated,
    isAnonymous,
    hasSession,
    isAdmin,
    login,
    handleCallback,
    initAuth,
    logout,
    anonymousLogin,
    refreshToken,
    getUserInfo,
    getUserRoles,
    startTokenRefreshTimer,
    stopTokenRefreshTimer
  }
}