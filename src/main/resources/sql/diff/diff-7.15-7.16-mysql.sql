SET @schema_version__major_new = 7;
SET @schema_version__minor_new = 16;

CREATE INDEX idx_imcms_doc_i18n_meta_alias ON imcms_doc_i18n_meta (alias);

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;
