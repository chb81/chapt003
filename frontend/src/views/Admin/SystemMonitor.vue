<template>
  <div class="system-monitor-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>系统性能监控</h2>
          <el-button @click="loadData" :loading="loading">刷新</el-button>
        </div>
      </template>

      <el-row :gutter="20" v-if="perf">
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-value">{{ perf.totalUsers }}</div>
            <div class="stat-label">注册用户数</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-value">{{ perf.totalSchools }}</div>
            <div class="stat-label">学校数量</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-value">{{ perf.totalApplications }}</div>
            <div class="stat-label">志愿填报数</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-value">{{ perf.activeThreads }}</div>
            <div class="stat-label">活跃线程</div>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px" v-if="perf">
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header><span>内存使用</span></template>
            <el-descriptions :column="2" border v-if="perf.memory">
              <el-descriptions-item label="已用内存">{{ formatBytes(perf.memory.used) }}</el-descriptions-item>
              <el-descriptions-item label="空闲内存">{{ formatBytes(perf.memory.free) }}</el-descriptions-item>
              <el-descriptions-item label="总内存">{{ formatBytes(perf.memory.total) }}</el-descriptions-item>
              <el-descriptions-item label="使用率">
                <el-progress :percentage="Number(perf.memory.usagePercent.toFixed(1))" :color="getProgressColor(perf.memory.usagePercent)" />
              </el-descriptions-item>
            </el-descriptions>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header><span>JVM 信息</span></template>
            <el-descriptions :column="1" border v-if="perf.jvm">
              <el-descriptions-item label="Java版本">{{ perf.jvm.javaVersion }}</el-descriptions-item>
              <el-descriptions-item label="供应商">{{ perf.jvm.javaVendor }}</el-descriptions-item>
              <el-descriptions-item label="最大可用内存">{{ formatBytes(perf.jvm.maxMemory) }}</el-descriptions-item>
              <el-descriptions-item label="堆使用率">
                <el-progress :percentage="Number(perf.jvm.heapUsagePercent.toFixed(1))" :color="getProgressColor(perf.jvm.heapUsagePercent)" />
              </el-descriptions-item>
            </el-descriptions>
          </el-card>
        </el-col>
      </el-row>

      <el-card shadow="hover" style="margin-top: 20px" v-if="perf">
        <template #header><span>运行信息</span></template>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="启动时间">{{ perf.startTime }}</el-descriptions-item>
          <el-descriptions-item label="运行时长">{{ formatUptime(perf.uptime) }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <el-empty v-if="!perf && !loading" description="暂无监控数据" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getSystemPerformance } from '@/api/admin'

const loading = ref(false)
const perf = ref<any>(null)
let timer: any = null

const loadData = async () => {
  loading.value = true
  try {
    const res = await getSystemPerformance()
    perf.value = res.data || res
  } catch {
    ElMessage.error('加载性能数据失败')
  } finally {
    loading.value = false
  }
}

const formatBytes = (bytes: number) => {
  if (!bytes) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return (bytes / Math.pow(k, i)).toFixed(2) + ' ' + sizes[i]
}

const formatUptime = (ms: number) => {
  const hours = Math.floor(ms / 3600000)
  const minutes = Math.floor((ms % 3600000) / 60000)
  return `${hours} 小时 ${minutes} 分钟`
}

const getProgressColor = (percent: number) => {
  if (percent < 60) return '#67C23A'
  if (percent < 80) return '#E6A23C'
  return '#F56C6C'
}

onMounted(() => {
  loadData()
  timer = setInterval(loadData, 30000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.system-monitor-container { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-header h2 { margin: 0; font-size: 20px; font-weight: 600; }
.stat-card {
  text-align: center; padding: 30px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px; color: #fff;
}
.stat-value { font-size: 36px; font-weight: 700; }
.stat-label { font-size: 14px; margin-top: 8px; opacity: 0.9; }
</style>
