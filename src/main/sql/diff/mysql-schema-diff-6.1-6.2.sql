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
  ADD COLUMN content_index int DEFAULT NULL,
  ADD CONSTRAINT uk__texts__text_in_a_content UNIQUE KEY (meta_id, meta_version, name, language_id, loop_no, content_index);


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
  ADD COLUMN content_index int DEFAULT NULL,
  ADD CONSTRAINT uk__images__image_in_a_content UNIQUE KEY (meta_id, meta_version, name, language_id, loop_no, content_index);


ALTER TABLE images_history
  ADD COLUMN loop_no int DEFAULT NULL,
  ADD COLUMN content_index int DEFAULT NULL;


--
-- Update schema version
--
UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;

ALTER TABLE text_doc_content_loops
  ADD COLUMN content_sequence_index INT NOT NULL DEFAULT 0,
  ADD COLUMN content_lower_order_index INT NOT NULL DEFAULT 0,
  ADD COLUMN content_higher_order_index INT NOT NULL DEFAULT 0;

--
-- CREATE TABLE text_doc_content_indexes (
--    loop_id INT NOT NULL PRIMARY KEY,
--    seq_index INT NOT NULL DEFAULT 0,
--    lower_ord_index INT NOT NULL DEFAULT 0,
--    higher_ord_index INT NOT NULL DEFAULT 0,
--
--    CONSTRAINT fk__text_doc_content_indexes__loop FOREIGN KEY (loop_id) REFERENCES text_doc_content_loops (loop_id)
-- );
