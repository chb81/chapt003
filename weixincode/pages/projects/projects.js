// pages/projects/projects.js
const app = getApp()
const volunteer = require('../../api/volunteer')
const auth = require('../../api/auth')

Page({
  data: {
    projects: [],
    loading: false,
    refreshing: false,
    hasMore: true,
    page: 1,
    pageSize: 10,
    filters: {
      category: '',
      status: '',
      location: ''
    },
    searchKeyword: '',
    activeTab: 'all',
    categories: [
      { value: 'education', label: '教育支持' },
      { value: 'environment', label: '环境保护' },
      { value: 'elderly', label: '敬老服务' },
      { value: 'medical', label: '医疗健康' },
      { value: 'poverty', label: '扶贫助困' },
      { value: 'community', label: '社区服务' },
      { value: 'other', label: '其他' }
    ],
    statusOptions: [
      { value: 'recruiting', label: '招募中' },
      { value: 'ongoing', label: '进行中' },
      { value: 'completed', label: '已结束' },
      { value: 'paused', label: '暂停中' }
    ],
    currentProject: null,
    showApplyModal: false,
    applyForm: {
      project_id: '',
      motivation: '',
      experience: '',
      availability: ''
    },
    submitting: false
  },

  onLoad() {
    this.checkLoginStatus()
  },

  onShow() {
    this.loadProjects()
  },

  onPullDownRefresh() {
    this.refreshProjects()
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadMoreProjects()
    }
  },

  checkLoginStatus() {
    const isLoggedIn = auth.checkLoginStatus()
    if (!isLoggedIn) {
      wx.showModal({
        title: '提示',
        content: '请先登录后再查看志愿项目',
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

  async loadProjects() {
    if (this.data.loading) return
    
    this.setData({ loading: true })
    wx.showLoading({ title: '加载中...' })
    
    try {
      const params = {
        page: 1,
        pageSize: this.data.pageSize,
        ...this.data.filters,
        keyword: this.data.searchKeyword
      }
      
      const response = await volunteer.getVolunteerProjects(params)
      const projects = response.data || []
      
      this.setData({
        projects: projects,
        hasMore: projects.length >= this.data.pageSize,
        page: 2,
        loading: false
      })
      
    } catch (error) {
      console.error('加载项目列表失败:', error)
      wx.showToast({
        title: '加载失败',
        icon: 'error'
      })
    } finally {
      wx.hideLoading()
    }
  },

  async refreshProjects() {
    if (this.data.refreshing) return
    
    this.setData({ refreshing: true })
    
    try {
      const params = {
        page: 1,
        pageSize: this.data.pageSize,
        ...this.data.filters,
        keyword: this.data.searchKeyword
      }
      
      const response = await volunteer.getVolunteerProjects(params)
      const projects = response.data || []
      
      this.setData({
        projects: projects,
        hasMore: projects.length >= this.data.pageSize,
        page: 2,
        refreshing: false
      })
      
      wx.stopPullDownRefresh()
      
    } catch (error) {
      console.error('刷新项目列表失败:', error)
      wx.showToast({
        title: '刷新失败',
        icon: 'error'
      })
      this.setData({ refreshing: false })
    }
  },

  async loadMoreProjects() {
    if (!this.data.hasMore || this.data.loading) return
    
    this.setData({ loading: true })
    
    try {
      const params = {
        page: this.data.page,
        pageSize: this.data.pageSize,
        ...this.data.filters,
        keyword: this.data.searchKeyword
      }
      
      const response = await volunteer.getVolunteerProjects(params)
      const newProjects = response.data || []
      
      this.setData({
        projects: [...this.data.projects, ...newProjects],
        hasMore: newProjects.length >= this.data.pageSize,
        page: this.data.page + 1,
        loading: false
      })
      
    } catch (error) {
      console.error('加载更多项目失败:', error)
      wx.showToast({
        title: '加载失败',
        icon: 'error'
      })
      this.setData({ loading: false })
    }
  },

  onSearchInput(e) {
    this.setData({ searchKeyword: e.detail.value })
  },

  onSearchConfirm() {
    this.loadProjects()
  },

  onCategoryChange(e) {
    const { category } = e.detail.value
    this.setData({
      'filters.category': category,
      activeTab: category || 'all'
    })
    this.loadProjects()
  },

  onStatusChange(e) {
    const { status } = e.detail.value
    this.setData({
      'filters.status': status
    })
    this.loadProjects()
  },

  onLocationChange(e) {
    this.setData({
      'filters.location': e.detail.value
    })
    this.loadProjects()
  },

  onLocationInput(e) {
    this.setData({
      'filters.location': e.detail.value
    })
  },

  clearFilters() {
    this.setData({
      filters: {
        category: '',
        status: '',
        location: ''
      },
      activeTab: 'all'
    })
    this.loadProjects()
  },

  async showProjectDetail(e) {
    const { id } = e.currentTarget.dataset
    try {
      const response = await volunteer.getProjectDetail(id)
      const project = response.data
      
      this.setData({ currentProject: project })
      this.showProjectModal()
    } catch (error) {
      console.error('获取项目详情失败:', error)
      wx.showToast({
        title: '获取详情失败',
        icon: 'error'
      })
    }
  },

  showProjectModal() {
    this.setData({
      showProjectModal: true
    })
  },

  hideProjectModal() {
    this.setData({
      showProjectModal: false,
      currentProject: null
    })
  },

  async applyForProject(e) {
    const { id } = e.currentTarget.dataset
    
    // 检查是否已申请过
    const hasApplied = await this.checkApplicationStatus(id)
    if (hasApplied) {
      wx.showToast({
        title: '您已申请过该项目',
        icon: 'none'
      })
      return
    }
    
    this.setData({
      showApplyModal: true,
      'applyForm.project_id': id
    })
  },

  async checkApplicationStatus(projectId) {
    try {
      const response = await volunteer.getUserApplications()
      const applications = response.data || []
      return applications.some(app => app.project_id === projectId)
    } catch (error) {
      console.error('检查申请状态失败:', error)
      return false
    }
  },

  onApplyInputChange(e) {
    const { field } = e.currentTarget.dataset
    this.setData({
      [`applyForm.${field}`]: e.detail.value
    })
  },

  async submitApplication() {
    if (this.data.submitting) return
    
    const { applyForm } = this.data
    
    // 表单验证
    if (!applyForm.motivation || applyForm.motivation.trim().length < 10) {
      wx.showToast({
        title: '请填写申请动机（至少10个字符）',
        icon: 'none'
      })
      return
    }
    
    if (!applyForm.experience || applyForm.experience.trim().length < 5) {
      wx.showToast({
        title: '请填写相关经验',
        icon: 'none'
      })
      return
    }
    
    this.setData({ submitting: true })
    wx.showLoading({ title: '提交中...' })
    
    try {
      const response = await volunteer.applyForProject(applyForm)
      
      wx.showToast({
        title: '申请成功',
        icon: 'success'
      })
      
      setTimeout(() => {
        this.setData({
          showApplyModal: false,
          submitting: false,
          applyForm: {
            project_id: '',
            motivation: '',
            experience: '',
            availability: ''
          }
        })
        this.hideProjectModal()
        wx.navigateBack()
      }, 1500)
      
    } catch (error) {
      console.error('提交申请失败:', error)
      wx.showToast({
        title: '申请失败，请重试',
        icon: 'error'
      })
      this.setData({ submitting: false })
    } finally {
      wx.hideLoading()
    }
  },

  cancelApplication() {
    this.setData({
      showApplyModal: false,
      submitting: false,
      applyForm: {
        project_id: '',
        motivation: '',
        experience: '',
        availability: ''
      }
    })
  },

  formatDate(dateString) {
    if (!dateString) return ''
    const date = new Date(dateString)
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
  },

  getCategoryLabel(value) {
    const category = this.data.categories.find(c => c.value === value)
    return category ? category.label : value
  },

  getStatusLabel(value) {
    const status = this.data.statusOptions.find(s => s.value === value)
    return status ? status.label : value
  },

  getStatusColor(value) {
    const colors = {
      recruiting: '#07c160',
      ongoing: '#1989fa',
      completed: '#8c8c8c',
      paused: '#ff976a'
    }
    return colors[value] || '#8c8c8c'
  }
})