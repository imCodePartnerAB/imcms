SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 96;

ALTER TABLE users add COLUMN blocked_date datetime default null;

ALTER TABLE users add COLUMN amount_attempts int NOT NULL default 0;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;