# API 文档

## 基础信息

- **基础URL**: `http://localhost:8080`
- **API版本**: `v1`
- **响应格式**: JSON
- **认证方式**: JWT Bearer Token
- **请求头**: `Authorization: Bearer <token>`

## 通用响应格式

### 成功响应
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    // 具体数据
  }
}
```

### 错误响应
```json
{
  "success": false,
  "code": 10001,
  "message": "错误信息",
  "data": null
}
```

### 分页响应
```json
{
  "success": true,
  "message": "获取数据成功",
  "data": {
    "content": [
      // 数据列表
    ],
    "pageable": {
      "sort": {
        "empty": false,
        "sorted": true,
        "unsorted": false
      },
      "offset": 0,
      "pageNumber": 0,
      "pageSize": 20,
      "paged": true,
      "unpaged": false
    },
    "last": false,
    "totalPages": 5,
    "totalElements": 100,
    "size": 20,
    "number": 0,
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "first": true,
    "numberOfElements": 20,
    "empty": false
  }
}
```

## 认证相关 API

### 1. 用户注册

**POST** `/v1/auth/register`

**请求体**:
```json
{
  "username": "string",
  "email": "string",
  "password": "string",
  "mobile": "string",
  "nickname": "string"
}
```

**参数说明**:
- `username`: 用户名，3-20位字母数字
- `email`: 邮箱地址，必须有效
- `password`: 密码，8-20位，必须包含字母和数字
- `mobile`: 手机号码，可选
- `nickname`: 昵称，2-20位字符

**响应**:
```json
{
  "success": true,
  "message": "注册成功",
  "data": {
    "userId": 1,
    "username": "testuser",
    "email": "test@example.com",
    "status": "PENDING_VERIFICATION",
    "createdAt": "2024-01-01T00:00:00Z"
  }
}
```

### 2. 登录

**POST** `/v1/auth/login`

**请求体**:
```json
{
  "username": "string",
  "password": "string",
  "rememberMe": true
}
```

**参数说明**:
- `username`: 用户名或邮箱
- `password`: 密码
- `rememberMe`: 是否记住我，默认false

**响应**:
```json
{
  "success": true,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "testuser",
      "email": "test@example.com",
      "nickname": "测试用户",
      "role": "USER",
      "status": "ACTIVE"
    },
    "expiresAt": "2024-01-01T08:00:00Z"
  }
}
```

### 3. 微信登录

**POST** `/v1/auth/wechat-login`

**请求体**:
```json
{
  "code": "string"
}
```

**参数说明**:
- `code`: 微信登录授权码

**响应**:
```json
{
  "success": true,
  "message": "微信登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "nickname": "微信用户",
      "avatarUrl": "https://example.com/avatar.jpg",
      "unionId": "union_id_string",
      "openId": "open_id_string"
    },
    "isNewUser": true
  }
}
```

### 4. 邮箱验证

**POST** `/v1/auth/verify`

**请求体**:
```json
{
  "email": "string",
  "code": "string"
}
```

**参数说明**:
- `email`: 邮箱地址
- `code`: 验证码

**响应**:
```json
{
  "success": true,
  "message": "邮箱验证成功",
  "data": null
}
```

### 5. 重新发送验证码

**POST** `/v1/auth/resend-verification`

**参数**:
- `email`: 邮箱地址

**响应**:
```json
{
  "success": true,
  "message": "验证码已重新发送",
  "data": null
}
```

### 6. 重置密码 - 发送验证码

**POST** `/v1/auth/password-reset/send-code`

**请求体**:
```json
{
  "email": "string",
  "mobile": "string"
}
```

**参数说明**:
- `email`: 邮箱地址
- `mobile`: 手机号码

**响应**:
```json
{
  "success": true,
  "message": "验证码已发送",
  "data": null
}
```

### 7. 重置密码 - 验证并重置

**POST** `/v1/auth/password-reset/verify`

**请求体**:
```json
{
  "email": "string",
  "mobile": "string",
  "verificationCode": "string",
  "newPassword": "string",
  "confirmPassword": "string"
}
```

**参数说明**:
- `email`: 邮箱地址
- `mobile`: 手机号码
- `verificationCode`: 验证码
- `newPassword`: 新密码
- `confirmPassword`: 确认密码

**响应**:
```json
{
  "success": true,
  "message": "密码重置成功",
  "data": null
}
```

## 用户管理 API

### 1. 获取用户信息

**GET** `/v1/user/profile`

**认证**: 需要登录

**响应**:
```json
{
  "success": true,
  "message": "获取用户信息成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "mobile": "13800138000",
    "nickname": "测试用户",
    "avatarUrl": "https://example.com/avatar.jpg",
    "role": "USER",
    "status": "ACTIVE",
    "lastLoginAt": "2024-01-01T00:00:00Z",
    "createdAt": "2024-01-01T00:00:00Z"
  }
}
```

### 2. 更新用户信息

**PUT** `/v1/user/profile`

**认证**: 需要登录

**请求体**:
```json
{
  "nickname": "string",
  "avatarUrl": "string",
  "email": "string",
  "mobile": "string"
}
```

**参数说明**:
- `nickname`: 昵称，2-20位字符
- `avatarUrl`: 头像URL
- `email`: 邮箱地址，可选
- `mobile`: 手机号码，可选

**响应**:
```json
{
  "success": true,
  "message": "资料更新成功",
  "data": null
}
```

### 3. 验证邮箱变更

**GET** `/v1/user/verify-email?token=string`

**参数**:
- `token`: 邮箱验证令牌

**响应**:
```json
{
  "success": true,
  "message": "邮箱验证成功",
  "data": null
}
```

### 4. 验证手机号变更

**POST** `/v1/user/verify-mobile`

**认证**: 需要登录

**请求体**:
```json
{
  "mobile": "string",
  "code": "string"
}
```

**参数说明**:
- `mobile`: 手机号码
- `code`: 验证码

**响应**:
```json
{
  "success": true,
  "message": "手机号验证成功",
  "data": null
}
```

## 管理员 API

### 1. 获取用户列表

**GET** `/api/admin/users`

**认证**: 管理员

**参数**:
- `keyword`: 搜索关键词，可选
- `role`: 用户角色，可选 (USER, ADMIN, SUPER_ADMIN)
- `status`: 用户状态，可选 (ACTIVE, PENDING_VERIFICATION, INACTIVE)
- `page`: 页码，默认0
- `size`: 页面大小，默认20
- `sortBy`: 排序字段，默认createdAt
- `sortDirection`: 排序方向 (asc, desc)，默认desc

**响应**:
```json
{
  "success": true,
  "message": "获取用户列表成功",
  "data": {
    "content": [
      {
        "id": 1,
        "username": "testuser",
        "email": "test@example.com",
        "nickname": "测试用户",
        "role": "USER",
        "status": "ACTIVE",
        "createdAt": "2024-01-01T00:00:00Z"
      }
    ],
    "pageable": {
      "sort": {
        "empty": false,
        "sorted": true,
        "unsorted": false
      },
      "offset": 0,
      "pageNumber": 0,
      "pageSize": 20,
      "paged": true,
      "unpaged": false
    },
    "last": false,
    "totalPages": 5,
    "totalElements": 100,
    "size": 20,
    "number": 0,
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "first": true,
    "numberOfElements": 20,
    "empty": false
  }
}
```

### 2. 获取用户详情

**GET** `/api/admin/users/{userId}`

**认证**: 管理员

**参数**:
- `userId`: 用户ID

**响应**:
```json
{
  "success": true,
  "message": "获取用户详情成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "mobile": "13800138000",
    "nickname": "测试用户",
    "avatarUrl": "https://example.com/avatar.jpg",
    "role": "USER",
    "status": "ACTIVE",
    "lastLoginAt": "2024-01-01T00:00:00Z",
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:00:00Z"
  }
}
```

### 3. 获取用户登录历史

**GET** `/api/admin/users/{userId}/login-history`

**认证**: 管理员

**参数**:
- `userId`: 用户ID
- `limit`: 返回记录数，默认10

**响应**:
```json
{
  "success": true,
  "message": "获取登录历史成功",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "ipAddress": "192.168.1.1",
      "userAgent": "Mozilla/5.0",
      "loginTime": "2024-01-01T00:00:00Z",
      "loginMethod": "PASSWORD",
      "success": true
    }
  ]
}
```

### 4. 获取用户审计日志

**GET** `/api/admin/users/{userId}/audit-logs`

**认证**: 管理员

**参数**:
- `userId`: 用户ID
- `limit`: 返回记录数，默认20

**响应**:
```json
{
  "success": true,
  "message": "获取审计日志成功",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "action": "UPDATE_PROFILE",
      "details": {
        "field": "email",
        "oldValue": "old@example.com",
        "newValue": "new@example.com"
      },
      "ipAddress": "192.168.1.1",
      "userAgent": "Mozilla/5.0",
      "timestamp": "2024-01-01T00:00:00Z"
    }
  ]
}
```

### 5. 更新用户角色

**PUT** `/api/admin/users/{userId}/role`

**认证**: 管理员

**参数**:
- `userId`: 用户ID

**请求体**:
```json
{
  "role": "USER|ADMIN|SUPER_ADMIN"
}
```

**响应**:
```json
{
  "success": true,
  "message": "更新用户角色成功",
  "data": null
}
```

### 6. 更新用户状态

**PUT** `/api/admin/users/{userId}/status`

**认证**: 管理员

**参数**:
- `userId`: 用户ID

**请求体**:
```json
{
  "status": "ACTIVE|PENDING_VERIFICATION|INACTIVE"
}
```

**响应**:
```json
{
  "success": true,
  "message": "更新用户状态成功",
  "data": null
}
```

### 7. 删除用户

**DELETE** `/api/admin/users/{userId}`

**认证**: 管理员

**参数**:
- `userId`: 用户ID

**响应**:
```json
{
  "success": true,
  "message": "删除用户成功",
  "data": null
}
```

## 错误代码

| 代码 | 说明 |
|------|------|
| 10001 | 参数验证失败 |
| 20001 | 验证码无效或已过期 |
| 20002 | 用户名已存在 |
| 20003 | 邮箱已存在 |
| 20004 | 手机号已存在 |
| 30001 | 用户不存在 |
| 30002 | 密码错误 |
| 30003 | 用户未激活 |
| 30004 | 账号已被锁定 |
| 40001 | 未授权访问 |
| 40002 | Token已过期 |
| 50001 | 服务器内部错误 |

## 速率限制

- 登录接口: 5次/分钟
- 注册接口: 3次/分钟
- 验证码发送: 1次/分钟
- API调用: 100次/分钟

## 安全说明

1. **密码安全**: 密码必须包含字母和数字，长度8-20位
2. **JWT安全**: Token有效期2小时，刷新Token有效期7天
3. **HTTPS**: 生产环境必须使用HTTPS
4. **输入验证**: 所有输入参数都进行严格验证
5. **SQL注入防护**: 使用参数化查询
6. **XSS防护**: 输出内容进行HTML转义
7. **CSRF防护**: 启用CSRF保护
8. **速率限制**: 关键接口实施速率限制
9. **日志记录**: 所有操作记录审计日志
10. **数据加密**: 敏感数据使用加密存储