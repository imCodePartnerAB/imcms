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
  ADD COLUMN content_index int DEFAULT NULL;


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



-- Adds indexes to content loop table
ALTER TABLE text_doc_content_loops
  ADD COLUMN content_sequence_index INT NOT NULL DEFAULT 0,
  ADD COLUMN content_lower_order_index INT NOT NULL DEFAULT 0,
  ADD COLUMN content_higher_order_index INT NOT NULL DEFAULT 0;


CREATE TEMPORARY TABLE text_doc_content_indexes AS
  SELECT loop_id, max(sequence_index) AS sequence_index,
                  min(order_index) AS lower_order_index,
                  max(order_index) higher_order_index FROM text_doc_contents GROUP BY loop_id;


UPDATE
  text_doc_content_loops t1, text_doc_content_indexes t2
SET
  t1.content_sequence_index  = t2.sequence_index,
  t1.content_lower_order_index = t2.lower_order_index,
  t1.content_higher_order_index = t2.higher_order_index
WHERE
  t1.id = t2.loop_id;



--
-- Update schema version
--
UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;