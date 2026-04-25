<template>
  <div class="system-config-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>系统参数配置</h2>
          <el-button type="primary" @click="showCreateDialog">新增配置</el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="configList" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="configKey" label="配置键" width="200" />
        <el-table-column prop="configValue" label="配置值" min-width="250" show-overflow-tooltip />
        <el-table-column prop="description" label="描述" width="200" show-overflow-tooltip />
        <el-table-column prop="updatedAt" label="更新时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.updatedAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="showEditDialog(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑配置' : '新增配置'" width="500px">
      <el-form :model="formData" label-width="80px">
        <el-form-item label="配置键" required>
          <el-input v-model="formData.configKey" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="配置值" required>
          <el-input v-model="formData.configValue" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSystemConfigs, saveSystemConfig, deleteSystemConfig } from '@/api/admin'

const loading = ref(false)
const configList = ref<any[]>([])
const dialogVisible = ref(false)
const isEdit = ref(false)

const formData = reactive({
  configKey: '',
  configValue: '',
  description: ''
})

const loadConfigs = async () => {
  loading.value = true
  try {
    const res = await getSystemConfigs()
    configList.value = (res.data || res) || []
  } catch {
    ElMessage.error('加载配置失败')
    configList.value = []
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  formData.configKey = ''
  formData.configValue = ''
  formData.description = ''
}

const showCreateDialog = () => {
  resetForm()
  isEdit.value = false
  dialogVisible.value = true
}

const showEditDialog = (row: any) => {
  formData.configKey = row.configKey
  formData.configValue = row.configValue
  formData.description = row.description || ''
  isEdit.value = true
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formData.configKey || !formData.configValue) {
    ElMessage.warning('配置键和配置值不能为空')
    return
  }
  try {
    await saveSystemConfig(formData)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadConfigs()
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败')
  }
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(`确定删除配置 "${row.configKey}"？`, '确认删除', { type: 'warning' })
    await deleteSystemConfig(row.id)
    ElMessage.success('删除成功')
    loadConfigs()
  } catch (error: any) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

const formatDate = (date: string) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

onMounted(() => { loadConfigs() })
</script>

<style scoped>
.system-config-container { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-header h2 { margin: 0; font-size: 20px; font-weight: 600; }
</style>
