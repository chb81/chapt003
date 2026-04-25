const { request } = require('../utils/request')

function getVolunteerProjects(params) {
  return request({
    url: '/volunteer/projects',
    method: 'GET',
    data: params || {}
  })
}

function getVolunteerProjectDetail(id) {
  return request({
    url: '/volunteer/projects/' + id,
    method: 'GET'
  })
}

function applyForProject(data) {
  return request({
    url: '/volunteer/applications',
    method: 'POST',
    data: data
  })
}

function getUserApplications(params) {
  return request({
    url: '/volunteer/applications',
    method: 'GET',
    data: params || {}
  })
}

function updateApplicationStatus(data) {
  return request({
    url: '/volunteer/applications/' + data.id + '/status',
    method: 'PUT',
    data: data
  })
}

function getActivityRecords(params) {
  return request({
    url: '/volunteer/activities',
    method: 'GET',
    data: params || {}
  })
}

function recordActivity(data) {
  return request({
    url: '/volunteer/activities',
    method: 'POST',
    data: data
  })
}

function uploadActivityProof(data) {
  return request({
    url: '/volunteer/proofs',
    method: 'POST',
    data: data
  })
}

function getVolunteerStats() {
  return request({
    url: '/volunteer/stats',
    method: 'GET'
  })
}

function getCertificates() {
  return request({
    url: '/volunteer/certificates',
    method: 'GET'
  })
}

function applyCertificate(data) {
  return request({
    url: '/volunteer/certificates',
    method: 'POST',
    data: data
  })
}

module.exports = {
  getVolunteerProjects,
  getVolunteerProjectDetail,
  applyForProject,
  getUserApplications,
  updateApplicationStatus,
  getActivityRecords,
  recordActivity,
  uploadActivityProof,
  getVolunteerStats,
  getCertificates,
  applyCertificate
}
