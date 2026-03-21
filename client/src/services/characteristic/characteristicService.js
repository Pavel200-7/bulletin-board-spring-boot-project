// src/services/characteristic/characteristicService.js
import apiClient from '@/utils/apiClient'

/**
 * Сервис для работы с характеристиками объявлений
 */
export const characteristicService = {
  // ========== ПОЛУЧЕНИЕ ==========

  /**
   * Получить характеристику по ID
   */
  getCharacteristic(id) {
    return apiClient.get(`/characteristic/${id}`)
  },

  /**
   * Получить все характеристики категории
   * @param {string} categoryId - ID категории
   */
  getCategoryCharacteristics(categoryId) {
    return apiClient.get(`/category/${categoryId}/characteristic`)
  },

  // ========== СОЗДАНИЕ ==========

  /**
   * Создать характеристику для категории
   * @param {string} categoryId - ID категории
   * @param {string} name - название характеристики
   */
  createCharacteristic(categoryId, name) {
    console.log(111);
    console.log(categoryId);

    return apiClient.post(`/category/${categoryId}/characteristic`, { categoryId, name })
  },

  // ========== ИЗМЕНЕНИЕ ==========

  /**
   * Переименовать характеристику
   * @param {string} id - ID характеристики
   * @param {string} name - новое название
   */
  renameCharacteristic(id, name) {
    return apiClient.put('/characteristic/name', { id, name })
  },

  // ========== УДАЛЕНИЕ ==========

  /**
   * Удалить характеристику у категории
   * @param {string} categoryId - ID категории
   * @param {string} characteristicId - ID характеристики
   */
  deleteCharacteristic(categoryId, characteristicId) {
    return apiClient.delete(`/category/${categoryId}/characteristic/${characteristicId}`)
  }
}