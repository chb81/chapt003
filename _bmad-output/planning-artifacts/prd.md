---
stepsCompleted: ["step-01-init.md", "step-02-discovery.md", "step-02b-vision.md", "step-02c-executive-summary.md", "step-03-success.md", "step-05-domain.md", "step-07-project-type.md", "step-08-scoping.md", "step-09-functional.md", "step-10-nonfunctional.md", "step-11-polish.md"]
inputDocuments: ["d:\\opt\\traswork\\chapt003\\_bmad-output\\brainstorming\\brainstorming-session-2026-04-11-0913.md"]
workflowType: 'prd'
documentCounts:
  briefs: 0
  research: 0
  brainstorming: 1
  projectDocs: 0
classification:
  projectType: "Web应用 + 管理平台（多端架构）"
  domain: "教育技术"
  complexity: "高"
  projectContext: "绿地项目"
  techStack:
    backend: "Java SpringBoot"
    adminFrontend: "Vue3 + Vite + ElementPlus + TypeScript"
    platforms: "PC Web、管理平台、微信小程序、公众号、H5"
vision:
  coreProblem: "家长缺乏一个可以实时模拟志愿填报、多维度分析学校优劣、提供科学决策依据的平台"
  keyPainPoints:
    - "分数不够考不上高中"
    - "高分进入较差的高中（分配生/指标生政策复杂导致择校错误）"
  competitiveAdvantages:
    smarter: "智能推荐学校、智能预测录取概率"
    moreComprehensive:
      - "历年录取数据的深度分析"
      - "分配生/指标生名额的实时查询"
      - "不同填报策略的模拟对比"
    moreConvenient: "操作更简单、随时随地访问"
  visionTransformations:
    - "从'不知道怎么填'变成'有策略地填'"
    - "从'凭感觉填'变成'凭数据填'"
    - "从'担心填错'变成'有信心填'"
    - "从'一次性决策工具'变成'全程陪伴伙伴'"
  timing: "政策变化、技术成熟、市场需求都是构建这个系统的最佳时机"
  trustFramework:
    drQuinn: "可信赖性 = 透明度 + 可追溯性 + 持续验证"
    winston: "三层架构 - 数据质量层 + 算法透明层 + 反馈闭环层"
    maya: "信任旅程 - 视觉信任 → 交互信任 → 价值信任"
---

# Product Requirements Document - chapt003

**Author:** chb81
**Date:** 2026-04-11

## Executive Summary

中考志愿填报系统是一个面向考生家长的智能决策支持平台，解决家长在复杂的中考录取政策下的志愿填报焦虑。系统通过实时模拟填报、多维度学校分析和科学决策依据，帮助家长从"凭感觉填"转变为"凭数据填"，从"担心填错"转变为"有信心填"。

核心问题：家长缺乏一个可以实时模拟志愿填报、多维度分析学校优劣、提供科学决策依据的平台。关键痛点包括：分数不够考不上高中；高分进入较差的高中（分配生/指标生政策复杂导致择校错误）。

目标用户是面临中考志愿填报决策的考生家长，特别是那些对分配生/指标生政策不熟悉、担心择校错误的焦虑型家长。

### What Makes This Special

相比传统志愿填报系统，本产品在三个维度上具有显著优势：

**更智能**：提供智能学校推荐和录取概率预测，帮助家长理解复杂的分配生/指标生政策影响。

**更全面**：提供历年录取数据的深度分析、分配生/指标生名额的实时查询、不同填报策略的模拟对比，覆盖家长决策所需的所有关键信息维度。

**更方便**：操作简单，支持PC Web、管理平台、微信小程序、公众号、H5多端访问，随时随地可用。

**从工具到伙伴**：不仅是决策工具，更是全程陪伴的数字伙伴，通过透明度+可追溯性+持续验证建立可信赖的决策支持关系。

核心洞察：可信赖的决策支持 = 透明度 + 可追溯性 + 持续验证。系统通过三层架构（数据质量层 + 算法透明层 + 反馈闭环层）和信任旅程设计（视觉信任 → 交互信任 → 价值信任），将原始数据转化为家长可信赖的决策依据。

