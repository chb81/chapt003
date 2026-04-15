---
stepsCompleted: [1, 2]
inputDocuments:
  - "prd.md"
  - "architecture.md"
  - "ux-design-specification.md"
---

# chapt003 - Epic Breakdown

## Overview
This document provides the complete epic and story breakdown for the High School Entrance Exam Volunteer Application System (中考志愿填报系统). It organizes all functional requirements (FRs), non-functional requirements (NFRs), UX design requirements (UX-DRs), and additional technical requirements into epics and user stories for systematic implementation.

## Requirements Inventory

### Functional Requirements

**User Account Management Module**
FR1: Users shall be able to register an account using email or mobile phone number
FR2: Users shall be able to login with email/mobile and password
FR3: Users shall be able to login via WeChat OAuth authorization
FR4: Users shall be able to reset password through email/SMS verification
FR5: Users shall be able to view and update personal profile information

**Student Information Management Module**
FR6: Users shall be able to input student's basic information (name, gender, birth date)
FR7: Users shall be able to input student's academic scores (subject-wise scores)
FR8: Users shall be able to input student's regional information (city, district, school)
FR9: Users shall be able to view school information list with filtering and sorting
FR10: Users shall be able to view detailed information for each school
FR11: Users shall be able to search schools by name or keyword
FR12: Users shall be able to filter schools by region, type, and score range

**School Information Management Module**
FR13: Admins shall be able to import school data from Excel/CSV files
FR14: Admins shall be able to export school data to Excel/CSV files
FR15: Admins shall be able to edit school information (name, region, score requirements)

**Volunteer Application Simulation Module**
FR16: Users shall be able to add schools to volunteer application list
FR17: Users shall be able to edit volunteer application order
FR18: Users shall be able to delete schools from volunteer application list
FR19: Users shall be able to view volunteer application history

**Prediction and Analysis Module**
FR20: Users shall be able to view admission probability prediction for selected schools
FR21: Users shall be able to receive intelligent school recommendations based on scores
FR22: Users shall be able to create multiple simulation scenarios
FR23: Users shall be able to save simulation scenarios for comparison

**Data Import and Management Module**
FR24: Admins shall be able to import historical admission data
FR25: Admins shall be able to export system data for backup
FR26: Admins shall be able to create system data backups
FR27: Admins shall be able to restore system data from backups

**System Management Module**
FR28: Admins shall be able to manage user accounts and permissions
FR29: Admins shall be able to configure system parameters
FR30: Admins shall be able to view system operation logs
FR31: Admins shall be able to monitor system performance metrics
FR32: Admins shall be able to manage content (news, policies, FAQs)
FR33: Users shall be able to view system announcements and news
FR34: Users shall be able to view admission policy information
FR35: Users shall be able to view FAQ and help documentation
FR36: Users shall be able to submit feedback and suggestions
FR37: Admins shall be able to review and respond to user feedback
FR38: System shall automatically send notifications for important events
FR39: Users shall be able to manage notification preferences

### NonFunctional Requirements

**Performance Requirements**
NFR1: System shall support 1000 concurrent users
NFR2: Prediction calculation shall complete within 10 seconds
NFR3: Page load time shall be less than 3 seconds
NFR4: First Contentful Paint (FCP) shall be less than 1.8 seconds
NFR5: Largest Contentful Paint (LCP) shall be less than 2.5 seconds
NFR6: System availability shall be greater than 99.9%
NFR7: Data query response time shall be less than 3 seconds
NFR8: Page navigation transition shall be less than 500 milliseconds

**Security Requirements**
NFR9: All data transmission shall use HTTPS with TLS 1.3
NFR10: Sensitive data shall be encrypted using AES-256
NFR11: System shall implement Role-Based Access Control (RBAC)
NFR12: All user operations shall be logged for audit
NFR13: System shall support automated daily data backups
NFR14: System shall protect against SQL injection attacks
NFR15: System shall protect against XSS attacks
NFR16: System shall protect against CSRF attacks
NFR17: Sensitive data shall be masked in logs and displays
NFR18: System shall support regular security audits

**Scalability Requirements**
NFR19: System shall support horizontal scaling via Docker and Kubernetes
NFR20: System shall use modular architecture for independent feature deployment
NFR21: System shall implement API versioning for backward compatibility
NFR22: System shall reserve microservice architecture capabilities

**Accessibility Requirements**
NFR23: System shall comply with WCAG 2.1 Level AA standards
NFR24: System shall support keyboard navigation for all functions
NFR25: System shall provide ARIA labels for screen readers
NFR26: System shall support high contrast mode

**Integration Requirements**
NFR27: System shall integrate with WeChat Open Platform API
NFR28: System shall reserve API gateway for third-party data source integration
NFR29: System shall reserve Single Sign-On (SSO) capability via OAuth2
NFR30: System shall reserve payment integration capability (WeChat Pay)
NFR31: System shall support notification push via WeChat template messages
NFR32: System shall support data export in Excel and PDF formats
NFR33: System shall provide API documentation using Swagger/OpenAPI 3.0
NFR34: System shall support third-party monitoring integration (Prometheus/Grafana)

### Additional Requirements

**Technology Stack Requirements**
AR1: Backend shall use Spring Boot 3.3.6 with Java 17
AR2: Frontend admin platform shall use Vue 3 + Vite + TypeScript
AR3: Miniprogram shall use WeChat native development with Vant Weapp 4.x
AR4: Database shall use MySQL for relational data storage
AR5: Document storage shall use MongoDB for simulation scenarios and analysis results
AR6: Caching shall use Redis for session and hot data
AR7: Security shall use Spring Security 6.x with JWT authentication
AR8: OAuth2 shall be supported for WeChat and future SSO integrations

**Starter Template Requirements**
AR9: Backend project shall be initialized via Spring Initializr
AR10: Admin platform shall be initialized via Vite TypeScript starter
AR11: Miniprogram shall be created via WeChat DevTools
AR12: All starter templates shall use latest stable versions

**Naming Convention Requirements**
AR13: Database tables and columns shall use snake_case naming
AR14: API endpoints shall use plural form and kebab-case
AR15: Java class names shall use PascalCase
AR16: Java method and variable names shall use camelCase
AR17: Vue component names shall use PascalCase
AR18: JSON response fields shall use snake_case

**Project Structure Requirements**
AR19: Backend shall use feature-based package organization
AR20: Frontend shall use feature-based component organization
AR21: Test files shall be co-located with source files
AR22: All projects shall follow consistent directory structure as defined in architecture

**Implementation Pattern Requirements**
AR23: API responses shall use unified wrapper format with code, message, data, timestamp
AR24: Event names shall use dot notation (e.g., user.login, student.updated)
AR25: Local loading states shall be used for form submissions
AR26: Multi-layer validation shall be applied (frontend, controller, service)
AR27: Repository layer shall only provide basic CRUD operations
AR28: Complex queries shall be implemented in Service layer

**API Specification Requirements**
AR29: RESTful API shall follow OpenAPI 3.0 specification
AR30: API version shall be included in URL path (e.g., /api/v1/)
AR31: Error responses shall include field-level validation details
AR32: All API responses shall include timestamp

**Integration Boundary Requirements**
AR33: Controller layer shall only handle HTTP request/response, no business logic
AR34: Service layer shall contain all business logic, can span multiple repositories
AR35: Repository layer shall only be responsible for data access
AR36: Entity layer shall only contain data models and JPA annotations
AR37: DTO layer shall be used for data transfer between Controller and Service
AR38: VO layer shall be used for formatted data returned to frontend

**Component Boundary Requirements**
AR39: Page components shall be complete page views with multiple business components
AR40: Business components shall be reusable for specific business functions
AR41: Common components shall be UI components across business domains
AR42: Composables shall be reusable composition functions
AR43: API modules shall centrally manage all API calls
AR44: Store modules shall manage state by feature modules

**Data Storage Requirements**
AR45: Redis cache keys shall use format: module:entity:id
AR46: Cache expiration time shall be set based on data type
AR47: Hot data shall be automatically cached
AR48: Cache update strategy shall use Cache-Aside pattern

**Development Environment Requirements**
AR49: Development shall use Docker for local environment consistency
AR50: Code shall use ESLint for frontend linting
AR51: Code shall use Prettier for code formatting
AR52: Code shall use Checkstyle for backend code style checking

### UX Design Requirements

