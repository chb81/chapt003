-- Add new column to schools table
ALTER TABLE schools ADD COLUMN applicant_count INT;

-- Create recommendation_preferences table
CREATE TABLE recommendation_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    preferred_districts VARCHAR(500),
    preferred_school_types VARCHAR(200),
    preferred_school_levels VARCHAR(200),
    min_probability INT DEFAULT 30,
    max_results INT DEFAULT 5,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);