---

## Project Classification

- **项目类型**：Web应用 + 管理平台（多端架构）
- **领域**：教育技术
- **复杂度**：高
- **项目上下文**：绿地项目
- **技术栈**：后端基于Java SpringBoot框架，管理端采用Vue3 + Vite + ElementPlus + TypeScript
- **平台支持**：PC Web、管理平台、微信小程序、公众号、H5

---

## Success Criteria

### User Success

1. **对目标高中有了清晰的了解**：用户在系统中查看的学校信息完整度 > 90%（用户查看了学校的关键信息如排名、特色、历年录取线等）

2. **对自己孩子有准确的定位**：系统生成的学生定位信息被用户标记为"准确"的比例 > 85%

3. **系统最大限度的帮助家长降低志愿填报风险**：用户使用系统后，"担心填错"的焦虑评分降低 > 60%（通过问卷或反馈收集）

4. **准确的推荐几所目标高中**：系统推荐的前3所学校中，用户最终填报至少1所的比例 > 70%

5. **使家长及考生不再迷茫**：用户在完成系统分析后，"我知道该填什么了"的信心评分 > 4.0/5.0

### Business Success

（暂不讨论，待后续补充）

### Technical Success

1. **高可用、安全、高并发**：系统支持中考填报高峰期的高并发访问，确保99.9%可用性，符合数据安全法规要求

2. **预测准确性**：录取概率预测准确率控制在90%以上，系统提供预测依据和置信区间

3. **数据实时性**：分配生/指标生名额数据发布后，第一时间导入系统，确保用户获取最新信息

### Measurable Outcomes

- 用户成功5个维度的评分达到以上阈值
- 系统预测准确率 > 90%
- 高峰期系统可用性 > 99.9%
- 数据更新延迟 < 24小时（政策发布后）

---

## Product Scope

### MVP - Minimum Viable Product

**开发周期**：3-4个月

**核心功能模块**：
1. **学校信息数据库**：学校基础信息（名称、排名、特色、历年录取分数线）、分配生/指标生名额数据、学校筛选和查询功能
2. **学生评估系统**：输入学生分数和排名，输出学生在校内和全市的相对定位，基础优势劣势分析
3. **录取概率预测引擎**：基于历史数据的录取概率预测（准确率90%+），考虑分配生/指标生政策影响，展示预测依据和置信区间
4. **智能学校推荐**：基于分数和概率推荐3-5所目标高中（冲刺、稳妥、保底各1-2所），展示推荐理由
5. **决策支持仪表盘**：一站式展示学生定位、学校信息、推荐结果，清晰的决策依据和风险提示
6. **多端支持**：微信小程序（核心功能）+ PC Web（深度分析功能），高可用、安全、高并发基础架构

### Growth Features (Post-MVP)

- 360度学生画像（性格分析、潜力挖掘）
- 深度匹配度分析（学校DNA匹配）
- VR校园体验
- 动态调整建议
- AI对话式交互

### Vision (Future)

- AI生命导师
- 全程护航系统（3年持续跟踪）
- 失败保险机制
- 社区化经验分享

---

## Domain-Specific Requirements

### 合规与监管

#### 学生隐私保护
- **模拟填报阶段**：用户可使用化名，系统不收集个人信息
- **数据最小化**：仅收集必要的成绩和排名数据，不收集学生真实姓名
- **本地化存储**：用户数据优先在本地存储，减少云端数据存储
- **免责声明**：系统需要明确的免责声明，说明预测结果仅供参考

#### 内容合规
- **信息来源**：学校信息、录取分数线从公开渠道收集
- **预测声明**：所有预测结果需标注"仅供参考，不作为录取依据"
- **备案要求**：需要ICP备案，可能需要教育应用备案

### 技术约束

#### 性能要求
- **并发用户数**：支持高峰期1000个并发用户
- **响应时间**：预测和查询操作在10秒内完成
- **可用性**：中考填报高峰期保持99.9%可用性

