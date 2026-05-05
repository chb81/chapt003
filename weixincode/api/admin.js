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

function getAllocationQuotas(params) {
  return request({
    url: '/admin/allocation/quotas',
    method: 'GET',
    data: params || {}
  })
}

function createAllocationQuota(data) {
  return request({
    url: '/admin/allocation/quotas',
    method: 'POST',
    data: data
  })
}

function updateAllocationQuota(id, data) {
  return request({
    url: '/admin/allocation/quotas/' + id,
    method: 'PUT',
    data: data
  })
}

function deleteAllocationQuota(id) {
  return request({
    url: '/admin/allocation/quotas/' + id,
    method: 'DELETE'
  })
}

function importAllocationQuotas(data) {
  return request({
    url: '/admin/allocation/quotas/import',
    method: 'POST',
    data: data
  })
}

function getAllocationPolicies(params) {
  return request({
    url: '/admin/allocation/policies',
    method: 'GET',
    data: params || {}
  })
}

function createAllocationPolicy(data) {
  return request({
    url: '/admin/allocation/policies',
    method: 'POST',
    data: data
  })
}

function updateAllocationPolicy(id, data) {
  return request({
    url: '/admin/allocation/policies/' + id,
    method: 'PUT',
    data: data
  })
}

function deleteAllocationPolicy(id) {
  return request({
    url: '/admin/allocation/policies/' + id,
    method: 'DELETE'
  })
}

function getScoreRanks(params) {
  return request({
    url: '/admin/allocation/score-ranks',
    method: 'GET',
    data: params || {}
  })
}

function createScoreRank(data) {
  return request({
    url: '/admin/allocation/score-ranks',
    method: 'POST',
    data: data
  })
}

function updateScoreRank(id, data) {
  return request({
    url: '/admin/allocation/score-ranks/' + id,
    method: 'PUT',
    data: data
  })
}

function deleteScoreRank(id) {
  return request({
    url: '/admin/allocation/score-ranks/' + id,
    method: 'DELETE'
  })
}

function importScoreRanks(data) {
  return request({
    url: '/admin/allocation/score-ranks/import',
    method: 'POST',
    data: data
  })
}

function getAnnouncements(params) {
  return request({
    url: '/admin/announcements',
    method: 'GET',
    data: params || {}
  })
}

function createAnnouncement(data) {
  return request({
    url: '/admin/announcements',
    method: 'POST',
    data: data
  })
}

function updateAnnouncement(id, data) {
  return request({
    url: '/admin/announcements/' + id,
    method: 'PUT',
    data: data
  })
}

function deleteAnnouncement(id) {
  return request({
    url: '/admin/announcements/' + id,
    method: 'DELETE'
  })
}

function getAnnouncementStats(id) {
  return request({
    url: '/admin/announcements/' + id + '/stats',
    method: 'GET'
  })
}

function getHelpDocuments(params) {
  return request({
    url: '/admin/help-documents',
    method: 'GET',
    data: params || {}
  })
}

function createHelpDocument(data) {
  return request({
    url: '/admin/help-documents',
    method: 'POST',
    data: data
  })
}

function updateHelpDocument(id, data) {
  return request({
    url: '/admin/help-documents/' + id,
    method: 'PUT',
    data: data
  })
}

function toggleHelpDocumentPublish(id, published) {
  return request({
    url: '/admin/help-documents/' + id + '/publish?published=' + published,
    method: 'PUT'
  })
}

function deleteHelpDocument(id) {
  return request({
    url: '/admin/help-documents/' + id,
    method: 'DELETE'
  })
}

function getCustomerServiceSessions(params) {
  return request({
    url: '/admin/customer-service/sessions',
    method: 'GET',
    data: params || {}
  })
}

function getCustomerServiceSessionDetail(sessionId) {
  return request({
    url: '/admin/customer-service/sessions/' + sessionId,
    method: 'GET'
  })
}

function replyCustomerServiceMessage(sessionId, adminId, content) {
  return request({
    url: '/admin/customer-service/sessions/' + sessionId + '/reply?adminId=' + adminId,
    method: 'POST',
    data: content
  })
}

function closeCustomerServiceSession(sessionId, resolutionNote) {
  return request({
    url: '/admin/customer-service/sessions/' + sessionId + '/close' + (resolutionNote ? '?resolutionNote=' + encodeURIComponent(resolutionNote) : ''),
    method: 'PUT'
  })
}

function assignCustomerServiceAgent(sessionId, agentId) {
  return request({
    url: '/admin/customer-service/sessions/' + sessionId + '/assign?agentId=' + agentId,
    method: 'PUT'
  })
}

function getCustomerServiceStats() {
  return request({
    url: '/admin/customer-service/stats',
    method: 'GET'
  })
}

function getNotifications(params) {
  return request({
    url: '/admin/notifications',
    method: 'GET',
    data: params || {}
  })
}

function sendNotification(data) {
  return request({
    url: '/admin/notifications/send',
    method: 'POST',
    data: data
  })
}

function sendNotificationToAll(data) {
  return request({
    url: '/admin/notifications/send-all',
    method: 'POST',
    data: data
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
  restoreSystemBackup,
  getAllocationQuotas,
  createAllocationQuota,
  updateAllocationQuota,
  deleteAllocationQuota,
  importAllocationQuotas,
  getAllocationPolicies,
  createAllocationPolicy,
  updateAllocationPolicy,
  deleteAllocationPolicy,
  getScoreRanks,
  createScoreRank,
  updateScoreRank,
  deleteScoreRank,
  importScoreRanks,
  getAnnouncements,
  createAnnouncement,
  updateAnnouncement,
  deleteAnnouncement,
  getAnnouncementStats,
  getHelpDocuments,
  createHelpDocument,
  updateHelpDocument,
  toggleHelpDocumentPublish,
  deleteHelpDocument,
  getCustomerServiceSessions,
  getCustomerServiceSessionDetail,
  replyCustomerServiceMessage,
  closeCustomerServiceSession,
  assignCustomerServiceAgent,
  getCustomerServiceStats,
  getNotifications,
  sendNotification,
  sendNotificationToAll
}
