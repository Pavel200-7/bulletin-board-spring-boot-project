<template>
  <div class="form-actions">
    <button type="submit" class="btn-save" :disabled="saving">
      <span v-if="saving" class="saving-spinner"></span>
      {{ saving ? 'Сохранение...' : 'Сохранить изменения' }}
    </button>
    <button 
      v-if="!isApproved" 
      type="button" 
      class="btn-approve" 
      @click="$emit('approve')"
      :disabled="approving"
    >
      {{ approving ? 'Подтверждение...' : 'Подтвердить аккаунт' }}
    </button>
  </div>
  
  <div v-if="isApproved" class="approved-badge">
    ✅ Аккаунт подтвержден
  </div>
</template>

<script setup>
defineProps({
  saving: Boolean,
  approving: Boolean,
  isApproved: Boolean
})

defineEmits(['approve'])
</script>

<style scoped>
.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
}

.btn-save, .btn-approve {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.875rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  transition: all 0.2s;
}

.btn-save {
  background: #48bb78;
  color: white;
}

.btn-save:hover:not(:disabled) {
  background: #38a169;
}

.btn-approve {
  background: #f59e0b;
  color: white;
}

.btn-approve:hover:not(:disabled) {
  background: #d97706;
}

.btn-save:disabled, .btn-approve:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.saving-spinner {
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.approved-badge {
  margin-top: 1rem;
  padding: 0.5rem;
  background: #c6f6d5;
  color: #2f855a;
  text-align: center;
  border-radius: 4px;
}
</style>