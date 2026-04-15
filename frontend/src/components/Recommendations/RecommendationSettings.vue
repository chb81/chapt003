<template>
  <div class="recommendation-settings">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>推荐偏好设置</span>
          <el-button 
            type="primary" 
            size="small" 
            @click="saveSettings"
            :loading="saving"
          >
            保存设置
          </el-button>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="settings"
        :rules="rules"
        label-width="120px"
      >
        <el-form-item label="活动类型偏好" prop="activityTypes">
          <el-checkbox-group v-model="settings.activityTypes">
            <el-checkbox 
              v-for="type in activityTypes" 
              :key="type.value"
              :label="type.value"
            >
              {{ type.label }}
            </el-checkbox>
          </el-checkbox-group>
          <div class="help-text">
            选择您感兴趣的活动类型，系统会基于您的偏好进行推荐
          </div>
        </el-form-item>

        <el-form-item label="活动地点" prop="locations">
          <el-select
            v-model="settings.locations"
            multiple
            filterable
            placeholder="选择您的活动地点偏好"
            style="width: 100%"
          >
            <el-option
              v-for="location in locations"
              :key="location"
              :label="location"
              :value="location"
            />
          </el-select>
          <div class="help-text">
            系统会优先推荐您所在地区的活动
          </div>
        </el-form-item>

        <el-form-item label="时间偏好" prop="timeSlots">
          <el-time-select
            v-model="settings.timeSlots"
            multiple
            placeholder="选择您空闲的时间段"
            style="width: 100%"
            :max-time="21"
          >
            <template v-slot:default="{ selected, options }">
              <el-option
                v-for="option in options"
                :key="option.value"
                :label="option.label"
                :value="option.value"
                :disabled="isTimeSlotDisabled(option.value)"
              />
            </template>
          </el-time-select>
          <div class="help-text">
            选择您通常空闲的时间段，有助于推荐更合适的时间安排
          </div>
        </el-form-item>

        <el-form-item label="技能要求" prop="skillRequirements">
          <el-select
            v-model="settings.skillRequirements"
            multiple
            filterable
            placeholder="选择您的技能专长"
            style="width: 100%"
          >
            <el-option
              v-for="skill in skills"
              :key="skill"
              :label="skill"
              :value="skill"
            />
          </el-select>
          <div class="help-text">
            选择您具备的技能，系统会推荐需要相应技能的活动
          </div>
        </el-form-item>

        <el-divider>时间设置</el-divider>

        <el-form-item label="最短时长" prop="minDuration">
          <el-input-number
            v-model="settings.minDuration"
            :min="30"
            :max="480"
            :step="30"
            placeholder="最短参与时长(分钟)"
          >
            <template #suffix>分钟</template>
          </el-input-number>
          <div class="help-text">
            设置您愿意参与的最短活动时长
          </div>
        </el-form-item>

        <el-form-item label="最长时长" prop="maxDuration">
          <el-input-number
            v-model="settings.maxDuration"
            :min="60"
            :max="480"
            :step="30"
            placeholder="最长参与时长(分钟)"
          >
            <template #suffix>分钟</template>
          </el-input-number>
          <div class="help-text">
            设置您愿意参与的最长活动时长
          </div>
        </el-form-item>

        <el-form-item label="最大距离" prop="maxDistance">
          <el-input-number
            v-model="settings.maxDistance"
            :min="1"
            :max="100"
            placeholder="最大活动距离(公里)"
          >
            <template #suffix>公里</template>
          </el-input-number>
          <div class="help-text">
            设置您愿意参与活动的最大距离范围
          </div>
        </el-form-item>

        <el-form-item label="偏好日期" prop="preferredDays">
          <el-checkbox-group v-model="settings.preferredDays">
            <el-checkbox 
              v-for="day in weekDays" 
              :key="day.value"
              :label="day.value"
            >
              {{ day.label }}
            </el-checkbox>
          </el-checkbox-group>
          <div class="help-text">
            选择您参与活动的偏好日期
          </div>
        </el-form-item>

        <el-divider>推荐权重设置</el-divider>

        <div class="weight-settings">
          <el-alert
            title="推荐权重说明"
            type="info"
            :closable="false"
            style="margin-bottom: 20px"
          >
            <p>调整各项因素的权重，系统会根据您的设置调整推荐算法。总权重必须为100%。</p>
          </el-alert>

          <el-form-item 
            label="活动类型权重" 
            prop="weights.type"
          >
            <div class="weight-control">
              <el-slider
                v-model="weights.type"
                :min="0"
                :max="100"
                :step="5"
                show-input
                @change="updateWeights"
              />
              <span class="weight-value">{{ weights.type }}%</span>
            </div>
          </el-form-item>

          <el-form-item 
            label="地点权重" 
            prop="weights.location"
          >
            <div class="weight-control">
              <el-slider
                v-model="weights.location"
                :min="0"
                :max="100"
                :step="5"
                show-input
                @change="updateWeights"
              />
              <span class="weight-value">{{ weights.location }}%</span>
            </div>
          </el-form-item>

          <el-form-item 
            label="时间权重" 
            prop="weights.time"
          >
            <div class="weight-control">
              <el-slider
                v-model="weights.time"
                :min="0"
                :max="100"
                :step="5"
                show-input
                @change="updateWeights"
              />
              <span class="weight-value">{{ weights.time }}%</span>
            </div>
          </el-form-item>

          <el-form-item 
            label="技能权重" 
            prop="weights.skill"
          >
            <div class="weight-control">
              <el-slider
                v-model="weights.skill"
                :min="0"
                :max="100"
                :step="5"
                show-input
                @change="updateWeights"
              />
              <span class="weight-value">{{ weights.skill }}%</span>
            </div>
          </el-form-item>

          <el-form-item 
            label="历史权重" 
            prop="weights.history"
          >
            <div class="weight-control">
              <el-slider
                v-model="weights.history"
                :min="0"
                :max="100"
                :step="5"
                show-input
                @change="updateWeights"
              />
              <span class="weight-value">{{ weights.history }}%</span>
            </div>
          </el-form-item>

          <el-form-item 
            label="可用性权重" 
            prop="weights.availability"
          >
            <div class="weight-control">
              <el-slider
                v-model="weights.availability"
                :min="0"
                :max="100"
                :step="5"
                show-input
                @change="updateWeights"
              />
              <span class="weight-value">{{ weights.availability }}%</span>
            </div>
          </el-form-item>

          <el-alert
            v-if="totalWeight !== 100"
            :title="`总权重：${totalWeight}%`"
            :type="totalWeight === 100 ? 'success' : 'warning'"
            :closable="false"
          >
            {{ totalWeight === 100 ? '权重设置正确' : '总权重必须为100%，请调整权重设置' }}
          </el-alert>
        </div>
      </el-form>
    </el-card>

    <el-card class="stats-card">
      <template #header>
        <span>推荐统计信息</span>
      </template>

      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-number">{{ stats.totalActivities }}</div>
            <div class="stat-label">总活动数</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-number">{{ stats.recommendedCount }}</div>
            <div class="stat-label">推荐活动数</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-number">{{ stats.averageScore }}</div>
            <div class="stat-label">平均匹配度</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-number">{{ stats.topReasons.length }}</div>
            <div class="stat-label">推荐理由数</div>
          </div>
        </el-col>
      </el-row>

      <div v-if="stats.topReasons.length > 0" class="top-reasons">
        <h4>主要推荐理由：</h4>
        <div class="reasons-list">
          <el-tag
            v-for="(reason, index) in stats.topReasons"
            :key="index"
            size="small"
            class="reason-tag"
          >
            {{ reason }}
          </el-tag>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { recommendationEngine } from '@/utils/recommendation'
