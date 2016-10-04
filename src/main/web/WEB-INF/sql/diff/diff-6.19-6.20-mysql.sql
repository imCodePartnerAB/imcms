-- ads versioning support for common contents

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 20;

ALTER TABLE `imcms_doc_i18n_meta` ADD COLUMN `version_no` int(11) DEFAULT 0;
ALTER TABLE `imcms_doc_i18n_meta` DROP KEY `uk__imcms_doc_i18n_meta__doc_id__language_id`;
ALTER TABLE `imcms_doc_i18n_meta` ADD UNIQUE KEY `uk__imcms_doc_i18n_meta__doc_id__language_id__version_no` (`doc_id`,`language_id`,`version_no`);

--
-- Update schema version
--

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;
