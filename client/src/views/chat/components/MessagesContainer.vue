<!-- src/views/chat/components/MessagesContainer.vue -->
<template>
  <div class="messages-container" ref="container" @scroll="handleScroll">
    <!-- Индикатор загрузки старых сообщений -->
    <div v-if="loadingOlder" class="loading-indicator top">
      <div class="spinner-small"></div>
      <span>Загрузка...</span>
    </div>
    
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>Загрузка сообщений...</p>
    </div>
    
    <div v-else-if="messages.length === 0" class="empty-state">
      <div class="empty-icon">💬</div>
      <p>Нет сообщений</p>
      <p class="empty-hint">Напишите первое сообщение</p>
    </div>
    
    <div v-else class="messages-list">
      <div
        v-for="message in messages"
        :key="message.id"
        class="message-item"
        :class="{ 'message-own': message.senderId === currentUserId }"
      >
        <div class="message-bubble">
          <div v-if="message.type === 'TEXT'" class="message-text">
            {{ message.content }}
          </div>
          <div v-else-if="message.type === 'IMAGE'" class="message-image">
            <img :src="getImageUrl(message.content)" alt="Изображение" />
          </div>
          <div class="message-time">
            {{ formatTime(message.createdAt) }}
          </div>
        </div>
      </div>
    </div>
    
    <div v-if="loadingNewer" class="loading-indicator bottom">
      <div class="spinner-small"></div>
      <span>Загрузка новых...</span>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick, onMounted } from 'vue'

