
SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 62;

ALTER TABLE categories ADD CONSTRAINT category_name_unq UNIQUE (name, category_type_id);
ALTER TABLE category_types ADD CONSTRAINT category_type_name_unq UNIQUE (name);

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;