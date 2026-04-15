import { ref, computed, onUnmounted } from 'vue'
import { getWebSocket, useWebSocket } from './websocket'

export interface NotificationData {
  title: string
  content: string
  path?: string
  type: 'task' | 'message' | 'activity' | 'system' | 'generic'
  data?: any
}

export interface Notification extends NotificationData {
  id: number
  timestamp: number
  read: boolean
  readAt?: number
}

export interface NotificationOptions {
  soundEnabled?: boolean
  badgeEnabled?: boolean
  autoMarkRead?: boolean
}

class NotificationManager {
  private ws: ReturnType<typeof getWebSocket>
  private notifications: Notification[] = []
  private options: Required<NotificationOptions>
  private sound: HTMLAudioElement | null = null

  constructor(options: NotificationOptions = {}) {
    this.ws = getWebSocket()
    this.options = {
      soundEnabled: options.soundEnabled !== false,
      badgeEnabled: options.badgeEnabled !== true,
      autoMarkRead: options.autoMarkRead !== false
    }

    this.initializeSound()
    this.setupEventListeners()
    this.loadNotifications()
  }

  private initializeSound() {
    try {
      this.sound = new Audio('/sounds/notification.mp3')
      this.sound.volume = 0.5
    } catch (error) {
      console.error('Failed to initialize notification sound:', error)
    }
  }

  private setupEventListeners() {
    this.ws.on('connected', () => {
      console.log('WebSocket connected, subscribing to notifications')
      this.subscribeAll()
    })

    this.ws.subscribe('notification', this.handleNotification.bind(this))
    this.ws.subscribe('task_update', this.handleTaskUpdate.bind(this))
    this.ws.subscribe('system_message', this.handleSystemMessage.bind(this))
    this.ws.subscribe('message_reminder', this.handleMessageReminder.bind(this))
    this.ws.subscribe('activity_reminder', this.handleActivityReminder.bind(this))
  }

  private subscribeAll() {
    const subscriptions = [
      'notification',
      'task_update',
      'system_message',
      'message_reminder',
      'activity_reminder'
    ]

    subscriptions.forEach(type => this.ws.subscribe(type))
  }

  private handleNotification(data: NotificationData) {
    console.log('Notification received:', data)
    this.showNotification(data)
  }

  private handleTaskUpdate(data: any) {
    console.log('Task update received:', data)
    
    if (data.notify) {
      this.showNotification({
        title: '任务更新',
        content: `任务"${data.title}"状态已更新为${this.getStatusText(data.status)}`,
        type: 'task',
        data
      })
    }
  }

  private handleSystemMessage(data: any) {
    console.log('System message received:', data)
    this.showNotification({
      title: data.title || '系统消息',
      content: data.content,
      type: 'system',
      data
    })
  }

  private handleMessageReminder(data: any) {
    console.log('Message reminder received:', data)
    this.showNotification({
      title: '消息提醒',
      content: `您有${data.count}条未读消息`,
      type: 'message',
      data
    })
  }

  private handleActivityReminder(data: any) {
    console.log('Activity reminder received:', data)
    this.showNotification({
      title: '活动提醒',
      content: data.content || '您关注的活动即将开始',
      type: 'activity',
      data
    })
  }

  private showNotification(data: NotificationData) {
    if (this.options.soundEnabled && this.sound) {
      this.sound.play().catch(error => {
        console.error('Failed to play notification sound:', error)
      })
    }

    const notification: Notification = {
      id: Date.now(),
      ...data,
      timestamp: Date.now(),
      read: false
    }

    this.notifications.unshift(notification)
    this.saveNotifications()
    this.updateBadge()

    this.triggerNotification(notification)
  }

  private triggerNotification(notification: Notification) {
    if ('Notification' in window && Notification.permission === 'granted') {
      new Notification(notification.title, {
        body: notification.content,
        icon: '/icon-192.png',
        tag: `notification-${notification.id}`,
        requireInteraction: false
      })
    } else if ('Notification' in window && Notification.permission !== 'denied') {
      Notification.requestPermission().then(permission => {
        if (permission === 'granted') {
          new Notification(notification.title, {
            body: notification.content,
            icon: '/icon-192.png'
          })
        }
      })
    }
  }

