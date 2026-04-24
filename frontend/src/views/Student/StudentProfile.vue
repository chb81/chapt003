<template>
  <div class="student-profile">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>学生信息管理</span>
          <el-button v-if="!profileEditing" type="primary" @click="profileEditing = true">编辑信息</el-button>
          <div v-else>
            <el-button type="primary" :loading="savingProfile" @click="handleSaveProfile">保存</el-button>
            <el-button @click="cancelProfileEdit">取消</el-button>
          </div>
        </div>
      </template>

      <div v-if="!hasProfile && !profileEditing" class="empty-state">
        <el-empty description="尚未填写学生信息">
          <el-button type="primary" @click="profileEditing = true">立即填写</el-button>
        </el-empty>
      </div>

      <el-form v-else :model="profileForm" label-width="100px" :disabled="!profileEditing">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" required>
              <el-input v-model="profileForm.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" required>
              <el-radio-group v-model="profileForm.gender">
                <el-radio label="MALE">男</el-radio>
                <el-radio label="FEMALE">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出生日期" required>
              <el-date-picker v-model="profileForm.birthDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="所在城市">
              <el-input v-model="profileForm.city" placeholder="请输入所在城市" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="所在区县">
              <el-input v-model="profileForm.district" placeholder="请输入所在区县" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="毕业学校">
              <el-input v-model="profileForm.school" placeholder="请输入毕业初中" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>成绩信息</span>
          <div>
            <el-button v-if="hasScore" type="primary" @click="scoreEditing = true" :disabled="scoreEditing">编辑成绩</el-button>
            <el-button v-if="hasScore" type="danger" plain @click="handleDeleteScore">删除成绩</el-button>
          </div>
        </div>
      </template>

      <div v-if="!hasScore && !scoreEditing" class="empty-state">
        <el-empty description="尚未录入成绩">
          <el-button type="primary" @click="scoreEditing = true">立即录入</el-button>
        </el-empty>
      </div>

      <div v-else>
        <el-form :model="scoreForm" label-width="80px" :disabled="!scoreEditing">
          <el-row :gutter="16">
            <el-col :span="8" v-for="subj in subjects" :key="subj.key">
              <el-form-item :label="subj.label">
                <el-input-number v-model="scoreForm[subj.key]" :min="0" :max="150" :precision="1" :step="1" style="width: 100%" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row v-if="scoreEditing" style="margin-top: 10px">
            <el-col :span="24" style="text-align: right">
              <el-button type="primary" :loading="savingScore" @click="handleSaveScore">保存成绩</el-button>
              <el-button @click="cancelScoreEdit">取消</el-button>
            </el-col>
          </el-row>
          <el-row v-if="hasScore" :gutter="16" style="margin-top: 16px; padding-top: 16px; border-top: 1px solid #eee">
            <el-col :span="8">
              <el-statistic title="总分" :value="scoreData.totalScore || 0" />
            </el-col>
            <el-col :span="8">
              <el-statistic title="平均分" :value="scoreData.averageScore || 0" :precision="1" />
            </el-col>
            <el-col :span="8">
              <el-statistic title="科目数" :value="subjects.length" />
            </el-col>
          </el-row>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getStudentProfile, createStudentProfile, updateStudentProfile, deleteStudentProfile,
  getStudentScore, createStudentScore, updateStudentScore, deleteStudentScore
} from '@/api/student'

const profileEditing = ref(false)
const savingProfile = ref(false)
const scoreEditing = ref(false)
const savingScore = ref(false)
const hasProfile = ref(false)
const hasScore = ref(false)

const profileForm = reactive({
  name: '', gender: 'MALE', birthDate: '', city: '', district: '', school: ''
})

const scoreForm = reactive<Record<string, number | null>>({
  chinese: null, math: null, english: null,
  physics: null, chemistry: null, politics: null,
  history: null, geography: null, biology: null
})

const scoreData = ref<any>({})

const subjects = [
  { key: 'chinese', label: '语文' },
  { key: 'math', label: '数学' },
  { key: 'english', label: '英语' },
  { key: 'physics', label: '物理' },
  { key: 'chemistry', label: '化学' },
  { key: 'politics', label: '政治' },
  { key: 'history', label: '历史' },
  { key: 'geography', label: '地理' },
  { key: 'biology', label: '生物' }
]

const loadProfile = async () => {
  try {
    const res = await getStudentProfile()
    const data = res.data
    if (data && data.id) {
      hasProfile.value = true
      Object.assign(profileForm, {
        name: data.name || '',
        gender: data.gender || 'MALE',
        birthDate: data.birthDate || '',
        city: data.city || '',
        district: data.district || '',
        school: data.school || ''
      })
    }
  } catch (e) {
    hasProfile.value = false
  }
}

const loadScore = async () => {
  try {
    const res = await getStudentScore()
    const data = res.data
    if (data && data.id) {
      hasScore.value = true
      scoreData.value = data
      Object.keys(scoreForm).forEach(key => {
        scoreForm[key] = data[key] != null ? Number(data[key]) : null
      })
    }
  } catch (e) {
    hasScore.value = false
  }
}

const handleSaveProfile = async () => {
  if (!profileForm.name) { ElMessage.warning('请输入姓名'); return }
  if (!profileForm.birthDate) { ElMessage.warning('请选择出生日期'); return }
  savingProfile.value = true
  try {
    if (hasProfile.value) {
      await updateStudentProfile(profileForm)
    } else {
      await createStudentProfile(profileForm)
    }
    ElMessage.success('保存成功')
    profileEditing.value = false
    loadProfile()
  } catch (e: any) {
    ElMessage.error(e.message || '保存失败')
  } finally {
    savingProfile.value = false
  }
}

const cancelProfileEdit = () => {
  profileEditing.value = false
  loadProfile()
}

const handleSaveScore = async () => {
  const hasAny = subjects.some(s => scoreForm[s.key] !== null && scoreForm[s.key] !== undefined)
  if (!hasAny) { ElMessage.warning('请至少填写一个科目成绩'); return }
  savingScore.value = true
  try {
    const payload: Record<string, number> = {}
    subjects.forEach(s => {
      if (scoreForm[s.key] != null) payload[s.key] = scoreForm[s.key] as number
    })
    if (hasScore.value) {
      await updateStudentScore(payload)
    } else {
      await createStudentScore(payload)
    }
    ElMessage.success('成绩保存成功')
    scoreEditing.value = false
    loadScore()
  } catch (e: any) {
    ElMessage.error(e.message || '保存失败')
  } finally {
    savingScore.value = false
  }
}

const cancelScoreEdit = () => {
  scoreEditing.value = false
  loadScore()
}

const handleDeleteScore = async () => {
  try {
    await ElMessageBox.confirm('确定要删除成绩信息吗？', '确认', { type: 'warning' })
    await deleteStudentScore()
    ElMessage.success('成绩已删除')
    hasScore.value = false
    Object.keys(scoreForm).forEach(key => { scoreForm[key] = null })
    scoreData.value = {}
  } catch (e: any) {
    if (e !== 'cancel') ElMessage.error(e.message || '删除失败')
  }
}

onMounted(() => {
  loadProfile()
  loadScore()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.empty-state {
  padding: 40px 0;
  text-align: center;
}
</style>
