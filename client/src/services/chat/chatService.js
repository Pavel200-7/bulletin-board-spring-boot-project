// src/services/chat/chatService.js
import apiClient from '@/utils/apiClient'

/**
 * Сервис для работы с чатами
 */
export const chatService = {
  /**
   * Получить данные чата по ID
   * @param {string} chatId - ID чата
   */
  getChat(chatId) {
    return apiClient.get(`/chat/${chatId}`)
  },

  /**
   * Получить количество непрочитанных сообщений
   * @param {string} chatId - ID чата
   */
  getUnreadMessageCount(chatId) {
    return apiClient.get(`/chat/${chatId}/unread-count`)
  },

  /**
   * Получить сообщения с пагинацией по курсору
   * @param {string} chatId - ID чата
   * @param {Object} params - параметры пагинации
   * @param {string} params.cursorMessageId - ID опорного сообщения
   * @param {string} params.direction - направление (ASC - старые, DESC - новые)
   * @param {number} params.size - количество сообщений
   */
  getMessagesByCursor(chatId, { cursorMessageId, direction, size }) {
    const res = apiClient.post(`/chat/${chatId}/messages/search`, {
      chatId,
      cursorMessageId,
      direction,
      size
    })
    return res
  },

  /**
   * Получить сообщения вокруг последнего прочитанного
   * @param {string} chatId - ID чата
   * @param {number} size - количество сообщений
   */
  getMessagesAroundLastRead(chatId, size = 20) {
    return apiClient.post(`/chat/${chatId}/messages/last-read`, { chatId, size })
  },

  /**
   * Получить более старые сообщения (скролл вверх)
   * @param {string} chatId - ID чата
   * @param {string} cursorMessageId - ID опорного сообщения (самое старое из загруженных)
   * @param {number} size - количество сообщений
   */
  getOlderMessages(chatId, cursorMessageId, size = 20) {
    if (!chatId || !cursorMessageId) {
      console.error('Invalid parameters for getOlderMessages:', { chatId, cursorMessageId })
      return Promise.reject(new Error('chatId and cursorMessageId are required'))
    }
    const res = apiClient.post(`/chat/${chatId}/messages/search`, {
      chatId,
      cursorMessageId,
      direction: 'DESC',
      size
    })
    return res
  },

  /**
   * Получить более новые сообщения (скролл вниз)
   * @param {string} chatId - ID чата
   * @param {string} cursorMessageId - ID опорного сообщения (самое новое из загруженных)
   * @param {number} size - количество сообщений
   */
  getNewerMessages(chatId, cursorMessageId, size = 20) {
    if (!chatId || !cursorMessageId) {
      console.error('Invalid parameters for getNewerMessages:', { chatId, cursorMessageId })
      return Promise.reject(new Error('chatId and cursorMessageId are required'))
    }
    const res =  apiClient.post(`/chat/${chatId}/messages/search`, {
      chatId,
      cursorMessageId,
      direction: 'ASC',
      size
    })
    return res
  },

  /**
   * Проверить, есть ли более старые сообщения (до указанного)
   * @param {string} chatId - ID чата
   * @param {string} cursorMessageId - ID опорного сообщения
   */
  hasOlderMessages(chatId, cursorMessageId) {
    if (!chatId || !cursorMessageId) {
      return Promise.resolve(false)
    }
    return apiClient.post(`/chat/${chatId}/messages/search`, {
      chatId,
      cursorMessageId,
      direction: 'DESC ',
      size: 1
    }).then(response => {
      const content = response.data?.chatMessagePage?.content || []
      return content.length > 0
    }).catch(() => false)
  },

  /**
   * Проверить, есть ли более новые сообщения (после указанного)
   * @param {string} chatId - ID чата
   * @param {string} cursorMessageId - ID опорного сообщения
   */
  hasNewerMessages(chatId, cursorMessageId) {
    if (!chatId || !cursorMessageId) {
      return Promise.resolve(false)
    }
    return apiClient.post(`/chat/${chatId}/messages/search`, {
      chatId,
      cursorMessageId,
      direction: 'ASC',
      size: 1
    }).then(response => {
      const content = response.data?.chatMessagePage?.content || []
      console.log('hasNewerMessages response:', content)
      return content.length > 0
    }).catch(() => false)
  },

  /**
   * Установить последнее прочитанное сообщение
   * @param {string} chatId - ID чата
   * @param {string} messageId - ID сообщения
   */
  setLastReadMessage(chatId, messageId) {
    try {
      return apiClient.put(`/chat/${chatId}/messages/${messageId}/last-read`)
    } catch (error) {
    }
  },

  getFirstMessages(chatId, size = 20) {
    return apiClient.post(`/chat/${chatId}/messages/first`, { chatId, size })
  }

}