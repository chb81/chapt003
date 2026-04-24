import request from './request'

export const getHelpDocuments = (params?: any) => {
  return request.get('/help-documents', { params })
}

export const getHelpDocumentDetail = (id: number) => {
  return request.get(`/help-documents/${id}`)
}

export const getHelpDocumentsByCategory = (category: string) => {
  return request.get(`/help-documents/category/${category}`)
}

export const searchHelpDocuments = (keyword: string) => {
  return request.get('/help-documents/search', { params: { keyword } })
}

export const toggleDocumentFavorite = (documentId: number) => {
  return request.post(`/help-documents/${documentId}/favorite`)
}

export const submitDocumentFeedback = (documentId: number, isHelpful: boolean) => {
  return request.post(`/help-documents/${documentId}/feedback`, null, {
    params: { isHelpful }
  })
}

export const getFavoriteDocuments = () => {
  return request.get('/help-documents/favorites')
}
