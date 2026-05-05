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
      title: '中考志愿填报 - 用户协议',
      path: '/pages/index/index'
    }
  }
})
