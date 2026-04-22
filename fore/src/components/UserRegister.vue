<template>
  <div class="auth-box">
    <h2>Memo Echo</h2>
    <p class="subtitle">新用户注册</p>

    <div class="form-container">
      <div class="form-row">
        <label>用户名</label>
        <div class="input-wrapper">
          <input
              v-model="regForm.username"
              :class="{ 'error-border': errors.username }"
              @blur="validateField('username')"
              type="text"
              placeholder="设置用户名"
          />
          <p v-if="errors.username" class="error-msg">{{ errorTips.username }}</p>
        </div>
      </div>

      <div class="form-row">
        <label>邮箱账号</label>
        <div class="input-wrapper">
          <input
              v-model="regForm.email"
              :class="{ 'error-border': errors.email }"
              @blur="validateField('email')"
              type="text"
              placeholder="name@example.com"
          />
          <p v-if="errors.email" class="error-msg">{{ errorTips.email }}</p>
        </div>
      </div>

      <div class="form-row">
        <label>设置密码</label>
        <div class="input-wrapper">
          <input
              v-model="regForm.password"
              :class="{ 'error-border': errors.password }"
              @blur="validateField('password')"
              type="password"
              placeholder="请输入密码"
          />
          <p v-if="errors.password" class="error-msg">{{ errorTips.password }}</p>
          <p v-else class="input-hint">密码为字母和数字混合，至少8位</p>
        </div>
      </div>

      <div class="form-row">
        <label>确认密码</label>
        <div class="input-wrapper">
          <input
              v-model="regForm.confirm"
              :class="{ 'error-border': errors.confirm }"
              @blur="validateField('confirm')"
              type="password"
              placeholder="请再次输入密码"
          />
          <p v-if="errors.confirm" class="error-msg">{{ errorTips.confirm }}</p>
        </div>
      </div>
    </div>

    <div class="action-area">
      <button class="main-btn" @click="doRegister">立即注册</button>
      <p class="switch-text">
        已有账号？<span @click="$emit('toLogin')">返回登录</span>
      </p>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue';

const emit = defineEmits(['toLogin']);

const regForm = reactive({
  username: '',
  email: '',
  password: '',
  confirm: ''
});

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

  // ✨ 1. 个性化非空判断
  if (!val) {
    errors[field] = true;
    // 根据不同的字段设置不同的空值文案
    const emptyMsgs = {
      username: '用户名不能为空',
      email: '请输入邮箱地址',
      password: '请设置你的登录密码',
      confirm: '请再次输入密码以确认'
    };
    errorTips[field] = emptyMsgs[field];
    return false;
  }

  // 2. 格式校验逻辑
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
  const fields = ['username', 'email', 'password', 'confirm'];
  const allValid = fields.every(f => validateField(f));

  if (allValid) {
    alert("验证成功！");
  }
};
</script>

<style scoped>
/* 样式部分保持不变，确保布局稳定 */
.auth-box { width: 450px; padding: 40px; background: #fff; border-radius: 12px; box-shadow: 0 8px 25px rgba(0,0,0,0.05); }
h2 { margin: 0; text-align: center; font-size: 26px; }
.subtitle { text-align: center; color: #888; margin-bottom: 30px; font-size: 14px; }
.form-row { display: flex; align-items: flex-start; margin-bottom: 24px; }
.form-row label { width: 80px; margin-right: 15px; text-align: right; font-size: 14px; line-height: 40px; font-weight: bold; }
.input-wrapper { flex: 1; position: relative; }
input { width: 100%; height: 40px; padding: 0 12px; border: 1px solid #dcdfe6; border-radius: 4px; box-sizing: border-box; }
.error-border { border-color: #f56c6c !important; background-color: #fff9f9; }
.error-msg { color: #f56c6c; font-size: 12px; margin: 4px 0 0; position: absolute; top: 40px; }
.input-hint { font-size: 12px; color: #909399; margin: 4px 0 0; }
.action-area { display: flex; flex-direction: column; align-items: center; margin-top: 30px; }
.main-btn { width: 100%; height: 44px; background-color: #409eff; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; font-weight: bold; }
.switch-text { margin-top: 15px; font-size: 13px; color: #606266; }
.switch-text span { color: #409eff; cursor: pointer; font-weight: bold; }
</style>