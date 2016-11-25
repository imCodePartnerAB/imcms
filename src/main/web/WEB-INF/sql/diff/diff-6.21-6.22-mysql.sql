-- Set column as NOT NULL to avoid NPE

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 22;

UPDATE `imcms_doc_i18n_meta`
SET `is_enabled` = 1
WHERE `is_enabled` IS NULL;

ALTER TABLE `imcms_doc_i18n_meta`
    MODIFY `is_enabled` tinyint(1) NOT NULL DEFAULT '1';

--
-- Update schema version
--

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;
