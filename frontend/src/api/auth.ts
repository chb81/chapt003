import request from './request'

export interface LoginRequest {
  email: string
  password: string
}

export interface LoginResponse {
  token: string
  username: string
  role: string
}

export interface RegisterRequest {
  username: string
  password: string
  email: string
  mobile?: string
}

export const login = (data: LoginRequest) => {
  return request.post<any, any>('/auth/login', data)
}

export const register = (data: RegisterRequest) => {
  return request.post<any, any>('/auth/register', data)
}

export const verifyEmail = (email: string, code: string) => {
  return request.post('/auth/verify', { email, code })
}

export const resendVerification = (email: string) => {
  return request.post(`/auth/resend-verification?email=${email}`)
}