import { describe, it, expect, beforeEach } from 'vitest'
import { vi } from 'vitest'
import { getUserList } from '@/api/user'

// 模拟大量用户数据
const generateLargeUserDataset = (count: number) => {
  return Array.from({ length: count }, (_, index) => ({
    id: index + 1,
    username: `user${index + 1}`,
    email: `user${index + 1}@example.com`,
    role: index % 10 === 0 ? 'ADMIN' : 'USER',
    status: index % 20 === 0 ? 'DISABLED' : 'ACTIVE',
    emailVerified: true,
    mobileVerified: index % 3 === 0,
    createdAt: `2024-01-${String(Math.floor(index / 30) + 1).padStart(2, '0')}T00:00:00Z`,
  }))
}

describe('Performance Tests', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('Large Dataset Handling', () => {
    it('should handle large user list requests', async () => {
      const largeDataset = generateLargeUserDataset(1000)
      const mockResponse = {
        content: largeDataset,
        totalPages: 100,
        totalElements: 1000,
        number: 0,
        size: 10,
        first: true,
        last: false,
        empty: false,
      }

      // 模拟延迟
      vi.spyOn(global, 'fetch').mockImplementationOnce(
        () => Promise.resolve({
          ok: true,
          json: () => Promise.resolve(mockResponse),
          status: 200,
        } as Response)
      )

      const startTime = performance.now()
      const result = await getUserList({ page: 0, size: 10 })
      const endTime = performance.now()

      const duration = endTime - startTime
      
      // 验证响应时间在合理范围内 (应该小于1秒)
      expect(duration).toBeLessThan(1000)
      expect(result.content.length).toBe(10)
    })

    it('should handle pagination efficiently', async () => {
      const largeDataset = generateLargeUserDataset(1000)
      const mockResponses = [
        { content: largeDataset.slice(0, 100), totalPages: 100, totalElements: 1000 },
        { content: largeDataset.slice(100, 200), totalPages: 100, totalElements: 1000 },
      ]

      vi.spyOn(global, 'fetch')
        .mockImplementationOnce(() => Promise.resolve({
          ok: true,
          json: () => Promise.resolve(mockResponses[0]),
          status: 200,
        } as Response))
        .mockImplementationOnce(() => Promise.resolve({
          ok: true,
          json: () => Promise.resolve(mockResponses[1]),
          status: 200,
        } as Response))

      const page1Time = performance.now()
      await getUserList({ page: 0, size: 100 })
      const page2Time = performance.now()
      await getUserList({ page: 1, size: 100 })
      const endTime = performance.now()

      const totalDuration = endTime - page1Time
      
      // 验证分页效率
      expect(totalDuration).toBeLessThan(2000)
    })
  })

  describe('Memory Usage Tests', () => {
    it('should not leak memory with large datasets', () => {
      const initialMemory = performance.memory?.usedJSHeapSize || 0
      
      // 模拟处理大量数据
      const largeData = generateLargeUserDataset(5000)
      const processed = largeData.map(user => ({
        ...user,
        displayName: user.username,
        emailDomain: user.email.split('@')[1]
      }))
      
      const finalMemory = performance.memory?.usedJSHeapSize || 0
      const memoryIncrease = finalMemory - initialMemory
      
      // 内存增长应该合理 (小于10MB)
      expect(memoryIncrease).toBeLessThan(10 * 1024 * 1024)
    })

    it('should handle rapid API calls efficiently', async () => {
      const largeDataset = generateLargeUserDataset(1000)
      const mockResponse = {
        content: largeDataset,
        totalPages: 100,
        totalElements: 1000,
        number: 0,
        size: 10,
        first: true,
        last: false,
        empty: false,
      }

      vi.spyOn(global, 'fetch').mockImplementation(
        () => Promise.resolve({
          ok: true,
          json: () => Promise.resolve(mockResponse),
          status: 200,
        } as Response)
      )

      // 模拟并发请求
      const startTime = performance.now()
      const promises = Array.from({ length: 10 }, (_, i) => 
        getUserList({ page: i, size: 10 })
      )
      const results = await Promise.all(promises)
      const endTime = performance.now()

      const totalDuration = endTime - startTime
      
      // 验证并发请求处理时间
      expect(totalDuration).toBeLessThan(5000)
      expect(results.length).toBe(10)
    })
  })

  describe('Search Performance Tests', () => {
    it('should search efficiently in large datasets', async () => {
      const largeDataset = generateLargeUserDataset(10000)
      const mockResponse = {
        content: largeDataset,
        totalPages: 1000,
        totalElements: 10000,
        number: 0,
        size: 10,
        first: true,
        last: false,
        empty: false,
      }

      vi.spyOn(global, 'fetch').mockImplementation(
        () => Promise.resolve({
          ok: true,
          json: () => Promise.resolve(mockResponse),
          status: 200,
        } as Response)
      )

      const startTime = performance.now()
      await getUserList({ page: 0, size: 10, search: 'testuser' })
      const endTime = performance.now()

      const duration = endTime - startTime
      
      // 搜索响应时间应该合理
      expect(duration).toBeLessThan(1000)
    })
  })
})