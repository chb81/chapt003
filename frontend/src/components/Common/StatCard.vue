<template>
  <el-card class="stat-card" :shadow="shadow" :body-style="{ padding: '20px' }">
    <div class="stat-card-content">
      <div class="stat-card-info">
        <div class="stat-card-label">{{ label }}</div>
        <div class="stat-card-value" :style="{ color: valueColor }">
          {{ prefix }}{{ formattedValue }}{{ suffix }}
        </div>
        <div v-if="subText" class="stat-card-sub">{{ subText }}</div>
      </div>
      <div v-if="icon" class="stat-card-icon" :style="{ backgroundColor: iconBgColor }">
        <el-icon :size="28" :color="iconColor">
          <component :is="icon" />
        </el-icon>
      </div>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { Component } from 'vue'

const props = withDefaults(defineProps<{
  label: string
  value: number | string
  prefix?: string
  suffix?: string
  subText?: string
  icon?: Component
  iconColor?: string
  iconBgColor?: string
  valueColor?: string
  shadow?: 'always' | 'hover' | 'never'
  format?: boolean
}>(), {
  prefix: '',
  suffix: '',
  iconColor: '#409eff',
  iconBgColor: '#ecf5ff',
  valueColor: '#303133',
  shadow: 'hover',
  format: false
})

const formattedValue = computed(() => {
  if (props.format && typeof props.value === 'number') {
    return props.value.toLocaleString('zh-CN')
  }
  return props.value
})
</script>

<style scoped>
.stat-card-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.stat-card-info {
  flex: 1;
}
.stat-card-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}
.stat-card-value {
  font-size: 28px;
  font-weight: 700;
  line-height: 1.2;
}
.stat-card-sub {
  font-size: 12px;
  color: #909399;
  margin-top: 6px;
}
.stat-card-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
