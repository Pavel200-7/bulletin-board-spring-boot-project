// src/composables/chat/useTextMessage.js
import { ref } from 'vue'
import { textMessageService } from '@/services/chat/textMessageService'

export function useTextMessage() {
  const sending = ref(false)
  const updating = ref(false)
  const deleting = ref(false)
  const error = ref(null)

  const handleRequest = async (requestFn) => {
    error.value = null
    try {
      const response = await requestFn()
      return response
    } catch (err) {
      error.value = err.response?.data?.message || err.message
      throw err
    }
  }

  /**
   * Отправить текстовое сообщение
   * @param {string} chatId - ID чата
   * @param {string} text - текст сообщения
   * @returns {Promise<Object>} - ответ с данными созданного сообщения
   */
  const sendMessage = async (chatId, text) => {
    sending.value = true
    try {
      const response = await handleRequest(() => 
        textMessageService.createTextMessage(chatId, text)
      )
      return response.data?.message || response.data
    } finally {
      sending.value = false
    }
  }

  /**
   * Обновить текстовое сообщение
   * @param {string} chatId - ID чата
   * @param {string} messageId - ID сообщения
   * @param {string} newText - новый текст
   * @returns {Promise<Object>} - ответ с данными обновленного сообщения
   */
  const updateMessage = async (chatId, messageId, newText) => {
    updating.value = true
    try {
      const response = await handleRequest(() => 
        textMessageService.updateTextMessage(chatId, messageId, newText)
      )
      return response.data?.message || response.data
    } finally {
      updating.value = false
    }
  }

  /**
   * Удалить сообщение
   * @param {string} chatId - ID чата
   * @param {string} messageId - ID сообщения
   * @returns {Promise<Object>} - ответ об успешном удалении
   */
  const deleteMessage = async (chatId, messageId) => {
    deleting.value = true
    try {
      const response = await handleRequest(() => 
        textMessageService.deleteMessage(chatId, messageId)
      )
      return response.data
    } finally {
      deleting.value = false
    }
  }

  return {
    // state
    sending,
    updating,
    deleting,
    error,

    // actions
    sendMessage,
    updateMessage,
    deleteMessage
  }
}