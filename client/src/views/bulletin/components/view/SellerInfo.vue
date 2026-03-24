<!-- src/views/bulletin/components/view/SellerInfo.vue -->
<template>
  <div class="seller-info">
    <div class="seller-avatar">
      <span>👤</span>
    </div>
    <div class="seller-details">
      <div class="seller-name">Продавец</div>
      <div class="seller-id">ID: {{ ownerId?.slice(0, 8) }}...</div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  ownerId: {
    type: String,
    required: true
  }
})
</script>

<style scoped>
.seller-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 12px;
  margin-bottom: 1rem;
}

.seller-avatar {
  width: 48px;
  height: 48px;
  background: #e2e8f0;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
}

.seller-details {
  flex: 1;
}

.seller-name {
  font-weight: 600;
  color: #212529;
  margin-bottom: 0.25rem;
}

.seller-id {
  font-size: 0.75rem;
  color: #6c757d;
}
</style>