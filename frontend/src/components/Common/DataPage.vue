<template>
  <div class="data-page-container">
    <el-card>
      <template #header>
        <slot name="header">
          <PageHeader :title="title">
            <template #actions>
              <slot name="header-actions" />
            </template>
          </PageHeader>
        </slot>
      </template>

      <div v-if="$slots.filters" class="data-page-filters">
        <el-form :inline="true" class="search-form">
          <slot name="filters" />
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table
        v-loading="loading"
        :data="data"
        stripe
        style="width: 100%"
        @selection-change="$emit('selection-change', $event)"
        @sort-change="$emit('sort-change', $event)"
      >
        <slot />
      </el-table>

      <el-pagination
        v-if="showPagination"
        v-model:current-page="currentPageModel"
        v-model:page-size="pageSizeModel"
        :total="total"
        :page-sizes="pageSizes"
        layout="total, sizes, prev, pager, next, jumper"
        class="data-page-pagination"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import PageHeader from './PageHeader.vue'

const props = withDefaults(defineProps<{
  title?: string
  data: any[]
  loading?: boolean
  total?: number
  currentPage?: number
  pageSize?: number
  pageSizes?: number[]
  showPagination?: boolean
}>(), {
  title: '',
  loading: false,
  total: 0,
  currentPage: 1,
  pageSize: 20,
  pageSizes: () => [10, 20, 50, 100],
  showPagination: true
})

const emit = defineEmits<{
  (e: 'search'): void
  (e: 'reset'): void
  (e: 'page-change', page: number): void
  (e: 'size-change', size: number): void
  (e: 'selection-change', selection: any[]): void
  (e: 'sort-change', sortInfo: any): void
  (e: 'update:currentPage', page: number): void
  (e: 'update:pageSize', size: number): void
}>()

const currentPageModel = computed({
  get: () => props.currentPage,
  set: (val) => emit('update:currentPage', val)
})

const pageSizeModel = computed({
  get: () => props.pageSize,
  set: (val) => emit('update:pageSize', val)
})

const handleSearch = () => emit('search')
const handleReset = () => emit('reset')
const handleSizeChange = (size: number) => {
  emit('update:pageSize', size)
  emit('size-change', size)
}
const handlePageChange = (page: number) => {
  emit('page-change', page)
}
</script>

<style scoped>
.data-page-container { padding: 20px; }
.search-form { margin-bottom: 20px; }
.data-page-pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
