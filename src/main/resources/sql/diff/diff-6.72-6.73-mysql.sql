SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 73;

DROP table if exists imcms_template_group_crossref;
DROP table if exists imcms_template_group;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;