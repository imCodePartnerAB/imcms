SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 91;

insert into roles_permissions (role_id)
select r.role_id
from roles r
where not EXISTS(select * from roles_permissions p where p.role_id = r.role_id);

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;