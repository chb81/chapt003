const { request } = require('../utils/request')

function getUsers(params) {
  return request({
    url: '/admin/users',
    method: 'GET',
    data: params || {}
  })
}

function getUserDetail(userId) {
  return request({
    url: '/admin/users/' + userId,
    method: 'GET'
  })
}

function updateUserRole(userId, role) {
  return request({
    url: '/admin/users/' + userId + '/role',
    method: 'PUT',
    data: { role: role }
  })
}

function updateUserStatus(userId, status) {
  return request({
    url: '/admin/users/' + userId + '/status',
    method: 'PUT',
    data: { status: status }
  })
}

function deleteUser(userId) {
  return request({
    url: '/admin/users/' + userId,
    method: 'DELETE'
  })
}

function getAuditLogs(params) {
  return request({
    url: '/admin/audit-logs',
    method: 'GET',
    data: params || {}
  })
}

function getHistoricalAdmissionData(params) {
  return request({
    url: '/admin/historical-admission-data',
    method: 'GET',
    data: params || {}
  })
}

function createHistoricalAdmissionData(data) {
  return request({
    url: '/admin/historical-admission-data',
    method: 'POST',
    data: data
  })
}

function updateHistoricalAdmissionData(id, data) {
  return request({
    url: '/admin/historical-admission-data/' + id,
    method: 'PUT',
    data: data
  })
}

function deleteHistoricalAdmissionData(id) {
  return request({
    url: '/admin/historical-admission-data/' + id,
    method: 'DELETE'
  })
}

function importHistoricalAdmissionData(data) {
  return request({
    url: '/admin/historical-admission-data/import',
    method: 'POST',
    data: data
  })
}

function getSystemConfigs() {
  return request({
    url: '/admin/system-config',
    method: 'GET'
  })
}

function saveSystemConfig(data) {
  return request({
    url: '/admin/system-config',
    method: 'POST',
    data: data
  })
}

function deleteSystemConfig(id) {
  return request({
    url: '/admin/system-config/' + id,
    method: 'DELETE'
  })
}

function getSystemPerformance() {
  return request({
    url: '/admin/system-monitor/performance',
    method: 'GET'
  })
}

function getBusinessMetrics() {
  return request({
    url: '/admin/metrics/business',
    method: 'GET'
  })
}

function exportSystemData(dataType) {
  return request({
    url: '/admin/system-data/export/' + dataType,
    method: 'GET'
  })
}

function createSystemBackup() {
  return request({
    url: '/admin/system-data/backup',
    method: 'POST'
  })
}

function getSystemBackups() {
  return request({
    url: '/admin/system-data/backups',
    method: 'GET'
  })
}

function deleteSystemBackup(filename) {
  return request({
    url: '/admin/system-data/backups/' + encodeURIComponent(filename),
    method: 'DELETE'
  })
}

function restoreSystemBackup(filename, dryRun) {
  return request({
    url: '/admin/system-data/restore/' + encodeURIComponent(filename) + '?dryRun=' + (dryRun || false),
    method: 'POST'
  })
}

module.exports = {
  getUsers,
  getUserDetail,
  updateUserRole,
  updateUserStatus,
  deleteUser,
  getAuditLogs,
  getHistoricalAdmissionData,
  createHistoricalAdmissionData,
  updateHistoricalAdmissionData,
  deleteHistoricalAdmissionData,
  importHistoricalAdmissionData,
  getSystemConfigs,
  saveSystemConfig,
  deleteSystemConfig,
  getSystemPerformance,
  getBusinessMetrics,
  exportSystemData,
  createSystemBackup,
  getSystemBackups,
  deleteSystemBackup,
  restoreSystemBackup
}
