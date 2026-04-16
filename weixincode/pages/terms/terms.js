Page({
  data: {
    loading: false
  },

  onLoad() {
    wx.setNavigationBarTitle({
      title: '用户协议'
    })
  },

  onShareAppMessage() {
    return {
      title: '志愿汇 - 用户协议',
      path: '/pages/index/index'
    }
  }
})
