const app = getApp()
const volunteer = require('../../api/volunteer')
const auth = require('../../api/auth')

Page({
  data: {
    userInfo: null,
    isLoggedIn: false,
    avatarLetter: '?',
    loading: true,
    stats: {
      totalSchools: 0,
      myApplications: 0,
      submittedCount: 0,
      simulationCount: 0
    },
    featuredSchools: [],
    recentApplications: [],
    quickActions: [
      {
        icon: 'search',
        label: '学校查询',
        url: '/pages/projects/projects',
        color: '#1989fa'
      },
      {
        icon: 'waiting',
        label: '我的志愿',
        url: '/pages/applications/applications',
        color: '#07c160'
      },
      {
        icon: 'success',
        label: '智能推荐',
        url: '/pages/applications/applications',
        color: '#ff976a'
      },
      {
        icon: 'info',
        label: '个人中心',
        url: '/pages/profile/profile',
        color: '#ee0a24'
      }
    ]
  },

  onLoad() {
    this.checkLoginStatus()
  },

  onShow() {
    this.loadPageData()
  },

  async checkLoginStatus() {
    // 优先检查本地 token
    const isLoggedIn = auth.checkLoginStatus()
    if (isLoggedIn) {
      const userInfo = wx.getStorageSync('userInfo')
      this.setData({
        isLoggedIn: true,
        userInfo: userInfo,
        avatarLetter: this.getAvatarLetter(userInfo)
      })
      return
    }

    // token 无效，尝试微信静默登录
    try {
      const app = getApp()
      const result = await app.autoWechatLogin()
      if (result.success) {
        const userInfo = wx.getStorageSync('userInfo')
        this.setData({
          isLoggedIn: true,
          userInfo: userInfo,
          avatarLetter: this.getAvatarLetter(userInfo)
        })
      }
    } catch (error) {
      console.warn('微信自动登录失败:', error)
    }
  },

  async loadPageData() {
    if (!this.data.isLoggedIn) {
      this.setData({ loading: false })
      return
    }

    this.setData({ loading: true })

    try {
      const [schoolsResponse, appResponse] = await Promise.all([
        volunteer.getSchoolList({ page: 0, size: 6 }).catch(() => ({ data: { content: [] } })),
        volunteer.getVolunteerApplications({ page: 0, size: 5 }).catch(() => ({ data: { content: [] } }))
      ])

      const schoolData = schoolsResponse.data || schoolsResponse || {}
      const appData = appResponse.data || appResponse || {}
      const schoolList = schoolData.schools || schoolData.content || []
      const appList = appData.content || appData.applications || []

      this.setData({
        featuredSchools: schoolList.map(s => ({
          id: s.id,
          name: s.name,
          type: s.type,
          city: s.city,
          district: s.district
        })),
        recentApplications: appList.map(a => ({
          id: a.id,
          name: a.name,
          status: a.status,
          createdAt: a.createdAt
        })),
        stats: {
          totalSchools: schoolData.totalElements || 0,
          myApplications: appData.totalElements || 0,
          submittedCount: appList.filter(a => a.status === 'SUBMITTED').length,
          simulationCount: 0
        },
        loading: false
      })
    } catch (error) {
      console.error('加载数据失败:', error)
      this.setData({ loading: false })
    }
  },

  onQuickAction(e) {
    const { url } = e.currentTarget.dataset
    const self = this

    if (!this.data.isLoggedIn) {
      wx.showModal({
        title: '提示',
        content: '请先登录后再使用此功能',
        success: function(res) {
          if (res.confirm) {
            var app = getApp()
            app.autoWechatLogin()
              .then(function(loginResult) {
                if (loginResult.success) {
                  var userInfo = wx.getStorageSync('userInfo')
                  self.setData({ isLoggedIn: true, userInfo: userInfo, avatarLetter: self.getAvatarLetter(userInfo) })
                  wx.switchTab({ url: url })
                } else {
                  wx.navigateTo({ url: '/pages/login/login' })
                }
              })
              .catch(function(error) {
                wx.navigateTo({ url: '/pages/login/login' })
              })
          }
        }
      })
      return
    }

    wx.switchTab({ url: url })
  },

  onSchoolDetail(e) {
    const { id } = e.currentTarget.dataset
    wx.switchTab({
      url: '/pages/projects/projects'
    })
  },

  onAllSchools() {
    wx.switchTab({
      url: '/pages/projects/projects'
    })
  },

  onLogin() {
    // 微信登录
    wx.navigateTo({
      url: '/pages/login/login'
    })
  },

  onRegister() {
    // 微信授权完善资料
    wx.navigateTo({
      url: '/pages/register/register'
    })
  },

  formatDate(dateString) {
    if (!dateString) return ''
    const date = new Date(dateString)
    const now = new Date()
    const diff = now - date
    const days = Math.floor(diff / (1000 * 60 * 60 * 24))

    if (days === 0) return '今天'
    if (days === 1) return '昨天'
    if (days < 7) return `${days}天前`

    return `${date.getMonth() + 1}月${date.getDate()}日`
  },

  getAvatarLetter(userInfo) {
    if (!userInfo) return '?'
    var name = userInfo.nickname || userInfo.email || userInfo.username || '用户'
    return name.charAt(0)
  }
})
