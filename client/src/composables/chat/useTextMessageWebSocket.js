// src/composables/chat/useTextMessageWebSocket.js
import { ref } from 'vue'
import { textMessageWebSocketService } from '@/services/websocket/textMessageService'

export function useTextMessageWebSocket() {
  const sending = ref(false)
  const updating = ref(false)
  const deleting = ref(false)
  const error = ref(null)

  /**
   * Отправить сообщение
   * @param {string} chatId - ID чата
   * @param {string} text - текст сообщения
   */
  const sendMessage = (chatId, text) => {
    if (!text.trim()) return false
    
    sending.value = true
    error.value = null
    
    try {
      const result = textMessageWebSocketService.sendMessage(chatId, text)
      return result
    } catch (err) {
      error.value = err.message
      return false
    } finally {
      sending.value = false
    }
  }

  /**
   * Обновить сообщение
   * @param {string} chatId - ID чата
   * @param {string} messageId - ID сообщения
   * @param {string} newText - новый текст
   */
  const updateMessage = (chatId, messageId, newText) => {
    if (!newText.trim()) return false
    
    updating.value = true
    error.value = null
    
    try {
      const result = textMessageWebSocketService.updateMessage(chatId, messageId, newText)
      return result
    } catch (err) {
      error.value = err.message
      return false
    } finally {
      updating.value = false
    }
  }

  /**
   * Удалить сообщение
   * @param {string} chatId - ID чата
   * @param {string} messageId - ID сообщения
   */
  const deleteMessage = (chatId, messageId) => {
    deleting.value = true
    error.value = null
    
    try {
      const result = textMessageWebSocketService.deleteMessage(chatId, messageId)
      return result
    } catch (err) {
      error.value = err.message
      return false
    } finally {
      deleting.value = false
    }
  }

//   /**
//    * Отправить уведомление о печатании
//    * @param {string} chatId - ID чата
//    * @param {boolean} isTyping - печатает ли пользователь
//    */
//   const sendTyping = (chatId, isTyping = true) => {
//     return textMessageWebSocketService.sendTyping(chatId, isTyping)
//   }

//   /**
//    * Отправить уведомление о прочтении
//    * @param {string} chatId - ID чата
//    * @param {string} lastReadMessageId - ID последнего прочитанного сообщения
//    */
//   const sendReadReceipt = (chatId, lastReadMessageId) => {
//     return textMessageWebSocketService.sendReadReceipt(chatId, lastReadMessageId)
//   }

  return {
    sending,
    updating,
    deleting,
    error,
    sendMessage,
    updateMessage,
    deleteMessage
  }
}