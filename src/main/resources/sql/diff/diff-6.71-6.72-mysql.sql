SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 72;


ALTER TABLE template
  DROP PRIMARY KEY;
ALTER TABLE template
  ADD template_id INT PRIMARY KEY AUTO_INCREMENT
  first;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;