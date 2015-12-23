-- adds table for saving template data

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 17;

DROP TABLE IF EXISTS template;

CREATE TABLE template (
  id            INT(11)      NOT NULL AUTO_INCREMENT,
  template_name VARCHAR(255) NOT NULL,
  is_hidden     TINYINT(1)   NOT NULL,
  PRIMARY KEY (id));

INSERT INTO template (template_name,is_hidden) VALUES('demo',false);
INSERT INTO template (template_name,is_hidden) VALUES('demoold',false);
INSERT INTO template (template_name,is_hidden) VALUES('imageArchive',false);
--
-- Update schema version
--

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;
