const auth = require('../../api/auth')
const { showError, showLoading, hideLoading } = require('../../utils/request')

Page({
  data: {
    loading: false
  },

  onLoad() {
    // 微信登录场景下，密码重置验证页已无实际用途
    // 如果通过其他方式进入，直接引导微信登录
  },

  /**
   * 引导微信登录
   */
  async handleWechatLogin() {
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