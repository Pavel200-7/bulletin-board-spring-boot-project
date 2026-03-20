// src/composables/useProfile.js
import { ref } from 'vue'
import { profileService } from '@/services/profile/profileService'

export function useProfile() {
  const profile = ref(null)
  const profiles = ref([])
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

  /**
   * Загрузить профиль по ID
   */
  const fetchProfile = async (id) => {
    const response = await handleRequest(() => profileService.getProfile(id))
    profile.value = response.data?.profileResponse || response.data
    return response
  }

  /**
   * Загрузить профиль по ID пользователя
   */
  const fetchProfileByUserId = async (userId) => {
    const response = await handleRequest(() => profileService.getProfileByUserId(userId))
    profile.value = response.data?.profileResponse || response.data
    return response
  }

  /**
   * Поиск профилей с пагинацией
   */
  const searchProfiles = async ({ page = 0, size = 20, criteria = {} } = {}) => {
    const response = await handleRequest(() => profileService.searchProfiles({ page, size, criteria }))
    
    const data = response.data
    profiles.value = data.page?.content || data.content || []
    pagination.value = {
      page,
      size,
      totalPages: data.page?.totalPages || data.totalPages,
      totalElements: data.page?.totalElements || data.totalElements
    }
    return response
  }

  // ========== ИЗМЕНЕНИЕ ==========

  /**
   * Обновить публичное имя
   */
  const updatePublicName = async (name) => {
    const response = await handleRequest(() => profileService.changePublicName(name))
    profile.value = response.data?.profileResponse || response.data
    return response
  }

  /**
   * Обновить описание
   */
  const updateDescription = async (description) => {
    const response = await handleRequest(() => profileService.changeDescription(description))
    profile.value = response.data?.profileResponse || response.data
    return response
  }

  return {
    // state
    profile,
    profiles,
    loading,
    error,
    pagination,

    // actions
    fetchProfile,
    fetchProfileByUserId,
    searchProfiles,
    updatePublicName,
    updateDescription
  }
}