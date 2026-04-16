CREATE TABLE IF NOT EXISTS volunteer_application_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    volunteer_application_id BIGINT NOT NULL,
    school_id BIGINT NOT NULL,
    priority INT NOT NULL,
    admission_probability DECIMAL(5,2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    INDEX idx_application_id (volunteer_application_id),
    INDEX idx_school_id (school_id),
    INDEX idx_priority (priority),
    INDEX idx_deleted (deleted),
    CONSTRAINT fk_items_application FOREIGN KEY (volunteer_application_id) REFERENCES volunteer_applications(id) ON DELETE CASCADE,
    CONSTRAINT fk_items_school FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
