<template>
  <div class="location-section">
    <h3 class="section-title">Местоположение</h3>
    
    <div class="form-group">
      <label>Примерное местоположение</label>
      <div class="location-group">
        <input
          :value="townName"
          @input="$emit('update:townName', $event.target.value)"
          placeholder="Город"
          class="form-input"
        />
        <input
          :value="latitude"
          @input="$emit('update:latitude', $event.target.value)"
          placeholder="Широта"
          type="number"
          step="any"
          class="form-input"
          :class="{ 'has-error': coordinatesError }"
        />
        <input
          :value="longitude"
          @input="$emit('update:longitude', $event.target.value)"
          placeholder="Долгота"
          type="number"
          step="any"
          class="form-input"
          :class="{ 'has-error': coordinatesError }"
        />
      </div>
      <div v-if="coordinatesError" class="field-error">
        {{ coordinatesError }}
      </div>
    </div>
    
    <div class="form-group">
      <label>Точное местоположение</label>
      <input
        :value="locationName"
        @input="$emit('update:locationName', $event.target.value)"
        placeholder="Адрес"
        class="form-input"
      />
    </div>
    
    <div v-if="locationError" class="field-error location-error">
      {{ locationError }}
    </div>
  </div>
</template>

<script setup>
defineProps({
  townName: String,
  latitude: [String, Number],
  longitude: [String, Number],
  locationName: String,
  locationError: String,
  coordinatesError: String
})

defineEmits([
  'update:townName',
  'update:latitude',
  'update:longitude',
  'update:locationName'
])
</script>

<style scoped>
.location-section {
  border-top: 1px solid #e2e8f0;
  padding-top: 1rem;
}

.section-title {
  font-size: 1rem;
  font-weight: 600;
  color: #4a5568;
  margin: 0 0 1rem 0;
}

.location-group {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 0.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.form-group label {
  font-weight: 500;
  color: #4a5568;
}

.form-input {
  padding: 0.5rem;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
}

.form-input.has-error {
  border-color: #e53e3e;
}

.field-error {
  color: #e53e3e;
  font-size: 0.75rem;
}

.location-error {
  margin-top: 0.5rem;
}
</style>