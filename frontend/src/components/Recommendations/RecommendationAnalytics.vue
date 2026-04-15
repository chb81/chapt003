<template>
  <div class="recommendation-analytics">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>推荐分析面板</span>
          <div class="header-actions">
            <el-button-group>
              <el-button 
                size="small" 
                :type="timeRange === 'week' ? 'primary' : 'default'"
                @click="setTimeRange('week')"
              >
                本周
              </el-button>
              <el-button 
                size="small" 
                :type="timeRange === 'month' ? 'primary' : 'default'"
                @click="setTimeRange('month')"
              >
                本月
              </el-button>
              <el-button 
                size="small" 
                :type="timeRange === 'year' ? 'primary' : 'default'"
                @click="setTimeRange('year')"
              >
                本年
              </el-button>
            </el-button-group>
            <el-button size="small" text @click="refreshData">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <el-row :gutter="20">
        <el-col :span="24">
          <div class="kpi-cards">
            <el-row :gutter="16">
              <el-col :xs="24" :sm="12" :md="6" v-for="kpi in kpis" :key="kpi.key">
                <el-card class="kpi-card">
                  <div class="kpi-content">
                    <div class="kpi-icon" :style="{ background: kpi.color }">
                      <el-icon :size="20"><component :is="kpi.icon" /></el-icon>
                    </div>
                    <div class="kpi-info">
                      <div class="kpi-value">{{ kpi.value }}</div>
                      <div class="kpi-label">{{ kpi.label }}</div>
                      <div class="kpi-trend" :class="kpi.trend >= 0 ? 'positive' : 'negative'">
                        <el-icon><component :is="kpi.trend >= 0 ? 'ArrowUp' : 'ArrowDown'" /></el-icon>
                        {{ Math.abs(kpi.trend) }}%
                      </div>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :xs="24" :lg="16">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>推荐效果趋势</span>
                <el-radio-group v-model="chartType" size="small">
                  <el-radio-button label="precision">精确度</el-radio-button>
                  <el-radio-button label="coverage">覆盖率</el-radio-button>
                  <el-radio-button label="engagement">参与率</el-radio-button>
                </el-radio-group>
              </div>
            </template>
            <div ref="trendChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="8">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>推荐类型分布</span>
              </div>
            </template>
            <div ref="typeChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>用户偏好分析</span>
              </div>
            </template>
            <div ref="preferenceChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>推荐准确率对比</span>
              </div>
            </template>
            <div ref="accuracyChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="24">
          <el-card class="analysis-card">
            <template #header>
              <div class="card-header">
                <span>详细分析</span>
                <el-button-group>
                  <el-button 
                    size="small" 
                    :type="analysisTab === 'user' ? 'primary' : 'default'"
                    @click="analysisTab = 'user'"
                  >
                    用户分析
                  </el-button>
                  <el-button 
                    size="small" 
                    :type="analysisTab === 'activity' ? 'primary' : 'default'"
                    @click="analysisTab = 'activity'"
                  >
                    活动分析
                  </el-button>
                  <el-button 
                    size="small" 
                    :type="analysisTab === 'algorithm' ? 'primary' : 'default'"
                    @click="analysisTab = 'algorithm'"
                  >
                    算法分析
                  </el-button>
                </el-button-group>
              </div>
            </template>

            <div v-if="analysisTab === 'user'" class="analysis-content">
              <el-descriptions title="用户推荐画像" :column="3" border>
                <el-descriptions-item label="活跃用户数">{{ analysisData.activeUsers }}</el-descriptions-item>
                <el-descriptions-item label="平均推荐数">{{ analysisData.avgRecommendations }}</el-descriptions-item>
                <el-descriptions-item label="接受率">{{ analysisData.acceptanceRate }}%</el-descriptions-item>
                <el-descriptions-item label="最热门类型">{{ analysisData.topType }}</el-descriptions-item>
                <el-descriptions-item label="平均参与时长">{{ analysisData.avgDuration }}分钟</el-descriptions-item>
                <el-descriptions-item label="用户满意度">{{ analysisData.satisfaction }}%</el-descriptions-item>
              </el-descriptions>

              <div class="user-insights">
                <h4>用户洞察</h4>
                <div class="insights-list">
                  <div class="insight-item">
                    <el-icon><UserFilled /></el-icon>
                    <div class="insight-content">
                      <h5>高价值用户群体</h5>
                      <p>{{ analysisData.highValueUsers.description }}</p>
                      <div class="insight-metrics">
                        <span>{{ analysisData.highValueUsers.count }}人</span>
                        <span>{{ analysisData.highValueUsers.avgActivities }}个活动</span>
                      </div>
                    </div>
                  </div>
                  <div class="insight-item">
                    <el-icon><TrendCharts /></el-icon>
                    <div class="insight-content">
                      <h5>用户活跃度趋势</h5>
                      <p>{{ analysisData.activityTrend.description }}</p>
                      <div class="insight-metrics">
                        <span>{{ analysisData.activityTrend.growth }}% 增长</span>
                        <span>{{ analysisData.activityTrend.avgInteractions }}次互动</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div v-else-if="analysisTab === 'activity'" class="analysis-content">
              <el-table :data="analysisData.activities" style="width: 100%">
                <el-table-column prop="name" label="活动名称" min-width="150" />
                <el-table-column prop="type" label="类型" width="100" />
                <el-table-column prop="recommendations" label="推荐次数" width="100" />
                <el-table-column prop="acceptanceRate" label="接受率" width="100">
                  <template #default="{ row }">
                    <el-progress 
                      :percentage="row.acceptanceRate" 
                      :stroke-width="6"
                      :color="getProgressColor(row.acceptanceRate)"
                    />
                  </template>
                </el-table-column>
                <el-table-column prop="avgScore" label="平均分" width="80" />
                <el-table-column prop="insights" label="分析" />
              </el-table>
            </div>

            <div v-else-if="analysisTab === 'algorithm'" class="analysis-content">
              <div class="algorithm-metrics">
                <h4>算法性能指标</h4>
                <el-row :gutter="16">
                  <el-col :span="6" v-for="metric in analysisData.algorithmMetrics" :key="metric.name">
                    <div class="metric-card">
                      <div class="metric-value">{{ metric.value }}</div>
                      <div class="metric-label">{{ metric.name }}</div>
                      <div class="metric-trend" :class="metric.trend >= 0 ? 'positive' : 'negative'">
                        {{ metric.trend >= 0 ? '↑' : '↓' }} {{ Math.abs(metric.trend) }}%
                      </div>
                    </div>
                  </el-col>
                </el-row>
              </div>

              <div class="algorithm-insights">
                <h4>算法优化建议</h4>
                <div class="suggestions-list">
                  <div v-for="suggestion in analysisData.suggestions" :key="suggestion.id" class="suggestion-item">
                    <el-icon :class="suggestion.type === 'warning' ? 'warning' : 'success'">
                      <component :is="suggestion.type === 'warning' ? 'Warning' : 'CircleCheck'" />
                    </el-icon>
                    <div class="suggestion-content">
                      <h5>{{ suggestion.title }}</h5>
                      <p>{{ suggestion.description }}</p>
                      <el-button size="small" @click="applySuggestion(suggestion)">
                        应用建议
                      </el-button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { 
  Refresh, 
  ArrowUp, 
  ArrowDown, 
  UserFilled, 
  TrendCharts, 
  Warning, 
  CircleCheck 
} from '@element-plus/icons-vue'
import { apiService } from '@/utils/api'
import * as echarts from 'echarts'

