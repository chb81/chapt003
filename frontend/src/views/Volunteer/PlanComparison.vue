<template>
  <div class="plan-comparison">
    <PageHeader title="方案对比分析" subtitle="对比不同志愿方案的风险和录取概率">
      <template #actions>
        <el-button type="primary" @click="showSelectDialog = true" :disabled="loading">
          选择方案对比
        </el-button>
      </template>
    </PageHeader>

    <el-row :gutter="20" v-if="comparisonResult">
      <el-col :span="24">
        <el-card class="recommendation-card">
          <template #header>
            <span>对比建议</span>
          </template>
          <el-alert
            :title="comparisonResult.recommendation"
            :description="comparisonResult.recommendationReason"
            type="success"
            show-icon
            :closable="false"
          />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" v-if="comparisonResult && comparisonResult.plans">
      <el-col :span="12" v-for="plan in comparisonResult.plans" :key="plan.planId">
        <el-card class="plan-card">
          <template #header>
            <div class="plan-header">
              <span>{{ plan.planName || ('方案 #' + plan.planId) }}</span>
              <el-tag :type="getRiskTagType(plan.riskLevel)">{{ getRiskLabel(plan.riskLevel) }}</el-tag>
            </div>
          </template>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="学校数量">{{ plan.schoolCount }}所</el-descriptions-item>
            <el-descriptions-item label="平均概率">{{ plan.averageProbability }}%</el-descriptions-item>
            <el-descriptions-item label="最高概率">{{ plan.maxProbability }}%</el-descriptions-item>
            <el-descriptions-item label="最低概率">{{ plan.minProbability }}%</el-descriptions-item>
            <el-descriptions-item label="滑档概率">
              <span :class="getSlideClass(plan.slideProbability)">{{ plan.slideProbability }}%</span>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" v-if="comparisonResult && comparisonResult.metrics">
      <el-col :span="24">
        <el-card>
          <template #header><span>对比指标</span></template>
          <el-descriptions :column="3" border>
            <el-descriptions-item label="概率差异">{{ comparisonResult.metrics.probabilityDifference }}%</el-descriptions-item>
            <el-descriptions-item label="风险差异">{{ comparisonResult.metrics.riskDifference }}%</el-descriptions-item>
            <el-descriptions-item label="更安全方案">{{ comparisonResult.metrics.saferPlan }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-if="!comparisonResult && !loading" description="请选择至少两个方案进行对比" />

    <el-dialog v-model="showSelectDialog" title="选择对比方案" width="500px">
      <el-checkbox-group v-model="selectedPlanIds">
        <el-checkbox v-for="plan in allPlans" :key="plan.id" :label="plan.id" :value="plan.id">
          {{ plan.simulationName || ('方案 #' + plan.id) }}
        </el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="showSelectDialog = false">取消</el-button>
        <el-button type="primary" @click="doCompare" :disabled="selectedPlanIds.length < 2">
          开始对比
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/Common/PageHeader.vue'
import { comparePlans } from '@/api/planAnalysis'
import { getApplications } from '@/api/volunteer'

const loading = ref(false)
const showSelectDialog = ref(false)
const allPlans = ref<any[]>([])
const selectedPlanIds = ref<number[]>([])
const comparisonResult = ref<any>(null)

onMounted(() => {
  loadPlans()
})

async function loadPlans() {
  try {
    const res = await getApplications()
    allPlans.value = res.data?.content || res.data || []
  } catch (e: any) {
    ElMessage.error('加载方案列表失败')
  }
}

async function doCompare() {
  if (selectedPlanIds.value.length < 2) {
    ElMessage.warning('请至少选择两个方案')
    return
  }
  loading.value = true
  try {
    const res = await comparePlans(selectedPlanIds.value)
    comparisonResult.value = res.data
    showSelectDialog.value = false
    ElMessage.success('对比分析完成')
  } catch (e: any) {
    ElMessage.error(e.message || '对比分析失败')
  } finally {
    loading.value = false
  }
}

function getRiskTagType(level: string) {
  if (level === 'LOW') return 'success'
  if (level === 'MEDIUM') return 'warning'
  return 'danger'
}

function getRiskLabel(level: string) {
  if (level === 'LOW') return '低风险'
  if (level === 'MEDIUM') return '中等风险'
  return '高风险'
}

function getSlideClass(prob: number) {
  if (prob > 20) return 'text-danger'
  if (prob > 10) return 'text-warning'
  return 'text-success'
}
</script>

<style scoped>
.plan-comparison { padding: 20px; }
.plan-card { margin-bottom: 20px; }
.plan-header { display: flex; justify-content: space-between; align-items: center; }
.recommendation-card { margin-bottom: 20px; }
.text-danger { color: #f56c6c; font-weight: bold; }
.text-warning { color: #e6a23c; font-weight: bold; }
.text-success { color: #67c23a; font-weight: bold; }
</style>
