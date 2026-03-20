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

  // ========== ПОЛУЧЕНИЕ ==========

  const fetchCharacteristic = async (id) => {
    const response = await handleRequest(() => characteristicService.getCharacteristic(id))
    characteristic.value = response.data?.characteristicResponse || response.data
    return response
  }

  const fetchCategoryCharacteristics = async (categoryId) => {
    const response = await handleRequest(() => characteristicService.getCategoryCharacteristics(categoryId))
    characteristics.value = response.data?.characteristicResponse || response.data || []
    return response
  }

  // ========== СОЗДАНИЕ ==========

  const createCharacteristic = async (categoryId, name) => {
    const response = await handleRequest(() => characteristicService.createCharacteristic(categoryId, name))
    characteristic.value = response.data?.characteristicResponse || response.data
    return response
  }

  // ========== ИЗМЕНЕНИЕ ==========

  const renameCharacteristic = async (id, name) => {
    const response = await handleRequest(() => characteristicService.renameCharacteristic(id, name))
    characteristic.value = response.data?.characteristicResponse || response.data
    return response
  }

  // ========== УДАЛЕНИЕ ==========

  const deleteCharacteristic = async (categoryId, characteristicId) => {
    return handleRequest(() => characteristicService.deleteCharacteristic(categoryId, characteristicId))
  }

  return {
    // state
    characteristic,
    characteristics,
    loading,
    error,

    // actions
    fetchCharacteristic,
    fetchCategoryCharacteristics,
    createCharacteristic,
    renameCharacteristic,
    deleteCharacteristic
  }
}