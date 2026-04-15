<template>
  <div class="notifications-panel">
    <div class="panel-header">
      <h3>通知中心</h3>
      <div class="panel-actions">
        <el-button
          v-if="unreadCount > 0"
          text
          type="primary"
          @click="markAllAsRead"
        >
          全部已读
        </el-button>
        <el-button
          v-if="notifications.length > 0"
          text
          type="danger"
          @click="clearNotifications"
        >
          清空
        </el-button>
      </div>
    </div>

    <div class="panel-body">
      <div v-if="loading" class="loading-container">
        <el-icon class="is-loading"><loading /></el-icon>
        <span>加载中...</span>
      </div>

      <div v-else-if="notifications.length === 0" class="empty-container">
        <el-empty description="暂无通知" />
      </div>

      <div v-else class="notifications-list">
        <div
          v-for="notification in notifications"
          :key="notification.id"
          :class="[
            'notification-item',
            { unread: !notification.read }
          ]"
          @click="handleNotificationClick(notification)"
        >
          <div class="notification-icon">
            <el-icon :color="getIconColor(notification.type)">
              <component :is="getIconComponent(notification.type)" />
            </el-icon>
          </div>

          <div class="notification-content">
            <div class="notification-header">
              <h4>{{ notification.title }}</h4>
              <span class="notification-time">{{ formatTime(notification.timestamp) }}</span>
            </div>
            <p class="notification-text">{{ notification.content }}</p>
          </div>

          <div v-if="!notification.read" class="notification-badge"></div>
        </div>
      </div>
    </div>

    <div v-if="notifications.length > 0" class="panel-footer">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="totalNotifications"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Loading, Bell, Message, Calendar, Warning, InfoFilled } from '@element-plus/icons-vue'
import { useNotifications, Notification } from '../utils/notifications'

const router = useRouter()
const {
  notifications: allNotifications,
  unreadCount,
  markAsRead,
  markAllAsRead,
  clearNotifications
} = useNotifications()

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)

const notifications = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return allNotifications.value.slice(start, end)
})

const totalNotifications = computed(() => allNotifications.value.length)

const getIconComponent = (type: string) => {
  const iconMap: Record<string, any> = {
    task: Bell,
    message: Message,
    activity: Calendar,
    system: Warning,
    generic: InfoFilled
  }
  return iconMap[type] || InfoFilled
}

const getIconColor = (type: string) => {
  const colorMap: Record<string, string> = {
    task: '#409EFF',
    message: '#67C23A',
    activity: '#E6A23C',
    system: '#F56C6C',
    generic: '#909399'
  }
  return colorMap[type] || '#909399'
}

const formatTime = (timestamp: number) => {
  const now = Date.now()
  const diff = now - timestamp

  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour

  if (diff < minute) {
    return '刚刚'
  } else if (diff < hour) {
    return `${Math.floor(diff / minute)}分钟前`
  } else if (diff < day) {
    return `${Math.floor(diff / hour)}小时前`
  } else if (diff < 7 * day) {
    return `${Math.floor(diff / day)}天前`
  } else {
    const date = new Date(timestamp)
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
  }
}

const handleNotificationClick = (notification: Notification) => {
  if (!notification.read) {
    markAsRead(notification.id)
  }

  if (notification.path) {
    router.push(notification.path)
  }
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
}

onMounted(() => {
  if ('Notification' in window && Notification.permission === 'default') {
    Notification.requestPermission()
  }
})

onUnmounted(() => {
  // 清理逻辑
})
</script>

<style scoped>
.notifications-panel {
  width: 400px;
  max-height: 600px;
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #e4e7ed;
}

.panel-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.panel-actions {
  display: flex;
  gap: 8px;
}

.panel-body {
  flex: 1;
  overflow-y: auto;
  padding: 0;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #909399;
}

.loading-container .el-icon {
  font-size: 32px;
  margin-bottom: 12px;
}

.empty-container {
  padding: 40px 20px;
}

.notifications-list {
  display: flex;
  flex-direction: column;
}

.notification-item {
  position: relative;
  display: flex;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.2s;
}

.notification-item:hover {
  background-color: #f5f7fa;
}

.notification-item.unread {
  background-color: #ecf5ff;
}

.notification-icon {
  display: flex;
  align-items: flex-start;
  margin-right: 12px;
  flex-shrink: 0;
}

.notification-icon .el-icon {
  font-size: 20px;
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 6px;
}

.notification-header h4 {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  flex: 1;
  margin-right: 8px;
}

.notification-time {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
}

.notification-text {
  margin: 0;
  font-size: 13px;
  color: #606266;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.notification-badge {
  position: absolute;
  top: 16px;
  right: 20px;
  width: 8px;
  height: 8px;
  background-color: #409EFF;
  border-radius: 50%;
}

.panel-footer {
  padding: 12px 20px;
  border-top: 1px solid #e4e7ed;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .notifications-panel {
    width: 100%;
    max-width: 100%;
  }
}
</style>