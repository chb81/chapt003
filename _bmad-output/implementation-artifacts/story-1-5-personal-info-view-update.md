# Story 1.5: 个人信息查看和更新

## Story Metadata

- **Story ID**: 1.5
- **Epic**: Epic 1 - 用户认证与账户管理
- **Title**: 个人信息查看和更新
- **Status**: ready-for-dev
- **Priority**: High
- **Story Points**: 8
- **Estimated Days**: 3-4
- **Assigned To**: TBD

## User Story

作为一个已登录用户，我希望能够查看和更新我的个人资料，以便保持我的账户信息准确和最新。

## Acceptance Criteria

### AC 1: 查看个人资料
**Given** 用户已登录
**When** 用户导航到"我的"页面
**Then** 系统应显示用户的个人资料（用户名、邮箱/手机号、创建日期）

### AC 2: 打开编辑表单
**Given** 用户在"我的"页面
**When** 用户点击"编辑资料"按钮
**Then** 系统应显示个人资料编辑表单

### AC 3: 更新用户名
**Given** 用户在个人资料编辑表单
**When** 用户修改用户名
**And** 用户点击"保存"按钮
**Then** 系统应验证用户名格式（长度2-20个字符）
**And** 系统应更新用户名
**And** 系统应显示"资料更新成功"的成功提示

### AC 4: 更新邮箱
**Given** 用户在个人资料编辑表单
**When** 用户修改邮箱
**And** 用户点击"保存"按钮
**Then** 系统应验证邮箱格式
**And** 系统应验证邮箱是否已被其他用户使用
**And** 系统应发送验证邮件到新邮箱
**And** 系统应显示"请验证新邮箱地址"的提示

### AC 5: 更新手机号
**Given** 用户在个人资料编辑表单
**When** 用户修改手机号
**And** 用户点击"保存"按钮
**Then** 系统应验证手机号格式
**And** 系统应验证手机号是否已被其他用户使用
**And** 系统应发送验证短信到新手机号
**And** 系统应显示"请验证新手机号"的提示

### AC 6: 验证新邮箱
**Given** 用户已修改邮箱并收到验证邮件
**When** 用户点击邮件中的验证链接
**Then** 系统应验证链接有效性
**And** 系统应更新用户的邮箱为新的邮箱地址
**And** 系统应显示"邮箱验证成功"的成功提示

### AC 7: 验证新手机号
**Given** 用户已修改手机号并收到验证短信
**When** 用户输入验证码并提交
**Then** 系统应验证验证码有效性
**And** 系统应更新用户的手机号为新的手机号
**And** 系统应显示"手机号验证成功"的成功提示

### AC 8: 错误处理
**Given** 用户在个人资料编辑表单
**When** 用户输入无效的用户名格式
**Then** 系统应显示"用户名长度必须在2-20个字符之间"的错误提示

**Given** 用户在个人资料编辑表单
**When** 用户输入无效的邮箱格式
**Then** 系统应显示"邮箱格式不正确"的错误提示

**Given** 用户在个人资料编辑表单
**When** 用户输入已被其他用户使用的邮箱
**Then** 系统应显示"该邮箱已被使用"的错误提示

**Given** 用户在个人资料编辑表单
**When** 用户输入无效的手机号格式
**Then** 系统应显示"手机号格式不正确"的错误提示

**Given** 用户在个人资料编辑表单
**When** 用户输入已被其他用户使用的手机号
**Then** 系统应显示"该手机号已被使用"的错误提示

## Technical Requirements

### Backend Requirements

#### API Endpoints

**1. 获取用户信息**
- **Endpoint**: `GET /api/user/profile`
- **Description**: 获取当前登录用户的个人信息
- **Authentication**: Required (JWT Bearer Token)
- **Request Headers**:
  - `Authorization`: Bearer {jwt_token}
- **Response**: `UserProfileResponse`

**2. 更新用户信息**
- **Endpoint**: `PUT /api/user/profile`
- **Description**: 更新用户个人信息
- **Authentication**: Required (JWT Bearer Token)
- **Request Headers**:
  - `Authorization`: Bearer {jwt_token}
- **Request Body**: `UpdateUserProfileRequest`
- **Response**: `ApiResponse`

**3. 验证新邮箱**
- **Endpoint**: `GET /api/user/verify-email`
- **Description**: 验证新邮箱地址
- **Authentication**: Not required (验证链接包含token)
- **Query Parameters**:
  - `token`: 验证token
- **Response**: `ApiResponse`

