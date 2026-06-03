<script setup>
import { nextTick, ref, watch } from 'vue'

const props = defineProps({
  title: {
    type: String,
    required: true,
  },
  subtitle: {
    type: String,
    default: '',
  },
  chatId: {
    type: String,
    default: '',
  },
  sendMessage: {
    type: Function,
    required: true,
  },
})

const messages = ref([])
const inputText = ref('')
const loading = ref(false)
const messageListRef = ref(null)
let abortController = null

watch(
  () => messages.value.length,
  async () => {
    await nextTick()
    if (messageListRef.value) {
      messageListRef.value.scrollTop = messageListRef.value.scrollHeight
    }
  },
)

async function handleSend() {
  const text = inputText.value.trim()
  if (!text || loading.value) return

  inputText.value = ''
  loading.value = true

  messages.value.push({
    id: Date.now(),
    role: 'user',
    content: text,
  })

  const aiMessage = {
    id: Date.now() + 1,
    role: 'assistant',
    content: '',
  }
  messages.value.push(aiMessage)

  abortController = new AbortController()

  try {
    await props.sendMessage(text, {
      onMessage: (chunk) => {
        aiMessage.content += chunk
      },
      signal: abortController.signal,
    })
  } catch (error) {
    if (error.name !== 'AbortError') {
      aiMessage.content += aiMessage.content
        ? `\n\n[錯誤] ${error.message}`
        : `[錯誤] ${error.message}`
    }
  } finally {
    loading.value = false
    abortController = null
  }
}

function handleKeydown(event) {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    handleSend()
  }
}

function handleStop() {
  abortController?.abort()
  loading.value = false
}
</script>

<template>
  <div class="chat-room">
    <header class="chat-header">
      <div class="header-left">
        <RouterLink to="/" class="back-link">← 返回首頁</RouterLink>
        <div>
          <h1>{{ title }}</h1>
          <p v-if="subtitle" class="subtitle">{{ subtitle }}</p>
          <p v-if="chatId" class="chat-id">會話 ID：{{ chatId }}</p>
        </div>
      </div>
    </header>

    <main ref="messageListRef" class="message-list">
      <div v-if="messages.length === 0" class="empty-hint">
        開始對話吧，輸入問題後按 Enter 或點擊發送。
      </div>

      <div
        v-for="msg in messages"
        :key="msg.id"
        class="message-row"
        :class="msg.role"
      >
        <div class="avatar">{{ msg.role === 'user' ? '我' : 'AI' }}</div>
        <div class="bubble">
          <pre class="content">{{ msg.content || (loading && msg.role === 'assistant' ? '思考中...' : '') }}</pre>
        </div>
      </div>
    </main>

    <footer class="input-area">
      <textarea
        v-model="inputText"
        class="input-box"
        placeholder="輸入訊息，Enter 發送，Shift+Enter 換行"
        rows="3"
        :disabled="loading"
        @keydown="handleKeydown"
      />
      <div class="actions">
        <button
          v-if="loading"
          type="button"
          class="btn btn-secondary"
          @click="handleStop"
        >
          停止
        </button>
        <button
          type="button"
          class="btn btn-primary"
          :disabled="loading || !inputText.trim()"
          @click="handleSend"
        >
          發送
        </button>
      </div>
    </footer>
  </div>
</template>

<style scoped>
.chat-room {
  display: flex;
  flex-direction: column;
  height: 100vh;
  max-width: 960px;
  margin: 0 auto;
  background: #f5f7fb;
}

.chat-header {
  padding: 16px 20px;
  background: #fff;
  border-bottom: 1px solid #e8ecf3;
  box-shadow: 0 1px 4px rgba(15, 23, 42, 0.06);
}

.header-left {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.back-link {
  color: #64748b;
  text-decoration: none;
  font-size: 14px;
  white-space: nowrap;
  padding-top: 4px;
}

.back-link:hover {
  color: #2563eb;
}

.chat-header h1 {
  margin: 0;
  font-size: 20px;
  color: #0f172a;
}

.subtitle {
  margin: 4px 0 0;
  font-size: 13px;
  color: #64748b;
}

.chat-id {
  margin: 6px 0 0;
  font-size: 12px;
  color: #94a3b8;
  font-family: ui-monospace, Consolas, monospace;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.empty-hint {
  text-align: center;
  color: #94a3b8;
  margin-top: 40px;
  font-size: 14px;
}

.message-row {
  display: flex;
  gap: 10px;
  max-width: 85%;
}

.message-row.user {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.message-row.assistant {
  align-self: flex-start;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  flex-shrink: 0;
}

.message-row.user .avatar {
  background: #2563eb;
  color: #fff;
}

.message-row.assistant .avatar {
  background: #10b981;
  color: #fff;
}

.bubble {
  padding: 12px 14px;
  border-radius: 12px;
  line-height: 1.6;
  word-break: break-word;
}

.message-row.user .bubble {
  background: #2563eb;
  color: #fff;
  border-bottom-right-radius: 4px;
}

.message-row.assistant .bubble {
  background: #fff;
  color: #1e293b;
  border: 1px solid #e2e8f0;
  border-bottom-left-radius: 4px;
}

.content {
  margin: 0;
  white-space: pre-wrap;
  font-family: inherit;
  font-size: 14px;
}

.input-area {
  padding: 16px 20px 20px;
  background: #fff;
  border-top: 1px solid #e8ecf3;
}

.input-box {
  width: 100%;
  box-sizing: border-box;
  padding: 12px 14px;
  border: 1px solid #dbe3f0;
  border-radius: 10px;
  resize: none;
  font-size: 14px;
  font-family: inherit;
  outline: none;
  transition: border-color 0.2s;
}

.input-box:focus {
  border-color: #2563eb;
}

.input-box:disabled {
  background: #f8fafc;
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 10px;
}

.btn {
  border: none;
  border-radius: 8px;
  padding: 8px 18px;
  font-size: 14px;
  cursor: pointer;
  transition: opacity 0.2s;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-primary {
  background: #2563eb;
  color: #fff;
}

.btn-primary:hover:not(:disabled) {
  background: #1d4ed8;
}

.btn-secondary {
  background: #e2e8f0;
  color: #475569;
}

.btn-secondary:hover {
  background: #cbd5e1;
}
</style>
