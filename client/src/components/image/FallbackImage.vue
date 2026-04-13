<!-- src/components/image/FallbackImage.vue -->
<template>
  <div class="fallback-image" :class="{ 'error': hasError }">
    <img 
      :src="currentSrc" 
      :alt="alt"
      @error="onError"
      @load="onLoad"
      class="image"
    />
    <div v-if="hasError && showFallbackIcon" class="fallback-icon">
      <span class="icon">📸</span>
      <span class="text">{{ fallbackText }}</span>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  src: {
    type: String,
    required: true
  },
  alt: {
    type: String,
    default: 'Image'
  },
  fallbackSrc: {
    type: String,
    default: '/images/image-placeholder.svg'
  },
  fallbackText: {
    type: String,
    default: 'Нет изображения'
  },
  showFallbackIcon: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['error', 'load'])

const hasError = ref(false)
const currentSrc = ref(props.src)

console.log(props.src)

// Следим за изменением src
watch(() => props.src, (newSrc) => {
  if (newSrc !== currentSrc.value) {
    currentSrc.value = newSrc
    hasError.value = false
  }
})

const onError = () => {
  // Если уже показываем fallback, не делаем ничего
  if (hasError.value) {
    return
  }
  
  // Если текущий src не fallback, переключаемся
  if (currentSrc.value !== props.fallbackSrc) {
    currentSrc.value = props.fallbackSrc
    hasError.value = true
    emit('error')
  } else {
    // Fallback тоже не загрузился
    hasError.value = true
    emit('error')
  }
}

const onLoad = () => {
  emit('load')
}
</script>

<style scoped>
.fallback-image {
  position: relative;
  width: 100%;
  height: 100%;
  background: #f7fafc;
  overflow: hidden;
}

.image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.fallback-image.error .image {
  opacity: 0.3;
}

.fallback-icon {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #a0aec0;
  pointer-events: none;
  background: rgba(247, 250, 252, 0.9);
}

.icon {
  font-size: 2rem;
  margin-bottom: 0.25rem;
}

.text {
  font-size: 0.7rem;
  text-align: center;
  padding: 0 0.5rem;
}
</style>