**4. 验证新手机号**
- **Endpoint**: `POST /api/user/verify-mobile`
- **Description**: 验证新手机号
- **Authentication**: Required (JWT Bearer Token)
- **Request Headers**:
  - `Authorization`: Bearer {jwt_token}
- **Request Body**: `VerifyMobileRequest`
- **Response**: `ApiResponse`

#### DTOs

**UserProfileResponse**
```java
{
  "username": "string",
  "email": "string | null",
  "mobile": "string | null",
  "createdAt": "string (ISO 8601)",
  "emailVerified": "boolean",
  "mobileVerified": "boolean"
}
```

**UpdateUserProfileRequest**
```java
{
  "username": "string (2-20 characters)",
  "email": "string | null",
  "mobile": "string | null"
}
```

**VerifyMobileRequest**
```java
{
  "code": "string (6 digits)",
  "newMobile": "string"
}
```

#### Service Layer

**UserProfileService**
- `getUserProfile(Long userId)`: 获取用户个人信息
- `updateUserProfile(Long userId, UpdateUserProfileRequest request)`: 更新用户个人信息
- `updateUsername(Long userId, String username)`: 更新用户名
- `initiateEmailChange(Long userId, String newEmail)`: 发起邮箱更改流程
- `verifyEmailChange(String token)`: 验证邮箱更改
- `initiateMobileChange(Long userId, String newMobile)`: 发起手机号更改流程
- `verifyMobileChange(Long userId, String code, String newMobile)`: 验证手机号更改

#### Controller Layer

**UserController**
- `getUserProfile()`: 获取当前用户信息
- `updateProfile(@RequestBody UpdateUserProfileRequest request)`: 更新用户信息
- `verifyEmail(@RequestParam String token)`: 验证邮箱
- `verifyMobile(@RequestBody VerifyMobileRequest request)`: 验证手机号

### Database Requirements

#### User Entity Updates

需要为 User 实体添加以下字段（如果不存在）：
- `email_verified` (Boolean): 邮箱是否已验证
- `mobile_verified` (Boolean): 手机号是否已验证
- `pending_email` (String): 待验证的新邮箱
- `pending_mobile` (String): 待验证的新手机号
- `email_verify_token` (String): 邮箱验证token
- `email_verify_token_expires` (LocalDateTime): 邮箱验证token过期时间

#### EmailVerificationToken Entity

如果需要独立的验证token管理，创建 EmailVerificationToken 实体：
```java
@Entity
public class EmailVerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String token;
    
    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String newEmail;
    
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    
    private LocalDateTime verifiedAt;
    
    private Boolean used = false;
}
```

### Security Requirements

- 所有 API 端点（除了邮箱验证）需要 JWT 认证
- 邮箱验证token必须有时效性（建议24小时）
- 邮箱验证token只能使用一次
- 用户只能查看和更新自己的信息
- 记录所有个人信息变更操作到审计日志
- 敏感信息（密码）不能在响应中返回

### Frontend Requirements

#### Pages

**1. 个人中心页面**
- 路径: `pages/profile/profile`
- 功能:
  - 显示用户头像（默认头像）
  - 显示用户名
  - 显示邮箱/手机号（显示已验证状态）
  - 显示账户创建日期
  - "编辑资料"按钮

**2. 编辑资料页面**
- 路径: `pages/profile-edit/profile-edit`
- 功能:
  - 用户名输入框（必填，2-20字符）
  - 邮箱输入框（选填，格式验证）
  - 手机号输入框（选填，格式验证）
  - 保存按钮
  - 取消按钮
  - 表单验证提示

**3. 验证邮箱页面**
- 路径: `pages/verify-email/verify-email`
- 功能:
  - 显示验证成功/失败消息
  - "返回个人中心"按钮

**4. 验证手机号页面**
- 路径: `pages/verify-mobile/verify-mobile`
- 功能:
  - 显示待验证的手机号
  - 6位验证码输入框
  - 重新发送验证码按钮（60秒倒计时）
  - 验证按钮
  - 返回按钮

#### Components

**UserInfoCard**
- 显示用户基本信息卡片
- 支持编辑模式切换

**FormInput**
- 统一的表单输入组件
- 支持验证和错误提示

**VerifyCodeInput**
- 6位验证码输入组件
- 自动聚焦下一个输入框

#### State Management

需要在 app.js 中管理以下状态：
- `userInfo`: 用户信息对象
- `profileLoading`: 加载状态

#### Navigation Flow

