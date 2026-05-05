const adminApi = require('../../api/admin')
Page({
  data: { sessionId: null, session: {}, messages: [], replyContent: '' },
  onLoad(options) { if (options.sessionId) { this.setData({ sessionId: options.sessionId }); this.loadDetail() } },
  loadDetail() {
    var self = this
    adminApi.getCustomerServiceSessionDetail(self.data.sessionId).then(function(res) {
      var data = res.data || res
      var session = data.session || data
      var messages = (data.messages || []).map(function(m) { m.messageTimeStr = m.messageTime ? m.messageTime.substring(11, 16) : ''; return m })
      self.setData({ session: session, messages: messages })
    }).catch(function() { wx.showToast({ title: '加载失败', icon: 'none' }) })
  },
  onReplyInput(e) { this.setData({ replyContent: e.detail.value }) },
  onSendReply() {
    var self = this, content = self.data.replyContent
    if (!content) return
    var adminId = wx.getStorageSync('userInfo').id
    adminApi.replyCustomerServiceMessage(self.data.sessionId, adminId, content).then(function() {
      self.setData({ replyContent: '' }); self.loadDetail()
    }).catch(function() { wx.showToast({ title: '发送失败', icon: 'none' }) })
  },
  onCloseSession() {
    var self = this
    wx.showModal({ title: '关闭会话', content: '确定要关闭此会话吗？', success: function(res) {
      if (res.confirm) { adminApi.closeCustomerServiceSession(self.data.sessionId).then(function() { wx.showToast({ title: '已关闭', icon: 'success' }); self.loadDetail() }) }
    }})
  }
})
