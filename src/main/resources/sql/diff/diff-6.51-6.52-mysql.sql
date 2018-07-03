# simplified composite primary key

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 52;

ALTER TABLE phones
  DROP PRIMARY KEY,
  ADD PRIMARY KEY (phone_id);

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;
