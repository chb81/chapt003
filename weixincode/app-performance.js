const { performanceMonitor } = require('./utils/performance')
const { getCacheInfo, clearExpiredCache } = require('./utils/cache')
const { getImageCacheStats, clearImageCache } = require('./utils/image')

App({
  onLaunch() {
    console.log('App Launch')
    
    this.initPerformanceMonitor()
    this.initCacheManager()
    this.initNetworkListener()
    this.checkAppUpdate()
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
      url: 'https://api.example.com/error-report',
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
  },

  globalData: {
    userInfo: null,
    token: null,
    isConnected: true,
    networkType: 'wifi',
    tokenExpireTime: 7 * 24 * 60 * 60 * 1000,
    baseUrl: 'https://api.example.com',
    apiVersion: 'v1'
  }
})
