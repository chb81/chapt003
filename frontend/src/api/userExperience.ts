import request from './request'

export function getOnboardingStatus() {
  return request({ url: '/user-experience/onboarding/status', method: 'get' })
}

export function completeOnboardingStep(step: number) {
  return request({ url: '/user-experience/onboarding/complete-step', method: 'post', data: { step } })
}

export function getNotifications(page = 0, size = 20) {
  return request({ url: '/user-experience/notifications', method: 'get', params: { page, size } })
}

export function getUnreadNotifications() {
  return request({ url: '/user-experience/notifications/unread', method: 'get' })
}

export function getUnreadCount() {
  return request({ url: '/user-experience/notifications/unread-count', method: 'get' })
}

export function markAsRead(id: number) {
  return request({ url: `/user-experience/notifications/${id}/read`, method: 'post' })
}

export function markAllAsRead() {
  return request({ url: '/user-experience/notifications/read-all', method: 'post' })
}

export function createShare(data: { shareType: string; targetId?: string; title?: string; description?: string }) {
  return request({ url: '/user-experience/share', method: 'post', data })
}

export function getShare(shareCode: string) {
  return request({ url: `/user-experience/share/${shareCode}`, method: 'get' })
}
