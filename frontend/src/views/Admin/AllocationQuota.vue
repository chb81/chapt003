<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>分配生名额管理</h2>
          <div>
            <el-button type="success" @click="showImportDialog">批量导入</el-button>
            <el-button type="primary" @click="showCreateDialog">新增名额</el-button>
          </div>
        </div>
      </template>
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="年度"><el-input v-model.number="queryParams.year" placeholder="如2025" clearable /></el-form-item>
        <el-form-item label="学校ID"><el-input v-model.number="queryParams.schoolId" placeholder="学校ID" clearable /></el-form-item>
        <el-form-item label="生源初中"><el-input v-model="queryParams.sourceSchoolName" placeholder="初中名称" clearable @keyup.enter="handleSearch" /></el-form-item>
        <el-form-item><el-button type="primary" @click="handleSearch">搜索</el-button><el-button @click="handleReset">重置</el-button></el-form-item>
      </el-form>
      <el-table v-loading="loading" :data="dataList" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="schoolName" label="目标高中" width="150" />
        <el-table-column prop="sourceSchoolName" label="生源初中" width="150" />
        <el-table-column prop="year" label="年度" width="70" />
        <el-table-column prop="quotaCount" label="名额" width="70" />
        <el-table-column prop="admissionScore" label="分配生线" width="100" />
        <el-table-column prop="unifiedScore" label="统招线" width="100" />
        <el-table-column prop="scoreDifference" label="分差" width="80" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="queryParams.page" :page-size="queryParams.size" :total="total" layout="total, prev, pager, next" @current-change="loadData" style="margin-top: 16px; justify-content: flex-end" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑名额' : '新增名额'" width="500px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="高中ID" required><el-input v-model.number="formData.schoolId" /></el-form-item>
        <el-form-item label="生源初中" required><el-input v-model="formData.sourceSchoolName" /></el-form-item>
        <el-form-item label="城市"><el-input v-model="formData.sourceSchoolCity" /></el-form-item>
        <el-form-item label="区县"><el-input v-model="formData.sourceSchoolDistrict" /></el-form-item>
        <el-form-item label="年度" required><el-input v-model.number="formData.year" /></el-form-item>
        <el-form-item label="名额数"><el-input-number v-model="formData.quotaCount" :min="0" /></el-form-item>
        <el-form-item label="分配生线"><el-input v-model="formData.admissionScore" /></el-form-item>
        <el-form-item label="统招线"><el-input v-model="formData.unifiedScore" /></el-form-item>
        <el-form-item label="分差"><el-input v-model="formData.scoreDifference" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getAllocationQuotas, createAllocationQuota, updateAllocationQuota, deleteAllocationQuota, importAllocationQuotas } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'
const loading = ref(false), dataList = ref([]), total = ref(0), dialogVisible = ref(false), editingId = ref<number | null>(null)
const queryParams = ref({ page: 0, size: 20, year: null as number | null, schoolId: null as number | null, sourceSchoolName: '' })
const formData = ref({ schoolId: null as number | null, sourceSchoolName: '', sourceSchoolCity: '', sourceSchoolDistrict: '', year: null as number | null, quotaCount: 0, admissionScore: '', unifiedScore: '', scoreDifference: '', policyRule: '' })
const loadData = async () => { loading.value = true; try { const { data: res } = await getAllocationQuotas(queryParams.value); const d = res.data?.content || res.data || {}; dataList.value = d.content || []; total.value = d.totalElements || 0 } finally { loading.value = false } }
const handleSearch = () => { queryParams.value.page = 0; loadData() }
const handleReset = () => { queryParams.value = { page: 0, size: 20, year: null, schoolId: null, sourceSchoolName: '' }; loadData() }
const showCreateDialog = () => { editingId.value = null; formData.value = { schoolId: null, sourceSchoolName: '', sourceSchoolCity: '', sourceSchoolDistrict: '', year: null, quotaCount: 0, admissionScore: '', unifiedScore: '', scoreDifference: '', policyRule: '' }; dialogVisible.value = true }
const handleEdit = (row: any) => { editingId.value = row.id; formData.value = { ...row, admissionScore: row.admissionScore ?? '', unifiedScore: row.unifiedScore ?? '', scoreDifference: row.scoreDifference ?? '', policyRule: row.policyRule ?? '' }; dialogVisible.value = true }
const handleSave = async () => { try { const d: any = { ...formData.value, schoolId: Number(formData.value.schoolId), year: Number(formData.value.year), quotaCount: Number(formData.value.quotaCount) || 0 }; if (editingId.value) await updateAllocationQuota(editingId.value, d); else await createAllocationQuota(d); ElMessage.success('保存成功'); dialogVisible.value = false; loadData() } catch { ElMessage.error('保存失败') } }
const handleDelete = async (row: any) => { await ElMessageBox.confirm('确定删除？', '确认'); await deleteAllocationQuota(row.id); ElMessage.success('删除成功'); loadData() }
const showImportDialog = () => { ElMessage.info('请使用API批量导入') }
onMounted(loadData)
</script>
