SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 78;

ALTER TABLE users
    MODIFY email VARCHAR (128) null;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;