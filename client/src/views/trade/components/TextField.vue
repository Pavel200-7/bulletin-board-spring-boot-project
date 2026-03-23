<template>
  <div class="form-group" :class="{ 'has-error': error }">
    <label>
      {{ label }}
      <span v-if="required" class="required-star">*</span>
    </label>
    <input
      :value="modelValue"
      @input="$emit('update:modelValue', $event.target.value)"
      :type="type"
      :placeholder="placeholder"
      class="form-input"
      :class="{ 'has-error': error }"
    />
    <div v-if="error" class="field-error">{{ error }}</div>
    <div v-if="hint" class="field-hint">{{ hint }}</div>
  </div>
</template>

<script setup>
defineProps({
  modelValue: [String, Number],
  label: String,
  type: {
    type: String,
    default: 'text'
  },
  placeholder: String,
  required: Boolean,
  error: String,
  hint: String
})

defineEmits(['update:modelValue'])
</script>

<style scoped>
.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group.has-error label {
  color: #e53e3e;
}

.form-group label {
  font-weight: 500;
  color: #4a5568;
}

.required-star {
  color: #e53e3e;
  margin-left: 2px;
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

.field-hint {
  color: #a0aec0;
  font-size: 0.7rem;
}
</style>