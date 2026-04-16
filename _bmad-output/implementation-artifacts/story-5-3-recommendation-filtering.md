# Story 5.3: 推荐结果筛选和排序

## User Story

作为一个使用智能推荐的用户，
我希望能够对推荐结果进行筛选和排序，
以便快速找到最符合我需求的学校。

## Acceptance Criteria

### 基本功能

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

## Performance Requirements

- 筛选操作响应时间少于1秒
- 排序操作响应时间少于1秒

## Accessibility Requirements

- 筛选面板支持键盘导航
- 筛选条件使用复选框或单选按钮（而非仅文本）
- 排序选项使用下拉菜单（ARIA角色combobox）
- 筛选结果数量使用ARIA live region

## Error Handling

- 如果筛选操作失败，显示"筛选失败，请重试"错误消息
- 如果排序操作失败，显示"排序失败，请重试"错误消息

## Technical Implementation Notes

### 后端实现

**修改文件：**
- `SmartRecommendationService.java` - 添加筛选和排序逻辑
- `SmartRecommendationRequest.java` - 添加筛选和排序参数
- `SmartRecommendationController.java` - 处理筛选和排序请求

### 筛选逻辑

```java
// 地区筛选
if (request.getDistricts() != null && !request.getDistricts().isEmpty()) {
    recommendations = recommendations.stream()
        .filter(r -> request.getDistricts().contains(r.getDistrict()))
        .collect(Collectors.toList());
}

// 学校类型筛选
if (request.getSchoolTypes() != null && !request.getSchoolTypes().isEmpty()) {
    recommendations = recommendations.stream()
        .filter(r -> request.getSchoolTypes().contains(r.getSchoolType()))
        .collect(Collectors.toList());
}

// 录取概率范围筛选
if (request.getMinProbability() != null || request.getMaxProbability() != null) {
    recommendations = recommendations.stream()
        .filter(r -> {
            if (request.getMinProbability() != null && r.getProbability() < request.getMinProbability()) {
                return false;
            }
            if (request.getMaxProbability() != null && r.getProbability() > request.getMaxProbability()) {
                return false;
            }
            return true;
        })
        .collect(Collectors.toList());
}
```

### 排序逻辑

```java
// 按录取概率从高到低
if ("PROBABILITY_DESC".equals(request.getSortBy())) {
    recommendations.sort((a, b) -> b.getProbability().compareTo(a.getProbability()));
}

// 按录取概率从低到高
if ("PROBABILITY_ASC".equals(request.getSortBy())) {
    recommendations.sort((a, b) -> a.getProbability().compareTo(b.getProbability()));
}

// 按匹配度从高到低
if ("MATCH_SCORE_DESC".equals(request.getSortBy())) {
    recommendations.sort((a, b) -> b.getRecommendationScore().compareTo(a.getRecommendationScore()));
}

// 按地区A-Z
if ("DISTRICT_ASC".equals(request.getSortBy())) {
    recommendations.sort((a, b) -> a.getDistrict().compareTo(b.getDistrict()));
}

// 按录取分数线从低到高
if ("SCORE_ASC".equals(request.getSortBy())) {
    recommendations.sort((a, b) -> a.getAdmissionScore().compareTo(b.getAdmissionScore()));
}
```

## Test Cases

1. 测试按地区筛选
2. 测试按学校类型筛选
3. 测试按录取概率范围筛选
4. 测试重置筛选
5. 测试按录取概率排序（高到低）
6. 测试按匹配度排序
7. 测试同时使用筛选和排序
8. 测试筛选结果为空的情况

## Dependencies

- 智能推荐功能已实现 (Story 5.2)
