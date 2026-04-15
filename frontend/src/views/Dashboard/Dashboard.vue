<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <h1>数据概览</h1>
      <div class="header-actions">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          @change="handleDateChange"
        />
        <el-button type="primary" @click="refreshData">
          <el-icon><refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <div class="stats-cards">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6" v-for="stat in stats" :key="stat.key">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon" :style="{ background: stat.color }">
                <el-icon :size="24">
                  <component :is="stat.icon" />
                </el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stat.value }}</div>
                <div class="stat-label">{{ stat.label }}</div>
                <div class="stat-trend" :class="stat.trend >= 0 ? 'positive' : 'negative'">
                  <el-icon>
                    <component :is="stat.trend >= 0 ? 'ArrowUp' : 'ArrowDown'" />
                  </el-icon>
                  {{ Math.abs(stat.trend) }}%
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="charts-section">
      <el-row :gutter="20">
        <el-col :xs="24" :lg="16">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>志愿者活动趋势</span>
                <el-radio-group v-model="activityChartType" size="small">
                  <el-radio-button label="week">周</el-radio-button>
                  <el-radio-button label="month">月</el-radio-button>
                  <el-radio-button label="year">年</el-radio-button>
                </el-radio-group>
              </div>
            </template>
            <div ref="activityChartRef" class="chart-container"></div>
          </el-card>
        </el-col>

        <el-col :xs="24" :lg="8">
          <el-card class="chart-card">
            <template #header>
              <span>志愿者类型分布</span>
            </template>
            <div ref="typeChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card">
            <template #header>
              <span>活动完成情况</span>
            </template>
            <div ref="completionChartRef" class="chart-container"></div>
          </el-card>
        </el-col>

        <el-col :xs="24" :lg="12">
          <el-card class="chart-card">
            <template #header>
              <span>服务时长统计</span>
            </template>
            <div ref="hoursChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card">
          <template #header>
            <span>活动完成情况</span>
          </template>
          <div ref="completionChartRef" class="chart-container"></div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card class="chart-card">
          <template #header>
            <span>服务时长统计</span>
          </template>
          <div ref="hoursChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>智能推荐</span>
              <el-button text type="primary" @click="viewRecommendations">
                查看全部
              </el-button>
            </div>
          </template>
          <div class="recommendations-section">
            <div 
              v-for="rec in recommendations.slice(0, 3)" 
              :key="rec.activity.id"
              class="recommendation-item"
              :class="{ 'high-score': rec.score >= 0.8 }"
            >
              <div class="item-header">
                <h3>{{ rec.activity.title }}</h3>
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
                </div>
                <div class="info-row">
                  <el-icon><User /></el-icon>
                  <span>{{ rec.activity.currentParticipants }}/{{ rec.activity.maxParticipants }}人</span>
                  <el-icon><Star /></el-icon>
                  <span>{{ rec.activity.points }}积分</span>
                </div>
              </div>
              <div class="match-reasons">
                <el-tag 
                  size="small" 
                  :type="getDifficultyType(rec.activity.difficulty)"
                >
                  {{ getDifficultyText(rec.activity.difficulty) }}
                </el-tag>
                <span class="reasons-text">
                  {{ rec.reasons[0] }}
                </span>
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
                  @click="viewRecommendationDetails(rec)"
                >
                  详情
                </el-button>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>最新活动</span>
              <el-button text type="primary" @click="viewAllActivities">
                查看全部
              </el-button>
            </div>
          </template>
          <el-table :data="recentActivities" style="width: 100%">
            <el-table-column prop="title" label="活动名称" min-width="200" />
            <el-table-column prop="organizer" label="组织者" width="120" />
            <el-table-column prop="participants" label="参与人数" width="100" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="startDate" label="开始时间" width="180" />
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button text type="primary" size="small" @click="viewActivity(row)">
                  查看
                </el-button>
                <el-button text type="success" size="small" @click="manageActivity(row)">
                  管理
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Refresh,
  User,
  Calendar,
  Timer,
  TrendCharts,
  ArrowUp,
  ArrowDown
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const router = useRouter()

