const { request } = require('../utils/request')

function getUsers(params) {
  return request({
    url: '/admin/users',
    method: 'GET',
    data: params || {}
  })
}

function getUserDetail(id) {
  return request({
    url: '/admin/users/' + id,
    method: 'GET'
  })
}

function updateUser(id, data) {
  return request({
    url: '/admin/users/' + id,
    method: 'PUT',
    data: data
  })
}

function deleteUser(id) {
  return request({
    url: '/admin/users/' + id,
    method: 'DELETE'
  })
}

function toggleUserStatus(id, status) {
  return request({
    url: '/admin/users/' + id + '/status',
    method: 'PUT',
    data: { status: status }
  })
}

function getProjects(params) {
  return request({
    url: '/admin/projects',
    method: 'GET',
    data: params || {}
  })
}

function createProject(data) {
  return request({
    url: '/admin/projects',
    method: 'POST',
    data: data
  })
}

function updateProject(id, data) {
  return request({
    url: '/admin/projects/' + id,
    method: 'PUT',
    data: data
  })
}

function deleteProject(id) {
  return request({
    url: '/admin/projects/' + id,
    method: 'DELETE'
  })
}

function toggleProjectStatus(id, status) {
  return request({
    url: '/admin/projects/' + id + '/status',
    method: 'PUT',
    data: { status: status }
  })
}

function getApplications(params) {
  return request({
    url: '/admin/applications',
    method: 'GET',
    data: params || {}
  })
}

function processApplication(id, data) {
  return request({
    url: '/admin/applications/' + id + '/process',
    method: 'PUT',
    data: data
  })
}

function getStats() {
  return request({
    url: '/admin/stats',
    method: 'GET'
  })
}

function exportData(params) {
  return request({
    url: '/admin/export',
    method: 'GET',
    data: params || {}
  })
}

function submitFeedback(data) {
  return request({
    url: '/feedback',
    method: 'POST',
    data: data
  })
}

module.exports = {
  getUsers,
  getUserDetail,
  updateUser,
  deleteUser,
  toggleUserStatus,
  getProjects,
  createProject,
  updateProject,
  deleteProject,
  toggleProjectStatus,
  getApplications,
  processApplication,
  getStats,
  exportData,
  submitFeedback
}
