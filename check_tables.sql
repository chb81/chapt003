-- 检查所有必需的表是否存在
SELECT 
    'users' AS table_name, 
    EXISTS(SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'users') AS exists_status
UNION ALL SELECT 'verification_codes', EXISTS(SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'verification_codes')
UNION ALL SELECT 'audit_log', EXISTS(SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'audit_log')
UNION ALL SELECT 'login_history', EXISTS(SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'login_history')
UNION ALL SELECT 'student_profiles', EXISTS(SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'student_profiles')
UNION ALL SELECT 'student_scores', EXISTS(SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'student_scores')
UNION ALL SELECT 'schools', EXISTS(SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'schools')
UNION ALL SELECT 'volunteer_applications', EXISTS(SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'volunteer_applications')
UNION ALL SELECT 'volunteer_application_items', EXISTS(SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'volunteer_application_items')
UNION ALL SELECT 'recommendation_preferences', EXISTS(SELECT 1 FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'recommendation_preferences');

-- 查看 Flyway 迁移历史
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
