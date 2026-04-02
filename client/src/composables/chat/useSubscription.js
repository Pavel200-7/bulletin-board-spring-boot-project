// src/composables/websocket/useSubscription.js
import { ref } from 'vue'
import { subscriptionService } from '@/services/websocket/subscriptionService'
import { websocketService } from '@/services/websocket/websocketService'
import { tokenManager } from '@/services/auth/tokenManager'

export function useSubscription() {
  const isSubscribed = ref(false)
  const subscriptions = ref(new Map())
  const isConnecting = ref(false)
  const connected = ref(false)

  /**
   * Подключиться к WebSocket
   */
  const connect = async () => {
    console.log('🔌 Connecting to WebSocket...')
    
    if (connected.value || websocketService.isConnected) {
      console.log('Already connected')
      connected.value = true
      return true
    }
    
    if (isConnecting.value) {
      console.log('Already connecting...')
      return new Promise((resolve) => {
        const checkInterval = setInterval(() => {
          if (connected.value || websocketService.isConnected) {
            clearInterval(checkInterval)
            connected.value = true
            resolve(true)
          }
        }, 100)
      })
    }
    
    isConnecting.value = true
    
    const token = tokenManager.getAccessToken()
    console.log('Token available:', !!token)
    
    if (!token) {
      console.warn('No token available')
      isConnecting.value = false
      return false
    }
    
    try {
      await websocketService.connect(token)
      connected.value = true
      console.log('✅ WebSocket connected successfully')
      return true
    } catch (err) {
      console.error('❌ Failed to connect to WebSocket:', err)
      connected.value = false
      return false
    } finally {
      isConnecting.value = false
    }
  }

  /**
   * Отключиться от WebSocket
   */
  const disconnect = () => {
    console.log('🔌 Disconnecting from WebSocket...')
    websocketService.disconnect()
    connected.value = false
    isSubscribed.value = false
    subscriptions.value.clear()
  }

  /**
   * Проверить подключение
   */
  const isConnected = () => {
    return connected.value || websocketService.isConnected()
  }

  /**
   * Подписаться на чат
   * @param {string} chatId - ID чата
   * @param {Object} handlers - обработчики событий
   * @param {Function} handlers.onMessage - новое сообщение
   * @param {Function} handlers.onUpdate - обновление сообщения
   * @param {Function} handlers.onDelete - удаление сообщения
   */
  const subscribeToChat = async (chatId, handlers = {}) => {
    console.log(`📡 Subscribing to chat ${chatId}...`)
    
    if (!isConnected()) {
      console.warn('WebSocket not connected, attempting to connect...')
      const connected_ = await connect()
      if (!connected_) {
        throw new Error('WebSocket not connected')
      }
    }
    
    const {
      onMessage,
      onUpdate,
      onDelete
    } = handlers

    // Сохраняем ID чата для отслеживания
    if (!subscriptions.value.has(chatId)) {
      subscriptions.value.set(chatId, {})
    }

    const chatSubs = subscriptions.value.get(chatId)

    // Подписка на новые сообщения
    if (onMessage && !chatSubs.message) {
      console.log(`  - Subscribing to messages for chat ${chatId}`)
      chatSubs.message = await subscriptionService.subscribeToChat(chatId, onMessage)
    }

    // Подписка на обновления
    if (onUpdate && !chatSubs.update) {
      console.log(`  - Subscribing to updates for chat ${chatId}`)
      chatSubs.update = await subscriptionService.subscribeToChatUpdates(chatId, onUpdate)
    }

    // Подписка на удаления
    if (onDelete && !chatSubs.delete) {
      console.log(`  - Subscribing to deletes for chat ${chatId}`)
      chatSubs.delete = await subscriptionService.subscribeToChatDeletes(chatId, onDelete)
    }

    isSubscribed.value = true
    console.log(`✅ Subscribed to all events for chat ${chatId}`)
  }

  /**
   * Отписаться от чата
   * @param {string} chatId - ID чата
   */
  const unsubscribeFromChat = (chatId) => {
    console.log(`📡 Unsubscribing from chat ${chatId}...`)
    subscriptionService.unsubscribeFromChat(chatId)
    subscriptions.value.delete(chatId)
    
    if (subscriptions.value.size === 0) {
      isSubscribed.value = false
    }
    console.log(`✅ Unsubscribed from chat ${chatId}`)
  }

  /**
   * Подписаться на личные ответы
   * @param {Function} onReply - обработчик ответов
   */
  const subscribeToReplies = async (onReply) => {
    console.log('📡 Subscribing to replies...')
    if (!isConnected()) {
      await connect()
    }
    return subscriptionService.subscribeToReplies(onReply)
  }

  /**
   * Отписаться от всех каналов
   */
  const unsubscribeAll = () => {
    console.log('📡 Unsubscribing from all channels...')
    subscriptions.value.forEach((_, chatId) => {
      subscriptionService.unsubscribeFromChat(chatId)
    })
    subscriptions.value.clear()
    isSubscribed.value = false
    console.log('✅ Unsubscribed from all channels')
  }

  return {
    isSubscribed,
    connected,
    isConnected,
    connect,
    disconnect,
    subscribeToChat,
    unsubscribeFromChat,
    subscribeToReplies,
    unsubscribeAll
  }
}