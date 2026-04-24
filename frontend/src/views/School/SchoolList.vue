<template>
  <div class="school-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>学校浏览</span>
          <div v-if="isAdmin" class="admin-actions">
            <el-button type="info" @click="handleExport" :loading="exporting">
              <el-icon><Download /></el-icon>
              导出数据
            </el-button>
            <el-button type="success" @click="showImportDialog">
              <el-icon><Upload /></el-icon>
              批量导入
            </el-button>
            <el-button type="primary" @click="showCreateDialog">
              <el-icon><Plus /></el-icon>
              添加学校
            </el-button>
          </div>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="搜索">
          <el-input v-model="searchForm.keyword" placeholder="学校名称" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="城市">
          <el-select v-model="searchForm.city" placeholder="选择城市" clearable @change="handleCityChange">
            <el-option v-for="c in cities" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="区县">
          <el-select v-model="searchForm.district" placeholder="选择区县" clearable>
            <el-option v-for="d in districts" :key="d" :label="d" :value="d" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchForm.type" placeholder="学校类型" clearable>
            <el-option label="重点高中" value="KEY_HIGH_SCHOOL" />
            <el-option label="普通高中" value="REGULAR_HIGH_SCHOOL" />
            <el-option label="职业高中" value="VOCATIONAL_HIGH_SCHOOL" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="schools" v-loading="loading" stripe>
        <el-table-column prop="name" label="学校名称" min-width="180">
          <template #default="{ row }">
            <el-link type="primary" @click="$router.push(`/schools/${row.id}`)">{{ row.name }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === 'KEY' ? 'danger' : row.type === 'REGULAR' ? '' : 'warning'" size="small">
              {{ row.type === 'KEY' ? '重点' : row.type === 'REGULAR' ? '普通' : '职高' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="city" label="城市" width="100" />
        <el-table-column prop="district" label="区县" width="100" />
        <el-table-column prop="admissionScoreYear1" label="录取分数线" width="110">
          <template #default="{ row }">
            {{ row.admission_score_year1 || row.admissionScoreYear1 || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="enrollmentQuota" label="招生计划" width="100">
          <template #default="{ row }">
            {{ row.enrollment_quota || row.enrollmentQuota || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" :width="isAdmin ? 220 : 120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="$router.push(`/schools/${row.id}`)">查看详情</el-button>
            <el-button v-if="isAdmin" type="warning" link @click="showEditDialog(row)">编辑</el-button>
            <el-button v-if="isAdmin" type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-if="total > 0"
        v-model:current-page="searchForm.page"
        v-model:page-size="searchForm.size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @current-change="loadSchools"
        @size-change="loadSchools"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="isEditing ? '编辑学校' : '添加学校'"
      width="600px"
      destroy-on-close
    >
      <el-form ref="schoolFormRef" :model="schoolForm" :rules="schoolRules" label-width="120px">
        <el-form-item label="学校名称" prop="name">
          <el-input v-model="schoolForm.name" placeholder="请输入学校名称" />
        </el-form-item>
        <el-form-item label="学校类型" prop="type">
          <el-select v-model="schoolForm.type" placeholder="请选择学校类型">
            <el-option label="重点高中" value="KEY_HIGH_SCHOOL" />
            <el-option label="普通高中" value="REGULAR_HIGH_SCHOOL" />
            <el-option label="职业高中" value="VOCATIONAL_HIGH_SCHOOL" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="城市" prop="city">
              <el-input v-model="schoolForm.city" placeholder="城市" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="区县" prop="district">
              <el-input v-model="schoolForm.district" placeholder="区县" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="分数线1">
              <el-input-number v-model="schoolForm.admissionScoreYear1" :min="0" :max="800" :precision="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="分数线2">
              <el-input-number v-model="schoolForm.admissionScoreYear2" :min="0" :max="800" :precision="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="分数线3">
              <el-input-number v-model="schoolForm.admissionScoreYear3" :min="0" :max="800" :precision="1" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="招生计划">
          <el-input-number v-model="schoolForm.enrollmentQuota" :min="0" :max="10000" style="width: 200px" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="schoolForm.phone" placeholder="联系电话" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="schoolForm.address" placeholder="学校地址" />
        </el-form-item>
        <el-form-item label="学校描述">
          <el-input v-model="schoolForm.description" type="textarea" :rows="3" placeholder="学校描述" />
        </el-form-item>
        <el-form-item label="学校特色">
          <el-input v-model="schoolForm.features" type="textarea" :rows="2" placeholder="学校特色" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ isEditing ? '保存修改' : '创建学校' }}
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="importDialogVisible" title="批量导入学校" width="700px" destroy-on-close>
      <el-alert type="info" :closable="false" style="margin-bottom: 16px">
        <p>请粘贴 JSON 数组格式的学校数据，每条记录包含以下字段：</p>
        <p>name（必填）, type（必填: KEY/REGULAR/VOCATIONAL）, city, district, admissionScoreYear1, admissionScoreYear2, admissionScoreYear3, enrollmentQuota, phone, address, description, features</p>
      </el-alert>
      <el-input
        v-model="importJson"
        type="textarea"
        :rows="12"
        placeholder='[
  {
    "name": "示例中学",
    "type": "KEY",
    "city": "北京",
    "district": "海淀区",
    "admissionScoreYear1": 580,
    "enrollmentQuota": 500
  }
]'
      />
      <template #footer>
        <el-button @click="importDialogVisible = false">取消</el-button>
        <el-button type="success" :loading="importing" @click="handleImport">确认导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSchoolList, getCities, getDistricts, createSchool, updateSchool, deleteSchool, importSchools, exportSchools } from '@/api/school'
import { Plus, Upload, Download } from '@element-plus/icons-vue'

const isAdmin = computed(() => localStorage.getItem('userRole') === 'ADMIN')
const loading = ref(false)
const schools = ref<any[]>([])
const total = ref(0)
const cities = ref<string[]>([])
const districts = ref<string[]>([])

const searchForm = reactive({
  keyword: '',
  city: '',
  district: '',
  type: '',
  page: 1,
  size: 20
})

const dialogVisible = ref(false)
const isEditing = ref(false)
const editingId = ref<number | null>(null)
const submitting = ref(false)
const schoolFormRef = ref()

const schoolForm = reactive({
  name: '',
  type: '',
  city: '',
  district: '',
  admissionScoreYear1: undefined as number | undefined,
  admissionScoreYear2: undefined as number | undefined,
  admissionScoreYear3: undefined as number | undefined,
  enrollmentQuota: undefined as number | undefined,
  phone: '',
  address: '',
  description: '',
  features: ''
})

const schoolRules = {
  name: [{ required: true, message: '请输入学校名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择学校类型', trigger: 'change' }]
}

const importDialogVisible = ref(false)
const importJson = ref('')
const importing = ref(false)
const exporting = ref(false)

const handleExport = async () => {
  exporting.value = true
  try {
    const res = await exportSchools()
    const schools = res.data || []
    const jsonStr = JSON.stringify(schools, null, 2)
    const blob = new Blob([jsonStr], { type: 'application/json' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `schools_export_${new Date().toISOString().slice(0, 10)}.json`
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(url)
    ElMessage.success(`成功导出 ${schools.length} 所学校数据`)
  } catch (e: any) {
    ElMessage.error(e.message || '导出失败')
  } finally {
    exporting.value = false
  }
}

const resetSchoolForm = () => {
  Object.assign(schoolForm, {
    name: '', type: '', city: '', district: '',
    admissionScoreYear1: undefined, admissionScoreYear2: undefined, admissionScoreYear3: undefined,
    enrollmentQuota: undefined, phone: '', address: '', description: '', features: ''
  })
}

const showCreateDialog = () => {
  resetSchoolForm()
  isEditing.value = false
  editingId.value = null
  dialogVisible.value = true
}

const showEditDialog = (row: any) => {
  isEditing.value = true
  editingId.value = row.id
  Object.assign(schoolForm, {
    name: row.name || '',
    type: row.type || '',
    city: row.city || '',
    district: row.district || '',
    admissionScoreYear1: row.admission_score_year1 || row.admissionScoreYear1,
    admissionScoreYear2: row.admission_score_year2 || row.admissionScoreYear2,
    admissionScoreYear3: row.admission_score_year3 || row.admissionScoreYear3,
    enrollmentQuota: row.enrollment_quota || row.enrollmentQuota,
    phone: row.phone || '',
    address: row.address || '',
    description: row.description || '',
    features: row.features || ''
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!schoolFormRef.value) return
  await schoolFormRef.value.validate()
  submitting.value = true
  try {
    const data = { ...schoolForm }
    if (isEditing.value && editingId.value) {
      await updateSchool(editingId.value, data)
      ElMessage.success('学校信息更新成功')
    } else {
      await createSchool(data)
      ElMessage.success('学校创建成功')
    }
    dialogVisible.value = false
    loadSchools()
  } catch (e: any) {
    ElMessage.error(e.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(`确定要删除学校"${row.name}"吗？此操作不可恢复。`, '删除确认', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteSchool(row.id)
    ElMessage.success('学校删除成功')
    loadSchools()
  } catch (e: any) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '删除失败')
    }
  }
}

const showImportDialog = () => {
  importJson.value = ''
  importDialogVisible.value = true
}

const handleImport = async () => {
  if (!importJson.value.trim()) {
    ElMessage.warning('请输入要导入的数据')
    return
  }
  let data: any[]
  try {
    data = JSON.parse(importJson.value)
    if (!Array.isArray(data)) {
      ElMessage.error('数据格式错误：必须是 JSON 数组')
      return
    }
  } catch {
    ElMessage.error('JSON 格式不正确，请检查后重试')
    return
  }
  importing.value = true
  try {
    await importSchools(data)
    ElMessage.success(`成功导入 ${data.length} 所学校`)
    importDialogVisible.value = false
    loadSchools()
  } catch (e: any) {
    ElMessage.error(e.message || '导入失败')
  } finally {
    importing.value = false
  }
}

const loadSchools = async () => {
  loading.value = true
  try {
    const params = { ...searchForm, page: searchForm.page - 1 }
    const res = await getSchoolList(params)
    schools.value = res.data?.schools || res.data?.content || []
    total.value = res.data?.totalElements || res.data?.total || 0
  } catch (e: any) {
    schools.value = []
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  searchForm.page = 1
  loadSchools()
}

const resetSearch = () => {
  Object.assign(searchForm, { keyword: '', city: '', district: '', type: '', page: 1, size: 20 })
  districts.value = []
  loadSchools()
}

const handleCityChange = async (city: string) => {
  searchForm.district = ''
  if (city) {
    try {
      const res = await getDistricts(city)
      districts.value = res.data || []
    } catch (e) {
      districts.value = []
    }
  } else {
    districts.value = []
  }
}

onMounted(async () => {
  loadSchools()
  try {
    const res = await getCities()
    cities.value = res.data || []
  } catch (e) { /* ignore */ }
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.admin-actions {
  display: flex;
  gap: 8px;
}

.search-form {
  margin-bottom: 20px;
}
</style>
