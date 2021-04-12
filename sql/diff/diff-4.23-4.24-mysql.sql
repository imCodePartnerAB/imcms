ALTER TABLE `users`
    MODIFY COLUMN `login_name` VARCHAR(256) NOT NULL;

ALTER TABLE `users`
    MODIFY COLUMN `login_password` VARCHAR(256) NOT NULL;

UPDATE database_version
SET major = 4, minor = 24;