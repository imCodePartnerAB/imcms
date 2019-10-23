SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 75;

ALTER table imcms_menu
  ADD COLUMN type_sort nvarchar(55) not null;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;