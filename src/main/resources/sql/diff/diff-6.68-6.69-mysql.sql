SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 69;

ALTER TABLE `external_to_local_roles_links`
  MODIFY COLUMN linked_local_role_id INT(11) not null,
  MODIFY COLUMN provider_id varchar(256) COLLATE utf8_swedish_ci NOT NULL,
  MODIFY COLUMN `external_role_id` varchar(256) COLLATE utf8_swedish_ci NOT NULL,
  drop index `provider_id`,
  ADD UNIQUE `provider_id` (provider_id(190), external_role_id(190), linked_local_role_id),
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_swedish_ci;


UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;