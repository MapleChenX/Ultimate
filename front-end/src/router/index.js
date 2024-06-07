import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    {
        path: '/',
        name: 'Welcome',
        component: () => import('@/views/welcome/Welcome.vue'),
        children: [
            {
                path: '/login',
                name: 'Login',
                component: () => import('../views/welcome/Login.vue')
            },
            {
                path: '/register',
                name: 'Register',
                component: () => import('@/views/welcome/Register.vue')
            }
        ]

    },
    {
        path: '/test',
        name: 'Test',
        component: () => import('@/views/Test.vue')
    },
    {
        path: '/father',
        name: 'Father',
        component: () => import('@/views/Father.vue')
    },
    {
        path: '/train',
        name: 'Train',
        component: () => import('@/views/ElemTrain.vue')
    },
    {
        path: '/home',
        name: 'home',
        component: () => import('@/views/Home.vue')
    },
    {
        path: '/tt',
        name: 'tt',
        component: () => import('@/views/test/TT.vue')
    },
    {
        path: '/fragUpload',
        name: 'fragUpload',
        component: () => import('@/views/test/FragUpload.vue')
    },
    {
        path: '/video',
        name: 'video',
        component: () => import('@/views/test/Video.vue')
    },
    {
        path: '/u',
        name: 'u',
        component: () => import('@/views/test/VideoUpload.vue')
    },
    {
        path: '/term',
        name: 'term',
        component: () => import('@/views/test/Term.vue')
    },
    {
        path: '/ai',
        name: 'ai',
        component: () => import('@/views/test/AI.vue')
    },
    {
        path: '/elem',
        name: 'elem',
        component: () => import('@/views/test/Elem.vue')
    }
]


const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to, from, next) => {
    if(to.name === 'Father'){
        next('/test')
    } else {
        next()
    }
})

export default router