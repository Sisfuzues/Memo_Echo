<template>
  <div class="login-wrapper">
    <div class="login-box">
      <h1>Memo Echo</h1>
      <p>机器人网关管理系统</p>

      <div class="form-item">
        <input v-model="loginForm.username" type="text" placeholder="请输入账号" />
      </div>

      <div class="form-item">
        <input v-model="loginForm.password" type="password" placeholder="请输入密码" />
      </div>

      <button @click="onLogin" :disabled="loading">
        {{ loading ? '登录中...' : '立即登录' }}
      </button>

      <div class="tips" v-if="errorMsg">{{ errorMsg }}</div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'

// 1. 定义响应式数据（对应你 API 里需要的字段）
const loginForm = reactive({
  username: '',
  password: ''
})

const loading = ref(false)
const errorMsg = ref('')

// 2. 登录逻辑处理
const onLogin = () => {
  if (!loginForm.username || !loginForm.password) {
    errorMsg.value = '账号密码不能为空哦！'
    return
  }

  loading.value = true
  errorMsg.value = ''

  // 模拟 API 请求过程
  setTimeout(() => {
    loading.value = false

    // 这里先做假校验，以后对接你 SpringBoot 的 /api/login
    if (loginForm.username === 'admin' && loginForm.password === '123456') {
      alert('登录成功！正在获取 Token...')

      // 模拟存入你那张图片里的 Token
      const mockToken = 'U~yiYdCgh6bRtl-~'
      localStorage.setItem('user_token', mockToken)

      // 登录成功后的跳转逻辑（比如去监控页）
      // router.push('/dashboard')
    } else {
      errorMsg.value = '用户名或密码错误，请检查！'
    }
  }, 1000)
}
</script>

<style scoped>
.login-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: #f0f2f5;
}
.login-box {
  background: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  width: 350px;
  text-align: center;
}
.form-item { margin-bottom: 20px; }
input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-sizing: border-box;
}
button {
  width: 100%;
  padding: 12px;
  background: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
button:disabled { background: #a0cfff; }
.tips { color: #f56c6c; margin-top: 15px; font-size: 14px; }
</style>