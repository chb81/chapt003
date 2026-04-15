const { getCached, cache } = require('./cache')

class ImageOptimizer {
  constructor() {
    this.imageCacheKeyPrefix = 'img_cache_'
    this.maxCacheSize = 10 * 1024 * 1024
    this.compressQuality = 0.8
  }

  loadImage(url, options = {}) {
    return new Promise((resolve, reject) => {
      const { mode = 'aspectFill', cache = true } = options
      
      if (cache) {
        const cachedImage = this.getCachedImage(url)
        if (cachedImage) {
          resolve(cachedImage)
          return
        }
      }
      
      wx.getImageInfo({
        src: url,
        success: (res) => {
          if (cache) {
            this.cacheImage(url, res)
          }
          resolve(res)
        },
        fail: (error) => {
          reject(error)
        }
      })
    })
  }

  compressImage(src, quality = this.compressQuality) {
    return new Promise((resolve, reject) => {
      wx.compressImage({
        src,
        quality: Math.round(quality * 100),
        success: (res) => {
          resolve(res.tempFilePath)
        },
        fail: (error) => {
          reject(error)
        }
      })
    })
  }

  chooseImage(options = {}) {
    const {
      count = 1,
      sizeType = ['original', 'compressed'],
      sourceType = ['album', 'camera']
    } = options
    
    return new Promise((resolve, reject) => {
      wx.chooseImage({
        count,
        sizeType,
        sourceType,
        success: (res) => {
          resolve(res.tempFilePaths)
        },
        fail: (error) => {
          reject(error)
        }
      })
    })
  }

  previewImage(urls, current = 0) {
    wx.previewImage({
      urls: Array.isArray(urls) ? urls : [urls],
      current: typeof current === 'number' ? urls[current] : current
    })
  }

  saveImageToPhotosAlbum(filePath) {
    return new Promise((resolve, reject) => {
      wx.saveImageToPhotosAlbum({
        filePath,
        success: () => {
          wx.showToast({
            title: '保存成功',
            icon: 'success'
          })
          resolve()
        },
        fail: (error) => {
          if (error.errMsg.includes('auth')) {
            wx.showModal({
              title: '提示',
              content: '需要授权访问相册才能保存图片',
              confirmText: '去授权',
              success: (res) => {
                if (res.confirm) {
                  wx.openSetting()
                }
              }
            })
          }
          reject(error)
        }
      })
    })
  }

  uploadImage(filePath, url, formData = {}) {
    return new Promise((resolve, reject) => {
      wx.uploadFile({
        url,
        filePath,
        name: 'file',
        formData,
        header: {
          'Authorization': wx.getStorageSync('token') || ''
        },
        success: (res) => {
          try {
            const data = JSON.parse(res.data)
            if (data.code === 200) {
              resolve(data)
            } else {
              reject(new Error(data.message || '上传失败'))
            }
          } catch (error) {
            reject(error)
          }
        },
        fail: (error) => {
          reject(error)
        }
      })
    })
  }

  cacheImage(url, imageInfo) {
    const cacheKey = this.imageCacheKeyPrefix + url
    
    try {
      wx.setStorageSync(cacheKey, {
        info: imageInfo,
        timestamp: Date.now()
      })
    } catch (error) {
      console.error('Image cache error:', error)
    }
  }

  getCachedImage(url) {
    const cacheKey = this.imageCacheKeyPrefix + url
    
    try {
      const cached = wx.getStorageSync(cacheKey)
      if (cached) {
        return cached.info
      }
    } catch (error) {
      console.error('Get cached image error:', error)
    }
    
    return null
  }

  clearImageCache() {
    try {
      const info = wx.getStorageInfoSync()
      const keys = info.keys
      
      keys.forEach(key => {
        if (key.startsWith(this.imageCacheKeyPrefix)) {
          wx.removeStorageSync(key)
        }
      })
      
      return true
    } catch (error) {
      console.error('Clear image cache error:', error)
      return false
    }
  }

  getImageCacheStats() {
    try {
      const info = wx.getStorageInfoSync()
      const keys = info.keys
      const imageKeys = keys.filter(key => key.startsWith(this.imageCacheKeyPrefix))
      
      return {
        count: imageKeys.length,
        totalSize: info.currentSize
      }
    } catch (error) {
      console.error('Get image cache stats error:', error)
      return null
    }
  }

  lazyLoadImages(imageUrls, callback) {
    const observer = wx.createIntersectionObserver()
    
    imageUrls.forEach((url, index) => {
      observer.observe(`#lazy-image-${index}`, (res) => {
        if (res.intersectionRatio > 0) {
          callback(url, index)
        }
      })
    })
    
    return observer
  }

  preloadImages(urls) {
    urls.forEach(url => {
      wx.getImageInfo({
        src: url,
        success: () => {},
        fail: () => {}
      })
    })
  }

  generateThumbnail(filePath, width = 200, height = 200) {
    return new Promise((resolve, reject) => {
      wx.getImageInfo({
        src: filePath,
        success: (res) => {
          const canvas = wx.createOffscreenCanvas({
            type: '2d',
            width,
            height
          })
          
          const ctx = canvas.getContext('2d')
          const image = canvas.createImage()
          
          image.onload = () => {
            ctx.drawImage(image, 0, 0, width, height)
            resolve(canvas.toDataURL())
          }
          
          image.onerror = (error) => {
            reject(error)
          }
          
          image.src = filePath
        },
        fail: (error) => {
          reject(error)
        }
      })
    })
  }
}

const imageOptimizer = new ImageOptimizer()

module.exports = {
  imageOptimizer,
  loadImage: (url, options) => imageOptimizer.loadImage(url, options),
  compressImage: (src, quality) => imageOptimizer.compressImage(src, quality),
  chooseImage: (options) => imageOptimizer.chooseImage(options),
  previewImage: (urls, current) => imageOptimizer.previewImage(urls, current),
  saveImageToPhotosAlbum: (filePath) => imageOptimizer.saveImageToPhotosAlbum(filePath),
  uploadImage: (filePath, url, formData) => imageOptimizer.uploadImage(filePath, url, formData),
  clearImageCache: () => imageOptimizer.clearImageCache(),
  getImageCacheStats: () => imageOptimizer.getImageCacheStats(),
  lazyLoadImages: (urls, callback) => imageOptimizer.lazyLoadImages(urls, callback),
  preloadImages: (urls) => imageOptimizer.preloadImages(urls),
  generateThumbnail: (filePath, width, height) => imageOptimizer.generateThumbnail(filePath, width, height)
}
