// src/services/websocket/websocketService.js
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

class WebSocketService {
  constructor() {
    this.client = null
    this.isConnected = false
    this.connectPromise = null
    this.subscriptions = new Map()
    this.wsUrl = '/api/v1/ws'
    console.log('WebSocketService initialized with URL:', this.wsUrl)
  }

  connect(token) {
      if (this.isConnected) {
          return Promise.resolve()
      }

      if (this.connectPromise) {
          return this.connectPromise
      }

      const wsUrlWithToken = `${this.wsUrl}?access_token=${encodeURIComponent(token)}`
      console.log('Connecting to WebSocket at:', wsUrlWithToken)

      this.connectPromise = new Promise((resolve, reject) => {
          const socket = new SockJS(wsUrlWithToken)
          
          this.client = new Client({
              webSocketFactory: () => socket,
              connectHeaders: {
                  Authorization: `Bearer ${token}`  
              },
              reconnectDelay: 5000,
              heartbeatIncoming: 4000,
              heartbeatOutgoing: 4000,
              
              onConnect: () => {
                  console.log('✅ WebSocket connected')
                  this.isConnected = true
                  this.connectPromise = null
                  resolve()
              },
              
              onStompError: (frame) => {
                  console.error('❌ STOMP error:', frame)
                  this.isConnected = false
                  this.connectPromise = null
                  reject(frame)
              },
              
              onWebSocketError: (error) => {
                  console.error('❌ WebSocket error:', error)
                  this.isConnected = false
                  this.connectPromise = null
                  reject(error)
              },
              
              onDisconnect: () => {
                  console.log('🔌 WebSocket disconnected')
                  this.isConnected = false
              }
          })

          this.client.activate()
      })

      return this.connectPromise
  }

  /**
   * Отключение от WebSocket
   */
  disconnect() {
    if (this.client && this.isConnected) {
      this.subscriptions.forEach((sub, key) => {
        if (sub) {
          sub.unsubscribe()
        }
      })
      this.subscriptions.clear()
      this.client.deactivate()
      this.isConnected = false
    }
    this.connectPromise = null
  }

  /**
   * Получить клиент для прямого использования
   */
  getClient() {
    return this.client
  }

  /**
   * Проверить подключение
   */
  isConnected() {
    return this.isConnected
  }

  /**
   * Зарегистрировать подписку (для внутреннего использования)
   */
  _registerSubscription(destination, subscription) {
    this.subscriptions.set(destination, subscription)
  }

  /**
   * Удалить подписку
   */
  _unregisterSubscription(destination) {
    const sub = this.subscriptions.get(destination)
    if (sub) {
      sub.unsubscribe()
      this.subscriptions.delete(destination)
    }
  }
}

export const websocketService = new WebSocketService()