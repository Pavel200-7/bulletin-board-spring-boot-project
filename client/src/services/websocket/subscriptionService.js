// src/services/websocket/subscriptionService.js
import { websocketService } from './websocketService'

class SubscriptionService {
  constructor() {
    this.handlers = new Map()
    this.subscribeQueue = []
    this.isProcessing = false
  }

  /**
   * Проверить подключение
   */
  isConnected() {
    return websocketService.isConnected()
  }

  /**
   * Подписаться на канал с очередью (чтобы избежать конфликтов)
   */
  async subscribe(destination, onMessage) {
    // Добавляем в очередь
    return new Promise((resolve, reject) => {
      this.subscribeQueue.push({
        destination,
        onMessage,
        resolve,
        reject
      })
      this.processQueue()
    })
  }

  async processQueue() {
    if (this.isProcessing || this.subscribeQueue.length === 0) {
      return
    }

    this.isProcessing = true

    while (this.subscribeQueue.length > 0) {
      const task = this.subscribeQueue.shift()
      
      try {
        // Ждем между подписками 200ms
        if (this.subscribeQueue.length > 0) {
          await new Promise(resolve => setTimeout(resolve, 200))
        }
        
        const subscription = await this.doSubscribe(task.destination, task.onMessage)
        task.resolve(subscription)
      } catch (err) {
        task.reject(err)
      }
    }

    this.isProcessing = false
  }

  async doSubscribe(destination, onMessage) {
    if (!websocketService.isConnected) {
      throw new Error('WebSocket not connected')
    }

    const client = websocketService.getClient()
    
    const callback = (message) => {
      try {
        const data = JSON.parse(message.body)
        onMessage(data)
      } catch (err) {
        console.error(`Error parsing message from ${destination}:`, err)
        onMessage(message.body)
      }
    }
    
    // console.log(`📡 Subscribing to ${destination}...`)
    const subscription = client.subscribe(destination, callback)
    websocketService._registerSubscription(destination, subscription, callback)
    // console.log(`✅ Subscribed to ${destination}`)
    
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
   * Подписаться на все события чата (один топик)
   * @param {string} chatId - ID чата
   * @param {Function} onMessage - обработчик сообщений (уже включает все типы событий)
   */
  async subscribeToChat(chatId, onMessage) {
    return this.subscribe(`/topic/chat/${chatId}`, onMessage)
  }

  /**
   * Отписаться от чата
   * @param {string} chatId - ID чата
   */
  unsubscribeFromChat(chatId) {
    this.unsubscribe(`/topic/chat/${chatId}`)
  }
}

export const subscriptionService = new SubscriptionService()