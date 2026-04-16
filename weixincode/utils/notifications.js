// 通知管理器
// 处理各类实时通知功能

const { getWebSocket } = require('./websocket.js')

class NotificationManager {
  constructor(options = {}) {
    this.ws = getWebSocket(options)
    this.enabled = true
    this.soundEnabled = options.soundEnabled !== false
    this.vibrationEnabled = options.vibrationEnabled !== false
    this.badgeEnabled = options.badgeEnabled !== true
    
    this.setupEventListeners()
  }
  
  // 设置事件监听器
  setupEventListeners() {
    // 连接成功事件
    this.ws.on('connected', () => {
      console.log('WebSocket 连接成功，开始订阅通知')
      this.subscribeAll()
    })
    
    // 连接断开事件
    this.ws.on('disconnected', () => {
      console.log('WebSocket 连接断开')
    })
    
    // 错误事件
    this.ws.on('error', (error) => {
      console.error('WebSocket 连接错误:', error)
    })
    
    // 新通知事件
    this.ws.subscribe('notification', this.handleNotification.bind(this))
    
    // 任务状态更新
    this.ws.subscribe('task_update', this.handleTaskUpdate.bind(this))
    
    // 系统消息
    this.ws.subscribe('system_message', this.handleSystemMessage.bind(this))
    
    // 消息提醒
    this.ws.subscribe('message_reminder', this.handleMessageReminder.bind(this))
    
    // 活动提醒
    this.ws.subscribe('activity_reminder', this.handleActivityReminder.bind(this))
  }
  
  // 订阅所有通知类型
  subscribeAll() {
    const subscriptions = [
      'notification',
      'task_update', 
      'system_message',
      'message_reminder',
      'activity_reminder'
    ]
    
    subscriptions.forEach(type => {
      this.ws.subscribe(type)
    })
  }
  
  // 处理通知消息
  handleNotification(data) {
    console.log('收到通知:', data)
    
    if (!this.enabled) return
    
    // 根据通知类型显示不同提示
    switch (data.type) {
      case 'task':
        this.showTaskNotification(data)
        break
      case 'message':
        this.showMessageNotification(data)
        break
      case 'activity':
        this.showActivityNotification(data)
        break
      case 'system':
        this.showSystemNotification(data)
        break
      default:
        this.showGenericNotification(data)
    }
  }
  
  // 显示任务通知
  showTaskNotification(data) {
    const title = data.title || '任务提醒'
    const content = data.content || '您有新的任务需要处理'
    const path = data.path || '/pages/tasks/tasks'
    
    this.showNotification({
      title,
      content,
      path,
      type: 'task',
      data
    })
  }
  
  // 显示消息通知
  showMessageNotification(data) {
    const title = data.title || '新消息'
    const content = data.content || '您收到了新的消息'
    const path = data.path || '/pages/messages/messages'
    
    this.showNotification({
      title,
      content,
      path,
      type: 'message',
      data
    })
  }
  
  // 显示活动通知
  showActivityNotification(data) {
    const title = data.title || '活动提醒'
    const content = data.content || '您关注的活动有更新'
    const path = data.path || '/pages/activities/activities'
    
    this.showNotification({
      title,
      content,
      path,
      type: 'activity',
      data
    })
  }
  
  // 显示系统通知
  showSystemNotification(data) {
    const title = data.title || '系统通知'
    const content = data.content || '系统消息'
    const path = data.path || '/pages/index/index'
    
    this.showNotification({
      title,
      content,
      path,
      type: 'system',
      data
    })
  }
  
  // 显示通用通知
  showGenericNotification(data) {
    const title = data.title || '通知'
    const content = data.content || '您有新的通知'
    const path = data.path || '/pages/index/index'
    
    this.showNotification({
      title,
      content,
      path,
      type: 'generic',
      data
    })
  }
  
  // 显示通知
  showNotification(options) {
    const { title, content, path, type, data } = options
    
    // 播放声音
    if (this.soundEnabled) {
      this.playNotificationSound()
    }
    
    // 震动提醒
    if (this.vibrationEnabled) {
      this.vibrate()
    }
    
    // 显示小程序通知
    wx.showModal({
      title: title,
      content: content,
      confirmText: '查看',
      success: (res) => {
        if (res.confirm) {
          // 导航到相应页面
          wx.navigateTo({
            url: path
          })
        }
      }
    })
    
    // 更新消息徽章
    if (this.badgeEnabled) {
      this.updateBadge(type)
    }
    
    // 记录通知日志
    this.logNotification({
      title,
      content,
      type,
      path,
      data,
      timestamp: Date.now()
    })
  }
  
  // 处理任务更新
  handleTaskUpdate(data) {
    console.log('任务状态更新:', data)
    
    // 更新本地任务状态
    const app = getApp()
    if (app.globalData.tasks) {
      const taskIndex = app.globalData.tasks.findIndex(task => task.id === data.taskId)
      if (taskIndex > -1) {
        app.globalData.tasks[taskIndex].status = data.status
        app.globalData.tasks[taskIndex].updatedAt = data.updatedAt
      }
    }
    
    // 显示任务更新通知
    if (data.notify) {
      this.showTaskNotification({
        title: '任务更新',
        content: `任务"${data.title}"状态已更新为${this.getStatusText(data.status)}`,
        path: '/pages/tasks/tasks',
        type: 'task_update',
        data
      })
    }
  }
  
