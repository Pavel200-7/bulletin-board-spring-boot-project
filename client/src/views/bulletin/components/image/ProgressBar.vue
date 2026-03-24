<!-- src/views/bulletin/components/image/ProgressBar.vue -->
<template>
  <div class="upload-progress">
    <div class="progress-bar" :style="{ width: progress + '%' }"></div>
    <span>{{ progress }}%</span>
  </div>
</template>

<script setup>
defineProps({
  progress: {
    type: Number,
    default: 0
  }
})
</script>

<style scoped>
.upload-progress {
  position: relative;
  height: 20px;
  background: #edf2f7;
  border-radius: 10px;
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  background: #48bb78;
  transition: width 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 0.75rem;
}
</style>