const dateRange = ref<[Date, Date]>([
  new Date(Date.now() - 7 * 24 * 60 * 60 * 1000),
  new Date()
])

const activityChartType = ref('week')

const activityChartRef = ref<HTMLElement>()
const typeChartRef = ref<HTMLElement>()
const completionChartRef = ref<HTMLElement>()
const hoursChartRef = ref<HTMLElement>()

let activityChart: echarts.ECharts | null = null
let typeChart: echarts.ECharts | null = null
let completionChart: echarts.ECharts | null = null
let hoursChart: echarts.ECharts | null = null

const stats = ref([
  {
    key: 'volunteers',
    label: '志愿者总数',
    value: '1,234',
    trend: 12.5,
    color: '#409EFF',
    icon: 'User'
  },
  {
    key: 'activities',
    label: '活动总数',
    value: '567',
    trend: 8.3,
    color: '#67C23A',
    icon: 'Calendar'
  },
  {
    key: 'hours',
    label: '服务时长',
    value: '12,456',
    trend: 15.7,
    color: '#E6A23C',
    icon: 'Timer'
  },
  {
    key: 'rating',
    label: '平均评分',
    value: '4.8',
    trend: 2.1,
    color: '#F56C6C',
    icon: 'TrendCharts'
  }
])

const recentActivities = ref([
  {
    id: 1,
    title: '社区清洁志愿服务',
    organizer: '环保协会',
    participants: 45,
    status: 'completed',
    startDate: '2024-01-15 09:00'
  },
  {
    id: 2,
    title: '老人院探访活动',
    organizer: '爱心社团',
    participants: 20,
    status: 'ongoing',
    startDate: '2024-01-16 14:00'
  },
  {
    id: 3,
    title: '义务支教活动',
    organizer: '教育联盟',
    participants: 30,
    status: 'upcoming',
    startDate: '2024-01-18 10:00'
  },
  {
    id: 4,
    title: '公益募捐活动',
    organizer: '慈善基金会',
    participants: 100,
    status: 'completed',
    startDate: '2024-01-10 08:00'
  },
  {
    id: 5,
    title: '环保宣传讲座',
    organizer: '绿色家园',
    participants: 60,
    status: 'upcoming',
    startDate: '2024-01-20 15:00'
  }
])

const getStatusType = (status: string) => {
  const typeMap: Record<string, any> = {
    completed: 'success',
    ongoing: 'warning',
    upcoming: 'info',
    cancelled: 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    completed: '已完成',
    ongoing: '进行中',
    upcoming: '即将开始',
    cancelled: '已取消'
  }
  return textMap[status] || status
}

const initActivityChart = () => {
  if (!activityChartRef.value) return

  activityChart = echarts.init(activityChartRef.value)

  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    legend: {
      data: ['参与人数', '活动数量']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '参与人数',
        type: 'line',
        smooth: true,
        data: [120, 132, 101, 134, 90, 230, 210],
        itemStyle: {
          color: '#409EFF'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
          ])
        }
      },
      {
        name: '活动数量',
        type: 'line',
        smooth: true,
        data: [8, 10, 12, 11, 9, 15, 14],
        itemStyle: {
          color: '#67C23A'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
            { offset: 1, color: 'rgba(103, 194, 58, 0.1)' }
          ])
        }
      }
    ]
  }

  activityChart.setOption(option)
}

const initTypeChart = () => {
  if (!typeChartRef.value) return

  typeChart = echarts.init(typeChartRef.value)

  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '志愿者类型',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 18,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: [
          { value: 735, name: '个人志愿者' },
          { value: 310, name: '团队志愿者' },
          { value: 145, name: '企业志愿者' },
          { value: 44, name: '其他' }
        ]
      }
    ]
  }

  typeChart.setOption(option)
}

