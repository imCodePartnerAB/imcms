-- Sets autoincrement on language table primary key

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 7;

ALTER TABLE imcms_languages MODIFY id INT AUTO_INCREMENT;

--
-- Update schema version
--
UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;



