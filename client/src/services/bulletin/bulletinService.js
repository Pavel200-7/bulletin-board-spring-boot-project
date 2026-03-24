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

   /**
   * Получить список объявлений текущего пользователя
   * @param {Object} params
   * @param {number} params.page - номер страницы (0-based)
   * @param {number} params.size - размер страницы
   * @param {string} params.state - состояние объявления (MODIFIABLE, APPROVED, PUBLISHED, COMPLETED)
   * @param {string} params.title - поиск по названию
   */
  getMyBulletins({ page = 0, size = 20, state = null, title = null } = {}) {
    const request = {
      pageData: { page, size },
      state,
      title
    }
    return apiClient.post('/bulletin/my', request)
  },

  /**
   * Получить черновики (MODIFIABLE) текущего пользователя
   */
  getMyDrafts(page = 0, size = 20) {
    return this.getMyBulletins({ page, size, state: BULLETIN_STATE.MODIFIABLE })
  },

  /**
   * Получить одобренные объявления текущего пользователя
   */
  getMyApproved(page = 0, size = 20) {
    return this.getMyBulletins({ page, size, state: BULLETIN_STATE.APPROVED })
  },

  /**
   * Получить опубликованные объявления текущего пользователя
   */
  getMyPublished(page = 0, size = 20) {
    return this.getMyBulletins({ page, size, state: BULLETIN_STATE.PUBLISHED })
  },

  /**
   * Получить завершенные объявления текущего пользователя
   */
  getMyCompleted(page = 0, size = 20) {
    return this.getMyBulletins({ page, size, state: BULLETIN_STATE.COMPLETED })
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
  console.log('bulletinService.updateBulletin called with:', bulletinRequest)
  return apiClient.put('/bulletin', bulletinRequest)
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