<template>
  <div class="had-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>历史录取数据</h2>
          <div>
            <el-button type="success" @click="showImportDialog">批量导入</el-button>
            <el-button type="primary" @click="showCreateDialog">新增记录</el-button>
          </div>
        </div>
      </template>

      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="年份">
          <el-input v-model.number="queryParams.year" placeholder="如 2025" clearable />
        </el-form-item>
        <el-form-item label="城市">
          <el-input v-model="queryParams.city" placeholder="城市" clearable />
        </el-form-item>
        <el-form-item label="学校类型">
          <el-select v-model="queryParams.schoolType" placeholder="全部" clearable>
            <el-option label="重点高中" value="KEY_HIGH_SCHOOL" />
            <el-option label="普通高中" value="REGULAR_HIGH_SCHOOL" />
            <el-option label="职业高中" value="VOCATIONAL_HIGH_SCHOOL" />
          </el-select>
        </el-form-item>
        <el-form-item label="搜索">
          <el-input v-model="queryParams.keyword" placeholder="学校名称" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="dataList" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="schoolName" label="学校名称" width="150" />
        <el-table-column prop="year" label="年份" width="80" />
        <el-table-column prop="admissionScore" label="录取分" width="90" />
        <el-table-column prop="lowestScore" label="最低分" width="90" />
        <el-table-column prop="highestScore" label="最高分" width="90" />
        <el-table-column prop="averageScore" label="平均分" width="90" />
        <el-table-column prop="enrollmentQuota" label="招生计划" width="90" />
        <el-table-column prop="applicantCount" label="报名人数" width="90" />
        <el-table-column prop="admissionRate" label="录取率(%)" width="100" />
        <el-table-column prop="city" label="城市" width="80" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="showEditDialog(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="queryParams.size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
      />
    </el-card>

    <el-dialog v-model="formDialogVisible" :title="isEdit ? '编辑录取数据' : '新增录取数据'" width="600px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="学校ID" required>
          <el-input v-model.number="formData.schoolId" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="学校名称" required>
          <el-input v-model="formData.schoolName" />
        </el-form-item>
        <el-form-item label="年份" required>
          <el-input v-model.number="formData.year" />
        </el-form-item>
        <el-form-item label="录取分数线">
          <el-input v-model.number="formData.admissionScore" />
        </el-form-item>
        <el-form-item label="最低分">
          <el-input v-model.number="formData.lowestScore" />
        </el-form-item>
        <el-form-item label="最高分">
          <el-input v-model.number="formData.highestScore" />
        </el-form-item>
        <el-form-item label="平均分">
          <el-input v-model.number="formData.averageScore" />
        </el-form-item>
        <el-form-item label="招生计划">
          <el-input v-model.number="formData.enrollmentQuota" />
        </el-form-item>
        <el-form-item label="报名人数">
          <el-input v-model.number="formData.applicantCount" />
        </el-form-item>
        <el-form-item label="录取率(%)">
          <el-input v-model.number="formData.admissionRate" />
        </el-form-item>
        <el-form-item label="城市">
          <el-input v-model="formData.city" />
        </el-form-item>
        <el-form-item label="区县">
          <el-input v-model="formData.district" />
        </el-form-item>
        <el-form-item label="学校类型">
          <el-select v-model="formData.schoolType" placeholder="请选择">
            <el-option label="重点高中" value="KEY_HIGH_SCHOOL" />
            <el-option label="普通高中" value="REGULAR_HIGH_SCHOOL" />
            <el-option label="职业高中" value="VOCATIONAL_HIGH_SCHOOL" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="importDialogVisible" title="批量导入录取数据" width="600px">
      <el-input
        v-model="importJson"
        type="textarea"
        :rows="12"
        placeholder='请粘贴JSON数组，如：[{"schoolId":1,"schoolName":"XX中学","year":2025,"admissionScore":580}]'
      />
      <template #footer>
        <el-button @click="importDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleImport" :loading="importing">导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getHistoricalAdmissionData, createHistoricalData,
  updateHistoricalData, deleteHistoricalData, importHistoricalData
} from '@/api/admin'

