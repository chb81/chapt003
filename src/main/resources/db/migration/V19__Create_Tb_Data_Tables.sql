CREATE TABLE IF NOT EXISTS tb_school (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    school_code VARCHAR(50) UNIQUE,
    school_name VARCHAR(100),
    school_type VARCHAR(20),
    school_nature VARCHAR(20),
    area_code VARCHAR(20),
    school_rank INT,
    school_remark TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tb_score_archives (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    score INT,
    ranking INT,
    exay_year VARCHAR(10),
    city_code VARCHAR(20),
    students_num INT,
    students_total_num INT,
    stundents_percentage DECIMAL(10,8),
    stundents_total_percentage DECIMAL(10,8),
    INDEX idx_score_year_city (exay_year, city_code, score)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tb_high_school_enrollment_plan (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    school_id BIGINT,
    school_code VARCHAR(50),
    school_name VARCHAR(100),
    plan_year VARCHAR(10),
    enrollment_quota INT,
    allocation_quota INT,
    city_code VARCHAR(20),
    INDEX idx_school_year (school_code, plan_year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tb_allocate_students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    middle_school_code VARCHAR(50),
    high_school_code VARCHAR(50),
    allocate_students_num INT,
    exay_year VARCHAR(10),
    INDEX idx_high_year (high_school_code, exay_year),
    INDEX idx_middle_year (middle_school_code, exay_year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tb_prediction_weight (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    weight_name VARCHAR(100),
    weight_value DECIMAL(5,4),
    year VARCHAR(10),
    city_code VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tb_dict_district (
    f_province_code INT,
    f_city_code INT PRIMARY KEY,
    f_county_code INT,
    f_level INT,
    f_province_name VARCHAR(50),
    f_city_name VARCHAR(50),
    f_county_name VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tb_administrative_division (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20),
    name VARCHAR(50),
    level INT,
    parent_code VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO tb_prediction_weight (weight_name, weight_value, year, city_code) VALUES
('rank_competition', 0.4000, '2025', '130000'),
('score_match', 0.2500, '2025', '130000'),
('trend', 0.2000, '2025', '130000'),
('volatility', 0.1500, '2025', '130000');
