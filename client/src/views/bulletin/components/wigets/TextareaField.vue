<!-- src/views/bulletin/components/wigets/TextareaField.vue -->
<template>
  <div class="form-group" :class="{ 'has-error': error }">
    <label>
      {{ label }}
      <span v-if="required" class="required-star">*</span>
    </label>
    <textarea
      :value="modelValue"
      @input="$emit('update:modelValue', $event.target.value)"
      :rows="rows"
      :placeholder="placeholder"
      :maxlength="maxLength"
      class="form-textarea"
      :class="{ 'has-error': error }"
    ></textarea>
    <div v-if="error" class="field-error">{{ error }}</div>
    <div v-if="hint" class="field-hint">
      {{ hint }}
      <span v-if="maxLength && modelValue?.length">
        ({{ modelValue.length }}/{{ maxLength }})
      </span>
    </div>
  </div>
</template>

<script setup>
defineProps({
  modelValue: String,
  label: String,
  rows: {
    type: [String, Number],
    default: 3
  },
  placeholder: String,
  required: Boolean,
  error: String,
  hint: String,
  maxLength: [String, Number]
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

.form-textarea {
  padding: 0.5rem;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
  font-size: 0.875rem;
  font-family: inherit;
  resize: vertical;
  transition: all 0.2s;
}

.form-textarea:focus {
  outline: none;
  border-color: #667eea;
}

.form-textarea.has-error {
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