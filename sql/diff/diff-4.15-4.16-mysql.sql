ALTER TABLE users
  MODIFY COLUMN login_password VARCHAR(128) NOT NULL,
  ADD COLUMN login_password_is_encrypted BOOLEAN DEFAULT FALSE,
  ADD COLUMN login_password_reset_id CHAR(36),
  ADD COLUMN login_password_reset_ts BIGINT,
  ADD UNIQUE INDEX ux__users__login_password_reset_id (login_password_reset_id);

INSERT INTO sys_types (type_id, name) VALUES (9, "UserLoginPasswordResetExpirationInterval");
INSERT INTO sys_data (sys_id, type_id, value) VALUES (9, 9, "24");

UPDATE database_version SET major = 4, minor = 16;