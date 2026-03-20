// src/utils/apiClient.js (дополнение)
import axios from 'axios'
import { tokenManager } from '@/services/auth/tokenManager'

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL + '/api/v1',
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

// Обрабатываем ошибки 401 (неавторизован)
apiClient.interceptors.response.use(
  response => response,
  async error => {
    const originalRequest = error.config
    
    // Если ошибка 401 и это не повторный запрос
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true
      
      // Здесь должна быть логика обновления токена через refresh token
      // Например, запрос к вашему бэкенду для обновления
      
      // Если обновить не удалось — чистим токены и редиректим на логин
      tokenManager.clearTokens()
      window.location.href = '/'
    }
    
    return Promise.reject(error)
  }
)

export default apiClient