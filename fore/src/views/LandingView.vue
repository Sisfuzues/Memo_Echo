<template>
  <main class="landing-page">
    <nav class="landing-nav">
      <RouterLink class="brand" to="/">
        <span class="brand-mark">ME</span>
        <span>Memo Echo</span>
      </RouterLink>

      <div class="nav-actions">
        <RouterLink class="ghost-link" to="/auth">登录 / 注册</RouterLink>
        <button class="primary-link" type="button" @click="openApp">打开 App</button>
      </div>
    </nav>

    <section class="hero-section">
      <div class="hero-copy">
        <p class="eyebrow">AI group memory</p>
        <h1>让群聊里的日程，变成可追问的团队记忆。</h1>
        <p class="hero-text">
          Memo Echo 连接机器人、群聊消息和 AI 提取能力，把散落在 QQ 群里的活动、课程、会议和提醒整理成可查询的日程上下文。
        </p>
        <div class="hero-actions">
          <button class="primary-link hero-primary" type="button" @click="openApp">打开 App</button>
          <RouterLink class="ghost-link hero-secondary" to="/auth">登录或创建账号</RouterLink>
        </div>
      </div>

      <div class="product-frame" aria-label="Memo Echo dashboard preview">
        <div class="frame-sidebar">
          <span></span>
          <span></span>
          <span class="active"></span>
          <span></span>
        </div>
        <div class="frame-main">
          <div class="frame-header">
            <span>群日程申请</span>
            <strong>12</strong>
          </div>
          <div class="frame-table">
            <div v-for="row in previewRows" :key="row.group" class="frame-row">
              <span>{{ row.group }}</span>
              <small>{{ row.status }}</small>
              <b :class="row.tone">{{ row.score }}</b>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="feature-grid">
      <article v-for="feature in features" :key="feature.title" class="feature-card">
        <span class="feature-kicker">{{ feature.kicker }}</span>
        <h2>{{ feature.title }}</h2>
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

const previewRows = [
  { group: '软件工程课程群', status: '待审批', score: 'new', tone: 'info' },
  { group: '志愿活动通知群', status: '机器人在线', score: 'ok', tone: 'success' },
  { group: '社团招新讨论组', status: '风险监控', score: '32', tone: 'warn' }
];

const features = [
  {
    kicker: 'Capture',
    title: '自动理解群聊日程',
    description: '从自然聊天中提取时间、地点、参与人和任务内容，减少人工整理成本。'
  },
  {
    kicker: 'Workflow',
    title: '申请与管理员审批',
    description: '普通用户提交托管申请，管理员统一同意或拒绝，让机器人权限流转更清晰。'
  },
  {
    kicker: 'Console',
    title: '机器人和风险集中管理',
    description: '在一个控制台里查看机器人状态、群聊列表、好友列表和风险消息。'
  }
];
</script>

<style scoped>
.landing-page {
  min-height: 100vh;
  background: #f7f8fb;
  color: #17171c;
}

.landing-nav {
  width: min(1180px, calc(100% - 40px));
  margin: 0 auto;
  padding: 22px 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.brand,
.nav-actions,
.hero-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand {
  color: #17171c;
  font-weight: 760;
}

.brand-mark {
  display: inline-grid;
  place-items: center;
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: #17171c;
  color: #fff;
  font-size: 0.76rem;
  font-weight: 800;
}

.ghost-link,
.primary-link {
  min-height: 38px;
  padding: 9px 14px;
  border-radius: 8px;
  font-weight: 650;
}

.ghost-link {
  color: #424654;
  border: 1px solid #d9dce7;
  background: #fff;
}

.primary-link {
  border: 1px solid #17171c;
  background: #17171c;
  color: #fff;
  cursor: pointer;
}

.hero-section {
  width: min(1180px, calc(100% - 40px));
  margin: 48px auto 0;
  display: grid;
  grid-template-columns: minmax(0, 0.95fr) minmax(420px, 1.05fr);
  gap: 58px;
  align-items: center;
}

.eyebrow,
.feature-kicker {
  color: #6c5ce7;
  text-transform: uppercase;
  letter-spacing: 0.13em;
  font-size: 0.78rem;
  font-weight: 760;
}

.hero-copy h1 {
  margin-top: 18px;
  font-size: clamp(3rem, 7vw, 6.2rem);
  line-height: 0.96;
  letter-spacing: -0.04em;
  font-weight: 800;
  max-width: 9ch;
}

.hero-text {
  margin-top: 24px;
  max-width: 560px;
  color: #626779;
  font-size: 1.05rem;
  line-height: 1.8;
}

.hero-actions {
  margin-top: 30px;
}

.hero-primary,
.hero-secondary {
  min-height: 44px;
  padding: 11px 16px;
}

.product-frame {
  display: grid;
  grid-template-columns: 92px minmax(0, 1fr);
  min-height: 420px;
  border: 1px solid #d9dce7;
  border-radius: 14px;
  overflow: hidden;
  background: #fff;
  box-shadow: 0 24px 80px rgba(15, 16, 21, 0.12);
}

.frame-sidebar {
  padding: 24px 18px;
  background: #101116;
  display: grid;
  align-content: start;
  gap: 12px;
}

.frame-sidebar span {
  height: 34px;
  border-radius: 8px;
  background: #242633;
}

.frame-sidebar .active {
  background: #6c5ce7;
}

.frame-main {
  padding: 28px;
}

.frame-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 18px;
  border-bottom: 1px solid #eceef4;
}

.frame-header span {
  font-weight: 760;
}

.frame-table {
  display: grid;
  gap: 12px;
  margin-top: 22px;
}

.frame-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 110px 54px;
  gap: 12px;
  align-items: center;
  padding: 14px 16px;
  border: 1px solid #eceef4;
  border-radius: 10px;
  background: #fafbff;
}

.frame-row span {
  font-weight: 650;
}

.frame-row small {
  color: #626779;
}

.frame-row b {
  text-align: center;
  border-radius: 999px;
  padding: 5px 8px;
  font-size: 0.78rem;
}

.success {
  color: #255834;
  background: rgba(66, 125, 78, 0.12);
}

.info {
  color: #4f46bd;
  background: #f3f1ff;
}

.warn {
  color: #8a5800;
  background: rgba(208, 131, 32, 0.16);
}

.feature-grid {
  width: min(1180px, calc(100% - 40px));
  margin: 70px auto 80px;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.feature-card {
  padding: 22px;
  border: 1px solid #e6e8ef;
  border-radius: 12px;
  background: #fff;
}

.feature-card h2 {
  margin-top: 14px;
  font-size: 1.1rem;
  font-weight: 760;
}

.feature-card p {
  margin-top: 10px;
  color: #626779;
  line-height: 1.7;
}

@media (max-width: 980px) {
  .hero-section,
  .feature-grid {
    grid-template-columns: 1fr;
  }

  .product-frame {
    grid-template-columns: 72px minmax(0, 1fr);
    min-height: 360px;
  }
}

@media (max-width: 620px) {
  .landing-nav,
  .nav-actions,
  .hero-actions {
    align-items: stretch;
    flex-direction: column;
  }

  .landing-nav {
    gap: 14px;
  }

  .hero-section {
    margin-top: 28px;
  }

  .hero-copy h1 {
    font-size: 3rem;
  }

  .product-frame {
    grid-template-columns: 1fr;
  }

  .frame-sidebar {
    display: none;
  }

  .frame-row {
    grid-template-columns: 1fr;
  }
}
</style>
