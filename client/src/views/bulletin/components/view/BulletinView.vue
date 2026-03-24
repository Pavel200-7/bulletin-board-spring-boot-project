<!-- src/views/bulletin/components/view/BulletinView.vue -->
<template>
  <div class="bulletin-view">
    <Breadcrumbs :title="bulletin?.title" />
    
    <!-- Состояния -->
    <LoadingState v-if="loading" text="Загрузка объявления..." />
    
    <ErrorState 
      v-else-if="error" 
      :error="error" 
      @retry="$emit('retry')" 
    />
    
    <div v-else-if="!bulletin" class="not-found-container">
      <div class="not-found-icon">🔍</div>
      <h3>Объявление не найдено</h3>
      <p>Возможно, оно было удалено или еще не опубликовано</p>
      <router-link to="/home" class="home-link">Вернуться на главную</router-link>
    </div>

    <div v-else class="content">
      <div class="main-grid">
        <div class="gallery-section">
          <ImageGallery 
            :images="bulletin.images || []" 
            :title="bulletin.title"
            :size="'medium'"
          />
        </div>

        <BulletinInfo
          :title="bulletin.title"
          :price="bulletin.price"
          :state="bulletin.state"
          :owner-id="bulletin.ownerId"
          :characteristics="bulletin.characteristics || []"
          @chat="$emit('chat')"
        />
      </div>

      <BulletinDescription :description="bulletin.description" />
      
      <BulletinMeta
        :created-at="bulletin.createdAt"
        :views="bulletin.views"
        :bulletin-id="bulletin.id"
      />
    </div>
  </div>
</template>

<script setup>
import Breadcrumbs from './Breadcrumbs.vue'
import BulletinInfo from './BulletinInfo.vue'
import BulletinDescription from './BulletinDescription.vue'
import BulletinMeta from './BulletinMeta.vue'
import ImageGallery from '@/views/bulletin/components/image/ImageGallery.vue'
import LoadingState from '@/views/bulletin/components/wigets/LoadingState.vue'
import ErrorState from '@/views/bulletin/components/wigets/ErrorState.vue'

defineProps({
  bulletin: {
    type: Object,
    default: null
  },
  loading: {
    type: Boolean,
    default: false
  },
  error: {
    type: String,
    default: null
  }
})

defineEmits(['retry', 'chat'])
</script>

<style scoped>
.bulletin-view {
  background: #f8f9fa;
  min-height: 100vh;
  padding: 2rem;
}

.content {
  max-width: 1280px;
  margin: 0 auto;
}

.main-grid {
  display: grid;
  grid-template-columns: 0.8fr 1.2fr;
  gap: 2rem;
  margin-bottom: 2rem;
}

@media (max-width: 768px) {
  .main-grid {
    grid-template-columns: 1fr;
  }
}

.gallery-section {
  min-width: 0;
}

.not-found-container {
  text-align: center;
  padding: 4rem;
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  max-width: 1280px;
  margin: 0 auto;
}

.not-found-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.home-link {
  display: inline-block;
  margin-top: 1rem;
  padding: 0.5rem 1rem;
  background: #667eea;
  color: white;
  text-decoration: none;
  border-radius: 8px;
}

.home-link:hover {
  background: #5a67d8;
}
</style>