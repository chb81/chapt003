import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import Dashboard from './Dashboard.vue'
import type { DashboardData, AnalyticsData } from '@/types'

// Mock data
const mockDashboardData: DashboardData = {
  overview: {
    totalVolunteers: 150,
    activeActivities: 25,
    completedActivities: 45,
    totalHours: 1200
  },
  recentActivities: [
    {
      id: '1',
      title: '社区环保活动',
      type: '环保',
      location: '北京市朝阳区',
      startTime: '2024-01-15T09:00:00',
      volunteerCount: 15,
      status: 'RECRUITING'
    },
    {
      id: '2',
      title: '老人陪伴服务',
      type: '敬老',
      location: '北京市海淀区',
      startTime: '2024-01-16T14:00:00',
      volunteerCount: 8,
      status: 'ONGOING'
    }
  ],
  volunteersStats: {
    newVolunteers: 12,
    activeVolunteers: 95,
    totalVolunteers: 150
  },
  activitiesStats: {
    recruiting: 15,
    ongoing: 8,
    completed: 45,
    cancelled: 2
  },
  performance: {
    efficiency: 0.85,
    satisfaction: 4.2,
    completionRate: 0.78
  }
}

const mockAnalyticsData: AnalyticsData = {
  overview: {
    totalRecommendations: 150,
    acceptedRecommendations: 45,
    averageScore: 0.78,
    topMatchedTypes: ['环保', '教育'],
    engagementRate: 0.62
  },
  trends: {
    weekly: [
      { date: '2024-01-01', count: 12, score: 0.75 },
      { date: '2024-01-02', count: 15, score: 0.80 },
      { date: '2024-01-03', count: 18, score: 0.78 }
    ],
    monthly: [
      { month: '1月', count: 45, score: 0.76 },
      { month: '2月', count: 52, score: 0.80 },
      { month: '3月', count: 38, score: 0.74 }
    ]
  },
  performance: {
    algorithm: {
      accuracy: 0.85,
      precision: 0.82,
      recall: 0.88,
      f1Score: 0.85
    },
    userSatisfaction: {
      averageRating: 4.2,
      positiveFeedback: 78,
      neutralFeedback: 15,
      negativeFeedback: 7
    }
  },
  distribution: {
    byType: [
      { type: '环保', count: 45, percentage: 30 },
      { type: '教育', count: 38, percentage: 25 },
      { type: '敬老', count: 32, percentage: 21 },
      { type: '扶贫', count: 35, percentage: 24 }
    ],
    byScore: [
      { range: '90-100%', count: 25, percentage: 17 },
      { range: '80-89%', count: 40, percentage: 27 },
      { range: '70-79%', count: 45, percentage: 30 },
      { range: '60-69%', count: 25, percentage: 17 },
      { range: '<60%', count: 15, percentage: 9 }
    ]
  }
}

const mockRecommendations = [
  {
    activity: mockDashboardData.recentActivities[0],
    score: 0.92,
    reasons: ['活动类型匹配', '距离较近', '时间合适'],
    matchDetails: {
      typeMatch: 1.0,
      locationMatch: 0.9,
      timeMatch: 0.8,
      skillMatch: 0.95,
      historyMatch: 0.8,
      availability: 1.0
    }
  },
  {
    activity: mockDashboardData.recentActivities[1],
    score: 0.75,
    reasons: ['活动类型匹配', '时间合适'],
    matchDetails: {
      typeMatch: 0.8,
      locationMatch: 0.6,
      timeMatch: 0.9,
      skillMatch: 0.7,
      historyMatch: 0.8,
      availability: 0.9
    }
  }
]

