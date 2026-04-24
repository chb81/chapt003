import { describe, it, expect, beforeEach, vi } from 'vitest'
import { getUserList, getUserDetail, updateUserRole, updateUserStatus, deleteUser } from '@/api/user'
import request from '@/api/request'

vi.mock('@/api/request', () => ({
  default: {
    get: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}))

describe('User API', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('getUserList', () => {
    it('should fetch user list with params', async () => {
      const mockResponse = {
        code: 200,
        message: '获取用户列表成功',
        data: {
          content: [
            {
              id: 1,
              username: 'testuser',
              email: 'test@example.com',
              role: 'USER',
              status: 'ACTIVE',
              emailVerified: true,
              mobileVerified: false,
              createdAt: '2024-01-01T00:00:00Z',
            },
          ],
          totalPages: 1,
          totalElements: 1,
          number: 0,
          size: 10,
          first: true,
          last: true,
          empty: false,
        },
      }

      vi.mocked(request.get).mockResolvedValue(mockResponse)

      const result = await getUserList({ page: 0, size: 10 })

      expect(request.get).toHaveBeenCalledWith('/admin/users', {
        params: { page: 0, size: 10 },
      })
    })

    it('should handle API errors', async () => {
      const error = new Error('Network error')
      vi.mocked(request.get).mockRejectedValue(error)

      await expect(getUserList({ page: 0, size: 10 })).rejects.toThrow('Network error')
    })
  })

  describe('getUserDetail', () => {
    it('should fetch user detail by ID', async () => {
      const mockResponse = {
        code: 200,
        message: '获取用户详情成功',
        data: {
          id: 1,
          username: 'testuser',
          email: 'test@example.com',
          role: 'USER',
          status: 'ACTIVE',
          emailVerified: true,
          mobileVerified: false,
          createdAt: '2024-01-01T00:00:00Z',
          loginHistory: [],
          auditLogs: [],
        },
      }

      vi.mocked(request.get).mockResolvedValue(mockResponse)

      const result = await getUserDetail(1)

      expect(request.get).toHaveBeenCalledWith('/admin/users/1')
    })
  })

  describe('updateUserRole', () => {
    it('should update user role', async () => {
      vi.mocked(request.put).mockResolvedValue({ code: 200, message: 'Success', data: null })

      await updateUserRole(1, { role: 'ADMIN' })

      expect(request.put).toHaveBeenCalledWith('/admin/users/1/role', { role: 'ADMIN' })
    })
  })

  describe('updateUserStatus', () => {
    it('should update user status', async () => {
      vi.mocked(request.put).mockResolvedValue({ code: 200, message: 'Success', data: null })

      await updateUserStatus(1, { status: 'DISABLED' })

      expect(request.put).toHaveBeenCalledWith('/admin/users/1/status', { status: 'DISABLED' })
    })
  })

  describe('deleteUser', () => {
    it('should delete user', async () => {
      vi.mocked(request.delete).mockResolvedValue({ code: 200, message: 'Success', data: null })

      await deleteUser(1)

      expect(request.delete).toHaveBeenCalledWith('/admin/users/1')
    })
  })
})
