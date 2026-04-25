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

  const app = getApp()
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
  verify,
  resendVerification,
  resetPasswordSendCode,
  resetPasswordVerify,
  getUserProfile,
  updateUserProfile,
  getStudentProfile,
  createOrUpdateStudentProfile,
  getStudentScore,
  createOrUpdateStudentScore,
  checkLoginStatus
}
