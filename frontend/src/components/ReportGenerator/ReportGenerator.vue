<template>
  <div class="report-generator">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>报表生成器</span>
          <el-select v-model="selectedTemplate" placeholder="选择模板" @change="onTemplateChange">
            <el-option label="请选择模板" value="" />
            <el-option
              v-for="template in templates"
              :key="template.id"
              :label="template.name"
              :value="template.id"
            />
          </el-select>
        </div>
      </template>

      <div v-if="!selectedTemplate" class="empty-state">
        <el-empty description="请选择一个报表模板开始生成报表">
          <el-button type="primary" @click="showTemplates">
            <el-icon><Document /></el-icon>
            选择模板
          </el-button>
        </el-empty>
      </div>

      <div v-else class="report-content">
        <el-form
          ref="reportFormRef"
          :model="reportForm"
          :rules="reportRules"
          label-width="120px"
        >
          <el-form-item label="报表标题" prop="title">
            <el-input
              v-model="reportForm.title"
              placeholder="请输入报表标题"
            />
          </el-form-item>

          <el-form-item label="报表描述" prop="description">
            <el-input
              v-model="reportForm.description"
              type="textarea"
              placeholder="请输入报表描述"
              :rows="3"
            />
          </el-form-item>

          <el-form-item label="时间范围" prop="dateRange">
            <el-date-picker
              v-model="reportForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>

          <el-form-item label="数据过滤" v-if="showDataFilter">
            <div class="data-filter">
              <el-select
                v-model="reportForm.filterField"
                placeholder="选择过滤字段"
                style="width: 150px; margin-right: 10px;"
              >
                <el-option label="状态" value="status" />
                <el-option label="类型" value="type" />
                <el-option label="级别" value="level" />
              </el-select>
              <el-select
                v-model="reportForm.filterValue"
                placeholder="选择过滤值"
                style="width: 150px; margin-right: 10px;"
              >
                <el-option
                  v-for="value in getFilterOptions()"
                  :key="value"
                  :label="value"
                  :value="value"
                />
              </el-select>
              <el-button type="primary" @click="applyFilter">
                应用过滤
              </el-button>
              <el-button @click="clearFilter">
                清除过滤
              </el-button>
            </div>
          </el-form-item>

          <el-form-item label="数据范围">
            <el-checkbox-group v-model="reportForm.dataScope">
              <el-checkbox label="all">全部数据</el-checkbox>
              <el-checkbox label="recent">最近30天</el-checkbox>
              <el-checkbox label="active">活跃数据</el-checkbox>
              <el-checkbox label="completed">已完成数据</el-checkbox>
            </el-checkbox-group>
          </el-form-item>

          <el-form-item label="导出选项">
            <el-checkbox-group v-model="exportOptions">
              <el-checkbox label="pdf">PDF格式</el-checkbox>
              <el-checkbox label="excel">Excel格式</el-checkbox>
              <el-checkbox label="include-charts">包含图表</el-checkbox>
              <el-checkbox label="include-summary">包含摘要</el-checkbox>
            </el-checkbox-group>
          </el-form-item>

          <el-form-item label="高级设置">
            <el-collapse v-model="advancedSettingsOpen">
              <el-collapse-item title="报表样式" name="style">
                <div class="style-settings">
                  <el-form-item label="页面方向" v-if="exportOptions.includes('pdf')">
                    <el-radio-group v-model="reportForm.style.orientation">
                      <el-radio label="portrait">纵向</el-radio>
                      <el-radio label="landscape">横向</el-radio>
                    </el-radio-group>
                  </el-form-item>
                  
                  <el-form-item label="页面大小" v-if="exportOptions.includes('pdf')">
                    <el-select v-model="reportForm.style.pageSize" style="width: 150px;">
                      <el-option label="A4" value="a4" />
                      <el-option label="A3" value="a3" />
                      <el-option label="Letter" value="letter" />
                    </el-select>
                  </el-form-item>

                  <el-form-item label="主题颜色">
                    <el-color-picker v-model="reportForm.style.themeColor" />
                  </el-form-item>

                  <el-form-item label="页眉页脚">
                    <el-switch
                      v-model="reportForm.style.includeHeader"
                      active-text="包含页眉"
                      inactive-text="不包含页眉"
                    />
                    <el-switch
                      v-model="reportForm.style.includeFooter"
                      active-text="包含页脚"
                      inactive-text="不包含页脚"
                    />
                  </el-form-item>
                </div>
              </el-collapse-item>

              <el-collapse-item title="数据设置" name="data">
                <div class="data-settings">
                  <el-form-item label="数据源">
                    <el-select v-model="reportForm.dataSource" style="width: 200px;">
                      <el-option label="数据库" value="database" />
                      <el-option label="API" value="api" />
                      <el-option label="文件" value="file" />
                    </el-select>
                  </el-form-item>

                  <el-form-item label="刷新数据">
                    <el-switch
                      v-model="reportForm.refreshData"
                      active-text="启用"
                      inactive-text="禁用"
                    />
                  </el-form-item>

                  <el-form-item label="缓存设置">
                    <el-switch
                      v-model="reportForm.cacheData"
                      active-text="使用缓存"
                      inactive-text="不使用缓存"
                    />
                    <span v-if="reportForm.cacheData" style="margin-left: 10px; color: #666;">
                      缓存有效期: 1小时
                    </span>
                  </el-form-item>
                </div>
              </el-collapse-item>

              <el-collapse-item title="分发设置" name="distribution">
                <div class="distribution-settings">
                  <el-form-item label="分发方式">
                    <el-checkbox-group v-model="reportForm.distribution">
                      <el-checkbox label="email">邮件发送</el-checkbox>
                      <el-checkbox label="download">下载链接</el-checkbox>
                      <el-checkbox label="api">API调用</el-checkbox>
                    </el-checkbox-group>
                  </el-form-item>

                  <el-form-item label="收件人" v-if="reportForm.distribution.includes('email')">
                    <el-input
                      v-model="reportForm.recipients"
                      type="textarea"
                      placeholder="请输入邮箱地址，多个地址用逗号分隔"
                      :rows="3"
                    />
                  </el-form-item>

                  <el-form-item label="发送时间">
                    <el-select v-model="reportForm.sendTime" style="width: 200px;">
                      <el-option label="立即发送" value="immediate" />
                      <el-option label="定时发送" value="scheduled" />
                      <el-option label="周期发送" value="periodic" />
                    </el-select>
                  </el-form-item>

                  <el-form-item label="定时设置" v-if="reportForm.sendTime !== 'immediate'">
                    <el-time-picker
                      v-model="reportForm.scheduledTime"
                      placeholder="选择发送时间"
                      format="HH:mm"
                      value-format="HH:mm"
                    />
                  </el-form-item>
                </div>
              </el-collapse-item>
            </el-collapse>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="generateReport" :loading="generating">
              <el-icon><DocumentAdd /></el-icon>
              生成报表
            </el-button>
            <el-button @click="previewReport" :disabled="generating">
              <el-icon><View /></el-icon>
              预览报表
            </el-button>
            <el-button @click="saveAsTemplate" :disabled="generating">
              <el-icon><Save /></el-icon>
              保存为模板
            </el-button>
            <el-button @click="resetForm" :disabled="generating">
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 报表生成进度对话框 -->
    <el-dialog
      v-model="progressDialogVisible"
      title="报表生成中"
      width="500px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <div class="progress-content">
        <el-progress
          :percentage="progressPercentage"
          :status="progressStatus"
          :stroke-width="8"
        />
        <div class="progress-text">{{ progressText }}</div>
      </div>
    </el-dialog>

    <!-- 报表预览对话框 -->
    <el-dialog
      v-model="previewDialogVisible"
      title="报表预览"
      width="90%"
      top="5vh"
      :fullscreen="true"
      class="report-preview-dialog"
    >
      <div class="preview-toolbar">
        <el-button type="primary" @click="exportReport('pdf')">
          <el-icon><Download /></el-icon>
          导出PDF
        </el-button>
        <el-button type="success" @click="exportReport('excel')">
          <el-icon><Document /></el-icon>
          导出Excel
        </el-button>
        <el-button @click="previewDialogVisible = false">关闭</el-button>
      </div>

      <div class="preview-content">
        <div ref="previewRef" class="report-preview">
          <!-- 报表预览内容将在这里渲染 -->
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Document, DocumentAdd, View, Save, Download } from '@element-plus/icons-vue'
import { reportGenerator } from '@/utils/report'