const props = defineProps({
  messages: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  loadingOlder: {
    type: Boolean,
    default: false
  },
  loadingNewer: {
    type: Boolean,
    default: false
  },
  currentUserId: {
    type: String,
    default: null
  },
  hasOlder: {
    type: Boolean,
    default: true
  },
  hasNewer: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['load-older', 'load-newer'])

const container = ref(null)
let previousScrollHeight = 0
let previousScrollTop = 0
let isFirstLoad = true
let resizeObserver = null
let animationFrameId = null

const getImageUrl = (imageId) => {
  if (!imageId) return null
  const MINIO_URL = import.meta.env.VITE_MINIO_URL || 'http://localhost:9001'
  const BUCKET = import.meta.env.VITE_MINIO_BUCKET || 'bulletins'
  return `${MINIO_URL}/${BUCKET}/${imageId}`
}

const formatTime = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleTimeString('ru-RU', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

const smoothScrollTo = (targetPosition, duration = 300) => {
  if (!container.value) return
  
  const element = container.value
  const startPosition = element.scrollTop
  const distance = targetPosition - startPosition
  const startTime = performance.now()
  
  if (animationFrameId) {
    cancelAnimationFrame(animationFrameId)
  }
  
  const animation = (currentTime) => {
    const elapsed = currentTime - startTime
    const progress = Math.min(elapsed / duration, 1)
    
    // easeOutCubic для плавного замедления в конце
    const easeProgress = 1 - Math.pow(1 - progress, 3)
    const newPosition = startPosition + distance * easeProgress
    
    element.scrollTop = newPosition
    
    if (progress < 1) {
      animationFrameId = requestAnimationFrame(animation)
    } else {
      animationFrameId = null
    }
  }
  
  animationFrameId = requestAnimationFrame(animation)
}

const scrollToLastMessages = () => {
  if (!container.value) return
  
  const el = container.value
  
  const performScroll = () => {
    const scrollHeight = el.scrollHeight
    const clientHeight = el.clientHeight
    
    if (scrollHeight > clientHeight) {
      const targetScrollTop = scrollHeight - clientHeight
      const offset = 100
      const finalPosition = Math.max(0, targetScrollTop - offset)
      
      // Плавная анимация прокрутки
      smoothScrollTo(finalPosition, 400)
    
      
      if (resizeObserver) {
        resizeObserver.disconnect()
        resizeObserver = null
      }
    }
  }
  
  performScroll()
  
  if (window.ResizeObserver && !resizeObserver) {
    resizeObserver = new ResizeObserver(() => {
      performScroll()
    })
    resizeObserver.observe(el)
    
    setTimeout(() => {
      if (resizeObserver) {
        resizeObserver.disconnect()
        resizeObserver = null
      }
    }, 1000)
  }
}

const handleScroll = () => {
  const el = container.value
  if (!el) return
  
  if (animationFrameId) return
  
  const scrollTop = el.scrollTop
  const scrollHeight = el.scrollHeight
  const clientHeight = el.clientHeight
  
  const isNearTop = scrollTop < 50
  const isNearBottom = scrollHeight - scrollTop - clientHeight < 50
  
  // Загрузка старых сообщений при скролле вверх
  if (isNearTop && props.hasOlder && !props.loadingOlder) {
    previousScrollHeight = scrollHeight
    previousScrollTop = scrollTop
    emit('load-older')
  }
  
  // Загрузка новых сообщений при скролле вниз (если есть непрочитанные)
  if (isNearBottom && props.hasNewer && !props.loadingNewer) {
    emit('load-newer')
  }
}

const scrollToBottom = () => {
  if (!container.value) return
  
  nextTick(() => {
    const targetScrollTop = container.value.scrollHeight
    smoothScrollTo(targetScrollTop, 300)
  })
}

const preserveScrollPosition = () => {
  if (!container.value) return
  
  const newScrollHeight = container.value.scrollHeight
  const heightDiff = newScrollHeight - previousScrollHeight
  
  if (heightDiff > 0) {
    const newScrollTop = previousScrollTop + heightDiff
    container.value.scrollTop = newScrollTop
  }
  
  previousScrollHeight = newScrollHeight
  previousScrollTop = container.value.scrollTop
}

const openImage = (imageId) => {
  window.open(getImageUrl(imageId), '_blank')
}

// При добавлении сообщений
watch(() => props.messages.length, (newLen, oldLen) => {
  if (newLen <= oldLen) return
  
  if (isFirstLoad) {
    isFirstLoad = false
    scrollToLastMessages()
    return
  }
  
  if (previousScrollHeight > 0) {
    preserveScrollPosition()
    nextTick(() => {
      previousScrollHeight = 0
      previousScrollTop = 0
    })
  }
})

onMounted(() => {
  if (container.value) {
    previousScrollHeight = container.value.scrollHeight
    previousScrollTop = container.value.scrollTop
  }
})

defineExpose({
  scrollToBottom
})
</script>

<style scoped>
.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 1rem;
  background: #f8f9fa;
  position: relative;
  scroll-behavior: auto; /* Отключаем стандартную анимацию, используем свою */
}

.empty-state {
  text-align: center;
  padding: 2rem;
  color: #a0aec0;
  min-height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.empty-hint {
  font-size: 0.75rem;
  margin-top: 0.5rem;
}

.messages-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.message-item {
  display: flex;
  margin-bottom: 0.5rem;
}

.message-own {
  justify-content: flex-end;
}

.message-bubble {
  max-width: 70%;
  padding: 0.5rem 0.75rem;
  border-radius: 18px;
  background: white;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.message-own .message-bubble {
  background: #667eea;
  color: white;
}

.message-text {
  font-size: 0.875rem;
  line-height: 1.4;
  word-break: break-word;
}

.message-image img {
  max-width: 200px;
  max-height: 200px;
  border-radius: 8px;
  cursor: pointer;
}

.message-time {
  font-size: 0.65rem;
  color: #a0aec0;
  margin-top: 0.25rem;
  text-align: right;
}

.message-own .message-time {
  color: rgba(255, 255, 255, 0.7);
}

.loading-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.5rem;
  color: #a0aec0;
  font-size: 0.75rem;
}

.loading-indicator.top {
  margin-bottom: 0.5rem;
}

.loading-indicator.bottom {
  margin-top: 0.5rem;
}

.spinner-small {
  width: 16px;
  height: 16px;
  border: 2px solid #e2e8f0;
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>