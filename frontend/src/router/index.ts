import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/users'
    },
    {
      path: '/users',
      name: 'UserList',
      component: () => import('@/views/UserList.vue'),
      meta: { title: '用户管理' }
    },
    {
      path: '/users/:id',
      name: 'UserDetail',
      component: () => import('@/views/UserDetail.vue'),
      meta: { title: '用户详情' }
    }
  ]
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (!token) {
    next('/login')
  } else {
    next()
  }
})

export default router
