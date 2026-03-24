<!-- src/views/bulletin/components/view/BulletinInfo.vue -->
<template>
  <div class="info-section">
    <BulletinHeader :price="price" :state="state" />
    <h1 class="title">{{ title }}</h1>
    <SellerInfo :owner-id="ownerId" />
    <ActionButtons @chat="$emit('chat')" />
    <CharacteristicsList :characteristics="characteristics" />
  </div>
</template>

<script setup>
import BulletinHeader from './BulletinHeader.vue'
import SellerInfo from './SellerInfo.vue'
import ActionButtons from './ActionButtons.vue'
import CharacteristicsList from './CharacteristicsList.vue'

defineProps({
  title: {
    type: String,
    required: true
  },
  price: {
    type: Number,
    required: true
  },
  state: {
    type: String,
    default: ''
  },
  ownerId: {
    type: String,
    required: true
  },
  characteristics: {
    type: Array,
    default: () => []
  }
})

defineEmits(['chat'])
</script>

<style scoped>
.info-section {
  background: white;
  border-radius: 16px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #212529;
  margin-bottom: 1rem;
  line-height: 1.3;
}
</style>