#### 安全要求
- **数据传输**：使用HTTPS加密传输
- **数据存储**：敏感数据（成绩、排名）加密存储
- **访问控制**：管理员分级权限，操作日志审计

#### 认证与授权
- **登录方式**：支持微信登录（无需复杂密码）
- **账号安全**：基础安全措施，不过度复杂化用户体验
- **权限管理**：家长端、管理端、学校端、教育局端、客服端分级权限

### 集成需求

#### 数据集成
- **无教育局同步**：不与教育局建立实时数据同步机制
- **手动数据导入**：分配生/指标生名额由管理员手动导入
- **批量导入支持**：支持Excel等格式的批量数据导入

#### 第三方集成
- **微信集成**：微信小程序、微信登录、微信推送通知
- **未来扩展**：预留与学校、教育局系统对接的接口（MVP之后）

### 架构设计

#### 模块化设计
- **分配生功能独立**：作为相对独立的功能模块，便于快速调整
- **政策配置化**：政策规则通过配置文件或数据库表存储
- **可扩展性**：预留扩展接口，支持未来功能扩展

#### 降级策略
- **高峰期降级**：负载过高时关闭非核心功能（如深度分析、VR体验等）
- **缓存机制**：常用数据缓存，减少数据库查询压力
- **限流措施**：单用户查询频率限制，防止系统过载

### 风险缓解

#### 数据准确性风险
- **数据校验**：导入数据时自动校验格式和完整性
- **异常检测**：自动识别数据异常（如名额大幅变化）
- **人工审核**：异常数据需人工确认后发布
- **数据溯源**：记录每条数据的来源、导入时间、修改历史

#### 算法准确性风险
- **置信区间**：预测结果提供置信区间
- **历史验证**：基于历史数据验证预测准确性
- **用户反馈**：收集用户反馈，持续优化算法
- **免责声明**：明确声明预测结果仅供参考

#### 系统稳定性风险
- **监控预警**：实时监控系统状态，异常情况自动预警
- **快速响应**：建立应急响应机制，快速处理故障
- **备份恢复**：定期数据备份，灾难恢复预案
- **负载测试**：高峰前进行压力测试，确保系统稳定

#### 政策变化风险
- **功能解耦**：分配生功能独立设计，降低政策变化影响
- **快速调整**：政策规则配置化，支持快速调整
- **用户通知**：政策变化后通过微信推送通知用户
- **数据版本管理**：记录政策版本变化，支持回溯

### 未来考虑（Post-MVP）

- **无障碍访问**：支持视力障碍、听力障碍用户（大字体、语音朗读、高对比度）
- **多语言支持**：支持英文等其他语言版本
- **离线功能**：支持离线查询学校信息和历史数据
- **数据导出**：支持PDF、Excel格式的报告导出
- **深度合规**：满足更严格的教育数据保护法规要求

---

## Web应用 + 管理平台特定需求

### 项目类型概述
这是一个多端响应式的Web应用，采用SPA架构，支持手机、平板、桌面三种设备类型，需要SEO优化和无障碍访问支持。

### 技术架构考虑

#### 1. 应用架构：SPA（单页应用）
- **选择理由**：频繁的"what-if"模拟和实时预测需要流畅的用户体验
- **技术方案**：Vue3 + Vite + TypeScript
- **路由管理**：Vue Router，支持动态路由和路由守卫
- **状态管理**：Pinia，管理全局状态（用户信息、模拟数据、预测结果）

#### 2. 响应式设计
- **移动端**：支持常用的安卓、iOS系统手机尺寸（320px - 428px宽度）
- **平板端**：支持常用的平板设备（768px - 1024px宽度）
- **桌面端**：PC Web（1024px以上）
- **技术方案**：Element Plus响应式组件 + CSS Grid/Flexbox布局
- **断点策略**：
  - 移动端：< 768px
  - 平板端：768px - 1024px
  - 桌面端：> 1024px

