# 开发环境配置指南

## 目录

1. [开发环境概述](#开发环境概述)
2. [环境要求](#环境要求)
3. [后端开发环境](#后端开发环境)
4. [前端开发环境](#前端开发环境)
5. [微信小程序开发环境](#微信小程序开发环境)
6. [开发工具配置](#开发工具配置)
7. [代码规范和格式化](#代码规范和格式化)
8. [调试和测试](#调试和测试)
9. [开发流程](#开发流程)
10. [常见问题](#常见问题)

## 开发环境概述

志愿汇系统采用前后端分离的架构，开发环境需要配置以下核心组件：

- **后端**: Spring Boot + Java + Maven + PostgreSQL + Redis
- **前端**: Vue3 + Vite + TypeScript + Element Plus + Node.js
- **微信小程序**: 微信开发者工具
- **数据库**: PostgreSQL + Redis
- **开发工具**: IDE、版本控制、调试工具等

### 开发架构

```
┌─────────────────────────────────────────────────────────────┐
│                     开发环境架构                           │
├─────────────────────────────────────────────────────────────┤
│                                                           │
│  ┌─────────────┐      ┌─────────────┐      ┌─────────────┐  │
│  │   Frontend  │      │   Backend   │      │  WeChat     │  │
│  │   (Vue3)    │      │  (Spring    │      │  Mini-      │  │
│  │   Dev       │      │  Boot)      │      │  Program)   │  │
│  └─────────────┘      └─────────────┘      └─────────────┘  │
│           │                   │                   │         │
│           │                   │                   │         │
│  ┌─────────────┐      ┌─────────────┐      ┌─────────────┐  │
│  │   Node.js   │      │   JDK 8+    │      │   WeChat    │  │
│  │   Runtime   │      │   Runtime   │      │   DevTools  │  │
│  └─────────────┘      └─────────────┘      └─────────────┘  │
│           │                   │                   │         │
│  ┌─────────────┐      ┌─────────────┐      ┌─────────────┐  │
│  │   npm/yarn  │      │   Maven    │      │   微信开发者│  │
│  │   Package   │      │   Build     │      │   工具      │  │
│  └─────────────┘      └─────────────┘      └─────────────┘  │
│                                                           │
└─────────────────────────────────────────────────────────────┘
```

## 环境要求

### 硬件要求

| 开发组件 | 最低配置 | 推荐配置 | 说明 |
|----------|----------|----------|------|
| CPU | 2核心 | 4核心或以上 | 编译和运行 |
| 内存 | 8GB | 16GB或以上 | 运行IDE和多个服务 |
| 硬盘 | 50GB | 100GB或以上 | 存储代码和依赖 |
| 显示器 | 1920x1080 | 2560x1440或更高 | 多窗口开发 |

### 软件要求

#### 基础软件

| 软件名称 | 最低版本 | 推荐版本 | 用途 |
|----------|----------|----------|------|
| 操作系统 | Windows 10 / macOS 10.14 / Ubuntu 18.04 | Windows 11 / macOS 12+ / Ubuntu 20.04+ | 开发环境基础 |
| Git | 2.20+ | 2.30+ | 版本控制 |
| Node.js | 16+ | 18+ | 前端开发 |
| Java | JDK 8 | JDK 11+ | 后端开发 |
| Maven | 3.6+ | 3.8+ | 后端构建 |

#### 数据库软件

| 软件名称 | 最低版本 | 推荐版本 | 用途 |
|----------|----------|----------|------|
| PostgreSQL | 12+ | 13+ | 主数据库 |
| Redis | 6.0+ | 6.2+ | 缓存数据库 |

#### 开发工具

| 工具名称 | 用途 | 推荐工具 |
|----------|------|----------|
| IDE | 代码编辑和调试 | IntelliJ IDEA / VS Code |
| API测试 | API测试和调试 | Postman / Insomnia |
| 数据库管理 | 数据库操作和管理 | DBeaver / pgAdmin |
| 版本控制 | Git图形界面 | SourceTree / GitKraken |
| 容器管理 | Docker管理 | Docker Desktop |

## 后端开发环境

### 1. Java开发环境

#### 安装JDK

```bash
# Windows (使用Chocolatey)
choco install openjdk --version=11.0.12

# macOS (使用Homebrew)
brew install openjdk@11

# Ubuntu
sudo apt update
sudo apt install openjdk-11-jdk

# 验证安装
java -version
javac -version
```

#### 配置环境变量

```bash
# Windows
setx JAVA_HOME "C:\Program Files\Java\jdk-11"
setx PATH "%JAVA_HOME%\bin;%PATH%"

# macOS / Linux
echo 'export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64' >> ~/.bashrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
source ~/.bashrc
```

### 2. Maven环境

#### 安装Maven

```bash
# 下载Maven
wget https://archive.apache.org/dist/maven/maven-3/3.8.6/binaries/apache-maven-3.8.6-bin.zip

# 解压Maven
unzip apache-maven-3.8.6-bin.zip
sudo mv apache-maven-3.8.6 /opt/maven

# 配置环境变量
export M2_HOME=/opt/maven
export PATH=$M2_HOME/bin:$PATH
```

#### 配置Maven

创建 `~/.m2/settings.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
    
    <localRepository>${user.home}/.m2/repository</localRepository>
    
    <mirrors>
        <mirror>
            <id>aliyun</id>
            <mirrorOf>central</mirrorOf>
            <url>https://maven.aliyun.com/repository/central</url>
        </mirror>
        <mirror>
            <id>aliyun-public</id>
            <mirrorOf>public</mirrorOf>
            <url>https://maven.aliyun.com/repository/public</url>
        </mirror>
    </mirrors>
    
    <profiles>
        <profile>
            <id>jdk-11</id>
            <activation>
                <activeByDefault>true</activeByDefault>
                <jdk>11</jdk>
            </activation>
            <properties>
                <maven.compiler.source>11</maven.compiler.source>
                <maven.compiler.target>11</maven.compiler.target>
                <maven.compiler.compilerVersion>11</maven.compiler.compilerVersion>
            </properties>
        </profile>
    </profiles>
</settings>
```

### 3. 数据库环境

#### 安装PostgreSQL

```bash
# Windows (使用Chocolatey)
choco install postgresql

# macOS (使用Homebrew)
brew install postgresql

# Ubuntu
sudo apt update
sudo apt install postgresql postgresql-contrib

# 启动PostgreSQL
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

#### 配置数据库

```bash
# 登录PostgreSQL
sudo -u postgres psql

# 创建数据库和用户
CREATE DATABASE volunteer_db;
CREATE USER volunteer_user WITH PASSWORD 'dev_password';
GRANT ALL PRIVILEGES ON DATABASE volunteer_db TO volunteer_user;
\q

# 设置信任连接
sudo nano /etc/postgresql/13/main/pg_hba.conf
```

修改 `pg_hba.conf`：

```
# 允许本地连接
local   all             all                                     md5
host    all             all             127.0.0.1/32            md5
host    all             all             ::1/128                 md5
```

#### 安装Redis

```bash
# Windows (使用Chocolatey)
choco install redis-64

# macOS (使用Homebrew)
brew install redis

# Ubuntu
sudo apt update
sudo apt install redis-server

# 启动Redis
sudo systemctl start redis
sudo systemctl enable redis
```

### 4. 后端项目配置

```bash
# 克隆项目
git clone https://github.com/yourusername/volunteer-system.git
cd volunteer-system/backend

# 创建配置文件
cp src/main/resources/application-dev.yml.example src/main/resources/application-dev.yml

# 修改配置
nano src/main/resources/application-dev.yml
```

配置文件示例：

```yaml
server:
  port: 8080
  servlet:
    context-path: /api/v1

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/volunteer_db
    username: volunteer_user
    password: dev_password
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      idle-timeout: 30000
      connection-timeout: 20000
      connection-test-query: SELECT 1

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true

  redis:
    host: localhost
    port: 6379
    password: dev_password
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0

  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

logging:
  level:
    root: INFO
    com.chapt003: DEBUG
    org.springframework.web: DEBUG
```

### 5. 运行后端服务

```bash
# 编译项目
mvn clean compile

# 运行测试
mvn test

# 运行应用
mvn spring-boot:run

# 打包应用
mvn package
```

## 前端开发环境

### 1. Node.js环境

#### 安装Node.js

```bash
# Windows (使用Chocolatey)
choco install nodejs

# macOS (使用Homebrew)
brew install node

# Ubuntu
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs

# 验证安装
node --version
npm --version
```

#### 使用nvm管理Node版本

```bash
# 安装nvm
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash

# 重新加载shell
source ~/.bashrc

# 安装Node.js
nvm install 18

# 设置默认版本
nvm alias default 18
```

### 2. 前端项目配置

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 创建环境配置文件
cp .env.example .env.development
```

配置文件示例：

```bash
# .env.development
VITE_API_BASE_URL=http://localhost:8080/api/v1
VITE_APP_TITLE=志愿汇系统 - 开发环境
VITE_APP_ENV=development
VITE_APP_VERSION=1.0.0-dev
```

### 3. 开发服务器配置

```bash
# 启动开发服务器
npm run dev

# 访问地址
# http://localhost:5173 (Vite默认端口)

# 查看更多命令
npm run
```

### 4. TypeScript配置

```json
{
  "compilerOptions": {
    "target": "ES2020",
    "useDefineForClassFields": true,
    "module": "ESNext",
    "lib": ["ES2020", "DOM", "DOM.Iterable"],
    "skipLibCheck": true,
    "moduleResolution": "bundler",
    "allowImportingTsExtensions": true,
    "resolveJsonModule": true,
    "isolatedModules": true,
    "noEmit": true,
    "jsx": "preserve",
    "strict": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noFallthroughCasesInSwitch": true,
    "baseUrl": ".",
    "paths": {
      "@/*": ["src/*"],
      "@/components/*": ["src/components/*"],
      "@/views/*": ["src/views/*"],
      "@/utils/*": ["src/utils/*"],
      "@/api/*": ["src/api/*"],
      "@/types/*": ["src/types/*"]
    }
  },
  "include": ["src/**/*.ts", "src/**/*.d.ts", "src/**/*.tsx", "src/**/*.vue"],
  "references": [{ "path": "./tsconfig.node.json" }]
}
```

### 5. 前端开发工具

#### VS Code配置

创建 `.vscode/settings.json`：

```json
{
  "editor.formatOnSave": true,
  "editor.codeActionsOnSave": {
    "source.fixAll.eslint": true
  },
  "editor.defaultFormatter": "esbenp.prettier-vscode",
  "typescript.preferences.importModuleSpecifier": "relative",
  "vue.complete.casing.tags": "pascal",
  "vue.complete.casing.props": "camel",
  "emmet.includeLanguages": {
    "vue-html": "html",
    "typescript": "html"
  },
  "files.associations": {
    "*.css": "postcss"
  },
  "eslint.validate": [
    "javascript",
    "javascriptreact",
    "typescript",
    "typescriptreact",
    "vue"
  ],
  "vetur.validation.template": false,
  "vetur.validation.script": false,
  "vetur.validation.style": false
}
```

创建 `.vscode/extensions.json`：

```json
{
  "recommendations": [
    "vue.volar",
    "vue.vscode-typescript-vue-plugin",
    "johnsoncodehk.volar",
    "esbenp.prettier-vscode",
    "dbaeumer.vscode-eslint",
    "ms-vscode.vscode-json",
    "bradlc.vscode-tailwindcss",
    "christian-kohler.path-intellisense",
    "ms-vscode.vscode-json"
  ]
}
```

#### ESLint配置

创建 `.eslintrc.js`：

```javascript
module.exports = {
  root: true,
  env: {
    node: true,
    browser: true,
    es2021: true
  },
  extends: [
    'plugin:vue/vue3-essential',
    'eslint:recommended',
    '@vue/typescript/recommended'
  ],
  parserOptions: {
    ecmaVersion: 2021,
    parser: '@typescript-eslint/parser'
  },
  rules: {
    'no-console': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    '@typescript-eslint/no-explicit-any': 'warn',
    '@typescript-eslint/no-unused-vars': 'warn',
    'vue/no-unused-vars': 'warn',
    'vue/no-unused-components': 'warn',
    'vue/require-default-prop': 'warn',
    'vue/require-explicit-emits': 'warn'
  }
}
```

#### Prettier配置

创建 `.prettierrc`：

```json
{
  "semi": false,
  "singleQuote": true,
  "trailingComma": "none",
  "printWidth": 100,
  "tabWidth": 2,
  "useTabs": false,
  "bracketSpacing": true,
  "arrowParens": "avoid",
  "endOfLine": "lf"
}
```

## 微信小程序开发环境

### 1. 微信开发者工具

#### 安装工具

1. 下载微信开发者工具
   - 访问 [https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html](https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html)
   - 下载对应系统的安装包
   - 安装并启动工具

2. 登录微信账号
   - 使用微信扫码登录
   - 确保开发者权限

#### 工具配置

```json
// 工具设置
{
  "editor.fontSize": 14,
  "editor.tabSize": 2,
  "editor.wordWrap": "on",
  "editor.minimap.enabled": false,
  "editor.renderWhitespace": "boundary",
  "editor.renderLineHighlight": "gutter",
  "editor.codeLens": true,
  "debug.consoleFontSize": 14,
  "debug.console.autoExpand": true
}
```

### 2. 小程序项目配置

#### 项目配置文件

```json
// app.json
{
  "pages": [
    "pages/index/index",
    "pages/projects/projects",
    "pages/applications/applications",
    "pages/profile/profile",
    "pages/settings/settings"
  ],
  "window": {
    "backgroundTextStyle": "light",
    "navigationBarBackgroundColor": "#1989fa",
    "navigationBarTitleText": "志愿汇",
    "navigationBarTextStyle": "white"
  },
  "tabBar": {
    "color": "#999999",
    "selectedColor": "#1989fa",
    "backgroundColor": "#ffffff",
    "borderStyle": "black",
    "list": [
      {
        "pagePath": "pages/index/index",
        "text": "首页",
        "iconPath": "images/home.png",
        "selectedIconPath": "images/home-active.png"
      },
      {
        "pagePath": "pages/projects/projects",
        "text": "项目",
        "iconPath": "images/project.png",
        "selectedIconPath": "images/project-active.png"
      },
      {
        "pagePath": "pages/applications/applications",
        "text": "申请",
        "iconPath": "images/application.png",
        "selectedIconPath": "images/application-active.png"
      },
      {
        "pagePath": "pages/profile/profile",
        "text": "我的",
        "iconPath": "images/profile.png",
        "selectedIconPath": "images/profile-active.png"
      }
    ]
  },
  "debug": true,
  "sitemapLocation": "sitemap.json"
}
```

#### 环境配置

```javascript
// utils/config.js
const config = {
  // 开发环境
  development: {
    baseUrl: 'http://localhost:8080/api/v1',
    env: 'dev'
  },
  // 测试环境
  testing: {
    baseUrl: 'http://test.example.com/api/v1',
    env: 'test'
  },
  // 生产环境
  production: {
    baseUrl: 'https://api.example.com/api/v1',
    env: 'prod'
  }
}

// 获取当前环境配置
function getConfig() {
  return config[process.env.NODE_ENV || 'development']
}

module.exports = getConfig()
```

### 3. 小程序开发工具

#### 微信开发者工具功能

1. **代码编辑**
   - 语法高亮
   - 自动补全
   - 错误提示
   - 代码格式化

2. **调试功能**
   - Console控制台
   - Network网络监控
   - Sources源码调试
   - AppData数据查看

3. **预览功能**
   - 模拟器预览
   - 真机预览
   - 页面性能分析

4. **发布功能**
   - 代码提交
   - 版本管理
   - 审核提交

#### 常用快捷键

```bash
# 代码编辑
Ctrl + S - 保存文件
Ctrl + Z - 撤销
Ctrl + Y - 重做
Ctrl + D - 复制当前行
Ctrl + / - 注释/取消注释

# 调试功能
F5 - 开始调试
F10 - 单步跳过
F11 - 单步进入
Shift + F5 - 停止调试

# 页面预览
Ctrl + R - 刷新页面
Ctrl + Shift + R - 强制刷新
```

## 开发工具配置

### 1. IDE配置

#### IntelliJ IDEA配置

```xml
<!-- 项目设置 -->
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="CompilerConfiguration">
    <option name="DEFAULT_COMPILER" value="Javac" />
    <resourceExtensions>
      <directory url="file://$PROJECT_DIR$" recursive="false" />
    </resourceExtensions>
    <wildcardResourcePatterns>
      <entry name="!?*.java" />
      <entry name="!?*.form" />
      <entry name="!?*.class" />
      <entry name="!?*.groovy" />
      <entry name="!?*.scala" />
      <entry name="!?*.flex" />
      <entry name="!?*.kt" />
      <entry name="!?*.clj" />
    </wildcardResourcePatterns>
  </component>
</project>
```

#### VS Code工作区

```json
// .vscode/settings.json
{
  "editor.formatOnSave": true,
  "editor.codeActionsOnSave": {
    "source.fixAll.eslint": true
  },
  "typescript.preferences.importModuleSpecifier": "relative",
  "vue.complete.casing.tags": "pascal",
  "vue.complete.casing.props": "camel",
  "emmet.includeLanguages": {
    "vue-html": "html",
    "typescript": "html"
  },
  "eslint.validate": [
    "javascript",
    "javascriptreact",
    "typescript",
    "typescriptreact",
    "vue"
  ],
  "vetur.validation.template": false,
  "vetur.validation.script": false,
  "vetur.validation.style": false,
  "files.associations": {
    "*.css": "postcss"
  }
}
```

### 2. 数据库工具配置

#### DBeaver配置

```sql
-- 创建数据库连接
-- 主机: localhost
-- 端口: 5432
-- 数据库: volunteer_db
-- 用户名: volunteer_user
-- 密码: dev_password

-- 创建表
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    avatar_url VARCHAR(255),
    role VARCHAR(20) DEFAULT 'volunteer',
    status VARCHAR(20) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
```

#### Redis工具配置

```bash
# Redis Desktop Manager配置
# 连接信息:
# 主机: localhost
# 端口: 6379
# 密码: dev_password
# 数据库: 0

# Redis命令示例
redis-cli
AUTH dev_password
SET key value
GET key
```

### 3. API测试工具配置

#### Postman配置

```json
{
  "info": {
    "_postman_id": "your-postman-collection-id",
    "name": "志愿汇API测试",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "用户管理",
      "item": [
        {
          "name": "用户注册",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"username\": \"testuser\",\n  \"email\": \"test@example.com\",\n  \"password\": \"password123\",\n  \"full_name\": \"测试用户\"\n}"
            },
            "url": {
              "raw": "http://localhost:8080/api/v1/auth/register",
              "protocol": "http",
              "host": ["localhost"],
              "port": "8080",
              "path": ["api", "v1", "auth", "register"]
            }
          }
        },
        {
          "name": "用户登录",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"username\": \"testuser\",\n  \"password\": \"password123\"\n}"
            },
            "url": {
              "raw": "http://localhost:8080/api/v1/auth/login",
              "protocol": "http",
              "host": ["localhost"],
              "port": "8080",
              "path": ["api", "v1", "auth", "login"]
            }
          }
        }
      ]
    }
  ]
}
```

## 代码规范和格式化

### 1. 代码规范

#### Java代码规范

```java
// 命名规范
public class UserService {
    private UserRepository userRepository;
    
    // 方法命名：动词 + 名词
    public User createUser(UserDTO userDTO) {
        // 参数验证
        if (userDTO == null) {
            throw new IllegalArgumentException("User DTO cannot be null");
        }
        
        // 业务逻辑
        User user = convertToEntity(userDTO);
        return userRepository.save(user);
    }
    
    // 私有方法：动词 + 名词
    private User convertToEntity(UserDTO userDTO) {
        // 转换逻辑
        return new User();
    }
}
```

#### Vue3代码规范

```vue
<template>
  <!-- 组件命名：PascalCase -->
  <UserList 
    :users="users"
    @user-selected="handleUserSelected"
    :loading="loading"
  />
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { User } from '@/types/user'
import UserList from '@/components/UserList.vue'

// 响应式数据：驼峰命名
const users = ref<User[]>([])
const loading = ref(false)

// 方法：动词 + 名词
const handleUserSelected = (user: User) => {
  console.log('User selected:', user)
}

const loadUsers = async () => {
  loading.value = true
  try {
    users.value = await fetchUsers()
  } catch (error) {
    console.error('Failed to load users:', error)
  } finally {
    loading.value = false
  }
}

// 生命周期
onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
/* CSS类名：kebab-case */
.user-list {
  padding: 16px;
}

.user-list-item {
  margin-bottom: 12px;
}
</style>
```

#### 微信小程序代码规范

```javascript
// 文件命名：kebab-case
// 组件命名：PascalCase

// 页面文件
Page({
  data: {
    // 数据字段：camelCase
    userInfo: null,
    loading: false,
    projectList: []
  },

  onLoad() {
    // 生命周期方法
    this.loadProjects()
  },

  // 事件处理：动词 + 名词
  handleProjectSelect(e) {
    const projectId = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/project-detail/project-detail?id=${projectId}`
    })
  },

  // 数据加载方法
  async loadProjects() {
    this.setData({ loading: true })
    
    try {
      const res = await wx.request({
        url: `${this.config.baseUrl}/projects`,
        method: 'GET'
      })
      
      this.setData({
        projectList: res.data.data
      })
    } catch (error) {
      console.error('Failed to load projects:', error)
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      })
    } finally {
      this.setData({ loading: false })
    }
  }
})
```

### 2. 代码格式化

#### ESLint配置

```javascript
// .eslintrc.js
module.exports = {
  root: true,
  env: {
    node: true,
    browser: true,
    es2021: true
  },
  extends: [
    'plugin:vue/vue3-essential',
    'eslint:recommended',
    '@vue/typescript/recommended'
  ],
  parserOptions: {
    ecmaVersion: 2021,
    parser: '@typescript-eslint/parser'
  },
  rules: {
    'no-console': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    '@typescript-eslint/no-explicit-any': 'warn',
    '@typescript-eslint/no-unused-vars': 'warn',
    'vue/no-unused-vars': 'warn',
    'vue/no-unused-components': 'warn',
    'vue/require-default-prop': 'warn',
    'vue/require-explicit-emits': 'warn'
  }
}
```

#### Prettier配置

```json
{
  "semi": false,
  "singleQuote": true,
  "trailingComma": "none",
  "printWidth": 100,
  "tabWidth": 2,
  "useTabs": false,
  "bracketSpacing": true,
  "arrowParens": "avoid",
  "endOfLine": "lf"
}
```

#### Git钩子配置

```bash
# 安装husky
npm install husky --save-dev
npx husky install

# 添加pre-commit钩子
npx husky add .husky/pre-commit "npm run lint"

# 添加commit-msg钩子
npx husky add .husky/commit-msg "npx --no-install commitlint --edit $1"
```

#### Commitlint配置

```javascript
// commitlint.config.js
module.exports = {
  extends: ['@commitlint/config-conventional'],
  rules: {
    'type-enum': [
      2,
      'always',
      [
        'feat',     // 新功能
        'fix',      // 修复bug
        'docs',     // 文档更新
        'style',    // 代码格式化
        'refactor', // 重构
        'perf',     // 性能优化
        'test',     // 测试
        'chore',    // 构建工具或依赖管理
        'revert',   // 回滚
        'wip'       // 进行中的工作
      ]
    ],
    'type-case': [2, 'always', 'lower-case'],
    'type-empty': [2, 'never'],
    'scope-case': [2, 'always', 'lower-case'],
    'subject-case': [2, 'never', ['sentence-case', 'start-case', 'pascal-case', 'upper-case']],
    'subject-empty': [2, 'never'],
    'subject-full-stop': [2, 'never', '.'],
    'header-max-length': [2, 'always', 72],
    'body-leading-blank': [1, 'always'],
    'body-max-line-length': [2, 'always', 100],
    'footer-leading-blank': [1, 'always'],
    'footer-max-line-length': [2, 'always', 100]
  }
}
```

## 调试和测试

### 1. 调试配置

#### 后端调试

```java
// Spring Boot调试配置
@SpringBootApplication
public class VolunteerApplication {
    public static void main(String[] args) {
        SpringApplication.run(VolunteerApplication.class, args);
    }
}
```

#### 前端调试

```javascript
// Vue3调试配置
import { createApp } from 'vue'
import App from './App.vue'
import { createPinia } from 'pinia'

const app = createApp(App)

// 开发环境配置
if (import.meta.env.DEV) {
  app.config.devtools = true
  app.config.performance = true
}

app.use(createPinia())
app.mount('#app')
```

#### 微信小程序调试

```javascript
// app.js
App({
  onLaunch() {
    // 开发环境调试
    if (process.env.NODE_ENV === 'development') {
      console.log('App launched in development mode')
    }
    
    // 初始化逻辑
    this.init()
  },
  
  init() {
    // 初始化代码
  }
})
```

### 2. 单元测试

#### Java测试

```java
// UserServiceTest.java
@SpringBootTest
@AutoConfigureMockMvc
class UserServiceTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Test
    void testCreateUser() throws Exception {
        UserDTO userDTO = new UserDTO("testuser", "test@example.com", "password123", "测试用户");
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        
        when(userService.createUser(userDTO)).thenReturn(user);
        
        mockMvc.perform(post("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(userDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("testuser"));
    }
}
```

#### Vue3测试

```javascript
// UserList.spec.ts
import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import UserList from '@/components/UserList.vue'

describe('UserList', () => {
  it('renders users correctly', () => {
    const users = [
      { id: 1, username: 'user1', email: 'user1@example.com' },
      { id: 2, username: 'user2', email: 'user2@example.com' }
    ]
    
    const wrapper = mount(UserList, {
      props: { users }
    })
    
    expect(wrapper.findAll('.user-item')).toHaveLength(2)
  })
  
  it('emits user-selected event when user clicked', async () => {
    const users = [{ id: 1, username: 'user1', email: 'user1@example.com' }]
    const wrapper = mount(UserList, {
      props: { users }
    })
    
    await wrapper.find('.user-item').trigger('click')
    expect(wrapper.emitted('user-selected')).toBeTruthy()
  })
})
```

#### 微信小程序测试

```javascript
// pages/index/index.spec.js
describe('首页测试', () => {
  it('测试首页加载', () => {
    const app = getApp()
    expect(app.globalData).toBeDefined()
  })
  
  it('测试用户统计信息', () => {
    const page = Page({
      data: {
        stats: {
          totalProjects: 10,
          myApplications: 3,
          completedHours: 20
        }
      }
    })
    
    expect(page.data.stats.totalProjects).toBe(10)
    expect(page.data.stats.myApplications).toBe(3)
    expect(page.data.stats.completedHours).toBe(20)
  })
})
```

### 3. 集成测试

#### API测试

```javascript
// api.test.js
import request from 'supertest'
import app from '../src/app'

describe('API Integration Tests', () => {
  test('POST /api/v1/auth/register - 创建用户', async () => {
    const userData = {
      username: 'testuser',
      email: 'test@example.com',
      password: 'password123',
      full_name: '测试用户'
    }
    
    const response = await request(app)
      .post('/api/v1/auth/register')
      .send(userData)
      .expect(201)
    
    expect(response.body).toHaveProperty('id')
    expect(response.body.username).toBe('testuser')
    expect(response.body.email).toBe('test@example.com')
  })
  
  test('POST /api/v1/auth/login - 用户登录', async () => {
    const loginData = {
      username: 'testuser',
      password: 'password123'
    }
    
    const response = await request(app)
      .post('/api/v1/auth/login')
      .send(loginData)
      .expect(200)
    
    expect(response.body).toHaveProperty('token')
    expect(response.body).toHaveProperty('user')
  })
})
```

## 开发流程

### 1. Git工作流

#### 分支管理策略

```bash
# 主分支
main           # 主分支，用于生产环境
develop        # 开发分支，用于集成开发

# 功能分支
feature/user-auth     # 用户认证功能
feature/project-mgmt   # 项目管理功能
feature/notification  # 通知系统功能

# 修复分支
fix/login-bug        # 登录功能修复
fix/ui-responsive     # UI响应式修复

# 发布分支
release/v1.0.0       # v1.0.0版本发布
```

#### 提交规范

```bash
# 提交消息格式
<type>(<scope>): <description>

# 类型说明
feat:     新功能
fix:      修复bug
docs:     文档更新
style:    代码格式化
refactor: 重构
perf:     性能优化
test:     测试
chore:    构建工具或依赖管理
revert:   回滚
wip:      进行中的工作

# 示例
feat(user): 添加用户注册功能
fix(auth): 修复登录验证失败问题
docs(api): 更新API文档
style: 格式化代码
```

#### 工作流程

```bash
# 1. 创建功能分支
git checkout develop
git pull origin develop
git checkout -b feature/user-auth

# 2. 开发功能
git add .
git commit -m "feat(auth): 添加用户注册功能"
git push origin feature/user-auth

# 3. 创建Pull Request
# 在GitHub/GitLab上创建PR

# 4. 代码审查
# 等待团队成员审查

# 5. 合并分支
# 审查通过后合并到develop分支

# 6. 发布版本
git checkout main
git merge develop
git tag v1.0.0
git push origin main --tags
```

### 2. 开发流程

#### 需求分析

```markdown
# 需求文档
## 需求描述
- 功能描述
- 用户故事
- 业务流程

## 技术方案
- 技术选型
- 架构设计
- 数据库设计
- 接口设计

## 开发计划
- 任务分解
- 时间安排
- 人员分配
```

#### 设计阶段

```markdown
# 设计文档
## 界面设计
- UI原型图
- 交互设计
- 响应式设计

## 数据设计
- 数据库ER图
- 数据字典
- 接口文档

## 架构设计
- 系统架构图
- 模块划分
- 接口规范
```

#### 开发阶段

```bash
# 1. 环境准备
git clone <repository>
cd volunteer-system

# 2. 安装依赖
cd frontend && npm install
cd ../backend && mvn clean install

# 3. 数据库初始化
cd ../backend
mvn flyway:migrate

# 4. 启动服务
cd frontend && npm run dev &
cd ../backend && mvn spring-boot:run

# 5. 功能开发
# 开发新功能

# 6. 测试
npm run test
mvn test

# 7. 提交代码
git add .
git commit -m "feat: 添加新功能"
git push
```

#### 测试阶段

```bash
# 1. 单元测试
npm run test
mvn test

# 2. 集成测试
npm run test:integration
mvn verify

# 3. 端到端测试
npm run test:e2e
mvn spring-boot:test

# 4. 性能测试
npm run test:performance
mvn gatling:test
```

#### 部署阶段

```bash
# 1. 构建项目
npm run build
mvn clean package

# 2. 部署到测试环境
./scripts/deploy-to-test.sh

# 3. 测试环境验证
curl http://test.example.com/health

# 4. 部署到生产环境
./scripts/deploy-to-prod.sh

# 5. 生产环境验证
curl http://prod.example.com/health
```

### 3. 持续集成/持续部署

#### GitHub Actions配置

```yaml
# .github/workflows/ci.yml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    
    services:
      postgres:
        image: postgres:13
        env:
          POSTGRES_DB: test_db
          POSTGRES_USER: test_user
          POSTGRES_PASSWORD: test_password
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
      
      redis:
        image: redis:6-alpine
        options: >-
          --health-cmd redis-cli ping
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

    steps:
    - uses: actions/checkout@v3
    
    - name: Setup Node.js
      uses: actions/setup-node@v3
      with:
        node-version: '18'
        cache: 'npm'
    
    - name: Install Frontend Dependencies
      run: cd frontend && npm install
    
    - name: Install Java and Maven
      uses: actions/setup-java@v3
      with:
        java-version: '11'
        distribution: 'temurin'
        cache: 'maven'
    
    - name: Build Frontend
      run: cd frontend && npm run build
    
    - name: Run Frontend Tests
      run: cd frontend && npm run test
    
    - name: Build Backend
      run: cd backend && mvn clean compile
    
    - name: Run Backend Tests
      run: cd backend && mvn test
    
    - name: Run Integration Tests
      run: cd backend && mvn verify
    
    - name: Build Docker Images
      run: |
        cd backend && docker build -t volunteer-backend .
        cd frontend && docker build -t volunteer-frontend .
    
    - name: Deploy to Staging
      if: github.ref == 'refs/heads/main'
      run: |
        echo "Deploying to staging environment..."
        # 部署脚本
```

#### Jenkins配置

```groovy
// Jenkinsfile
pipeline {
    agent any
    
    environment {
        NODE_VERSION = '18'
        JAVA_VERSION = '11'
        MAVEN_VERSION = '3.8.6'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Setup') {
            steps {
                script {
                    // 设置Node.js
                    sh "nvm use ${NODE_VERSION}"
                    sh "npm install -g npm@latest"
                    
                    // 设置Java和Maven
                    sh "export JAVA_HOME=/usr/lib/jvm/java-${JAVA_VERSION}-openjdk-amd64"
                    sh "export PATH=\$JAVA_HOME/bin:\$PATH"
                    sh "export MAVEN_HOME=/opt/maven"
                    sh "export PATH=\$MAVEN_HOME/bin:\$PATH"
                }
            }
        }
        
        stage('Build Frontend') {
            steps {
                sh 'cd frontend && npm install'
                sh 'cd frontend && npm run build'
            }
        }
        
        stage('Build Backend') {
            steps {
                sh 'cd backend && mvn clean compile'
            }
        }
        
        stage('Test') {
            parallel {
                stage('Frontend Tests') {
                    steps {
                        sh 'cd frontend && npm run test'
                    }
                }
                stage('Backend Tests') {
                    steps {
                        sh 'cd backend && mvn test'
                    }
                }
            }
        }
        
        stage('Integration Tests') {
            steps {
                sh 'cd backend && mvn verify'
            }
        }
        
        stage('Deploy') {
            steps {
                script {
                    if (env.BRANCH_NAME == 'main') {
                        sh './scripts/deploy-to-prod.sh'
                    } else {
                        sh './scripts/deploy-to-staging.sh'
                    }
                }
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
            emailext (
                subject: "Pipeline Failed: ${env.JOB_NAME} - ${env.BUILD_NUMBER}",
                body: """
                Build failed for ${env.JOB_NAME} - ${env.BUILD_NUMBER}
                
                Build URL: ${env.BUILD_URL}
                """,
                to: "${env.CHANGE_AUTHOR_EMAIL}, team@example.com"
            )
        }
    }
}
```

## 常见问题

### 1. 环境配置问题

#### Java环境问题

```bash
# 问题：Java版本不兼容
# 解决方案：确保使用正确的Java版本
java -version
# 应该显示 Java 11

# 如果版本不正确，使用nvm管理
nvm install 11
nvm use 11
```

#### Node.js环境问题

```bash
# 问题：npm安装依赖失败
# 解决方案：清理缓存并重新安装
npm cache clean --force
rm -rf node_modules package-lock.json
npm install

# 或者使用yarn
yarn install
```

#### 数据库连接问题

```bash
# 问题：PostgreSQL连接失败
# 解决方案：检查PostgreSQL服务状态和配置
sudo systemctl status postgresql
sudo systemctl start postgresql

# 检查数据库配置
nano /etc/postgresql/13/main/postgresql.conf
nano /etc/postgresql/13/main/pg_hba.conf

# 测试连接
psql -U volunteer_user -d volunteer_db
```

### 2. 开发工具问题

#### IDE配置问题

```bash
# 问题：IDE无法识别TypeScript
# 解决方案：确保TypeScript正确安装
npm install -D typescript @types/node

# 检查TypeScript版本
npx tsc --version

# 重新生成tsconfig
npx tsc --init
```

#### VS Code插件问题

```bash
# 问题：Vue3插件不工作
# 解决方案：更新VS Code和插件
code --version

# 更新插件
code --install-extension johnsoncodehk.volar
code --install-extension vue.volar
```

### 3. 代码问题

#### 编译错误

```java
// 问题：Java编译错误
// 解决方案：检查导入和语法
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class User {
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;
    
    // 其他字段和方法
}
```

#### TypeScript错误

```typescript
// 问题：TypeScript类型错误
// 解决方案：检查类型定义
interface User {
  id: number;
  username: string;
  email: string;
}

const user: User = {
  id: 1,
  username: 'test',
  email: 'test@example.com'
};
```

### 4. 调试问题

#### 后端调试

```java
// 问题：Spring Boot应用启动失败
// 解决方案：检查端口和配置
@SpringBootApplication
public class VolunteerApplication {
    public static void main(String[] args) {
        // 设置端口号
        System.setProperty("server.port", "8080");
        SpringApplication.run(VolunteerApplication.class, args);
    }
}
```

#### 前端调试

```javascript
// 问题：Vue3应用启动失败
// 解决方案：检查依赖和配置
import { createApp } from 'vue'
import App from './App.vue'

const app = createApp(App)
app.mount('#app')
```

### 5. 部署问题

#### Docker构建问题

```bash
# 问题：Docker构建失败
# 解决方案：检查Dockerfile配置
docker build -t volunteer-backend .

# 查看详细错误
docker build --no-cache -t volunteer-backend .
```

#### 服务启动问题

```bash
# 问题：服务无法启动
# 解决方案：检查端口占用和日志
netstat -tulpn | grep :8080
docker logs volunteer-backend
```

---

*本文档最后更新时间: 2024-01-15*
*版本: 1.0.0*
*适用版本: 志愿汇系统 v1.0.0*