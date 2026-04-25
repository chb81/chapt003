const app = getApp()
const volunteer = require('../../api/volunteer')
const auth = require('../../api/auth')

Page({
  data: {
    applications: [],
    loading: false,
    refreshing: false,
    activeTab: 'all',
    tabs: [
      { value: 'all', label: '全部' },
      { value: 'DRAFT', label: '草稿' },
      { value: 'SUBMITTED', label: '已提交' },
      { value: 'SIMULATION', label: '模拟方案' }
    ]
  },

  onLoad() {
    this.checkLoginAndLoad()
  },

  onShow() {
    this.loadApplications()
  },

  onPullDownRefresh() {
    this.refreshApplications()
  },

  checkLoginAndLoad() {
    if (!auth.checkLoginStatus()) {
      wx.showModal({
        title: '提示',
        content: '请先登录后再查看志愿填报',
        success: (res) => {
          if (res.confirm) {
            wx.navigateTo({ url: '/pages/login/login' })
          } else {
            wx.navigateBack()
          }
        }
      })
    }
  },

  async loadApplications() {
    if (this.data.loading) return

    this.setData({ loading: true })

    try {
      const response = await volunteer.getVolunteerApplications({ page: 0, size: 50 })
      const data = response.data || response || {}
      const content = data.content || []

      this.setData({
        applications: content,
        loading: false
      })
    } catch (error) {
      console.error('加载志愿列表失败:', error)
      wx.showToast({ title: '加载失败', icon: 'error' })
      this.setData({ loading: false })
    }
  },

  async refreshApplications() {
    this.setData({ refreshing: true })

    try {
      const response = await volunteer.getVolunteerApplications({ page: 0, size: 50 })
      const data = response.data || response || {}
      const content = data.content || []

      this.setData({
        applications: content,
        refreshing: false
      })
      wx.stopPullDownRefresh()
    } catch (error) {
      console.error('刷新志愿列表失败:', error)
      this.setData({ refreshing: false })
    }
  },

  onTabChange(e) {
    const tab = e.currentTarget.dataset.tab
    this.setData({ activeTab: tab })
  },

  getFilteredApplications() {
    const { applications, activeTab } = this.data
    if (activeTab === 'all') return applications
    return applications.filter(app => app.status === activeTab)
  },

  async onViewDetail(e) {
    const { id } = e.currentTarget.dataset
    try {
      const response = await volunteer.getVolunteerApplicationDetail(id)
      const data = response.data || response

      wx.showModal({
        title: data.name || '志愿方案详情',
        content: `状态: ${this.getStatusLabel(data.status)}\n学校数量: ${(data.items || []).length}\n创建时间: ${this.formatDate(data.createdAt)}`,
        showCancel: false
      })
    } catch (error) {
      console.error('获取志愿详情失败:', error)
      wx.showToast({ title: '获取详情失败', icon: 'error' })
    }
  },

  async onSubmitApplication(e) {
    const { id } = e.currentTarget.dataset

    wx.showModal({
      title: '确认提交',
      content: '提交后不可修改，确定提交吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await volunteer.submitVolunteerApplication(id)
            wx.showToast({ title: '提交成功', icon: 'success' })
            this.loadApplications()
          } catch (error) {
            console.error('提交志愿失败:', error)
            wx.showToast({ title: '提交失败', icon: 'error' })
          }
        }
      }
    })
  },

  async onDeleteApplication(e) {
    const { id } = e.currentTarget.dataset

    wx.showModal({
      title: '确认删除',
      content: '确定删除该志愿方案吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await volunteer.deleteVolunteerApplication(id)
            wx.showToast({ title: '删除成功', icon: 'success' })
            this.loadApplications()
          } catch (error) {
            console.error('删除志愿失败:', error)
            wx.showToast({ title: '删除失败', icon: 'error' })
          }
        }
      }
    })
  },

  onCreateNew() {
    wx.switchTab({
      url: '/pages/projects/projects'
    })
  },

  getStatusLabel(status) {
    const labels = {
      DRAFT: '草稿',
      SUBMITTED: '已提交',
      SIMULATION: '模拟方案'
    }
    return labels[status] || status || ''
  },

  getStatusColor(status) {
    const colors = {
      DRAFT: '#909399',
      SUBMITTED: '#67c23a',
      SIMULATION: '#e6a23c'
    }
    return colors[status] || '#909399'
  },

  formatDate(dateString) {
    if (!dateString) return ''
    const date = new Date(dateString)
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
  }
})
