--------------------------------------------------------------------------------
-- DBArtisan Change Manager Synchronization Script
-- FILE                : c:\Mina Dokument\imCMS1.2-1.3 Upgrade.sql
-- DATE                : 12/14/2000 09:51:40AM
-- 
-- SOURCE DATASOURCE   : dev.imcode.com.Janus_2000
-- TARGET DATASOURCE   : Njord
--------------------------------------------------------------------------------
 
USE intranet
go

--
-- Procedure Drop
-- imse.test
--
IF OBJECT_ID('imse.test') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.test
    IF OBJECT_ID('imse.test') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.test >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.test >>>'
END
go

--
-- Procedure Drop
-- imse.phoneNbrAdd
--
IF OBJECT_ID('imse.phoneNbrAdd') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.phoneNbrAdd
    IF OBJECT_ID('imse.phoneNbrAdd') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.phoneNbrAdd >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.phoneNbrAdd >>>'
END
go

--
-- Procedure Drop
-- imse.meta_select
--
IF OBJECT_ID('imse.meta_select') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.meta_select
    IF OBJECT_ID('imse.meta_select') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.meta_select >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.meta_select >>>'
END
go

--
-- Procedure Drop
-- imse.magnustest
--
IF OBJECT_ID('imse.magnustest') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.magnustest
    IF OBJECT_ID('imse.magnustest') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.magnustest >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.magnustest >>>'
END
go

--
-- Procedure Drop
-- imse.getUserWriteRights
--
IF OBJECT_ID('imse.getUserWriteRights') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.getUserWriteRights
    IF OBJECT_ID('imse.getUserWriteRights') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.getUserWriteRights >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.getUserWriteRights >>>'
END
go

--
-- Procedure Drop
-- imse.getTemplatesInGroup
--
IF OBJECT_ID('imse.getTemplatesInGroup') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.getTemplatesInGroup
    IF OBJECT_ID('imse.getTemplatesInGroup') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.getTemplatesInGroup >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.getTemplatesInGroup >>>'
END
go

--
-- Procedure Drop
-- imse.getTemplates
--
IF OBJECT_ID('imse.getTemplates') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.getTemplates
    IF OBJECT_ID('imse.getTemplates') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.getTemplates >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.getTemplates >>>'
END
go

--
-- Procedure Drop
-- imse.getTemplategroups
--
IF OBJECT_ID('imse.getTemplategroups') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.getTemplategroups
    IF OBJECT_ID('imse.getTemplategroups') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.getTemplategroups >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.getTemplategroups >>>'
END
go

--
-- Procedure Drop
-- imse.getMenuDocChilds
--
IF OBJECT_ID('imse.getMenuDocChilds') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.getMenuDocChilds
    IF OBJECT_ID('imse.getMenuDocChilds') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.getMenuDocChilds >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.getMenuDocChilds >>>'
END
go

--
-- Procedure Drop
-- imse.getLanguages
--
IF OBJECT_ID('imse.getLanguages') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.getLanguages
    IF OBJECT_ID('imse.getLanguages') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.getLanguages >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.getLanguages >>>'
END
go

--
-- Procedure Drop
-- imse.getDocs
--
IF OBJECT_ID('imse.getDocs') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.getDocs
    IF OBJECT_ID('imse.getDocs') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.getDocs >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.getDocs >>>'
END
go

--
-- Procedure Drop
-- imse.getBrowserDocChilds
--
IF OBJECT_ID('imse.getBrowserDocChilds') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.getBrowserDocChilds
    IF OBJECT_ID('imse.getBrowserDocChilds') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.getBrowserDocChilds >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.getBrowserDocChilds >>>'
END
go

--
-- Procedure Drop
-- imse.classification_convert
--
IF OBJECT_ID('imse.classification_convert') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.classification_convert
    IF OBJECT_ID('imse.classification_convert') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.classification_convert >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.classification_convert >>>'
END
go

--
-- Procedure Drop
-- imse.UserPrefsChange
--
IF OBJECT_ID('imse.UserPrefsChange') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.UserPrefsChange
    IF OBJECT_ID('imse.UserPrefsChange') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.UserPrefsChange >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.UserPrefsChange >>>'
END
go

--
-- Procedure Drop
-- imse.UpdateUser
--
IF OBJECT_ID('imse.UpdateUser') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.UpdateUser
    IF OBJECT_ID('imse.UpdateUser') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.UpdateUser >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.UpdateUser >>>'
END
go

--
-- Procedure Drop
-- imse.UpdateTemplateTextsAndImages
--
IF OBJECT_ID('imse.UpdateTemplateTextsAndImages') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.UpdateTemplateTextsAndImages
    IF OBJECT_ID('imse.UpdateTemplateTextsAndImages') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.UpdateTemplateTextsAndImages >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.UpdateTemplateTextsAndImages >>>'
END
go

--
-- Procedure Drop
-- imse.TestJanusDB
--
IF OBJECT_ID('imse.TestJanusDB') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.TestJanusDB
    IF OBJECT_ID('imse.TestJanusDB') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.TestJanusDB >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.TestJanusDB >>>'
END
go

--
-- Procedure Drop
-- imse.SystemMessageSet
--
IF OBJECT_ID('imse.SystemMessageSet') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.SystemMessageSet
    IF OBJECT_ID('imse.SystemMessageSet') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.SystemMessageSet >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.SystemMessageSet >>>'
END
go

--
-- Procedure Drop
-- imse.SystemMessageGet
--
IF OBJECT_ID('imse.SystemMessageGet') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.SystemMessageGet
    IF OBJECT_ID('imse.SystemMessageGet') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.SystemMessageGet >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.SystemMessageGet >>>'
END
go

--
-- Procedure Drop
-- imse.SetSessionCounterDate
--
IF OBJECT_ID('imse.SetSessionCounterDate') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.SetSessionCounterDate
    IF OBJECT_ID('imse.SetSessionCounterDate') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.SetSessionCounterDate >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.SetSessionCounterDate >>>'
END
go

--
-- Procedure Drop
-- imse.RoleUpdatePermissions
--
IF OBJECT_ID('imse.RoleUpdatePermissions') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.RoleUpdatePermissions
    IF OBJECT_ID('imse.RoleUpdatePermissions') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.RoleUpdatePermissions >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.RoleUpdatePermissions >>>'
END
go

--
-- Procedure Drop
-- imse.RoleUpdateName
--
IF OBJECT_ID('imse.RoleUpdateName') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.RoleUpdateName
    IF OBJECT_ID('imse.RoleUpdateName') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.RoleUpdateName >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.RoleUpdateName >>>'
END
go

--
-- Procedure Drop
-- imse.RolePermissionsAddNew
--
IF OBJECT_ID('imse.RolePermissionsAddNew') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.RolePermissionsAddNew
    IF OBJECT_ID('imse.RolePermissionsAddNew') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.RolePermissionsAddNew >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.RolePermissionsAddNew >>>'
END
go

--
-- Procedure Drop
-- imse.RoleGetPermissionsFromRole
--
IF OBJECT_ID('imse.RoleGetPermissionsFromRole') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.RoleGetPermissionsFromRole
    IF OBJECT_ID('imse.RoleGetPermissionsFromRole') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.RoleGetPermissionsFromRole >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.RoleGetPermissionsFromRole >>>'
END
go

--
-- Procedure Drop
-- imse.RoleGetPermissionsByLanguage
--
IF OBJECT_ID('imse.RoleGetPermissionsByLanguage') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.RoleGetPermissionsByLanguage
    IF OBJECT_ID('imse.RoleGetPermissionsByLanguage') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.RoleGetPermissionsByLanguage >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.RoleGetPermissionsByLanguage >>>'
END
go

--
-- Procedure Drop
-- imse.RoleGetName
--
IF OBJECT_ID('imse.RoleGetName') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.RoleGetName
    IF OBJECT_ID('imse.RoleGetName') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.RoleGetName >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.RoleGetName >>>'
END
go

--
-- Procedure Drop
-- imse.RoleGetConferenceAllowed
--
IF OBJECT_ID('imse.RoleGetConferenceAllowed') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.RoleGetConferenceAllowed
    IF OBJECT_ID('imse.RoleGetConferenceAllowed') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.RoleGetConferenceAllowed >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.RoleGetConferenceAllowed >>>'
END
go

--
-- Procedure Drop
-- imse.RoleGetAllApartFromRole
--
IF OBJECT_ID('imse.RoleGetAllApartFromRole') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.RoleGetAllApartFromRole
    IF OBJECT_ID('imse.RoleGetAllApartFromRole') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.RoleGetAllApartFromRole >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.RoleGetAllApartFromRole >>>'
END
go

--
-- Procedure Drop
-- imse.RoleFindName
--
IF OBJECT_ID('imse.RoleFindName') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.RoleFindName
    IF OBJECT_ID('imse.RoleFindName') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.RoleFindName >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.RoleFindName >>>'
END
go

--
-- Procedure Drop
-- imse.RoleDeleteViewAffectedUsers
--
IF OBJECT_ID('imse.RoleDeleteViewAffectedUsers') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.RoleDeleteViewAffectedUsers
    IF OBJECT_ID('imse.RoleDeleteViewAffectedUsers') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.RoleDeleteViewAffectedUsers >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.RoleDeleteViewAffectedUsers >>>'
END
go

--
-- Procedure Drop
-- imse.RoleDeleteViewAffectedMetaIds
--
IF OBJECT_ID('imse.RoleDeleteViewAffectedMetaIds') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.RoleDeleteViewAffectedMetaIds
    IF OBJECT_ID('imse.RoleDeleteViewAffectedMetaIds') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.RoleDeleteViewAffectedMetaIds >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.RoleDeleteViewAffectedMetaIds >>>'
END
go

--
-- Procedure Drop
-- imse.RoleDelete
--
IF OBJECT_ID('imse.RoleDelete') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.RoleDelete
    IF OBJECT_ID('imse.RoleDelete') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.RoleDelete >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.RoleDelete >>>'
END
go

--
-- Procedure Drop
-- imse.RoleCountAffectedUsers
--
IF OBJECT_ID('imse.RoleCountAffectedUsers') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.RoleCountAffectedUsers
    IF OBJECT_ID('imse.RoleCountAffectedUsers') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.RoleCountAffectedUsers >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.RoleCountAffectedUsers >>>'
END
go

--
-- Procedure Drop
-- imse.RoleCount
--
IF OBJECT_ID('imse.RoleCount') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.RoleCount
    IF OBJECT_ID('imse.RoleCount') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.RoleCount >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.RoleCount >>>'
END
go

--
-- Procedure Drop
-- imse.RoleAdminGetAll
--
IF OBJECT_ID('imse.RoleAdminGetAll') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.RoleAdminGetAll
    IF OBJECT_ID('imse.RoleAdminGetAll') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.RoleAdminGetAll >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.RoleAdminGetAll >>>'
END
go

--
-- Procedure Drop
-- imse.RoleAddNew
--
IF OBJECT_ID('imse.RoleAddNew') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.RoleAddNew
    IF OBJECT_ID('imse.RoleAddNew') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.RoleAddNew >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.RoleAddNew >>>'
END
go

--
-- Procedure Drop
-- imse.RemoveUserFromRole
--
IF OBJECT_ID('imse.RemoveUserFromRole') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.RemoveUserFromRole
    IF OBJECT_ID('imse.RemoveUserFromRole') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.RemoveUserFromRole >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.RemoveUserFromRole >>>'
END
go

--
-- Procedure Drop
-- imse.PhoneNbrUpdate
--
IF OBJECT_ID('imse.PhoneNbrUpdate') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.PhoneNbrUpdate
    IF OBJECT_ID('imse.PhoneNbrUpdate') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.PhoneNbrUpdate >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.PhoneNbrUpdate >>>'
END
go

--
-- Procedure Drop
-- imse.PhoneNbrDelete
--
IF OBJECT_ID('imse.PhoneNbrDelete') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.PhoneNbrDelete
    IF OBJECT_ID('imse.PhoneNbrDelete') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.PhoneNbrDelete >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.PhoneNbrDelete >>>'
END
go

--
-- Procedure Drop
-- imse.PermissionsGetPermission
--
IF OBJECT_ID('imse.PermissionsGetPermission') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.PermissionsGetPermission
    IF OBJECT_ID('imse.PermissionsGetPermission') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.PermissionsGetPermission >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.PermissionsGetPermission >>>'
END
go

--
-- Procedure Drop
-- imse.ListDocsGetInternalDocTypesValue
--
IF OBJECT_ID('imse.ListDocsGetInternalDocTypesValue') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.ListDocsGetInternalDocTypesValue
    IF OBJECT_ID('imse.ListDocsGetInternalDocTypesValue') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.ListDocsGetInternalDocTypesValue >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.ListDocsGetInternalDocTypesValue >>>'
END
go

--
-- Procedure Drop
-- imse.ListDocsGetInternalDocTypes
--
IF OBJECT_ID('imse.ListDocsGetInternalDocTypes') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.ListDocsGetInternalDocTypes
    IF OBJECT_ID('imse.ListDocsGetInternalDocTypes') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.ListDocsGetInternalDocTypes >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.ListDocsGetInternalDocTypes >>>'
END
go

--
-- Procedure Drop
-- imse.ListDocsByDate
--
IF OBJECT_ID('imse.ListDocsByDate') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.ListDocsByDate
    IF OBJECT_ID('imse.ListDocsByDate') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.ListDocsByDate >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.ListDocsByDate >>>'
END
go

--
-- Procedure Drop
-- imse.ListConferences
--
IF OBJECT_ID('imse.ListConferences') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.ListConferences
    IF OBJECT_ID('imse.ListConferences') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.ListConferences >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.ListConferences >>>'
END
go

--
-- Procedure Drop
-- imse.IncSessionCounter
--
IF OBJECT_ID('imse.IncSessionCounter') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.IncSessionCounter
    IF OBJECT_ID('imse.IncSessionCounter') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.IncSessionCounter >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.IncSessionCounter >>>'
END
go

--
-- Procedure Drop
-- imse.IPAccessesGetAll
--
IF OBJECT_ID('imse.IPAccessesGetAll') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.IPAccessesGetAll
    IF OBJECT_ID('imse.IPAccessesGetAll') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.IPAccessesGetAll >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.IPAccessesGetAll >>>'
END
go

--
-- Procedure Drop
-- imse.IPAccessUpdate
--
IF OBJECT_ID('imse.IPAccessUpdate') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.IPAccessUpdate
    IF OBJECT_ID('imse.IPAccessUpdate') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.IPAccessUpdate >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.IPAccessUpdate >>>'
END
go

--
-- Procedure Drop
-- imse.IPAccessDelete
--
IF OBJECT_ID('imse.IPAccessDelete') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.IPAccessDelete
    IF OBJECT_ID('imse.IPAccessDelete') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.IPAccessDelete >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.IPAccessDelete >>>'
END
go

--
-- Procedure Drop
-- imse.IPAccessAdd
--
IF OBJECT_ID('imse.IPAccessAdd') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.IPAccessAdd
    IF OBJECT_ID('imse.IPAccessAdd') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.IPAccessAdd >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.IPAccessAdd >>>'
END
go

--
-- Procedure Drop
-- imse.IMC_GetTemplateId
--
IF OBJECT_ID('imse.IMC_GetTemplateId') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.IMC_GetTemplateId
    IF OBJECT_ID('imse.IMC_GetTemplateId') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.IMC_GetTemplateId >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.IMC_GetTemplateId >>>'
END
go

--
-- Procedure Drop
-- imse.IMC_GetSortOrderNum
--
IF OBJECT_ID('imse.IMC_GetSortOrderNum') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.IMC_GetSortOrderNum
    IF OBJECT_ID('imse.IMC_GetSortOrderNum') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.IMC_GetSortOrderNum >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.IMC_GetSortOrderNum >>>'
END
go

--
-- Procedure Drop
-- imse.IMC_GetNbrOfText
--
IF OBJECT_ID('imse.IMC_GetNbrOfText') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.IMC_GetNbrOfText
    IF OBJECT_ID('imse.IMC_GetNbrOfText') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.IMC_GetNbrOfText >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.IMC_GetNbrOfText >>>'
END
go

--
-- Procedure Drop
-- imse.IMC_GetMaxMetaID
--
IF OBJECT_ID('imse.IMC_GetMaxMetaID') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.IMC_GetMaxMetaID
    IF OBJECT_ID('imse.IMC_GetMaxMetaID') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.IMC_GetMaxMetaID >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.IMC_GetMaxMetaID >>>'
END
go

--
-- Procedure Drop
-- imse.IMC_ExecuteExample
--
IF OBJECT_ID('imse.IMC_ExecuteExample') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.IMC_ExecuteExample
    IF OBJECT_ID('imse.IMC_ExecuteExample') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.IMC_ExecuteExample >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.IMC_ExecuteExample >>>'
END
go

--
-- Procedure Drop
-- imse.IMC_CreateNewMeta
--
IF OBJECT_ID('imse.IMC_CreateNewMeta') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.IMC_CreateNewMeta
    IF OBJECT_ID('imse.IMC_CreateNewMeta') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.IMC_CreateNewMeta >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.IMC_CreateNewMeta >>>'
END
go

--
-- Procedure Drop
-- imse.IMC_CheckMenuSort
--
IF OBJECT_ID('imse.IMC_CheckMenuSort') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.IMC_CheckMenuSort
    IF OBJECT_ID('imse.IMC_CheckMenuSort') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.IMC_CheckMenuSort >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.IMC_CheckMenuSort >>>'
END
go

--
-- Procedure Drop
-- imse.IMC_AddUserRights
--
IF OBJECT_ID('imse.IMC_AddUserRights') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.IMC_AddUserRights
    IF OBJECT_ID('imse.IMC_AddUserRights') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.IMC_AddUserRights >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.IMC_AddUserRights >>>'
END
go

--
-- Procedure Drop
-- imse.IMC_AddTexts
--
IF OBJECT_ID('imse.IMC_AddTexts') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.IMC_AddTexts
    IF OBJECT_ID('imse.IMC_AddTexts') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.IMC_AddTexts >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.IMC_AddTexts >>>'
END
go

--
-- Procedure Drop
-- imse.IMC_AddTextDoc
--
IF OBJECT_ID('imse.IMC_AddTextDoc') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.IMC_AddTextDoc
    IF OBJECT_ID('imse.IMC_AddTextDoc') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.IMC_AddTextDoc >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.IMC_AddTextDoc >>>'
END
go

--
-- Procedure Drop
-- imse.IMC_AddRole
--
IF OBJECT_ID('imse.IMC_AddRole') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.IMC_AddRole
    IF OBJECT_ID('imse.IMC_AddRole') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.IMC_AddRole >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.IMC_AddRole >>>'
END
go

--
-- Procedure Drop
-- imse.IMC_AddOwnerRights
--
IF OBJECT_ID('imse.IMC_AddOwnerRights') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.IMC_AddOwnerRights
    IF OBJECT_ID('imse.IMC_AddOwnerRights') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.IMC_AddOwnerRights >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.IMC_AddOwnerRights >>>'
END
go

--
-- Procedure Drop
-- imse.IMC_AddImageRef
--
IF OBJECT_ID('imse.IMC_AddImageRef') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.IMC_AddImageRef
    IF OBJECT_ID('imse.IMC_AddImageRef') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.IMC_AddImageRef >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.IMC_AddImageRef >>>'
END
go

--
-- Procedure Drop
-- imse.IMC_AddImage
--
IF OBJECT_ID('imse.IMC_AddImage') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.IMC_AddImage
    IF OBJECT_ID('imse.IMC_AddImage') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.IMC_AddImage >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.IMC_AddImage >>>'
END
go

--
-- Procedure Drop
-- imse.IMC_AddChild
--
IF OBJECT_ID('imse.IMC_AddChild') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.IMC_AddChild
    IF OBJECT_ID('imse.IMC_AddChild') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.IMC_AddChild >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.IMC_AddChild >>>'
END
go

--
-- Procedure Drop
-- imse.GetUsersWhoBelongsToRole
--
IF OBJECT_ID('imse.GetUsersWhoBelongsToRole') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.GetUsersWhoBelongsToRole
    IF OBJECT_ID('imse.GetUsersWhoBelongsToRole') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.GetUsersWhoBelongsToRole >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.GetUsersWhoBelongsToRole >>>'
END
go

--
-- Procedure Drop
-- imse.GetUserTypes
--
IF OBJECT_ID('imse.GetUserTypes') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.GetUserTypes
    IF OBJECT_ID('imse.GetUserTypes') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.GetUserTypes >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.GetUserTypes >>>'
END
go

--
-- Procedure Drop
-- imse.GetUserType
--
IF OBJECT_ID('imse.GetUserType') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.GetUserType
    IF OBJECT_ID('imse.GetUserType') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.GetUserType >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.GetUserType >>>'
END
go

--
-- Procedure Drop
-- imse.GetUserRolesIds
--
IF OBJECT_ID('imse.GetUserRolesIds') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.GetUserRolesIds
    IF OBJECT_ID('imse.GetUserRolesIds') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.GetUserRolesIds >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.GetUserRolesIds >>>'
END
go

--
-- Procedure Drop
-- imse.GetUserPhones
--
IF OBJECT_ID('imse.GetUserPhones') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.GetUserPhones
    IF OBJECT_ID('imse.GetUserPhones') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.GetUserPhones >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.GetUserPhones >>>'
END
go

--
-- Procedure Drop
-- imse.GetUserPhoneNumbers
--
IF OBJECT_ID('imse.GetUserPhoneNumbers') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.GetUserPhoneNumbers
    IF OBJECT_ID('imse.GetUserPhoneNumbers') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.GetUserPhoneNumbers >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.GetUserPhoneNumbers >>>'
END
go

--
-- Procedure Drop
-- imse.GetUserPassword
--
IF OBJECT_ID('imse.GetUserPassword') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.GetUserPassword
    IF OBJECT_ID('imse.GetUserPassword') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.GetUserPassword >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.GetUserPassword >>>'
END
go

--
-- Procedure Drop
-- imse.GetUserNames
--
IF OBJECT_ID('imse.GetUserNames') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.GetUserNames
    IF OBJECT_ID('imse.GetUserNames') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.GetUserNames >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.GetUserNames >>>'
END
go

--
-- Procedure Drop
-- imse.GetUserInfo
--
IF OBJECT_ID('imse.GetUserInfo') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.GetUserInfo
    IF OBJECT_ID('imse.GetUserInfo') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.GetUserInfo >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.GetUserInfo >>>'
END
go

--
-- Procedure Drop
-- imse.GetUserIdFromName
--
IF OBJECT_ID('imse.GetUserIdFromName') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.GetUserIdFromName
    IF OBJECT_ID('imse.GetUserIdFromName') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.GetUserIdFromName >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.GetUserIdFromName >>>'
END
go

--
-- Procedure Drop
-- imse.GetUserId
--
IF OBJECT_ID('imse.GetUserId') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.GetUserId
    IF OBJECT_ID('imse.GetUserId') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.GetUserId >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.GetUserId >>>'
END
go

--
-- Procedure Drop
-- imse.GetUserCreateDate
--
IF OBJECT_ID('imse.GetUserCreateDate') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.GetUserCreateDate
    IF OBJECT_ID('imse.GetUserCreateDate') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.GetUserCreateDate >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.GetUserCreateDate >>>'
END
go

--
-- Procedure Drop
-- imse.GetTexts
--
IF OBJECT_ID('imse.GetTexts') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.GetTexts
    IF OBJECT_ID('imse.GetTexts') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.GetTexts >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.GetTexts >>>'
END
go

--
-- Procedure Drop
-- imse.GetTextDocData
--
IF OBJECT_ID('imse.GetTextDocData') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.GetTextDocData
    IF OBJECT_ID('imse.GetTextDocData') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.GetTextDocData >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.GetTextDocData >>>'
END
go

--
-- Procedure Drop
-- imse.GetNoOfTemplates
--
IF OBJECT_ID('imse.GetNoOfTemplates') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.GetNoOfTemplates
    IF OBJECT_ID('imse.GetNoOfTemplates') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.GetNoOfTemplates >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.GetNoOfTemplates >>>'
END
go

--
-- Procedure Drop
-- imse.GetNewUserId
--
IF OBJECT_ID('imse.GetNewUserId') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.GetNewUserId
    IF OBJECT_ID('imse.GetNewUserId') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.GetNewUserId >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.GetNewUserId >>>'
END
go

--
-- Procedure Drop
-- imse.GetMetaPathInfo
--
IF OBJECT_ID('imse.GetMetaPathInfo') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.GetMetaPathInfo
    IF OBJECT_ID('imse.GetMetaPathInfo') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.GetMetaPathInfo >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.GetMetaPathInfo >>>'
END
go

