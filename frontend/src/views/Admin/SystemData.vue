<template>
  <div class="system-data-container">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <h2>数据导出</h2>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-card shadow="hover" class="export-card" @click="handleExport('schools')">
                <el-icon :size="40" color="#409EFF"><OfficeBuilding /></el-icon>
                <div class="export-title">学校数据</div>
                <div class="export-desc">导出所有学校基本信息</div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card shadow="hover" class="export-card" @click="handleExport('historical-admission')">
                <el-icon :size="40" color="#67C23A"><TrendCharts /></el-icon>
                <div class="export-title">历史录取数据</div>
                <div class="export-desc">导出历史录取分数数据</div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card shadow="hover" class="export-card" @click="handleExport('audit-logs')">
                <el-icon :size="40" color="#E6A23C"><Document /></el-icon>
                <div class="export-title">操作日志</div>
                <div class="export-desc">导出系统操作日志</div>
              </el-card>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <h2>数据备份与恢复</h2>
          <el-button type="primary" @click="handleCreateBackup" :loading="backupLoading">创建备份</el-button>
        </div>
      </template>

      <el-table v-loading="backupsLoading" :data="backupList" stripe style="width: 100%">
        <el-table-column prop="filename" label="备份文件" min-width="250" />
        <el-table-column prop="size" label="大小" width="120">
          <template #default="{ row }">{{ formatSize(row.size) }}</template>
        </el-table-column>
        <el-table-column prop="lastModified" label="备份时间" width="180" />
        <el-table-column label="操作" width="280">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleDownloadBackup(row.filename)">下载</el-button>
            <el-button type="warning" link @click="handleRestore(row.filename, true)">验证</el-button>
            <el-button type="success" link @click="handleRestore(row.filename, false)">恢复</el-button>
            <el-button type="danger" link @click="handleDeleteBackup(row.filename)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { OfficeBuilding, TrendCharts, Document } from '@element-plus/icons-vue'
import {
  exportSystemData, createSystemBackup, getSystemBackups,
  deleteSystemBackup, downloadSystemBackup, restoreSystemBackup
} from '@/api/admin'

const backupLoading = ref(false)
const backupsLoading = ref(false)
const backupList = ref<any[]>([])

const handleExport = async (dataType: string) => {
  try {
    const res = await exportSystemData(dataType)
    const blob = new Blob([res as any], { type: 'application/json' })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `${dataType}_${new Date().toISOString().slice(0, 10)}.json`
    link.click()
    URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch {
    ElMessage.error('导出失败')
  }
}

const handleCreateBackup = async () => {
  backupLoading.value = true
  try {
    const res = await createSystemBackup()
    const result = res.data || res
    ElMessage.success(`备份创建成功: ${result.data?.filename || ''}`)
    loadBackups()
  } catch {
    ElMessage.error('备份创建失败')
  } finally {
    backupLoading.value = false
  }
}

const loadBackups = async () => {
  backupsLoading.value = true
  try {
    const res = await getSystemBackups()
    backupList.value = (res.data || res) || []
  } catch {
    backupList.value = []
  } finally {
    backupsLoading.value = false
  }
}

const handleDownloadBackup = async (filename: string) => {
  try {
    const res = await downloadSystemBackup(filename)
    const blob = new Blob([res as any], { type: 'application/zip' })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = filename
    link.click()
    URL.revokeObjectURL(url)
  } catch {
    ElMessage.error('下载失败')
  }
}

const handleRestore = async (filename: string, dryRun: boolean) => {
  const action = dryRun ? '验证' : '恢复'
  try {
    const msg = dryRun
      ? `确定要验证备份文件 "${filename}" 吗？（试运行模式，不会修改数据）`
      : `确定要从备份 "${filename}" 恢复数据吗？此操作将覆盖现有数据！`
    await ElMessageBox.confirm(msg, `确认${action}`, { type: dryRun ? 'info' : 'warning' })
    const res = await restoreSystemBackup(filename, dryRun)
    const result = res.data || res
    ElMessage.success(result.message || `${action}完成`)
  } catch (error: any) {
    if (error !== 'cancel') ElMessage.error(`${action}失败`)
  }
}

const handleDeleteBackup = async (filename: string) => {
  try {
    await ElMessageBox.confirm(`确定删除备份 "${filename}"？`, '确认删除', { type: 'warning' })
    await deleteSystemBackup(filename)
    ElMessage.success('删除成功')
    loadBackups()
  } catch (error: any) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

const formatSize = (bytes: number) => {
  if (!bytes) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return (bytes / Math.pow(k, i)).toFixed(2) + ' ' + sizes[i]
}

onMounted(() => { loadBackups() })
</script>

<style scoped>
.system-data-container { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-header h2 { margin: 0; font-size: 20px; font-weight: 600; }
.export-card {
  text-align: center; padding: 20px; cursor: pointer;
  transition: transform 0.2s;
}
.export-card:hover { transform: translateY(-4px); }
.export-title { font-size: 16px; font-weight: 600; margin-top: 10px; }
.export-desc { font-size: 12px; color: #999; margin-top: 5px; }
</style>
