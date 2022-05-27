SET @schema_version__major_new = 7;
SET @schema_version__minor_new = 6;

ALTER TABLE roles_permissions
    ADD COLUMN publish_own_documents tinyint default '0' not null,
    ADD COLUMN publish_all_documents tinyint default '0' not null;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;