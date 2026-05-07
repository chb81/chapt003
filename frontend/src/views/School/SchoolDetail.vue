<template>
  <div class="school-detail" v-loading="loading">
    <el-page-header @back="$router.back()" title="返回列表" :content="school.name || '学校详情'" />

    <el-card v-if="school.id" style="margin-top: 20px">
      <el-descriptions :column="3" border>
        <el-descriptions-item label="学校名称">{{ school.name }}</el-descriptions-item>
        <el-descriptions-item label="学校类型">
          <el-tag :type="school.type === 'KEY_HIGH_SCHOOL' ? 'danger' : school.type === 'REGULAR_HIGH_SCHOOL' ? '' : 'info'">
            {{ typeLabel }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="学校性质">
          <el-tag v-if="school.schoolNature" type="warning">{{ school.schoolNature }}</el-tag>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="所在城市">{{ school.city || '-' }}</el-descriptions-item>
        <el-descriptions-item label="所在区县">{{ school.district || '-' }}</el-descriptions-item>
        <el-descriptions-item label="学校排名" v-if="school.schoolRank">
          第{{ school.schoolRank }}名
        </el-descriptions-item>
        <el-descriptions-item label="学校地址">{{ school.address || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ school.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="招生计划">{{ school.enrollmentQuota ? school.enrollmentQuota + '人' : '-' }}</el-descriptions-item>
        <el-descriptions-item label="学校等级" v-if="school.schoolLevel">
          <el-tag type="warning">{{ school.schoolLevel }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="学校代码" v-if="school.schoolCode">{{ school.schoolCode }}</el-descriptions-item>
      </el-descriptions>

      <div v-if="school.features" style="margin-top: 16px">
        <h4>学校特色</h4>
        <p>{{ school.features }}</p>
      </div>

      <div v-if="school.schoolRemark" style="margin-top: 16px">
        <h4>备注</h4>
        <p>{{ school.schoolRemark }}</p>
      </div>

      <div v-if="school.description" style="margin-top: 16px">
        <h4>学校简介</h4>
        <p class="school-desc">{{ school.description }}</p>
      </div>
    </el-card>

    <el-row :gutter="20" style="margin-top: 20px" v-if="hasScoreData">
      <el-col :span="24">
        <el-card>
          <template #header><span>历年录取分数线趋势</span></template>
          <EChart :option="scoreTrendOption" height="350px" />
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px" v-if="school.id">
      <template #header><span>分配生/指标生名额</span></template>
      <el-table :data="quotas" stripe v-loading="quotaLoading">
        <el-table-column prop="sourceSchoolName" label="生源初中" min-width="180" />
        <el-table-column prop="quotaCount" label="分配名额" width="100" />
        <el-table-column prop="admissionScore" label="分配生分数线" width="140" />
        <el-table-column prop="unifiedScore" label="统招分数线" width="120" />
        <el-table-column prop="scoreDifference" label="分数线差" width="120">
          <template #default="{ row }">
            <span v-if="row.scoreDifference" style="color: #67c23a; font-weight: bold">降{{ row.scoreDifference }}分</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!quotaLoading && quotas.length === 0" description="暂无分配生数据" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getSchoolDetail } from '@/api/school'
import { getAllocationQuotas } from '@/api/planAnalysis'
import EChart from '@/components/Common/EChart.vue'

const route = useRoute()
const loading = ref(false)
const quotaLoading = ref(false)
const school = ref<any>({})
const quotas = ref<any[]>([])

const typeLabel = computed(() => {
  const m: Record<string, string> = { KEY_HIGH_SCHOOL: '重点高中', REGULAR_HIGH_SCHOOL: '普通高中', VOCATIONAL_HIGH_SCHOOL: '职业高中' }
  return m[school.value.type] || school.value.type || '-'
})

const hasScoreData = computed(() => {
  return school.value.admissionScoreYear1 || school.value.admissionScoreYear2 || school.value.admissionScoreYear3
})

const scoreTrendOption = computed(() => {
  const s = school.value
  const years = []
  const scores = []
  if (s.admissionScoreYear3) { years.push('三年前'); scores.push(s.admissionScoreYear3) }
  if (s.admissionScoreYear2) { years.push('两年前'); scores.push(s.admissionScoreYear2) }
  if (s.admissionScoreYear1) { years.push('去年'); scores.push(s.admissionScoreYear1) }
  return {
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: years },
    yAxis: { type: 'value', axisLabel: { formatter: '{value}分' } },
    series: [{
      type: 'line',
      data: scores,
      smooth: true,
      lineStyle: { width: 3, color: '#409eff' },
      itemStyle: { color: '#409eff' },
      areaStyle: { color: 'rgba(64,158,255,0.15)' },
      label: { show: true, position: 'top', formatter: '{c}分' }
    }]
  }
})

onMounted(async () => {
  const id = Number(route.params.id)
  if (!id) return
  loading.value = true
  try {
    const res = await getSchoolDetail(id)
    school.value = res.data || {}
  } catch (e: any) { school.value = {} }
  finally { loading.value = false }

  quotaLoading.value = true
  try {
    const qRes = await getAllocationQuotas(id)
    quotas.value = qRes.data || []
  } catch (e: any) { quotas.value = [] }
  finally { quotaLoading.value = false }
})
</script>

<style scoped>
.school-desc { line-height: 1.8; color: #666; text-indent: 2em; }
</style>
