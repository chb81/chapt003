const adminApi = require('../../api/admin')

Page({
  data: {
    policies: [],
    loading: false,
    currentPage: 0,
    totalPages: 1,
    filterYear: null,
    filterCity: '',
    filterDistrict: '',
    yearIndex: -1,
    yearRange: [],
    policyTypes: ['分配生', '指标生', '特长生'],
    policyTypeIndex: -1,
    showForm: false,
    editingId: null,
    formData: {
      city: '',
      district: '',
      year: '',
      policyName: '',
      policyType: '',
      totalQuotaPercentage: '',
      minScoreGap: '',
      eligibleConditions: '',
      description: ''
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
      size: 10
    }
    if (self.data.filterYear) params.year = self.data.filterYear
    if (self.data.filterCity) params.city = self.data.filterCity
    if (self.data.filterDistrict) params.district = self.data.filterDistrict

    return adminApi.getAllocationPolicies(params).then(function (res) {
      var data = res.data || res
      var pageData = data.content || data
      self.setData({
        policies: pageData.content || pageData || [],
        totalPages: pageData.totalPages || 1,
        loading: false
      })
    }).catch(function () {
      self.setData({ loading: false })
      wx.showToast({ title: '加载失败', icon: 'none' })
    })
  },

  onYearChange: function (e) {
    this.setData({
      yearIndex: e.detail.value,
      filterYear: this.data.yearRange[e.detail.value]
    })
    this.loadData()
  },

  onCityInput: function (e) { this.setData({ filterCity: e.detail.value }) },
  onDistrictInput: function (e) { this.setData({ filterDistrict: e.detail.value }) },
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
      policyTypeIndex: -1,
      formData: {
        city: this.data.filterCity || '',
        district: this.data.filterDistrict || '',
        year: this.data.filterYear ? String(this.data.filterYear) : '',
        policyName: '',
        policyType: '',
        totalQuotaPercentage: '',
        minScoreGap: '',
        eligibleConditions: '',
        description: ''
      }
    })
  },

  onEdit: function (e) {
    var item = e.currentTarget.dataset.item
    var typeIdx = this.data.policyTypes.indexOf(item.policyType)
    this.setData({
      showForm: true,
      editingId: item.id,
      policyTypeIndex: typeIdx >= 0 ? typeIdx : -1,
      formData: {
        city: item.city || '',
        district: item.district || '',
        year: String(item.year || ''),
        policyName: item.policyName || '',
        policyType: item.policyType || '',
        totalQuotaPercentage: item.totalQuotaPercentage ? String(item.totalQuotaPercentage) : '',
        minScoreGap: item.minScoreGap ? String(item.minScoreGap) : '',
        eligibleConditions: item.eligibleConditions || '',
        description: item.description || ''
      }
    })
  },

  onDelete: function (e) {
    var id = e.currentTarget.dataset.id
    var name = e.currentTarget.dataset.name
    var self = this
    wx.showModal({
      title: '确认删除',
      content: '确定要删除政策"' + (name || '') + '"吗？',
      success: function (res) {
        if (res.confirm) {
          adminApi.deleteAllocationPolicy(id).then(function () {
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

  onPolicyTypeChange: function (e) {
    var idx = e.detail.value
    var formData = this.data.formData
    formData.policyType = this.data.policyTypes[idx]
    this.setData({ policyTypeIndex: idx, formData: formData })
  },

  onCancelForm: function () {
    this.setData({ showForm: false, editingId: null })
  },

  onSaveForm: function () {
    var fd = this.data.formData
    if (!fd.city || !fd.district || !fd.year || !fd.policyName || !fd.policyType) {
      wx.showToast({ title: '请填写必填项', icon: 'none' })
      return
    }

    var data = {
      city: fd.city,
      district: fd.district,
      year: Number(fd.year),
      policyName: fd.policyName,
      policyType: fd.policyType,
      totalQuotaPercentage: fd.totalQuotaPercentage ? Number(fd.totalQuotaPercentage) : null,
      minScoreGap: fd.minScoreGap ? Number(fd.minScoreGap) : null,
      eligibleConditions: fd.eligibleConditions || null,
      description: fd.description || null,
      isActive: true
    }

    var self = this
    var promise = self.data.editingId
      ? adminApi.updateAllocationPolicy(self.data.editingId, data)
      : adminApi.createAllocationPolicy(data)

    promise.then(function () {
      wx.showToast({ title: '保存成功', icon: 'success' })
      self.setData({ showForm: false, editingId: null })
      self.loadData()
    }).catch(function () {
      wx.showToast({ title: '保存失败', icon: 'none' })
    })
  }
})
