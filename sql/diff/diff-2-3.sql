DROP PROCEDURE GetCurrentSessionCounter
DROP PROCEDURE GetCurrentSessionCounterDate
DROP PROCEDURE GetTemplateGroupsWithNewPermissions
DROP PROCEDURE GetTemplateGroupsWithPermissions
DROP PROCEDURE GetUserPermissionSet
DROP PROCEDURE GetUserPermissionSetEx
DROP PROCEDURE IncSessionCounter
DROP PROCEDURE SectionGetInheritId
DROP PROCEDURE ServerMasterGet
DROP PROCEDURE StartDocGet
DROP PROCEDURE SystemMessageGet
DROP PROCEDURE WebMasterGet

-- 2005-02-28 Kreiger

DROP PROCEDURE GetDocTypesForUser
DROP PROCEDURE SetRoleDocPermissionSetId

-- 2005-03-01 Kreiger

DROP PROCEDURE DocumentDelete
DROP PROCEDURE GetAllUsersInList

-- 2005-03-03 Kreiger

DROP PROCEDURE FindUserName

-- 2005-03-21 Kreiger

DROP PROCEDURE SectionDelete
DROP PROCEDURE SectionChangeAndDeleteCrossref

-- 2005-04-06 Kreiger

DROP PROCEDURE RoleFindName
DROP PROCEDURE RoleGetName
DROP PROCEDURE RoleUpdateName
DROP PROCEDURE RoleDelete
DROP PROCEDURE RoleDeleteViewAffectedMetaIds
DROP PROCEDURE RoleDeleteViewAffectedUsers

-- 2005-04-13 Kreiger

ALTER TABLE category_types ADD inherited BIT
GO
UPDATE category_types SET inherited = 1 WHERE inherited IS NULL
GO
ALTER TABLE category_types ALTER COLUMN inherited BIT NOT NULL
GO
ALTER TABLE category_types ADD CONSTRAINT UQ__category_types__name UNIQUE ( name )
GO

-- 2005-04-14 Kreiger

DROP PROCEDURE SortOrder_GetExistingDocs
DROP TABLE display_name
DROP TABLE sort_by

-- 2005-04-19 Kreiger

UPDATE meta SET meta_image = REPLACE(meta_image, '../', '')

-- 2005-04-26 Lennart �
