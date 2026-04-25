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
