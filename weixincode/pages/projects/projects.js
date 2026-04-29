const app = getApp()
const volunteer = require('../../api/volunteer')
const auth = require('../../api/auth')

Page({
  data: {
    schools: [],
    loading: false,
    refreshing: false,
    hasMore: true,
    page: 0,
    pageSize: 10,
    filters: {
      city: '',
      district: '',
      schoolType: ''
    },
    searchKeyword: '',
    activeTab: 'all',
    schoolTypes: [
      { value: 'KEY_HIGH_SCHOOL', label: '重点高中' },
      { value: 'REGULAR_HIGH_SCHOOL', label: '普通高中' },
      { value: 'VOCATIONAL_HIGH_SCHOOL', label: '职业高中' }
    ],
    cities: [],
    districts: [],
    currentSchool: null,
    showDetailModal: false,
    admissionProbability: null
  },

  onLoad() {
    this.loadCities()
    this.loadSchools()
  },

  onShow() {
    if (this.data.schools.length === 0) {
      this.loadSchools()
    }
  },

  onPullDownRefresh() {
    this.refreshSchools()
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadMoreSchools()
    }
  },

  async loadCities() {
    try {
      const response = await volunteer.getCities()
      const data = response.data || response || []
      this.setData({
        cities: Array.isArray(data) ? data : []
      })
    } catch (error) {
      console.error('加载城市列表失败:', error)
    }
  },

  async loadDistricts(city) {
    if (!city) {
      this.setData({ districts: [] })
      return
    }
    try {
      const response = await volunteer.getDistricts(city)
      const data = response.data || response || []
      this.setData({
        districts: Array.isArray(data) ? data : []
      })
    } catch (error) {
      console.error('加载区县列表失败:', error)
    }
  },

  async loadSchools() {
    if (this.data.loading) return

    this.setData({ loading: true })
    wx.showLoading({ title: '加载中...' })

    try {
      const params = {
        page: 0,
        size: this.data.pageSize,
        keyword: this.data.searchKeyword || undefined,
        city: this.data.filters.city || undefined,
        district: this.data.filters.district || undefined,
        type: this.data.filters.schoolType || undefined
      }

      Object.keys(params).forEach(key => {
        if (params[key] === undefined || params[key] === '') delete params[key]
      })

      const response = await volunteer.getSchoolList(params)
      const data = response.data || response || {}
      const schoolList = data.schools || data.content || []

      this.setData({
        schools: schoolList,
        hasMore: schoolList.length >= this.data.pageSize,
        page: 1,
        loading: false
      })

    } catch (error) {
      console.error('加载学校列表失败:', error)
      wx.showToast({ title: '加载失败', icon: 'error' })
    } finally {
      wx.hideLoading()
    }
  },

  async refreshSchools() {
    if (this.data.refreshing) return

    this.setData({ refreshing: true })

    try {
      const params = {
        page: 0,
        size: this.data.pageSize,
        keyword: this.data.searchKeyword || undefined,
        city: this.data.filters.city || undefined,
        district: this.data.filters.district || undefined,
        type: this.data.filters.schoolType || undefined
      }

      Object.keys(params).forEach(key => {
        if (params[key] === undefined || params[key] === '') delete params[key]
      })

      const response = await volunteer.getSchoolList(params)
      const data = response.data || response || {}
      const schoolList = data.schools || data.content || []

      this.setData({
        schools: schoolList,
        hasMore: schoolList.length >= this.data.pageSize,
        page: 1,
        refreshing: false
      })

      wx.stopPullDownRefresh()

    } catch (error) {
      console.error('刷新学校列表失败:', error)
      this.setData({ refreshing: false })
    }
  },

  async loadMoreSchools() {
    if (!this.data.hasMore || this.data.loading) return

    this.setData({ loading: true })

    try {
      const params = {
        page: this.data.page,
        size: this.data.pageSize,
        keyword: this.data.searchKeyword || undefined,
        city: this.data.filters.city || undefined,
        district: this.data.filters.district || undefined,
        type: this.data.filters.schoolType || undefined
      }

      Object.keys(params).forEach(key => {
        if (params[key] === undefined || params[key] === '') delete params[key]
      })

      const response = await volunteer.getSchoolList(params)
      const data = response.data || response || {}
      const schoolList = data.schools || data.content || []

      this.setData({
        schools: [...this.data.schools, ...schoolList],
        hasMore: schoolList.length >= this.data.pageSize,
        page: this.data.page + 1,
        loading: false
      })

    } catch (error) {
      console.error('加载更多学校失败:', error)
      this.setData({ loading: false })
    }
  },

  onSearchInput(e) {
    this.setData({ searchKeyword: e.detail.value })
  },

  onSearchConfirm() {
    this.loadSchools()
  },

  onCityChange(e) {
    const index = e.detail.value
    const city = this.data.cities[index] || ''
    this.setData({ 'filters.city': city, 'filters.district': '' })
    this.loadDistricts(city)
    this.loadSchools()
  },

  onDistrictChange(e) {
    const index = e.detail.value
    const district = this.data.districts[index] || ''
    this.setData({ 'filters.district': district })
    this.loadSchools()
  },

  onTypeChange(e) {
    const type = e.currentTarget.dataset.value
    this.setData({
      'filters.schoolType': type,
      activeTab: type || 'all'
    })
    this.loadSchools()
  },

  clearFilters() {
    this.setData({
      filters: { city: '', district: '', schoolType: '' },
      activeTab: 'all',
      searchKeyword: '',
      districts: []
    })
    this.loadSchools()
  },

  async showSchoolDetail(e) {
    const { id } = e.currentTarget.dataset
    try {
      const response = await volunteer.getSchoolDetail(id)
      const data = response.data || response
      this.setData({
        currentSchool: data,
        showDetailModal: true
      })

      const isLoggedIn = auth.checkLoginStatus()
      if (isLoggedIn) {
        this.calculateProbability(id)
      }
    } catch (error) {
      console.error('获取学校详情失败:', error)
      wx.showToast({ title: '获取详情失败', icon: 'error' })
    }
  },

  async calculateProbability(schoolId) {
    try {
      const response = await volunteer.calculateAdmissionProbability(schoolId)
      const data = response.data || response
      this.setData({ admissionProbability: data })
    } catch (error) {
      console.error('计算录取概率失败:', error)
      this.setData({ admissionProbability: null })
    }
  },

  hideDetailModal() {
    this.setData({
      showDetailModal: false,
      currentSchool: null,
      admissionProbability: null
    })
  },

  getSchoolTypeLabel(value) {
    const type = this.data.schoolTypes.find(t => t.value === value)
    return type ? type.label : value || ''
  },

  formatDate(dateString) {
    if (!dateString) return ''
    const date = new Date(dateString)
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
  }
})