  private updateBadge() {
    if (!this.options.badgeEnabled) return

    const unreadCount = this.getUnreadCount()
    
    if ('setAppBadge' in navigator) {
      navigator.setAppBadge(unreadCount)
    } else if ('setExperimentalAppBadge' in navigator) {
      (navigator as any).setExperimentalAppBadge(unreadCount)
    } else {
      document.title = unreadCount > 0 ? `(${unreadCount}) 志愿汇` : '志愿汇'
    }
  }

  private getStatusText(status: string): string {
    const statusMap: Record<string, string> = {
      'pending': '待处理',
      'in_progress': '进行中',
      'completed': '已完成',
      'cancelled': '已取消'
    }
    return statusMap[status] || status
  }

  private saveNotifications() {
    try {
      localStorage.setItem('notifications', JSON.stringify(this.notifications.slice(0, 100)))
    } catch (error) {
      console.error('Failed to save notifications:', error)
    }
  }

  private loadNotifications() {
    try {
      const saved = localStorage.getItem('notifications')
      if (saved) {
        this.notifications = JSON.parse(saved)
      }
    } catch (error) {
      console.error('Failed to load notifications:', error)
    }
  }

  markAsRead(notificationId: number) {
    const notification = this.notifications.find(n => n.id === notificationId)
    if (notification && !notification.read) {
      notification.read = true
      notification.readAt = Date.now()
      this.saveNotifications()
      this.updateBadge()
    }
  }

  markAllAsRead() {
    const now = Date.now()
    this.notifications.forEach(notification => {
      if (!notification.read) {
        notification.read = true
        notification.readAt = now
      }
    })
    this.saveNotifications()
    this.updateBadge()
  }

  getUnreadCount(): number {
    return this.notifications.filter(n => !n.read).length
  }

  getNotifications(): Notification[] {
    return [...this.notifications]
  }

  clearNotifications() {
    this.notifications = []
    this.saveNotifications()
    this.updateBadge()
  }

  deleteNotification(notificationId: number) {
    const index = this.notifications.findIndex(n => n.id === notificationId)
    if (index > -1) {
      this.notifications.splice(index, 1)
      this.saveNotifications()
      this.updateBadge()
    }
  }

  enable() {
    this.options.soundEnabled = true
  }

  disable() {
    this.options.soundEnabled = false
  }

  setSoundEnabled(enabled: boolean) {
    this.options.soundEnabled = enabled
  }

  setBadgeEnabled(enabled: boolean) {
    this.options.badgeEnabled = enabled
  }

  destroy() {
    this.ws.unsubscribe('notification', this.handleNotification.bind(this))
    this.ws.unsubscribe('task_update', this.handleTaskUpdate.bind(this))
    this.ws.unsubscribe('system_message', this.handleSystemMessage.bind(this))
    this.ws.unsubscribe('message_reminder', this.handleMessageReminder.bind(this))
    this.ws.unsubscribe('activity_reminder', this.handleActivityReminder.bind(this))
  }
}

let globalNotificationManager: NotificationManager | null = null

export function getNotificationManager(options?: NotificationOptions): NotificationManager {
  if (!globalNotificationManager) {
    globalNotificationManager = new NotificationManager(options)
  }
  return globalNotificationManager
}

export function useNotifications(options?: NotificationOptions) {
  const manager = getNotificationManager(options)
  const notifications = ref<Notification[]>(manager.getNotifications())
  const unreadCount = computed(() => manager.getUnreadCount())

  const updateNotifications = () => {
    notifications.value = manager.getNotifications()
  }

  const showNotification = (data: NotificationData) => {
    const notification: Notification = {
      id: Date.now(),
      ...data,
      timestamp: Date.now(),
      read: false
    }
    notifications.value.unshift(notification)
    return notification.id
  }

  const markAsRead = (notificationId: number) => {
    manager.markAsRead(notificationId)
    updateNotifications()
  }

  const markAllAsRead = () => {
    manager.markAllAsRead()
    updateNotifications()
  }

  const deleteNotification = (notificationId: number) => {
    manager.deleteNotification(notificationId)
    updateNotifications()
  }

  const clearNotifications = () => {
    manager.clearNotifications()
    updateNotifications()
  }

  onUnmounted(() => {
    // 清理逻辑
  })

  return {
    notifications,
    unreadCount,
    showNotification,
    markAsRead,
    markAllAsRead,
    deleteNotification,
    clearNotifications
  }
}