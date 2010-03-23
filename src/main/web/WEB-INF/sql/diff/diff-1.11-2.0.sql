-- diff-1.11-2.0.sql

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
GO
UPDATE users SET language = 'swe' WHERE lang_id = 1
UPDATE users SET language = 'eng' WHERE lang_id = 2
GO
ALTER TABLE users DROP COLUMN lang_id
GO
ALTER TABLE users DROP COLUMN last_page
GO
ALTER TABLE users DROP COLUMN archive_mode
GO
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

ALTER TABLE text_docs ADD default_template INT
ALTER TABLE text_docs ADD CONSTRAINT FK_text_docs_default_template FOREIGN KEY ( default_template ) REFERENCES templates ( template_id )
GO

-- 2004-12-06 Kreiger

DROP PROCEDURE GetTemplates
DROP PROCEDURE GetUserRolesDocPermissions
DROP PROCEDURE UpdateDefaultTemplates
GO

-- 2004-12-07 Kreiger

DROP PROCEDURE GetUserPermissionSetEx
DROP PROCEDURE SetDocPermissionSetEx
DROP PROCEDURE SetNewDocPermissionSetEx
DROP PROCEDURE DeleteDocPermissionSetEx
DROP PROCEDURE DeleteNewDocPermissionSetEx
DROP PROCEDURE GetRolesDocPermissions
GO

-- 2004-12-09 Kreiger

DROP PROCEDURE RoleGetPermissionsByLanguage
DROP PROCEDURE RoleGetPermissionsFromRole
DROP PROCEDURE RoleCheckConferenceAllowed
DROP PROCEDURE RoleGetConferenceAllowed
DROP TABLE roles_permissions

-- 2004-12-16 Kreiger
