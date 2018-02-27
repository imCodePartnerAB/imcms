ALTER TABLE `users`
  ADD COLUMN `last_login` TIMESTAMP NULL;

UPDATE database_version
SET major = 4, minor = 21;
