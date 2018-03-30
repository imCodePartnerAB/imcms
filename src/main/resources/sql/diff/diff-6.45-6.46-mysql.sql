# updating image table to support new feature

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 46;

ALTER TABLE imcms_text_doc_images
  DROP COLUMN v_space,
  DROP COLUMN h_space,
  ADD COLUMN top_space INT DEFAULT 0 NOT NULL,
  ADD COLUMN right_space INT DEFAULT 0 NOT NULL,
  ADD COLUMN bottom_space INT DEFAULT 0 NOT NULL,
  ADD COLUMN left_space INT DEFAULT 0 NOT NULL;

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;
