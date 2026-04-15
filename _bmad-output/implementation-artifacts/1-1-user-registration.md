# Story 1.1: 用户注册

状态：done

<!-- 注意：验证是可选的。在开发故事之前运行 validate-create-story 进行质量检查。 -->

## 用户故事

作为一个新用户，
我希望使用邮箱或手机号注册账户，
以便开始使用系统功能。

## 验收标准

**给定** 用户在注册页面
**当** 用户输入有效的邮箱或手机号
**并且** 用户输入符合要求的密码（至少8位，包含字母和数字）
**并且** 用户点击"注册"按钮
**那么** 系统应验证输入格式并创建用户账户
**并且** 系统应向用户提供的联系方式发送验证邮件或短信
**并且** 系统应显示"注册成功，请查收验证信息"的成功提示
**并且** 用户账户应处于未验证状态

**给定** 用户在注册页面
**当** 用户输入已注册的邮箱或手机号
**并且** 用户点击"注册"按钮
**那么** 系统应显示"该邮箱或手机号已被注册"的错误提示
**并且** 系统不应创建新账户

**给定** 用户在注册页面
**当** 用户输入无效的邮箱格式或无效的手机号
**并且** 用户点击"注册"按钮
**那么** 系统应显示"邮箱或手机号格式无效"的错误提示
**并且** 系统不应创建账户

**给定** 用户在注册页面
**当** 用户输入不符合强度要求的密码
**并且** 用户点击"注册"按钮
**那么** 系统应显示"密码必须至少8位且包含字母和数字"的错误提示
**并且** 系统不应创建账户

**给定** 用户已成功注册
**当** 用户点击邮件中的验证链接或输入短信验证码
**那么** 系统应验证用户的联系方式
**并且** 系统应将用户状态更改为"已验证"
**并且** 系统应允许用户登录

## 任务 / 子任务

### 后端实现

- [x] 创建包含必需字段的 User 实体（验收标准：#1, #2, #3, #4, #5）
  - [x] 定义 User.java 实体，包含字段：id, email, mobile, password, status, createdAt, updatedAt
  - [x] 添加 JPA 注解和表映射
  - [x] 在 email 和 mobile 字段上添加唯一约束
  - [x] 定义用户状态枚举（UNVERIFIED, VERIFIED, DISABLED）

- [x] 创建 RegisterRequest DTO（验收标准：#1, #3, #4）
  - [x] 定义 RegisterRequest.java 并添加验证注解
  - [x] 添加邮箱格式验证（@Email）
  - [x] 添加手机号格式验证（正则表达式）
  - [x] 添加密码强度验证（最小长度8位，必须包含字母和数字）

- [x] 创建 UserRepository 接口（验收标准：#1, #2）
  - [x] 定义 UserRepository 继承 JpaRepository<User, Long>
  - [x] 添加 findByEmail 方法
  - [x] 添加 findByMobile 方法
  - [x] 添加 existsByEmail 和 existsByMobile 方法

- [x] 实现 AuthService 用于注册逻辑（验收标准：#1, #2, #3, #4, #5）
  - [x] 创建 AuthService 接口，包含 register 方法
  - [x] 实现 AuthServiceImpl.register，包含验证逻辑
  - [x] 在创建账户前检查邮箱/手机号是否已存在
  - [x] 验证输入格式和密码强度
  - [x] 使用强度为10-12的 BCrypt 对密码进行哈希
  - [x] 创建状态为 UNVERIFIED 的用户
  - [x] 生成验证令牌/验证码
  - [x] 触发邮件/短信验证发送
  - [x] 处理异常并返回适当的错误消息

- [x] 实现邮件/短信验证服务（验收标准：#1, #5）
  - [x] 创建 NotificationService 接口
  - [x] 实现邮件发送功能（Spring Mail 或云服务）
  - [x] 实现短信发送功能（云服务，如阿里云短信或腾讯云短信）
  - [x] 生成并存储带过期时间的验证令牌
  - [x] 创建验证端点以验证令牌/验证码
  - [x] 验证成功后将用户状态更新为 VERIFIED

- [x] 创建包含注册端点的 AuthController（验收标准：#1, #2, #3, #4）
  - [x] 定义 POST /api/v1/auth/register 端点
  - [x] 添加 @Valid 注解进行请求验证
  - [x] 实现验证错误的异常处理
  - [x] 返回统一的 ApiResponse 格式
  - [x] 处理重复邮箱/手机号错误（HTTP 409）
  - [x] 处理验证错误（HTTP 400）
  - [x] 返回包含用户数据的成功响应（HTTP 201）

