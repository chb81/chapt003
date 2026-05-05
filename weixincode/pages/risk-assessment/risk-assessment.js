const volunteer = require('../../api/volunteer')

Page({
  data: {
    plans: [],
    planNames: [],
    selectedPlanIndex: 0,
    loading: false,
    riskData: null,
    riskLevelText: ''
  },

  onLoad() {
    this.loadPlans()
  },

  async loadPlans() {
    try {
      const response = await volunteer.getVolunteerApplications({ page: 0, size: 50 })
      const data = response.data || response
      const plans = data.applications || data.content || data || []
      const planNames = plans.map(p => p.name || ('方案' + (p.id || '')))
      this.setData({ plans, planNames })
      if (plans.length > 0) {
        this.assessRisk(plans[0].id)
      }
    } catch (error) {
      console.error('加载方案失败:', error)
    }
  },

  onPlanChange(e) {
    const index = e.detail.value
    this.setData({ selectedPlanIndex: index })
    if (this.data.plans[index]) {
      this.assessRisk(this.data.plans[index].id)
    }
  },

  async assessRisk(planId) {
    this.setData({ loading: true, riskData: null })
    try {
      const response = await volunteer.getRiskAssessment(planId)
      const data = response.data || response
      const riskLevelText = { LOW: '低风险', MEDIUM: '中等风险', HIGH: '高风险' }
      this.setData({
        riskData: data,
        riskLevelText: riskLevelText[data.riskLevel] || data.riskLevel,
        loading: false
      })
    } catch (error) {
      console.error('风险评估失败:', error)
      wx.showToast({ title: '风险评估失败', icon: 'none' })
      this.setData({ loading: false })
    }
  },

  onShareAppMessage() {
    return {
      title: '中考志愿填报 - 风险评估',
      path: '/pages/index/index'
    }
  }
})
