ALTER TABLE users ADD session_id NVARCHAR(128);

UPDATE database_version SET major = 4, minor = 10;
