# document content loop tables update
SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 24;

ALTER TABLE `imcms_text_doc_content_loops`
  CHANGE `no` `index` INT NOT NULL,
  DROP COLUMN `next_content_no`;

ALTER TABLE `imcms_text_doc_contents`
  CHANGE `no` `index` INT NOT NULL,
  CHANGE `ix` `order_index` INT NOT NULL;

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;
