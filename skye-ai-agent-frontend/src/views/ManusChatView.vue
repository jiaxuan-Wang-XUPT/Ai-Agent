<script setup>
import ChatRoom from '../components/ChatRoom.vue'
import { chatWithManus } from '../api/chat'

function sendMessage(message, { onMessage, signal }) {
  return chatWithManus(message, { onMessage, signal })
}
</script>

<template>
  <ChatRoom
    title="AI 超級智能體"
    subtitle="具備工具能力的 Manus 智能體，每次對話獨立實例"
    :send-message="sendMessage"
  />
</template>
