// pages/applications/applications.js
const app = getApp()
const { volunteer } = require('../../api/volunteer')
const { auth } = require('../../api/auth')

Page({
  data: {
    applications: [],
    loading: false,
    refreshing: false,
    activeTab: 'all',
    tabs: [
      { key: 'all', label: '全部' },
      { key: 'pending', label: '待审核' },
      { key: 'approved', label: '已通过' },
      { key: 'rejected', label: '已拒绝' },
      { key: 'completed', label: '已完成' }
    ],
    showDetailModal: false,
    currentApplication: null,
    cancelling: false
  },

  onLoad() {
    this.checkLoginStatus()
  },

  onShow() {
    this.loadApplications()
  },

  onPullDownRefresh() {
    this.refreshApplications()
  },

  checkLoginStatus() {
    const isLoggedIn = auth.checkLoginStatus()
    if (!isLoggedIn) {
      wx.showModal({
        title: '提示',
        content: '请先登录后再查看申请记录',
        success: (res) => {
          if (res.confirm) {
            wx.navigateTo({
              url: '/pages/login/login'
            })
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
    wx.showLoading({ title: '加载中...' })
    
    try {
      const response = await volunteer.getUserApplications()
      let applications = response.data || []
      
      // 根据当前tab过滤
      if (this.data.activeTab !== 'all') {
        applications = applications.filter(app => app.status === this.data.activeTab)
      }
      
      this.setData({
        applications: applications,
        loading: false
      })
      
    } catch (error) {
      console.error('加载申请记录失败:', error)
      wx.showToast({
        title: '加载失败',
        icon: 'error'
      })
      this.setData({ loading: false })
    } finally {
      wx.hideLoading()
    }
  },

  async refreshApplications() {
    if (this.data.refreshing) return
    
    this.setData({ refreshing: true })
    
    try {
      const response = await volunteer.getUserApplications()
      let applications = response.data || []
      
      if (this.data.activeTab !== 'all') {
        applications = applications.filter(app => app.status === this.data.activeTab)
      }
      
      this.setData({
        applications: applications,
        refreshing: false
      })
      
      wx.stopPullDownRefresh()
      
    } catch (error) {
      console.error('刷新申请记录失败:', error)
      wx.showToast({
        title: '刷新失败',
        icon: 'error'
      })
      this.setData({ refreshing: false })
    }
  },

  onTabChange(e) {
    const { key } = e.currentTarget.dataset
    this.setData({ activeTab: key })
    this.loadApplications()
  },

  showApplicationDetail(e) {
    const { id } = e.currentTarget.dataset
    const application = this.data.applications.find(app => app.id === id)
    if (application) {
      this.setData({
        currentApplication: application,
        showDetailModal: true
      })
    }
  },

  hideDetailModal() {
    this.setData({
      showDetailModal: false,
      currentApplication: null
    })
  },

  async cancelApplication(e) {
    const { id } = e.currentTarget.dataset
    
    wx.showModal({
      title: '确认取消',
      content: '确定要取消这个申请吗？',
      success: async (res) => {
        if (res.confirm) {
          await this.doCancelApplication(id)
        }
      }
    })
  },

  async doCancelApplication(applicationId) {
    if (this.data.cancelling) return
    
    this.setData({ cancelling: true })
    wx.showLoading({ title: '取消中...' })
    
    try {
      await volunteer.cancelApplication(applicationId)
      
      wx.showToast({
        title: '已取消申请',
        icon: 'success'
      })
      
      setTimeout(() => {
        this.loadApplications()
      }, 1500)
      
    } catch (error) {
      console.error('取消申请失败:', error)
      wx.showToast({
        title: '取消失败，请重试',
        icon: 'error'
      })
    } finally {
      this.setData({ cancelling: false })
      wx.hideLoading()
    }
  },

  formatDate(dateString) {
    if (!dateString) return ''
    const date = new Date(dateString)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hour = String(date.getHours()).padStart(2, '0')
    const minute = String(date.getMinutes()).padStart(2, '0')
    return `${year}-${month}-${day} ${hour}:${minute}`
  },

  getStatusLabel(status) {
    const labels = {
      pending: '待审核',
      approved: '已通过',
      rejected: '已拒绝',
      completed: '已完成',
      cancelled: '已取消'
    }
    return labels[status] || status
  },

  getStatusColor(status) {
    const colors = {
      pending: '#ff976a',
      approved: '#07c160',
      rejected: '#ee0a24',
      completed: '#1989fa',
      cancelled: '#969799'
    }
    return colors[status] || '#969799'
  },

  getStatusBgColor(status) {
    const bgColors = {
      pending: '#fff7e6',
      approved: '#f0f9ff',
      rejected: '#fef0f0',
      completed: '#f0f9ff',
      cancelled: '#f5f5f5'
    }
    return bgColors[status] || '#f5f5f5'
  }
})