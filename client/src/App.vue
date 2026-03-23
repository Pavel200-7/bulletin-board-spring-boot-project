<script setup>
import { onMounted, onUnmounted } from 'vue'
import { useAuth } from './composables/useAuth'
import { useRouter } from 'vue-router'

const { initAuth, isAuthenticated, stopTokenRefreshTimer } = useAuth()
const router = useRouter()

onMounted(() => {
  if (initAuth()) {
    router.push('/home')
  }
})

onUnmounted(() => {
  stopTokenRefreshTimer()
})
</script>

<template>
  <router-view />
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
}
</style>