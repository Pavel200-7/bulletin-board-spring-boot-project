// src/composables/useCharacteristic.js
import { ref } from 'vue'
import { characteristicService } from '@/services/characteristic/characteristicService'

export function useCharacteristic() {
  const characteristic = ref(null)
  const characteristics = ref([])
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

  const fetchCharacteristic = async (id) => {
    const response = await handleRequest(() => characteristicService.getCharacteristic(id))
    characteristic.value = response.data?.characteristicResponse || response.data
    return response
  }

  const fetchCategoryCharacteristics = async (categoryId) => {
    const response = await handleRequest(() => characteristicService.getCategoryCharacteristics(categoryId))
    // Правильно обрабатываем ответ
    const data = response.data?.characteristicResponse || response.data
    characteristics.value = Array.isArray(data) ? data : []
    return response
  }

  const createCharacteristic = async (categoryId, name) => {
    console.log('Creating characteristic with categoryId:', categoryId, 'name:', name) // для отладки
    const response = await handleRequest(() => characteristicService.createCharacteristic(categoryId, name))
    characteristic.value = response.data?.characteristicResponse || response.data
    return response
  }

  const renameCharacteristic = async (id, name) => {
    const response = await handleRequest(() => characteristicService.renameCharacteristic(id, name))
    characteristic.value = response.data?.characteristicResponse || response.data
    return response
  }

  const deleteCharacteristic = async (categoryId, characteristicId) => {
    return handleRequest(() => characteristicService.deleteCharacteristic(categoryId, characteristicId))
  }

  return {
    characteristic,
    characteristics,
    loading,
    error,
    fetchCharacteristic,
    fetchCategoryCharacteristics,
    createCharacteristic,
    renameCharacteristic,
    deleteCharacteristic
  }
}