<template>
  <el-container class="main-layout">
    <a href="#main-content" class="skip-link" @click.prevent="skipToContent">跳转到主要内容</a>
    <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar" role="navigation" aria-label="主导航菜单">
      <div class="logo-area" @click="$router.push('/')">
        <div class="logo-icon">
          <el-icon :size="24"><School /></el-icon>
        </div>
        <transition name="fade">
          <span v-show="!isCollapse" class="logo-text">中考志愿系统</span>
        </transition>
      </div>

      <div class="menu-scroll">
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          :router="true"
          class="sidebar-menu"
        >
          <el-menu-item index="/dashboard">
            <el-icon><DataAnalysis /></el-icon>
            <template #title>仪表盘</template>
          </el-menu-item>

          <div class="menu-divider"><span v-show="!isCollapse">核心功能</span></div>

          <el-menu-item index="/schools">
            <el-icon><OfficeBuilding /></el-icon>
            <template #title>学校浏览</template>
          </el-menu-item>
          <el-menu-item index="/student">
            <el-icon><User /></el-icon>
            <template #title>学生信息</template>
          </el-menu-item>
          <el-menu-item index="/volunteer">
            <el-icon><Edit /></el-icon>
            <template #title>志愿填报</template>
          </el-menu-item>
          <el-menu-item index="/recommendation">
            <el-icon><MagicStick /></el-icon>
            <template #title>智能推荐</template>
          </el-menu-item>

          <div class="menu-divider"><span v-show="!isCollapse">分析工具</span></div>

          <el-sub-menu index="/plan-tools">
            <template #title>
              <el-icon><TrendCharts /></el-icon>
              <span>方案工具</span>
            </template>
            <el-menu-item index="/risk-assessment">风险评估</el-menu-item>
            <el-menu-item index="/plan-comparison">方案对比</el-menu-item>
          </el-sub-menu>

          <div class="menu-divider"><span v-show="!isCollapse">帮助支持</span></div>

          <el-sub-menu index="/help-center">
            <template #title>
              <el-icon><QuestionFilled /></el-icon>
              <span>帮助中心</span>
            </template>
            <el-menu-item index="/help-docs">帮助文档</el-menu-item>
            <el-menu-item index="/announcements">系统公告</el-menu-item>
            <el-menu-item index="/customer-service">在线客服</el-menu-item>
          </el-sub-menu>

          <template v-if="isAdmin">
            <div class="menu-divider"><span v-show="!isCollapse">管理后台</span></div>

            <el-sub-menu index="/admin-allocation">
              <template #title>
                <el-icon><Coin /></el-icon>
                <span>分配生管理</span>
              </template>
              <el-menu-item index="/admin/allocation-quotas">名额管理</el-menu-item>
              <el-menu-item index="/admin/allocation-policies">政策管理</el-menu-item>
              <el-menu-item index="/admin/score-ranks">分数位次</el-menu-item>
            </el-sub-menu>

            <el-sub-menu index="/admin-content">
              <template #title>
                <el-icon><Document /></el-icon>
                <span>内容管理</span>
              </template>
              <el-menu-item index="/admin/announcements">公告管理</el-menu-item>
              <el-menu-item index="/admin/help-documents">帮助文档</el-menu-item>
              <el-menu-item index="/admin/notifications">通知管理</el-menu-item>
            </el-sub-menu>

            <el-menu-item index="/admin/customer-service">
              <el-icon><ChatDotRound /></el-icon>
              <template #title>客服管理</template>
            </el-menu-item>
            <el-menu-item index="/users">
              <el-icon><UserFilled /></el-icon>
              <template #title>用户管理</template>
            </el-menu-item>
            <el-menu-item index="/admin/historical-admission">
              <el-icon><TrendCharts /></el-icon>
              <template #title>历史录取数据</template>
            </el-menu-item>

            <el-sub-menu index="/admin-system">
              <template #title>
                <el-icon><Setting /></el-icon>
                <span>系统管理</span>
              </template>
              <el-menu-item index="/admin/audit-logs">操作日志</el-menu-item>
              <el-menu-item index="/admin/system-config">系统配置</el-menu-item>
              <el-menu-item index="/admin/system-monitor">系统监控</el-menu-item>
              <el-menu-item index="/admin/system-data">数据管理</el-menu-item>
            </el-sub-menu>
          </template>
        </el-menu>
      </div>

      <div class="collapse-trigger" @click="isCollapse = !isCollapse">
        <el-icon :size="18">
          <Fold v-if="!isCollapse" />
          <Expand v-else />
        </el-icon>
      </div>
    </el-aside>

    <el-container>
      <el-header class="header" role="banner">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" icon="UserFilled" />
              <span class="username">{{ username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main id="main-content" class="main-content" role="main" tabindex="-1">
        <router-view />
      </el-main>
    </el-container>
    <OnboardingGuide />
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import OnboardingGuide from '@/components/Common/OnboardingGuide.vue'
import {
  School, DataAnalysis, OfficeBuilding, User, Edit,
  MagicStick, QuestionFilled, Setting, Fold, Expand, ArrowDown,
  Monitor, List, TrendCharts, Tools, FolderOpened,
  Coin, Document, ChatDotRound, UserFilled
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const isCollapse = ref(false)

const username = computed(() => localStorage.getItem('username') || '用户')
const isAdmin = computed(() => localStorage.getItem('userRole') === 'ADMIN')

const activeMenu = computed(() => route.path)

const currentTitle = computed(() => {
  const titles: Record<string, string> = {
    '/dashboard': '仪表盘',
    '/schools': '学校浏览',
    '/student': '学生信息',
    '/volunteer': '志愿填报',
    '/recommendation': '智能推荐',
    '/help-docs': '帮助文档',
    '/announcements': '系统公告',
    '/customer-service': '在线客服',
    '/users': '用户管理',
    '/admin/audit-logs': '操作日志',
    '/admin/historical-admission': '历史录取数据',
    '/admin/system-config': '系统配置',
    '/admin/system-monitor': '系统监控',
    '/admin/system-data': '数据管理',
    '/admin/allocation-quotas': '分配生名额管理',
    '/admin/allocation-policies': '分配生政策管理',
    '/admin/score-ranks': '分数位次管理',
    '/admin/announcements': '公告管理',
    '/admin/help-documents': '帮助文档管理',
    '/admin/customer-service': '客服管理',
    '/admin/notifications': '通知管理',
  }
  return titles[route.path] || route.meta?.title as string || '页面'
})

const handleCommand = (command: string) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      localStorage.removeItem('token')
      localStorage.removeItem('userRole')
      localStorage.removeItem('username')
      router.push('/login')
      ElMessage.success('已退出登录')
    }).catch(() => {})
  } else if (command === 'profile') {
    router.push('/student')
  }
}

