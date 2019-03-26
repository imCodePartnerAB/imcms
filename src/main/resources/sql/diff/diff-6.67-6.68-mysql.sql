SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 68;

ALTER TABLE `category_types`
  DROP COLUMN is_image_archive;
ALTER TABLE categories
  DROP COLUMN `image`;

ALTER TABLE category_types
  CHANGE max_choices is_multi_select tinyint(1);

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;
