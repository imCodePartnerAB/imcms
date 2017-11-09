# no more nullable doc versions in common content
START TRANSACTION;

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 30;

# delete wrong data
DELETE a
FROM imcms_doc_i18n_meta AS a, imcms_doc_i18n_meta AS b
WHERE (a.doc_id = b.doc_id) AND a.language_id = b.language_id AND a.version_no IS NULL AND b.version_no = 0;

# fix what can be fixed
UPDATE imcms_doc_i18n_meta
SET version_no = 0
WHERE version_no IS NULL;
ALTER TABLE `imcms_doc_i18n_meta`
  MODIFY `version_no` INT(11) DEFAULT 0 NOT NULL;

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;

COMMIT;