interface KPI {
  key: string
  label: string
  value: string
  icon: string
  color: string
  trend: number
}

interface ChartData {
  labels: string[]
  data: number[]
}

interface AnalysisData {
  activeUsers: number
  avgRecommendations: number
  acceptanceRate: number
  topType: string
  avgDuration: number
  satisfaction: number
  highValueUsers: {
    count: number
    avgActivities: number
    description: string
  }
  activityTrend: {
    growth: number
    avgInteractions: number
    description: string
  }
  activities: Array<{
    name: string
    type: string
    recommendations: number
    acceptanceRate: number
    avgScore: number
    insights: string
  }>
  algorithmMetrics: Array<{
    name: string
    value: string
    trend: number
  }>
  suggestions: Array<{
    id: string
    title: string
    description: string
    type: 'success' | 'warning'
  }>
}

const timeRange = ref<'week' | 'month' | 'year'>('week')
const chartType = ref<'precision' | 'coverage' | 'engagement'>('precision')
const analysisTab = ref<'user' | 'activity' | 'algorithm'>('user')

const trendChartRef = ref()
const typeChartRef = ref()
const preferenceChartRef = ref()
const accuracyChartRef = ref()

const kpis = ref<KPI[]>([])
const analysisData = ref<AnalysisData>({
  activeUsers: 0,
  avgRecommendations: 0,
  acceptanceRate: 0,
  topType: '',
  avgDuration: 0,
  satisfaction: 0,
  highValueUsers: {
    count: 0,
    avgActivities: 0,
    description: ''
  },
  activityTrend: {
    growth: 0,
    avgInteractions: 0,
    description: ''
  },
  activities: [],
  algorithmMetrics: [],
  suggestions: []
})

