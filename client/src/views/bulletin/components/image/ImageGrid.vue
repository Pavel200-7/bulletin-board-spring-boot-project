<!-- src/views/bulletin/components/image/ImageGrid.vue -->
<template>
  <div v-if="images.length > 0" class="images-grid">
    <ImageItem
      v-for="(image, index) in images"
      :key="image.minioId || index"
      :image-url="getImageUrl(image)"
      :is-main="image.main"
      :total-images="images.length"
      @set-main="$emit('set-main', image)"
      @delete="$emit('delete', image)"
      @error="$emit('error', image)"
    />
  </div>
</template>

<script setup>
import ImageItem from './ImageItem.vue'

defineProps({
  images: {
    type: Array,
    required: true
  },
  getImageUrl: {
    type: Function,
    required: true
  }
})

defineEmits(['set-main', 'delete', 'error'])
</script>

<style scoped>
.images-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 1rem;
  margin-top: 0.5rem;
}
</style>