--
-- Procedure Drop
-- imse.GetLanguageList
--
IF OBJECT_ID('imse.GetLanguageList') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.GetLanguageList
    IF OBJECT_ID('imse.GetLanguageList') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.GetLanguageList >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.GetLanguageList >>>'
END
go

--
-- Procedure Drop
-- imse.GetLangPrefixFromId
--
IF OBJECT_ID('imse.GetLangPrefixFromId') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.GetLangPrefixFromId
    IF OBJECT_ID('imse.GetLangPrefixFromId') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.GetLangPrefixFromId >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.GetLangPrefixFromId >>>'
END
go

--
-- Procedure Drop
-- imse.GetLangPrefix
--
IF OBJECT_ID('imse.GetLangPrefix') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.GetLangPrefix
    IF OBJECT_ID('imse.GetLangPrefix') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.GetLangPrefix >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.GetLangPrefix >>>'
END
go

--
-- Procedure Drop
-- imse.GetImgs
--
IF OBJECT_ID('imse.GetImgs') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.GetImgs
    IF OBJECT_ID('imse.GetImgs') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.GetImgs >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.GetImgs >>>'
END
go

--
-- Procedure Drop
-- imse.GetHighestUserId
--
IF OBJECT_ID('imse.GetHighestUserId') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.GetHighestUserId
    IF OBJECT_ID('imse.GetHighestUserId') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.GetHighestUserId >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.GetHighestUserId >>>'
END
go

--
-- Procedure Drop
-- imse.GetDocType
--
IF OBJECT_ID('imse.GetDocType') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.GetDocType
    IF OBJECT_ID('imse.GetDocType') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.GetDocType >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.GetDocType >>>'
END
go

--
-- Procedure Drop
-- imse.GetCurrentSessionCounterDate
--
IF OBJECT_ID('imse.GetCurrentSessionCounterDate') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.GetCurrentSessionCounterDate
    IF OBJECT_ID('imse.GetCurrentSessionCounterDate') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.GetCurrentSessionCounterDate >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.GetCurrentSessionCounterDate >>>'
END
go

--
-- Procedure Drop
-- imse.GetCurrentSessionCounter
--
IF OBJECT_ID('imse.GetCurrentSessionCounter') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.GetCurrentSessionCounter
    IF OBJECT_ID('imse.GetCurrentSessionCounter') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.GetCurrentSessionCounter >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.GetCurrentSessionCounter >>>'
END
go

--
-- Procedure Drop
-- imse.GetChilds
--
IF OBJECT_ID('imse.GetChilds') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.GetChilds
    IF OBJECT_ID('imse.GetChilds') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.GetChilds >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.GetChilds >>>'
END
go

--
-- Procedure Drop
-- imse.GetCategoryUsers
--
IF OBJECT_ID('imse.GetCategoryUsers') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.GetCategoryUsers
    IF OBJECT_ID('imse.GetCategoryUsers') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.GetCategoryUsers >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.GetCategoryUsers >>>'
END
go

--
-- Procedure Drop
-- imse.GetAllUsersInList
--
IF OBJECT_ID('imse.GetAllUsersInList') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.GetAllUsersInList
    IF OBJECT_ID('imse.GetAllUsersInList') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.GetAllUsersInList >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.GetAllUsersInList >>>'
END
go

--
-- Procedure Drop
-- imse.GetAllUsers
--
IF OBJECT_ID('imse.GetAllUsers') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.GetAllUsers
    IF OBJECT_ID('imse.GetAllUsers') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.GetAllUsers >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.GetAllUsers >>>'
END
go

--
-- Procedure Drop
-- imse.GetAllRoles
--
IF OBJECT_ID('imse.GetAllRoles') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.GetAllRoles
    IF OBJECT_ID('imse.GetAllRoles') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.GetAllRoles >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.GetAllRoles >>>'
END
go

--
-- Procedure Drop
-- imse.GetAdminChilds
--
IF OBJECT_ID('imse.GetAdminChilds') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.GetAdminChilds
    IF OBJECT_ID('imse.GetAdminChilds') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.GetAdminChilds >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.GetAdminChilds >>>'
END
go

--
-- Procedure Drop
-- imse.FindUserName
--
IF OBJECT_ID('imse.FindUserName') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.FindUserName
    IF OBJECT_ID('imse.FindUserName') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.FindUserName >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.FindUserName >>>'
END
go

--
-- Procedure Drop
-- imse.FindMetaId
--
IF OBJECT_ID('imse.FindMetaId') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.FindMetaId
    IF OBJECT_ID('imse.FindMetaId') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.FindMetaId >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.FindMetaId >>>'
END
go

--
-- Procedure Drop
-- imse.DelUserRoles
--
IF OBJECT_ID('imse.DelUserRoles') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.DelUserRoles
    IF OBJECT_ID('imse.DelUserRoles') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.DelUserRoles >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.DelUserRoles >>>'
END
go

--
-- Procedure Drop
-- imse.DelUser
--
IF OBJECT_ID('imse.DelUser') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.DelUser
    IF OBJECT_ID('imse.DelUser') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.DelUser >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.DelUser >>>'
END
go

--
-- Procedure Drop
-- imse.Classification_Get_All
--
IF OBJECT_ID('imse.Classification_Get_All') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.Classification_Get_All
    IF OBJECT_ID('imse.Classification_Get_All') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.Classification_Get_All >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.Classification_Get_All >>>'
END
go

--
-- Procedure Drop
-- imse.Classification_Fix
--
IF OBJECT_ID('imse.Classification_Fix') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.Classification_Fix
    IF OBJECT_ID('imse.Classification_Fix') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.Classification_Fix >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.Classification_Fix >>>'
END
go

--
-- Procedure Drop
-- imse.ClassificationAdd
--
IF OBJECT_ID('imse.ClassificationAdd') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.ClassificationAdd
    IF OBJECT_ID('imse.ClassificationAdd') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.ClassificationAdd >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.ClassificationAdd >>>'
END
go

--
-- Procedure Drop
-- imse.CheckExistsInMenu
--
IF OBJECT_ID('imse.CheckExistsInMenu') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.CheckExistsInMenu
    IF OBJECT_ID('imse.CheckExistsInMenu') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.CheckExistsInMenu >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.CheckExistsInMenu >>>'
END
go

--
-- Procedure Drop
-- imse.CheckAdminRights
--
IF OBJECT_ID('imse.CheckAdminRights') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.CheckAdminRights
    IF OBJECT_ID('imse.CheckAdminRights') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.CheckAdminRights >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.CheckAdminRights >>>'
END
go

--
-- Procedure Drop
-- imse.ChangeUserActiveStatus
--
IF OBJECT_ID('imse.ChangeUserActiveStatus') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.ChangeUserActiveStatus
    IF OBJECT_ID('imse.ChangeUserActiveStatus') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.ChangeUserActiveStatus >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.ChangeUserActiveStatus >>>'
END
go

--
-- Procedure Drop
-- imse.AddUserRole
--
IF OBJECT_ID('imse.AddUserRole') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.AddUserRole
    IF OBJECT_ID('imse.AddUserRole') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.AddUserRole >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.AddUserRole >>>'
END
go

--
-- Procedure Drop
-- imse.AddNewuser
--
IF OBJECT_ID('imse.AddNewuser') IS NOT NULL
BEGIN
    DROP PROCEDURE imse.AddNewuser
    IF OBJECT_ID('imse.AddNewuser') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE imse.AddNewuser >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE imse.AddNewuser >>>'
END
go

--
-- Procedure Drop
-- dbo.testProc
--
IF OBJECT_ID('dbo.testProc') IS NOT NULL
BEGIN
    DROP PROCEDURE dbo.testProc
    IF OBJECT_ID('dbo.testProc') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE dbo.testProc >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE dbo.testProc >>>'
END
go

--
-- Procedure Drop
-- dbo.getDocsParentCount
--
IF OBJECT_ID('dbo.getDocsParentCount') IS NOT NULL
BEGIN
    DROP PROCEDURE dbo.getDocsParentCount
    IF OBJECT_ID('dbo.getDocsParentCount') IS NOT NULL
        PRINT '<<< FAILED DROPPING PROCEDURE dbo.getDocsParentCount >>>'
    ELSE
        PRINT '<<< DROPPED PROCEDURE dbo.getDocsParentCount >>>'
END
go

--
-- Table Drop
-- dbo.user_roles_OLD
--
DROP TABLE dbo.user_roles_OLD
go

--
-- Table Drop
-- dbo.urls_OLD
--
DROP TABLE dbo.urls_OLD
go

--
-- Table Drop
-- dbo.status_se_OLD
--
DROP TABLE dbo.status_se_OLD
go

--
-- Table Drop
-- dbo.status_OLD
--
DROP TABLE dbo.status_OLD
go

--
-- Table Drop
-- dbo.processings_se_OLD
--
DROP TABLE dbo.processings_se_OLD
go

--
-- Table Drop
-- dbo.processings_OLD
--
DROP TABLE dbo.processings_OLD
go

--
-- Table Drop
-- dbo.parents_OLD
--
DROP TABLE dbo.parents_OLD
go

--
-- Table Drop
-- dbo.new_images_OLD
--
DROP TABLE dbo.new_images_OLD
go

--
-- Table Drop
-- dbo.meta_logs_OLD
--
DROP TABLE dbo.meta_logs_OLD
go

--
-- Table Drop
-- dbo.meta_log_types_OLD
--
DROP TABLE dbo.meta_log_types_OLD
go

--
-- Table Drop
-- dbo.languages_se_OLD
--
DROP TABLE dbo.languages_se_OLD
go

--
-- Table Drop
-- dbo.image_docs_OLD
--
DROP TABLE dbo.image_docs_OLD
go

--
-- Table Drop
-- dbo.help_texts_OLD
--
DROP TABLE dbo.help_texts_OLD
go

--
-- Table Drop
-- dbo.help_text_se_OLD
--
DROP TABLE dbo.help_text_se_OLD
go

--
-- Table Drop
-- dbo.domains_OLD
--
DROP TABLE dbo.domains_OLD
go

--
-- Table Drop
-- dbo.doc_logs_OLD
--
DROP TABLE dbo.doc_logs_OLD
go

--
-- Table Drop
-- dbo.doc_log_types_OLD
--
DROP TABLE dbo.doc_log_types_OLD
go

--
-- Table Drop
-- dbo.categories_se_OLD
--
DROP TABLE dbo.categories_se_OLD
go

--
-- Table Drop
-- dbo.categories_OLD
--
DROP TABLE dbo.categories_OLD
go

--
-- Table Drop
-- dbo.admin_table_OLD
--
DROP TABLE dbo.admin_table_OLD
go

--
-- Table Drop
-- dbo.admin_roles_OLD
--
DROP TABLE dbo.admin_roles_OLD
go

USE master
go

--
-- Remote Server Drop
-- NJORD
--
IF EXISTS (SELECT * FROM master.dbo.sysservers WHERE srvname='NJORD')
BEGIN
    EXEC sp_dropserver 'NJORD'
    IF EXISTS (SELECT * FROM master.dbo.sysservers WHERE srvname='NJORD')
        PRINT '<<< FAILED DROPPING REMOTE SERVER NJORD >>>'
    ELSE
        PRINT '<<< DROPPED REMOTE SERVER NJORD >>>'
END
go

--
-- Remote Server Drop
-- FTIndexWeb
--
IF EXISTS (SELECT * FROM master.dbo.sysservers WHERE srvname='FTIndexWeb')
BEGIN
    EXEC sp_dropserver 'FTIndexWeb'
    IF EXISTS (SELECT * FROM master.dbo.sysservers WHERE srvname='FTIndexWeb')
        PRINT '<<< FAILED DROPPING REMOTE SERVER FTIndexWeb >>>'
    ELSE
        PRINT '<<< DROPPED REMOTE SERVER FTIndexWeb >>>'
END
go

USE master
go

--
-- Remote Server Create
-- IMCWEB
--
USE master
go
EXEC sp_addserver @server='IMCWEB', @local='local', @duplicate_ok=NULL
go
EXEC sp_serveroption 'IMCWEB', 'rpc out', true
go

USE intranet
go

--
-- Table Alter
-- dbo.browser_docs
--
ALTER TABLE dbo.browser_docs ADD DEFAULT 0 FOR browser_id
go
ALTER TABLE dbo.browser_docs DROP CONSTRAINT FK_browser_docs_browsers
go
ALTER TABLE dbo.browser_docs ADD CONSTRAINT FK_browser_docs_meta
FOREIGN KEY (meta_id)
REFERENCES dbo.meta (meta_id)
go
ALTER TABLE dbo.browser_docs DROP CONSTRAINT FK_browser_docs_meta
go

--
-- Table Alter
-- dbo.childs
--
ALTER TABLE dbo.childs ADD CONSTRAINT FK_childs_meta1
FOREIGN KEY (meta_id)
REFERENCES dbo.meta (meta_id)
go

--
-- Table Create
-- dbo.doc_permission_sets
--
CREATE TABLE dbo.doc_permission_sets 
(
    meta_id       int NOT NULL,
    set_id        int NOT NULL,
    permission_id int NOT NULL,
    CONSTRAINT PK_doc_permission_sets
    PRIMARY KEY NONCLUSTERED (meta_id,set_id)
)
go

--
-- Table Create
-- dbo.doc_permission_sets_ex
--
CREATE TABLE dbo.doc_permission_sets_ex 
(
    meta_id         int NOT NULL,
    set_id          int NOT NULL,
    permission_id   int NOT NULL,
    permission_data int NOT NULL,
    CONSTRAINT PK_permission_sets_ex
    PRIMARY KEY NONCLUSTERED (meta_id,set_id,permission_id,permission_data)
)
go

--
-- Table Create
-- dbo.doc_permissions
--
CREATE TABLE dbo.doc_permissions 
(
    permission_id int         NOT NULL,
    doc_type      int         NOT NULL,
    lang_prefix   varchar(3)  NOT NULL,
    description   varchar(50) NOT NULL,
    CONSTRAINT PK_doc_permissions
    PRIMARY KEY NONCLUSTERED (permission_id,doc_type,lang_prefix)
)
go

--
-- Table Extended Alter
-- dbo.doc_types
--
EXEC sp_rename 'dbo.doc_types','doc_types_12142000071744000',OBJECT
go
CREATE TABLE dbo.doc_types 
(
    doc_type    int         NOT NULL,
    lang_prefix varchar(3)  DEFAULT 'se' NOT NULL,
    type        varchar(50) NULL
)
go
INSERT INTO dbo.doc_types(
                          doc_type,
--                        lang_prefix,
                          type
                         )
                   SELECT 
                          doc_type,
--                        'se',
                          CONVERT(varchar(50),type)
                     FROM dbo.doc_types_12142000071744000
go
ALTER TABLE dbo.doc_types ADD CONSTRAINT PK_doc_types
PRIMARY KEY NONCLUSTERED (doc_type,lang_prefix) 
go

--
-- Table Alter
-- dbo.frameset_docs
--
ALTER TABLE dbo.frameset_docs ADD CONSTRAINT FK_frameset_docs_meta
FOREIGN KEY (meta_id)
REFERENCES dbo.meta (meta_id)
go

--
-- Table Alter
-- dbo.images
--
ALTER TABLE dbo.images ADD CONSTRAINT FK_images_meta
FOREIGN KEY (meta_id)
REFERENCES dbo.meta (meta_id)
go

--
-- Table Extended Alter
-- dbo.ip_accesses
--
DECLARE @string VARCHAR(15)
DECLARE @string_start VARCHAR(15)
DECLARE @string_end VARCHAR(15)

--Don't forget to change ip_end to ip_start too.
DECLARE my_curse CURSOR
FOR (SELECT ip_start, ip_end FROM ip_accesses)

DECLARE @ipnum INT
DECLARE @index INT
DECLARE @oldindex INT
DECLARE @ip DECIMAL
DECLARE @exp INT

OPEN my_curse

FETCH NEXT FROM my_curse
INTO @string_start, @string_end

WHILE @@FETCH_STATUS = 0 BEGIN
	SET @ip = 0
	SET @exp = 4
	SET @index = 1
	SET @oldindex = 1
	SET @string = @string_start
	WHILE @index <= LEN(@string)+1 BEGIN
		IF SUBSTRING(@string,@index,1) = '.' OR @index = LEN(@string)+1  BEGIN
			SET @exp = @exp - 1
			SET @ipnum = CAST(SUBSTRING(@string,@oldindex,@index-@oldindex) AS INT)
			SET @oldindex = @index + 1
			SET @ip = @ip + POWER(256.0, @exp) * @ipnum
		END
		SET @index = @index+1
	END

	UPDATE ip_accesses SET ip_start = @ip
	WHERE CURRENT OF my_curse

	SET @ip = 0
	SET @exp = 4
	SET @index = 1
	SET @oldindex = 1
	SET @string = @string_end
	WHILE @index <= LEN(@string)+1 BEGIN
		IF SUBSTRING(@string,@index,1) = '.' OR @index = LEN(@string)+1  BEGIN
			SET @exp = @exp - 1
			SET @ipnum = CAST(SUBSTRING(@string,@oldindex,@index-@oldindex) AS INT)
			SET @oldindex = @index + 1
			SET @ip = @ip + POWER(256.0, @exp) * @ipnum
		END
		SET @index = @index+1
	END

	UPDATE ip_accesses SET ip_end = @ip
	WHERE CURRENT OF my_curse

	FETCH NEXT FROM my_curse
	INTO @string_start, @string_end
END
CLOSE my_curse
DEALLOCATE my_curse
GO

ALTER TABLE ip_accesses ALTER COLUMN ip_start DECIMAL NOT NULL
ALTER TABLE ip_accesses ALTER COLUMN ip_end DECIMAL NOT NULL
GO
--
-- Table Extended Alter
-- dbo.languages
--
EXEC sp_rename 'dbo.languages','languages_12142000071751000',OBJECT
go
CREATE TABLE dbo.languages 
(
    lang_prefix varchar(3)  NOT NULL,
    user_prefix varchar(3)  NOT NULL,
    language    varchar(30) NULL
)
go
INSERT INTO dbo.languages(
                          lang_prefix,
                          user_prefix,
                          language
                         )
                   SELECT 
                          CONVERT(varchar(3),lang_prefix),
                          ' ',
                          language
                     FROM dbo.languages_12142000071751000
go
ALTER TABLE dbo.languages ADD CONSTRAINT PK_languages
PRIMARY KEY NONCLUSTERED (lang_prefix,user_prefix) 
go

--
-- Table Extended Alter
-- dbo.meta
--
sp_rename 'meta.category_id', 'owner_id', 'COLUMN'
GO

sp_rename 'meta.processing_id', 'permissions', 'COLUMN'
GO

UPDATE meta SET permissions = 0
GO

UPDATE meta SET owner_id = ISNULL((SELECT TOP 1 user_id FROM user_rights WHERE meta_id = meta.meta_id AND permission_id = 99),0)
GO


--
-- Table Alter
-- dbo.meta_classification
--
ALTER TABLE dbo.meta_classification DROP CONSTRAINT FK_meta_classification_classification
go
ALTER TABLE dbo.meta_classification ADD CONSTRAINT FK_meta_classification_meta
FOREIGN KEY (meta_id)
REFERENCES dbo.meta (meta_id)
go
ALTER TABLE dbo.meta_classification DROP CONSTRAINT FK_meta_classification_meta
go
ALTER TABLE dbo.meta_classification ADD CONSTRAINT FK_meta_classification_classification
FOREIGN KEY (class_id)
REFERENCES dbo.classification (class_id)
go

--
-- Table Extended Alter
-- dbo.mime_types
--
EXEC sp_rename 'dbo.mime_types','mime_types_12142000071757000',OBJECT
go
CREATE TABLE dbo.mime_types 
(
    mime_id     int         IDENTITY,
    mime_name   varchar(50) NOT NULL,
    mime        varchar(50) NOT NULL,
    lang_prefix varchar(3)  DEFAULT 'se' NOT NULL
)
go
SET IDENTITY_INSERT dbo.mime_types ON
go
INSERT INTO dbo.mime_types(
                           mime_id,
                           mime_name,
                           mime,
                           lang_prefix
                          )
                    SELECT 
                           mime_id,
                           mime_name,
                           mime,
                           lang_prefix
                      FROM dbo.mime_types_12142000071757000
go
SET IDENTITY_INSERT dbo.mime_types OFF
go
ALTER TABLE dbo.mime_types ADD CONSTRAINT PK_mime_types
PRIMARY KEY NONCLUSTERED (mime_id) 
go

--
-- Table Create
-- dbo.new_doc_permission_sets
--
CREATE TABLE dbo.new_doc_permission_sets 
(
    meta_id       int NOT NULL,
    set_id        int NOT NULL,
    permission_id int NOT NULL,
    CONSTRAINT PK_new_doc_permission_sets
    PRIMARY KEY NONCLUSTERED (meta_id,set_id)
)
go

--
-- Table Create
-- dbo.new_doc_permission_sets_ex
--
CREATE TABLE dbo.new_doc_permission_sets_ex 
(
    meta_id         int NOT NULL,
    set_id          int NOT NULL,
    permission_id   int NOT NULL,
    permission_data int NOT NULL,
    CONSTRAINT PK_new_doc_permission_sets_ex
    PRIMARY KEY NONCLUSTERED (meta_id,set_id,permission_id,permission_data)
)
go

--
-- Table Create
-- dbo.permission_sets
--
CREATE TABLE dbo.permission_sets 
(
    set_id      int         NOT NULL,
    description varchar(30) NOT NULL,
    CONSTRAINT PK_permission_types
    PRIMARY KEY NONCLUSTERED (set_id)
)
go

--
-- Table Extended Alter
-- dbo.permissions
--
EXEC sp_rename 'dbo.permissions','permission_12142000071802000',OBJECT
go
CREATE TABLE dbo.permissions 
(
    permission_id tinyint     NOT NULL,
    lang_prefix   varchar(3)  DEFAULT 'se' NOT NULL,
    description   varchar(50) NOT NULL
)
go
INSERT INTO dbo.permissions(
                            permission_id,
--                          lang_prefix,
                            description
                           )
                     SELECT 
                            permission_id,
--                          'se',
                            CONVERT(varchar(50),description)
                       FROM dbo.permission_12142000071802000
go
ALTER TABLE dbo.permissions ADD CONSTRAINT PK_permissions
PRIMARY KEY NONCLUSTERED (permission_id,lang_prefix) 
go

--
-- Table Alter
-- dbo.phones
--
ALTER TABLE dbo.phones ADD CONSTRAINT PK_phones
PRIMARY KEY NONCLUSTERED (phone_id,user_id) 
go
ALTER TABLE dbo.phones ADD CONSTRAINT FK_phones_users
FOREIGN KEY (user_id)
REFERENCES dbo.users (user_id)
go

--
-- Table Alter
-- dbo.roles
--
ALTER TABLE dbo.roles ADD CONSTRAINT PK_roles
PRIMARY KEY NONCLUSTERED (role_id) 
go

--
-- Table Extended Alter
-- dbo.roles_rights
--
EXEC sp_rename 'roles_rights.permission_id', 'set_id', 'COLUMN'
go

UPDATE roles_rights SET set_id = 0 where set_id = 3
UPDATE roles_rights SET set_id = 3 where set_id = 1
go

--
-- Table Create
-- dbo.stats
--
CREATE TABLE dbo.stats 
(
    name varchar(120) NOT NULL,
    num  int          NOT NULL,
    CONSTRAINT stats_pk
    PRIMARY KEY CLUSTERED (name)
)
go

--
-- Table Extended Alter
-- dbo.sys_data
--
EXEC sp_rename 'dbo.sys_data','sys_data_12142000071810000',OBJECT
go
CREATE TABLE dbo.sys_data 
(
    sys_id  tinyint     IDENTITY,
    type_id tinyint     NOT NULL,
    value   varchar(80) NULL
)
go
SET IDENTITY_INSERT dbo.sys_data ON
go
INSERT INTO dbo.sys_data(
                         sys_id,
                         type_id,
                         value
                        )
                  SELECT 
                         sys_id,
                         type_id,
                         CONVERT(varchar(80),value)
                    FROM dbo.sys_data_12142000071810000
go
SET IDENTITY_INSERT dbo.sys_data OFF
go
ALTER TABLE dbo.sys_data ADD CONSTRAINT PK_sys_data
PRIMARY KEY NONCLUSTERED (sys_id,type_id) 
go

--
-- Table Extended Alter
-- dbo.sys_types
--
EXEC sp_rename 'dbo.sys_types','sys_types_12142000071811000',OBJECT
go
CREATE TABLE dbo.sys_types 
(
    type_id tinyint     IDENTITY,
    name    varchar(50) NULL
)
go
SET IDENTITY_INSERT dbo.sys_types ON
go
INSERT INTO dbo.sys_types(
                          type_id,
                          name
                         )
                   SELECT 
                          type_id,
                          CONVERT(varchar(50),name)
                     FROM dbo.sys_types_12142000071811000
