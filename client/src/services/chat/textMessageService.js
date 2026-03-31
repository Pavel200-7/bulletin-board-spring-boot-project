// src/services/chat/textMessageService.js
import apiClient from '@/utils/apiClient'

/**
 * Сервис для работы с текстовыми сообщениями
 */
export const textMessageService = {
  /**
   * Создать текстовое сообщение
   * @param {string} chatId - ID чата
   * @param {string} text - текст сообщения
   */
  createTextMessage(chatId, text) {
    return apiClient.post(`/chat/${chatId}/messages/text`, { chatId, text })
  },

  /**
   * Обновить текстовое сообщение
   * @param {string} chatId - ID чата
   * @param {string} messageId - ID сообщения
   * @param {string} newText - новый текст
   */
  updateTextMessage(chatId, messageId, newText) {
    return apiClient.put(`/chat/${chatId}/messages/text/${messageId}`, { chatId, newText })
  },

  /**
   * Удалить сообщение
   * @param {string} chatId - ID чата
   * @param {string} messageId - ID сообщения
   */
  deleteMessage(chatId, messageId) {
    return apiClient.delete(`/chat/${chatId}/messages/text/${messageId}`)
  }
}