#### 3. 浏览器兼容性
- **支持浏览器**：Chrome/Edge/Safari/Firefox最新版本
- **不支持**：IE11及以下版本
- **技术方案**：使用现代ES6+语法，无需polyfill
- **版本策略**：每年更新一次浏览器兼容性要求

#### 4. 实时性
- **无需实时推送**：不使用WebSocket
- **数据更新方式**：用户手动刷新或重新查询
- **缓存策略**：客户端缓存常用数据（学校信息、历史分数线），减少服务器压力

#### 5. SEO优化
- **挑战**：SPA架构对SEO不友好，需要特殊处理
- **技术方案**：
  - **方案1（推荐）**：Nuxt.js服务端渲染（SSR）
  - **方案2（备选）**：Vite SSG静态站点生成 + 预渲染关键页面
  - **方案3（MVP）**：客户端渲染 + 服务器端meta标签动态生成
- **SEO要求**：
  - 动态生成title、description、keywords
  - 语义化HTML标签
  - 结构化数据（JSON-LD）
  - Sitemap.xml和robots.txt
  - 页面加载性能优化（Lighthouse分数>90）

#### 6. 无障碍访问（WCAG 2.0 AA增强级）
- **键盘导航**：所有交互元素支持Tab键导航
- **屏幕阅读器**：ARIA标签完整，支持NVDA、VoiceOver等
- **颜色对比度**：文本与背景对比度至少4.5:1
- **高对比度模式**：支持系统高对比度模式
- **字体大小**：支持用户自定义字体大小（100%-200%）
- **焦点指示器**：清晰的焦点视觉反馈
- **错误提示**：屏幕阅读器友好的错误消息
- **图像替代文本**：所有图像提供alt文本

### 性能目标

#### 页面加载性能
- **首次内容绘制（FCP）**：< 1.8秒
- **最大内容绘制（LCP）**：< 2.5秒
- **首次输入延迟（FID）**：< 100毫秒
- **累积布局偏移（CLS）**：< 0.1

#### 运行时性能
- **预测计算**：< 10秒
- **页面切换**：< 500毫秒
- **数据查询**：< 3秒

### 实现考虑

#### 前端架构
- **框架**：Vue 3 + TypeScript
- **构建工具**：Vite
- **UI组件库**：Element Plus
- **路由**：Vue Router
- **状态管理**：Pinia
- **HTTP客户端**：Axios
- **表单验证**：VeeValidate
- **图表库**：ECharts（雷达图、趋势图）

#### 后端架构
- **框架**：Java SpringBoot
- **API风格**：RESTful API
- **数据格式**：JSON
- **认证方式**：JWT + OAuth2（微信登录）
- **缓存**：Redis（缓存学校信息、历史数据）
- **数据库**：MySQL（关系型数据）+ MongoDB（非结构化数据）

#### 部署架构
- **前端部署**：
  - SPA：Nginx静态文件服务
  - SSR：Node.js + PM2进程管理
- **后端部署**：Docker容器 + Kubernetes编排
- **CDN**：静态资源CDN加速
- **负载均衡**：Nginx反向代理
- **监控**：Prometheus + Grafana

#### 微信小程序
- **框架**：微信原生小程序或uni-app
- **与Web端共享**：业务逻辑层代码复用
- **独立API**：复用后端RESTful API
- **微信登录**：OAuth2授权登录

---

## 项目范围界定与分阶段开发

### MVP策略与哲学

**MVP方法**：体验MVP（Experience MVP）- 完整用户体验，功能适度简化
**核心理念**：优先验证用户使用意愿，通过完整流程建立信任，再逐步优化算法准确性
**资源需求**：
- **团队规模**：5-7人（前端2人、后端2人、产品1人、设计1人、测试1人）
- **关键技能**：Vue3/小程序开发、Java SpringBoot、数据导入/处理、算法开发
- **开发周期**：3-4个月

### MVP功能集（Phase 1）