const loading = ref(false)
const dataList = ref<any[]>([])
const total = ref(0)
const currentPage = ref(1)
const formDialogVisible = ref(false)
const importDialogVisible = ref(false)
const importing = ref(false)
const isEdit = ref(false)
const editId = ref<number>(0)
const importJson = ref('')

const queryParams = reactive({
  year: undefined as number | undefined,
  city: '',
  schoolType: '',
  keyword: '',
  size: 20
})

const formData = reactive({
  schoolId: undefined as number | undefined,
  schoolName: '',
  year: undefined as number | undefined,
  admissionScore: undefined as number | undefined,
  lowestScore: undefined as number | undefined,
  highestScore: undefined as number | undefined,
  averageScore: undefined as number | undefined,
  enrollmentQuota: undefined as number | undefined,
  applicantCount: undefined as number | undefined,
  admissionRate: undefined as number | undefined,
  city: '',
  district: '',
  schoolType: ''
})

const loadData = async () => {
  loading.value = true
  try {
    const params: any = { page: currentPage.value - 1, size: queryParams.size }
    if (queryParams.year) params.year = queryParams.year
    if (queryParams.city) params.city = queryParams.city
    if (queryParams.schoolType) params.schoolType = queryParams.schoolType
    if (queryParams.keyword) params.keyword = queryParams.keyword

    const res = await getHistoricalAdmissionData(params)
    const data = res.data || res
    dataList.value = data.content || []
    total.value = data.totalElements || 0
  } catch {
    ElMessage.error('加载数据失败')
    dataList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { currentPage.value = 1; loadData() }
const handleReset = () => {
  queryParams.year = undefined
  queryParams.city = ''
  queryParams.schoolType = ''
  queryParams.keyword = ''
  currentPage.value = 1
  loadData()
}

const resetForm = () => {
  Object.assign(formData, {
    schoolId: undefined, schoolName: '', year: undefined,
    admissionScore: undefined, lowestScore: undefined, highestScore: undefined,
    averageScore: undefined, enrollmentQuota: undefined, applicantCount: undefined,
    admissionRate: undefined, city: '', district: '', schoolType: ''
  })
}

const showCreateDialog = () => {
  resetForm()
  isEdit.value = false
  formDialogVisible.value = true
}

const showEditDialog = (row: any) => {
  Object.assign(formData, {
    schoolId: row.schoolId, schoolName: row.schoolName, year: row.year,
    admissionScore: row.admissionScore, lowestScore: row.lowestScore,
    highestScore: row.highestScore, averageScore: row.averageScore,
    enrollmentQuota: row.enrollmentQuota, applicantCount: row.applicantCount,
    admissionRate: row.admissionRate, city: row.city, district: row.district,
    schoolType: row.schoolType
  })
  editId.value = row.id
  isEdit.value = true
  formDialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    if (isEdit.value) {
      await updateHistoricalData(editId.value, formData)
      ElMessage.success('更新成功')
    } else {
      await createHistoricalData(formData)
      ElMessage.success('创建成功')
    }
    formDialogVisible.value = false
    loadData()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(`确定删除 ${row.schoolName} ${row.year}年 的数据？`, '确认删除', { type: 'warning' })
    await deleteHistoricalData(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error: any) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

const showImportDialog = () => {
  importJson.value = ''
  importDialogVisible.value = true
}

const handleImport = async () => {
  if (!importJson.value.trim()) {
    ElMessage.warning('请输入JSON数据')
    return
  }
  try {
    const data = JSON.parse(importJson.value)
    if (!Array.isArray(data)) {
      ElMessage.warning('请输入JSON数组')
      return
    }
    importing.value = true
    const res = await importHistoricalData(data)
    const result = res.data || res
    ElMessage.success(`导入成功，共 ${result.data || data.length} 条`)
    importDialogVisible.value = false
    loadData()
  } catch (e: any) {
    if (e instanceof SyntaxError) {
      ElMessage.error('JSON格式错误，请检查')
    } else {
      ElMessage.error(e.message || '导入失败')
    }
  } finally {
    importing.value = false
  }
}

onMounted(() => { loadData() })
</script>

<style scoped>
.had-container { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-header h2 { margin: 0; font-size: 20px; font-weight: 600; }
.search-form { margin-bottom: 20px; }
.el-pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
