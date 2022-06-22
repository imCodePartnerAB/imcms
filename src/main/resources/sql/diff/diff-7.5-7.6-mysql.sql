SET @schema_version__major_new = 7;
SET @schema_version__minor_new = 6;

ALTER TABLE meta
	ADD COLUMN default_language_alias_enabled boolean default false after disabled_language_show_rule;

ALTER TABLE imcms_doc_i18n_meta
	ADD COLUMN alias varchar(255) after headline;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;