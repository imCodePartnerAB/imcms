SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 94;

ALTER TABLE users
    ADD COLUMN ref VARCHAR (128) AFTER email;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;