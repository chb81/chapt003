const admin = require('../../api/admin')

Page({
  data: {
    activeTab: 'faq',
    tabs: [
      { key: 'faq', label: '常见问题' },
      { key: 'feedback', label: '意见反馈' }
    ],
    faqList: [
      {
        question: '如何申请志愿服务项目？',
        answer: '在首页或项目页面找到感兴趣的项目，点击项目卡片查看详情，然后点击"立即申请"按钮即可提交申请。我们会尽快审核您的申请。'
      },
      {
        question: '申请后多久能得到回复？',
        answer: '通常情况下，我们会在1-3个工作日内审核您的申请。紧急项目可能会更快处理。您可以在"我的申请"页面查看申请状态。'
      },
      {
        question: '如何记录志愿服务时长？',
        answer: '项目完成后，组织方会为您记录志愿服务时长。您可以在个人中心查看累计的志愿时长。如有疑问，请联系项目组织方。'
      },
      {
        question: '如何修改个人信息？',
        answer: '进入个人中心，点击头像或昵称，进入编辑页面即可修改您的个人信息。部分信息需要通过实名认证后才能修改。'
      },
      {
        question: '志愿服务证书如何获取？',
        answer: '当您完成一定数量的志愿服务后，系统会自动为您生成志愿服务证书。您可以在个人中心"志愿证书"页面查看和下载。'
      },
      {
        question: '如何联系客服？',
        answer: '您可以通过"意见反馈"功能向我们提交问题，我们会尽快回复。紧急问题请拨打客服电话：400-XXX-XXXX'
      }
    ],
    expandedFaqIndex: -1,
    feedbackForm: {
      type: '功能建议',
      content: '',
      contact: '',
      images: []
    },
    feedbackTypes: [
      '功能建议',
      '问题反馈',
      '使用咨询',
      '其他'
    ],
    showTypePicker: false,
    submitting: false
  },

  onLoad(options) {
    if (options.tab) {
      this.setData({ activeTab: options.tab })
    }
  },

  switchTab(e) {
    const tab = e.currentTarget.dataset.tab
    this.setData({ activeTab: tab })
  },

  toggleFaq(e) {
    const index = e.currentTarget.dataset.index
    const expandedIndex = this.data.expandedFaqIndex === index ? -1 : index
    this.setData({ expandedFaqIndex })
  },

  showTypePicker() {
    this.setData({ showTypePicker: true })
  },

  hideTypePicker() {
    this.setData({ showTypePicker: false })
  },

  selectFeedbackType(e) {
    const index = e.detail.value
    const type = this.data.feedbackTypes[index]
    this.setData({
      'feedbackForm.type': type,
      showTypePicker: false
    })
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

  chooseImage() {
    const remainingCount = 3 - this.data.feedbackForm.images.length
    
    if (remainingCount <= 0) {
      wx.showToast({
        title: '最多上传3张图片',
        icon: 'none'
      })
      return
    }
    
    wx.chooseImage({
      count: remainingCount,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const tempFilePaths = res.tempFilePaths
        const images = [...this.data.feedbackForm.images, ...tempFilePaths]
        
        this.setData({
          'feedbackForm.images': images
        })
      }
    })
  },

  removeImage(e) {
    const index = e.currentTarget.dataset.index
    const images = [...this.data.feedbackForm.images]
    images.splice(index, 1)
    
    this.setData({
      'feedbackForm.images': images
    })
  },

  async submitFeedback() {
    const { content, contact, type } = this.data.feedbackForm
    
    if (!content.trim()) {
      wx.showToast({
        title: '请填写反馈内容',
        icon: 'none'
      })
      return
    }
    
    if (content.length < 10) {
      wx.showToast({
        title: '反馈内容至少10个字',
        icon: 'none'
      })
      return
    }
    
    this.setData({ submitting: true })
    wx.showLoading({ title: '提交中...' })
    
    try {
      const response = await admin.submitFeedback({
        type,
        content,
        contact,
        images: this.data.feedbackForm.images
      })
      
      if (response.code === 200) {
        wx.showToast({
          title: '提交成功',
          icon: 'success'
        })
        
        this.setData({
          feedbackForm: {
            type: '功能建议',
            content: '',
            contact: '',
            images: []
          }
        })
      } else {
        wx.showToast({
          title: response.message || '提交失败',
          icon: 'error'
        })
      }
    } catch (error) {
      console.error('提交反馈失败:', error)
      wx.showToast({
        title: '提交失败，请重试',
        icon: 'error'
      })
    } finally {
      this.setData({ submitting: false })
      wx.hideLoading()
    }
  },

  previewImage(e) {
    const index = e.currentTarget.dataset.index
    wx.previewImage({
      current: this.data.feedbackForm.images[index],
      urls: this.data.feedbackForm.images
    })
  },

  handleContact() {
    wx.makePhoneCall({
      phoneNumber: '400-XXX-XXXX'
    })
  },

  handleShare() {
    return {
      title: '志愿汇 - 让爱心传递',
      path: '/pages/index/index'
    }
  },

  onShareAppMessage() {
    return this.handleShare()
  },

  onShareTimeline() {
    return this.handleShare()
  }
})
