import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import NotificationsPanel from './NotificationsPanel.vue'
import type { Notification } from '@/types'

// Mock notification data
const mockNotifications: Notification[] = [
  {
    id: '1',
    title: '新活动推荐',
    message: '为您推荐了新的环保活动',
    type: 'recommendation',
    priority: 'high',
    timestamp: new Date().toISOString(),
    read: false,
    action: {
      type: 'view',
      target: 'activity/1'
    }
  },
  {
    id: '2',
    title: '申请已通过',
    message: '您的志愿者申请已通过审核',
    type: 'application',
    priority: 'medium',
    timestamp: new Date(Date.now() - 3600000).toISOString(),
    read: true,
    action: {
      type: 'view',
      target: 'application/2'
    }
  },
  {
    id: '3',
    title: '系统维护通知',
    message: '系统将于今晚进行维护',
    type: 'system',
    priority: 'low',
    timestamp: new Date(Date.now() - 86400000).toISOString(),
    read: false,
    action: {
      type: 'dismiss',
      target: null
    }
  }
]

describe('NotificationsPanel.vue', () => {
  let wrapper: any
  let pinia: any

  beforeEach(() => {
    pinia = createPinia()
    setActivePinia(pinia)
    
    // Mock localStorage
    const localStorageMock = {
      getItem: vi.fn(),
      setItem: vi.fn(),
      removeItem: vi.fn(),
      clear: vi.fn()
    }
    global.localStorage = localStorageMock as any
    
    // Mock fetch API
    global.fetch = vi.fn()
    
    // Mock WebSocket
    global.WebSocket = vi.fn().mockImplementation(() => ({
      send: vi.fn(),
      close: vi.fn(),
      addEventListener: vi.fn(),
      removeEventListener: vi.fn()
    }))
  })

  afterEach(() => {
    vi.clearAllMocks()
  })

  const createWrapper = (props = {}) => {
    return mount(NotificationsPanel, {
      props: {
        notifications: mockNotifications,
        ...props
      }
    })
  }

  describe('Rendering', () => {
    it('renders correctly with notifications', () => {
      wrapper = createWrapper()
      
      expect(wrapper.find('.notifications-panel').exists()).toBe(true)
      expect(wrapper.find('.panel-header').exists()).toBe(true)
      expect(wrapper.find('.notification-list').exists()).toBe(true)
      
      // Check header info
      const headerTitle = wrapper.find('.header-title')
      expect(headerTitle.text()).toContain('通知中心')
      expect(headerTitle.text()).toContain('3 条通知')
      
      // Check unread count
      const unreadBadge = wrapper.find('.unread-badge')
      expect(unreadBadge.exists()).toBe(true)
      expect(unreadBadge.text()).toBe('2')
    })

    it('renders notifications correctly', () => {
      wrapper = createWrapper()
      
      const notificationItems = wrapper.findAll('.notification-item')
      expect(notificationItems.length).toBe(3)
      
      // Check first notification (high priority, unread)
      const firstItem = notificationItems[0]
      expect(firstItem.classes()).toContain('high-priority')
      expect(firstItem.classes()).toContain('unread')
      expect(firstItem.find('.notification-title').text()).toBe('新活动推荐')
      expect(firstItem.find('.notification-message').text()).toBe('为您推荐了新的环保活动')
      
      // Check second notification (medium priority, read)
      const secondItem = notificationItems[1]
      expect(secondItem.classes()).toContain('medium-priority')
      expect(secondItem.classes()).not.toContain('unread')
      expect(secondItem.find('.notification-title').text()).toBe('申请已通过')
      expect(secondItem.find('.notification-message').text()).toBe('您的志愿者申请已通过审核')
      
      // Check third notification (low priority, unread)
      const thirdItem = notificationItems[2]
      expect(thirdItem.classes()).toContain('low-priority')
      expect(thirdItem.classes()).toContain('unread')
      expect(thirdItem.find('.notification-title').text()).toBe('系统维护通知')
      expect(thirdItem.find('.notification-message').text()).toBe('系统将于今晚进行维护')
    })

    it('renders empty state when no notifications', () => {
      wrapper = createWrapper({ notifications: [] })
      
      expect(wrapper.find('.empty-state').exists()).toBe(true)
      expect(wrapper.text()).toContain('暂无通知')
    })

    it('renders notification types correctly', () => {
      wrapper = createWrapper()
      
      const typeIcons = wrapper.findAll('.notification-icon')
      expect(typeIcons.length).toBe(3)
      
      // Check icon types
      expect(typeIcons[0].classes()).toContain('recommendation')
      expect(typeIcons[1].classes()).toContain('application')
      expect(typeIcons[2].classes()).toContain('system')
    })

    it('renders timestamps correctly', () => {
      wrapper = createWrapper()
      
      const timestampElements = wrapper.findAll('.notification-timestamp')
      expect(timestampElements.length).toBe(3)
      
      // Check that timestamps are formatted (should be relative time)
      expect(timestampElements[0].text()).toBeTruthy()
      expect(timestampElements[1].text()).toBeTruthy()
      expect(timestampElements[2].text()).toBeTruthy()
    })
  })

  describe('Filtering', () => {
    it('shows all notifications by default', () => {
      wrapper = createWrapper()
      
      const items = wrapper.findAll('.notification-item')
      expect(items.length).toBe(3)
    })

    it('filters unread notifications', async () => {
      wrapper = createWrapper()
      
      await wrapper.find('[data-filter="unread"]').trigger('click')
      
      const items = wrapper.findAll('.notification-item')
      expect(items.length).toBe(2)
      
      // Only unread notifications should be visible
      items.forEach((item: any) => {
        expect(item.classes()).toContain('unread')
      })
    })

    it('filters by notification type', async () => {
      wrapper = createWrapper()
      
      await wrapper.find('[data-filter="recommendation"]').trigger('click')
      
      const items = wrapper.findAll('.notification-item')
      expect(items.length).toBe(1)
      expect(items[0].find('.notification-title').text()).toBe('新活动推荐')
    })

    it('filters by priority', async () => {
      wrapper = createWrapper()
      
      await wrapper.find('[data-filter="high"]').trigger('click')
      
      const items = wrapper.findAll('.notification-item')
      expect(items.length).toBe(1)
      expect(items[0].find('.notification-title').text()).toBe('新活动推荐')
    })
  })

  describe('Notification Actions', () => {
    it('marks notification as read when clicked', async () => {
      wrapper = createWrapper()
      
      const firstItem = wrapper.find('.notification-item')
      await firstItem.trigger('click')
      
      expect(wrapper.vm.markAsRead).toHaveBeenCalledWith('1')
    })

    it('displays action buttons for notifications', () => {
      wrapper = createWrapper()
      
      const actionButtons = wrapper.findAll('.action-button')
      expect(actionButtons.length).toBe(3)
      
      // Check button types
      expect(actionButtons[0].attributes('data-action')).toBe('view')
      expect(actionButtons[1].attributes('data-action')).toBe('view')
      expect(actionButtons[2].attributes('data-action')).toBe('dismiss')
    })

    it('handles view action', async () => {
      wrapper = createWrapper()
      
      const viewButton = wrapper.find('[data-action="view"]')
      await viewButton.trigger('click')
      
      expect(wrapper.emitted('view')).toBeTruthy()
      expect(wrapper.emitted('view')[0][0]).toBe('activity/1')
    })

    it('handles dismiss action', async () => {
      wrapper = createWrapper()
      
      const dismissButton = wrapper.find('[data-action="dismiss"]')
      await dismissButton.trigger('click')
      
      expect(wrapper.emitted('dismiss')).toBeTruthy()
      expect(wrapper.emitted('dismiss')[0][0]).toBe('3')
    })

    it('handles mark all as read', async () => {
      wrapper = createWrapper()
      
      await wrapper.find('.mark-all-read').trigger('click')
      
      expect(wrapper.vm.markAllAsRead).toHaveBeenCalled()
    })

    it('handles delete notification', async () => {
      wrapper = createWrapper()
      
      const deleteButton = wrapper.find('[data-delete="1"]')
      await deleteButton.trigger('click')
      
      expect(wrapper.emitted('delete')).toBeTruthy()
      expect(wrapper.emitted('delete')[0][0]).toBe('1')
    })
  })

  describe('WebSocket Integration', () => {
    it('connects to WebSocket on mount', () => {
      wrapper = createWrapper()
      
      expect(global.WebSocket).toHaveBeenCalledWith('ws://localhost:8080/ws/notifications')
    })

    it('handles WebSocket message', () => {
      wrapper = createWrapper()
      
      const mockEvent = {
        data: JSON.stringify({
          type: 'new_notification',
          notification: {
            id: '4',
            title: '新通知',
            message: '测试消息',
            type: 'system',
            priority: 'medium',
            timestamp: new Date().toISOString(),
            read: false
          }
        })
      }
      
      wrapper.vm.handleWebSocketMessage(mockEvent)
      
      expect(wrapper.vm.notifications.length).toBe(4)
      expect(wrapper.vm.notifications[3].title).toBe('新通知')
    })

    it('handles WebSocket connection close', () => {
      wrapper = createWrapper()
      
      const mockEvent = { code: 1006, reason: '' }
      wrapper.vm.handleWebSocketClose(mockEvent)
      
      expect(wrapper.vm.reconnectAttempts).toBe(1)
    })

    it('reconnects WebSocket automatically', async () => {
      wrapper = createWrapper()
      
      // Simulate connection close
      wrapper.vm.handleWebSocketClose({ code: 1006, reason: '' })
      
      // Simulate successful reconnect after delay
      await new Promise(resolve => setTimeout(resolve, 3000))
      
      expect(wrapper.vm.reconnectAttempts).toBe(1)
    })
  })

  describe('Real-time Updates', () => {
    it('updates notification count in real-time', () => {
      wrapper = createWrapper()
      
      // Add a new notification
      const newNotification = {
        id: '4',
        title: '新通知',
        message: '测试消息',
        type: 'system',
        priority: 'low',
        timestamp: new Date().toISOString(),
        read: false
      }
      
      wrapper.vm.notifications.push(newNotification)
      
      const headerTitle = wrapper.find('.header-title')
      expect(headerTitle.text()).toContain('4 条通知')
    })

    it('updates unread count in real-time', () => {
      wrapper = createWrapper()
      
      // Mark a notification as read
      wrapper.vm.markAsRead('1')
      
      const unreadBadge = wrapper.find('.unread-badge')
      expect(unreadBadge.text()).toBe('1')
    })

    it('handles notification priority changes', () => {
      wrapper = createWrapper()
      
      // Update notification priority
      const notification = wrapper.vm.notifications[0]
      notification.priority = 'low'
      
      const firstItem = wrapper.find('.notification-item')
      expect(firstItem.classes()).toContain('low-priority')
    })
  })

  describe('Accessibility', () => {
    it('has proper ARIA attributes', () => {
      wrapper = createWrapper()
      
      const panel = wrapper.find('.notifications-panel')
      expect(panel.attributes('aria-label')).toBe('通知中心')
      
      const items = wrapper.findAll('.notification-item')
      items.forEach((item: any, index: number) => {
        expect(item.attributes('role')).toBe('article')
        expect(item.attributes('aria-label')).toContain(`通知 ${index + 1}`)
      })
    })

    it('handles keyboard navigation', async () => {
      wrapper = createWrapper()
      
      // Test Enter key on notification item
      const firstItem = wrapper.find('.notification-item')
      await firstItem.trigger('keydown', { key: 'Enter' })
      
      expect(wrapper.vm.markAsRead).toHaveBeenCalledWith('1')
      
      // Test Space key on action button
      const actionButton = wrapper.find('.action-button')
      await actionButton.trigger('keydown', { key: ' ' })
      
      expect(wrapper.emitted('view')).toBeTruthy()
      
      // Test Escape key on dismiss
      const dismissButton = wrapper.find('[data-action="dismiss"]')
      await dismissButton.trigger('keydown', { key: 'Escape' })
      
      expect(wrapper.emitted('dismiss')).toBeTruthy()
    })

    it('supports screen reader announcements', () => {
      wrapper = createWrapper()
      
      // Check for live region
      const liveRegion = wrapper.find('.live-region')
      expect(liveRegion.exists()).toBe(true)
      
      // Check for proper ARIA live attributes
      expect(liveRegion.attributes('aria-live')).toBe('polite')
      expect(liveRegion.attributes('aria-atomic')).toBe('true')
    })
  })

  describe('Performance', () => {
    it('handles large number of notifications efficiently', () => {
      const largeNotifications = Array.from({ length: 100 }, (_, i) => ({
        id: `${i + 1}`,
        title: `通知 ${i + 1}`,
        message: `这是第 ${i + 1} 条通知`,
        type: i % 3 === 0 ? 'recommendation' : i % 3 === 1 ? 'application' : 'system',
        priority: i % 4 === 0 ? 'high' : i % 4 === 1 ? 'medium' : 'low',
        timestamp: new Date(Date.now() - i * 3600000).toISOString(),
        read: i % 2 === 0
      }))
      
      wrapper = createWrapper({ notifications: largeNotifications })
      
      expect(wrapper.vm.notifications.length).toBe(100)
      
      // Check that virtualization is working (should only render visible items)
      const visibleItems = wrapper.findAll('.notification-item')
      expect(visibleItems.length).toBeLessThan(100)
    })

    it('uses debounced search for performance', async () => {
      wrapper = createWrapper()
      
      const searchInput = wrapper.find('.search-input')
      await searchInput.setValue('test')
      
      // Search should be debounced
      expect(wrapper.vm.searchQuery).toBe('test')
    })
  })

  describe('Error Handling', () => {
    it('handles WebSocket connection errors', () => {
      wrapper = createWrapper()
      
      const mockError = new Error('连接失败')
      wrapper.vm.handleWebSocketError(mockError)
      
      expect(wrapper.vm.error).toBe('连接失败')
    })

    it('handles message parsing errors', () => {
      wrapper = createWrapper()
      
      const mockEvent = {
        data: 'invalid json'
      }
      
      wrapper.vm.handleWebSocketMessage(mockEvent)
      
      // Should not crash, just ignore invalid message
      expect(wrapper.vm.notifications.length).toBe(3)
    })

    it('handles notification validation errors', () => {
      wrapper = createWrapper()
      
      const invalidNotification = {
        id: '5',
        // Missing required fields
        title: '无效通知'
      }
      
      wrapper.vm.addNotification(invalidNotification)
      
      // Should not add invalid notification
      expect(wrapper.vm.notifications.length).toBe(3)
    })
  })
})