go
SET IDENTITY_INSERT dbo.sys_types OFF
go
ALTER TABLE dbo.sys_types ADD CONSTRAINT PK_sys_types
PRIMARY KEY NONCLUSTERED (type_id) 
go

--
-- Table Alter
-- dbo.templates_cref
--
ALTER TABLE dbo.templates_cref DROP CONSTRAINT FK_templates_cref_templates
go
ALTER TABLE dbo.templates_cref ADD CONSTRAINT FK_templates_cref_templategroups
FOREIGN KEY (group_id)
REFERENCES dbo.templategroups (group_id)
go
ALTER TABLE dbo.templates_cref DROP CONSTRAINT FK_templates_cref_templategroups
go
ALTER TABLE dbo.templates_cref ADD CONSTRAINT FK_templates_cref_templates
FOREIGN KEY (template_id)
REFERENCES dbo.templates (template_id)
go

--
-- Table Alter
-- dbo.text_docs
--
ALTER TABLE dbo.text_docs DROP CONSTRAINT DF_text_docs_group_id
go
ALTER TABLE dbo.text_docs ADD DEFAULT 1 FOR group_id
go

--
-- Table Alter
-- dbo.texts
--
ALTER TABLE dbo.texts ADD CONSTRAINT FK_texts_meta
FOREIGN KEY (meta_id)
REFERENCES dbo.meta (meta_id)
go

--
-- Table Extended Alter
-- dbo.url_docs
--
EXEC sp_rename 'url_docs.PK_url_docs','PK_url_doc_12142000071819001'
go
EXEC sp_rename 'dbo.url_docs','url_docs_12142000071819000',OBJECT
go
CREATE TABLE dbo.url_docs 
(
    meta_id     int          NOT NULL,
    frame_name  varchar(80)  NOT NULL,
    target      varchar(15)  NOT NULL,
    url_ref     varchar(255) NOT NULL,
    url_txt     varchar(255) NOT NULL,
    lang_prefix varchar(3)   NOT NULL
)
go
INSERT INTO dbo.url_docs(
                         meta_id,
                         frame_name,
                         target,
                         url_ref,
                         url_txt,
                         lang_prefix
                        )
                  SELECT 
                         meta_id,
                         frame_name,
                         target,
                         url_ref,
                         url_txt,
                         lang_prefix
                    FROM dbo.url_docs_12142000071819000
go
ALTER TABLE dbo.url_docs ADD CONSTRAINT PK_url_docs
PRIMARY KEY NONCLUSTERED (meta_id,lang_prefix) 
go

--
-- Table Alter
-- dbo.user_rights
--
ALTER TABLE dbo.user_rights ADD CONSTRAINT PK_user_rights
PRIMARY KEY NONCLUSTERED (user_id,meta_id,permission_id) 
go
ALTER TABLE dbo.user_rights ADD CONSTRAINT FK_user_rights_meta
FOREIGN KEY (meta_id)
REFERENCES dbo.meta (meta_id)
go
ALTER TABLE dbo.user_rights ADD CONSTRAINT FK_user_rights_users
FOREIGN KEY (user_id)
REFERENCES dbo.users (user_id)
go

--
-- Table Alter
-- dbo.user_roles_crossref
--
ALTER TABLE dbo.user_roles_crossref ADD CONSTRAINT FK_user_roles_crossref_roles
FOREIGN KEY (role_id)
REFERENCES dbo.roles (role_id)
go
ALTER TABLE dbo.user_roles_crossref ADD CONSTRAINT FK_user_roles_crossref_users
FOREIGN KEY (user_id)
REFERENCES dbo.users (user_id)
go

--
-- Table Extended Alter
-- dbo.users
--
EXEC sp_rename 'users.PK_users','PK_users_12142000071825001'
go
EXEC sp_rename 'dbo.users','users_12142000071825000',OBJECT
go
CREATE TABLE dbo.users 
(
    user_id        int           NOT NULL,
    login_name     char(15)      NOT NULL,
    login_password char(15)      NOT NULL,
    first_name     char(25)      NOT NULL,
    last_name      char(30)      NOT NULL,
    title          char(30)      DEFAULT '' NOT NULL,
    company        char(30)      DEFAULT '' NOT NULL,
    address        char(40)      NOT NULL,
    city           char(30)      NOT NULL,
    zip            char(15)      NOT NULL,
    country        char(30)      NOT NULL,
    county_council char(30)      NOT NULL,
    email          char(50)      NOT NULL,
    admin_mode     int           NOT NULL,
    last_page      int           NOT NULL,
    archive_mode   int           NOT NULL,
    lang_id        int           NOT NULL,
    user_type      int           DEFAULT 1 NOT NULL,
    active         int           DEFAULT 1 NOT NULL,
    create_date    smalldatetime NOT NULL
)
go
INSERT INTO dbo.users(
                      user_id,
                      login_name,
                      login_password,
                      first_name,
                      last_name,
                      title,
                      company,
                      address,
                      city,
                      zip,
                      country,
                      county_council,
                      email,
                      admin_mode,
                      last_page,
                      archive_mode,
                      lang_id,
                      user_type,
                      active,
                      create_date
                     )
               SELECT 
                      user_id,
                      login_name,
                      login_password,
                      first_name,
                      last_name,
                      title,
                      company,
                      address,
                      city,
                      zip,
                      country,
                      county_council,
                      email,
                      admin_mode,
                      last_page,
                      archive_mode,
                      lang_id,
                      user_type,
                      active,
                      create_date
                 FROM dbo.users_12142000071825000
go
ALTER TABLE dbo.users ADD CONSTRAINT PK_users
PRIMARY KEY NONCLUSTERED (user_id) 
go

--
-- Procedure Recreate
-- imse.ListDocsByDate
--
EXEC sp_rename 'imse.ListDocsByDate','ListDocsBy_12142000071744001',OBJECT
go
/****** Object:  Stored Procedure dbo.ListDocsByDate    Script Date: 2000-10-27 14:21:06 ******/
CREATE PROCEDURE imse.ListDocsByDate @listMod int,  @doc_type int, @startDate varchar(10), @endDate varchar(20) AS
/*
 lists doctyps where activate = 1
 @listMod 0 = all date, 1 = only creatdat, 2 = only modifieddata
 @startDoc yyyy-mm-dd or 0 then not set
 @endDate yyyy-mm-dd or 0 then not set
*/


-- Listdate fix 
if ( @endDate <> '0') BEGIN
	SET @endDate = @endDate + ' 23:59:59'
	PRINT @endDate
END 

/* list all (not in use ) */
if ( @listMod = 0) begin
	if ( @startDate = '0' ) begin
		if ( @endDate = '0' ) begin
			select m.meta_id, dt.type, m.meta_headline, m.date_modified
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and activate = 1

			order by m.date_modified
		end
		else begin
			select m.meta_id, dt.type, m.meta_headline, m.date_modified
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and m.date_modified <= @endDate and activate = 1

			order by m.date_modified
		end
	end
	else if ( @startDate != '0' ) begin
		if ( @endDate = '0' ) begin
			select m.meta_id, dt.type, m.meta_headline, m.date_modified
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and m.date_modified >= @startDate and activate = 1

			order by m.date_modified
		end
		else
		begin
			select m.meta_id, dt.type, m.meta_headline, m.date_modified
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and m.date_modified <= @endDate and m.date_modified >= @startDate and activate = 1

			order by m.date_modified
		end
	end
end

/* list creatdate */
else if ( @listMod = 1) begin
	if ( @startDate = '0' ) begin
		if ( @endDate = '0' ) begin
			select m.meta_id, dt.type, m.meta_headline, m.date_created
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and activate = 1

			order by m.date_created
		end
		else begin
			select m.meta_id, dt.type, m.meta_headline, m.date_created
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and m.date_created <= @endDate and activate = 1

			order by m.date_created
		end
	end
	else if ( @startDate != '0' ) begin
		if ( @endDate = '0' ) begin
			select m.meta_id, dt.type, m.meta_headline, m.date_created
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and m.date_created >= @startDate and activate = 1

			order by m.date_created
		end
		else
		begin
			select m.meta_id, dt.type, m.meta_headline, m.date_created
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and m.date_created <= @endDate and m.date_created >= @startDate and activate = 1

			order by m.date_created
		end
	end
end
/* list only modified*/
else begin
	if ( @startDate = '0' ) begin
		if ( @endDate = '0' ) begin
			select m.meta_id, dt.type, m.meta_headline, m.date_modified
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and activate = 1 and m.date_modified != m.date_created

			order by m.date_modified
		end
		else begin
			select m.meta_id, dt.type, m.meta_headline, m.date_modified
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and m.date_modified <= @endDate and activate = 1 and m.date_modified != m.date_created

			order by m.date_modified
		end
	end
	else if ( @startDate != '0' ) begin
		if ( @endDate = '0' ) begin
			select m.meta_id, dt.type, m.meta_headline, m.date_modified
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and m.date_modified >= @startDate and activate = 1 and m.date_modified != m.date_created

			order by m.date_modified
		end
		else
		begin
			select m.meta_id, dt.type, m.meta_headline, m.date_modified
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and m.date_modified <= @endDate and m.date_modified >= @startDate and activate = 1 and m.date_modified != m.date_created

			order by m.date_modified
		end
	end
end
go
IF OBJECT_ID('imse.ListDocsByDate') IS NOT NULL
     DROP PROCEDURE imse.ListDocsBy_12142000071744001
ELSE 
     EXEC sp_rename 'imse.ListDocsBy_12142000071744001','ListDocsByDate',OBJECT
go

--
-- Procedure Recreate
-- imse.ListDocsGetInternalDocTypes
--
EXEC sp_rename 'imse.ListDocsGetInternalDocTypes','ListDocsGe_12142000071744002',OBJECT
go
/****** Object:  Stored Procedure dbo.ListDocsGetInternalDocTypes    Script Date: 2000-10-27 14:21:06 ******/
CREATE PROCEDURE ListDocsGetInternalDocTypes AS

/* selct all internal doc types */
select doc_type, type 

from doc_types

where doc_type <= 100
go
IF OBJECT_ID('imse.ListDocsGetInternalDocTypes') IS NOT NULL
     DROP PROCEDURE imse.ListDocsGe_12142000071744002
ELSE 
     EXEC sp_rename 'imse.ListDocsGe_12142000071744002','ListDocsGetInternalDocTypes',OBJECT
go

--
-- Procedure Recreate
-- imse.ListDocsGetInternalDocTypesValue
--
EXEC sp_rename 'imse.ListDocsGetInternalDocTypesValue','ListDocsGe_12142000071744003',OBJECT
go
/****** Object:  Stored Procedure dbo.ListDocsGetInternalDocTypesValue    Script Date: 2000-10-27 14:21:06 ******/
CREATE PROCEDURE ListDocsGetInternalDocTypesValue AS

/* selct all internal doc types */
select doc_type

from doc_types

where doc_type <= 100
go
IF OBJECT_ID('imse.ListDocsGetInternalDocTypesValue') IS NOT NULL
     DROP PROCEDURE imse.ListDocsGe_12142000071744003
ELSE 
     EXEC sp_rename 'imse.ListDocsGe_12142000071744003','ListDocsGetInternalDocTypesValue',OBJECT
go

--
-- Procedure Recreate
-- imse.IPAccessAdd
--
EXEC sp_rename 'imse.IPAccessAdd','IPAccessAd_12142000071749001',OBJECT
go
CREATE PROCEDURE IPAccessAdd
/*
This function adds a new ip-access to the db. Used by AdminManager
*/
 @user_id int,
 @ip_start varchar(15) , 
 @ip_end varchar(15)
AS
INSERT INTO IP_ACCESSES ( user_id , ip_start , ip_end )
VALUES ( @user_id , @ip_start , @ip_end )
go
IF OBJECT_ID('imse.IPAccessAdd') IS NOT NULL
     DROP PROCEDURE imse.IPAccessAd_12142000071749001
ELSE 
     EXEC sp_rename 'imse.IPAccessAd_12142000071749001','IPAccessAdd',OBJECT
go

--
-- Procedure Recreate
-- imse.IPAccessDelete
--
EXEC sp_rename 'imse.IPAccessDelete','IPAccessDe_12142000071749002',OBJECT
go
CREATE PROCEDURE IPAccessDelete
/*
 Deletes an Ip-access for a user. Used by the AdminIpAccess servlet
*/
 @ipAccessId int
AS
DELETE FROM IP_ACCESSES 
WHERE ip_access_id = @ipAccessId
go
IF OBJECT_ID('imse.IPAccessDelete') IS NOT NULL
     DROP PROCEDURE imse.IPAccessDe_12142000071749002
ELSE 
     EXEC sp_rename 'imse.IPAccessDe_12142000071749002','IPAccessDelete',OBJECT
go

--
-- Procedure Recreate
-- imse.IPAccessesGetAll
--
EXEC sp_rename 'imse.IPAccessesGetAll','IPAccesses_12142000071749003',OBJECT
go
CREATE PROCEDURE IPAccessesGetAll AS
/*
Lets get all IPaccesses from db. Used  by the AdminIpAccesses
*/
SELECT ip.ip_access_id, ip.user_id, usr.login_name, ip.ip_start, ip.ip_end    
FROM IP_ACCESSES ip, USERS usr
WHERE ip.user_id = usr.user_id
go
IF OBJECT_ID('imse.IPAccessesGetAll') IS NOT NULL
     DROP PROCEDURE imse.IPAccesses_12142000071749003
ELSE 
     EXEC sp_rename 'imse.IPAccesses_12142000071749003','IPAccessesGetAll',OBJECT
go

--
-- Procedure Recreate
-- imse.IPAccessUpdate
--
EXEC sp_rename 'imse.IPAccessUpdate','IPAccessUp_12142000071749004',OBJECT
go
CREATE PROCEDURE IPAccessUpdate
/*
Updates the IPaccess table
*/
 @IpAccessId int ,
 @newUserId int,
 @newIpStart varchar(15) ,
 @newIpEnd varchar(15) 
AS
UPDATE IP_ACCESSES
SET user_id = @newUserId ,
 ip_start = @newIpStart,
 ip_end = @newIpEnd
WHERE ip_access_id = @IpAccessId
go
IF OBJECT_ID('imse.IPAccessUpdate') IS NOT NULL
     DROP PROCEDURE imse.IPAccessUp_12142000071749004
ELSE 
     EXEC sp_rename 'imse.IPAccessUp_12142000071749004','IPAccessUpdate',OBJECT
go

--
-- Procedure Recreate
-- imse.GetLanguageList
--
EXEC sp_rename 'imse.GetLanguageList','GetLanguag_12142000071751001',OBJECT
go
CREATE PROCEDURE GetLanguageList AS

/*
 Returns all 
*/

SELECT lp.lang_id , lang.language
FROM lang_prefixes lp, languages lang
WHERE lp.lang_prefix = lang.lang_prefix
go
IF OBJECT_ID('imse.GetLanguageList') IS NOT NULL
     DROP PROCEDURE imse.GetLanguag_12142000071751001
ELSE 
     EXEC sp_rename 'imse.GetLanguag_12142000071751001','GetLanguageList',OBJECT
go

--
-- Procedure Recreate
-- imse.getLanguages
--
EXEC sp_rename 'imse.getLanguages','getLanguag_12142000071751002',OBJECT
go
CREATE PROCEDURE getLanguages AS
select lang_prefix,language from languages order by language
go
IF OBJECT_ID('imse.getLanguages') IS NOT NULL
     DROP PROCEDURE imse.getLanguag_12142000071751002
ELSE 
     EXEC sp_rename 'imse.getLanguag_12142000071751002','getLanguages',OBJECT
go

--
-- Procedure Recreate
-- dbo.getDocsParentCount
--
EXEC sp_rename 'dbo.getDocsParentCount','getDocsPar_12142000071754002',OBJECT
go
CREATE PROCEDURE getDocsParentCount @user_id int AS
-- Lists the documents that have parents, and how many, provided the user may write to them.
if exists ( -- Is user superadmin?
 select
  *
 from
  user_roles_crossref
 where
  user_id = @user_id
  and role_id = 0
) begin -- User is superadmin. Select all rows.
 select
  to_meta_id,
  count(meta_id) as parents
 from
  childs 
 group by
  to_meta_id
 union
 select
  distinct to_meta_id,
  count(meta_id) as parents
 from
  browser_docs
 group by
  to_meta_id
 order by
  to_meta_id
end else begin -- User is not superadmin.
 select
  to_meta_id,
  count(childs.meta_id) as parents
 from
  childs
  join meta
 on
  childs.to_meta_id = meta.meta_id
 where
  meta.meta_id in ( -- Is user member of role allowed to write to the document?
   select
    meta.meta_id 
   from
    meta
    join roles_rights
   on
    meta.meta_id = roles_rights.meta_id
    and roles_rights.permission_id = 3
    join user_roles_crossref
   on
    roles_rights.role_id = user_roles_crossref.role_id
    and user_roles_crossref.user_id = @user_id
  ) or meta.meta_id in ( -- Is user the owner of the document?
   select
    meta_id 
   from
    user_rights
   where
    user_id = @user_id
    and permission_id = 99
  )
 group by
  to_meta_id
 union
  select
   distinct to_meta_id,
   count(browser_docs.meta_id) as parents
  from
   browser_docs
   join meta
  on
   to_meta_id = meta.meta_id
  where
   meta.meta_id in ( -- Is user member of role allowed to write to the document?
    select
     meta.meta_id 
    from
     meta
     join roles_rights
    on
     meta.meta_id = roles_rights.meta_id
     and roles_rights.permission_id = 3
     join user_roles_crossref
    on
     roles_rights.role_id = user_roles_crossref.role_id
     and user_roles_crossref.user_id = @user_id
   ) or meta.meta_id in ( -- Is user the owner of the document?
    select
     meta_id 
    from
     user_rights
    where
     user_id = @user_id
     and permission_id = 99
   )
  group by
   to_meta_id
 order by 
  to_meta_id
end
go
IF OBJECT_ID('dbo.getDocsParentCount') IS NOT NULL
     DROP PROCEDURE dbo.getDocsPar_12142000071754002
ELSE 
     EXEC sp_rename 'dbo.getDocsPar_12142000071754002','getDocsParentCount',OBJECT
go

--
-- Procedure Recreate
-- dbo.testProc
--
EXEC sp_rename 'dbo.testProc','testProc_12142000071754003',OBJECT
go
CREATE PROCEDURE testProc AS
select * from meta
go
IF OBJECT_ID('dbo.testProc') IS NOT NULL
     DROP PROCEDURE dbo.testProc_12142000071754003
ELSE 
     EXEC sp_rename 'dbo.testProc_12142000071754003','testProc',OBJECT
go

--
-- Procedure Recreate
-- imse.classification_convert
--
EXEC sp_rename 'imse.classification_convert','classifica_12142000071754004',OBJECT
go
CREATE PROCEDURE imse.classification_convert AS
-- Hämta alla klassificeringskoder och för över dem till nya systemet. Observera att denna inte fixar
-- de fält som har , tecken som delimiter
DECLARE @meta_id int
DECLARE @class varchar(200)
DECLARE tmpCursor CURSOR FOR
 SELECT meta_id, classification
 FROM meta
 WHERE classification IS NOT NULL
 and classification <> ''
 and classification NOT LIKE 'META NAME%'
 and classification NOT LIKE 'Test'
 -- AND meta_id = 2201
OPEN tmpCursor
FETCH NEXT FROM tmpCursor INTO @meta_id, @class
WHILE @@fetch_status = 0 BEGIN
 PRINT 'Class: ' + @class 
EXEC classification_fix @meta_id, @class 
 FETCH NEXT FROM tmpCursor INTO @meta_id, @class
END
CLOSE tmpCursor
DEALLOCATE tmpCursor
go
IF OBJECT_ID('imse.classification_convert') IS NOT NULL
     DROP PROCEDURE imse.classifica_12142000071754004
ELSE 
     EXEC sp_rename 'imse.classifica_12142000071754004','classification_convert',OBJECT
go

--
-- Procedure Recreate
-- imse.Classification_Get_All
--
EXEC sp_rename 'imse.Classification_Get_All','Classifica_12142000071754005',OBJECT
go
CREATE PROCEDURE Classification_Get_All AS
/*
Get the meta_id and classifcation so we can start convert them
*/
SELECT meta_id, classification
 FROM meta
 WHERE classification IS NOT NULL
 and classification <> ''
 and classification NOT LIKE 'META NAME%'
 and classification NOT LIKE 'Test'
go
IF OBJECT_ID('imse.Classification_Get_All') IS NOT NULL
     DROP PROCEDURE imse.Classifica_12142000071754005
ELSE 
     EXEC sp_rename 'imse.Classifica_12142000071754005','Classification_Get_All',OBJECT
go

--
-- Procedure Recreate
-- imse.FindMetaId
--
EXEC sp_rename 'imse.FindMetaId','FindMetaId_12142000071754006',OBJECT
go
CREATE PROCEDURE [FindMetaId]
 @meta_id int
 AS
SELECT meta_id 
FROM meta
WHERE meta_id = @meta_id
go
IF OBJECT_ID('imse.FindMetaId') IS NOT NULL
     DROP PROCEDURE imse.FindMetaId_12142000071754006
ELSE 
     EXEC sp_rename 'imse.FindMetaId_12142000071754006','FindMetaId',OBJECT
go

--
-- Procedure Recreate
-- imse.GetAdminChilds
--
EXEC sp_rename 'imse.GetAdminChilds','GetAdminCh_12142000071754007',OBJECT
go
CREATE PROCEDURE GetAdminChilds
@meta_id int,
@user_id int
AS
select   to_meta_id
from   childs c
join   meta m    
     on   m.meta_id = c.to_meta_id    -- meta.meta_id corresponds to childs.to_meta_id
      and m.activate != 0      -- Only include the documents that are active in the meta table.
      and c.meta_id = @meta_id     -- Only include documents that are children to this particular meta_id
left join  roles_rights rr        -- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
     on  c.to_meta_id = rr.meta_id     -- Only include rows with the documents we are interested in
       and rr.permission_id  = 3     -- Only include permissions that gives right to change the document
join  user_roles_crossref urc       -- This table tells us which users have which roles
     on  urc.user_id = @user_id     -- Only include the rows with the user we are interested in.
      and ( rr.role_id = urc.role_id    -- Include rows where the users roles match the roles that have permissions on the documents
       or urc.role_id = 0     -- and also include the rows that tells us this user is a superadmin (That is, return a row with urc.role_id = 0 for each document.)
       )
left join  user_rights ur        -- We may have permission, even though we don't own any documents... That is, if we're roleadmins or superadmin. When we lose ownership-based rights, this may be skipped.
     on  ur.meta_id = c.to_meta_id    -- Only include rows with the documents we are interested in
      and ur.user_id =  urc.user_id     -- Only include rows with the user  we are interested in
      and ur.permission_id = 99     -- Only include rows that mean "Ownership"
go
IF OBJECT_ID('imse.GetAdminChilds') IS NOT NULL
     DROP PROCEDURE imse.GetAdminCh_12142000071754007
ELSE 
     EXEC sp_rename 'imse.GetAdminCh_12142000071754007','GetAdminChilds',OBJECT
go

--
-- Procedure Recreate
-- imse.getBrowserDocChilds
--
EXEC sp_rename 'imse.getBrowserDocChilds','getBrowser_12142000071754008',OBJECT
go
CREATE PROCEDURE getBrowserDocChilds @meta_id int, @user_id int AS
-- Lists the childs for a specific browser_doc
SELECT DISTINCT to_meta_id,
   meta_headline
FROM   browser_docs bd
JOIN   meta m   ON bd.to_meta_id = m.meta_id
      AND bd.meta_id = @meta_id
LEFT JOIN  user_rights ur  ON ur.meta_id = m.meta_id
      AND ur.user_id = @user_id
      AND ur.permission_id = 99
LEFT JOIN  roles_rights rr  ON rr.meta_id = m.meta_id
      AND rr.permission_id = 3
JOIN   user_roles_crossref urc ON urc.user_id = @user_id
      AND (
        urc.role_id = 0
       OR (
         urc.role_id = rr.role_id
        AND urc.user_id = ur.user_id
        )
       OR m.shared = 1
       )
WHERE m.activate = 1
ORDER BY to_meta_id
go
IF OBJECT_ID('imse.getBrowserDocChilds') IS NOT NULL
     DROP PROCEDURE imse.getBrowser_12142000071754008
ELSE 
     EXEC sp_rename 'imse.getBrowser_12142000071754008','getBrowserDocChilds',OBJECT
