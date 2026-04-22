import { createRouter, createWebHistory } from 'vue-router'
// 导入你的登录组件
import Login from '../components/Login.vue'

const routes = [
    {
        path: '/',
        name: 'Login',
        component: Login
    },
    {
        path: '/dashboard',
        name: 'Dashboard',
        // 建议：主页可以使用路由懒加载
        component: () => import('../views/Dashboard.vue')
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router