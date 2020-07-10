SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 79;

ALTER TABLE imcms_menu_item
    Add column sort_number VARCHAR(50) default '' not null;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;