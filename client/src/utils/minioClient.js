// src/utils/minioClient.js
import axios from 'axios'
import { tokenManager } from '@/services/auth/tokenManager'

const MINIO_URL = import.meta.env.VITE_MINIO_URL || 'http://localhost:9001'
const BUCKET = import.meta.env.VITE_MINIO_BUCKET || 'bulletins'

const minioClient = axios.create({
  baseURL: `${MINIO_URL}/${BUCKET}`,
  timeout: 60000 // 60 секунд для загрузки файлов
})

// Добавляем токен к каждому запросу (если нужна авторизация)
minioClient.interceptors.request.use(config => {
  // console.log(111)
  const token = tokenManager.getAccessToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
    console.log('MinIO Request:', config.method, config.url, config.headers) // ← добавить

  return config
})

// Перехватчик для обработки ошибок (без обновления токена, так как MinIO не через Gateway)
minioClient.interceptors.response.use(
  response => response,
  error => {
    console.error('MinIO request error:', error.response?.status, error.message)
    return Promise.reject(error)
  }
)

export default minioClient