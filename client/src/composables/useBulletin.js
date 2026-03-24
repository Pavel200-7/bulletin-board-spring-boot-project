// src/composables/useBulletin.js
import { ref } from 'vue'
import { bulletinService } from '@/services/bulletin/bulletinService'

export function useBulletin() {
  const bulletin = ref(null)
  const bulletins = ref([])
  const loading = ref(false)
  const error = ref(null)
  const pagination = ref({})

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

  const fetchPublicBulletin = async (id) => {
    const response = await handleRequest(() => bulletinService.getPublicBulletin(id))
    bulletin.value = response.data?.bulletinResponse || response.data
    return response
  }

  const fetchEditableBulletin = async (id) => {
    const response = await handleRequest(() => bulletinService.getEditableBulletin(id))
    bulletin.value = response.data?.bulletinResponse || response.data
    return response
  }

  const fetchPublishedBulletins = async (params = {}) => {
    const response = await handleRequest(() => bulletinService.getPublishedBulletins(params))
    const data = response.data
    bulletins.value = data.content || []
    pagination.value = {
      page: params.page || 0,
      size: params.size || 20,
      totalPages: data.totalPages,
      totalElements: data.totalElements
    }
    return response
  }

  // ========== МОИ ОБЪЯВЛЕНИЯ ==========

  const fetchMyBulletins = async ({ page = 0, size = 20, state = null, title = null } = {}) => {
    const response = await handleRequest(() => bulletinService.getMyBulletins({ page, size, state, title }))
    
    const data = response.data
    bulletins.value = data.page?.content || data.content || []
    pagination.value = {
      page,
      size,
      totalPages: data.page?.totalPages || data.totalPages,
      totalElements: data.page?.totalElements || data.totalElements
    }
    return response
  }

  const fetchMyDrafts = async (page = 0, size = 20, title = null) => {
    return fetchMyBulletins({ page, size, state: 'MODIFIABLE', title })
  }

  const fetchMyPublished = async (page = 0, size = 20, title = null) => {
    return fetchMyBulletins({ page, size, state: 'PUBLISHED', title })
  }

  const fetchMyCompleted = async (page = 0, size = 20, title = null) => {
    return fetchMyBulletins({ page, size, state: 'COMPLETED', title })
  }

  const fetchMyApproved = async (page = 0, size = 20, title = null) => {
    return fetchMyBulletins({ page, size, state: 'APPROVED', title })
  }

  // ========== СОЗДАНИЕ И РЕДАКТИРОВАНИЕ ==========

  const createDraft = async () => {
    const response = await handleRequest(() => bulletinService.createDraft())
    bulletin.value = response.data?.bulletinResponse || response.data
    return response
  }

  const updateBulletin = async (data) => {  
    const payload = {
      bulletinRequest: data.bulletinRequest
    }
    
    const response = await handleRequest(() => bulletinService.updateBulletin(payload))
    console.log('updateBulletin response:', response.data)
    bulletin.value = response.data?.bulletinResponse || response.data
    return response
  }

  // ========== ИЗМЕНЕНИЕ СТАТУСА ==========

  /**
   * Подтвердить объявление (проверка валидности)
   * @param {string} bulletinId - ID объявления
   */
  const approve = async (bulletinId) => {
    console.log('=== approve вызван ===', bulletinId)
    try {
      const response = await handleRequest(() => bulletinService.approveBulletin(bulletinId))
      console.log('approve response:', response.data)
      return response
    } catch (err) {
      console.error('approve error:', err)
      throw err
    }
  }

  /**
   * Опубликовать объявление (только если оно подтверждено)
   * @param {string} bulletinId - ID объявления
   */
  const publishBulletin = async (bulletinId) => {
    console.log('=== publishBulletin вызван ===', bulletinId)
    try {
      const response = await handleRequest(() => bulletinService.publishBulletin(bulletinId))
      console.log('publishBulletin response:', response.data)
      return response
    } catch (err) {
      console.error('publishBulletin error:', err)
      throw err
    }
  }

  /**
   * Закрыть объявление
   * @param {string} bulletinId - ID объявления
   */
  const closeBulletin = async (bulletinId) => {
    return handleRequest(() => bulletinService.closeBulletin(bulletinId))
  }

  // ========== РАБОТА С ИЗОБРАЖЕНИЯМИ ==========

  const addImage = async (bulletinId, providerImageId) => {
    return handleRequest(() => bulletinService.addImage(bulletinId, providerImageId))
  }

  const removeImage = async (bulletinId, imageId) => {
    return handleRequest(() => bulletinService.removeImage(bulletinId, imageId))
  }

  const setMainImage = async (bulletinId, imageId) => {
    return handleRequest(() => bulletinService.setMainImage(bulletinId, imageId))
  }

  return {
    // state
    bulletin,
    bulletins,
    loading,
    error,
    pagination,

    // actions
    fetchPublicBulletin,
    fetchEditableBulletin,
    fetchPublishedBulletins,
    fetchMyBulletins,
    fetchMyDrafts,
    fetchMyApproved,
    fetchMyPublished,
    fetchMyCompleted,
    createDraft,
    updateBulletin,
    approve,  // переименовано
    publishBulletin,
    closeBulletin,
    addImage,
    removeImage,
    setMainImage
  }
}