const app = getApp()
const volunteer = require('../../api/volunteer')
const auth = require('../../api/auth')

Page({
  data: {
    applications: [],
    filteredApplications: [],
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
      // 尝试微信静默登录
      const app = getApp()
      app.autoWechatLogin().then(result => {
        if (result.success) {
          this.loadApplications()
        } else {
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
      }).catch(() => {
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
      })
    }
  },

  loadApplications() {
    if (this.data.loading) return
    const self = this
    this.setData({ loading: true })

    volunteer.getVolunteerApplications({ page: 0, size: 50 })
      .then(function(response) {
        const data = response.data || response || {}
        const content = data.applications || data.content || []
        self.setData({
          applications: content,
          filteredApplications: self.filterByTab(content, self.data.activeTab),
          loading: false
        })
      })
      .catch(function(error) {
        console.error('加载志愿列表失败:', error)
        wx.showToast({ title: '加载失败', icon: 'error' })
        self.setData({ loading: false })
      })
  },

  refreshApplications() {
    const self = this
    this.setData({ refreshing: true })

    volunteer.getVolunteerApplications({ page: 0, size: 50 })
      .then(function(response) {
        const data = response.data || response || {}
        const content = data.applications || data.content || []
        self.setData({
          applications: content,
          filteredApplications: self.filterByTab(content, self.data.activeTab),
          refreshing: false
        })
        wx.stopPullDownRefresh()
      })
      .catch(function(error) {
        console.error('刷新志愿列表失败:', error)
        self.setData({ refreshing: false })
      })
  },

  onTabChange(e) {
    const tab = e.currentTarget.dataset.tab
    this.setData({ 
      activeTab: tab,
      filteredApplications: this.filterByTab(this.data.applications, tab)
    })
  },

  filterByTab(applications, tab) {
    if (tab === 'all') return applications
    return applications.filter(app => app.status === tab)
  },

  onViewDetail(e) {
    const { id } = e.currentTarget.dataset
    const self = this
    volunteer.getVolunteerApplicationDetail(id)
      .then(function(response) {
        const data = response.data || response
        wx.showModal({
          title: data.name || '志愿方案详情',
          content: '状态: ' + self.getStatusLabel(data.status) + '\n学校数量: ' + (data.items || []).length + '\n创建时间: ' + self.formatDate(data.createdAt),
          showCancel: false
        })
      })
      .catch(function(error) {
        console.error('获取志愿详情失败:', error)
        wx.showToast({ title: '获取详情失败', icon: 'error' })
      })
  },

  onSubmitApplication(e) {
    const { id } = e.currentTarget.dataset
    const self = this

    wx.showModal({
      title: '确认提交',
      content: '提交后不可修改，确定提交吗？',
      success: function(res) {
        if (res.confirm) {
          volunteer.submitVolunteerApplication(id)
            .then(function() {
              wx.showToast({ title: '提交成功', icon: 'success' })
              self.loadApplications()
            })
            .catch(function(error) {
              console.error('提交志愿失败:', error)
              wx.showToast({ title: '提交失败', icon: 'error' })
            })
        }
      }
    })
  },

  onDeleteApplication(e) {
    const { id } = e.currentTarget.dataset
    const self = this

    wx.showModal({
      title: '确认删除',
      content: '确定删除该志愿方案吗？',
      success: function(res) {
        if (res.confirm) {
          volunteer.deleteVolunteerApplication(id)
            .then(function() {
              wx.showToast({ title: '删除成功', icon: 'success' })
              self.loadApplications()
            })
            .catch(function(error) {
              console.error('删除志愿失败:', error)
              wx.showToast({ title: '删除失败', icon: 'error' })
            })
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
