CREATE TABLE IF NOT EXISTS volunteer_applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    year INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    simulation_name VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    submitted_at TIMESTAMP NULL,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    INDEX idx_user_id (user_id),
    INDEX idx_year (year),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted),
    CONSTRAINT fk_volunteer_applications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
