import request from './request'

export const getAuditLogs = (params: any) => {
  return request.get('/admin/audit-logs', { params })
}

export const getHistoricalAdmissionData = (params: any) => {
  return request.get('/admin/historical-admission-data', { params })
}

export const getHistoricalDataBySchool = (schoolId: number) => {
  return request.get(`/admin/historical-admission-data/school/${schoolId}`)
}

export const createHistoricalData = (data: any) => {
  return request.post('/admin/historical-admission-data', data)
}

export const updateHistoricalData = (id: number, data: any) => {
  return request.put(`/admin/historical-admission-data/${id}`, data)
}

export const deleteHistoricalData = (id: number) => {
  return request.delete(`/admin/historical-admission-data/${id}`)
}

export const importHistoricalData = (data: any[]) => {
  return request.post('/admin/historical-admission-data/import', data)
}

export const exportHistoricalData = () => {
  return request.get('/admin/historical-admission-data/export')
}

export const getSystemConfigs = () => {
  return request.get('/admin/system-config')
}

export const getSystemConfigByKey = (key: string) => {
  return request.get(`/admin/system-config/${key}`)
}

export const saveSystemConfig = (data: any) => {
  return request.post('/admin/system-config', data)
}

export const deleteSystemConfig = (id: number) => {
  return request.delete(`/admin/system-config/${id}`)
}

export const getSystemPerformance = () => {
  return request.get('/admin/system-monitor/performance')
}

export const exportSystemData = (dataType: string) => {
  return request.get(`/admin/system-data/export/${dataType}`, { responseType: 'blob' })
}

export const createSystemBackup = () => {
  return request.post('/admin/system-data/backup')
}

export const getSystemBackups = () => {
  return request.get('/admin/system-data/backups')
}

export const deleteSystemBackup = (filename: string) => {
  return request.delete(`/admin/system-data/backups/${filename}`)
}

export const downloadSystemBackup = (filename: string) => {
  return request.get(`/admin/system-data/backups/${filename}/download`, { responseType: 'blob' })
}

export const restoreSystemBackup = (filename: string, dryRun = false) => {
  return request.post(`/admin/system-data/restore/${filename}`, null, { params: { dryRun } })
}

export const getAllocationQuotas = (params: any) => {
  return request.get('/admin/allocation/quotas', { params })
}

export const createAllocationQuota = (data: any) => {
  return request.post('/admin/allocation/quotas', data)
}

export const updateAllocationQuota = (id: number, data: any) => {
  return request.put(`/admin/allocation/quotas/${id}`, data)
}

export const deleteAllocationQuota = (id: number) => {
  return request.delete(`/admin/allocation/quotas/${id}`)
}

export const importAllocationQuotas = (data: any[]) => {
  return request.post('/admin/allocation/quotas/import', data)
}

export const getAllocationPolicies = (params: any) => {
  return request.get('/admin/allocation/policies', { params })
}

export const createAllocationPolicy = (data: any) => {
  return request.post('/admin/allocation/policies', data)
}

export const updateAllocationPolicy = (id: number, data: any) => {
  return request.put(`/admin/allocation/policies/${id}`, data)
}

export const deleteAllocationPolicy = (id: number) => {
  return request.delete(`/admin/allocation/policies/${id}`)
}

export const getScoreRanks = (params: any) => {
  return request.get('/admin/allocation/score-ranks', { params })
}

export const createScoreRank = (data: any) => {
  return request.post('/admin/allocation/score-ranks', data)
}

export const updateScoreRank = (id: number, data: any) => {
  return request.put(`/admin/allocation/score-ranks/${id}`, data)
}

export const deleteScoreRank = (id: number) => {
  return request.delete(`/admin/allocation/score-ranks/${id}`)
}

export const importScoreRanks = (data: any[]) => {
  return request.post('/admin/allocation/score-ranks/import', data)
}

export const getAdminAnnouncements = (params: any) => {
  return request.get('/admin/announcements', { params })
}

export const createAnnouncement = (data: any) => {
  return request.post('/admin/announcements', data)
}

export const updateAnnouncement = (id: number, data: any) => {
  return request.put(`/admin/announcements/${id}`, data)
}

export const deleteAnnouncement = (id: number) => {
  return request.delete(`/admin/announcements/${id}`)
}

export const getAnnouncementStats = (id: number) => {
  return request.get(`/admin/announcements/${id}/stats`)
}

export const getAdminHelpDocuments = (params: any) => {
  return request.get('/admin/help-documents', { params })
}

export const createHelpDocument = (data: any) => {
  return request.post('/admin/help-documents', data)
}

export const updateHelpDocument = (id: number, data: any) => {
  return request.put(`/admin/help-documents/${id}`, data)
}

export const toggleHelpDocumentPublish = (id: number, published: boolean) => {
  return request.put(`/admin/help-documents/${id}/publish`, null, { params: { published } })
}

export const deleteHelpDocument = (id: number) => {
  return request.delete(`/admin/help-documents/${id}`)
}

export const getCustomerServiceSessions = (params: any) => {
  return request.get('/admin/customer-service/sessions', { params })
}

export const getCustomerServiceSessionDetail = (sessionId: number) => {
  return request.get(`/admin/customer-service/sessions/${sessionId}`)
}

export const replyCustomerServiceMessage = (sessionId: number, adminId: number, content: string) => {
  return request.post(`/admin/customer-service/sessions/${sessionId}/reply`, content, { params: { adminId } })
}

export const closeCustomerServiceSession = (sessionId: number, resolutionNote?: string) => {
  return request.put(`/admin/customer-service/sessions/${sessionId}/close`, null, { params: { resolutionNote } })
}

export const assignCustomerServiceAgent = (sessionId: number, agentId: number) => {
  return request.put(`/admin/customer-service/sessions/${sessionId}/assign`, null, { params: { agentId } })
}

export const getCustomerServiceStats = () => {
  return request.get('/admin/customer-service/stats')
}

export const getAdminNotifications = (params: any) => {
  return request.get('/admin/notifications', { params })
}

export const sendNotification = (data: any) => {
  return request.post('/admin/notifications/send', data)
}

export const sendNotificationToAll = (data: any) => {
  return request.post('/admin/notifications/send-all', data)
}
