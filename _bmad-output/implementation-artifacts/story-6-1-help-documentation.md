# Story 6.1: 帮助文档浏览

## User Story

作为一个使用系统的用户，
我希望能够浏览帮助文档和用户指南，
以便了解如何使用系统的各项功能。

## Acceptance Criteria

### 基本功能

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

## Performance Requirements

- 文档列表加载时间少于2秒
- 文档详细内容加载时间少于2秒
- 搜索响应时间少于1秒

## Accessibility Requirements

- 文档目录支持键盘导航
- 文档内容使用语义化HTML标签
- 图片应包含alt文本描述
- 搜索框使用ARIA标签
- 所有交互元素最小44x44像素

## Error Handling

- 如果文档加载失败，显示"文档加载失败，请重试"错误消息
- 如果搜索失败，显示"搜索失败，请重试"错误消息

## Technical Implementation Notes

### 后端实现

**新增文件：**
- `HelpDocument.java` - 帮助文档实体
- `HelpDocumentRepository.java` - 帮助文档仓库
- `HelpDocumentCategory.java` - 文档分类枚举
- `HelpDocumentService.java` - 帮助文档服务
- `HelpDocumentController.java` - 帮助文档控制器
- `HelpDocumentDTO.java` - 文档DTO
- `HelpDocumentDetailDTO.java` - 文档详情DTO
- `DocumentFeedback.java` - 文档反馈实体
- `DocumentFeedbackRepository.java` - 文档反馈仓库
- `DocumentFavorite.java` - 文档收藏实体
- `DocumentFavoriteRepository.java` - 文档收藏仓库

**修改文件：**
- 无

### 数据库 Schema

```sql
-- 帮助文档表
CREATE TABLE help_documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description VARCHAR(500),
    content TEXT NOT NULL,
    reading_time INT,
    view_count INT DEFAULT 0,
    helpful_count INT DEFAULT 0,
    not_helpful_count INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    published BOOLEAN DEFAULT FALSE,
    deleted BOOLEAN DEFAULT FALSE
);

-- 文档收藏表
CREATE TABLE document_favorites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    document_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (document_id) REFERENCES help_documents(id),
    UNIQUE KEY (user_id, document_id)
);

-- 文档反馈表
CREATE TABLE document_feedback (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    document_id BIGINT NOT NULL,
    is_helpful BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (document_id) REFERENCES help_documents(id)
);
```

## Test Cases

1. 测试获取文档分类列表
2. 测试按分类获取文档列表
3. 测试搜索文档
4. 测试获取文档详情
5. 测试收藏文档
6. 测试获取收藏列表
7. 测试取消收藏
8. 测试提交文档反馈

## Dependencies

- 无前置依赖
