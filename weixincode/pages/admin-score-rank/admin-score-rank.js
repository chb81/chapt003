const adminApi = require('../../api/admin')

Page({
  data: {
    scoreRanks: [],
    loading: false,
    currentPage: 0,
    totalPages: 1,
    filterCity: '',
    filterYear: null,
    yearIndex: -1,
    yearRange: [],
    showForm: false,
    editingId: null,
    formData: {
      city: '',
      year: '',
      totalScore: '',
      cityRank: '',
      district: '',
      districtRank: '',
      studentCount: '',
      cumulativeCount: ''
    }
  },

  onLoad: function () {
    this.generateYearRange()
    this.loadData()
  },

  onPullDownRefresh: function () {
    this.setData({ currentPage: 0 })
    this.loadData().then(function () {
      wx.stopPullDownRefresh()
    })
  },

  generateYearRange: function () {
    var currentYear = new Date().getFullYear()
    var years = []
    for (var i = currentYear + 1; i >= currentYear - 5; i--) {
      years.push(i)
    }
    this.setData({ yearRange: years })
  },

  loadData: function () {
    var self = this
    self.setData({ loading: true })
    var params = {
      page: self.data.currentPage,
      size: 20
    }
    if (self.data.filterYear) params.year = self.data.filterYear
    if (self.data.filterCity) params.city = self.data.filterCity

    return adminApi.getScoreRanks(params).then(function (res) {
      var data = res.data || res
      var pageData = data.content || data
      self.setData({
        scoreRanks: pageData.content || pageData || [],
        totalPages: pageData.totalPages || 1,
        loading: false
      })
    }).catch(function () {
      self.setData({ loading: false })
      wx.showToast({ title: '加载失败', icon: 'none' })
    })
  },

  onCityInput: function (e) { this.setData({ filterCity: e.detail.value }) },
  onYearChange: function (e) {
    this.setData({
      yearIndex: e.detail.value,
      filterYear: this.data.yearRange[e.detail.value]
    })
    this.loadData()
  },
  onSearch: function () { this.setData({ currentPage: 0 }); this.loadData() },

  onPrevPage: function () {
    if (this.data.currentPage > 0) {
      this.setData({ currentPage: this.data.currentPage - 1 })
      this.loadData()
    }
  },

  onNextPage: function () {
    if (this.data.currentPage < this.data.totalPages - 1) {
      this.setData({ currentPage: this.data.currentPage + 1 })
      this.loadData()
    }
  },

  onAdd: function () {
    this.setData({
      showForm: true,
      editingId: null,
      formData: {
        city: this.data.filterCity || '',
        year: this.data.filterYear ? String(this.data.filterYear) : '',
        totalScore: '',
        cityRank: '',
        district: '',
        districtRank: '',
        studentCount: '',
        cumulativeCount: ''
      }
    })
  },

  onEdit: function (e) {
    var item = e.currentTarget.dataset.item
    this.setData({
      showForm: true,
      editingId: item.id,
      formData: {
        city: item.city || '',
        year: String(item.year || ''),
        totalScore: item.totalScore ? String(item.totalScore) : '',
        cityRank: item.cityRank ? String(item.cityRank) : '',
        district: item.district || '',
        districtRank: item.districtRank ? String(item.districtRank) : '',
        studentCount: item.studentCount ? String(item.studentCount) : '',
        cumulativeCount: item.cumulativeCount ? String(item.cumulativeCount) : ''
      }
    })
  },

  onDelete: function (e) {
    var id = e.currentTarget.dataset.id
    var self = this
    wx.showModal({
      title: '确认删除',
      content: '确定要删除这条分数位次数据吗？',
      success: function (res) {
        if (res.confirm) {
          adminApi.deleteScoreRank(id).then(function () {
            wx.showToast({ title: '删除成功', icon: 'success' })
            self.loadData()
          }).catch(function () {
            wx.showToast({ title: '删除失败', icon: 'none' })
          })
        }
      }
    })
  },

  onFormInput: function (e) {
    var field = e.currentTarget.dataset.field
    var formData = this.data.formData
    formData[field] = e.detail.value
    this.setData({ formData: formData })
  },

  onCancelForm: function () {
    this.setData({ showForm: false, editingId: null })
  },

  onSaveForm: function () {
    var fd = this.data.formData
    if (!fd.city || !fd.year || !fd.totalScore || !fd.cityRank) {
      wx.showToast({ title: '请填写必填项', icon: 'none' })
      return
    }

    var data = {
      city: fd.city,
      year: Number(fd.year),
      totalScore: Number(fd.totalScore),
      cityRank: Number(fd.cityRank),
      district: fd.district || null,
      districtRank: fd.districtRank ? Number(fd.districtRank) : null,
      studentCount: fd.studentCount ? Number(fd.studentCount) : null,
      cumulativeCount: fd.cumulativeCount ? Number(fd.cumulativeCount) : null
    }

    var self = this
    var promise = self.data.editingId
      ? adminApi.updateScoreRank(self.data.editingId, data)
      : adminApi.createScoreRank(data)

    promise.then(function () {
      wx.showToast({ title: '保存成功', icon: 'success' })
      self.setData({ showForm: false, editingId: null })
      self.loadData()
    }).catch(function () {
      wx.showToast({ title: '保存失败', icon: 'none' })
    })
  }
})
