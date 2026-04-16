const app = getApp()
const { auth } = require('../../api/auth')
const { showError, showLoading, hideLoading } = require('../../utils/request')

Page({
  data: {
    username: '',
    password: '',
    loading: false,
    rememberMe: true,
    loginForm: {
      username: '',
      password: '',
      captcha: ''
    }
  },

  onLoad() {
    // 检查是否已经登录
    if (auth.checkLoginStatus()) {
      wx.redirectTo({
        url: '/pages/index/index'
      })
      return
    }
    
    // 加载保存的登录信息
    const savedUsername = wx.getStorageSync('username')
    if (savedUsername) {
      this.setData({
        'loginForm.username': savedUsername,
        rememberMe: true
      })
    }
  },

  // 输入框变化处理
  onInputChange(e) {
    const { field } = e.currentTarget.dataset
    const { value } = e.detail
    this.setData({
      [`loginForm.${field}`]: value
    })
  },

  // 登录处理
  handleLogin() {
    const { loginForm, rememberMe } = this.data
    
    // 表单验证
    if (!this.validateForm(loginForm)) {
      return
    }
    
    this.setData({ loading: true })
    showLoading('登录中...')
    
    // 调用登录接口
    auth.login(loginForm)
      .then(response => {
        // 保存登录信息
        if (response.token) {
          wx.setStorageSync('token', response.token)
          wx.setStorageSync('userInfo', response.userInfo)
          wx.setStorageSync('loginTime', new Date().getTime())
          
          // 保存用户名
          if (rememberMe) {
            wx.setStorageSync('username', loginForm.username)
          } else {
            wx.removeStorageSync('username')
          }
          
          // 保存全局数据
          app.globalData.token = response.token
          app.globalData.userInfo = response.userInfo
          app.globalData.loginTime = new Date().getTime()
          
          wx.showToast({
            title: '登录成功',
            icon: 'success'
          })
          
          // 跳转到首页
          setTimeout(() => {
            wx.switchTab({
              url: '/pages/index/index'
            })
          }, 1500)
        }
      })
      .catch(error => {
        console.error('登录失败:', error)
        showError(error.message || '登录失败，请检查用户名和密码')
      })
      .finally(() => {
        this.setData({ loading: false })
        hideLoading()
      })
  },

  // 表单验证
  validateForm(formData) {
    if (!formData.username || !formData.username.trim()) {
      showError('请输入用户名')
      return false
    }
    
    if (!formData.password || !formData.password.trim()) {
      showError('请输入密码')
      return false
    }
    
    if (formData.password.length < 6) {
      showError('密码长度不能少于6位')
      return false
    }
    
    return true
  },

  // 注册按钮点击
  handleRegister() {
    wx.navigateTo({
      url: '/pages/register/register'
    })
  },

  // 找回密码
  handleForgotPassword() {
    wx.navigateTo({
      url: '/pages/password-reset-request/password-reset-request'
    })
  },

  // 微信登录（可选）
  handleWechatLogin() {
    wx.getUserProfile({
      desc: '用于完善会员资料',
      success: (res) => {
        const userInfo = res.userInfo
        // 这里可以调用微信登录接口
        console.log('微信登录信息:', userInfo)
        // 示例：可以调用微信登录接口获取openid等
      },
      fail: (err) => {
        console.error('获取用户信息失败:', err)
        showError('微信登录失败')
      }
    })
  },

  // 切换记住密码
  toggleRememberMe() {
    this.setData({
      rememberMe: !this.data.rememberMe
    })
  },

  // 输入框获取焦点
  onFocus(e) {
    const { field } = e.currentTarget.dataset
    this.setData({
      [`loginForm.${field}Focus`]: true
    })
  },

  // 输入框失去焦点
  onBlur(e) {
    const { field } = e.currentTarget.dataset
    this.setData({
      [`loginForm.${field}Focus`]: false
    })
  },

  // 监听键盘事件
  onKeyPress(e) {
    if (e.detail.keyCode === 13) {
      // 回车键触发登录
      this.handleLogin()
    }
  },

  // 页面显示
  onShow() {
    // 可以在这里做一些页面显示时的逻辑
  },

  // 页面隐藏
  onHide() {
    // 可以在这里做一些页面隐藏时的逻辑
  },

  // 页面卸载
  onUnload() {
    // 清理定时器等
  }
})