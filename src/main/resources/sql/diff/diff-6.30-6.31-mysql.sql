# document role permissions change from int to speaking enum as string
START TRANSACTION;

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 31;

ALTER TABLE `roles_rights`
  ADD COLUMN `permission_tmp` VARCHAR(16) NOT NULL;


UPDATE `roles_rights`
SET `permission_tmp` = 'FULL'
WHERE `set_id` = 0;
UPDATE `roles_rights`
SET `permission_tmp` = 'RESTRICTED_1'
WHERE `set_id` = 1;
UPDATE `roles_rights`
SET `permission_tmp` = 'RESTRICTED_2'
WHERE `set_id` = 2;
UPDATE `roles_rights`
SET `permission_tmp` = 'READ'
WHERE `set_id` = 3;
UPDATE `roles_rights`
SET `permission_tmp` = 'NONE'
WHERE `set_id` = 4;


ALTER TABLE `roles_rights`
  DROP COLUMN `set_id`;
ALTER TABLE `roles_rights`
  CHANGE `permission_tmp` `permission` VARCHAR(16) NOT NULL;

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;

COMMIT;
