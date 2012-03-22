ALTER TABLE users ADD COLUMN session_id VARCHAR(128) null;

UPDATE database_version SET major = 4, minor = 10;