go

--
-- Procedure Recreate
-- imse.GetChilds
--
EXEC sp_rename 'imse.GetChilds','GetChilds_12142000071754009',OBJECT
go

--
-- Procedure Create
-- dbo.GetChilds
--
CREATE PROCEDURE GetChilds
	@meta_id int,
	@user_id int
AS
/*
Nice little query that lists the children of a document that a particular user may see, and includes a field that tells you wether he may do something to it or not.
*/
declare @sort_by int

select @sort_by = sort_order from text_docs where meta_id = @meta_id

-- Manual sort order
if @sort_by = 2
begin
select  to_meta_id, c.menu_sort,manual_sort_order, doc_type,
		archive,target, left(convert (varchar,date_created,120),10),
		meta_headline,meta_text,meta_image,frame_name,
		activated_date+activated_time,archived_date+archived_time,
		min(urc.role_id * ISNULL(dps.permission_id&~1,1) * ISNULL(rr.set_id,1)),
		fd.filename
from   childs c
join   meta m    
					on   	m.meta_id = c.to_meta_id					-- meta.meta_id corresponds to childs.to_meta_id
					and 	m.activate > 0							-- Only include the documents that are active in the meta table.
					and 	c.meta_id = @meta_id						-- Only include documents that are children to this particular meta_id
left join roles_rights rr												-- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
					on 	c.to_meta_id = rr.meta_id						-- Only include rows with the documents we are interested in
left join doc_permission_sets dps											-- Include the permission_sets
					on 	c.to_meta_id = dps.meta_id					-- for each document
					and	dps.set_id = rr.set_id						-- and only the sets for the roles we are interested in
					and	dps.permission_id > 0						-- and only the sets that have any permission
join user_roles_crossref urc											-- This table tells us which users have which roles
					on	urc.user_id = @user_id						-- Only include the rows with the user we are interested in...
					and	( 
							rr.role_id = urc.role_id					-- Include rows where the users roles match the roles that have permissions on the documents
						or 	urc.role_id = 0						-- and also include the rows that tells us this user is a superadmin
						or 	(
								m.show_meta != 0				-- and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
							and	ISNULL(dps.permission_id&~1,1) != 1
						)
					)
left join fileupload_docs fd
					on  fd.meta_id = c.to_meta_id
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
		archive,target, left(convert (varchar,date_created,120),10),
		meta_headline,meta_text,meta_image,frame_name,
		activated_date+activated_time,archived_date+archived_time,
		fd.filename
order by  menu_sort,c.manual_sort_order desc
end
else if @sort_by = 3
begin
select  to_meta_id, c.menu_sort,manual_sort_order, doc_type,
		archive,target, left(convert (varchar,date_created,120),10),
		meta_headline,meta_text,meta_image,frame_name,
		activated_date+activated_time,archived_date+archived_time,
		min(urc.role_id * ISNULL(dps.permission_id&~1,1) * ISNULL(rr.set_id,1)),
		fd.filename
from   childs c
join   meta m    
					on   	m.meta_id = c.to_meta_id					-- meta.meta_id corresponds to childs.to_meta_id
					and 	m.activate > 0							-- Only include the documents that are active in the meta table.
					and 	c.meta_id = @meta_id						-- Only include documents that are children to this particular meta_id
left join roles_rights rr												-- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
					on 	c.to_meta_id = rr.meta_id						-- Only include rows with the documents we are interested in
left join doc_permission_sets dps											-- Include the permission_sets
					on 	c.to_meta_id = dps.meta_id					-- for each document
					and	dps.set_id = rr.set_id						-- and only the sets for the roles we are interested in
					and	dps.permission_id > 0						-- and only the sets that have any permission
join user_roles_crossref urc											-- This table tells us which users have which roles
					on	urc.user_id = @user_id						-- Only include the rows with the user we are interested in...
					and	( 
							rr.role_id = urc.role_id					-- Include rows where the users roles match the roles that have permissions on the documents
						or 	urc.role_id = 0						-- and also include the rows that tells us this user is a superadmin
						or 	(
								m.show_meta != 0				-- and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
							and	ISNULL(dps.permission_id&~1,1) != 1
						)
					)
left join fileupload_docs fd
					on  fd.meta_id = c.to_meta_id
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
		archive,target, left(convert (varchar,date_created,120),10),
		meta_headline,meta_text,meta_image,frame_name,
		activated_date+activated_time,archived_date+archived_time,
		fd.filename
order by		menu_sort,left(convert (varchar,date_created,120),10) desc
end
else if @sort_by = 1
begin
select  to_meta_id, c.menu_sort,manual_sort_order, doc_type,
		archive,target, left(convert (varchar,date_created,120),10),
		meta_headline,meta_text,meta_image,frame_name,
		activated_date+activated_time,archived_date+archived_time,
		min(urc.role_id * ISNULL(dps.permission_id&~1,1) * ISNULL(rr.set_id,1)),
		fd.filename
from   childs c
join   meta m    
					on   	m.meta_id = c.to_meta_id					-- meta.meta_id corresponds to childs.to_meta_id
					and 	m.activate > 0							-- Only include the documents that are active in the meta table.
					and 	c.meta_id = @meta_id						-- Only include documents that are children to this particular meta_id
left join roles_rights rr												-- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
					on 	c.to_meta_id = rr.meta_id						-- Only include rows with the documents we are interested in
left join doc_permission_sets dps											-- Include the permission_sets
					on 	c.to_meta_id = dps.meta_id					-- for each document
					and	dps.set_id = rr.set_id						-- and only the sets for the roles we are interested in
					and	dps.permission_id > 0						-- and only the sets that have any permission
join user_roles_crossref urc											-- This table tells us which users have which roles
					on	urc.user_id = @user_id						-- Only include the rows with the user we are interested in...
					and	( 
							rr.role_id = urc.role_id					-- Include rows where the users roles match the roles that have permissions on the documents
						or 	urc.role_id = 0						-- and also include the rows that tells us this user is a superadmin
						or 	(
								m.show_meta != 0				-- and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
							and	ISNULL(dps.permission_id&~1,1) != 1
						)
					)
left join fileupload_docs fd
					on  fd.meta_id = c.to_meta_id
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
		archive,target, left(convert (varchar,date_created,120),10),
		meta_headline,meta_text,meta_image,frame_name,
		activated_date+activated_time,archived_date+archived_time,
		fd.filename
order by		menu_sort,meta_headline
end

GO
--
-- Procedure Recreate
-- imse.getDocs
--
EXEC sp_rename 'imse.getDocs','getDocs_12142000071754010',OBJECT
go
CREATE PROCEDURE getDocs @user_id int, @start int, @end int AS
-- Lists documents user is allowed to see.
SELECT DISTINCT m.meta_id,
   COUNT(DISTINCT c.meta_id) parentcount,
   meta_headline,
   doc_type
FROM   meta m
LEFT JOIN  childs c   ON c.to_meta_id = m.meta_id
LEFT JOIN  user_rights ur  ON ur.meta_id = m.meta_id
      AND ur.permission_id = 99
LEFT JOIN  roles_rights rr  ON rr.meta_id = m.meta_id
      AND rr.permission_id = 3
JOIN   user_roles_crossref urc ON urc.user_id = @user_id
      AND (
        urc.role_id = 0
       OR (
         urc.role_id = rr.role_id
        AND urc.user_id = ur.user_id
        )
       OR m.shared = 1
       )
WHERE  m.activate = 1
  AND m.meta_id > (@start-1) 
  AND m.meta_id < (@end+1)
GROUP BY  m.meta_id,m.meta_headline,m.doc_type,c.to_meta_id
ORDER BY  m.meta_id
go
IF OBJECT_ID('imse.getDocs') IS NOT NULL
     DROP PROCEDURE imse.getDocs_12142000071754010
ELSE 
     EXEC sp_rename 'imse.getDocs_12142000071754010','getDocs',OBJECT
go

--
-- Procedure Recreate
-- imse.GetDocType
--
EXEC sp_rename 'imse.GetDocType','GetDocType_12142000071754011',OBJECT
go
CREATE PROCEDURE GetDocType
 @meta_id int
AS
/*
 Used by external systems to get the docType
*/
SELECT doc_type
FROM meta
WHERE meta_id = @meta_id
go
IF OBJECT_ID('imse.GetDocType') IS NOT NULL
     DROP PROCEDURE imse.GetDocType_12142000071754011
ELSE 
     EXEC sp_rename 'imse.GetDocType_12142000071754011','GetDocType',OBJECT
go

--
-- Procedure Recreate
-- imse.GetLangPrefix
--
EXEC sp_rename 'imse.GetLangPrefix','GetLangPre_12142000071754012',OBJECT
go
CREATE PROCEDURE GetLangPrefix
 @meta_id int
AS
/*
 Used by external systems to get the langprefix
*/
SELECT lang_prefix 
FROM meta
WHERE meta_id = @meta_id
go
IF OBJECT_ID('imse.GetLangPrefix') IS NOT NULL
     DROP PROCEDURE imse.GetLangPre_12142000071754012
ELSE 
     EXEC sp_rename 'imse.GetLangPre_12142000071754012','GetLangPrefix',OBJECT
go

--
-- Procedure Recreate
-- imse.getMenuDocChilds
--
EXEC sp_rename 'imse.getMenuDocChilds','getMenuDoc_12142000071754013',OBJECT
go
CREATE PROCEDURE getMenuDocChilds @meta_id int, @user_id int AS
-- Lists the childs of menudoc @meta_id
SELECT DISTINCT to_meta_id,
   meta_headline
FROM   childs c
JOIN   meta m   ON c.to_meta_id = m.meta_id
      AND c.meta_id = @meta_id
LEFT JOIN  user_rights ur  ON ur.meta_id = m.meta_id
      AND ur.user_id = @user_id
      AND ur.permission_id = 99
LEFT JOIN  roles_rights rr  ON rr.meta_id = m.meta_id
      AND rr.permission_id = 3
JOIN   user_roles_crossref urc ON urc.user_id = @user_id
      AND ( urc.role_id = 0
       OR (
         urc.role_id = rr.role_id
        AND urc.user_id = ur.user_id
        )
       OR m.shared = 1
       )
WHERE m.activate = 1
ORDER BY to_meta_id
go
IF OBJECT_ID('imse.getMenuDocChilds') IS NOT NULL
     DROP PROCEDURE imse.getMenuDoc_12142000071754013
ELSE 
     EXEC sp_rename 'imse.getMenuDoc_12142000071754013','getMenuDocChilds',OBJECT
go

--
-- Procedure Recreate
-- imse.GetMetaPathInfo
--
EXEC sp_rename 'imse.GetMetaPathInfo','GetMetaPat_12142000071754014',OBJECT
go
CREATE PROCEDURE GetMetaPathInfo
 @meta_id int
AS
/*
 Used by external systems to get the meta_id dependent part of the path to
for example the image folder or to the html folder
Ex of what this function will return: 
*/
DECLARE @docType char(20)
DECLARE @langPrefix char(20)
SELECT RTRIM(lang_prefix) + '/' +  RTRIM(doc_type) + '/' 
FROM META 
WHERE meta_id = @meta_id
go
IF OBJECT_ID('imse.GetMetaPathInfo') IS NOT NULL
     DROP PROCEDURE imse.GetMetaPat_12142000071754014
ELSE 
     EXEC sp_rename 'imse.GetMetaPat_12142000071754014','GetMetaPathInfo',OBJECT
go

--
-- Procedure Recreate
-- imse.IMC_CreateNewMeta
--
EXEC sp_rename 'imse.IMC_CreateNewMeta','IMC_Create_12142000071754015',OBJECT
go
CREATE PROCEDURE IMC_CreateNewMeta
/* create new metadata */
@new_meta_id int,
@description varchar(80),
@doc_type int,
@meta_head_line varchar(255),
@meta_text varchar(1000),
@meta_image varchar(255),
@category_id int,
@processing_id int,
@shared int,
@expanded int,
@show_meta int,
@help_text_id int, /* help_text_id = 1 */
@archived int,
@status_id int,   /* status_id  = 1 */
@lang_prefix varchar(3),
@classification varchar(20),
@date_created datetime,
@date_modified datetime,
@sort_position int,
@menu_position int,
@disable_search int,
@activated_date varchar(10),
@activated_time varchar(6),
@archived_date varchar(10),
@archived_time varchar(6),
@target varchar(10),
@frame_name varchar(20),
@activate int
 AS
insert into meta
values(@new_meta_id ,@description,@doc_type,@meta_head_line,@meta_text,@meta_image, @category_id,
@processing_id,@shared,@expanded,@help_text_id,@show_meta,@archived,@status_id,@lang_prefix,@classification,@date_created,
@date_modified, @sort_position,@menu_position,@disable_search,@activated_date,@activated_time,@archived_date,
@archived_time,@target,@frame_name,@activate)
go
IF OBJECT_ID('imse.IMC_CreateNewMeta') IS NOT NULL
     DROP PROCEDURE imse.IMC_Create_12142000071754015
ELSE 
     EXEC sp_rename 'imse.IMC_Create_12142000071754015','IMC_CreateNewMeta',OBJECT
go

--
-- Procedure Recreate
-- imse.IMC_GetMaxMetaID
--
EXEC sp_rename 'imse.IMC_GetMaxMetaID','IMC_GetMax_12142000071754016',OBJECT
go
CREATE PROCEDURE IMC_GetMaxMetaID AS
/* get max meta id */
select max(meta_id) from meta
go
IF OBJECT_ID('imse.IMC_GetMaxMetaID') IS NOT NULL
     DROP PROCEDURE imse.IMC_GetMax_12142000071754016
ELSE 
     EXEC sp_rename 'imse.IMC_GetMax_12142000071754016','IMC_GetMaxMetaID',OBJECT
go

--
-- Procedure Recreate
-- imse.ListConferences
--
EXEC sp_rename 'imse.ListConferences','ListConfer_12142000071754017',OBJECT
go
CREATE PROCEDURE ListConferences AS

select meta_id, meta_headline 
from meta 
where doc_type = 102
go
IF OBJECT_ID('imse.ListConferences') IS NOT NULL
     DROP PROCEDURE imse.ListConfer_12142000071754017
ELSE 
     EXEC sp_rename 'imse.ListConfer_12142000071754017','ListConferences',OBJECT
go

--
-- Procedure Recreate
-- imse.meta_select
--
EXEC sp_rename 'imse.meta_select','meta_selec_12142000071754019',OBJECT
go
CREATE PROCEDURE meta_select AS
select menu_sort,manual_sort_order,date_created,meta_headline,target from meta,childs,roles_rights,user_roles_crossref 
where meta.meta_id = childs.to_meta_id 
and childs.meta_id = 4260 
and meta.archive=0 
and meta.activate=1 
and  roles_rights.meta_id = childs.to_meta_id 
and roles_rights.permission_id > 0 
and roles_rights.role_id = user_roles_crossref.role_id 
and user_roles_crossref.user_id =99
and meta.activated_date + ' ' + meta.activated_time <= '2000-05-02 02:37' 
and (meta.archived_date + ' ' + meta.archived_time >= '2000-05-02 02:37' or meta.archived_date + ' ' + meta.archived_time = '') 
union 
 select menu_sort,manual_sort_order,date_created,meta_headline,target from meta,childs,user_rights 
where meta.meta_id = childs.to_meta_id 
and childs.meta_id =  4260
and meta.archive=0 
and meta.activate=1 
and user_rights.meta_id = childs.to_meta_id 
and user_rights.permission_id > 0 
and user_rights.user_id = 99
and meta.activated_date + ' ' + meta.activated_time <= '2000-05-02 02:37' 
and (meta.archived_date + ' ' + meta.archived_time >= '2000-05-02 02:37' 
or meta.archived_date + ' ' + meta.archived_time = '') 
order by menu_sort,meta.meta_headline
go
IF OBJECT_ID('imse.meta_select') IS NOT NULL
     DROP PROCEDURE imse.meta_selec_12142000071754019
ELSE 
     EXEC sp_rename 'imse.meta_selec_12142000071754019','meta_select',OBJECT
go

--
-- Procedure Recreate
-- imse.getUserWriteRights
--
EXEC sp_rename 'imse.getUserWriteRights','getUserWri_12142000071807007',OBJECT
go
CREATE PROCEDURE getUserWriteRights AS
DECLARE @user int
DECLARE @doc int
select user_id from user_roles_crossref where user_id = @user and role_id = 0 -- Returnerar en rad om användare 1 är superadmin
select user_id from user_rights where meta_id = @doc and user_id = @user and permission_id = 99 -- Returnerar en rad om användare 1 skapade dokument 1351
select user_id from roles_rights join user_roles_crossref on roles_rights.role_id = user_roles_crossref.role_id where meta_id = @doc and user_id = @doc and permission_id = 3 -- Returnerar en rad om användare 1 är medlem i en grupp som har skrivrättigheter i dokument 1351
go
IF OBJECT_ID('imse.getUserWriteRights') IS NOT NULL
     DROP PROCEDURE imse.getUserWri_12142000071807007
ELSE 
     EXEC sp_rename 'imse.getUserWri_12142000071807007','getUserWriteRights',OBJECT
go

--
-- Procedure Recreate
-- imse.IMC_AddRole
--
EXEC sp_rename 'imse.IMC_AddRole','IMC_AddRol_12142000071807008',OBJECT
go
CREATE PROCEDURE IMC_AddRole 
/*
 Lets detect if we should add a read / or a write option
*/
 @metaId int,
 @aRole int,
 @typeOfRole int
AS
IF( @typeOfRole = 1) BEGIN
 -- Lets insert a read
 INSERT INTO roles_rights
 VALUES( @aRole ,  @metaId ,@typeOfRole )
END 
IF( @typeOfRole = 3 ) BEGIN
 -- WRITE
 INSERT INTO roles_rights
 VALUES ( @aRole , @metaId , @typeOfRole)
END
go
IF OBJECT_ID('imse.IMC_AddRole') IS NOT NULL
     DROP PROCEDURE imse.IMC_AddRol_12142000071807008
ELSE 
     EXEC sp_rename 'imse.IMC_AddRol_12142000071807008','IMC_AddRole',OBJECT
go

--
-- Procedure Recreate
-- imse.RoleCount
--
EXEC sp_rename 'imse.RoleCount','RoleCount_12142000071807010',OBJECT
go
CREATE PROCEDURE RoleCount
 @aRoleId int
AS
/*
 This function is used when an administrator tries to delete a role.
 This function counts in how many documents the role is used
*/
DECLARE @returnVal int
SELECT  @returnVal = COUNT(  r.role_id ) 
FROM ROLES_RIGHTS r
WHERE ROLE_ID = @aRoleId
-- Lets validate for null
SELECT @returnVal = ISNULL(  @returnVal , 0 )
SELECT @returnVal AS 'Number_of_roles'
go
IF OBJECT_ID('imse.RoleCount') IS NOT NULL
     DROP PROCEDURE imse.RoleCount_12142000071807010
ELSE 
     EXEC sp_rename 'imse.RoleCount_12142000071807010','RoleCount',OBJECT
go

--
-- Procedure Recreate
-- imse.RoleDelete
--
EXEC sp_rename 'imse.RoleDelete','RoleDelete_12142000071807011',OBJECT
go
/****** Object:  Stored Procedure dbo.RoleDelete    Script Date: 2000-10-27 14:21:05 ******/
CREATE PROCEDURE RoleDelete
/* Deletes an role from the role table. Used by the AdminRoles servlet
*/
	@roleId int
AS

DELETE FROM ROLES_RIGHTS WHERE ROLE_ID = @roleId
DELETE FROM user_roles_crossref WHERE ROLE_ID =@roleId
DELETE FROM ROLES WHERE ROLE_ID = @roleId
go
IF OBJECT_ID('imse.RoleDelete') IS NOT NULL
     DROP PROCEDURE imse.RoleDelete_12142000071807011
ELSE 
     EXEC sp_rename 'imse.RoleDelete_12142000071807011','RoleDelete',OBJECT
go

--
-- Procedure Recreate
-- imse.RoleDeleteViewAffectedMetaIds
--
EXEC sp_rename 'imse.RoleDeleteViewAffectedMetaIds','RoleDelete_12142000071807012',OBJECT
go
CREATE PROCEDURE RoleDeleteViewAffectedMetaIds
 @aRoleId int
AS
/*
 This function is used when an administrator tries to delete a role.
 All metaids where the role is used will be presenteted in i list
*/
SELECT  TOP 50 r.meta_id , r.meta_id
FROM ROLES_RIGHTS r
WHERE ROLE_ID = @aRoleId
-- Lets validate for null
--SELECT @returnVal = ISNULL(  @returnVal , -1 )
--SELECT @returnVal AS 'FoundRoleName'
go
IF OBJECT_ID('imse.RoleDeleteViewAffectedMetaIds') IS NOT NULL
     DROP PROCEDURE imse.RoleDelete_12142000071807012
ELSE 
     EXEC sp_rename 'imse.RoleDelete_12142000071807012','RoleDeleteViewAffectedMetaIds',OBJECT
go

--
-- Procedure Recreate
-- imse.GetCurrentSessionCounter
--
EXEC sp_rename 'imse.GetCurrentSessionCounter','GetCurrent_12142000071810001',OBJECT
go
CREATE PROCEDURE GetCurrentSessionCounter 
 
AS
 SELECT value 
 FROM sys_data
 WHERE type_id  = 1
go
IF OBJECT_ID('imse.GetCurrentSessionCounter') IS NOT NULL
     DROP PROCEDURE imse.GetCurrent_12142000071810001
ELSE 
     EXEC sp_rename 'imse.GetCurrent_12142000071810001','GetCurrentSessionCounter',OBJECT
go

--
-- Procedure Recreate
-- imse.GetCurrentSessionCounterDate
--
EXEC sp_rename 'imse.GetCurrentSessionCounterDate','GetCurrent_12142000071810002',OBJECT
go
CREATE PROCEDURE GetCurrentSessionCounterDate
 
AS
 SELECT value 
 FROM sys_data
 WHERE type_id  = 2
go
IF OBJECT_ID('imse.GetCurrentSessionCounterDate') IS NOT NULL
     DROP PROCEDURE imse.GetCurrent_12142000071810002
ELSE 
     EXEC sp_rename 'imse.GetCurrent_12142000071810002','GetCurrentSessionCounterDate',OBJECT
go

--
-- Procedure Recreate
-- imse.IncSessionCounter
--
EXEC sp_rename 'imse.IncSessionCounter','IncSession_12142000071810003',OBJECT
go
CREATE PROCEDURE IncSessionCounter 
AS
      
    DECLARE @current_value int
  select @current_value = (select value from sys_data where type_id = 1)
  set @current_value  =  @current_value +1
 update sys_data
 set value = @current_value where type_id = 1
 
  return
go
IF OBJECT_ID('imse.IncSessionCounter') IS NOT NULL
     DROP PROCEDURE imse.IncSession_12142000071810003
ELSE 
     EXEC sp_rename 'imse.IncSession_12142000071810003','IncSessionCounter',OBJECT
go

--
-- Procedure Recreate
-- imse.SetSessionCounterDate
--
EXEC sp_rename 'imse.SetSessionCounterDate','SetSession_12142000071810004',OBJECT
go
CREATE PROCEDURE SetSessionCounterDate
   @new_date varchar(20)
AS
      
 update sys_data
 set value = @new_date where type_id = 2
 
  return
go
IF OBJECT_ID('imse.SetSessionCounterDate') IS NOT NULL
     DROP PROCEDURE imse.SetSession_12142000071810004
ELSE 
     EXEC sp_rename 'imse.SetSession_12142000071810004','SetSessionCounterDate',OBJECT
go

--
-- Procedure Recreate
-- imse.SystemMessageGet
--
EXEC sp_rename 'imse.SystemMessageGet','SystemMess_12142000071810005',OBJECT
go
CREATE PROCEDURE SystemMessageGet AS
/*
 Used by the AdminSystemMessage servlet to retrieve the systemmessage
*/
SELECT s.value
FROM sys_data s
WHERE s.type_id = 3
go
IF OBJECT_ID('imse.SystemMessageGet') IS NOT NULL
     DROP PROCEDURE imse.SystemMess_12142000071810005
ELSE 
     EXEC sp_rename 'imse.SystemMess_12142000071810005','SystemMessageGet',OBJECT
go

--
-- Procedure Recreate
-- imse.SystemMessageSet
--
EXEC sp_rename 'imse.SystemMessageSet','SystemMess_12142000071810006',OBJECT
go
CREATE PROCEDURE SystemMessageSet
/*
Lets update the system message table. Used by the AdminSystemMessage servlet
*/
 @newMsg varchar(80)
