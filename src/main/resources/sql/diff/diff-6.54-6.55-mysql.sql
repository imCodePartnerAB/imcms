# shifting all role ids to +1 because of stupid 0 id for super admin

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 55;


ALTER TABLE `archive_category_roles`
  DROP FOREIGN KEY `archive_category_roles_role_id_fk`;

ALTER TABLE `archive_library_roles`
  change column created_dt created_dt timestamp default CURRENT_TIMESTAMP not null,
  DROP FOREIGN KEY `archive_library_roles_role_id_fk`;

ALTER TABLE `user_roles_crossref`
  DROP FOREIGN KEY `user_roles_crossref_FK_role_id_roles`;

ALTER TABLE `useradmin_role_crossref`
  DROP FOREIGN KEY `useradmin_role_crossref_FK_role_id_roles`;

ALTER TABLE `roles_permissions`
  DROP FOREIGN KEY `roles_permissions_ibfk_1`;

ALTER TABLE `roles_rights`
  DROP FOREIGN KEY `roles_rights_FK_role_id_roles`;


create temporary table roles_tmp
(
  role_id     int auto_increment
    primary key,
  role_name   varchar(60)     not null,
  admin_role  int default '0' not null,
  permissions int default '0' not null,
  constraint UQ_roles__role_name
  unique (role_name)
)
  charset = utf8;

insert into roles_tmp (select *
                       from roles
                       where role_id > 0);


select @max_id := max(role_id)
from roles_tmp;
set @new_max_id := @max_id + 1;


UPDATE `roles_tmp`
SET `role_id` = `role_id` + @new_max_id;

UPDATE `roles_tmp`
SET `role_id` = `role_id` - @max_id;

SET @sql = CONCAT('ALTER TABLE `roles_tmp` AUTO_INCREMENT = ', @new_max_id);
PREPARE st FROM @sql;
EXECUTE st;


insert into roles_tmp
value (1, 'Superadmin', 0, 1);

truncate table roles;

insert into roles (select *
                   from roles_tmp);

drop table roles_tmp;


UPDATE `archive_category_roles`
SET `role_id` = `role_id` + @new_max_id;

UPDATE `archive_library_roles`
SET `role_id` = `role_id` + @new_max_id;

UPDATE `user_roles_crossref`
SET `role_id` = `role_id` + @new_max_id;

UPDATE `useradmin_role_crossref`
SET `role_id` = `role_id` + @new_max_id;

UPDATE `roles_permissions`
SET `role_id` = `role_id` + @new_max_id;

UPDATE `roles_rights`
SET `role_id` = `role_id` + @new_max_id;

UPDATE `archive_category_roles`
SET `role_id` = `role_id` - @max_id;

UPDATE `archive_library_roles`
SET `role_id` = `role_id` - @max_id;

UPDATE `user_roles_crossref`
SET `role_id` = `role_id` - @max_id;

UPDATE `useradmin_role_crossref`
SET `role_id` = `role_id` - @max_id;

UPDATE `roles_permissions`
SET `role_id` = `role_id` - @max_id;

UPDATE `roles_rights`
SET `role_id` = `role_id` - @max_id;


ALTER TABLE `archive_category_roles`
  add constraint `archive_category_roles_role_id_fk`
foreign key (role_id) references roles (role_id);

ALTER TABLE `archive_library_roles`
  add constraint `archive_library_roles_role_id_fk`
foreign key (role_id) references roles (role_id);

ALTER TABLE `user_roles_crossref`
  add constraint `user_roles_crossref_FK_role_id_roles`
foreign key (role_id) references roles (role_id);

ALTER TABLE `useradmin_role_crossref`
  add constraint `useradmin_role_crossref_FK_role_id_roles`
foreign key (role_id) references roles (role_id);

ALTER TABLE `roles_permissions`
  add constraint `roles_permissions_FK_role_id_roles`
foreign key (role_id) references roles (role_id);

ALTER TABLE `roles_rights`
  add constraint `roles_rights_FK_role_id_roles`
foreign key (role_id) references roles (role_id);




UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;
