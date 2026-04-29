const auth = require('../../api/auth')
const { showError, showLoading, hideLoading } = require('../../utils/request')

Page({
  data: {
    email: '',
    code: '',
    loading: false,
    countdown: 0
  },

  onLoad(options) {
    // 微信登录后绑定邮箱
    const userInfo = wx.getStorageSync('userInfo')
    if (userInfo && userInfo.email && !userInfo.email.includes('wechat_')) {
      // 已有真实邮箱，无需验证
      wx.navigateBack()
    }
  },

  onEmailInput(e) {
    this.setData({ email: e.detail.value })
  },

  onCodeInput(e) {
    this.setData({ code: e.detail.value })
  },

  async sendVerifyCode() {
    const { email } = this.data
    if (!email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      showError('请输入正确的邮箱地址')
      return
    }

    try {
      showLoading('发送中...')
      await auth.resendVerification(email)
      hideLoading()
      wx.showToast({ title: '验证码已发送', icon: 'success' })
      this.startCountdown()
    } catch (error) {
      hideLoading()
      showError(error.message || '发送失败')
    }
  },

  async handleVerify() {
    const { email, code } = this.data

    if (!email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      showError('请输入正确的邮箱地址')
      return
    }
    if (!code || code.trim().length === 0) {
      showError('请输入验证码')
      return
    }

    this.setData({ loading: true })
    showLoading('验证中...')

    try {
      await auth.verify({ email, code })
      hideLoading()
      wx.showToast({ title: '绑定成功', icon: 'success' })
      setTimeout(() => wx.navigateBack(), 1000)
    } catch (error) {
      hideLoading()
      showError(error.message || '验证失败')
    } finally {
      this.setData({ loading: false })
    }
  },

  startCountdown() {
    this.setData({ countdown: 60 })
    this._timer = setInterval(() => {
      if (this.data.countdown <= 1) {
        clearInterval(this._timer)
        this.setData({ countdown: 0 })
      } else {
        this.setData({ countdown: this.data.countdown - 1 })
      }
    }, 1000)
  },

  onUnload() {
    if (this._timer) clearInterval(this._timer)
  }
})