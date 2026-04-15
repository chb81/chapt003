<template>
  <div class="recommendation-panel">
    <el-card>
      <template #header>
        <div class="panel-header">
          <div class="header-title">
            <el-icon><Magic /></el-icon>
            <span>智能推荐</span>
            <el-tag size="small" type="info">{{ recommendedCount }} 个推荐</el-tag>
          </div>
          <div class="header-actions">
            <el-button-group>
              <el-button 
                size="small" 
                :type="filterType === 'all' ? 'primary' : 'default'"
                @click="setFilter('all')"
              >
                全部
              </el-button>
              <el-button 
                size="small" 
                :type="filterType === 'high' ? 'primary' : 'default'"
                @click="setFilter('high')"
              >
                高匹配度
              </el-button>
              <el-button 
                size="small" 
                :type="filterType === 'nearby' ? 'primary' : 'default'"
                @click="setFilter('nearby')"
              >
                附近活动
              </el-button>
            </el-button-group>
            <el-button size="small" text @click="refreshRecommendations">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <div v-if="loading" class="loading-state">
        <el-skeleton animated :rows="3" />
      </div>

      <div v-else-if="filteredRecommendations.length === 0" class="empty-state">
        <el-empty description="暂无推荐活动">
          <el-button type="primary" @click="refreshRecommendations">
            重新加载
          </el-button>
        </el-empty>
      </div>

      <div v-else class="recommendations-list">
        <div
          v-for="rec in filteredRecommendations"
          :key="rec.activity.id"
          class="recommendation-item"
          :class="{ 'high-score': rec.score >= 0.8 }"
        >
          <div class="item-header">
            <div class="activity-title">
              <h3>{{ rec.activity.title }}</h3>
              <el-tag 
                size="small" 
                :type="getDifficultyType(rec.activity.difficulty)"
              >
                {{ getDifficultyText(rec.activity.difficulty) }}
              </el-tag>
            </div>
            <div class="match-score">
              <div class="score-circle" :class="getScoreClass(rec.score)">
                {{ Math.round(rec.score * 100) }}%
              </div>
            </div>
          </div>

          <div class="activity-info">
            <div class="info-row">
              <el-icon><Location /></el-icon>
              <span>{{ rec.activity.location }}</span>
              <el-icon><Clock /></el-icon>
              <span>{{ formatTime(rec.activity.startTime) }}</span>
              <el-icon><Timer /></el-icon>
              <span>{{ rec.activity.duration }}分钟</span>
            </div>
            <div class="info-row">
              <el-icon><User /></el-icon>
              <span>{{ rec.activity.currentParticipants }}/{{ rec.activity.maxParticipants }}人</span>
              <el-icon><Star /></el-icon>
              <span>{{ rec.activity.points }}积分</span>
              <el-icon><Flag /></el-icon>
              <span>{{ getActivityTypeText(rec.activity.type) }}</span>
            </div>
          </div>

          <div class="match-reasons">
            <h4>推荐理由：</h4>
            <ul>
              <li v-for="(reason, index) in rec.reasons" :key="index">
                <el-icon><Check /></el-icon>
                {{ reason }}
              </li>
            </ul>
          </div>

          <div class="activity-actions">
            <el-button 
              type="primary" 
              size="small"
              @click="handleJoin(rec)"
              :loading="loadingActivity === rec.activity.id"
            >
              立即报名
            </el-button>
            <el-button 
              size="small" 
              @click="handleLike(rec)"
              :loading="likingActivity === rec.activity.id"
            >
              <el-icon><Heart /></el-icon>
              喜欢
            </el-button>
            <el-button 
              size="small" 
              @click="showDetails(rec)"
            >
              详情
            </el-button>
          </div>

          <div class="progress-bar">
            <el-progress 
              :percentage="getFillPercentage(rec.activity)" 
              :show-text="false"
              :stroke-width="6"
              :color="getProgressColor(rec.activity)"
            />
          </div>
        </div>
      </div>

      <div v-if="hasMore" class="load-more">
        <el-button 
          size="small" 
          plain 
          @click="loadMore"
          :loading="loadingMore"
        >
          加载更多
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Magic, 
  Refresh, 
  Location, 
  Clock, 
  Timer, 
  User, 
  Star, 
  Flag, 
  Check, 
  Heart 
} from '@element-plus/icons-vue'
import { recommendationEngine } from '@/utils/recommendation'
import type { Recommendation, Activity } from '@/utils/recommendation'
import { apiService } from '@/utils/api'

const props = defineProps<{
  userId: string
}>()

const loading = ref(false)
const loadingMore = ref(false)
const loadingActivity = ref<string>('')
const likingActivity = ref<string>('')
const recommendations = ref<Recommendation[]>([])
const filterType = ref<'all' | 'high' | 'nearby'>('all')
const displayCount = ref(5)
const hasMore = ref(false)

const filteredRecommendations = computed(() => {
  let filtered = recommendations.value

  if (filterType.value === 'high') {
    filtered = filtered.filter(r => r.score >= 0.8)
  } else if (filterType.value === 'nearby') {
    filtered = filtered.filter(r => r.matchDetails.locationMatch > 0.5)
  }

  return filtered.slice(0, displayCount.value)
})

const recommendedCount = computed(() => {
  if (filterType.value === 'high') {
    return recommendations.value.filter(r => r.score >= 0.8).length
  } else if (filterType.value === 'nearby') {
    return recommendations.value.filter(r => r.matchDetails.locationMatch > 0.5).length
  }
  return recommendations.value.length
})

