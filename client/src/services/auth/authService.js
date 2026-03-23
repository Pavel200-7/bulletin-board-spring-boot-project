// src/services/auth/authService.js
import apiClient from '@/utils/apiClient'
import { tokenManager } from './tokenManager'

const API_GATEWAY_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

export const authService = {
  redirectToLogin() {
    const url = `${API_GATEWAY_URL}/api/v1/auth/authorize`
    console.log('Redirecting to login:', url)
    window.location.href = url
  },

  handleCallback() {    
    if (!window.location.hash) return false
    
    const hash = window.location.hash.substring(1)
    const params = new URLSearchParams(hash)

    const accessToken = params.get('access_token')
    const refreshToken = params.get('refresh_token')
    
    console.log('accessToken found:', !!accessToken)

    if (accessToken) {
      tokenManager.setTokens(accessToken, refreshToken)
      window.history.replaceState({}, document.title, '/callback')
      return true
    }
    return false
  },

  async refreshToken() {
    const refreshToken = tokenManager.getRefreshToken()
    if (!refreshToken) {
      console.error('No refresh token available')
      throw new Error('No refresh token')
    }

    console.log('Attempting to refresh token...')
    console.log('API Base URL:', apiClient.defaults.baseURL)
    
    try {
      // Проверяем, какой URL формируется
      const url = '/auth/refresh'
      console.log('Request URL:', apiClient.defaults.baseURL + url)
      
      const response = await apiClient.post(url, { refreshToken })
      console.log(response)

      console.log('Refresh response:', response.data)
      
      const { accessToken, refreshToken: newRefreshToken } = response.data
      tokenManager.setTokens(accessToken, newRefreshToken)
      console.log('Token refreshed successfully')
      return response.data
    } catch (error) {
      console.error('Refresh token error details:', {
        message: error.message,
        code: error.code,
        status: error.response?.status,
        statusText: error.response?.statusText,
        data: error.response?.data,
        config: {
          url: error.config?.url,
          method: error.config?.method,
          baseURL: error.config?.baseURL,
          headers: error.config?.headers
        }
      })
      throw error
    }
  },

  async logout() {
    const refreshToken = tokenManager.getRefreshToken()
    if (refreshToken) {
      try {
        await apiClient.post('/auth/logout', { refreshToken })
        console.log('Logout successful')
      } catch (e) {
        console.error('Logout error:', e)
      }
    }
    tokenManager.clearTokens()
    sessionStorage.removeItem('anonymous')
    
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