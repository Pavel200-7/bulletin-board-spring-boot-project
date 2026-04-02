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
        class="message-item"
        :class="{ 
          'message-own': message.senderId === currentUserProfileId,
          'message-own-edit': message.senderId === currentUserProfileId && editingMessageId === message.id
        }"
        @contextmenu="(e) => openContextMenu(e, message)"
      >
        <div class="message-bubble">
          <div v-if="message.type === 'TEXT'" class="message-text">
            <template v-if="editingMessageId === message.id">
              <textarea
                ref="editTextareaRef"
                v-model="editText"
                class="edit-input"
                @keyup.enter.prevent="saveEdit(message.id)"
                @keyup.esc="cancelEdit"
              ></textarea>
              <div class="edit-actions">
                <button @click="saveEdit(message.id)" class="save-btn">✓</button>
                <button @click="cancelEdit" class="cancel-btn">✕</button>
              </div>
            </template>
            <template v-else>
              {{ message.content }}
              <span v-if="message.updated" class="edited-badge">(изменено)</span>
            </template>
          </div>
          <div v-else-if="message.type === 'IMAGE'" class="message-image">
            <img :src="getImageUrl(message.content)" alt="Изображение" @click="openImage(message.content)" />
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

    <!-- Контекстное меню -->
    <MessageActions
      :show="contextMenu.show"
      :position="contextMenu.position"
      @edit="handleEditMessage"
      @delete="handleDeleteMessage"
      @close="closeContextMenu"
    />

    <!-- Модальное окно редактирования -->
    <EditMessageModal
      :show="showEditModal"
      :current-text="editText"
      @save="confirmEdit"
      @close="closeEditModal"
    />

    <!-- Модальное окно подтверждения удаления -->
    <DeleteConfirmModal
      :show="showDeleteModal"
      @confirm="confirmDelete"
      @close="closeDeleteModal"
    />
  </div>
</template>

<script setup>
import { ref, watch, nextTick, onMounted } from 'vue'
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
  }
})

const emit = defineEmits(['load-older', 'load-newer', 'edit-message', 'delete-message'])

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

const openContextMenu = (event, message) => {
  event.preventDefault()
  
  // Только свои сообщения можно редактировать/удалять
  if (message.senderId !== props.currentUserProfileId) return
  
  selectedMessage.value = message
  console.log(selectedMessage.value)

  contextMenu.value = {
    show: true,
    position: { x: event.clientX, y: event.clientY }
  }
}

const closeContextMenu = () => {
  contextMenu.value.show = false
  // selectedMessage.value = null
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
  console.log('Sending 22222222222222222222222211111111111')
  console.log('Sending newText', newText)
  console.log(selectedMessage.value)
  console.log(newText.trim())


  if (selectedMessage.value && newText.trim()) {
    console.log('Sending 222222222222222222222222')

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

// Закрываем контекстное меню при клике вне его
const handleClickOutside = () => {
  if (contextMenu.value.show) {
    closeContextMenu()
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
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

.message-own-edit {
  opacity: 0.5;
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

.message-text {
  font-size: 0.875rem;
  line-height: 1.4;
  white-space: pre-wrap;
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

.edited-badge {
  font-size: 0.65rem;
  margin-left: 0.25rem;
  opacity: 0.7;
}

.edit-input {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.875rem;
  font-family: inherit;
  resize: vertical;
}

.edit-actions {
  display: flex;
  gap: 0.5rem;
  margin-top: 0.5rem;
}

.save-btn, .cancel-btn {
  padding: 0.25rem 0.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.75rem;
}

.save-btn {
  background: #48bb78;
  color: white;
}

.save-btn:hover {
  background: #38a169;
}

.cancel-btn {
  background: #e2e8f0;
  color: #4a5568;
}

.cancel-btn:hover {
  background: #cbd5e0;
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