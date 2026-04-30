-- V17: 分配生/指标生政策引擎 + 学校维度扩充 + 分数位次换算

-- 1. 扩充学校信息字段
ALTER TABLE schools ADD COLUMN school_level VARCHAR(30) COMMENT '学校等级(省示范/市示范/普通)';
ALTER TABLE schools ADD COLUMN college_admission_rate DECIMAL(5,2) COMMENT '高考本科率(%)';
ALTER TABLE schools ADD COLUMN first_tier_rate DECIMAL(5,2) COMMENT '高考一本率(%)';
ALTER TABLE schools ADD COLUMN boarding_type VARCHAR(20) COMMENT '住宿条件(走读/寄宿/均可)';
ALTER TABLE schools ADD COLUMN tuition_range VARCHAR(100) COMMENT '学费范围';
ALTER TABLE schools ADD COLUMN special_classes TEXT COMMENT '特色班/实验班信息(JSON)';
ALTER TABLE schools ADD COLUMN facilities_score DECIMAL(3,1) COMMENT '硬件评分(1-10)';
ALTER TABLE schools ADD COLUMN transportation VARCHAR(200) COMMENT '交通信息';

-- 2. 分配生/指标生名额表
CREATE TABLE allocation_quotas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    school_id BIGINT NOT NULL COMMENT '目标高中ID',
    source_school_name VARCHAR(100) NOT NULL COMMENT '生源初中名称',
    source_school_city VARCHAR(50) COMMENT '生源初中所在城市',
    source_school_district VARCHAR(50) COMMENT '生源初中所在区县',
    year INT NOT NULL COMMENT '年度',
    quota_count INT NOT NULL DEFAULT 0 COMMENT '分配名额数',
    admission_score DECIMAL(5,2) COMMENT '分配生录取分数线(低于统招线)',
    unified_score DECIMAL(5,2) COMMENT '统招录取分数线',
    score_difference DECIMAL(5,2) COMMENT '分配生与统招分数线差值',
    policy_rule VARCHAR(200) COMMENT '适用政策规则',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted BIT(1) NOT NULL DEFAULT 0,
    INDEX idx_school_year (school_id, year),
    INDEX idx_source_school (source_school_name, year),
    INDEX idx_city_district (source_school_city, source_school_district, year),
    CONSTRAINT fk_allocation_school FOREIGN KEY (school_id) REFERENCES schools(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分配生/指标生名额表';

-- 3. 分配生政策配置表
CREATE TABLE allocation_policies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    city VARCHAR(50) NOT NULL COMMENT '城市',
    district VARCHAR(50) NOT NULL COMMENT '区县',
    year INT NOT NULL COMMENT '年度',
    policy_name VARCHAR(100) NOT NULL COMMENT '政策名称',
    policy_type VARCHAR(30) NOT NULL COMMENT '政策类型(分配生/指标生/特长生)',
    total_quota_percentage DECIMAL(5,2) COMMENT '分配生占总招生比例(%)',
    min_score_gap DECIMAL(5,2) COMMENT '最低分数差(分配生分数线最多低于统招线多少分)',
    eligible_conditions TEXT COMMENT '享受分配生资格条件(JSON)',
    description TEXT COMMENT '政策详细说明',
    is_active BIT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_city_district_year (city, district, year),
    INDEX idx_policy_type (policy_type, year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分配生政策配置表';

-- 4. 分数-位次换算表
CREATE TABLE score_rank_mapping (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    city VARCHAR(50) NOT NULL COMMENT '城市',
    year INT NOT NULL COMMENT '年度',
    total_score DECIMAL(5,2) NOT NULL COMMENT '中考总分',
    city_rank INT NOT NULL COMMENT '全市排名',
    district VARCHAR(50) COMMENT '区县',
    district_rank INT COMMENT '区内排名',
    student_count INT COMMENT '该分数段人数',
    cumulative_count INT COMMENT '累计人数(从高到低)',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_city_year_score (city, year, total_score),
    INDEX idx_city_year_rank (city, year, city_rank),
    UNIQUE KEY uk_city_year_score_district (city, year, total_score, district)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分数-位次换算表';
