import { createRouter, createWebHistory } from 'vue-router'
import AuthView from '../views/AuthView.vue'
import { getAuthToken } from '@/utils/auth'

const routes = [
    {
        path: '/',
        name: 'Auth',
        component: AuthView,
        meta: { guestOnly: true }
    },
    {
        path: '/dashboard',
        name: 'Dashboard',
        // 建议：主页可以使用路由懒加载
        component: () => import('../views/Dashboard.vue'),
        meta: { requiresAuth: true }
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to) => {
    const token = getAuthToken()

    if (to.meta.requiresAuth && !token) {
        return { path: '/' }
    }

    if (to.meta.guestOnly && token) {
        return { path: '/dashboard' }
    }
})

export default router
