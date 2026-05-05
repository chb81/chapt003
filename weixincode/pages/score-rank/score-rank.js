const volunteer = require('../../api/volunteer')

Page({
  data: {
    totalScore: '',
    cities: ['北京', '上海', '广州', '深圳', '杭州', '南京', '武汉', '成都', '重庆', '西安'],
    cityIndex: 0,
    years: ['2024', '2023', '2022'],
    yearIndex: 0,
    converting: false,
    rankData: null
  },

  onScoreInput(e) {
    this.setData({ totalScore: e.detail.value })
  },

  onCityChange(e) {
    this.setData({ cityIndex: e.detail.value })
  },

  onYearChange(e) {
    this.setData({ yearIndex: e.detail.value })
  },

  async doConvert() {
    const { totalScore, cities, cityIndex, years, yearIndex } = this.data
    if (!totalScore || isNaN(parseFloat(totalScore))) {
      wx.showToast({ title: '请输入有效分数', icon: 'none' })
      return
    }
    this.setData({ converting: true, rankData: null })
    try {
      const response = await volunteer.convertScoreToRank(
        parseFloat(totalScore),
        cities[cityIndex],
        parseInt(years[yearIndex])
      )
      const data = response.data || response
      this.setData({ rankData: data, converting: false })
    } catch (error) {
      console.error('换算失败:', error)
      wx.showToast({ title: '换算失败，请重试', icon: 'none' })
      this.setData({ converting: false })
    }
  },

  async getMyRank() {
    this.setData({ converting: true, rankData: null })
    try {
      const response = await volunteer.getMyRank()
      const data = response.data || response
      if (data.totalScore) {
        this.setData({ totalScore: String(data.totalScore) })
      }
      this.setData({ rankData: data, converting: false })
    } catch (error) {
      console.error('获取位次失败:', error)
      wx.showToast({ title: '请先填写成绩信息', icon: 'none' })
      this.setData({ converting: false })
    }
  },

  onShareAppMessage() {
    return {
      title: '中考志愿填报 - 分数位次换算',
      path: '/pages/index/index'
    }
  }
})
