const app = getApp()
const auth = require('../../api/auth')
const { showError, showLoading, hideLoading } = require('../../utils/request')

Page({
  data: {
    loading: false,
    // 传统登录表单（降级方案，默认隐藏）
    showTraditionalLogin: false,
    loginForm: {
      username: '',
      password: ''
    },
    rememberMe: true
  },

  onLoad() {
    // 检查是否已经登录
    if (auth.checkLoginStatus()) {
      wx.switchTab({
        url: '/pages/index/index'
      })
      return
    }
  },

  /**
   * 微信一键登录（主流程）
   */
  async handleWechatLogin() {
    if (this.data.loading) return

    this.setData({ loading: true })
    showLoading('微信登录中...')

    try {
      const result = await auth.performWechatLogin()

      hideLoading()

      if (result.success) {
        wx.showToast({ title: '登录成功', icon: 'success' })

        // 新用户跳转完善资料页
        if (result.isNewUser) {
          setTimeout(() => {
            wx.redirectTo({
              url: '/pages/edit-profile/edit-profile?isNewUser=true'
            })
          }, 1000)
        } else {
          // 老用户直接进首页
          setTimeout(() => {
            wx.switchTab({
              url: '/pages/index/index'
            })
          }, 1000)
        }
      } else {
        showError(result.message || '微信登录失败，请重试')
      }
    } catch (error) {
      hideLoading()
      console.error('微信登录失败:', error)
      showError('微信登录失败，请重试')
    } finally {
      this.setData({ loading: false })
    }
  },

  /**
   * 切换显示传统登录表单
   */
  toggleTraditionalLogin() {
    this.setData({
      showTraditionalLogin: !this.data.showTraditionalLogin
    })
  },

  /**
   * 传统账号密码登录（降级方案）
   */
  onInputChange(e) {
    const { field } = e.currentTarget.dataset
    this.setData({ [`loginForm.${field}`]: e.detail.value })
  },

  handleTraditionalLogin() {
    const { loginForm } = this.data

    if (!loginForm.username || !loginForm.username.trim()) {
      showError('请输入用户名')
      return
    }

    if (!loginForm.password || !loginForm.password.trim()) {
      showError('请输入密码')
      return
    }

    if (loginForm.password.length < 6) {
      showError('密码长度不能少于6位')
      return
    }

    this.setData({ loading: true })
    showLoading('登录中...')

    auth.login(loginForm)
      .then(response => {
        hideLoading()
        if (response.token) {
          wx.setStorageSync('token', response.token)
          wx.setStorageSync('userInfo', response.userInfo || {})
          wx.setStorageSync('loginTime', new Date().getTime())

          app.globalData.token = response.token
          app.globalData.userInfo = response.userInfo

          wx.showToast({ title: '登录成功', icon: 'success' })
          setTimeout(() => {
            wx.switchTab({ url: '/pages/index/index' })
          }, 1000)
        }
      })
      .catch(error => {
        hideLoading()
        showError(error.message || '登录失败')
      })
      .finally(() => {
        this.setData({ loading: false })
      })
  },

  onShow() {},
  onHide() {},
  onUnload() {}
})