<template>
  <div v-if="visible" class="onboarding-overlay">
    <div class="onboarding-dialog">
      <div class="onboarding-header">
        <h2>欢迎使用中考志愿填报系统</h2>
        <p class="subtitle">3步快速开始，科学填报志愿</p>
      </div>

      <div class="steps-container">
        <div class="step-item" :class="{ active: currentStep === 1, completed: status?.step1ScoreEntered }">
          <div class="step-number">
            <span v-if="status?.step1ScoreEntered">✓</span>
            <span v-else>1</span>
          </div>
          <div class="step-content">
            <h3>录入成绩</h3>
            <p>输入您孩子的中考各科成绩</p>
          </div>
          <el-button v-if="!status?.step1ScoreEntered" type="primary" size="small" @click="goTo('/student-profile')">
            去录入
          </el-button>
          <el-tag v-else type="success" size="small">已完成</el-tag>
        </div>

        <div class="step-item" :class="{ active: currentStep === 2, completed: status?.step2RecommendationViewed }">
          <div class="step-number">
            <span v-if="status?.step2RecommendationViewed">✓</span>
            <span v-else>2</span>
          </div>
          <div class="step-content">
            <h3>查看推荐</h3>
            <p>获取基于成绩的智能学校推荐</p>
          </div>
          <el-button v-if="!status?.step2RecommendationViewed && status?.step1ScoreEntered" type="primary" size="small" @click="goTo('/recommendation')">
            去查看
          </el-button>
          <el-tag v-else-if="status?.step2RecommendationViewed" type="success" size="small">已完成</el-tag>
          <el-tag v-else type="info" size="small">请先完成步骤1</el-tag>
        </div>

        <div class="step-item" :class="{ active: currentStep === 3, completed: status?.step3PlanCreated }">
          <div class="step-number">
            <span v-if="status?.step3PlanCreated">✓</span>
            <span v-else>3</span>
          </div>
          <div class="step-content">
            <h3>创建方案</h3>
            <p>创建您的第一个志愿填报方案</p>
          </div>
          <el-button v-if="!status?.step3PlanCreated && status?.step2RecommendationViewed" type="primary" size="small" @click="goTo('/volunteer')">
            去创建
          </el-button>
          <el-tag v-else-if="status?.step3PlanCreated" type="success" size="small">已完成</el-tag>
          <el-tag v-else type="info" size="small">请先完成步骤2</el-tag>
        </div>
      </div>

      <div class="progress-bar">
        <div class="progress-fill" :style="{ width: progressPercent + '%' }"></div>
      </div>
      <p class="progress-text">{{ status?.stepCompleted || 0 }}/3 步已完成</p>

      <div class="onboarding-footer">
        <el-button @click="skip">稍后再说</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getOnboardingStatus, completeOnboardingStep } from '@/api/userExperience'

const router = useRouter()
const visible = ref(false)
const status = ref<any>(null)

const currentStep = computed(() => {
  if (!status.value) return 1
  if (!status.value.step1ScoreEntered) return 1
  if (!status.value.step2RecommendationViewed) return 2
  if (!status.value.step3PlanCreated) return 3
  return 4
})

const progressPercent = computed(() => ((status.value?.stepCompleted || 0) / 3) * 100)

onMounted(async () => {
  const dismissed = localStorage.getItem('onboarding_dismissed')
  if (dismissed) return
  try {
    const res = await getOnboardingStatus()
    status.value = res.data
    if (!status.value?.onboardingCompleted) {
      visible.value = true
    }
  } catch (e) { /* ignore */ }
})

async function goTo(path: string) {
  const stepMap: Record<string, number> = {
    '/student-profile': 1,
    '/recommendation': 2,
    '/volunteer': 3
  }
  const step = stepMap[path]
  if (step) {
    try { await completeOnboardingStep(step) } catch (e) { /* ignore */ }
  }
  visible.value = false
  router.push(path)
}

function skip() {
  visible.value = false
  localStorage.setItem('onboarding_dismissed', 'true')
}
</script>

<style scoped>
.onboarding-overlay {
  position: fixed; top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.5); z-index: 9999;
  display: flex; align-items: center; justify-content: center;
}
.onboarding-dialog {
  background: #fff; border-radius: 16px; padding: 40px;
  max-width: 520px; width: 90%; box-shadow: 0 20px 60px rgba(0,0,0,0.3);
}
.onboarding-header { text-align: center; margin-bottom: 32px; }
.onboarding-header h2 { font-size: 22px; color: #303133; margin-bottom: 8px; }
.subtitle { color: #909399; font-size: 14px; }
.steps-container { display: flex; flex-direction: column; gap: 20px; margin-bottom: 24px; }
.step-item {
  display: flex; align-items: center; gap: 16px; padding: 16px;
  border: 2px solid #e8e8e8; border-radius: 12px; transition: all 0.3s;
}
.step-item.active { border-color: #409eff; background: #f0f7ff; }
.step-item.completed { border-color: #67c23a; background: #f0f9eb; }
.step-number {
  width: 36px; height: 36px; border-radius: 50%;
  background: #e8e8e8; display: flex; align-items: center; justify-content: center;
  font-weight: bold; color: #fff; flex-shrink: 0;
}
.step-item.active .step-number { background: #409eff; }
.step-item.completed .step-number { background: #67c23a; }
.step-content { flex: 1; }
.step-content h3 { font-size: 15px; color: #303133; margin-bottom: 4px; }
.step-content p { font-size: 13px; color: #909399; }
.progress-bar { height: 6px; background: #e8e8e8; border-radius: 3px; overflow: hidden; margin-bottom: 8px; }
.progress-fill { height: 100%; background: #409eff; transition: width 0.5s; }
.progress-text { text-align: center; font-size: 13px; color: #909399; margin-bottom: 16px; }
.onboarding-footer { text-align: center; }
</style>
