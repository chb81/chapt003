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
      title: '志愿汇 - 隐私政策',
      path: '/pages/index/index'
    }
  }
})
