<template>
  <div class="announcements">
    <el-card>
      <template #header><span>系统公告</span></template>
      <div v-if="announcements.length === 0" class="empty-state">
        <el-empty description="暂无公告" />
      </div>
      <div v-else>
        <div v-for="item in announcements" :key="item.id" class="announcement-item" @click="viewAnnouncement(item)">
          <div class="announcement-header">
            <el-tag :type="item.type === 'IMPORTANT' ? 'danger' : 'info'" size="small">
              {{ item.type === 'IMPORTANT' ? '重要' : '普通' }}
            </el-tag>
            <span class="announcement-title">{{ item.title }}</span>
            <span v-if="!item.is_read" class="unread-dot"></span>
            <span class="announcement-time">{{ formatTime(item.published_at) }}</span>
          </div>
          <div class="announcement-preview">{{ item.content?.substring(0, 100) }}...</div>
        </div>
      </div>
    </el-card>

    <el-dialog v-model="showDetail" :title="currentItem.title" width="700px">
      <div class="announcement-detail">
        <div class="detail-meta">
          <el-tag :type="currentItem.type === 'IMPORTANT' ? 'danger' : 'info'" size="small">
            {{ currentItem.type === 'IMPORTANT' ? '重要公告' : '普通公告' }}
          </el-tag>
          <span>发布时间: {{ formatTime(currentItem.published_at) }}</span>
        </div>
        <div class="detail-content">{{ currentItem.content }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getAnnouncements, markAnnouncementRead } from '@/api/announcement'

const announcements = ref<any[]>([])
const showDetail = ref(false)
const currentItem = ref<any>({})

const loadAnnouncements = async () => {
  try {
    const res = await getAnnouncements()
    announcements.value = res.data || []
  } catch (e) { announcements.value = [] }
}

const formatTime = (t: string) => {
  if (!t) return ''
  return new Date(t).toLocaleString('zh-CN')
}

const viewAnnouncement = async (item: any) => {
  currentItem.value = item
  showDetail.value = true
  if (!item.is_read) {
    try {
      await markAnnouncementRead(item.id)
      item.is_read = true
    } catch (e) { /* ignore */ }
  }
}

onMounted(() => { loadAnnouncements() })
</script>

<style scoped>
.empty-state {
  padding: 40px 0;
  text-align: center;
}

.announcement-item {
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background 0.3s;
}

.announcement-item:hover {
  background: #f5f7fa;
}

.announcement-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.announcement-title {
  font-size: 16px;
  font-weight: 500;
  flex: 1;
}

.unread-dot {
  width: 8px;
  height: 8px;
  background: #f56c6c;
  border-radius: 50%;
}

.announcement-time {
  color: #999;
  font-size: 13px;
}

.announcement-preview {
  margin-top: 8px;
  color: #666;
  font-size: 14px;
}

.detail-meta {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
  color: #999;
}

.detail-content {
  line-height: 1.8;
  font-size: 15px;
}
</style>