SET @schema_version__major_new = 7;
SET @schema_version__minor_new = 14;

ALTER TABLE fileupload_docs
    ADD COLUMN original_filename VARCHAR(255) NOT NULL;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;