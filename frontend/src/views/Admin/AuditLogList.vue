<template>
  <div class="audit-log-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>操作日志</h2>
        </div>
      </template>

      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="操作类型">
          <el-input v-model="queryParams.action" placeholder="如 UPDATE_ROLE" clearable />
        </el-form-item>
        <el-form-item label="操作人ID">
          <el-input v-model.number="queryParams.operatorId" placeholder="操作人ID" clearable />
        </el-form-item>
        <el-form-item label="用户ID">
          <el-input v-model.number="queryParams.userId" placeholder="目标用户ID" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="logList" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="目标用户" width="120" />
        <el-table-column prop="operatorName" label="操作人" width="120" />
        <el-table-column prop="action" label="操作类型" width="160" />
        <el-table-column prop="details" label="详情" min-width="300" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="操作时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="queryParams.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadLogs"
        @current-change="loadLogs"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAuditLogs } from '@/api/admin'

const loading = ref(false)
const logList = ref<any[]>([])
const total = ref(0)
const currentPage = ref(1)

const queryParams = reactive({
  action: '',
  operatorId: undefined as number | undefined,
  userId: undefined as number | undefined,
  size: 20
})

const loadLogs = async () => {
  loading.value = true
  try {
    const params: any = {
      page: currentPage.value - 1,
      size: queryParams.size
    }
    if (queryParams.action) params.action = queryParams.action
    if (queryParams.operatorId) params.operatorId = queryParams.operatorId
    if (queryParams.userId) params.userId = queryParams.userId

    const res = await getAuditLogs(params)
    const data = res.data || res
    logList.value = data.content || []
    total.value = data.totalElements || 0
  } catch (error: any) {
    ElMessage.error('加载操作日志失败')
    logList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadLogs()
}

const handleReset = () => {
  queryParams.action = ''
  queryParams.operatorId = undefined
  queryParams.userId = undefined
  currentPage.value = 1
  loadLogs()
}

const formatDate = (date: string) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

onMounted(() => {
  loadLogs()
})
</script>

<style scoped>
.audit-log-container { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-header h2 { margin: 0; font-size: 20px; font-weight: 600; }
.search-form { margin-bottom: 20px; }
.el-pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
