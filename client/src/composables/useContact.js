// src/composables/useContact.js
import { ref } from 'vue'
import { contactService } from '@/services/contact/contactService'

export function useContact() {
  const contacts = ref([])
  const currentContact = ref(null)
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
   * Загрузить список контактов
   */
  const fetchContacts = async () => {
    const response = await handleRequest(() => contactService.getMyContacts())
    contacts.value = response.data?.contacts || response.data || []
    return response
  }

  /**
   * Получить контакт по ID
   * @param {string} contactId - ID контакта
   */
  const fetchContactById = async (contactId) => {
    const response = await handleRequest(() => contactService.getContactById(contactId))
    currentContact.value = response.data?.contactResponse || response.data
    return response
  }

  /**
   * Получить контакт по ID профиля
   * @param {string} profileId - ID профиля
   */
  const fetchContactByProfileId = async (profileId) => {
    const response = await handleRequest(() => contactService.getContactByProfileId(profileId))
    currentContact.value = response.data?.contactResponse || response.data
    return response
  }

  /**
   * Добавить пользователя в контакты
   * @param {string} profileId - ID профиля для добавления
   */
  const addContact = async (profileId) => {
    const response = await handleRequest(() => contactService.createContact(profileId))
    
    const newContact = response.data?.contact || response.data
    if (newContact) {
      contacts.value = [...contacts.value, newContact]
    }
    
    return response
  }

  /**
   * Переименовать контакт
   * @param {string} contactId - ID контакта
   * @param {string} newName - новое имя
   */
  const renameContact = async (contactId, newName) => {
    const response = await handleRequest(() => contactService.changeContactName(contactId, newName))
    
    const updatedContact = response.data?.contact || response.data
    if (updatedContact) {
      contacts.value = contacts.value.map(c => 
        c.id === contactId ? updatedContact : c
      )
    }
    
    return response
  }

  return {
    // state
    contacts,
    currentContact,
    loading,
    error,

    // actions
    fetchContacts,
    fetchContactById,
    fetchContactByProfileId,
    addContact,
    renameContact
  }
}