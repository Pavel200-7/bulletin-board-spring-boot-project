<!-- src/views/chat/components/MessagesContainer.vue -->
<template>
  <div class="messages-container" ref="container" @scroll="handleScroll" @contextmenu.prevent>
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
        :data-message-id="message.id"
        class="message-item"
        :class="{ 
          'message-own': message.senderId === currentUserProfileId
        }"
        @contextmenu="(e) => openContextMenu(e, message)"
      >
        <div class="message-bubble">
          <TextMessage
            v-if="message.type === 'TEXT'"
            :message="message"
            :is-own="message.senderId === currentUserProfileId"
            :editing-id="editingMessageId"
            :edit-text="editText"
            @start-edit="startEdit"
            @save-edit="saveEdit"
            @cancel-edit="cancelEdit"
            @update-edit-text="updateEditText"
          />
          
          <ImageMessage
            v-else-if="message.type === 'IMAGE'"
            :message="message"
            :is-own="message.senderId === currentUserProfileId"
          />
        </div>
      </div>
    </div>
    
    <div v-if="loadingNewer" class="loading-indicator bottom">
      <div class="spinner-small"></div>
      <span>Загрузка новых...</span>
    </div>

    <MessageActions
      :show="contextMenu.show"
      :position="contextMenu.position"
      @edit="handleEditMessage"
      @delete="handleDeleteMessage"
      @close="closeContextMenu"
    />

    <EditMessageModal
      :show="showEditModal"
      :current-text="editText"
      @save="confirmEdit"
      @close="closeEditModal"
    />

    <DeleteConfirmModal
      :show="showDeleteModal"
      @confirm="confirmDelete"
      @close="closeDeleteModal"
    />
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted, onBeforeUnmount, watch } from 'vue'
import TextMessage from './messages/TextMessage.vue'
import ImageMessage from './messages/ImageMessage.vue'
import MessageActions from './MessageActions.vue'
import EditMessageModal from './EditMessageModal.vue'
import DeleteConfirmModal from './DeleteConfirmModal.vue'

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
  currentUserProfileId: {
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
  },
  currentLastReadMessageId: {
    type: String,
    default: null
  }
})

const emit = defineEmits([
  'load-older', 
  'load-newer', 
  'edit-message', 
  'delete-message',
  'update-last-read'  // Новое событие для обновления lastRead
])

const container = ref(null)
const isUserAtBottom = ref(true)
const selectedMessage = ref(null)
const editText = ref('')
const editingMessageId = ref(null)
const showEditModal = ref(false)
const showDeleteModal = ref(false)
const contextMenu = ref({
  show: false,
  position: { x: 0, y: 0 }
})

// Intersection Observer для отслеживания видимых сообщений
let observer = null
const lastProcessedMessageId = ref(null)
const updateTimeout = ref(null)

const handleScroll = () => {
  const el = container.value
  if (!el) return
  
  const scrollTop = el.scrollTop
  const scrollHeight = el.scrollHeight
  const clientHeight = el.clientHeight
  
  const isNearTop = scrollTop < 50
  const isNearBottom = scrollHeight - scrollTop - clientHeight < 50
  
  isUserAtBottom.value = isNearBottom
  
  if (isNearTop && props.hasOlder && !props.loadingOlder) {
    emit('load-older')
  }
  
  if (isNearBottom && props.hasNewer && !props.loadingNewer) {
    emit('load-newer')
  }
}

const scrollToBottom = () => {
  if (isUserAtBottom.value && container.value) {
    nextTick(() => {
      container.value.scrollTop = container.value.scrollHeight
    })
  }
}

