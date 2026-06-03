<script setup>
const apps = [
  {
    id: 'health',
    title: 'AI 健康問答',
    description: '專業健康諮詢助手，支援多輪對話與會話隔離。',
    path: '/health',
    icon: '🏥',
    color: '#10b981',
  },
  {
    id: 'manus',
    title: 'AI 超級智能體',
    description: '具備工具調用能力的超級智能體，可完成複雜任務。',
    path: '/manus',
    icon: '🤖',
    color: '#2563eb',
  },
]
</script>

<template>
  <div class="home">
    <header class="hero">
      <h1>Skye AI Agent</h1>
      <p class="hero-desc">選擇一個應用，開始你的 AI 對話體驗</p>
    </header>

    <section class="app-grid">
      <RouterLink
        v-for="app in apps"
        :key="app.id"
        :to="app.path"
        class="app-card"
        :style="{ '--accent': app.color }"
      >
        <span class="app-icon">{{ app.icon }}</span>
        <h2>{{ app.title }}</h2>
        <p>{{ app.description }}</p>
        <span class="enter-link">進入應用 →</span>
      </RouterLink>
    </section>
  </div>
</template>

<style scoped>
.home {
  min-height: 100vh;
  padding: 48px 24px 64px;
  background: linear-gradient(160deg, #f0f4ff 0%, #f8fafc 45%, #fff 100%);
}

.hero {
  text-align: center;
  max-width: 640px;
  margin: 0 auto 48px;
}

.hero h1 {
  margin: 0 0 12px;
  font-size: 36px;
  color: #0f172a;
  letter-spacing: -0.5px;
}

.hero-desc {
  margin: 0;
  color: #64748b;
  font-size: 16px;
}

.app-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
  max-width: 760px;
  margin: 0 auto;
}

.app-card {
  display: block;
  padding: 28px 24px;
  background: #fff;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  text-decoration: none;
  color: inherit;
  transition: transform 0.2s, box-shadow 0.2s, border-color 0.2s;
  box-shadow: 0 4px 16px rgba(15, 23, 42, 0.04);
}

.app-card:hover {
  transform: translateY(-4px);
  border-color: var(--accent);
  box-shadow: 0 12px 32px rgba(15, 23, 42, 0.1);
}

.app-icon {
  font-size: 36px;
  display: block;
  margin-bottom: 16px;
}

.app-card h2 {
  margin: 0 0 8px;
  font-size: 20px;
  color: #0f172a;
}

.app-card p {
  margin: 0 0 20px;
  font-size: 14px;
  line-height: 1.6;
  color: #64748b;
}

.enter-link {
  font-size: 14px;
  font-weight: 600;
  color: var(--accent);
}
</style>
