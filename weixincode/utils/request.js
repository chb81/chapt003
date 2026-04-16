const app = getApp()

// 请求配置
const requestConfig = {
  baseURL: app.globalData.apiBaseUrl,
  timeout: 30000,
  header: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
}

// 显示加载状态
let loadingCount = 0
const showLoading = (title = '加载中...') => {
  loadingCount++
  if (loadingCount === 1) {
    wx.showLoading({
      title: title,
      mask: true
    })
  }
}

const hideLoading = () => {
  loadingCount--
  if (loadingCount <= 0) {
    wx.hideLoading()
    loadingCount = 0
  }
}

// 错误提示
const showError = (message) => {
  wx.showToast({
    title: message,
    icon: 'none',
    duration: 3000
  })
}

// 网络状态检查
const checkNetwork = () => {
  return new Promise((resolve, reject) => {
    wx.getNetworkType({
      success: (res) => {
        if (res.networkType === 'none') {
          reject(new Error('网络不可用'))
        } else {
          resolve(res.networkType)
        }
      },
      fail: () => {
        reject(new Error('无法获取网络状态'))
      }
    })
  })
}

// 请求队列
const requestQueue = []
let isRequesting = false

const processQueue = () => {
  if (!isRequesting && requestQueue.length > 0) {
    isRequesting = true
    const { resolve, reject } = requestQueue.shift()
    resolve()
    setTimeout(() => {
      isRequesting = false
      processQueue()
    }, 100)
  }
}

// 添加请求到队列
const enqueueRequest = (promise) => {
  return new Promise((resolve, reject) => {
    requestQueue.push({ resolve, reject })
    processQueue()
    promise.then(resolve).catch(reject)
  })
}

// 主请求函数
function request(options = {}) {
  const { 
    url, 
    method = 'GET', 
    data = {}, 
    header = {}, 
    showLoading: shouldShowLoading = true,
    noToast = false
  } = options
  
  // 网络检查
  return checkNetwork()
    .then(() => {
      // 显示加载状态
      if (shouldShowLoading) {
        showLoading(options.loadingTitle)
      }
      
      // 获取token
      const token = wx.getStorageSync('token')
      const finalHeader = {
        ...requestConfig.header,
        ...header
      }
      
      if (token) {
        finalHeader['Authorization'] = `Bearer ${token}`
      }
      
      // 构建请求配置
      const requestOptions = {
        url: `${requestConfig.baseURL}${url}`,
        method: method.toUpperCase(),
        data: data,
        header: finalHeader,
        timeout: options.timeout || requestConfig.timeout
      }
      
      // 请求队列控制
      return enqueueRequest(new Promise((resolve, reject) => {
        wx.request({
          ...requestOptions,
          success: (res) => {
            try {
              const { statusCode, data: responseData } = res
              
              if (statusCode === 200 || statusCode === 201) {
                // 成功响应
                resolve(responseData)
              } else if (statusCode === 401) {
                // 未授权，跳转到登录页
                wx.removeStorageSync('token')
                wx.removeStorageSync('userInfo')
                wx.removeStorageSync('loginTime')
                
                if (!noToast) {
                  showError('登录已过期，请重新登录')
                }
                
                setTimeout(() => {
                  wx.reLaunch({
                    url: '/pages/login/login'
                  })
                }, 1500)
                
                reject(new Error('未授权'))
              } else if (statusCode >= 400 && statusCode < 500) {
                // 客户端错误
                const errorMsg = responseData.message || '请求失败'
                if (!noToast) {
                  showError(errorMsg)
                }
                reject(new Error(errorMsg))
              } else if (statusCode >= 500) {
                // 服务器错误
                const errorMsg = responseData.message || '服务器错误'
                if (!noToast) {
                  showError(errorMsg)
                }
                reject(new Error(errorMsg))
              } else {
                // 其他状态码
                const errorMsg = responseData.message || '未知错误'
                if (!noToast) {
                  showError(errorMsg)
                }
                reject(new Error(errorMsg))
              }
            } catch (error) {
              reject(new Error('解析响应失败'))
            }
          },
          fail: (err) => {
            if (!noToast) {
              showError('网络请求失败，请检查网络连接')
            }
            reject(new Error(err.errMsg || '网络请求失败'))
          }
        })
      }))
    })
    .catch(error => {
      if (!noToast && error.message) {
        showError(error.message)
      }
      throw error
    })
    .finally(() => {
      // 隐藏加载状态
      if (shouldShowLoading) {
        hideLoading()
      }
    })
}

// 便捷方法
const http = {
  get: (url, data, options = {}) => {
    return request({
      url,
      method: 'GET',
      data,
      ...options
    })
  },
  
  post: (url, data, options = {}) => {
    return request({
      url,
      method: 'POST',
      data,
      ...options
    })
  },
  
  put: (url, data, options = {}) => {
    return request({
      url,
      method: 'PUT',
      data,
      ...options
    })
  },
  
  delete: (url, options = {}) => {
    return request({
      url,
      method: 'DELETE',
      ...options
    })
  },
  
  upload: (url, filePath, formData, options = {}) => {
    return new Promise((resolve, reject) => {
      wx.uploadFile({
        url: `${requestConfig.baseURL}${url}`,
        filePath: filePath,
        name: 'file',
        formData: formData,
        success: (res) => {
          try {
            const data = JSON.parse(res.data)
            if (res.statusCode === 200) {
              resolve(data)
            } else {
              reject(new Error(data.message || '上传失败'))
            }
          } catch (error) {
            reject(new Error('解析上传响应失败'))
          }
        },
        fail: (err) => {
          reject(new Error(err.errMsg || '上传失败'))
        }
      })
    })
  }
}

// 重试机制
const retryRequest = (requestFn, maxRetries = 3, delay = 1000) => {
  return new Promise((resolve, reject) => {
    let retryCount = 0
    
    const attempt = () => {
      requestFn()
        .then(resolve)
        .catch(error => {
          retryCount++
          if (retryCount < maxRetries) {
            setTimeout(attempt, delay * retryCount)
          } else {
            reject(error)
          }
        })
    }
    
    attempt()
  })
}

module.exports = {
  request,
  http,
  retryRequest,
  showLoading,
  hideLoading,
  showError
}