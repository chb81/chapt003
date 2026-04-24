<template>
  <div class="volunteer-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>志愿填报</span>
          <el-button type="primary" @click="showCreateDialog = true">新建志愿表</el-button>
        </div>
      </template>

      <div v-if="applications.length === 0" class="empty-state">
        <el-empty description="尚未创建志愿表">
          <el-button type="primary" @click="showCreateDialog = true">创建志愿表</el-button>
        </el-empty>
      </div>

      <el-table v-else :data="applications" stripe>
        <el-table-column prop="year" label="年份" width="100" />
        <el-table-column prop="simulationName" label="方案名称" min-width="200">
          <template #default="{ row }">{{ row.simulationName || '正式志愿' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 'DRAFT' ? 'info' : row.status === 'SUBMITTED' ? 'success' : 'warning'">
              {{ statusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="志愿数" width="100">
          <template #default="{ row }">{{ row.items?.length || 0 }}</template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewApplication(row)">查看</el-button>
            <el-button v-if="row.status === 'DRAFT'" type="success" link @click="submitApp(row.id)">提交</el-button>
            <el-button type="warning" link @click="simulateApp(row)">模拟</el-button>
            <el-button v-if="row.status === 'DRAFT'" type="danger" link @click="deleteApp(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card v-if="currentApp" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>{{ currentApp.simulationName || '正式志愿' }} ({{ currentApp.year }}) - 志愿列表</span>
          <el-button v-if="currentApp.status === 'DRAFT'" type="primary" @click="showAddVolunteer = true">添加志愿</el-button>
        </div>
      </template>

      <el-table :data="currentApp.items || []" stripe>
        <el-table-column type="index" label="志愿顺序" width="100" />
        <el-table-column prop="schoolName" label="学校名称" min-width="200" />
        <el-table-column label="操作" width="200">
          <template #default="{ row, $index }">
            <el-button v-if="$index > 0" type="primary" link @click="moveUp($index)">上移</el-button>
            <el-button v-if="$index < (currentApp.items?.length || 0) - 1" link @click="moveDown($index)">下移</el-button>
            <el-button v-if="currentApp.status === 'DRAFT'" type="danger" link @click="removeItem(row.schoolId)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showCreateDialog" title="新建志愿表" width="500px">
      <el-form :model="createForm" label-width="100px">
        <el-form-item label="年份" required>
          <el-date-picker v-model="createForm.year" type="year" placeholder="选择年份" value-format="YYYY" style="width:100%" />
        </el-form-item>
        <el-form-item label="方案名称">
          <el-input v-model="createForm.simulationName" placeholder="如：第一方案（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">创建</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showAddVolunteer" title="添加志愿" width="500px">
      <el-form :model="volunteerForm" label-width="100px">
        <el-form-item label="选择学校">
          <el-select
            v-model="volunteerForm.schoolId"
            filterable
            remote
            reserve-keyword
            placeholder="输入学校名称搜索"
            :remote-method="searchSchools"
            :loading="schoolSearching"
            style="width:100%"
          >
            <el-option v-for="s in schoolOptions" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-input-number v-model="volunteerForm.priority" :min="1" :max="8" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddVolunteer = false">取消</el-button>
        <el-button type="primary" @click="handleAddVolunteer">添加</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getApplications, createApplication, submitApplication, deleteApplication, createSimulation, addVolunteerItem, removeVolunteerItem, reorderItems, getApplicationDetail } from '@/api/volunteer'
import { getSchoolList } from '@/api/school'

const applications = ref<any[]>([])
const currentApp = ref<any>(null)
const showCreateDialog = ref(false)
const showAddVolunteer = ref(false)
const schoolOptions = ref<any[]>([])
const schoolSearching = ref(false)

const createForm = reactive({ year: '', simulationName: '' })
const volunteerForm = reactive({ schoolId: null as number | null, priority: 1 })

const statusText = (status: string) => {
  const map: Record<string, string> = { DRAFT: '草稿', SUBMITTED: '已提交', SIMULATION: '已模拟' }
  return map[status] || status
}

const formatDate = (date: string) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

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

const loadApplications = async () => {
  try {
    const res = await getApplications()
    const data = res.data || res
    applications.value = data?.applications || data || []
  } catch (e) {
    applications.value = []
  }
}

const viewApplication = async (app: any) => {
  try {
    const res = await getApplicationDetail(app.id)
    currentApp.value = res.data || res
  } catch (e: any) {
    currentApp.value = app
  }
}

const handleCreate = async () => {
  if (!createForm.year) { ElMessage.warning('请选择年份'); return }
  try {
    await createApplication({
      year: parseInt(createForm.year),
      simulationName: createForm.simulationName || null,
      items: []
    })
    ElMessage.success('创建成功')
    showCreateDialog.value = false
    createForm.year = ''
    createForm.simulationName = ''
    loadApplications()
  } catch (e: any) {
    ElMessage.error(e.message || '创建失败')
  }
}

const submitApp = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定提交此志愿表？提交后不可修改。', '提示', { type: 'warning' })
    await submitApplication(id)
    ElMessage.success('提交成功')
    loadApplications()
    if (currentApp.value?.id === id) {
      const res = await getApplicationDetail(id)
      currentApp.value = res.data || res
    }
  } catch (e: any) {
    if (e !== 'cancel') ElMessage.error(e.message || '提交失败')
  }
}

