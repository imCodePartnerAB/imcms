# setting longer image name

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 44;

ALTER TABLE imcms_text_doc_images
  CHANGE image_name image_name VARCHAR(256) DEFAULT '' NOT NULL;

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;
