SET @schema_version__major_new = 7;
SET @schema_version__minor_new = 15;

ALTER TABLE imcms_text_doc_images
    ADD COLUMN description_text VARCHAR(255) NOT NULL AFTER alt_text;

ALTER TABLE imcms_text_doc_images_history
    ADD COLUMN description_text VARCHAR(255) NOT NULL AFTER alt_text;

ALTER TABLE imcms_last_time_use
    ADD COLUMN time_last_image_files_reindex DATETIME;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;