import { apiService } from '@/utils/api'

const formRef = ref()
const saving = ref(false)

const settings = ref({
  activityTypes: [],
  locations: [],
  timeSlots: [],
  skillRequirements: [],
  minDuration: 60,
  maxDuration: 240,
  maxDistance: 10,
  preferredDays: [1, 2, 3, 4, 5, 6, 7]
})

const weights = ref({
  type: 25,
  location: 20,
  time: 20,
  skill: 15,
  history: 15,
  availability: 5
})

const totalWeight = computed(() => {
  return Object.values(weights.value).reduce((sum, weight) => sum + weight, 0)
})

const stats = ref({
  totalActivities: 0,
  recommendedCount: 0,
  averageScore: 0,
  topReasons: []
})

const activityTypes = [
  { value: 'environmental', label: '环保活动' },
  { value: 'education', label: '教育支持' },
  { value: 'health', label: '健康医疗' },
  { value: 'elderly', label: '养老服务' },
  { value: 'children', label: '儿童关爱' },
  { value: 'animal', label: '动物保护' },
  { value: 'community', label: '社区服务' },
  { value: 'culture', label: '文化传播' }
]

const weekDays = [
  { value: 1, label: '周一' },
  { value: 2, label: '周二' },
  { value: 3, label: '周三' },
  { value: 4, label: '周四' },
  { value: 5, label: '周五' },
  { value: 6, label: '周六' },
  { value: 7, label: '周日' }
]

