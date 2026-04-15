export enum UserRole {
  USER = 'USER',
  ADMIN = 'ADMIN'
}

export enum UserStatus {
  ACTIVE = 'ACTIVE',
  DISABLED = 'DISABLED',
  UNVERIFIED = 'UNVERIFIED',
  DELETED = 'DELETED'
}

export enum LoginMethod {
  PASSWORD = 'PASSWORD',
  WECHAT = 'WECHAT'
}

export interface User {
  id: number
  username: string
  email?: string
  mobile?: string
  wechatOpenId?: string
  role: UserRole
  status: UserStatus
  emailVerified: boolean
  mobileVerified: boolean
  createdAt: string
  updatedAt?: string
  lastLoginAt?: string
  deletedAt?: string
}

export interface LoginHistory {
  id: number
  userId: number
  loginTime: string
  ipAddress: string
  userAgent: string
  loginMethod: LoginMethod
  loginSuccess: boolean
  failureReason?: string
  logoutTime?: string
}

export interface AuditLog {
  id: number
  adminId: number
  adminEmail: string
  targetUserId: number
  targetUserEmail: string
  action: string
  entityType?: string
  entityId?: number
  oldValue?: string
  newValue?: string
  ipAddress?: string
  userAgent?: string
  timestamp: string
}

export interface UserDetail extends User {
  loginHistory?: LoginHistory[]
  auditLogs?: AuditLog[]
}

export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

export interface PageResponse<T> {
  content: T[]
  totalPages: number
  totalElements: number
  number: number
  size: number
  first: boolean
  last: boolean
  empty: boolean
}

export interface UserListParams {
  page: number
  size: number
  keyword?: string
  role?: UserRole
  status?: UserStatus
  sortBy?: string
  sortDirection?: string
}

export interface UpdateUserRoleRequest {
  role: UserRole
}

export interface UpdateUserStatusRequest {
  status: UserStatus
}
