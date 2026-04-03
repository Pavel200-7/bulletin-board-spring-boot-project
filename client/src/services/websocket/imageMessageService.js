// src/services/websocket/imageMessageService.js
import { websocketService } from './websocketService'

class ImageMessageWebSocketService {
  /**
   * Отправить image сообщение через WebSocket
   * @param {string} chatId - ID чата
   * @param {string} imageId - ID изображения
   * @returns {boolean} - успешность отправки
   */
  sendImageMessage(chatId, imageId) {
    if (!websocketService.isConnected) {
      console.warn('WebSocket not connected')
      return false
    }

    const client = websocketService.getClient()
    client.publish({
      destination: `/app/chat/${chatId}/message/image/create`,
      body: JSON.stringify({ imageId })
    })
    
    return true
  }

  /**
   * Удалить image сообщение через WebSocket
   * @param {string} chatId - ID чата
   * @param {string} messageId - ID сообщения
   * @returns {boolean} - успешность отправки
   */
  deleteImageMessage(chatId, messageId) {
    if (!websocketService.isConnected) {
      console.warn('WebSocket not connected')
      return false
    }

    const client = websocketService.getClient()
    client.publish({
      destination: `/app/chat/${chatId}/message/image/${messageId}/delete`,
      body: JSON.stringify({})
    })
    
    return true
  }
}

export const imageMessageWebSocketService = new ImageMessageWebSocketService()