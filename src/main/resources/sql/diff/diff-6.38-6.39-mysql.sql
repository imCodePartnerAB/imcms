# texts history table upgrade

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 39;

DELETE FROM imcms_text_doc_texts_history
WHERE doc_id IS NULL;

ALTER TABLE imcms_text_doc_texts_history
  MODIFY doc_id INT NOT NULL,
  DROP FOREIGN KEY fk__imcms_text_doc_texts_history__doc_versions,
  ADD CONSTRAINT fk__imcms_text_doc_texts_history__meta FOREIGN KEY (doc_id) REFERENCES meta (meta_id)
  ON DELETE CASCADE,
  DROP COLUMN doc_version_no;

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;
