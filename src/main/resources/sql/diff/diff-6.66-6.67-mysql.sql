SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 67;

DROP TABLE IF EXISTS `images`;
DROP TABLE IF EXISTS `images_history`;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;