const simulateApp = async (app: any) => {
  try {
    const res = await createSimulation({
      year: app.year,
      simulationName: (app.simulationName || '正式志愿') + ' - 模拟',
      sourceApplicationId: app.id,
      items: app.items || []
    })
    ElMessage.success('模拟完成')
    loadApplications()
  } catch (e: any) {
    ElMessage.error(e.message || '模拟失败')
  }
}

const deleteApp = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定删除此志愿表？', '提示', { type: 'warning' })
    await deleteApplication(id)
    ElMessage.success('已删除')
    if (currentApp.value?.id === id) currentApp.value = null
    loadApplications()
  } catch (e: any) {
    if (e !== 'cancel') ElMessage.error(e.message || '删除失败')
  }
}

const handleAddVolunteer = async () => {
  if (!currentApp.value) return
  if (!volunteerForm.schoolId) { ElMessage.warning('请选择学校'); return }
  try {
    await addVolunteerItem(currentApp.value.id, {
      schoolId: volunteerForm.schoolId,
      priority: volunteerForm.priority
    })
    ElMessage.success('添加成功')
    showAddVolunteer.value = false
    volunteerForm.schoolId = null
    volunteerForm.priority = 1
    viewApplication(currentApp.value)
  } catch (e: any) {
    ElMessage.error(e.message || '添加失败')
  }
}

const removeItem = async (schoolId: number) => {
  if (!currentApp.value) return
  try {
    await removeVolunteerItem(currentApp.value.id, schoolId)
    ElMessage.success('已删除')
    viewApplication(currentApp.value)
  } catch (e: any) {
    ElMessage.error(e.message || '删除失败')
  }
}

const moveUp = async (index: number) => {
  if (!currentApp.value?.items || index <= 0) return
  const items = [...currentApp.value.items]
  const swap = items[index - 1]
  items[index - 1] = items[index]
  items[index] = swap
  try {
    await reorderItems(currentApp.value.id, { schoolIds: items.map((it: any) => it.schoolId || it.id) })
    viewApplication(currentApp.value)
  } catch (e: any) {
    ElMessage.error(e.message || '调整失败')
  }
}

const moveDown = async (index: number) => {
  if (!currentApp.value?.items) return
  const items = [...currentApp.value.items]
  if (index >= items.length - 1) return
  const swap = items[index + 1]
  items[index + 1] = items[index]
  items[index] = swap
  try {
    await reorderItems(currentApp.value.id, { schoolIds: items.map((it: any) => it.schoolId || it.id) })
    viewApplication(currentApp.value)
  } catch (e: any) {
    ElMessage.error(e.message || '调整失败')
  }
}

onMounted(() => { loadApplications() })
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
