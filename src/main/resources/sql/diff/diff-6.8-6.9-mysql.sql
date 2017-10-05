SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 9;

ALTER TABLE archive_category_roles ADD COLUMN canUse tinyint(1) NOT NULL;
ALTER TABLE archive_category_roles ADD COLUMN canChange tinyint(1) NOT NULL;

ALTER TABLE archive_library_roles ADD COLUMN canUse tinyint(1) NOT NULL;
ALTER TABLE archive_library_roles ADD COLUMN canChange tinyint(1) NOT NULL;

ALTER TABLE archive_images ADD COLUMN alt_text varchar(125);

--
-- Update schema version
--
UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;



