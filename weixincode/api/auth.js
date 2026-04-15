const app = getApp()
const { request } = require('../utils/request')

// 用户登录
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'POST',
    data
  })
}

// 用户注册
export function register(data) {
  return request({
    url: '/auth/register',
    method: 'POST',
    data
  })
}

// 用户退出登录
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'POST'
  })
}

// 获取当前用户信息
export function getCurrentUser() {
  return request({
    url: '/users/current',
    method: 'GET'
  })
}

// 更新用户信息
export function updateUser(data) {
  return request({
    url: '/users/current',
    method: 'PUT',
    data
  })
}

// 更新个人资料
export function updateProfile(data) {
  return request({
    url: '/users/profile',
    method: 'PUT',
    data
  })
}

// 获取用户信息
export function getUserInfo() {
  return request({
    url: '/users/current',
    method: 'GET'
  })
}

// 修改密码
export function changePassword(data) {
  return request({
    url: '/auth/change-password',
    method: 'POST',
    data
  })
}

// 发送验证码
export function sendVerificationCode(data) {
  return request({
    url: '/auth/send-verification-code',
    method: 'POST',
    data
  })
}

// 重置密码
export function resetPassword(data) {
  return request({
    url: '/auth/reset-password',
    method: 'POST',
    data
  })
}

// 验证邮箱
export function verifyEmail(data) {
  return request({
    url: '/auth/verify-email',
    method: 'POST',
    data
  })
}

// 验证手机号
export function verifyMobile(data) {
  return request({
    url: '/auth/verify-mobile',
    method: 'POST',
    data
  })
}

// 检查登录状态
export function checkLoginStatus() {
  const token = wx.getStorageSync('token')
  if (!token) {
    return false
  }
  
  // 检查token是否过期
  const loginTime = wx.getStorageSync('loginTime')
  const currentTime = new Date().getTime()
  const tokenExpireTime = app.globalData.tokenExpireTime
  
  if (loginTime && (currentTime - loginTime) > tokenExpireTime) {
    wx.removeStorageSync('token')
    wx.removeStorageSync('userInfo')
    wx.removeStorageSync('loginTime')
    return false
  }
  
  return true
}