AS
UPDATE sys_data
SET value = @newMsg
WHERE type_id = 3
go
IF OBJECT_ID('imse.SystemMessageSet') IS NOT NULL
     DROP PROCEDURE imse.SystemMess_12142000071810006
ELSE 
     EXEC sp_rename 'imse.SystemMess_12142000071810006','SystemMessageSet',OBJECT
go

--
-- Procedure Recreate
-- imse.AddNewuser
--
EXEC sp_rename 'imse.AddNewuser','AddNewuser_12142000071825002',OBJECT
go
CREATE PROCEDURE AddNewuser

/*
Adds a new user to the user table
usertype. 0=special, 1=default, 2=conferenceuser 
*/
 @user_id int,
 @login_name char(15),
 @login_password char(15),
 @first_name char(25),
 @last_name char(30),
 @title char(30),
 @company char(30),
 @address char(40),
 @city char(30),
 @zip char (15),
 @country char(30),
 @county_council char(30),
 @email char(50),
 @admin_mode int,
 @last_page int,
 @archive_mode int,
 @lang_id int,
 @user_type int,
 @active int

AS
INSERT INTO users (user_id,login_name,login_password,first_name,last_name, title, company, address,city,zip,country,county_council,email,admin_mode,last_page,archive_mode,lang_id, user_type, active, create_date)
VALUES (@user_id, @login_name, @login_password, @first_name, @last_name, @title, @company,  @address, @city, @zip, @country,
   @county_council, @email, @admin_mode, @last_page, @archive_mode, @lang_id ,@user_type, @active, getDate())

/*

EATE PROCEDURE AddNewuser

Adds a new user to the user table
usertype. 0=special, 1=default, 2=conferenceuser

 @user_id int,
 @login_name char(15),
 @login_password char(15),
 @first_name char(25),
 @last_name char(30),
 @address char(40),
 @city char(30),
 @zip char (15),
 @country char(30),
 @county_council char(30),
 @email char(50),
 @admin_mode int,
 @last_page int,
 @archive_mode int,
 @lang_id int,
 @user_type int,
 @active int
AS
INSERT INTO users (user_id,login_name,login_password,first_name,last_name,address,city,zip,country,county_council,email,admin_mode,last_page,archive_mode,lang_id, user_type, active, create_date)
VALUES (@user_id, @login_name, @login_password, @first_name, @last_name, @address, @city, @zip, @country,
   @county_council, @email, @admin_mode, @last_page, @archive_mode, @lang_id ,@user_type, @active, getDate())



*/
/*
CREATE PROCEDURE AddNewuser

Adds a new user to the user table
usertype. 0=special, 1=default, 2=conferenceuser 

 @user_id int,
 @login_name char(15),
 @login_password char(15),
 @first_name char(25),
 @last_name char(30),
 @address char(40),
 @city char(30),
 @zip char (15),
 @country char(30),
 @county_council char(30),
 @email char(50),
 @admin_mode int,
 @last_page int,
 @archive_mode int,
 @lang_id int,
 @user_type int,
 @active int
AS
INSERT INTO users (user_id,login_name,login_password,first_name,last_name,address,city,zip,country,county_council,email,admin_mode,last_page,archive_mode,lang_id, user_type, active, create_date)
VALUES (@user_id, @login_name, @login_password, @first_name, @last_name, @address, @city, @zip, @country,
   @county_council, @email, @admin_mode, @last_page, @archive_mode, @lang_id ,@user_type, @active, getDate())

*/
go
IF OBJECT_ID('imse.AddNewuser') IS NOT NULL
     DROP PROCEDURE imse.AddNewuser_12142000071825002
ELSE 
     EXEC sp_rename 'imse.AddNewuser_12142000071825002','AddNewuser',OBJECT
go

--
-- Procedure Recreate
-- imse.ChangeUserActiveStatus
--
EXEC sp_rename 'imse.ChangeUserActiveStatus','ChangeUser_12142000071825003',OBJECT
go
/****** Object:  Stored Procedure dbo.ChangeUserActiveStatus    Script Date: 2000-10-27 14:21:06 ******/
CREATE PROCEDURE ChangeUserActiveStatus @user_id int, @active int AS


/* 
 * change users activestate
*/
UPDATE users 
SET 
active = @active

WHERE user_id = @user_id
go
IF OBJECT_ID('imse.ChangeUserActiveStatus') IS NOT NULL
     DROP PROCEDURE imse.ChangeUser_12142000071825003
ELSE 
     EXEC sp_rename 'imse.ChangeUser_12142000071825003','ChangeUserActiveStatus',OBJECT
go

--
-- Procedure Recreate
-- imse.GetUserPhoneNumbers
--
EXEC sp_rename 'imse.GetUserPhoneNumbers','GetUserPho_12142000071825004',OBJECT
go
/****** Object:  Stored Procedure dbo.GetUserPhoneNumbers    Script Date: 2000-10-27 15:43:09 ******/
CREATE PROCEDURE GetUserPhoneNumbers
/*
Returns a users phonenumbers. Used by AdminUserProps servlet
*/
	@user_id int

AS

-- The new version which includes phones
SELECT p.phone_id, p.country_code , p.area_code , p.number, p.user_id 
FROM users u , phones p 
WHERE u.user_id = p.user_id
AND u.user_id = @user_id
go
IF OBJECT_ID('imse.GetUserPhoneNumbers') IS NOT NULL
     DROP PROCEDURE imse.GetUserPho_12142000071825004
ELSE 
     EXEC sp_rename 'imse.GetUserPho_12142000071825004','GetUserPhoneNumbers',OBJECT
go

--
-- Procedure Recreate
-- imse.GetUserPhones
--
EXEC sp_rename 'imse.GetUserPhones','GetUserPho_12142000071825005',OBJECT
go
/****** Object:  Stored Procedure dbo.GetUserPhones    Script Date: 2000-10-27 15:43:09 ******/
CREATE PROCEDURE GetUserPhones
	@user_id int
AS
/*
Used to generate a list with all type of users. Used from AdminUserProps
*/

SELECT p.phone_id, RTRIM(p.country_code) + ' ' + RTRIM(p.area_code) + ' ' + RTRIM(p.number) as numbers
FROM users u , phones p 
WHERE u.user_id = p.user_id
AND u.user_id = @user_id
go
IF OBJECT_ID('imse.GetUserPhones') IS NOT NULL
     DROP PROCEDURE imse.GetUserPho_12142000071825005
ELSE 
     EXEC sp_rename 'imse.GetUserPho_12142000071825005','GetUserPhones',OBJECT
go

--
-- Procedure Recreate
-- imse.GetUsersWhoBelongsToRole
--
EXEC sp_rename 'imse.GetUsersWhoBelongsToRole','GetUsersWh_12142000071825006',OBJECT
go
/****** Object:  Stored Procedure dbo.GetUsersWhoBelongsToRole    Script Date: 2000-10-27 14:21:05 ******/
CREATE PROCEDURE GetUsersWhoBelongsToRole @role_id int AS

/*
 * select user who belongs to role role_id
*/
select us.user_id, u.last_name + ', ' + u.first_name 

from user_roles_crossref us

join users u
		on us.user_id = u.user_id

where role_id = @role_id

order by  last_name
go
IF OBJECT_ID('imse.GetUsersWhoBelongsToRole') IS NOT NULL
     DROP PROCEDURE imse.GetUsersWh_12142000071825006
ELSE 
     EXEC sp_rename 'imse.GetUsersWh_12142000071825006','GetUsersWhoBelongsToRole',OBJECT
go

--
-- Procedure Recreate
-- imse.PermissionsGetPermission
--
EXEC sp_rename 'imse.PermissionsGetPermission','Permission_12142000071825007',OBJECT
go
/****** Object:  Stored Procedure dbo.PermissionsGetPermission    Script Date: 2000-10-27 14:21:06 ******/
CREATE PROCEDURE PermissionsGetPermission @login_name varchar(15), @permission int AS
/*

*/
select login_password, first_name, last_name, email, min(permissions&@permission), lang_prefix 
from users u 
join lang_prefixes lp 
				on u.lang_id = lp.lang_id 
join user_roles_crossref urc 
				on u.user_id = urc.user_id left 
join roles r 
				on r.role_id = urc.role_id
where login_name = @login_name
group by login_password, first_name, last_name, email, lang_prefix
go
IF OBJECT_ID('imse.PermissionsGetPermission') IS NOT NULL
     DROP PROCEDURE imse.Permission_12142000071825007
ELSE 
     EXEC sp_rename 'imse.Permission_12142000071825007','PermissionsGetPermission',OBJECT
go

--
-- Procedure Recreate
-- imse.UpdateUser
--
EXEC sp_rename 'imse.UpdateUser','UpdateUser_12142000071825008',OBJECT
go
CREATE PROCEDURE imse.UpdateUser
/*
usertype. 0=special, 1=default, 2=conferenceuser 
*/
	@user_id int,
	@login_name char(15),
	@login_password char(15),
	@first_name char(25),
	@last_name char(30),
	@title char(30),
	@company char(30),
	@address char(40),
	@city char(30),
	@zip char (15),
	@country char(30),
	@county_council char(30),
	@email char(50),
	@admin_mode int,
	@last_page int,
	@archive_mode int,
	@lang_id int,
	@user_type int,
	@active int
AS
UPDATE users 
SET login_name = @login_name,
login_password = @login_password,
first_name = @first_name,
last_name = @last_name,
title = @title,
company = @company,
address =  @address,
city = @city,
zip = @zip,
country = @country,
county_council = @county_council,
email = @email,
user_type = @user_type,
active = @active,
lang_id = @lang_id

WHERE user_id = @User_id 


/****** Object:  Stored Procedure dbo.UpdateUser    Script Date: 2000-10-27 15:19:01 *****
CREATE PROCEDURE UpdateUser

--usertype. 0=special, 1=default, 2=conferenceuser 

	@user_id int,
	@login_name char(15),
	@login_password char(15),
	@first_name char(25),
	@last_name char(30),
	@title char(30),
	@company char(30),
	@address char(40),
	@city char(30),
	@zip char (15),
	@country char(30),
	@county_council char(30),
	@email char(50),
	@admin_mode int,
	@last_page int,
	@archive_mode int,
	@lang_id int,
	@user_type int,
	@active int
AS
UPDATE users 
SET login_name = @login_name,
login_password = @login_password,
first_name = @first_name,
last_name = @last_name,
title = @title,
company = @company,
address =  @address,
city = @city,
zip = @zip,
country = @country,
county_council = @county_council,
email = @email,
user_type = @user_type,
active = @active

WHERE user_id = @User_id 

*/
go
IF OBJECT_ID('imse.UpdateUser') IS NOT NULL
     DROP PROCEDURE imse.UpdateUser_12142000071825008
ELSE 
     EXEC sp_rename 'imse.UpdateUser_12142000071825008','UpdateUser',OBJECT
go

--
-- Procedure Recreate
-- imse.UserPrefsChange
--
EXEC sp_rename 'imse.UserPrefsChange','UserPrefsC_12142000071825009',OBJECT
go
CREATE PROCEDURE imse.UserPrefsChange
	 @aUserId int
/*
  Returns the information for a user which he is able to change self. Observer that we
  return the password as an empty string
*/
AS


-- SELECT @aUserId AS 'TEST'
SELECT user_id, login_name,  "", "", first_name, last_name,  title, company, address, city, zip, country, county_council, email --, profession, company
FROM users
WHERE user_id = @aUserId 

/****** Object:  Stored Procedure dbo.UserPrefsChange    Script Date: 2000-10-27 15:19:01 *****
CREATE PROCEDURE imse.UserPrefsChange
	 @aUserId int

--  Returns the information for a user which he is able to change self. Observer that we
--  return the password as an empty string

AS


-- SELECT @aUserId AS 'TEST'
SELECT user_id, login_name,  "", "", first_name, last_name,  title, company, address, city, zip, country, county_council, email --, profession, company
FROM users
WHERE user_id = @aUserId 

*/
go
IF OBJECT_ID('imse.UserPrefsChange') IS NOT NULL
     DROP PROCEDURE imse.UserPrefsC_12142000071825009
ELSE 
     EXEC sp_rename 'imse.UserPrefsC_12142000071825009','UserPrefsChange',OBJECT
go

--
-- Index Create
-- dbo.browsers.IX_browsers
--
CREATE NONCLUSTERED INDEX IX_browsers
    ON dbo.browsers(value)
    ON [PRIMARY]
go

--
-- Index Create
-- dbo.childs.childs_meta_id
--
CREATE CLUSTERED INDEX childs_meta_id
    ON dbo.childs(meta_id)
    ON [PRIMARY]
go

--
-- Index Create
-- dbo.doc_permission_sets.IX_doc_permission_sets
--
CREATE CLUSTERED INDEX IX_doc_permission_sets
    ON dbo.doc_permission_sets(meta_id,set_id)
    ON [PRIMARY]
go

--
-- Index Create
-- dbo.doc_permissions.IX_doc_permissions
--
CREATE CLUSTERED INDEX IX_doc_permissions
    ON dbo.doc_permissions(permission_id)
    ON [PRIMARY]
go

--
-- Index Create
-- dbo.doc_types.IX_doc_types
--
CREATE CLUSTERED INDEX IX_doc_types
    ON dbo.doc_types(lang_prefix,doc_type)
    ON [PRIMARY]
go

--
-- Index Create
-- dbo.fileupload_docs.fileupload_docs_meta_id
--
CREATE CLUSTERED INDEX fileupload_docs_meta_id
    ON dbo.fileupload_docs(meta_id)
    ON [PRIMARY]
go

--
-- Index Create
-- dbo.meta.meta_meta_id
--
CREATE CLUSTERED INDEX meta_meta_id
    ON dbo.meta(meta_id,show_meta,activate)
    ON [PRIMARY]
go

--
-- Index Create
-- dbo.roles_rights.roles_rights_meta_id
--
CREATE CLUSTERED INDEX roles_rights_meta_id
    ON dbo.roles_rights(meta_id,role_id,set_id)
    ON [PRIMARY]
go

--
-- Index Create
-- dbo.roles_rights.roles_rights_role_id
--
CREATE NONCLUSTERED INDEX roles_rights_role_id
    ON dbo.roles_rights(role_id)
    ON [PRIMARY]
go

--
-- Procedure Create
-- dbo.AddNewuser
--
CREATE PROCEDURE AddNewuser
/*
Adds a new user to the user table
usertype. 0=special, 1=default, 2=conferenceuser 
*/
 @user_id int,
 @login_name char(15),
 @login_password char(15),
 @first_name char(25),
 @last_name char(30),
 @address char(40),
 @city char(30),
 @zip char (15),
 @country char(30),
 @county_council char(30),
 @email char(50),
 @admin_mode int,
 @last_page int,
 @archive_mode int,
 @lang_id int,
 @user_type int,
 @active int

AS
INSERT INTO users (user_id,login_name,login_password,first_name,last_name,address,city,zip,country,county_council,email,admin_mode,last_page,archive_mode,lang_id, user_type, active, create_date)
VALUES (@user_id, @login_name, @login_password, @first_name, @last_name, @address, @city, @zip, @country,
   @county_council, @email, @admin_mode, @last_page, @archive_mode, @lang_id ,@user_type, @active, getDate())
go

--
-- Procedure Create
-- dbo.AddStatistics
--
CREATE PROCEDURE AddStatistics @name VARCHAR(120) AS

UPDATE	stats
SET		num = num + 1
WHERE	name = @name

IF @@ROWCOUNT = 0
BEGIN
INSERT	stats
VALUES	(	@name,
			1
		)
END
go

--
-- Procedure Create
-- dbo.AddUserRole
--
CREATE PROCEDURE AddUserRole
/* Adds a role to a particular user
*/
 @aUser_id int,
 @aRole_id int
AS
 INSERT INTO  user_roles_crossref(user_id, role_id)
 VALUES( @aUser_id , @aRole_id)
go

--
-- Procedure Create
-- dbo.ChangeUserActiveStatus
--
CREATE PROCEDURE ChangeUserActiveStatus @user_id int, @active int AS


/* 
 * change users activestate
*/
UPDATE users 
SET 
active = @active

WHERE user_id = @user_id
go

--
-- Procedure Create
-- dbo.CheckAdminRights
--
CREATE PROCEDURE CheckAdminRights
/*
Detects if a user is administrator or not
*/
 @aUserId int
AS
SELECT users.user_id, roles.role_id
FROM users INNER JOIN
    user_roles_crossref ON 
    users.user_id = user_roles_crossref.user_id INNER JOIN
    roles ON user_roles_crossref.role_id = roles.role_id
WHERE roles.role_id = 0 AND users.user_id = @aUserId
go

--
-- Procedure Create
-- dbo.CheckExistsInMenu
--
CREATE PROCEDURE CheckExistsInMenu

/*
This function is used by servlet ConfAdd to check if the meta_id argument
already exists in the database. Thas because a db can be used from
different servers, and a meta_id can be used twice to be added in the 
database
*/
	@aMetaId int
 AS

DECLARE @returnVal int

SELECT @returnVal = meta_id
FROM childs
WHERE to_meta_id = @aMetaId

SELECT @returnVal = ISNULL(@returnVal, 0) 
SELECT @returnVal AS 'ExistsInMenu'
go

--
-- Procedure Create
-- dbo.ClassificationAdd
--
CREATE PROCEDURE ClassificationAdd 

	@theMetaId int,
	 @theClassCode varchar(200)
AS
/*
Adds a classification code and fix the crossreference. If a code already exists in the table, it will link to that 
code 
*/


-- Lets check if a code already exists, if so just link to that code
DECLARE @foundCode int
SELECT @foundCode = 0

-- Lets start with to find the id for the classification code
SELECT @foundCode = class_id
FROM classification
WHERE code LIKE @theClassCode

-- Lets check if the lassification code exists or if we should create it 
-- IF ( @foundCode <> 0 ) BEGIN	
	-- PRINT 'Koden fanns redan'
--END ELSE BEGIN 
-- Ok, Lets link to that code
IF ( @foundCode = 0 ) BEGIN	
	--PRINT 'Koden fanns inte'
	-- Lets start to add the classification
	INSERT INTO classification (  Code)
	VALUES (  @theClassCode )
	SELECT @foundCode = @@identity
END 

-- Lets insert the new crossreferences
INSERT INTO meta_classification (meta_id,class_id)
VALUES (  @theMetaId , @foundCode )
go

--
-- Procedure Create
-- dbo.Classification_Fix
--
CREATE PROCEDURE Classification_Fix
	@meta_id int ,
	@string varchar(2000)
AS

declare @value varchar(50)
declare @pos int

-- Lets delete all current crossreferences, if any
DELETE 
FROM meta_classification 
WHERE meta_id = @meta_id

--SELECT @string = 'ett;två;tre;fyra;fem'
-- Lets search for semicolon, if not found then look for a , This is relevant 
-- when we convert the db. After convertion, only look for semicolons
SELECT @pos = PATINDEX('%;%', @string)
IF( @pos = 0 ) BEGIN
	SELECT @pos = PATINDEX('%,%', @string)
END
WHILE @pos > 0
BEGIN
	SELECT @value = LEFT(@string,@pos-1)
	SELECT @pos = LEN(@string) - @pos
	SELECT @string = RIGHT(@string,@pos)
	SELECT  @value  = lTrim(rTrim( ( @value ) ))	

	EXEC ClassificationAdd @meta_id , @value
	--INSERT INTO data (value) VALUES (@value)
	SELECT @pos = PATINDEX('%;%', @string)
	-- PRINT @value
END
-- Lets get the last part of the string
--PRINT @string
SELECT @value = @string
SELECT  @value  = lTrim(rTrim( ( @value ) ))	
EXEC ClassificationAdd @meta_id , @value

-- INSERT INTO data (value) VALUES (@string)
go

--
-- Procedure Create
-- dbo.Classification_Get_All
--
CREATE PROCEDURE Classification_Get_All AS
/*
Get the meta_id and classifcation so we can start convert them
*/

SELECT meta_id, classification
	FROM meta
	WHERE classification IS NOT NULL
	and classification <> ''
	and classification NOT LIKE 'META NAME%'
	and classification NOT LIKE 'Test'
go

--
-- Procedure Create
-- dbo.DelUser
--
CREATE PROCEDURE DelUser
 @aUserId int
AS
 
 DELETE
 FROM user_roles_crossref
 WHERE user_id = @aUserId
 DELETE 
 FROM users
 WHERE user_id = @aUserId
go

--
-- Procedure Create
-- dbo.DelUserRoles
--
CREATE PROCEDURE DelUserRoles
 @aUserId int
AS
 DELETE 
 FROM user_roles_crossref
 WHERE user_id = @aUserId
go

--
-- Procedure Create
-- dbo.DeleteDocPermissionSetEx
--
CREATE PROCEDURE DeleteDocPermissionSetEx @meta_id INT, @set_id INT AS

/*
	Delete extended permissions for a permissionset for a document
*/

DELETE FROM		doc_permission_sets_ex
WHERE		meta_id = @meta_id
		AND	set_id = @set_id
go

--
-- Procedure Create
-- dbo.DeleteNewDocPermissionSetEx
--
CREATE PROCEDURE DeleteNewDocPermissionSetEx @meta_id INT, @set_id INT AS

/*
	Delete extended permissions for a permissionset for a document
*/

DELETE FROM		new_doc_permission_sets_ex
WHERE		meta_id = @meta_id
		AND	set_id = @set_id
go

--
-- Procedure Create
-- dbo.FindMetaId
--
CREATE PROCEDURE [FindMetaId]
	@meta_id int
 AS

SELECT meta_id 
FROM meta
WHERE meta_id = @meta_id
go

--
-- Procedure Create
-- dbo.FindUserName
--
CREATE PROCEDURE [FindUserName] 
	@userName char(15)

AS
/*
 This function is used from the conference when  someone is logging in to the 
conference. The system searches for the username and returns the 
userId, userName and password
*/
SELECT  u.login_name
FROM users u
WHERE u.login_name = @userName
go

--
-- Procedure Create
-- dbo.GetAdminChilds
--
CREATE PROCEDURE GetAdminChilds
@meta_id int,
@user_id int
AS

select 		to_meta_id
from 		childs c
join 		meta m 			
					on 		m.meta_id = c.to_meta_id				-- meta.meta_id corresponds to childs.to_meta_id
						and	m.activate != 0						-- Only include the documents that are active in the meta table.
						and	c.meta_id = @meta_id					-- Only include documents that are children to this particular meta_id
left join		roles_rights rr								-- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
					on		c.to_meta_id = rr.meta_id					-- Only include rows with the documents we are interested in
							and rr.permission_id  = 3					-- Only include permissions that gives right to change the document
join		user_roles_crossref urc							-- This table tells us which users have which roles
					on		urc.user_id = @user_id					-- Only include the rows with the user we are interested in.
						and	(	rr.role_id = urc.role_id				-- Include rows where the users roles match the roles that have permissions on the documents
							or	urc.role_id = 0					-- and also include the rows that tells us this user is a superadmin (That is, return a row with urc.role_id = 0 for each document.)
							)
left join		user_rights ur								-- We may have permission, even though we don't own any documents... That is, if we're roleadmins or superadmin. When we lose ownership-based rights, this may be skipped.
					on		ur.meta_id = c.to_meta_id				-- Only include rows with the documents we are interested in
						and	ur.user_id =  urc.user_id					-- Only include rows with the user  we are interested in
						and	ur.permission_id = 99					-- Only include rows that mean "Ownership"
go

--
-- Procedure Create
-- dbo.GetAllRoles
--
CREATE PROCEDURE GetAllRoles AS
SELECT role_id, role_name
FROM roles
 
ORDER BY role_name
go

--
-- Procedure Create
-- dbo.GetAllUsers
--
CREATE PROCEDURE [GetAllUsers] AS
  select *
 from USERS
 
 order by  last_name
go

--
-- Procedure Create
-- dbo.GetAllUsersInList
--
CREATE PROCEDURE GetAllUsersInList AS
/*
This function is used from AdminIpAcces servlet to generate a list
*/

SELECT user_id, last_name + ', ' + first_name from users
ORDER BY last_name
go

--
-- Procedure Create
-- dbo.GetCategoryUsers
--
CREATE PROCEDURE GetCategoryUsers
/*
Used from servlet AdminUser
*/
	@category int

AS
SELECT user_id, last_name + ', ' + first_name
FROM users
WHERE user_type = @category
ORDER BY last_name
go

--
-- Procedure Create
-- dbo.GetChilds
--
CREATE PROCEDURE GetChilds
	@meta_id int,
	@user_id int
AS
/*
Nice little query that lists the children of a document that a particular user may see, and includes a field that tells you wether he may do something to it or not.
*/
declare @sort_by int

select @sort_by = sort_order from text_docs where meta_id = @meta_id

