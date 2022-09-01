SET @schema_version__major_new = 7;
SET @schema_version__minor_new = 6;

ALTER TABLE roles_permissions
    ADD COLUMN publish_own_documents tinyint default '0' not null,
    ADD COLUMN publish_all_documents tinyint default '0' not null;


ALTER TABLE meta
    ADD COLUMN default_language_alias_enabled boolean default false after disabled_language_show_rule;

ALTER TABLE imcms_doc_i18n_meta
    ADD COLUMN alias varchar(255) after headline;

UPDATE meta, imcms_doc_i18n_meta commonContent, document_properties properties
SET commonContent.alias = properties.value,
    meta.default_language_alias_enabled = true
WHERE commonContent.doc_id = properties.meta_id
  AND meta.meta_id = properties.meta_id
  AND properties.key_name = 'imcms.document.alias'
  AND commonContent.language_id = 2;

DELETE
FROM document_properties
WHERE key_name = 'imcms.document.alias';

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;