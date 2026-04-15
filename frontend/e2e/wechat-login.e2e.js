const { request } = require('../utils/request')

describe('WeChat Login E2E Tests', () => {
  let mockWxLogin
  let mockWxRequest
  let mockWxGetStorageSync
  let mockWxSetStorageSync
  let mockWxRemoveStorageSync
  let mockWxShowToast
  let mockWxSwitchTab
  let mockWxReLaunch

  beforeEach(() => {
    mockWxLogin = jest.fn()
    mockWxRequest = jest.fn()
    mockWxGetStorageSync = jest.fn()
    mockWxSetStorageSync = jest.fn()
    mockWxRemoveStorageSync = jest.fn()
    mockWxShowToast = jest.fn()
    mockWxSwitchTab = jest.fn()
    mockWxReLaunch = jest.fn()

    global.wx = {
      login: mockWxLogin,
      request: mockWxRequest,
      getStorageSync: mockWxGetStorageSync,
      setStorageSync: mockWxSetStorageSync,
      removeStorageSync: mockWxRemoveStorageSync,
      showToast: mockWxShowToast,
      switchTab: mockWxSwitchTab,
      reLaunch: mockWxReLaunch
    }

    global.getApp = jest.fn(() => ({
      globalData: {
        apiBaseUrl: 'http://localhost:8080/api'
      }
    }))
  })

  afterEach(() => {
    jest.clearAllMocks()
  })

  describe('Successful WeChat Login - New User', () => {
    it('should successfully login a new WeChat user', async () => {
      mockWxLogin.mockImplementation(({ success }) => {
        success({ code: 'test_wechat_code_123' })
      })

      mockWxRequest.mockImplementation(({ success }) => {
        success({
          statusCode: 200,
          data: {
            code: 200,
            message: '微信登录成功',
            data: {
              token: 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test',
              userId: 1,
              email: 'wechat_test_o@wechat.local',
              mobile: '10000000te',
              role: 'USER',
              expiresIn: 86400000,
              isNewUser: true
            }
          }
        })
      })

      const loginPage = require('../pages/login/login')

      await loginPage.onWeChatLogin()

      expect(mockWxLogin).toHaveBeenCalledTimes(1)
      expect(mockWxSetStorageSync).toHaveBeenCalledWith('token', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test')
      expect(mockWxSetStorageSync).toHaveBeenCalledWith('userInfo', expect.objectContaining({
        userId: 1,
        email: 'wechat_test_o@wechat.local',
        role: 'USER'
      }))
      expect(mockWxShowToast).toHaveBeenCalledWith({
        title: '欢迎使用',
        icon: 'success'
      })
      expect(mockWxSwitchTab).toHaveBeenCalledWith({
        url: '/pages/index/index'
      })
    })
  })

  describe('Successful WeChat Login - Existing User', () => {
    it('should successfully login an existing WeChat user', async () => {
      mockWxLogin.mockImplementation(({ success }) => {
        success({ code: 'test_wechat_code_456' })
      })

      mockWxRequest.mockImplementation(({ success }) => {
        success({
          statusCode: 200,
          data: {
            code: 200,
            message: '微信登录成功',
            data: {
              token: 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.existing',
              userId: 2,
              email: 'existing@example.com',
              mobile: '13800138000',
              role: 'USER',
              expiresIn: 86400000,
              isNewUser: false
            }
          }
        })
      })

      const loginPage = require('../pages/login/login')

      await loginPage.onWeChatLogin()

      expect(mockWxSetStorageSync).toHaveBeenCalledWith('token', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.existing')
      expect(mockWxSetStorageSync).toHaveBeenCalledWith('userInfo', expect.objectContaining({
        userId: 2,
        email: 'existing@example.com',
        mobile: '13800138000',
        role: 'USER'
      }))
      expect(mockWxShowToast).toHaveBeenCalledWith({
        title: '登录成功',
        icon: 'success'
      })
    })
  })

  describe('WeChat Login - Error Scenarios', () => {
    it('should handle wx.login failure', async () => {
      mockWxLogin.mockImplementation(({ fail }) => {
        fail({ errMsg: 'wx.login:fail' })
      })

      const loginPage = require('../pages/login/login')

      await loginPage.onWeChatLogin()

      expect(mockWxShowToast).toHaveBeenCalledWith({
        title: '获取微信授权失败',
        icon: 'none'
      })
      expect(mockWxRequest).not.toHaveBeenCalled()
    })

    it('should handle missing code from wx.login', async () => {
      mockWxLogin.mockImplementation(({ success }) => {
        success({ code: '' })
      })

      const loginPage = require('../pages/login/login')

      await loginPage.onWeChatLogin()

      expect(mockWxShowToast).toHaveBeenCalledWith({
        title: '获取微信授权失败',
        icon: 'none'
      })
      expect(mockWxRequest).not.toHaveBeenCalled()
    })

    it('should handle backend API error', async () => {
      mockWxLogin.mockImplementation(({ success }) => {
        success({ code: 'invalid_code' })
      })

      mockWxRequest.mockImplementation(({ fail }) => {
        fail({ errMsg: 'request:fail' })
      })

      const loginPage = require('../pages/login/login')

      await loginPage.onWeChatLogin()

      expect(mockWxShowToast).toHaveBeenCalledWith({
        title: '登录失败，请重试',
        icon: 'none'
      })
      expect(mockWxSetStorageSync).not.toHaveBeenCalled()
    })

    it('should handle backend error response', async () => {
      mockWxLogin.mockImplementation(({ success }) => {
        success({ code: 'error_code' })
      })

      mockWxRequest.mockImplementation(({ success }) => {
        success({
          statusCode: 400,
          data: {
            code: 40029,
            message: '微信授权失败: invalid code'
          }
        })
      })

      const loginPage = require('../pages/login/login')

      await loginPage.onWeChatLogin()

      expect(mockWxShowToast).toHaveBeenCalledWith({
        title: '微信授权失败: invalid code',
        icon: 'none'
      })
      expect(mockWxSetStorageSync).not.toHaveBeenCalled()
    })
  })

  describe('WeChat Login - Loading State', () => {
    it('should set loading state and prevent duplicate clicks', async () => {
      mockWxLogin.mockImplementation(({ success }) => {
        success({ code: 'test_code' })
      })

      mockWxRequest.mockImplementation(({ success }) => {
        success({
          statusCode: 200,
          data: {
            code: 200,
            message: '微信登录成功',
            data: {
              token: 'test.token',
              userId: 1,
              email: 'test@example.com',
              mobile: '13800138000',
              role: 'USER',
              expiresIn: 86400000,
              isNewUser: false
            }
          }
        })
      })

      const loginPage = require('../pages/login/login')

      expect(loginPage.data.wechatLoading).toBe(false)

      const firstCall = loginPage.onWeChatLogin()
      expect(loginPage.data.wechatLoading).toBe(true)

      const secondCall = loginPage.onWeChatLogin()
      expect(loginPage.data.wechatLoading).toBe(true)

      await firstCall
      expect(loginPage.data.wechatLoading).toBe(false)

      await secondCall
    })
  })

  describe('WeChat Login - Token Storage', () => {
    it('should store token correctly in storage', async () => {
      mockWxLogin.mockImplementation(({ success }) => {
        success({ code: 'test_code' })
      })

      mockWxRequest.mockImplementation(({ success }) => {
        success({
          statusCode: 200,
          data: {
            code: 200,
            message: '微信登录成功',
            data: {
              token: 'stored.token.123',
              userId: 1,
              email: 'test@example.com',
              mobile: '13800138000',
              role: 'USER',
              expiresIn: 86400000,
              isNewUser: false
            }
          }
        })
      })

      mockWxGetStorageSync.mockReturnValue('stored.token.123')

      const loginPage = require('../pages/login/login')

      await loginPage.onWeChatLogin()

      expect(mockWxSetStorageSync).toHaveBeenCalledWith('token', 'stored.token.123')
      
      const storedToken = mockWxGetStorageSync('token')
      expect(storedToken).toBe('stored.token.123')
    })

    it('should store user info correctly in storage', async () => {
      const userInfo = {
        userId: 3,
        email: 'user@example.com',
        mobile: '13700137000',
        role: 'ADMIN'
      }

      mockWxLogin.mockImplementation(({ success }) => {
        success({ code: 'test_code' })
      })

      mockWxRequest.mockImplementation(({ success }) => {
        success({
          statusCode: 200,
          data: {
            code: 200,
            message: '微信登录成功',
            data: {
              token: 'admin.token.456',
              userId: userInfo.userId,
              email: userInfo.email,
              mobile: userInfo.mobile,
              role: userInfo.role,
              expiresIn: 86400000,
              isNewUser: false
            }
          }
        })
      })

      const loginPage = require('../pages/login/login')

      await loginPage.onWeChatLogin()

      expect(mockWxSetStorageSync).toHaveBeenCalledWith('userInfo', expect.objectContaining(userInfo))
    })
  })

  describe('WeChat Login - Navigation', () => {
    it('should navigate to index page after successful login', async () => {
      mockWxLogin.mockImplementation(({ success }) => {
        success({ code: 'test_code' })
      })

      mockWxRequest.mockImplementation(({ success }) => {
        success({
          statusCode: 200,
          data: {
            code: 200,
            message: '微信登录成功',
            data: {
              token: 'nav.token.789',
              userId: 1,
              email: 'test@example.com',
              mobile: '13800138000',
              role: 'USER',
              expiresIn: 86400000,
              isNewUser: false
            }
          }
        })
      })

      const loginPage = require('../pages/login/login')

      await loginPage.onWeChatLogin()

      expect(mockWxSwitchTab).toHaveBeenCalledWith({
        url: '/pages/index/index'
      })
    })
  })

  describe('WeChat Login - Admin User', () => {
    it('should handle admin user login correctly', async () => {
      mockWxLogin.mockImplementation(({ success }) => {
        success({ code: 'admin_code' })
      })

      mockWxRequest.mockImplementation(({ success }) => {
        success({
          statusCode: 200,
          data: {
            code: 200,
            message: '微信登录成功',
            data: {
              token: 'admin.jwt.token',
              userId: 1,
              email: 'admin@example.com',
              mobile: '13900139000',
              role: 'ADMIN',
              expiresIn: 86400000,
              isNewUser: false
            }
          }
        })
      })

      const loginPage = require('../pages/login/login')

      await loginPage.onWeChatLogin()

      expect(mockWxSetStorageSync).toHaveBeenCalledWith('userInfo', expect.objectContaining({
        role: 'ADMIN'
      }))
      expect(mockWxShowToast).toHaveBeenCalledWith({
        title: '登录成功',
        icon: 'success'
      })
    })
  })
})
