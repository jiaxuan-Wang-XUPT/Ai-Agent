<script setup>
import { onMounted, ref } from 'vue'
import ChatRoom from '../components/ChatRoom.vue'
import { chatWithHealthApp } from '../api/chat'
import { generateChatId } from '../utils/chatId'

const chatId = ref('')
const quickQuestions = [
  { id: 1, text: '感冒了应该注意什么？', icon: '🤧' },
  { id: 2, text: '失眠有什么改善方法？', icon: '😴' },
  { id: 3, text: '如何缓解眼睛疲劳？', icon: '👀' },
  { id: 4, text: '头痛可能是什么原因？', icon: '🤕' },
]

onMounted(() => {
  chatId.value = generateChatId()
})

function sendMessage(message, { onMessage, signal }) {
  return chatWithHealthApp(message, chatId.value, { onMessage, signal })
}

function copyQuestion(questionText) {
  navigator.clipboard.writeText(questionText)
  const toast = document.createElement('div')
  toast.textContent = `已复制：“${questionText}” 请粘贴到输入框发送`
  toast.style.position = 'fixed'
  toast.style.bottom = '80px'
  toast.style.left = '50%'
  toast.style.transform = 'translateX(-50%)'
  toast.style.backgroundColor = '#10b981'
  toast.style.color = 'white'
  toast.style.padding = '8px 16px'
  toast.style.borderRadius = '40px'
  toast.style.fontSize = '13px'
  toast.style.zIndex = '1000'
  document.body.appendChild(toast)
  setTimeout(() => toast.remove(), 2000)
}
</script>

<template>
  <div class="health-chat-view">
    <!-- 背景装饰 -->
    <div class="bg-orb orb-1"></div>
    <div class="bg-orb orb-2"></div>
    <div class="bg-orb orb-3"></div>
    <div class="grid-pattern"></div>

    <!-- 顶部免责条（全宽） -->
    <div class="disclaimer-bar">
      <span class="disclaimer-icon">⚕️</span>
      <span>本AI提供健康建议仅供参考，不能替代专业医生的诊断。如有紧急情况请立即就医。</span>
    </div>

    <!-- 主体：左右两栏 -->
    <div class="main-layout">
      <!-- 左侧：预设问题 -->
      <aside class="quick-sidebar">
        <div class="quick-header">
          <span class="quick-icon">💡</span>
          <span>常见问题</span>
        </div>
        <div class="questions-list">
          <button
              v-for="q in quickQuestions"
              :key="q.id"
              class="quick-btn"
              @click="copyQuestion(q.text)"
          >
            <span class="q-icon">{{ q.icon }}</span>
            <span class="q-text">{{ q.text }}</span>
          </button>
        </div>
        <div class="sidebar-note">
          <span>点击复制问题</span>
        </div>
      </aside>

      <!-- 右侧：聊天区域 -->
      <div class="chat-wrapper">
        <ChatRoom
            ref="chatRoomRef"
            title="AI 健康问答"
            subtitle="基于 SSE 流式的即时健康咨询"
            :chat-id="chatId"
            :send-message="sendMessage"
        />
      </div>
    </div>

    <!-- 底部署名 -->
    <footer class="chat-footer">
      <div class="credit">清华大学 by 程序猿skye</div>
    </footer>
  </div>
</template>

<style scoped>
.health-chat-view {
  position: relative;
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: linear-gradient(135deg, #f0f9f4 0%, #f8fafc 50%, #ffffff 100%);
}

/* 背景装饰圆 */
.bg-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.4;
  pointer-events: none;
  z-index: 0;
}

.orb-1 {
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(16, 185, 129, 0.25), transparent);
  top: -200px;
  left: -150px;
}

.orb-2 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(52, 211, 153, 0.2), transparent);
  bottom: -100px;
  right: -100px;
}

.orb-3 {
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(5, 150, 105, 0.15), transparent);
  top: 30%;
  left: 70%;
}

