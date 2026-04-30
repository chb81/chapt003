import request from './request'

export function getRiskAssessment(planId: number) {
  return request({
    url: `/plan-analysis/${planId}/risk`,
    method: 'get'
  })
}

export function comparePlans(planIds: number[]) {
  return request({
    url: '/plan-analysis/compare',
    method: 'post',
    data: planIds
  })
}

export function convertScoreToRank(totalScore: number, city: string, year?: number) {
  return request({
    url: '/plan-analysis/score-rank',
    method: 'get',
    params: { totalScore, city, year }
  })
}

export function getMyRank() {
  return request({
    url: '/plan-analysis/my-rank',
    method: 'get'
  })
}

export function getAvailableYears(city: string) {
  return request({
    url: '/plan-analysis/score-rank/years',
    method: 'get',
    params: { city }
  })
}

export function getAllocationQuotas(schoolId: number, year?: number) {
  return request({
    url: `/allocation/schools/${schoolId}/quotas`,
    method: 'get',
    params: { year }
  })
}

export function getAllocationPolicies(city: string, district: string, year?: number) {
  return request({
    url: '/allocation/policies',
    method: 'get',
    params: { city, district, year }
  })
}

export function getMyAllocationOptions() {
  return request({
    url: '/allocation/my-options',
    method: 'get'
  })
}

export function checkAllocationAdvantage(schoolId: number) {
  return request({
    url: '/allocation/advantage',
    method: 'get',
    params: { schoolId }
  })
}
