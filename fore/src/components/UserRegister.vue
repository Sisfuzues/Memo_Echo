<template>
  <div class="auth-card">
    <div class="card-header">
      <p class="eyebrow">创建账号</p>
      <h2>注册新的管理账号</h2>
      <p class="subtitle">先完成基础信息校验，后续可以继续接入真实注册接口。</p>
    </div>

    <form class="auth-form" @submit.prevent="doRegister">
      <label class="field">
        <span>用户名</span>
        <input
          v-model.trim="regForm.username"
          :class="{ 'error-border': errors.username }"
          @blur="validateField('username')"
          type="text"
          placeholder="设置用户名"
        />
        <p v-if="errors.username" class="error-msg">{{ errorTips.username }}</p>
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
        <p v-else class="input-hint">密码需要同时包含字母和数字，长度至少 8 位。</p>
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

      <p v-if="submitMessage" class="feedback">{{ submitMessage }}</p>

      <button class="submit-btn" type="submit">立即注册</button>
    </form>

    <p class="switch-text">
      已有账号？
      <button type="button" class="inline-action" @click="$emit('toLogin')">返回登录</button>
    </p>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';

defineEmits(['toLogin']);

const regForm = reactive({
  username: '',
  email: '',
  password: '',
  confirm: ''
});
const showPassword = ref(false);
const submitMessage = ref('');

const errors = reactive({
  username: false,
  email: false,
  password: false,
  confirm: false
});

const errorTips = reactive({
  username: '',
  email: '',
  password: '',
  confirm: ''
});

const validateField = (field) => {
  const val = regForm[field].trim();
  errors[field] = false;
  errorTips[field] = '';

  if (!val) {
    errors[field] = true;
    const emptyMsgs = {
      username: '用户名不能为空',
      email: '请输入邮箱地址',
      password: '请设置你的登录密码',
      confirm: '请再次输入密码以确认'
    };
    errorTips[field] = emptyMsgs[field];
    return false;
  }

  const EMAIL_REG = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  const PWD_REG = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/;

  if (field === 'email' && !EMAIL_REG.test(val)) {
    errors.email = true;
    errorTips.email = '邮箱格式看起来不太对';
    return false;
  }

  if (field === 'password' && !PWD_REG.test(val)) {
    errors.password = true;
    errorTips.password = '密码需要包含字母和数字，且至少为8位';
    return false;
  }

  if (field === 'confirm' && val !== regForm.password) {
    errors.confirm = true;
    errorTips.confirm = '两次输入的密码不匹配';
    return false;
  }

  return true;
};

const doRegister = () => {
  submitMessage.value = '';
  const fields = ['username', 'email', 'password', 'confirm'];
  const allValid = fields.every((f) => validateField(f));

  if (allValid) {
    submitMessage.value = '注册信息已通过前端校验，可以接入后端注册接口。';
  }
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
