// src/composables/useTradeAccount.js
import { ref } from 'vue'
import { tradeAccountService } from '@/services/tradeAccount/tradeAccountService'

export function useTradeAccount() {
  const account = ref(null)
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

  const fetchTradeAccount = async (id) => {
    const response = await handleRequest(() => tradeAccountService.getTradeAccount(id))
    account.value = response.data?.tradeAccountResponse || response.data
    return response
  }

  // ========== ОСНОВНЫЕ ДАННЫЕ ==========

  const updateName = async (name) => {
    const response = await handleRequest(() => tradeAccountService.changeName(name))
    account.value = response.data?.tradeAccountResponse || response.data
    return response
  }

  const updatePhone = async (phone) => {
    const response = await handleRequest(() => tradeAccountService.changePhone(phone))
    account.value = response.data?.tradeAccountResponse || response.data
    return response
  }

  const updateContacts = async (contacts) => {
    const response = await handleRequest(() => tradeAccountService.changeContacts(contacts))
    account.value = response.data?.tradeAccountResponse || response.data
    return response
  }

  const updateDescription = async (description) => {
    const response = await handleRequest(() => tradeAccountService.changeDescription(description))
    account.value = response.data?.tradeAccountResponse || response.data
    return response
  }

  // ========== ЛОКАЦИЯ ==========

  const setApproximateLocation = async (locationData) => {
    const response = await handleRequest(() => tradeAccountService.setApproximateLocation(locationData))
    account.value = response.data?.tradeAccountResponse || response.data
    return response
  }

  const setExactLocation = async (locationData) => {
    const response = await handleRequest(() => tradeAccountService.setExactLocation(locationData))
    account.value = response.data?.tradeAccountResponse || response.data
    return response
  }

  // ========== ВЕРИФИКАЦИЯ ==========

  const approveAccount = async () => {
    const response = await handleRequest(() => tradeAccountService.approveAccount())
    account.value = response.data?.tradeAccountResponse || response.data
    return response
  }

  return {
    // state
    account,
    loading,
    error,

    // actions
    fetchTradeAccount,
    updateName,
    updatePhone,
    updateContacts,
    updateDescription,
    setApproximateLocation,
    setExactLocation,
    approveAccount
  }
}