const skipToContent = () => {
  const mainContent = document.getElementById('main-content')
  if (mainContent) {
    mainContent.focus()
  }
}
</script>

<style scoped>
.main-layout {
  height: 100vh;
}

.sidebar {
  background-color: #ffffff;
  border-right: 1px solid #e8ecf1;
  transition: width 0.28s ease;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.03);
  z-index: 10;
}

.logo-area {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 16px;
  gap: 12px;
  cursor: pointer;
  background: linear-gradient(135deg, #409eff 0%, #337ecc 100%);
  color: #fff;
  flex-shrink: 0;
}

.logo-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.2);
  flex-shrink: 0;
}

.logo-text {
  font-size: 15px;
  font-weight: 600;
  white-space: nowrap;
  letter-spacing: 1px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.menu-scroll {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 8px 0;
}

.menu-scroll::-webkit-scrollbar {
  width: 4px;
}
.menu-scroll::-webkit-scrollbar-thumb {
  background: #dcdfe6;
  border-radius: 2px;
}
.menu-scroll::-webkit-scrollbar-track {
  background: transparent;
}

.sidebar-menu {
  border-right: none !important;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 220px;
}

:deep(.sidebar-menu .el-menu-item),
:deep(.sidebar-menu .el-sub-menu__title) {
  height: 44px;
  line-height: 44px;
  margin: 2px 8px;
  border-radius: 8px;
  color: #5a5e66;
  font-size: 14px;
}

:deep(.sidebar-menu .el-menu-item:hover),
:deep(.sidebar-menu .el-sub-menu__title:hover) {
  background-color: #f0f5ff !important;
  color: #409eff;
}

:deep(.sidebar-menu .el-menu-item.is-active) {
  background-color: #409eff !important;
  color: #ffffff !important;
  font-weight: 500;
}

:deep(.sidebar-menu .el-sub-menu.is-active > .el-sub-menu__title) {
  color: #409eff;
}

:deep(.sidebar-menu .el-sub-menu .el-menu-item) {
  height: 40px;
  line-height: 40px;
  padding-left: 52px !important;
  margin: 1px 8px;
  min-width: auto;
  font-size: 13px;
}

:deep(.sidebar-menu .el-menu--inline) {
  background-color: transparent !important;
}

.menu-divider {
  padding: 8px 20px 4px;
  margin-top: 4px;
  display: flex;
  align-items: center;
}

.menu-divider::before {
  content: '';
  flex: 1;
  height: 1px;
  background: #e8ecf1;
}

.menu-divider span {
  font-size: 11px;
  color: #a8abb2;
  white-space: nowrap;
  padding: 0 8px;
  letter-spacing: 1px;
}

.collapse-trigger {
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #8c939d;
  border-top: 1px solid #e8ecf1;
  flex-shrink: 0;
  transition: all 0.2s;
}

.collapse-trigger:hover {
  color: #409eff;
  background-color: #f0f5ff;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #ffffff;
  border-bottom: 1px solid #e8ecf1;
  padding: 0 24px;
  height: 56px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.04);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #333;
  padding: 4px 8px;
  border-radius: 6px;
  transition: background-color 0.2s;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.username {
  font-size: 14px;
  color: #303133;
}

.main-content {
  background-color: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
}

.skip-link {
  position: absolute;
  top: -40px;
  left: 0;
  background: #409eff;
  color: #fff;
  padding: 8px 16px;
  z-index: 10000;
  text-decoration: none;
  font-size: 14px;
  border-radius: 0 0 4px 0;
  transition: top 0.2s;
}

.skip-link:focus {
  top: 0;
}

:deep(.el-menu--collapse .menu-divider) {
  padding: 8px 8px 4px;
}
:deep(.el-menu--collapse .menu-divider span) {
  display: none;
}
</style>
