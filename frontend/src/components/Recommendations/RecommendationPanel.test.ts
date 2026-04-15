import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import RecommendationPanel from './RecommendationPanel.vue'
import type { Recommendation, Activity } from '@/types'

// Mock data
const mockActivities: Activity[] = [
  {
    id: '1',
    title: '社区环保活动',
    type: '环保',
    location: '北京市朝阳区',
    startTime: '2024-01-15T09:00:00',
    endTime: '2024-01-15T12:00:00',
    requiredSkills: ['环保知识'],
    volunteerCount: 15,
    maxVolunteers: 30,
    description: '参与社区环保活动，美化环境',
    requirements: '身体健康，有环保意识',
    contactInfo: '环保协会',
    status: 'RECRUITING'
  },
  {
    id: '2',
    title: '老人陪伴服务',
    type: '敬老',
    location: '北京市海淀区',
    startTime: '2024-01-16T14:00:00',
    endTime: '2024-01-16T17:00:00',
    requiredSkills: ['沟通能力'],
    volunteerCount: 8,
    maxVolunteers: 20,
    description: '陪伴老人聊天，提供情感支持',
    requirements: '有耐心，善于沟通',
    contactInfo: '社区服务中心',
    status: 'RECRUITING'
  }
]

const mockRecommendations: Recommendation[] = [
  {
    activity: mockActivities[0],
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
    activity: mockActivities[1],
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

describe('RecommendationPanel.vue', () => {
  let wrapper: any
  let pinia: any

  beforeEach(() => {
    pinia = createPinia()
    setActivePinia(pinia)
    
    // Mock fetch API
    global.fetch = vi.fn()
  })

  afterEach(() => {
    vi.clearAllMocks()
  })

  const createWrapper = (props = {}) => {
    return mount(RecommendationPanel, {
      props: {
        recommendations: mockRecommendations,
        ...props
      }
    })
  }

  describe('Rendering', () => {
    it('renders correctly with recommendations', () => {
      wrapper = createWrapper()
      
      expect(wrapper.find('.recommendation-panel').exists()).toBe(true)
      expect(wrapper.find('.panel-header').exists()).toBe(true)
      expect(wrapper.find('.recommendation-list').exists()).toBe(true)
      
      // Check header title
      const headerTitle = wrapper.find('.header-title')
      expect(headerTitle.text()).toContain('智能推荐')
      expect(headerTitle.text()).toContain('2 个推荐')
    })

    it('renders empty state when no recommendations', () => {
      wrapper = createWrapper({ recommendations: [] })
      
      expect(wrapper.find('.empty-state').exists()).toBe(true)
      expect(wrapper.text()).toContain('暂无推荐')
    })
  })

  describe('Filtering', () => {
    it('shows all recommendations by default', () => {
      wrapper = createWrapper()
      
      const items = wrapper.findAll('.recommendation-item')
      expect(items.length).toBe(2)
    })

    it('filters high score recommendations', async () => {
      wrapper = createWrapper()
      
      await wrapper.find('[data-filter="high"]').trigger('click')
      
      const items = wrapper.findAll('.recommendation-item')
      expect(items.length).toBe(1)
      expect(items[0].text()).toContain('社区环保活动')
    })

    it('filters nearby recommendations', async () => {
      wrapper = createWrapper()
      
      await wrapper.find('[data-filter="nearby"]').trigger('click')
      
      // Check if nearby filtering is applied (mock implementation)
      const items = wrapper.findAll('.recommendation-item')
      expect(items.length).toBe(2) // Both are considered nearby in mock
    })
  })

  describe('Interaction', () => {
    it('refreshes recommendations when refresh button clicked', async () => {
      wrapper = createWrapper()
      
      const refreshSpy = vi.fn()
      wrapper.vm.refreshRecommendations = refreshSpy
      
      await wrapper.find('.refresh-button').trigger('click')
      
      expect(refreshSpy).toHaveBeenCalled()
    })

    it('emits join event when join button clicked', async () => {
      wrapper = createWrapper()
      
      await wrapper.find('[data-join="1"]').trigger('click')
      
      expect(wrapper.emitted('join')).toBeTruthy()
      expect(wrapper.emitted('join')[0]).toEqual(['1'])
    })

    it('emits like event when like button clicked', async () => {
      wrapper = createWrapper()
      
      await wrapper.find('[data-like="1"]').trigger('click')
      
      expect(wrapper.emitted('like')).toBeTruthy()
      expect(wrapper.emitted('like')[0]).toEqual(['1'])
    })

    it('emits dislike event when dislike button clicked', async () => {
      wrapper = createWrapper()
      
      await wrapper.find('[data-dislike="1"]').trigger('click')
      
      expect(wrapper.emitted('dislike')).toBeTruthy()
      expect(wrapper.emitted('dislike')[0]).equalTo(['1'])
    })
  })

  describe('Score Display', () => {
    it('displays score correctly', () => {
      wrapper = createWrapper()
      
      const scoreElements = wrapper.findAll('.score-circle')
      expect(scoreElements.length).toBe(2)
      expect(scoreElements[0].text()).toBe('92%')
      expect(scoreElements[1].text()).toBe('75%')
    })

    it('applies correct CSS classes based on score', () => {
      wrapper = createWrapper()
      
      const highScoreItem = wrapper.find('.recommendation-item.high-score')
      expect(highScoreItem.exists()).toBe(true)
      
      const item = highScoreItem.find('.score-circle')
      expect(item.classes()).toContain('high-score')
    })
  })

  describe('Loading State', () => {
    it('shows loading state', async () => {
      wrapper = createWrapper({ loading: true })
      
      expect(wrapper.find('.loading').exists()).toBe(true)
      expect(wrapper.find('.recommendation-list').exists()).toBe(false)
    })

    it('hides loading state when loading is false', async () => {
      wrapper = createWrapper({ loading: true })
      await wrapper.setProps({ loading: false })
      
      expect(wrapper.find('.loading').exists()).toBe(false)
      expect(wrapper.find('.recommendation-list').exists()).toBe(true)
    })
  })

  describe('Error Handling', () => {
    it('shows error message when error occurs', async () => {
      wrapper = createWrapper({ error: '加载失败' })
      
      expect(wrapper.find('.error').exists()).toBe(true)
      expect(wrapper.text()).toContain('加载失败')
    })

    it('hides error message when error is cleared', async () => {
      wrapper = createWrapper({ error: '加载失败' })
      await wrapper.setProps({ error: null })
      
      expect(wrapper.find('.error').exists()).toBe(false)
    })
  })

  describe('Accessibility', () => {
    it('has proper ARIA attributes', () => {
      wrapper = createWrapper()
      
      const panel = wrapper.find('.recommendation-panel')
      expect(panel.attributes('aria-label')).toBe('智能推荐面板')
      
      const items = wrapper.findAll('.recommendation-item')
      items.forEach((item: any, index: number) => {
        expect(item.attributes('role')).toBe('article')
        expect(item.attributes('aria-label')).toContain(`推荐活动 ${index + 1}`)
      })
    })

    it('handles keyboard navigation', async () => {
      wrapper = createWrapper()
      
      // Test Enter key on join button
      const joinButton = wrapper.find('[data-join="1"]')
      await joinButton.trigger('keydown', { key: 'Enter' })
      
      expect(wrapper.emitted('join')).toBeTruthy()
      
      // Test Space key on like button
      const likeButton = wrapper.find('[data-like="1"]')
      await likeButton.trigger('keydown', { key: ' ' })
      
      expect(wrapper.emitted('like')).toBeTruthy()
    })
  })
})