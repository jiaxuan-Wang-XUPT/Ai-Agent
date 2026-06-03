<script setup>
const apps = [
  {
    id: 'health',
    title: 'AI 健康问答',
    description: '专业健康咨询助手，支持多轮对话与会话隔离',
    path: '/health',
    icon: '🏥',
    color: '#10b981',
  },
  {
    id: 'manus',
    title: 'AI 超级智能体',
    description: '具备工具调用能力的超级智能体，可完成复杂任务',
    path: '/manus',
    icon: '🤖',
    color: '#2563eb',
  },
]

// 为每个应用添加特色标签
const getTags = (appId) => {
  const tagsMap = {
    health: ['🏷️ 医疗健康', '💬 多轮对话', '🔒 会话隔离'],
    manus: ['🧠 智能体', '🛠️ 工具调用', '⚡ 复杂任务'],
  }
  return tagsMap[appId] || ['✨ AI 驱动']
}
</script>

<template>
  <div class="home">
    <!-- 背景装饰元素 -->
    <div class="bg-orb orb-1"></div>
    <div class="bg-orb orb-2"></div>
    <div class="bg-orb orb-3"></div>
    <div class="grid-pattern"></div>

    <header class="hero">
      <div class="hero-badge">
        <span class="magic-icon">✨</span>
        <span>AI Agent 平台</span>
      </div>
      <h1>
        Skye AI Agent
        <span class="cursor">⚡</span>
      </h1>
      <p class="hero-desc">
        选择一个应用，开启智能对话体验
        <span class="wave">👋</span>
      </p>
      <div class="hero-stats">
        <div class="stat">
          <span class="stat-number">2</span>
          <span class="stat-label">专业服务</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat">
          <span class="stat-number">24hours</span>
          <span class="stat-label">智能响应</span>
        </div>
      </div>
    </header>

    <section class="app-grid">
      <RouterLink
          v-for="app in apps"
          :key="app.id"
          :to="app.path"
          class="app-card"
          :style="{ '--accent': app.color }"
      >
        <div class="card-glow"></div>
        <div class="card-icon-wrapper" :style="{ background: `${app.color}15` }">
          <span class="app-icon">{{ app.icon }}</span>
        </div>
        <div class="card-content">
          <h2>{{ app.title }}</h2>
          <p>{{ app.description }}</p>
          <div class="tags">
            <span
                v-for="(tag, idx) in getTags(app.id)"
                :key="idx"
                class="tag"
                :style="{ background: `${app.color}10`, color: app.color }"
            >
              {{ tag }}
            </span>
          </div>
          <div class="enter-link">
            <span>进入应用</span>
            <span class="arrow">→</span>
          </div>
        </div>
      </RouterLink>
    </section>

    <footer class="home-footer">
      <div class="footer-content">
        <div class="footer-info">
          <span class="footer-dot"></span>
          <span>智能体系统 · 实时响应</span>
        </div>
        <div class="footer-message">
          <span>💡 提示：点击卡片即可开始对话</span>
        </div>
      </div>
      <!-- 新增署名，底部居中 -->
      <div class="credit">清华大学 by 程序猿skye</div>
    </footer>
  </div>
</template>

<style scoped>
.home {
  position: relative;
  min-height: 100vh;
  padding: 48px 24px 32px;
  background: linear-gradient(135deg, #f5f7ff 0%, #f8fafc 50%, #ffffff 100%);
  overflow-x: hidden;
}

/* 背景装饰元素 */
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
  background: radial-gradient(circle, rgba(16, 185, 129, 0.3), transparent);
  top: -200px;
  left: -150px;
}

.orb-2 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(37, 99, 235, 0.25), transparent);
  bottom: -100px;
  right: -100px;
}

.orb-3 {
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(139, 92, 246, 0.2), transparent);
  top: 40%;
  left: 70%;
}

.grid-pattern {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image:
      linear-gradient(rgba(0, 0, 0, 0.02) 1px, transparent 1px),
      linear-gradient(90deg, rgba(0, 0, 0, 0.02) 1px, transparent 1px);
  background-size: 40px 40px;
  pointer-events: none;
  z-index: 0;
}

/* Hero 区域 */
.hero {
  position: relative;
  z-index: 2;
  text-align: center;
  max-width: 720px;
  margin: 0 auto 56px;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
  padding: 6px 16px;
  border-radius: 40px;
  font-size: 13px;
  font-weight: 500;
  color: #4b5563;
  margin-bottom: 24px;
  border: 1px solid rgba(0, 0, 0, 0.05);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
}

.magic-icon {
  font-size: 16px;
  animation: sparkle 2s ease-in-out infinite;
}

@keyframes sparkle {
  0%, 100% { opacity: 0.6; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.1); }
}