-- Manual sort order
if @sort_by = 2
begin
select  to_meta_id, c.menu_sort,manual_sort_order, doc_type,
		archive,target, left(convert (varchar,date_created,120),10),
		meta_headline,meta_text,meta_image,frame_name,
		activated_date+activated_time,archived_date+archived_time,
		min(urc.role_id * ISNULL(~CAST(dps.permission_id AS BIT),1) * ISNULL(rr.set_id,1)),
		fd.filename
from   childs c
join   meta m    
					on   	m.meta_id = c.to_meta_id					-- meta.meta_id corresponds to childs.to_meta_id
					and 	m.activate > 0							-- Only include the documents that are active in the meta table.
					and 	c.meta_id = @meta_id						-- Only include documents that are children to this particular meta_id
left join roles_rights rr												-- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
					on 	c.to_meta_id = rr.meta_id						-- Only include rows with the documents we are interested in
left join doc_permission_sets dps											-- Include the permission_sets
					on 	c.to_meta_id = dps.meta_id					-- for each document
					and	dps.set_id = rr.set_id						-- and only the sets for the roles we are interested in
					and	dps.permission_id > 0						-- and only the sets that have any permission
join user_roles_crossref urc											-- This table tells us which users have which roles
					on	urc.user_id = @user_id						-- Only include the rows with the user we are interested in...
					and	( 
							rr.role_id = urc.role_id					-- Include rows where the users roles match the roles that have permissions on the documents
						or 	urc.role_id = 0						-- and also include the rows that tells us this user is a superadmin
						or 	(
								m.show_meta != 0				-- and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
							and	ISNULL(~CAST(dps.permission_id AS BIT),1) != 1
						)
					)
left join fileupload_docs fd
					on  fd.meta_id = c.to_meta_id
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
		archive,target, left(convert (varchar,date_created,120),10),
		meta_headline,meta_text,meta_image,frame_name,
		activated_date+activated_time,archived_date+archived_time,
		fd.filename
order by  menu_sort,c.manual_sort_order desc
end
else if @sort_by = 3
begin
select  to_meta_id, c.menu_sort,manual_sort_order, doc_type,
		archive,target, left(convert (varchar,date_created,120),10),
		meta_headline,meta_text,meta_image,frame_name,
		activated_date+activated_time,archived_date+archived_time,
		min(urc.role_id * ISNULL(dps.permission_id&~1,1) * ISNULL(rr.set_id,1)),
		fd.filename
from   childs c
join   meta m    
					on   	m.meta_id = c.to_meta_id					-- meta.meta_id corresponds to childs.to_meta_id
					and 	m.activate > 0							-- Only include the documents that are active in the meta table.
					and 	c.meta_id = @meta_id						-- Only include documents that are children to this particular meta_id
left join roles_rights rr												-- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
					on 	c.to_meta_id = rr.meta_id						-- Only include rows with the documents we are interested in
left join doc_permission_sets dps											-- Include the permission_sets
					on 	c.to_meta_id = dps.meta_id					-- for each document
					and	dps.set_id = rr.set_id						-- and only the sets for the roles we are interested in
					and	dps.permission_id > 0						-- and only the sets that have any permission
join user_roles_crossref urc											-- This table tells us which users have which roles
					on	urc.user_id = @user_id						-- Only include the rows with the user we are interested in...
					and	( 
							rr.role_id = urc.role_id					-- Include rows where the users roles match the roles that have permissions on the documents
						or 	urc.role_id = 0						-- and also include the rows that tells us this user is a superadmin
						or 	(
								m.show_meta != 0				-- and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
							and	ISNULL(dps.permission_id&~1,1) != 1
						)
					)
left join fileupload_docs fd
					on  fd.meta_id = c.to_meta_id
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
		archive,target, left(convert (varchar,date_created,120),10),
		meta_headline,meta_text,meta_image,frame_name,
		activated_date+activated_time,archived_date+archived_time,
		fd.filename
order by		menu_sort,left(convert (varchar,date_created,120),10) desc
end
else if @sort_by = 1
begin
select  to_meta_id, c.menu_sort,manual_sort_order, doc_type,
		archive,target, left(convert (varchar,date_created,120),10),
		meta_headline,meta_text,meta_image,frame_name,
		activated_date+activated_time,archived_date+archived_time,
		min(urc.role_id * ISNULL(dps.permission_id&~1,1) * ISNULL(rr.set_id,1)),
		fd.filename
from   childs c
join   meta m    
					on   	m.meta_id = c.to_meta_id					-- meta.meta_id corresponds to childs.to_meta_id
					and 	m.activate > 0							-- Only include the documents that are active in the meta table.
					and 	c.meta_id = @meta_id						-- Only include documents that are children to this particular meta_id
left join roles_rights rr												-- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
					on 	c.to_meta_id = rr.meta_id						-- Only include rows with the documents we are interested in
left join doc_permission_sets dps											-- Include the permission_sets
					on 	c.to_meta_id = dps.meta_id					-- for each document
					and	dps.set_id = rr.set_id						-- and only the sets for the roles we are interested in
					and	dps.permission_id > 0						-- and only the sets that have any permission
join user_roles_crossref urc											-- This table tells us which users have which roles
					on	urc.user_id = @user_id						-- Only include the rows with the user we are interested in...
					and	( 
							rr.role_id = urc.role_id					-- Include rows where the users roles match the roles that have permissions on the documents
						or 	urc.role_id = 0						-- and also include the rows that tells us this user is a superadmin
						or 	(
								m.show_meta != 0				-- and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
							and	ISNULL(dps.permission_id&~1,1) != 1
						)
					)
left join fileupload_docs fd
					on  fd.meta_id = c.to_meta_id
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
		archive,target, left(convert (varchar,date_created,120),10),
		meta_headline,meta_text,meta_image,frame_name,
		activated_date+activated_time,archived_date+archived_time,
		fd.filename
order by		menu_sort,meta_headline
end
go

--
-- Procedure Create
-- dbo.GetCurrentSessionCounter
--
CREATE PROCEDURE GetCurrentSessionCounter 
 
AS
 SELECT value 
 FROM sys_data
 WHERE type_id  = 1
go

--
-- Procedure Create
-- dbo.GetCurrentSessionCounterDate
--
CREATE PROCEDURE GetCurrentSessionCounterDate
 
AS
 SELECT value 
 FROM sys_data
 WHERE type_id  = 2
go

--
-- Procedure Create
-- dbo.GetDocType
--
CREATE PROCEDURE GetDocType
	@meta_id int
AS
/*
 Used by external systems to get the docType
*/

SELECT doc_type
FROM meta
WHERE meta_id = @meta_id
go

--
-- Procedure Create
-- dbo.GetDocTypes
--
CREATE PROCEDURE GetDocTypes @lang_prefix VARCHAR(3) AS

SELECT doc_type,type FROM doc_types
WHERE lang_prefix = @lang_prefix
ORDER BY doc_type
go

--
-- Procedure Create
-- dbo.GetDocTypesForUser
--
CREATE PROCEDURE GetDocTypesForUser @meta_id INT,@user_id INT, @lang_prefix VARCHAR(3) AS

/*
	Nice query that fetches all document types a user may create in a document,
	for easy insertion into an html-option-list, no less!
*/
SELECT	DISTINCT dt.doc_type, dt.type
FROM 		doc_types dt
JOIN		user_roles_crossref urc
							ON	urc.user_id = @user_id
							AND	dt.lang_prefix = @lang_prefix
LEFT JOIN	roles_rights rr
							ON	rr.meta_id = @meta_id
							AND	rr.role_id = urc.role_id
LEFT JOIN	doc_permission_sets dps
							ON	dps.meta_id = rr.meta_id
							AND	dps.set_id = rr.set_id
LEFT JOIN	doc_permission_sets_ex dpse
							ON	dpse.permission_data = dt.doc_type
							AND	dpse.meta_id = rr.meta_id
							AND	dpse.set_id = rr.set_id
							AND	dpse.permission_id = 8 -- Create document
WHERE
								dpse.permission_data IS NOT NULL
							OR	rr.set_id = 0
							OR	urc.role_id = 0
ORDER BY	dt.doc_type
go

--
-- Procedure Create
-- dbo.GetDocTypesWithNewPermissions
--
CREATE PROCEDURE GetDocTypesWithNewPermissions @meta_id INT,@set_id INT, @lang_prefix VARCHAR(3) AS

/*
	Retrieves a list of all doc-types, with a indicator of wether a particular permission-set may use it.
	The permission-set must still have the "Create document"-permission set, though. ( Not checked in this proc )

	Column 1:	The doc-type
	Column 2:	The name of the doc-type
	Column 3:	> -1 if this set_id may use this.
*/
SELECT	doc_type,type,ISNULL(dpse.permission_data,-1)
FROM 		doc_types dt
LEFT JOIN	new_doc_permission_sets_ex dpse
							ON	dpse.permission_data = dt.doc_type
							AND	dpse.meta_id = @meta_id
							AND	dpse.set_id = @set_id
							AND	dpse.permission_id = 8
WHERE	dt.lang_prefix = @lang_prefix
ORDER	BY	CAST(ISNULL(dpse.permission_data,-1)+1  AS BIT) DESC,doc_type
go

--
-- Procedure Create
-- dbo.GetDocTypesWithPermissions
--
CREATE PROCEDURE GetDocTypesWithPermissions @meta_id INT,@set_id INT, @lang_prefix VARCHAR(3) AS

/*
	Retrieves a list of all doc-types, with a indicator of wether a particular permission-set may use it.
	The permission-set must still have the "Create document"-permission set, though. ( Not checked in this proc )

	Column 1:	The doc-type
	Column 2:	The name of the doc-type
	Column 3:	> -1 if this set_id may use this.
*/
SELECT	doc_type,type,ISNULL(dpse.permission_data,-1)
FROM 		doc_types dt
LEFT JOIN	doc_permission_sets_ex dpse
							ON	dpse.permission_data = dt.doc_type
							AND	dpse.meta_id = @meta_id
							AND	dpse.set_id = @set_id
							AND	dpse.permission_id = 8
WHERE	dt.lang_prefix = @lang_prefix
ORDER	BY	CAST(ISNULL(dpse.permission_data,-1)+1  AS BIT) DESC,doc_type
go

--
-- Procedure Create
-- dbo.GetHighestUserId
--
CREATE PROCEDURE GetHighestUserId
AS
--DECLARE @retVal int

SELECT MAX(user_id) +1
FROM users
go

--
-- Procedure Create
-- dbo.GetImgs
--
CREATE PROCEDURE GetImgs
@meta_id int AS

select '#img'+convert(varchar(5), name)+'#',name,imgurl,linkurl,width,height,border,v_space,h_space,image_name,align,alt_text,low_scr,target,target_name from images where meta_id = @meta_id
go

--
-- Procedure Create
-- dbo.GetLangPrefix
--
CREATE PROCEDURE GetLangPrefix
	@meta_id int
AS
/*
 Used by external systems to get the langprefix
*/

SELECT lang_prefix 
FROM meta
WHERE meta_id = @meta_id
go

--
-- Procedure Create
-- dbo.GetLangPrefixFromId
--
CREATE PROCEDURE GetLangPrefixFromId
/* Get the users preferred language. Used by the administrator functions.
Begin with getting the users langId from the userobject.
*/
	@aLangId int
 AS
SELECT lang_prefix 
FROM lang_prefixes
WHERE lang_id = @aLangId
go

--
-- Procedure Create
-- dbo.GetLanguageList
--
CREATE PROCEDURE GetLanguageList AS

/*
 Returns all 
*/

SELECT lp.lang_id , lang.language
FROM lang_prefixes lp, languages lang
WHERE lp.lang_prefix = lang.lang_prefix
go

--
-- Procedure Create
-- dbo.GetMetaPathInfo
--
CREATE PROCEDURE GetMetaPathInfo
	@meta_id int
AS
/*
 Used by external systems to get the meta_id dependent part of the path to
for example the image folder or to the html folder
Ex of what this function will return: 
*/

DECLARE @docType char(20)
DECLARE @langPrefix char(20)

SELECT RTRIM(lang_prefix) + '/' +  RTRIM(doc_type) + '/' 
FROM META 
WHERE meta_id = @meta_id
go

--
-- Procedure Create
-- dbo.GetNewPermissionSet
--
CREATE PROCEDURE [GetNewPermissionSet] @meta_id INT, @set_id INT, @lang_prefix VARCHAR(3) AS


/*
	Nice little query that returns which permissions a permissionset consists of.

	Column 1:	The id of the permission
	Column 2:	The description of the permission
	Column 3:	Wether the permission is set. 0 or 1.
*/

SELECT	p.permission_id AS p_id, p.description,CAST(ISNULL((p.permission_id & dps.permission_id),0) AS BIT)
FROM 		new_doc_permission_sets dps
RIGHT JOIN	permissions p
							ON	(p.permission_id & dps.permission_id) > 0
							AND	dps.meta_id = @meta_id
							AND	dps.set_id = @set_id
							AND	p.lang_prefix = @lang_prefix

UNION
SELECT	dp.permission_id AS p_id, dp.description,CAST(ISNULL((dp.permission_id & dps.permission_id),0) AS BIT)
FROM 		meta m
JOIN		doc_permissions dp
							ON	dp.doc_type = m.doc_type
							AND	m.meta_id = @meta_id
							AND	dp.lang_prefix = @lang_prefix
LEFT JOIN	new_doc_permission_sets dps
							ON	(dp.permission_id & dps.permission_id) > 0
							AND	dps.set_id = @set_id
							AND	dps.meta_id = m.meta_id
go

--
-- Procedure Create
-- dbo.GetNoOfTemplates
--
CREATE PROCEDURE GetNoOfTemplates AS
select count(*) from templates
go

--
-- Procedure Create
-- dbo.GetPermissionSet
--
CREATE PROCEDURE [GetPermissionSet] @meta_id INT, @set_id INT, @lang_prefix VARCHAR(3) AS


/*
	Nice little query that returns which permissions a permissionset consists of.

	Column 1:	The id of the permission
	Column 2:	The description of the permission
	Column 3:	Wether the permission is set. 0 or 1.
*/

SELECT	p.permission_id AS p_id, p.description,CAST(ISNULL((p.permission_id & dps.permission_id),0) AS BIT)
FROM 		doc_permission_sets dps
RIGHT JOIN	permissions p
							ON	(p.permission_id & dps.permission_id) > 0
							AND	dps.meta_id = @meta_id
							AND	dps.set_id = @set_id
							AND	p.lang_prefix = @lang_prefix

UNION
SELECT	dp.permission_id AS p_id, dp.description,CAST(ISNULL((dp.permission_id & dps.permission_id),0) AS BIT)
FROM 		meta m
JOIN		doc_permissions dp
							ON	dp.doc_type = m.doc_type
							AND	m.meta_id = @meta_id
							AND	dp.lang_prefix = @lang_prefix
LEFT JOIN	doc_permission_sets dps
							ON	(dp.permission_id & dps.permission_id) > 0
							AND	dps.set_id = @set_id
							AND	dps.meta_id = m.meta_id
go

--
-- Procedure Create
-- dbo.GetRolesDocPermissions
--
CREATE PROCEDURE GetRolesDocPermissions @meta_id INT AS

/*	Selects all roles except for superadmin, and returns the permissionset each has for the document.	*/

SELECT
		r.role_id,
		r.role_name,
		ISNULL(rr.set_id,4)
FROM
		roles_rights rr 
RIGHT JOIN 
		roles r 
						ON 	rr.role_id = r.role_id
						AND	rr.meta_id = @meta_id
WHERE	r.role_id > 0
ORDER BY	role_name
go

--
-- Procedure Create
-- dbo.GetTemplateGroupsForUser
--
CREATE PROCEDURE GetTemplateGroupsForUser @meta_id INT, @user_id INT AS

/*
	Nice query that fetches all templategroups a user may use in a document,
	for easy insertion into an html-option-list, no less!
*/
SELECT	distinct group_id,group_name
FROM 		templategroups dt
JOIN		user_roles_crossref urc
							ON	urc.user_id = @user_id
LEFT JOIN	roles_rights rr
							ON	rr.meta_id = @meta_id
							AND	rr.role_id = urc.role_id
LEFT JOIN	doc_permission_sets dps
							ON	dps.meta_id = rr.meta_id
							AND	dps.set_id = rr.set_id
LEFT JOIN	doc_permission_sets_ex dpse
							ON	dpse.permission_data = dt.group_id
							AND	(dpse.permission_id & dps.permission_id) > 0
							AND	dpse.meta_id = rr.meta_id
							AND	dpse.set_id = rr.set_id
							AND	dpse.permission_id = 524288 -- Change template
WHERE
								dpse.permission_data IS NOT NULL
							OR	rr.set_id = 0
							OR	urc.role_id = 0
ORDER BY	dt.group_id
go

--
-- Procedure Create
-- dbo.GetTemplateGroupsWithNewPermissions
--
CREATE PROCEDURE GetTemplateGroupsWithNewPermissions @meta_id INT, @set_id INT AS

/*
	Retrieves a list of all templategroups, with a indicator of wether a particular permission-set may use it.
	The permission-set must still have the "Change template"-permission set, though. ( Not checked in this proc )

	Column 1:	The templategroup
	Column 2:	The name of the templategroup
	Column 3:	> -1 if this set_id may use this.
*/

SELECT	group_id,group_name,ISNULL(dpse.permission_data,-1)
FROM 		templategroups tg
LEFT JOIN	new_doc_permission_sets_ex dpse
							ON	dpse.permission_data = tg.group_id
							AND	dpse.meta_id = @meta_id
							AND	dpse.set_id = @set_id
							AND	dpse.permission_id = 524288
ORDER		BY CAST(ISNULL(dpse.permission_data,-1)+1  AS BIT) DESC, group_name
go

--
-- Procedure Create
-- dbo.GetTemplateGroupsWithPermissions
--
CREATE PROCEDURE GetTemplateGroupsWithPermissions @meta_id INT, @set_id INT AS

/*
	Retrieves a list of all templategroups, with a indicator of wether a particular permission-set may use it.
	The permission-set must still have the "Change template"-permission set, though. ( Not checked in this proc )

	Column 1:	The templategroup
	Column 2:	The name of the templategroup
	Column 3:	> -1 if this set_id may use this.
*/

SELECT	group_id,group_name,ISNULL(dpse.permission_data,-1)
FROM 		templategroups tg
LEFT JOIN	doc_permission_sets_ex dpse
							ON	dpse.permission_data = tg.group_id
							AND	dpse.meta_id = @meta_id
							AND	dpse.set_id = @set_id
							AND	dpse.permission_id = 524288
ORDER		BY CAST(ISNULL(dpse.permission_data,-1)+1  AS BIT) DESC, group_name
go

--
-- Procedure Create
-- dbo.GetTextDocData
--
CREATE PROCEDURE GetTextDocData @meta_id INT AS

SELECT	t.template_id, simple_name, sort_order, t.group_id
FROM 		text_docs t 	
JOIN 		templates c 
					ON t.template_id = c.template_id
WHERE meta_id = @meta_id
go

--
-- Procedure Create
-- dbo.GetTexts
--
CREATE PROCEDURE GetTexts
@meta_id int AS

select '#txt'+convert(varchar(5), name)+'#',name,type,text from texts where meta_id = @meta_id
go

--
-- Procedure Create
-- dbo.GetUserCreateDate
--
CREATE PROCEDURE GetUserCreateDate
/*
 Returns the date when the user was created in the system
Used by servlet AdminUserProps
*/
	@userId int
AS

DECLARE @retVal smalldatetime
SELECT @retVal = create_date
FROM users
WHERE users.user_id = @userId

-- Lets validate for null
-- SELECT @retVal = ISNULL(  @retVal , '' )
---SELECT @retVal AS 'TemplateId'
SELECT @retVal  AS 'Usercreatedate'
go

--
-- Procedure Create
-- dbo.GetUserId
--
CREATE PROCEDURE GetUserId 
 @aUserId int
AS
 SELECT user_id 
 FROM users
 WHERE user_id  = @aUserId
go

--
-- Procedure Create
-- dbo.GetUserIdFromName
--
CREATE PROCEDURE [GetUserIdFromName] 
/*
Used by the conferences loginfunction, to detect a users userid from
the username
*/
	@userName char(15),
	@userPwd char(15)

AS

SELECT  u.user_id 
FROM users u
WHERE u.login_name = @userName
AND u.login_password = @userPwd
go

--
-- Procedure Create
-- dbo.GetUserInfo
--
CREATE PROCEDURE GetUserInfo
/* Returns all the information about a user. Used by adminsystem & conference system
*/

 @aUserId int
AS
 SELECT * 
 FROM users
 WHERE user_id = @aUserId
go

--
-- Procedure Create
-- dbo.GetUserNames
--
CREATE PROCEDURE GetUserNames
/* 
This procedure is used to retrieve a users full name (first name + last name
concateneted.
*/
	@user_id int,
	@what int
AS
	DECLARE @returnVal char(25)

IF(@what = 1) BEGIN
	SELECT @returnVal = RTRIM(first_name) 
	FROM users
	WHERE users.user_id = @user_id 
END ELSE BEGIN  
	SELECT @returnVal =  RTRIM(last_name) 
	FROM users
	WHERE users.user_id = @user_id 
END

SELECT @returnVal =  ISNULL(@returnVal, -1) 
SELECT @returnVal AS 'UserName'
go

--
-- Procedure Create
-- dbo.GetUserPassword
--
CREATE PROCEDURE GetUserPassword 
/* Used by AdminUserProps servlet to retrieve the users password 
*/
	@user_id int
AS

DECLARE @retVal char(15)
SELECT @retVal  = login_password 
FROM USERS
WHERE user_id = @user_id

SELECT @retVal =  ISNULL(@retVal , '') 
SELECT @retVal AS 'Password'
go

--
-- Procedure Create
-- dbo.GetUserPermissionSet
--
CREATE PROCEDURE GetUserPermissionSet @meta_id INT, @user_id INT AS

/*
	Finds out what is the most privileged permission_set a user has for a document.

	Column 1:	The users most privileged set_id
	Column 2:	The users permission-set for this set_id
	Column 3:	The permissions for this document. ( At the time of this writing, the only permission there is is wether or not set_id 1 is more privileged than set_id 2, and it's stored in bit 0 )

	set_id's:

	0 - most privileged (full rights)
	1 & 2 - misc. They may be equal, and 1 may have permission to modify 2.
	3 - only read rights
	4 - least privileged (no rights)
*/

SELECT TOP 1	ISNULL((MIN(ISNULL(rr.set_id,4))*CAST(MIN(ISNULL(urc.role_id,1)) AS BIT)),4),
		ISNULL(dps.permission_id,0),
		ISNULL(m.permissions,0)
FROM 		roles_rights rr
RIGHT JOIN 	user_roles_crossref urc
						ON	urc.user_id = @user_id
						AND	rr.meta_id = @meta_id
						AND	(
								rr.role_id = urc.role_id
							OR	urc.role_id < 1
							)						
JOIN		meta m
						ON	m.meta_id = @meta_id
						AND	urc.user_id = @user_id
						AND	(
								rr.meta_id = @meta_id
							OR	urc.role_id = 0
							)
LEFT JOIN	doc_permission_sets dps
						ON	dps.meta_id = @meta_id
						AND	rr.set_id = dps.set_id
GROUP BY	ISNULL(dps.permission_id,0),m.permissions
ORDER BY	ISNULL((MIN(ISNULL(rr.set_id,4))*CAST(MIN(ISNULL(urc.role_id,1)) AS BIT)),4)
go

--
-- Procedure Create
-- dbo.GetUserPermissionSetEx
--
CREATE PROCEDURE GetUserPermissionSetEx @meta_id INT, @user_id INT AS

/*
	Finds out what extended permissions (extra permissiondata) the user has for this document.
	Does not return correct data for a superadmin, or full admin, so check that first.
*/
SELECT	dps.permission_id, dps.permission_data
FROM 		roles_rights rr
JOIN 		user_roles_crossref urc
						ON	urc.user_id = @user_id
						AND	rr.role_id = urc.role_id
JOIN		meta m
						ON	m.meta_id = @meta_id
						AND	rr.meta_id = m.meta_id
JOIN		doc_permission_sets_ex dps
						ON	dps.meta_id = m.meta_id
						AND	rr.set_id = dps.set_id
go

--
-- Procedure Create
-- dbo.GetUserPhoneNumbers
--
CREATE PROCEDURE GetUserPhoneNumbers
/*
Returns a users phonenumbers. Used by AdminUserProps servlet
*/
	@user_id int

AS

-- The new version which includes phones
SELECT p.phone_id, p.country_code , p.area_code , p.number, p.user_id 
FROM users u , phones p 
WHERE u.user_id = p.user_id
AND u.user_id = @user_id
go

