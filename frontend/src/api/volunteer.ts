import request from './request'

export const getApplications = () => {
  return request.get('/volunteer-applications')
}

export const getApplicationDetail = (id: number) => {
  return request.get(`/volunteer-applications/${id}`)
}

export const createApplication = (data: any) => {
  return request.post('/volunteer-applications', data)
}

export const submitApplication = (id: number) => {
  return request.post(`/volunteer-applications/${id}/submit`)
}

export const deleteApplication = (id: number) => {
  return request.delete(`/volunteer-applications/${id}`)
}

export const addVolunteerItem = (applicationId: number, data: any) => {
  return request.post(`/volunteer-applications/${applicationId}/items`, data)
}

export const removeVolunteerItem = (applicationId: number, schoolId: number) => {
  return request.delete(`/volunteer-applications/${applicationId}/items/${schoolId}`)
}

export const reorderItems = (applicationId: number, data: any) => {
  return request.put(`/volunteer-applications/${applicationId}/items/reorder`, data)
}

export const createSimulation = (data: any) => {
  return request.post('/volunteer-applications/simulations', data)
}

export const getSimulations = (params?: any) => {
  return request.get('/volunteer-applications/simulations', { params })
}

export const getHistory = () => {
  return request.get('/volunteer-applications/history')
}