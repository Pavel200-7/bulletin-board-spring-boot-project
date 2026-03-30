// src/composables/useContact.js
import { ref } from 'vue'
import { contactService } from '@/services/contact/contactService'

export function useContact() {
  const contacts = ref([])
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
    console.log('Contacts:', response.data)
    contacts.value = response.data?.contacts || response.data || []
    return response
  }

  /**
   * Добавить пользователя в контакты
   * @param {string} profileId - ID профиля для добавления
   */
  const addContact = async (profileId) => {
    const response = await handleRequest(() => contactService.createContact(profileId))
    
    // Добавляем новый контакт в список (если он там уже есть по id)
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
    
    // Обновляем имя в локальном списке
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
    loading,
    error,

    // actions
    fetchContacts,
    addContact,
    renameContact
  }
}