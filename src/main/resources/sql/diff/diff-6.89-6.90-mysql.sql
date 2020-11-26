SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 90;

alter table imcms_menu
    drop column nested;
alter table document_properties
    modify column value varchar(4096);

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;