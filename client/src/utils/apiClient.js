// src/utils/apiClient.js
import axios from 'axios'
import { tokenManager } from '@/services/auth/tokenManager'
import { authService } from '@/services/auth/authService'

let isRefreshing = false
let failedQueue = []

const processQueue = (error, token = null) => {
  failedQueue.forEach(prom => {
    if (error) {
      prom.reject(error)
    } else {
      prom.resolve(token)
    }
  })
  failedQueue = []
}

const apiClient = axios.create({
  baseURL: (import.meta.env.VITE_API_URL || 'http://localhost:8080') + '/api/v1',
  timeout: 10000
})

// Добавляем токен к каждому запросу
apiClient.interceptors.request.use(config => {
  const token = tokenManager.getAccessToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

const redirectToLogin = () => {
  // Очищаем все токены и сессию
  tokenManager.clearTokens()
  sessionStorage.removeItem('anonymous')
  
  // Проверяем, не находимся ли уже на странице входа
  if (!window.location.pathname.includes('/callback') && 
      window.location.pathname !== '/' && 
      window.location.pathname !== '/home') {
    // Сохраняем текущий URL для возврата после входа (опционально)
    sessionStorage.setItem('redirectAfterLogin', window.location.pathname + window.location.search)
    window.location.href = '/'
  }
}

// Перехватчик для обновления токена
apiClient.interceptors.response.use(
  response => response,
  async error => {
    const originalRequest = error.config
    
    // Проверяем статус 401
    if (error.response?.status === 401) {
      console.warn('Received 401 Unauthorized for:', originalRequest.url)
      
      // Если это запрос на обновление токена
      if (originalRequest.url.includes('/auth/refresh')) {
        console.warn('Refresh token request failed, redirecting to login...')
        redirectToLogin()
        return Promise.reject(error)
      }
      
      // Если это не запрос на обновление токена, пробуем обновить
      if (!originalRequest._retry) {
        originalRequest._retry = true
        
        if (isRefreshing) {
          // Если уже идет обновление, добавляем запрос в очередь
          return new Promise((resolve, reject) => {
            failedQueue.push({ resolve, reject })
          })
            .then(token => {
              originalRequest.headers.Authorization = `Bearer ${token}`
              return apiClient(originalRequest)
            })
            .catch(err => Promise.reject(err))
        }

        isRefreshing = true

        try {
          const refreshToken = tokenManager.getRefreshToken()
          
          if (!refreshToken) {
            console.warn('No refresh token available, redirecting to login...')
            redirectToLogin()
            return Promise.reject(error)
          }

          // Пытаемся обновить токен
          console.log('Attempting to refresh token...')
          await authService.refreshToken()
          const newToken = tokenManager.getAccessToken()
          
          console.log('Token refreshed successfully')
          
          // Обрабатываем очередь запросов
          processQueue(null, newToken)
          
          // Повторяем исходный запрос
          originalRequest.headers.Authorization = `Bearer ${newToken}`
          return apiClient(originalRequest)
        } catch (refreshError) {
          // Обновление не удалось — перенаправляем на логин
          console.error('Token refresh failed:', refreshError)
          processQueue(refreshError, null)
          redirectToLogin()
          return Promise.reject(refreshError)
        } finally {
          isRefreshing = false
        }
      }
    }
    
    // Обработка других ошибок
    if (error.response?.status === 403) {
      console.warn('Received 403 Forbidden, redirecting to home...')
      window.location.href = '/home'
    }
    
    return Promise.reject(error)
  }
)

export default apiClient