  // 处理系统消息
  handleSystemMessage(data) {
    console.log('系统消息:', data)
    
    this.showSystemNotification({
      title: data.title || '系统消息',
      content: data.content,
      path: '/pages/system/system',
      type: 'system_message',
      data
    })
  }
  
  // 处理消息提醒
  handleMessageReminder(data) {
    console.log('消息提醒:', data)
    
    this.showMessageNotification({
      title: '消息提醒',
      content: `您有${data.count}条未读消息`,
      path: '/pages/messages/messages',
      type: 'message_reminder',
      data
    })
  }
  
  // 处理活动提醒
  handleActivityReminder(data) {
    console.log('活动提醒:', data)
    
    this.showActivityNotification({
      title: '活动提醒',
      content: data.content || '您关注的活动即将开始',
      path: '/pages/activities/activities',
      type: 'activity_reminder',
      data
    })
  }
  
  // 播放通知声音
  playNotificationSound() {
    const audioContext = wx.createAudioContext('notificationSound')
    audioContext.setSrc('/assets/sounds/notification.mp3')
    audioContext.play()
  }
  
  // 震动提醒
  vibrate() {
    wx.vibrateShort({
      type: 'light'
    })
  }
  
  // 更新消息徽章
  updateBadge(type) {
    // 更新小程序图标徽章
    wx.setTabBarBadge({
      index: this.getBadgeIndex(type),
      text: '1'
    })
  }
  
  // 获取徽章索引
  getBadgeIndex(type) {
    const badgeMapping = {
      'message': 1,  // 消息页面
      'task': 2,     // 任务页面
      'activity': 0  // 首页
    }
    return badgeMapping[type] || 0
  }
  
  // 获取状态文本
  getStatusText(status) {
    const statusMap = {
      'pending': '待处理',
      'in_progress': '进行中',
      'completed': '已完成',
      'cancelled': '已取消'
    }
    return statusMap[status] || status
  }
  
  // 记录通知日志
  logNotification(notification) {
    const app = getApp()
    if (!app.globalData.notificationLogs) {
      app.globalData.notificationLogs = []
    }
    
    app.globalData.notificationLogs.unshift({
      ...notification,
      id: Date.now(),
      read: false
    })
    
    // 保留最近100条日志
    if (app.globalData.notificationLogs.length > 100) {
      app.globalData.notificationLogs = app.globalData.notificationLogs.slice(0, 100)
    }
  }
  
  // 标记通知为已读
  markAsRead(notificationId) {
    const app = getApp()
    if (app.globalData.notificationLogs) {
      const notification = app.globalData.notificationLogs.find(n => n.id === notificationId)
      if (notification) {
        notification.read = true
        notification.readAt = Date.now()
      }
    }
  }
  
  // 标记所有通知为已读
  markAllAsRead() {
    const app = getApp()
    if (app.globalData.notificationLogs) {
      app.globalData.notificationLogs.forEach(notification => {
        notification.read = true
        notification.readAt = Date.now()
      })
    }
    
    // 清除消息徽章
    wx.removeTabBarBadge({ index: 1 })
    wx.removeTabBarBadge({ index: 2 })
  }
  
  // 获取未读通知数量
  getUnreadCount() {
    const app = getApp()
    if (!app.globalData.notificationLogs) {
      return 0
    }
    
    return app.globalData.notificationLogs.filter(n => !n.read).length
  }
  
  // 获取通知列表
  getNotifications() {
    const app = getApp()
    return app.globalData.notificationLogs || []
  }
  
  // 启用通知
  enable() {
    this.enabled = true
  }
  
  // 禁用通知
  disable() {
    this.enabled = false
  }
  
  // 设置声音
  setSoundEnabled(enabled) {
    this.soundEnabled = enabled
  }
  
  // 设置震动
  setVibrationEnabled(enabled) {
    this.vibrationEnabled = enabled
  }
  
  // 设置徽章
  setBadgeEnabled(enabled) {
    this.badgeEnabled = enabled
  }
  
  // 清除通知
  clearNotifications() {
    const app = getApp()
    app.globalData.notificationLogs = []
    
    // 清除徽章
    wx.removeTabBarBadge({ index: 0 })
    wx.removeTabBarBadge({ index: 1 })
    wx.removeTabBarBadge({ index: 2 })
  }
  
  // 销毁通知管理器
  destroy() {
    this.ws.off('notification', this.handleNotification)
    this.ws.off('task_update', this.handleTaskUpdate)
    this.ws.off('system_message', this.handleSystemMessage)
    this.ws.off('message_reminder', this.handleMessageReminder)
    this.ws.off('activity_reminder', this.handleActivityReminder)
  }
}

module.exports = NotificationManager