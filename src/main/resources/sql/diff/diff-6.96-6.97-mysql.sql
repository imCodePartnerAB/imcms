SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 97;

ALTER TABLE imcms_text_doc_texts ADD COLUMN like_published tinyint not null default 0;
ALTER TABLE imcms_text_doc_texts_history ADD COLUMN like_published tinyint not null default 0;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;