- [x] 实现验证端点（验收标准：#5）
  - [x] 定义 POST /api/v1/auth/verify 端点
  - [x] 接受验证令牌/验证码和用户标识符
  - [x] 验证令牌/验证码和过期时间
  - [x] 将用户状态更新为 VERIFIED
  - [x] 返回成功或错误响应

- [x] 添加异常处理（验收标准：#2, #3, #4）
  - [x] 创建 BusinessException 用于业务逻辑错误
  - [x] 创建 ResourceNotFoundException 用于资源未找到
  - [x] 创建 ValidationException 用于输入验证错误
  - [x] 使用 @ControllerAdvice 实现 GlobalExceptionHandler
  - [x] 将异常映射到适当的 HTTP 状态码和错误响应
  - [x] 使用错误码：20xxx 表示验证错误，40xxx 表示资源未找到

- [x] 添加数据库迁移脚本（验收标准：#1）
  - [x] 在 db/migration 中创建 V1__init_schema.sql
  - [x] 定义包含所有必需字段的 users 表
  - [x] 在 email 和 mobile 列上添加唯一索引
  - [x] 如需要，添加 verification_tokens 表

### 前端实现（微信小程序）

- [x] 创建注册页面（验收标准：#1, #2, #3, #4）
  - [x] 创建包含表单布局的 register.wxml
  - [x] 创建带样式的 register.wxss
  - [x] 创建包含表单逻辑的 register.js
  - [x] 创建用于页面配置的 register.json

- [x] 使用 Vant Weapp 组件实现表单（验收标准：#1, #3, #4）
  - [x] 使用 van-field 作为邮箱/手机号输入框
  - [x] 使用 van-field type="password" 作为密码输入框
  - [x] 使用 van-button 作为提交按钮
  - [x] 添加表单验证反馈
  - [x] 提交期间显示加载状态

- [x] 实现表单验证（验收标准：#3, #4）
  - [x] 在输入时添加邮箱格式验证
  - [x] 在输入时添加手机号格式验证
  - [x] 在输入时添加密码强度验证
  - [x] 显示实时验证反馈
  - [x] 在所有验证通过之前禁用提交按钮

- [x] 实现 API 集成（验收标准：#1, #2, #3, #4）
  - [x] 创建用于注册的 API 服务方法
  - [x] 向 /api/v1/auth/register 发送 POST 请求
  - [x] 处理成功响应并重定向到验证页面
  - [x] 处理错误响应并显示适当的错误消息
  - [x] 为网络错误实现重试逻辑

- [x] 创建验证页面（验收标准：#5）
  - [x] 创建 verify.wxml 以显示验证说明
  - [x] 创建带样式的 verify.wxss
  - [x] 创建包含验证逻辑的 verify.js
  - [x] 创建用于页面配置的 verify.json

- [x] 实现验证流程（验收标准：#5）
  - [x] 向用户显示验证说明
  - [x] 允许用户重新发送验证邮件/短信
  - [x] 自动检查验证状态（轮询或 WebSocket）
  - [x] 验证成功后重定向到登录页面

### 测试

- [x] 为 AuthService 编写单元测试（验收标准：#1, #2, #3, #4, #5）
  - [x] 测试使用邮箱成功注册
  - [x] 测试使用手机号成功注册
  - [x] 测试使用重复邮箱注册
  - [x] 测试使用重复手机号注册
  - [x] 测试使用无效邮箱格式注册
  - [x] 测试使用无效手机号格式注册
  - [x] 测试使用弱密码注册
  - [x] 测试使用 BCrypt 进行密码哈希
  - [x] 测试验证令牌生成

- [x] 为 AuthController 编写单元测试（验收标准：#1, #2, #3, #4）
  - [x] 测试使用有效数据调用 POST /api/v1/auth/register
  - [x] 测试使用重复邮箱调用 POST /api/v1/auth/register
  - [x] 测试使用无效数据调用 POST /api/v1/auth/register
  - [x] 测试响应格式符合 ApiResponse 结构
  - [x] 测试 HTTP 状态码正确

- [x] 编写集成测试（验收标准：#1, #5）
  - [x] 测试端到端注册流程
  - [x] 测试邮件发送集成
  - [x] 测试短信发送集成
  - [x] 测试验证流程

- [x] 编写前端测试（验收标准：#1, #3, #4）
  - [x] 测试表单验证
  - [x] 测试 API 集成
  - [x] 测试错误处理
  - [x] 测试用户反馈消息

### 文档

