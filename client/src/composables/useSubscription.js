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
   * Создать подписку
   * @param {Object} data - данные подписки
   */
  const createSubscription = async (data) => {
    const response = await handleRequest(() => subscriptionService.createSubscription(data))
    // можно обновить список после создания
    await fetchMySubscriptions()
    return response
  }

  /**
   * Удалить подписку
   * @param {string} id - ID подписки
   */
  const deleteSubscription = async (id) => {
    const response = await handleRequest(() => subscriptionService.deleteSubscription(id))
    // обновляем список после удаления
    subscriptions.value = subscriptions.value.filter(s => s.id !== id)
    return response
  }

  /**
   * Проверить, подписан ли пользователь на определенный тип
   * @param {string} type - тип уведомления
   * @param {string} [publisherId] - ID издателя (опционально)
   */
  const isSubscribed = (type, publisherId) => {
    return subscriptions.value.some(s => 
      s.type === type && 
      (!publisherId || s.publisherId === publisherId)
    )
  }

  return {
    // state
    subscriptions,
    loading,
    error,

    // actions
    fetchMySubscriptions,
    createSubscription,
    deleteSubscription,
    isSubscribed
  }
}