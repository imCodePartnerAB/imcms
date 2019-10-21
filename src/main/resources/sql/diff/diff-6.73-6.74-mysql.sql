SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 74;

ALTER table imcms_menu
  ADD COLUMN nested boolean not null default false;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;