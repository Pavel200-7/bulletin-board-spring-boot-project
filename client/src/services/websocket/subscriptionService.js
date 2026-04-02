// src/services/websocket/subscriptionService.js
import { websocketService } from './websocketService'

class SubscriptionService {
  constructor() {
    this.handlers = new Map()
  }

  /**
   * Проверить подключение
   */
  isConnected() {
    return websocketService.isConnected()
  }

  /**
   * Подписаться на канал
   * @param {string} destination - адрес канала (например, /topic/chat/{chatId})
   * @param {Function} onMessage - обработчик сообщений
   * @returns {Promise} - промис с объектом подписки
   */
  async subscribe(destination, onMessage) {
    if (!websocketService.isConnected) {
      throw new Error('WebSocket not connected')
    }

    const client = websocketService.getClient()
    
    const subscription = client.subscribe(destination, (message) => {
      try {
        const data = JSON.parse(message.body)
        onMessage(data)
      } catch (err) {
        console.error('Error parsing message:', err)
        onMessage(message.body)
      }
    })

    websocketService._registerSubscription(destination, subscription)
    
    return subscription
  }

  /**
   * Отписаться от канала
   * @param {string} destination - адрес канала
   */
  unsubscribe(destination) {
    websocketService._unregisterSubscription(destination)
  }

  /**
   * Подписаться на личные ответы
   * @param {Function} onReply - обработчик ответов
   */
  async subscribeToReplies(onReply) {
    return this.subscribe('/user/queue/reply', onReply)
  }

  /**
   * Подписаться на сообщения чата
   * @param {string} chatId - ID чата
   * @param {Function} onMessage - обработчик сообщений
   */
  async subscribeToChat(chatId, onMessage) {
    return this.subscribe(`/topic/chat/${chatId}`, onMessage)
  }

  /**
   * Подписаться на обновления в чате
   * @param {string} chatId - ID чата
   * @param {Function} onUpdate - обработчик обновлений
   */
  async subscribeToChatUpdates(chatId, onUpdate) {
    return this.subscribe(`/topic/chat/${chatId}/updates`, onUpdate)
  }

  /**
   * Подписаться на удаления в чате
   * @param {string} chatId - ID чата
   * @param {Function} onDelete - обработчик удалений
   */
  async subscribeToChatDeletes(chatId, onDelete) {
    return this.subscribe(`/topic/chat/${chatId}/deletes`, onDelete)
  }

  /**
   * Отписаться от всех каналов чата
   * @param {string} chatId - ID чата
   */
  unsubscribeFromChat(chatId) {
    this.unsubscribe(`/topic/chat/${chatId}`)
    this.unsubscribe(`/topic/chat/${chatId}/updates`)
    this.unsubscribe(`/topic/chat/${chatId}/deletes`)
  }
}

export const subscriptionService = new SubscriptionService()