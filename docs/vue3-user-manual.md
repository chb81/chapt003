# Vue3 前端用户手册

## 目录

1. [系统概述](#系统概述)
2. [快速开始](#快速开始)
3. [用户登录](#用户登录)
4. [功能模块详解](#功能模块详解)
5. [数据可视化](#数据可视化)
6. [智能推荐](#智能推荐)
7. [通知管理](#通知管理)
8. [报表生成](#报表生成)
9. [系统设置](#系统设置)
10. [快捷键](#快捷键)
11. [常见问题](#常见问题)

## 系统概述

志愿汇系统前端采用 Vue3 + Vite + TypeScript + Element Plus 构建，提供现代化的管理界面和丰富的交互体验。

### 技术栈

- **框架**: Vue 3 (Composition API)
- **构建工具**: Vite
- **开发语言**: TypeScript
- **UI 组件库**: Element Plus
- **状态管理**: Pinia
- **路由管理**: Vue Router
- **HTTP 客户端**: Axios
- **测试框架**: Vitest + Playwright

### 系统架构

```
前端应用架构
├── 视图层 (Views)
│   ├── 用户管理 (UserList, UserDetail)
│   ├── 数据看板 (Dashboard)
│   └── 其他页面
├── 组件层 (Components)
│   ├── 通知面板 (NotificationsPanel)
│   ├── 推荐系统 (Recommendations)
│   └── 报表生成 (ReportGenerator)
├── 服务层 (Services)
│   ├── API 请求 (request.ts)
│   ├── 状态管理 (Pinia Store)
│   └── WebSocket 通信
└── 工具层 (Utils)
    ├── 性能优化 (performance.ts)
    ├── 通知管理 (notifications.ts)
    ├── 推荐算法 (recommendation.ts)
    └── 报表工具 (report.ts)
```

## 快速开始

### 环境要求

- Node.js: 18.0.0 或更高版本
- npm: 9.0.0 或更高版本
- 现代浏览器 (Chrome 90+, Firefox 88+, Safari 14+)

### 安装步骤

1. **克隆项目**
```bash
git clone <repository-url>
cd frontend
```

2. **安装依赖**
```bash
npm install
```

3. **启动开发服务器**
```bash
npm run dev
```

4. **访问系统**
- 打开浏览器访问: `http://localhost:5173`
- 系统会自动重定向到用户管理页面

### 构建部署

```bash
# 开发环境构建
npm run build

# 预览构建结果
npm run preview

# 生产环境构建
npm run build:prod
```

## 用户登录

### 登录流程

1. **访问登录页面**
   - 系统启动后自动重定向到登录页面
   - 或直接访问 `/login`

2. **输入登录信息**
   - 用户名: 系统分配的管理员账号
   - 密码: 初始密码或修改后的密码
   - 验证码: 可选的安全验证

3. **登录成功**
   - 自动保存 token 到 localStorage
   - 跳转到用户管理页面
   - 记录登录日志

### 自动登录

- 系统会自动检查 localStorage 中是否有有效的 token
- 如果 token 有效且未过期，自动登录并跳转到管理页面
- Token 过期时间: 24小时

### 退出登录

- 点击右上角用户头像
- 选择"退出登录"选项
- 清除本地存储的 token
- 跳转到登录页面

## 功能模块详解

### 用户管理

#### 主要功能
- 用户列表查看
- 用户详情查看
- 用户状态管理 (启用/禁用)
- 用户角色管理
- 用户信息编辑
- 批量操作

#### 操作流程

1. **查看用户列表**
   - 访问 `/users` 页面
   - 支持搜索 (用户名、邮箱、手机号)
   - 支持筛选 (角色、状态)
   - 支持排序 (创建时间、更新时间)

2. **查看用户详情**
   - 点击用户列表中的用户名或查看详情按钮
   - 显示用户基本信息、权限信息、活动记录
   - 支持编辑用户信息

3. **管理用户状态**
   - 启用/禁用用户账号
   - 重置用户密码
   - 删除用户账号

#### 界面元素

| 元素 | 说明 | 操作方式 |
|------|------|----------|
| 搜索框 | 输入关键词搜索用户 | 输入关键词，按回车或点击搜索 |
| 筛选器 | 按角色、状态筛选用户 | 选择下拉选项，点击搜索 |
| 分页器 | 浏览不同页面的用户数据 | 点击页码或使用上一页/下一页 |
| 操作按钮 | 对用户进行各种操作 | 点击相应按钮 |

### 数据看板

#### 主要功能
- 实时数据展示
- 图表可视化
- 数据趋势分析
- 性能监控
- 用户活跃度分析

#### 图表类型

1. **用户增长趋势图**
   - 显示新增用户数量随时间的变化
   - 支持时间范围选择 (7天、30天、90天)
   - 支持导出数据

2. **用户活跃度分析**
   - 显示活跃用户数量
   - 按时间维度展示活跃度
   - 支持对比分析

3. **系统性能监控**
   - 响应时间监控
   - 错误率统计
   - 资源使用情况

#### 交互功能

- **图表缩放**: 鼠标滚轮缩放图表
- **数据筛选**: 选择时间范围和数据类型
- **数据导出**: 支持导出为 Excel、PDF 格式
- **全屏显示**: 点击全屏按钮查看大图

### 智能推荐

#### 主要功能
- 活动推荐
- 用户匹配
- 个性化推荐
- 推荐效果分析

#### 推荐算法

1. **基于内容的推荐**
   - 分析用户历史参与的活动
   - 推荐相似类型的新活动
   - 权重: 40%

2. **协同过滤推荐**
   - 分析用户相似行为
   - 推荐相似用户喜欢的活动
   - 权重: 35%

3. **基于规则的推荐**
   - 基于用户属性和活动规则
   - 推荐符合条件的高质量活动
   - 权重: 25%

#### 推荐设置

| 设置项 | 说明 | 默认值 |
|--------|------|--------|
| 推荐数量 | 每次推荐的活动数量 | 5个 |
| 更新频率 | 推荐数据更新频率 | 24小时 |
| 过滤规则 | 推荐结果过滤条件 | 无 |
| 权重调整 | 各算法权重调整 | 默认权重 |

### 通知管理

#### 主要功能
- 实时通知接收
- 通知分类管理
- 通知优先级设置
- 通知模板管理

#### 通知类型

1. **系统通知**
   - 系统更新公告
   - 维护通知
   - 功能变更通知

2. **用户通知**
   - 用户注册审核
   - 活动申请审批
   - 用户反馈处理

3. **业务通知**
   - 活动开始提醒
   - 报表生成完成
   - 异常情况预警

#### 优先级设置

| 优先级 | 图标 | 颜色 | 处理方式 |
|--------|------|------|----------|
| 高 | 🔴 | 红色 | 立即处理 |
| 中 | 🟡 | 黄色 | 及时处理 |
| 低 | 🔵 | 蓝色 | 常规处理 |

### 报表生成

#### 主要功能
- 报表模板管理
- 定时报表生成
- 报表导出功能
- 报表历史记录

#### 报表类型

1. **用户报表**
   - 用户增长报表
   - 用户活跃度报表
   - 用户分布报表

2. **活动报表**
   - 活动参与报表
   - 活动效果报表
   - 活动满意度报表

3. **系统报表**
   - 系统性能报表
   - 错误统计报表
   - 资源使用报表

#### 报表模板

| 模板名称 | 包含内容 | 生成周期 | 导出格式 |
|----------|----------|----------|----------|
| 日用户报表 | 新增用户、活跃用户 | 每日 | PDF, Excel |
| 周活动报表 | 活动统计、参与度 | 每周 | PDF, Excel |
| 月度总结 | 系统统计、用户反馈 | 每月 | PDF, Excel |

## 数据可视化

### 图表组件

#### 1. 折线图 (Line Chart)
- **用途**: 展示数据趋势
- **适用场景**: 用户增长、活跃度变化
- **配置选项**: 时间范围、数据维度、颜色主题

#### 2. 柱状图 (Bar Chart)
- **用途**: 对比数据差异
- **适用场景**: 不同类型用户数量对比
- **配置选项**: 排序方式、显示值、颜色方案

#### 3. 饼图 (Pie Chart)
- **用途**: 展示数据占比
- **适用场景**: 用户角色分布、活动类型分布
- **配置选项**: 显示标签、图例位置、颜色主题

#### 4. 散点图 (Scatter Chart)
- **用途**: 展示数据相关性
- **适用场景**: 用户年龄与活跃度关系
- **配置选项**: 点大小、透明度、趋势线

### 图表配置

#### 基本配置

```typescript
interface ChartConfig {
  type: 'line' | 'bar' | 'pie' | 'scatter';
  title: string;
  data: ChartData;
  options: ChartOptions;
}
```

#### 数据格式

```typescript
interface ChartData {
  labels: string[];
  datasets: Dataset[];
}

interface Dataset {
  label: string;
  data: number[];
  backgroundColor?: string;
  borderColor?: string;
  borderWidth?: number;
}
```

### 性能优化

#### 图表性能优化

1. **数据采样**: 大数据集采用采样策略
2. **虚拟渲染**: 仅渲染可见区域的数据
3. **缓存机制**: 缓存计算结果和渲染数据
4. **按需加载**: 按需加载图表数据和样式

## 智能推荐

### 推荐算法详解

#### 1. 基于内容的推荐 (Content-Based)

**原理**: 分析用户历史参与的活动内容和属性，推荐相似的新活动。

**优势**:
- 不需要其他用户数据
- 推荐结果可解释性强
- 冷启动问题较少

**实现**:
```typescript
// 计算活动相似度
function calculateActivitySimilarity(activity1: Activity, activity2: Activity): number {
  const categoryWeight = 0.4;
  const locationWeight = 0.3;
  const tagWeight = 0.3;
  
  const categorySimilarity = activity1.category === activity2.category ? 1 : 0;
  const locationSimilarity = calculateLocationDistance(activity1.location, activity2.location);
  const tagSimilarity = calculateTagSimilarity(activity1.tags, activity2.tags);
  
  return categoryWeight * categorySimilarity + 
         locationWeight * locationSimilarity + 
         tagWeight * tagSimilarity;
}
```

#### 2. 协同过滤推荐 (Collaborative Filtering)

**原理**: 分析用户之间的相似行为，推荐相似用户喜欢的活动。

**优势**:
- 发现用户潜在兴趣
- 推荐结果多样化
- 不需要内容特征分析

**实现**:
```typescript
// 计算用户相似度
function calculateUserSimilarity(user1: User, user2: User): number {
  const commonActivities = user1.activities.filter(activity => 
    user2.activities.includes(activity)
  );
  
  if (commonActivities.length === 0) return 0;
  
  const intersectionSize = commonActivities.length;
  const unionSize = user1.activities.length + user2.activities.length - intersectionSize;
  
  return intersectionSize / unionSize; // Jaccard 相似度
}
```

#### 3. 基于规则的推荐 (Rule-Based)

**原理**: 基于预设的业务规则和用户属性进行推荐。

**优势**:
- 规则明确，易于理解
- 可快速调整策略
- 适合特定场景推荐

**实现**:
```typescript
// 基于规则推荐
function recommendByRules(user: User, activities: Activity[]): Activity[] {
  return activities.filter(activity => {
    // 规则1: 用户所在城市优先推荐
    if (user.city !== activity.city) {
      return false;
    }
    
    // 规则2: 用户年龄符合活动要求
    if (activity.ageRange && 
        (user.age < activity.ageRange.min || user.age > activity.ageRange.max)) {
      return false;
    }
    
    // 规则3: 排除用户已参与的活动
    if (user.participatedActivities.includes(activity.id)) {
      return false;
    }
    
    return true;
  });
}
```

### 推荐效果分析

#### 评价指标

| 指标 | 计算公式 | 含义 | 目标值 |
|------|----------|------|--------|
| 准确率 | TP / (TP + FP) | 推荐结果的准确程度 | > 0.8 |
| 召回率 | TP / (TP + FN) | 推荐覆盖的完整性 | > 0.6 |
| F1分数 | 2 * (P * R) / (P + R) | 综合评价指标 | > 0.7 |
| 点击率 | Clicks / Impressions | 用户点击推荐的比例 | > 0.15 |
| 转化率 | Conversions / Clicks | 用户参与活动的比例 | > 0.1 |

#### 效果优化

1. **A/B 测试**
   - 分组测试不同推荐策略
   - 对比关键指标差异
   - 选择最优策略

2. **参数调优**
   - 调整算法权重
   - 优化特征提取
   - 调整推荐数量

3. **反馈学习**
   - 收集用户反馈
   - 持续优化算法
   - 更新推荐策略

## 通知管理

### 通知类型详解

#### 1. 系统通知

**触发条件**:
- 系统版本更新
- 服务器维护
- 安全漏洞修复
- 功能上线公告

**推送方式**:
- 邮件通知
- 系统内消息
- 短信通知 (重要通知)

#### 2. 用户通知

**触发条件**:
- 用户注册审核结果
- 活动申请审批结果
- 用户反馈处理结果
- 账户状态变更

**推送方式**:
- 系统内消息
- 邮件通知
- 微信通知 (配置微信小程序时)

#### 3. 业务通知

**触发条件**:
- 活动开始提醒
- 报表生成完成
- 异常情况预警
- 数据统计完成

**推送方式**:
- 系统内消息
- WebSocket 实时推送
- 邮件通知

### 通知配置

#### 通知规则配置

| 规则名称 | 触发条件 | 通知类型 | 通知方式 |
|----------|----------|----------|----------|
| 用户注册审核 | 用户注册 | 系统通知 | 邮件、系统内 |
| 活动申请审批 | 活动申请 | 用户通知 | 系统、微信 |
| 系统维护提醒 | 维护前2小时 | 系统通知 | 邮件、短信 |
| 报表完成通知 | 报表生成 | 业务通知 | 系统、邮件 |

#### 通知模板

```typescript
interface NotificationTemplate {
  id: string;
  name: string;
  title: string;
  content: string;
  type: 'system' | 'user' | 'business';
  priority: 'high' | 'medium' | 'low';
  channels: ('email' | 'sms' | 'wechat' | 'system')[];
  variables: string[];
}
```

### 通知队列管理

#### 优先级队列

```typescript
class NotificationQueue {
  private highPriority: Notification[] = [];
  private mediumPriority: Notification[] = [];
  private lowPriority: Notification[] = [];
  
  addNotification(notification: Notification) {
    switch (notification.priority) {
      case 'high':
        this.highPriority.push(notification);
        break;
      case 'medium':
        this.mediumPriority.push(notification);
        break;
      case 'low':
        this.lowPriority.push(notification);
        break;
    }
  }
  
  getNextNotification(): Notification | null {
    // 高优先级队列不为空时，优先处理
    if (this.highPriority.length > 0) {
      return this.highPriority.shift()!;
    }
    // 中优先级队列不为空时，处理中优先级
    if (this.mediumPriority.length > 0) {
      return this.mediumPriority.shift()!;
    }
    // 低优先级队列不为空时，处理低优先级
    if (this.lowPriority.length > 0) {
      return this.lowPriority.shift()!;
    }
    // 所有队列都为空，返回null
    return null;
  }
}
```

## 报表生成

### 报表模板管理

#### 模板创建

```typescript
interface ReportTemplate {
  id: string;
  name: string;
  description: string;
  type: 'user' | 'activity' | 'system';
  config: ReportConfig;
  schedule?: ReportSchedule;
}
```

#### 模板配置

| 配置项 | 说明 | 可选值 |
|--------|------|--------|
| 报表类型 | 报表数据类型 | user, activity, system |
| 数据源 | 数据来源 | api, database, manual |
| 时间范围 | 数据时间范围 | custom, daily, weekly, monthly |
| 图表类型 | 图表显示类型 | line, bar, pie, scatter |
| 导出格式 | 导出文件格式 | pdf, excel, csv, json |

### 定时报表

#### 调度配置

```typescript
interface ReportSchedule {
  frequency: 'daily' | 'weekly' | 'monthly';
  time: string; // HH:mm 格式
  days?: number[]; // 仅在weekly时使用
  recipients: string[]; // 收件人列表
  attachments?: string[]; // 附件列表
}
```

#### 调度示例

```typescript
// 每日报表配置
const dailyReport: ReportSchedule = {
  frequency: 'daily',
  time: '09:00',
  recipients: ['admin@example.com', 'manager@example.com'],
  attachments: ['daily_report.pdf']
};

// 每周报表配置
const weeklyReport: ReportSchedule = {
  frequency: 'weekly',
  time: '10:00',
  days: [1], // 周一
  recipients: ['admin@example.com']
};
```

### 报表生成流程

1. **数据收集**
   - 从 API 或数据库获取数据
   - 数据清洗和格式化
   - 数据验证和完整性检查

2. **数据处理**
   - 数据聚合和计算
   - 统计分析
   - 趋势预测

3. **报表生成**
   - 模板渲染
   - 图表生成
   - 格式转换

4. **分发发送**
   - 邮件发送
   - 文件存储
   - 通知推送

## 系统设置

### 个性化设置

#### 界面设置

| 设置项 | 说明 | 可选值 |
|--------|------|--------|
| 主题颜色 | 界面主题色 | blue, green, purple, red |
| 字体大小 | 文字显示大小 | small, medium, large |
| 语言设置 | 界面显示语言 | zh-CN, en-US |
| 时区设置 | 时间显示时区 | UTC+8, UTC+0, UTC-5 |

#### 通知设置

| 设置项 | 说明 | 可选值 |
|--------|------|--------|
| 通知频率 | 推送通知频率 | real-time, hourly, daily |
| 通知方式 | 接收通知方式 | email, sms, wechat, system |
| 静音时间 | 免打扰时间段 | 22:00-08:00 |
| 通知优先级 | 通知显示优先级 | all, high, medium |

### 数据设置

#### 数据源配置

```typescript
interface DataSourceConfig {
  type: 'api' | 'database' | 'file';
  url: string;
  credentials?: {
    username: string;
    password: string;
  };
  refreshInterval: number; // 刷新间隔（秒）
}
```

#### 数据同步

```typescript
interface DataSyncConfig {
  enabled: boolean;
  schedule: string; // cron 表达式
  conflictResolution: 'overwrite' | 'merge' | 'skip';
  syncDirection: 'upload' | 'download' | 'bidirectional';
}
```

### 安全设置

#### 访问控制

| 设置项 | 说明 | 配置选项 |
|--------|------|----------|
| 会话超时 | 用户会话有效期 | 30分钟、1小时、24小时 |
| 登录尝试 | 最大登录尝试次数 | 3次、5次、10次 |
| 密码策略 | 密码复杂度要求 | 长度、字符类型、历史记录 |
| IP限制 | 允许访问的IP范围 | 允许列表、禁止列表 |

#### 数据加密

```typescript
interface SecurityConfig {
  encryption: {
    enabled: boolean;
    algorithm: 'AES-256' | 'RSA-2048';
    keyRotation: number; // 密钥轮换周期（天）
  };
  audit: {
    enabled: boolean;
    logLevel: 'info' | 'warn' | 'error';
    retention: number; // 日志保留天数
  };
}
```

## 快捷键

### 通用快捷键

| 快捷键 | 功能 | 适用范围 |
|--------|------|----------|
| Ctrl + S | 保存当前页面 | 所有页面 |
| Ctrl + R | 刷新当前页面 | 所有页面 |
| Ctrl + F | 搜索功能 | 列表页面 |
| Ctrl + P | 打印页面 | 报表页面 |
| Esc | 关闭弹窗 | 所有弹窗 |

### 导航快捷键

| 快捷键 | 功能 | 页面 |
|--------|------|------|
| Alt + 1 | 用户管理 | 所有页面 |
| Alt + 2 | 数据看板 | 所有页面 |
| Alt + 3 | 智能推荐 | 所有页面 |
| Alt + 4 | 通知管理 | 所有页面 |
| Alt + 5 | 报表生成 | 所有页面 |

### 数据操作快捷键

| 快捷键 | 功能 | 适用场景 |
|--------|------|----------|
| Ctrl + N | 新建项目 | 用户管理、活动管理 |
| Ctrl + E | 编辑项目 | 列表页面 |
| Ctrl + D | 删除项目 | 列表页面 |
| Ctrl + A | 全选 | 列表页面 |
| Ctrl + I | 反选 | 列表页面 |

### 图表操作快捷键

| 快捷键 | 功能 | 图表类型 |
|--------|------|----------|
| + | 放大图表 | 所有图表 |
| - | 缩小图表 | 所有图表 |
| 0 | 重置缩放 | 所有图表 |
| F11 | 全屏显示 | 所有图表 |
| Space | 暂停动画 | 动态图表 |

## 常见问题

### 安装和启动问题

#### Q: npm install 失败
**A**: 检查 Node.js 版本是否 >= 18.0.0，清除 npm 缓存后重试：
```bash
npm cache clean --force
npm install
```

#### Q: 启动开发服务器失败
**A**: 检查端口是否被占用，修改端口：
```bash
npm run dev -- --port 3000
```

#### Q: TypeScript 类型错误
**A**: 确保 TypeScript 版本 >= 5.0.0，重新安装依赖：
```bash
npm install typescript@latest
npm install
```

### 登录问题

#### Q: 登录失败
**A**: 
1. 检查用户名和密码是否正确
2. 确认账号状态是否正常
3. 清除浏览器缓存和 Cookie
4. 检查网络连接

#### Q: Token 过期
**A**: 
1. 重新登录获取新 Token
2. 检查系统时间是否正确
3. 联系管理员延长 Token 有效期

#### Q: 权限不足
**A**: 
1. 确认用户角色和权限
2. 联系管理员分配相应权限
3. 检查权限配置是否正确

### 数据显示问题

#### Q: 图表不显示
**A**: 
1. 检查数据源是否正常
2. 确认数据格式是否正确
3. 检查浏览器兼容性
4. 查看控制台错误信息

#### Q: 列表数据加载慢
**A**: 
1. 检查网络连接
2. 优化查询条件
3. 分页加载大量数据
4. 使用数据缓存

#### Q: 报表生成失败
**A**: 
1. 检查数据完整性
2. 确认模板配置正确
3. 检查磁盘空间
4. 查看生成日志

### 性能问题

#### Q: 页面响应慢
**A**: 
1. 清理浏览器缓存
2. 检查网络带宽
3. 优化前端代码
4. 使用 CDN 加速

#### Q: 内存占用高
**A**: 
1. 关闭不必要的标签页
2. 清理浏览器缓存
3. 检查内存泄漏
4. 重启浏览器

### 兼容性问题

#### Q: 浏览器兼容性
**A**: 
- 推荐使用 Chrome 90+, Firefox 88+, Safari 14+
- 禁用 IE 浏览器
- 启用 JavaScript 支持

#### Q: 移动设备适配
**A**: 
- 使用响应式设计
- 支持触摸操作
- 优化移动端显示

## 联系支持

### 技术支持
- **邮箱**: support@example.com
- **电话**: 400-123-4567
- **工单系统**: [在线提交工单](https://support.example.com)

### 文档资源
- **API 文档**: [查看 API 文档](./api.md)
- **开发文档**: [查看开发文档](./DEPLOYMENT.md)
- **更新日志**: [查看更新日志](./CHANGELOG.md)

### 社区支持
- **GitHub**: [项目主页](https://github.com/example/project)
- **文档**: [在线文档](https://docs.example.com)
- **论坛**: [用户社区](https://forum.example.com)

---

*本文档最后更新时间: 2024-01-15*
*版本: 1.0.0*