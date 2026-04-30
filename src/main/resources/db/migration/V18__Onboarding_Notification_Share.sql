-- V18: 用户引导状态 + 通知提醒 + 分享记录

-- 1. 用户引导状态表
CREATE TABLE user_onboarding (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    step_completed INT NOT NULL DEFAULT 0 COMMENT '已完成步骤数(0-3)',
    step1_score_entered BIT(1) NOT NULL DEFAULT 0 COMMENT '步骤1: 录入成绩',
    step2_recommendation_viewed BIT(1) NOT NULL DEFAULT 0 COMMENT '步骤2: 查看推荐',
    step3_plan_created BIT(1) NOT NULL DEFAULT 0 COMMENT '步骤3: 创建方案',
    onboarding_completed BIT(1) NOT NULL DEFAULT 0 COMMENT '引导是否全部完成',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_id (user_id),
    CONSTRAINT fk_onboarding_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户引导状态表';

-- 2. 通知提醒表
CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    type VARCHAR(30) NOT NULL COMMENT '通知类型(DEADLINE/REMINDER/SYSTEM/PROMOTION)',
    title VARCHAR(200) NOT NULL COMMENT '通知标题',
    content TEXT COMMENT '通知内容',
    link VARCHAR(500) COMMENT '跳转链接',
    is_read BIT(1) NOT NULL DEFAULT 0 COMMENT '是否已读',
    read_at TIMESTAMP NULL COMMENT '阅读时间',
    scheduled_at TIMESTAMP NULL COMMENT '计划发送时间',
    sent_at TIMESTAMP NULL COMMENT '实际发送时间',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_read (user_id, is_read),
    INDEX idx_user_type (user_id, type),
    INDEX idx_scheduled (scheduled_at),
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知提醒表';

-- 3. 分享记录表
CREATE TABLE share_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '分享用户ID',
    share_type VARCHAR(30) NOT NULL COMMENT '分享类型(PLAN/SCHOOL/RECOMMENDATION)',
    target_id BIGINT COMMENT '关联ID(方案ID/学校ID)',
    share_code VARCHAR(32) NOT NULL COMMENT '分享码',
    title VARCHAR(200) COMMENT '分享标题',
    description TEXT COMMENT '分享描述',
    view_count INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
    expires_at TIMESTAMP NULL COMMENT '过期时间',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_share_code (share_code),
    INDEX idx_user_type (user_id, share_type),
    CONSTRAINT fk_share_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分享记录表';

-- 4. 用户表增加引导相关字段
ALTER TABLE users ADD COLUMN onboarding_completed BIT(1) NOT NULL DEFAULT 0 COMMENT '引导是否完成';
ALTER TABLE users ADD COLUMN last_login_at TIMESTAMP NULL COMMENT '最后登录时间';
