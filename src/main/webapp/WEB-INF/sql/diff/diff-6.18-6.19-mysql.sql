-- ads flag is_enabled for every lang in 'imcms_doc_i18n_meta'

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 19;

ALTER TABLE `imcms_doc_i18n_meta` ADD COLUMN `is_enabled` TINYINT(1) DEFAULT 1;

--
-- Update schema version
--

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;
