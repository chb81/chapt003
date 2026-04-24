import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue'),
      meta: { title: '登录', requiresAuth: false }
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/Register.vue'),
      meta: { title: '注册', requiresAuth: false }
    },
    {
      path: '/',
      component: () => import('@/layouts/MainLayout.vue'),
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('@/views/Dashboard/Index.vue'),
          meta: { title: '仪表盘' }
        },
        {
          path: 'schools',
          name: 'SchoolList',
          component: () => import('@/views/School/SchoolList.vue'),
          meta: { title: '学校浏览' }
        },
        {
          path: 'schools/:id',
          name: 'SchoolDetail',
          component: () => import('@/views/School/SchoolDetail.vue'),
          meta: { title: '学校详情' }
        },
        {
          path: 'student',
          name: 'StudentProfile',
          component: () => import('@/views/Student/StudentProfile.vue'),
          meta: { title: '学生信息' }
        },
        {
          path: 'volunteer',
          name: 'VolunteerApplication',
          component: () => import('@/views/Volunteer/VolunteerApplication.vue'),
          meta: { title: '志愿填报' }
        },
        {
          path: 'recommendation',
          name: 'Recommendation',
          component: () => import('@/views/Recommendation/Index.vue'),
          meta: { title: '智能推荐' }
        },
        {
          path: 'help-docs',
          name: 'HelpDocs',
          component: () => import('@/views/Help/HelpDocs.vue'),
          meta: { title: '帮助文档' }
        },
        {
          path: 'announcements',
          name: 'Announcements',
          component: () => import('@/views/Announcement/AnnouncementList.vue'),
          meta: { title: '系统公告' }
        },
        {
          path: 'customer-service',
          name: 'CustomerService',
          component: () => import('@/views/CustomerService/Chat.vue'),
          meta: { title: '在线客服' }
        },
        {
          path: 'users',
          name: 'UserList',
          component: () => import('@/views/UserList.vue'),
          meta: { title: '用户管理' }
        },
        {
          path: 'users/:id',
          name: 'UserDetail',
          component: () => import('@/views/UserDetail.vue'),
          meta: { title: '用户详情' }
        },
        {
          path: 'admin/audit-logs',
          name: 'AuditLogList',
          component: () => import('@/views/Admin/AuditLogList.vue'),
          meta: { title: '操作日志', requiresAdmin: true }
        },
        {
          path: 'admin/historical-admission',
          name: 'HistoricalAdmissionData',
          component: () => import('@/views/Admin/HistoricalAdmissionData.vue'),
          meta: { title: '历史录取数据', requiresAdmin: true }
        },
        {
          path: 'admin/system-config',
          name: 'SystemConfig',
          component: () => import('@/views/Admin/SystemConfig.vue'),
          meta: { title: '系统配置', requiresAdmin: true }
        },
        {
          path: 'admin/system-monitor',
          name: 'SystemMonitor',
          component: () => import('@/views/Admin/SystemMonitor.vue'),
          meta: { title: '系统监控', requiresAdmin: true }
        },
        {
          path: 'admin/system-data',
          name: 'SystemData',
          component: () => import('@/views/Admin/SystemData.vue'),
          meta: { title: '数据管理', requiresAdmin: true }
        }
      ]
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/dashboard'
    }
  ]
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userRole = localStorage.getItem('userRole')

  if (to.meta.requiresAuth !== false && !token) {
    next('/login')
    return
  }

  if (to.meta.requiresAdmin && userRole !== 'ADMIN') {
    next('/dashboard')
    return
  }

  if (to.path === '/login' && token) {
    next('/dashboard')
    return
  }

  next()
})

export default router