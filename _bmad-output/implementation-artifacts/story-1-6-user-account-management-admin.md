# Story 1.6: 用户账户管理（管理员功能）

## Story Metadata

- **Story ID**: 1.6
- **Epic**: Epic 1 - 用户认证与账户管理
- **Title**: 用户账户管理（管理员功能）
- **Status**: ready-for-dev
- **Priority**: High
- **Story Points**: 13
- **Estimated Days**: 5-6
- **Assigned To**: TBD

## User Story

作为一个系统管理员，我希望能够管理用户账户和权限，以便控制用户访问和系统安全。

## Acceptance Criteria

### AC 1: 查看用户列表
**Given** 管理员已登录
**When** 管理员导航到"用户管理"页面
**Then** 系统应显示用户列表（包括用户名、邮箱/手机号、角色、状态、创建日期）
**And** 系统应支持按用户名或邮箱/手机号搜索
**And** 系统应支持按角色筛选（普通用户、管理员）
**And** 系统应支持按状态筛选（正常、禁用、未验证）
**And** 系统应支持分页显示

### AC 2: 查看用户详情
**Given** 管理员在"用户管理"页面
**When** 管理员点击某个用户的"查看详情"按钮
**Then** 系统应显示该用户的完整信息（个人资料、登录历史、操作日志）

### AC 3: 修改用户角色
**Given** 管理员在用户详情页面
**When** 管理员修改用户角色
**And** 管理员点击"保存"按钮
**Then** 系统应更新用户角色
**And** 系统应记录操作日志
**And** 系统应显示"角色更新成功"的成功提示

### AC 4: 禁用用户
**Given** 管理员在用户详情页面
**When** 管理员点击"禁用用户"按钮
**And** 管理员确认禁用操作
**Then** 系统应禁用该用户账户
**And** 系统应记录操作日志
**And** 系统应显示"用户已禁用"的成功提示

### AC 5: 被禁用用户无法登录
**Given** 用户账户被禁用
**When** 该用户尝试登录
**Then** 系统应显示"您的账户已被禁用，请联系管理员"的错误提示

### AC 6: 删除用户
**Given** 管理员在用户详情页面
**When** 管理员点击"删除用户"按钮
**And** 管理员确认删除操作
**Then** 系统应软删除该用户账户（标记为已删除而非物理删除）
**And** 系统应记录操作日志
**And** 系统应显示"用户已删除"的成功提示

### AC 7: 权限验证
**Given** 普通用户尝试访问用户管理页面
**When** 用户发送请求
**Then** 系统应返回403 Forbidden错误
**And** 系统应显示"权限不足"的错误提示

## Technical Requirements

### Backend Requirements

#### API Endpoints

**1. 获取用户列表**
- **Endpoint**: `GET /api/admin/users`
- **Description**: 获取用户列表（仅管理员）
- **Authentication**: Required (JWT Bearer Token, ADMIN role)
- **Request Headers**:
  - `Authorization`: Bearer {jwt_token}
- **Query Parameters**:
  - `page` (int, default: 0): 页码
  - `size` (int, default: 10): 每页大小
  - `search` (string, optional): 搜索关键词（用户名或邮箱/手机号）
  - `role` (string, optional): 角色筛选（USER, ADMIN）
  - `status` (string, optional): 状态筛选（ACTIVE, DISABLED, UNVERIFIED, DELETED）
- **Response**: `Page<UserListResponse>`

**2. 获取用户详情**
- **Endpoint**: `GET /api/admin/users/{userId}`
- **Description**: 获取用户完整信息（仅管理员）
- **Authentication**: Required (JWT Bearer Token, ADMIN role)
- **Request Headers**:
  - `Authorization`: Bearer {jwt_token}
- **Path Parameters**:
  - `userId` (long): 用户ID
- **Response**: `UserDetailResponse`

**3. 更新用户角色**
- **Endpoint**: `PUT /api/admin/users/{userId}/role`
- **Description**: 更新用户角色（仅管理员）
- **Authentication**: Required (JWT Bearer Token, ADMIN role)
- **Request Headers**:
  - `Authorization`: Bearer {jwt_token}
- **Path Parameters**:
  - `userId` (long): 用户ID
- **Request Body**: `UpdateUserRoleRequest`
- **Response**: `ApiResponse`

