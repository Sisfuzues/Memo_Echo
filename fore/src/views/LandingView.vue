<template>
  <main class="landing-page">
    <header class="nav-shell">
      <RouterLink class="brand" to="/">
        <span class="brand-glyph"></span>
        <span>MemoEcho</span>
      </RouterLink>

      <nav class="nav-links" aria-label="Landing navigation">
        <a href="#product">Product</a>
        <a href="#features">Features</a>
        <a href="#docs">Docs</a>
      </nav>

      <button class="open-app-btn" type="button" @click="openApp">Open app</button>
    </header>

    <section class="hero-section" id="product">
      <div class="live-badge">
        <span class="live-dot"></span>
        MemoEcho v1.0 is live ->
      </div>

      <h1>把 QQ 群聊变成可追问的日程记忆</h1>
      <p class="hero-subtitle">
        MemoEcho 为校园群聊、社团通知和课程协作而生，自动提取时间、地点、参与人和待办，让机器人随时回答群里的日程安排。
      </p>

      <div class="hero-actions">
        <button class="hero-primary" type="button" @click="openApp">Get Started</button>
        <RouterLink class="hero-secondary" to="/auth">Login / Register</RouterLink>
      </div>
    </section>

    <section class="mockup-wrap" aria-label="MemoEcho app preview">
      <div class="app-mockup">
        <aside class="mock-sidebar">
          <div class="window-dots">
            <span></span>
            <span></span>
            <span></span>
          </div>
          <div class="mock-menu">
            <button class="active">Inbox</button>
            <button>My issues</button>
            <button>Reviews</button>
            <button>Groups</button>
            <button>Risk logs</button>
          </div>
        </aside>

        <section class="mock-main">
          <div class="mock-topbar">
            <span>MemoEcho / workspace / schedule-memory</span>
            <div class="mock-status">
              <span class="status-pill progress"><i></i>In Progress</span>
              <span class="status-pill priority"><i></i>High Priority</span>
            </div>
          </div>

          <div class="mock-content">
            <div class="mock-copy">
              <span class="mock-kicker">AI 日程记忆</span>
              <h2>彩虹长廊志愿服务集合提醒</h2>
              <p>
                从群公告中提取集合时间、地点、参与对象和服务内容，并进入可查询的群日程记忆库。
              </p>
            </div>

            <div class="mock-grid">
              <article>
                <span>时间</span>
                <strong>4月11日 10:00</strong>
              </article>
              <article>
                <span>地点</span>
                <strong>八食堂门口</strong>
              </article>
              <article>
                <span>审核状态</span>
                <strong>已入库</strong>
              </article>
            </div>

            <div class="issue-list">
              <div v-for="item in issues" :key="item.title" class="issue-row">
                <span :class="['issue-dot', item.tone]"></span>
                <div>
                  <strong>{{ item.title }}</strong>
                  <small>{{ item.meta }}</small>
                </div>
                <b>{{ item.state }}</b>
              </div>
            </div>
          </div>
        </section>
      </div>
    </section>

    <section class="feature-section" id="features">
      <article v-for="feature in features" :key="feature.title" class="feature-card">
        <span>{{ feature.kicker }}</span>
        <h3>{{ feature.title }}</h3>
        <p>{{ feature.description }}</p>
      </article>
    </section>
  </main>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { getAuthToken } from '@/utils/auth';

const router = useRouter();

const openApp = () => {
  router.push(getAuthToken() ? '/dashboard' : '/auth');
};

const issues = [
  { title: '软件工程课程群提交托管申请', meta: '等待管理员审核机器人入群', state: '待审', tone: 'violet' },
  { title: 'QQ 机器人状态同步完成', meta: '3 个服务在线，NapCat 心跳正常', state: '在线', tone: 'green' },
  { title: '群聊日程已完成向量索引', meta: '今日新增 15 条可查询记忆', state: '已同步', tone: 'blue' }
];

const features = [
  {
    kicker: '提取',
    title: '自动识别群聊日程',
    description: '从 QQ 群自然聊天和通知中提取时间、地点、参与人和任务内容。'
  },
  {
    kicker: '审批',
    title: '普通用户申请，管理员审核',
    description: '群日程托管需要经过同意或拒绝流程，避免机器人权限失控。'
  },
  {
    kicker: '追问',
    title: '机器人回答历史安排',
    description: '用户在群里或私聊中提问，机器人从群日程记忆里检索并回答。'
  }
];
</script>

