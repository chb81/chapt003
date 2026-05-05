<template>
  <div class="page-container">
    <el-card>
      <template #header><div class="card-header"><h2>帮助文档管理</h2><el-button type="primary" @click="showCreateDialog">新增文档</el-button></div></template>
      <el-form :inline="true" :model="queryParams"><el-form-item label="分类"><el-select v-model="queryParams.category" clearable><el-option label="指南" value="GUIDE" /><el-option label="FAQ" value="FAQ" /><el-option label="政策" value="POLICY" /><el-option label="教程" value="TUTORIAL" /></el-select></el-form-item><el-form-item label="状态"><el-select v-model="queryParams.published" clearable><el-option label="已发布" :value="true" /><el-option label="草稿" :value="false" /></el-select></el-form-item><el-form-item><el-button type="primary" @click="handleSearch">搜索</el-button></el-form-item></el-form>
      <el-table v-loading="loading" :data="dataList" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="category" label="分类" width="80" />
        <el-table-column prop="viewCount" label="浏览" width="70" />
        <el-table-column prop="helpfulCount" label="有用" width="70" />
        <el-table-column prop="published" label="状态" width="80"><template #default="{ row }"><el-tag :type="row.published ? 'success' : 'warning'">{{ row.published ? '已发布' : '草稿' }}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="200" fixed="right"><template #default="{ row }"><el-button link type="primary" @click="handleEdit(row)">编辑</el-button><el-button link :type="row.published ? 'warning' : 'success'" @click="handleTogglePublish(row)">{{ row.published ? '下架' : '发布' }}</el-button><el-button link type="danger" @click="handleDelete(row)">删除</el-button></template></el-table-column>
      </el-table>
      <el-pagination v-model:current-page="queryParams.page" :page-size="queryParams.size" :total="total" layout="total, prev, pager, next" @current-change="loadData" style="margin-top: 16px" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑文档' : '新增文档'" width="600px">
      <el-form :model="formData" label-width="80px">
        <el-form-item label="标题" required><el-input v-model="formData.title" /></el-form-item>
        <el-form-item label="分类" required><el-select v-model="formData.category"><el-option label="指南" value="GUIDE" /><el-option label="FAQ" value="FAQ" /><el-option label="政策" value="POLICY" /><el-option label="教程" value="TUTORIAL" /><el-option label="其他" value="OTHER" /></el-select></el-form-item>
        <el-form-item label="描述"><el-input v-model="formData.description" /></el-form-item>
        <el-form-item label="阅读时间"><el-input-number v-model="formData.readingTime" :min="1" /></el-form-item>
        <el-form-item label="内容" required><el-input v-model="formData.content" type="textarea" :rows="8" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getAdminHelpDocuments, createHelpDocument, updateHelpDocument, toggleHelpDocumentPublish, deleteHelpDocument } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'
const loading = ref(false), dataList = ref([]), total = ref(0), dialogVisible = ref(false), editingId = ref<number | null>(null)
const queryParams = ref({ page: 0, size: 20, category: '', published: null as boolean | null })
const formData = ref({ title: '', category: 'GUIDE', description: '', content: '', readingTime: 5, published: false })
const loadData = async () => { loading.value = true; try { const { data: res } = await getAdminHelpDocuments(queryParams.value); const d = res.data?.content || res.data || {}; dataList.value = d.content || []; total.value = d.totalElements || 0 } finally { loading.value = false } }
const handleSearch = () => { queryParams.value.page = 0; loadData() }
const showCreateDialog = () => { editingId.value = null; formData.value = { title: '', category: 'GUIDE', description: '', content: '', readingTime: 5, published: false }; dialogVisible.value = true }
const handleEdit = (row: any) => { editingId.value = row.id; formData.value = { title: row.title, category: row.category, description: row.description || '', content: row.content, readingTime: row.readingTime || 5, published: row.published }; dialogVisible.value = true }
const handleSave = async () => { try { if (editingId.value) await updateHelpDocument(editingId.value, formData.value); else await createHelpDocument(formData.value); ElMessage.success('保存成功'); dialogVisible.value = false; loadData() } catch { ElMessage.error('保存失败') } }
const handleTogglePublish = async (row: any) => { await toggleHelpDocumentPublish(row.id, !row.published); ElMessage.success(row.published ? '已下架' : '已发布'); loadData() }
const handleDelete = async (row: any) => { await ElMessageBox.confirm('确定删除？', '确认'); await deleteHelpDocument(row.id); ElMessage.success('删除成功'); loadData() }
onMounted(loadData)
</script>
