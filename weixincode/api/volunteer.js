const { request } = require('../utils/request')

// 获取志愿项目列表
export function getVolunteerProjects(params = {}) {
  return request({
    url: '/volunteer/projects',
    method: 'GET',
    data: params
  })
}

// 获取志愿项目详情
export function getVolunteerProjectDetail(id) {
  return request({
    url: `/volunteer/projects/${id}`,
    method: 'GET'
  })
}

// 申请志愿项目
export function applyForProject(data) {
  return request({
    url: '/volunteer/applications',
    method: 'POST',
    data
  })
}

// 获取用户的申请记录
export function getUserApplications(params = {}) {
  return request({
    url: '/volunteer/applications',
    method: 'GET',
    data: params
  })
}

// 更新申请状态
export function updateApplicationStatus(data) {
  return request({
    url: `/volunteer/applications/${data.id}/status`,
    method: 'PUT',
    data
  })
}

// 获取志愿活动记录
export function getActivityRecords(params = {}) {
  return request({
    url: '/volunteer/activities',
    method: 'GET',
    data: params
  })
}

// 记录志愿活动
export function recordActivity(data) {
  return request({
    url: '/volunteer/activities',
    method: 'POST',
    data
  })
}

// 上传志愿活动证明
export function uploadActivityProof(data) {
  return request({
    url: '/volunteer/proofs',
    method: 'POST',
    data
  })
}

// 获取志愿统计信息
export function getVolunteerStats() {
  return request({
    url: '/volunteer/stats',
    method: 'GET'
  })
}

// 获取志愿证书
export function getCertificates() {
  return request({
    url: '/volunteer/certificates',
    method: 'GET'
  })
}

// 申请志愿证书
export function applyCertificate(data) {
  return request({
    url: '/volunteer/certificates',
    method: 'POST',
    data
  })
}