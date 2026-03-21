// src/services/category/categoryService.js
import apiClient from '@/utils/apiClient'

/**
 * Сервис для работы с категориями
 */
export const categoryService = {
  // ========== ПОЛУЧЕНИЕ ==========

  /**
   * Получить категорию по ID
   */
  getCategory(id) {
    return apiClient.get(`/category/${id}`)
  },

  /**
   * Получить все корневые категории
   */
  getRootCategories() {
    return apiClient.get('/category/root')
  },

  /**
   * Получить категорию с её родственниками (предки + прямые дети)
   */
  getCategoryWithFamily(id) {
    return apiClient.get(`/category/family/${id}`)
  },

  /**
   * Получить категорию с прямыми детьми
   * @param {string} id - UUID категории
   */
  getCategoryWithChildren(id) {
    return apiClient.get(`/category/${id}/with-children`)
  },

  // ========== СОЗДАНИЕ ==========

  /**
   * Создать корневую категорию (без родителя)
   * @param {string} name - название категории
   */
  createRootCategory(name) {
    return apiClient.post('/category/root', { name })
  },

  /**
   * Создать дочернюю категорию (может иметь своих детей)
   * @param {string} parentId - ID родительской категории
   * @param {string} name - название категории
   */
  createChildCategory(parentId, name) {
    return apiClient.post('/category/child', { parentId, name })
  },

  /**
   * Создать листовую категорию (не может иметь детей)
   * @param {string} parentId - ID родительской категории
   * @param {string} name - название категории
   */
  createLeafCategory(parentId, name) {
    return apiClient.post('/category/leafy-child', { parentId, name })
  },

  // ========== ИЗМЕНЕНИЕ ==========

  /**
   * Переименовать категорию
   * @param {string} id - ID категории
   * @param {string} name - новое название
   */
  renameCategory(id, name) {
    return apiClient.put('/category/name', { id, name })
  },

  // ========== УДАЛЕНИЕ ==========

  /**
   * Удалить дочернюю категорию
   * @param {string} parentId - ID родительской категории
   * @param {string} childId - ID удаляемой категории
   */
  deleteChildCategory(parentId, childId) {
    return apiClient.delete(`/category/${parentId}/children/${childId}`)
  },

  /**
   * Удалить корневую категорию
   * @param {string} id - ID корневой категории
   */
  deleteRootCategory(id) {
    return apiClient.delete(`/category/root/${id}`)
  }
}