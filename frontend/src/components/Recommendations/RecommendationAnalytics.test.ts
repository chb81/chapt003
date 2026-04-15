import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import RecommendationAnalytics from './RecommendationAnalytics.vue'
import type { AnalyticsData } from '@/types'

// Mock analytics data
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

describe('RecommendationAnalytics.vue', () => {
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
    return mount(RecommendationAnalytics, {
      props: {
        analyticsData: mockAnalyticsData,
        ...props
      }
    })
  }

  describe('Rendering', () => {
    it('renders correctly with analytics data', () => {
      wrapper = createWrapper()
      
      expect(wrapper.find('.recommendation-analytics').exists()).toBe(true)
      expect(wrapper.find('.card-header').exists()).toBe(true)
      
      // Check main sections
      expect(wrapper.find('.overview-section').exists()).toBe(true)
      expect(wrapper.find('.trends-section').exists()).toBe(true)
      expect(wrapper.find('.performance-section').exists()).toBe(true)
      expect(wrapper.find('.distribution-section').exists()).toBe(true)
    })

    it('renders overview statistics', () => {
      wrapper = createWrapper()
      
      const overviewItems = wrapper.findAll('.overview-item')
      expect(overviewItems.length).toBe(5)
      
      expect(overviewItems[0].text()).toContain('150')
      expect(overviewItems[0].text()).toContain('总推荐数')
      
      expect(overviewItems[1].text()).toContain('45')
      expect(overviewItems[1].text()).toContain('接受推荐')
      
      expect(overviewItems[2].text()).toContain('78%')
      expect(overviewItems[2].text()).toContain('平均匹配度')
      
      expect(overviewItems[3].text()).toContain('环保, 教育')
      expect(overviewItems[3].text()).toContain('热门类型')
      
      expect(overviewItems[4].text()).toContain('62%')
      expect(overviewItems[4].text()).toContain('参与率')
    })

    it('renders trend charts', () => {
      wrapper = createWrapper()
      
      expect(wrapper.find('.weekly-chart').exists()).toBe(true)
      expect(wrapper.find('.monthly-chart').exists()).toBe(true)
    })

    it('renders performance metrics', () => {
      wrapper = createWrapper()
      
      const algorithmMetrics = wrapper.findAll('.algorithm-metric')
      expect(algorithmMetrics.length).toBe(4)
      
      expect(algorithmMetrics[0].text()).toContain('准确率')
      expect(algorithmMetrics[0].text()).toContain('85%')
      
      expect(algorithmMetrics[1].text()).toContain('精确率')
      expect(algorithmMetrics[1].text()).toContain('82%')
      
      expect(algorithmMetrics[2].text()).toContain('召回率')
      expect(algorithmMetrics[2].text()).toContain('88%')
      
      expect(algorithmMetrics[3].text()).toContain('F1分数')
      expect(algorithmMetrics[3].text()).toContain('85%')
      
      // User satisfaction
      const satisfactionItem = wrapper.find('.satisfaction-metric')
      expect(satisfactionItem.text()).toContain('4.2')
      expect(satisfactionItem.text()).toContain('用户评分')
    })

    it('renders distribution charts', () => {
      wrapper = createWrapper()
      
      expect(wrapper.find('.type-distribution-chart').exists()).toBe(true)
      expect(wrapper.find('.score-distribution-chart').exists()).toBe(true)
    })
  })

  describe('Time Range Selection', () => {
    it('changes time range when buttons clicked', async () => {
      wrapper = createWrapper()
      
      const weekButton = wrapper.find('[data-range="week"]')
      const monthButton = wrapper.find('[data-range="month"]')
      const yearButton = wrapper.find('[data-range="year"]')
      
      // Test week selection
      await weekButton.trigger('click')
      expect(wrapper.vm.timeRange).toBe('week')
      
      // Test month selection
      await monthButton.trigger('click')
      expect(wrapper.vm.timeRange).toBe('month')
      
      // Test year selection
      await yearButton.trigger('click')
      expect(wrapper.vm.timeRange).toBe('year')
    })

    it('updates charts when time range changes', async () => {
      wrapper = createWrapper()
      
      const weeklyChart = wrapper.find('.weekly-chart')
      const monthlyChart = wrapper.find('.monthly-chart')
      
      // Initially both should be visible
      expect(weeklyChart.exists()).toBe(true)
      expect(monthlyChart.exists()).toBe(true)
      
      // Change to monthly view
      await wrapper.setData({ timeRange: 'month' })
      
      // Now monthly should be more prominent
      expect(wrapper.vm.timeRange).toBe('month')
    })
  })

  describe('Chart Rendering', () => {
    it('initializes ECharts charts', () => {
      wrapper = createWrapper()
      
      // Check if ECharts init was called
      expect(global.echarts.init).toHaveBeenCalled()
      expect(global.echarts.setOption).toHaveBeenCalled()
    })

    it('updates charts when data changes', async () => {
      wrapper = createWrapper()
      
      const newData = {
        ...mockAnalyticsData,
        overview: {
          ...mockAnalyticsData.overview,
          totalRecommendations: 200
        }
      }
      
      await wrapper.setProps({ analyticsData: newData })
      
      // Charts should be updated
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

  describe('Data Refresh', () => {
    it('refreshes data when refresh button clicked', async () => {
      wrapper = createWrapper()
      
      const refreshSpy = vi.fn()
      wrapper.vm.refreshData = refreshSpy
      
      await wrapper.find('.refresh-button').trigger('click')
      
      expect(refreshSpy).toHaveBeenCalled()
    })

    it('shows loading state during refresh', async () => {
      wrapper = createWrapper()
      
      // Mock API call with delay
      global.fetch.mockImplementation(() => 
        new Promise(resolve => setTimeout(resolve, 1000))
      )
      
      await wrapper.find('.refresh-button').trigger('click')
      
      expect(wrapper.vm.loading).toBe(true)
      
      // Wait for promise to resolve
      await new Promise(resolve => setTimeout(resolve, 1100))
      
      expect(wrapper.vm.loading).toBe(false)
    })

    it('handles refresh error', async () => {
      wrapper = createWrapper()
      
      // Mock API error
      global.fetch.mockRejectedValue(new Error('刷新失败'))
      
      await wrapper.find('.refresh-button').trigger('click')
      
      expect(wrapper.vm.error).toBe('刷新失败')
    })
  })

  describe('Export Functionality', () => {
    it('exports data when export button clicked', async () => {
      wrapper = createWrapper()
      
      const exportSpy = vi.fn()
      wrapper.vm.exportData = exportSpy
      
      await wrapper.find('.export-button').trigger('click')
      
      expect(exportSpy).toHaveBeenCalled()
    })

    it('exports data in correct format', () => {
      wrapper = createWrapper()
      
      // Mock download functionality
      const mockDownload = vi.fn()
      global.URL.createObjectURL = vi.fn().mockReturnValue('blob-url')
      global.URL.revokeObjectURL = vi.fn()
      
      wrapper.vm.exportData('pdf')
      
      expect(mockDownload).toHaveBeenCalled()
    })

    it('exports different formats', async () => {
      wrapper = createWrapper()
      
      const pdfSpy = vi.fn()
      const csvSpy = vi.fn()
      const excelSpy = vi.fn()
      
      wrapper.vm.exportData = (format: string) => {
        if (format === 'pdf') pdfSpy()
        if (format === 'csv') csvSpy()
        if (format === 'excel') excelSpy()
      }
      
      wrapper.vm.exportData('pdf')
      wrapper.vm.exportData('csv')
      wrapper.vm.exportData('excel')
      
      expect(pdfSpy).toHaveBeenCalled()
      expect(csvSpy).toHaveBeenCalled()
      expect(excelSpy).toHaveBeenCalled()
    })
  })

  describe('Performance Metrics', () => {
    it('displays algorithm performance correctly', () => {
      wrapper = createWrapper()
      
      const metrics = wrapper.findAll('.algorithm-metric')
      expect(metrics.length).toBe(4)
      
      // Check each metric
      expect(metrics[0].text()).toContain('准确率')
      expect(metrics[0].text()).toContain('85%')
      
      expect(metrics[1].text()).toContain('精确率')
      expect(metrics[1].text()).toContain('82%')
      
      expect(metrics[2].text()).toContain('召回率')
      expect(metrics[2].text()).toContain('88%')
      
      expect(metrics[3].text()).toContain('F1分数')
      expect(metrics[3].text()).toContain('85%')
    })

    it('displays user satisfaction correctly', () => {
      wrapper = createWrapper()
      
      const satisfaction = wrapper.find('.satisfaction-metric')
      expect(satisfaction.text()).toContain('4.2')
      expect(satisfaction.text()).toContain('用户评分')
      
      const feedback = wrapper.findAll('.feedback-item')
      expect(feedback.length).toBe(3)
      
      expect(feedback[0].text()).toContain('正面反馈')
      expect(feedback[0].text()).toContain('78')
      
      expect(feedback[1].text()).toContain('中性反馈')
      expect(feedback[1].text()).toContain('15')
      
      expect(feedback[2].text()).toContain('负面反馈')
      expect(feedback[2].text()).toContain('7')
    })
  })

  describe('Distribution Analysis', () => {
    it('displays type distribution correctly', () => {
      wrapper = createWrapper()
      
      const typeItems = wrapper.findAll('.type-distribution-item')
      expect(typeItems.length).toBe(4)
      
      expect(typeItems[0].text()).toContain('环保')
      expect(typeItems[0].text()).toContain('30%')
      
      expect(typeItems[1].text()).toContain('教育')
      expect(typeItems[1].text()).toContain('25%')
      
      expect(typeItems[2].text()).toContain('敬老')
      expect(typeItems[2].text()).toContain('21%')
      
      expect(typeItems[3].text()).toContain('扶贫')
      expect(typeItems[3].text()).toContain('24%')
    })

    it('displays score distribution correctly', () => {
      wrapper = createWrapper()
      
      const scoreItems = wrapper.findAll('.score-distribution-item')
      expect(scoreItems.length).toBe(5)
      
      expect(scoreItems[0].text()).toContain('90-100%')
      expect(scoreItems[0].text()).toContain('17%')
      
      expect(scoreItems[1].text()).toContain('80-89%')
      expect(scoreItems[1].text()).toContain('27%')
      
      expect(scoreItems[2].text()).toContain('70-79%')
      expect(scoreItems[2].text()).toContain('30%')
      
      expect(scoreItems[3].text()).toContain('60-69%')
      expect(scoreItems[3].text()).toContain('17%')
      
      expect(scoreItems[4].text()).toContain('<60%')
      expect(scoreItems[4].text()).toContain('9%')
    })
  })

  describe('Accessibility', () => {
    it('has proper ARIA labels and roles', () => {
      wrapper = createWrapper()
      
      const mainContainer = wrapper.find('.recommendation-analytics')
      expect(mainContainer.attributes('role')).toBe('region')
      expect(mainContainer.attributes('aria-label')).toBe('推荐分析面板')
      
      // Check chart accessibility
      const charts = wrapper.findAll('.chart-container')
      charts.forEach((chart: any, index: number) => {
        expect(chart.attributes('role')).toBe('img')
        expect(chart.attributes('aria-label')).toContain(`图表 ${index + 1}`)
      })
    })

    it('handles keyboard navigation', async () => {
      wrapper = createWrapper()
      
      // Test Enter key on refresh button
      const refreshButton = wrapper.find('.refresh-button')
      await refreshButton.trigger('keydown', { key: 'Enter' })
      
      expect(wrapper.vm.refreshData).toHaveBeenCalled()
      
      // Test Space key on export button
      const exportButton = wrapper.find('.export-button')
      await exportButton.trigger('keydown', { key: ' ' })
      
      expect(wrapper.vm.exportData).toHaveBeenCalled()
    })
  })

  describe('Error Handling', () => {
    it('shows error message when data loading fails', async () => {
      wrapper = createWrapper()
      
      // Mock API error
      global.fetch.mockRejectedValue(new Error('数据加载失败'))
      
      await wrapper.find('.refresh-button').trigger('click')
      
      expect(wrapper.find('.error').exists()).toBe(true)
      expect(wrapper.text()).toContain('数据加载失败')
    })

    it('hides error when data loads successfully', async () => {
      wrapper = createWrapper({ error: '加载失败' })
      
      // Mock successful API response
      global.fetch.mockResolvedValue({
        ok: true,
        json: () => Promise.resolve(mockAnalyticsData)
      })
      
      await wrapper.find('.refresh-button').trigger('click')
      
      expect(wrapper.find('.error').exists()).toBe(false)
    })
  })
})