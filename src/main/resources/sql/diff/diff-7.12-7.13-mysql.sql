SET @schema_version__major_new = 7;
SET @schema_version__minor_new = 13;

ALTER TABLE imcms_doc_metadata
    DROP FOREIGN KEY imcms_doc_metadata_FK_imcms_doc_i18n_meta;

ALTER TABLE imcms_doc_metadata
    ADD CONSTRAINT imcms_doc_metadata_FK_imcms_doc_i18n_meta
    FOREIGN KEY (imcms_doc_i18n_meta_id) REFERENCES imcms_doc_i18n_meta (id)
    ON DELETE CASCADE;


ALTER TABLE fileupload_docs
    ADD COLUMN original_filename VARCHAR(255) NOT NULL;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;