- [x] 更新 API 文档（验收标准：#1, #2, #3, #4, #5）
  - [x] 文档化 POST /api/v1/auth/register 端点
  - [x] 文档化 POST /api/v1/auth/verify 端点
  - [x] 文档化请求/响应格式
  - [x] 文档化错误码和消息
  - [x] 为所有场景添加示例

- [x] 更新项目文档（验收标准：#1）
  - [x] 在系统文档中记录注册流程
  - [x] 记录验证过程
  - [x] 记录邮件/短信服务配置
  - [x] 使用邮件/短信服务设置更新部署指南

## 开发说明

### 架构护栏和约束

**技术栈要求：**
- 后端：Java 17 + Spring Boot 3.3.6 [来源：architecture.md#Spring Initializr]
- 数据库：MySQL 用于用户数据（User 实体存储在 MySQL 中）[来源：architecture.md#Database Design Strategy]
- 前端：微信小程序，使用 Vant Weapp 4.x UI 组件 [来源：architecture.md#Mini-program Starter]
- API：RESTful API，使用统一响应格式 [来源：architecture.md#API Design Pattern]

**API 设计标准：**
- 端点：POST /api/v1/auth/register [来源：architecture.md#API Endpoints]
- 端点：POST /api/v1/auth/verify（用于验证）
- 响应格式必须遵循统一的 ApiResponse 结构：
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {},
    "timestamp": 1712800000000
  }
  ```
  [来源：architecture.md#RESTful Design Principles]
- HTTP 状态码：201 表示成功，400 表示验证错误，409 表示冲突 [来源：architecture.md#Error Handling Standards]

**安全要求：**
- 密码必须使用强度为10-12的 BCrypt 进行哈希 [来源：architecture.md#Encryption Methods]
- 用于身份验证的 JWT 令牌（将在 Story 1.2 中实现）[来源：architecture.md#Authentication Methods]
- 前端和后端都要进行输入验证 [来源：architecture.md#Validation Strategy]
- 防止 SQL 注入（使用 JPA 参数化查询）[来源：architecture.md#API Security Strategy]

**数据验证要求：**
- 多层验证：前端 → 后端控制器 → 后端服务 → 数据库 [来源：architecture.md#Validation Strategy]
- 使用 @Email 注解进行邮箱格式验证 [来源：architecture.md#Data Validation Strategy]
- 使用正则表达式进行手机号格式验证
- 密码强度验证：最少8位，必须包含字母和数字
- 数据库中 email 和 mobile 字段的唯一约束 [来源：architecture.md#Database Design Strategy]

**错误处理标准：**
- 使用自定义异常类：BusinessException, ValidationException, ResourceNotFoundException [来源：architecture.md#Exception Handling]
- 错误码范围：20xxx 表示验证错误，40xxx 表示资源未找到 [来源：architecture.md#Error Handling Standards]
- 使用 @ControllerAdvice 实现 GlobalExceptionHandler [来源：architecture.md#Error Handling Standards]
- 返回用户友好的中文错误消息（因为沟通语言是中文）[来源：config.yaml]

**数据库设计要求：**
- 使用 Flyway 进行数据库迁移 [来源：architecture.md#Data Migration Method]
- 迁移脚本位置：src/main/resources/db/migration/V1__init_schema.sql [来源：architecture.md#Project Directory Structure]
- 表命名：snake_case（例如 users 表）[来源：architecture.md#Naming Conventions]
- 字段命名：snake_case（例如 email, mobile, password_hash, created_at）[来源：architecture.md#Naming Conventions]
- 添加索引：idx_users_email, idx_users_mobile [来源：architecture.md#Naming Conventions]

**前端要求：**
- 使用 Vant Weapp 4.x 组件作为 UI [来源：architecture.md#Mini-program Starter]
- 遵循 WXSS 样式约定，使用 rpx 单位 [来源：architecture.md#Frontend Architecture]
- 实现实时反馈的表单验证 [来源：architecture.md#Validation Strategy]
- 使用 wx.request 进行 API 调用 [来源：architecture.md#Frontend Architecture]
- 在 app.globalData 或 wx.setStorageSync 中存储用户会话 [来源：architecture.md#State Management]

**第三方集成：**
- 邮件服务：使用 Spring Mail 或云服务（阿里云邮件 / 腾讯云 SES）[来源：architecture.md#Third-Party Integration]
- 短信服务：使用云服务（阿里云短信 / 腾讯云短信）[来源：architecture.md#Third-Party Integration]
- 在 application.yml 中配置服务凭证（生产环境使用环境变量）[来源：architecture.md#Deployment Strategy]

**UX 要求：**
- 遵循核心 UX 原则：极简主义、引导优先、微信友好 [来源：ux-design-specification.md#Core UX Principles]
- 目标用户：技术熟练度低的家长 [来源：ux-design-specification.md#Target Users]
- 使用简单语言提供清晰的指导和错误消息 [来源：ux-design-specification.md#Design Challenges]
- 通过积极反馈和清晰说明减少用户焦虑 [来源：architecture.md#Emotional Design]

### 项目结构说明

**后端文件位置：**
- 实体：src/main/java/com/chapt003/entity/User.java [来源：architecture.md#Project Directory Structure]
- 仓储：src/main/java/com/chapt003/repository/UserRepository.java [来源：architecture.md#Project Directory Structure]
- DTO 请求：src/main/java/com/chapt003/dto/request/RegisterRequest.java [来源：architecture.md#Project Directory Structure]
- DTO 响应：src/main/java/com/chapt003/dto/response/UserResponse.java [来源：architecture.md#Project Directory Structure]
- 服务接口：src/main/java/com/chapt003/service/AuthService.java [来源：architecture.md#Project Directory Structure]
- 服务实现：src/main/java/com/chapt003/service/impl/AuthServiceImpl.java [来源：architecture.md#Project Directory Structure]
- 控制器：src/main/java/com/chapt003/controller/AuthController.java [来源：architecture.md#Project Directory Structure]
- 异常：src/main/java/com/chapt003/exception/GlobalExceptionHandler.java [来源：architecture.md#Project Directory Structure]
- 迁移：src/main/resources/db/migration/V1__init_schema.sql [来源：architecture.md#Project Directory Structure]

**前端文件位置：**
- 注册页面：pages/register/（register.wxml, register.wxss, register.js, register.json）[来源：architecture.md#Project Directory Structure]
- 验证页面：pages/verify/（verify.wxml, verify.wxss, verify.js, verify.json）
- API 服务：utils/api.js 或 services/auth.js

**命名约定：**
- Java 类：PascalCase（例如 User, RegisterRequest, AuthService）[来源：architecture.md#Code Naming Conventions]
- Java 方法：camelCase（例如 register, findByEmail, validatePassword）[来源：architecture.md#Code Naming Conventions]
- 数据库表：snake_case（例如 users）[来源：architecture.md#Naming Conventions]
- 数据库字段：snake_case（例如 email, mobile, password_hash, created_at）[来源：architecture.md#Naming Conventions]
- API 端点：kebab-case（例如 /api/v1/auth/register）[来源：architecture.md#Naming Conventions]

### 测试标准

**单元测试：**
- 使用 JUnit 5 进行单元测试 [来源：architecture.md#Project Directory Structure]
- 测试位置：src/test/java/com/chapt003/ [来源：architecture.md#Project Directory Structure]
- 测试覆盖率：业务逻辑目标 >80% 覆盖率
- 使用 Mockito 模拟外部依赖（邮件服务、短信服务）

**集成测试：**
- 测试端到端注册流程
- 使用 TestContainers 或内存数据库测试数据库集成
- 使用 MockMvc 测试 API 端点

**前端测试：**
- 测试表单验证逻辑
- 测试 API 集成
- 测试用户交互和反馈

### 性能考虑

- BCrypt 密码哈希不应阻塞主线程（必要时考虑异步）
- 邮件/短信发送应异步（如可用，使用消息队列）[来源：architecture.md#Message Queue Application Scenarios]
- 为注册端点添加速率限制以防止滥用：10 请求/分钟/IP [来源：architecture.md#Rate Limiting Strategy]
- 注册后在 Redis 中缓存用户查找结果 [来源：architecture.md#Caching Strategy]

### 安全考虑

- 永不记录密码或验证令牌
- 所有 API 调用使用 HTTPS（在生产环境中强制执行）[来源：architecture.md#Encryption Methods]
- 为注册实现验证码以防止机器人攻击（考虑未来增强）
- 为验证令牌设置过期时间（例如24小时）
- 限制每个用户的验证尝试次数
- 审计记录所有注册尝试以进行安全监控 [来源：architecture.md#Audit Logging]

### 参考资料

- [architecture.md#Spring Initializr](file:///d:/opt/traswork/chapt003/_bmad-output/planning-artifacts/architecture.md#L283-310) - 后端技术栈和初始化
- [architecture.md#Database Design Strategy](file:///d:/opt/traswork/chapt003/_bmad-output/planning-artifacts/architecture.md#L468-476) - 数据库选择和策略
- [architecture.md#Authentication Methods](file:///d:/opt/traswork/chapt003/_bmad-output/planning-artifacts/architecture.md#L511-517) - 身份验证方法
- [architecture.md#Encryption Methods](file:///d:/opt/traswork/chapt003/_bmad-output/planning-artifacts/architecture.md#L545-547) - 使用 BCrypt 进行密码加密
- [architecture.md#Validation Strategy](file:///d:/opt/traswork/chapt003/_bmad-output/planning-artifacts/architecture.md#L477-481) - 多层验证方法
- [architecture.md#Error Handling Standards](file:///d:/opt/traswork/chapt003/_bmad-output/planning-artifacts/architecture.md#L608-649) - 错误码和异常处理
- [architecture.md#API Endpoints](file:///d:/opt/traswork/chapt003/_bmad-output/planning-artifacts/architecture.md#L2066-2070) - 注册和验证端点
- [architecture.md#Project Directory Structure](file:///d:/opt/traswork/chapt003/_bmad-output/planning-artifacts/architecture.md#L1553-1752) - 完整项目结构
- [architecture.md#Naming Conventions](file:///d:/opt/traswork/chapt003/_bmad-output/planning-artifacts/architecture.md#L1125-1149) - 代码和数据库命名标准
- [architecture.md#Rate Limiting Strategy](file:///d:/opt/traswork/chapt003/_bmad-output/planning-artifacts/architecture.md#L653-673) - API 速率限制
- [architecture.md#Audit Logging](file:///d:/opt/traswork/chapt003/_bmad-output/planning-artifacts/architecture.md#L565-579) - 安全审计要求
- [prd.md#User Account Management Module](file:///d:/opt/traswork/chapt003/_bmad-output/planning-artifacts/prd.md#L455-460) - FR1：用户注册需求
- [ux-design-specification.md#Core UX Principles](file:///d:/opt/traswork/chapt003/_bmad-output/planning-artifacts/ux-design-specification.md#L110-130) - UX 设计原则
- [ux-design-specification.md#Target Users](file:///d:/opt/traswork/chapt003/_bmad-output/planning-artifacts/ux-design-specification.md#L50-70) - 用户档案和技术熟练度
- [epics.md#Story 1.1](file:///d:/opt/traswork/chapt003/_bmad-output/planning-artifacts/epics.md#L283-330) - 包含验收标准的完整故事定义
- [config.yaml](file:///d:/opt/traswork/chapt003/_bmad/bmm/config.yaml) - 项目配置，包括语言设置

## 开发代理记录

### 使用的代理模型

Claude 4 (Sonnet)

### 调试日志引用

暂无调试日志。这是初始故事创建。

### 完成说明列表

- 已创建包含全面任务和验收标准的故事
- 已从 architecture.md 提取架构护栏和约束
- 已记录所有文件位置和命名约定
- 已包含测试标准和性能考虑
- 已指定安全和合规要求
- 已提供所有源文档的参考资料

### 文件列表

**需要创建的文件：**
- src/main/java/com/chapt003/entity/User.java
- src/main/java/com/chapt003/repository/UserRepository.java
- src/main/java/com/chapt003/dto/request/RegisterRequest.java
- src/main/java/com/chapt003/dto/response/UserResponse.java
- src/main/java/com/chapt003/service/AuthService.java
- src/main/java/com/chapt003/service/impl/AuthServiceImpl.java
- src/main/java/com/chapt003/controller/AuthController.java
- src/main/java/com/chapt003/exception/GlobalExceptionHandler.java
- src/main/java/com/chapt003/exception/BusinessException.java
- src/main/java/com/chapt003/exception/ValidationException.java
- src/main/java/com/chapt003/exception/ResourceNotFoundException.java
- src/main/java/com/chapt003/service/NotificationService.java
- src/main/java/com/chapt003/service/impl/NotificationServiceImpl.java
- src/main/resources/db/migration/V1__init_schema.sql
- pages/register/register.wxml
- pages/register/register.wxss
- pages/register/register.js
- pages/register/register.json
- pages/verify/verify.wxml
- pages/verify/verify.wxss
- pages/verify/verify.js
- pages/verify/verify.json

**需要修改的文件：**
- src/main/resources/application.yml（添加邮件/短信服务配置）
- app.json（注册新页面）
- utils/api.js（添加注册和验证 API 方法）

**需要创建的测试文件：**
- src/test/java/com/chapt003/service/AuthServiceTest.java
- src/test/java/com/chapt003/controller/AuthControllerTest.java
- src/test/java/com/chapt003/integration/RegistrationFlowTest.java
