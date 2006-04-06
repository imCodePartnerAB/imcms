CREATE PROCEDURE RoleUpdatePermissions @role_id int,  @permissions int AS
/* update permissions for role */
update roles 
Set permissions = @permissions 
where role_id = @role_id
