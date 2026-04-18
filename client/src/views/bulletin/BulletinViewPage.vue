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
import { useRoute, useRouter } from 'vue-router'
import { useBulletin } from '@/composables/useBulletin'
import { useProfile } from '@/composables/useProfile'
import BulletinView from './components/view/BulletinView.vue'

const route = useRoute()
const router = useRouter()
const { bulletin, loading, error, fetchPublicBulletin } = useBulletin()
const { profile, fetchProfileByUserId, loading: profileLoading } = useProfile()

const bulletinId = route.params.id

const loadBulletin = async () => {
  if (bulletinId) {
    await fetchPublicBulletin(bulletinId)
  }
}

const startChat = async () => {
  if (!bulletin.value) {
    alert('Информация об объявлении не загружена')
    return
  }

  const ownerId = bulletin.value.ownerId
  if (!ownerId) {
    alert('Не удалось определить владельца объявления')
    return
  }

  try {
    // Загружаем профиль продавца
    await fetchProfileByUserId(ownerId)
    const sellerProfile = profile.value
    
    if (!sellerProfile || !sellerProfile.publicName) {
      alert('Не удалось найти профиль продавца')
      return
    }

    // Переходим в поиск с параметром поиска по имени продавца
    router.push({
      name: 'chat-search',
      query: { q: sellerProfile.publicName }
    })
  } catch (err) {
    console.error('Ошибка загрузки профиля продавца:', err)
    alert('Не удалось загрузить информацию о продавце')
  }
}

onMounted(() => {
  loadBulletin()
})
</script>