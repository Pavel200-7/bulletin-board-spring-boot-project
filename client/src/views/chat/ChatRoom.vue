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
      :loading="loadingMessages"
      :loading-older="loadingOlder"
      :loading-newer="loadingNewer"
      :current-user-profile-id="currentUserProfileId"
      :has-older="hasOlder"
      :has-newer="hasNewer"
      :current-last-read-message-id="currentUserParticipantLastSeemMessageId"
      @load-older="() => loadOlderMessages(chatId)"
      @load-newer="() => loadNewerMessages(chatId)"
      @edit-message="handleEditMessage"
      @delete-message="handleDeleteMessage"
      @update-last-read="handleUpdateLastRead"
    />

    <ChatInput
      v-model="messageText"
      :chat-id="chatId"
      :disabled="sending"
      @send="sendMessage"
      @send-image="handleSendImage"
    />

    <UserProfileModal
      v-if="otherParticipantProfileId"
      :show="showUserModal"
      :profile-id="otherParticipantProfileId"
      :contact-name="chatName"
      :profile-data="otherParticipantProfile"
      :contact-id="currentContactId"
      @close="showUserModal = false"
      @name-updated="handleContactNameUpdated"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useChat } from '@/composables/chat/useChat'
import { useTextMessage } from '@/composables/chat/useTextMessage'
import { useProfile } from '@/composables/useProfile'
import { useContact } from '@/composables/useContact'
import { useAuth } from '@/composables/useAuth'
import { useSubscription } from '@/composables/chat/useSubscription'
import { useTextMessageWebSocket } from '@/composables/chat/useTextMessageWebSocket'
import { useImageMessageWebSocket } from '@/composables/chat/useImageMessageWebSocket'
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
  loadOlderMessages,
  loadNewerMessages,
  messages, 
  loadingOlder,
  loadingNewer,
  hasOlder,
  hasNewer,
  currentChat,
  addMessage,
  removeMessage,
  updateMessage,
  resetHasNewer,
  setLastRead
} = useChat()
const { sendMessage: sendTextMessage, sending } = useTextMessage()
const { fetchProfile, profile: otherProfile } = useProfile()
const { fetchContactByProfileId, currentContact } = useContact()
const { fetchMyProfile, profile: myProfile } = useProfile()
const { 
  connect: wsConnect, 
  disconnect: wsDisconnect, 
  subscribeToChat, 
  subscribeToReplies,
  unsubscribeFromChat,
  isConnected: wsIsConnected,
  connected: wsConnected
} = useSubscription()
const { 
  sendMessage: wsSendMessage,
  updateMessage: wsUpdateMessage, 
  deleteMessage: wsDeleteMessage 
} = useTextMessageWebSocket()
const { 
  sendImageMessage: wsSendImageMessage
} = useImageMessageWebSocket()

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
const currentUserProfileId = ref(null)
const currentUserParticipant = ref(null)
const currentUserParticipantLastSeemMessageId = ref(null)
const currentContactId = ref(null)

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

const findMeParticipant = (participants) => {
  if (!myProfile.value || !participants?.length) return null
  return participants.find(p => p.profileId === myProfile.value.id)
}

const loadChatData = async () => {
  try {
    loadingMessages.value = true
    console.log('📥 Loading chat data for chat:', chatId)
    
    await fetchMyProfile()

    currentUserId.value = getUserId()
    console.log('Current user ID:', currentUserId.value)

    currentUserProfileId.value = myProfile.value?.id
    console.log('Current user Profile ID:', currentUserProfileId.value)

    
    await fetchChat(chatId)
    await fetchUnreadCount(chatId)
    await loadMessagesAroundLastRead(chatId)
    
    console.log('After loadMessagesAroundLastRead:', {
      hasOlder: hasOlder.value,
      hasNewer: hasNewer.value,
      messagesCount: messages.value.length
    })
    
    if (!currentChat.value) return

    
    const participants = currentChat.value.participantResponses || []
    currentUserParticipant.value = findMeParticipant(participants)
    currentUserParticipantLastSeemMessageId.value = currentUserParticipant.value?.lastReadMessageId

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
        console.log('Other participant:', { name: chatName.value, id: otherParticipantProfileId.value })
      }
      
      try {
        await fetchContactByProfileId(other.profileId)
        if (currentContact.value) {
          currentContactId.value = currentContact.value.id
          if (currentContact.value?.contactName) {
            chatName.value = currentContact.value.contactName
          }
        }
      } catch (err) {
        console.log('Contact not found, using profile name')
        currentContactId.value = null
      }
    }
    
  } catch (err) {
    console.error('❌ Error loading chat data:', err)
  } finally {
    loadingMessages.value = false
  }
}

