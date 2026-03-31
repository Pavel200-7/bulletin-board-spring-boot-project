// src/services/contact/contactService.js
import apiClient from '@/utils/apiClient'

/**
 * Сервис для работы с контактами
 */
export const contactService = {
  // ========== ПОЛУЧЕНИЕ ==========

  /**
   * Получить контакт по ID
   * @param {string} contactId - ID контакта
   */
  getContactById(contactId) {
    return apiClient.get(`/contact/${contactId}`)
  },

  /**
   * Получить контакт по ID профиля
   * @param {string} profileId - ID профиля
   */
  getContactByProfileId(profileId) {
    return apiClient.get(`/contact/by-profile/${profileId}`)
  },

  /**
   * Получить список контактов текущего пользователя
   */
  getMyContacts() {
    return apiClient.get('/contact')
  },

  // ========== СОЗДАНИЕ ==========

  /**
   * Создать новый контакт (автоматически создает чат)
   * @param {string} profileId - ID профиля, которого добавляем в контакты
   */
  createContact(profileId) {
    return apiClient.post('/contact', { profileId })
  },

  // ========== ИЗМЕНЕНИЕ ==========

  /**
   * Изменить отображаемое имя контакта
   * @param {string} contactId - ID контакта
   * @param {string} newName - новое имя
   */
  changeContactName(contactId, newName) {
    return apiClient.put(`/contact/${contactId}/name`, { newName })
  }
}