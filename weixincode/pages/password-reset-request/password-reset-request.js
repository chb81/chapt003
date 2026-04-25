const auth = require('../../api/auth')
const { showError, showLoading, hideLoading } = require('../../utils/request')

Page({
  data: {
    loading: false,
    isWechatUser: false
  },

  onLoad() {
    // 微信登录用户不需要密码重置
    const userInfo = wx.getStorageSync('userInfo')
    if (userInfo && userInfo.isWechatUser) {
      this.setData({ isWechatUser: true })
    }
  },

  /**
   * 重新微信登录（微信用户的“重置密码”方式）
   */
  async handleWechatRelogin() {
    this.setData({ loading: true })
    showLoading('登录中...')

    try {
      const result = await auth.performWechatLogin()
      hideLoading()

      if (result.success) {
        wx.showToast({ title: '登录成功', icon: 'success' })
        setTimeout(() => {
          wx.switchTab({ url: '/pages/index/index' })
        }, 1000)
      } else {
        showError(result.message || '登录失败')
      }
    } catch (error) {
      hideLoading()
      showError('微信登录失败')
    } finally {
      this.setData({ loading: false })
    }
  }
})