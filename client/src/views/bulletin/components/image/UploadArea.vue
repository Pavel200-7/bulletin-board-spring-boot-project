<!-- src/views/bulletin/components/image/UploadArea.vue -->
<template>
  <div 
    class="upload-area" 
    :class="{ 'dragover': isDragover }" 
    @dragover.prevent="isDragover = true" 
    @dragleave="isDragover = false" 
    @drop.prevent="handleDrop"
  >
    <input
      ref="fileInput"
      type="file"
      accept="image/jpeg,image/jpg,image/png,image/gif,image/webp"
      multiple
      class="file-input"
      @change="handleFileSelect"
    />
    <div class="upload-placeholder">
      <span class="upload-icon">📸</span>
      <p>Перетащите изображения сюда или нажмите для выбора</p>
      <small>Поддерживаются JPEG, PNG, GIF, WEBP (до 5MB)</small>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const emit = defineEmits(['files-selected'])

const fileInput = ref(null)
const isDragover = ref(false)

const handleFileSelect = (event) => {
  const files = Array.from(event.target.files)
  emit('files-selected', files)
  fileInput.value.value = ''
}

const handleDrop = (event) => {
  isDragover.value = false
  const files = Array.from(event.dataTransfer.files)
  emit('files-selected', files)
}
</script>

<style scoped>
.upload-area {
  border: 2px dashed #e2e8f0;
  border-radius: 8px;
  padding: 2rem;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
  background: #fafbfc;
}

.upload-area.dragover {
  border-color: #667eea;
  background: #f0f4ff;
}

.file-input {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  cursor: pointer;
}

.upload-placeholder {
  pointer-events: none;
}

.upload-icon {
  font-size: 2rem;
  display: block;
  margin-bottom: 0.5rem;
}

.upload-placeholder p {
  margin: 0.5rem 0;
  color: #4a5568;
}

.upload-placeholder small {
  color: #a0aec0;
}
</style>