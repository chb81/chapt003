import request from './request'

export const getAnnouncements = (params?: any) => {
  return request.get('/announcements', { params })
}

export const getAnnouncementDetail = (id: number) => {
  return request.get(`/announcements/${id}`)
}

export const getUnreadAnnouncements = () => {
  return request.get('/announcements/unread')
}

export const markAnnouncementRead = (id: number) => {
  return request.post(`/announcements/${id}/mark-read`)
}

export const getAnnouncementsByType = (type: string) => {
  return request.get(`/announcements/type/${type}`)
}