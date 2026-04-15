import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import RecommendationSettings from './RecommendationSettings.vue'
import type { UserPreference } from '@/utils/recommendation'

// Mock data
const mockUserPreference: UserPreference = {
  userId: 'user1',
  activityTypes: ['环保', '教育'],
  locations: ['北京市朝阳区'],
  timeSlots: ['周末', '晚上'],
  skillRequirements: ['沟通能力'],
  preferences: {
    minDuration: 2,
    maxDuration: 8,
    maxDistance: 10,
    preferredDays: [0, 6]
  },
  history: {
    joinedActivities: ['activity1'],
    likedActivities: ['activity2'],
    dislikedActivities: []
  },
  scores: {
    typeScore: 0.8,
    locationScore: 0.7,
    timeScore: 0.9,
    skillScore: 0.6,
    historyScore: 0.5
  }
}

const mockActivityTypes = [
  { value: '环保', label: '环保活动' },
  { value: '教育', label: '教育活动' },
  { value: '敬老', label: '敬老活动' },
  { value: '扶贫', label: '扶贫活动' }
]

describe('RecommendationSettings.vue', () => {
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
  })

  afterEach(() => {
    vi.clearAllMocks()
  })

  const createWrapper = (props = {}) => {
    return mount(RecommendationSettings, {
      props: {
        userPreference: mockUserPreference,
        activityTypes: mockActivityTypes,
        ...props
      }
    })
  }

  describe('Rendering', () => {
    it('renders correctly with user preference', () => {
      wrapper = createWrapper()
      
      expect(wrapper.find('.recommendation-settings').exists()).toBe(true)
      expect(wrapper.find('.card-header').exists()).toBe(true)
      
      // Check form elements
      expect(wrapper.find('form').exists()).toBe(true)
      expect(wrapper.find('.save-button').exists()).toBe(true)
    })

    it('renders activity types correctly', () => {
      wrapper = createWrapper()
      
      const checkboxes = wrapper.findAll('.activity-type-checkbox')
      expect(checkboxes.length).toBe(4)
      
      // Check selected states
      expect(checkboxes[0].attributes('checked')).toBe('checked') // 环保
      expect(checkboxes[1].attributes('checked')).toBe('checked') // 教育
      expect(checkboxes[2].attributes('checked')).toBe(undefined) // 敬老
      expect(checkboxes[3].attributes('checked')).toBe(undefined) // 扶贫
    })

    it('renders location preferences correctly', () => {
      wrapper = createWrapper()
      
      expect(wrapper.find('.location-input').element.value).toBe('北京市朝阳区')
    })

    it('renders time preferences correctly', () => {
      wrapper = createWrapper()
      
      const timeCheckboxes = wrapper.findAll('.time-slot-checkbox')
      expect(timeCheckboxes.length).toBe(3)
      
      expect(timeCheckboxes[0].attributes('checked')).toBe('checked') // 周末
      expect(timeCheckboxes[1].attributes('checked')).toBe('checked') // 晚上
      expect(timeCheckboxes[2].attributes('checked')).toBe(undefined) // 工作日
    })

    it('renders skill requirements correctly', () => {
      wrapper = createWrapper()
      
      expect(wrapper.find('.skill-input').element.value).toBe('沟通能力')
    })

    it('renders duration preferences correctly', () => {
      wrapper = createWrapper()
      
      const minDuration = wrapper.find('.min-duration-input')
      const maxDuration = wrapper.find('.max-duration-input')
      
      expect(minDuration.element.value).toBe('2')
      expect(maxDuration.element.value).toBe('8')
    })

    it('renders distance preferences correctly', () => {
      wrapper = createWrapper()
      
      const distanceInput = wrapper.find('.distance-input')
      expect(distanceInput.element.value).toBe('10')
    })

    it('renders preferred days correctly', () => {
      wrapper = createWrapper()
      
      const dayCheckboxes = wrapper.findAll('.preferred-day-checkbox')
      expect(dayCheckboxes.length).toBe(7)
      
      expect(dayCheckboxes[0].attributes('checked')).toBe(undefined) // 周一
      expect(dayCheckboxes[6].attributes('checked')).toBe('checked') // 周日
    })
  })

  describe('Form Validation', () => {
    it('validates required fields', async () => {
      wrapper = createWrapper()
      
      // Clear activity types (required field)
      await wrapper.setData({ 
        settings: { 
          ...mockUserPreference,
          activityTypes: []
        } 
      })
      
      await wrapper.find('form').trigger('submit')
      
      expect(wrapper.vm.errors.activityTypes).toBe('请至少选择一个活动类型')
    })

    it('validates duration range', async () => {
      wrapper = createWrapper()
      
      // Set invalid duration range
      await wrapper.setData({ 
        settings: { 
          ...mockUserPreference,
          preferences: {
            ...mockUserPreference.preferences,
            minDuration: 10,
            maxDuration: 2
          }
        } 
      })
      
      await wrapper.find('form').trigger('submit')
      
      expect(wrapper.vm.errors.durationRange).toBe('最小时长不能大于最大时长')
    })

    it('validates distance', async () => {
      wrapper = createWrapper()
      
      // Set invalid distance
      await wrapper.setData({ 
        settings: { 
          ...mockUserPreference,
          preferences: {
            ...mockUserPreference.preferences,
            maxDistance: -1
          }
        } 
      })
      
      await wrapper.find('form').trigger('submit')
      
      expect(wrapper.vm.errors.maxDistance).toBe('最大距离必须大于0')
    })
  })

  describe('User Interaction', () => {
    it('handles activity type changes', async () => {
      wrapper = createWrapper()
      
      const checkbox = wrapper.find('.activity-type-checkbox[value="敬老"]')
      await checkbox.trigger('click')
      
      expect(wrapper.vm.settings.activityTypes).toContain('敬老')
    })

    it('handles location input changes', async () => {
      wrapper = createWrapper()
      
      const input = wrapper.find('.location-input')
      await input.setValue('北京市海淀区')
      
      expect(wrapper.vm.settings.locations).toEqual(['北京市海淀区'])
    })

    it('handles time slot changes', async () => {
      wrapper = createWrapper()
      
      const checkbox = wrapper.find('.time-slot-checkbox[value="工作日"]')
      await checkbox.trigger('click')
      
      expect(wrapper.vm.settings.timeSlots).toContain('工作日')
    })

    it('handles skill requirement changes', async () => {
      wrapper = createWrapper()
      
      const input = wrapper.find('.skill-input')
      await input.setValue('专业技能')
      
      expect(wrapper.vm.settings.skillRequirements).toEqual(['专业技能'])
    })

    it('handles duration changes', async () => {
      wrapper = createWrapper()
      
      const minInput = wrapper.find('.min-duration-input')
      const maxInput = wrapper.find('.max-duration-input')
      
      await minInput.setValue('3')
      await maxInput.setValue('6')
      
      expect(wrapper.vm.settings.preferences.minDuration).toBe(3)
      expect(wrapper.vm.settings.preferences.maxDuration).toBe(6)
    })

    it('handles distance changes', async () => {
      wrapper = createWrapper()
      
      const input = wrapper.find('.distance-input')
      await input.setValue('15')
      
      expect(wrapper.vm.settings.preferences.maxDistance).toBe(15)
    })

    it('handles preferred day changes', async () => {
      wrapper = createWrapper()
      
      const checkbox = wrapper.find('.preferred-day-checkbox[value="1"]') // 周二
      await checkbox.trigger('click')
      
      expect(wrapper.vm.settings.preferences.preferredDays).toContain(1)
    })
  })

  describe('Form Submission', () => {
    it('submits form successfully', async () => {
      wrapper = createWrapper()
      
      const saveSpy = vi.fn()
      wrapper.vm.saveSettings = saveSpy
      
      await wrapper.find('form').trigger('submit')
      
      expect(saveSpy).toHaveBeenCalled()
    })

    it('shows loading state during save', async () => {
      wrapper = createWrapper()
      
      // Mock API call
      global.fetch.mockImplementation(() => 
        new Promise(resolve => setTimeout(resolve, 1000))
      )
      
      await wrapper.find('form').trigger('submit')
      
      expect(wrapper.vm.saving).toBe(true)
      
      // Wait for promise to resolve
      await new Promise(resolve => setTimeout(resolve, 1100))
      
      expect(wrapper.vm.saving).toBe(false)
    })

    it('handles save error', async () => {
      wrapper = createWrapper()
      
      // Mock API error
      global.fetch.mockRejectedValue(new Error('保存失败'))
      
      await wrapper.find('form').trigger('submit')
      
      expect(wrapper.vm.error).toBe('保存失败')
    })

    it('emits save event on successful save', async () => {
      wrapper = createWrapper()
      
      // Mock successful API response
      global.fetch.mockResolvedValue({
        ok: true,
        json: () => Promise.resolve({ success: true })
      })
      
      await wrapper.find('form').trigger('submit')
      
      expect(wrapper.emitted('save')).toBeTruthy()
      expect(wrapper.emitted('save')[0][0]).toEqual(mockUserPreference)
    })
  })

  describe('Reset Functionality', () => {
    it('resets form to original values', async () => {
      wrapper = createWrapper()
      
      // Make some changes
      await wrapper.setData({ 
        settings: { 
          ...mockUserPreference,
          activityTypes: ['教育']
        } 
      })
      
      await wrapper.find('.reset-button').trigger('click')
      
      expect(wrapper.vm.settings.activityTypes).toEqual(['环保', '教育'])
      expect(wrapper.vm.errors).toEqual({})
    })
  })

  describe('Statistics Display', () => {
    it('displays recommendation statistics', () => {
      wrapper = createWrapper()
      
      expect(wrapper.find('.statistics-section').exists()).toBe(true)
      
      // Check score displays
      const scoreItems = wrapper.findAll('.score-item')
      expect(scoreItems.length).toBe(5)
      
      expect(scoreItems[0].text()).toContain('类型匹配度')
      expect(scoreItems[0].text()).toContain('80%')
      expect(scoreItems[1].text()).toContain('位置匹配度')
      expect(scoreItems[1].text()).toContain('70%')
    })

    it('updates statistics when preferences change', async () => {
      wrapper = createWrapper()
      
      // Add more activity types
      await wrapper.setData({ 
        settings: { 
          ...mockUserPreference,
          activityTypes: ['环保', '教育', '敬老']
        } 
      })
      
      // Statistics should be updated
      const scoreItems = wrapper.findAll('.score-item')
      expect(scoreItems[0].text()).toContain('类型匹配度')
    })
  })

  describe('Accessibility', () => {
    it('has proper form labels and accessibility attributes', () => {
      wrapper = createWrapper()
      
      const form = wrapper.find('form')
      expect(form.attributes('role')).toBe('form')
      expect(form.attributes('aria-label')).toBe('推荐偏好设置')
      
      // Check input accessibility
      const inputs = wrapper.findAll('input')
      inputs.forEach((input: any) => {
        expect(input.attributes('type') || input.attributes('role')).toBeTruthy()
      })
    })

    it('handles keyboard navigation', async () => {
      wrapper = createWrapper()
      
      // Test Enter key on save button
      const saveButton = wrapper.find('.save-button')
      await saveButton.trigger('keydown', { key: 'Enter' })
      
      expect(wrapper.vm.saveSettings).toHaveBeenCalled()
      
      // Test Escape key on reset
      const resetButton = wrapper.find('.reset-button')
      await resetButton.trigger('keydown', { key: 'Escape' })
      
      expect(wrapper.vm.resetSettings).toHaveBeenCalled()
    })
  })
})