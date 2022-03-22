SET @schema_version__major_new = 7;
SET @schema_version__minor_new = 5;

ALTER TABLE meta ADD visible int NOT NULL DEFAULT 0 AFTER owner_id;

SELECT @users_role_id := role_id FROM roles WHERE role_name = 'Users';

UPDATE meta SET visible = 1 WHERE meta_id IN (
    SELECT meta_id FROM roles_rights WHERE role_id = @users_role_id);

DELETE FROM roles_rights WHERE role_id = @users_role_id;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;