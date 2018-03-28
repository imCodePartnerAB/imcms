# image and loop field "no" renamed to "index"
SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 25;

ALTER TABLE `imcms_text_doc_images`
  CHANGE `no` `index` INT NOT NULL,
  CHANGE `content_loop_no` `loop_index` INT NULL,
  CHANGE `content_no` `loop_entry_index` INT NULL;

ALTER TABLE `imcms_text_doc_texts`
  CHANGE `no` `index` INT NOT NULL,
  CHANGE `content_loop_no` `loop_index` INT NULL,
  CHANGE `content_no` `loop_entry_index` INT NULL;

ALTER TABLE `imcms_text_doc_texts_history`
  CHANGE `no` `index` INT NOT NULL,
  CHANGE `content_loop_no` `loop_index` INT NULL,
  CHANGE `content_no` `loop_entry_index` INT NULL;

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;
