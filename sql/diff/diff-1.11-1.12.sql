-- diff-1.11-1.12.sql

BEGIN TRANSACTION
SET QUOTED_IDENTIFIER ON
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE
SET ARITHABORT ON
SET NUMERIC_ROUNDABORT OFF
SET CONCAT_NULL_YIELDS_NULL ON
SET ANSI_NULLS ON
SET ANSI_PADDING ON
SET ANSI_WARNINGS ON
COMMIT
BEGIN TRANSACTION
CREATE UNIQUE NONCLUSTERED INDEX IX_users_login_name ON dbo.users
	(
	login_name
	) ON [PRIMARY]
GO
COMMIT

-- 2004-11-12 Lennart Å

ALTER TABLE roles ADD CONSTRAINT roles_role_name UNIQUE ( role_name )

-- 2004-11-15 Kreiger

ALTER TABLE users ADD CONSTRAINT users_login_name UNIQUE ( login_name )

-- 2004-11-18 Kreiger

BEGIN TRANSACTION
ALTER TABLE dbo.roles
	DROP CONSTRAINT DF_roles_permissions
GO
ALTER TABLE dbo.roles
	DROP CONSTRAINT DF_roles_admin_role
GO
CREATE TABLE dbo.Tmp_roles
	(
	role_id int NOT NULL IDENTITY (1, 1),
	role_name varchar(60) NOT NULL,
	permissions int NOT NULL,
	admin_role int NOT NULL
	)  ON [PRIMARY]
GO
ALTER TABLE dbo.Tmp_roles ADD CONSTRAINT
	DF_roles_permissions DEFAULT (0) FOR permissions
GO
ALTER TABLE dbo.Tmp_roles ADD CONSTRAINT
	DF_roles_admin_role DEFAULT (0) FOR admin_role
GO
SET IDENTITY_INSERT dbo.Tmp_roles ON
GO
IF EXISTS(SELECT * FROM dbo.roles)
	 EXEC('INSERT INTO dbo.Tmp_roles (role_id, role_name, permissions, admin_role)
		SELECT role_id, role_name, permissions, admin_role FROM dbo.roles TABLOCKX')
GO
SET IDENTITY_INSERT dbo.Tmp_roles OFF
GO
ALTER TABLE dbo.roles_rights
	DROP CONSTRAINT FK_roles_rights_roles
GO
ALTER TABLE dbo.user_roles_crossref
	DROP CONSTRAINT FK_user_roles_crossref_roles
GO
ALTER TABLE dbo.useradmin_role_crossref
	DROP CONSTRAINT FK_useradmin_role_crossref_roles
GO
DROP TABLE dbo.roles
GO
EXECUTE sp_rename N'dbo.Tmp_roles', N'roles', 'OBJECT'
GO
ALTER TABLE dbo.roles ADD CONSTRAINT
	PK_roles PRIMARY KEY NONCLUSTERED
	(
	role_id
	) ON [PRIMARY]

GO
ALTER TABLE dbo.roles ADD CONSTRAINT
	roles_role_name UNIQUE NONCLUSTERED
	(
	role_name
	) ON [PRIMARY]

GO
COMMIT
BEGIN TRANSACTION
ALTER TABLE dbo.useradmin_role_crossref WITH NOCHECK ADD CONSTRAINT
	FK_useradmin_role_crossref_roles FOREIGN KEY
	(
	role_id
	) REFERENCES dbo.roles
	(
	role_id
	)
GO
COMMIT
BEGIN TRANSACTION
ALTER TABLE dbo.user_roles_crossref WITH NOCHECK ADD CONSTRAINT
	FK_user_roles_crossref_roles FOREIGN KEY
	(
	role_id
	) REFERENCES dbo.roles
	(
	role_id
	)
GO
COMMIT
BEGIN TRANSACTION
ALTER TABLE dbo.roles_rights WITH NOCHECK ADD CONSTRAINT
	FK_roles_rights_roles FOREIGN KEY
	(
	role_id
	) REFERENCES dbo.roles
	(
	role_id
	)
GO
COMMIT

DROP PROCEDURE RoleAddNew
GO

-- 2004-11-25 Kreiger

ALTER TABLE users ADD language VARCHAR(3) NOT NULL DEFAULT ''
UPDATE users SET language = 'swe' WHERE lang_id = 1
UPDATE users SET language = 'eng' WHERE lang_id = 2
ALTER TABLE users DROP COLUMN lang_id
ALTER TABLE users DROP COLUMN last_page
ALTER TABLE users DROP COLUMN archive_mode
DROP PROCEDURE AddNewUser
DROP PROCEDURE UpdateUser
DROP PROCEDURE GetPhoneTypes
DROP PROCEDURE GetPhoneTypeName
DROP PROCEDURE GetLanguageList
DROP PROCEDURE GetUserByLogin
DROP PROCEDURE GetUserInfo
DROP PROCEDURE GetUserPhoneNumbers
DROP PROCEDURE PermissionsGetPermission

-- 2004-11-25 Kreiger