const setFilter = (type: 'all' | 'high' | 'nearby') => {
  filterType.value = type
  displayCount.value = 5
  hasMore.value = false
}

const getFillPercentage = (activity: Activity) => {
  return (activity.currentParticipants / activity.maxParticipants) * 100
}

const getProgressColor = (activity: Activity) => {
  const percentage = getFillPercentage(activity)
  if (percentage < 30) return '#67C23A'
  if (percentage < 70) return '#E6A23C'
  return '#F56C6C'
}

const getScoreClass = (score: number) => {
  if (score >= 0.8) return 'score-high'
  if (score >= 0.6) return 'score-medium'
  return 'score-low'
}

const getDifficultyType = (difficulty: string) => {
  switch (difficulty) {
    case 'easy': return 'success'
    case 'medium': return 'warning'
    case 'hard': return 'danger'
    default: return 'info'
  }
}

const getDifficultyText = (difficulty: string) => {
  switch (difficulty) {
    case 'easy': return '简单'
    case 'medium': return '中等'
    case 'hard': return '困难'
    default: return '未知'
  }
}

const getActivityTypeText = (type: string) => {
  const typeMap: Record<string, string> = {
    'environmental': '环保',
    'education': '教育',
    'health': '健康',
    'elderly': '养老',
    'children': '儿童',
    'animal': '动物',
    'community': '社区',
    'culture': '文化'
  }
  return typeMap[type] || type
}

const formatTime = (time: string) => {
  return new Date(time).toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const loadRecommendations = async () => {
  loading.value = true
  try {
    const activities = await apiService.getActivities()
    recommendationEngine.setActivities(activities)
    
    const recs = recommendationEngine.generateRecommendations(props.userId, 20)
    recommendations.value = recs
    
    hasMore.value = recs.length > displayCount.value
  } catch (error) {
    console.error('加载推荐失败:', error)
    ElMessage.error('加载推荐失败，请重试')
  } finally {
    loading.value = false
  }
}

const refreshRecommendations = () => {
  loadRecommendations()
}

const loadMore = async () => {
  loadingMore.value = true
  try {
    const additionalCount = 5
    const newCount = displayCount.value + additionalCount
    
    if (newCount >= recommendations.value.length) {
      hasMore.value = false
    }
    
    displayCount.value = newCount
  } catch (error) {
    console.error('加载更多失败:', error)
  } finally {
    loadingMore.value = false
  }
}

const handleJoin = async (rec: Recommendation) => {
  loadingActivity.value = rec.activity.id
  try {
    const result = await apiService.joinActivity(rec.activity.id)
    if (result.success) {
      recommendationEngine.updateUserPreference(props.userId, {
        activityId: rec.activity.id,
        action: 'join'
      })
      ElMessage.success('报名成功！')
      refreshRecommendations()
    } else {
      ElMessage.error(result.message || '报名失败')
    }
  } catch (error) {
    console.error('报名失败:', error)
    ElMessage.error('报名失败，请重试')
  } finally {
    loadingActivity.value = ''
  }
}

const handleLike = async (rec: Recommendation) => {
  likingActivity.value = rec.activity.id
  try {
    const result = await apiService.likeActivity(rec.activity.id)
    if (result.success) {
      recommendationEngine.updateUserPreference(props.userId, {
        activityId: rec.activity.id,
        action: 'like'
      })
      ElMessage.success('已添加到喜欢列表')
    } else {
      ElMessage.error(result.message || '操作失败')
    }
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败，请重试')
  } finally {
    likingActivity.value = ''
  }
}

const showDetails = (rec: Recommendation) => {
  router.push(`/activities/${rec.activity.id}`)
}

onMounted(() => {
  loadRecommendations()
})
</script>

<style scoped>
.recommendation-panel {
  padding: 20px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 10px;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 500;
}

.header-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.recommendations-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.recommendation-item {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
  transition: all 0.3s ease;
  background: #fafafa;
}

.recommendation-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.recommendation-item.high-score {
  border-color: #67c23a;
  background: linear-gradient(135deg, #f0f9eb 0%, #e8f5e8 100%);
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.activity-title {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.activity-title h3 {
  margin: 0;
  font-size: 16px;
  color: #303133;
}

.match-score {
  display: flex;
  align-items: center;
}

.score-circle {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 14px;
  color: white;
}

.score-high {
  background: linear-gradient(135deg, #67c23a, #85ce61);
}

.score-medium {
  background: linear-gradient(135deg, #e6a23c, #ebb563);
}

.score-low {
  background: linear-gradient(135deg, #f56c6c, #f78989);
}

.activity-info {
  margin-bottom: 12px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
  font-size: 14px;
  color: #606266;
}

.match-reasons {
  margin-bottom: 12px;
}

.match-reasons h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.match-reasons ul {
  margin: 0;
  padding-left: 20px;
  list-style: none;
}

.match-reasons li {
  margin-bottom: 4px;
  font-size: 13px;
  color: #606266;
  display: flex;
  align-items: center;
  gap: 4px;
}

.activity-actions {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.progress-bar {
  width: 100%;
}

.load-more {
  text-align: center;
  margin-top: 16px;
}

.loading-state {
  padding: 20px 0;
}

.empty-state {
  text-align: center;
  padding: 40px 0;
}
</style>