// src/services/websocket/textMessageService.js
import { websocketService } from './websocketService'

class TextMessageWebSocketService {
  /**
   * Отправить сообщение через WebSocket
   * @param {string} chatId - ID чата
   * @param {string} text - текст сообщения
   * @returns {boolean} - успешность отправки
   */
  sendMessage(chatId, text) {
    if (!websocketService.isConnected) {
      console.warn('WebSocket not connected')
      return false
    }

    const client = websocketService.getClient()
    client.publish({
      destination: `/app/chat/${chatId}/message/create`,
      body: JSON.stringify({ text })
    })
    
    return true
  }

  /**
   * Обновить сообщение через WebSocket
   * @param {string} chatId - ID чата
   * @param {string} messageId - ID сообщения
   * @param {string} newText - новый текст
   * @returns {boolean} - успешность отправки
   */
  updateMessage(chatId, messageId, newText) {
    if (!websocketService.isConnected) {
      console.warn('WebSocket not connected')
      return false
    }

    const client = websocketService.getClient()
    client.publish({
      destination: `/app/chat/${chatId}/message/${messageId}/update`,
      body: JSON.stringify({ newText })
    })
    
    return true
  }

  /**
   * Удалить сообщение через WebSocket
   * @param {string} chatId - ID чата
   * @param {string} messageId - ID сообщения
   * @returns {boolean} - успешность отправки
   */
  deleteMessage(chatId, messageId) {
    if (!websocketService.isConnected) {
      console.warn('WebSocket not connected')
      return false
    }

    const client = websocketService.getClient()
    client.publish({
      destination: `/app/chat/${chatId}/message/${messageId}/delete`,
      body: JSON.stringify({})
    })
    
    return true
  }

//   /**
//    * Отправить уведомление о печатании
//    * @param {string} chatId - ID чата
//    * @param {boolean} isTyping - печатает ли пользователь
//    */
//   sendTyping(chatId, isTyping = true) {
//     if (!websocketService.isConnected) return false

//     const client = websocketService.getClient()
//     client.publish({
//       destination: `/app/chat/${chatId}/typing`,
//       body: JSON.stringify({ typing: isTyping })
//     })
    
//     return true
//   }
// 
//   /**
//    * Отправить уведомление о прочтении
//    * @param {string} chatId - ID чата
//    * @param {string} lastReadMessageId - ID последнего прочитанного сообщения
//    */
//   sendReadReceipt(chatId, lastReadMessageId) {
//     if (!websocketService.isConnected) return false

//     const client = websocketService.getClient()
//     client.publish({
//       destination: `/app/chat/${chatId}/read`,
//       body: JSON.stringify({ lastReadMessageId })
//     })
    
//     return true
//   }
}

export const textMessageWebSocketService = new TextMessageWebSocketService()