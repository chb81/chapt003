const app = getApp()
const { request } = require('../utils/request')

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

function logout() {
  return request({
    url: '/auth/logout',
    method: 'POST'
  })
}

function getCurrentUser() {
  return request({
    url: '/users/current',
    method: 'GET'
  })
}

function updateUser(data) {
  return request({
    url: '/users/current',
    method: 'PUT',
    data
  })
}

function updateProfile(data) {
  return request({
    url: '/user/profile',
    method: 'PUT',
    data
  })
}

function getUserInfo() {
  return request({
    url: '/users/current',
    method: 'GET'
  })
}

function changePassword(data) {
  return request({
    url: '/auth/change-password',
    method: 'POST',
    data
  })
}

function sendVerificationCode(data) {
  return request({
    url: '/auth/send-verification-code',
    method: 'POST',
    data
  })
}

function resetPassword(data) {
  return request({
    url: '/auth/password-reset/verify',
    method: 'POST',
    data
  })
}

function verifyEmail(data) {
  return request({
    url: '/auth/verify',
    method: 'POST',
    data
  })
}

function verifyMobile(data) {
  return request({
    url: '/auth/verify-mobile',
    method: 'POST',
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
  const tokenExpireTime = app.globalData.tokenExpireTime

  if (loginTime && (currentTime - loginTime) > tokenExpireTime) {
    wx.removeStorageSync('token')
    wx.removeStorageSync('userInfo')
    wx.removeStorageSync('loginTime')
    return false
  }

  return true
}

module.exports = {
  login,
  register,
  logout,
  getCurrentUser,
  updateUser,
  updateProfile,
  getUserInfo,
  changePassword,
  sendVerificationCode,
  resetPassword,
  verifyEmail,
  verifyMobile,
  checkLoginStatus
}
