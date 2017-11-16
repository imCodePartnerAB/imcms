# removing no more used things and created new document restricted permissions table

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 34;

DROP TABLE `imcms_doc_languages`;
ALTER TABLE `imcms_doc_i18n_meta`
  ADD FOREIGN KEY (doc_id) REFERENCES `meta` (meta_id)
  ON DELETE CASCADE;
ALTER TABLE `imcms_doc_i18n_meta`
  ADD FOREIGN KEY (doc_id, version_no) REFERENCES imcms_doc_versions (doc_id, no)
  ON DELETE CASCADE;

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;
