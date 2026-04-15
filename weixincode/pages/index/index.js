// pages/index/index.js
const app = getApp()
const { volunteer } = require('../../api/volunteer')
const { auth } = require('../../api/auth')

Page({
  data: {
    userInfo: null,
    isLoggedIn: false,
    loading: true,
    stats: {
      totalProjects: 0,
      recruitingProjects: 0,
      myApplications: 0,
      completedHours: 0
    },
    featuredProjects: [],
    recentActivities: [],
    quickActions: [
      {
        icon: 'search',
        label: '找项目',
        url: '/pages/projects/projects',
        color: '#1989fa'
      },
      {
        icon: 'waiting',
        label: '我的申请',
        url: '/pages/applications/applications',
        color: '#07c160'
      },
      {
        icon: 'success',
        label: '志愿时长',
        url: '/pages/profile/profile',
        color: '#ff976a'
      },
      {
        icon: 'info',
        label: '关于我们',
        url: '/pages/profile/profile',
        color: '#ee0a24'
      }
    ],
    banners: [
      {
        image: '/images/banner1.png',
        title: '加入志愿服务',
        description: '用爱心温暖社会'
      },
      {
        image: '/images/banner2.png',
        title: '志愿时长认证',
        description: '官方认证，权威可靠'
      }
    ]
  },

  onLoad() {
    this.checkLoginStatus()
  },

  onShow() {
    this.loadPageData()
  },

  async checkLoginStatus() {
    const isLoggedIn = auth.checkLoginStatus()
    if (isLoggedIn) {
      const userInfo = wx.getStorageSync('userInfo')
      this.setData({
        isLoggedIn: true,
        userInfo: userInfo
      })
    }
  },

  async loadPageData() {
    if (this.data.loading) return
    
    this.setData({ loading: true })
    
    try {
      // 并行加载数据
      const [featuredProjects, statsData, recentActivities] = await Promise.all([
        this.loadFeaturedProjects(),
        this.loadStats(),
        this.loadRecentActivities()
      ])
      
      this.setData({
        featuredProjects: featuredProjects,
        stats: statsData,
        recentActivities: recentActivities,
        loading: false
      })
      
    } catch (error) {
      console.error('加载数据失败:', error)
      this.setData({ loading: false })
    }
  },

  async loadFeaturedProjects() {
    try {
      const response = await volunteer.getVolunteerProjects({
        page: 1,
        pageSize: 6,
        status: 'recruiting'
      })
      return response.data || []
    } catch (error) {
      console.error('加载精选项目失败:', error)
      return []
    }
  },

  async loadStats() {
    try {
      const [projectsResponse, applicationsResponse] = await Promise.all([
        volunteer.getVolunteerProjects({ page: 1, pageSize: 1 }),
        this.data.isLoggedIn ? volunteer.getUserApplications() : Promise.resolve({ data: [] })
      ])
      
      const allProjects = projectsResponse.total || 0
      const recruitingProjects = await this.getRecruitingCount()
      const myApplications = applicationsResponse.data ? applicationsResponse.data.length : 0
      const completedHours = this.data.userInfo ? this.data.userInfo.volunteer_hours || 0 : 0
      
      return {
        totalProjects: allProjects,
        recruitingProjects: recruitingProjects,
        myApplications: myApplications,
        completedHours: completedHours
      }
    } catch (error) {
      console.error('加载统计数据失败:', error)
      return this.data.stats
    }
  },

  async getRecruitingCount() {
    try {
      const response = await volunteer.getVolunteerProjects({
        page: 1,
        pageSize: 100,
        status: 'recruiting'
      })
      return response.data ? response.data.length : 0
    } catch (error) {
      return 0
    }
  },

  async loadRecentActivities() {
    if (!this.data.isLoggedIn) return []
    
    try {
      const response = await volunteer.getUserApplications()
      const applications = response.data || []
      return applications.slice(0, 5).map(app => ({
        id: app.id,
        title: app.project_title,
        status: app.status,
        time: app.created_at
      }))
    } catch (error) {
      console.error('加载最近活动失败:', error)
      return []
    }
  },

  onQuickAction(e) {
    const { url } = e.currentTarget.dataset
    
    if (!this.data.isLoggedIn) {
      wx.showModal({
        title: '提示',
        content: '请先登录后再使用此功能',
        success: (res) => {
          if (res.confirm) {
            wx.navigateTo({
              url: '/pages/login/login'
            })
          }
        }
      })
      return
    }
    
    wx.switchTab({
      url: url
    })
  },

  onProjectDetail(e) {
    const { id } = e.currentTarget.dataset
    
    if (!this.data.isLoggedIn) {
      wx.showModal({
        title: '提示',
        content: '请先登录后再查看项目详情',
        success: (res) => {
          if (res.confirm) {
            wx.navigateTo({
              url: '/pages/login/login'
            })
          }
        }
      })
      return
    }
    
    wx.switchTab({
      url: '/pages/projects/projects'
    })
  },

  onAllProjects() {
    wx.switchTab({
      url: '/pages/projects/projects'
    })
  },

  onLogin() {
    wx.navigateTo({
      url: '/pages/login/login'
    })
  },

  onRegister() {
    wx.navigateTo({
      url: '/pages/register/register'
    })
  },

  formatDate(dateString) {
    if (!dateString) return ''
    const date = new Date(dateString)
    const now = new Date()
    const diff = now - date
    const days = Math.floor(diff / (1000 * 60 * 60 * 24))
    
    if (days === 0) return '今天'
    if (days === 1) return '昨天'
    if (days < 7) return `${days}天前`
    
    return `${date.getMonth() + 1}月${date.getDate()}日`
  },

  getStatusLabel(status) {
    const labels = {
      pending: '待审核',
      approved: '已通过',
      rejected: '已拒绝',
      completed: '已完成'
    }
    return labels[status] || status
  }
})