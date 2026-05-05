const adminApi = require('../../api/admin')

Page({
  data: {
    quotas: [],
    loading: false,
    currentPage: 0,
    totalPages: 1,
    filterYear: null,
    filterSourceSchool: '',
    yearIndex: -1,
    yearRange: [],
    showForm: false,
    editingId: null,
    formData: {
      schoolId: '',
      sourceSchoolName: '',
      sourceSchoolCity: '',
      sourceSchoolDistrict: '',
      year: '',
      quotaCount: '0',
      admissionScore: '',
      unifiedScore: '',
      scoreDifference: '',
      policyRule: ''
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
    if (self.data.filterYear) {
      params.year = self.data.filterYear
    }
    if (self.data.filterSourceSchool) {
      params.sourceSchoolName = self.data.filterSourceSchool
    }

    return adminApi.getAllocationQuotas(params).then(function (res) {
      var data = res.data || res
      var pageData = data.content || data
      self.setData({
        quotas: pageData.content || pageData || [],
        totalPages: pageData.totalPages || 1,
        loading: false
      })
    }).catch(function (err) {
      self.setData({ loading: false })
      wx.showToast({ title: '加载失败', icon: 'none' })
    })
  },

  onYearChange: function (e) {
    var idx = e.detail.value
    this.setData({
      yearIndex: idx,
      filterYear: this.data.yearRange[idx]
    })
    this.loadData()
  },

  onSourceSchoolInput: function (e) {
    this.setData({ filterSourceSchool: e.detail.value })
  },

  onSearch: function () {
    this.setData({ currentPage: 0 })
    this.loadData()
  },

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
        schoolId: '',
        sourceSchoolName: '',
        sourceSchoolCity: '',
        sourceSchoolDistrict: '',
        year: this.data.filterYear ? String(this.data.filterYear) : '',
        quotaCount: '0',
        admissionScore: '',
        unifiedScore: '',
        scoreDifference: '',
        policyRule: ''
      }
    })
  },

  onEdit: function (e) {
    var item = e.currentTarget.dataset.item
    this.setData({
      showForm: true,
      editingId: item.id,
      formData: {
        schoolId: String(item.schoolId || ''),
        sourceSchoolName: item.sourceSchoolName || '',
        sourceSchoolCity: item.sourceSchoolCity || '',
        sourceSchoolDistrict: item.sourceSchoolDistrict || '',
        year: String(item.year || ''),
        quotaCount: String(item.quotaCount || 0),
        admissionScore: item.admissionScore ? String(item.admissionScore) : '',
        unifiedScore: item.unifiedScore ? String(item.unifiedScore) : '',
        scoreDifference: item.scoreDifference ? String(item.scoreDifference) : '',
        policyRule: item.policyRule || ''
      }
    })
  },

  onDelete: function (e) {
    var id = e.currentTarget.dataset.id
    var name = e.currentTarget.dataset.name
    var self = this
    wx.showModal({
      title: '确认删除',
      content: '确定要删除 ' + (name || '') + ' 的分配生名额吗？',
      success: function (res) {
        if (res.confirm) {
          adminApi.deleteAllocationQuota(id).then(function () {
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
    var value = e.detail.value
    var formData = this.data.formData
    formData[field] = value
    this.setData({ formData: formData })
  },

  onCancelForm: function () {
    this.setData({ showForm: false, editingId: null })
  },

  onSaveForm: function () {
    var fd = this.data.formData
    if (!fd.schoolId || !fd.sourceSchoolName || !fd.year) {
      wx.showToast({ title: '请填写必填项', icon: 'none' })
      return
    }

    var data = {
      schoolId: Number(fd.schoolId),
      sourceSchoolName: fd.sourceSchoolName,
      sourceSchoolCity: fd.sourceSchoolCity || null,
      sourceSchoolDistrict: fd.sourceSchoolDistrict || null,
      year: Number(fd.year),
      quotaCount: Number(fd.quotaCount) || 0,
      admissionScore: fd.admissionScore ? Number(fd.admissionScore) : null,
      unifiedScore: fd.unifiedScore ? Number(fd.unifiedScore) : null,
      scoreDifference: fd.scoreDifference ? Number(fd.scoreDifference) : null,
      policyRule: fd.policyRule || null
    }

    var self = this
    var promise
    if (self.data.editingId) {
      promise = adminApi.updateAllocationQuota(self.data.editingId, data)
    } else {
      promise = adminApi.createAllocationQuota(data)
    }

    promise.then(function () {
      wx.showToast({ title: '保存成功', icon: 'success' })
      self.setData({ showForm: false, editingId: null })
      self.loadData()
    }).catch(function () {
      wx.showToast({ title: '保存失败', icon: 'none' })
    })
  }
})
