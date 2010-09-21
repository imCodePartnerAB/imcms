-- This diff fixes cascade deletion of content loop items.
-- When hibernate saves ContentLoop it also saves dependent Content/s mapped using @ElementCollection
-- This mapping recreates contents but as a side effect deletes texts and images which reference contents being recreated.
-- Since contents are never deleted by design this change is safe.

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 3;

ALTER TABLE imcms_text_doc_texts DROP FOREIGN KEY fk__imcms_text_doc_texts__content;
ALTER TABLE imcms_text_doc_images DROP FOREIGN KEY fk__imcms_text_doc_images__content;

--
-- Update schema version
--
UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;
