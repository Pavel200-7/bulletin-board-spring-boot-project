// src/services/chat/imageMessageService.js
import api from '@/services/api'

class ImageMessageService {
  /**
   * Создать image сообщение через REST API
   * @param {string} chatId - ID чата
   * @param {string} imageId - ID изображения
   * @returns {Promise} - ответ с данными созданного сообщения
   */
  createImageMessage(chatId, imageId) {
    return api.post(`/api/v1/chat/${chatId}/messages/image`, { imageId })
  }

  /**
   * Удалить image сообщение через REST API
   * @param {string} chatId - ID чата
   * @param {string} messageId - ID сообщения
   * @returns {Promise} - ответ об успешном удалении
   */
  deleteImageMessage(chatId, messageId) {
    return api.delete(`/api/v1/chat/${chatId}/messages/image/${messageId}`)
  }
}

export const imageMessageService = new ImageMessageService()