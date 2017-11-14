# removing no more used things and created new document restricted permissions table
START TRANSACTION;

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 33;

ALTER TABLE `meta`
  DROP COLUMN `permissions`;

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;

COMMIT;
