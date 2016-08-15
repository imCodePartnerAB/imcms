-- ads two columns into table 'meta'

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 18;

ALTER TABLE meta ADD COLUMN `archiver_id` int(11) DEFAULT NULL;
ALTER TABLE meta ADD COLUMN `depublisher_id` int(11) DEFAULT NULL;

--
-- Update schema version
--

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;