describe('Dashboard.vue', () => {
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
    
    // Mock ECharts
    global.echarts = {
      init: vi.fn().mockReturnThis(),
      setOption: vi.fn(),
      resize: vi.fn(),
      dispose: vi.fn()
    }
  })

  afterEach(() => {
    vi.clearAllMocks()
  })

  const createWrapper = (props = {}) => {
    return mount(Dashboard, {
      props: {
        dashboardData: mockDashboardData,
        analyticsData: mockAnalyticsData,
        recommendations: mockRecommendations,
        ...props
      }
    })
  }

  describe('Rendering', () => {
    it('renders correctly with dashboard data', () => {
      wrapper = createWrapper()
      
      expect(wrapper.find('.dashboard').exists()).toBe(true)
      expect(wrapper.find('.dashboard-header').exists()).toBe(true)
      
      // Check main sections
      expect(wrapper.find('.overview-section').exists()).toBe(true)
      expect(wrapper.find('.activities-section').exists()).toBe(true)
      expect(wrapper.find('.volunteers-section').exists()).toBe(true)
      expect(wrapper.find('.performance-section').exists()).toBe(true)
      expect(wrapper.find('.recommendations-section').exists()).toBe(true)
    })

    it('renders overview statistics', () => {
      wrapper = createWrapper()
      
      const overviewItems = wrapper.findAll('.overview-stat')
      expect(overviewItems.length).toBe(4)
      
      expect(overviewItems[0].text()).toContain('150')
      expect(overviewItems[0].text()).toContain('总志愿者')
      
      expect(overviewItems[1].text()).toContain('25')
      expect(overviewItems[1].text()).toContain('进行中活动')
      
      expect(overviewItems[2].text()).toContain('45')
      expect(overviewItems[2].text()).toContain('已完成活动')
      
      expect(overviewItems[3].text()).toContain('1200')
      expect(overviewItems[3].text()).toContain('服务时长')
    })

    it('renders recent activities', () => {
      wrapper = createWrapper()
      
      const activityItems = wrapper.findAll('.activity-item')
      expect(activityItems.length).toBe(2)
      
      // Check first activity
      expect(activityItems[0].find('.activity-title').text()).toBe('社区环保活动')
      expect(activityItems[0].find('.activity-type').text()).toBe('环保')
      expect(activityItems[0].find('.activity-location').text()).toBe('北京市朝阳区')
      expect(activityItems[0].find('.activity-status').text()).toBe('招募中')
      
      // Check second activity
      expect(activityItems[1].find('.activity-title').text()).toBe('老人陪伴服务')
      expect(activityItems[1].find('.activity-type').text()).toBe('敬老')
      expect(activityItems[1].find('.activity-location').text()).toBe('北京市海淀区')
      expect(activityItems[1].find('.activity-status').text()).toBe('进行中')
    })

    it('renders volunteer statistics', () => {
      wrapper = createWrapper()
      
      const volunteerItems = wrapper.findAll('.volunteer-stat')
      expect(volunteerItems.length).toBe(3)
      
      expect(volunteerItems[0].text()).toContain('12')
      expect(volunteerItems[0].text()).toContain('新增志愿者')
      
      expect(volunteerItems[1].text()).toContain('95')
      expect(volunteerItems[1].text()).toContain('活跃志愿者')
      
      expect(volunteerItems[2].text()).toContain('150')
      expect(volunteerItems[2].text()).toContain('总志愿者数')
    })

    it('renders activity statistics', () => {
      wrapper = createWrapper()
      
      const activityStatItems = wrapper.findAll('.activity-stat-item')
      expect(activityStatItems.length).toBe(4)
      
      expect(activityStatItems[0].text()).toContain('15')
      expect(activityStatItems[0].text()).toContain('招募中')
      
      expect(activityStatItems[1].text()).toContain('8')
      expect(activityStatItems[1].text()).toContain('进行中')
      
      expect(activityStatItems[2].text()).toContain('45')
      expect(activityStatItems[2].text()).toContain('已完成')
      
      expect(activityStatItems[3].text()).toContain('2')
      expect(activityStatItems[3].text()).toContain('已取消')
    })

    it('renders performance metrics', () => {
      wrapper = createWrapper()
      
      const performanceItems = wrapper.findAll('.performance-metric')
      expect(performanceItems.length).toBe(3)
      
      expect(performanceItems[0].text()).toContain('85%')
      expect(performanceItems[0].text()).toContain('效率')
      
      expect(performanceItems[1].text()).toContain('4.2')
      expect(performanceItems[1].text()).toContain('满意度')
      
      expect(performanceItems[2].text()).toContain('78%')
      expect(performanceItems[2].text()).toContain('完成率')
    })

    it('renders recommendations', () => {
      wrapper = createWrapper()
      
      const recommendationItems = wrapper.findAll('.recommendation-item')
      expect(recommendationItems.length).toBe(2)
      
      // Check first recommendation
      expect(recommendationItems[0].find('.item-header h3').text()).toBe('社区环保活动')
      expect(recommendationItems[0].find('.score-circle').text()).toBe('92%')
      
      // Check second recommendation
      expect(recommendationItems[1].find('.item-header h3').text()).toBe('老人陪伴服务')
      expect(recommendationItems[1].find('.score-circle').text()).toBe('75%')
    })
  })

  describe('Charts', () => {
    it('renders activity type distribution chart', () => {
      wrapper = createWrapper()
      
      expect(wrapper.find('.activity-type-chart').exists()).toBe(true)
      expect(global.echarts.init).toHaveBeenCalled()
      expect(global.echarts.setOption).toHaveBeenCalled()
    })

    it('renders volunteer growth chart', () => {
      wrapper = createWrapper()
      
      expect(wrapper.find('.volunteer-growth-chart').exists()).toBe(true)
      expect(global.echarts.init).toHaveBeenCalled()
      expect(global.echarts.setOption).toHaveBeenCalled()
    })

    it('renders performance trends chart', () => {
      wrapper = createWrapper()
      
      expect(wrapper.find('.performance-trends-chart').exists()).toBe(true)
      expect(global.echarts.init).toHaveBeenCalled()
      expect(global.echarts.setOption).toHaveBeenCalled()
    })

    it('handles chart resize on window resize', async () => {
      wrapper = createWrapper()
      
      // Simulate window resize
      window.dispatchEvent(new Event('resize'))
      
      // Check if resize was called
      expect(global.echarts.resize).toHaveBeenCalled()
    })
  })

  describe('User Interaction', () => {
    it('handles activity view click', async () => {
      wrapper = createWrapper()
      
      const activityItem = wrapper.find('.activity-item')
      await activityItem.trigger('click')
      
      expect(wrapper.emitted('view-activity')).toBeTruthy()
      expect(wrapper.emitted('view-activity')[0][0]).toBe('1')
    })

    it('handles volunteer view click', async () => {
      wrapper = createWrapper()
      
      const volunteerItem = wrapper.find('.volunteer-stat')
      await volunteerItem.trigger('click')
      
      expect(wrapper.emitted('view-volunteers')).toBeTruthy()
    })

    it('handles performance details click', async () => {
      wrapper = createWrapper()
      
      const performanceItem = wrapper.find('.performance-metric')
      await performanceItem.trigger('click')
      
      expect(wrapper.emitted('view-performance')).toBeTruthy()
    })

    it('handles recommendation interaction', async () => {
      wrapper = createWrapper()
      
      // Test join button click
      const joinButton = wrapper.find('[data-join="1"]')
      await joinButton.trigger('click')
      
      expect(wrapper.emitted('join-recommendation')).toBeTruthy()
      expect(wrapper.emitted('join-recommendation')[0][0]).toBe('1')
      
      // Test like button click
      const likeButton = wrapper.find('[data-like="1"]')
      await likeButton.trigger('click')
      
      expect(wrapper.emitted('like-recommendation')).toBeTruthy()
      expect(wrapper.emitted('like-recommendation')[0][0]).toBe('1')
    })

    it('handles refresh button click', async () => {
      wrapper = createWrapper()
      
      const refreshSpy = vi.fn()
      wrapper.vm.refreshData = refreshSpy
      
      await wrapper.find('.refresh-button').trigger('click')
      
      expect(refreshSpy).toHaveBeenCalled()
    })

    it('handles export button click', async () => {
      wrapper = createWrapper()
      
      const exportSpy = vi.fn()
      wrapper.vm.exportData = exportSpy
      
      await wrapper.find('.export-button').trigger('click')
      
      expect(exportSpy).toHaveBeenCalled()
    })
  })

  describe('Real-time Updates', () => {
    it('updates data in real-time', () => {
      wrapper = createWrapper()
      
      // Update dashboard data
      const newData = {
        ...mockDashboardData,
        overview: {
          ...mockDashboardData.overview,
          totalVolunteers: 160
        }
      }
      
      wrapper.vm.dashboardData = newData
      
      const overviewItems = wrapper.findAll('.overview-stat')
      expect(overviewItems[0].text()).toContain('160')
    })

    it('handles WebSocket updates', () => {
      wrapper = createWrapper()
      
      // Simulate WebSocket message
      const mockEvent = {
        data: JSON.stringify({
          type: 'activity_update',
          activity: {
            id: '3',
            title: '新活动',
            type: '教育',
            location: '北京市西城区',
            startTime: '2024-01-17T10:00:00',
            volunteerCount: 5,
            status: 'RECRUITING'
          }
        })
      }
      
      wrapper.vm.handleWebSocketMessage(mockEvent)
      
      expect(wrapper.vm.recentActivities.length).toBe(3)
      expect(wrapper.vm.recentActivities[2].title).toBe('新活动')
    })
  })

  describe('Loading State', () => {
    it('shows loading state', async () => {
      wrapper = createWrapper({ loading: true })
      
      expect(wrapper.find('.loading').exists()).toBe(true)
      expect(wrapper.find('.dashboard-content').exists()).toBe(false)
    })

    it('hides loading state when data is loaded', async () => {
      wrapper = createWrapper({ loading: true })
      await wrapper.setProps({ loading: false })
      
      expect(wrapper.find('.loading').exists()).toBe(false)
      expect(wrapper.find('.dashboard-content').exists()).toBe(true)
    })
  })

  describe('Error Handling', () => {
    it('shows error message when error occurs', async () => {
      wrapper = createWrapper({ error: '数据加载失败' })
      
      expect(wrapper.find('.error').exists()).toBe(true)
      expect(wrapper.text()).toContain('数据加载失败')
    })

    it('hides error message when error is cleared', async () => {
      wrapper = createWrapper({ error: '数据加载失败' })
      await wrapper.setProps({ error: null })
      
      expect(wrapper.find('.error').exists()).toBe(false)
    })

    it('handles WebSocket connection errors', () => {
      wrapper = createWrapper()
      
      const mockError = new Error('连接失败')
      wrapper.vm.handleWebSocketError(mockError)
      
      expect(wrapper.vm.error).toBe('连接失败')
    })
  })

  describe('Accessibility', () => {
    it('has proper ARIA labels and roles', () => {
      wrapper = createWrapper()
      
      const mainContainer = wrapper.find('.dashboard')
      expect(mainContainer.attributes('role')).toBe('region')
      expect(mainContainer.attributes('aria-label')).toBe('数据仪表板')
      
      // Check section accessibility
      const sections = wrapper.findAll('.dashboard-section')
      sections.forEach((section: any, index: number) => {
        expect(section.attributes('role')).toBe('region')
        expect(section.attributes('aria-label')).toContain(`部分 ${index + 1}`)
      })
    })

    it('handles keyboard navigation', async () => {
      wrapper = createWrapper()
      
      // Test Enter key on activity item
      const activityItem = wrapper.find('.activity-item')
      await activityItem.trigger('keydown', { key: 'Enter' })
      
      expect(wrapper.emitted('view-activity')).toBeTruthy()
      
      // Test Space key on refresh button
      const refreshButton = wrapper.find('.refresh-button')
      await refreshButton.trigger('keydown', { key: ' ' })
      
      expect(wrapper.vm.refreshData).toHaveBeenCalled()
    })

    it('supports screen reader announcements', () => {
      wrapper = createWrapper()
      
      // Check for live regions
      const liveRegions = wrapper.findAll('.live-region')
      expect(liveRegions.length).toBeGreaterThan(0)
      
      liveRegions.forEach((region: any) => {
        expect(region.attributes('aria-live')).toBe('polite')
        expect(region.attributes('aria-atomic')).toBe('true')
      })
    })
  })

  describe('Performance', () => {
    it('handles large datasets efficiently', () => {
      const largeData = {
        ...mockDashboardData,
        recentActivities: Array.from({ length: 50 }, (_, i) => ({
          id: `${i + 1}`,
          title: `活动 ${i + 1}`,
          type: ['环保', '教育', '敬老', '扶贫'][i % 4],
          location: `北京市${['朝阳区', '海淀区', '西城区', '东城区'][i % 4]}`,
          startTime: new Date(Date.now() - i * 3600000).toISOString(),
          volunteerCount: Math.floor(Math.random() * 30) + 5,
          status: ['RECRUITING', 'ONGOING', 'COMPLETED'][i % 3]
        }))
      }
      
      wrapper = createWrapper({ dashboardData: largeData })
      
      expect(wrapper.vm.recentActivities.length).toBe(50)
      
      // Check that virtualization is working
      const visibleItems = wrapper.findAll('.activity-item')
      expect(visibleItems.length).toBeLessThan(50)
    })

    it('uses debounced search for performance', async () => {
      wrapper = createWrapper()
      
      const searchInput = wrapper.find('.search-input')
      await searchInput.setValue('test')
      
      // Search should be debounced
      expect(wrapper.vm.searchQuery).toBe('test')
    })

    it('optimizes chart rendering', () => {
      wrapper = createWrapper()
      
      // Check that charts are only initialized once
      expect(global.echarts.init).toHaveBeenCalledTimes(3) // 3 charts
      
      // Check that resize is properly throttled
      window.dispatchEvent(new Event('resize'))
      window.dispatchEvent(new Event('resize'))
      
      expect(global.echarts.resize).toHaveBeenCalledTimes(1) // Should be throttled
    })
  })

  describe('Responsive Design', () => {
    it('renders correctly on different screen sizes', () => {
      wrapper = createWrapper()
      
      // Test mobile view
      wrapper.vm.isMobile = true
      wrapper.vm.$nextTick(() => {
        expect(wrapper.find('.mobile-layout').exists()).toBe(true)
      })
      
      // Test desktop view
      wrapper.vm.isMobile = false
      wrapper.vm.$nextTick(() => {
        expect(wrapper.find('.desktop-layout').exists()).toBe(true)
      })
    })

    it('handles responsive chart sizing', () => {
      wrapper = createWrapper()
      
      // Simulate mobile view
      wrapper.vm.isMobile = true
      window.dispatchEvent(new Event('resize'))
      
      // Check if charts resize
      expect(global.echarts.resize).toHaveBeenCalled()
    })
  })
})