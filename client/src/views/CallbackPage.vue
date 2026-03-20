<!-- src/views/CallbackPage.vue -->
<script setup>
import { onMounted } from 'vue'
import { useAuth } from '@/composables/useAuth'
import { useRouter } from 'vue-router'

const { handleCallback } = useAuth()
const router = useRouter()

onMounted(() => {
  const success = handleCallback()  
  if (success) {
    router.push('/home')
  } else {
    router.push('/')
  }
})
</script>

<template>
  <div>Processing login...</div>
</template>