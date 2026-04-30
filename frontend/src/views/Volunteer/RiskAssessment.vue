<template>
  <div class="risk-assessment-page">
    <PageHeader title="志愿风险评估" subtitle="分析志愿方案的整体风险，获取智能建议">
      <template #actions>
        <el-select v-model="selectedPlanId" placeholder="选择志愿方案" @change="loadRisk" style="width: 250px; margin-right: 12px">
          <el-option v-for="p in plans" :key="p.id" :label="p.simulationName || ('方案 #' + p.id)" :value="p.id" />
        </el-select>
        <el-button type="primary" :loading="loading" :disabled="!selectedPlanId" @click="loadRisk">分析风险</el-button>
      </template>
    </PageHeader>

    <template v-if="risk">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="metric-card" :class="'risk-' + risk.overallRiskLevel.toLowerCase()">
            <div class="metric-value">{{ riskLabel }}</div>
            <div class="metric-label">整体风险</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="metric-card">
            <div class="metric-value" :style="{ color: risk.slideProbability > 20 ? '#f56c6c' : risk.slideProbability > 10 ? '#e6a23c' : '#67c23a' }">
              {{ risk.slideProbability }}%
            </div>
            <div class="metric-label">滑档概率</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="metric-card">
            <div class="metric-value" style="color: #409eff">{{ (risk.safetyScore * 100).toFixed(0) }}%</div>
            <div class="metric-label">保底安全度</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="metric-card">
            <div class="metric-value" style="color: #e6a23c">{{ (risk.balanceScore * 100).toFixed(0) }}%</div>
            <div class="metric-label">方案平衡度</div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="12">
          <el-card>
            <template #header><span>各志愿录取概率</span></template>
            <EChart :option="probChartOption" height="320px" />
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card>
            <template #header><span>风险评估雷达图</span></template>
            <EChart :option="radarChartOption" height="320px" />
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="24">
          <el-card>
            <template #header><span>各志愿详情</span></template>
            <el-table :data="risk.schoolRisks" stripe>
              <el-table-column prop="order" label="志愿序号" width="100">
                <template #default="{ row }">第{{ row.order }}志愿</template>
              </el-table-column>
              <el-table-column prop="schoolName" label="学校名称" min-width="180" />
              <el-table-column label="录取概率" width="200">
                <template #default="{ row }">
                  <el-progress :percentage="row.probability" :stroke-width="16" :text-inside="true" :color="getProbColor(row.probability)" />
                </template>
              </el-table-column>
              <el-table-column label="置信区间" width="140">
                <template #default="{ row }">{{ row.lowerBound }}% ~ {{ row.upperBound }}%</template>
              </el-table-column>
              <el-table-column prop="probabilityLevel" label="概率等级" width="100">
                <template #default="{ row }">
                  <el-tag :type="row.probabilityLevel === 'HIGH' ? 'success' : row.probabilityLevel === 'MEDIUM' ? 'warning' : 'danger'" size="small">
                    {{ row.probabilityLevel === 'HIGH' ? '高' : row.probabilityLevel === 'MEDIUM' ? '中' : '低' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="riskTag" label="定位" width="120">
                <template #default="{ row }">
                  <el-tag :type="row.riskTag.includes('冲刺') ? 'danger' : row.riskTag.includes('保底') ? 'success' : 'warning'" size="small">{{ row.riskTag }}</el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :span="24">
          <el-card>
            <template #header><span>智能建议</span></template>
            <el-timeline>
              <el-timeline-item v-for="(s, i) in risk.suggestions" :key="i" :type="i === 0 ? 'primary' : 'info'" placement="top">
                <p>{{ s }}</p>
              </el-timeline-item>
            </el-timeline>
            <el-alert :title="risk.summary" type="info" show-icon :closable="false" style="margin-top: 16px" />
          </el-card>
        </el-col>
      </el-row>
    </template>

    <el-empty v-if="!risk && !loading" description="请选择志愿方案进行风险评估" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/Common/PageHeader.vue'
import EChart from '@/components/Common/EChart.vue'
import { getRiskAssessment } from '@/api/planAnalysis'
import { getApplications } from '@/api/volunteer'

const loading = ref(false)
const plans = ref<any[]>([])
const selectedPlanId = ref<number>()
const risk = ref<any>(null)

const riskLabel = computed(() => {
  if (!risk.value) return ''
  const m: Record<string, string> = { HIGH: '高风险', MEDIUM: '中等风险', LOW: '低风险' }
  return m[risk.value.overallRiskLevel] || risk.value.overallRiskLevel
})

const probChartOption = computed(() => {
  if (!risk.value?.schoolRisks) return {}
  const data = risk.value.schoolRisks
  return {
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: data.map((r: any) => '第' + r.order + '志愿'), axisLabel: { fontSize: 12 } },
    yAxis: { type: 'value', max: 100, axisLabel: { formatter: '{value}%' } },
    series: [{
      type: 'bar',
      data: data.map((r: any) => ({
        value: r.probability,
        itemStyle: { color: r.probability >= 70 ? '#67c23a' : r.probability >= 40 ? '#e6a23c' : '#f56c6c' }
      })),
      barWidth: '40%',
      label: { show: true, position: 'top', formatter: '{c}%' }
    }]
  }
})

const radarChartOption = computed(() => {
  if (!risk.value) return {}
  return {
    tooltip: {},
    radar: {
      indicator: [
        { name: '安全度', max: 100 },
        { name: '冲刺力', max: 100 },
        { name: '平衡度', max: 100 },
        { name: '稳定性', max: 100 }
      ]
    },
    series: [{
      type: 'radar',
      data: [{
        value: [
          (risk.value.safetyScore * 100).toFixed(0),
          (risk.value.sprintScore * 100).toFixed(0),
          (risk.value.balanceScore * 100).toFixed(0),
          (100 - risk.value.slideProbability).toFixed(0)
        ],
        name: '当前方案',
        areaStyle: { color: 'rgba(64,158,255,0.3)' },
        lineStyle: { color: '#409eff' },
        itemStyle: { color: '#409eff' }
      }]
    }]
  }
})

onMounted(() => { loadPlans() })

async function loadPlans() {
  try {
    const res = await getApplications()
    plans.value = res.data?.content || res.data?.applications || res.data || []
  } catch (e: any) { ElMessage.error('加载方案失败') }
}

async function loadRisk() {
  if (!selectedPlanId.value) return
  loading.value = true
  try {
    const res = await getRiskAssessment(selectedPlanId.value)
    risk.value = res.data
    ElMessage.success('风险评估完成')
  } catch (e: any) { ElMessage.error(e.message || '风险评估失败') }
  finally { loading.value = false }
}

function getProbColor(p: number) {
  if (p >= 70) return '#67c23a'
  if (p >= 40) return '#e6a23c'
  return '#f56c6c'
}
</script>

<style scoped>
.risk-assessment-page { padding: 20px; }
.metric-card { text-align: center; padding: 10px 0; }
.metric-card .metric-value { font-size: 28px; font-weight: bold; }
.metric-card .metric-label { font-size: 14px; color: #909399; margin-top: 8px; }
.risk-high .metric-value { color: #f56c6c; }
.risk-medium .metric-value { color: #e6a23c; }
.risk-low .metric-value { color: #67c23a; }
</style>
