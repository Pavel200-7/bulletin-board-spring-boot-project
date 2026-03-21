// src/composables/useAuth.js
import { ref, computed } from 'vue'
import { authService } from '@/services/auth/authService'
import { tokenManager } from '@/services/auth/tokenManager'

export function useAuth() {
  const isAuthenticated = ref(tokenManager.isAuthenticated())
  const isAnonymous = ref(authService.isAnonymous())
  
  const hasSession = computed(() => isAuthenticated.value || isAnonymous.value)
  const isAdmin = computed(() => authService.isAdmin())

  const login = () => {
    authService.redirectToLogin()
  }

  const handleCallback = () => {
    const success = authService.handleCallback()
    if (success) {
      isAuthenticated.value = true
    }
    return success
  }

  const logout = async () => {
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
      return handleCallback()
    }
    return false
  }

  const getUserInfo = () => {
    return authService.getUserInfo()
  }

  const getUserRoles = () => {
    return authService.getUserRoles()
  }

  return {
    // state
    isAuthenticated,
    isAnonymous,
    hasSession,
    isAdmin,  // ← теперь здесь

    // actions
    login,
    handleCallback,
    initAuth,
    logout,
    anonymousLogin,
    refreshToken,
    getUserInfo,
    getUserRoles
  }
}