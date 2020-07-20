SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 81;

alter table imcms_menu
    modify type_sort nvarchar(25);

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;