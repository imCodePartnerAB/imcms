ALTER TABLE users
   ALTER COLUMN login_password NVARCHAR(128) NOT NULL;
ALTER TABLE users
   ADD login_password_is_encrypted BIT DEFAULT 0;

ALTER TABLE users ADD login_password_reset_id CHAR(36) DEFAULT NEWID();
GO
UPDATE users SET login_password_reset_id = NEWID();
GO
CREATE UNIQUE NONCLUSTERED INDEX ux__users__login_password_reset_id ON users ( login_password_reset_id )
ALTER TABLE users
   ADD login_password_reset_ts BIGINT;

INSERT INTO sys_types (type_id, name) VALUES (9, "UserLoginPasswordResetExpirationInterval");
INSERT INTO sys_data (sys_id, type_id, value) VALUES (9, 9, "24");

UPDATE database_version SET major = 4, minor = 16;
