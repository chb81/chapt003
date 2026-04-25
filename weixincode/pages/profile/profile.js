const auth = require('../../api/auth')
const volunteer = require('../../api/volunteer')

Page({
  data: {
    userInfo: null,
    isLoggedIn: false,
    loading: false,
    menuItems: [
      {
        icon: '📋',
        title: '我的志愿',
        path: '/pages/applications/applications'
      },
      {
        icon: '📊',
        title: '录取概率',
        path: '/pages/projects/projects'
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
    ]
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
      const response = await auth.getUserProfile()
      const data = response.data || response

      if (data) {
        this.setData({ userInfo: data })
        wx.setStorageSync('userInfo', data)
      }
    } catch (error) {
      console.error('加载用户数据失败:', error)
    } finally {
      this.setData({ loading: false })
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
          wx.removeStorageSync('loginTime')

          this.setData({
            isLoggedIn: false,
            userInfo: null
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

  onShareAppMessage() {
    return {
      title: '中考志愿填报系统',
      path: '/pages/index/index'
    }
  }
})
