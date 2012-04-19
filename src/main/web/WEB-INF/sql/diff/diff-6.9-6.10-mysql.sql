SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 10;


INSERT INTO category_types(name, max_choices, inherited, is_image_archive) SELECT 'Images', 0, 1, 1 FROM DUAL WHERE (SELECT COUNT(*) FROM category_types WHERE name='Images') = 0;
--
-- Update schema version
--
UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;