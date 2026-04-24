import request from './request'

export const getUserList = (params: any) => {
  return request.get('/admin/users', { params })
}

export const getUserDetail = (userId: number) => {
  return request.get(`/admin/users/${userId}`)
}

export const updateUserRole = (userId: number, data: { role: string }) => {
  return request.put(`/admin/users/${userId}/role`, data)
}

export const updateUserStatus = (userId: number, data: { status: string }) => {
  return request.put(`/admin/users/${userId}/status`, data)
}

export const deleteUser = (userId: number) => {
  return request.delete(`/admin/users/${userId}`)
}
