SET @schema_version__major_new = 7;
SET @schema_version__minor_new = 2;

ALTER TABLE user_properties ADD CONSTRAINT `uk_user_id_key_name` UNIQUE (user_id, key_name);

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;

