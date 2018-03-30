# removing no more used things from `text_docs` table

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 37;

ALTER TABLE `text_docs`
  DROP COLUMN default_template,
  CHANGE `default_children_template` `children_template_name` VARCHAR(255) NOT NULL;

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;
