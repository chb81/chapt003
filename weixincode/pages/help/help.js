const volunteer = require('../../api/volunteer')

Page({
  data: {
    activeTab: 'faq',
    tabs: [
      { key: 'faq', label: '常见问题' },
      { key: 'feedback', label: '意见反馈' }
    ],
    faqList: [
      {
        question: '如何填报中考志愿？',
        answer: '在学校列表页面浏览学校信息，将心仪的学校添加到志愿方案中，调整好顺序后提交即可。'
      },
      {
        question: '录取概率是如何计算的？',
        answer: '系统根据您的历史成绩信息和学校历年录取分数线，通过算法模型预测录取概率，仅供参考。'
      },
      {
        question: '志愿提交后还能修改吗？',
        answer: '志愿方案提交后不可修改，请在提交前仔细核对。您可以先创建模拟方案进行测试。'
      },
      {
        question: '如何查看学校详情？',
        answer: '在学校列表页面，点击学校卡片即可查看学校的详细信息，包括历年录取分数线、招生计划等。'
      },
      {
        question: '智能推荐是怎么工作的？',
        answer: '系统根据您的成绩、偏好设置和学校历史数据，为您推荐最适合的学校组合。'
      }
    ],
    expandedFaqIndex: -1,
    feedbackForm: {
      type: '功能建议',
      content: '',
      contact: ''
    },
    feedbackTypes: [
      '功能建议',
      '问题反馈',
      '使用咨询',
      '其他'
    ],
    submitting: false
  },

  onLoad(options) {
    if (options.tab) {
      this.setData({ activeTab: options.tab })
    }
    this.loadHelpDocuments()
  },

  async loadHelpDocuments() {
    try {
      const response = await volunteer.getHelpDocuments()
      const data = response.data || response || []
      if (Array.isArray(data) && data.length > 0) {
        const docsAsFaq = data.slice(0, 10).map(doc => ({
          question: doc.title,
          answer: doc.content
        }))
        this.setData({
          faqList: [...this.data.faqList, ...docsAsFaq]
        })
      }
    } catch (error) {
      console.error('加载帮助文档失败:', error)
    }
  },

  switchTab(e) {
    const tab = e.currentTarget.dataset.tab
    this.setData({ activeTab: tab })
  },

  toggleFaq(e) {
    const index = e.currentTarget.dataset.index
    const expandedFaqIndex = this.data.expandedFaqIndex === index ? -1 : index
    this.setData({ expandedFaqIndex })
  },

  handleContentInput(e) {
    this.setData({
      'feedbackForm.content': e.detail.value
    })
  },

  handleContactInput(e) {
    this.setData({
      'feedbackForm.contact': e.detail.value
    })
  },

  async submitFeedback() {
    const { content } = this.data.feedbackForm

    if (!content.trim()) {
      wx.showToast({ title: '请填写反馈内容', icon: 'none' })
      return
    }

    if (content.length < 10) {
      wx.showToast({ title: '反馈内容至少10个字', icon: 'none' })
      return
    }

    this.setData({ submitting: true })
    wx.showLoading({ title: '提交中...' })

    try {
      wx.showToast({ title: '感谢您的反馈', icon: 'success' })
      this.setData({
        feedbackForm: { type: '功能建议', content: '', contact: '' }
      })
    } catch (error) {
      console.error('提交反馈失败:', error)
      wx.showToast({ title: '提交失败，请重试', icon: 'error' })
    } finally {
      this.setData({ submitting: false })
      wx.hideLoading()
    }
  },

  onShareAppMessage() {
    return {
      title: '中考志愿填报系统',
      path: '/pages/index/index'
    }
  }
})
