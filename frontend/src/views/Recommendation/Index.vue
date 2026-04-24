<template>
  <div class="recommendation-page">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <template #header><span>录取概率预测</span></template>
          <el-form label-width="100px">
            <el-form-item label="目标学校">
              <el-select
                v-model="selectedSchoolId"
                filterable
                remote
                reserve-keyword
                placeholder="输入学校名称搜索"
                :remote-method="searchSchools"
                :loading="schoolSearching"
                @change="handleSchoolSelect"
              >
                <el-option
                  v-for="s in schoolOptions"
                  :key="s.id"
                  :label="s.name"
                  :value="s.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="calculating" :disabled="!selectedSchoolId" @click="calculateProb">开始预测</el-button>
            </el-form-item>
          </el-form>
          <div v-if="probResult" class="prob-result">
            <el-descriptions :column="1" border>
              <el-descriptions-item label="你的总分">{{ probResult.student_total_score }}</el-descriptions-item>
              <el-descriptions-item label="去年录取线">{{ probResult.school_admission_score_year1 }}</el-descriptions-item>
              <el-descriptions-item label="前年录取线">{{ probResult.school_admission_score_year2 }}</el-descriptions-item>
              <el-descriptions-item label="三年前录取线">{{ probResult.school_admission_score_year3 }}</el-descriptions-item>
              <el-descriptions-item label="录取概率">
                <el-progress
                  :percentage="probResult.probability || 0"
                  :stroke-width="18"
                  :text-inside="true"
                  :color="getProbabilityColor(probResult.probability)"
                />
              </el-descriptions-item>
              <el-descriptions-item label="建议">{{ probResult.message || '-' }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div style="display:flex;justify-content:space-between;align-items:center">
              <span>智能推荐学校</span>
              <el-button type="primary" size="small" :loading="loading" @click="loadRecommendations">获取推荐</el-button>
            </div>
          </template>
          <el-table :data="recommendations" stripe v-loading="loading">
            <el-table-column prop="school_name" label="学校名称" min-width="180" />
            <el-table-column prop="school_type" label="类型" width="100">
              <template #default="{ row }">
                <el-tag size="small">{{ row.school_type === 'KEY' ? '重点' : row.school_type === 'REGULAR' ? '普通' : '职高' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="录取概率" width="120">
              <template #default="{ row }">{{ row.probability }}%</template>
            </el-table-column>
            <el-table-column prop="reason" label="推荐理由" min-width="200" />
          </el-table>
          <el-empty v-if="!loading && recommendations.length === 0" description="点击获取推荐按钮生成推荐" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { calculateProbability, generateRecommendations } from '@/api/recommendation'
import { getSchoolList } from '@/api/school'

const calculating = ref(false)
const loading = ref(false)
const probResult = ref<any>(null)
const recommendations = ref<any[]>([])
const selectedSchoolId = ref<number | null>(null)
const schoolOptions = ref<any[]>([])
const schoolSearching = ref(false)

const searchSchools = async (query: string) => {
  if (!query) return
  schoolSearching.value = true
  try {
    const res = await getSchoolList({ keyword: query, page: 0, size: 20 })
    const data = res.data || res
    schoolOptions.value = data?.schools || data?.content || []
  } catch (e) {
    schoolOptions.value = []
  } finally {
    schoolSearching.value = false
  }
}

const handleSchoolSelect = (id: number) => {
  selectedSchoolId.value = id
}

const calculateProb = async () => {
  if (!selectedSchoolId.value) {
    ElMessage.warning('请先选择学校')
    return
  }
  calculating.value = true
  try {
    const res = await calculateProbability(selectedSchoolId.value)
    probResult.value = res.data || res
  } catch (e: any) {
    ElMessage.error(e.message || '预测失败')
  } finally {
    calculating.value = false
  }
}

const loadRecommendations = async () => {
  loading.value = true
  try {
    const res = await generateRecommendations()
    const data = res.data || res
    recommendations.value = data?.recommendations || []
  } catch (e: any) {
    ElMessage.error(e.message || '获取推荐失败')
  } finally {
    loading.value = false
  }
}

const getProbabilityColor = (probability: number) => {
  if (probability >= 70) return '#67c23a'
  if (probability >= 40) return '#e6a23c'
  return '#f56c6c'
}

onMounted(() => {
  loadRecommendations()
})
</script>

<style scoped>
.prob-result {
  margin-top: 20px;
}
</style>