const initCompletionChart = () => {
  if (!completionChartRef.value) return

  completionChart = echarts.init(completionChartRef.value)

  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      boundaryGap: [0, 0.01]
    },
    yAxis: {
      type: 'category',
      data: ['社区服务', '环保活动', '教育支教', '医疗健康', '其他']
    },
    series: [
      {
        name: '已完成',
        type: 'bar',
        data: [18203, 23489, 29034, 104970, 131744],
        itemStyle: {
          color: '#67C23A'
        }
      },
      {
        name: '进行中',
        type: 'bar',
        data: [19325, 23438, 31000, 121594, 134141],
        itemStyle: {
          color: '#E6A23C'
        }
      }
    ]
  }

  completionChart.setOption(option)
}

const initHoursChart = () => {
  if (!hoursChartRef.value) return

  hoursChart = echarts.init(hoursChartRef.value)

  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['本月', '上月']
    },
    radar: {
      indicator: [
        { name: '社区服务', max: 6500 },
        { name: '环保活动', max: 16000 },
        { name: '教育支教', max: 30000 },
        { name: '医疗健康', max: 38000 },
        { name: '其他', max: 52000 }
      ]
    },
    series: [
      {
        name: '服务时长对比',
        type: 'radar',
        data: [
          {
            value: [4200, 3000, 20000, 35000, 50000],
            name: '本月',
            itemStyle: {
              color: '#409EFF'
            },
            areaStyle: {
              color: 'rgba(64, 158, 255, 0.3)'
            }
          },
          {
            value: [5000, 14000, 28000, 26000, 42000],
            name: '上月',
            itemStyle: {
              color: '#67C23A'
            },
            areaStyle: {
              color: 'rgba(103, 194, 58, 0.3)'
            }
          }
        ]
      }
    ]
  }

  hoursChart.setOption(option)
}

const handleDateChange = (dates: [Date, Date]) => {
  console.log('Date range changed:', dates)
  ElMessage.success('数据已更新')
}

const refreshData = () => {
  ElMessage.info('正在刷新数据...')
  setTimeout(() => {
    ElMessage.success('数据刷新成功')
  }, 1000)
}

const viewAllActivities = () => {
  router.push('/activities')
}

const viewActivity = (activity: any) => {
  router.push(`/activities/${activity.id}`)
}

const manageActivity = (activity: any) => {
  router.push(`/activities/${activity.id}/edit`)
}

const handleResize = () => {
  activityChart?.resize()
  typeChart?.resize()
  completionChart?.resize()
  hoursChart?.resize()
}

onMounted(async () => {
  await nextTick()

  initActivityChart()
  initTypeChart()
  initCompletionChart()
  initHoursChart()

  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)

  activityChart?.dispose()
  typeChart?.dispose()
  completionChart?.dispose()
  hoursChart?.dispose()
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.dashboard-header h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
}

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.stats-cards {
  margin-bottom: 20px;
}

.stat-card {
  height: 120px;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
}

.stat-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px;
  border-radius: 12px;
  margin-right: 16px;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.stat-trend {
  font-size: 12px;
  margin-top: 4px;
  display: flex;
  align-items: center;
  gap: 2px;
}

.stat-trend.positive {
  color: #67C23A;
}

.stat-trend.negative {
  color: #F56C6C;
}

.charts-section {
  margin-bottom: 20px;
}

.chart-card {
  height: 400px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  width: 100%;
  height: 320px;
}

.activity-list {
  margin-top: 20px;
}

@media (max-width: 768px) {
  .dashboard-header {
    flex-direction: column;
    align-items: stretch;
  }

  .header-actions {
    flex-direction: column;
    align-items: stretch;
  }

  .stat-card {
    height: auto;
    min-height: 120px;
  }

  .chart-card {
    height: 350px;
  }

  .chart-container {
    height: 270px;
  }
}
</style>