--
-- Procedure Create
-- dbo.GetUserPhones
--
CREATE PROCEDURE GetUserPhones
	@user_id int
AS
/*
Used to generate a list with all type of users. Used from AdminUserProps
*/

SELECT p.phone_id, RTRIM(p.country_code) + ' ' + RTRIM(p.area_code) + ' ' + RTRIM(p.number) as numbers
FROM users u , phones p 
WHERE u.user_id = p.user_id
AND u.user_id = @user_id
go

--
-- Procedure Extended Alter
-- dbo.GetUserRoles
--
EXEC sp_rename 'dbo.GetUserRoles','GetUserRol_12142000071836000',OBJECT
go
CREATE PROCEDURE GetUserRoles
/*
Used to get all roles for a user
*/

 @aUserId int
 AS
 SELECT role_name 
 FROM roles,user_roles_crossref 
 WHERE roles.role_id = user_roles_crossref.role_id
  AND user_roles_crossref.user_id = @aUserId
go
IF OBJECT_ID('dbo.GetUserRoles') IS NOT NULL
     DROP PROCEDURE dbo.GetUserRol_12142000071836000
ELSE 
     EXEC sp_rename 'dbo.GetUserRol_12142000071836000','GetUserRoles',OBJECT
go

--
-- Procedure Create
-- dbo.GetUserRolesDocPermissions
--
CREATE PROCEDURE GetUserRolesDocPermissions @meta_id INT, @user_id INT AS

SELECT
		r.role_id,
		r.role_name,
		ISNULL(rr.set_id,4),
		ISNULL(urc.role_id,0)
FROM
		roles_rights rr 
RIGHT JOIN 
		roles r 
						ON 	rr.role_id = r.role_id
						AND	rr.meta_id = @meta_id
LEFT JOIN	user_roles_crossref urc
						ON	r.role_id = urc.role_id
						AND	urc.user_id = @user_id
WHERE	r.role_id > 0
ORDER BY	role_name
go

--
-- Procedure Create
-- dbo.GetUserRolesIds
--
CREATE PROCEDURE GetUserRolesIds
/* Returns the roles id:s for a user 
*/
 @aUserId int
 AS
 SELECT roles.role_id, role_name 
 FROM roles, user_roles_crossref 
 WHERE roles.role_id = user_roles_crossref.role_id
  AND user_roles_crossref.user_id = @aUserId
go

--
-- Procedure Create
-- dbo.GetUserType
--
CREATE PROCEDURE GetUserType
/*
Used to get a users usertype. used from adminuser
*/
	@User_id int
 AS
DECLARE @returnVal int
SELECT DISTINCT @returnVal =  user_type  
FROM users
WHERE user_id = @User_id


SELECT @returnVal =  ISNULL(@returnVal, 1) 
SELECT @returnVal AS 'Usertype'
go

--
-- Procedure Create
-- dbo.GetUserTypes
--
CREATE PROCEDURE GetUserTypes
/*
Used to generate a list with all type of users. Used from AdminUserProps
*/

 AS
 SELECT DISTINCT user_type, type_name 
 FROM user_types
go

--
-- Procedure Create
-- dbo.GetUsersWhoBelongsToRole
--
CREATE PROCEDURE GetUsersWhoBelongsToRole @role_id int AS

/*
 * select user who belongs to role role_id
*/
select us.user_id, u.last_name + ', ' + u.first_name 

from user_roles_crossref us

join users u
		on us.user_id = u.user_id

where role_id = @role_id

order by  last_name
go

--
-- Procedure Create
-- dbo.IMC_AddChild
--
CREATE PROCEDURE IMC_AddChild 
	@meta_id int , 
	@newMetaId int ,
	@doc_menu_no int,
	@newSortNo int
AS
INSERT INTO childs(meta_id,to_meta_id,menu_sort,manual_sort_order)
VALUES ( @meta_id , @newMetaId , @doc_menu_no , @newSortNo )
go

--
-- Procedure Create
-- dbo.IMC_AddImage
--
CREATE PROCEDURE IMC_AddImage
	@newMetaId int ,
	@t int
 AS
/*
INSERT INTO images( meta_id, width, height, border, v_space, h_space,
	name, image_name,target,target_name,align,alt_text,low_scr,imgurl,linkurl)
VALUES ( @newMetaId, 0, 0, 0, 0, 0, @t, '_self' ,  '_top', '' , '' )	
*/
go

--
-- Procedure Create
-- dbo.IMC_AddImageRef
--
CREATE PROCEDURE IMC_AddImageRef
	@newMetaId int ,
	@t int
 AS

-- add imageref to database     	
INSERT INTO images (meta_id, width, height, border, v_space, h_space,
name,image_name,target,target_name,align,alt_text,low_scr,imgurl,linkurl)
VALUES ( @newMetaId, 0, 0, 0, 0, 0,  @t, '',  '_self' , '',  '_top',  '','' ,'' , '' )
go

--
-- Procedure Create
-- dbo.IMC_AddOwnerRights
--
CREATE PROCEDURE IMC_AddOwnerRights
	@metaId int,
	@userId int
AS

INSERT INTO user_rights
VALUES ( @userId , @metaId , 99 )
go

--
-- Procedure Create
-- dbo.IMC_AddRole
--
CREATE PROCEDURE IMC_AddRole 
/*
 Lets detect if we should add a read / or a write option
*/
	@metaId int,
	@aRole int,
	@typeOfRole intAS

IF( @typeOfRole = 1) BEGIN
	-- Lets insert a read
	INSERT INTO roles_rights
	VALUES( @aRole ,  @metaId ,@typeOfRole )
END 

IF( @typeOfRole = 3 ) BEGIN
	-- WRITE
	INSERT INTO roles_rights
	VALUES ( @aRole , @metaId , @typeOfRole)
END
go

--
-- Procedure Create
-- dbo.IMC_AddTextDoc
--
CREATE PROCEDURE IMC_AddTextDoc
	@meta_id int ,
	@template_id int ,
	@sort_order int
AS
INSERT INTO text_docs ( meta_id,template_id,sort_order )
VALUES ( @meta_id , @template_id , @sort_order )
go

--
-- Procedure Create
-- dbo.IMC_AddTexts
--
CREATE PROCEDURE IMC_AddTexts
/*
Adds a new texttype  to the texts table
*/
	@newMetaId int ,
	@name int ,
	@text text
 AS

-- add texts to database     	
INSERT INTO texts(meta_id,name,text,type)
VALUES ( @newMetaId , @name , @text , 1 )
go

--
-- Procedure Create
-- dbo.IMC_AddUserRights
--
CREATE PROCEDURE IMC_AddUserRights
	@metaId int,
	@userRight int,
	@typeOfRight int
AS


-- READ
IF( @typeOfRight = 1 ) BEGIN
	INSERT INTO user_rights
	VALUES ( @userRight , @metaId , @typeOfRight ) 
END

-- WRITE
IF( @typeOfRight = 3 ) BEGIN
	INSERT INTO user_rights
	VALUES ( @userRight , @metaId , @typeOfRight ) 
END
go

--
-- Procedure Create
-- dbo.IMC_CheckMenuSort
--
CREATE PROCEDURE IMC_CheckMenuSort
	@meta_id int,
	@doc_menu_no int
AS

-- test if this is the first child with this  menusort
SELECT to_meta_id 
FROM childs
WHERE meta_id =  @meta_id 
AND menu_sort = @doc_menu_no
go

--
-- Procedure Create
-- dbo.IMC_CreateNewMeta
--
CREATE PROCEDURE IMC_CreateNewMeta
/* create new metadata */
@new_meta_id int,
@description varchar(80),
@doc_type int,
@meta_head_line varchar(255),
@meta_text varchar(1000),
@meta_image varchar(255),
@category_id int,
@processing_id int,
@shared int,
@expanded int,
@show_meta int,
@help_text_id int, /* help_text_id = 1 */
@archived int,
@status_id int,   /* status_id  = 1 */
@lang_prefix varchar(3),
@classification varchar(20),
@date_created datetime,
@date_modified datetime,
@sort_position int,
@menu_position int,
@disable_search int,
@activated_date varchar(10),
@activated_time varchar(6),
@archived_date varchar(10),
@archived_time varchar(6),
@target varchar(10),
@frame_name varchar(20),
@activate int

 AS

insert into meta
values(@new_meta_id ,@description,@doc_type,@meta_head_line,@meta_text,@meta_image, @category_id,
@processing_id,@shared,@expanded,@help_text_id,@show_meta,@archived,@status_id,@lang_prefix,@classification,@date_created,
@date_modified, @sort_position,@menu_position,@disable_search,@activated_date,@activated_time,@archived_date,
@archived_time,@target,@frame_name,@activate)
go

--
-- Procedure Create
-- dbo.IMC_ExecuteExample
--
CREATE PROCEDURE [IMC_ExecuteExample] AS

-- Lets create the templates library path as well
EXEC AddNewTemplateLib 1
go

--
-- Procedure Create
-- dbo.IMC_GetMaxMetaID
--
CREATE PROCEDURE IMC_GetMaxMetaID AS

/* get max meta id */

select max(meta_id) from meta
go

--
-- Procedure Create
-- dbo.IMC_GetNbrOfText
--
CREATE PROCEDURE IMC_GetNbrOfText
	@meta_id int 
 AS

-- find no_of_txt for the template
SELECT no_of_txt,no_of_img,no_of_url 
FROM text_docs,templates
WHERE meta_id = @meta_id
AND templates.template_id = text_docs.template_id ;
go

--
-- Procedure Create
-- dbo.IMC_GetSortOrderNum
--
CREATE PROCEDURE IMC_GetSortOrderNum
/* 
 Returns the highest sortOrderNumber
*/
	@meta_id int,
	@doc_menu_no int
 AS

-- update child table
SELECT MAX(manual_sort_order) 
FROM childs
WHERE meta_id = @meta_id 
AND menu_sort = @doc_menu_no
go

--
-- Procedure Create
-- dbo.IMC_GetTemplateId
--
CREATE PROCEDURE IMC_GetTemplateId
	@meta_id int
AS

SELECT template_id
FROM text_docs
WHERE meta_id = @meta_id
go

--
-- Procedure Create
-- dbo.IPAccessAdd
--
CREATE PROCEDURE IPAccessAdd
/*
This function adds a new ip-access to the db. Used by AdminManager
*/

	@user_id int,
	@ip_start DECIMAL , 
	@ip_end DECIMAL
AS

INSERT INTO IP_ACCESSES ( user_id , ip_start , ip_end )
VALUES ( @user_id , @ip_start , @ip_end )
go

--
-- Procedure Create
-- dbo.IPAccessDelete
--
CREATE PROCEDURE IPAccessDelete
/*
 Deletes an Ip-access for a user. Used by the AdminIpAccess servlet
*/

	@ipAccessId int
AS

DELETE FROM IP_ACCESSES 
WHERE ip_access_id = @ipAccessId
go

--
-- Procedure Create
-- dbo.IPAccessUpdate
--
CREATE PROCEDURE IPAccessUpdate
/*
Updates the IPaccess table
*/
	@IpAccessId int ,
	@newUserId int,
	@newIpStart DECIMAL ,
	@newIpEnd DECIMAL 

AS

UPDATE IP_ACCESSES
SET user_id = @newUserId ,
 ip_start = @newIpStart,
 ip_end = @newIpEnd
WHERE ip_access_id = @IpAccessId
go

--
-- Procedure Create
-- dbo.IPAccessesGetAll
--
CREATE PROCEDURE IPAccessesGetAll AS
/*
Lets get all IPaccesses from db. Used  by the AdminIpAccesses
*/
SELECT ip.ip_access_id, ip.user_id, usr.login_name, ip.ip_start, ip.ip_end    
FROM IP_ACCESSES ip, USERS usr
WHERE ip.user_id = usr.user_id
go

--
-- Procedure Create
-- dbo.IncSessionCounter
--
CREATE PROCEDURE IncSessionCounter 
AS
      
    DECLARE @current_value int
  select @current_value = (select value from sys_data where type_id = 1)
  set @current_value  =  @current_value +1
 update sys_data
 set value = @current_value where type_id = 1
 
  return
go

--
-- Procedure Create
-- dbo.InheritPermissions
--
CREATE PROCEDURE InheritPermissions @new_meta_id INT, @parent_meta_id INT, @doc_type INT AS

INSERT INTO	doc_permission_sets
SELECT 	@new_meta_id,
		ndps.set_id,
		ndps.permission_id | (ISNULL(CAST(permission_data AS BIT),0) * 65536)
FROM 		new_doc_permission_sets ndps
LEFT JOIN 	new_doc_permission_sets_ex ndpse	ON	ndps.meta_id = ndpse.meta_id
							AND	ndps.set_id = ndpse.set_id
							AND	ndpse.permission_id = 8
							AND	ndpse.permission_data = @doc_type
							AND	@doc_type != 2
WHERE	ndps.meta_id = @parent_meta_id
GROUP BY	ndps.meta_id,
		ndps.set_id,
		ndps.permission_id,
		ndpse.permission_id,
		ndpse.permission_data

INSERT INTO	doc_permission_sets_ex
SELECT	@new_meta_id,
		ndpse.set_id,
		ndpse.permission_id,
		ndpse.permission_data
FROM		new_doc_permission_sets_ex ndpse
WHERE	ndpse.meta_id = @parent_meta_id
	AND	@doc_type = 2

INSERT INTO	new_doc_permission_sets
SELECT	@new_meta_id,
		ndps.set_id,
		ndps.permission_id
FROM		new_doc_permission_sets ndps
WHERE	ndps.meta_id = @parent_meta_id
	AND	@doc_type = 2

INSERT INTO	new_doc_permission_sets_ex
SELECT	@new_meta_id,
		ndpse.set_id,
		ndpse.permission_id,
		ndpse.permission_data
FROM		new_doc_permission_sets_ex ndpse
WHERE	ndpse.meta_id = @parent_meta_id
	AND	@doc_type = 2

INSERT INTO	roles_rights
SELECT	role_id, @new_meta_id, set_id
FROM		roles_rights
WHERE	meta_id = @parent_meta_id
go

--
-- Procedure Create
-- dbo.ListConferences
--
CREATE PROCEDURE ListConferences AS

select meta_id, meta_headline 
from meta 
where doc_type = 102
go

--
-- Procedure Create
-- dbo.ListDocsByDate
--
CREATE PROCEDURE ListDocsByDate @listMod int,  @doc_type int, @startDate varchar(10), @endDate varchar(20) AS
/*
 lists doctyps where activate = 1
 @listMod 0 = all date, 1 = only creatdat, 2 = only modifieddata
 @startDoc yyyy-mm-dd or 0 then not set
 @endDate yyyy-mm-dd or 0 then not set
*/

-- Listdate fix 
if ( @endDate <> '0') BEGIN
	SET @endDate = @endDate + ' 23:59:59'
	PRINT @endDate
END 

/* list all (not in use ) */
if ( @listMod = 0) begin
	if ( @startDate = '0' ) begin
		if ( @endDate = '0' ) begin
			select m.meta_id, dt.type, m.meta_headline, m.date_modified
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and activate = 1

			order by m.date_modified
		end
		else begin
			select m.meta_id, dt.type, m.meta_headline, m.date_modified
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and m.date_modified <= @endDate and activate = 1

			order by m.date_modified
		end
	end
	else if ( @startDate != '0' ) begin
		if ( @endDate = '0' ) begin
			select m.meta_id, dt.type, m.meta_headline, m.date_modified
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and m.date_modified >= @startDate and activate = 1

			order by m.date_modified
		end
		else
		begin
			select m.meta_id, dt.type, m.meta_headline, m.date_modified
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and m.date_modified <= @endDate and m.date_modified >= @startDate and activate = 1

			order by m.date_modified
		end
	end
end

/* list creatdate */
else if ( @listMod = 1) begin
	if ( @startDate = '0' ) begin
		if ( @endDate = '0' ) begin
			select m.meta_id, dt.type, m.meta_headline, m.date_created
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and activate = 1

			order by m.date_created
		end
		else begin
			select m.meta_id, dt.type, m.meta_headline, m.date_created
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and m.date_created <= @endDate and activate = 1

			order by m.date_created
		end
	end
	else if ( @startDate != '0' ) begin
		if ( @endDate = '0' ) begin
			select m.meta_id, dt.type, m.meta_headline, m.date_created
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and m.date_created >= @startDate and activate = 1

			order by m.date_created
		end
		else
		begin
			select m.meta_id, dt.type, m.meta_headline, m.date_created
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and m.date_created <= @endDate and m.date_created >= @startDate and activate = 1

			order by m.date_created
		end
	end
end
/* list only modified*/
else begin
	if ( @startDate = '0' ) begin
		if ( @endDate = '0' ) begin
			select m.meta_id, dt.type, m.meta_headline, m.date_modified
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and activate = 1 and m.date_modified != m.date_created

			order by m.date_modified
		end
		else begin
			select m.meta_id, dt.type, m.meta_headline, m.date_modified
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and m.date_modified <= @endDate and activate = 1 and m.date_modified != m.date_created

			order by m.date_modified
		end
	end
	else if ( @startDate != '0' ) begin
		if ( @endDate = '0' ) begin
			select m.meta_id, dt.type, m.meta_headline, m.date_modified
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and m.date_modified >= @startDate and activate = 1 and m.date_modified != m.date_created

			order by m.date_modified
		end
		else
		begin
			select m.meta_id, dt.type, m.meta_headline, m.date_modified
	
			from meta m

			join doc_types dt
			 		on m.doc_type = dt.doc_type
			where m.doc_type = @doc_type and m.date_modified <= @endDate and m.date_modified >= @startDate and activate = 1 and m.date_modified != m.date_created

			order by m.date_modified
		end
	end
end
go

--
-- Procedure Create
-- dbo.ListDocsGetInternalDocTypes
--
CREATE PROCEDURE ListDocsGetInternalDocTypes AS

/* selct all internal doc types */
select doc_type, type 

from doc_types

where doc_type <= 100
go

--
-- Procedure Create
-- dbo.ListDocsGetInternalDocTypesValue
--
CREATE PROCEDURE ListDocsGetInternalDocTypesValue AS

/* selct all internal doc types */
select doc_type

from doc_types

where doc_type <= 100
go

--
-- Procedure Create
-- dbo.PermissionsGetPermission
--
CREATE PROCEDURE PermissionsGetPermission @login_name varchar(15), @permission int AS
/*

*/
select login_password, first_name, last_name, email, min(permissions&@permission), lang_prefix 
from users u 
join lang_prefixes lp 
				on u.lang_id = lp.lang_id 
join user_roles_crossref urc 
				on u.user_id = urc.user_id left 
join roles r 
				on r.role_id = urc.role_id
where login_name = @login_name
group by login_password, first_name, last_name, email, lang_prefix
go

--
-- Procedure Create
-- dbo.PhoneNbrDelete
--
CREATE PROCEDURE PhoneNbrDelete
/*
 Deletes an Ip-access for a user. Used by the PhoneNbrDelete
*/

	@phoneId int
AS

DELETE FROM PHONES 
WHERE phone_id = @phoneId
go

--
-- Procedure Create
-- dbo.PhoneNbrUpdate
--
CREATE PROCEDURE PhoneNbrUpdate 
/*
This function adds a new phone numbers to the db. Used by AdminUserPhones
*/
	@user_id int,
	@phone_id int,
	@country varchar(15) ,
	@area varchar(15) , 
	@nbr varchar(15)
AS

UPDATE phones
	SET country_code = @country,
	area_code = @area ,
	number = @nbr
WHERE phones.user_id = @user_id
AND phones.phone_id = @phone_id
go

--
-- Procedure Create
-- dbo.RemoveUserFromRole
--
CREATE PROCEDURE RemoveUserFromRole
	@userId int, @role_id int
AS

/* removes user from role */
DELETE 
FROM user_roles_crossref
WHERE user_id = @userId and role_id = @role_id
go

--
-- Procedure Create
-- dbo.RoleAddNew
--
CREATE PROCEDURE RoleAddNew
	@newRoleName char(25)
/* Adds a new role */
AS
DECLARE @newRoleId int
SELECT @newRoleId = MAX(r.role_id) + 1
FROM roles r

INSERT INTO roles (  role_id , role_name )
VALUES( @newRoleId , @newRoleName )
go

--
-- Procedure Create
-- dbo.RoleAdminGetAll
--
CREATE PROCEDURE RoleAdminGetAll AS
/*
 Used by AdminRoles servlet to retrieve all roles except the Superadmin role
*/
SELECT role_id , role_name FROM ROLES
WHERE role_id != 0
ORDER BY role_name
go

--
-- Procedure Create
-- dbo.RoleCount
--
CREATE PROCEDURE RoleCount
	@aRoleId int
AS
/*
 This function is used when an administrator tries to delete a role.
 This function counts in how many documents the role is used
*/
DECLARE @returnVal int
SELECT  @returnVal = COUNT(  r.role_id ) 
FROM ROLES_RIGHTS r
WHERE ROLE_ID = @aRoleId

-- Lets validate for null
SELECT @returnVal = ISNULL(  @returnVal , 0 )
SELECT @returnVal AS 'Number_of_roles'
go

--
-- Procedure Create
-- dbo.RoleCountAffectedUsers
--
CREATE PROCEDURE RoleCountAffectedUsers
	@aRoleId int
AS
/*
 This function is used when an administrator tries to delete a role.
 This function counts how many users who will be affected
*/

SELECT  DISTINCT COUNT(usr.role_id )
FROM user_roles_crossref usr, roles r, users u
WHERE usr.ROLE_ID = @aRoleId 
AND usr.user_id = u.user_id
go

--
-- Procedure Create
-- dbo.RoleDelete
--
CREATE PROCEDURE RoleDelete
/* Deletes an role from the role table. Used by the AdminRoles servlet
*/
	@roleId int
AS

DELETE FROM ROLES_RIGHTS WHERE ROLE_ID = @roleId
DELETE FROM user_roles_crossref WHERE ROLE_ID =@roleId
DELETE FROM ROLES WHERE ROLE_ID = @roleId
go

--
-- Procedure Create
-- dbo.RoleDeleteViewAffectedMetaIds
--
CREATE PROCEDURE RoleDeleteViewAffectedMetaIds
	@aRoleId int
AS
/*
 This function is used when an administrator tries to delete a role.
 All metaids where the role is used will be presenteted in i list
*/

SELECT  TOP 50 r.meta_id , r.meta_id
FROM ROLES_RIGHTS r
WHERE ROLE_ID = @aRoleId

-- Lets validate for null
--SELECT @returnVal = ISNULL(  @returnVal , -1 )
--SELECT @returnVal AS 'FoundRoleName'
go

--
-- Procedure Create
-- dbo.RoleDeleteViewAffectedUsers
--
CREATE PROCEDURE RoleDeleteViewAffectedUsers
	@aRoleId int
AS
/*
 This function is used when an administrator tries to delete a role.
 All users which will be affected of the deletion will be presenteted in a list
*/

SELECT distinct TOP 50  usr.role_id , (RTRIM(last_name) + ', ' + RTRIM(first_name))
FROM user_roles_crossref usr, roles r, users u
WHERE usr.ROLE_ID = @aRoleId 
AND usr.user_id = u.user_id
--GROUP BY (RTRIM(last_name) + ', ' + RTRIM(first_name)), usr.role_id
ORDER BY (RTRIM(last_name) + ', ' + RTRIM(first_name))
go

--
-- Procedure Create
-- dbo.RoleFindName
--
CREATE PROCEDURE RoleFindName
	@newRoleName char(25)
AS
/*
This function is when an administrator tries to add a new roleName.  
The system searches for the rolename and returns the the id it exists otherwize -1
*/
DECLARE @returnVal int
SELECT  @returnVal = r.role_id
FROM roles r
WHERE r.role_name = @newRoleName

-- Lets validate for null
SELECT @returnVal = ISNULL(  @returnVal , -1 )
SELECT @returnVal AS 'FoundRoleName'
go

--
-- Procedure Create
-- dbo.RoleGetAllApartFromRole
--
CREATE PROCEDURE RoleGetAllApartFromRole @role_id int AS
/*
 Used by AdminRoleBelongings servlet to retrieve all roles except the Superadmin role and role role_id
*/
SELECT role_id , role_name FROM ROLES
WHERE role_id != 0 and role_id != @role_id
ORDER BY role_id
go

--
-- Procedure Create
-- dbo.RoleGetName
--
CREATE PROCEDURE RoleGetName
	@roleId int
AS
/*
This function is when an administrator tries to add a new roleName.  
The system searches for the rolename and returns the the id it exists otherwize -1
*/
DECLARE @returnStr char(25)
SELECT  @returnStr = r.role_name
FROM roles r
WHERE r.role_id = @roleId

