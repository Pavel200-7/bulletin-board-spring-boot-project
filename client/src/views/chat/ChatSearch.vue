<!-- src/views/chat/ChatSearch.vue -->
<template>
  <div class="chat-search">
    <div class="search-card">
      <h2>Поиск пользователей</h2>
      <p class="placeholder">Страница в разработке</p>
      <p>Здесь можно будет искать пользователей по публичному имени</p>
    </div>
  </div>
</template>

<script setup>
// TODO: Реализовать поиск пользователей
</script>

<style scoped>
.chat-search {
  max-width: 800px;
  margin: 0 auto;
}

.search-card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.search-card h2 {
  margin-top: 0;
  margin-bottom: 1rem;
  color: #333;
}

.placeholder {
  color: #a0aec0;
  margin-bottom: 0.5rem;
}
</style>