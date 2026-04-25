const app = getApp()
const auth = require('../../api/auth')
const { showError, showLoading, hideLoading } = require('../../utils/request')

Page({
  data: {
    form: {
      nickname: '',
      phone: '',
      gender: '',
      birthday: ''
    },
    wechatUserInfo: null,
    loading: false,
    step: 'authorize' // authorize -> profile
  },

  onLoad() {
    // 检查是否已登录（微信自动创建账号）
    if (!auth.checkLoginStatus()) {
      // 未登录，先引导微信登录
      this.handleWechatLogin()
    } else {
      this.setData({ step: 'profile' })
    }
  },

  /**
   * 微信登录授权
   */
  async handleWechatLogin() {
    this.setData({ loading: true })
    showLoading('微信授权中...')

    try {
      const result = await auth.performWechatLogin()
      hideLoading()

      if (result.success) {
        this.setData({ step: 'profile' })
        wx.showToast({ title: '授权成功', icon: 'success' })
      } else {
        showError(result.message || '微信授权失败')
      }
    } catch (error) {
      hideLoading()
      showError('微信授权失败，请重试')
    } finally {
      this.setData({ loading: false })
    }
  },

  /**
   * 获取微信用户资料
   */
  async handleGetUserProfile() {
    try {
      const wechatInfo = await auth.getWechatUserProfile()
      this.setData({
        wechatUserInfo: wechatInfo,
        'form.nickname': wechatInfo.nickName || '',
        'form.gender': wechatInfo.gender === 1 ? '男' : (wechatInfo.gender === 2 ? '女' : '')
      })
    } catch (error) {
      console.warn('获取微信用户资料失败:', error)
    }
  },

  onInputChange(e) {
    const { field } = e.currentTarget.dataset
    this.setData({ [`form.${field}`]: e.detail.value })
  },

  async handleSubmitProfile() {
    const { nickname, phone } = this.data.form

    if (!nickname || !nickname.trim()) {
      showError('请输入昵称')
      return
    }

    this.setData({ loading: true })
    showLoading('保存中...')

    try {
      await auth.updateUserProfile(this.data.form)
      hideLoading()
      wx.showToast({ title: '保存成功', icon: 'success' })

      setTimeout(() => {
        wx.switchTab({ url: '/pages/index/index' })
      }, 1000)
    } catch (error) {
      hideLoading()
      showError(error.message || '保存失败')
    } finally {
      this.setData({ loading: false })
    }
  },

  skipProfile() {
    wx.switchTab({ url: '/pages/index/index' })
  }
})
