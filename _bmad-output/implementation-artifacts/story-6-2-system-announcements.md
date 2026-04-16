# Story 6.2: 系统公告

## User Story

作为一个使用系统的用户，
我希望能够查看系统公告和通知，
以便了解系统的重要更新、维护信息和使用提示。

## Acceptance Criteria

### 基本功能

**Given** 用户已登录或未登录（公告无需登录）

**When** 用户访问公告页面
**Then** 系统应显示公告列表
**And** 公告应按发布时间倒序排列
**And** 每个公告应显示：
  - 标题
  - 类型（重要/普通）
  - 发布时间
  - 预览文本（前100字）
  - 阅读状态（如果是未读）

**Given** 用户在公告列表页面
**When** 用户点击某个公告
**Then** 系统应显示公告详情页面
**And** 公告详情应包含：
  - 标题
  - 公告类型
  - 发布时间
  - 发布者（管理员）
  - 完整内容
  - 阅读时间（如果已读）

**Given** 用户正在查看公告
**When** 用户使用移动设备
**Then** 公告应支持垂直滚动阅读
**And** 公告内容应适配小屏幕显示
**And** 图片应支持点击放大查看

**Given** 用户在公告列表页面
**When** 用户点击"未读"标签
**Then** 系统应只显示用户未读的公告

**Given** 用户在公告列表页面
**When** 用户点击"全部"标签
**Then** 系统应显示所有公告

**Given** 用户在公告列表页面
**When** 用户点击"重要"标签
**Then** 系统应只显示重要公告

**Given** 用户查看公告详情
**When** 用户点击"标记为已读"按钮
**Then** 系统应标记该公告为已读
**And** 按钮状态应变为"已读"
**And** 显示"已标记为已读"提示

**Given** 用户在公告页面
**When** 用户点击"发布时间"排序按钮
**Then** 系统应按发布时间重新排序
**And** 按钮状态应更新为"按发布时间排序"

**Given** 用户在公告页面
**When** 用户点击"查看发布者"按钮
**Then** 系统应显示发布者信息（如果有权限）

## Performance Requirements

- 公告列表加载时间少于2秒
- 公告详情加载时间少于2秒

## Accessibility Requirements

- 公告列表支持键盘导航
- 公告内容使用语义化HTML标签
- 图片应包含alt文本描述
- 所有交互元素最小44x44像素

## Error Handling

- 如果公告加载失败，显示"公告加载失败，请重试"错误消息
- 如果没有公告，显示"暂无公告"提示

## Technical Implementation Notes

### 后端实现

**新增文件：**
- `Announcement.java` - 公告实体
- `AnnouncementRepository.java` - 公告仓库
- `AnnouncementType.java` - 公告类型枚举
- `AnnouncementService.java` - 公告服务
- `AnnouncementController.java` - 公告控制器
- `AnnouncementDTO.java` - 公告DTO
- `AnnouncementDetailDTO.java` - 公告详情DTO
- `UserAnnouncementRead.java` - 用户已读公告实体
- `UserAnnouncementReadRepository.java` - 用户已读公告仓库

**修改文件：**
- 无

### 数据库 Schema

```sql
-- 公告表
CREATE TABLE announcements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    type VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    publisher VARCHAR(100),
    priority INT DEFAULT 0,
    published_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 用户已读公告表
CREATE TABLE user_announcement_reads (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    announcement_id BIGINT NOT NULL,
    read_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (announcement_id) REFERENCES announcements(id),
    UNIQUE KEY (user_id, announcement_id)
);
```

## Test Cases

1. 测试获取公告列表
2. 测试按类型筛选公告
3. 测试获取公告详情
4. 测试标记已读
5. 测试未读公告筛选
6. 测试重要公告筛选
7. 测试排序功能

## Dependencies

- 帮助文档功能已实现 (Story 6.1)
