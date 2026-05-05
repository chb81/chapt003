<template>
  <div class="page-container">
    <el-card>
      <template #header><div class="card-header"><h2>通知管理</h2><el-button type="primary" @click="showSendDialog">发送全员通知</el-button></div></template>
      <el-table v-loading="loading" :data="dataList" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="type" label="类型" width="100" />
        <el-table-column prop="content" label="内容" min-width="300" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="创建时间" width="160" />
      </el-table>
      <el-pagination v-model:current-page="queryParams.page" :page-size="queryParams.size" :total="total" layout="total, prev, pager, next" @current-change="loadData" style="margin-top: 16px" />
    </el-card>
    <el-dialog v-model="sendDialogVisible" title="发送通知" width="500px">
      <el-form :model="formData" label-width="80px">
        <el-form-item label="标题" required><el-input v-model="formData.title" /></el-form-item>
        <el-form-item label="类型"><el-select v-model="formData.type"><el-option label="系统" value="SYSTEM" /><el-option label="提醒" value="REMINDER" /><el-option label="推广" value="PROMOTION" /><el-option label="警告" value="WARNING" /></el-select></el-form-item>
        <el-form-item label="内容" required><el-input v-model="formData.content" type="textarea" :rows="4" /></el-form-item>
        <el-form-item label="链接"><el-input v-model="formData.link" placeholder="可选" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="sendDialogVisible = false">取消</el-button><el-button type="primary" @click="handleSendAll">发送</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getAdminNotifications, sendNotificationToAll } from '@/api/admin'
import { ElMessage } from 'element-plus'
const loading = ref(false), dataList = ref([]), total = ref(0), sendDialogVisible = ref(false)
const queryParams = ref({ page: 0, size: 20 })
const formData = ref({ title: '', type: 'SYSTEM', content: '', link: '' })
const loadData = async () => { loading.value = true; try { const { data: res } = await getAdminNotifications(queryParams.value); const d = res.data?.content || res.data || {}; dataList.value = d.content || []; total.value = d.totalElements || 0 } finally { loading.value = false } }
const showSendDialog = () => { formData.value = { title: '', type: 'SYSTEM', content: '', link: '' }; sendDialogVisible.value = true }
const handleSendAll = async () => { try { await sendNotificationToAll(formData.value); ElMessage.success('发送成功'); sendDialogVisible.value = false; loadData() } catch { ElMessage.error('发送失败') } }
onMounted(loadData)
</script>
