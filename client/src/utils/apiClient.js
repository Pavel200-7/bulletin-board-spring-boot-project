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

// Перехватчик для обновления токена
apiClient.interceptors.response.use(
  response => response,
  async error => {
    const originalRequest = error.config
    
    // Если ошибка 401 и это не повторный запрос
    if (error.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        // Если уже идет обновление токена, добавляем запрос в очередь
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject })
        })
          .then(token => {
            originalRequest.headers.Authorization = `Bearer ${token}`
            return apiClient(originalRequest)
          })
          .catch(err => Promise.reject(err))
      }

      originalRequest._retry = true
      isRefreshing = true

      try {
        const refreshToken = tokenManager.getRefreshToken()
        
        if (!refreshToken) {
          // Нет refresh токена — перенаправляем на логин
          tokenManager.clearTokens()
          window.location.href = '/'
          return Promise.reject(error)
        }

        // Пытаемся обновить токен
        await authService.refreshToken()
        const newToken = tokenManager.getAccessToken()
        
        // Обрабатываем очередь запросов
        processQueue(null, newToken)
        
        // Повторяем исходный запрос
        originalRequest.headers.Authorization = `Bearer ${newToken}`
        return apiClient(originalRequest)
      } catch (refreshError) {
        // Обновление не удалось — очищаем токены и перенаправляем на логин
        processQueue(refreshError, null)
        tokenManager.clearTokens()
        window.location.href = '/'
        return Promise.reject(refreshError)
      } finally {
        isRefreshing = false
      }
    }
    
    return Promise.reject(error)
  }
)

export default apiClient