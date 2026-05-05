<template>
  <div class="page-container">
    <el-card>
      <template #header><div class="card-header"><h2>客服会话管理</h2></div></template>
      <el-form :inline="true" :model="queryParams"><el-form-item label="状态"><el-select v-model="queryParams.status" clearable><el-option label="进行中" value="ACTIVE" /><el-option label="已关闭" value="CLOSED" /><el-option label="已解决" value="RESOLVED" /></el-select></el-form-item><el-form-item><el-button type="primary" @click="handleSearch">搜索</el-button></el-form-item></el-form>
      <el-table v-loading="loading" :data="dataList" stripe @row-click="handleViewSession" style="cursor: pointer">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户" width="120" />
        <el-table-column prop="status" label="状态" width="100"><template #default="{ row }"><el-tag :type="row.status === 'ACTIVE' ? 'success' : row.status === 'RESOLVED' ? 'primary' : 'info'">{{ row.status }}</el-tag></template></el-table-column>
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="priority" label="优先级" width="80" />
        <el-table-column prop="startTime" label="开始时间" width="160" />
        <el-table-column prop="resolutionNote" label="备注" min-width="120" />
      </el-table>
      <el-pagination v-model:current-page="queryParams.page" :page-size="queryParams.size" :total="total" layout="total, prev, pager, next" @current-change="loadData" style="margin-top: 16px" />
    </el-card>
    <el-dialog v-model="detailVisible" title="会话详情" width="700px">
      <div v-if="currentSession" style="margin-bottom: 16px"><el-descriptions :column="3" border><el-descriptions-item label="用户">{{ currentSession.username }}</el-descriptions-item><el-descriptions-item label="状态">{{ currentSession.status }}</el-descriptions-item><el-descriptions-item label="分类">{{ currentSession.category || '-' }}</el-descriptions-item></el-descriptions></div>
      <div style="max-height: 400px; overflow-y: auto; margin-bottom: 16px">
        <div v-for="msg in messages" :key="msg.id" :style="{ textAlign: msg.messageType === 'AGENT_MESSAGE' ? 'right' : 'left', marginBottom: '8px' }">
          <el-tag :type="msg.messageType === 'AGENT_MESSAGE' ? 'primary' : 'success'" size="small">{{ msg.senderName || (msg.messageType === 'AGENT_MESSAGE' ? '客服' : '用户') }}</el-tag>
          <div style="display: inline-block; max-width: 70%; padding: 8px 12px; border-radius: 8px; background: #f5f5f5; text-align: left; margin: 4px 8px">{{ msg.content }}</div>
        </div>
      </div>
      <div v-if="currentSession?.status === 'ACTIVE'" style="display: flex; gap: 8px">
        <el-input v-model="replyContent" placeholder="输入回复" @keyup.enter="handleReply" />
        <el-button type="primary" @click="handleReply">回复</el-button>
        <el-button type="danger" @click="handleCloseSession">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getCustomerServiceSessions, getCustomerServiceSessionDetail, replyCustomerServiceMessage, closeCustomerServiceSession } from '@/api/admin'
import { ElMessage } from 'element-plus'
const loading = ref(false), dataList = ref([]), total = ref(0), detailVisible = ref(false), currentSession = ref<any>(null), messages = ref([]), replyContent = ref('')
const queryParams = ref({ page: 0, size: 20, status: '' })
const loadData = async () => { loading.value = true; try { const { data: res } = await getCustomerServiceSessions(queryParams.value); const d = res.data?.content || res.data || {}; dataList.value = d.content || []; total.value = d.totalElements || 0 } finally { loading.value = false } }
const handleSearch = () => { queryParams.value.page = 0; loadData() }
const handleViewSession = async (row: any) => { try { const { data: res } = await getCustomerServiceSessionDetail(row.id); const d = res.data || res; currentSession.value = d.session; messages.value = d.messages || []; detailVisible.value = true } catch { ElMessage.error('加载失败') } }
const handleReply = async () => { if (!replyContent.value) return; try { const adminId = Number(localStorage.getItem('userId')); await replyCustomerServiceMessage(currentSession.value.id, adminId, replyContent.value); replyContent.value = ''; handleViewSession({ id: currentSession.value.id }) } catch { ElMessage.error('回复失败') } }
const handleCloseSession = async () => { try { await closeCustomerServiceSession(currentSession.value.id); ElMessage.success('已关闭'); detailVisible.value = false; loadData() } catch { ElMessage.error('关闭失败') } }
onMounted(loadData)
</script>
