SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 86;

DELETE from imcms_test.imcms_languages where id > 2;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;

