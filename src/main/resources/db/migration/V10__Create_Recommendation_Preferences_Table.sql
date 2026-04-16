CREATE TABLE IF NOT EXISTS recommendation_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    preferred_districts VARCHAR(500),
    preferred_school_types VARCHAR(200),
    preferred_school_levels VARCHAR(200),
    min_probability INT DEFAULT 30,
    max_results INT DEFAULT 5,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
