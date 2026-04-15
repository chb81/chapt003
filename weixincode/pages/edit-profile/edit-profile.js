const auth = require('../../api/auth')

Page({
  data: {
    userInfo: null,
    form: {
      nickname: '',
      phone: '',
      email: '',
      realName: '',
      idCard: '',
      gender: '',
      birthday: '',
      address: '',
      avatarUrl: ''
    },
    genderOptions: ['男', '女'],
    showGenderPicker: false,
    showDatePicker: false,
    loading: false,
    submitting: false
  },

  onLoad() {
    this.loadUserInfo()
  },

  loadUserInfo() {
    const userInfo = wx.getStorageSync('userInfo')
    if (userInfo) {
      this.setData({
        userInfo: userInfo,
        form: {
          nickname: userInfo.nickname || '',
          phone: userInfo.phone || '',
          email: userInfo.email || '',
          realName: userInfo.realName || '',
          idCard: userInfo.idCard || '',
          gender: userInfo.gender || '',
          birthday: userInfo.birthday || '',
          address: userInfo.address || '',
          avatarUrl: userInfo.avatarUrl || ''
        }
      })
    }
  },

  handleNicknameInput(e) {
    this.setData({
      'form.nickname': e.detail.value
    })
  },

  handlePhoneInput(e) {
    this.setData({
      'form.phone': e.detail.value
    })
  },

  handleEmailInput(e) {
    this.setData({
      'form.email': e.detail.value
    })
  },

  handleRealNameInput(e) {
    this.setData({
      'form.realName': e.detail.value
    })
  },

  handleIdCardInput(e) {
    this.setData({
      'form.idCard': e.detail.value
    })
  },

  handleAddressInput(e) {
    this.setData({
      'form.address': e.detail.value
    })
  },

  showGenderPicker() {
    this.setData({ showGenderPicker: true })
  },

  hideGenderPicker() {
    this.setData({ showGenderPicker: false })
  },

  handleGenderChange(e) {
    const index = e.detail.value
    const gender = this.data.genderOptions[index]
    this.setData({
      'form.gender': gender,
      showGenderPicker: false
    })
  },

  showDatePicker() {
    this.setData({ showDatePicker: true })
  },

  hideDatePicker() {
    this.setData({ showDatePicker: false })
  },

  handleDateChange(e) {
    this.setData({
      'form.birthday': e.detail.value,
      showDatePicker: false
    })
  },

  chooseAvatar() {
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const tempFilePath = res.tempFilePaths[0]
        
        this.setData({
          'form.avatarUrl': tempFilePath
        })
        
        wx.showToast({
          title: '头像已选择',
          icon: 'success'
        })
      }
    })
  },

  validateForm() {
    const { nickname, phone, email, realName, idCard } = this.data.form
    
    if (!nickname.trim()) {
      wx.showToast({
        title: '请输入昵称',
        icon: 'none'
      })
      return false
    }
    
    if (nickname.length > 20) {
      wx.showToast({
        title: '昵称不能超过20个字',
        icon: 'none'
      })
      return false
    }
    
    if (phone && !/^1[3-9]\d{9}$/.test(phone)) {
      wx.showToast({
        title: '请输入正确的手机号',
        icon: 'none'
      })
      return false
    }
    
    if (email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      wx.showToast({
        title: '请输入正确的邮箱',
        icon: 'none'
      })
      return false
    }
    
    if (realName && realName.length > 10) {
      wx.showToast({
        title: '姓名不能超过10个字',
        icon: 'none'
      })
      return false
    }
    
    if (idCard && !/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(idCard)) {
      wx.showToast({
        title: '请输入正确的身份证号',
        icon: 'none'
      })
      return false
    }
    
    return true
  },

  async saveProfile() {
    if (!this.validateForm()) {
      return
    }
    
    if (this.data.submitting) return
    
    this.setData({ submitting: true })
    wx.showLoading({ title: '保存中...' })
    
    try {
      const response = await auth.updateProfile(this.data.form)
      
      if (response.code === 200) {
        const updatedUserInfo = response.data
        
        wx.setStorageSync('userInfo', updatedUserInfo)
        
        this.setData({
          userInfo: updatedUserInfo,
          form: {
            nickname: updatedUserInfo.nickname || '',
            phone: updatedUserInfo.phone || '',
            email: updatedUserInfo.email || '',
            realName: updatedUserInfo.realName || '',
            idCard: updatedUserInfo.idCard || '',
            gender: updatedUserInfo.gender || '',
            birthday: updatedUserInfo.birthday || '',
            address: updatedUserInfo.address || '',
            avatarUrl: updatedUserInfo.avatarUrl || ''
          }
        })
        
        wx.showToast({
          title: '保存成功',
          icon: 'success'
        })
        
        setTimeout(() => {
          wx.navigateBack()
        }, 1500)
      } else {
        wx.showToast({
          title: response.message || '保存失败',
          icon: 'error'
        })
      }
    } catch (error) {
      console.error('保存个人资料失败:', error)
      wx.showToast({
        title: '保存失败，请重试',
        icon: 'error'
      })
    } finally {
      this.setData({ submitting: false })
      wx.hideLoading()
    }
  },

  handleCancel() {
    wx.showModal({
      title: '提示',
      content: '确定要放弃修改吗？未保存的内容将丢失。',
      confirmText: '确定',
      cancelText: '继续编辑',
      success: (res) => {
        if (res.confirm) {
          wx.navigateBack()
        }
      }
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
