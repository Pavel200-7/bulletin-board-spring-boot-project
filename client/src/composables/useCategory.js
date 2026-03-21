// src/composables/useCategory.js
import { ref } from 'vue'
import { categoryService } from '@/services/category/categoryService'

export function useCategory() {
  const category = ref(null)
  const categories = ref([])
  const rootCategories = ref([])
  const family = ref(null)
  const categoryWithChildren = ref(null)
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

  const fetchCategory = async (id) => {
    const response = await handleRequest(() => categoryService.getCategory(id))
    category.value = response.data?.categoryResponse || response.data
    return response
  }

  const fetchRootCategories = async () => {
    const response = await handleRequest(() => categoryService.getRootCategories())
    rootCategories.value = response.data?.categoryResponse || response.data || []
    return response
  }

  const fetchCategoryWithFamily = async (id) => {
    const response = await handleRequest(() => categoryService.getCategoryWithFamily(id))
    family.value = response.data?.categoryFamilyResponse || response.data
    return response
  }

  const fetchCategoryWithChildren = async (id) => {
    const response = await handleRequest(() => categoryService.getCategoryWithChildren(id))
    categoryWithChildren.value = response.data?.categoryWithChildrenResponse || response.data
    return response
  }

  // ========== СОЗДАНИЕ ==========

  const createRootCategory = async (name) => {
    const response = await handleRequest(() => categoryService.createRootCategory(name))
    category.value = response.data?.categoryResponse || response.data
    return response
  }

  const createChildCategory = async (parentId, name) => {
    const response = await handleRequest(() => categoryService.createChildCategory(parentId, name))
    category.value = response.data?.categoryResponse || response.data
    return response
  }

  const createLeafCategory = async (parentId, name) => {
    const response = await handleRequest(() => categoryService.createLeafCategory(parentId, name))
    category.value = response.data?.categoryResponse || response.data
    return response
  }

  // ========== ИЗМЕНЕНИЕ ==========

  const renameCategory = async (id, name) => {
    const response = await handleRequest(() => categoryService.renameCategory(id, name))
    category.value = response.data?.categoryResponse || response.data
    return response
  }

  // ========== УДАЛЕНИЕ ==========

  const deleteChildCategory = async (parentId, childId) => {
    return handleRequest(() => categoryService.deleteChildCategory(parentId, childId))
  }

  const deleteRootCategory = async (id) => {
    return handleRequest(() => categoryService.deleteRootCategory(id))
  }

  return {
    // state
    category,
    categories,
    rootCategories,
    family,
    categoryWithChildren,
    loading,
    error,

    // actions
    fetchCategory,
    fetchRootCategories,
    fetchCategoryWithFamily,
    fetchCategoryWithChildren,
    createRootCategory,
    createChildCategory,
    createLeafCategory,
    renameCategory,
    deleteChildCategory,
    deleteRootCategory
  }
}