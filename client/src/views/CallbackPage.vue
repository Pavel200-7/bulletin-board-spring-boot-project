<!-- src/views/CallbackPage.vue -->
<script setup>
import { onMounted } from 'vue'
import { useAuth } from '@/composables/useAuth'
import { useRouter } from 'vue-router'

const { handleCallback } = useAuth()
const router = useRouter()

onMounted(() => {
  console.log('CallbackPage mounted')  // <-- добавить для отладки
  console.log('window.location.hash:', window.location.hash)  // <-- добавить
  
  const success = handleCallback()
  console.log('handleCallback result:', success)  // <-- добавить
  
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