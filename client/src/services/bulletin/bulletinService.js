// src/services/bulletin/bulletinService.js
import apiClient from '@/utils/apiClient'

/**
 * Сервис для работы с объявлениями
 */
export const bulletinService = {
  // ========== ПОЛУЧЕНИЕ ==========
  
  /**
   * Получить публичное объявление по ID (доступно всем)
   */
  getPublicBulletin(id) {
    return apiClient.get(`/bulletin/${id}`)
  },

  /**
   * Получить объявление для редактирования (только для владельца)
   */
  getEditableBulletin(id) {
    return apiClient.get(`/bulletin/modifiable/${id}`)
  },

  /**
   * Получить страницу опубликованных объявлений (для главной)
   */
  getPublishedBulletins({ page = 0, size = 20, criteria = {} } = {}) {
    const request = {
      pageData: { page, size },
      criteria
    }
    return apiClient.post('/bulletin/page', request)
  },

  // ========== СОЗДАНИЕ И РЕДАКТИРОВАНИЕ ==========

  /**
   * Создать черновик объявления
   */
  createDraft() {
    return apiClient.post('/bulletin')
  },

  /**
   * Обновить существующее объявление
   */
  updateBulletin(bulletinRequest) {
    return apiClient.put('/bulletin', { bulletinRequest })
  },

  // ========== ИЗМЕНЕНИЕ СТАТУСА ==========

  /**
   * Проверить объявление перед публикацией
   */
  approveAndPublish(bulletinId) {
    return apiClient.put('/bulletin/approve', { bulletinId })
  },

  /**
   * Опубликовать объявление (если уже прошло проверку)
   */
  publishBulletin(bulletinId) {
    return apiClient.put('/bulletin/publish', { bulletinId })
  },

  /**
   * Закрыть объявление
   */
  closeBulletin(bulletinId) {
    return apiClient.put('/bulletin/close', { bulletinId })
  },

  // ========== РАБОТА С ИЗОБРАЖЕНИЯМИ ==========

  /**
   * Добавить изображение к объявлению
   */
  addImage(bulletinId, providerImageId) {
    return apiClient.put('/bulletin/add-image', { bulletinId, providerImageId })
  },

  /**
   * Удалить изображение из объявления
   */
  removeImage(bulletinId, imageId) {
    return apiClient.put('/bulletin/remove-image', { bulletinId, imageId })
  },

  /**
   * Установить главное изображение
   */
  setMainImage(bulletinId, imageId) {
    return apiClient.put('/bulletin/main-image', { bulletinId, imageId })
  }
}