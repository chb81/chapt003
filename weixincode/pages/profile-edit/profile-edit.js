Page({
  onLoad: function() {
    wx.redirectTo({
      url: '/pages/edit-profile/edit-profile',
      fail: function() {
        wx.navigateBack()
      }
    })
  }
})
