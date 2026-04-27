<template>
  <div class="auth-card">
    <div class="card-header">
      <p class="eyebrow">账户登录</p>
      <h2>进入 Memo Echo 控制台</h2>
      <p class="subtitle">使用你的账号继续访问机器人网关管理系统。</p>
    </div>

    <form class="auth-form" @submit.prevent="handleLogin">
      <label class="field">
        <span>用户ID</span>
        <input
          v-model.trim="loginForm.userId"
          type="text"
          autocomplete="username"
          inputmode="numeric"
          placeholder="请输入数字用户ID"
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
        <span class="meta-tip">对接 persistence /user/login</span>
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
import { apiFetch } from '@/utils/api';
import { setAuthToken, setCurrentUserId } from '@/utils/auth';

defineEmits(['toRegister']);
const router = useRouter();

const loginForm = reactive({
  userId: '',
  password: ''
});
const errorMsg = ref('');
const successMsg = ref('');
const showPassword = ref(false);
const rememberMe = ref(true);
const isSubmitting = ref(false);

const handleLogin = async () => {
  const parsedUserId = Number(loginForm.userId);

  if (!loginForm.userId || !loginForm.password) {
    successMsg.value = '';
    errorMsg.value = '请输入完整的用户ID和密码。';
    return;
  }

  if (!Number.isInteger(parsedUserId) || parsedUserId <= 0) {
    successMsg.value = '';
    errorMsg.value = '用户ID必须是正整数。';
    return;
  }

  errorMsg.value = '';
  successMsg.value = '';
  isSubmitting.value = true;

  try {
    const response = await apiFetch('/api/persistence/user/login', {
      method: 'POST',
      auth: false,
      json: {
        userId: parsedUserId,
        password: loginForm.password
      }
    });

    const result = await response.json().catch(() => null);
    const data = result?.data ?? null;

    if (!response.ok) {
      errorMsg.value = result?.message || '登录失败，请检查用户ID和密码。';
      return;
    }

    if (result?.code !== 200) {
      errorMsg.value = result?.message || data?.message || '登录失败，请检查用户ID和密码。';
      return;
    }

    if (!data?.token) {
      errorMsg.value = '登录响应缺少 token，无法建立会话。';
      return;
    }

    setAuthToken(data.token, rememberMe.value);
    setCurrentUserId(String(data.userId ?? parsedUserId), rememberMe.value);
    successMsg.value = rememberMe.value ? '登录成功，登录状态已保留。' : '登录成功，当前会话有效。';
    await router.push('/dashboard');
  } catch (error) {
    errorMsg.value = '无法连接登录服务，请确认 persistence 已启动。';
  } finally {
    isSubmitting.value = false;
  }
};
</script>

<style scoped>
.auth-card {
  width: min(100%, 430px);
  padding: 30px;
  border-radius: var(--radius-card);
  background: #fff;
  border: 1px solid #e6e8ef;
  box-shadow: 0 18px 50px rgba(15, 16, 21, 0.08);
}

.card-header h2 {
  margin-top: 8px;
  font-size: clamp(1.65rem, 3vw, 2.05rem);
  line-height: 1.15;
  color: #17171c;
  font-weight: 760;
}

.eyebrow {
  color: #6c5ce7;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  font-size: 0.78rem;
  font-weight: 700;
}

.subtitle {
  margin-top: 12px;
  color: #626779;
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
  color: #2b2f3d;
  font-weight: 600;
}

input {
  display: block;
  width: 100%;
  border: 1px solid #d9dce7;
  border-radius: var(--radius-control);
  padding: 14px 16px;
  background: #fff;
  color: #17171c;
  box-sizing: border-box;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, transform 0.2s ease;
}

input:focus {
  outline: none;
  border-color: rgba(108, 92, 231, 0.62);
  box-shadow: 0 0 0 4px rgba(108, 92, 231, 0.12);
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
  background: #f3f1ff;
  color: #4f46bd;
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
  color: #626779;
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
  border-radius: var(--radius-control);
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
  border-radius: var(--radius-control);
  padding: 14px 18px;
  background: #6c5ce7;
  color: #fff;
  font-weight: 700;
  font-size: 1rem;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease, opacity 0.2s ease;
  box-shadow: none;
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
  color: #626779;
  text-align: center;
}

.inline-action {
  border: 0;
  background: transparent;
  color: #4f46bd;
  font-weight: 700;
  cursor: pointer;
}

@media (max-width: 640px) {
  .auth-card {
    padding: 24px;
    border-radius: var(--radius-card);
  }

  .form-meta {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
