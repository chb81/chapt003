const { request } = require('../utils/request')

// ==================== 微信登录相关 ====================

/**
 * 微信登录 - 使用 wx.login 获取 code 换取 token
 * 后端接口: POST /auth/wechat-login
 */
function wechatLogin(code) {
  return request({
    url: '/auth/wechat-login',
    method: 'POST',
    data: { code }
  })
}

/**
 * 静默微信登录 - 自动获取 code 并登录
 * 返回 { token, userId, isNewUser, ... } 或 null(失败)
 */
function silentWechatLogin() {
  return new Promise(function(resolve, reject) {
    wx.login({
      success: function(loginRes) {
        if (loginRes.code) {
          wechatLogin(loginRes.code)
            .then(function(response) {
              resolve(response)
            })
            .catch(function(error) {
              console.error('微信静默登录失败:', error)
              reject(error)
            })
        } else {
          reject(new Error('微信登录获取code失败'))
        }
      },
      fail: function(err) {
        console.error('wx.login 调用失败:', err)
        reject(err)
      }
    })
  })
}

/**
 * 获取微信用户信息（头像、昵称等）
 * 使用 wx.getUserProfile API
 */
function getWechatUserProfile() {
  return new Promise((resolve, reject) => {
    wx.getUserProfile({
      desc: '用于完善个人资料',
      success: (res) => {
        resolve(res.userInfo)
      },
      fail: (err) => {
        reject(err)
      }
    })
  })
}

// ==================== 传统登录（保留，降级方案） ====================

function login(data) {
  return request({
    url: '/auth/login',
    method: 'POST',
    data
  })
}

function register(data) {
  return request({
    url: '/auth/register',
    method: 'POST',
    data
  })
}

function verify(data) {
  return request({
    url: '/auth/verify',
    method: 'POST',
    data
  })
}

function resendVerification(email) {
  return request({
    url: '/auth/resend-verification?email=' + encodeURIComponent(email),
    method: 'POST'
  })
}

function resetPasswordSendCode(data) {
  return request({
    url: '/auth/password-reset/send-code',
    method: 'POST',
    data
  })
}

function resetPasswordVerify(data) {
  return request({
    url: '/auth/password-reset/verify',
    method: 'POST',
    data
  })
}

function getUserProfile() {
  return request({
    url: '/user/profile',
    method: 'GET'
  })
}

function updateUserProfile(data) {
  return request({
    url: '/user/profile',
    method: 'PUT',
    data
  })
}

function getStudentProfile() {
  return request({
    url: '/student/profile',
    method: 'GET'
  })
}

function createOrUpdateStudentProfile(data) {
  return request({
    url: '/student/profile',
    method: 'PUT',
    data
  })
}

function getStudentScore() {
  return request({
    url: '/student/score',
    method: 'GET'
  })
}

function createOrUpdateStudentScore(data) {
  return request({
    url: '/student/score',
    method: 'PUT',
    data
  })
}

function checkLoginStatus() {
  const token = wx.getStorageSync('token')
  if (!token) {
    return false
  }

  const loginTime = wx.getStorageSync('loginTime')
  const currentTime = new Date().getTime()
  const app = getApp()
  const tokenExpireTime = app.globalData.tokenExpireTime

  if (loginTime && (currentTime - loginTime) > tokenExpireTime) {
    // Token 过期，清除登录信息
    wx.removeStorageSync('token')
    wx.removeStorageSync('userInfo')
    wx.removeStorageSync('loginTime')
    return false
  }

  return true
}

/**
 * 保存微信登录结果到本地存储
 */
function saveWechatLoginResult(loginResult) {
  const app = getApp()
  const now = new Date().getTime()

  wx.setStorageSync('token', loginResult.token)
  wx.setStorageSync('loginTime', now)

  const userInfo = {
    id: loginResult.userId,
    userId: loginResult.userId,
    email: loginResult.email,
    mobile: loginResult.mobile,
    phone: loginResult.mobile,
    role: loginResult.role,
    isNewUser: loginResult.isNewUser
  }
  wx.setStorageSync('userInfo', userInfo)

  // 同步全局数据
  app.globalData.token = loginResult.token
  app.globalData.userInfo = userInfo
  app.globalData.loginTime = now

  return userInfo
}

/**
 * 完整的微信一键登录流程
 * 1. 调用 wx.login 获取 code
 * 2. 发送 code 到后端换取 token
 * 3. 保存登录信息
 * 4. 返回登录结果
 */
function performWechatLogin() {
  return silentWechatLogin()
    .then(function(response) {
      const loginResult = response.data || response

      if (loginResult && loginResult.token) {
        const userInfo = saveWechatLoginResult(loginResult)
        return {
          success: true,
          isNewUser: loginResult.isNewUser || false,
          userInfo: userInfo
        }
      } else {
        return { success: false, message: '登录返回数据异常' }
      }
    })
    .catch(function(error) {
      console.error('微信登录失败:', error)
      return { success: false, message: error.message || '微信登录失败' }
    })
}

module.exports = {
  // 微信登录相关（主要）
  wechatLogin,
  silentWechatLogin,
  getWechatUserProfile,
  performWechatLogin,
  saveWechatLoginResult,
  // 传统登录（降级方案）
  login,
  register,
  verify,
  resendVerification,
  resetPasswordSendCode,
  resetPasswordVerify,
  // 通用
  getUserProfile,
  updateUserProfile,
  getStudentProfile,
  createOrUpdateStudentProfile,
  getStudentScore,
  createOrUpdateStudentScore,
  checkLoginStatus
}
