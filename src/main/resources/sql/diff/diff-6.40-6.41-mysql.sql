# added boolean flag "in text" into image table

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 41;

ALTER TABLE imcms_text_doc_images
  ADD in_text TINYINT DEFAULT '0';

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;