1. 用户点击"我的"tab → 进入个人中心页面
2. 点击"编辑资料" → 进入编辑资料页面
3. 修改信息并保存 → 显示成功提示 → 返回个人中心
4. 如果修改了邮箱 → 发送验证邮件 → 显示提示
5. 用户点击邮件链接 → 跳转到验证邮箱页面
6. 如果修改了手机号 → 跳转到验证手机号页面 → 输入验证码

### Testing Strategy

#### Unit Tests

**UserProfileServiceTest**
- 测试获取用户信息
- 测试更新用户名（有效/无效格式）
- 测试发起邮箱更改
- 测试验证邮箱更改（有效/过期/已使用token）
- 测试发起手机号更改
- 测试验证手机号更改（正确/错误验证码）
- 测试邮箱/手机号唯一性验证

#### Integration Tests

**UserControllerIntegrationTest**
- 测试获取用户信息端点（未认证/已认证）
- 测试更新用户信息端点
- 测试验证邮箱端点
- 测试验证手机号端点

#### Frontend Tests

- 测试个人中心页面渲染
- 测试编辑资料页面表单验证
- 测试保存成功/失败场景
- 测试验证码输入交互

### Developer Context

#### Architecture Compliance

本故事必须遵循以下架构决策：
- 使用 Spring Boot RESTful API
- 使用 JWT 进行认证
- 使用 BCrypt 进行密码加密（如果涉及密码）
- 使用 Redis 存储验证码
- 使用 MySQL 存储用户数据
- 遵循分层架构：Controller → Service → Repository
- 使用 GlobalExceptionHandler 处理异常
- 使用 ApiResponse 统一响应格式

#### Integration Points

- **EmailService**: 使用 Story 1.4 中创建的 EmailService 发送验证邮件
- **SmsService**: 使用 Story 1.4 中创建的 SmsService 发送验证短信
- **VerificationCodeService**: 使用 Story 1.4 中创建的 VerificationCodeService 管理验证码
- **JwtTokenProvider**: 使用现有的 JWT token 提供者
- **UserRepository**: 使用现有的用户仓库

#### Existing Patterns to Follow

参考 Story 1.4 的实现模式：
- DTO 模式（Request/Response 对象）
- Service 层业务逻辑
- Controller 层 REST API
- 统一异常处理
- 前端表单验证
- 加载状态管理

#### Code Style

- 遵循项目现有代码风格
- 使用 Java 8+ 特性
- 使用 Lombok 注解减少样板代码
- 使用 Spring Validation 进行参数验证
- 使用 @Transactional 保证数据一致性

#### Dependencies

使用项目现有依赖：
- Spring Boot Starter Web
- Spring Boot Starter Validation
- Spring Boot Starter Data JPA
- Spring Boot Starter Mail
- Lombok
- MySQL Connector
- Jedis (Redis client)

#### Configuration

需要在 application.yml 中配置：
- 邮箱验证链接的base URL
- 验证码过期时间
- 验证码长度

#### Performance Considerations

- 用户信息查询应该快速（< 500ms）
- 邮箱/手机号唯一性检查应该使用数据库索引
- 验证码存储使用 Redis 以提高性能
- 考虑添加缓存层缓存用户信息

#### Security Considerations

- 邮箱验证token必须足够长且随机（建议UUID）
- 验证token必须有时效性
- 防止验证token被重复使用
- 记录所有个人信息变更到审计日志
- 防止邮箱/手机号枚举攻击

#### Error Handling

- 统一使用 ApiResponse 返回错误信息
- 使用适当的 HTTP 状态码
- 前端显示用户友好的错误消息
- 记录详细的错误日志

#### Accessibility (AR Requirements)

遵循 AR7-AR8 的可访问性要求：
- 所有表单字段有清晰的标签
- 支持键盘导航
- 足够大的触摸目标（44x44像素）
- 颜色对比度符合 WCAG 2.1 AA 标准

#### UX Design Principles

遵循 UX-DR23-UX-DR28 的用户体验设计要求：
- 简洁的界面设计
- 清晰的反馈提示
- 微信友好的交互方式
- 情感化设计（缓解焦虑）

## Definition of Done

- [ ] 所有 acceptance criteria 已实现并通过测试
- [ ] 后端 API 已实现并通过单元测试
- [ ] 前端页面已实现并通过功能测试
- [ ] 代码审查已完成
- [ ] 代码符合项目规范
- [ ] API 文档已更新
- [ ] 用户故事已在 sprint-status.yaml 中标记为 done
- [ ] 没有已知的严重bug
- [ ] 性能测试通过（响应时间 < 1s）
- [ ] 安全测试通过（无已知安全漏洞）
