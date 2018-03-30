SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 13;

ALTER TABLE imcms_text_doc_contents
  CHANGE COLUMN order_no ix INT NOT NULL;

ALTER TABLE imcms_text_doc_content_loops
  ADD COLUMN next_content_no INT DEFAULT 0,
  ADD COLUMN version INT NOT NULL DEFAULT 0;

UPDATE imcms_text_doc_content_loops l
  SET next_content_no = (
    SELECT coalesce(max(no), 0) FROM imcms_text_doc_contents
    WHERE doc_id = l.doc_id AND doc_version_no = l.doc_version_no);

ALTER TABLE imcms_text_doc_content_loops
  CHANGE COLUMN next_content_no next_content_no INT NOT NULL;

--
-- Update schema version
--
UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;



