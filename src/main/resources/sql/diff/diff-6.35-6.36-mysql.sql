# removing no more used things from `text_docs` table

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 36;

UPDATE `text_docs`
SET default_template = 'demo'
WHERE default_template IS NULL;

ALTER TABLE `text_docs`
  DROP COLUMN default_template_1,
  DROP COLUMN default_template_2,
  CHANGE `default_template` `default_template` VARCHAR(255) NOT NULL,
  ADD COLUMN `default_children_template` VARCHAR(255);

UPDATE `text_docs`
SET default_children_template = 'demo'
WHERE text_docs.default_children_template IS NULL;

ALTER TABLE `text_docs`
  CHANGE `default_children_template` `default_children_template` VARCHAR(255) NOT NULL;


UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;