.grid-pattern {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image:
      linear-gradient(rgba(16, 185, 129, 0.02) 1px, transparent 1px),
      linear-gradient(90deg, rgba(16, 185, 129, 0.02) 1px, transparent 1px);
  background-size: 40px 40px;
  pointer-events: none;
  z-index: 0;
}

/* 免责条 */
.disclaimer-bar {
  position: relative;
  z-index: 2;
  flex-shrink: 0;
  margin: 16px 24px 0 24px;
  background: rgba(255, 248, 225, 0.95);
  backdrop-filter: blur(8px);
  border-left: 4px solid #f59e0b;
  padding: 8px 20px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: #92400e;
  box-shadow: 0 2px 8px rgba(0,0,0,0.02);
}

.disclaimer-icon {
  font-size: 16px;
}

/* 左右布局容器 */
.main-layout {
  position: relative;
  z-index: 2;
  flex: 1;
  min-height: 0;
  display: flex;
  gap: 20px;
  margin: 16px 24px 0 24px;
}

/* 左侧预设问题栏（固定宽度） */
.quick-sidebar {
  flex-shrink: 0;
  width: 220px;
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(12px);
  border-radius: 24px;
  border: 1px solid rgba(16, 185, 129, 0.2);
  padding: 20px 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
}

.quick-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #0f172a;
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(16, 185, 129, 0.2);
}

.quick-icon {
  font-size: 18px;
}

.questions-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.quick-btn {
  display: flex;
  align-items: center;
  gap: 10px;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 40px;
  padding: 8px 14px;
  font-size: 13px;
  color: #1e293b;
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: left;
  width: 100%;
}

.quick-btn:hover {
  border-color: #10b981;
  background: #f0fdf4;
  transform: translateX(4px);
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.1);
}

.q-icon {
  font-size: 16px;
  flex-shrink: 0;
}

.q-text {
  line-height: 1.4;
}

.sidebar-note {
  font-size: 11px;
  color: #94a3b8;
  text-align: center;
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid rgba(0,0,0,0.05);
}

/* 右侧聊天区域包装（占据剩余空间） */
.chat-wrapper {
  flex: 1;
  min-width: 0;       /* 防止溢出 */
  display: flex;
  flex-direction: column;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(8px);
  border-radius: 28px;
  border: 1px solid rgba(255, 255, 255, 0.5);
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.04);
}

/* 强制 ChatRoom 组件填充高度并内部滚动 */
.chat-wrapper > * {
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* 假设 ChatRoom 根元素类名为 .chat-room，如没有则用通用深样式兼容 */
.chat-wrapper :deep(.chat-room) {
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* 底部 footer */
.chat-footer {
  position: relative;
  z-index: 2;
  flex-shrink: 0;
  text-align: center;
  padding: 12px 0 16px;
  margin-top: 4px;
  border-top: 1px solid rgba(0,0,0,0.05);
}

.credit {
  font-size: 11px;
  color: #94a3b8;
  letter-spacing: 0.5px;
}

/* 响应式：屏幕宽度小于 800px 时，左侧问题栏变成顶部横向滚动条，或堆叠？为了保持体验，改为堆叠布局 */
@media (max-width: 780px) {
  .main-layout {
    flex-direction: column;
    gap: 16px;
    margin: 12px 16px 0 16px;
  }

  .quick-sidebar {
    width: auto;
    flex-direction: row;
    flex-wrap: wrap;
    align-items: center;
    padding: 12px 16px;
    gap: 12px;
  }

  .quick-header {
    border-bottom: none;
    padding-bottom: 0;
    width: auto;
  }

  .questions-list {
    flex-direction: row;
    flex-wrap: wrap;
    gap: 8px;
    flex: 1;
  }

  .quick-btn {
    width: auto;
    padding: 6px 12px;
  }

  .sidebar-note {
    display: none;
  }

  .chat-wrapper {
    min-height: 300px;
  }
}

@media (max-width: 480px) {
  .disclaimer-bar {
    margin-left: 12px;
    margin-right: 12px;
    font-size: 10px;
    padding: 6px 12px;
  }

  .quick-btn {
    font-size: 11px;
    padding: 5px 10px;
  }
}
</style>