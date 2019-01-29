SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 64;

ALTER TABLE imcms_text_doc_images
  MODIFY image_name varchar(255) null;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;