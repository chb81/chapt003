import request from './request'

export const createSession = (data: any) => {
  return request.post('/customer-service/sessions', data)
}

export const getUserSessions = () => {
  return request.get('/customer-service/sessions')
}

export const getActiveSessions = () => {
  return request.get('/customer-service/sessions/active')
}

export const getSessionDetail = (id: number) => {
  return request.get(`/customer-service/sessions/${id}`)
}

export const updateSession = (id: number, data: any) => {
  return request.put(`/customer-service/sessions/${id}`, data)
}

export const sendMessage = (sessionId: number, data: any) => {
  return request.post(`/customer-service/sessions/${sessionId}/messages`, data)
}

export const getSessionMessages = (sessionId: number) => {
  return request.get(`/customer-service/sessions/${sessionId}/messages`)
}

export const getNewMessages = (sessionId: number, lastMessageId?: number) => {
  return request.get(`/customer-service/sessions/${sessionId}/messages/new`, {
    params: { lastMessageId }
  })
}

export const markMessageRead = (messageId: number) => {
  return request.put(`/customer-service/messages/${messageId}/read`)
}

export const getUnreadCount = () => {
  return request.get('/customer-service/messages/unread-count')
}