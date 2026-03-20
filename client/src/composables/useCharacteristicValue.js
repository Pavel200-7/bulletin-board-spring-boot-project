// src/composables/useCharacteristicValue.js
import { ref } from 'vue'
import { characteristicValueService } from '@/services/characteristicValue/characteristicValueService'

export function useCharacteristicValue() {
  const value = ref(null)
  const values = ref([])
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

  const fetchCharacteristicValue = async (id) => {
    const response = await handleRequest(() => characteristicValueService.getCharacteristicValue(id))
    value.value = response.data?.characteristicValueResponse || response.data
    return response
  }

  const fetchCharacteristicValues = async (characteristicId) => {
    const response = await handleRequest(() => characteristicValueService.getCharacteristicValues(characteristicId))
    values.value = response.data?.characteristicValueResponse || response.data || []
    return response
  }

  // ========== СОЗДАНИЕ ==========

  const createCharacteristicValue = async (characteristicId, name) => {
    const response = await handleRequest(() => characteristicValueService.createCharacteristicValue(characteristicId, name))
    value.value = response.data?.characteristicValueResponse || response.data
    return response
  }

  // ========== ИЗМЕНЕНИЕ ==========

  const renameCharacteristicValue = async (id, name) => {
    const response = await handleRequest(() => characteristicValueService.renameCharacteristicValue(id, name))
    value.value = response.data?.characteristicValueResponse || response.data
    return response
  }

  // ========== УДАЛЕНИЕ ==========

  const deleteCharacteristicValue = async (characteristicId, valueId) => {
    return handleRequest(() => characteristicValueService.deleteCharacteristicValue(characteristicId, valueId))
  }

  return {
    // state
    value,
    values,
    loading,
    error,

    // actions
    fetchCharacteristicValue,
    fetchCharacteristicValues,
    createCharacteristicValue,
    renameCharacteristicValue,
    deleteCharacteristicValue
  }
}