const { request } = require('../utils/request')

// 获取用户列表
export function getUsers(params = {}) {
  return request({
    url: '/admin/users',
    method: 'GET',
    data: params
  })
}

// 获取用户详情
export function getUserDetail(id) {
  return request({
    url: `/admin/users/${id}`,
    method: 'GET'
  })
}

// 更新用户信息
export function updateUser(id, data) {
  return request({
    url: `/admin/users/${id}`,
    method: 'PUT',
    data
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/admin/users/${id}`,
    method: 'DELETE'
  })
}

// 启用/禁用用户
export function toggleUserStatus(id, status) {
  return request({
    url: `/admin/users/${id}/status`,
    method: 'PUT',
    data: { status }
  })
}

// 获取项目列表
export function getProjects(params = {}) {
  return request({
    url: '/admin/projects',
    method: 'GET',
    data: params
  })
}

// 创建项目
export function createProject(data) {
  return request({
    url: '/admin/projects',
    method: 'POST',
    data
  })
}

// 更新项目
export function updateProject(id, data) {
  return request({
    url: `/admin/projects/${id}`,
    method: 'PUT',
    data
  })
}

// 删除项目
export function deleteProject(id) {
  return request({
    url: `/admin/projects/${id}`,
    method: 'DELETE'
  })
}

// 发布/下架项目
export function toggleProjectStatus(id, status) {
  return request({
    url: `/admin/projects/${id}/status`,
    method: 'PUT',
    data: { status }
  })
}

// 获取申请列表
export function getApplications(params = {}) {
  return request({
    url: '/admin/applications',
    method: 'GET',
    data: params
  })
}

// 处理申请
export function processApplication(id, data) {
  return request({
    url: `/admin/applications/${id}/process`,
    method: 'PUT',
    data
  })
}

// 获取统计数据
export function getStats() {
  return request({
    url: '/admin/stats',
    method: 'GET'
  })
}

// 导出数据
export function exportData(params = {}) {
  return request({
    url: '/admin/export',
    method: 'GET',
    data: params
  })
}

// 提交反馈
export function submitFeedback(data) {
  return request({
    url: '/feedback',
    method: 'POST',
    data
  })
}