-- Upgrade documents data in DB to have both working and public version if there is no any non-working version yet

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 21;

INSERT INTO `imcms_doc_versions`(doc_id, no, created_by, created_dt, modified_by, modified_dt)
  SELECT doc_id, no+1 as no, created_by, created_dt, modified_by, modified_dt
  FROM imcms_doc_versions
  WHERE NOT EXISTS(SELECT * FROM `imcms_doc_versions` WHERE no > 0);


--
-- Update schema version
--

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;
