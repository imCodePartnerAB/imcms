SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 93;

alter table users add column ref varchar(128) after email;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;