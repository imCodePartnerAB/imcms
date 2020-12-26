SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 93;

ALTER TABLE roles_permissions
  ADD COLUMN access_to_document_editor tinyint default '0' not null;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;