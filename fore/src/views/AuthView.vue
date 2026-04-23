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
  grid-template-columns: minmax(0, 1.1fr) minmax(320px, 460px);
  gap: 48px;
  align-items: center;
  padding: 48px clamp(20px, 4vw, 56px);
}

.auth-hero {
  color: var(--auth-text-primary);
  padding-right: clamp(0px, 4vw, 48px);
}

.hero-visual {
  position: relative;
  min-height: 360px;
  margin-bottom: 28px;
  border-radius: 32px;
  overflow: hidden;
  background:
    linear-gradient(180deg, rgba(27, 15, 6, 0.08), rgba(27, 15, 6, 0.3)),
    url('/auth/memoecho-bg.png') center/cover no-repeat;
  box-shadow: 0 28px 80px rgba(67, 42, 14, 0.24);
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(135deg, rgba(244, 109, 28, 0.32), transparent 44%),
    linear-gradient(180deg, rgba(19, 10, 4, 0.04), rgba(19, 10, 4, 0.5));
}

.floating-card {
  position: absolute;
  z-index: 1;
  max-width: 280px;
  padding: 18px 20px;
  border-radius: 22px;
  background: rgba(255, 248, 240, 0.88);
  backdrop-filter: blur(12px);
  box-shadow: 0 12px 32px rgba(40, 24, 7, 0.16);
}

.floating-card strong {
  display: block;
  margin-top: 10px;
  font-size: 1.05rem;
}

.floating-card p {
  margin-top: 8px;
  font-size: 0.9rem;
  line-height: 1.6;
  color: #5f4c3b;
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
  background: rgba(214, 111, 42, 0.14);
  color: var(--auth-accent-deep);
  font-size: 0.78rem;
  font-weight: 700;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  padding: 8px 14px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.14);
  color: var(--auth-accent-strong);
  font-size: 0.85rem;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.auth-hero h1 {
  margin-top: 24px;
  font-size: clamp(2.6rem, 5vw, 4.8rem);
  line-height: 1.02;
  font-weight: 700;
  max-width: 10ch;
}

.auth-hero p {
  margin-top: 20px;
  max-width: 44ch;
  font-size: 1rem;
  line-height: 1.8;
  color: var(--auth-text-secondary);
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
  color: var(--auth-text-primary);
}

.hero-points li::before {
  content: '';
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--auth-accent), var(--auth-accent-strong));
  box-shadow: 0 0 0 6px rgba(255, 159, 28, 0.14);
}

.auth-panel {
  display: flex;
  justify-content: center;
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
    gap: 28px;
    padding-top: 32px;
    padding-bottom: 32px;
  }

  .auth-hero {
    padding-right: 0;
  }

  .auth-hero h1 {
    max-width: 14ch;
  }

  .hero-visual {
    min-height: 300px;
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
