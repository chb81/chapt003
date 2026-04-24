import request from './request'

export interface SchoolListParams {
  keyword?: string
  city?: string
  district?: string
  type?: string
  minScore?: number
  maxScore?: number
  sortBy?: string
  sortDirection?: string
  page?: number
  size?: number
}

export const getSchoolList = (params: SchoolListParams) => {
  return request.get('/schools', { params })
}

export const getSchoolDetail = (id: number) => {
  return request.get(`/schools/${id}`)
}

export const getCities = () => {
  return request.get('/schools/cities')
}

export const getDistricts = (city: string) => {
  return request.get('/schools/districts', { params: { city } })
}

export const createSchool = (data: any) => {
  return request.post('/schools', data)
}

export const updateSchool = (id: number, data: any) => {
  return request.put(`/schools/${id}`, data)
}

export const deleteSchool = (id: number) => {
  return request.delete(`/schools/${id}`)
}

export const importSchools = (data: any[]) => {
  return request.post('/schools/import', data)
}

export const exportSchools = () => {
  return request.get('/schools/export')
}