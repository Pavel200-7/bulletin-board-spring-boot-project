// src/services/tradeAccount/tradeAccountService.js
import apiClient from '@/utils/apiClient'

/**
 * Сервис для работы с торговым аккаунтом
 */
export const tradeAccountService = {
  // ========== ПОЛУЧЕНИЕ ==========

  /**
   * Получить данные торгового аккаунта по ID
   * @param {string} id - UUID торгового аккаунта
   */
  getTradeAccount(id) {
    return apiClient.get(`/trade-account/${id}`)
  },

  /**
   * Получить данные торгового аккаунта по ID пользователя
   * @param {string} userId - UUID пользователя
   */
  getTradeAccountByUserId(userId) {
    return apiClient.get(`/trade-account/by-user/${userId}`)
  },

  getMyTradeAccount() {
    return apiClient.get('/trade-account/my')
  },

  // ========== ОСНОВНЫЕ ДАННЫЕ ==========

  /**
   * Изменить название торговой точки
   * @param {string} name - новое название
   */
  changeName(name) {
    return apiClient.put('/trade-account/name', { name })
  },

  /**
   * Изменить номер телефона
   * @param {string} phone - новый номер телефона
   */
  changePhone(phone) {
    return apiClient.put('/trade-account/phone', { phone })
  },

  /**
   * Изменить контактные данные
   * @param {string} contacts - новые контактные данные
   */
  changeContacts(contacts) {
    return apiClient.put('/trade-account/contacts', { contacts })
  },

  /**
   * Изменить описание
   * @param {string} description - новое описание
   */
  changeDescription(description) {
    return apiClient.put('/trade-account/description', { description })
  },

  /**
   * Изменить описание
   * @param {string} imageId - Id (UUID) Изображения
   */
  changeImage(imageId) {
    return apiClient.put('/trade-account/image', { imageId })
  },

  // ========== ЛОКАЦИЯ ==========

  /**
   * Установить примерное местоположение (только город/координаты)
   * @param {Object} location - данные локации
   * @param {number} location.latitude - широта
   * @param {number} location.longitude - долгота
   * @param {string} location.townName - название города
   */
  setApproximateLocation({ latitude, longitude, townName }) {
    return apiClient.put('/trade-account/approximate-location', {
      latitude,
      longitude,
      townName
    })
  },

  /**
   * Установить точное местоположение (с конкретным адресом)
   * @param {Object} location - данные локации
   * @param {number} location.latitude - широта
   * @param {number} location.longitude - долгота
   * @param {string} location.townName - название города
   * @param {string} location.locationName - точное название места/адрес
   */
  setExactLocation({ latitude, longitude, townName, locationName }) {
    return apiClient.put('/trade-account/exact-location', {
      latitude,
      longitude,
      townName,
      locationName
    })
  },

  // ========== ВЕРИФИКАЦИЯ ==========

  /**
   * Подтвердить торговый аккаунт (после заполнения всех обязательных полей)
   * Авторизация определяет, какой аккаунт подтверждается
   */
  approveAccount() {
    return apiClient.put('/trade-account/approve')
  }
}