# deleting unused view

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 53;

DROP VIEW IF EXISTS imcms_text_doc_contents_v;

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;
