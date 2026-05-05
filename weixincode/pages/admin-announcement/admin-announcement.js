const adminApi = require('../../api/admin')
Page({
  data: { items: [], loading: false, currentPage: 0, totalPages: 1, types: ['IMPORTANT', 'NORMAL'], typeIndex: 0, showForm: false, editingId: null, formData: { title: '', type: 'NORMAL', content: '', publisher: '', priority: '0' } },
  onLoad() { this.loadData() },
  loadData() {
    var self = this; self.setData({ loading: true })
    return adminApi.getAnnouncements({ page: self.data.currentPage, size: 10 }).then(function(res) {
      var data = res.data || res; var pageData = data.content || data
      var items = (pageData.content || pageData || []).map(function(item) { item.publishedAtStr = item.publishedAt ? item.publishedAt.substring(0, 16).replace('T', ' ') : '-'; return item })
      self.setData({ items: items, totalPages: pageData.totalPages || 1, loading: false })
    }).catch(function() { self.setData({ loading: false }); wx.showToast({ title: '加载失败', icon: 'none' }) })
  },
  onPrevPage() { if (this.data.currentPage > 0) { this.setData({ currentPage: this.data.currentPage - 1 }); this.loadData() } },
  onNextPage() { if (this.data.currentPage < this.data.totalPages - 1) { this.setData({ currentPage: this.data.currentPage + 1 }); this.loadData() } },
  onAdd() { this.setData({ showForm: true, editingId: null, typeIndex: 1, formData: { title: '', type: 'NORMAL', content: '', publisher: '', priority: '0' } }) },
  onEdit(e) {
    var item = e.currentTarget.dataset.item
    this.setData({ showForm: true, editingId: item.id, typeIndex: this.data.types.indexOf(item.type), formData: { title: item.title, type: item.type, content: item.content, publisher: item.publisher || '', priority: String(item.priority || 0) } })
  },
  onDelete(e) {
    var self = this, id = e.currentTarget.dataset.id
    wx.showModal({ title: '确认删除', content: '确定要删除这条公告吗？', success: function(res) { if (res.confirm) { adminApi.deleteAnnouncement(id).then(function() { wx.showToast({ title: '删除成功', icon: 'success' }); self.loadData() }) } } })
  },
  onFormInput(e) { var fd = this.data.formData; fd[e.currentTarget.dataset.field] = e.detail.value; this.setData({ formData: fd }) },
  onTypeChange(e) { var fd = this.data.formData; fd.type = this.data.types[e.detail.value]; this.setData({ typeIndex: e.detail.value, formData: fd }) },
  onCancelForm() { this.setData({ showForm: false, editingId: null }) },
  onSaveForm() {
    var fd = this.data.formData
    if (!fd.title || !fd.type || !fd.content) { wx.showToast({ title: '请填写必填项', icon: 'none' }); return }
    var data = { title: fd.title, type: fd.type, content: fd.content, publisher: fd.publisher || null, priority: Number(fd.priority) || 0 }
    var self = this
    var promise = self.data.editingId ? adminApi.updateAnnouncement(self.data.editingId, data) : adminApi.createAnnouncement(data)
    promise.then(function() { wx.showToast({ title: '保存成功', icon: 'success' }); self.setData({ showForm: false, editingId: null }); self.loadData() }).catch(function() { wx.showToast({ title: '保存失败', icon: 'none' }) })
  }
})
