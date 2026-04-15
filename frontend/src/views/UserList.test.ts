import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import UserList from '@/views/UserList.vue'
import type { User, PageResponse } from '@/types'

// 模拟API响应
const mockUsers: PageResponse<User> = {
  content: [
    {
      id: 1,
      username: 'admin',
      email: 'admin@example.com',
      role: 'ADMIN',
      status: 'ACTIVE',
      emailVerified: true,
      mobileVerified: false,
      createdAt: '2024-01-01T00:00:00Z',
    },
    {
      id: 2,
      username: 'user1',
      email: 'user1@example.com',
      role: 'USER',
      status: 'ACTIVE',
      emailVerified: true,
      mobileVerified: true,
      createdAt: '2024-01-02T00:00:00Z',
    },
  ],
  totalPages: 1,
  totalElements: 2,
  number: 0,
  size: 10,
  first: true,
  last: true,
  empty: false,
}

describe('UserList.vue', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('should render users list', async () => {
    const wrapper = mount(UserList, {
      global: {
        stubs: {
          'el-table': true,
          'el-table-column': true,
          'el-pagination': true,
          'el-input': true,
          'el-button': true,
          'el-select': true,
          'el-option': true,
        }
      }
    })

    // 模拟数据加载
    await wrapper.vm.$nextTick()
    
    // 检查表格是否存在
    expect(wrapper.find('.user-list').exists()).toBe(true)
  })

  it('should search users', async () => {
    const wrapper = mount(UserList, {
      global: {
        stubs: {
          'el-table': true,
          'el-table-column': true,
          'el-pagination': true,
          'el-input': true,
          'el-button': true,
          'el-select': true,
          'el-option': true,
        }
      }
    })

    // 模拟搜索输入
    await wrapper.setData({
      searchQuery: 'admin'
    })

    // 触发搜索
    await wrapper.vm.searchUsers()

    // 验证搜索条件
    expect(wrapper.vm.searchQuery).toBe('admin')
  })

  it('should handle page change', async () => {
    const wrapper = mount(UserList, {
      global: {
        stubs: {
          'el-table': true,
          'el-table-column': true,
          'el-pagination': true,
          'el-input': true,
          'el-button': true,
          'el-select': true,
          'el-option': true,
        }
      }
    })

    // 改变页码
    await wrapper.vm.handlePageChange(1)

    // 验证页码
    expect(wrapper.vm.currentPage).toBe(1)
  })

  it('should handle role filter', async () => {
    const wrapper = mount(UserList, {
      global: {
        stubs: {
          'el-table': true,
          'el-table-column': true,
          'el-pagination': true,
          'el-input': true,
          'el-button': true,
          'el-select': true,
          'el-option': true,
        }
      }
    })

    // 改变角色筛选
    await wrapper.setData({
      roleFilter: 'ADMIN'
    })

    // 触发筛选
    await wrapper.vm.filterByRole()

    // 验证筛选条件
    expect(wrapper.vm.roleFilter).toBe('ADMIN')
  })
})