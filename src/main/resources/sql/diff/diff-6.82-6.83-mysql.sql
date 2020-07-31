SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 83;

alter table imcms_menu_item
    drop column sort_number;

alter table imcms_menu
    drop column type_sort;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;