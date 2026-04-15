<template>
  <div class="report-templates">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>报表模板管理</span>
          <el-button type="primary" size="small" @click="showCreateTemplateDialog">
            <el-icon><Plus /></el-icon>
            创建模板
          </el-button>
        </div>
      </template>

      <div class="templates-grid">
        <div
          v-for="template in templates"
          :key="template.id"
          class="template-card"
          @click="selectTemplate(template)"
        >
          <el-card class="card-item">
            <template #header>
              <div class="template-header">
                <h3>{{ template.name }}</h3>
                <el-tag size="small" type="info">{{ template.sections.length }} 个部分</el-tag>
              </div>
            </template>
            
            <div class="template-content">
              <p class="template-description">{{ template.description }}</p>
              
              <div class="template-sections">
                <div v-for="section in template.sections" :key="section.title" class="section-item">
                  <el-icon :class="getSectionIconClass(section.type)">
                    <component :is="getSectionIcon(section.type)" />
                  </el-icon>
                  <span>{{ section.title }}</span>
                  <el-tag size="small" :type="getSectionTypeClass(section.type)">
                    {{ getSectionTypeName(section.type) }}
                  </el-tag>
                </div>
              </div>

              <div class="template-actions">
                <el-button-group size="small">
                  <el-button type="primary" @click.stop="editTemplate(template)">
                    <el-icon><Edit /></el-icon>
                  </el-button>
                  <el-button type="warning" @click.stop="duplicateTemplate(template)">
                    <el-icon><CopyDocument /></el-icon>
                  </el-button>
                  <el-button type="danger" @click.stop="deleteTemplate(template)">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </el-button-group>
              </div>
            </div>
          </el-card>
        </div>
      </div>

      <div v-if="templates.length === 0" class="empty-state">
        <el-empty description="暂无报表模板">
          <el-button type="primary" @click="showCreateTemplateDialog">
            <el-icon><Plus /></el-icon>
            创建第一个模板
          </el-button>
        </el-empty>
      </div>
    </el-card>

    <!-- 创建模板对话框 -->
    <el-dialog
      v-model="createDialogVisible"
      title="创建报表模板"
      width="800px"
      @close="resetTemplateForm"
    >
      <el-form
        ref="templateFormRef"
        :model="templateForm"
        :rules="templateRules"
        label-width="100px"
        @submit.prevent="saveTemplate"
      >
        <el-form-item label="模板名称" prop="name">
          <el-input
            v-model="templateForm.name"
            placeholder="请输入模板名称"
            clearable
          />
        </el-form-item>

        <el-form-item label="模板描述" prop="description">
          <el-input
            v-model="templateForm.description"
            type="textarea"
            placeholder="请输入模板描述"
            :rows="3"
          />
        </el-form-item>

        <el-form-item label="报表标题" prop="config.title">
          <el-input
            v-model="templateForm.config.title"
            placeholder="请输入报表标题"
          />
        </el-form-item>

        <el-form-item label="报表描述" prop="config.description">
          <el-input
            v-model="templateForm.config.description"
            type="textarea"
            placeholder="请输入报表描述"
            :rows="2"
          />
        </el-form-item>

        <el-form-item label="报表部分">
          <div class="sections-list">
            <div
              v-for="(section, index) in templateForm.sections"
              :key="index"
              class="section-item"
            >
              <div class="section-header">
                <span>部分 {{ index + 1 }}</span>
                <el-button
                  type="danger"
                  size="small"
                  circle
                  :icon="Close"
                  @click="removeSection(index)"
                />
              </div>

              <div class="section-content">
                <el-form-item label="标题">
                  <el-input
                    v-model="section.title"
                    placeholder="请输入部分标题"
                  />
                </el-form-item>

                <el-form-item label="类型">
                  <el-select v-model="section.type" placeholder="请选择部分类型">
                    <el-option label="表格" value="table" />
                    <el-option label="图表" value="chart" />
                    <el-option label="文本" value="text" />
                    <el-option label="列表" value="list" />
                  </el-select>
                </el-form-item>

                <el-form-item
                  v-if="section.type === 'chart'"
                  label="图表类型"
                >
                  <el-select v-model="section.config.chartType" placeholder="请选择图表类型">
                    <el-option label="折线图" value="line" />
                    <el-option label="柱状图" value="bar" />
                    <el-option label="饼图" value="pie" />
                    <el-option label="雷达图" value="radar" />
                    <el-option label="散点图" value="scatter" />
                  </el-select>
                </el-form-item>
              </div>
            </div>
          </div>

          <el-button
            type="primary"
            size="small"
            @click="addSection"
          >
            <el-icon><Plus /></el-icon>
            添加部分
          </el-button>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveTemplate">保存模板</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编辑模板对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑报表模板"
      width="800px"
      @close="resetTemplateForm"
    >
      <el-form
        ref="templateFormRef"
        :model="templateForm"
        :rules="templateRules"
        label-width="100px"
        @submit.prevent="updateTemplate"
      >
        <el-form-item label="模板名称" prop="name">
          <el-input
            v-model="templateForm.name"
            placeholder="请输入模板名称"
            clearable
          />
        </el-form-item>

        <el-form-item label="模板描述" prop="description">
          <el-input
            v-model="templateForm.description"
            type="textarea"
            placeholder="请输入模板描述"
            :rows="3"
          />
        </el-form-item>

        <el-form-item label="报表标题" prop="config.title">
          <el-input
            v-model="templateForm.config.title"
            placeholder="请输入报表标题"
          />
        </el-form-item>

        <el-form-item label="报表描述" prop="config.description">
          <el-input
            v-model="templateForm.config.description"
            type="textarea"
            placeholder="请输入报表描述"
            :rows="2"
          />
        </el-form-item>

        <el-form-item label="报表部分">
          <div class="sections-list">
            <div
              v-for="(section, index) in templateForm.sections"
              :key="index"
              class="section-item"
            >
              <div class="section-header">
                <span>部分 {{ index + 1 }}</span>
                <el-button
                  type="danger"
                  size="small"
                  circle
                  :icon="Close"
                  @click="removeSection(index)"
                />
              </div>

              <div class="section-content">
                <el-form-item label="标题">
                  <el-input
                    v-model="section.title"
                    placeholder="请输入部分标题"
                  />
                </el-form-item>

                <el-form-item label="类型">
                  <el-select v-model="section.type" placeholder="请选择部分类型">
                    <el-option label="表格" value="table" />
                    <el-option label="图表" value="chart" />
                    <el-option label="文本" value="text" />
                    <el-option label="列表" value="list" />
                  </el-select>
                </el-form-item>

                <el-form-item
                  v-if="section.type === 'chart'"
                  label="图表类型"
                >
                  <el-select v-model="section.config.chartType" placeholder="请选择图表类型">
                    <el-option label="折线图" value="line" />
                    <el-option label="柱状图" value="bar" />
                    <el-option label="饼图" value="pie" />
                    <el-option label="雷达图" value="radar" />
                    <el-option label="散点图" value="scatter" />
                  </el-select>
                </el-form-item>
              </div>
            </div>
          </div>

          <el-button
            type="primary"
            size="small"
            @click="addSection"
          >
            <el-icon><Plus /></el-icon>
            添加部分
          </el-button>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="updateTemplate">更新模板</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 报表预览对话框 -->
    <el-dialog
      v-model="previewDialogVisible"
      title="报表预览"
      width="1000px"
      top="5vh"
      class="report-preview-dialog"
    >
      <div class="report-preview-content">
        <div class="report-header">
          <h2>{{ previewTemplate?.config.title }}</h2>
          <p v-if="previewTemplate?.config.description" class="report-description">
            {{ previewTemplate.config.description }}
          </p>
        </div>

        <div class="report-body">
          <div
            v-for="section in previewTemplate?.sections"
            :key="section.title"
            class="report-section"
          >
            <h3>{{ section.title }}</h3>
            <div class="section-preview">
              <div
                v-if="section.type === 'table'"
                class="table-preview"
              >
                <el-table
                  :data="[]"
                  style="width: 100%"
                  size="small"
                >
                  <el-table-column
                    v-for="column in getSampleColumns(section)"
                    :key="column.prop"
                    :prop="column.prop"
                    :label="column.label"
                    width="150"
                  />
                </el-table>
              </div>

              <div
                v-else-if="section.type === 'chart'"
                class="chart-preview"
              >
                <div class="chart-placeholder">
                  <el-icon size="48"><PieChart /></el-icon>
                  <p>图表预览区域</p>
                </div>
              </div>

              <div
                v-else-if="section.type === 'text'"
                class="text-preview"
              >
                <div class="text-placeholder">
                  <p>文本内容预览区域</p>
                </div>
              </div>

              <div
                v-else-if="section.type === 'list'"
                class="list-preview"
              >
                <div class="list-placeholder">
                  <el-tag v-for="i in 3" :key="i" size="small" class="list-item-tag">
                    列表项 {{ i }}
                  </el-tag>
                </div>
              </div>
            </div>
          </div>
        </div>

        <template #footer>
          <span class="dialog-footer">
            <el-button @click="previewDialogVisible = false">关闭</el-button>
            <el-button type="primary" @click="generateReportFromTemplate(previewTemplate!)">
              生成报表
            </el-button>
          </span>
        </template>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, CopyDocument, Close, PieChart } from '@element-plus/icons-vue'
