Page({
  data: {
    userInfo: null,
    settings: {
      notificationEnabled: true,
      messageEnabled: true,
      darkMode: false,
      language: 'zh_CN'
    },
    version: '1.0.0',
    loading: false
  },

  onLoad() {
    this.loadUserInfo()
    this.loadSettings()
  },

  loadUserInfo() {
    const userInfo = wx.getStorageSync('userInfo')
    this.setData({ userInfo })
  },

  loadSettings() {
    const settings = wx.getStorageSync('settings') || this.data.settings
    this.setData({ settings })
  },

  saveSettings() {
    wx.setStorageSync('settings', this.data.settings)
  },

  handleNotificationChange(e) {
    const value = e.detail.value
    this.setData({
      'settings.notificationEnabled': value
    })
    this.saveSettings()
  },

  handleMessageChange(e) {
    const value = e.detail.value
    this.setData({
      'settings.messageEnabled': value
    })
    this.saveSettings()
  },

  handleDarkModeChange(e) {
    const value = e.detail.value
    this.setData({
      'settings.darkMode': value
    })
    this.saveSettings()
    
    if (value) {
      wx.showToast({
        title: '深色模式已启用',
        icon: 'success'
      })
    }
  },

  handleLanguageChange() {
    const languageOptions = ['zh_CN', 'en_US']
    const currentIndex = languageOptions.indexOf(this.data.settings.language)
    const nextIndex = (currentIndex + 1) % languageOptions.length
    const nextLanguage = languageOptions[nextIndex]
    
    this.setData({
      'settings.language': nextLanguage
    })
    this.saveSettings()
  },

  getLanguageLabel(language) {
    const labels = {
      'zh_CN': '简体中文',
      'en_US': 'English'
    }
    return labels[language] || language
  },

  handleClearCache() {
    wx.showModal({
      title: '清除缓存',
      content: '确定要清除所有缓存数据吗？',
      success: (res) => {
        if (res.confirm) {
          try {
            wx.clearStorageSync()
            this.setData({
              userInfo: null
            })
            
            wx.showToast({
              title: '缓存已清除',
              icon: 'success'
            })
            
            setTimeout(() => {
              wx.reLaunch({
                url: '/pages/index/index'
              })
            }, 1500)
          } catch (error) {
            console.error('清除缓存失败:', error)
            wx.showToast({
              title: '清除失败',
              icon: 'error'
            })
          }
        }
      }
    })
  },

  handleClearAllCache() {
    wx.showModal({
      title: '清除全部数据',
      content: '确定要清除所有数据和缓存吗？这将包括登录信息、设置等所有数据。',
      success: (res) => {
        if (res.confirm) {
          try {
            const app = getApp()
            if (app.globalData.clearAllCache) {
              app.globalData.clearAllCache()
            } else {
              wx.clearStorageSync()
            }
            
            this.setData({
              userInfo: null,
              settings: {
                notificationEnabled: true,
                messageEnabled: true,
                darkMode: false,
                language: 'zh_CN'
              }
            })
            
            wx.showToast({
              title: '数据已清除',
              icon: 'success'
            })
            
            setTimeout(() => {
              wx.reLaunch({
                url: '/pages/index/index'
              })
            }, 1500)
          } catch (error) {
            console.error('清除数据失败:', error)
            wx.showToast({
              title: '清除失败',
              icon: 'error'
            })
          }
        }
      }
    })
  },

  handleAbout() {
    wx.showModal({
      title: '关于中考志愿填报',
      content: `中考志愿填报小程序\n版本: ${this.data.version}\n\n让志愿填报更科学、更轻松`,
      showCancel: false
    })
  },

  handlePrivacy() {
    wx.showModal({
      title: '隐私政策',
      content: '我们重视您的隐私保护。在使用本应用时，我们将按照相关法律法规要求收集、使用和存储您的个人信息。更多详情请访问我们的隐私政策页面。',
      confirmText: '知道了',
      showCancel: false
    })
  },

  handleTerms() {
    wx.showModal({
      title: '用户协议',
      content: '欢迎使用中考志愿填报小程序。使用本应用即表示您同意我们的用户协议和隐私政策。请仔细阅读相关条款。',
      confirmText: '知道了',
      showCancel: false
    })
  },

  handleFeedback() {
    wx.navigateTo({
      url: '/pages/help/help?tab=feedback'
    })
  },

  handleShare() {
    return {
      title: '中考志愿填报',
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