-- Lets validate for null
SELECT @returnStr = ISNULL(  @returnStr , '---' )
SELECT @returnStr AS 'Rolename'
go

--
-- Procedure Create
-- dbo.RoleGetPermissionsByLanguage
--
CREATE PROCEDURE RoleGetPermissionsByLanguage @lang_prefix varchar(3) AS
/*select permissions by language prefix.*/
select	permission_id, description

from	roles_permissions 

where	lang_prefix = @lang_prefix

order by permission_id
go

--
-- Procedure Create
-- dbo.RoleGetPermissionsFromRole
--
CREATE PROCEDURE RoleGetPermissionsFromRole @role_id int, @lang_prefix varchar(3) AS

/*
  select rolepermission from role id
*/
SELECT		ISNULL(r.permissions & rp.permission_id,0) AS value,rp.permission_id,rp.description
FROM			roles_permissions rp
LEFT JOIN		roles r
					ON	rp.permission_id & r.permissions != 0
					AND	r.role_id = @role_id
WHERE lang_prefix = @lang_prefix
go

--
-- Procedure Create
-- dbo.RolePermissionsAddNew
--
CREATE PROCEDURE RolePermissionsAddNew
	@newRoleName char(25), @permissions int
/* Adds a new role */
AS
DECLARE @newRoleId int
SELECT @newRoleId = MAX(r.role_id) + 1
FROM roles r

INSERT INTO roles (  role_id , role_name, permissions )
VALUES( @newRoleId , @newRoleName, @permissions )
go

--
-- Procedure Create
-- dbo.RoleUpdateName
--
CREATE PROCEDURE RoleUpdateName
/*
Updates the name on a role in the db
*/
	@role_id int,
	@newRole_name char(25)
AS

UPDATE ROLES
SET role_name = @newRole_name
WHERE role_id = @role_id
go

--
-- Procedure Create
-- dbo.RoleUpdatePermissions
--
CREATE PROCEDURE RoleUpdatePermissions @role_id int,  @permissions int AS

/* update permissions for role */
update roles 

Set permissions = @permissions 

where role_id = @role_id
go

--
-- Procedure Create
-- dbo.SetDocPermissionSet
--
CREATE PROCEDURE SetDocPermissionSet @meta_id INT, @set_id INT, @permission_id INT AS

/*
	Updates a permissionset for a document.
*/

-- Delete the previous value
DELETE FROM doc_permission_sets
WHERE	meta_id = @meta_id
AND		set_id = @set_id

-- Insert new value
INSERT INTO	doc_permission_sets
VALUES	(@meta_id,@set_id,@permission_id)
go

--
-- Procedure Create
-- dbo.SetDocPermissionSetEx
--
CREATE PROCEDURE SetDocPermissionSetEx @meta_id INT, @set_id INT, @permission_id INT, @permission_data INT AS

/*
	Updates an extended permissionset for a document.
*/

-- Insert new value
INSERT INTO	doc_permission_sets_ex
VALUES	(@meta_id,@set_id,@permission_id, @permission_data)
go

--
-- Procedure Create
-- dbo.SetNewDocPermissionSet
--
CREATE PROCEDURE SetNewDocPermissionSet @meta_id INT, @set_id INT, @permission_id INT AS

/*
	Updates a permissionset for a document.
*/

-- Delete the previous value
DELETE FROM new_doc_permission_sets
WHERE	meta_id = @meta_id
AND		set_id = @set_id

-- Insert new value
INSERT INTO	new_doc_permission_sets
VALUES	(@meta_id,@set_id,@permission_id)
go

--
-- Procedure Create
-- dbo.SetNewDocPermissionSetEx
--
CREATE PROCEDURE SetNewDocPermissionSetEx @meta_id INT, @set_id INT, @permission_id INT, @permission_data INT AS

/*
	Updates an extended permissionset for a document.
*/

-- Insert new value
INSERT INTO	new_doc_permission_sets_ex
VALUES	(@meta_id,@set_id,@permission_id, @permission_data)
go

--
-- Procedure Create
-- dbo.SetRoleDocPermissionSetId
--
CREATE PROCEDURE SetRoleDocPermissionSetId @role_id INT, @meta_id INT, @set_id INT AS

-- First delete the previous set_id
DELETE FROM 		roles_rights 
WHERE 		meta_id = @meta_id
		AND 	role_id = @role_id


-- Now insert the new one
IF @set_id < 4
BEGIN
	INSERT INTO roles_rights (role_id, meta_id, set_id)
	VALUES ( @role_id, @meta_id, @set_id )
END
go

--
-- Procedure Create
-- dbo.SetSessionCounterDate
--
CREATE PROCEDURE SetSessionCounterDate
   @new_date varchar(20)
AS
      
 update sys_data
 set value = @new_date where type_id = 2
 
  return
go

--
-- Procedure Create
-- dbo.SystemMessageGet
--
CREATE PROCEDURE SystemMessageGet AS
/*
	Used by the AdminSystemMessage servlet to retrieve the systemmessage
*/
SELECT s.value
FROM sys_data s
WHERE s.type_id = 3
go

--
-- Procedure Create
-- dbo.SystemMessageSet
--
CREATE PROCEDURE SystemMessageSet
/*
Lets update the system message table. Used by the AdminSystemMessage servlet
*/
	@newMsg varchar(80)
AS

UPDATE sys_data
SET value = @newMsg
WHERE type_id = 3
go

--
-- Procedure Create
-- dbo.TestJanusDB
--
CREATE PROCEDURE TestJanusDB

AS

select 'Hurra!'
go

--
-- Procedure Create
-- dbo.UpdateParentsDateModified
--
CREATE PROCEDURE [UpdateParentsDateModified] @meta_id INT AS

UPDATE meta
SET date_modified = GETDATE() 
FROM meta JOIN childs c
ON meta.meta_id = c.meta_id 
WHERE c.to_meta_id = @meta_id
go

--
-- Procedure Create
-- dbo.UpdateTemplateTextsAndImages
--
CREATE PROCEDURE UpdateTemplateTextsAndImages
@t_id int AS

declare @new_no_txt int
declare @new_no_img int

select @new_no_txt = no_of_txt, @new_no_img = no_of_img from templates where template_id = @t_id

declare tmp cursor for
select td.meta_id,max(t.name),max(i.name) from text_docs td
left join texts t on td.meta_id = t.meta_id
left join images i on td.meta_id = i.meta_id
where td.template_id = @t_id
group by td.meta_id
having max(t.name) < @new_no_txt
or max(i.name) < @new_no_img

open tmp
declare @meta_id int
declare @max_txt int
declare @max_img int
fetch next from tmp
into @meta_id,@max_txt,@max_img

while @@fetch_status = 0
begin
	declare @no_txt int	
	declare @no_img int
	set @no_txt = @max_txt
	set @no_img = @max_img
	while @no_txt < @new_no_txt
	begin
		set @no_txt = @no_txt + 1
		insert into texts values (@meta_id,@no_txt,'',1)
	end
	while @no_img < @new_no_img
	begin
		set @no_img = @no_img + 1
		insert into images values (@meta_id,0,0,0,0,0,@no_img,'','_self','','_top','','','','')
	end
	fetch next from tmp
	into @meta_id,@max_txt,@max_img
end

close tmp
deallocate tmp
go

--
-- Procedure Create
-- dbo.UpdateUser
--
CREATE PROCEDURE UpdateUser
/*
usertype. 0=special, 1=default, 2=conferenceuser 
*/
	@user_id int,
	@login_name char(15),
	@login_password char(15),
	@first_name char(25),
	@last_name char(30),
	@title char(30),
	@company char(30),
	@address char(40),
	@city char(30),
	@zip char (15),
	@country char(30),
	@county_council char(30),
	@email char(50),
	@admin_mode int,
	@last_page int,
	@archive_mode int,
	@lang_id int,
	@user_type int,
	@active int
AS
UPDATE users 
SET login_name = @login_name,
login_password = @login_password,
first_name = @first_name,
last_name = @last_name,
title = @title,
company = @company,
address =  @address,
city = @city,
zip = @zip,
country = @country,
county_council = @county_council,
email = @email,
user_type = @user_type,
active = @active,
lang_id = @lang_id

WHERE user_id = @User_id
go

--
-- Procedure Create
-- dbo.UserPrefsChange
--
CREATE PROCEDURE UserPrefsChange
	 @aUserId int
/*
  Returns the information for a user which he is able to change self. Observer that we
  return the password as an empty string
*/
AS


-- SELECT @aUserId AS 'TEST'
SELECT user_id, login_name,  "", "", first_name, last_name,  title, company, address, city, zip, country, county_council, email, lang_id --, profession, company
FROM users
WHERE user_id = @aUserId
go

--
-- Procedure Create
-- dbo.classification_convert
--
CREATE PROCEDURE classification_convert AS

-- Hämta alla klassificeringskoder och för över dem till nya systemet. Observera att denna inte fixar
-- de fält som har , tecken som delimiter

DECLARE @meta_id int
DECLARE @class varchar(200)
DECLARE tmpCursor CURSOR FOR
	SELECT meta_id, classification
	FROM meta
	WHERE classification IS NOT NULL
	and classification <> ''
	and classification NOT LIKE 'META NAME%'
	and classification NOT LIKE 'Test'
	--AND meta_id = 1014
OPEN tmpCursor
FETCH NEXT FROM tmpCursor INTO @meta_id, @class
WHILE @@fetch_status = 0 BEGIN
	PRINT 'Class: ' + @class 
	EXEC classification_fix @meta_id, @class 
	FETCH NEXT FROM tmpCursor INTO @meta_id, @class
END
CLOSE tmpCursor
DEALLOCATE tmpCursor
go

--
-- Procedure Extended Alter
-- dbo.dt_adduserobject
--
EXEC sp_rename 'dbo.dt_adduserobject','dt_adduser_12142000071846000',OBJECT
go
CREATE procedure dbo.dt_adduserobject
as
/*
**	Add an object to the dtproperties table
*/
	set nocount on
	/*
	** Create the user object if it does not exist already
	*/
	begin transaction
		insert dbo.dtproperties (property) VALUES ('DtgSchemaOBJECT')
		update dbo.dtproperties set objectid=@@identity 
			where id=@@identity and property='DtgSchemaOBJECT'
	commit
	return @@identity
go
IF OBJECT_ID('dbo.dt_adduserobject') IS NOT NULL
     DROP PROCEDURE dbo.dt_adduser_12142000071846000
ELSE 
     EXEC sp_rename 'dbo.dt_adduser_12142000071846000','dt_adduserobject',OBJECT
go
GRANT EXECUTE ON dbo.dt_adduserobject TO [public]
go

--
-- Procedure Extended Alter
-- dbo.dt_droppropertiesbyid
--
EXEC sp_rename 'dbo.dt_droppropertiesbyid','dt_droppro_12142000071848000',OBJECT
go
CREATE procedure dbo.dt_droppropertiesbyid
	@id int,
	@property varchar(64)
as
/*
**	Drop one or all the associated properties of an object or an attribute 
**
**	dt_dropproperties objid, null or '' -- drop all properties of the object itself
**	dt_dropproperties objid, property -- drop the property
*/
	set nocount on

	if (@property is null) or (@property = '')
		delete from dbo.dtproperties where objectid=@id
	else
		delete from dbo.dtproperties 
			where objectid=@id and property=@property
go
IF OBJECT_ID('dbo.dt_droppropertiesbyid') IS NOT NULL
     DROP PROCEDURE dbo.dt_droppro_12142000071848000
ELSE 
     EXEC sp_rename 'dbo.dt_droppro_12142000071848000','dt_droppropertiesbyid',OBJECT
go
GRANT EXECUTE ON dbo.dt_droppropertiesbyid TO [public]
go

--
-- Procedure Extended Alter
-- dbo.dt_dropuserobjectbyid
--
EXEC sp_rename 'dbo.dt_dropuserobjectbyid','dt_dropuse_12142000071848000',OBJECT
go
CREATE procedure dbo.dt_dropuserobjectbyid
	@id int
as
/*
**	Drop an object from the dbo.dtproperties table
*/
	set nocount on
	delete from dbo.dtproperties where objectid=@id
go
IF OBJECT_ID('dbo.dt_dropuserobjectbyid') IS NOT NULL
     DROP PROCEDURE dbo.dt_dropuse_12142000071848000
ELSE 
     EXEC sp_rename 'dbo.dt_dropuse_12142000071848000','dt_dropuserobjectbyid',OBJECT
go
GRANT EXECUTE ON dbo.dt_dropuserobjectbyid TO [public]
go

--
-- Procedure Extended Alter
-- dbo.dt_getobjwithprop
--
EXEC sp_rename 'dbo.dt_getobjwithprop','dt_getobjw_12142000071849000',OBJECT
go
CREATE procedure dbo.dt_getobjwithprop
	@property varchar(30),
	@value varchar(255)
as
/*
**	Retrieve the owner object(s) of a given property
*/
	set nocount on

	if (@property is null) or (@property = '')
	begin
		raiserror('Must specify a property name.',-1,-1)
		return (1)
	end

	if (@value is null)
		select objectid id from dbo.dtproperties
			where property=@property

	else
		select objectid id from dbo.dtproperties
			where property=@property and value=@value
go
IF OBJECT_ID('dbo.dt_getobjwithprop') IS NOT NULL
     DROP PROCEDURE dbo.dt_getobjw_12142000071849000
ELSE 
     EXEC sp_rename 'dbo.dt_getobjw_12142000071849000','dt_getobjwithprop',OBJECT
go
GRANT EXECUTE ON dbo.dt_getobjwithprop TO [public]
go

--
-- Procedure Extended Alter
-- dbo.dt_getpropertiesbyid
--
EXEC sp_rename 'dbo.dt_getpropertiesbyid','dt_getprop_12142000071849000',OBJECT
go
CREATE procedure dbo.dt_getpropertiesbyid
	@id int,
	@property varchar(64)
as
/*
**	Retrieve properties by id's
**
**	dt_getproperties objid, null or '' -- retrieve all properties of the object itself
**	dt_getproperties objid, property -- retrieve the property specified
*/
	set nocount on

	if (@property is null) or (@property = '')
		select property, version, value, lvalue
			from dbo.dtproperties
			where  @id=objectid
	else
		select property, version, value, lvalue
			from dbo.dtproperties
			where  @id=objectid and @property=property
go
IF OBJECT_ID('dbo.dt_getpropertiesbyid') IS NOT NULL
     DROP PROCEDURE dbo.dt_getprop_12142000071849000
ELSE 
     EXEC sp_rename 'dbo.dt_getprop_12142000071849000','dt_getpropertiesbyid',OBJECT
go
GRANT EXECUTE ON dbo.dt_getpropertiesbyid TO [public]
go

--
-- Procedure Extended Alter
-- dbo.dt_setpropertybyid
--
EXEC sp_rename 'dbo.dt_setpropertybyid','dt_setprop_12142000071850000',OBJECT
go
CREATE procedure dbo.dt_setpropertybyid
	@id int,
	@property varchar(64),
	@value varchar(255),
	@lvalue image
as
/*
**	If the property already exists, reset the value; otherwise add property
**		id -- the id in sysobjects of the object
**		property -- the name of the property
**		value -- the text value of the property
**		lvalue -- the binary value of the property (image)
*/
	set nocount on
	if exists (select * from dbo.dtproperties 
			where objectid=@id and property=@property)
	begin
		--
		-- bump the version count for this row as we update it
		--
		update dbo.dtproperties set value=@value, lvalue=@lvalue, version=version+1
			where objectid=@id and property=@property
	end
	else
	begin
		--
		-- version count is auto-set to 0 on initial insert
		--
		insert dbo.dtproperties (property, objectid, value, lvalue)
			values (@property, @id, @value, @lvalue)
	end
go
IF OBJECT_ID('dbo.dt_setpropertybyid') IS NOT NULL
     DROP PROCEDURE dbo.dt_setprop_12142000071850000
ELSE 
     EXEC sp_rename 'dbo.dt_setprop_12142000071850000','dt_setpropertybyid',OBJECT
go
GRANT EXECUTE ON dbo.dt_setpropertybyid TO [public]
go

--
-- Procedure Extended Alter
-- dbo.dt_verstamp006
--
EXEC sp_rename 'dbo.dt_verstamp006','dt_verstam_12142000071851000',OBJECT
go
CREATE procedure dbo.dt_verstamp006
as
/*
**	This procedure returns the version number of the stored
**	procedures used by the Microsoft Visual Database Tools.
**	Current version is 6.1.00.
*/
	select 6100
go
IF OBJECT_ID('dbo.dt_verstamp006') IS NOT NULL
     DROP PROCEDURE dbo.dt_verstam_12142000071851000
ELSE 
     EXEC sp_rename 'dbo.dt_verstam_12142000071851000','dt_verstamp006',OBJECT
go
GRANT EXECUTE ON dbo.dt_verstamp006 TO [public]
go

--
-- Procedure Create
-- dbo.getBrowserDocChilds
--
CREATE PROCEDURE getBrowserDocChilds @meta_id int, @user_id int AS
-- Lists the childs for a specific browser_doc
SELECT DISTINCT	to_meta_id,
			meta_headline
FROM	browser_docs bd
JOIN meta m
						ON 	bd.to_meta_id = m.meta_id
						AND 	bd.meta_id = @meta_id
LEFT JOIN roles_rights rr
						ON	rr.meta_id = m.meta_id
						AND	rr.set_id < 4
JOIN user_roles_crossref urc
						ON	urc.user_id = @user_id
						AND (
								urc.role_id = 0
							OR	urc.role_id = rr.role_id
							OR	m.shared = 1
						)
WHERE m.activate = 1
ORDER BY to_meta_id
go

--
-- Procedure Create
-- dbo.getDocs
--
CREATE PROCEDURE getDocs @user_id int, @start int, @end int AS

-- Lists documents user is allowed to see.

SELECT DISTINCT	m.meta_id,
			COUNT(DISTINCT c.meta_id) parentcount,
			meta_headline,
			doc_type
FROM			meta m
LEFT JOIN		childs c			ON	c.to_meta_id = m.meta_id
LEFT JOIN		roles_rights rr		ON	rr.meta_id = m.meta_id
						AND	rr.set_id < 4
JOIN			user_roles_crossref urc	ON	urc.user_id = @user_id
						AND	(
								urc.role_id = 0
							OR	(
									urc.role_id = rr.role_id
								)
							OR	m.shared = 1
							)
WHERE		m.activate = 1
		AND	m.meta_id > (@start-1) 
		AND	m.meta_id < (@end+1)
GROUP BY		m.meta_id,m.meta_headline,m.doc_type,c.to_meta_id
ORDER BY		m.meta_id
go

--
-- Procedure Create
-- dbo.getLanguages
--
CREATE PROCEDURE getLanguages AS
select lang_prefix,language from languages order by language
go

--
-- Procedure Create
-- dbo.getMenuDocChilds
--
CREATE PROCEDURE getMenuDocChilds @meta_id int, @user_id int AS
-- Lists the childs of menudoc @meta_id
SELECT DISTINCT	to_meta_id,
			meta_headline
FROM		childs c
JOIN		meta m
					ON	c.to_meta_id = m.meta_id
	      				AND	c.meta_id = @meta_id
LEFT JOIN	roles_rights rr
					ON	rr.meta_id = m.meta_id
					AND	rr.set_id < 4
JOIN		user_roles_crossref urc
					ON	urc.user_id = @user_id
      					AND (		urc.role_id = 0
						OR	urc.role_id = rr.role_id
						OR 	m.shared = 1
					)
WHERE m.activate = 1
ORDER BY to_meta_id
go

--
-- Procedure Create
-- dbo.getTemplategroups
--
CREATE PROCEDURE getTemplategroups AS
select group_id,group_name from templategroups order by group_name
go

--
-- Procedure Create
-- dbo.getTemplates
--
CREATE PROCEDURE getTemplates AS
select template_id, simple_name from templates
go

--
-- Procedure Create
-- dbo.getTemplatesInGroup
--
CREATE PROCEDURE getTemplatesInGroup @grp_id INT AS
SELECT	t.template_id,simple_name
FROM		templates t	JOIN
		templates_cref c
ON		t.template_id = c.template_id
WHERE	c.group_id = @grp_id
ORDER BY	simple_name
go

--
-- Procedure Create
-- dbo.getUserWriteRights
--
CREATE PROCEDURE getUserWriteRights AS
DECLARE @user int
DECLARE @doc int
select user_id from user_roles_crossref where user_id = @user and role_id = 0 -- Returnerar en rad om användare 1 är superadmin
select user_id from user_rights where meta_id = @doc and user_id = @user and permission_id = 99 -- Returnerar en rad om användare 1 skapade dokument 1351
select user_id from roles_rights join user_roles_crossref on roles_rights.role_id = user_roles_crossref.role_id where meta_id = @doc and user_id = @doc and permission_id = 3 -- Returnerar en rad om användare 1 är medlem i en grupp som har skrivrättigheter i dokument 1351
go


--
-- Procedure Create
-- dbo.phoneNbrAdd
--
CREATE PROCEDURE phoneNbrAdd
/*
This function adds a new phone numbers to the db. Used by AdminUserPhones
*/
	@user_id int,
	@country varchar(15) ,
	@area varchar(15) , 
	@nbr varchar(15)
AS

DECLARE @newPhoneId int
SELECT @newPhoneId = MAX(phone_id) + 1
FROM phones

IF @newPhoneId IS NULL 
	SET @newPhoneId = 1

INSERT INTO PHONES ( phone_id , country_code, area_code, number , user_id )
VALUES (@newPhoneId , @country, @area,  @nbr, @user_id )
go

--
-- Procedure Create
-- dbo.test
--
CREATE PROCEDURE test AS

SELECT COUNT(usr.role_id) , (RTRIM(last_name) + ', ' + RTRIM(first_name))   
FROM user_roles_crossref usr, roles r, users u
WHERE usr.ROLE_ID = 5 
AND usr.user_id = u.user_id
GROUP BY (RTRIM(last_name) + ', ' + RTRIM(first_name)), usr.role_id
go

--
-- Procedure Create
-- dbo.AddBrowserStatistics
--
CREATE PROCEDURE AddBrowserStatistics @os VARCHAR(30), @browser varchar(30), @version varchar(30) AS

DECLARE @newline CHAR(2)
SET @newline = CHAR(13)+CHAR(10)

DECLARE @browserstring VARCHAR(120)
SET @browserstring = 	'Os: '+@os+@newline+
			'Browser: '+@browser+@newline+
			'Version: '+@version

EXEC AddStatistics @browserstring
go

--
-- Procedure Create
-- dbo.AddScreenStatistics
--
CREATE PROCEDURE AddScreenStatistics @width INT, @height INT, @bits INT AS

DECLARE @screen VARCHAR(20) 
SET @screen = 'Screen: '+LTRIM(STR(@width))+'x'+LTRIM(STR(@height))+'x'+LTRIM(STR(@bits))

EXEC AddStatistics @screen
go

--
-- Procedure Create
-- dbo.AddStatisticsCount
--
CREATE PROCEDURE AddStatisticsCount AS

EXEC AddStatistics 'Count'
go

--
-- Procedure Create
-- dbo.AddVersionStatistics
--
CREATE PROCEDURE AddVersionStatistics @name VARCHAR(30), @version VARCHAR(30) AS

DECLARE @string VARCHAR(62)
SET @string = @name+': '+@version

EXEC AddStatistics @string
go

INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(65536,2,'se','Ändra text')
INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(65536,5,'se','Redigera')
INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(65536,6,'se','Redigera')
INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(65536,7,'se','Redigera')
INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(65536,8,'se','Redigera')
INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(65536,101,'se','Redigera')
INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(65536,102,'se','Redigera')
INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(131072,2,'se','Ändra bild')
INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(262144,2,'se','Ändra meny')
INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(524288,2,'se','Ändra utseende')
GO

INSERT INTO permission_sets (set_id, description) VALUES(0,'Full')
INSERT INTO permission_sets (set_id, description) VALUES(1,'Begränsad 1')
INSERT INTO permission_sets (set_id, description) VALUES(2,'Begränsad 2')
INSERT INTO permission_sets (set_id, description) VALUES(3,'Läs')
GO

DELETE FROM permissions
INSERT INTO permissions (permission_id,lang_prefix,description) VALUES(         1,'se','Ändra rubrik')
INSERT INTO permissions (permission_id,lang_prefix,description) VALUES(         2,'se','Ändra dokinfo')
INSERT INTO permissions (permission_id,lang_prefix,description) VALUES(         4,'se','Ändra rättigheter för roller')
INSERT INTO permissions (permission_id,lang_prefix,description) VALUES(         8,'se','Skapa dokument')
GO