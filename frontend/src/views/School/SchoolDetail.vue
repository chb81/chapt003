<template>
  <div class="school-detail" v-loading="loading">
    <el-page-header @back="$router.back()" title="返回列表" :content="school.name || '学校详情'" />

    <el-card v-if="school.id" style="margin-top: 20px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="学校名称">{{ school.name }}</el-descriptions-item>
        <el-descriptions-item label="学校类型">
          <el-tag :type="school.type === 'KEY' ? 'danger' : 'info'">
            {{ school.type === 'KEY' ? '重点高中' : school.type === 'REGULAR' ? '普通高中' : '职业高中' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="所在城市">{{ school.city }}</el-descriptions-item>
        <el-descriptions-item label="所在区县">{{ school.district }}</el-descriptions-item>
        <el-descriptions-item label="学校地址">{{ school.address || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ school.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="最低分数线">{{ school.minScore || '-' }}</el-descriptions-item>
        <el-descriptions-item label="招生计划">{{ school.enrollmentPlan || '-' }}人</el-descriptions-item>
        <el-descriptions-item label="学费标准">{{ school.tuitionFee ? school.tuitionFee + '元/年' : '-' }}</el-descriptions-item>
        <el-descriptions-item label="住宿情况">{{ school.accommodation || '-' }}</el-descriptions-item>
      </el-descriptions>

      <div v-if="school.description" style="margin-top: 20px">
        <h4>学校简介</h4>
        <p class="school-desc">{{ school.description }}</p>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getSchoolDetail } from '@/api/school'

const route = useRoute()
const loading = ref(false)
const school = ref<any>({})

onMounted(async () => {
  const id = Number(route.params.id)
  if (!id) return
  loading.value = true
  try {
    const res = await getSchoolDetail(id)
    school.value = res.data || {}
  } catch (e: any) {
    school.value = {}
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.school-desc {
  line-height: 1.8;
  color: #666;
  text-indent: 2em;
}
</style>