**支持的核心用户旅程**：
- 家长用户：从注册登录到完成志愿模拟的完整流程
- 管理员：数据导入和管理

**必须具备的能力**：

**第一阶段（基础功能）**：
- 用户注册/登录（微信登录）
- 学生信息输入（分数、排名、毕业学校）
- 学校信息查询
  - 学校名称、历年分数线
  - 学校特色、排名
  - 分配生名额展示（仅展示，不参与计算）

**第二阶段（核心功能）**：
- 模拟志愿填报
  - 选择学校、调整志愿顺序
  - 智能推荐（基于距离、特色、排名等多因素）
  - 策略对比（不同志愿组合的录取概率对比）
- 预测结果查看
  - 基于历史数据的简单预测
  - 考虑排名波动、年份趋势
  - 提供置信区间
  - 风险提示
- 管理员数据导入
  - 学校信息批量导入
  - 历年分数线导入
  - 分配生名额导入
  - 数据校验和错误提示

**第三阶段（增强功能）**：
- 保存/分享模拟方案
- 历史模拟记录查看

**设备支持**：
- 微信小程序（优先）
- PC Web（同步开发）

### MVP后功能

**Phase 2（Growth阶段）**：

**功能扩展**：
- 分配生功能（分配生名额参与预测计算和推荐）
- 学校端管理（学校管理员管理本校信息）
- 教育局端（政策发布、监控）
- 客服端（用户问题解答、反馈管理）

**算法优化**：
- 复杂机器学习模型
- 个性化推荐（基于用户历史行为）
- 预测准确性持续优化

**用户体验提升**：
- 数据可视化增强（雷达图、趋势图）
- 个性化报告生成
- PDF/Excel导出

**Phase 3（Expansion阶段）**：

**平台扩展**：
- 高考志愿填报支持
- 更多区域/城市覆盖
- 与教育部门深度合作

**商业化功能**：
- 高级功能付费（专家咨询、深度报告）
- 学校广告/推广
- 数据分析服务

**高级特性**：
- AI智能问答
- 虚拟咨询师
- 社区交流功能

### 风险缓解策略

**技术风险缓解**：
- **算法准确性**：MVP使用简单算法 + 明确预期管理 + 基于历史数据回测
- **多因素推荐**：MVP使用固定权重 + 收集用户反馈 + Growth阶段优化
- **双端开发**：复用业务逻辑 + 使用uni-app框架 + 优先小程序
- **数据导入**：严格数据校验 + 详细错误提示 + 导入模板标准化

**市场风险缓解**：
- **用户接受度**：提供预测依据透明化 + 历史数据展示 + 持续用户教育
- **数据获取**：MVP聚焦1-2个试点区域 + 建立数据合作关系 + 验证后扩展
- **政策变化**：算法参数可配置 + 快速迭代机制 + 政策监控机制

**资源风险缓解**：
- **团队规模**：MVP优先核心功能 + 小程序优先 + PC Web可延后
- **数据准备**：提前准备数据模板 + 导入工具自动化 + 预留充足时间
- **测试资源**：自动化测试 + 历史数据回测 + 灰度发布验证

---

## 功能需求

### 用户账户管理模块

- FR1: Users shall be able to register an account using email or mobile phone number
- FR2: Users shall be able to login with email/mobile and password
- FR3: Users shall be able to login via WeChat OAuth authorization
- FR4: Users shall be able to reset password through email/SMS verification
- FR5: Users shall be able to view and update personal profile information

### 学生信息管理模块

- FR6: Users shall be able to input student's basic information (name, gender, birth date)
- FR7: Users shall be able to input student's academic scores (subject-wise scores)
- FR8: Users shall be able to input student's regional information (city, district, school)
- FR9: Users shall be able to view school information list with filtering and sorting
- FR10: Users shall be able to view detailed information for each school
- FR11: Users shall be able to search schools by name or keyword
- FR12: Users shall be able to filter schools by region, type, and score range

### 学校信息管理模块

