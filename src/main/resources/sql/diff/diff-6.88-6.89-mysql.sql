SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 89;

alter table imcms_menu
    add column type_sort varchar(75) not null default '';

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;