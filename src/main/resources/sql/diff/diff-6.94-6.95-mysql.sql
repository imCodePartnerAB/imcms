SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 95;

ALTER TABLE users MODIFY COLUMN login_name VARCHAR(255) NOT NULL;

ALTER TABLE users MODIFY COLUMN login_password VARCHAR(255) NOT NULL;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;