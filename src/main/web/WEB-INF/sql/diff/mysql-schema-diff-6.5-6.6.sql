-- Adds last modified and modified by columns into the document version table

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 6;

UPDATE imcms_doc_versions v, meta m
SET v.created_dt = m.date_created, v.created_by = m.owner_id, v.modified_dt = m.date_modified, v.modified_by = m.owner_id
WHERE v.doc_id = m.meta_id;

ALTER TABLE imcms_doc_versions
  DROP FOREIGN KEY fk__imcms_doc_versions__user1,
  DROP FOREIGN KEY fk__imcms_doc_versions__user2;

ALTER TABLE imcms_doc_versions
  MODIFY created_dt datetime NOT NULL,
  MODIFY modified_dt datetime NOT NULL,
  MODIFY created_by int NOT NULL,
  MODIFY modified_by int NOT NULL;

ALTER TABLE imcms_doc_versions
   ADD CONSTRAINT `fk__imcms_doc_versions__user1` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`),
   ADD CONSTRAINT `fk__imcms_doc_versions__user2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`user_id`);

--
-- Update schema version
--
UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;



