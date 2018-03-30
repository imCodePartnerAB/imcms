# deleting no more used table
SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 28;

DROP TABLE IF EXISTS `imcms_text_doc_images_cache`;

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;
