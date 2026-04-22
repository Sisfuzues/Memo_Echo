<template>
  <div class="auth-box">
    <h2>Memo Echo</h2>
    <p class="subtitle">机器人网关管理系统</p>

    <div class="form-item">
      <input v-model="loginForm.username" type="text" placeholder="请输入账号" />
    </div>
    <div class="form-item">
      <input v-model="loginForm.password" type="password" placeholder="请输入密码" />
    </div>

    <button class="login-btn" @click="handleLogin">立即登录</button>

    <p class="switch-text">
      还没有账号？<span @click="$emit('toRegister')">立即注册</span>
    </p>

    <p v-if="errorMsg" class="error-tips">{{ errorMsg }}</p>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';

// 定义向外发出的事件
defineEmits(['toRegister']);

const loginForm = reactive({
  username: '',
  password: ''
});
const errorMsg = ref('');

const handleLogin = () => {
  if (!loginForm.username || !loginForm.password) {
    errorMsg.value = '账号密码不能为空哦！';
  } else {
    errorMsg.value = '';
    console.log('执行登录逻辑...');
  }
};
</script>

<style scoped>
/* 这里保留你截图里的样式，重点是增加 switch-text 样式 */
.auth-box { width: 350px; margin: 100px auto; padding: 40px; border-radius: 8px; box-shadow: 0 0 20px rgba(0,0,0,0.05); background: #fff; text-align: center; }
.subtitle { color: #666; margin-bottom: 20px; }
.form-item { margin-bottom: 15px; }
input { width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }
.login-btn { width: 100%; padding: 12px; background: #409eff; color: white; border: none; border-radius: 4px; cursor: pointer; }
.switch-text { margin-top: 15px; font-size: 14px; color: #666; }
.switch-text span { color: #409eff; cursor: pointer; }
.error-tips { color: #f56c6c; font-size: 12px; margin-top: 10px; }
</style>