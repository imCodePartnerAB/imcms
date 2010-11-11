-- Adds last modified and modified by columns into the document version table

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 6;

ALTER TABLE imcms_doc_versions
  ADD COLUMN modified_dt datetime NULL,
  ADD COLUMN modified_by int NULL,
  ADD CONSTRAINT fk__imcms_doc_versions__users FOREIGN KEY (modified_dt) REFERENCES users (user_id);

-- UPDATE imcms_doc_versions 
-- INSERT INTO imcms_doc_versions (create_dt, created_by, modified_dt, modified_by)
-- SELECT `owner_id`, `date_created`, `owner_id`, `date_modified`
-- FROM meta

--
-- Update schema version
--
UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;



