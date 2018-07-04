# prolong mime-type length

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 51;

ALTER TABLE fileupload_docs
  CHANGE COLUMN mime mime varchar(255) not null;

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;
