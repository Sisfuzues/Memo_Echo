<template>
  <div class="main-container">
    <Login v-if="isLoginMode" @toRegister="isLoginMode = false" />
    <UserRegister v-else @toLogin="isLoginMode = true" />
  </div>
</template>

<script setup>
import { ref } from 'vue';
import Login from './components/Login.vue';
import UserRegister from './components/UserRegister.vue';

const isLoginMode = ref(true);
</script>

<style>
body {
  margin: 0;
  background-color: #f5f7fa;
}
.main-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
}
</style>