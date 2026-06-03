<script setup>
import { onMounted, ref } from 'vue'
import ChatRoom from '../components/ChatRoom.vue'
import { chatWithHealthApp } from '../api/chat'
import { generateChatId } from '../utils/chatId'

const chatId = ref('')

onMounted(() => {
  chatId.value = generateChatId()
})

function sendMessage(message, { onMessage, signal }) {
  return chatWithHealthApp(message, chatId.value, { onMessage, signal })
}
</script>

<template>
  <ChatRoom
    title="AI 健康問答"
    subtitle="基於 SSE 串流的即時健康諮詢"
    :chat-id="chatId"
    :send-message="sendMessage"
  />
</template>
