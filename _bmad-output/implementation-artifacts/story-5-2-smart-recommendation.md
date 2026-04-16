# Story 5.2: 智能推荐系统

## User Story

作为一个使用志愿填报系统的用户，
我希望系统能够根据我的成绩、偏好和历史数据智能推荐适合的学校，
以便提高志愿填报的成功率。

## Acceptance Criteria

### 基本功能

**Given** 用户已登录
**And** 用户已完成学生基本信息录入
**And** 用户已设置推荐偏好（地区、学校类型等）
**And** 系统已加载学校数据

**When** 用户点击"智能推荐"按钮
**Then** 系统应基于以下因素推荐学校：
  - 学生成绩与学校录取分数线的匹配度
  - 用户设置的偏好（地区、学校类型、学校等级）
  - 学校录取概率（≥50%的学校优先）
  - 学校特色标签与用户兴趣的匹配
  - 往年同分数段学生的录取情况

**Given** 系统生成推荐结果
**Then** 系统应显示最多5个推荐学校
**And** 每个推荐学校应显示：
  - 学校名称和类型
  - 录取概率（带颜色标识）
  - 推荐理由（如"录取概率高，符合您的地区偏好"）
  - "添加到志愿"按钮

### 推荐结果显示

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

## Performance Requirements

- 推荐生成时间少于3秒
- 推荐结果列表加载时间少于2秒

## Accessibility Requirements

- 推荐结果卡片支持键盘导航
- 推荐标签使用ARIA属性描述
- "添加到志愿"按钮最小44x44像素
- 推荐设置表单支持屏幕阅读器

## Error Handling

- 如果推荐服务不可用，显示"智能推荐服务暂时不可用"并提供重试按钮
- 如果用户未设置偏好，显示"请先设置推荐偏好"提示
- 如果推荐生成失败，显示"推荐生成失败，请重试"错误消息

## Technical Implementation Notes

### 推荐算法

```
推荐分数 = W1 × 录取概率 + W2 × 偏好匹配度 + W3 × 学校等级 + W4 × 特色标签匹配

其中：
- W1 = 0.5（录取概率权重最高）
- W2 = 0.25（偏好匹配度）
- W3 = 0.15（学校等级）
- W4 = 0.1（特色标签匹配）
```

### 偏好匹配度计算

```
偏好匹配度 = (地区匹配 × 0.4) + (学校类型匹配 × 0.3) + (学校等级匹配 × 0.3)
```

### 后端实现

**新增文件：**
- `SmartRecommendationService.java` - 智能推荐服务
- `SmartRecommendationController.java` - 智能推荐API控制器
- `RecommendationPreference.java` - 推荐偏好实体
- `RecommendationPreferenceRepository.java` - 推荐偏好仓库
- `SmartRecommendationRequest.java` - 推荐请求DTO
- `SmartRecommendationResponse.java` - 推荐响应DTO
- `SchoolRecommendationDTO.java` - 学校推荐DTO

**修改文件：**
- 无

### 数据库 Schema

```sql
CREATE TABLE recommendation_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    preferred_districts VARCHAR(500),
    preferred_school_types VARCHAR(200),
    preferred_school_levels VARCHAR(200),
    min_probability INT DEFAULT 30,
    max_results INT DEFAULT 5,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

## Test Cases

1. 测试基于成绩和偏好的智能推荐
2. 测试无合适学校的情况
3. 测试已添加志愿的标记
4. 测试从推荐添加到志愿
5. 测试查看全部推荐
6. 测试推荐性能（生成时间 < 3秒）
7. 测试用户未设置偏好的处理

## Dependencies

- 录取概率计算功能已实现 (Story 5.1)
- 学校信息查询功能已实现 (Epic 3)
- 志愿填报功能已实现 (Epic 4)
