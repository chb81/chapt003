# Story 5.1: 录取概率计算

## User Story

作为一个已登录的用户，
我希望系统能够根据我的学生成绩和历史录取数据计算每所学校的录取概率，
以便做出更明智的志愿填报决策。

## Acceptance Criteria

### 基本功能

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

### 动态更新

**Given** 用户已查看学校录取概率
**When** 学生的成绩发生变化（如修改某科成绩）
**Then** 系统应重新计算所有学校的录取概率
**And** 更新后的概率应在3秒内显示

### 概率显示样式

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

### 数据不足处理

**Given** 系统计算录取概率
**When** 学校历史录取数据不足（少于2年）
**Then** 系统应显示"数据不足"提示
**And** 建议用户咨询学校招生办

### 概率详情

**Given** 系统显示录取概率
**When** 用户点击概率数字
**Then** 系统应弹出概率计算详情面板
**And** 面板应显示：
  - 学生总分
  - 学校近3年录取分数线
  - 录取人数与申请人数比例
  - 同分数段录取率

## Performance Requirements

- 概率计算应在2秒内完成
- 概率更新响应时间少于3秒

## Accessibility Requirements

- 概率百分比使用ARIA标签和角色属性
- 概率高亮颜色应符合WCAG 2.1 AA对比度标准
- 概率详情面板支持键盘导航和屏幕阅读器
- 所有交互元素最小44x44像素

## Error Handling

- 如果历史录取数据加载失败，显示"暂时无法计算录取概率"错误消息
- 如果成绩数据不完整，提示用户完善成绩信息
- 如果计算服务不可用，显示"录取概率计算服务暂时不可用"并提供重试按钮

## Technical Implementation Notes

### 录取概率计算算法

```
录取概率 = W1 × 分数匹配度 + W2 × 录取率 + W3 × 历史趋势

其中：
- 分数匹配度 = (学生总分 - 学校最低分数线) / (学校最高分数线 - 学校最低分数线)
- 录取率 = 录取人数 / 申请人数
- 历史趋势 = 基于近3年分数线变化的加权
- W1 = 0.5, W2 = 0.3, W3 = 0.2
```

### 后端实现

**新增文件：**
- `AdmissionProbabilityService.java` - 录取概率计算服务
- `AdmissionProbabilityController.java` - 录取概率API控制器

**修改文件：**
- `SchoolController.java` - 添加录取概率到学校列表响应
- `School.java` - 添加历史录取数据字段

### 数据库 Schema

```sql
-- 添加到 schools 表
ALTER TABLE schools ADD COLUMN admission_score_year1 DECIMAL(10,2);
ALTER TABLE schools ADD COLUMN admission_score_year2 DECIMAL(10,2);
ALTER TABLE schools ADD COLUMN admission_score_year3 DECIMAL(10,2);
ALTER TABLE schools ADD COLUMN admission_quota INT;
ALTER TABLE schools ADD COLUMN applicant_count INT;
```

## Test Cases

1. 测试正常录取概率计算
2. 测试分数高于学校最高分数线的情况
3. 测试分数低于学校最低分数线的情况
4. 测试历史录取数据不足的情况
5. 测试学生成绩不完整的情况
6. 测试录取概率详情查询
7. 测试概率性能（计算时间 < 2秒）

## Dependencies

- 学生成绩数据已录入 (Epic 2)
- 学校历史录取数据已导入 (Epic 3)
- 学校信息查询功能已实现 (Epic 3)
