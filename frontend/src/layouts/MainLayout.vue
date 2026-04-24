<template>
  <el-container class="main-layout">
    <a href="#main-content" class="skip-link" @click.prevent="skipToContent">跳转到主要内容</a>
    <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar" role="navigation" aria-label="主导航菜单">
      <div class="logo" @click="$router.push('/')">
        <el-icon :size="28"><School /></el-icon>
        <span v-show="!isCollapse" class="logo-text">中考志愿系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :router="true"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <template #title>仪表盘</template>
        </el-menu-item>
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
        <el-sub-menu index="/help-center">
          <template #title>
            <el-icon><QuestionFilled /></el-icon>
            <span>帮助中心</span>
          </template>
          <el-menu-item index="/help-docs">帮助文档</el-menu-item>
          <el-menu-item index="/announcements">系统公告</el-menu-item>
          <el-menu-item index="/customer-service">在线客服</el-menu-item>
        </el-sub-menu>
        <el-menu-item v-if="isAdmin" index="/users">
          <el-icon><Setting /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
        <el-sub-menu v-if="isAdmin" index="/admin">
          <template #title>
            <el-icon><Monitor /></el-icon>
            <span>管理后台</span>
          </template>
          <el-menu-item index="/admin/audit-logs">
            <el-icon><List /></el-icon>
            <template #title>操作日志</template>
          </el-menu-item>
          <el-menu-item index="/admin/historical-admission">
            <el-icon><TrendCharts /></el-icon>
            <template #title>历史录取数据</template>
          </el-menu-item>
          <el-menu-item index="/admin/system-config">
            <el-icon><Tools /></el-icon>
            <template #title>系统配置</template>
          </el-menu-item>
          <el-menu-item index="/admin/system-monitor">
            <el-icon><Monitor /></el-icon>
            <template #title>系统监控</template>
          </el-menu-item>
          <el-menu-item index="/admin/system-data">
            <el-icon><FolderOpened /></el-icon>
            <template #title>数据管理</template>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header" role="banner">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
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
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  School, DataAnalysis, OfficeBuilding, User, Edit,
  MagicStick, QuestionFilled, Setting, Fold, Expand, ArrowDown,
  Monitor, List, TrendCharts, Tools, FolderOpened
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
    '/schools/:id': '学校详情',
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
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #fff;
  cursor: pointer;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}

.logo-text {
  font-size: 16px;
  font-weight: 600;
  white-space: nowrap;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,21,41,0.08);
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #333;
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
}

.username {
  font-size: 14px;
}

.main-content {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}

.el-menu {
  border-right: none;
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
</style>