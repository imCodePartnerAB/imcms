SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 80;

alter table imcms_menu
    modify type_sort varchar(55) default '';

UPDATE imcms_menu
set type_sort = 'MANUAL'
where nested = 0;
UPDATE imcms_menu
set type_sort = 'TREE_SORT'
where nested = 1;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;