let charts: {
  trend?: echarts.ECharts
  type?: echarts.ECharts
  preference?: echarts.ECharts
  accuracy?: echarts.ECharts
} = {}

const setTimeRange = (range: 'week' | 'month' | 'year') => {
  timeRange.value = range
  refreshData()
}

const refreshData = async () => {
  try {
    await Promise.all([
      loadKPIs(),
      loadAnalysisData(),
      renderCharts()
    ])
  } catch (error) {
    console.error('刷新数据失败:', error)
  }
}

const loadKPIs = async () => {
  const kpiData = await apiService.getRecommendationKPIs(timeRange.value)
  
  kpis.value = [
    {
      key: 'totalRecommendations',
      label: '总推荐数',
      value: kpiData.totalRecommendations,
      icon: 'Magic',
      color: '#409eff',
      trend: kpiData.recommendationsTrend
    },
    {
      key: 'acceptanceRate',
      label: '接受率',
      value: `${kpiData.acceptanceRate}%`,
      icon: 'UserFilled',
      color: '#67c23a',
      trend: kpiData.acceptanceTrend
    },
    {
      key: 'userEngagement',
      label: '用户参与度',
      value: `${kpiData.userEngagement}%`,
      icon: 'TrendCharts',
      color: '#e6a23c',
      trend: kpiData.engagementTrend
    },
    {
      key: 'precision',
      label: '推荐精确度',
      value: `${kpiData.precision}%`,
      icon: 'Target',
      color: '#f56c6c',
      trend: kpiData.precisionTrend
    }
  ]
}

const loadAnalysisData = async () => {
  analysisData.value = await apiService.getRecommendationAnalysis(timeRange.value)
}

const renderCharts = () => {
  renderTrendChart()
  renderTypeChart()
  renderPreferenceChart()
  renderAccuracyChart()
}

const renderTrendChart = () => {
  if (!trendChartRef.value) return
  
  if (charts.trend) {
    charts.trend.dispose()
  }
  
  charts.trend = echarts.init(trendChartRef.value)
  
  const chartData = getTrendChartData()
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: chartData.labels
    },
    yAxis: {
      type: 'value',
      max: 100,
      axisLabel: {
        formatter: '{value}%'
      }
    },
    series: [{
      data: chartData.data,
      type: 'line',
      smooth: true,
      itemStyle: {
        color: '#409eff'
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
          { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
        ])
      }
    }]
  }
  
  charts.trend.setOption(option)
}

const renderTypeChart = () => {
  if (!typeChartRef.value) return
  
  if (charts.type) {
    charts.type.dispose()
  }
  
  charts.type = echarts.init(typeChartRef.value)
  
  const chartData = getTypeChartData()
  
  const option = {
    tooltip: {
      trigger: 'item'
    },
    series: [{
      type: 'pie',
      radius: '60%',
      data: chartData,
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  }
  
  charts.type.setOption(option)
}

const renderPreferenceChart = () => {
  if (!preferenceChartRef.value) return
  
  if (charts.preference) {
    charts.preference.dispose()
  }
  
  charts.preference = echarts.init(preferenceChartRef.value)
  
  const chartData = getPreferenceChartData()
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: chartData.labels
    },
    yAxis: {
      type: 'value',
      max: 100
    },
    series: [
      {
        name: '用户偏好',
        type: 'bar',
        data: chartData.data,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#67c23a' },
            { offset: 1, color: '#85ce61' }
          ])
        }
      }
    ]
  }
  
  charts.preference.setOption(option)
}

