const adminApi = require('../../api/admin')
Page({
  data: { items: [], loading: false, currentPage: 0, totalPages: 1, stats: {}, statusOptions: ['', 'ACTIVE', 'CLOSED', 'RESOLVED'], statusIndex: 0, filterStatus: '' },
  onLoad() { this.loadData(); this.loadStats() },
  loadData() {
    var self = this; self.setData({ loading: true })
    var params = { page: self.data.currentPage, size: 10 }
    if (self.data.filterStatus) params.status = self.data.filterStatus
    return adminApi.getCustomerServiceSessions(params).then(function(res) {
      var data = res.data || res; var pageData = data.content || data
      var items = (pageData.content || pageData || []).map(function(item) { item.startTimeStr = item.startTime ? item.startTime.substring(0, 16).replace('T', ' ') : '-'; return item })
      self.setData({ items: items, totalPages: pageData.totalPages || 1, loading: false })
    }).catch(function() { self.setData({ loading: false }); wx.showToast({ title: '加载失败', icon: 'none' }) })
  },
  loadStats() { adminApi.getCustomerServiceStats().then(function(res) { var d = res.data || res; if (d) { /* stats set */ } }) },
  onStatusChange(e) { this.setData({ statusIndex: e.detail.value, filterStatus: this.data.statusOptions[e.detail.value] }) },
  onSearch() { this.setData({ currentPage: 0 }); this.loadData() },
  onPrevPage() { if (this.data.currentPage > 0) { this.setData({ currentPage: this.data.currentPage - 1 }); this.loadData() } },
  onNextPage() { if (this.data.currentPage < this.data.totalPages - 1) { this.setData({ currentPage: this.data.currentPage + 1 }); this.loadData() } },
  onViewSession(e) { wx.navigateTo({ url: '/pages/admin-cs-detail/admin-cs-detail?sessionId=' + e.currentTarget.dataset.id }) }
})