import { reportGenerator } from '@/utils/report'

const templates = ref<any[]>([])
const selectedTemplate = ref<any>(null)
const previewTemplate = ref<any>(null)

const createDialogVisible = ref(false)
const editDialogVisible = ref(false)
const previewDialogVisible = ref(false)

const templateFormRef = ref()

const templateForm = reactive({
  name: '',
  description: '',
  config: {
    title: '',
    description: ''
  },
  sections: [] as Array<{
    title: string
    type: 'table' | 'chart' | 'text' | 'list'
    config: any
  }>
})

const templateRules = {
  name: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
  description: [{ required: true, message: '请输入模板描述', trigger: 'blur' }],
  'config.title': [{ required: true, message: '请输入报表标题', trigger: 'blur' }],
  'config.description': [{ required: true, message: '请输入报表描述', trigger: 'blur' }]
}

const sampleData = {
  sections: [
    {
      title: '示例数据',
      type: 'table',
      config: {},
      data: [
        { name: '示例1', value: 100, status: '活跃' },
        { name: '示例2', value: 200, status: '待处理' },
        { name: '示例3', value: 150, status: '已完成' }
      ]
    }
  ]
}

const getSectionIcon = (type: string) => {
  switch (type) {
    case 'table': return 'Grid'
    case 'chart': return 'PieChart'
    case 'text': return 'Document'
    case 'list': return 'List'
    default: return 'Document'
  }
}

