<template>
  <div class="page-container">
    <el-card>
      <template #header><div class="card-header"><h2>公告管理</h2><el-button type="primary" @click="showCreateDialog">新增公告</el-button></div></template>
      <el-form :inline="true" :model="queryParams"><el-form-item label="类型"><el-select v-model="queryParams.type" clearable><el-option label="重要" value="IMPORTANT" /><el-option label="普通" value="NORMAL" /></el-select></el-form-item><el-form-item><el-button type="primary" @click="handleSearch">搜索</el-button></el-form-item></el-form>
      <el-table v-loading="loading" :data="dataList" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="type" label="类型" width="80"><template #default="{ row }"><el-tag :type="row.type === 'IMPORTANT' ? 'danger' : 'info'">{{ row.type === 'IMPORTANT' ? '重要' : '普通' }}</el-tag></template></el-table-column>
        <el-table-column prop="publisher" label="发布者" width="100" />
        <el-table-column prop="priority" label="优先级" width="80" />
        <el-table-column prop="readCount" label="已读" width="70" />
        <el-table-column prop="publishedAt" label="发布时间" width="160" />
        <el-table-column label="操作" width="150" fixed="right"><template #default="{ row }"><el-button link type="primary" @click="handleEdit(row)">编辑</el-button><el-button link type="danger" @click="handleDelete(row)">删除</el-button></template></el-table-column>
      </el-table>
      <el-pagination v-model:current-page="queryParams.page" :page-size="queryParams.size" :total="total" layout="total, prev, pager, next" @current-change="loadData" style="margin-top: 16px" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑公告' : '新增公告'" width="600px">
      <el-form :model="formData" label-width="80px">
        <el-form-item label="标题" required><el-input v-model="formData.title" /></el-form-item>
        <el-form-item label="类型" required><el-select v-model="formData.type"><el-option label="重要" value="IMPORTANT" /><el-option label="普通" value="NORMAL" /></el-select></el-form-item>
        <el-form-item label="发布者"><el-input v-model="formData.publisher" /></el-form-item>
        <el-form-item label="优先级"><el-input-number v-model="formData.priority" :min="0" :max="10" /></el-form-item>
        <el-form-item label="内容" required><el-input v-model="formData.content" type="textarea" :rows="5" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getAdminAnnouncements, createAnnouncement, updateAnnouncement, deleteAnnouncement } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'
const loading = ref(false), dataList = ref([]), total = ref(0), dialogVisible = ref(false), editingId = ref<number | null>(null)
const queryParams = ref({ page: 0, size: 20, type: '' })
const formData = ref({ title: '', type: 'NORMAL', content: '', publisher: '', priority: 0 })
const loadData = async () => { loading.value = true; try { const { data: res } = await getAdminAnnouncements(queryParams.value); const d = res.data?.content || res.data || {}; dataList.value = d.content || []; total.value = d.totalElements || 0 } finally { loading.value = false } }
const handleSearch = () => { queryParams.value.page = 0; loadData() }
const showCreateDialog = () => { editingId.value = null; formData.value = { title: '', type: 'NORMAL', content: '', publisher: '', priority: 0 }; dialogVisible.value = true }
const handleEdit = (row: any) => { editingId.value = row.id; formData.value = { title: row.title, type: row.type, content: row.content, publisher: row.publisher || '', priority: row.priority || 0 }; dialogVisible.value = true }
const handleSave = async () => { try { if (editingId.value) await updateAnnouncement(editingId.value, formData.value); else await createAnnouncement(formData.value); ElMessage.success('保存成功'); dialogVisible.value = false; loadData() } catch { ElMessage.error('保存失败') } }
const handleDelete = async (row: any) => { await ElMessageBox.confirm('确定删除？', '确认'); await deleteAnnouncement(row.id); ElMessage.success('删除成功'); loadData() }
onMounted(loadData)
</script>
