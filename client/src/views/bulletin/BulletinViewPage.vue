<!-- src/views/bulletin/BulletinViewPage.vue -->
<template>
  <BulletinView
    :bulletin="bulletin"
    :loading="loading"
    :error="error"
    @retry="loadBulletin"
    @chat="startChat"
  />
</template>

<script setup>
import { onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useBulletin } from '@/composables/useBulletin'
import BulletinView from './components/view/BulletinView.vue'

const route = useRoute()
const { bulletin, loading, error, fetchPublicBulletin } = useBulletin()

const bulletinId = route.params.id

const loadBulletin = async () => {
  if (bulletinId) {
    await fetchPublicBulletin(bulletinId)
  }
}

const startChat = () => {
  alert('Функция чата в разработке')
}

onMounted(() => {
  loadBulletin()
})
</script>