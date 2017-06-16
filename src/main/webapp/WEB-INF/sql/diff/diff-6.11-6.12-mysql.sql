SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 12;


ALTER TABLE archive_images
  DROP COLUMN publish_dt,
  DROP COLUMN archive_dt,
  DROP COLUMN publish_end_dt;


--
-- Update schema version
--
UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;



