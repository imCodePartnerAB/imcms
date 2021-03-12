SET @schema_version__major_new = 7;
SET @schema_version__minor_new = 1;

ALTER TABLE imcms_text_doc_images_history ADD COLUMN compress tinyint DEFAULT 0;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;