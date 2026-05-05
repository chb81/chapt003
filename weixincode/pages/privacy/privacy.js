Page({
  data: {
    loading: false
  },

  onLoad() {
    wx.setNavigationBarTitle({
      title: '隐私政策'
    })
  },

  onShareAppMessage() {
    return {
      title: '中考志愿填报 - 隐私政策',
      path: '/pages/index/index'
    }
  }
})
