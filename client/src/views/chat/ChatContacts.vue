<!-- src/views/chat/ChatContacts.vue -->
<template>
  <div class="chat-contacts">
    <div class="contacts-card">
      <h2>Контакты</h2>
      <p class="placeholder">Страница в разработке</p>
      <p>Здесь будет список ваших контактов для чата</p>
    </div>
  </div>
</template>

<script setup>
// TODO: Реализовать список контактов
</script>

<style scoped>
.chat-contacts {
  max-width: 800px;
  margin: 0 auto;
}

.contacts-card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.contacts-card h2 {
  margin-top: 0;
  margin-bottom: 1rem;
  color: #333;
}

.placeholder {
  color: #a0aec0;
  margin-bottom: 0.5rem;
}
</style>