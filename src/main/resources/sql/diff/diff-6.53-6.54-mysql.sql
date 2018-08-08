# new table for roles permissions

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 54;

CREATE TABLE `roles_permissions` (
  role_id                        int(11)             not null primary key,
  get_password_by_email          tinyint default '0' not null,
  access_to_admin_pages          tinyint default '0' not null,
  use_images_in_image_archive    tinyint default '0' not null,
  change_images_in_image_archive tinyint default '0' not null,
  constraint role_id_fk foreign key (role_id) references roles (role_id)
);

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;
