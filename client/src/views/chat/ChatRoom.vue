<!-- src/views/chat/ChatRoom.vue -->
<template>
  <div class="chat-room">
    <ChatHeader
      :chat-name="chatName"
      :status="otherParticipantPublicName"
      :avatar-url="otherParticipantAvatar"
      :profile-id="otherParticipantProfileId"
      @back="goBack"
      @open-profile="openUserModal"
    />

    <MessagesContainer
      ref="messagesContainer"
      :messages="messages"
      :current-user-id="currentUserId"
    />

    <ChatInput
      v-model="messageText"
      @send="sendMessage"
      :disabled="sending"
    />

    <UserProfileModal
      v-if="otherParticipantProfileId"
      :show="showUserModal"
      :profile-id="otherParticipantProfileId"
      :contact-name="chatName"
      :profile-data="otherParticipantProfile"
      @close="showUserModal = false"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useChat } from '@/composables/useChat'
import { useTextMessage } from '@/composables/useTextMessage'
import { useProfile } from '@/composables/useProfile'
import { useContact } from '@/composables/useContact'
import { useAuth } from '@/composables/useAuth'
import ChatHeader from './components/ChatHeader.vue'
import MessagesContainer from './components/MessagesContainer.vue'
import ChatInput from './components/ChatInput.vue'
import UserProfileModal from './components/modals/UserProfileModal.vue'

const route = useRoute()
const router = useRouter()
const { getUserId } = useAuth()
const { 
  fetchChat, 
  fetchUnreadCount,
  loadMessagesAroundLastRead,
  messages, 
  currentChat 
} = useChat()
const { sendMessage: sendTextMessage, sending } = useTextMessage()
const { fetchProfile, profile: otherProfile } = useProfile()
const { fetchContactByProfileId, currentContact } = useContact()
const { fetchMyProfile, profile: myProfile } = useProfile()

const chatId = route.params.id
const messageText = ref('')
const loadingMessages = ref(false)
const otherParticipant = ref(null)
const otherParticipantProfileId = ref(null)
const otherParticipantProfile = ref(null)
const chatName = ref('')
const otherParticipantAvatar = ref(null)
const otherParticipantPublicName = ref('')
const showUserModal = ref(false)
const currentUserId = ref(null)

const getImageUrl = (imageId) => {
  if (!imageId) return null
  const MINIO_URL = import.meta.env.VITE_MINIO_URL || 'http://localhost:9001'
  const BUCKET = import.meta.env.VITE_MINIO_BUCKET || 'bulletins'
  return `${MINIO_URL}/${BUCKET}/${imageId}`
}

const findOtherParticipant = (participants) => {
  if (!myProfile.value || !participants?.length) return null
  return participants.find(p => p.profileId !== myProfile.value.id)
}

const loadChatData = async () => {
  try {
    loadingMessages.value = true
    await fetchMyProfile()
    currentUserId.value = getUserId()
    
    await fetchChat(chatId)
    await fetchUnreadCount(chatId)
    await loadMessagesAroundLastRead(chatId)
    
    if (!currentChat.value) return
    
    const participants = currentChat.value.participantResponses || []
    const other = findOtherParticipant(participants)
    
    if (other) {
      otherParticipant.value = other
      otherParticipantProfileId.value = other.profileId
      
      await fetchProfile(other.profileId)
      if (otherProfile.value) {
        otherParticipantProfile.value = otherProfile.value
        otherParticipantAvatar.value = getImageUrl(otherProfile.value.imageId)
        otherParticipantPublicName.value = otherProfile.value.publicName || 'Пользователь'
        chatName.value = otherProfile.value.publicName || 'Пользователь'
      }
      
      try {
        await fetchContactByProfileId(other.profileId)
        if (currentContact.value?.contactName) {
          chatName.value = currentContact.value.contactName
        }
      } catch (err) {
        console.log('Контакт не найден, используем имя профиля')
      }
    }
  } catch (err) {
    console.error('Ошибка загрузки чата:', err)
  } finally {
    loadingMessages.value = false
  }
}

const goBack = () => {
  router.push('/chat/contacts')
}

const openUserModal = () => {
  showUserModal.value = true
}

const sendMessage = async (text) => {
  if (!text.trim() || sending.value) return
  
  try {
    await sendTextMessage(chatId, text)
    messageText.value = ''
    await loadMessagesAroundLastRead(chatId)
  } catch (err) {
    console.error('Ошибка отправки сообщения:', err)
    alert('Не удалось отправить сообщение')
  }
}

const messagesContainer = ref(null)

onMounted(() => {
  if (chatId) {
    loadChatData()
  }
})
</script>

<style scoped>
.chat-room {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 120px);
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}
</style>