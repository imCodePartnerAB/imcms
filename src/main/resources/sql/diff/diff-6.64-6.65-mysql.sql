SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 65;

ALTER TABLE imcms_doc_i18n_meta
  DROP menu_image_url;
;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;