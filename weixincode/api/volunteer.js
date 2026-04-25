const { request } = require('../utils/request')

function getSchoolList(params) {
  return request({
    url: '/schools',
    method: 'GET',
    data: params || {}
  })
}

function getSchoolDetail(id) {
  return request({
    url: '/schools/' + id,
    method: 'GET'
  })
}

function getCities() {
  return request({
    url: '/schools/cities',
    method: 'GET'
  })
}

function getDistricts(city) {
  return request({
    url: '/schools/districts?city=' + encodeURIComponent(city),
    method: 'GET'
  })
}

function getVolunteerApplications(params) {
  return request({
    url: '/volunteer-applications',
    method: 'GET',
    data: params || {}
  })
}

function getVolunteerApplicationDetail(id) {
  return request({
    url: '/volunteer-applications/' + id,
    method: 'GET'
  })
}

function createVolunteerApplication(data) {
  return request({
    url: '/volunteer-applications',
    method: 'POST',
    data: data
  })
}

function submitVolunteerApplication(id) {
  return request({
    url: '/volunteer-applications/' + id + '/submit',
    method: 'POST'
  })
}

function deleteVolunteerApplication(id) {
  return request({
    url: '/volunteer-applications/' + id,
    method: 'DELETE'
  })
}

function addVolunteerItem(id, data) {
  return request({
    url: '/volunteer-applications/' + id + '/items',
    method: 'POST',
    data: data
  })
}

function removeVolunteerItem(id, schoolId) {
  return request({
    url: '/volunteer-applications/' + id + '/items/' + schoolId,
    method: 'DELETE'
  })
}

function reorderVolunteerItems(id, data) {
  return request({
    url: '/volunteer-applications/' + id + '/items/reorder',
    method: 'PUT',
    data: data
  })
}

function getSimulations(params) {
  return request({
    url: '/volunteer-applications/simulations',
    method: 'GET',
    data: params || {}
  })
}

function createSimulation(data) {
  return request({
    url: '/volunteer-applications/simulations',
    method: 'POST',
    data: data
  })
}

function getApplicationHistory() {
  return request({
    url: '/volunteer-applications/history',
    method: 'GET'
  })
}

function calculateAdmissionProbability(schoolId) {
  return request({
    url: '/admission-probability/calculate?schoolId=' + schoolId,
    method: 'GET'
  })
}

function generateSmartRecommendation(data) {
  return request({
    url: '/smart-recommendation/generate',
    method: 'POST',
    data: data
  })
}

function getRecommendationPreferences() {
  return request({
    url: '/recommendation-preferences',
    method: 'GET'
  })
}

function saveRecommendationPreferences(data) {
  return request({
    url: '/recommendation-preferences',
    method: 'POST',
    data: data
  })
}

function getAnnouncements() {
  return request({
    url: '/announcements',
    method: 'GET'
  })
}

function getHelpDocuments(category) {
  return request({
    url: category ? '/help-documents/category/' + category : '/help-documents',
    method: 'GET'
  })
}

module.exports = {
  getSchoolList,
  getSchoolDetail,
  getCities,
  getDistricts,
  getVolunteerApplications,
  getVolunteerApplicationDetail,
  createVolunteerApplication,
  submitVolunteerApplication,
  deleteVolunteerApplication,
  addVolunteerItem,
  removeVolunteerItem,
  reorderVolunteerItems,
  getSimulations,
  createSimulation,
  getApplicationHistory,
  calculateAdmissionProbability,
  generateSmartRecommendation,
  getRecommendationPreferences,
  saveRecommendationPreferences,
  getAnnouncements,
  getHelpDocuments
}
