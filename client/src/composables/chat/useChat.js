// src/composables/chat/useChat.js
import { ref } from 'vue'
import { chatService } from '@/services/chat/chatService'

export function useChat() {
  const currentChat = ref(null)
  const messages = ref([])
  const unreadCount = ref(0)
  const loading = ref(false)
  const loadingOlder = ref(false)
  const loadingNewer = ref(false)
  const error = ref(null)
  const hasOlder = ref(true)
  const hasNewer = ref(false)
  const firstMessageId = ref(null)
  const lastMessageId = ref(null)

  const handleRequest = async (requestFn) => {
    loading.value = true
    error.value = null
    try {
      const response = await requestFn()
      return response
    } catch (err) {
      error.value = err.response?.data?.message || err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * Получить данные чата
   * @param {string} chatId - ID чата
   */
  const fetchChat = async (chatId) => {
    const response = await handleRequest(() => chatService.getChat(chatId))
    currentChat.value = response.data?.chatRoomResponse || response.data
    return response
  }

  /**
   * Получить количество непрочитанных сообщений
   * @param {string} chatId - ID чата
   */
  const fetchUnreadCount = async (chatId) => {
    const response = await handleRequest(() => chatService.getUnreadMessageCount(chatId))
    unreadCount.value = response.data?.count || 0
    return response
  }

  /**
   * Проверить, есть ли более старые сообщения
   * @param {string} chatId - ID чата
   * @param {string} messageId - ID опорного сообщения
   */
  const checkHasOlder = async (chatId, messageId) => {
    if (!messageId) return false
    return await chatService.hasOlderMessages(chatId, messageId)
  }

  /**
   * Проверить, есть ли более новые сообщения
   * @param {string} chatId - ID чата
   * @param {string} messageId - ID опорного сообщения
   */
  const checkHasNewer = async (chatId, messageId) => {
    if (!messageId) return false
    return await chatService.hasNewerMessages(chatId, messageId)
  }

  /**
   * Загрузить сообщения вокруг последнего прочитанного
   * @param {string} chatId - ID чата
   * @param {number} size - количество сообщений
   */
  const loadMessagesAroundLastRead = async (chatId, size = 20) => {
    try {
      loading.value = true
      const response = await chatService.getMessagesAroundLastRead(chatId, size)
      const pageData = response.data?.chatMessagePage || response.data
      
      const content = pageData?.content || []
      
      messages.value = content
      
      if (content.length > 0) {
        firstMessageId.value = content[0]?.id
        lastMessageId.value = content[content.length - 1]?.id
        
        // Проверяем наличие старых и новых сообщений
        hasOlder.value = await checkHasOlder(chatId, firstMessageId.value)
        hasNewer.value = await checkHasNewer(chatId, lastMessageId.value)
      } else {
        hasOlder.value = false
        hasNewer.value = false
      }

      return response
    } catch (err) {
      error.value = err.response?.data?.message || err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * Загрузить более старые сообщения (скролл вверх)
   */
  const loadOlderMessages = async (chatId, size = 20) => {
    if (loadingOlder.value || !hasOlder.value || !firstMessageId.value) {
      return
    }
    
    loadingOlder.value = true
    
    try {
      const response = await chatService.getOlderMessages(chatId, firstMessageId.value, size)
      const pageData = response.data?.chatMessagePage || response.data
      const olderMessages = pageData?.content || []
      
      if (olderMessages.length > 0) {
        messages.value = [...olderMessages, ...messages.value]
        firstMessageId.value = messages.value[0]?.id
        // Проверяем, есть ли еще более старые
        hasOlder.value = await checkHasOlder(chatId, firstMessageId.value)
      } else {
        hasOlder.value = false
      }
      
      return response
    } catch (err) {
      console.error('Ошибка загрузки старых сообщений:', err)
      throw err
    } finally {
      loadingOlder.value = false
    }
  }

  /**
   * Загрузить более новые сообщения (скролл вниз / кнопка)
   */
  const loadNewerMessages = async (chatId, size = 20) => {
    if (loadingNewer.value || !hasNewer.value || !lastMessageId.value) {
      return
    }
    
    loadingNewer.value = true
    
    try {
      const response = await chatService.getNewerMessages(chatId, lastMessageId.value, size)
      const pageData = response.data?.chatMessagePage || response.data
      const newerMessages = pageData?.content || []
      
      if (newerMessages.length > 0) {
        messages.value = [...messages.value, ...newerMessages]
        lastMessageId.value = messages.value[messages.value.length - 1]?.id
        // Проверяем, есть ли еще более новые
        hasNewer.value = await checkHasNewer(chatId, lastMessageId.value)
      } else {
        hasNewer.value = false
      }
      
      return response
    } catch (err) {
      console.error('Ошибка загрузки новых сообщений:', err)
      throw err
    } finally {
      loadingNewer.value = false
    }
  }

  /**
   * Добавить новое сообщение в список (из WebSocket)
   * @param {Object} message - новое сообщение
   */
  const addMessage = (message) => {
    if (!message) return
    
    // Проверяем, нет ли уже такого сообщения
    const exists = messages.value.some(m => m.id === message.id)
    if (exists) return
    
    // Добавляем сообщение в конец списка
    messages.value = [...messages.value, message]
    
    // Обновляем lastMessageId
    if (messages.value.length > 0) {
      lastMessageId.value = messages.value[messages.value.length - 1]?.id
    }
  }

  /**
   * Удалить сообщение из списка (по WebSocket уведомлению)
   * @param {string} messageId - ID сообщения для удаления
   */
  const removeMessage = (messageId) => {
    if (!messageId) return
    
    const index = messages.value.findIndex(m => m.id === messageId)
    if (index === -1) return
    
    messages.value = messages.value.filter(m => m.id !== messageId)
    
    // Если удалили последнее сообщение, обновляем lastMessageId
    if (messages.value.length > 0) {
      lastMessageId.value = messages.value[messages.value.length - 1]?.id
    } else {
      lastMessageId.value = null
    }
    
    // Если удалили первое сообщение, обновляем firstMessageId
    if (messages.value.length > 0) {
      firstMessageId.value = messages.value[0]?.id
    } else {
      firstMessageId.value = null
      hasOlder.value = false
      hasNewer.value = false
    }
  }

  /**
   * Обновить сообщение в списке (по WebSocket уведомлению)
   * @param {string} messageId - ID сообщения
   * @param {Object} updates - обновленные поля
   */
  const updateMessage = (messageId, updates) => {
    if (!messageId || !updates) return
    
    const index = messages.value.findIndex(m => m.id === messageId)
    if (index === -1) return
    
    messages.value[index] = { ...messages.value[index], ...updates }
  }

  /**
   * Установить последнее прочитанное сообщение
   * @param {string} chatId - ID чата
   * @param {string} messageId - ID сообщения
   */
  const setLastRead = async (chatId, messageId) => {
    try {
      await chatService.setLastReadMessage(chatId, messageId)
      await fetchUnreadCount(chatId)
    } catch (err) {
      console.error('Ошибка установки последнего прочитанного:', err)
    }
  }

  /**
   * Сбросить флаг hasNewer (когда пользователь доскроллил вниз)
   */
  const resetHasNewer = () => {
    hasNewer.value = true
  }

  return {
    // state
    currentChat,
    messages,
    unreadCount,
    loading,
    loadingOlder,
    loadingNewer,
    error,
    hasOlder,
    hasNewer,
    firstMessageId,
    lastMessageId,

    // actions
    fetchChat,
    fetchUnreadCount,
    loadMessagesAroundLastRead,
    loadOlderMessages,
    loadNewerMessages,
    setLastRead,
    checkHasOlder,
    checkHasNewer,
    
    // WebSocket методы
    addMessage,
    removeMessage,
    updateMessage,
    resetHasNewer
  }
}