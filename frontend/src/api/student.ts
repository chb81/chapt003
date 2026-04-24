import request from './request'

export const getStudentProfile = () => {
  return request.get('/student/profile')
}

export const createStudentProfile = (data: any) => {
  return request.post('/student/profile', data)
}

export const updateStudentProfile = (data: any) => {
  return request.put('/student/profile', data)
}

export const deleteStudentProfile = () => {
  return request.delete('/student/profile')
}

export const getStudentScore = () => {
  return request.get('/student/score')
}

export const createStudentScore = (data: any) => {
  return request.post('/student/score', data)
}

export const updateStudentScore = (data: any) => {
  return request.put('/student/score', data)
}

export const deleteStudentScore = () => {
  return request.delete('/student/score')
}
