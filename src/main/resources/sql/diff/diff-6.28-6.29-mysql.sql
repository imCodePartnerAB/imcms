# no more template name duplicates
START TRANSACTION;

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 29;

DELETE a
FROM template AS a, template AS b
WHERE (a.template_name = b.template_name) AND a.id > b.id;

ALTER TABLE template
  ADD UNIQUE (template_name);

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;

COMMIT;
