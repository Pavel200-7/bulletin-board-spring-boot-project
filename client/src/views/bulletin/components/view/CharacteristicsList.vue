<!-- src/views/bulletin/components/view/CharacteristicsList.vue -->
<template>
  <div v-if="characteristics.length > 0" class="details-block">
    <h3>Характеристики</h3>
    <div class="characteristics-list">
      <div 
        v-for="char in characteristics" 
        :key="char.id"
        class="characteristic-item"
      >
        <span class="char-name">{{ getCharacteristicName(char) }}</span>
        <span class="char-value">{{ getCharacteristicValue(char) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  characteristics: {
    type: Array,
    default: () => []
  }
})

const getCharacteristicName = (char) => {
  return char.name?.name || char.characteristicName || char.name || 'Характеристика'
}

const getCharacteristicValue = (char) => {
  return char.value?.name || char.valueName || char.value || '—'
}
</script>

<style scoped>
.details-block {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 1rem;
}

.details-block h3 {
  font-size: 1rem;
  font-weight: 600;
  color: #212529;
  margin-bottom: 0.75rem;
}

.characteristics-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.characteristic-item {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  font-size: 0.875rem;
  padding: 0.5rem 0;
  border-bottom: 1px solid #e2e8f0;
}

.characteristic-item:last-child {
  border-bottom: none;
}

.char-name {
  color: #6c757d;
  flex: 1;
}

.char-value {
  font-weight: 500;
  color: #212529;
  text-align: right;
  margin-left: 1rem;
}
</style>