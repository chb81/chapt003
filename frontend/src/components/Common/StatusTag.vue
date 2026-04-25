<template>
  <el-tag :type="tagType" :size="size">
    {{ displayText }}
  </el-tag>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(defineProps<{
  status: string
  statusMap?: Record<string, { text: string; type: string }>
  size?: 'large' | 'default' | 'small'
}>(), {
  size: 'default'
})

const defaultMap: Record<string, { text: string; type: string }> = {
  ACTIVE: { text: '正常', type: 'success' },
  VERIFIED: { text: '已验证', type: 'success' },
  UNVERIFIED: { text: '未验证', type: 'warning' },
  DISABLED: { text: '禁用', type: 'danger' },
  DELETED: { text: '已删除', type: 'info' },
  ADMIN: { text: '管理员', type: 'danger' },
  USER: { text: '普通用户', type: '' },
  SUBMITTED: { text: '已提交', type: 'success' },
  DRAFT: { text: '草稿', type: 'info' },
  SIMULATION: { text: '模拟', type: 'warning' }
}

const mergedMap = computed(() => ({
  ...defaultMap,
  ...props.statusMap
}))

const tagType = computed(() => {
  return mergedMap.value[props.status]?.type || ''
})

const displayText = computed(() => {
  return mergedMap.value[props.status]?.text || props.status
})
</script>
