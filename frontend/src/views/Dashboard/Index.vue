<template>
  <div class="dashboard">
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">学校总数</div>
              <div class="stat-value">{{ stats.schoolCount }}</div>
            </div>
            <el-icon class="stat-icon" :size="48" color="#409eff"><OfficeBuilding /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">已填志愿</div>
              <div class="stat-value">{{ stats.volunteerCount }}</div>
            </div>
            <el-icon class="stat-icon" :size="48" color="#67c23a"><Edit /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">推荐方案</div>
              <div class="stat-value">{{ stats.recommendationCount }}</div>
            </div>
            <el-icon class="stat-icon" :size="48" color="#e6a23c"><MagicStick /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">未读公告</div>
              <div class="stat-value">{{ stats.unreadAnnouncements }}</div>
            </div>
            <el-icon class="stat-icon" :size="48" color="#f56c6c"><Bell /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>快捷操作</span>
          </template>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-button type="primary" size="large" class="quick-btn" @click="$router.push('/schools')">
                <el-icon><Search /></el-icon>
                浏览学校
              </el-button>
            </el-col>
            <el-col :span="8">
              <el-button type="success" size="large" class="quick-btn" @click="$router.push('/volunteer')">
                <el-icon><Edit /></el-icon>
                填报志愿
              </el-button>
            </el-col>
            <el-col :span="8">
              <el-button type="warning" size="large" class="quick-btn" @click="$router.push('/recommendation')">
                <el-icon><MagicStick /></el-icon>
                智能推荐
              </el-button>
            </el-col>
          </el-row>
          <el-row :gutter="20" style="margin-top: 15px">
            <el-col :span="8">
              <el-button size="large" class="quick-btn" @click="$router.push('/student')">
                <el-icon><User /></el-icon>
                学生信息
              </el-button>
            </el-col>
            <el-col :span="8">
              <el-button size="large" class="quick-btn" @click="$router.push('/help-docs')">
                <el-icon><Document /></el-icon>
                帮助文档
              </el-button>
            </el-col>
            <el-col :span="8">
              <el-button size="large" class="quick-btn" @click="$router.push('/customer-service')">
                <el-icon><ChatDotRound /></el-icon>
                在线客服
              </el-button>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>系统公告</span>
          </template>
          <div v-if="announcements.length === 0" class="empty-text">暂无公告</div>
          <div v-else>
            <div v-for="item in announcements" :key="item.id" class="announcement-item" @click="$router.push('/announcements')">
              <el-tag :type="item.type === 'IMPORTANT' ? 'danger' : 'info'" size="small">
                {{ item.type === 'IMPORTANT' ? '重要' : item.type === 'URGENT' ? '紧急' : '普通' }}
              </el-tag>
              <span class="announcement-title">{{ item.title }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { OfficeBuilding, Edit, MagicStick, Bell, Search, User, Document, ChatDotRound } from '@element-plus/icons-vue'
import { getSchoolList } from '@/api/school'
import { getAnnouncements } from '@/api/announcement'

const stats = ref({
  schoolCount: 0,
  volunteerCount: 0,
  recommendationCount: 0,
  unreadAnnouncements: 0
})

const announcements = ref<any[]>([])

onMounted(async () => {
  try {
    const res = await getSchoolList({ page: 0, size: 1 })
    const data = res.data || res
    stats.value.schoolCount = data?.totalElements || 0
  } catch (e) { /* ignore */ }

  try {
    const res = await getAnnouncements()
    const data = res.data || res
    announcements.value = (Array.isArray(data) ? data : []).slice(0, 5)
  } catch (e) { /* ignore */ }
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stat-card {
  height: 120px;
}

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 80px;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #333;
}

.stat-icon {
  opacity: 0.8;
}

.quick-btn {
  width: 100%;
  height: 60px;
  font-size: 16px;
}

.announcement-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
}

.announcement-item:hover .announcement-title {
  color: #409eff;
}

.announcement-title {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 14px;
}

.empty-text {
  text-align: center;
  color: #999;
  padding: 20px;
}
</style>