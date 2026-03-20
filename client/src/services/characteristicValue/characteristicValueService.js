// src/services/characteristicValue/characteristicValueService.js
import apiClient from '@/utils/apiClient'

/**
 * Сервис для работы со значениями характеристик
 */
export const characteristicValueService = {
  // ========== ПОЛУЧЕНИЕ ==========

  /**
   * Получить значение характеристики по ID
   * @param {string} id - ID значения характеристики
   */
  getCharacteristicValue(id) {
    return apiClient.get(`/characteristic-value/${id}`)
  },

  /**
   * Получить все возможные значения для характеристики
   * @param {string} characteristicId - ID характеристики
   */
  getCharacteristicValues(characteristicId) {
    return apiClient.get(`/characteristic/${characteristicId}/characteristic-value`)
  },

  // ========== СОЗДАНИЕ ==========

  /**
   * Создать новое значение для характеристики
   * @param {string} characteristicId - ID характеристики
   * @param {string} name - название значения
   */
  createCharacteristicValue(characteristicId, name) {
    return apiClient.post(`/characteristic/${characteristicId}/characteristic-value`, { name })
  },

  // ========== ИЗМЕНЕНИЕ ==========

  /**
   * Переименовать значение характеристики
   * @param {string} id - ID значения
   * @param {string} name - новое название
   */
  renameCharacteristicValue(id, name) {
    return apiClient.put('/characteristic-value/name', { id, name })
  },

  // ========== УДАЛЕНИЕ ==========

  /**
   * Удалить значение характеристики
   * @param {string} characteristicId - ID характеристики
   * @param {string} valueId - ID удаляемого значения
   */
  deleteCharacteristicValue(characteristicId, valueId) {
    return apiClient.delete(`/characteristic/${characteristicId}/characteristic-value/${valueId}`)
  }
}