const getSectionIconClass = (type: string) => {
  switch (type) {
    case 'table': return 'icon-table'
    case 'chart': return 'icon-chart'
    case 'text': return 'icon-text'
    case 'list': return 'icon-list'
    default: return ''
  }
}

const getSectionTypeClass = (type: string) => {
  switch (type) {
    case 'table': return 'primary'
    case 'chart': return 'success'
    case 'text': return 'info'
    case 'list': return 'warning'
    default: return ''
  }
}

const getSectionTypeName = (type: string) => {
  switch (type) {
    case 'table': return '表格'
    case 'chart': return '图表'
    case 'text': return '文本'
    case 'list': return '列表'
    default: return '未知'
  }
}

const addSection = () => {
  templateForm.sections.push({
    title: `部分 ${templateForm.sections.length + 1}`,
    type: 'table',
    config: {}
  })
}

const removeSection = (index: number) => {
  templateForm.sections.splice(index, 1)
}

const resetTemplateForm = () => {
  Object.assign(templateForm, {
    name: '',
    description: '',
    config: {
      title: '',
      description: ''
    },
    sections: []
  })
}

const showCreateTemplateDialog = () => {
  resetTemplateForm()
  createDialogVisible.value = true
}

const showEditTemplateDialog = (template: any) => {
  Object.assign(templateForm, {
    name: template.name,
    description: template.description,
    config: { ...template.config },
    sections: JSON.parse(JSON.stringify(template.sections))
  })
  selectedTemplate.value = template
  editDialogVisible.value = true
}

const showPreviewTemplateDialog = (template: any) => {
  previewTemplate.value = JSON.parse(JSON.stringify(template))
  previewDialogVisible.value = true
}

const saveTemplate = async () => {
  try {
    const valid = await templateFormRef.value.validate()
    if (!valid) return

    const templateData = {
      ...templateForm,
      sections: templateForm.sections.map(section => ({
        ...section,
        data: []
      }))
    }

    const newTemplate = reportGenerator.createTemplate(templateData)
    templates.value.push(newTemplate)
    
    createDialogVisible.value = false
    resetTemplateForm()
    ElMessage.success('模板创建成功')
  } catch (error) {
    console.error('保存模板失败:', error)
    ElMessage.error('保存模板失败')
  }
}

const updateTemplate = async () => {
  try {
    const valid = await templateFormRef.value.validate()
    if (!valid) return

    const templateData = {
      ...templateForm,
      sections: templateForm.sections.map(section => ({
        ...section,
        data: []
      }))
    }

    const updated = reportGenerator.updateTemplate(selectedTemplate.value.id, templateData)
    if (updated) {
      const index = templates.value.findIndex(t => t.id === selectedTemplate.value.id)
      if (index !== -1) {
        templates.value[index] = updated
      }
    }

    editDialogVisible.value = false
    resetTemplateForm()
    ElMessage.success('模板更新成功')
  } catch (error) {
    console.error('更新模板失败:', error)
    ElMessage.error('更新模板失败')
  }
}

const editTemplate = (template: any) => {
  showEditTemplateDialog(template)
}

const duplicateTemplate = (template: any) => {
  const duplicate = {
    ...template,
    name: `${template.name} (副本)`,
    description: `${template.description} - 复制`,
    id: undefined
  }
  showCreateTemplateDialog()
  Object.assign(templateForm, {
    name: duplicate.name,
    description: duplicate.description,
    config: { ...duplicate.config },
    sections: JSON.parse(JSON.stringify(duplicate.sections))
  })
}