const renderAccuracyChart = () => {
  if (!accuracyChartRef.value) return
  
  if (charts.accuracy) {
    charts.accuracy.dispose()
  }
  
  charts.accuracy = echarts.init(accuracyChartRef.value)
  
  const chartData = getAccuracyChartData()
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    xAxis: {
      type: 'category',
      data: chartData.labels
    },
    yAxis: {
      type: 'value',
      max: 100,
      axisLabel: {
        formatter: '{value}%'
      }
    },
    series: [
      {
        name: '推荐准确率',
        type: 'bar',
        data: chartData.data,
        itemStyle: {
          color: '#409eff'
        }
      }
    ]
  }
  
  charts.accuracy.setOption(option)
}

const getTrendChartData = () => {
  const data = {
    precision: {
      labels: ['1月', '2月', '3月', '4月', '5月', '6月'],
      data: [65, 72, 78, 82, 85, 88]
    },
    coverage: {
      labels: ['1月', '2月', '3月', '4月', '5月', '6月'],
      data: [80, 78, 82, 85, 83, 87]
    },
    engagement: {
      labels: ['1月', '2月', '3月', '4月', '5月', '6月'],
      data: [45, 52, 58, 65, 72, 78]
    }
  }
  
  return data[chartType.value]
}

const getTypeChartData = () => {
  return [
    { value: 35, name: '环保活动' },
    { value: 25, name: '教育支持' },
    { value: 20, name: '健康医疗' },
    { value: 12, name: '养老服务' },
    { value: 8, name: '其他' }
  ]
}

const getPreferenceChartData = () => {
  return {
    labels: ['环保', '教育', '医疗', '养老', '其他'],
    data: [85, 78, 72, 65, 58]
  }
}

const getAccuracyChartData = () => {
  return {
    labels: ['基于类型', '基于地点', '基于时间', '基于技能', '基于历史', '综合推荐'],
    data: [78, 82, 75, 70, 85, 88]
  }
}

const getProgressColor = (percentage: number) => {
  if (percentage >= 80) return '#67c23a'
  if (percentage >= 60) return '#e6a23c'
  return '#f56c6c'
}

const applySuggestion = async (suggestion: any) => {
  try {
    await apiService.applyRecommendationSuggestion(suggestion.id)
    ElMessage.success('建议已应用')
    refreshData()
  } catch (error) {
    console.error('应用建议失败:', error)
    ElMessage.error('应用建议失败')
  }
}

const resizeCharts = () => {
  Object.values(charts).forEach(chart => {
    chart?.resize()
  })
}

onMounted(async () => {
  await refreshData()
  window.addEventListener('resize', resizeCharts)
})

onUnmounted(() => {
  window.removeEventListener('resize', resizeCharts)
  Object.values(charts).forEach(chart => {
    chart?.dispose()
  })
})
</script>

<style scoped>
.recommendation-analytics {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 10px;
}

.header-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.kpi-cards {
  margin-bottom: 20px;
}

.kpi-card {
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.kpi-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.kpi-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.kpi-info {
  flex: 1;
}

.kpi-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}

.kpi-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 4px;
}

.kpi-trend {
  font-size: 12px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 4px;
}

.kpi-trend.positive {
  color: #67c23a;
}

.kpi-trend.negative {
  color: #f56c6c;
}

.chart-card {
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.chart-container {
  height: 300px;
  width: 100%;
}

.analysis-card {
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.analysis-content {
  padding: 16px 0;
}

.user-insights {
  margin-top: 20px;
}

.insights-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.insight-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.insight-content {
  flex: 1;
}

.insight-content h5 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #303133;
}

.insight-content p {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
}

.insight-metrics {
  display: flex;
  gap: 12px;
}

.insight-metrics span {
  font-size: 12px;
  padding: 4px 8px;
  background: #e3f2fd;
  color: #1976d2;
  border-radius: 4px;
}

.algorithm-metrics {
  margin-bottom: 24px;
}

.metric-card {
  text-align: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.metric-value {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}

.metric-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.metric-trend {
  font-size: 12px;
  font-weight: bold;
}

.metric-trend.positive {
  color: #67c23a;
}

.metric-trend.negative {
  color: #f56c6c;
}

.algorithm-insights {
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #e4e7ed;
}

.suggestions-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.suggestion-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.suggestion-content {
  flex: 1;
}

.suggestion-content h5 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #303133;
}

.suggestion-content p {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
}

.warning {
  color: #e6a23c;
}

.success {
  color: #67c23a;
}
</style>