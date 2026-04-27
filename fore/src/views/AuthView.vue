<template>
  <main class="auth-page">
    <section class="auth-hero">
      <div class="hero-visual">
        <div class="hero-overlay"></div>
        <div class="floating-card floating-card-primary">
          <span class="card-tag">群聊读取</span>
          <strong>提取聊天中的日程线索</strong>
          <p>机器人进入群组后，持续读取消息并识别时间、地点、任务等信息。</p>
        </div>
        <div class="floating-card floating-card-secondary">
          <span class="card-tag">AI 分析</span>
          <strong>问机器人，就能回忆群里的安排</strong>
          <p>支持群聊问答，也支持用户私信机器人查询日程。</p>
        </div>
      </div>

      <span class="hero-badge">MemoEcho</span>
      <h1>让群聊内容变成可被追问的日程记忆。</h1>
      <p>
        MemoEcho 可以让机器人加入群组，读取群聊天内容，提取其中的日程信息，再交给 AI 分析整理。
        当用户在群里或私信中询问机器人时，机器人会根据这些提取出的日程信息直接回答。
      </p>
      <ul class="hero-points">
        <li>机器人入群后自动读取并理解聊天上下文</li>
        <li>从自然聊天中提取日程、提醒和待办信息</li>
        <li>支持群内提问，也支持私信机器人单独查询</li>
      </ul>
    </section>

    <section class="auth-panel">
      <transition name="panel-switch" mode="out-in">
        <Login
          v-if="isLoginMode"
          @toRegister="isLoginMode = false"
        />
        <UserRegister
          v-else
          @toLogin="isLoginMode = true"
        />
      </transition>
    </section>
  </main>
</template>

<script setup>
import { ref } from 'vue';
import Login from '../components/Login.vue';
import UserRegister from '../components/UserRegister.vue';

const isLoginMode = ref(true);
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: minmax(320px, 42vw) minmax(360px, 1fr);
  background: #f7f8fb;
}

.auth-hero {
  min-height: 100vh;
  padding: 34px;
  background: #0f1015;
  color: #f7f7fb;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.hero-visual {
  position: relative;
  min-height: 220px;
  margin-bottom: 34px;
  border-radius: 8px;
  overflow: hidden;
  background:
    linear-gradient(180deg, rgba(8, 10, 18, 0.1), rgba(8, 10, 18, 0.55)),
    url('/auth/memoecho-bg.png') center/cover no-repeat;
  border: 1px solid #262833;
  box-shadow: 0 24px 80px rgba(0, 0, 0, 0.28);
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(135deg, rgba(108, 92, 231, 0.26), transparent 48%),
    linear-gradient(180deg, rgba(8, 10, 18, 0.02), rgba(8, 10, 18, 0.62));
}

.floating-card {
  position: absolute;
  z-index: 1;
  max-width: 280px;
  padding: 14px 16px;
  border-radius: 8px;
  background: rgba(23, 25, 35, 0.88);
  backdrop-filter: blur(12px);
  border: 1px solid #343746;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.22);
}

.floating-card strong {
  display: block;
  margin-top: 10px;
  font-size: 1.05rem;
  color: #fff;
}

.floating-card p {
  margin-top: 8px;
  font-size: 0.9rem;
  line-height: 1.6;
  color: #b9bdca;
}

.floating-card-primary {
  left: 24px;
  bottom: 24px;
}

.floating-card-secondary {
  right: 24px;
  top: 24px;
}

.card-tag {
  display: inline-flex;
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(108, 92, 231, 0.18);
  color: #d8d2ff;
  font-size: 0.78rem;
  font-weight: 700;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  padding: 8px 14px;
  border-radius: 999px;
  background: rgba(108, 92, 231, 0.18);
  color: #d8d2ff;
  font-size: 0.85rem;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.auth-hero h1 {
  margin-top: 20px;
  font-size: clamp(2.1rem, 4.4vw, 4.1rem);
  line-height: 1.08;
  font-weight: 760;
  max-width: 13ch;
  color: #fff;
}

.auth-hero p {
  margin-top: 20px;
  max-width: 44ch;
  font-size: 1rem;
  line-height: 1.8;
  color: #b9bdca;
}

.hero-points {
  margin-top: 28px;
  padding: 0;
  list-style: none;
  display: grid;
  gap: 14px;
}

.hero-points li {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #f7f7fb;
}

.hero-points li::before {
  content: '';
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #6c5ce7;
  box-shadow: 0 0 0 5px rgba(108, 92, 231, 0.18);
}

.auth-panel {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 40px clamp(22px, 6vw, 88px);
}

.panel-switch-enter-active,
.panel-switch-leave-active {
  transition: opacity 0.22s ease, transform 0.22s ease;
}

.panel-switch-enter-from,
.panel-switch-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

@media (max-width: 960px) {
  .auth-page {
    grid-template-columns: 1fr;
  }

  .auth-hero {
    min-height: auto;
    padding: 28px;
  }

  .auth-hero h1 {
    max-width: 14ch;
  }

  .hero-visual {
    min-height: 260px;
  }

  .auth-panel {
    min-height: auto;
    padding: 28px;
  }
}

@media (max-width: 640px) {
  .hero-visual {
    min-height: 320px;
  }

  .floating-card {
    max-width: calc(100% - 32px);
    padding: 14px 16px;
  }

  .floating-card-primary {
    left: 16px;
    right: 16px;
    bottom: 16px;
  }

  .floating-card-secondary {
    display: none;
  }
}
</style>
