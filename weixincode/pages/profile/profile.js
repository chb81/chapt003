const auth = require('../../api/auth')
const volunteer = require('../../api/volunteer')

Page({
  data: {
    userInfo: null,
    isLoggedIn: false,
    stats: {
      totalHours: 0,
      projectCount: 0,
      creditScore: 0
    },
    menuItems: [
      {
        icon: '📋',
        title: '我的申请',
        path: '/pages/applications/applications'
      },
      {
        icon: '⭐',
        title: '收藏的项目',
        path: '/pages/favorites/favorites'
      },
      {
        icon: '📊',
        title: '志愿统计',
        path: '/pages/statistics/statistics'
      },
      {
        icon: '📜',
        title: '志愿证书',
        path: '/pages/certificates/certificates'
      },
      {
        icon: '⚙️',
        title: '设置',
        path: '/pages/settings/settings'
      },
      {
        icon: '❓',
        title: '帮助与反馈',
        path: '/pages/help/help'
      }
    ],
    loading: false
  },

  onLoad() {
    this.checkLoginStatus()
  },

  onShow() {
    if (this.data.isLoggedIn) {
      this.loadUserData()
    }
  },

  checkLoginStatus() {
    const token = wx.getStorageSync('token')
    const userInfo = wx.getStorageSync('userInfo')
    
    if (token && userInfo) {
      this.setData({
        isLoggedIn: true,
        userInfo: userInfo
      })
      this.loadUserData()
    }
  },

  async loadUserData() {
    if (this.data.loading) return
    
    this.setData({ loading: true })
    
    try {
      const [userResponse, statsResponse] = await Promise.all([
        auth.getUserInfo(),
        this.loadStats()
      ])
      
      if (userResponse.code === 200) {
        this.setData({
          userInfo: userResponse.data,
          stats: statsResponse
        })
        
        wx.setStorageSync('userInfo', userResponse.data)
      }
    } catch (error) {
      console.error('加载用户数据失败:', error)
      wx.showToast({
        title: '加载失败',
        icon: 'error'
      })
    } finally {
      this.setData({ loading: false })
    }
  },

  async loadStats() {
    try {
      const response = await volunteer.getUserStats()
      return {
        totalHours: response.data?.totalHours || 0,
        projectCount: response.data?.projectCount || 0,
        creditScore: response.data?.creditScore || 0
      }
    } catch (error) {
      console.error('加载统计数据失败:', error)
      return {
        totalHours: 0,
        projectCount: 0,
        creditScore: 0
      }
    }
  },

  handleLogin() {
    wx.navigateTo({
      url: '/pages/login/login'
    })
  },

  handleLogout() {
    wx.showModal({
      title: '退出登录',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          wx.removeStorageSync('token')
          wx.removeStorageSync('userInfo')
          
          this.setData({
            isLoggedIn: false,
            userInfo: null,
            stats: {
              totalHours: 0,
              projectCount: 0,
              creditScore: 0
            }
          })
          
          wx.showToast({
            title: '已退出登录',
            icon: 'success'
          })
        }
      }
    })
  },

  handleMenuClick(e) {
    const { path } = e.currentTarget.dataset
    
    if (path) {
      wx.navigateTo({
        url: path,
        fail: () => {
          wx.showToast({
            title: '页面开发中',
            icon: 'none'
          })
        }
      })
    }
  },

  handleEditProfile() {
    wx.navigateTo({
      url: '/pages/edit-profile/edit-profile'
    })
  },

  handleShare() {
    return {
      title: '志愿汇 - 让爱心传递',
      path: '/pages/index/index'
    }
  },

  onShareAppMessage() {
    return this.handleShare()
  },

  onShareTimeline() {
    return this.handleShare()
  }
})
