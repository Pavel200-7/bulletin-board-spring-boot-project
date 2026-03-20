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

  const createDraft = async () => {
    const response = await handleRequest(() => bulletinService.createDraft())
    bulletin.value = response.data?.bulletinResponse || response.data
    return response
  }

  const updateBulletin = async (bulletinRequest) => {
    const response = await handleRequest(() => bulletinService.updateBulletin(bulletinRequest))
    bulletin.value = response.data?.bulletinResponse || response.data
    return response
  }

  const approveAndPublish = async (bulletinId) => {
    return handleRequest(() => bulletinService.approveAndPublish(bulletinId))
  }

  const publishBulletin = async (bulletinId) => {
    return handleRequest(() => bulletinService.publishBulletin(bulletinId))
  }

  const closeBulletin = async (bulletinId) => {
    return handleRequest(() => bulletinService.closeBulletin(bulletinId))
  }

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
    createDraft,
    updateBulletin,
    approveAndPublish,
    publishBulletin,
    closeBulletin,
    addImage,
    removeImage,
    setMainImage
  }
}