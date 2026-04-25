<template>
  <el-dialog
    v-model="visible"
    :title="title"
    :width="width"
    :close-on-click-modal="!danger"
    :close-on-press-escape="!danger"
    @close="handleClose"
  >
    <div class="confirm-dialog-content">
      <el-icon v-if="danger" class="confirm-icon danger"><WarningFilled /></el-icon>
      <el-icon v-else-if="type === 'success'" class="confirm-icon success"><SuccessFilled /></el-icon>
      <el-icon v-else class="confirm-icon info"><InfoFilled /></el-icon>
      <span>{{ message }}</span>
    </div>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button
        :type="danger ? 'danger' : 'primary'"
        :loading="confirmLoading"
        @click="handleConfirm"
      >
        {{ confirmText }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { WarningFilled, SuccessFilled, InfoFilled } from '@element-plus/icons-vue'

const props = withDefaults(defineProps<{
  modelValue: boolean
  title?: string
  message: string
  type?: 'info' | 'success' | 'warning' | 'danger'
  danger?: boolean
  confirmText?: string
  width?: string
  confirmLoading?: boolean
}>(), {
  title: '确认操作',
  type: 'info',
  danger: false,
  confirmText: '确定',
  width: '420px',
  confirmLoading: false
})

const emit = defineEmits<{
  (e: 'update:modelValue', val: boolean): void
  (e: 'confirm'): void
  (e: 'cancel'): void
}>()

const visible = ref(props.modelValue)

watch(() => props.modelValue, (val) => {
  visible.value = val
})

const handleClose = () => {
  visible.value = false
  emit('update:modelValue', false)
  emit('cancel')
}

const handleConfirm = () => {
  emit('confirm')
}
</script>

<style scoped>
.confirm-dialog-content {
  display: flex;
  align-items: center;
  gap: 12px;
}
.confirm-icon {
  font-size: 24px;
  flex-shrink: 0;
}
.confirm-icon.danger { color: #f56c6c; }
.confirm-icon.success { color: #67c23a; }
.confirm-icon.info { color: #409eff; }
</style>