**4. 禁用/启用用户**
- **Endpoint**: `PUT /api/admin/users/{userId}/status`
- **Description**: 禁用或启用用户账户（仅管理员）
- **Authentication**: Required (JWT Bearer Token, ADMIN role)
- **Request Headers**:
  - `Authorization`: Bearer {jwt_token}
- **Path Parameters**:
  - `userId` (long): 用户ID
- **Request Body**: `UpdateUserStatusRequest`
- **Response**: `ApiResponse`

**5. 删除用户**
- **Endpoint**: `DELETE /api/admin/users/{userId}`
- **Description**: 软删除用户账户（仅管理员）
- **Authentication**: Required (JWT Bearer Token, ADMIN role)
- **Request Headers**:
  - `Authorization`: Bearer {jwt_token}
- **Path Parameters**:
  - `userId` (long): 用户ID
- **Response**: `ApiResponse`

#### DTOs

**UserListResponse**
```java
{
  "id": "long",
  "username": "string",
  "email": "string | null",
  "mobile": "string | null",
  "role": "USER | ADMIN",
  "status": "ACTIVE | DISABLED | UNVERIFIED | DELETED",
  "createdAt": "string (ISO 8601)",
  "lastLoginAt": "string | null (ISO 8601)"
}
```

**UserDetailResponse**
```java
{
  "id": "long",
  "username": "string",
  "email": "string | null",
  "mobile": "string | null",
  "role": "USER | ADMIN",
  "status": "ACTIVE | DISABLED | UNVERIFIED | DELETED",
  "emailVerified": "boolean",
  "mobileVerified": "boolean",
  "createdAt": "string (ISO 8601)",
  "updatedAt": "string (ISO 8601)",
  "lastLoginAt": "string | null (ISO 8601)",
  "loginHistory": [
    {
      "loginTime": "string (ISO 8601)",
      "ipAddress": "string",
      "userAgent": "string",
      "loginMethod": "PASSWORD | WECHAT"
    }
  ],
  "auditLogs": [
    {
      "id": "long",
      "action": "string",
      "details": "string",
      "createdAt": "string (ISO 8601)",
      "operatorName": "string"
    }
  ]
}
```

**UpdateUserRoleRequest**
```java
{
  "role": "USER | ADMIN"
}
```

**UpdateUserStatusRequest**
```java
{
  "status": "ACTIVE | DISABLED"
}
```

#### Enums

**UserRole**
```java
public enum UserRole {
    USER,
    ADMIN
}
```

**UserStatus**
```java
public enum UserStatus {
    ACTIVE,        // 正常
    DISABLED,      // 禁用
    UNVERIFIED,    // 未验证
    DELETED        // 已删除
}
```

#### Service Layer

**UserAdminService**
- `getUserList(Pageable pageable, String search, UserRole role, UserStatus status)`: 获取用户列表
- `getUserDetail(Long userId)`: 获取用户详情
- `updateUserRole(Long userId, UserRole newRole, Long adminId)`: 更新用户角色
- `updateUserStatus(Long userId, UserStatus newStatus, Long adminId)`: 更新用户状态
- `deleteUser(Long userId, Long adminId)`: 软删除用户
- `getLoginHistory(Long userId, Pageable pageable)`: 获取用户登录历史
- `getAuditLogs(Long userId, Pageable pageable)`: 获取用户操作日志

**AuditLogService**
- `logAction(Long userId, String action, String details, Long operatorId)`: 记录操作日志
- `getAuditLogs(Long userId, Pageable pageable)`: 获取操作日志

#### Controller Layer

**UserAdminController**
- `getUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String search, @RequestParam(required = false) UserRole role, @RequestParam(required = false) UserStatus status)`: 获取用户列表
- `getUserDetail(@PathVariable Long userId)`: 获取用户详情
- `updateUserRole(@PathVariable Long userId, @RequestBody UpdateUserRoleRequest request)`: 更新用户角色
- `updateUserStatus(@PathVariable Long userId, @RequestBody UpdateUserStatusRequest request)`: 更新用户状态
- `deleteUser(@PathVariable Long userId)`: 删除用户

### Database Requirements

#### User Entity Updates

需要为 User 实体添加以下字段（如果不存在）：
```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 20)
    private String username;
    
    @Column(unique = true)
    private String email;
    
    @Column(unique = true)
    private String mobile;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.UNVERIFIED;
    
    @Column(name = "email_verified")
    private Boolean emailVerified = false;
    
    @Column(name = "mobile_verified")
    private Boolean mobileVerified = false;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    private String password;
    
    private String openId;
}
```

