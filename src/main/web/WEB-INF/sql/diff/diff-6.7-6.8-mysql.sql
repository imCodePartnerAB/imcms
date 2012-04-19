SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 8;

ALTER TABLE imcms_text_doc_images ADD COLUMN gen_file varchar(255);
ALTER TABLE imcms_text_doc_images_history ADD COLUMN gen_file varchar(255);

--
-- Update schema version
--
UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;



