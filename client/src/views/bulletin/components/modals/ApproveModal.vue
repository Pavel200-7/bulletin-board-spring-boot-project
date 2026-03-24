<!-- src/views/bulletin/components/modals/ApproveModal.vue -->
<template>
  <BaseModal
    :show="show"
    title="Подтверждение объявления"
    confirm-text="Подтвердить"
    confirm-loading-text="Проверка..."
    :confirm-loading="loading"
    @confirm="$emit('confirm')"
    @cancel="$emit('cancel')"
    @close="$emit('close')"
  >
    <template #body>
      <p>Проверить объявление на валидность?</p>
      <p>После подтверждения объявление получит статус "Готов к публикации".</p>
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