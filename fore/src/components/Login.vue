<template>
  <div class="auth-card">
    <div class="card-header">
      <p class="eyebrow">账户登录</p>
      <h2>进入 Memo Echo 控制台</h2>
      <p class="subtitle">使用你的账号继续访问机器人网关管理系统。</p>
    </div>

    <form class="auth-form" @submit.prevent="handleLogin">
      <label class="field">
        <span>账号</span>
        <input
          v-model.trim="loginForm.username"
          type="text"
          autocomplete="username"
          placeholder="请输入账号或邮箱"
        />
      </label>

      <label class="field">
        <span>密码</span>
        <div class="password-field">
          <input
            v-model="loginForm.password"
            :type="showPassword ? 'text' : 'password'"
            autocomplete="current-password"
            placeholder="请输入密码"
            @keydown.enter="handleLogin"
          />
          <button
            class="text-btn"
            type="button"
            @click="showPassword = !showPassword"
          >
            {{ showPassword ? '隐藏' : '显示' }}
          </button>
        </div>
      </label>

      <div class="form-meta">
        <label class="remember-me">
          <input v-model="rememberMe" type="checkbox" />
          <span>记住本次登录</span>
        </label>
        <span class="meta-tip">后续可接入真实鉴权接口</span>
      </div>

      <p v-if="errorMsg" class="feedback feedback-error">{{ errorMsg }}</p>
      <p v-else-if="successMsg" class="feedback feedback-success">{{ successMsg }}</p>

      <button class="submit-btn" type="submit" :disabled="isSubmitting">
        {{ isSubmitting ? '登录中...' : '立即登录' }}
      </button>
    </form>

    <p class="switch-text">
      还没有账号？
      <button type="button" class="inline-action" @click="$emit('toRegister')">创建账号</button>
    </p>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';

defineEmits(['toRegister']);
const router = useRouter();

const loginForm = reactive({
  username: '',
  password: ''
});
const errorMsg = ref('');
const successMsg = ref('');
const showPassword = ref(false);
const rememberMe = ref(true);
const isSubmitting = ref(false);

const handleLogin = async () => {
  if (!loginForm.username || !loginForm.password) {
    successMsg.value = '';
    errorMsg.value = '请输入完整的账号和密码。';
    return;
  }

  errorMsg.value = '';
  successMsg.value = '';
  isSubmitting.value = true;

  await new Promise((resolve) => setTimeout(resolve, 700));

  isSubmitting.value = false;
  successMsg.value = rememberMe.value ? '登录成功，已为你保留登录状态偏好。' : '登录成功，正在跳转。';
  router.push('/dashboard');
};
</script>

<style scoped>
.auth-card {
  width: min(100%, 430px);
  padding: 32px;
  border-radius: 28px;
  background: rgba(255, 250, 243, 0.88);
  border: 1px solid rgba(138, 109, 59, 0.12);
  box-shadow: 0 24px 80px rgba(71, 46, 16, 0.14);
  backdrop-filter: blur(18px);
}

.card-header h2 {
  margin-top: 8px;
  font-size: clamp(1.8rem, 3vw, 2.3rem);
  line-height: 1.15;
  color: var(--auth-text-primary);
  font-weight: 700;
}

.eyebrow {
  color: var(--auth-accent-deep);
  text-transform: uppercase;
  letter-spacing: 0.12em;
  font-size: 0.78rem;
  font-weight: 700;
}

.subtitle {
  margin-top: 12px;
  color: var(--auth-text-secondary);
}

.auth-form {
  margin-top: 28px;
}

.field {
  display: block;
  margin-bottom: 18px;
}

.field span {
  display: block;
  margin-bottom: 8px;
  color: var(--auth-text-primary);
  font-weight: 600;
}

input {
  display: block;
  width: 100%;
  border: 1px solid rgba(118, 89, 43, 0.14);
  border-radius: 16px;
  padding: 14px 16px;
  background: rgba(255, 255, 255, 0.92);
  color: var(--auth-text-primary);
  box-sizing: border-box;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, transform 0.2s ease;
}

input:focus {
  outline: none;
  border-color: rgba(214, 111, 42, 0.65);
  box-shadow: 0 0 0 4px rgba(255, 159, 28, 0.18);
  transform: translateY(-1px);
}

.password-field {
  position: relative;
  width: 100%;
}

.password-field input {
  padding-right: 88px;
}

.text-btn {
  position: absolute;
  top: 50%;
  right: 12px;
  transform: translateY(-50%);
  min-width: 56px;
  height: 34px;
  padding: 0 12px;
  border: 0;
  border-radius: 999px;
  background: rgba(214, 111, 42, 0.12);
  color: var(--auth-accent-deep);
  font-weight: 600;
  cursor: pointer;
}

.form-meta {
  margin: 6px 0 18px;
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
  font-size: 0.9rem;
  color: var(--auth-text-secondary);
}

.remember-me {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.remember-me input {
  width: 16px;
  height: 16px;
  padding: 0;
}

.meta-tip {
  text-align: right;
}

.feedback {
  padding: 12px 14px;
  border-radius: 14px;
  margin-bottom: 16px;
  font-size: 0.92rem;
}

.feedback-error {
  color: #a63c28;
  background: rgba(236, 97, 63, 0.14);
}

.feedback-success {
  color: #22623d;
  background: rgba(96, 182, 124, 0.14);
}

.submit-btn {
  width: 100%;
  border: 0;
  border-radius: 18px;
  padding: 14px 18px;
  background: linear-gradient(135deg, var(--auth-accent), var(--auth-accent-strong));
  color: #1d1204;
  font-weight: 700;
  font-size: 1rem;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease, opacity 0.2s ease;
  box-shadow: 0 16px 32px rgba(214, 111, 42, 0.28);
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-1px);
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: wait;
}

.switch-text {
  margin-top: 20px;
  color: var(--auth-text-secondary);
  text-align: center;
}

.inline-action {
  border: 0;
  background: transparent;
  color: var(--auth-accent-deep);
  font-weight: 700;
  cursor: pointer;
}

@media (max-width: 640px) {
  .auth-card {
    padding: 24px;
    border-radius: 24px;
  }

  .form-meta {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
