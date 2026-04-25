const { performanceMonitor } = require('./utils/performance')
const { getCacheInfo, clearExpiredCache } = require('./utils/cache')
const { getImageCacheStats } = require('./utils/image')
const auth = require('./api/auth')

App({
  globalData: {
    apiBaseUrl: 'http://localhost:8080/api/v1',
    tokenExpireTime: 24 * 60 * 60 * 1000,
    loginTime: null,
    userInfo: null,
    token: null,
    isConnected: true,
    networkType: 'wifi',
    wechatLoginPromise: null  // 防止重复登录
  },

  onLaunch() {
    console.log('App Launch')
    performanceMonitor.startMeasure('appLaunch')
    
    // 优先检查已有登录态，若无效则尝试静默微信登录
    this.autoWechatLogin()
    this.initPerformanceMonitor()
    this.initCacheManager()
    this.initNetworkListener()
    this.checkAppUpdate()
    
    performanceMonitor.endMeasure('appLaunch')
  },

  onShow() {
    console.log('App Show')
    
    this.clearExpiredCache()
  },

  onHide() {
    console.log('App Hide')
  },

  onError(error) {
    console.error('App Error:', error)
    this.reportError(error)
  },

  /**
   * 自动微信登录
   * 1. 若已有有效 token，直接返回
   * 2. 若 token 过期或不存在，调用微信静默登录
   */
  autoWechatLogin() {
    // 防止重复调用
    if (this.globalData.wechatLoginPromise) {
      return this.globalData.wechatLoginPromise
    }

    // 检查已有登录态
    const existingToken = wx.getStorageSync('token')
    const existingLoginTime = wx.getStorageSync('loginTime')
    if (existingToken && existingLoginTime) {
      const elapsed = new Date().getTime() - existingLoginTime
      if (elapsed < this.globalData.tokenExpireTime) {
        // Token 仍有效，恢复全局状态
        this.globalData.token = existingToken
        this.globalData.userInfo = wx.getStorageSync('userInfo')
        this.globalData.loginTime = existingLoginTime
        console.log('已有有效登录态，无需重新登录')
        return Promise.resolve({ success: true, restored: true })
      }
    }

    // Token 无效或不存在，执行静默微信登录
    console.log('开始微信静默登录...')
    this.globalData.wechatLoginPromise = auth.silentWechatLogin()
      .then(response => {
        const loginResult = response.data || response
        if (loginResult && loginResult.token) {
          auth.saveWechatLoginResult(loginResult)
          console.log('微信静默登录成功, isNewUser:', loginResult.isNewUser)
          return { success: true, isNewUser: loginResult.isNewUser }
        } else {
          console.warn('微信静默登录返回数据异常')
          return { success: false, message: '返回数据异常' }
        }
      })
      .catch(error => {
        console.warn('微信静默登录失败，用户可能需要手动登录:', error.message)
        return { success: false, message: error.message }
      })
      .finally(() => {
        this.globalData.wechatLoginPromise = null
      })

    return this.globalData.wechatLoginPromise
  },

  checkTokenExpiration() {
    const loginTime = wx.getStorageSync('loginTime')
    const currentTime = new Date().getTime()
    
    if (loginTime) {
      const elapsed = currentTime - loginTime
      if (elapsed > this.globalData.tokenExpireTime) {
        wx.removeStorageSync('token')
        wx.removeStorageSync('userInfo')
        wx.removeStorageSync('loginTime')
        // Token 过期后尝试静默重新登录
        this.autoWechatLogin()
      }
    }
  },

  initPerformanceMonitor() {
    console.log('Performance Monitor initialized')
  },

  initCacheManager() {
    const cacheInfo = getCacheInfo()
    console.log('Cache Info:', cacheInfo)
    
    if (cacheInfo && cacheInfo.usagePercent > 80) {
      console.warn('Cache usage is high:', cacheInfo.usagePercent + '%')
    }
  },

  initNetworkListener() {
    wx.onNetworkStatusChange((res) => {
      console.log('Network Status Change:', res)
      this.globalData.isConnected = res.isConnected
      this.globalData.networkType = res.networkType
      
      if (!res.isConnected) {
        wx.showToast({
          title: '网络连接已断开',
          icon: 'none'
        })
      }
    })
  },

  checkAppUpdate() {
    if (wx.canIUse('getUpdateManager')) {
      const updateManager = wx.getUpdateManager()
      
      updateManager.onCheckForUpdate((res) => {
        console.log('Check for update:', res.hasUpdate)
      })
      
      updateManager.onUpdateReady(() => {
        wx.showModal({
          title: '更新提示',
          content: '新版本已经准备好，是否重启应用？',
          success: (res) => {
            if (res.confirm) {
              updateManager.applyUpdate()
            }
          }
        })
      })
      
      updateManager.onUpdateFailed(() => {
        console.error('Update failed')
        wx.showToast({
          title: '更新失败',
          icon: 'none'
        })
      })
    }
  },

  clearExpiredCache() {
    const clearedCount = clearExpiredCache()
    if (clearedCount > 0) {
      console.log(`Cleared ${clearedCount} expired cache items`)
    }
  },

  clearAllCache() {
    const imageCacheStats = getImageCacheStats()
    if (imageCacheStats && imageCacheStats.count > 0) {
      const { clearImageCache } = require('./utils/image')
      clearImageCache()
    }
    
    wx.clearStorageSync()
    
    wx.showToast({
      title: '缓存已清除',
      icon: 'success'
    })
  },

  getPerformanceReport() {
    return performanceMonitor.getMetricsReport()
  },

  reportError(error) {
    console.error('Error reported:', error)
    
    wx.request({
      url: this.globalData.apiBaseUrl + '/error-report',
      method: 'POST',
      data: {
        message: error.message || error,
        stack: error.stack,
        timestamp: Date.now(),
        platform: wx.getSystemInfoSync().platform
      },
      fail: (err) => {
        console.error('Error report failed:', err)
      }
    })
  }
})
