const volunteer = require('../../api/volunteer')

Page({
  data: {
    plans: [],
    planNames: [],
    planAIndex: 0,
    planBIndex: 1,
    loading: false,
    comparisonData: null
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
      const planBIndex = plans.length > 1 ? 1 : 0
      this.setData({ plans, planNames, planBIndex })
    } catch (error) {
      console.error('加载方案失败:', error)
    }
  },

  onPlanAChange(e) {
    this.setData({ planAIndex: e.detail.value })
  },

  onPlanBChange(e) {
    this.setData({ planBIndex: e.detail.value })
  },

  async doCompare() {
    const { plans, planAIndex, planBIndex } = this.data
    if (!plans[planAIndex] || !plans[planBIndex]) {
      wx.showToast({ title: '请选择两个方案', icon: 'none' })
      return
    }
    if (planAIndex === planBIndex) {
      wx.showToast({ title: '请选择不同的方案', icon: 'none' })
      return
    }
    const planIds = [plans[planAIndex].id, plans[planBIndex].id]
    this.setData({ loading: true, comparisonData: null })
    try {
      const response = await volunteer.comparePlans(planIds)
      const data = response.data || response
      this.setData({ comparisonData: data, loading: false })
    } catch (error) {
      console.error('方案对比失败:', error)
      wx.showToast({ title: '对比分析失败', icon: 'none' })
      this.setData({ loading: false })
    }
  },

  onShareAppMessage() {
    return {
      title: '中考志愿填报 - 方案对比',
      path: '/pages/index/index'
    }
  }
})