const deleteTemplate = async (template: any) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除模板"${template.name}"吗？删除后将无法恢复。`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const success = reportGenerator.deleteTemplate(template.id)
    if (success) {
      templates.value = templates.value.filter(t => t.id !== template.id)
      ElMessage.success('模板删除成功')
    } else {
      ElMessage.error('模板删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除模板失败:', error)
      ElMessage.error('模板删除失败')
    }
  }
}

const selectTemplate = (template: any) => {
  showPreviewTemplateDialog(template)
}

const getSampleColumns = (section: any) => {
  if (section.type === 'table') {
    const sampleColumns = [
      { prop: 'name', label: '名称' },
      { prop: 'value', label: '数值' },
      { prop: 'status', label: '状态' }
    ]
    return sampleColumns
  }
  return []
}

const generateReportFromTemplate = async (template: any) => {
  previewDialogVisible.value = false
  
  try {
    ElMessage.info('正在生成报表数据...')
    
    const mockData = {
      dateRange: {
        start: new Date(),
        end: new Date()
      },
      [template.sections[0]?.title]: [
        { name: '数据1', value: 100, status: '活跃' },
        { name: '数据2', value: 200, status: '待处理' },
        { name: '数据3', value: 150, status: '已完成' }
      ]
    }

    const report = reportGenerator.generateReport(template.id, mockData)
    
    const exportDialog = window.open('', '_blank')
    if (exportDialog) {
      exportDialog.document.write(`
        <html>
          <head>
            <title>${report.config.title}</title>
            <style>
              body { font-family: Arial, sans-serif; margin: 20px; }
              h1 { color: #333; }
              table { border-collapse: collapse; width: 100%; }
              th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
              th { background-color: #f2f2f2; }
            </style>
          </head>
          <body>
            <h1>${report.config.title}</h1>
            <p>${report.config.description}</p>
            <p>生成时间: ${new Date().toLocaleString()}</p>
            <table>
              <tr>
                <th>示例数据</th>
                <th>数值</th>
                <th>状态</th>
              </tr>
              <tr>
                <td>数据1</td>
                <td>100</td>
                <td>活跃</td>
              </tr>
              <tr>
                <td>数据2</td>
                <td>200</td>
                <td>待处理</td>
              </tr>
              <tr>
                <td>数据3</td>
                <td>150</td>
                <td>已完成</td>
              </tr>
            </table>
          </body>
        </html>
      `)
    }
    
    ElMessage.success('报表生成成功！')
  } catch (error) {
    console.error('生成报表失败:', error)
    ElMessage.error('生成报表失败')
  }
}

onMounted(() => {
  templates.value = reportGenerator.getAllTemplates()
})
</script>

<style scoped>
.report-templates {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.templates-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.template-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.template-card:hover {
  transform: translateY(-2px);
}

.card-item {
  height: 100%;
}

.template-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.template-description {
  color: #666;
  margin-bottom: 15px;
  font-size: 14px;
}

.template-sections {
  margin-bottom: 15px;
}

.section-item {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
  padding: 8px;
  background: #f5f5f5;
  border-radius: 4px;
}

.section-item .el-icon {
  margin-right: 5px;
}

.template-actions {
  text-align: right;
}

.empty-state {
  text-align: center;
  padding: 40px 0;
}

.sections-list {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 15px;
  background: #fafafa;
}

.section-item {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  margin-bottom: 15px;
  padding: 15px;
  background: white;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  font-weight: bold;
}

.section-content {
  padding-left: 20px;
}

.report-preview-dialog {
  .report-preview-content {
    max-height: 80vh;
    overflow-y: auto;
  }

  .report-header {
    text-align: center;
    margin-bottom: 30px;
    padding-bottom: 20px;
    border-bottom: 2px solid #e4e7ed;
  }

  .report-description {
    color: #666;
    margin: 10px 0;
  }

  .report-section {
    margin-bottom: 40px;
  }

  .section-preview {
    padding: 20px;
    background: #f8f9fa;
    border-radius: 8px;
    border: 1px solid #e9ecef;
  }

  .table-preview {
    background: white;
    border-radius: 4px;
  }

  .chart-placeholder,
  .text-placeholder,
  .list-placeholder {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    min-height: 200px;
    background: white;
    border: 2px dashed #dcdfe6;
    border-radius: 4px;
    padding: 20px;
  }

  .chart-placeholder .el-icon,
  .text-placeholder .el-icon {
    margin-bottom: 10px;
    color: #909399;
  }

  .list-placeholder {
    align-items: flex-start;
  }

  .list-item-tag {
    margin: 5px;
  }
}
</style>