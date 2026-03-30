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
  let isRefreshing = false // Флаг для предотвращения одновременных обновлений

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
    isRefreshing = false
  }

  const anonymousLogin = () => {
    authService.anonymousLogin()
    isAnonymous.value = true
  }

  const refreshToken = async () => {
    // Предотвращаем одновременные обновления
    if (isRefreshing) {
      console.log('Refresh already in progress, skipping...')
      return false
    }
    
    isRefreshing = true
    try {
      await authService.refreshToken()
      isAuthenticated.value = true
      console.log('Token refreshed successfully')
      return true
    } catch (e) {
      console.error('Failed to refresh token:', e)
      isAuthenticated.value = false
      return false
    } finally {
      isRefreshing = false
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
    // Останавливаем существующий таймер
    stopTokenRefreshTimer()
    
    const expirationTime = tokenManager.getTokenExpirationTime()
    if (!expirationTime) {
      console.log('No token expiration time found')
      return
    }
    
    const now = Date.now()
    const timeUntilExpiry = expirationTime - now
    
    // Если токен уже истек — сразу обновляем
    if (timeUntilExpiry <= 0) {
      console.log('Token already expired, refreshing...')
      refreshToken().catch(console.error)
      return
    }
    
    // Обновляем за 5 минут до истечения
    const refreshTime = Math.max(timeUntilExpiry - 5 * 60 * 1000, 1000) // Минимум 1 секунда
    
    console.log(`Token expires in ${Math.floor(timeUntilExpiry / 1000)} sec. Will refresh in ${Math.floor(refreshTime / 1000)} sec.`)
    
    refreshTimeout = setTimeout(async () => {
      console.log('Time to refresh token...')
      try {
        const success = await refreshToken()
        if (success) {
          // После успешного обновления запускаем новый таймер
          startTokenRefreshTimer()
        } else {
          console.error('Failed to refresh token, logging out...')
          // await logout()
        }
      } catch (err) {
        console.error('Error during token refresh:', err)
        // await logout()
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

  const getUserId = () => {
    return authService.getUserId()
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
    getUserId,
    startTokenRefreshTimer,
    stopTokenRefreshTimer
  }
}