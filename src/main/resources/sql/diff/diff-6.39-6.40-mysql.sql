# added boolean flag "all languages" into image table

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 40;

ALTER TABLE imcms_text_doc_images
  ADD all_languages TINYINT DEFAULT '0';

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;