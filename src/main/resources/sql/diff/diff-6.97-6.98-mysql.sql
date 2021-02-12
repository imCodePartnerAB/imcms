SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 98;

ALTER TABLE users add COLUMN login_date datetime default null;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;