.hero h1 {
  margin: 0 0 16px;
  font-size: 48px;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  letter-spacing: -0.5px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.cursor {
  display: inline-block;
  background: linear-gradient(135deg, #10b981, #3b82f6);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 0.6; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.1); }
}

.hero-desc {
  margin: 0 0 32px;
  color: #64748b;
  font-size: 18px;
}

.wave {
  display: inline-block;
  animation: wave 2s ease-in-out infinite;
  transform-origin: 70% 70%;
}

@keyframes wave {
  0% { transform: rotate(0deg); }
  10% { transform: rotate(14deg); }
  20% { transform: rotate(-8deg); }
  30% { transform: rotate(14deg); }
  40% { transform: rotate(-8deg); }
  50% { transform: rotate(10deg); }
  60% { transform: rotate(0deg); }
  100% { transform: rotate(0deg); }
}

.hero-stats {
  display: inline-flex;
  align-items: center;
  gap: 24px;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(8px);
  padding: 12px 28px;
  border-radius: 60px;
  border: 1px solid rgba(0, 0, 0, 0.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
}

.stat {
  display: flex;
  align-items: baseline;
  gap: 6px;
}

.stat-number {
  font-size: 20px;
  font-weight: 700;
  color: #0f172a;
}

.stat-label {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
}

.stat-divider {
  width: 1px;
  height: 20px;
  background: #cbd5e1;
}

/* 卡片网格 */
.app-grid {
  position: relative;
  z-index: 2;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 32px;
  max-width: 880px;
  margin: 0 auto 48px;
}

.app-card {
  position: relative;
  display: block;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(12px);
  border-radius: 28px;
  border: 1px solid rgba(255, 255, 255, 0.5);
  text-decoration: none;
  color: inherit;
  transition: all 0.4s cubic-bezier(0.2, 0.9, 0.4, 1.1);
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.04);
}

.card-glow {
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 50% 0%, var(--accent), transparent 70%);
  opacity: 0;
  transition: opacity 0.4s ease;
  pointer-events: none;
}

.app-card:hover {
  transform: translateY(-6px);
  border-color: var(--accent);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.12);
}

.app-card:hover .card-glow {
  opacity: 0.08;
}

.card-icon-wrapper {
  width: 64px;
  height: 64px;
  margin: 28px 28px 0 28px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.3s ease;
}

.app-card:hover .card-icon-wrapper {
  transform: scale(1.05);
}

.app-icon {
  font-size: 32px;
}

.card-content {
  padding: 20px 28px 28px;
}

.app-card h2 {
  margin: 0 0 8px;
  font-size: 22px;
  font-weight: 600;
  color: #0f172a;
}

.app-card p {
  margin: 0 0 16px;
  font-size: 14px;
  line-height: 1.5;
  color: #64748b;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}

.tag {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 30px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.app-card:hover .tag {
  transform: translateY(-1px);
}

.enter-link {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: var(--accent);
  transition: gap 0.3s ease;
}

.arrow {
  transition: transform 0.3s ease;
}

.app-card:hover .arrow {
  transform: translateX(4px);
}

/* 底部区域 */
.home-footer {
  position: relative;
  z-index: 2;
  max-width: 880px;
  margin: 0 auto;
  padding-top: 24px;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

.footer-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 20px;
}

.footer-info {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
  color: #94a3b8;
}

.footer-dot {
  width: 6px;
  height: 6px;
  background: #10b981;
  border-radius: 50%;
  display: inline-block;
  animation: breathe 2s ease-in-out infinite;
}

@keyframes breathe {
  0%, 100% { opacity: 0.4; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.2); }
}

.footer-message {
  font-size: 12px;
  color: #94a3b8;
  background: rgba(0, 0, 0, 0.02);
  padding: 6px 12px;
  border-radius: 40px;
}

/* 新增署名样式 */
.credit {
  text-align: center;
  font-size: 12px;
  color: #a0afc0;
  letter-spacing: 0.5px;
  padding-top: 8px;
  border-top: 1px dashed rgba(0, 0, 0, 0.04);
  margin-top: 4px;
}

/* 响应式 */
@media (max-width: 768px) {
  .home {
    padding: 32px 20px 24px;
  }

  .hero h1 {
    font-size: 36px;
  }

  .hero-stats {
    padding: 8px 20px;
  }

  .app-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .card-icon-wrapper {
    width: 56px;
    height: 56px;
    margin: 24px 24px 0 24px;
  }

  .card-content {
    padding: 16px 24px 24px;
  }

  .footer-content {
    flex-direction: column;
    text-align: center;
  }

  .credit {
    font-size: 11px;
  }
}

@media (max-width: 480px) {
  .hero h1 {
    font-size: 28px;
  }

  .hero-desc {
    font-size: 15px;
  }

  .stat-number {
    font-size: 16px;
  }

  .stat-label {
    font-size: 11px;
  }
}
</style>