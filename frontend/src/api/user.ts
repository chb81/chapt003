import request from './request'
import type {
  User,
  UserDetail,
  PageResponse,
  UserListParams,
  UpdateUserRoleRequest,
  UpdateUserStatusRequest
} from '@/types'

export const getUserList = (params: UserListParams) => {
  return request.get<any, PageResponse<User>>('/admin/users', { params })
}

export const getUserDetail = (userId: number) => {
  return request.get<any, UserDetail>(`/admin/users/${userId}`)
}

export const updateUserRole = (userId: number, data: UpdateUserRoleRequest) => {
  return request.put(`/admin/users/${userId}/role`, data)
}

export const updateUserStatus = (userId: number, data: UpdateUserStatusRequest) => {
  return request.put(`/admin/users/${userId}/status`, data)
}

export const deleteUser = (userId: number) => {
  return request.delete(`/admin/users/${userId}`)
}

export const getLoginHistory = (userId: number, limit = 10) => {
  return request.get<any, any[]>(`/admin/users/${userId}/login-history`, {
    params: { limit }
  })
}

export const getAuditLogs = (userId: number, limit = 20) => {
  return request.get<any, any[]>(`/admin/users/${userId}/audit-logs`, {
    params: { limit }
  })
}