#### LoginHistory Entity

```java
@Entity
@Table(name = "login_history")
public class LoginHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private LocalDateTime loginTime;
    
    private String ipAddress;
    
    @Column(columnDefinition = "TEXT")
    private String userAgent;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginMethod loginMethod;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
```

#### AuditLog Entity

```java
@Entity
@Table(name = "audit_log")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "operator_id")
    private User operator;
    
    @Column(nullable = false, length = 100)
    private String action;
    
    @Column(columnDefinition = "TEXT")
    private String details;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
```

#### LoginMethod Enum

```java
public enum LoginMethod {
    PASSWORD,
    WECHAT
}
```

#### Repository Interfaces

**LoginHistoryRepository**
```java
@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
    Page<LoginHistory> findByUserIdOrderByLoginTimeDesc(Long userId, Pageable pageable);
}
```

**AuditLogRepository**
```java
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    Page<AuditLog> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
```

**UserRepository Updates**

需要在 UserRepository 中添加新的查询方法：
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsernameAndStatusNot(String username, UserStatus status);
    
    Optional<User> findByEmailAndStatusNot(String email, UserStatus status);
    
    Optional<User> findByMobileAndStatusNot(String mobile, UserStatus status);
    
    @Query("SELECT u FROM User u WHERE u.status != :status AND " +
           "(LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.mobile) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> findBySearchAndStatusNot(@Param("search") String search, 
                                        @Param("status") UserStatus status, 
                                        Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.status != :status AND u.role = :role")
    Page<User> findByRoleAndStatusNot(@Param("role") UserRole role,
                                      @Param("status") UserStatus status,
                                      Pageable pageable);
}
```

### Security Requirements

- 所有 API 端点需要 JWT 认证和 ADMIN 角色
- 管理员不能删除自己
- 管理员不能禁用自己
- 系统必须至少保留一个管理员账户
- 记录所有管理操作到审计日志
- 防止未授权访问用户信息
- 使用软删除而非物理删除，保留数据审计轨迹

### Frontend Requirements

#### Pages

**1. 用户管理页面**
- 路径: `pages/admin/users/users`
- 功能:
  - 搜索框（按用户名或邮箱/手机号搜索）
  - 角色筛选下拉框（全部、普通用户、管理员）
  - 状态筛选下拉框（全部、正常、禁用、未验证）
  - 用户列表表格
  - 分页控件
  - 表格列：用户名、邮箱/手机号、角色、状态、创建日期、最后登录、操作
  - 操作列包含"查看详情"按钮

**2. 用户详情页面**
- 路径: `pages/admin/user-detail/user-detail?userId={userId}`
- 功能:
  - 显示用户基本信息（用户名、邮箱、手机号、角色、状态、创建日期、最后登录）
  - 显示登录历史列表（时间、IP地址、登录方式）
  - 显示操作日志列表（时间、操作、详情、操作人）
  - 角色修改下拉框和保存按钮
  - 禁用/启用用户按钮
  - 删除用户按钮
  - 返回按钮

#### Components

**UserListTable**
- 用户列表表格组件
- 支持分页
- 支持搜索和筛选
- 状态标签显示（不同颜色）

**UserDetailCard**
- 用户详细信息卡片
- 支持编辑模式

**LoginHistoryList**
- 登录历史列表组件

**AuditLogList**
- 操作日志列表组件

**ConfirmDialog**
- 确认对话框组件

#### State Management

需要在 app.js 中管理以下状态：
- `userList`: 用户列表
- `userListLoading`: 加载状态
- `userListPagination`: 分页信息
- `filters`: 筛选条件
- `selectedUser`: 当前选中的用户

#### Navigation Flow

1. 管理员点击"用户管理" → 进入用户管理页面
2. 使用搜索框或筛选器 → 更新用户列表
3. 点击"查看详情" → 进入用户详情页面
4. 修改角色并保存 → 显示成功提示 → 更新用户列表
5. 点击"禁用用户" → 显示确认对话框 → 确认后禁用用户
6. 点击"删除用户" → 显示确认对话框 → 确认后软删除用户

### Testing Strategy

#### Unit Tests

**UserAdminServiceTest**
- 测试获取用户列表（各种筛选条件）
- 测试获取用户详情
- 测试更新用户角色
- 测试禁用/启用用户
- 测试删除用户
- 测试权限验证（非管理员访问）
- 测试自我删除/禁用防护
- 测试最后管理员保护

**AuditLogServiceTest**
- 测试记录操作日志
- 测试获取操作日志

#### Integration Tests

**UserAdminControllerIntegrationTest**
- 测试获取用户列表端点
- 测试获取用户详情端点
- 测试更新用户角色端点
- 测试更新用户状态端点
- 测试删除用户端点
- 测试权限验证

#### Frontend Tests

- 测试用户管理页面渲染
- 测试搜索和筛选功能
- 测试分页功能
- 测试用户详情页面渲染
- 测试角色修改功能
- 测试禁用/启用用户功能
- 测试删除用户功能
- 测试确认对话框交互

### Developer Context

#### Architecture Compliance

本故事必须遵循以下架构决策：
- 使用 Spring Boot RESTful API
- 使用 JWT 进行认证
- 使用 Spring Security 进行授权（基于角色）
- 使用 MySQL 存储用户数据
- 遵循分层架构：Controller → Service → Repository
- 使用 GlobalExceptionHandler 处理异常
- 使用 ApiResponse 统一响应格式
- 使用软删除保留数据审计轨迹

#### Integration Points

- **JwtTokenProvider**: 使用现有的 JWT token 提供者
- **UserRepository**: 使用现有的用户仓库（需要更新）
- **UserDetailsService**: 需要更新以支持用户状态检查
- **AuthenticationService**: 需要更新以记录登录历史

#### Existing Patterns to Follow

参考 Story 1.1-1.5 的实现模式：
- DTO 模式（Request/Response 对象）
- Service 层业务逻辑
- Controller 层 REST API
- 统一异常处理
- 前端表格和分页组件
- 确认对话框组件

#### Code Style

- 遵循项目现有代码风格
- 使用 Java 8+ 特性
- 使用 Lombok 注解减少样板代码
- 使用 Spring Validation 进行参数验证
- 使用 @Transactional 保证数据一致性

#### Dependencies

使用项目现有依赖：
- Spring Boot Starter Web
- Spring Boot Starter Security
- Spring Boot Starter Validation
- Spring Boot Starter Data JPA
- Lombok
- MySQL Connector

#### Configuration

需要在 application.yml 中配置：
- 管理员权限相关的配置
- 审计日志保留天数
- 登录历史保留天数

#### Performance Considerations

- 用户列表查询应该高效（使用分页）
- 为搜索字段添加数据库索引
- 审计日志表应该有适当的索引
- 考虑添加缓存层缓存用户信息
- 大数据量时考虑使用异步加载

#### Security Considerations

- 严格的权限检查（ADMIN角色）
- 防止管理员自我删除/禁用
- 确保系统至少有一个管理员
- 记录所有管理操作到审计日志
- 敏感操作需要确认
- 防止CSRF攻击
- 使用HTTPS传输

#### Error Handling

- 统一使用 ApiResponse 返回错误信息
- 使用适当的 HTTP 状态码
- 前端显示用户友好的错误消息
- 记录详细的错误日志
- 处理并发修改冲突

#### Accessibility (AR Requirements)

遵循 AR7-AR8 的可访问性要求：
- 所有表单字段有清晰的标签
- 支持键盘导航
- 足够大的触摸目标（44x44像素）
- 颜色对比度符合 WCAG 2.1 AA 标准
- 状态标签使用颜色和文字双重标识

#### UX Design Principles

遵循 UX-DR23-UX-DR28 的用户体验设计要求：
- 清晰的界面布局
- 直观的操作流程
- 明确的确认提示
- 友好的错误消息
- 操作成功后的反馈

## Definition of Done

- [ ] 所有 acceptance criteria 已实现并通过测试
- [ ] 后端 API 已实现并通过单元测试
- [ ] 前端页面已实现并通过功能测试
- [ ] 代码审查已完成
- [ ] 代码符合项目规范
- [ ] API 文档已更新
- [ ] 用户故事已在 sprint-status.yaml 中标记为 done
- [ ] 没有已知的严重bug
- [ ] 性能测试通过（列表查询 < 1s）
- [ ] 安全测试通过（无已知安全漏洞）
- [ ] 审计日志记录正确
- [ ] 权限控制正确