<style scoped>
.landing-page {
  min-height: 100vh;
  background: #0a0a0a;
  color: #fff;
  font-family: Inter, 'Segoe UI', 'PingFang SC', sans-serif;
  overflow-x: hidden;
}

.nav-shell {
  position: sticky;
  top: 0;
  z-index: 10;
  width: min(1180px, calc(100% - 40px));
  margin: 0 auto;
  padding: 22px 0;
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(10, 10, 10, 0.78);
  backdrop-filter: blur(16px);
}

.brand,
.nav-links,
.hero-actions,
.mock-status {
  display: flex;
  align-items: center;
}

.brand {
  gap: 10px;
  color: #fff;
  font-weight: 760;
}

.brand-glyph {
  width: 26px;
  height: 26px;
  border: 1px solid rgba(255, 255, 255, 0.22);
  border-radius: 8px;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(255, 255, 255, 0.08)),
    #111;
  box-shadow: 0 0 28px rgba(255, 255, 255, 0.08);
}

.nav-links {
  gap: 28px;
}

.nav-links a {
  color: #8f8f99;
  font-size: 0.9rem;
}

.nav-links a:hover {
  color: #fff;
}

.open-app-btn {
  justify-self: end;
  min-height: 36px;
  padding: 8px 15px;
  border: 1px solid #fff;
  border-radius: 999px;
  background: #fff;
  color: #000;
  font-weight: 700;
  cursor: pointer;
}

.open-app-btn:hover,
.hero-primary:hover {
  box-shadow: 0 0 34px rgba(255, 255, 255, 0.18);
}

.hero-section {
  width: min(1180px, calc(100% - 40px));
  margin: 0 auto;
  padding: 118px 0 52px;
  text-align: center;
}

.live-badge {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 7px 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 999px;
  color: #d8d8df;
  background: rgba(255, 255, 255, 0.035);
  box-shadow: 0 0 32px rgba(124, 92, 255, 0.10);
}

.live-dot {
  width: 7px;
  height: 7px;
  border-radius: 999px;
  background: #8b5cf6;
  box-shadow: 0 0 16px rgba(139, 92, 246, 0.9);
}

h1 {
  max-width: 980px;
  margin: 28px auto 0;
  font-size: clamp(4rem, 10vw, 8.8rem);
  line-height: 1.12;
  letter-spacing: -0.045em;
  font-weight: 700;
  color: #f4f4f5;
}

.hero-subtitle {
  max-width: 690px;
  margin: 38px auto 0;
  color: #a3a3ad;
  font-size: clamp(1.08rem, 1.7vw, 1.35rem);
  line-height: 1.75;
}

.hero-actions {
  justify-content: center;
  gap: 12px;
  margin-top: 34px;
}

.hero-primary,
.hero-secondary {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 42px;
  padding: 10px 17px;
  border: 1px solid transparent;
  border-radius: 999px;
  font-weight: 730;
  line-height: 1;
  box-sizing: border-box;
}

.hero-primary {
  border: 1px solid #fff;
  background: #fff;
  color: #000;
  cursor: pointer;
}

.hero-secondary {
  border: 1px solid rgba(255, 255, 255, 0.12);
  color: #d8d8df;
  background: rgba(255, 255, 255, 0.035);
}

.mockup-wrap {
  width: min(1180px, calc(100% - 40px));
  margin: 18px auto 0;
  padding-bottom: 86px;
}

.app-mockup {
  display: grid;
  grid-template-columns: 220px minmax(0, 1fr);
  min-height: 540px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 18px;
  overflow: hidden;
  background: #111;
  box-shadow:
    0 0 0 1px rgba(255, 255, 255, 0.035),
    0 40px 140px rgba(0, 0, 0, 0.65),
    0 0 80px rgba(124, 92, 255, 0.08);
}

.mock-sidebar {
  padding: 22px 18px;
  border-right: 1px solid rgba(255, 255, 255, 0.08);
  background: #0d0d0f;
}

