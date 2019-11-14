SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 77;

ALTER TABLE category_types
  ADD COLUMN is_visible boolean not null default true;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;