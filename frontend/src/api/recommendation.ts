import request from './request'

export const calculateProbability = (schoolId: number) => {
  return request.get('/admission-probability/calculate', {
    params: { schoolId }
  })
}

export const generateRecommendations = (data?: any) => {
  return request.post('/smart-recommendation/generate', data || {})
}

export const getRecommendationPreferences = () => {
  return request.get('/recommendation-preferences')
}

export const saveRecommendationPreferences = (data: any) => {
  return request.post('/recommendation-preferences', data)
}