.window-dots {
  display: flex;
  gap: 7px;
  margin-bottom: 28px;
}

.window-dots span {
  width: 10px;
  height: 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.18);
}

.mock-menu {
  display: grid;
  gap: 8px;
}

.mock-menu button {
  min-height: 36px;
  border: 0;
  border-radius: 8px;
  padding: 8px 10px;
  background: transparent;
  color: #8f8f99;
  text-align: left;
}

.mock-menu .active {
  color: #fff;
  background: rgba(255, 255, 255, 0.08);
}

.mock-main {
  min-width: 0;
}

.mock-topbar {
  min-height: 64px;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  color: #9f9fa8;
  font-size: 0.92rem;
}

.mock-status {
  gap: 9px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.status-pill {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-height: 30px;
  padding: 6px 10px;
  border-radius: 999px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: #d8d8df;
  background: rgba(255, 255, 255, 0.04);
}

.status-pill i {
  width: 7px;
  height: 7px;
  border-radius: 999px;
}

.progress i {
  background: #facc15;
}

.priority i {
  width: 10px;
  border-radius: 2px;
  background: linear-gradient(90deg, #f87171 25%, #fb923c 25% 58%, #facc15 58%);
}

.mock-content {
  padding: 34px;
}

.mock-kicker {
  color: #8b5cf6;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  font-size: 0.78rem;
  font-weight: 760;
}

.mock-copy h2 {
  max-width: 620px;
  margin-top: 14px;
  font-size: clamp(1.7rem, 3.1vw, 3.2rem);
  line-height: 1.12;
  letter-spacing: -0.03em;
  font-weight: 600;
}

.mock-copy p {
  max-width: 660px;
  margin-top: 18px;
  color: #a3a3ad;
  line-height: 1.8;
}

.mock-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-top: 32px;
}

.mock-grid article,
.issue-row,
.feature-card {
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.035);
}

.mock-grid article {
  padding: 16px;
  border-radius: 12px;
}

.mock-grid span,
.issue-row small,
.feature-card p {
  color: #9f9fa8;
}

.mock-grid strong {
  display: block;
  margin-top: 7px;
  color: #fff;
}

.issue-list {
  display: grid;
  gap: 10px;
  margin-top: 18px;
}

.issue-row {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  gap: 12px;
  align-items: center;
  padding: 13px 14px;
  border-radius: 12px;
}

.issue-row strong {
  display: block;
  color: #f5f5f7;
}

.issue-row b {
  color: #d8d8df;
  font-size: 0.82rem;
}

.issue-dot {
  width: 9px;
  height: 9px;
  border-radius: 999px;
}

.violet {
  background: #8b5cf6;
}

.green {
  background: #22c55e;
}

.blue {
  background: #38bdf8;
}

.feature-section {
  width: min(1180px, calc(100% - 40px));
  margin: 0 auto;
  padding: 0 0 96px;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.feature-card {
  padding: 32px;
  border-radius: 14px;
}

.feature-card:hover {
  box-shadow: 0 0 44px rgba(255, 255, 255, 0.045);
}

.feature-card span {
  color: #8b5cf6;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  font-size: 0.75rem;
  font-weight: 760;
}

.feature-card h3 {
  margin-top: 14px;
  font-size: 1.12rem;
}

.feature-card p {
  margin-top: 10px;
  line-height: 1.75;
}

@media (max-width: 900px) {
  .nav-shell {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .nav-links,
  .open-app-btn {
    justify-self: start;
  }

  .app-mockup,
  .feature-section {
    grid-template-columns: 1fr;
  }

  .mock-sidebar {
    display: none;
  }

  .mock-topbar {
    align-items: flex-start;
    flex-direction: column;
    padding: 18px;
  }
}

@media (max-width: 640px) {
  h1 {
    font-size: 3.6rem;
  }

  .hero-section {
    padding-top: 72px;
  }

  .mock-content {
    padding: 22px;
  }

  .mock-grid {
    grid-template-columns: 1fr;
  }

  .issue-row {
    grid-template-columns: auto minmax(0, 1fr);
  }

  .issue-row b {
    grid-column: 2;
  }
}
</style>
