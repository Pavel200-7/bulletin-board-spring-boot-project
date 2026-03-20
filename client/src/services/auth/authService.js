// src/services/auth/authService.js
import apiClient from '@/utils/apiClient'
import { tokenManager } from './tokenManager'

// API Gateway URL (уже настроен в apiClient)
const AUTH_BASE_URL = '/api/auth'  // относительный путь, пойдет через apiClient

export const authService = {
  /**
   * Редирект на страницу входа Keycloak
   */
  redirectToLogin() {
    // Используем API Gateway
    const url = `${import.meta.env.VITE_API_URL}/api/auth/authorize`
    console.log('Redirecting to:', url)  
    window.location.href = `${import.meta.env.VITE_API_URL}/api/auth/authorize`
  },

  /**
   * Обработка callback после редиректа (парсинг токенов из URL)
   */
  handleCallback() {
    console.log('handleCallback called')  
    console.log('window.location.hash:', window.location.hash)  
    
    if (!window.location.hash) return false
    
    const hash = window.location.hash.substring(1) // убираем '#'
    const params = new URLSearchParams(hash)

    const accessToken = params.get('access_token')
    const refreshToken = params.get('refresh_token')
    
    console.log('accessToken found:', !!accessToken)

    if (accessToken) {
      tokenManager.setTokens(accessToken, refreshToken)
      // Очищаем URL от токенов
      window.history.replaceState({}, document.title, '/callback')
      return true
    }
    return false
  },

  /**
   * Обновление токена
   */
  async refreshToken() {
    const refreshToken = tokenManager.getRefreshToken()
    if (!refreshToken) throw new Error('No refresh token')

    const response = await apiClient.post(`${AUTH_BASE_URL}/refresh`, { refreshToken })
    const { accessToken, refreshToken: newRefreshToken } = response.data
    tokenManager.setTokens(accessToken, newRefreshToken)
    return response.data
  },

  /**
   * Выход
   */
  async logout() {
    const refreshToken = tokenManager.getRefreshToken()
    if (refreshToken) {
      try {
        await apiClient.post(`${AUTH_BASE_URL}/logout`, { refreshToken })
      } catch (e) {
        console.error('Logout error:', e)
      }
    }
    tokenManager.clearTokens()
    sessionStorage.removeItem('anonymous')
    
    // Редирект на главную
    window.location.href = '/'
  },

  /**
   * Анонимный вход
   */
  anonymousLogin() {
    sessionStorage.setItem('anonymous', 'true')
  },

  isAnonymous() {
    return sessionStorage.getItem('anonymous') === 'true'
  },

  /**
   * Проверить авторизацию
   */
  isAuthenticated() {
    return tokenManager.isAuthenticated()
  }
}