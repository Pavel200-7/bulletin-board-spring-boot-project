// src/services/subscription/subscriptionService.js
import apiClient from '@/utils/apiClient'

/**
 * Сервис для работы с подписками на уведомления
 */
export const subscriptionService = {
  /**
   * Получить все подписки текущего пользователя
   */
  getMySubscriptions() {
    return apiClient.get('/subscription')
  },

  /**
   * Проверить существование подписки по критериям
   * @param {string} subscriptionType - тип уведомления
   * @param {string} publisherId - ID издателя
   * @returns {Promise<{exists: boolean, subscriptionResponse: object|null}>}
   */
  existsByCriteria(subscriptionType, publisherId) {
    return apiClient.get(`/subscription/exists/${subscriptionType}/${publisherId}`)
  },

  /**
   * Создать новую подписку
   * @param {Object} data - данные подписки
   * @param {string} data.subscriptionType - тип уведомления
   * @param {string} [data.publisherId] - ID издателя
   */
  createSubscription({ subscriptionType, publisherId }) {
    const request = { subscriptionType }
    if (publisherId) {
      request.publisherId = publisherId
    }
    return apiClient.post('/subscription', request)
  },

  /**
   * Удалить подписку по ID
   * @param {string} id - ID подписки
   */
  deleteSubscription(id) {
    return apiClient.delete(`/subscription/${id}`)
  }
}