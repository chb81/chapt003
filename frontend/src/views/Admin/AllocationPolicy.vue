<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header"><h2>分配生政策管理</h2><el-button type="primary" @click="showCreateDialog">新增政策</el-button></div>
      </template>
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="年度"><el-input v-model.number="queryParams.year" placeholder="如2025" clearable /></el-form-item>
        <el-form-item label="城市"><el-input v-model="queryParams.city" placeholder="城市" clearable /></el-form-item>
        <el-form-item label="区县"><el-input v-model="queryParams.district" placeholder="区县" clearable /></el-form-item>
        <el-form-item><el-button type="primary" @click="handleSearch">搜索</el-button><el-button @click="handleReset">重置</el-button></el-form-item>
      </el-form>
      <el-table v-loading="loading" :data="dataList" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="policyName" label="政策名称" width="200" />
        <el-table-column prop="policyType" label="类型" width="80" />
        <el-table-column prop="city" label="城市" width="80" />
        <el-table-column prop="district" label="区县" width="80" />
        <el-table-column prop="year" label="年度" width="70" />
        <el-table-column prop="totalQuotaPercentage" label="分配比例(%)" width="110" />
        <el-table-column prop="minScoreGap" label="最低分差" width="90" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }"><el-button link type="primary" @click="handleEdit(row)">编辑</el-button><el-button link type="danger" @click="handleDelete(row)">删除</el-button></template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="queryParams.page" :page-size="queryParams.size" :total="total" layout="total, prev, pager, next" @current-change="loadData" style="margin-top: 16px; justify-content: flex-end" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑政策' : '新增政策'" width="500px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="城市" required><el-input v-model="formData.city" /></el-form-item>
        <el-form-item label="区县" required><el-input v-model="formData.district" /></el-form-item>
        <el-form-item label="年度" required><el-input v-model.number="formData.year" /></el-form-item>
        <el-form-item label="政策名称" required><el-input v-model="formData.policyName" /></el-form-item>
        <el-form-item label="政策类型" required><el-select v-model="formData.policyType"><el-option label="分配生" value="分配生" /><el-option label="指标生" value="指标生" /><el-option label="特长生" value="特长生" /></el-select></el-form-item>
        <el-form-item label="分配比例"><el-input v-model="formData.totalQuotaPercentage" /></el-form-item>
        <el-form-item label="最低分差"><el-input v-model="formData.minScoreGap" /></el-form-item>
        <el-form-item label="说明"><el-input v-model="formData.description" type="textarea" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getAllocationPolicies, createAllocationPolicy, updateAllocationPolicy, deleteAllocationPolicy } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'
const loading = ref(false), dataList = ref([]), total = ref(0), dialogVisible = ref(false), editingId = ref<number | null>(null)
const queryParams = ref({ page: 0, size: 20, year: null as number | null, city: '', district: '' })
const formData = ref({ city: '', district: '', year: null as number | null, policyName: '', policyType: '分配生', totalQuotaPercentage: '', minScoreGap: '', eligibleConditions: '', description: '' })
const loadData = async () => { loading.value = true; try { const { data: res } = await getAllocationPolicies(queryParams.value); const d = res.data?.content || res.data || {}; dataList.value = d.content || []; total.value = d.totalElements || 0 } finally { loading.value = false } }
const handleSearch = () => { queryParams.value.page = 0; loadData() }
const handleReset = () => { queryParams.value = { page: 0, size: 20, year: null, city: '', district: '' }; loadData() }
const showCreateDialog = () => { editingId.value = null; formData.value = { city: '', district: '', year: null, policyName: '', policyType: '分配生', totalQuotaPercentage: '', minScoreGap: '', eligibleConditions: '', description: '' }; dialogVisible.value = true }
const handleEdit = (row: any) => { editingId.value = row.id; formData.value = { ...row, totalQuotaPercentage: row.totalQuotaPercentage ?? '', minScoreGap: row.minScoreGap ?? '', eligibleConditions: row.eligibleConditions ?? '', description: row.description ?? '' }; dialogVisible.value = true }
const handleSave = async () => { try { const d = { ...formData.value, year: Number(formData.value.year), isActive: true }; if (editingId.value) await updateAllocationPolicy(editingId.value, d); else await createAllocationPolicy(d); ElMessage.success('保存成功'); dialogVisible.value = false; loadData() } catch { ElMessage.error('保存失败') } }
const handleDelete = async (row: any) => { await ElMessageBox.confirm('确定删除？', '确认'); await deleteAllocationPolicy(row.id); ElMessage.success('删除成功'); loadData() }
onMounted(loadData)
</script>
