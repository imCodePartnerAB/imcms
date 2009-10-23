--
-- Improves content loop support
--

-- New schema version to assign after upgrade
SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 2;


--
-- Adds loop and content reference to texts and texts_history tables. 
--

ALTER TABLE texts
  DROP INDEX uk__texts__meta_id__meta_version__name__language_id,
  -- new columns and constraint
  ADD COLUMN loop_no int DEFAULT NULL,
  ADD COLUMN content_no int DEFAULT NULL,
  ADD CONSTRAINT uk__texts__text_in_a_content UNIQUE KEY (meta_id, meta_version, name, language_id, loop_no, content_no);


ALTER TABLE texts_history
  ADD COLUMN loop_no int DEFAULT NULL,
  ADD COLUMN content_no int DEFAULT NULL;


--
-- Adds loop and content reference to images and images_history tables.
--

ALTER TABLE images
  DROP INDEX uk__images__meta_id__meta_version__name__language_id,
  -- new columns and constraint
  ADD COLUMN loop_no int DEFAULT NULL,
  ADD COLUMN content_no int DEFAULT NULL,
  ADD CONSTRAINT uk__texts__image_in_a_content UNIQUE KEY (meta_id, meta_version, name, language_id, loop_no, content_no);


ALTER TABLE images_history
  ADD COLUMN loop_no int DEFAULT NULL,
  ADD COLUMN content_no int DEFAULT NULL;


--
-- Update schema version
--
UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;