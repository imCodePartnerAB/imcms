ALTER TABLE `imcms_ip_white_list`
  ADD COLUMN `description` VARCHAR(256) NULL;

UPDATE database_version
SET major = 4, minor = 22;
