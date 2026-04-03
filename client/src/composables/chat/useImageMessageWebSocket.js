// src/composables/chat/useImageMessageWebSocket.js
import { ref } from 'vue'
import { imageMessageWebSocketService } from '@/services/websocket/imageMessageService'
import { websocketService } from '@/services/websocket/websocketService'

export function useImageMessageWebSocket() {
  const sending = ref(false)
  const deleting = ref(false)
  const error = ref(null)

  /**
   * Отправить image сообщение
   * @param {string} chatId - ID чата
   * @param {string} imageId - ID изображения
   * @returns {boolean} - успешность отправки
   */
  const sendImageMessage = (chatId, imageId) => {
    if (!imageId) {
      error.value = 'Image ID is required'
      return false
    }
    
    // Проверяем подключение ДО установки sending
    if (!websocketService.isConnected) {
      console.warn('WebSocket not connected, cannot send image message')
      error.value = 'WebSocket not connected'
      return false
    }
    
    sending.value = true
    error.value = null
    
    console.log("Отправляю image message", { chatId, imageId })

    try {
      const result = imageMessageWebSocketService.sendImageMessage(chatId, imageId)
      console.log("Результат отправки:", result)
      return result
    } catch (err) {
      console.error("Ошибка при отправке:", err)
      error.value = err.message
      return false
    } finally {
      sending.value = false
    }
  }

  /**
   * Удалить image сообщение
   * @param {string} chatId - ID чата
   * @param {string} messageId - ID сообщения
   * @returns {boolean} - успешность отправки
   */
  const deleteImageMessage = (chatId, messageId) => {
    if (!messageId) {
      error.value = 'Message ID is required'
      return false
    }
    
    if (!websocketService.isConnected) {
      console.warn('WebSocket not connected, cannot delete image message')
      error.value = 'WebSocket not connected'
      return false
    }
    
    deleting.value = true
    error.value = null
    
    try {
      const result = imageMessageWebSocketService.deleteImageMessage(chatId, messageId)
      return result
    } catch (err) {
      error.value = err.message
      return false
    } finally {
      deleting.value = false
    }
  }

  return {
    sending,
    deleting,
    error,
    sendImageMessage,
    deleteImageMessage
  }
}