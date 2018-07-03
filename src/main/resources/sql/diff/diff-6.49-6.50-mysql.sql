# removing redundant lang_id in phone types

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 50;

DELETE FROM phonetypes
WHERE lang_id = 1;

ALTER TABLE phonetypes
  DROP PRIMARY KEY,
  ADD PRIMARY KEY (phonetype_id),
  DROP FOREIGN KEY phonetypes_FK_lang_id_lang_prefixes;

ALTER TABLE phonetypes
  DROP COLUMN lang_id;

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;
