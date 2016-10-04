CREATE TABLE `internal_error` (
  `error_id` INT NOT NULL AUTO_INCREMENT,
  `message` TEXT NOT NULL,
  `cause` TEXT NOT NULL,
  `placement` TEXT NULL,
  PRIMARY KEY (`error_id`));

UPDATE database_version SET major = 4, minor = 18;