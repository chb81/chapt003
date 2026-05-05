const adminApi = require('../../api/admin')
Page({
  data: { items: [], loading: false, currentPage: 0, totalPages: 1, categories: ['GUIDE', 'FAQ', 'POLICY', 'TUTORIAL', 'OTHER'], categoryIndex: 0, showForm: false, editingId: null, formData: { title: '', category: 'GUIDE', description: '', content: '', readingTime: '5', published: false } },
  onLoad() { this.loadData() },
  loadData() {
    var self = this; self.setData({ loading: true })
    return adminApi.getHelpDocuments({ page: self.data.currentPage, size: 10 }).then(function(res) {
      var data = res.data || res; var pageData = data.content || data
      self.setData({ items: pageData.content || pageData || [], totalPages: pageData.totalPages || 1, loading: false })
    }).catch(function() { self.setData({ loading: false }); wx.showToast({ title: '加载失败', icon: 'none' }) })
  },
  onPrevPage() { if (this.data.currentPage > 0) { this.setData({ currentPage: this.data.currentPage - 1 }); this.loadData() } },
  onNextPage() { if (this.data.currentPage < this.data.totalPages - 1) { this.setData({ currentPage: this.data.currentPage + 1 }); this.loadData() } },
  onAdd() { this.setData({ showForm: true, editingId: null, categoryIndex: 0, formData: { title: '', category: 'GUIDE', description: '', content: '', readingTime: '5', published: false } }) },
  onEdit(e) {
    var item = e.currentTarget.dataset.item
    this.setData({ showForm: true, editingId: item.id, categoryIndex: this.data.categories.indexOf(item.category), formData: { title: item.title, category: item.category, description: item.description || '', content: item.content, readingTime: String(item.readingTime || 5), published: item.published } })
  },
  onTogglePublish(e) {
    var self = this, item = e.currentTarget.dataset.item
    adminApi.toggleHelpDocumentPublish(item.id, !item.published).then(function() { wx.showToast({ title: item.published ? '已下架' : '已发布', icon: 'success' }); self.loadData() })
  },
  onDelete(e) {
    var self = this, id = e.currentTarget.dataset.id
    wx.showModal({ title: '确认删除', content: '确定要删除这篇文档吗？', success: function(res) { if (res.confirm) { adminApi.deleteHelpDocument(id).then(function() { wx.showToast({ title: '删除成功', icon: 'success' }); self.loadData() }) } } })
  },
  onFormInput(e) { var fd = this.data.formData; fd[e.currentTarget.dataset.field] = e.detail.value; this.setData({ formData: fd }) },
  onCategoryChange(e) { var fd = this.data.formData; fd.category = this.data.categories[e.detail.value]; this.setData({ categoryIndex: e.detail.value, formData: fd }) },
  onPublishChange(e) { var fd = this.data.formData; fd.published = e.detail.value; this.setData({ formData: fd }) },
  onCancelForm() { this.setData({ showForm: false, editingId: null }) },
  onSaveForm() {
    var fd = this.data.formData
    if (!fd.title || !fd.category || !fd.content) { wx.showToast({ title: '请填写必填项', icon: 'none' }); return }
    var data = { title: fd.title, category: fd.category, description: fd.description || null, content: fd.content, readingTime: Number(fd.readingTime) || null, published: fd.published }
    var self = this
    var promise = self.data.editingId ? adminApi.updateHelpDocument(self.data.editingId, data) : adminApi.createHelpDocument(data)
    promise.then(function() { wx.showToast({ title: '保存成功', icon: 'success' }); self.setData({ showForm: false, editingId: null }); self.loadData() }).catch(function() { wx.showToast({ title: '保存失败', icon: 'none' }) })
  }
})
