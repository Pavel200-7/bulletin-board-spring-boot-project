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
    window.location.href = `${import.meta.env.VITE_API_URL}/api/auth/authorize`
  },

  /**
   * Обработка callback после редиректа (парсинг токенов из URL)
   */
  handleCallback() {    
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
  },
  getToken() {
    return tokenManager.getAccessToken()
  },

  /**
   * Проверить, является ли пользователь администратором
   */
  isAdmin() {
    const token = this.getToken()
    if (!token) return false
    
    try {
      const payload = JSON.parse(atob(token.split('.')[1]))
      const roles = payload.spring_sec_roles || payload.realm_access?.roles || []
      return roles.includes('admin') || roles.includes('ROLE_admin') || roles.includes('ADMIN')
    } catch (e) {
      console.error('Failed to parse token for admin check:', e)
      return false
    }
  },

  /**
   * Получить роли пользователя из токена
   */
  getUserRoles() {
    const token = this.getToken()
    if (!token) return []
    
    try {
      const payload = JSON.parse(atob(token.split('.')[1]))
      return payload.spring_sec_roles || payload.realm_access?.roles || []
    } catch (e) {
      console.error('Failed to parse token for roles:', e)
      return []
    }
  },

  /**
   * Получить информацию о пользователе из токена
   */
  getUserInfo() {
    const token = this.getToken()
    if (!token) return null
    
    try {
      const payload = JSON.parse(atob(token.split('.')[1]))
      return {
        userId: payload.sub,
        username: payload.preferred_username,
        email: payload.email,
        name: payload.name,
        roles: this.getUserRoles()
      }
    } catch (e) {
      console.error('Failed to parse token for user info:', e)
      return null
    }
  }
}