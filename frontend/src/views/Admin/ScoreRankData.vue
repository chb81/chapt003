<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header"><h2>分数位次数据管理</h2><div><el-button type="success" @click="showImportDialog">批量导入</el-button><el-button type="primary" @click="showCreateDialog">新增数据</el-button></div></div>
      </template>
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="城市"><el-input v-model="queryParams.city" clearable /></el-form-item>
        <el-form-item label="年度"><el-input v-model.number="queryParams.year" clearable /></el-form-item>
        <el-form-item><el-button type="primary" @click="handleSearch">搜索</el-button><el-button @click="handleReset">重置</el-button></el-form-item>
      </el-form>
      <el-table v-loading="loading" :data="dataList" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="city" label="城市" width="80" />
        <el-table-column prop="year" label="年度" width="70" />
        <el-table-column prop="totalScore" label="总分" width="80" />
        <el-table-column prop="cityRank" label="全市排名" width="100" />
        <el-table-column prop="district" label="区县" width="80" />
        <el-table-column prop="districtRank" label="区内排名" width="100" />
        <el-table-column prop="studentCount" label="同分人数" width="90" />
        <el-table-column prop="cumulativeCount" label="累计人数" width="90" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }"><el-button link type="primary" @click="handleEdit(row)">编辑</el-button><el-button link type="danger" @click="handleDelete(row)">删除</el-button></template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="queryParams.page" :page-size="queryParams.size" :total="total" layout="total, prev, pager, next" @current-change="loadData" style="margin-top: 16px; justify-content: flex-end" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑数据' : '新增数据'" width="500px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="城市" required><el-input v-model="formData.city" /></el-form-item>
        <el-form-item label="年度" required><el-input v-model.number="formData.year" /></el-form-item>
        <el-form-item label="总分" required><el-input v-model="formData.totalScore" /></el-form-item>
        <el-form-item label="全市排名" required><el-input v-model.number="formData.cityRank" /></el-form-item>
        <el-form-item label="区县"><el-input v-model="formData.district" /></el-form-item>
        <el-form-item label="区内排名"><el-input v-model.number="formData.districtRank" /></el-form-item>
        <el-form-item label="同分人数"><el-input v-model.number="formData.studentCount" /></el-form-item>
        <el-form-item label="累计人数"><el-input v-model.number="formData.cumulativeCount" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getScoreRanks, createScoreRank, updateScoreRank, deleteScoreRank } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'
const loading = ref(false), dataList = ref([]), total = ref(0), dialogVisible = ref(false), editingId = ref<number | null>(null)
const queryParams = ref({ page: 0, size: 20, city: '', year: null as number | null })
const formData = ref({ city: '', year: null as number | null, totalScore: '', cityRank: null as number | null, district: '', districtRank: null as number | null, studentCount: null as number | null, cumulativeCount: null as number | null })
const loadData = async () => { loading.value = true; try { const { data: res } = await getScoreRanks(queryParams.value); const d = res.data?.content || res.data || {}; dataList.value = d.content || []; total.value = d.totalElements || 0 } finally { loading.value = false } }
const handleSearch = () => { queryParams.value.page = 0; loadData() }
const handleReset = () => { queryParams.value = { page: 0, size: 20, city: '', year: null }; loadData() }
const showCreateDialog = () => { editingId.value = null; formData.value = { city: '', year: null, totalScore: '', cityRank: null, district: '', districtRank: null, studentCount: null, cumulativeCount: null }; dialogVisible.value = true }
const handleEdit = (row: any) => { editingId.value = row.id; formData.value = { ...row }; dialogVisible.value = true }
const handleSave = async () => { try { const d = { ...formData.value, year: Number(formData.value.year), totalScore: Number(formData.value.totalScore) }; if (editingId.value) await updateScoreRank(editingId.value, d); else await createScoreRank(d); ElMessage.success('保存成功'); dialogVisible.value = false; loadData() } catch { ElMessage.error('保存失败') } }
const handleDelete = async (row: any) => { await ElMessageBox.confirm('确定删除？', '确认'); await deleteScoreRank(row.id); ElMessage.success('删除成功'); loadData() }
const showImportDialog = () => { ElMessage.info('请使用API批量导入') }
onMounted(loadData)
</script>
