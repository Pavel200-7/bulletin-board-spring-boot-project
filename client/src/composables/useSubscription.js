// src/composables/useSubscription.js
import { ref } from 'vue'
import { subscriptionService } from '@/services/subscription/subscriptionService'

export function useSubscription() {
  const subscriptions = ref([])
  const loading = ref(false)
  const error = ref(null)

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
   * Загрузить список подписок текущего пользователя
   */
  const fetchMySubscriptions = async () => {
    const response = await handleRequest(() => subscriptionService.getMySubscriptions())
    subscriptions.value = response.data?.subscriptionResponses || response.data || []
    return response
  }

  /**
   * Проверить, существует ли подписка по критериям
   * @param {string} subscriptionType - тип уведомления
   * @param {string} publisherId - ID издателя
   * @returns {Promise<{exists: boolean, subscriptionId: string|null}>}
   */
  const checkExists = async (subscriptionType, publisherId) => {
    try {
      const response = await handleRequest(() => subscriptionService.existsByCriteria(subscriptionType, publisherId))
      const data = response.data
      return {
        exists: data?.exists || false,
        subscriptionId: data?.subscriptionResponse?.id || null
      }
    } catch (err) {
      console.error('Ошибка проверки подписки:', err)
      return { exists: false, subscriptionId: null }
    }
  }

  /**
   * Создать подписку
   * @param {Object} data - данные подписки
   */
  const createSubscription = async (data) => {
    const response = await handleRequest(() => subscriptionService.createSubscription(data))
    await fetchMySubscriptions()
    return response
  }

  /**
   * Удалить подписку
   * @param {string} id - ID подписки
   */
  const deleteSubscription = async (id) => {
    const response = await handleRequest(() => subscriptionService.deleteSubscription(id))
    subscriptions.value = subscriptions.value.filter(s => s.id !== id)
    return response
  }

  /**
   * Проверить, подписан ли пользователь на определенный тип (по локальному списку)
   * @param {string} type - тип уведомления
   * @param {string} [publisherId] - ID издателя (опционально)
   */
  const isSubscribed = (type, publisherId) => {
    return subscriptions.value.some(s => 
      s.subscriptionType === type && 
      (!publisherId || s.publisherId === publisherId)
    )
  }

  /**
   * Переключить подписку (создать если нет, удалить если есть)
   * @param {string} subscriptionType - тип уведомления
   * @param {string} publisherId - ID издателя
   * @returns {Promise<boolean>} - новое состояние подписки
   */
  const toggleSubscription = async (subscriptionType, publisherId) => {
    // Проверяем существование подписки
    const { exists, subscriptionId } = await checkExists(subscriptionType, publisherId)
    
    if (exists && subscriptionId) {
      // Если подписка существует, удаляем
      await deleteSubscription(subscriptionId)
      return false
    } else {
      // Если подписки нет, создаем
      await createSubscription({ subscriptionType, publisherId })
      return true
    }
  }

  return {
    // state
    subscriptions,
    loading,
    error,

    // actions
    fetchMySubscriptions,
    checkExists,
    createSubscription,
    deleteSubscription,
    isSubscribed,
    toggleSubscription
  }
}