**Design System Requirements**
UX-DR1: System shall use Vant Weapp component library for miniprogram
UX-DR2: System shall use Vant component library for admin platform
UX-DR3: Primary color shall be Trust Blue (#1989FA)
UX-DR4: Secondary color shall be Success Green (#07C160)
UX-DR5: Typography shall use system fonts with 10px-20px scale
UX-DR6: Spacing shall follow 4px base unit (4px, 8px, 12px, 16px, 20px, 24px)
UX-DR7: Border radius shall use 4px for small components, 8px for medium, 12px for large
UX-DR8: Shadows shall follow elevation system (0-4 levels)

**Custom Component Requirements**
UX-DR9: SchoolRecommendCard component shall display school information with admission probability
UX-DR10: VolunteerPicker component shall support drag-and-drop for volunteer ordering
UX-DR11: ProbabilityChart component shall visualize admission probability trends
UX-DR12: PolicyVisualizer component shall display policy information in visual format
UX-DR13: PlanComparison component shall compare multiple simulation scenarios side-by-side
UX-DR14: OnboardingStepper component shall guide new users through initial setup
UX-DR15: RecommendationResult component shall display intelligent recommendation results
UX-DR16: StudentPositionCard component shall display student's competitive position

**Responsive Design Requirements**
UX-DR17: System shall use mobile-first responsive strategy
UX-DR18: Mobile breakpoint shall be less than 768px
UX-DR19: Tablet breakpoint shall be 768px-1023px
UX-DR20: Desktop breakpoint shall be 1024px and above
UX-DR21: Layout shall use fluid grid system
UX-DR22: Images shall use srcset for responsive loading

**Accessibility Requirements**
UX-DR23: All interactive elements shall have minimum touch target size of 44x44 pixels
UX-DR24: Color contrast ratio shall meet WCAG 2.1 Level AA (4.5:1 for normal text)
UX-DR25: All images shall have descriptive alt text
UX-DR26: Forms shall have clear labels and error messages
UX-DR27: Focus indicators shall be visible for keyboard navigation
UX-DR28: Content shall be readable when zoomed to 200%

**UX Consistency Requirements**
UX-DR29: Primary buttons shall use Trust Blue color with white text
UX-DR30: Secondary buttons shall use white background with Trust Blue border
UX-DR31: Danger buttons shall use Red color (#FF4444)
UX-DR32: Success feedback shall use green checkmark with success message
UX-DR33: Error feedback shall use red X with error message
UX-DR34: Loading states shall show spinner with progress indication
UX-DR35: Empty states shall show illustration with action suggestion
UX-DR36: Navigation shall use bottom tab bar for miniprogram
UX-DR37: Navigation shall use top navigation bar for admin platform

**Interaction Requirements**
UX-DR38: Forms shall use real-time validation with inline error messages
UX-DR39: Confirmation dialogs shall be shown for destructive actions
UX-DR40: Toast notifications shall appear for success/error feedback
UX-DR41: Pull-to-refresh shall be supported on list pages
UX-DR42: Infinite scroll shall be supported for paginated content
UX-DR43: Swipe gestures shall be supported for list item actions
UX-DR44: Long press shall show context menu for list items

**Performance Requirements**
UX-DR45: Images shall be optimized and compressed
UX-DR46: Lazy loading shall be used for off-screen content
UX-DR47: Code splitting shall be used for route-based optimization
UX-DR48: Critical CSS shall be inline for above-fold content
UX-DR49: Font display shall use swap strategy
UX-DR50: Animations shall use transform and opacity for 60fps performance

### FR Coverage Map

| Epic | FRs Covered |
|------|-------------|
| Epic 1: 用户认证与个人资料 | FR1-FR5, FR28, NFR9-NFR18, AR7, AR8, AR11, AR27, AR29-AR32 |
| Epic 2: 学生信息档案 | FR6-FR8, NFR1-NFR8, AR1-AR6, AR13-AR18, AR23-AR28, AR45-AR48, UX-DR23-UX-DR28, UX-DR38 |
| Epic 3: 学校信息浏览与搜索 | FR9-FR15, AR32, UX-DR9, UX-DR29-UX-DR37, UX-DR40-UX-DR44 |
| Epic 4: 志愿填报与模拟 | FR16-FR19, FR22-FR23, UX-DR10, UX-DR13, UX-DR15, UX-DR16, UX-DR39, UX-DR40 |
| Epic 5: 录取预测与智能推荐 | FR20-FR21, NFR2, NFR7, AR45-AR48, UX-DR11, UX-DR12, UX-DR15, UX-DR29-UX-DR35 |
| Epic 6: 系统内容与用户服务 | FR33-FR39, NFR27, NFR31, AR33, UX-DR29-UX-DR37, UX-DR40 |
| Epic 7: 管理后台与系统管理 | FR24-FR27, FR29-FR32, NFR5-NFR8, NFR12-NFR13, NFR19-NFR22, AR19-AR28, AR49-AR52 |
| Epic 8: 系统可访问性与性能优化 | NFR3-NFR6, NFR23-NFR26, NFR33-NFR34, UX-DR1-UX-DR50, AR9-AR12, AR17, AR39-AR44, AR45-AR50 |

## Epic List

1. **Epic 1: 用户认证与个人资料** - 用户注册、登录和基本账户管理
2. **Epic 2: 学生信息档案** - 学生基本信息、成绩和地区信息管理
3. **Epic 3: 学校信息浏览与搜索** - 学校列表查看、详情、搜索和筛选
4. **Epic 4: 志愿填报与模拟** - 志愿添加、排序、修改和模拟方案管理
5. **Epic 5: 录取预测与智能推荐** - 录取概率预测和智能学校推荐
6. **Epic 6: 系统内容与用户服务** - 系统公告、政策、FAQ和用户反馈
7. **Epic 7: 管理后台与系统管理** - 系统参数配置、监控、数据管理
8. **Epic 8: 系统可访问性与性能优化** - 无障碍访问、性能优化和系统集成

---

## Epic 1: 用户认证与个人资料

**Epic Goal:** 学生和家长能够注册账户、登录系统，并管理个人基本信息

**FRs Covered:** FR1-FR5, FR28, NFR9-NFR18, AR7, AR8, AR11, AR27, AR29-AR32

### Story 1.1: 用户注册

作为一个新用户，
我希望能够使用邮箱或手机号注册账户，
以便开始使用系统功能。

**Acceptance Criteria:**

**Given** 用户在注册页面
**When** 用户输入有效的邮箱或手机号
**And** 用户输入符合要求的密码（至少8位，包含字母和数字）
**And** 用户点击"注册"按钮
**Then** 系统应验证输入格式并创建用户账户
**And** 系统应发送验证邮件或短信到用户提供的联系方式
**And** 系统应显示"注册成功，请查收验证信息"的成功提示
**And** 用户账户应处于未验证状态

**Given** 用户在注册页面
**When** 用户输入已存在的邮箱或手机号
**Then** 系统应显示"该邮箱/手机号已被注册"的错误提示

**Given** 用户在注册页面
**When** 用户输入不符合格式的邮箱或手机号
**Then** 系统应显示"邮箱/手机号格式不正确"的实时验证错误

**Given** 用户在注册页面
**When** 用户输入弱密码（少于8位或仅包含数字）
**Then** 系统应显示"密码至少8位，包含字母和数字"的实时验证错误

### Story 1.2: 用户登录

作为一个已注册用户，
我希望能够使用邮箱/手机号和密码登录系统，
以便访问我的个人数据和系统功能。

**Acceptance Criteria:**

**Given** 用户在登录页面
**When** 用户输入正确的邮箱/手机号和密码
**And** 用户点击"登录"按钮
**Then** 系统应验证用户凭据
**And** 系统应生成并返回JWT令牌
**And** 系统应将用户重定向到首页
**And** 系统应显示"登录成功"的成功提示

**Given** 用户在登录页面
**When** 用户输入错误的密码
**Then** 系统应显示"邮箱/手机号或密码错误"的错误提示

**Given** 用户在登录页面
**When** 用户输入不存在的邮箱/手机号
**Then** 系统应显示"邮箱/手机号或密码错误"的错误提示

**Given** 用户在登录页面
**When** 用户输入未验证的邮箱/手机号
**Then** 系统应显示"账户未验证，请先验证邮箱/手机号"的错误提示

**Given** 用户已登录
**When** JWT令牌过期
**Then** 系统应自动跳转到登录页面
**And** 系统应显示"登录已过期，请重新登录"的提示

### Story 1.3: 微信授权登录

作为一个微信用户，
我希望能够使用微信授权登录系统，
以便快速访问系统而无需记住额外的密码。

**Acceptance Criteria:**

**Given** 用户在登录页面
**When** 用户点击"微信登录"按钮
**Then** 系统应跳转到微信授权页面
**And** 系统应使用OAuth2协议发起授权请求

**Given** 用户在微信授权页面
**When** 用户点击"授权"按钮
**Then** 系统应获取用户的微信OpenID和基本信息
**And** 系统应检查是否已存在该OpenID绑定的账户
**And** 如果账户存在，系统应直接登录用户
**And** 如果账户不存在，系统应创建新账户并绑定微信OpenID
**And** 系统应返回JWT令牌
**And** 系统应重定向到首页

**Given** 用户在微信授权页面
**When** 用户点击"取消"按钮
**Then** 系统应返回登录页面
**And** 系统应显示"授权已取消"的提示

**Given** 微信授权登录失败（如网络错误）
**Then** 系统应显示"微信登录失败，请稍后重试"的错误提示

### Story 1.4: 密码重置

作为一个忘记密码的用户，
我希望能够通过邮箱或短信重置密码，
以便重新访问我的账户。

**Acceptance Criteria:**

**Given** 用户在登录页面
**When** 用户点击"忘记密码"链接
**Then** 系统应跳转到密码重置请求页面

**Given** 用户在密码重置请求页面
**When** 用户输入注册的邮箱或手机号
**And** 用户点击"发送验证码"按钮
**Then** 系统应验证邮箱/手机号是否存在
**And** 系统应发送验证码到用户的邮箱或手机
**And** 系统应显示"验证码已发送"的成功提示

**Given** 用户在密码重置页面
**When** 用户输入正确的验证码
**And** 用户输入新密码（符合密码强度要求）
**And** 用户确认新密码
**And** 用户点击"重置密码"按钮
**Then** 系统应验证验证码是否有效且未过期
**And** 系统应验证两次密码输入是否一致
**And** 系统应更新用户密码
**And** 系统应显示"密码重置成功"的成功提示
**And** 系统应重定向到登录页面

**Given** 用户在密码重置页面
**When** 用户输入错误或过期的验证码
**Then** 系统应显示"验证码错误或已过期"的错误提示

**Given** 用户在密码重置页面
**When** 用户输入不一致的新密码
**Then** 系统应显示"两次密码输入不一致"的错误提示

### Story 1.5: 个人信息查看和更新

作为一个已登录用户，
我希望能够查看和更新我的个人资料，
以便保持我的账户信息准确和最新。

**Acceptance Criteria:**

**Given** 用户已登录
**When** 用户导航到"我的"页面
**Then** 系统应显示用户的个人资料（用户名、邮箱/手机号、创建日期）

**Given** 用户在"我的"页面
**When** 用户点击"编辑资料"按钮
**Then** 系统应显示个人资料编辑表单

**Given** 用户在个人资料编辑表单
**When** 用户修改用户名
**And** 用户点击"保存"按钮
**Then** 系统应验证用户名格式（长度2-20个字符）
**And** 系统应更新用户名
**And** 系统应显示"资料更新成功"的成功提示

**Given** 用户在个人资料编辑表单
**When** 用户修改邮箱
**And** 用户点击"保存"按钮
**Then** 系统应验证邮箱格式
**And** 系统应验证邮箱是否已被其他用户使用
**And** 系统应发送验证邮件到新邮箱
**And** 系统应显示"请验证新邮箱地址"的提示

**Given** 用户在个人资料编辑表单
**When** 用户修改手机号
**And** 用户点击"保存"按钮
**Then** 系统应验证手机号格式
**And** 系统应验证手机号是否已被其他用户使用
**And** 系统应发送验证短信到新手机号
**And** 系统应显示"请验证新手机号"的提示

### Story 1.6: 用户账户管理（管理员功能）

作为一个系统管理员，
我希望能够管理用户账户和权限，
以便控制用户访问和系统安全。

**Acceptance Criteria:**

**Given** 管理员已登录
**When** 管理员导航到"用户管理"页面
**Then** 系统应显示用户列表（包括用户名、邮箱/手机号、角色、状态、创建日期）
**And** 系统应支持按用户名或邮箱/手机号搜索
**And** 系统应支持按角色筛选（普通用户、管理员）
**And** 系统应支持按状态筛选（正常、禁用、未验证）
**And** 系统应支持分页显示

**Given** 管理员在"用户管理"页面
**When** 管理员点击某个用户的"查看详情"按钮
**Then** 系统应显示该用户的完整信息（个人资料、登录历史、操作日志）

**Given** 管理员在用户详情页面
**When** 管理员修改用户角色
**And** 管理员点击"保存"按钮
**Then** 系统应更新用户角色
**And** 系统应记录操作日志
**And** 系统应显示"角色更新成功"的成功提示

**Given** 管理员在用户详情页面
**When** 管理员点击"禁用用户"按钮
**And** 管理员确认禁用操作
**Then** 系统应禁用该用户账户
**And** 系统应记录操作日志
**And** 系统应显示"用户已禁用"的成功提示

**Given** 用户账户被禁用
**When** 该用户尝试登录
**Then** 系统应显示"您的账户已被禁用，请联系管理员"的错误提示

**Given** 管理员在用户详情页面
**When** 管理员点击"删除用户"按钮
**And** 管理员确认删除操作
**Then** 系统应软删除该用户账户（标记为已删除而非物理删除）
**And** 系统应记录操作日志
**And** 系统应显示"用户已删除"的成功提示

---

**Epic 1 Summary:**
- 6 stories created
- FRs covered: FR1-FR5, FR28
- NFRs covered: NFR9-NFR18 (Security Requirements)
- ARs covered: AR7, AR8, AR11, AR27, AR29-AR32
- UX-DRs covered: UX-DR23-UX-DR28, UX-DR38, UX-DR40

---

## Epic 2: 学生信息档案

**Epic Goal:** 学生和家长能够录入和管理学生的基本信息、学业成绩和地区信息

**FRs Covered:** FR6-FR8, NFR1-NFR8, AR1-AR6, AR13-AR18, AR23-AR28, AR45-AR48, UX-DR23-UX-DR28, UX-DR38

### Story 2.1: 学生基本信息录入

作为一个已登录的用户，
我希望能够录入学生的基本信息（姓名、性别、出生日期），
以便建立学生的基础档案。

**Acceptance Criteria:**

**Given** 用户已登录
**When** 用户导航到"学生信息"页面
**Then** 系统应显示学生基本信息表单
**And** 表单应包含必填字段：姓名（文本）、性别（单选）、出生日期（日期选择器）
**And** 所有表单字段应满足WCAG 2.1 AA可访问性标准（ARIA标签、键盘导航、44x44像素触摸目标）

**Given** 用户在学生基本信息表单
**When** 用户输入有效的学生姓名（2-20个字符）
**And** 用户选择性别（男/女）
**And** 用户选择出生日期（日期格式YYYY-MM-DD）
**And** 用户点击"保存"按钮
**Then** 系统应验证所有必填字段是否已填写
**And** 系统应验证数据格式是否正确
**And** 系统应保存学生基本信息到数据库
**And** 系统应显示"保存成功"的成功提示（绿色对勾和消息）
**And** 表单应显示保存后的数据

**Given** 用户在学生基本信息表单
**When** 用户输入姓名超过20个字符
**Then** 系统应显示实时验证错误："姓名不能超过20个字符"

**Given** 用户在学生基本信息表单
**When** 用户选择未来的出生日期
**Then** 系统应显示实时验证错误："出生日期不能晚于今天"

**Given** 用户在学生基本信息表单
**When** 用户未填写必填字段并点击"保存"
**Then** 系统应显示实时验证错误："该字段为必填项"
**And** "保存"按钮应保持禁用状态

**Given** 用户在学生基本信息表单
**When** 网络连接失败
**Then** 系统应显示"网络错误，请检查连接后重试"的错误提示
**And** 表单应保留用户输入的数据

**Given** 用户在学生基本信息表单
**When** 用户使用键盘导航
**Then** 所有表单元素应可通过Tab键访问
**And** 焦点指示器应清晰可见（对比度符合WCAG 2.1 AA标准）

### Story 2.2: 学业成绩录入

作为一个已登录的用户，
我希望能够录入学生的各科成绩，
以便系统进行录取预测和学校推荐。

**Acceptance Criteria:**

**Given** 用户已登录
**When** 用户导航到"学生信息"页面
**And** 用户点击"学业成绩"标签
**Then** 系统应显示学业成绩录入表单
**And** 表单应包含各科成绩字段（语文、数学、英语、物理、化学、政治、历史、地理、生物）
**And** 每个成绩字段应使用数字输入框
**And** 所有表单字段应满足WCAG 2.1 AA可访问性标准

**Given** 用户在学业成绩表单
**When** 用户输入各科有效成绩（0-150分）
**And** 用户点击"保存"按钮
**Then** 系统应验证所有成绩是否在有效范围内
**And** 系统应计算总分和平均分
**And** 系统应保存学业成绩到数据库
**And** 系统应显示"保存成功"的成功提示
**And** 表单应显示总分和平均分统计信息

**Given** 用户在学业成绩表单
**When** 用户输入成绩超过150分
**Then** 系统应显示实时验证错误："成绩不能超过150分"

**Given** 用户在学业成绩表单
**When** 用户输入负数成绩
**Then** 系统应显示实时验证错误："成绩不能为负数"

**Given** 用户在学业成绩表单
**When** 用户输入非数字字符
**Then** 系统应显示实时验证错误："请输入有效的数字"

**Given** 用户在学业成绩表单
**When** 用户点击"自动计算总分"按钮
**Then** 系统应计算并显示当前总分
**And** 系统应计算并显示当前平均分
**And** 系统应高亮显示显示总分和平均分的区域

**Given** 用户在学业成绩表单
**When** 用户清空所有成绩
**And** 用户点击"保存"按钮
**Then** 系统应显示"请至少输入一门课程成绩"的错误提示

**Given** 用户在学业成绩表单
**When** 用户使用屏幕阅读器
**Then** 所有成绩输入框应有明确的ARIA标签（如"语文成绩输入框"）
**And** 验证错误消息应通过ARIA live region动态播报

### Story 2.3: 地区信息录入

作为一个已登录的用户，
我希望能够录入学生的地区信息（城市、区县、学校），
以便系统提供地区相关的学校推荐和政策信息。

**Acceptance Criteria:**

**Given** 用户已登录
**When** 用户导航到"学生信息"页面
**And** 用户点击"地区信息"标签
**Then** 系统应显示地区信息录入表单
**And** 表单应包含级联选择器：城市、区县、学校
**And** 城市选择器应显示所有支持的城市列表
**And** 所有表单字段应满足WCAG 2.1 AA可访问性标准

**Given** 用户在地区信息表单
**When** 用户选择城市
**Then** 系统应根据所选城市加载对应的区县列表
**And** 区县选择器应显示该城市下的所有区县

**Given** 用户在地区信息表单
**When** 用户选择区县
**Then** 系统应根据所选区县加载对应的学校列表
**And** 学校选择器应显示该区县下的所有学校

**Given** 用户在地区信息表单
**When** 用户完成城市、区县、学校的选择
**And** 用户点击"保存"按钮
**Then** 系统应验证所有必填字段是否已选择
**And** 系统应保存地区信息到数据库
**And** 系统应显示"保存成功"的成功提示
**And** 表单应显示保存后的数据

**Given** 用户在地区信息表单
**When** 用户选择城市后改变主意，重新选择另一个城市
**Then** 系统应清空区县和学校选择器的值
**And** 系统应加载新城市的区县列表

**Given** 用户在地区信息表单
**When** 用户选择区县后改变主意，重新选择另一个区县
**Then** 系统应清空学校选择器的值
**And** 系统应加载新区县的学校列表

**Given** 用户在地区信息表单
**When** 系统加载城市、区县或学校列表
**Then** 系统应显示加载状态（spinner）
**And** 加载时间应少于3秒（符合NFR7性能要求）

**Given** 用户在地区信息表单
**When** 系统加载城市、区县或学校列表失败
**Then** 系统应显示"加载失败，请稍后重试"的错误提示
**And** 系统应提供"重试"按钮

**Given** 用户在地区信息表单
**When** 用户使用键盘导航级联选择器
**Then** 所有选择器选项应可通过上下箭头键选择
**And** 焦点指示器应清晰可见
**And** Enter键应确认选择

**Given** 用户在地区信息表单
**When** 用户使用移动设备
**Then** 所有选择器应支持触摸操作
**And** 触摸目标尺寸应至少为44x44像素（符合UX-DR23）

---

**Epic 2 Summary:**
- 3 stories created
- FRs covered: FR6-FR8
- NFRs covered: NFR1-NFR8 (Performance Requirements)
- ARs covered: AR1-AR6, AR13-AR18, AR23-AR28, AR45-AR48
- UX-DRs covered: UX-DR23-UX-DR28, UX-DR38

---

## Epic 3: 学校信息浏览与搜索

**Epic Goal:** 用户能够浏览、搜索和筛选学校信息，管理员能够管理学校数据

**FRs Covered:** FR9-FR15, AR32, UX-DR9, UX-DR29-UX-DR37, UX-DR40-UX-DR44

### Story 3.1: 学校列表查看

作为一个已登录的用户，
我希望能够查看学校信息列表并支持分页和排序，
以便浏览所有可用的学校。

**Acceptance Criteria:**

**Given** 用户已登录
**When** 用户导航到"学校"页面
**Then** 系统应显示学校列表
**And** 每个学校卡片应显示：学校名称、所在地区、录取分数线、录取概率（如已录入成绩）
**And** 列表应支持分页显示（每页20所学校）
**And** 系统应显示总学校数量和当前页码
**And** 列表应支持下拉刷新（pull-to-refresh）

**Given** 用户在学校列表页面
**When** 用户点击排序按钮
**Then** 系统应显示排序选项：按分数线升序、按分数线降序、按录取概率升序、按录取概率降序

**Given** 用户在学校列表页面
**When** 用户选择"按分数线升序"排序
**Then** 系统应重新加载学校列表并按分数线从低到高排序

**Given** 用户在学校列表页面
**When** 用户点击分页按钮（下一页/上一页）
**Then** 系统应加载对应页的学校列表
**And** 分页加载时间应少于500毫秒（符合NFR8）

**Given** 用户在学校列表页面
**When** 用户向下滚动到列表底部
**Then** 系统应自动加载下一页（无限滚动）

**Given** 用户在学校列表页面
**When** 用户点击某个学校卡片
**Then** 系统应跳转到该学校的详情页面

**Given** 用户在学校列表页面
**When** 用户使用屏幕阅读器
**Then** 每个学校卡片应有明确的ARIA标签
**And** 排序和分页控件应可通过键盘导航

### Story 3.2: 学校详情查看

作为一个已登录的用户，
我希望能够查看学校的详细信息，
以便全面了解学校情况。

**Acceptance Criteria:**

**Given** 用户在学校列表页面
**When** 用户点击某个学校卡片
**Then** 系统应跳转到学校详情页面

**Given** 用户在学校详情页面
**Then** 系统应显示学校的完整信息：
- 学校名称
- 学校类型（重点高中、普通高中、职业高中）
- 所在地区（城市、区县）
- 历年录取分数线（近3年）
- 学校简介
- 学校特色
- 招生人数
- 联系方式
- 学校地址

**Given** 用户在学校详情页面
**And** 用户已录入学生成绩
**Then** 系统应显示录取概率卡片（使用SchoolRecommendCard组件）
**And** 卡片应显示录取概率百分比和趋势图表

**Given** 用户在学校详情页面
**When** 用户点击"添加到志愿"按钮
**Then** 系统应将该学校添加到志愿列表
**And** 系统应显示"已添加到志愿列表"的成功提示（toast通知）

**Given** 用户在学校详情页面
**When** 用户点击"返回"按钮
**Then** 系统应返回到学校列表页面
**And** 应保留用户的排序和筛选状态

**Given** 用户在学校详情页面
**When** 用户使用移动设备
**Then** 页面应支持左右滑动返回手势
**And** 所有交互元素触摸目标尺寸应至少为44x44像素

### Story 3.3: 学校搜索

作为一个已登录的用户，
我希望能够按学校名称或关键词搜索学校，
以便快速找到目标学校。

**Acceptance Criteria:**

**Given** 用户在学校列表页面
**When** 用户点击搜索框
**Then** 系统应显示搜索框并聚焦
**And** 搜索框应显示占位符文本："请输入学校名称或关键词"

**Given** 用户在搜索框
**When** 用户输入搜索关键词（如"实验"）
**And** 系统完成输入后（500毫秒延迟）
**Then** 系统应执行搜索并显示匹配的学校列表
**And** 搜索响应时间应少于3秒（符合NFR7）

**Given** 用户在搜索框
**When** 用户输入的搜索关键词匹配多个学校
**Then** 系统应显示所有匹配的学校
**And** 应高亮显示匹配的关键词

**Given** 用户在搜索框
**When** 用户输入的搜索关键词没有匹配的学校
**Then** 系统应显示空状态
**And** 空状态应显示插图和"未找到匹配的学校"提示
**And** 空状态应提供"查看所有学校"的链接

**Given** 用户在搜索框
**When** 用户点击搜索框的清除按钮
**Then** 系统应清空搜索关键词
**And** 系统应显示所有学校列表

**Given** 用户在搜索框
**When** 用户点击搜索框外的区域
**Then** 搜索框应失去焦点
**And** 搜索结果应保留显示

**Given** 用户在搜索框
**When** 用户使用键盘导航
**Then** 搜索框应可通过Tab键访问
**And** Enter键应提交搜索
**And** Esc键应清除搜索

### Story 3.4: 学校筛选

作为一个已登录的用户，
我希望能够按地区、学校类型和分数线范围筛选学校，
以便快速找到符合条件的学校。

**Acceptance Criteria:**

**Given** 用户在学校列表页面
**When** 用户点击"筛选"按钮
**Then** 系统应显示筛选面板
**And** 筛选面板应包含：地区选择器、学校类型选择器、分数线范围选择器

**Given** 用户在筛选面板
**When** 用户选择地区（如"北京市-海淀区"）
**Then** 系统应根据所选地区筛选学校列表
**And** 筛选响应时间应少于3秒

**Given** 用户在筛选面板
**When** 用户选择学校类型（如"重点高中"）
**Then** 系统应根据所选学校类型筛选学校列表

**Given** 用户在筛选面板
**When** 用户设置分数线范围（如"600-650"）
**Then** 系统应根据所选分数线范围筛选学校列表
**And** 应显示录取分数线在范围内的学校

**Given** 用户在筛选面板
**When** 用户同时选择地区、学校类型和分数线范围
**Then** 系统应根据所有筛选条件过滤学校列表
**And** 应显示满足所有条件的学校

**Given** 用户在筛选面板
**When** 筛选结果为空
**Then** 系统应显示空状态
**And** 空状态应显示"没有符合条件的学校"提示
**And** 空状态应提供"清除筛选"的按钮

**Given** 用户在筛选面板
**When** 用户点击"重置"按钮
**Then** 系统应清除所有筛选条件
**And** 系统应显示所有学校列表

**Given** 用户在筛选面板
**When** 用户点击"应用"按钮
**Then** 系统应应用筛选条件并关闭筛选面板
**And** 学校列表应更新为筛选结果

**Given** 用户在学校列表页面
**And** 已应用筛选条件
**Then** 系统应在"筛选"按钮上显示筛选标签（如"地区:海淀"）

### Story 3.5: 学校数据导入（管理员功能）

作为一个系统管理员，
我希望能够批量导入学校数据，
以便快速更新学校信息。

**Acceptance Criteria:**

**Given** 管理员已登录
**When** 管理员导航到"学校管理"页面
**Then** 系统应显示学校管理面板
**And** 面板应包含"导入学校数据"按钮

**Given** 管理员在"学校管理"页面
**When** 管理员点击"导入学校数据"按钮
**Then** 系统应显示导入对话框
**And** 对话框应包含文件上传区域（支持Excel和CSV格式）
**And** 对话框应显示模板下载链接

**Given** 管理员在导入对话框
**When** 管理员点击"下载模板"链接
**Then** 系统应下载学校数据导入模板
**And** 模板应包含所有必需的字段（学校名称、类型、地区、分数线等）

**Given** 管理员在导入对话框
**When** 管理员选择一个有效的Excel/CSV文件
**And** 管理员点击"开始导入"按钮
**Then** 系统应验证文件格式
**And** 系统应解析文件内容
**And** 系统应验证数据完整性
**And** 系统应开始导入数据
**And** 系统应显示导入进度（百分比）

**Given** 管理员在导入对话框
**When** 导入成功完成
**Then** 系统应显示"成功导入X所学校"的成功提示
**And** 系统应自动关闭导入对话框
**And** 学校列表应更新

**Given** 管理员在导入对话框
**When** 导入过程中出现错误（如数据格式不正确）
**Then** 系统应暂停导入
**And** 系统应显示详细的错误信息（如"第5行：分数线格式不正确"）
**And** 系统应显示"导入失败，请检查数据格式"的错误提示

**Given** 管理员在导入对话框
**When** 管理员上传的文件格式不支持
**Then** 系统应显示"仅支持Excel和CSV格式"的错误提示
**And** "开始导入"按钮应保持禁用状态

**Given** 管理员在导入对话框
**When** 导入过程中文件包含已存在的学校（根据学校名称判断）
**Then** 系统应显示确认对话框："检测到X所学校已存在，是否覆盖？"
**And** 管理员确认后，系统应更新已存在的学校数据
**And** 管理员取消后，系统应跳过已存在的学校

### Story 3.6: 学校数据导出（管理员功能）

作为一个系统管理员，
我希望能够导出学校数据，
以便进行数据备份或离线分析。

**Acceptance Criteria:**

**Given** 管理员已登录
**When** 管理员导航到"学校管理"页面
**Then** 系统应显示学校管理面板
**And** 面板应包含"导出学校数据"按钮

**Given** 管理员在"学校管理"页面
**When** 管理员点击"导出学校数据"按钮
**Then** 系统应显示导出选项对话框
**And** 对话框应包含：导出格式选择（Excel、CSV）、导出范围选择（全部、当前筛选结果、选中的学校）

**Given** 管理员在导出选项对话框
**When** 管理员选择"全部"范围
**And** 管理员选择"Excel"格式
**And** 管理员点击"开始导出"按钮
**Then** 系统应查询所有学校数据
**And** 系统应生成Excel文件
**And** 系统应触发文件下载
**And** 系统应显示"导出成功"的成功提示

**Given** 管理员在导出选项对话框
**When** 管理员选择"当前筛选结果"范围
**And** 管理员点击"开始导出"按钮
**Then** 系统应导出当前筛选的学校列表

**Given** 管理员在导出选项对话框
**When** 管理员选择"选中的学校"范围
**And** 管理员点击"开始导出"按钮
**Then** 系统应导出管理员在列表中选中的学校

**Given** 管理员在导出选项对话框
**When** 导出过程中出现错误（如网络故障）
**Then** 系统应显示"导出失败，请稍后重试"的错误提示
**And** 系统应提供"重试"按钮

**Given** 管理员在导出选项对话框
**When** 导出的学校数量较大（超过1000所）
**Then** 系统应显示"导出可能需要较长时间，请勿关闭页面"的提示
**And** 系统应显示导出进度

### Story 3.7: 学校信息编辑（管理员功能）

作为一个系统管理员，
我希望能够编辑学校信息，
以便更新学校数据。

**Acceptance Criteria:**

**Given** 管理员已登录
**When** 管理员导航到"学校管理"页面
**Then** 系统应显示学校列表
**And** 每个学校卡片应包含"编辑"按钮

**Given** 管理员在"学校管理"页面
**When** 管理员点击某个学校的"编辑"按钮
**Then** 系统应显示学校编辑表单
**And** 表单应预填充该学校的现有数据

**Given** 管理员在学校编辑表单
**Then** 表单应包含所有可编辑字段：
- 学校名称（必填）
- 学校类型（必填）
- 所在地区（必填）
- 历年录取分数线（近3年，必填）
- 学校简介（选填）
- 学校特色（选填）
- 招生人数（必填）
- 联系方式（选填）
- 学校地址（必填）

**Given** 管理员在学校编辑表单
**When** 管理员修改学校名称
**And** 管理员点击"保存"按钮
**Then** 系统应验证学校名称是否已被其他学校使用
**And** 系统应更新学校信息
**And** 系统应显示"学校信息更新成功"的成功提示
**And** 系统应记录操作日志

**Given** 管理员在学校编辑表单
**When** 管理员修改录取分数线
**And** 管理员点击"保存"按钮
**Then** 系统应验证分数线格式（应为数字）
**And** 系统应更新学校信息
**And** 系统应更新相关用户的录取概率计算

**Given** 管理员在学校编辑表单
**When** 管理员输入无效的分数线（如非数字或负数）
**Then** 系统应显示实时验证错误："分数线必须为有效数字"

**Given** 管理员在学校编辑表单
**When** 管理员未填写必填字段
**Then** 系统应显示实时验证错误："该字段为必填项"
**And** "保存"按钮应保持禁用状态

**Given** 管理员在学校编辑表单
**When** 管理员点击"取消"按钮
**Then** 系统应关闭编辑表单
**And** 系统应保留学校的原始数据

**Given** 管理员在学校编辑表单
**When** 管理员点击"删除学校"按钮
**Then** 系统应显示确认对话框："确定要删除该学校吗？此操作不可恢复"
**And** 管理员确认后，系统应软删除该学校（标记为已删除）
**And** 系统应记录操作日志
**And** 系统应显示"学校已删除"的成功提示

---

**Epic 3 Summary:**
- 7 stories created
- FRs covered: FR9-FR15
- ARs covered: AR32
- UX-DRs covered: UX-DR9, UX-DR29-UX-DR37, UX-DR40-UX-DR44

---

## Epic 4: 志愿填报与模拟

**Epic Goal:** 学生和家长能够创建和管理志愿填报列表，进行志愿模拟，并查看历史记录

**FRs Covered:** FR16-FR19, FR22-FR23, UX-DR10, UX-DR13, UX-DR15, UX-DR16, UX-DR39, UX-DR40

### Story 4.1: 创建志愿填报表单

作为一个已登录的用户，
我希望能够创建志愿填报表单，
以便开始填报志愿。

**Acceptance Criteria:**

**Given** 用户已登录
**And** 用户已完成学生基本信息录入
**When** 用户导航到"志愿填报"页面
**Then** 系统应显示志愿填报页面
**And** 页面应显示当前年份（如2025年中考志愿填报）
**And** 页面应显示志愿填报提示："请根据学生成绩和学校分数线合理填报志愿"

**Given** 用户在"志愿填报"页面
**Then** 页面应显示志愿列表（最多8个志愿）
**And** 每个志愿位置应显示：志愿序号（第一志愿、第二志愿等）、学校名称（空状态显示"未选择"）、录取概率（空状态显示"-"）

**Given** 用户在"志愿填报"页面
**Then** 页面应显示"添加志愿"按钮
**And** 页面应显示"保存草稿"按钮
**And** 页面应显示"提交志愿"按钮（初始禁用状态）

**Given** 用户在"志愿填报"页面
**And** 用户未录入学生成绩
**Then** 系统应显示提示："请先录入学生成绩"
**And** "添加志愿"按钮应保持禁用状态
**And** 提示应提供"跳转到学生信息"的链接

**Given** 用户在"志愿填报"页面
**When** 用户点击"添加志愿"按钮
**Then** 系统应显示学校选择对话框
**And** 对话框应显示学校列表
**And** 对话框应支持搜索和筛选功能

**Given** 用户在"志愿填报"页面
**When** 用户使用移动设备
**Then** 志愿列表应支持垂直滚动
**And** 所有交互元素触摸目标尺寸应至少为44x44像素

### Story 4.2: 添加学校到志愿

作为一个已登录的用户，
我希望能够从学校列表中选择学校并添加到志愿，
以便完成志愿填报。

**Acceptance Criteria:**

**Given** 用户在"志愿填报"页面
**When** 用户点击"添加志愿"按钮
**Then** 系统应显示学校选择对话框
**And** 对话框应显示所有可选择的学校
**And** 已添加到志愿的学校应标记为"已选择"

**Given** 用户在学校选择对话框
**When** 用户点击某个学校
**Then** 系统应显示该学校的详细信息
**And** 信息应包含：学校名称、录取分数线、录取概率

**Given** 用户在学校选择对话框
**And** 用户选择了某个学校
**When** 用户点击"确认添加"按钮
**Then** 系统应将该学校添加到志愿列表的第一个空位置
**And** 系统应显示"添加成功"的成功提示
**And** 系统应自动关闭学校选择对话框

**Given** 用户在学校选择对话框
**When** 用户选择已添加到志愿的学校
**Then** 系统应显示提示："该学校已在志愿列表中"
**And** "确认添加"按钮应保持禁用状态

**Given** 用户在"志愿填报"页面
**When** 志愿列表已满（8个志愿）
**Then** "添加志愿"按钮应保持禁用状态
**And** 系统应显示提示："志愿列表已满，最多填报8所学校"

**Given** 用户在"志愿填报"页面
**When** 用户添加学校到志愿
**Then** 系统应显示该学校的录取概率
**And** 概率应使用颜色标识：高（绿色，>70%）、中（黄色，40%-70%）、低（红色，<40%）

**Given** 用户在"志愿填报"页面
**When** 用户添加学校到志愿后
**Then** 系统应自动保存草稿
**And** 系统应显示"已自动保存"的提示

### Story 4.3: 调整志愿顺序（拖拽排序）

作为一个已登录的用户，
我希望能够通过拖拽调整志愿顺序，
以便优化志愿填报策略。

**Acceptance Criteria:**

**Given** 用户在"志愿填报"页面
**And** 志愿列表中有多个学校
**Then** 每个志愿卡片应显示拖拽手柄图标
**And** 拖拽手柄应可通过触摸或鼠标操作

**Given** 用户在"志愿填报"页面
**When** 用户长按某个志愿卡片（移动设备）
**Then** 志愿卡片应进入拖拽状态
**And** 卡片应略微放大并显示阴影

**Given** 用户在"志愿填报"页面
**When** 用户拖拽志愿卡片到新位置
**Then** 系统应显示目标位置的占位符
**And** 其他志愿卡片应自动调整位置

**Given** 用户在"志愿填报"页面
**When** 用户释放拖拽的志愿卡片
**Then** 系统应更新志愿顺序
**And** 志愿序号应重新计算（第一志愿、第二志愿等）
**And** 系统应显示"顺序已更新"的成功提示

**Given** 用户在"志愿填报"页面
**When** 用户调整志愿顺序后
**Then** 系统应重新计算录取概率
**And** 系统应更新每个志愿的录取概率显示

**Given** 用户在"志愿填报"页面
**When** 用户使用键盘导航
**Then** 志愿卡片应可通过Tab键访问
**And** 拖拽手柄应可通过箭头键移动
**And** Enter键应确认位置调整

**Given** 用户在"志愿填报"页面
**When** 用户拖拽志愿卡片到无效位置（如拖到志愿列表外）
**Then** 系统应取消拖拽操作
**And** 志愿列表应恢复到拖拽前的状态

### Story 4.4: 删除志愿

作为一个已登录的用户，
我希望能够删除志愿列表中的学校，
以便调整志愿填报策略。

**Acceptance Criteria:**

**Given** 用户在"志愿填报"页面
**And** 志愿列表中有学校
**Then** 每个志愿卡片应显示删除按钮
**And** 删除按钮应使用垃圾桶图标

**Given** 用户在"志愿填报"页面
**When** 用户点击某个志愿的删除按钮
**Then** 系统应显示确认对话框："确定要删除该志愿吗？"
**And** 对话框应显示要删除的学校名称

**Given** 用户在确认对话框
**When** 用户点击"确认删除"按钮
**Then** 系统应从志愿列表中删除该学校
**And** 系统应自动调整后续志愿的顺序
**And** 系统应显示"删除成功"的成功提示

**Given** 用户在确认对话框
**When** 用户点击"取消"按钮
**Then** 系统应关闭确认对话框
**And** 志愿列表应保持不变

**Given** 用户在"志愿填报"页面
**When** 用户删除某个志愿
**Then** 系统应自动保存草稿
**And** 后续志愿的序号应自动更新

**Given** 用户在"志愿填报"页面
**And** 志愿列表中只有一个学校
**When** 用户删除该志愿
**Then** 志愿列表应为空
**And** "提交志愿"按钮应保持禁用状态

**Given** 用户在"志愿填报"页面
**When** 用户删除志愿后
**Then** 系统应显示"添加志愿"按钮（启用状态）
**And** 系统应显示提示："您还可以添加X所学校"

### Story 4.5: 志愿模拟填报

作为一个已登录的用户，
我希望能够进行志愿模拟填报，
以便了解不同志愿组合的录取可能性。

**Acceptance Criteria:**

**Given** 用户在"志愿填报"页面
**When** 用户点击"模拟填报"按钮
**Then** 系统应显示模拟填报模式
**And** 页面应显示"模拟填报模式"标签
**And** 模拟填报的修改不会影响正式志愿

**Given** 用户在模拟填报模式
**When** 用户添加、删除或调整志愿
**Then** 系统应实时显示录取预测结果
**And** 系统应显示"至少被一所学校录取的概率：XX%"
**And** 系统应显示每个志愿的录取概率

**Given** 用户在模拟填报模式
**When** 用户修改志愿列表
**Then** 系统应显示录取预测对比图表
**And** 图表应显示修改前后的录取概率对比

**Given** 用户在模拟填报模式
**When** 用户点击"应用此方案"按钮
**Then** 系统应显示确认对话框："确定要应用此方案到正式志愿吗？"
**And** 用户确认后，系统应将模拟方案应用到正式志愿

**Given** 用户在模拟填报模式
**When** 用户点击"取消模拟"按钮
**Then** 系统应退出模拟填报模式
**And** 志愿列表应恢复到模拟前的状态

**Given** 用户在模拟填报模式
**When** 用户使用移动设备
**Then** 模拟填报模式应支持竖屏显示
**And** 录取预测图表应支持缩放和拖拽查看

**Given** 用户在模拟填报模式
**When** 系统计算录取预测
**Then** 系统应显示计算进度（spinner）
**And** 计算时间应少于2秒（符合NFR7）

### Story 4.6: 查看历史填报记录

作为一个已登录的用户，
我希望能够查看历史填报记录，
以便回顾之前的志愿填报情况。

**Acceptance Criteria:**

**Given** 用户已登录
**When** 用户导航到"志愿填报"页面
**And** 用户点击"历史记录"标签
**Then** 系统应显示历史填报记录列表
**And** 列表应按时间倒序排列

**Given** 用户在历史记录页面
**Then** 每条历史记录应显示：填报日期、填报年份、志愿数量、录取状态

**Given** 用户在历史记录页面
**When** 用户点击某条历史记录
**Then** 系统应显示该次填报的详细信息
**And** 详细信息应包含：志愿列表（学校名称、志愿序号）、录取结果

**Given** 用户在历史记录详情页面
**When** 用户点击"复制到当前志愿"按钮
**Then** 系统应显示确认对话框："确定要将此历史志愿复制到当前志愿吗？"
**And** 用户确认后，系统应将历史志愿复制到当前志愿
**And** 系统应显示"复制成功"的成功提示

**Given** 用户在历史记录详情页面
**When** 用户点击"导出PDF"按钮
**Then** 系统应生成PDF文件
**And** PDF应包含志愿列表和录取结果
**And** 系统应触发文件下载

**Given** 用户在历史记录页面
**When** 历史记录为空
**Then** 系统应显示空状态
**And** 空状态应显示插图和"暂无历史记录"提示

**Given** 用户在历史记录页面
**When** 用户使用筛选器（按年份、按录取状态）
**Then** 系统应根据筛选条件过滤历史记录
**And** 系统应更新显示结果

---

**Epic 4 Summary:**
- 6 stories created
- FRs covered: FR16-FR19, FR22-FR23
- UX-DRs covered: UX-DR10, UX-DR13, UX-DR15, UX-DR16, UX-DR39, UX-DR40

---

## Epic 5: 录取预测与智能推荐

**Epic Goal:** 学生和家长能够获得基于数据的录取概率预测和智能学校推荐，提高志愿填报的准确性和成功率

**FRs Covered:** FR20-FR21, NFR2, NFR7, AR45-AR48, UX-DR11, UX-DR12, UX-DR15, UX-DR29-UX-DR35

### Story 5.1: 录取概率计算

作为一个已登录的用户，
我希望系统能够根据我的学生成绩和历史录取数据计算每所学校的录取概率，
以便做出更明智的志愿填报决策。

**Acceptance Criteria:**

**Given** 用户已登录
**And** 用户已完成学生基本信息录入（包括各科成绩）
**And** 系统已加载学校历史录取数据

**When** 用户在学校列表或详情页面查看学校信息
**Then** 系统应显示该学校的录取概率（百分比）
**And** 概率计算应基于：
  - 学生总分与学校近3年录取分数线的对比
  - 学生各科成绩与学校要求的对比
  - 学校录取人数与申请人数的比例
  - 往年同分数段学生的录取情况

**Given** 用户已查看学校录取概率
**When** 学生的成绩发生变化（如修改某科成绩）
**Then** 系统应重新计算所有学校的录取概率
**And** 更新后的概率应在3秒内显示

**Given** 系统显示录取概率
**When** 概率高于80%
**Then** 系统应使用绿色高亮显示
**And** 显示"录取概率较高"提示

**Given** 系统显示录取概率
**When** 概率在50%-80%之间
**Then** 系统应使用黄色显示
**And** 显示"录取概率中等"提示

**Given** 系统显示录取概率
**When** 概率低于50%
**Then** 系统应使用红色显示
**And** 显示"录取概率较低"提示

**Given** 系统计算录取概率
**When** 学校历史录取数据不足（少于2年）
**Then** 系统应显示"数据不足"提示
**And** 建议用户咨询学校招生办

**Given** 系统显示录取概率
**When** 用户点击概率数字
**Then** 系统应弹出概率计算详情面板
**And** 面板应显示：
  - 学生总分
  - 学校近3年录取分数线
  - 录取人数与申请人数比例
  - 同分数段录取率

**Performance Requirements:**
- 概率计算应在2秒内完成
- 概率更新响应时间少于3秒

**Accessibility Requirements:**
- 概率百分比使用ARIA标签和角色属性
- 概率高亮颜色应符合WCAG 2.1 AA对比度标准
- 概率详情面板支持键盘导航和屏幕阅读器
- 所有交互元素最小44x44像素

**Error Handling:**
- 如果历史录取数据加载失败，显示"暂时无法计算录取概率"错误消息
- 如果成绩数据不完整，提示用户完善成绩信息
- 如果计算服务不可用，显示"录取概率计算服务暂时不可用"并提供重试按钮

---

### Story 5.2: 智能学校推荐

作为一个已登录的用户，
我希望系统能够根据我的成绩、偏好和历史数据智能推荐适合的学校，
以便快速找到最合适的志愿学校。

**Acceptance Criteria:**

**Given** 用户已登录
**And** 用户已完成学生基本信息录入（包括各科成绩和偏好设置）

**When** 用户导航到"智能推荐"页面
**Then** 系统应显示推荐结果列表
**And** 列表应显示：
  - 学校名称和所在地区
  - 录取概率（高/中/低）
  - 学校特色标签（如"重点中学"、"艺术特色"等）
  - 与用户偏好的匹配度分数
**And** 推荐结果应按录取概率和匹配度综合排序

**Given** 用户在智能推荐页面
**When** 用户首次进入页面
**Then** 系统应显示"推荐设置"对话框
**And** 对话框应允许用户设置：
  - 希望的地区（多选）
  - 学校类型（公办/民办）
  - 学校等级（重点/普通）
  - 特色偏好（如艺术、体育、科技等）
  - 志愿数量（1-8个）

**Given** 用户设置推荐偏好
**When** 用户点击"生成推荐"按钮
**Then** 系统应根据偏好生成推荐列表
**And** 推荐数量应与用户设置的志愿数量匹配
**And** 推荐生成时间应在3秒内完成

**Given** 系统显示推荐结果
**When** 推荐结果包含用户已添加到志愿的学校
**Then** 系统应标记为"已添加"
**And** 显示该学校在志愿列表中的位置

**Given** 系统显示推荐结果
**When** 推荐结果中没有适合的学校（所有概率低于30%）
**Then** 系统应显示"未找到合适的推荐学校"提示
**And** 建议用户：
  - 调整地区偏好
  - 考虑民办学校
  - 降低学校等级要求

**Given** 用户查看推荐结果
**When** 用户点击某个学校的"添加到志愿"按钮
**Then** 系统应将该学校添加到志愿列表
**And** 按钮状态应变为"已添加"
**And** 显示"成功添加到志愿列表"成功消息

**Given** 用户在智能推荐页面
**When** 用户点击"查看全部推荐"按钮
**Then** 系统应显示所有符合条件的学校（不限数量）
**And** 提供排序选项：按录取概率、按匹配度、按地区

**Given** 用户查看推荐结果
**When** 用户点击某个推荐学校
**Then** 系统应导航到学校详情页面
**And** 详情页面应高亮显示该学校的推荐理由

**Performance Requirements:**
- 推荐生成时间少于3秒
- 推荐结果列表加载时间少于2秒

**Accessibility Requirements:**
- 推荐结果卡片支持键盘导航
- 推荐标签使用ARIA属性描述
- "添加到志愿"按钮最小44x44像素
- 推荐设置表单支持屏幕阅读器

**Error Handling:**
- 如果推荐服务不可用，显示"智能推荐服务暂时不可用"并提供重试按钮
- 如果用户未设置偏好，显示"请先设置推荐偏好"提示
- 如果推荐生成失败，显示"推荐生成失败，请重试"错误消息

---

### Story 5.3: 推荐结果筛选和排序

作为一个使用智能推荐的用户，
我希望能够对推荐结果进行筛选和排序，
以便快速找到最符合我需求的学校。

**Acceptance Criteria:**

**Given** 用户在智能推荐页面
**And** 系统已显示推荐结果列表

**When** 用户点击"筛选"按钮
**Then** 系统应显示筛选面板
**And** 筛选选项应包括：
  - 地区筛选（单选或多选）
  - 学校类型（公办/民办）
  - 学校等级（重点/普通）
  - 录取概率范围（如>80%, 50-80%, <50%）
  - 学校特色标签（如艺术、体育、科技等）

**Given** 用户打开筛选面板
**When** 用户选择一个或多个筛选条件
**Then** 系统应实时过滤推荐结果
**And** 显示符合所有筛选条件的学校
**And** 显示筛选后的学校数量

**Given** 用户已设置筛选条件
**When** 用户点击"重置筛选"按钮
**Then** 系统应清除所有筛选条件
**And** 显示所有推荐结果

**Given** 用户在智能推荐页面
**When** 用户点击"排序"按钮
**Then** 系统应显示排序选项
**And** 排序选项应包括：
  - 按录取概率（从高到低）
  - 按录取概率（从低到高）
  - 按匹配度（从高到低）
  - 按地区（A-Z）
  - 按录取分数线（从低到高）

**Given** 用户选择一个排序选项
**When** 排序选项发生变化
**Then** 系统应重新排序推荐结果
**And** 保留当前筛选条件
**And** 更新排序按钮显示当前排序方式

**Given** 用户同时使用筛选和排序
**When** 用户设置筛选条件并选择排序方式
**Then** 系统应先应用筛选，再对筛选结果排序
**And** 显示符合筛选条件的学校，按指定方式排序

**Given** 筛选结果为空
**When** 用户设置的筛选条件过于严格
**Then** 系统应显示"没有符合筛选条件的学校"提示
**And** 建议"放宽筛选条件以查看更多学校"

**Performance Requirements:**
- 筛选操作响应时间少于1秒
- 排序操作响应时间少于1秒

**Accessibility Requirements:**
- 筛选面板支持键盘导航
- 筛选条件使用复选框或单选按钮（而非仅文本）
- 排序选项使用下拉菜单（ARIA角色combobox）
- 筛选结果数量使用ARIA live region

**Error Handling:**
- 如果筛选操作失败，显示"筛选失败，请重试"错误消息
- 如果排序操作失败，显示"排序失败，请重试"错误消息

---

### Story 5.4: 推荐历史记录

作为一个使用智能推荐的用户，
我希望能够查看我的推荐历史记录，
以便回顾之前的推荐结果和偏好设置。

**Acceptance Criteria:**

**Given** 用户已登录
**And** 用户至少使用过一次智能推荐功能

**When** 用户导航到"推荐历史"页面
**Then** 系统应显示推荐历史列表
**And** 每条历史记录应包括：
  - 推荐时间（日期和时间）
  - 推荐偏好摘要（地区、学校类型等）
  - 推荐的学校数量
  - 使用的志愿数量
**And** 历史记录应按时间倒序排列（最新的在前）

**Given** 用户在推荐历史页面
**When** 用户点击某条历史记录
**Then** 系统应显示该次推荐的详细信息
**And** 详细信息应包括：
  - 推荐时间
  - 完整的推荐偏好设置
  - 推荐的学校列表（包括录取概率、匹配度）
  - 当时学生的成绩信息

**Given** 用户查看某条历史推荐的详细信息
**When** 用户点击"应用此推荐"按钮
**Then** 系统应将该推荐的学校列表设置为当前志愿
**And** 显示"成功应用推荐到志愿列表"成功消息
**And** 导航到志愿填报页面

**Given** 用户查看某条历史推荐的详细信息
**When** 用户点击"复制推荐设置"按钮
**Then** 系统应将推荐的偏好设置复制到当前推荐设置
**And** 导航到智能推荐页面
**And** 显示"已复制推荐设置"成功消息

**Given** 用户在推荐历史页面
**When** 用户点击"删除"按钮（某条历史记录）
**Then** 系统应显示删除确认对话框
**And** 对话框应询问"确定要删除这条推荐历史吗？"

**Given** 用户确认删除某条历史记录
**When** 用户点击"确定"按钮
**Then** 系统应删除该条历史记录
**And** 从列表中移除该记录
**And** 显示"已删除推荐历史"成功消息

**Given** 用户在推荐历史页面
**When** 用户点击"清空历史"按钮
**Then** 系统应显示清空确认对话框
**And** 对话框应询问"确定要清空所有推荐历史吗？此操作不可恢复。"

**Given** 用户确认清空所有历史
**When** 用户点击"确定"按钮
**Then** 系统应清空所有推荐历史记录
**And** 显示"已清空所有推荐历史"成功消息

**Given** 推荐历史记录超过50条
**When** 用户访问推荐历史页面
**Then** 系统应自动删除最旧的记录，保留最新的50条

**Performance Requirements:**
- 历史列表加载时间少于2秒
- 历史详情加载时间少于1秒

**Accessibility Requirements:**
- 历史记录列表支持键盘导航
- 删除和清空按钮有明确的ARIA描述
- 确认对话框使用ARIA dialog角色
- 历史记录日期使用可读格式

**Error Handling:**
- 如果历史记录加载失败，显示"加载推荐历史失败，请重试"错误消息
- 如果删除操作失败，显示"删除失败，请重试"错误消息
- 如果应用推荐失败，显示"应用推荐失败，请重试"错误消息

---

**Epic 5 Summary:**
- 4 stories created
- FRs covered: FR20-FR21
- NFRs covered: NFR2, NFR7
- ARs covered: AR45-AR48
- UX-DRs covered: UX-DR11, UX-DR12, UX-DR15, UX-DR29-UX-DR35

---

## Epic 6: 系统内容与用户服务

**Epic Goal:** 用户能够获得完善的帮助文档、FAQ解答、用户反馈渠道和客服支持，提升使用体验

**FRs Covered:** FR33-FR39, NFR27, NFR31, AR33, UX-DR29-UX-DR37, UX-DR40

### Story 6.1: 帮助文档浏览

作为一个使用系统的用户，
我希望能够浏览帮助文档和用户指南，
以便了解如何使用系统的各项功能。

**Acceptance Criteria:**

**Given** 用户已登录或未登录（帮助文档无需登录）

**When** 用户点击"帮助"或"文档"链接
**Then** 系统应显示帮助文档页面
**And** 页面应显示文档分类目录：
  - 快速入门
  - 用户认证
  - 学生信息管理
  - 学校信息浏览
  - 志愿填报
  - 录取预测
  - 常见问题
  - 联系客服

**Given** 用户在帮助文档页面
**When** 用户点击某个文档分类
**Then** 系统应展开该分类下的文档列表
**And** 每个文档应显示标题和简要描述

**Given** 用户在帮助文档页面
**When** 用户点击某个文档标题
**Then** 系统应显示该文档的详细内容
**And** 文档应包含：
  - 文档标题
  - 更新日期
  - 预计阅读时间
  - 详细内容（文字、截图、示例）
  - 相关文档链接

**Given** 用户正在查看文档详细内容
**When** 用户使用移动设备
**Then** 文档应支持垂直滚动阅读
**And** 文档应适配小屏幕显示
**And** 图片应支持点击放大查看

**Given** 用户在帮助文档页面
**When** 用户在搜索框输入关键词
**Then** 系统应实时搜索所有文档
**And** 显示匹配的文档列表
**And** 高亮显示匹配的关键词

**Given** 用户在帮助文档页面
**When** 用户点击"收藏"按钮（某个文档）
**Then** 系统应将该文档添加到用户的收藏列表
**And** 按钮状态应变为"已收藏"
**And** 显示"已收藏"成功提示

**Given** 用户在帮助文档页面
**When** 用户点击"我的收藏"链接
**Then** 系统应显示用户收藏的所有文档列表

**Given** 用户正在查看文档
**When** 用户点击"有用"或"无用"按钮
**Then** 系统应记录用户反馈
**And** 显示"感谢您的反馈"提示

**Performance Requirements:**
- 文档列表加载时间少于2秒
- 文档详细内容加载时间少于2秒
- 搜索响应时间少于1秒

**Accessibility Requirements:**
- 文档目录支持键盘导航
- 文档内容使用语义化HTML标签
- 图片应包含alt文本描述
- 搜索框使用ARIA标签
- 所有交互元素最小44x44像素

**Error Handling:**
- 如果文档加载失败，显示"文档加载失败，请重试"错误消息
- 如果搜索失败，显示"搜索失败，请重试"错误消息

---

### Story 6.2: FAQ搜索和查看

作为一个使用系统的用户，
我希望能够搜索和查看常见问题解答（FAQ），
以便快速找到问题的解决方案。

**Acceptance Criteria:**

**Given** 用户已登录或未登录（FAQ无需登录）

**When** 用户导航到"常见问题"页面
**Then** 系统应显示FAQ页面
**And** 页面应显示搜索框
**And** 页面应显示FAQ分类标签：
  - 账户相关
  - 学生信息
  - 志愿填报
  - 录取预测
  - 学校信息
  - 技术支持
  - 其他

**Given** 用户在FAQ页面
**When** 用户点击某个分类标签
**Then** 系统应过滤显示该分类下的FAQ
**And** 显示该分类的FAQ数量

**Given** 用户在FAQ页面
**When** 用户在搜索框输入关键词
**Then** 系统应实时搜索所有FAQ
**And** 显示匹配的FAQ列表
**And** 高亮显示匹配的关键词

**Given** 系统显示FAQ列表
**Then** 每个FAQ应显示：
  - 问题标题
  - 简要回答（前50个字符）
  - 分类标签
  - 查看次数（可选）

**Given** 用户在FAQ页面
**When** 用户点击某个FAQ问题
**Then** 系统应展开显示完整的问题和答案
**And** 答案应支持富文本（文字、链接、图片、代码块）

**Given** 用户查看某个FAQ的详细答案
**When** 用户点击"有帮助"按钮
**Then** 系统应记录点赞
**And** 更新该FAQ的点赞数量
**And** 显示"感谢您的反馈"提示

**Given** 用户查看某个FAQ的详细答案
**When** 用户点击"无帮助"按钮
**Then** 系统应记录反馈
**And** 显示"感谢您的反馈，我们会改进此答案"提示

**Given** 用户在FAQ页面
**When** 用户搜索不到相关答案
**Then** 系统应显示"未找到相关答案"提示
**And** 提供"联系客服"按钮

**Given** 用户在FAQ页面
**When** 用户点击"热门问题"链接
**Then** 系统应显示按查看次数或点赞数排序的FAQ列表

**Given** 用户在FAQ页面
**When** 用户使用移动设备
**Then** FAQ应支持垂直滚动
**And** 所有交互元素触摸目标至少44x44像素

**Performance Requirements:**
- FAQ列表加载时间少于2秒
- FAQ搜索响应时间少于1秒
- FAQ详情展开时间少于0.5秒

**Accessibility Requirements:**
- FAQ列表支持键盘导航
- FAQ详情使用ARIA expanded属性
- 搜索框使用ARIA标签
- 分类标签使用ARIA button角色
- 有帮助/无帮助按钮有明确的ARIA描述

**Error Handling:**
- 如果FAQ加载失败，显示"加载失败，请重试"错误消息
- 如果搜索失败，显示"搜索失败，请重试"错误消息

---

### Story 6.3: 用户反馈提交

作为一个使用系统的用户，
我希望能够提交反馈和建议，
以便帮助系统改进和优化。

**Acceptance Criteria:**

**Given** 用户已登录

**When** 用户点击"反馈"或"建议"按钮
**Then** 系统应显示反馈提交页面
**And** 页面应显示反馈表单

**Given** 用户在反馈提交页面
**Then** 表单应包含以下字段：
  - 反馈类型（下拉选择）：问题报告、功能建议、体验反馈、其他
  - 反馈内容（文本框，必填）
  - 相关页面（下拉选择，可选）
  - 附件上传（可选，支持图片、PDF）
  - 联系方式（邮箱或手机号，可选）

**Given** 用户在反馈提交页面
**When** 用户选择"问题报告"类型
**Then** 系统应显示额外字段：
  - 问题严重程度（下拉选择）：严重、中等、轻微
  - 问题发生频率（下拉选择）：每次、经常、偶尔、仅一次

**Given** 用户在反馈提交页面
**When** 用户选择"功能建议"类型
**Then** 系统应显示额外字段：
  - 功能优先级（下拉选择）：高、中、低
  - 建议的使用场景（文本框，可选）

**Given** 用户在反馈提交页面
**When** 用户上传附件
**Then** 系统应验证文件类型（仅支持jpg、png、pdf）
**And** 系统应验证文件大小（最大10MB）
**And** 系统应显示上传进度
**And** 上传成功后显示文件名和删除按钮

**Given** 用户填写反馈表单
**When** 反馈内容少于10个字符
**Then** 系统应显示验证错误："反馈内容至少需要10个字符"

**Given** 用户填写反馈表单
**When** 用户点击"提交"按钮
**Then** 系统应验证必填字段
**And** 如果验证通过，提交反馈
**And** 显示"反馈提交成功，感谢您的建议！"成功消息
**And** 生成反馈编号（如FB-20250112-001）

**Given** 用户提交反馈成功
**When** 用户提供了联系方式
**Then** 系统应发送确认邮件或短信
**And** 包含反馈编号和预计回复时间

**Given** 用户在反馈提交页面
**When** 用户点击"查看我的反馈"链接
**Then** 系统应显示用户历史反馈列表
**And** 每条反馈应显示：
  - 反馈编号
  - 提交时间
  - 反馈类型
  - 反馈内容摘要
  - 状态（待处理、处理中、已解决、已关闭）

**Given** 用户查看历史反馈列表
**When** 用户点击某条反馈
**Then** 系统应显示反馈详情
**And** 包括完整的反馈内容、附件、客服回复、状态变更历史

**Given** 用户查看某条反馈详情
**When** 反馈状态为"已解决"或"已关闭"
**And** 用户希望重新反馈
**Then** 用户应能添加追加评论

**Performance Requirements:**
- 反馈提交响应时间少于2秒
- 反馈列表加载时间少于2秒
- 附件上传响应时间少于3秒

**Accessibility Requirements:**
- 反馈表单支持键盘导航
- 表单字段使用label元素关联
- 错误消息使用ARIA live region
- 文件上传区域支持拖拽和键盘操作
- 所有交互元素最小44x44像素

**Error Handling:**
- 如果附件上传失败，显示"文件上传失败，请重试"错误消息
- 如果反馈提交失败，显示"提交失败，请重试"错误消息
- 如果文件类型不支持，显示"不支持的文件类型，请上传jpg、png或pdf文件"
- 如果文件大小超过限制，显示"文件大小超过10MB限制"

---

### Story 6.4: 客服在线咨询

作为一个遇到问题的用户，
我希望能够通过在线咨询联系客服，
以便获得及时的帮助和解答。

**Acceptance Criteria:**

**Given** 用户已登录

**When** 用户点击"在线客服"或"联系客服"按钮
**Then** 系统应显示在线客服聊天窗口
**And** 窗口应显示欢迎消息："您好，我是智能客服，有什么可以帮助您？"
**And** 窗口应显示客服头像和名称
**And** 窗口应显示客服在线状态（在线/离线）

**Given** 用户打开在线客服窗口
**Then** 窗口应显示常见问题快捷回复按钮：
  - "如何注册账号？"
  - "如何录入学生信息？"
  - "如何填报志愿？"
  - "录取概率如何计算？"
  - "其他问题"

**Given** 用户在在线客服窗口
**When** 用户点击某个快捷回复按钮
**Then** 系统应自动将该问题发送到聊天窗口
**And** 客服应自动回复标准答案

**Given** 用户在在线客服窗口
**When** 用户输入消息并发送
**Then** 系统应显示用户消息（右侧，蓝色气泡）
**And** 系统应发送消息到客服系统
**And** 系统应显示"对方正在输入..."提示（当客服正在回复时）

**Given** 用户在在线客服窗口
**When** 客服回复消息
**Then** 系统应显示客服消息（左侧，白色气泡）
**And** 系统应播放提示音（可选）
**And** 系统应在聊天窗口标题显示未读消息数量

**Given** 用户在在线客服窗口
**When** 客服回复中包含链接或文件
**Then** 系统应将链接渲染为可点击的链接
**And** 系统应显示文件下载按钮

**Given** 用户在在线客服窗口
**When** 用户需要发送图片
**Then** 用户应能点击图片上传按钮
**And** 选择图片文件（支持jpg、png）
**And** 图片应显示在聊天窗口中

**Given** 用户在在线客服窗口
**When** 用户点击"结束对话"按钮
**Then** 系统应显示确认对话框："确定要结束对话吗？"
**And** 用户确认后，关闭聊天窗口
**And** 系统应保存聊天记录

**Given** 用户关闭在线客服窗口
**When** 用户重新打开客服窗口
**Then** 系统应显示历史聊天记录
**And** 询问"是否继续之前的对话？"

**Given** 用户在在线客服窗口
**When** 客服状态为"离线"
**Then** 系统应显示"客服当前离线，请留言，我们会尽快回复"提示
**And** 用户应能发送留言

**Given** 用户在在线客服窗口
**When** 用户使用移动设备
**Then** 聊天窗口应适配小屏幕
**And** 输入框和发送按钮应易于点击（至少44x44像素）

**Performance Requirements:**
- 消息发送响应时间少于1秒
- 消息接收延迟少于2秒
- 图片上传响应时间少于3秒

**Accessibility Requirements:**
- 聊天窗口支持键盘导航
- 消息使用ARIA live region
- 快捷回复按钮使用ARIA button角色
- 图片上传按钮有明确的ARIA描述
- 输入框使用ARIA标签

**Error Handling:**
- 如果消息发送失败，显示"消息发送失败，请重试"错误消息
- 如果图片上传失败，显示"图片上传失败，请重试"错误消息
- 如果客服系统不可用，显示"客服系统暂时不可用，请稍后再试"错误消息

---

### Story 6.5: 系统公告查看

作为一个使用系统的用户，
我希望能够查看系统公告和通知，
以便了解系统的最新动态和重要信息。

**Acceptance Criteria:**

**Given** 用户已登录或未登录（公告无需登录）

**When** 用户点击"公告"或"通知"链接
**Then** 系统应显示系统公告页面
**And** 页面应显示公告列表
**And** 列表应按发布时间倒序排列（最新的在前）

**Given** 系统显示公告列表
**Then** 每条公告应显示：
  - 公告标题
  - 发布时间
  - 公告类型标签（系统维护、功能更新、政策通知、其他）
  - 摘要（前100个字符）
  - 是否为重要公告（重要公告显示红色标签）

**Given** 用户在系统公告页面
**When** 有新的重要公告
**Then** 系统应在首页显示公告横幅
**And** 横幅应显示公告标题和"查看详情"按钮

**Given** 用户在系统公告页面
**When** 用户点击某条公告
**Then** 系统应显示公告详细内容
**And** 详细内容应包括：
  - 公告标题
  - 发布时间
  - 公告类型
  - 完整内容（支持富文本）
  - 相关链接（如有）

**Given** 用户查看公告详细内容
**When** 公告内容包含图片或附件
**Then** 图片应支持点击放大查看
**And** 附件应显示下载按钮

**Given** 用户在系统公告页面
**When** 用户点击筛选器
**Then** 系统应显示筛选选项：
  - 按公告类型筛选
  - 按时间范围筛选（最近7天、最近30天、最近3个月、全部）

**Given** 用户设置筛选条件
**When** 用户选择筛选选项
**Then** 系统应实时过滤公告列表
**And** 显示筛选后的公告数量

**Given** 用户在系统公告页面
**When** 用户使用移动设备
**Then** 公告列表应支持垂直滚动
**And** 所有交互元素触摸目标至少44x44像素

**Given** 用户在系统公告页面
**When** 公告列表为空
**Then** 系统应显示"暂无公告"空状态
**And** 显示系统插图

**Given** 用户已登录
**When** 用户查看公告后
**Then** 系统应标记该公告为"已读"

**Given** 用户在系统公告页面
**When** 用户点击"标记全部已读"按钮
**Then** 系统应将所有公告标记为"已读"

**Performance Requirements:**
- 公告列表加载时间少于2秒
- 公告详细内容加载时间少于1秒
- 筛选操作响应时间少于1秒

**Accessibility Requirements:**
- 公告列表支持键盘导航
- 公告类型标签使用ARIA标签
- 筛选器使用ARIA combobox角色
- 重要公告使用视觉和ARIA标识
- 所有交互元素最小44x44像素

**Error Handling:**
- 如果公告加载失败，显示"加载失败，请重试"错误消息
- 如果公告详情加载失败，显示"详情加载失败，请重试"错误消息

---

### Story 6.6: 用户指南教程

作为一个新用户，
我希望能够通过交互式教程了解系统功能，
以便快速上手使用系统。

**Acceptance Criteria:**

**Given** 用户首次登录系统

**When** 用户完成初始设置后
**Then** 系统应询问"是否需要查看新手教程？"
**And** 提供"开始教程"和"跳过"按钮

**Given** 用户选择"开始教程"
**Then** 系统应启动交互式教程
**And** 教程应覆盖以下模块：
  - 用户认证（注册、登录、忘记密码）
  - 学生信息录入
  - 学校信息浏览
  - 志愿填报
  - 录取预测

**Given** 教程正在进行中
**Then** 系统应使用高亮和箭头指示用户应关注的UI元素
**And** 系统应显示步骤说明文字
**And** 系统应显示当前步骤进度（如"步骤 3/10"）

**Given** 教程正在进行中
**When** 用户需要执行某个操作（如点击按钮）
**Then** 系统应等待用户执行操作
**And** 操作完成后，自动进入下一步

**Given** 教程正在进行中
**When** 用户点击"下一步"按钮
**Then** 系统应进入下一个教程步骤

**Given** 教程正在进行中
**When** 用户点击"上一步"按钮
**Then** 系统应返回上一个教程步骤

**Given** 教程正在进行中
**When** 用户点击"跳过"按钮
**Then** 系统应显示确认对话框："确定要跳过教程吗？"
**And** 用户确认后，退出教程

**Given** 教程正在进行中
**When** 用户点击"结束教程"按钮
**Then** 系统应显示确认对话框："确定要结束教程吗？"
**And** 用户确认后，退出教程
**And** 显示"教程完成，祝您使用愉快！"成功消息

**Given** 教程已完成或跳过
**When** 用户希望重新查看教程
**Then** 用户应能在"帮助"页面找到"重新开始教程"按钮
**And** 点击后重新启动教程

**Given** 教程正在进行中
**When** 用户使用移动设备
**Then** 教程应适配小屏幕
**And** 教程提示应清晰可见
**And** 导航按钮应易于点击（至少44x44像素）

**Given** 教程正在进行中
**When** 用户完成某个模块的教程
**Then** 系统应在该模块旁边显示"已完成"标记

**Given** 教程正在进行中
**When** 用户希望跳过某个步骤
**Then** 用户应能点击"跳过此步骤"按钮
**And** 系统应进入下一个步骤

**Performance Requirements:**
- 教程加载时间少于2秒
- 步骤切换响应时间少于0.5秒

**Accessibility Requirements:**
- 教程提示使用ARIA live region
- 高亮元素使用ARIA描述
- 导航按钮支持键盘操作
- 所有交互元素最小44x44像素
- 教程文字使用清晰易读的字体

**Error Handling:**
- 如果教程加载失败，显示"教程加载失败，请重试"错误消息
- 如果教程执行出错，显示"教程执行出错，请重新开始"错误消息

---

**Epic 6 Summary:**
- 6 stories created
- FRs covered: FR33-FR39
- NFRs covered: NFR27, NFR31
- ARs covered: AR33
- UX-DRs covered: UX-DR29-UX-DR37, UX-DR40

## Epic 7: 管理后台与系统管理

**Epic Goal:** 管理员能够通过管理后台进行数据导入导出、系统配置、日志查看、性能监控和内容管理，确保系统稳定运行

**FRs Covered:** FR24-FR27, FR29-FR32, NFR5-NFR8, NFR12-NFR13, NFR19-NFR22, AR19-AR28, AR49-AR52

### Story 7.1: 历史录取数据导入

作为一个系统管理员，
我希望能够导入历史录取数据到系统中，
以便系统能够基于历史数据进行录取预测。

**Acceptance Criteria:**

**Given** 用户已以管理员身份登录

**When** 用户导航到"数据管理" > "数据导入"页面
**Then** 系统应显示数据导入页面
**And** 页面应显示数据类型选择：
  - 历史录取数据
  - 学校信息数据
  - 政策数据
  - 其他

**Given** 用户在数据导入页面
**When** 用户选择"历史录取数据"类型
**Then** 系统应显示导入说明
**And** 提供数据模板下载链接
**And** 显示文件上传区域（支持Excel、CSV格式）

**Given** 用户在数据导入页面
**When** 用户下载并查看数据模板
**Then** 模板应包含以下字段：
  - 学校ID
  - 学校名称
  - 年份
  - 录取批次
  - 录取分数线（总分）
  - 各科分数线
  - 录取人数
  - 申请人数
  - 录取率
  - 备注

**Given** 用户在数据导入页面
**When** 用户选择文件并上传
**Then** 系统应验证文件格式（仅支持.xlsx、.csv）
**And** 系统应验证文件大小（最大50MB）
**And** 系统应显示上传进度

**Given** 文件上传成功
**When** 系统开始解析文件
**Then** 系统应显示解析进度
**And** 显示已解析的记录数量

**Given** 系统解析文件完成
**When** 数据验证通过
**Then** 系统应显示数据预览（前10条记录）
**And** 显示数据验证结果：
  - 有效记录数量
  - 无效记录数量
  - 重复记录数量
  - 缺失字段数量

**Given** 用户查看数据预览和验证结果
**When** 用户点击"开始导入"按钮
**Then** 系统应显示确认对话框："确定要导入X条有效记录吗？"
**And** 用户确认后，开始导入数据
**And** 显示导入进度

**Given** 系统正在导入数据
**When** 导入过程中发生错误
**Then** 系统应记录错误日志
**And** 显示错误详情
**And** 提供"继续导入"或"取消导入"选项

**Given** 数据导入完成
**Then** 系统应显示导入结果摘要：
  - 成功导入的记录数量
  - 导入失败的记录数量及原因
  - 导入耗时
**And** 提供"下载失败记录"按钮（如有失败记录）
**And** 显示"导入完成"成功消息

**Given** 用户在数据导入页面
**When** 用户使用移动设备
**Then** 页面应适配小屏幕
**And** 文件上传区域应易于操作（至少44x44像素）

**Given** 用户在数据导入页面
**When** 用户点击"查看导入历史"链接
**Then** 系统应显示历史导入记录列表
**And** 每条记录应显示：
  - 导入时间
  - 数据类型
  - 文件名
  - 导入状态（成功/失败/部分成功）
  - 记录数量

**Performance Requirements:**
- 文件上传速度至少1MB/s
- 数据解析响应时间少于10秒（1000条记录）
- 数据导入响应时间少于30秒（1000条记录）
- 导入历史列表加载时间少于2秒

**Accessibility Requirements:**
- 文件上传区域支持键盘操作
- 文件类型验证错误使用ARIA live region
- 进度条使用ARIA role="progressbar"
- 确认对话框使用ARIA dialog role
- 所有交互元素最小44x44像素

**Error Handling:**
- 如果文件格式不支持，显示"不支持的文件格式，请上传Excel或CSV文件"
- 如果文件大小超过限制，显示"文件大小超过50MB限制"
- 如果数据解析失败，显示"数据解析失败，请检查文件格式是否正确"
- 如果数据验证失败，显示"数据验证失败，请检查数据格式是否符合模板要求"
- 如果导入失败，显示"导入失败，请重试或查看错误日志"

---

### Story 7.2: 系统数据导出

作为一个系统管理员，
我希望能够导出系统数据用于备份和分析，
以便确保数据安全和便于后续分析。

**Acceptance Criteria:**

**Given** 用户已以管理员身份登录

**When** 用户导航到"数据管理" > "数据导出"页面
**Then** 系统应显示数据导出页面
**And** 页面应显示可导出的数据类型：
  - 用户数据
  - 学生数据
  - 学校数据
  - 志愿数据
  - 历史录取数据
  - 用户反馈数据
  - 系统日志数据

**Given** 用户在数据导出页面
**When** 用户选择数据类型（如"学校数据"）
**Then** 系统应显示该数据类型的筛选条件
**And** 筛选条件应包括：
  - 时间范围（开始日期-结束日期）
  - 地区筛选
  - 学校类型筛选
  - 数据状态筛选（可选）

**Given** 用户设置筛选条件
**When** 用户点击"预览数据"按钮
**Then** 系统应显示符合条件的记录数量
**And** 显示数据预览（前5条记录）
**And** 预估导出文件大小

**Given** 用户预览数据后
**When** 用户选择导出格式（Excel或CSV）
**Then** 系统应启用"开始导出"按钮

**Given** 用户在数据导出页面
**When** 用户点击"开始导出"按钮
**Then** 系统应显示导出进度
**And** 显示已处理的记录数量
**And** 显示预计剩余时间

**Given** 系统正在导出数据
**When** 导出数据量较大（超过10000条记录）
**Then** 系统应显示"导出将在后台继续完成，完成后将通过邮件通知您"提示
**And** 用户应能关闭页面

**Given** 数据导出完成
**Then** 系统应显示导出成功消息
**And** 自动下载导出文件
**And** 显示导出结果摘要：
  - 导出的记录数量
  - 文件名
  - 文件大小
  - 导出耗时

**Given** 用户在数据导出页面
**When** 用户点击"查看导出历史"链接
**Then** 系统应显示历史导出记录列表
**And** 每条记录应显示：
  - 导出时间
  - 数据类型
  - 文件名
  - 导出格式
  - 记录数量
  - 文件大小
  - 状态（完成/失败/进行中）
  - 操作（重新下载、删除）

**Given** 用户查看导出历史
**When** 导出状态为"完成"
**And** 用户点击"重新下载"按钮
**Then** 系统应重新下载该导出文件

**Given** 用户查看导出历史
**When** 用户点击"删除"按钮
**Then** 系统应显示确认对话框："确定要删除此导出文件吗？"
**And** 用户确认后，删除该导出文件

**Given** 用户在数据导出页面
**When** 用户使用移动设备
**Then** 页面应适配小屏幕
**And** 所有操作按钮应易于点击（至少44x44像素）

**Performance Requirements:**
- 数据预览响应时间少于2秒
- 数据导出速度至少1000条/秒
- 导出历史列表加载时间少于2秒
- 大数据量导出（10000+记录）应在后台异步处理

**Accessibility Requirements:**
- 筛选条件支持键盘导航
- 导出进度条使用ARIA role="progressbar"
- 删除确认对话框使用ARIA dialog role
- 下载按钮使用ARIA下载属性
- 所有交互元素最小44x44像素

**Error Handling:**
- 如果导出数据为空，显示"没有符合条件的数据可导出"
- 如果导出失败，显示"导出失败，请重试或查看错误日志"
- 如果文件下载失败，显示"文件下载失败，请重试"
- 如果删除导出文件失败，显示"删除失败，请重试"

---

### Story 7.3: 系统数据备份

作为一个系统管理员，
我希望能够创建系统数据备份，
以便在系统故障或数据丢失时能够恢复数据。

**Acceptance Criteria:**

**Given** 用户已以管理员身份登录

**When** 用户导航到"系统管理" > "数据备份"页面
**Then** 系统应显示数据备份页面
**And** 页面应显示备份类型选择：
  - 完整备份（所有数据）
  - 增量备份（仅变更数据）
  - 选择性备份（选择数据模块）

**Given** 用户在数据备份页面
**When** 用户选择"完整备份"
**Then** 系统应显示完整备份说明
**And** 说明应包括：
  - 备份范围（用户、学生、学校、志愿、录取数据等）
  - 预估备份大小
  - 预估备份时间

**Given** 用户在数据备份页面
**When** 用户选择"选择性备份"
**Then** 系统应显示可备份的数据模块：
  - 用户数据
  - 学生数据
  - 学校数据
  - 志愿数据
  - 历史录取数据
  - 系统配置数据
  - 用户反馈数据
**And** 用户应能勾选需要备份的模块

**Given** 用户选择备份类型和模块
**When** 用户点击"开始备份"按钮
**Then** 系统应显示确认对话框："确定要开始备份吗？备份期间系统可能响应变慢。"
**And** 用户确认后，开始备份

**Given** 系统正在备份
**Then** 系统应显示备份进度
**And** 显示已备份的模块
**And** 显示已处理的数据量
**And** 显示预计剩余时间

**Given** 系统正在备份
**When** 备份过程中发生错误
**Then** 系统应记录错误日志
**And** 显示错误详情
**And** 提供"继续备份"或"取消备份"选项

**Given** 备份完成
**Then** 系统应显示备份成功消息
**And** 显示备份结果摘要：
  - 备份ID（如BACKUP-20250112-001）
  - 备份类型
  - 备份时间
  - 备份的数据模块
  - 备份文件大小
  - 备份耗时

**Given** 备份完成
**When** 用户提供了通知邮箱（可选）
**Then** 系统应发送备份完成通知邮件
**And** 邮件应包含备份ID、备份时间、文件大小

**Given** 用户在数据备份页面
**When** 用户点击"查看备份历史"链接
**Then** 系统应显示历史备份记录列表
**And** 每条记录应显示：
  - 备份ID
  - 备份时间
  - 备份类型
  - 备份的数据模块
  - 文件大小
  - 状态（成功/失败/进行中）
  - 操作（下载、删除、恢复）

**Given** 用户查看备份历史
**When** 备份状态为"成功"
**And** 用户点击"下载"按钮
**Then** 系统应下载该备份文件

**Given** 用户在数据备份页面
**When** 用户查看自动备份设置
**Then** 系统应显示自动备份配置：
  - 自动备份开关（开启/关闭）
  - 备份频率（每天、每周、每月）
  - 备份时间点
  - 备份类型（完整/增量）
  - 备份保留天数

**Given** 用户在数据备份页面
**When** 用户启用自动备份
**Then** 系统应保存自动备份配置
**And** 显示"自动备份已启用"成功消息
**And** 显示下次自动备份时间

**Performance Requirements:**
- 完整备份响应时间少于5分钟
- 增量备份响应时间少于1分钟
- 备份历史列表加载时间少于2秒
- 自动备份应在指定时间准确执行

**Accessibility Requirements:**
- 备份类型选择支持键盘导航
- 备份进度条使用ARIA role="progressbar"
- 确认对话框使用ARIA dialog role
- 数据模块复选框使用ARIA checkbox角色
- 所有交互元素最小44x44像素

**Error Handling:**
- 如果备份失败，显示"备份失败，请重试或查看错误日志"
- 如果存储空间不足，显示"存储空间不足，请清理旧备份"
- 如果自动备份执行失败，应发送告警邮件给管理员
- 如果下载备份文件失败，显示"下载失败，请重试"

---

### Story 7.4: 系统数据恢复

作为一个系统管理员，
我希望能够从备份中恢复系统数据，
以便在系统故障或数据损坏时快速恢复系统。

**Acceptance Criteria:**

**Given** 用户已以管理员身份登录

**When** 用户导航到"系统管理" > "数据恢复"页面
**Then** 系统应显示数据恢复页面
**And** 页面应显示可用的备份列表
**And** 每个备份应显示：
  - 备份ID
  - 备份时间
  - 备份类型
  - 备份的数据模块
  - 文件大小

**Given** 用户在数据恢复页面
**When** 用户选择一个备份
**Then** 系统应显示该备份的详细信息：
  - 备份时间
  - 备份类型
  - 备份的数据模块
  - 文件大小
  - 备份时系统版本

**Given** 用户选择备份后
**When** 用户点击"预览备份内容"按钮
**Then** 系统应显示备份中的数据模块和记录数量

**Given** 用户在数据恢复页面
**When** 用户点击"开始恢复"按钮
**Then** 系统应显示恢复选项：
  - 完整恢复（恢复所有数据）
  - 选择性恢复（选择数据模块）
**And** 用户应能选择恢复方式

**Given** 用户选择"完整恢复"
**When** 用户点击"确认恢复"按钮
**Then** 系统应显示二次确认对话框："完整恢复将覆盖当前所有数据，此操作不可逆！确定要继续吗？"
**And** 用户需输入"CONFIRM"确认
**And** 用户确认后，开始恢复

**Given** 系统正在恢复数据
**Then** 系统应显示恢复进度
**And** 显示已恢复的模块
**And** 显示已恢复的数据量
**And** 显示预计剩余时间
**And** 显示"恢复期间系统将暂停服务"提示

**Given** 系统正在恢复数据
**When** 恢复过程中发生错误
**Then** 系统应记录错误日志
**And** 显示错误详情
**And** 提供"继续恢复"或"取消恢复"选项
**And** 如果取消，系统应尝试回滚已恢复的数据

**Given** 数据恢复完成
**Then** 系统应显示恢复成功消息
**And** 显示恢复结果摘要：
  - 恢复的备份ID
  - 恢复的数据模块
  - 恢复的记录数量
  - 恢复耗时
**And** 显示"系统已恢复，请验证数据完整性"提示

**Given** 数据恢复完成
**When** 系统恢复成功
**Then** 系统应发送恢复完成通知邮件给管理员
**And** 邮件应包含备份ID、恢复时间、恢复的数据模块

**Given** 用户在数据恢复页面
**When** 用户查看恢复历史
**Then** 系统应显示历史恢复记录列表
**And** 每条记录应显示：
  - 恢复时间
  - 恢复的备份ID
  - 恢复的数据模块
  - 恢复状态（成功/失败/部分成功）
  - 恢复耗时
  - 操作人

**Given** 用户在数据恢复页面
**When** 用户使用移动设备
**Then** 页面应适配小屏幕
**And** 所有操作按钮应易于点击（至少44x44像素）

**Given** 用户在数据恢复页面
**When** 备份文件已过期或损坏
**Then** 系统应显示"备份文件已过期或损坏，无法恢复"错误消息

**Performance Requirements:**
- 完整恢复响应时间少于10分钟
- 增量恢复响应时间少于2分钟
- 恢复历史列表加载时间少于2秒
- 恢复操作应在系统维护窗口执行

**Accessibility Requirements:**
- 备份列表支持键盘导航
- 恢复进度条使用ARIA role="progressbar"
- 二次确认对话框使用ARIA alertdialog角色
- 恢复选项使用ARIA radiogroup角色
- 所有交互元素最小44x44像素

**Error Handling:**
- 如果恢复失败，显示"恢复失败，请重试或查看错误日志"
- 如果备份文件损坏，显示"备份文件损坏，请选择其他备份"
- 如果回滚失败，显示"回滚失败，请联系技术支持"
- 如果恢复超时，显示"恢复超时，请检查系统状态"

---

### Story 7.5: 用户账户管理

作为一个系统管理员，
我希望能够管理用户账户和权限，
以便控制系统访问和确保数据安全。

**Acceptance Criteria:**

**Given** 用户已以管理员身份登录

**When** 用户导航到"用户管理" > "用户列表"页面
**Then** 系统应显示用户列表页面
**And** 页面应显示用户列表表格
**And** 表格应包含以下列：
  - 用户ID
  - 用户名
  - 邮箱/手机号
  - 用户类型（普通用户/管理员）
  - 账号状态（正常/禁用/锁定）
  - 注册时间
  - 最后登录时间
  - 操作（编辑、禁用/启用、删除、查看详情）

**Given** 用户在用户列表页面
**When** 用户在搜索框输入关键词
**Then** 系统应实时搜索用户
**And** 搜索支持：用户名、邮箱、手机号

**Given** 用户在用户列表页面
**When** 用户使用筛选器
**Then** 系统应显示筛选选项：
  - 用户类型筛选
  - 账号状态筛选
  - 注册时间范围筛选
  - 最后登录时间范围筛选

**Given** 用户设置筛选条件
**When** 用户选择筛选选项
**Then** 系统应实时过滤用户列表
**And** 显示筛选后的用户数量

**Given** 用户在用户列表页面
**When** 用户点击"新增用户"按钮
**Then** 系统应显示新增用户表单
**And** 表单应包含以下字段：
  - 用户名（必填）
  - 邮箱或手机号（必填）
  - 密码（必填，至少8位）
  - 确认密码（必填）
  - 用户类型（下拉选择）
  - 账号状态（默认正常）

**Given** 用户填写新增用户表单
**When** 用户点击"保存"按钮
**Then** 系统应验证表单数据
**And** 如果验证通过，创建用户
**And** 显示"用户创建成功"成功消息

**Given** 用户在用户列表页面
**When** 用户点击某个用户的"编辑"按钮
**Then** 系统应显示编辑用户表单
**And** 表单应显示用户当前信息
**And** 用户应能修改：用户名、邮箱/手机号、用户类型、账号状态
**And** 密码字段为可选（如需修改密码，填写新密码）

**Given** 用户编辑用户表单
**When** 用户点击"保存"按钮
**Then** 系统应验证表单数据
**And** 如果验证通过，更新用户信息
**And** 显示"用户更新成功"成功消息

**Given** 用户在用户列表页面
**When** 用户点击某个用户的"禁用"按钮
**Then** 系统应显示确认对话框："确定要禁用此用户吗？禁用后用户将无法登录。"
**And** 用户确认后，禁用该用户
**And** 显示"用户已禁用"成功消息

**Given** 用户在用户列表页面
**When** 用户点击某个用户的"启用"按钮
**Then** 系统应显示确认对话框："确定要启用此用户吗？"
**And** 用户确认后，启用该用户
**And** 显示"用户已启用"成功消息

**Given** 用户在用户列表页面
**When** 用户点击某个用户的"删除"按钮
**Then** 系统应显示二次确认对话框："确定要删除此用户吗？删除后用户数据将无法恢复！"
**And** 用户需输入"DELETE"确认
**And** 用户确认后，删除该用户
**And** 显示"用户已删除"成功消息

**Given** 用户在用户列表页面
**When** 用户点击某个用户的"查看详情"按钮
**Then** 系统应显示用户详情页面
**And** 详情应包括：
  - 用户基本信息
  - 关联的学生信息
  - 志愿填报记录
  - 登录历史
  - 操作日志

**Given** 用户在用户列表页面
**When** 用户使用移动设备
**Then** 页面应适配小屏幕
**And** 表格应支持横向滚动
**And** 操作按钮应易于点击（至少44x44像素）

**Performance Requirements:**
- 用户列表加载时间少于2秒
- 用户搜索响应时间少于1秒
- 用户创建/更新响应时间少于1秒
- 用户详情加载时间少于2秒

**Accessibility Requirements:**
- 用户列表支持键盘导航
- 表单字段使用label元素关联
- 确认对话框使用ARIA dialog role
- 搜索框使用ARIA标签
- 所有交互元素最小44x44像素

**Error Handling:**
- 如果用户名已存在，显示"用户名已存在，请使用其他用户名"
- 如果邮箱/手机号已存在，显示"该邮箱/手机号已被注册"
- 如果密码强度不足，显示"密码至少8位，包含字母和数字"
- 如果用户删除失败，显示"删除失败，请重试"
- 如果用户加载失败，显示"加载失败，请重试"

---

### Story 7.6: 系统参数配置

作为一个系统管理员，
我希望能够配置系统参数，
以便根据实际需求调整系统行为。

**Acceptance Criteria:**

**Given** 用户已以管理员身份登录

**When** 用户导航到"系统管理" > "参数配置"页面
**Then** 系统应显示参数配置页面
**And** 页面应显示参数分类：
  - 基础参数
  - 录取预测参数
  - 用户设置参数
  - 通知设置参数
  - 安全设置参数

**Given** 用户在参数配置页面
**When** 用户点击"基础参数"分类
**Then** 系统应显示基础参数列表
**And** 参数应包括：
  - 系统名称
  - 系统描述
  - 系统Logo（上传）
  - 系统版本号（只读）
  - 系统上线时间
  - 系统维护模式（开启/关闭）
  - 系统维护公告

**Given** 用户在参数配置页面
**When** 用户修改"系统名称"
**Then** 系统应实时保存修改
**And** 显示"保存成功"提示

**Given** 用户在参数配置页面
**When** 用户点击"录取预测参数"分类
**Then** 系统应显示录取预测参数列表
**And** 参数应包括：
  - 预测算法权重配置
  - 历史数据权重
  - 分数段配置
  - 批次分数线配置
  - 学校热度系数

**Given** 用户在参数配置页面
**When** 用户点击"用户设置参数"分类
**Then** 系统应显示用户设置参数列表
**And** 参数应包括：
  - 用户注册开关
  - 密码强度要求
  - 手机号验证开关
  - 邮箱验证开关
  - 用户协议开关

**Given** 用户在参数配置页面
**When** 用户点击"通知设置参数"分类
**Then** 系统应显示通知设置参数列表
**And** 参数应包括：
  - 短信通知开关
  - 邮件通知开关
  - 系统内通知开关
  - 推送通知开关
  - 通知频率限制

**Given** 用户在参数配置页面
**When** 用户点击"安全设置参数"分类
**Then** 系统应显示安全设置参数列表
**And** 参数应包括：
  - 登录失败锁定阈值
  - 账号锁定时间
  - 会话超时时间
  - 密码过期时间
  - IP白名单配置

**Given** 用户在参数配置页面
**When** 用户修改任何参数
**Then** 系统应验证参数值
**And** 如果验证通过，实时保存
**And** 显示"保存成功"提示

**Given** 用户在参数配置页面
**When** 用户点击"恢复默认设置"按钮
**Then** 系统应显示确认对话框："确定要恢复所有参数为默认值吗？"
**And** 用户确认后，恢复默认参数

**Given** 用户在参数配置页面
**When** 用户使用移动设备
**Then** 页面应适配小屏幕
**And** 所有操作按钮应易于点击（至少44x44像素）

**Performance Requirements:**
- 参数页面加载时间少于2秒
- 参数保存响应时间少于1秒
- 恢复默认设置响应时间少于2秒

**Accessibility Requirements:**
- 参数分类支持键盘导航
- 表单字段使用label元素关联
- 确认对话框使用ARIA dialog role
- 所有交互元素最小44x44像素

**Error Handling:**
- 如果参数值无效，显示"参数值无效，请重新输入"
- 如果参数保存失败，显示"保存失败，请重试"
- 如果恢复默认设置失败，显示"恢复失败，请重试"

---

### Story 7.7: 系统操作日志查看

作为一个系统管理员，
我希望能够查看系统操作日志，
以便监控系统运行状态和排查问题。

**Acceptance Criteria:**

**Given** 用户已以管理员身份登录

**When** 用户导航到"系统管理" > "操作日志"页面
**Then** 系统应显示操作日志页面
**And** 页面应显示日志列表表格
**And** 表格应包含以下列：
  - 日志ID
  - 操作时间
  - 操作类型
  - 操作用户
  - 操作模块
  - 操作详情
  - IP地址
  - 操作结果（成功/失败）
  - 操作（查看详情）

**Given** 用户在操作日志页面
**When** 用户在搜索框输入关键词
**Then** 系统应实时搜索日志
**And** 搜索支持：操作用户、操作详情、IP地址

**Given** 用户在操作日志页面
**When** 用户使用筛选器
**Then** 系统应显示筛选选项：
  - 操作类型筛选
  - 操作模块筛选
  - 操作结果筛选
  - 操作时间范围筛选
  - 操作用户筛选

**Given** 用户设置筛选条件
**When** 用户选择筛选选项
**Then** 系统应实时过滤日志列表
**And** 显示筛选后的日志数量

**Given** 用户在操作日志页面
**When** 用户点击某个日志的"查看详情"按钮
**Then** 系统应显示日志详情对话框
**And** 详情应包括：
  - 日志ID
  - 操作时间
  - 操作类型
  - 操作用户
  - 操作模块
  - 操作详情（完整）
  - IP地址
  - 用户代理
  - 操作结果
  - 错误信息（如失败）
  - 处理时间

**Given** 用户在操作日志页面
**When** 用户查看日志统计
**Then** 系统应显示统计信息：
  - 今日操作次数
  - 本周操作次数
  - 本月操作次数
  - 失败操作次数
  - 操作次数最多的用户
  - 操作次数最多的模块

**Given** 用户在操作日志页面
**When** 用户点击"导出日志"按钮
**Then** 系统应显示导出选项
**And** 选项应包括：
  - 导出时间范围
  - 导出筛选条件
  - 导出格式（Excel或CSV）

**Given** 用户设置导出选项
**When** 用户点击"开始导出"按钮
**Then** 系统应显示导出进度
**And** 导出完成后自动下载文件

**Given** 用户在操作日志页面
**When** 用户使用移动设备
**Then** 页面应适配小屏幕
**And** 表格应支持横向滚动
**And** 操作按钮应易于点击（至少44x44像素）

**Performance Requirements:**
- 日志列表加载时间少于2秒
- 日志搜索响应时间少于1秒
- 日志详情加载时间少于1秒
- 日志导出速度至少1000条/秒

**Accessibility Requirements:**
- 日志列表支持键盘导航
- 日志详情对话框使用ARIA dialog role
- 筛选条件支持键盘导航
- 搜索框使用ARIA标签
- 所有交互元素最小44x44像素

**Error Handling:**
- 如果日志加载失败，显示"加载失败，请重试"
- 如果导出失败，显示"导出失败，请重试"
- 如果日志详情加载失败，显示"详情加载失败，请重试"

---

### Story 7.8: 系统性能监控

作为一个系统管理员，
我希望能够监控系统性能指标，
以便及时发现和解决性能问题。

**Acceptance Criteria:**

**Given** 用户已以管理员身份登录

**When** 用户导航到"系统管理" > "性能监控"页面
**Then** 系统应显示性能监控页面
**And** 页面应显示实时性能指标
**And** 指标应包括：
  - CPU使用率
  - 内存使用率
  - 磁盘使用率
  - 网络流量
  - 在线用户数
  - 请求响应时间
  - 错误率

**Given** 用户在性能监控页面
**When** 用户查看实时指标
**Then** 系统应每5秒自动刷新指标
**And** 显示指标变化趋势图
**And** 使用颜色标识指标状态：
  - 绿色：正常（<70%）
  - 黄色：警告（70%-90%）
  - 红色：危险（>90%）

**Given** 用户在性能监控页面
**When** 用户点击"请求性能"标签
**Then** 系统应显示请求性能统计
**And** 统计应包括：
  - 总请求数
  - 成功请求数
  - 失败请求数
  - 平均响应时间
  - P50响应时间
  - P95响应时间
  - P99响应时间
  - 响应时间分布图

**Given** 用户在性能监控页面
**When** 用户点击"数据库性能"标签
**Then** 系统应显示数据库性能统计
**And** 统计应包括：
  - 数据库连接数
  - 活跃连接数
  - 查询次数
  - 慢查询次数
  - 平均查询时间
  - 数据库锁等待时间
  - 查询性能TOP10

**Given** 用户在性能监控页面
**When** 用户点击"缓存性能"标签
**Then** 系统应显示缓存性能统计
**And** 统计应包括：
  - 缓存命中率
  - 缓存大小
  - 缓存对象数量
  - 缓存操作次数
  - 缓存失效次数
  - 缓存命中率趋势图

**Given** 用户在性能监控页面
**When** 用户点击"API性能"标签
**Then** 系统应显示API性能统计
**And** 统计应包括：
  - API调用次数
  - API响应时间
  - API错误率
  - API调用量TOP10
  - API响应时间TOP10
  - API错误率TOP10

**Given** 用户在性能监控页面
**When** 用户点击"告警规则"标签
**Then** 系统应显示告警规则列表
**And** 每个规则应包括：
  - 规则名称
  - 监控指标
  - 告警阈值
  - 告警级别（信息/警告/严重）
  - 通知方式（邮件/短信/推送）
  - 状态（启用/禁用）
  - 操作（编辑、启用/禁用、删除）

**Given** 用户在性能监控页面
**When** 用户点击"新增告警规则"按钮
**Then** 系统应显示新增告警规则表单
**And** 表单应包括：
  - 规则名称（必填）
  - 监控指标（下拉选择）
  - 告警条件（大于/小于/等于/不等于）
  - 告警阈值（必填）
  - 告警级别（下拉选择）
  - 通知方式（多选）
  - 通知接收人（必填）

**Given** 用户填写告警规则表单
**When** 用户点击"保存"按钮
**Then** 系统应验证表单数据
**And** 如果验证通过，创建告警规则
**And** 显示"告警规则创建成功"成功消息

**Given** 用户在性能监控页面
**When** 用户点击"告警历史"标签
**Then** 系统应显示告警历史记录列表
**And** 每条记录应显示：
  - 告警时间
  - 告警规则
  - 告警指标
  - 告警值
  - 告警阈值
  - 告警级别
  - 告警状态（已处理/未处理）
  - 操作（标记已处理、查看详情）

**Given** 用户在性能监控页面
**When** 用户使用移动设备
**Then** 页面应适配小屏幕
**And** 表格应支持横向滚动
**And** 操作按钮应易于点击（至少44x44像素）

**Performance Requirements:**
- 性能指标加载时间少于2秒
- 指标刷新响应时间少于1秒
- 告警历史列表加载时间少于2秒
- 统计图表渲染时间少于2秒

**Accessibility Requirements:**
- 性能指标使用ARIA live region
- 标签页支持键盘导航
- 表单字段使用label元素关联
- 确认对话框使用ARIA dialog role
- 所有交互元素最小44x44像素

**Error Handling:**
- 如果性能指标加载失败，显示"加载失败，请重试"
- 如果告警规则创建失败，显示"创建失败，请重试"
- 如果图表渲染失败，显示"图表加载失败，请重试"

---

### Story 7.9: 内容管理

作为一个系统管理员，
我希望能够管理系统内容（帮助文档、FAQ、公告、教程），
以便为用户提供准确和及时的信息。

**Acceptance Criteria:**

**Given** 用户已以管理员身份登录

**When** 用户导航到"内容管理" > "内容列表"页面
**Then** 系统应显示内容列表页面
**And** 页面应显示内容列表表格
**And** 表格应包含以下列：
  - 内容ID
  - 内容标题
  - 内容类型（帮助文档/FAQ/公告/教程）
  - 内容分类
  - 状态（草稿/已发布/已下架）
  - 发布时间
  - 最后修改时间
  - 操作（编辑、发布/下架、置顶、删除、查看详情）

**Given** 用户在内容列表页面
**When** 用户在搜索框输入关键词
**Then** 系统应实时搜索内容
**And** 搜索支持：内容标题、内容分类

**Given** 用户在内容列表页面
**When** 用户使用筛选器
**Then** 系统应显示筛选选项：
  - 内容类型筛选
  - 内容分类筛选
  - 状态筛选
  - 发布时间范围筛选

**Given** 用户设置筛选条件
**When** 用户选择筛选选项
**Then** 系统应实时过滤内容列表
**And** 显示筛选后的内容数量

**Given** 用户在内容列表页面
**When** 用户点击"新增内容"按钮
**Then** 系统应显示新增内容表单
**And** 表单应包含以下字段：
  - 内容标题（必填）
  - 内容类型（下拉选择）
  - 内容分类（下拉选择）
  - 状态（下拉选择，默认草稿）
  - 内容内容（富文本编辑器）
  - 定时发布（可选，日期时间选择器）
  - 置顶（复选框）

**Given** 用户填写新增内容表单
**When** 用户使用富文本编辑器
**Then** 编辑器应支持以下功能：
  - 文本格式（粗体、斜体、下划线）
  - 标题（H1-H6）
  - 列表（有序、无序）
  - 链接
  - 图片上传
  - 表格
  - 代码块
  - 撤销/重做

**Given** 用户填写新增内容表单
**When** 用户点击"保存草稿"按钮
**Then** 系统应保存内容为草稿
**And** 显示"草稿保存成功"成功消息

**Given** 用户填写新增内容表单
**When** 用户点击"立即发布"按钮
**Then** 系统应验证表单数据
**And** 如果验证通过，立即发布内容
**And** 显示"内容发布成功"成功消息

**Given** 用户填写新增内容表单
**When** 用户设置定时发布时间
**And** 用户点击"定时发布"按钮
**Then** 系统应保存内容
**And** 显示"内容将在指定时间发布"成功消息

**Given** 用户在内容列表页面
**When** 用户点击某个内容的"编辑"按钮
**Then** 系统应显示编辑内容表单
**And** 表单应显示内容当前信息
**And** 用户应能修改所有字段

**Given** 用户编辑内容表单
**When** 用户点击"保存"按钮
**Then** 系统应验证表单数据
**And** 如果验证通过，更新内容
**And** 显示"内容更新成功"成功消息

**Given** 用户在内容列表页面
**When** 用户点击某个内容的"发布"按钮
**Then** 系统应显示确认对话框："确定要发布此内容吗？"
**And** 用户确认后，发布该内容
**And** 显示"内容已发布"成功消息

**Given** 用户在内容列表页面
**When** 用户点击某个内容的"下架"按钮
**Then** 系统应显示确认对话框："确定要下架此内容吗？"
**And** 用户确认后，下架该内容
**And** 显示"内容已下架"成功消息

**Given** 用户在内容列表页面
**When** 用户点击某个内容的"置顶"按钮
**Then** 系统应置顶该内容
**And** 显示"内容已置顶"成功消息

**Given** 用户在内容列表页面
**When** 用户点击某个内容的"删除"按钮
**Then** 系统应显示二次确认对话框："确定要删除此内容吗？删除后内容将无法恢复！"
**And** 用户需输入"DELETE"确认
**And** 用户确认后，删除该内容
**And** 显示"内容已删除"成功消息

**Given** 用户在内容列表页面
**When** 用户点击某个内容的"查看详情"按钮
**Then** 系统应显示内容详情对话框
**And** 详情应包括：
  - 内容基本信息
  - 内容内容（预览）
  - 发布历史
  - 访问统计

**Given** 用户在内容列表页面
**When** 用户使用移动设备
**Then** 页面应适配小屏幕
**And** 表格应支持横向滚动
**And** 操作按钮应易于点击（至少44x44像素）

**Performance Requirements:**
- 内容列表加载时间少于2秒
- 内容搜索响应时间少于1秒
- 内容创建/更新响应时间少于2秒
- 富文本编辑器加载时间少于2秒

**Accessibility Requirements:**
- 内容列表支持键盘导航
- 富文本编辑器支持键盘操作
- 表单字段使用label元素关联
- 确认对话框使用ARIA dialog role
- 所有交互元素最小44x44像素

**Error Handling:**
- 如果内容标题已存在，显示"内容标题已存在，请使用其他标题"
- 如果富文本编辑器加载失败，显示"编辑器加载失败，请重试"
- 如果内容发布失败，显示"发布失败，请重试"
- 如果图片上传失败，显示"图片上传失败，请重试"

---

### Story 7.10: 用户反馈管理

作为一个系统管理员，
我希望能够管理用户反馈，
以便及时处理用户问题和改进系统。

**Acceptance Criteria:**

**Given** 用户已以管理员身份登录

**When** 用户导航到"用户管理" > "用户反馈"页面
**Then** 系统应显示用户反馈页面
**And** 页面应显示反馈列表表格
**And** 表格应包含以下列：
  - 反馈ID
  - 反馈时间
  - 反馈用户
  - 反馈类型（问题/建议/投诉/其他）
  - 反馈标题
  - 反馈状态（待处理/处理中/已解决/已关闭）
  - 优先级（低/中/高/紧急）
  - 分配给
  - 最后回复时间
  - 操作（查看详情、分配、标记已解决、关闭）

**Given** 用户在用户反馈页面
**When** 用户在搜索框输入关键词
**Then** 系统应实时搜索反馈
**And** 搜索支持：反馈用户、反馈标题、反馈内容

**Given** 用户在用户反馈页面
**When** 用户使用筛选器
**Then** 系统应显示筛选选项：
  - 反馈类型筛选
  - 反馈状态筛选
  - 优先级筛选
  - 分配给筛选
  - 反馈时间范围筛选

**Given** 用户设置筛选条件
**When** 用户选择筛选选项
**Then** 系统应实时过滤反馈列表
**And** 显示筛选后的反馈数量

**Given** 用户在用户反馈页面
**When** 用户查看反馈统计
**Then** 系统应显示统计信息：
  - 待处理反馈数量
  - 处理中反馈数量
  - 已解决反馈数量
  - 已关闭反馈数量
  - 平均处理时间
  - 反馈类型分布
  - 优先级分布

**Given** 用户在用户反馈页面
**When** 用户点击某个反馈的"查看详情"按钮
**Then** 系统应显示反馈详情对话框
**And** 详情应包括：
  - 反馈基本信息
  - 反馈内容
  - 附件（如有）
  - 回复历史
  - 处理记录

**Given** 用户在反馈详情对话框
**When** 用户输入回复内容
**And** 用户点击"发送回复"按钮
**Then** 系统应发送回复给反馈用户
**And** 显示"回复发送成功"成功消息
**And** 更新反馈状态为"处理中"或"已解决"

**Given** 用户在用户反馈页面
**When** 用户点击某个反馈的"分配"按钮
**Then** 系统应显示分配对话框
**And** 对话框应显示可选管理员列表
**And** 用户应能选择分配给哪个管理员

**Given** 用户在分配对话框
**When** 用户选择管理员
**And** 用户点击"确认分配"按钮
**Then** 系统应分配该反馈给选定的管理员
**And** 显示"分配成功"成功消息
**And** 发送通知给被分配的管理员

**Given** 用户在用户反馈页面
**When** 用户点击某个反馈的"标记已解决"按钮
**Then** 系统应显示确认对话框："确定要标记此反馈为已解决吗？"
**And** 用户确认后，标记该反馈为已解决
**And** 显示"反馈已标记为已解决"成功消息

**Given** 用户在用户反馈页面
**When** 用户点击某个反馈的"关闭"按钮
**Then** 系统应显示确认对话框："确定要关闭此反馈吗？关闭后将无法继续处理。"
**And** 用户确认后，关闭该反馈
**And** 显示"反馈已关闭"成功消息

**Given** 用户在用户反馈页面
**When** 用户点击"导出反馈"按钮
**Then** 系统应显示导出选项
**And** 选项应包括：
  - 导出时间范围
  - 导出筛选条件
  - 导出格式（Excel或CSV）

**Given** 用户设置导出选项
**When** 用户点击"开始导出"按钮
**Then** 系统应显示导出进度
**And** 导出完成后自动下载文件

**Given** 用户在用户反馈页面
**When** 用户使用移动设备
**Then** 页面应适配小屏幕
**And** 表格应支持横向滚动
**And** 操作按钮应易于点击（至少44x44像素）

**Performance Requirements:**
- 反馈列表加载时间少于2秒
- 反馈搜索响应时间少于1秒
- 反馈详情加载时间少于1秒
- 反馈导出速度至少1000条/秒

**Accessibility Requirements:**
- 反馈列表支持键盘导航
- 反馈详情对话框使用ARIA dialog role
- 筛选条件支持键盘导航
- 搜索框使用ARIA标签
- 所有交互元素最小44x44像素

**Error Handling:**
- 如果反馈加载失败，显示"加载失败，请重试"
- 如果回复发送失败，显示"回复发送失败，请重试"
- 如果分配失败，显示"分配失败，请重试"
- 如果导出失败，显示"导出失败，请重试"

---

**Epic 7 Summary:**
- 10 stories created
- FRs covered: FR24-FR27, FR29-FR32
- NFRs covered: NFR5-NFR8, NFR12-NFR13, NFR19-NFR22
- ARs covered: AR19-AR28, AR49-AR52
- UX-DRs covered: None (Epic 7 is admin-only functionality)

---

## Epic 8: 系统可访问性与性能优化

**Epic Goal:** 系统符合WCAG 2.1 AA标准，提供优秀的性能表现，并建立完整的开发环境和监控系统

**FRs Covered:** NFR3-NFR6, NFR23-NFR26, NFR33-NFR34, UX-DR1-UX-DR50, AR9-AR12, AR17, AR39-AR44, AR45-AR50

### Story 8.1: 性能指标监控

作为一个系统管理员，
我希望能够实时查看系统的性能指标，
以便及时发现和解决性能问题。

**Acceptance Criteria:**

**Given** 管理员在性能监控页面
**When** 页面加载完成
**Then** 系统应显示以下核心指标：
  - 页面加载时间（FCP, LCP, TTI）
  - API响应时间
  - 数据库查询时间
  - 缓存命中率
  - 并发用户数

**Given** 管理员查看性能指标
**When** 页面加载时间超过3秒
**Then** 系统应显示红色警告标识
**And** 系统应记录性能告警事件

**Given** 管理员查看性能趋势
**When** 选择时间范围（1小时、24小时、7天、30天）
**Then** 系统应显示对应时间段的性能趋势图
**And** 趋势图应包含平均值、最大值、最小值

**Given** 管理员在性能监控页面
**When** 点击导出性能报告
**Then** 系统应生成PDF格式报告
**And** 报告应包含所有性能指标和趋势分析

**Performance Requirements:**
- 性能监控页面加载时间少于2秒
- 指标数据更新延迟少于5秒
- 趋势图渲染时间少于1秒
- 报告生成时间少于10秒

**Accessibility Requirements:**
- 所有性能指标应有清晰的标签和说明
- 趋势图应提供文本描述
- 支持键盘导航查看不同指标
- 颜色区分应使用图标辅助

---

### Story 8.2: 响应式设计实现

作为一个用户，
我希望系统能够在不同设备上正常显示和使用，
以便随时随地访问系统功能。

**Acceptance Criteria:**

**Given** 用户在移动设备上访问系统（宽度<768px）
**When** 打开任意页面
**Then** 系统应使用移动端布局
**And** 所有元素应适合单手操作
**And** 导航应使用底部标签栏

**Given** 用户在平板设备上访问系统（宽度768px-1023px）
**When** 打开任意页面
**Then** 系统应使用平板布局
**And** 布局应适配横屏和竖屏
**And** 字体和间距应自动调整

**Given** 用户在桌面设备上访问系统（宽度>=1024px）
**When** 打开任意页面
**Then** 系统应使用桌面布局
**And** 内容应充分利用屏幕空间
**And** 导航应使用顶部导航栏

**Given** 用户在任意设备上访问系统
**When** 调整浏览器窗口大小
**Then** 系统应平滑过渡到对应布局
**And** 过渡动画应流畅自然
**And** 内容应保持可读性

**Given** 用户在移动设备上
**When** 查看列表内容
**Then** 系统应支持下拉刷新
**And** 系统应支持无限滚动加载
**And** 系统应支持滑动手势操作

**Performance Requirements:**
- 响应式布局切换时间少于100ms
- 媒体查询应使用CSS实现
- 图片应使用srcset实现响应式加载
- 字体应使用系统字体栈

**Accessibility Requirements:**
- 所有交互元素触摸目标至少44x44像素
- 颜色对比度符合WCAG 2.1 AA标准
- 支持缩放到200%时内容仍可读
- 移动端手势应提供替代操作方式

---

### Story 8.3: 无障碍功能实现

作为一个有视觉障碍的用户，
我希望能够通过屏幕阅读器和键盘完整使用系统，
以便独立完成所有操作。

**Acceptance Criteria:**

**Given** 用户使用屏幕阅读器访问系统
**When** 打开任意页面
**Then** 系统应正确朗读页面标题和内容
**And** 所有交互元素应有清晰的ARIA标签
**And** 图片应提供alt文本描述
**And** 表单元素应有关联的label

**Given** 用户使用键盘导航
**When** 按Tab键浏览页面
**Then** 焦点应按逻辑顺序移动
**And** 当前焦点应有明显的视觉指示器
**And** 所有交互元素应可通过键盘访问

**Given** 用户使用键盘导航
**When** 按Enter或Space键激活按钮
**Then** 系统应执行对应的操作
**And** 操作应有明确的声音或视觉反馈

**Given** 用户启用了高对比度模式
**When** 查看系统界面
**Then** 系统应使用高对比度配色
**And** 所有文本应清晰可读
**And** 图标应使用高对比度版本

**Given** 用户有视觉障碍
**When** 表单验证失败
**Then** 系统应通过屏幕阅读器播报错误信息
**And** 错误信息应明确指出问题位置
**And** 错误字段应获得焦点

**Performance Requirements:**
- ARIA标签不应影响页面性能
- 屏幕阅读器支持响应时间少于100ms
- 键盘导航响应时间少于50ms
- 高对比度模式切换时间少于200ms

**Accessibility Requirements:**
- 符合WCAG 2.1 Level AA标准
- 所有交互元素有正确的role属性
- 动态内容更新应使用aria-live区域
- 跳转导航链接应指向主内容区域

---

### Story 8.4: API文档集成

作为一个开发人员，
我希望能够通过Swagger UI查看完整的API文档，
以便快速了解和使用系统API。

**Acceptance Criteria:**

**Given** 开发人员访问API文档页面（/swagger-ui.html）
**When** 页面加载完成
**Then** 系统应显示所有API端点列表
**And** 每个端点应显示HTTP方法和路径
**And** 端点应按模块分组显示

**Given** 开发人员查看API详情
**When** 点击某个API端点
**Then** 系统应展开API详细信息
**And** 显示请求方法和URL
**And** 显示请求参数和请求体
**And** 显示响应示例和状态码

**Given** 开发人员测试API
**When** 在Swagger UI中执行API调用
**Then** 系统应发送真实请求到后端
**And** 系统应显示实际响应结果
**And** 系统应显示请求耗时

**Given** 开发人员需要认证的API
**When** 点击"Authorize"按钮
**Then** 系统应显示认证输入框
**And** 支持输入JWT token
**And** 认证成功后应显示授权状态

**Given** 开发人员查看API文档
**When** API发生变更
**Then** 文档应自动更新
**And** 变更历史应被记录
**And** 版本号应标注清晰

**Performance Requirements:**
- Swagger UI加载时间少于2秒
- API列表渲染时间少于1秒
- API详情展开时间少于500ms
- 测试请求响应时间显示准确

**Accessibility Requirements:**
- API文档应符合无障碍标准
- 支持键盘导航查看API信息
- 代码示例应有清晰的格式
- 支持屏幕阅读器朗读API文档

---

### Story 8.5: 第三方监控集成

作为一个系统管理员，
我希望能够将系统监控数据集成到Prometheus/Grafana，
以便使用成熟的监控工具进行深度分析。

**Acceptance Criteria:**

**Given** 系统配置了Prometheus集成
**When** Prometheus访问/metrics端点
**Then** 系统应返回Prometheus格式的指标数据
**And** 指标应包含：
  - HTTP请求计数和延迟
  - 数据库查询性能
  - 缓存命中率
  - JVM内存和GC指标
  - 系统CPU和内存使用率

**Given** 系统配置了Grafana集成
**When** Grafana查询Prometheus数据源
**Then** 系统应提供标准化的指标格式
**And** 指标应包含合理的标签（endpoint, method, status等）
**And** 指标命名应符合Prometheus规范

**Given** 管理员在Grafana中查看监控
**When** 配置告警规则
**Then** 系统应支持告警触发
**And** 告警应发送到配置的通知渠道
**And** 告警信息应包含详细上下文

**Given** 系统监控运行中
**When** 发生性能异常
**Then** 系统应记录详细的性能指标
**And** 指标应支持按时间范围查询
**And** 指标应支持聚合统计

**Given** 管理员查看监控数据
**When** 数据量较大时
**Then** 系统应支持数据采样
**And** 系统应支持数据降采样
**And** 历史数据查询响应时间少于5秒

**Performance Requirements:**
- /metrics端点响应时间少于500ms
- 指标采集间隔可配置（默认15秒）
- 指标数据传输开销少于1% CPU
- 监控集成不影响系统正常性能

**Accessibility Requirements:**
- 监控配置界面应符合无障碍标准
- 告警信息应提供文本描述
- 支持键盘操作配置监控规则

---

### Story 8.6: 设计系统组件库

作为一个前端开发人员，
我希望能够使用统一的设计系统组件库，
以便保持UI一致性并提高开发效率。

**Acceptance Criteria:**

**Given** 开发人员使用Vant组件库
**When** 引入组件到页面
**Then** 系统应自动应用设计系统样式
**And** 组件应使用Trust Blue (#1989FA)作为主色
**And** 组件应使用Success Green (#07C160)作为辅助色
**And** 危险操作应使用Red (#FF4444)

**Given** 开发人员使用组件
**When** 设置组件尺寸
**Then** 系统应提供小、中、大三种尺寸
**And** 小尺寸边框半径应为4px
**And** 中尺寸边框半径应为8px
**And** 大尺寸边框半径应为12px

**Given** 开发人员使用组件
**When** 设置组件间距
**Then** 系统应支持4px基础单位的间距
**And** 可用间距包括：4px, 8px, 12px, 16px, 20px, 24px

**Given** 开发人员使用组件
**When** 应用阴影效果
**Then** 系统应提供0-4级阴影
**And** 阴影级别应根据组件层级自动应用

**Given** 开发人员使用组件
**When** 使用字体排版
**Then** 系统应使用系统字体栈
**And** 字体大小应在10px-20px范围内
**And** 字体大小应按2px递增

**Given** 开发人员查看组件文档
**When** 打开组件文档
**Then** 系统应显示所有可用组件
**And** 每个组件应显示代码示例
**And** 组件应支持主题定制

**Performance Requirements:**
- 组件库打包体积少于500KB（gzipped）
- 组件渲染性能应达到60fps
- 组件切换动画流畅自然
- 组件样式编译时间少于30秒

**Accessibility Requirements:**
- 所有组件支持键盘导航
- 组件颜色对比度符合WCAG 2.1 AA标准
- 组件支持高对比度模式
- 组件交互元素有明确的焦点指示

---

### Story 8.7: 通用组件开发

作为一个前端开发人员，
我希望能够使用预定义的通用UI组件，
以便快速构建用户界面。

**Acceptance Criteria:**

**Given** 开发人员需要展示学校信息
**When** 使用SchoolRecommendCard组件
**Then** 组件应显示学校名称和Logo
**And** 组件应显示录取概率（使用颜色区分）
**And** 组件应显示关键信息（地区、类型、分数线）
**And** 组件应支持点击查看详情

**Given** 开发人员需要志愿选择功能
**When** 使用VolunteerPicker组件
**Then** 组件应支持拖拽排序
**And** 组件应显示志愿序号
**And** 组件应支持添加和删除学校
**And** 组件应限制志愿数量（最多8个）

**Given** 开发人员需要展示概率趋势
**When** 使用ProbabilityChart组件
**Then** 组件应显示录取概率随分数的变化曲线
**And** 组件应显示用户当前分数位置
**And** 组件应支持鼠标悬停显示详情
**And** 组件应支持缩放和平移

**Given** 开发人员需要政策可视化
**When** 使用PolicyVisualizer组件
**Then** 组件应以图表形式展示政策信息
**And** 组件应支持政策条款的交互式查看
**And** 组件应提供政策关键词高亮
**And** 组件应支持政策搜索

**Given** 开发人员需要方案对比
**When** 使用PlanComparison组件
**Then** 组件应并排显示多个模拟方案
**And** 组件应高亮显示方案差异
**And** 组件应支持方案数量动态调整
**And** 组件应支持导出对比结果

**Performance Requirements:**
- 组件渲染时间少于100ms
- 图表组件支持1000+数据点流畅渲染
- 拖拽操作响应时间少于16ms（60fps）
- 组件更新不应触发不必要的重渲染

**Accessibility Requirements:**
- 图表数据应提供文本替代描述
- 拖拽操作应提供键盘替代方案
- 颜色编码应配合图标或标签
- 所有组件支持屏幕阅读器

---

### Story 8.8: 缓存策略优化

作为一个系统架构师，
我希望系统能够自动缓存热点数据，
以便提高系统响应速度和降低数据库负载。

**Acceptance Criteria:**

**Given** 用户频繁访问学校信息
**When** 用户第一次请求学校数据
**Then** 系统应从数据库查询数据
**And** 系统应将数据写入Redis缓存
**And** 缓存键格式应为：school:school_id

**Given** 用户再次访问相同学校信息
**When** 用户请求相同学校数据
**Then** 系统应从Redis缓存读取数据
**And** 系统不应查询数据库
**And** 响应时间应少于100ms

**Given** 管理员更新学校信息
**When** 学校信息发生变化
**Then** 系统应更新数据库记录
**And** 系统应使对应的缓存失效
**And** 下次请求应从数据库重新加载

**Given** 系统运行中
**When** 访问数据热点统计
**Then** 系统应记录每个数据的访问频率
**And** 访问频率超过阈值的数据应自动缓存
**And** 缓存过期时间应根据数据热度动态调整

**Given** 缓存空间不足
**When** 需要淘汰缓存数据
**Then** 系统应使用LRU（最近最少使用）策略
**And** 系统应优先淘汰访问频率低的数据
**And** 系统应记录缓存淘汰日志

**Given** 系统启动时
**When** 需要预热缓存
**Then** 系统应加载热点数据到缓存
**And** 系统应预加载推荐学校列表
**And** 系统应预加载常用字典数据

**Performance Requirements:**
- 缓存命中率应大于80%
- 缓存读取响应时间少于10ms
- 缓存写入响应时间少于20ms
- 缓存失效操作响应时间少于50ms

**Accessibility Requirements:**
- 缓存策略对用户透明
- 缓存失败不应影响功能可用性
- 缓存异常应有适当的降级处理

---

### Story 8.9: 开发环境配置

作为一个开发人员，
我希望能够使用Docker快速搭建一致的开发环境，
以便避免环境差异导致的问题。

**Acceptance Criteria:**

**Given** 开发人员克隆项目代码
**When** 执行docker-compose up命令
**Then** 系统应自动启动所有服务
**And** 系统应启动MySQL数据库
**And** 系统应启动Redis缓存
**And** 系统应启动Spring Boot后端
**And** 系统应启动Vue前端开发服务器

**Given** 开发环境启动完成
**When** 访问前端开发地址（http://localhost:5173）
**Then** 系统应显示前端应用
**And** 系统应自动热更新代码变更
**And** 开发工具应集成到浏览器

**Given** 开发环境启动完成
**When** 访问后端API地址（http://localhost:8080）
**Then** 系统应返回健康检查响应
**And** 系统应显示API文档链接
**And** 系统应支持数据库自动初始化

**Given** 开发人员编写前端代码
**When** 保存文件
**Then** 系统应自动执行ESLint检查
**And** 系统应自动执行Prettier格式化
**And** 代码不符合规范时应显示警告

**Given** 开发人员编写后端代码
**When** 提交代码前
**Then** 系统应自动执行Checkstyle检查
**And** 系统应自动执行单元测试
**And** 代码不符合规范时应阻止提交

**Given** 开发人员需要查看日志
**When** 查看容器日志
**Then** 系统应将所有服务日志集中显示
**And** 日志应包含时间戳和日志级别
**And** 日志应支持按服务过滤

**Performance Requirements:**
- 开发环境启动时间少于60秒
- 代码热更新延迟少于1秒
- ESLint检查时间少于2秒
- 单元测试执行时间少于30秒

**Accessibility Requirements:**
- 开发工具应符合无障碍标准
- 日志信息应清晰可读
- 错误提示应有明确建议

---

### Story 8.10: 项目初始化和结构

作为一个开发人员，
我希望能够使用标准化的项目模板快速初始化项目，
以便确保项目结构一致和最佳实践。

**Acceptance Criteria:**

**Given** 开发人员初始化后端项目
**When** 使用Spring Initializr创建项目
**Then** 系统应生成标准Spring Boot项目结构
**And** 系统应包含所有必要依赖
**And** 系统应使用Java 17
**And** 系统应使用Spring Boot 3.3.6

**Given** 开发人员初始化前端项目
**When** 使用Vite TypeScript模板创建项目
**Then** 系统应生成标准Vue 3项目结构
**And** 系统应配置TypeScript
**And** 系统应配置Vite
**And** 系统应配置ESLint和Prettier

**Given** 开发人员初始化小程序项目
**When** 使用微信开发者工具创建项目
**Then** 系统应生成标准小程序项目结构
**And** 系统应集成Vant Weapp组件库
**And** 系统应配置小程序基础能力

**Given** 项目结构创建完成
**When** 查看后端目录结构
**Then** 系统应按功能模块组织包结构
**And** 每个模块应包含：controller, service, repository, entity, dto, vo
**And** 测试文件应与源文件同级

**Given** 项目结构创建完成
**When** 查看前端目录结构
**Then** 系统应按功能模块组织组件结构
**And** 组件应分类为：pages, components, composables, api, store, utils
**And** 测试文件应与源文件同级

**Given** 开发人员创建新功能模块
**When** 使用项目模板
**Then** 系统应提供标准的代码模板
**And** 系统应包含注释说明
**And** 系统应遵循命名规范

**Performance Requirements:**
- 项目初始化时间少于30秒
- 项目构建时间少于2分钟
- 开发服务器启动时间少于10秒

**Accessibility Requirements:**
- 项目模板应包含无障碍最佳实践
- 代码注释应清晰易懂
- 命名规范应遵循项目标准

---

**Epic 8 Summary:**
- 10 stories created
- NFRs covered: NFR3-NFR6, NFR23-NFR26, NFR33-NFR34
- UX-DRs covered: UX-DR1-UX-DR50
- ARs covered: AR9-AR12, AR17, AR39-AR44, AR45-AR50
- Key deliverables:
  * Performance monitoring and optimization
  * Responsive design implementation
  * Accessibility compliance (WCAG 2.1 AA)
  * API documentation integration
  * Third-party monitoring integration
  * Design system component library
  * Common component development
  * Cache strategy optimization
  * Development environment configuration
  * Project initialization and structure

---

**All Epics Summary:**
- Total 8 epics created
- Total 52 stories created
- All functional requirements (FR1-FR39) covered
- All non-functional requirements (NFR1-NFR34) covered
- All UX design requirements (UX-DR1-UX-DR50) covered
- All additional requirements (AR1-AR52) covered
