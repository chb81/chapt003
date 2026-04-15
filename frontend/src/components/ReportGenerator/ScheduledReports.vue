<template>
  <div class="scheduled-reports">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>定时报表管理</span>
          <el-button type="primary" size="small" @click="showCreateDialog">
            <el-icon><Plus /></el-icon>
            创建定时报表
          </el-button>
        </div>
      </template>

      <div class="statistics-bar">
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-number">{{ statistics.totalScheduledReports }}</div>
              <div class="stat-label">总定时报表</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-number">{{ statistics.activeScheduledReports }}</div>
              <div class="stat-label">活跃报表</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-number">{{ statistics.totalTemplates }}</div>
              <div class="stat-label">可用模板</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-number">{{ getNextRunCount() }}</div>
              <div class="stat-label">即将运行</div>
            </div>
          </el-col>
        </el-row>
      </div>

      <el-table
        :data="scheduledReports"
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column
          prop="name"
          label="报表名称"
          min-width="200"
        >
          <template #default="{ row }">
            <div class="report-name">
              <el-icon><Document /></el-icon>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column
          prop="templateName"
          label="使用模板"
          width="150"
        >
          <template #default="{ row }">
            <el-tag size="small" type="info">
              {{ row.templateName }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column
          prop="schedule"
          label="运行频率"
          width="120"
        >
          <template #default="{ row }">
            <el-tag :type="getScheduleType(row.schedule)">
              {{ getScheduleText(row.schedule) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column
          prop="nextRunDate"
          label="下次运行"
          width="180"
        >
          <template #default="{ row }">
            <div class="next-run-time">
              <el-icon :class="{ 'is-running': isRunningSoon(row.nextRunDate) }">
                <Clock />
              </el-icon>
              <span>{{ formatDateTime(row.nextRunDate) }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column
          label="收件人"
          width="200"
        >
          <template #default="{ row }">
            <div class="recipients">
              <el-tooltip
                :content="row.recipients.join(', ')"
                placement="top"
                :show-after="500"
              >
                <el-tag size="small" type="warning">
                  {{ row.recipients.length }} 个收件人
                </el-tag>
              </el-tooltip>
            </div>
          </template>
        </el-table-column>

        <el-table-column
          prop="enabled"
          label="状态"
          width="100"
        >
          <template #default="{ row }">
            <el-switch
              v-model="row.enabled"
              @change="toggleSchedule(row)"
              :loading="row.changing"
            />
          </template>
        </el-table-column>

        <el-table-column
          label="操作"
          width="200"
          fixed="right"
        >
          <template #default="{ row }">
            <el-button-group size="small">
              <el-button
                type="primary"
                @click="runNow(row)"
                :loading="row.running"
              >
                <el-icon><VideoPlay /></el-icon>
                立即运行
              </el-button>
              <el-button
                type="info"
                @click="editSchedule(row)"
              >
                <el-icon><Edit /></el-icon>
              </el-button>
              <el-button
                type="danger"
                @click="deleteSchedule(row)"
              >
                <el-icon><Delete /></el-icon>
              </el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="scheduledReports.length === 0" class="empty-state">
        <el-empty description="暂无定时报表">
          <el-button type="primary" @click="showCreateDialog">
            <el-icon><Plus /></el-icon>
            创建第一个定时报表
          </el-button>
        </el-empty>
      </div>
    </el-card>

    <!-- 创建定时报表对话框 -->
    <el-dialog
      v-model="createDialogVisible"
      title="创建定时报表"
      width="800px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="scheduleForm"
        :rules="scheduleRules"
        label-width="120px"
      >
        <el-form-item label="报表名称" prop="name">
          <el-input
            v-model="scheduleForm.name"
            placeholder="请输入报表名称"
            clearable
          />
        </el-form-item>

        <el-form-item label="选择模板" prop="templateId">
          <el-select
            v-model="scheduleForm.templateId"
            placeholder="请选择报表模板"
            style="width: 100%;"
          >
            <el-option
              v-for="template in templates"
              :key="template.id"
              :label="template.name"
              :value="template.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="运行频率" prop="schedule">
          <el-radio-group v-model="scheduleForm.schedule">
            <el-radio label="daily">每日</el-radio>
            <el-radio label="weekly">每周</el-radio>
            <el-radio label="monthly">每月</el-radio>
            <el-radio label="quarterly">每季度</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="运行时间">
          <el-time-picker
            v-model="scheduleForm.runTime"
            placeholder="选择运行时间"
            format="HH:mm"
            value-format="HH:mm"
            style="width: 200px;"
          />
        </el-form-item>

        <el-form-item label="收件人" prop="recipients">
          <div class="recipients-input">
            <el-input
              v-model="newRecipient"
              placeholder="输入邮箱地址，按回车添加"
              @keyup.enter="addRecipient"
              style="width: 400px; margin-right: 10px;"
            />
            <el-button type="primary" @click="addRecipient">添加</el-button>
          </div>
          <div class="recipients-list">
            <el-tag
              v-for="(recipient, index) in scheduleForm.recipients"
              :key="index"
              closable
              @close="removeRecipient(index)"
              class="recipient-tag"
            >
              {{ recipient }}
            </el-tag>
          </div>
        </el-form-item>

        <el-form-item label="报表设置">
          <el-collapse v-model="settingsOpen">
            <el-collapse-item title="高级设置" name="settings">
              <el-form-item label="报表标题">
                <el-input
                  v-model="scheduleForm.settings.title"
                  placeholder="自定义报表标题"
                />
              </el-form-item>

              <el-form-item label="时间范围">
                <el-date-picker
                  v-model="scheduleForm.settings.dateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  style="width: 100%;"
                />
              </el-form-item>

              <el-form-item label="导出格式">
                <el-checkbox-group v-model="scheduleForm.settings.exportFormats">
                  <el-checkbox label="pdf">PDF</el-checkbox>
                  <el-checkbox label="excel">Excel</el-checkbox>
                  <el-checkbox label="word">Word</el-checkbox>
                </el-checkbox-group>
              </el-form-item>

              <el-form-item label="包含内容">
                <el-checkbox-group v-model="scheduleForm.settings.includeContent">
                  <el-checkbox label="charts">包含图表</el-checkbox>
                  <el-checkbox label="summary">包含摘要</el-checkbox>
                  <el-checkbox label="charts">包含明细</el-checkbox>
                </el-checkbox-group>
              </el-form-item>

              <el-form-item label="分页设置">
                <el-switch
                  v-model="scheduleForm.settings.paginated"
                  active-text="启用分页"
                  inactive-text="禁用分页"
                />
                <el-input-number
                  v-if="scheduleForm.settings.paginated"
                  v-model="scheduleForm.settings.pageSize"
                  :min="1"
                  :max="100"
                  style="margin-left: 10px;"
                />
              </el-form-item>

              <el-form-item label="主题样式">
                <el-select
                  v-model="scheduleForm.settings.theme"
                  style="width: 200px;"
                >
                  <el-option label="默认主题" value="default" />
                  <el-option label="专业主题" value="professional" />
                  <el-option label="简洁主题" value="simple" />
                  <el-option label="彩色主题" value="colorful" />
                </el-select>
              </el-form-item>
            </el-collapse-item>

            <el-collapse-item title="通知设置" name="notifications">
              <el-form-item label="发送通知">
                <el-switch
                  v-model="scheduleForm.notifications.enabled"
                  active-text="启用"
                  inactive-text="禁用"
                />
              </el-form-item>

              <el-form-item label="通知方式">
                <el-checkbox-group v-model="scheduleForm.notifications.methods">
                  <el-checkbox label="email">邮件通知</el-checkbox>
                  <el-checkbox label="sms">短信通知</el-checkbox>
                  <el-checkbox label="webhook">Webhook</el-checkbox>
                </el-checkbox-group>
              </el-form-item>

              <el-form-item label="失败重试">
                <el-input-number
                  v-model="scheduleForm.notifications.maxRetries"
                  :min="0"
                  :max="10"
                  placeholder="最大重试次数"
                />
                <span style="margin-left: 10px; color: #666;">0表示不重试</span>
              </el-form-item>

              <el-form-item label="超时设置">
                <el-input-number
                  v-model="scheduleForm.notifications.timeout"
                  :min="30"
                  :max="300"
                  placeholder="超时时间(秒)"
                />
                <span style="margin-left: 10px; color: #666;">默认60秒</span>
              </el-form-item>
            </el-collapse-item>
          </el-collapse>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="createSchedule" :loading="creating">
            <el-icon><Plus /></el-icon>
            创建定时报表
          </el-button>
          <el-button @click="resetForm">取消</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>

    <!-- 编辑定时报表对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑定时报表"
      width="800px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="scheduleForm"
        :rules="scheduleRules"
        label-width="120px"
      >
        <el-form-item label="报表名称" prop="name">
          <el-input
            v-model="scheduleForm.name"
            placeholder="请输入报表名称"
            clearable
          />
        </el-form-item>

        <el-form-item label="选择模板" prop="templateId">
          <el-select
            v-model="scheduleForm.templateId"
            placeholder="请选择报表模板"
            style="width: 100%;"
            disabled
          >
            <el-option
              v-for="template in templates"
              :key="template.id"
              :label="template.name"
              :value="template.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="运行频率" prop="schedule">
          <el-radio-group v-model="scheduleForm.schedule">
            <el-radio label="daily">每日</el-radio>
            <el-radio label="weekly">每周</el-radio>
            <el-radio label="monthly">每月</el-radio>
            <el-radio label="quarterly">每季度</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="运行时间">
          <el-time-picker
            v-model="scheduleForm.runTime"
            placeholder="选择运行时间"
            format="HH:mm"
            value-format="HH:mm"
            style="width: 200px;"
          />
        </el-form-item>

        <el-form-item label="收件人" prop="recipients">
          <div class="recipients-input">
            <el-input
              v-model="newRecipient"
              placeholder="输入邮箱地址，按回车添加"
              @keyup.enter="addRecipient"
              style="width: 400px; margin-right: 10px;"
            />
            <el-button type="primary" @click="addRecipient">添加</el-button>
          </div>
          <div class="recipients-list">
            <el-tag
              v-for="(recipient, index) in scheduleForm.recipients"
              :key="index"
              closable
              @close="removeRecipient(index)"
              class="recipient-tag"
            >
              {{ recipient }}
            </el-tag>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="updateSchedule" :loading="updating">
            <el-icon><Edit /></el-icon>
            更新定时报表
          </el-button>
          <el-button @click="resetForm">取消</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>

    <!-- 运行历史对话框 -->
    <el-dialog
      v-model="historyDialogVisible"
      title="运行历史"
      width="900px"
    >
      <el-table
        :data="runHistory"
        style="width: 100%"
        v-loading="historyLoading"
      >
        <el-table-column
          prop="runTime"
          label="运行时间"
          width="180"
        >
          <template #default="{ row }">
            <span>{{ formatDateTime(row.runTime) }}</span>
          </template>
        </el-table-column>

        <el-table-column
          prop="status"
          label="状态"
          width="100"
        >
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column
          prop="duration"
          label="耗时"
          width="100"
        >
          <template #default="{ row }">
            <span>{{ row.duration }}秒</span>
          </template>
        </el-table-column>

        <el-table-column
          prop="recipients"
          label="收件人"
          width="150"
        >
          <template #default="{ row }">
            <el-tag size="small" type="info">
              {{ row.recipients.length }}个
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column
          prop="exportFormats"
          label="导出格式"
          width="120"
        >
          <template #default="{ row }">
            <el-tag size="small" v-for="format in row.exportFormats" :key="format" :type="getExportFormatType(format)">
              {{ format.toUpperCase() }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column
          label="操作"
          width="100"
          fixed="right"
        >
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="viewHistoryDetail(row)"
            >
              查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Plus, 
  Edit, 
  Delete, 
  Document, 
  Clock, 
  VideoPlay,
  Check,
  Close
} from '@element-plus/icons-vue'
import { reportGenerator } from '@/utils/report'

const scheduledReports = ref<any[]>([])
const templates = ref<any[]>([])
const statistics = ref({
  totalScheduledReports: 0,
  activeScheduledReports: 0,
  totalTemplates: 0
})

const loading = ref(false)
const creating = ref(false)
const updating = ref(false)
 historyLoading = ref(false)

const createDialogVisible = ref(false)
const editDialogVisible = ref(false)
const historyDialogVisible = ref(false)

const formRef = ref()
const settingsOpen = ref<string[]>([])
const runHistory = ref<any[]>([])

const scheduleForm = reactive({
  name: '',
  templateId: '',
  schedule: 'daily',
  runTime: '09:00',
  recipients: [] as string[],
  settings: {
    title: '',
    dateRange: [] as string[],
    exportFormats: ['pdf'],
    includeContent: ['charts', 'summary'],
    paginated: true,
    pageSize: 20,
    theme: 'default'
  },
  notifications: {
    enabled: true,
    methods: ['email'],
    maxRetries: 3,
    timeout: 60
  }
})

const scheduleRules = {
  name: [{ required: true, message: '请输入报表名称', trigger: 'blur' }],
  templateId: [{ required: true, message: '请选择报表模板', trigger: 'change' }],
  schedule: [{ required: true, message: '请选择运行频率', trigger: 'change' }],
  recipients: [
    { required: true, message: '请添加至少一个收件人', trigger: 'change' },
    {
      validator: (rule: any, value: string[], callback: any) => {
        const hasInvalidEmail = value.some(email => !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email))
        if (hasInvalidEmail) {
          callback(new Error('存在无效的邮箱地址'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
}

const newRecipient = ref('')

const getScheduleType = (schedule: string) => {
  const typeMap: Record<string, string> = {
    daily: 'success',
    weekly: 'warning',
    monthly: 'info',
    quarterly: 'danger'
  }
  return typeMap[schedule] || 'info'
}

const getScheduleText = (schedule: string) => {
  const textMap: Record<string, string> = {
    daily: '每日',
    weekly: '每周',
    monthly: '每月',
    quarterly: '每季度'
  }
  return textMap[schedule] || schedule
}

const getStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    success: 'success',
    failed: 'danger',
    running: 'warning',
    pending: 'info'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    success: '成功',
    failed: '失败',
    running: '运行中',
    pending: '等待中'
  }
  return textMap[status] || status
}

const getExportFormatType = (format: string) => {
  const typeMap: Record<string, string> = {
    pdf: 'danger',
    excel: 'success',
    word: 'primary'
  }
  return typeMap[format] || 'info'
}

const formatDateTime = (date: Date | string) => {
  const d = typeof date === 'string' ? new Date(date) : date
  return d.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const isRunningSoon = (date: Date | string) => {
  const now = new Date()
  const target = typeof date === 'string' ? new Date(date) : date
  const diff = target.getTime() - now.getTime()
  return diff > 0 && diff < 24 * 60 * 60 * 1000 // 24小时内
}

const getNextRunCount = () => {
  const now = new Date()
  return scheduledReports.value.filter(schedule => {
    const nextRun = typeof schedule.nextRunDate === 'string' ? new Date(schedule.nextRunDate) : schedule.nextRunDate
    const diff = nextRun.getTime() - now.getTime()
    return diff > 0 && diff < 24 * 60 * 60 * 1000 // 24小时内
  }).length
}

const addRecipient = () => {
  const email = newRecipient.value.trim()
  if (email && !scheduleForm.recipients.includes(email)) {
    scheduleForm.recipients.push(email)
    newRecipient.value = ''
  }
}

const removeRecipient = (index: number) => {
  scheduleForm.recipients.splice(index, 1)
}

const resetForm = () => {
  Object.assign(scheduleForm, {
    name: '',
    templateId: '',
    schedule: 'daily',
    runTime: '09:00',
    recipients: [],
    settings: {
      title: '',
      dateRange: [],
      exportFormats: ['pdf'],
      includeContent: ['charts', 'summary'],
      paginated: true,
      pageSize: 20,
      theme: 'default'
    },
    notifications: {
      enabled: true,
      methods: ['email'],
      maxRetries: 3,
      timeout: 60
    }
  })
  newRecipient.value = ''
  settingsOpen.value = []
}

const showCreateDialog = () => {
  resetForm()
  createDialogVisible.value = true
}

const showEditDialog = (schedule: any) => {
  Object.assign(scheduleForm, {
    name: schedule.name,
    templateId: schedule.templateId,
    schedule: schedule.schedule,
    runTime: schedule.runTime,
    recipients: [...schedule.recipients],
    settings: { ...schedule.settings },
    notifications: { ...schedule.notifications }
  })
  editDialogVisible.value = true
}

const showHistoryDialog = (schedule: any) => {
  historyDialogVisible.value = true
  loadRunHistory(schedule)
}

const createSchedule = async () => {
  try {
    const valid = await formRef.value.validate()
    if (!valid) return

    creating.value = true

    const template = templates.value.find(t => t.id === scheduleForm.templateId)
    const scheduleData = {
      name: scheduleForm.name,
      templateId: scheduleForm.templateId,
      templateName: template?.name || '未知模板',
      schedule: scheduleForm.schedule,
      runTime: scheduleForm.runTime,
      recipients: scheduleForm.recipients,
      enabled: true,
      settings: scheduleForm.settings,
      notifications: scheduleForm.notifications
    }

    const newSchedule = reportGenerator.scheduleReport(scheduleData)
    scheduledReports.value.push(newSchedule)
    
    createDialogVisible.value = false
    resetForm()
    creating.value = false

    ElMessage.success('定时报表创建成功!')
    
  } catch (error) {
    console.error('创建定时报表失败:', error)
    creating.value = false
    ElMessage.error('创建定时报表失败')
  }
}

const updateSchedule = async () => {
  try {
    const valid = await formRef.value.validate()
    if (!valid) return

    updating.value = true

    const updatedData = {
      ...scheduleForm,
      enabled: true
    }

    const updated = reportGenerator.updateSchedule(scheduleForm.id, updatedData)
    if (updated) {
      const index = scheduledReports.value.findIndex(s => s.id === scheduleForm.id)
      if (index !== -1) {
        scheduledReports.value[index] = updated
      }
    }

    editDialogVisible.value = false
    resetForm()
    updating.value = false

    ElMessage.success('定时报表更新成功!')
    
  } catch (error) {
    console.error('更新定时报表失败:', error)
    updating.value = false
    ElMessage.error('更新定时报表失败')
  }
}

const toggleSchedule = async (schedule: any) => {
  try {
    schedule.changing = true
    const updated = reportGenerator.updateSchedule(schedule.id, {
      enabled: schedule.enabled
    })
    
    if (updated) {
      const index = scheduledReports.value.findIndex(s => s.id === schedule.id)
      if (index !== -1) {
        scheduledReports.value[index] = updated
      }
    }

    schedule.changing = false
    ElMessage.success(schedule.enabled ? '定时报表已启用' : '定时报表已禁用')
    
  } catch (error) {
    console.error('切换定时报表状态失败:', error)
    schedule.enabled = !schedule.enabled
    schedule.changing = false
    ElMessage.error('操作失败')
  }
}

const runNow = async (schedule: any) => {
  try {
    schedule.running = true
    ElMessage.info('正在立即运行定时报表...')
    
    await new Promise(resolve => setTimeout(resolve, 3000))
    
    schedule.running = false
    ElMessage.success('报表运行完成!')
    
    addRunHistory({
      runTime: new Date(),
      status: 'success',
      duration: 15,
      recipients: schedule.recipients,
      exportFormats: schedule.settings.exportFormats
    })
    
  } catch (error) {
    console.error('立即运行失败:', error)
    schedule.running = false
    ElMessage.error('立即运行失败')
  }
}

const deleteSchedule = async (schedule: any) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除定时报表"${schedule.name}"吗？删除后将无法恢复。`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const success = reportGenerator.deleteSchedule(schedule.id)
    if (success) {
      scheduledReports.value = scheduledReports.value.filter(s => s.id !== schedule.id)
      ElMessage.success('定时报表删除成功')
    } else {
      ElMessage.error('定时报表删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除定时报表失败:', error)
      ElMessage.error('删除定时报表失败')
    }
  }
}

const loadRunHistory = async (schedule: any) => {
  try {
    historyLoading.value = true
    
    runHistory.value = [
      {
        runTime: new Date(Date.now() - 86400000),
        status: 'success',
        duration: 12,
        recipients: schedule.recipients,
        exportFormats: schedule.settings.exportFormats
      },
      {
        runTime: new Date(Date.now() - 172800000),
        status: 'failed',
        duration: 25,
        recipients: schedule.recipients,
        exportFormats: schedule.settings.exportFormats
      },
      {
        runTime: new Date(Date.now() - 259200000),
        status: 'success',
        duration: 18,
        recipients: schedule.recipients,
        exportFormats: schedule.settings.exportFormats
      }
    ]
    
    historyLoading.value = false
    
  } catch (error) {
    console.error('加载运行历史失败:', error)
    historyLoading.value = false
    ElMessage.error('加载运行历史失败')
  }
}

const viewHistoryDetail = (record: any) => {
  const detailWindow = window.open('', '_blank')
  if (detailWindow) {
    detailWindow.document.write(`
      <html>
        <head>
          <title>报表运行详情</title>
          <style>
            body { font-family: Arial, sans-serif; margin: 20px; }
            .detail { margin: 20px 0; padding: 20px; border: 1px solid #e4e7ed; border-radius: 8px; }
            .detail h3 { margin: 0 0 15px 0; color: #409eff; }
            .detail-item { margin: 10px 0; }
            .detail-label { font-weight: bold; margin-right: 10px; }
            .success { color: #67c23a; }
            .failed { color: #f56c6c; }
            .warning { color: #e6a23c; }
          </style>
        </head>
        <body>
          <h1>报表运行详情</h1>
          <div class="detail">
            <h3>基本信息</h3>
            <div class="detail-item">
              <span class="detail-label">运行时间:</span>
              <span>${formatDateTime(record.runTime)}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">状态:</span>
              <span class="${record.status}">${getStatusText(record.status)}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">耗时:</span>
              <span>${record.duration}秒</span>
            </div>
          </div>
          <div class="detail">
            <h3>收件人信息</h3>
            <div class="detail-item">
              <span class="detail-label">收件人数量:</span>
              <span>${record.recipients.length}个</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">收件人列表:</span>
              <span>${record.recipients.join(', ')}</span>
            </div>
          </div>
          <div class="detail">
            <h3>导出信息</h3>
            <div class="detail-item">
              <span class="detail-label">导出格式:</span>
              <span>${record.exportFormats.join(', ')}</span>
            </div>
          </div>
        </body>
      </html>
    `)
  }
}

const addRunHistory = (record: any) => {
  runHistory.value.unshift(record)
  if (runHistory.value.length > 10) {
    runHistory.value.pop()
  }
}

onMounted(() => {
  templates.value = reportGenerator.getAllTemplates()
  statistics.value = reportGenerator.getReportStatistics()
  
  scheduledReports.value = [
    {
      id: 'schedule-1',
      name: '每日志愿者活动报表',
      templateId: 'volunteer-activity',
      templateName: '志愿者活动报表',
      schedule: 'daily',
      runTime: '09:00',
      recipients: ['admin@example.com', 'manager@example.com'],
      enabled: true,
      nextRunDate: new Date(Date.now() + 24 * 60 * 60 * 1000),
      settings: {
        exportFormats: ['pdf'],
        includeContent: ['charts', 'summary']
      }
    },
    {
      id: 'schedule-2',
      name: '每周服务时长统计',
      templateId: 'volunteer-hours',
      templateName: '服务时长报表',
      schedule: 'weekly',
      runTime: '10:00',
      recipients: ['hr@example.com', 'finance@example.com'],
      enabled: false,
      nextRunDate: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000),
      settings: {
        exportFormats: ['excel', 'pdf'],
        includeContent: ['charts', 'details']
      }
    }
  ]
  
  statistics.value.totalScheduledReports = scheduledReports.value.length
  statistics.value.activeScheduledReports = scheduledReports.value.filter(s => s.enabled).length
})
</script>

<style scoped>
.scheduled-reports {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.statistics-bar {
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 10px;
}

.stat-label {
  color: #666;
  font-size: 14px;
}

.report-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.next-run-time {
  display: flex;
  align-items: center;
  gap: 8px;
}

.next-run-time .el-icon {
  color: #67c23a;
}

.next-run-time.is-running .el-icon {
  color: #e6a23c;
  animation: pulse 2s infinite;
}

.recipients {
  display: flex;
  align-items: center;
}

.recipients-input {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.recipients-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.recipient-tag {
  margin-right: 8px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.5; }
  100% { opacity: 1; }
}
</style>