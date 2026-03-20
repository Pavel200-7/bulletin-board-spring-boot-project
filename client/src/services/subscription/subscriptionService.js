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
   * Создать новую подписку
   * @param {Object} data - данные подписки
   * @param {string} data.subscriptionType - тип уведомления (BULLETIN_PUBLISHED, TEST_USER_NOTIFICATION, TEST_SYSTEM_NOTIFICATION)
   * @param {string} [data.publisherId] - ID издателя (если нужен, например ID пользователя)
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