const setupWebSocket = async () => {
  try {
    console.log('🔌 Setting up WebSocket connection for chat:', chatId)
    
    const connected = await wsConnect()
    if (!connected) {
      console.warn('⚠️ WebSocket connection failed, will use REST fallback')
      return
    }
    console.log('✅ WebSocket connected')
    
    await subscribeToReplies((reply) => {
      console.log('📨 Server reply:', reply)
      if (reply.error) {
        console.error('Server error:', reply.error)
      }
      if (reply.chatMessageResponse) {
        console.log('Message sent successfully:', reply.chatMessageResponse)
      }
    })
    console.log('✅ Subscribed to replies')
    
    await subscribeToChat(chatId, {
      onMessageCreated: onMessageCreated,
      onMessageUpdated: (update) => {
        console.log('✏️ Message update via WebSocket:', update)
        updateMessage(update.id, { 
          content: update.content,
          updated: update.updated 
        })
      },
      onMessageDeleted: (deleteMsg) => {
        console.log('🗑️ Message delete via WebSocket:', deleteMsg)
        removeMessage(deleteMsg.messageId)
      }
    })
    console.log('✅ Subscribed to chat events for:', chatId)
    
  } catch (err) {
    console.error('❌ Failed to setup WebSocket:', err)
  }
}

const onMessageCreated = async (message) => {
  console.log('💬 New message via WebSocket:', message)
  resetHasNewer()
  await loadNewerMessages(chatId)
  
  if (message.senderId !== currentUserProfileId.value && document.hasFocus()) {
    setTimeout(() => {
      handleUpdateLastRead(message.id)
    }, 100)
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
  
  console.log('📤 Sending message via WebSocket:', { chatId, text })
  
  try {
    const sent = wsSendMessage(chatId, text)
    
    if (sent && wsIsConnected()) {
      console.log('✅ Message sent via WebSocket')
      messageText.value = ''
    } else {
      console.warn('⚠️ WebSocket send failed, falling back to REST')
      await sendTextMessage(chatId, text)
      messageText.value = ''
      await loadMessagesAroundLastRead(chatId)
    }
    
  } catch (err) {
    console.error('❌ Error sending message:', err)
    alert('Не удалось отправить сообщение')
  }
}

const handleSendImage = async (imageId) => {
  console.log('📤 Sending image via WebSocket:', { chatId, imageId })
  
  console.log('WebSocket state:', {
    isConnected: wsIsConnected(),
    connected: wsConnected.value,
  })

  try {
    const sent = wsSendImageMessage(chatId, imageId)
    
    if (sent && wsIsConnected()) {
      console.log('✅ Image message sent via WebSocket')
    } else {
      console.warn('⚠️ WebSocket send failed, need REST fallback')
    }
  } catch (err) {
    console.error('❌ Error sending image:', err)
    alert('Не удалось отправить изображение')
  }
}

const handleEditMessage = async ({ messageId, newText }) => {
  try {
    const sent = wsUpdateMessage(chatId, messageId, newText)
    console.log('✅ Message update sent via WebSocket')
    
    if (!sent) {
      console.warn('WebSocket update failed, need REST fallback')
    }
  } catch (err) {
    console.error('Error editing message:', err)
    alert('Не удалось изменить сообщение')
  }
}

const handleDeleteMessage = async (messageId) => {
  try {
    const sent = wsDeleteMessage(chatId, messageId)
    
    if (!sent) {
      console.warn('WebSocket delete failed, need REST fallback')
    }
  } catch (err) {
    console.error('Error deleting message:', err)
    alert('Не удалось удалить сообщение')
  }
}

const handleUpdateLastRead = async (messageId) => {
  if (!messageId) return
  
  if (currentUserParticipantLastSeemMessageId.value === messageId) return
  
  console.log(`📖 Updating last read to: ${messageId}`)
  
  const updated = await setLastRead(
    chatId, 
    messageId, 
    currentUserParticipantLastSeemMessageId.value
  )
  
  if (updated) {
    currentUserParticipantLastSeemMessageId.value = messageId
    console.log(`✅ Last read updated to: ${messageId}`)
  }
}

const handleContactNameUpdated = ({ contactId, newName }) => {
  chatName.value = newName
  console.log(`Contact ${contactId} renamed to: ${newName}`)
}

const messagesContainer = ref(null)

onMounted(async () => {
  if (chatId) {
    console.log('🚀 ChatRoom mounted, chatId:', chatId)
    await loadChatData()
    await setupWebSocket()
  }
})

onUnmounted(() => {
  console.log('🔌 Cleaning up WebSocket subscriptions for chat:', chatId)
  if (chatId) {
    try {
      unsubscribeFromChat(chatId)
    } catch (err) {
      console.warn('Error unsubscribing:', err)
    }
    wsDisconnect()
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