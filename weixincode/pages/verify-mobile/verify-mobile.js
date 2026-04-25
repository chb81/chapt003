const auth = require('../../api/auth')
const { showError, showLoading, hideLoading } = require('../../utils/request')

Page({
  data: {
    phone: '',
    code: '',
    loading: false,
    countdown: 0
  },

  onLoad(options) {
    // 微信登录后绑定手机号
    const userInfo = wx.getStorageSync('userInfo')
    if (userInfo && userInfo.phone) {
      wx.navigateBack()
    }
  },

  /**
   * 微信快捷获取手机号
   */
  getPhoneNumber(e) {
    if (e.detail.errMsg !== 'getPhoneNumber:ok') {
      console.warn('用户拒绝授权手机号')
      return
    }

    this.setData({ loading: true })
    showLoading('绑定中...')

    auth.updateUserProfile({
      encryptedData: e.detail.encryptedData,
      iv: e.detail.iv
    }).then(() => {
      hideLoading()
      wx.showToast({ title: '绑定成功', icon: 'success' })
      setTimeout(() => wx.navigateBack(), 1000)
    }).catch(error => {
      hideLoading()
      showError(error.message || '绑定失败')
    }).finally(() => {
      this.setData({ loading: false })
    })
  },

  onPhoneInput(e) {
    this.setData({ phone: e.detail.value })
  },

  onCodeInput(e) {
    this.setData({ code: e.detail.value })
  },

  async sendVerifyCode() {
    const { phone } = this.data
    if (!phone || !/^1[3-9]\d{9}$/.test(phone)) {
      showError('请输入正确的手机号')
      return
    }

    try {
      showLoading('发送中...')
      await auth.resetPasswordSendCode({ phone })
      hideLoading()
      wx.showToast({ title: '验证码已发送', icon: 'success' })
      this.startCountdown()
    } catch (error) {
      hideLoading()
      showError(error.message || '发送失败')
    }
  },

  async handleVerify() {
    const { phone, code } = this.data

    if (!phone || !/^1[3-9]\d{9}$/.test(phone)) {
      showError('请输入正确的手机号')
      return
    }
    if (!code || code.trim().length === 0) {
      showError('请输入验证码')
      return
    }

    this.setData({ loading: true })
    showLoading('验证中...')

    try {
      await auth.updateUserProfile({ phone, verificationCode: code })
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