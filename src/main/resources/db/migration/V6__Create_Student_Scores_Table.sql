CREATE TABLE IF NOT EXISTS student_scores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    chinese DECIMAL(5,2) NOT NULL,
    math DECIMAL(5,2) NOT NULL,
    english DECIMAL(5,2) NOT NULL,
    physics DECIMAL(5,2) NOT NULL,
    chemistry DECIMAL(5,2) NOT NULL,
    politics DECIMAL(5,2) NOT NULL,
    history DECIMAL(5,2) NOT NULL,
    geography DECIMAL(5,2) NOT NULL,
    biology DECIMAL(5,2) NOT NULL,
    total_score DECIMAL(5,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_total_score (total_score),
    CONSTRAINT fk_student_scores_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
