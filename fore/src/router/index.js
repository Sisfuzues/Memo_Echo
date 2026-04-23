import { createRouter, createWebHistory } from 'vue-router'
import AuthView from '../views/AuthView.vue'

const routes = [
    {
        path: '/',
        name: 'Auth',
        component: AuthView
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
