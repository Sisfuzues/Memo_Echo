<template>
  <div class="auth-card">
    <div class="card-header">
      <p class="eyebrow">创建账号</p>
      <h2>注册新的管理账号</h2>
      <p class="subtitle">按 persistence 的注册协议创建账号，需先获取邮箱验证码。</p>
    </div>

    <form class="auth-form" @submit.prevent="doRegister">
      <label class="field">
        <span>用户ID</span>
        <input
          v-model.trim="regForm.userId"
          :class="{ 'error-border': errors.userId }"
          @blur="validateField('userId')"
          type="text"
          inputmode="numeric"
          placeholder="设置数字用户ID"
        />
        <p v-if="errors.userId" class="error-msg">{{ errorTips.userId }}</p>
      </label>

      <label class="field">
        <span>邮箱账号</span>
        <input
          v-model.trim="regForm.email"
          :class="{ 'error-border': errors.email }"
          @blur="validateField('email')"
          type="email"
          placeholder="name@example.com"
        />
        <p v-if="errors.email" class="error-msg">{{ errorTips.email }}</p>
      </label>

      <label class="field">
        <span>邮箱验证码</span>
        <div class="code-field">
          <input
            v-model.trim="regForm.code"
            :class="{ 'error-border': errors.code }"
            @blur="validateField('code')"
            type="text"
            placeholder="请输入6位验证码"
          />
          <button
            class="secondary-btn"
            type="button"
            :disabled="isSendingCode || countdown > 0"
            @click="sendCode"
          >
            {{ isSendingCode ? '发送中...' : countdown > 0 ? `${countdown}s后重发` : '发送验证码' }}
          </button>
        </div>
        <p v-if="errors.code" class="error-msg">{{ errorTips.code }}</p>
        <p v-else class="input-hint">验证码会发送到填写的邮箱，后端校验以 persistence 为准。</p>
      </label>

      <label class="field">
        <span>设置密码</span>
        <div class="password-field">
          <input
            v-model="regForm.password"
            :class="{ 'error-border': errors.password }"
            @blur="validateField('password')"
            :type="showPassword ? 'text' : 'password'"
            placeholder="请输入密码"
          />
          <button
            class="text-btn"
            type="button"
            @click="showPassword = !showPassword"
          >
            {{ showPassword ? '隐藏' : '显示' }}
          </button>
        </div>
        <p v-if="errors.password" class="error-msg">{{ errorTips.password }}</p>
        <p v-else class="input-hint">密码长度需在 6 到 20 位之间，规则以 persistence 后端校验为准。</p>
      </label>

      <label class="field">
        <span>确认密码</span>
        <input
          v-model="regForm.confirm"
          :class="{ 'error-border': errors.confirm }"
          @blur="validateField('confirm')"
          :type="showPassword ? 'text' : 'password'"
          placeholder="请再次输入密码"
        />
        <p v-if="errors.confirm" class="error-msg">{{ errorTips.confirm }}</p>
      </label>

      <p
        v-if="submitMessage"
        class="feedback"
        :class="submitIsError ? 'feedback-error' : 'feedback-success'"
      >
        {{ submitMessage }}
      </p>

      <button class="submit-btn" type="submit" :disabled="isSubmitting">
        {{ isSubmitting ? '注册中...' : '立即注册' }}
      </button>
    </form>

    <p class="switch-text">
      已有账号？
      <button type="button" class="inline-action" @click="$emit('toLogin')">返回登录</button>
    </p>
  </div>
</template>

<script setup>
import { onBeforeUnmount, reactive, ref } from 'vue';
import { apiFetch } from '@/utils/api';

defineEmits(['toLogin']);

const regForm = reactive({
  userId: '',
  email: '',
  code: '',
  password: '',
  confirm: ''
});
const showPassword = ref(false);
const submitMessage = ref('');
const submitIsError = ref(false);
const isSubmitting = ref(false);
const isSendingCode = ref(false);
const countdown = ref(0);
let countdownTimer = null;

const errors = reactive({
  userId: false,
  email: false,
  code: false,
  password: false,
  confirm: false
});

const errorTips = reactive({
  userId: '',
  email: '',
  code: '',
  password: '',
  confirm: ''
});

const validateField = (field) => {
  const rawVal = String(regForm[field] ?? '');
  const normalizedVal = ['userId', 'email', 'code'].includes(field) ? rawVal.trim() : rawVal;
  errors[field] = false;
  errorTips[field] = '';

  if (!normalizedVal) {
    errors[field] = true;
    const emptyMsgs = {
      userId: '请输入用户ID',
      email: '请输入邮箱地址',
      code: '请输入邮箱验证码',
      password: '请设置你的登录密码',
      confirm: '请再次输入密码以确认'
    };
    errorTips[field] = emptyMsgs[field];
    return false;
  }

  const EMAIL_REG = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  const PWD_REG = /^.{6,20}$/;
  const CODE_REG = /^\d{6}$/;
  const userIdNum = Number(regForm.userId);

  if (field === 'userId' && (!Number.isInteger(userIdNum) || userIdNum <= 0)) {
    errors.userId = true;
    errorTips.userId = '用户ID必须是正整数';
    return false;
  }

  if (field === 'email' && !EMAIL_REG.test(normalizedVal)) {
    errors.email = true;
    errorTips.email = '邮箱格式看起来不太对';
    return false;
  }

  if (field === 'code' && !CODE_REG.test(normalizedVal)) {
    errors.code = true;
    errorTips.code = '验证码应为6位数字';
    return false;
  }

  if (field === 'password' && !PWD_REG.test(normalizedVal)) {
    errors.password = true;
    errorTips.password = '密码长度需要在6到20位之间';
    return false;
  }

  if (field === 'confirm' && rawVal !== regForm.password) {
    errors.confirm = true;
    errorTips.confirm = '两次输入的密码不匹配';
    return false;
  }

  return true;
};

