# changed text type from int to string

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 57;


ALTER TABLE `imcms_text_doc_texts`
  ADD COLUMN `type_tmp` VARCHAR(32) NOT NULL;

UPDATE `imcms_text_doc_texts`
SET `type_tmp` = 'TEXT'
WHERE `type` = 0
   OR `type` IS NULL
   OR `type` < 0
   OR `type` > 2;
UPDATE `imcms_text_doc_texts`
SET `type_tmp` = 'HTML'
WHERE `type` = 1;
UPDATE `imcms_text_doc_texts`
SET `type_tmp` = 'CLEAN_HTML'
WHERE `type` = 2;

ALTER TABLE `imcms_text_doc_texts`
  DROP COLUMN `type`;
ALTER TABLE `imcms_text_doc_texts`
  CHANGE `type_tmp` `type` VARCHAR(32) NOT NULL;


ALTER TABLE `imcms_text_doc_texts_history`
  ADD COLUMN `type_tmp` VARCHAR(32) NOT NULL;

UPDATE `imcms_text_doc_texts_history`
SET `type_tmp` = 'TEXT'
WHERE `type` = 0
   OR `type` IS NULL
   OR `type` < 0
   OR `type` > 2;
UPDATE `imcms_text_doc_texts_history`
SET `type_tmp` = 'HTML'
WHERE `type` = 1;
UPDATE `imcms_text_doc_texts_history`
SET `type_tmp` = 'CLEAN_HTML'
WHERE `type` = 2;

ALTER TABLE `imcms_text_doc_texts_history`
  DROP COLUMN `type`;
ALTER TABLE `imcms_text_doc_texts_history`
  CHANGE `type_tmp` `type` VARCHAR(32) NOT NULL;


UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;