- FR13: Admins shall be able to import school data from Excel/CSV files
- FR14: Admins shall be able to export school data to Excel/CSV files
- FR15: Admins shall be able to edit school information (name, region, score requirements)

### 志愿填报模拟模块

- FR16: Users shall be able to add schools to volunteer application list
- FR17: Users shall be able to edit volunteer application order
- FR18: Users shall be able to delete schools from volunteer application list
- FR19: Users shall be able to view volunteer application history

### 预测分析模块

- FR20: Users shall be able to view admission probability prediction for selected schools
- FR21: Users shall be able to receive intelligent school recommendations based on scores
- FR22: Users shall be able to create multiple simulation scenarios
- FR23: Users shall be able to save simulation scenarios for comparison

### 数据导入与管理模块

- FR24: Admins shall be able to import historical admission data
- FR25: Admins shall be able to export system data for backup
- FR26: Admins shall be able to create system data backups
- FR27: Admins shall be able to restore system data from backups

### 系统管理模块

- FR28: Admins shall be able to manage user accounts and permissions
- FR29: Admins shall be able to configure system parameters
- FR30: Admins shall be able to view system operation logs
- FR31: Admins shall be able to monitor system performance metrics
- FR32: Admins shall be able to manage content (news, policies, FAQs)
- FR33: Users shall be able to view system announcements and news
- FR34: Users shall be able to view admission policy information
- FR35: Users shall be able to view FAQ and help documentation
- FR36: Users shall be able to submit feedback and suggestions
- FR37: Admins shall be able to review and respond to user feedback
- FR38: System shall automatically send notifications for important events
- FR39: Users shall be able to manage notification preferences

---

## 非功能需求

### 性能

- NFR1: 学校信息查询响应时间 ≤ 2秒
- NFR2: 预测计算响应时间 ≤ 3秒
- NFR3: 智能推荐响应时间 ≤ 2秒
- NFR4: 模拟方案保存响应时间 ≤ 1秒
- NFR5: 系统支持500并发用户时，性能下降 < 10%
- NFR6: 中考期间支持10倍平时流量

### 安全性

- NFR7: 所有用户数据在传输时使用TLS 1.3加密
- NFR8: 所有敏感数据在数据库中使用AES-256加密存储
- NFR9: 用户密码使用bcrypt哈希存储
- NFR10: 实施基于角色的访问控制（RBAC）
- NFR11: 所有用户操作记录审计日志（至少保留6个月）
- NFR12: 敏感操作（如数据修改）需要二次验证
- NFR13: 定期进行安全漏洞扫描和渗透测试
- NFR14: 符合《个人信息保护法》要求
- NFR15: 实现数据备份机制（每日增量备份，每周全量备份）

### 可扩展性

- NFR16: 支持水平扩展，通过增加服务器节点提升处理能力
- NFR17: 实现读写分离，查询操作只读副本
- NFR18: 使用Redis缓存热点数据，缓存命中率 > 80%
- NFR19: 静态资源使用CDN加速
- NFR20: 实现负载均衡，支持Nginx + 多实例部署
- NFR21: 数据库支持分库分表，应对数据量增长

### 可访问性

- NFR22: 符合WCAG 2.0 AA级标准
- NFR23: 所有交互元素支持键盘导航（Tab键）
- NFR24: 提供ARIA标签，支持屏幕阅读器
- NFR25: 文本与背景对比度 ≥ 4.5:1
- NFR26: 支持系统高对比度模式
- NFR27: 字体大小支持100%-200%缩放
- NFR28: 所有图像提供alt文本

### 集成

- NFR29: 集成微信小程序API，支持微信登录和授权
- NFR30: 支持OAuth 2.0协议，实现第三方登录
- NFR31: 提供RESTful API接口，支持JSON数据格式
- NFR32: 支持Excel/CSV格式的数据批量导入
- NFR33: 支持数据导出为PDF和Excel格式
- NFR34: 预留与教育局系统的数据对接接口
- NFR35: API接口支持版本控制（/v1/, /v2/）
- NFR36: API响应格式统一使用JSON