const startCountdown = () => {
  countdown.value = 60;
  countdownTimer = window.setInterval(() => {
    countdown.value -= 1;
    if (countdown.value <= 0) {
      window.clearInterval(countdownTimer);
      countdownTimer = null;
    }
  }, 1000);
};

const sendCode = async () => {
  submitMessage.value = '';
  submitIsError.value = false;

  if (!validateField('email')) {
    return;
  }

  isSendingCode.value = true;

  try {
    const response = await apiFetch('/api/persistence/email/send-email', {
      method: 'POST',
      auth: false,
      json: {
        email: regForm.email.trim()
      }
    });

    const result = await response.json().catch(() => null);
    const data = result?.data ?? null;

    if (!response.ok) {
      submitIsError.value = true;
      submitMessage.value = result?.message || '验证码发送失败，请稍后重试。';
      return;
    }

    if (result?.code !== 200 || data?.code !== '200') {
      submitIsError.value = true;
      submitMessage.value = result?.message || data?.message || '验证码发送失败，请检查邮箱。';
      return;
    }

    submitMessage.value = data.message || '验证码已发送，请检查邮箱。';
    startCountdown();
  } catch (error) {
    submitIsError.value = true;
    submitMessage.value = '无法连接验证码服务，请确认 persistence 已启动。';
  } finally {
    isSendingCode.value = false;
  }
};

const doRegister = async () => {
  submitMessage.value = '';
  submitIsError.value = false;
  const fields = ['userId', 'email', 'code', 'password', 'confirm'];
  const allValid = fields.every((f) => validateField(f));

  if (!allValid) {
    return;
  }

  isSubmitting.value = true;

  try {
    const response = await apiFetch('/api/persistence/user/register', {
      method: 'POST',
      auth: false,
      json: {
        userId: Number(regForm.userId),
        email: regForm.email.trim(),
        code: regForm.code.trim(),
        password: regForm.password
      }
    });

    const result = await response.json().catch(() => null);
    const data = result?.data ?? null;

    if (!response.ok) {
      submitIsError.value = true;
      submitMessage.value = result?.message || '注册失败，请检查输入信息。';
      return;
    }

    if (result?.code !== 200) {
      submitIsError.value = true;
      submitMessage.value = result?.message || data?.message || '注册失败，请检查输入信息。';
      return;
    }

    const message = data?.message || '';
    submitMessage.value = message || '注册请求已提交。';
    submitIsError.value = message !== '注册成功';

    if (message === '注册成功') {
      regForm.code = '';
      regForm.password = '';
      regForm.confirm = '';
    }
  } catch (error) {
    submitIsError.value = true;
    submitMessage.value = '无法连接注册服务，请确认 persistence 已启动。';
  } finally {
    isSubmitting.value = false;
  }
};

onBeforeUnmount(() => {
  if (countdownTimer) {
    window.clearInterval(countdownTimer);
  }
});
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
  font-size: clamp(1.8rem, 3vw, 2.2rem);
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

.code-field {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 12px;
  align-items: center;
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

.secondary-btn {
  border: 0;
  border-radius: 16px;
  height: 48px;
  padding: 0 16px;
  background: rgba(214, 111, 42, 0.12);
  color: var(--auth-accent-deep);
  font-weight: 700;
  cursor: pointer;
  white-space: nowrap;
}

.secondary-btn:disabled {
  opacity: 0.65;
  cursor: wait;
}

.error-border {
  border-color: #d45536;
  background: rgba(246, 115, 79, 0.08);
}

.error-msg {
  margin-top: 6px;
  color: #a63c28;
  font-size: 0.86rem;
}

.input-hint {
  margin-top: 6px;
  color: var(--auth-text-secondary);
  font-size: 0.86rem;
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

.submit-btn:disabled {
  opacity: 0.7;
  cursor: wait;
}

.input-hint {
  margin-top: 6px;
  color: var(--auth-text-secondary);
  font-size: 0.88rem;
}

.feedback {
  padding: 12px 14px;
  border-radius: 14px;
  margin-bottom: 16px;
  font-size: 0.92rem;
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
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  box-shadow: 0 16px 32px rgba(214, 111, 42, 0.28);
}

.submit-btn:hover {
  transform: translateY(-1px);
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
}
</style>
