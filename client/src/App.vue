<!-- src/App.vue -->
<template>
  <router-view />
</template>

<script setup>
import { onMounted } from 'vue'
import { useAuth } from './composables/useAuth'
import { useRouter } from 'vue-router'

const { initAuth, isAuthenticated } = useAuth()
const router = useRouter()

onMounted(() => {
  console.log('App mounted')  
  console.log('Current path:', router.currentRoute.value.path) 
  console.log('Hash:', window.location.hash) 
  if (initAuth()) {
    console.log('initAuth returned true')  

    router.push('/home')
  }
})
</script>

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