const locations = [
  '北京市朝阳区',
  '北京市海淀区',
  '北京市西城区',
  '北京市东城区',
  '北京市丰台区',
  '北京市石景山区',
  '北京市通州区',
  '北京市昌平区'
]

const skills = [
  '教学辅导',
  '医疗护理',
  '计算机技能',
  '外语能力',
  '艺术表演',
  '体育运动',
  '心理咨询',
  '法律援助',
  '财务会计',
  '项目管理',
  '活动组织',
  '写作翻译'
]

const rules = {
  activityTypes: [
    { type: 'array', required: true, message: '请选择活动类型偏好' }
  ],
  locations: [
    { type: 'array', required: true, message: '请选择活动地点' }
  ],
  timeSlots: [
    { type: 'array', required: true, message: '请选择时间偏好' }
  ],
  skillRequirements: [
    { type: 'array', required: true, message: '请选择技能要求' }
  ],
  minDuration: [
    { type: 'number', required: true, message: '请设置最短时长' },
    { 
      validator: (rule: any, value: any, callback: any) => {
        if (value > settings.value.maxDuration) {
          callback(new Error('最短时长不能大于最长时长'))
        } else {
          callback()
        }
      }
    }
  ],
  maxDuration: [
    { type: 'number', required: true, message: '请设置最长时长' }
  ],
  maxDistance: [
    { type: 'number', required: true, message: '请设置最大距离' }
  ]
}

const isTimeSlotDisabled = (time: string) => {
  const timeNum = parseInt(time)
  return settings.value.timeSlots.includes(time)
}

const updateWeights = () => {
  if (totalWeight.value !== 100) {
    return
  }
  
  recommendationEngine.updateWeights({
    type: weights.value.type / 100,
    location: weights.value.location / 100,
    time: weights.value.time / 100,
    skill: weights.value.skill / 100,
    history: weights.value.history / 100,
    availability: weights.value.availability / 100
  })
}

const saveSettings = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    if (totalWeight.value !== 100) {
      ElMessage.warning('总权重必须为100%，请调整权重设置')
      return
    }
    
    saving.value = true
    
    const userPreference = {
      userId: props.userId,
      activityTypes: settings.value.activityTypes,
      locations: settings.value.locations,
      timeSlots: settings.value.timeSlots,
      skillRequirements: settings.value.skillRequirements,
      preferences: {
        minDuration: settings.value.minDuration,
        maxDuration: settings.value.maxDuration,
        maxDistance: settings.value.maxDistance,
        preferredDays: settings.value.preferredDays
      },
      history: {
        joinedActivities: [],
        likedActivities: [],
        dislikedActivities: []
      },
      scores: {
        typeScore: 0,
        locationScore: 0,
        timeScore: 0,
        skillScore: 0,
        historyScore: 0
      }
    }
    
    recommendationEngine.setUserPreference(props.userId, userPreference)
    updateWeights()
    updateStats()
    
    ElMessage.success('推荐设置已保存')
  } catch (error) {
    console.error('保存设置失败:', error)
    ElMessage.error('保存设置失败，请检查表单填写')
  } finally {
    saving.value = false
  }
}

const updateStats = () => {
  const currentStats = recommendationEngine.getRecommendationStats(props.userId)
  stats.value = currentStats
}

const loadSettings = async () => {
  try {
    const currentSettings = await apiService.getUserSettings(props.userId)
    if (currentSettings) {
      settings.value = currentSettings
    }
    
    updateStats()
  } catch (error) {
    console.error('加载设置失败:', error)
  }
}

onMounted(() => {
  loadSettings()
})
</script>

<style scoped>
.recommendation-settings {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 10px;
}

.help-text {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
  line-height: 1.4;
}

.weight-settings {
  margin-top: 20px;
}

.weight-control {
  display: flex;
  align-items: center;
  gap: 12px;
}

.weight-value {
  min-width: 40px;
  text-align: center;
  font-weight: bold;
  color: #409eff;
}

.stats-card {
  margin-top: 20px;
}

.stat-item {
  text-align: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}

.top-reasons {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e4e7ed;
}

.top-reasons h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.reasons-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.reason-tag {
  background: #ecf5ff;
  color: #409eff;
  border-color: #b3d8ff;
}
</style>