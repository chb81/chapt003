const app = getApp()

class CacheManager {
  constructor() {
    this.cacheKeyPrefix = 'cache_'
    this.defaultExpireTime = 30 * 60 * 1000
  }

  set(key, data, expireTime = this.defaultExpireTime) {
    const cacheKey = this.cacheKeyPrefix + key
    const expireTimestamp = Date.now() + expireTime
    
    try {
      wx.setStorageSync(cacheKey, {
        data: data,
        expire: expireTimestamp
      })
      return true
    } catch (error) {
      console.error('Cache set error:', error)
      return false
    }
  }

  get(key) {
    const cacheKey = this.cacheKeyPrefix + key
    
    try {
      const cache = wx.getStorageSync(cacheKey)
      
      if (!cache) {
        return null
      }
      
      if (Date.now() > cache.expire) {
        this.remove(key)
        return null
      }
      
      return cache.data
    } catch (error) {
      console.error('Cache get error:', error)
      return null
    }
  }

  remove(key) {
    const cacheKey = this.cacheKeyPrefix + key
    
    try {
      wx.removeStorageSync(cacheKey)
      return true
    } catch (error) {
      console.error('Cache remove error:', error)
      return false
    }
  }

  clear() {
    try {
      wx.clearStorageSync()
      return true
    } catch (error) {
      console.error('Cache clear error:', error)
      return false
    }
  }

  has(key) {
    return this.get(key) !== null
  }

  getOrSet(key, fetchFunction, expireTime = this.defaultExpireTime) {
    const cached = this.get(key)
    
    if (cached !== null) {
      return Promise.resolve(cached)
    }
    
    return fetchFunction().then(data => {
      this.set(key, data, expireTime)
      return data
    })
  }

  compressData(data) {
    if (typeof data === 'string') {
      try {
        return JSON.stringify({ compressed: true, data })
      } catch (error) {
        return data
      }
    }
    return data
  }

  decompressData(data) {
    if (typeof data === 'string') {
      try {
        const parsed = JSON.parse(data)
        if (parsed.compressed) {
          return parsed.data
        }
      } catch (error) {
        console.error('Decpress error:', error)
      }
    }
    return data
  }

  getCacheSize() {
    try {
      const info = wx.getStorageInfoSync()
      return {
        currentSize: info.currentSize,
        limitSize: info.limitSize,
        usagePercent: Math.round((info.currentSize / info.limitSize) * 100)
      }
    } catch (error) {
      console.error('Get cache size error:', error)
      return null
    }
  }

  clearExpiredCache() {
    try {
      const info = wx.getStorageInfoSync()
      const keys = info.keys
      
      let clearedCount = 0
      
      keys.forEach(key => {
        if (key.startsWith(this.cacheKeyPrefix)) {
          try {
            const cache = wx.getStorageSync(key)
            if (cache && Date.now() > cache.expire) {
              wx.removeStorageSync(key)
              clearedCount++
            }
          } catch (error) {
            console.error('Clear expired cache error:', error)
          }
        }
      })
      
      console.log(`Cleared ${clearedCount} expired cache items`)
      return clearedCount
    } catch (error) {
      console.error('Clear expired cache error:', error)
      return 0
    }
  }

  getCacheStats() {
    try {
      const info = wx.getStorageInfoSync()
      const keys = info.keys
      const cacheKeys = keys.filter(key => key.startsWith(this.cacheKeyPrefix))
      
      let totalItems = 0
      let expiredItems = 0
      let validItems = 0
      
      cacheKeys.forEach(key => {
        try {
          const cache = wx.getStorageSync(key)
          totalItems++
          if (cache) {
            if (Date.now() > cache.expire) {
              expiredItems++
            } else {
              validItems++
            }
          }
        } catch (error) {
          console.error('Get cache stats error:', error)
        }
      })
      
      return {
        totalItems,
        expiredItems,
        validItems,
        size: info.currentSize,
        limit: info.limitSize,
        usagePercent: Math.round((info.currentSize / info.limitSize) * 100)
      }
    } catch (error) {
      console.error('Get cache stats error:', error)
      return null
    }
  }
}

const cacheManager = new CacheManager()

function cache(key, data, expireTime) {
  return cacheManager.set(key, data, expireTime)
}

function getCached(key) {
  return cacheManager.get(key)
}

function removeCache(key) {
  return cacheManager.remove(key)
}

function clearAllCache() {
  return cacheManager.clear()
}

function getOrSetCache(key, fetchFunction, expireTime) {
  return cacheManager.getOrSet(key, fetchFunction, expireTime)
}

function getCacheInfo() {
  return cacheManager.getCacheStats()
}

function clearExpiredCache() {
  return cacheManager.clearExpiredCache()
}

module.exports = {
  cacheManager,
  cache,
  getCached,
  removeCache,
  clearAllCache,
  getOrSetCache,
  getCacheInfo,
  clearExpiredCache
}
