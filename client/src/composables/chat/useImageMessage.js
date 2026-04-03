// src/composables/chat/useImageMessage.js
import { ref } from 'vue'
import { imageMessageService } from '@/services/chat/imageMessageService'
import { imageMessageWebSocketService } from '@/services/websocket/imageMessageService'
import { websocketService } from '@/services/websocket/websocketService'

export function useImageMessage() {
  const sending = ref(false)
  const deleting = ref(false)
  const error = ref(null)

  const handleRequest = async (requestFn, webSocketFn, ...args) => {
    error.value = null
    
    // Пробуем сначала отправить через WebSocket
    if (websocketService.isConnected()) {
      try {
        const success = webSocketFn(...args)
        if (success) {
          console.log('Image message sent via WebSocket')
          return { success: true, viaWebSocket: true }
        }
      } catch (err) {
        console.warn('WebSocket send failed, falling back to REST:', err)
      }
    }
    
    // Fallback на REST
    try {
      const response = await requestFn()
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || err.message
      throw err
    }
  }

  /**
   * Отправить image сообщение
   * @param {string} chatId - ID чата
   * @param {string} imageId - ID изображения
   * @returns {Promise<Object>} - ответ с данными созданного сообщения
   */
  const sendImageMessage = async (chatId, imageId) => {
    sending.value = true
    try {
      const result = await handleRequest(
        () => imageMessageService.createImageMessage(chatId, imageId),
        (id, imgId) => imageMessageWebSocketService.sendImageMessage(id, imgId),
        chatId,
        imageId
      )
      return result
    } finally {
      sending.value = false
    }
  }

  /**
   * Удалить image сообщение
   * @param {string} chatId - ID чата
   * @param {string} messageId - ID сообщения
   * @returns {Promise<Object>} - ответ об успешном удалении
   */
  const deleteImageMessage = async (chatId, messageId) => {
    deleting.value = true
    try {
      const result = await handleRequest(
        () => imageMessageService.deleteImageMessage(chatId, messageId),
        (id, msgId) => imageMessageWebSocketService.deleteImageMessage(id, msgId),
        chatId,
        messageId
      )
      return result
    } finally {
      deleting.value = false
    }
  }

  return {
    // state
    sending,
    deleting,
    error,

    // actions
    sendImageMessage,
    deleteImageMessage
  }
}