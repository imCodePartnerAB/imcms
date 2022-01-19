SET @schema_version__major_new = 7;
SET @schema_version__minor_new = 4;

DROP TABLE useradmin_role_crossref;

SELECT @useradmin_role_id := role_id FROM roles WHERE role_name = 'Useradmin';
DELETE FROM user_roles_crossref WHERE role_id = @useradmin_role_id;
DELETE FROM roles_permissions WHERE role_id = @useradmin_role_id;
DELETE FROM roles_rights WHERE role_id = @useradmin_role_id;
DELETE FROM roles WHERE role_id = @useradmin_role_id;

ALTER TABLE roles_permissions DROP COLUMN use_images_in_image_archive, DROP COLUMN change_images_in_image_archive;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;