// Инициализация Intersection Observer для отслеживания видимых сообщений
const initVisibilityObserver = () => {
  if (observer) {
    observer.disconnect()
  }
  
  const options = {
    root: container.value,
    threshold: 0.5 // Сообщение считается видимым, если видно 50%
  }
  
  observer = new IntersectionObserver((entries) => {
    // Находим все видимые сообщения
    const visibleMessages = entries
      .filter(entry => entry.isIntersecting)
      .map(entry => ({
        id: entry.target.dataset.messageId,
        index: props.messages.findIndex(m => m.id === entry.target.dataset.messageId)
      }))
      .filter(item => item.index !== -1)
      .sort((a, b) => a.index - b.index) // Сортируем по индексу
    
    if (visibleMessages.length === 0) return
    
    // Берем последнее видимое сообщение (самое новое из видимых)
    const lastVisible = visibleMessages[visibleMessages.length - 1]
    
    // Проверяем, что это сообщение не обработано недавно
    if (lastProcessedMessageId.value === lastVisible.id) return
    
    // Проверяем, что это сообщение новее текущего lastRead
    if (props.currentLastReadMessageId) {
      const currentIndex = props.messages.findIndex(m => m.id === props.currentLastReadMessageId)
      
      // Если текущий lastRead найден и он новее или равен видимому, не обновляем
      if (currentIndex !== -1 && currentIndex >= lastVisible.index) {
        return
      }
    }
    
    // Дебаунсим обновление, чтобы не спамить запросами
    if (updateTimeout.value) {
      clearTimeout(updateTimeout.value)
    }
    
    updateTimeout.value = setTimeout(() => {
      console.log(`📖 User viewed message: ${lastVisible.id} (index: ${lastVisible.index})`)
      lastProcessedMessageId.value = lastVisible.id
      emit('update-last-read', lastVisible.id)
    }, 500)
    
  }, options)
  
  // Начинаем наблюдение за всеми сообщениями
  observeMessages()
}

// Наблюдение за сообщениями
const observeMessages = () => {
  if (!observer || !container.value) return
  
  const messageElements = container.value.querySelectorAll('.message-item')
  messageElements.forEach(el => {
    observer.observe(el)
  })
}

// При изменении списка сообщений, переинициализируем observer
watch(() => props.messages, () => {
  nextTick(() => {
    if (observer) {
      initVisibilityObserver()
    }
  })
}, { deep: true })

// При изменении currentLastReadMessageId, обновляем lastProcessed
watch(() => props.currentLastReadMessageId, (newId) => {
  if (newId) {
    lastProcessedMessageId.value = newId
  }
})

// Очистка observer при размонтировании
onBeforeUnmount(() => {
  if (observer) {
    observer.disconnect()
    observer = null
  }
  if (updateTimeout.value) {
    clearTimeout(updateTimeout.value)
  }
})

const openContextMenu = (event, message) => {
  event.preventDefault()
  
  if (message.senderId !== props.currentUserProfileId) return
  
  selectedMessage.value = message
  contextMenu.value = {
    show: true,
    position: { x: event.clientX, y: event.clientY }
  }
}

const closeContextMenu = () => {
  contextMenu.value.show = false
}

const handleEditMessage = () => {
  if (!selectedMessage.value) return
  editText.value = selectedMessage.value.content
  showEditModal.value = true
}

const handleDeleteMessage = () => {
  if (!selectedMessage.value) return
  showDeleteModal.value = true
}

const confirmEdit = (newText) => {
  if (selectedMessage.value && newText.trim()) {
    emit('edit-message', {
      messageId: selectedMessage.value.id,
      newText: newText.trim()
    })
  }
  closeEditModal()
}

const closeEditModal = () => {
  showEditModal.value = false
  editText.value = ''
}

const confirmDelete = () => {
  if (selectedMessage.value) {
    emit('delete-message', selectedMessage.value.id)
  }
  closeDeleteModal()
}

const closeDeleteModal = () => {
  showDeleteModal.value = false
}

const startEdit = (messageId, content) => {
  editingMessageId.value = messageId
  editText.value = content
}

const saveEdit = (messageId) => {
  if (editText.value.trim()) {
    emit('edit-message', {
      messageId: messageId,
      newText: editText.value.trim()
    })
  }
  cancelEdit()
}

const cancelEdit = () => {
  editingMessageId.value = null
  editText.value = ''
}

const updateEditText = (text) => {
  editText.value = text
}

const handleClickOutside = () => {
  if (contextMenu.value.show) {
    closeContextMenu()
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
  initVisibilityObserver()
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
  word-break: break-word;
}

.message-own .message-bubble {
  background: #667eea;
  color: white;
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