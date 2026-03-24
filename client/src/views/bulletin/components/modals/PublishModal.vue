<!-- src/views/bulletin/components/modals/PublishModal.vue -->
<template>
  <BaseModal
    :show="show"
    title="Публикация объявления"
    confirm-text="Опубликовать"
    confirm-loading-text="Публикация..."
    :confirm-loading="loading"
    @confirm="$emit('confirm')"
    @cancel="$emit('cancel')"
    @close="$emit('close')"
  >
    <template #body>
      <p>Вы уверены, что хотите опубликовать это объявление?</p>
      <p>После публикации оно станет доступно всем пользователям.</p>
    </template>
  </BaseModal>
</template>

<script setup>
import BaseModal from './BaseModal.vue'

defineProps({
  show: {
    type: Boolean,
    default: false
  },
  loading: {
    type: Boolean,
    default: false
  }
})

defineEmits(['confirm', 'cancel', 'close'])
</script>