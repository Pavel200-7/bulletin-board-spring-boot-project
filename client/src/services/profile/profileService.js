// src/services/profile/profileService.js
import apiClient from '@/utils/apiClient'

/**
 * Сервис для работы с профилями пользователей
 */
export const profileService = {
  // ========== ПОЛУЧЕНИЕ ==========

  /**
   * Получить профиль по ID профиля
   * @param {string} id - UUID профиля
   */
  getProfile(id) {
    return apiClient.get(`/profile/${id}`)
  },

  /**
   * Получить профиль по ID пользователя
   * @param {string} userId - UUID пользователя
   */
  getProfileByUserId(userId) {
    return apiClient.get(`/profile/by-user/${userId}`)
  },

  /**
   * Получить профиль текущего пользователя
   */
  getMyProfile() {
    return apiClient.get('/profile/my')
  },

  /**
   * Получить страницу профилей с пагинацией и поиском
   * @param {Object} params - параметры пагинации и поиска
   * @param {number} params.page - номер страницы (0-based)
   * @param {number} params.size - размер страницы
   * @param {Object} params.criteria - критерии поиска
   * @param {string} params.criteria.publicName - поиск по имени
   * @param {string} params.criteria.orderBy - поле сортировки (PUBLIC_NAME, CREATED_AT, UPDATED_AT)
   * @param {string} params.criteria.direction - направление сортировки (ASC, DESC)
   */
  searchProfiles({ page = 0, size = 20, criteria = {} }) {
    const request = {
      pageData: { page, size },
      criteria
    }
    const res = apiClient.post('/profile/search', request)
    console.log(res)
    return res
  },

  /**
   * Проверить существование профиля текущего пользователя
   * @returns {Promise<{exists: boolean, profileResponse: Object|null}>}
   */
  existsMyProfile() {
    return apiClient.get('/profile/exists/my')
  },

  // ========== СОЗДАНИЕ ==========

  /**
   * Создать профиль
   * @param {Object} data - данные профиля
   * @param {string} data.ownerId - ID пользователя
   * @param {string} data.ownerName - имя пользователя (опционально)
   */
  createProfile({ ownerId, ownerName }) {
    return apiClient.post('/profile', { ownerId, ownerName })
  },

  // ========== ИЗМЕНЕНИЕ ==========

  /**
   * Изменить публичное имя профиля
   * @param {string} name - новое публичное имя
   */
  changePublicName(name) {
    return apiClient.put('/profile/public-name', { name })
  },

  /**
   * Изменить описание профиля
   * @param {string} description - новое описание
   */
  changeDescription(description) {
    return apiClient.put('/profile/description', { description })
  },

  /**
   * Изменить изображение профиля
   * @param {string} imageId - ID изображения в MinIO
   */
  changeImage(imageId) {
    return apiClient.put('/profile/image', { imageId })
  }
}