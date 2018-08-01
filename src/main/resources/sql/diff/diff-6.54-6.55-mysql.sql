# new table for local<->external roles linking

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 55;

CREATE TABLE `external_to_local_roles_links` (
  id                   int(11)      not null auto_increment primary key,
  provider_id          varchar(256) not null,
  external_role_id     varchar(256) not null,
  linked_local_role_id int(11)      not null,
  unique (provider_id, external_role_id, linked_local_role_id),
  foreign key role_id_fk (linked_local_role_id) references roles (role_id)
    on delete cascade
);

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;
