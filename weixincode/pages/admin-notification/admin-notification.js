const adminApi = require('../../api/admin')
Page({
  data: { items: [], loading: false, currentPage: 0, totalPages: 1, showForm: false, types: ['SYSTEM', 'REMINDER', 'PROMOTION', 'WARNING'], typeIndex: 0, formData: { title: '', type: 'SYSTEM', content: '', link: '' } },
  onLoad() { this.loadData() },
  loadData() {
    var self = this; self.setData({ loading: true })
    return adminApi.getNotifications({ page: self.data.currentPage, size: 20 }).then(function(res) {
      var data = res.data || res; var pageData = data.content || data
      var items = (pageData.content || pageData || []).map(function(item) { if (item.createdAt) item.createdAt = item.createdAt.substring(0, 16).replace('T', ' '); return item })
      self.setData({ items: items, totalPages: pageData.totalPages || 1, loading: false })
    }).catch(function() { self.setData({ loading: false }); wx.showToast({ title: '加载失败', icon: 'none' }) })
  },
  onPrevPage() { if (this.data.currentPage > 0) { this.setData({ currentPage: this.data.currentPage - 1 }); this.loadData() } },
  onNextPage() { if (this.data.currentPage < this.data.totalPages - 1) { this.setData({ currentPage: this.data.currentPage + 1 }); this.loadData() } },
  onSendAll() { this.setData({ showForm: true, typeIndex: 0, formData: { title: '', type: 'SYSTEM', content: '', link: '' } }) },
  onFormInput(e) { var fd = this.data.formData; fd[e.currentTarget.dataset.field] = e.detail.value; this.setData({ formData: fd }) },
  onTypeChange(e) { var fd = this.data.formData; fd.type = this.data.types[e.detail.value]; this.setData({ typeIndex: e.detail.value, formData: fd }) },
  onCancelForm() { this.setData({ showForm: false }) },
  onConfirmSend() {
    var fd = this.data.formData
    if (!fd.title || !fd.content) { wx.showToast({ title: '请填写必填项', icon: 'none' }); return }
    var data = { title: fd.title, type: fd.type, content: fd.content, link: fd.link || null }
    var self = this
    adminApi.sendNotificationToAll(data).then(function(res) {
      wx.showToast({ title: '发送成功', icon: 'success' }); self.setData({ showForm: false }); self.loadData()
    }).catch(function() { wx.showToast({ title: '发送失败', icon: 'none' }) })
  }
})
