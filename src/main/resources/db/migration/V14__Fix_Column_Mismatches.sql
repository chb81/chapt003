ALTER TABLE help_documents ADD COLUMN deleted TINYINT(1) DEFAULT 0 AFTER updated_at;
ALTER TABLE volunteer_applications CHANGE COLUMN year application_year INT NOT NULL;