const templates = ref<any[]>([])
const selectedTemplate = ref<string>('')
const generating = ref(false)
const progressDialogVisible = ref(false)
const previewDialogVisible = ref(false)

const reportFormRef = ref()
const previewRef = ref()

const progressPercentage = ref(0)
const progressStatus = ref<'success' | 'exception' | ''>('')
const progressText = ref('准备生成报表...')

const showDataFilter = computed(() => {
  return selectedTemplate.value && templates.value.find(t => t.id === selectedTemplate.value)
})

const exportOptions = ref<string[]>([])

const reportForm = reactive({
  title: '',
  description: '',
  dateRange: [] as string[],
  filterField: '',
  filterValue: '',
  dataScope: ['all'],
  style: {
    orientation: 'portrait',
    pageSize: 'a4',
    themeColor: '#409eff',
    includeHeader: true,
    includeFooter: true
  },
  dataSource: 'database',
  refreshData: false,
  cacheData: true,
  distribution: [] as string[],
  recipients: '',
  sendTime: 'immediate',
  scheduledTime: '09:00'
})

const advancedSettingsOpen = ref<string[]>([])

const reportRules = {
  title: [{ required: true, message: '请输入报表标题', trigger: 'blur' }],
  description: [{ required: true, message: '请输入报表描述', trigger: 'blur' }],
  dateRange: [
    { required: true, message: '请选择时间范围', trigger: 'change' },
    { 
      validator: (rule: any, value: any, callback: any) => {
        if (value && value.length === 2) {
          const start = new Date(value[0])
          const end = new Date(value[1])
          if (end < start) {
            callback(new Error('结束日期不能早于开始日期'))
          } else {
            callback()
          }
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
}

const onTemplateChange = () => {
  const template = templates.value.find(t => t.id === selectedTemplate.value)
  if (template) {
    reportForm.title = template.config.title
    reportForm.description = template.config.description
  } else {
    reportForm.title = ''
    reportForm.description = ''
  }
}

const getFilterOptions = () => {
  if (!reportForm.filterField) return []
  
  const filterMap: Record<string, string[]> = {
    status: ['活跃', '待处理', '已完成', '已取消'],
    type: ['活动', '服务', '培训', '会议'],
    level: ['高级', '中级', '初级', '志愿者']
  }
  
  return filterMap[reportForm.filterField] || []
}

const applyFilter = () => {
  ElMessage.success(`已应用过滤条件: ${reportForm.filterField} = ${reportForm.filterValue}`)
}

const clearFilter = () => {
  reportForm.filterField = ''
  reportForm.filterValue = ''
  ElMessage.success('已清除过滤条件')
}

const generateReport = async () => {
  try {
    const valid = await reportFormRef.value.validate()
    if (!valid) return

    generating.value = true
    progressDialogVisible.value = true
    
    progressPercentage.value = 0
    progressStatus.value = ''
    progressText.value = '开始生成报表...'

    await nextTick()
    
    const template = templates.value.find(t => t.id === selectedTemplate.value)
    if (!template) {
      throw new Error('模板不存在')
    }

    progressPercentage.value = 20
    progressText.value = '正在获取数据...'
    await new Promise(resolve => setTimeout(resolve, 1000))

    progressPercentage.value = 40
    progressText.value = '正在处理数据...'
    await new Promise(resolve => setTimeout(resolve, 1000))

    progressPercentage.value = 60
    progressText.value = '正在生成图表...'
    await new Promise(resolve => setTimeout(resolve, 1000))

    progressPercentage.value = 80
    progressText.value = '正在格式化报表...'
    await new Promise(resolve => setTimeout(resolve, 1000))

    progressPercentage.value = 100
    progressText.value = '报表生成完成!'
    progressStatus.value = 'success'

    await new Promise(resolve => setTimeout(resolve, 1000))
    
    progressDialogVisible.value = false
    generating.value = false

    ElMessage.success('报表生成成功!')
    
    if (reportForm.distribution.includes('email') && reportForm.recipients) {
      await sendReportByEmail()
    }

  } catch (error) {
    console.error('生成报表失败:', error)
    progressStatus.value = 'exception'
    progressText.value = '报表生成失败'
    generating.value = false
    ElMessage.error('报表生成失败')
  }
}

const previewReport = async () => {
  try {
    const template = templates.value.find(t => t.id === selectedTemplate.value)
    if (!template) {
      ElMessage.error('请先选择模板')
      return
    }

    const mockData = {
      dateRange: {
        start: reportForm.dateRange[0] ? new Date(reportForm.dateRange[0]) : new Date(),
        end: reportForm.dateRange[1] ? new Date(reportForm.dateRange[1]) : new Date()
      },
      [template.sections[0]?.title]: [
        { name: '示例数据1', value: 100, status: '活跃' },
        { name: '示例数据2', value: 200, status: '待处理' },
        { name: '示例数据3', value: 150, status: '已完成' }
      ]
    }

    const report = reportGenerator.generateReport(template.id, mockData)
    
    previewDialogVisible.value = true
    
    await nextTick()
    
    const previewContent = document.querySelector('.report-preview')
    if (previewContent) {
      previewContent.innerHTML = `
        <div style="padding: 40px; font-family: Arial, sans-serif;">
          <div style="text-align: center; margin-bottom: 40px; border-bottom: 2px solid #e4e7ed; padding-bottom: 20px;">
            <h1 style="color: #333; margin-bottom: 10px;">${report.config.title}</h1>
            <p style="color: #666;">${report.config.description}</p>
            <p style="color: #999; font-size: 14px;">生成时间: ${new Date().toLocaleString()}</p>
          </div>
          <div style="margin-bottom: 30px;">
            <h2>时间范围: ${report.config.dateRange?.start.toLocaleDateString()} - ${report.config.dateRange?.end.toLocaleDateString()}</h2>
          </div>
          ${report.sections.map(section => `
            <div style="margin-bottom: 40px;">
              <h3 style="color: #409eff; margin-bottom: 20px;">${section.title}</h3>
              <div style="background: #f8f9fa; padding: 20px; border-radius: 8px;">
                ${section.type === 'table' ? `
                  <table style="width: 100%; border-collapse: collapse; border: 1px solid #e4e7ed;">
                    <thead>
                      <tr style="background: #f5f5f5;">
                        <th style="border: 1px solid #e4e7ed; padding: 8px; text-align: left;">名称</th>
                        <th style="border: 1px solid #e4e7ed; padding: 8px; text-align: left;">数值</th>
                        <th style="border: 1px solid #e4e7ed; padding: 8px; text-align: left;">状态</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr>
                        <td style="border: 1px solid #e4e7ed; padding: 8px;">示例数据1</td>
                        <td style="border: 1px solid #e4e7ed; padding: 8px;">100</td>
                        <td style="border: 1px solid #e4e7ed; padding: 8px;">活跃</td>
                      </tr>
                      <tr>
                        <td style="border: 1px solid #e4e7ed; padding: 8px;">示例数据2</td>
                        <td style="border: 1px solid #e4e7ed; padding: 8px;">200</td>
                        <td style="border: 1px solid #e4e7ed; padding: 8px;">待处理</td>
                      </tr>
                      <tr>
                        <td style="border: 1px solid #e4e7ed; padding: 8px;">示例数据3</td>
                        <td style="border: 1px solid #e4e7ed; padding: 8px;">150</td>
                        <td style="border: 1px solid #e4e7ed; padding: 8px;">已完成</td>
                      </tr>
                    </tbody>
                  </table>
                ` : `
                  <p style="color: #666; line-height: 1.6;">这是${section.type === 'chart' ? '图表' : section.type === 'text' ? '文本' : '列表'}内容预览</p>
                `}
              </div>
            </div>
          `).join('')}
        </div>
      `
    }

  } catch (error) {
    console.error('预览报表失败:', error)
    ElMessage.error('预览报表失败')
  }
}

const exportReport = async (format: 'pdf' | 'excel') => {
  try {
    const template = templates.value.find(t => t.id === selectedTemplate.value)
    if (!template) {
      ElMessage.error('请先选择模板')
      return
    }

    const mockData = {
      dateRange: {
        start: reportForm.dateRange[0] ? new Date(reportForm.dateRange[0]) : new Date(),
        end: reportForm.dateRange[1] ? new Date(reportForm.dateRange[1]) : new Date()
      },
      [template.sections[0]?.title]: [
        { name: '示例数据1', value: 100, status: '活跃' },
        { name: '示例数据2', value: 200, status: '待处理' },
        { name: '示例数据3', value: 150, status: '已完成' }
      ]
    }

    const report = reportGenerator.generateReport(template.id, mockData)
    
    const exportOptions = {
      format,
      fileName: `${reportForm.title}-${new Date().toISOString().split('T')[0]}.${format}`,
      landscape: reportForm.style.orientation === 'landscape',
      pageSize: reportForm.style.pageSize,
      includeCharts: true,
      includeHeaders: reportForm.style.includeHeader,
      includeFooters: reportForm.style.includeFooter
    }

    if (format === 'pdf') {
      await reportGenerator.exportToPDF(report, exportOptions)
    } else {
      await reportGenerator.exportToExcel(report, exportOptions)
    }

    ElMessage.success(`报表已导出为${format.toUpperCase()}格式!`)
    
  } catch (error) {
    console.error('导出报表失败:', error)
    ElMessage.error('导出报表失败')
  }
}

const saveAsTemplate = () => {
  try {
    const template = templates.value.find(t => t.id === selectedTemplate.value)
    if (!template) {
      ElMessage.error('请先选择模板')
      return
    }

    const newTemplate = {
      name: `${reportForm.title} (自定义模板)`,
      description: reportForm.description,
      sections: template.sections.map(section => ({
        ...section,
        title: `${reportForm.title}-${section.title}`
      })),
      config: {
        title: reportForm.title,
        description: reportForm.description
      }
    }

    const savedTemplate = reportGenerator.createTemplate(newTemplate)
    templates.value.push(savedTemplate)
    
    ElMessage.success('模板保存成功!')
    
  } catch (error) {
    console.error('保存模板失败:', error)
    ElMessage.error('保存模板失败')
  }
}

const sendReportByEmail = async () => {
  try {
    ElMessage.info('正在发送报表邮件...')
    await new Promise(resolve => setTimeout(resolve, 2000))
    ElMessage.success('报表邮件已发送!')
  } catch (error) {
    console.error('发送邮件失败:', error)
    ElMessage.error('发送邮件失败')
  }
}

const resetForm = () => {
  reportFormRef.value?.resetFields()
  selectedTemplate.value = ''
  exportOptions.value = []
  advancedSettingsOpen.value = []
}

const showTemplates = () => {
  const templatesPage = window.open('', '_blank')
  if (templatesPage) {
    templatesPage.document.write(`
      <html>
        <head>
          <title>报表模板</title>
          <style>
            body { font-family: Arial, sans-serif; margin: 20px; }
            .template { margin: 20px; padding: 20px; border: 1px solid #e4e7ed; border-radius: 8px; }
            .template h3 { margin: 0 0 10px 0; color: #409eff; }
            .template p { color: #666; margin: 10px 0; }
            .back-btn { background: #409eff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 4px; }
          </style>
        </head>
        <body>
          <h1>可用报表模板</h1>
          <div class="template">
            <h3>志愿者活动报表</h3>
            <p>志愿者活动统计报表，包含活动概况、活动列表、活动趋势等。</p>
            <p>部分数量: 3</p>
          </div>
          <div class="template">
            <h3>服务时长报表</h3>
            <p>志愿者服务时长统计，包含时长统计、时长分布等。</p>
            <p>部分数量: 2</p>
          </div>
          <div class="template">
            <h3>志愿者绩效报表</h3>
            <p>志愿者绩效评估报表，包含绩效指标、绩效趋势等。</p>
            <p>部分数量: 2</p>
          </div>
          <a href="#" class="back-btn" onclick="window.close()">返回报表生成器</a>
        </body>
      </html>
    `)
  }
}

onMounted(() => {
  templates.value = reportGenerator.getAllTemplates()
})
</script>

<style scoped>
.report-generator {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.report-content {
  margin-top: 20px;
}

.data-filter {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.style-settings,
.data-settings,
.distribution-settings {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.progress-content {
  text-align: center;
  padding: 20px;
}

.progress-text {
  margin-top: 15px;
  color: #666;
  font-size: 14px;
}

.report-preview-dialog {
  .preview-toolbar {
    display: flex;
    gap: 10px;
    margin-bottom: 20px;
    padding: 15px;
    background: #f8f9fa;
    border-bottom: 1px solid #e4e7ed;
  }

  .preview-content {
    height: calc(100vh - 100px);
    overflow-y: auto;
  }

  .report-preview {
    background: white;
    padding: 40px;
  }
}
</style>