CREATE TABLE user_properties
(
  id       INT AUTO_INCREMENT PRIMARY KEY,
  user_id  INT          NOT NULL,
  key_name VARCHAR(255) NOT NULL,
  value    VARCHAR(255) NOT NULL,
  CONSTRAINT UQ_user_properties__user_id__key_name
  UNIQUE (user_id, key_name),
  CONSTRAINT user_properties_FK_user_id_users
  FOREIGN KEY (user_id) REFERENCES users (user_id)
);

UPDATE database_version
SET major = 4, minor = 20;
