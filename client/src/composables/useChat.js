// src/composables/useChat.js
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
 * Загрузить сообщения вокруг последнего прочитанного
 * @param {string} chatId - ID чата
 * @param {number} size - количество сообщений
 */
const loadMessagesAroundLastRead = async (chatId, size = 20) => {
  try {
    loading.value = true
    const response = await chatService.getMessagesAroundLastRead(chatId, size)
    const pageData = response.data?.chatMessagePage || response.data
    
    // Важно: правильно определяем first и last
    const content = pageData?.content || []
    
    messages.value = content
    
    if (content.length > 0) {
      firstMessageId.value = content[0]?.id
      lastMessageId.value = content[content.length - 1]?.id
    }
    
    // Определяем наличие старых и новых сообщений
    // first = true означает, что это первая страница (нет более старых)
    // last = true означает, что это последняя страница (нет более новых)
    hasOlder.value = !pageData?.first && content.length > 0
    hasNewer.value = !pageData?.last && content.length > 0
    
    console.log('loadMessagesAroundLastRead result:', {
      contentLength: content.length,
      hasOlder: hasOlder.value,
      hasNewer: hasNewer.value,
      first: pageData?.first,
      last: pageData?.last
    })
    
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
 * @param {string} chatId - ID чата
 * @param {number} size - количество сообщений
 */
const loadOlderMessages = async (chatId, size = 20) => {
  if (!chatId) {
    console.error('loadOlderMessages: chatId is required')
    return
  }
  
  if (loadingOlder.value || !hasOlder.value || !firstMessageId.value) {
    console.log('Skipping load older:', { 
      loadingOlder: loadingOlder.value, 
      hasOlder: hasOlder.value, 
      firstMessageId: firstMessageId.value 
    })
    return
  }
  
  loadingOlder.value = true
  
  try {
    const response = await chatService.getOlderMessages(chatId, firstMessageId.value, size)
    const pageData = response.data?.chatMessagePage || response.data
    const olderMessages = pageData?.content || []
    
    console.log('loadOlderMessages result:', {
      olderCount: olderMessages.length,
      first: pageData?.first,
      last: pageData?.last
    })
    
    if (olderMessages.length > 0) {
      messages.value = [...olderMessages, ...messages.value]
      firstMessageId.value = messages.value[0]?.id
      hasOlder.value = !pageData?.first
    } else {
      hasOlder.value = false
    }
    
    return response
  } catch (err) {
    console.error('Ошибка загрузки старых сообщений:', err)
    error.value = err.response?.data?.message || err.message
    throw err
  } finally {
    loadingOlder.value = false
  }
}

/**
 * Загрузить более новые сообщения (скролл вниз / при получении новых)
 * @param {string} chatId - ID чата
 * @param {number} size - количество сообщений
 */
const loadNewerMessages = async (chatId, size = 20) => {
  if (!chatId) {
    console.error('loadNewerMessages: chatId is required')
    return
  }
  
  if (loadingNewer.value || !hasNewer.value || !lastMessageId.value) {
    console.log('Skipping load newer:', { 
      loadingNewer: loadingNewer.value, 
      hasNewer: hasNewer.value, 
      lastMessageId: lastMessageId.value 
    })
    return
  }
  
  loadingNewer.value = true
  
  try {
    const response = await chatService.getNewerMessages(chatId, lastMessageId.value, size)
    const pageData = response.data?.chatMessagePage || response.data
    const newerMessages = pageData?.content || []
    
    console.log('loadNewerMessages result:', {
      newerCount: newerMessages.length,
      first: pageData?.first,
      last: pageData?.last
    })
    
    if (newerMessages.length > 0) {
      messages.value = [...messages.value, ...newerMessages]
      lastMessageId.value = messages.value[messages.value.length - 1]?.id
      hasNewer.value = !pageData?.last
    } else {
      hasNewer.value = false
    }
    
    return response
  } catch (err) {
    console.error('Ошибка загрузки новых сообщений:', err)
    error.value = err.response?.data?.message || err.message
    throw err
  } finally {
    loadingNewer.value = false
  }
}

  /**
   * Добавить сообщение в список (оптимистичное обновление)
   * @param {Object} message - сообщение
   */
  const addMessage = (message) => {
    messages.value = [...messages.value, message]
    lastMessageId.value = message.id
  }

  /**
   * Обновить сообщение в списке
   * @param {string} messageId - ID сообщения
   * @param {Object} updatedData - обновленные данные
   */
  const updateMessageInList = (messageId, updatedData) => {
    const index = messages.value.findIndex(m => m.id === messageId)
    if (index !== -1) {
      messages.value[index] = { ...messages.value[index], ...updatedData }
    }
  }

  /**
   * Удалить сообщение из списка
   * @param {string} messageId - ID сообщения
   */
  const removeMessageFromList = (messageId) => {
    messages.value = messages.value.filter(m => m.id !== messageId)
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
    addMessage,
    updateMessageInList,
    removeMessageFromList,
    setLastRead
  }
}