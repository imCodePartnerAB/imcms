SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 82;

alter table imcms_menu_item
    modify sort_number nvarchar(25) default '' not null;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;