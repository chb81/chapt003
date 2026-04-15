# 志愿填报系统 - 管理平台

基于 Vue3 + Vite + ElementPlus + TypeScript 的 PC 端管理平台。

## 技术栈

- Vue 3.4+
- Vite 5.0+
- TypeScript 5.3+
- Element Plus 2.5+
- Vue Router 4.2+
- Pinia 2.1+
- Axios 1.6+

## 项目结构

```
frontend/
├── public/              # 静态资源
│   ├── api-test.html   # API集成测试页面
├── src/
│   ├── api/            # API 请求
│   ├── assets/         # 资源文件
│   ├── components/     # 公共组件
│   ├── router/         # 路由配置
│   ├── stores/         # 状态管理
│   ├── tests/          # 单元测试
│   ├── types/          # TypeScript 类型定义
│   ├── views/          # 页面组件
│   ├── App.vue         # 根组件
│   └── main.ts         # 入口文件
├── e2e/                # E2E测试
│   ├── user-management.spec.ts  # 用户管理E2E测试
│   └── basic.spec.ts           # 基础功能测试
├── tests/              # 测试工具
│   └── setup.ts        # 测试配置
├── vitest.config.ts    # 单元测试配置
├── playwright.config.ts # E2E测试配置
├── INTEGRATION-REPORT.md # 集成测试报告
├── index.html          # HTML 模板
├── package.json        # 项目配置
├── tsconfig.json       # TypeScript 配置
├── vite.config.ts      # Vite 配置
└── README.md          # 项目文档
```

## 快速开始

### 安装依赖
## 快速开始

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run dev
```

访问 http://localhost:5173

### 构建生产版本

```bash
npm run build
```

### 预览生产版本

```bash
npm run preview
```

## 功能特性

### 用户管理页面 (`/users`)
- 用户列表展示
- 搜索用户（用户名/邮箱/手机号）
- 按角色筛选
- 按状态筛选
- 分页显示
- 查看用户详情

### 用户详情页面 (`/users/:id`)
- 用户基本信息展示
- 登录历史记录（时间轴）
- 操作审计日志（时间轴）
- 修改用户角色
- 启用/禁用用户
- 删除用户（软删除）

## API 代理

开发环境下，前端会代理 `/api` 请求到后端服务 `http://localhost:8080`。

## 认证

前端使用 JWT Token 进行认证，Token 存储在 localStorage 中。
所有 API 请求都会在请求头中携带 `Authorization: Bearer {token}`。

## 状态标签

- **正常** (ACTIVE) - 绿色
- **禁用** (DISABLED) - 红色
- **未验证** (UNVERIFIED) - 橙色
- **已删除** (DELETED) - 灰色
