if exists (select * from sysobjects where id = object_id(N'[dbo].[AddBrowserStatistics]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[AddBrowserStatistics]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[AddNewuser]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[AddNewuser]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[AddPhoneNr]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[AddPhoneNr]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[AddScreenStatistics]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[AddScreenStatistics]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[AddStatistics]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[AddStatistics]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[AddStatisticsCount]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[AddStatisticsCount]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[AddUserRole]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[AddUserRole]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[AddVersionStatistics]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[AddVersionStatistics]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[ChangeUserActiveStatus]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[ChangeUserActiveStatus]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[CheckAdminRights]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[CheckAdminRights]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[CheckExistsInMenu]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[CheckExistsInMenu]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[CheckUserDocSharePermission]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[CheckUserDocSharePermission]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[classification_convert]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[classification_convert]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[Classification_Fix]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[Classification_Fix]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[Classification_Get_All]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[Classification_Get_All]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[ClassificationAdd]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[ClassificationAdd]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[CopyDocs]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[CopyDocs]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[DeleteDocPermissionSetEx]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[DeleteDocPermissionSetEx]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[DeleteInclude]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[DeleteInclude]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[DeleteNewDocPermissionSetEx]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[DeleteNewDocPermissionSetEx]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[DelPhoneNr]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[DelPhoneNr]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[DelUser]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[DelUser]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[DelUserRoles]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[DelUserRoles]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[DocumentDelete]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[DocumentDelete]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[ExistingDocsGetSelectedMetaIds]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[ExistingDocsGetSelectedMetaIds]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[FindMetaId]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[FindMetaId]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[FindUserName]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[FindUserName]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetAllRoles]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetAllRoles]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetAllUsers]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetAllUsers]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetAllUsersInList]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetAllUsersInList]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[getBrowserDocChilds]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[getBrowserDocChilds]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetCategoryUsers]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetCategoryUsers]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetChilds]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetChilds]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetCurrentSessionCounter]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetCurrentSessionCounter]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetCurrentSessionCounterDate]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetCurrentSessionCounterDate]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[getDocs]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[getDocs]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetDocType]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetDocType]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetDocTypes]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetDocTypes]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetDocTypesForUser]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetDocTypesForUser]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetDocTypesWithNewPermissions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetDocTypesWithNewPermissions]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetDocTypesWithPermissions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetDocTypesWithPermissions]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetHighestUserId]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetHighestUserId]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetImgs]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetImgs]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetIncludes]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetIncludes]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetLangPrefix]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetLangPrefix]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetLangPrefixFromId]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetLangPrefixFromId]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetLanguageList]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetLanguageList]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[getLanguages]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[getLanguages]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[getMenuDocChilds]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[getMenuDocChilds]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetMetaPathInfo]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetMetaPathInfo]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetNewPermissionSet]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetNewPermissionSet]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetNoOfTemplates]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetNoOfTemplates]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetPermissionSet]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetPermissionSet]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetRolesDocPermissions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetRolesDocPermissions]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[getTemplategroups]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[getTemplategroups]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetTemplateGroupsForUser]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetTemplateGroupsForUser]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetTemplateGroupsWithNewPermissions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetTemplateGroupsWithNewPermissions]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetTemplateGroupsWithPermissions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetTemplateGroupsWithPermissions]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[getTemplates]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[getTemplates]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[getTemplatesInGroup]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[getTemplatesInGroup]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetTextDocData]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetTextDocData]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetTextNumber]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetTextNumber]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetTexts]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetTexts]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetUserCreateDate]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserCreateDate]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetUserId]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserId]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetUserIdFromName]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserIdFromName]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetUserInfo]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserInfo]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetUserNames]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserNames]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetUserPassword]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserPassword]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetUserPermissionSet]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserPermissionSet]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetUserPermissionSetEx]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserPermissionSetEx]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetUserPhoneNumbers]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserPhoneNumbers]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetUserPhones]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserPhones]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetUserRoles]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserRoles]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetUserRolesDocPermissions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserRolesDocPermissions]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetUserRolesIds]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserRolesIds]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetUsersWhoBelongsToRole]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUsersWhoBelongsToRole]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetUserType]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserType]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetUserTypes]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserTypes]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[IncSessionCounter]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[IncSessionCounter]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[InheritPermissions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[InheritPermissions]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[IPAccessAdd]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[IPAccessAdd]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[IPAccessDelete]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[IPAccessDelete]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[IPAccessesGetAll]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[IPAccessesGetAll]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[IPAccessUpdate]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[IPAccessUpdate]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[ListConferences]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[ListConferences]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[ListDocsByDate]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[ListDocsByDate]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[ListDocsGetInternalDocTypes]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[ListDocsGetInternalDocTypes]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[ListDocsGetInternalDocTypesValue]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[ListDocsGetInternalDocTypesValue]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[PermissionsGetPermission]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[PermissionsGetPermission]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[phoneNbrAdd]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[phoneNbrAdd]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[PhoneNbrDelete]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[PhoneNbrDelete]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[PhoneNbrUpdate]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[PhoneNbrUpdate]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[RemoveUserFromRole]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RemoveUserFromRole]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[RoleAddNew]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleAddNew]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[RoleAdminGetAll]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleAdminGetAll]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[RoleCheckConferenceAllowed]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleCheckConferenceAllowed]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[RoleCount]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleCount]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[RoleCountAffectedUsers]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleCountAffectedUsers]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[RoleDelete]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleDelete]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[RoleDeleteViewAffectedMetaIds]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleDeleteViewAffectedMetaIds]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[RoleDeleteViewAffectedUsers]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleDeleteViewAffectedUsers]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[RoleFindName]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleFindName]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[RoleGetAllApartFromRole]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleGetAllApartFromRole]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[RoleGetConferenceAllowed]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleGetConferenceAllowed]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[RoleGetName]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleGetName]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[RoleGetPermissionsByLanguage]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleGetPermissionsByLanguage]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[RoleGetPermissionsFromRole]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleGetPermissionsFromRole]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[RolePermissionsAddNew]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RolePermissionsAddNew]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[RoleUpdateName]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleUpdateName]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[RoleUpdatePermissions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleUpdatePermissions]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[SearchDocs]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SearchDocs]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[ServerMasterGet]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[ServerMasterGet]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[ServerMasterSet]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[ServerMasterSet]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[SetDocPermissionSet]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SetDocPermissionSet]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[SetDocPermissionSetEx]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SetDocPermissionSetEx]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[SetInclude]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SetInclude]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[SetNewDocPermissionSet]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SetNewDocPermissionSet]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[SetNewDocPermissionSetEx]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SetNewDocPermissionSetEx]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[SetRoleDocPermissionSetId]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SetRoleDocPermissionSetId]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[SetSessionCounterDate]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SetSessionCounterDate]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[SortOrder_GetExistingDocs]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SortOrder_GetExistingDocs]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[SystemMessageGet]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SystemMessageGet]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[SystemMessageSet]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SystemMessageSet]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[UpdateParentsDateModified]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[UpdateParentsDateModified]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[UpdateTemplateTextsAndImages]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[UpdateTemplateTextsAndImages]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[UpdateUser]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[UpdateUser]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[UserPrefsChange]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[UserPrefsChange]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[WebMasterGet]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[WebMasterGet]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[WebMasterSet]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[WebMasterSet]
GO

SET QUOTED_IDENTIFIER  ON    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE AddBrowserStatistics @os VARCHAR(30), @browser varchar(30), @version varchar(30) AS
DECLARE @newline CHAR(2)
SET @newline = CHAR(13)+CHAR(10)
DECLARE @browserstring VARCHAR(120)
SET @browserstring =  'Os: '+@os+@newline+
   'Browser: '+@browser+@newline+
   'Version: '+@version
EXEC AddStatistics @browserstring

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE AddPhoneNr
/* Adds a role to a particular user
*/
 @aUser_id int,
 @aRole_id int
AS
-- Lets check if the role already exists
DECLARE @foundFlag int
SET @foundFlag = 0 
SELECT @foundFlag = ref.role_id
FROM user_roles_crossref ref
WHERE ref.role_id = @aRole_id
 AND ref.user_id = @aUser_id
IF @@rowcount  = 0 BEGIN
 INSERT INTO  user_roles_crossref(user_id, role_id)
 VALUES( @aUser_id , @aRole_id)
END
/*
CREATE PROCEDURE AddUserRole
 Adds a role to a particular user
 @aUser_id int,
 @aRole_id int
AS
 INSERT INTO  user_roles_crossref(user_id, role_id)
 VALUES( @aUser_id , @aRole_id)
*/ 


GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  ON    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE AddScreenStatistics @width INT, @height INT, @bits INT AS
DECLARE @screen VARCHAR(20) 
SET @screen = 'Screen: '+LTRIM(STR(@width))+'x'+LTRIM(STR(@height))+'x'+LTRIM(STR(@bits))
EXEC AddStatistics @screen

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE AddStatistics @name VARCHAR(120) AS
UPDATE stats
SET  num = num + 1
WHERE name = @name
IF @@ROWCOUNT = 0
BEGIN
INSERT stats
VALUES ( @name,
   1
  )
END

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE AddStatisticsCount AS
EXEC AddStatistics 'Count'

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE AddUserRole
/* Adds a role to a particular user
*/
 @aUser_id int,
 @aRole_id int
AS
-- Lets check if the role already exists
DECLARE @foundFlag int
SET @foundFlag = 0 
SELECT @foundFlag = ref.role_id
FROM user_roles_crossref ref
WHERE ref.role_id = @aRole_id
 AND ref.user_id = @aUser_id
IF @@rowcount  = 0 BEGIN
 INSERT INTO  user_roles_crossref(user_id, role_id)
 VALUES( @aUser_id , @aRole_id)
END
/*
CREATE PROCEDURE AddUserRole
 Adds a role to a particular user
 @aUser_id int,
 @aRole_id int
AS
 INSERT INTO  user_roles_crossref(user_id, role_id)
 VALUES( @aUser_id , @aRole_id)
*/ 

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE AddVersionStatistics @name VARCHAR(30), @version VARCHAR(30) AS
DECLARE @string VARCHAR(62)
SET @string = @name+': '+@version
EXEC AddStatistics @string

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE ChangeUserActiveStatus @user_id int, @active int AS
/* 
 * change users activestate
*/
UPDATE users 
SET 
active = @active
WHERE user_id = @user_id

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  OFF 
GO

CREATE PROCEDURE CheckUserDocSharePermission @user_id INT, @meta_id INT AS

SELECT m.meta_id
FROM meta m
JOIN user_roles_crossref urc
				ON	urc.user_id = @user_id
				AND	m.meta_id = @meta_id
LEFT join roles_rights rr
				ON	rr.meta_id = m.meta_id
				AND	rr.role_id = urc.role_id
WHERE				(
						shared = 1
					OR	rr.set_id < 3
					OR	urc.role_id = 0
				)
GROUP BY m.meta_id
GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  OFF 
GO


CREATE PROCEDURE CopyDocs @documents_string VARCHAR(200), @parent_id INT, @menu_id INT, @user INT AS

CREATE TABLE #documents (
  meta_id VARCHAR(10)
)

CREATE TABLE #documents2 (
  meta_id VARCHAR(10)
)

DECLARE @substring VARCHAR(30)
DECLARE @index INT
DECLARE @endindex INT

IF LEN(@documents_string) > 0 BEGIN
 SET @index = 1
 WHILE @index <= LEN(@documents_string) BEGIN
  SET @endindex = CHARINDEX(',',@documents_string,@index+1)
  IF @endindex = 0 BEGIN
   SET @endindex = LEN(@documents_string)+1
  END --IF
  SET @substring = SUBSTRING(@documents_string,@index,@endindex-@index)
  INSERT INTO #documents2 VALUES (@substring)
  SET @index = @endindex + 1
 END -- WHILE
END -- IF

INSERT INTO 	#documents
SELECT		t.meta_id
FROM		#documents2 t
JOIN		meta m
					ON	t.meta_id = m.meta_id
JOIN		user_roles_crossref urc
					ON	urc.user_id = @user
LEFT JOIN	roles_rights rr
					ON	rr.set_id < 3
					AND	rr.meta_id = @parent_id
					AND	rr.role_id = urc.role_id
LEFT JOIN	doc_permission_sets_ex dpse
					ON	dpse.meta_id = @parent_id
					AND	dpse.set_id = rr.set_id
					AND	permission_id = 8
					AND	permission_data = m.doc_type
					AND	(
							rr.set_id = 1
						OR	rr.set_id = 2
					)
WHERE 		urc.role_id = 0	
	OR	rr.set_id = 0
	OR	dpse.permission_id = 8
GROUP BY	t.meta_id

DROP TABLE #documents2

DECLARE documents_cursor CURSOR FOR
SELECT	meta.meta_id,
	description,
	doc_type,
	meta_headline,
	meta_text,
	meta_image,
	owner_id,
	permissions,
	shared,
	expand,
	show_meta,
	help_text_id,
	archive,
	status_id,
	lang_prefix,
	classification,
	date_created,
	date_modified,
	sort_position,
	menu_position,
	disable_search,
	activated_date,
	activated_time,
	archived_date,
	archived_time,
	target,
	frame_name,
	activate
FROM	meta, #documents d
WHERE	meta.meta_id = d.meta_id

OPEN documents_cursor

DECLARE @meta_id int,
	@description varchar(80),
	@doc_type int,
	@meta_headline varchar(255),
	@meta_text varchar(1000),
	@meta_image varchar(255),
	@owner_id int,
	@permissions int,
	@shared int,
	@expand int,
	@show_meta int,
	@help_text_id int,
	@archive int,
	@status_id int,
	@lang_prefix varchar(3),
	@classification varchar(200),
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

FETCH NEXT FROM documents_cursor
INTO	@meta_id,
 	@description,
	@doc_type,
	@meta_headline,
	@meta_text,
	@meta_image,
	@owner_id,
	@permissions,
	@shared,
	@expand,
	@show_meta,
	@help_text_id,
	@archive,
	@status_id,
	@lang_prefix,
	@classification,
	@date_created,
	@date_modified,
	@sort_position,
	@menu_position,
	@disable_search,
	@activated_date,
	@activated_time,
	@archived_date,
	@archived_time,
	@target,
	@frame_name,
	@activate

WHILE (@@FETCH_STATUS = 0) BEGIN
	INSERT INTO meta (
		description,
		doc_type,
		meta_headline,
		meta_text,
		meta_image,
		owner_id,
		permissions,
		shared,
		expand,
		show_meta,
		help_text_id,
		archive,
		status_id,
		lang_prefix,
		classification,
		date_created,
		date_modified,
		sort_position,
		menu_position,
		disable_search,
		activated_date,
		activated_time,
		archived_date,
		archived_time,
		target,
		frame_name,
		activate
	) VALUES (
		@description,
		@doc_type,
		@meta_headline + ' (2)',
		@meta_text,
		@meta_image,
		@owner_id,
		@permissions,
		@shared,
		@expand,
		@show_meta,
		@help_text_id,
		@archive,
		@status_id,
		@lang_prefix,
		@classification,
		@date_created,
		@date_modified,
		@sort_position,
		@menu_position,
		@disable_search,
		@activated_date,
		@activated_time,
		@archived_date,
		@archived_time,
		@target,
		@frame_name,
		@activate
	)

	DECLARE @copy_id INT
	SET @copy_id = @@IDENTITY

	INSERT INTO text_docs 
	SELECT	@copy_id,
		template_id,
		group_id,
		sort_order
	FROM	text_docs
	WHERE	meta_id = @meta_id

	INSERT INTO url_docs
	SELECT	@copy_id,
		frame_name,
		target,
		url_ref,
		url_txt,
		lang_prefix
	FROM	url_docs
	WHERE	meta_id = @meta_id

	INSERT INTO browser_docs
	SELECT	@copy_id,
		to_meta_id,
		browser_id
	FROM	browser_docs
	WHERE	meta_id = @meta_id

	INSERT INTO frameset_docs
	SELECT	@copy_id,
		frame_set
	FROM	frameset_docs
	WHERE	meta_id = @meta_id

	INSERT INTO fileupload_docs
	SELECT	@copy_id,
		filename,
		mime
	FROM	fileupload_docs
	WHERE	meta_id = @meta_id

	INSERT INTO texts
	SELECT	@copy_id,
		name,
		text,
		type
	FROM	texts
	WHERE	meta_id = @meta_id

	INSERT INTO images
	SELECT	@copy_id,
		width,
		height,
		border,
		v_space,
		h_space,
		name,
		image_name,
		target,
		target_name,
		align,
		alt_text,
		low_scr,
		imgurl,
		linkurl
	FROM	images
	WHERE	meta_id = @meta_id

	INSERT INTO includes
	SELECT	@copy_id,
		include_id,
		included_meta_id
	FROM	includes
	WHERE	meta_id = @meta_id

	INSERT INTO doc_permission_sets
	SELECT	@copy_id,
		set_id,
		permission_id
	FROM	doc_permission_sets
	WHERE	meta_id = @meta_id

	INSERT INTO new_doc_permission_sets
	SELECT	@copy_id,
		set_id,
		permission_id
	FROM	new_doc_permission_sets
	WHERE	meta_id = @meta_id

	INSERT INTO doc_permission_sets_ex
	SELECT	@copy_id,
		set_id,
		permission_id,
		permission_data
	FROM	doc_permission_sets_ex
	WHERE	meta_id = @meta_id

	INSERT INTO new_doc_permission_sets_ex
	SELECT	@copy_id,
		set_id,
		permission_id,
		permission_data
	FROM	new_doc_permission_sets_ex
	WHERE	meta_id = @meta_id

	INSERT INTO roles_rights
	SELECT	role_id,
		@copy_id,
		set_id
	FROM	roles_rights
	WHERE	meta_id = @meta_id

	INSERT INTO user_rights
	SELECT	user_id,
		@copy_id,
		permission_id
	FROM	user_rights
	WHERE	meta_id = @meta_id

	INSERT INTO meta_classification
	SELECT	@copy_id,
		class_id
	FROM	meta_classification
	WHERE	meta_id = @meta_id

	INSERT INTO childs
	SELECT	@copy_id,
			to_meta_id,
			menu_sort,
			manual_sort_order
	FROM		childs
	WHERE	meta_id = @meta_id

	DECLARE @child_max INT
	-- FIXME: manual_sort_order should be an identity column
	SELECT @child_max = MAX(manual_sort_order)+10 FROM childs WHERE meta_id = @parent_id AND menu_sort = @menu_id
	INSERT INTO childs VALUES(@parent_id, @copy_id, @menu_id, @child_max)

	FETCH NEXT FROM documents_cursor
	INTO	@meta_id,
 		@description,
		@doc_type,
		@meta_headline,
		@meta_text,
		@meta_image,
		@owner_id,
		@permissions,
		@shared,
		@expand,
		@show_meta,
		@help_text_id,
		@archive,
		@status_id,
		@lang_prefix,
		@classification,
		@date_created,
		@date_modified,
		@sort_position,
		@menu_position,
		@disable_search,
		@activated_date,
		@activated_time,
		@archived_date,
		@archived_time,
		@target,
		@frame_name,
		@activate

END --WHILE

CLOSE documents_cursor
DEALLOCATE documents_cursor

/*
select * from meta_classification where meta_id = 1009
*/

DROP TABLE #documents

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE DeleteDocPermissionSetEx @meta_id INT, @set_id INT AS
/*
 Delete extended permissions for a permissionset for a document
*/
DELETE FROM  doc_permission_sets_ex
WHERE  meta_id = @meta_id
  AND set_id = @set_id

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  OFF 
GO

CREATE PROCEDURE DeleteInclude @meta_id INT, @include_id INT AS

DELETE FROM includes WHERE meta_id = @meta_id AND include_id = @include_id

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  ON    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE DeleteNewDocPermissionSetEx @meta_id INT, @set_id INT AS
/*
 Delete extended permissions for a permissionset for a document
*/
DELETE FROM  new_doc_permission_sets_ex
WHERE  meta_id = @meta_id
  AND set_id = @set_id

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE DelPhoneNr
 @aUserId int
AS
 DELETE 
 FROM phones
 WHERE user_id = @aUserId


GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE DelUser
 @aUserId int
AS
 
 DELETE
 FROM user_roles_crossref
 WHERE user_id = @aUserId
 DELETE 
 FROM users
 WHERE user_id = @aUserId

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE DelUserRoles
 @aUserId int
AS
 DELETE 
 FROM user_roles_crossref
 WHERE user_id = @aUserId

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  OFF 
GO

CREATE PROCEDURE [dbo].[DocumentDelete] 
	@meta_id int
AS

/*
Deletes a meta Id in the system. Used by func deleteDocAll in the ImcService class
*/



delete from meta_classification where meta_id = @meta_id
delete from childs where to_meta_id = 	@meta_id   
delete from childs where meta_id =	@meta_id 
delete from text_docs where meta_id = 	@meta_id  

delete from texts where meta_id = @meta_id  
delete from images where meta_id = @meta_id  
delete from roles_rights where meta_id = @meta_id  
delete from user_rights where meta_id = @meta_id  
delete from url_docs where meta_id = @meta_id 
delete from browser_docs where meta_id = @meta_id 
delete from fileupload_docs where meta_id = @meta_id  
delete from frameset_docs where meta_id = @meta_id  
delete from meta where meta_id = @meta_id
GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  OFF 
GO

CREATE PROCEDURE ExistingDocsGetSelectedMetaIds
		@string varchar(1024) 
AS

/*
Creates a table with the meta id:s we are looking for.
ImcServices function ExistingDocsGetSelectedMetaIds is using this method
*/

SET NOCOUNT ON

-- Lets create the table where we gonna put the found products
CREATE TABLE #wanted_meta_id (
	meta_id INT PRIMARY KEY
)


DECLARE @substring VARCHAR(50)
DECLARE @index INT
DECLARE @endindex INT

IF LEN(@string) > 0 BEGIN
	SET @index = 1
	WHILE @index <= LEN(@string) BEGIN
		SET @endindex = CHARINDEX(',',@string,@index+1)
		IF @endindex = 0 BEGIN
			SET @endindex = LEN(@string)+1
		END -- IF
		SET @substring = SUBSTRING(@string,@index,@endindex-@index)
		SET @subString =  LTRIM (  RTRIM(@subString) )
		
		-- Lets check if the meta id already exists in the table
		DECLARE @foundMetaId int
		SET @foundMetaId = 0

		DECLARE @tmpId int
		SELECT @tmpId = CAST( @subString AS INT)

		SELECT @foundMetaId = meta_id 
		FROM #wanted_meta_id 
		WHERE #wanted_meta_id.meta_id  = @tmpId
		
		-- Lets insert the meta id:s into table
		IF ( @foundMetaId = 0 )
			INSERT INTO #wanted_meta_id  (meta_id ) VALUES (  @subString  ) 
		SET @index = @endindex + 1
	END -- WHILE
END -- IF


--SELECT * 
--FROM #wanted_meta_id

SELECT 	meta_id,
		doc_type,
		meta_headline,
		meta_text,
		date_created,
		date_modified,
		ISNULL(CONVERT(VARCHAR,NULLIF(activated_date,''''),121),'''') AS date_activated,
		ISNULL(CONVERT(VARCHAR,NULLIF(archived_date,''''),121),'''') AS date_archived,
		archive,
		shared,
		show_meta,
		disable_search
--		' + STR(@doc_count) + ' AS doc_count
FROM meta
WHERE meta_id IN 
	(	SELECT meta_id
		FROM #wanted_meta_id
	)

DROP TABLE #wanted_meta_id

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE [FindMetaId]
 @meta_id int
 AS
SELECT meta_id 
FROM meta
WHERE meta_id = @meta_id

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE GetAllRoles AS
SELECT role_id, role_name
FROM roles
 
ORDER BY role_name

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE [GetAllUsers] AS
  select *
 from USERS
 
 order by  last_name

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE GetAllUsersInList AS
/*
This function is used from AdminIpAcces servlet to generate a list
*/
SELECT user_id, last_name + ', ' + first_name from users
ORDER BY last_name

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE getBrowserDocChilds @meta_id int, @user_id int AS
-- Lists the childs for a specific browser_doc
SELECT DISTINCT to_meta_id,
   meta_headline
FROM browser_docs bd
JOIN meta m
      ON  bd.to_meta_id = m.meta_id
      AND  bd.meta_id = @meta_id
LEFT JOIN roles_rights rr
      ON rr.meta_id = m.meta_id
      AND rr.set_id < 4
JOIN user_roles_crossref urc
      ON urc.user_id = @user_id
      AND (
        urc.role_id = 0
       OR urc.role_id = rr.role_id
       OR m.shared = 1
      )
WHERE m.activate = 1
ORDER BY to_meta_id

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE GetCategoryUsers
/*
Used from servlet AdminUser
*/
 @category int
AS
SELECT user_id, last_name + ', ' + first_name + ' ['+LTRIM(RTRIM(login_name))+']'
FROM users
WHERE user_type = @category
ORDER BY last_name




GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO



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
  archive,target, left(convert (varchar,date_created,120),10), left(convert (varchar,date_modified,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time,
  min(urc.role_id * ISNULL(~CAST(dps.permission_id AS BIT),1) * ISNULL(rr.set_id,1)),
  fd.filename
from   childs c
join   meta m    
     on    m.meta_id = c.to_meta_id     -- meta.meta_id corresponds to childs.to_meta_id
     and  m.activate > 0       -- Only include the documents that are active in the meta table.
     and  c.meta_id = @meta_id      -- Only include documents that are children to this particular meta_id
left join roles_rights rr            -- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
     on  c.to_meta_id = rr.meta_id      -- Only include rows with the documents we are interested in
left join doc_permission_sets dps           -- Include the permission_sets
     on  c.to_meta_id = dps.meta_id     -- for each document
     and dps.set_id = rr.set_id      -- and only the sets for the roles we are interested in
     and dps.permission_id > 0      -- and only the sets that have any permission
join user_roles_crossref urc           -- This table tells us which users have which roles
     on urc.user_id = @user_id      -- Only include the rows with the user we are interested in...
     and ( 
       rr.role_id = urc.role_id     -- Include rows where the users roles match the roles that have permissions on the documents
      or  urc.role_id = 0      -- and also include the rows that tells us this user is a superadmin
      or  (
        m.show_meta != 0    -- and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
       and ISNULL(~CAST(dps.permission_id AS BIT),1) != 1
      )
     )
left join fileupload_docs fd
     on  fd.meta_id = c.to_meta_id
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10), left(convert (varchar,date_modified,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time,
  fd.filename
order by  menu_sort,c.manual_sort_order desc
end
else if @sort_by = 3
begin
select  to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10), left(convert (varchar,date_modified,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time,
  min(urc.role_id * ISNULL(~CAST(dps.permission_id AS BIT),1) * ISNULL(rr.set_id,1)),
  fd.filename
from   childs c
join   meta m    
     on    m.meta_id = c.to_meta_id     -- meta.meta_id corresponds to childs.to_meta_id
     and  m.activate > 0       -- Only include the documents that are active in the meta table.
     and  c.meta_id = @meta_id      -- Only include documents that are children to this particular meta_id
left join roles_rights rr            -- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
     on  c.to_meta_id = rr.meta_id      -- Only include rows with the documents we are interested in
left join doc_permission_sets dps           -- Include the permission_sets
     on  c.to_meta_id = dps.meta_id     -- for each document
     and dps.set_id = rr.set_id      -- and only the sets for the roles we are interested in
     and dps.permission_id > 0      -- and only the sets that have any permission
join user_roles_crossref urc           -- This table tells us which users have which roles
     on urc.user_id = @user_id      -- Only include the rows with the user we are interested in...
     and ( 
       rr.role_id = urc.role_id     -- Include rows where the users roles match the roles that have permissions on the documents
      or  urc.role_id = 0      -- and also include the rows that tells us this user is a superadmin
      or  (
        m.show_meta != 0    -- and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
       and ISNULL(~CAST(dps.permission_id AS BIT),1) != 1
      )
     )
left join fileupload_docs fd
     on  fd.meta_id = c.to_meta_id
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10), left(convert (varchar,date_modified,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time,
  fd.filename
order by  menu_sort,left(convert (varchar,date_created,120),10) desc
end
else if @sort_by = 1
begin
select  to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10), left(convert (varchar,date_modified,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time,
  min(urc.role_id * ISNULL(~CAST(dps.permission_id AS BIT),1) * ISNULL(rr.set_id,1)),
  fd.filename
from   childs c
join   meta m    
     on    m.meta_id = c.to_meta_id     -- meta.meta_id corresponds to childs.to_meta_id
     and  m.activate > 0       -- Only include the documents that are active in the meta table.
     and  c.meta_id = @meta_id      -- Only include documents that are children to this particular meta_id
left join roles_rights rr            -- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
     on  c.to_meta_id = rr.meta_id      -- Only include rows with the documents we are interested in
left join doc_permission_sets dps           -- Include the permission_sets
     on  c.to_meta_id = dps.meta_id     -- for each document
     and dps.set_id = rr.set_id      -- and only the sets for the roles we are interested in
     and dps.permission_id > 0      -- and only the sets that have any permission
join user_roles_crossref urc           -- This table tells us which users have which roles
     on urc.user_id = @user_id      -- Only include the rows with the user we are interested in...
     and ( 
       rr.role_id = urc.role_id     -- Include rows where the users roles match the roles that have permissions on the documents
      or  urc.role_id = 0      -- and also include the rows that tells us this user is a superadmin
      or  (
        m.show_meta != 0    -- and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
       and ISNULL(~CAST(dps.permission_id AS BIT),1) != 1
      )
     )
left join fileupload_docs fd
     on  fd.meta_id = c.to_meta_id
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10), left(convert (varchar,date_modified,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time,
  fd.filename
order by  menu_sort,meta_headline
end




GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE GetCurrentSessionCounter 
 
AS
 SELECT value 
 FROM sys_data
 WHERE type_id  = 1

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE GetCurrentSessionCounterDate
 
AS
 SELECT value 
 FROM sys_data
 WHERE type_id  = 2

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE getDocs @user_id int, @start int, @end int AS
-- Lists documents user is allowed to see.
SELECT DISTINCT m.meta_id,
   COUNT(DISTINCT c.meta_id) parentcount,
   meta_headline,
   doc_type
FROM   meta m
LEFT JOIN  childs c   ON c.to_meta_id = m.meta_id
LEFT JOIN  roles_rights rr  ON rr.meta_id = m.meta_id
      AND rr.set_id < 4
JOIN   user_roles_crossref urc ON urc.user_id = @user_id
      AND (
        urc.role_id = 0
       OR (
         urc.role_id = rr.role_id
        )
       OR m.shared = 1
       )
WHERE  m.activate = 1
  AND m.meta_id > (@start-1) 
  AND m.meta_id < (@end+1)
GROUP BY  m.meta_id,m.meta_headline,m.doc_type,c.to_meta_id
ORDER BY  m.meta_id

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE GetDocType
 @meta_id int
AS
/*
 Used by external systems to get the docType
*/
SELECT doc_type
FROM meta
WHERE meta_id = @meta_id

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  ON    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE GetDocTypes @lang_prefix VARCHAR(3) AS
SELECT doc_type,type FROM doc_types
WHERE lang_prefix = @lang_prefix
ORDER BY doc_type

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE GetDocTypesForUser @meta_id INT,@user_id INT, @lang_prefix VARCHAR(3) AS
/*
 Nice query that fetches all document types a user may create in a document,
 for easy insertion into an html-option-list, no less!
*/
SELECT DISTINCT dt.doc_type, dt.type
FROM   doc_types dt
JOIN  user_roles_crossref urc
       ON urc.user_id = @user_id
       AND dt.lang_prefix = @lang_prefix
LEFT JOIN roles_rights rr
       ON rr.meta_id = @meta_id
       AND rr.role_id = urc.role_id
LEFT JOIN doc_permission_sets dps
       ON dps.meta_id = rr.meta_id
       AND dps.set_id = rr.set_id
LEFT JOIN doc_permission_sets_ex dpse
       ON dpse.permission_data = dt.doc_type
       AND dpse.meta_id = rr.meta_id
       AND dpse.set_id = rr.set_id
       AND dpse.permission_id = 8 -- Create document
WHERE
        dpse.permission_data IS NOT NULL
       OR rr.set_id = 0
       OR urc.role_id = 0
ORDER BY dt.doc_type

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

--
-- Procedure Create
-- dbo.GetDocTypesWithNewPermissions
--
CREATE PROCEDURE GetDocTypesWithNewPermissions @meta_id INT,@set_id INT, @lang_prefix VARCHAR(3) AS
/*
 Retrieves a list of all doc-types, with a indicator of wether a particular permission-set may use it.
 The permission-set must still have the "Create document"-permission set, though. ( Not checked in this proc )
 Column 1: The doc-type
 Column 2: The name of the doc-type
 Column 3: > -1 if this set_id may use this.
*/
SELECT doc_type,type,ISNULL(dpse.permission_data,-1)
FROM   doc_types dt
LEFT JOIN new_doc_permission_sets_ex dpse
       ON dpse.permission_data = dt.doc_type
       AND dpse.meta_id = @meta_id
       AND dpse.set_id = @set_id
       AND dpse.permission_id = 8
WHERE dt.lang_prefix = @lang_prefix
ORDER BY CAST(ISNULL(dpse.permission_data,-1)+1  AS BIT) DESC,doc_type

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

--
-- Procedure Create
-- dbo.GetDocTypesWithPermissions
--
CREATE PROCEDURE GetDocTypesWithPermissions @meta_id INT,@set_id INT, @lang_prefix VARCHAR(3) AS
/*
 Retrieves a list of all doc-types, with a indicator of wether a particular permission-set may use it.
 The permission-set must still have the "Create document"-permission set, though. ( Not checked in this proc )
 Column 1: The doc-type
 Column 2: The name of the doc-type
 Column 3: > -1 if this set_id may use this.
*/
SELECT doc_type,type,ISNULL(dpse.permission_data,-1)
FROM   doc_types dt
LEFT JOIN doc_permission_sets_ex dpse
       ON dpse.permission_data = dt.doc_type
       AND dpse.meta_id = @meta_id
       AND dpse.set_id = @set_id
       AND dpse.permission_id = 8
WHERE dt.lang_prefix = @lang_prefix
ORDER BY CAST(ISNULL(dpse.permission_data,-1)+1  AS BIT) DESC,doc_type

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE GetHighestUserId
AS
--DECLARE @retVal int
SELECT MAX(user_id) +1
FROM users

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE GetImgs
@meta_id int AS
select '#img'+convert(varchar(5), name)+'#',name,imgurl,linkurl,width,height,border,v_space,h_space,image_name,align,alt_text,low_scr,target,target_name from images where meta_id = @meta_id

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  OFF 
GO

CREATE PROCEDURE GetIncludes @meta_id INT AS

SELECT include_id, included_meta_id  FROM includes WHERE meta_id = @meta_id
ORDER BY include_id
GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE GetLangPrefix
 @meta_id int
AS
/*
 Used by external systems to get the langprefix
*/
SELECT lang_prefix 
FROM meta
WHERE meta_id = @meta_id

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE GetLangPrefixFromId
/* Get the users preferred language. Used by the administrator functions.
Begin with getting the users langId from the userobject.
*/
 @aLangId int
 AS
SELECT lang_prefix 
FROM lang_prefixes
WHERE lang_id = @aLangId

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE GetLanguageList
 @user_lang_prefix VARCHAR(3)
AS
/*
 Returns all 
*/
SELECT lp.lang_id , lang.language
FROM lang_prefixes lp, languages lang
WHERE lp.lang_prefix = lang.lang_prefix
AND lang.user_prefix = @user_lang_prefix

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE getLanguages AS
select lang_prefix,language from languages order by language

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE getMenuDocChilds @meta_id int, @user_id int AS
-- Lists the childs of menudoc @meta_id
SELECT DISTINCT to_meta_id,
   meta_headline
FROM  childs c
JOIN  meta m
     ON c.to_meta_id = m.meta_id
           AND c.meta_id = @meta_id
LEFT JOIN roles_rights rr
     ON rr.meta_id = m.meta_id
     AND rr.set_id < 4
JOIN  user_roles_crossref urc
     ON urc.user_id = @user_id
           AND (  urc.role_id = 0
      OR urc.role_id = rr.role_id
      OR  m.shared = 1
     )
WHERE m.activate = 1
ORDER BY to_meta_id

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE [GetNewPermissionSet] @meta_id INT, @set_id INT, @lang_prefix VARCHAR(3) AS
/*
 Nice little query that returns which permissions a permissionset consists of.
 Column 1: The id of the permission
 Column 2: The description of the permission
 Column 3: Wether the permission is set. 0 or 1.
*/
SELECT p.permission_id AS p_id, p.description,CAST(ISNULL((p.permission_id & dps.permission_id),0) AS BIT)
FROM   new_doc_permission_sets dps
RIGHT JOIN permissions p
       ON (p.permission_id & dps.permission_id) > 0
       AND dps.meta_id = @meta_id
       AND dps.set_id = @set_id
       AND p.lang_prefix = @lang_prefix
UNION
SELECT dp.permission_id AS p_id, dp.description,CAST(ISNULL((dp.permission_id & dps.permission_id),0) AS BIT)
FROM   meta m
JOIN  doc_permissions dp
       ON dp.doc_type = m.doc_type
       AND m.meta_id = @meta_id
       AND dp.lang_prefix = @lang_prefix
LEFT JOIN new_doc_permission_sets dps
       ON (dp.permission_id & dps.permission_id) > 0
       AND dps.set_id = @set_id
       AND dps.meta_id = m.meta_id

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE GetNoOfTemplates AS
select count(*) from templates

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  ON    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE [GetPermissionSet] @meta_id INT, @set_id INT, @lang_prefix VARCHAR(3) AS
/*
 Nice little query that returns which permissions a permissionset consists of.
 Column 1: The id of the permission
 Column 2: The description of the permission
 Column 3: Wether the permission is set. 0 or 1.
*/
SELECT p.permission_id AS p_id, p.description,CAST(ISNULL((p.permission_id & dps.permission_id),0) AS BIT)
FROM   doc_permission_sets dps
RIGHT JOIN permissions p
       ON (p.permission_id & dps.permission_id) > 0
       AND dps.meta_id = @meta_id
       AND dps.set_id = @set_id
       AND p.lang_prefix = @lang_prefix
UNION
SELECT dp.permission_id AS p_id, dp.description,CAST(ISNULL((dp.permission_id & dps.permission_id),0) AS BIT)
FROM   meta m
JOIN  doc_permissions dp
       ON dp.doc_type = m.doc_type
       AND m.meta_id = @meta_id
       AND dp.lang_prefix = @lang_prefix
LEFT JOIN doc_permission_sets dps
       ON (dp.permission_id & dps.permission_id) > 0
       AND dps.set_id = @set_id
       AND dps.meta_id = m.meta_id

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE GetRolesDocPermissions @meta_id INT AS
/* Selects all roles except for superadmin, and returns the permissionset each has for the document. */
SELECT
  r.role_id,
  r.role_name,
  ISNULL(rr.set_id,4)
FROM
  roles_rights rr 
RIGHT JOIN 
  roles r 
      ON  rr.role_id = r.role_id
      AND rr.meta_id = @meta_id
WHERE r.role_id > 0
ORDER BY role_name

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE getTemplategroups AS
select group_id,group_name from templategroups order by group_name

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE GetTemplateGroupsForUser @meta_id INT, @user_id INT AS
/*
 Nice query that fetches all templategroups a user may use in a document,
 for easy insertion into an html-option-list, no less!
*/
SELECT distinct group_id,group_name
FROM   templategroups dt
JOIN  user_roles_crossref urc
       ON urc.user_id = @user_id
LEFT JOIN roles_rights rr
       ON rr.meta_id = @meta_id
       AND rr.role_id = urc.role_id
LEFT JOIN doc_permission_sets dps
       ON dps.meta_id = rr.meta_id
       AND dps.set_id = rr.set_id
LEFT JOIN doc_permission_sets_ex dpse
       ON dpse.permission_data = dt.group_id
       AND (dpse.permission_id & dps.permission_id) > 0
       AND dpse.meta_id = rr.meta_id
       AND dpse.set_id = rr.set_id
       AND dpse.permission_id = 524288 -- Change template
WHERE
        dpse.permission_data IS NOT NULL
       OR rr.set_id = 0
       OR urc.role_id = 0
ORDER BY dt.group_id

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

--
-- Procedure Create
-- dbo.GetTemplateGroupsWithNewPermissions
--
CREATE PROCEDURE GetTemplateGroupsWithNewPermissions @meta_id INT, @set_id INT AS
/*
 Retrieves a list of all templategroups, with a indicator of wether a particular permission-set may use it.
 The permission-set must still have the "Change template"-permission set, though. ( Not checked in this proc )
 Column 1: The templategroup
 Column 2: The name of the templategroup
 Column 3: > -1 if this set_id may use this.
*/
SELECT group_id,group_name,ISNULL(dpse.permission_data,-1)
FROM   templategroups tg
LEFT JOIN new_doc_permission_sets_ex dpse
       ON dpse.permission_data = tg.group_id
       AND dpse.meta_id = @meta_id
       AND dpse.set_id = @set_id
       AND dpse.permission_id = 524288
ORDER  BY CAST(ISNULL(dpse.permission_data,-1)+1  AS BIT) DESC, group_name

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE GetTemplateGroupsWithPermissions @meta_id INT, @set_id INT AS
/*
 Retrieves a list of all templategroups, with a indicator of wether a particular permission-set may use it.
 The permission-set must still have the "Change template"-permission set, though. ( Not checked in this proc )
 Column 1: The templategroup
 Column 2: The name of the templategroup
 Column 3: > -1 if this set_id may use this.
*/
SELECT group_id,group_name,ISNULL(dpse.permission_data,-1)
FROM   templategroups tg
LEFT JOIN doc_permission_sets_ex dpse
       ON dpse.permission_data = tg.group_id
       AND dpse.meta_id = @meta_id
       AND dpse.set_id = @set_id
       AND dpse.permission_id = 524288
ORDER  BY CAST(ISNULL(dpse.permission_data,-1)+1  AS BIT) DESC, group_name

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE getTemplates AS
select template_id, simple_name from templates

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE getTemplatesInGroup @grp_id INT AS
SELECT t.template_id,simple_name
FROM  templates t JOIN
  templates_cref c
ON  t.template_id = c.template_id
WHERE c.group_id = @grp_id
ORDER BY simple_name

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  ON    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE GetTextDocData @meta_id INT AS
SELECT t.template_id, simple_name, sort_order, t.group_id
FROM   text_docs t  
JOIN   templates c 
     ON t.template_id = c.template_id
WHERE meta_id = @meta_id

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE GetTextNumber @meta_id int,  @name int AS
/* selects text name @number from meta id @meta_id */
select text from 
 texts 
where
 meta_id = @meta_id  and name = @name

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE GetTexts
@meta_id int AS
select '#txt'+convert(varchar(5), name)+'#',name,type,text from texts where meta_id = @meta_id

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  ON    SET ANSI_NULLS  ON 
GO

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

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE GetUserId 
 @aUserId int
AS
 SELECT user_id 
 FROM users
 WHERE user_id  = @aUserId

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  ON    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE GetUserInfo
/* Returns all the information about a user. Used by adminsystem & conference system
*/
 @aUserId int
AS
 SELECT * 
 FROM users
 WHERE user_id = @aUserId 

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE GetUserPermissionSet @meta_id INT, @user_id INT AS
/*
 Finds out what is the most privileged permission_set a user has for a document.
 Column 1: The users most privileged set_id
 Column 2: The users permission-set for this set_id
 Column 3: The permissions for this document. ( At the time of this writing, the only permission there is is wether or not set_id 1 is more privileged than set_id 2, and it's stored in bit 0 )
 set_id's:
 0 - most privileged (full rights)
 1 & 2 - misc. They may be equal, and 1 may have permission to modify 2.
 3 - only read rights
 4 - least privileged (no rights)
*/
SELECT TOP 1 ISNULL((MIN(ISNULL(rr.set_id,4))*CAST(MIN(ISNULL(urc.role_id,1)) AS BIT)),4),
  ISNULL(dps.permission_id,0),
  ISNULL(m.permissions,0)
FROM   roles_rights rr
FULL JOIN  user_roles_crossref urc
      ON urc.user_id = @user_id
      AND rr.meta_id = @meta_id
      AND (
        rr.role_id = urc.role_id
       OR urc.role_id < 1
       )      
RIGHT JOIN  meta m
      ON m.meta_id = @meta_id
      AND urc.user_id = @user_id
      AND (
        rr.meta_id IS NOT NULL
       OR urc.role_id = 0
       )
LEFT JOIN doc_permission_sets dps
      ON dps.meta_id = @meta_id
      AND rr.set_id = dps.set_id
GROUP BY ISNULL(dps.permission_id,0),m.permissions
ORDER BY ISNULL((MIN(ISNULL(rr.set_id,4))*CAST(MIN(ISNULL(urc.role_id,1)) AS BIT)),4)

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE GetUserPermissionSetEx @meta_id INT, @user_id INT AS
/*
 Finds out what extended permissions (extra permissiondata) the user has for this document.
 Does not return correct data for a superadmin, or full admin, so check that first.
*/
SELECT dps.permission_id, dps.permission_data
FROM   roles_rights rr
JOIN   user_roles_crossref urc
      ON urc.user_id = @user_id
      AND rr.role_id = urc.role_id
JOIN  meta m
      ON m.meta_id = @meta_id
      AND rr.meta_id = m.meta_id
JOIN  doc_permission_sets_ex dps
      ON dps.meta_id = m.meta_id
      AND rr.set_id = dps.set_id

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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
      ON  rr.role_id = r.role_id
      AND rr.meta_id = @meta_id
LEFT JOIN user_roles_crossref urc
      ON r.role_id = urc.role_id
      AND urc.user_id = @user_id
WHERE r.role_id > 0
ORDER BY role_name

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE GetUserRolesIds
/* Returns the roles id:s for a user 
*/
 @aUserId int
 AS
 SELECT roles.role_id, role_name 
 FROM roles, user_roles_crossref 
 WHERE roles.role_id = user_roles_crossref.role_id
  AND user_roles_crossref.user_id = @aUserId

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  ON    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE GetUserTypes
/*
Used to generate a list with all type of users. Used from AdminUserProps
*/
  @lang_prefix VARCHAR(3)

 AS
 SELECT DISTINCT user_type, type_name 
 FROM user_types
 WHERE lang_prefix=@lang_prefix
GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE IncSessionCounter 
AS
      
    DECLARE @current_value int
  select @current_value = (select value from sys_data where type_id = 1)
  set @current_value  =  @current_value +1
 update sys_data
 set value = @current_value where type_id = 1
 
  return

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE InheritPermissions @new_meta_id INT, @parent_meta_id INT, @doc_type INT AS

/* Inherit permissions for new documents in the parent to the new document */
INSERT INTO doc_permission_sets
SELECT  @new_meta_id,
  ndps.set_id,
  ndps.permission_id
FROM   new_doc_permission_sets ndps
WHERE ndps.meta_id = @parent_meta_id

IF @doc_type = 2 BEGIN

/* Inherit permissions for new documents in the new document to the new document */
INSERT INTO new_doc_permission_sets
SELECT @new_meta_id,
  ndps.set_id,
  ndps.permission_id
FROM  new_doc_permission_sets ndps
WHERE ndps.meta_id = @parent_meta_id
 AND @doc_type = 2

/* Inherit permissions for new documents in the parent to the new document */
INSERT INTO doc_permission_sets_ex
SELECT @new_meta_id,
  ndpse.set_id,
  ndpse.permission_id,
  ndpse.permission_data
FROM  new_doc_permission_sets_ex ndpse
WHERE ndpse.meta_id = @parent_meta_id
 AND @doc_type = 2

/* Inherit permissions for new documents in the new document to the new document */
INSERT INTO new_doc_permission_sets_ex
SELECT @new_meta_id,
  ndpse.set_id,
  ndpse.permission_id,
  ndpse.permission_data
FROM  new_doc_permission_sets_ex ndpse
WHERE ndpse.meta_id = @parent_meta_id
 AND @doc_type = 2

END ELSE BEGIN

	DECLARE @permission1 INT
	DECLARE @permission2 INT

	SELECT @permission1 = (65535 & dps.permission_id) FROM doc_permission_sets dps WHERE dps.meta_id = @new_meta_id AND dps.set_id = 1
	SELECT @permission2 = (65535 & dps.permission_id) FROM doc_permission_sets dps WHERE dps.meta_id = @new_meta_id AND dps.set_id = 2

	DELETE FROM doc_permission_sets WHERE meta_id = @new_meta_id

	IF @permission1 IS NULL BEGIN
		SET @permission1 = 0
	END

	IF @permission2 IS NULL BEGIN
		SET @permission2 = 0
	END

	IF (@permission1 != 0) OR 1 IN (SELECT set_id FROM doc_permission_sets_ex WHERE meta_id = @parent_meta_id AND permission_id = 8 AND permission_data = @doc_type) BEGIN
		INSERT INTO doc_permission_sets VALUES(@new_meta_id, 1, 65536|@permission1)
	END

	IF (@permission2 != 0) OR 2 IN (SELECT set_id FROM doc_permission_sets_ex WHERE meta_id = @parent_meta_id AND permission_id = 8 AND permission_data = @doc_type) BEGIN
		INSERT INTO doc_permission_sets VALUES(@new_meta_id, 2, 65536|@permission2)
	END

END

INSERT INTO roles_rights
SELECT role_id, @new_meta_id, set_id
FROM  roles_rights
WHERE meta_id = @parent_meta_id





GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  ON    SET ANSI_NULLS  ON 
GO

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

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE IPAccessDelete
/*
 Deletes an Ip-access for a user. Used by the AdminIpAccess servlet
*/
 @ipAccessId int
AS
DELETE FROM IP_ACCESSES 
WHERE ip_access_id = @ipAccessId

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE IPAccessesGetAll AS
/*
Lets get all IPaccesses from db. Used  by the AdminIpAccesses
*/
SELECT ip.ip_access_id, ip.user_id, usr.login_name, ip.ip_start, ip.ip_end    
FROM IP_ACCESSES ip, USERS usr
WHERE ip.user_id = usr.user_id

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  ON    SET ANSI_NULLS  ON 
GO

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

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE ListConferences AS
select meta_id, meta_headline 
from meta 
where doc_type = 102

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE ListDocsByDate @listMod int,  @doc_type int, @startDate varchar(10), @endDate varchar(20), @lang_prefix varchar(3) AS
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
   where m.doc_type = @doc_type and activate = 1 and dt.lang_prefix = @lang_prefix
   order by m.date_modified
  end
  else begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_modified <= @endDate and activate = 1 and dt.lang_prefix = @lang_prefix
   order by m.date_modified
  end
 end
 else if ( @startDate != '0' ) begin
  if ( @endDate = '0' ) begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_modified >= @startDate and activate = 1 and dt.lang_prefix = @lang_prefix
   order by m.date_modified
  end
  else
  begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_modified <= @endDate and m.date_modified >= @startDate and activate = 1 and dt.lang_prefix = @lang_prefix
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
   where m.doc_type = @doc_type and activate = 1 and dt.lang_prefix = @lang_prefix
   order by m.date_created
  end
  else begin
   select m.meta_id, dt.type, m.meta_headline, m.date_created
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_created <= @endDate and activate = 1 and dt.lang_prefix = @lang_prefix
   order by m.date_created
  end
 end
 else if ( @startDate != '0' ) begin
  if ( @endDate = '0' ) begin
   select m.meta_id, dt.type, m.meta_headline, m.date_created
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_created >= @startDate and activate = 1 and dt.lang_prefix = @lang_prefix
   order by m.date_created
  end
  else
  begin
   select m.meta_id, dt.type, m.meta_headline, m.date_created
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_created <= @endDate and m.date_created >= @startDate and activate = 1 and dt.lang_prefix = @lang_prefix
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
   where m.doc_type = @doc_type and activate = 1 and m.date_modified != m.date_created and dt.lang_prefix = @lang_prefix
   order by m.date_modified
  end
  else begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_modified <= @endDate and activate = 1 and m.date_modified != m.date_created and dt.lang_prefix = @lang_prefix
   order by m.date_modified
  end
 end
 else if ( @startDate != '0' ) begin
  if ( @endDate = '0' ) begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_modified >= @startDate and activate = 1 and m.date_modified != m.date_created and dt.lang_prefix = @lang_prefix
   order by m.date_modified
  end
  else
  begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_modified <= @endDate and m.date_modified >= @startDate and activate = 1 and m.date_modified != m.date_created and dt.lang_prefix = @lang_prefix
   order by m.date_modified
  end
 end
end

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE ListDocsGetInternalDocTypes @lang_prefix VARCHAR(3) AS
/* selct all internal doc types */
select doc_type, type 
from doc_types
where doc_type <= 100
and lang_prefix = @lang_prefix

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE ListDocsGetInternalDocTypesValue AS
/* selct all internal doc types */
select doc_type
from doc_types
where doc_type <= 100

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE PhoneNbrDelete
/*
 Deletes an Ip-access for a user. Used by the PhoneNbrDelete
*/
 @phoneId int
AS
DELETE FROM PHONES 
WHERE phone_id = @phoneId

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE RemoveUserFromRole
 @userId int, @role_id int
AS
/* removes user from role */
DELETE 
FROM user_roles_crossref
WHERE user_id = @userId and role_id = @role_id

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE RoleAddNew
 @newRoleName char(25)
/* Adds a new role */
AS
DECLARE @newRoleId int
SELECT @newRoleId = MAX(r.role_id) + 1
FROM roles r
INSERT INTO roles (  role_id , role_name )
VALUES( @newRoleId , @newRoleName )
 

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE RoleAdminGetAll AS
/*
 Used by AdminRoles servlet to retrieve all roles except the Superadmin role
*/
SELECT role_id , role_name FROM ROLES
WHERE role_id != 0
ORDER BY role_name

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  OFF 
GO

CREATE PROCEDURE RoleCheckConferenceAllowed 
 @lang_prefix varchar(3),
 @lookForRoleId int 
AS
-- Checks if the role passed, is still avaible to use for the conference when 
-- a user tries to add himself in the conflogin servlet
DECLARE @bitNbrMaxValue int
SELECT @bitNbrMaxValue = 2  -- Max value the bit position we look for
SELECT  r.role_id, r.role_name
FROM   roles_permissions rp
JOIN  roles r
 ON rp.permission_id & r.permissions & @bitNbrMaxValue  != 0
 AND r.role_id = @lookForRoleId
WHERE lang_prefix = @lang_prefix

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE RoleDelete
/* Deletes an role from the role table. Used by the AdminRoles servlet
*/
 @roleId int
AS
DELETE FROM ROLES_RIGHTS WHERE ROLE_ID = @roleId
DELETE FROM user_roles_crossref WHERE ROLE_ID =@roleId
DELETE FROM ROLES WHERE ROLE_ID = @roleId

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE RoleGetAllApartFromRole @role_id int AS
/*
 Used by AdminRoleBelongings servlet to retrieve all roles except the Superadmin role and role role_id
*/
SELECT role_id , role_name FROM ROLES
WHERE role_id != 0 and role_id != @role_id
ORDER BY role_id

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  OFF 
GO

CREATE PROCEDURE RoleGetConferenceAllowed
 @lang_prefix varchar(3)
AS
/*
  select all roles for a certain language which has the the bitmaskflag
  set for the selfregister in conference permission.
  As the permissionId = 2, then it is the bitposition nbr 2 we have
 to look at. The maximun value for bitpos 2 is 2, so 
  Eftersom permissionid är 2 --> så är det bit nr 2 vi är ute efter.
  Maxvärdet för bit nr 2 är 2, bit nr 1 = 1, bit nr 3 = 4, bit nr 4  
*/
--SELECT  ISNULL(r.permissions & rp.permission_id,0) AS value,rp.permission_id,rp.description
DECLARE @bitNbrMaxValue int
SELECT @bitNbrMaxValue = 2  -- Max value the bit position we look for
SELECT  r.role_id, r.role_name
FROM   roles_permissions rp
JOIN  roles r
 ON rp.permission_id & r.permissions & @bitNbrMaxValue  != 0
 --AND r.role_id = 5
WHERE lang_prefix = @lang_prefix

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE RoleGetPermissionsByLanguage @lang_prefix varchar(3) AS
/*select permissions by language prefix.*/
select permission_id, description
from roles_permissions 
where lang_prefix = @lang_prefix
order by permission_id

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE RoleGetPermissionsFromRole @role_id int, @lang_prefix varchar(3) AS
/*
  select rolepermission from role id
*/
SELECT  ISNULL(r.permissions & rp.permission_id,0) AS value,rp.permission_id,rp.description
FROM   roles_permissions rp
LEFT JOIN  roles r
     ON rp.permission_id & r.permissions != 0
     AND r.role_id = @role_id
WHERE lang_prefix = @lang_prefix

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE RolePermissionsAddNew
 @newRoleName char(25), @permissions int
/* Adds a new role */
AS
DECLARE @newRoleId int
SELECT @newRoleId = MAX(r.role_id) + 1
FROM roles r
INSERT INTO roles (  role_id , role_name, permissions )
VALUES( @newRoleId , @newRoleName, @permissions )

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE RoleUpdatePermissions @role_id int,  @permissions int AS
/* update permissions for role */
update roles 
Set permissions = @permissions 
where role_id = @role_id

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE SearchDocs
  @user_id INT,
  @keyword_string VARCHAR(128),  -- Must be large enough to encompass an entire searchstring.
  @and_mode VARCHAR(3),  -- 'AND' or something else
  @doc_types_string VARCHAR(30), -- Must be large enough to encompass all possible doc_types, commaseparated and expressed in decimal notation.
  @fromdoc INT,
  @num_docs INT,
  @sortorder VARCHAR(256),  -- doc_type, date_modified, date_created, archived_datetime, activated_datetime, meta_id, meta_headline
  @created_startdate DATETIME,
  @created_enddate DATETIME,
  @modified_startdate DATETIME,
  @modified_enddate DATETIME,
  @activated_startdate DATETIME,
  @activated_enddate DATETIME,
  @archived_startdate DATETIME,
  @archived_enddate DATETIME,
  @only_addable TINYINT  -- 1 to show only documents the user may add.
AS
/*
SET @keyword_string = 'kreiger'+CHAR(13)+'test'
SET @doc_types_string = '2,5,6,7,8,101,102'
SET @created_startdate = '1999-09-01'
SET @created_enddate = '2000-01-01'
SET @modified_startdate = ''
SET @modified_enddate = ''
SET @activated_startdate = ''
SET @activated_enddate = ''
SET @archived_startdate = ''
SET @archived_enddate = ''
SET @and_mode = 'OR'
SET @user_id = 98
SET @fromdoc = 1
SET @num_docs = 10
SET @sortorder = 'doc_type,meta_id DESC'
*/
SET nocount on
SET @fromdoc = @fromdoc - 1
DECLARE @created_sd DATETIME,
  @modified_sd DATETIME,
  @activated_sd DATETIME,
  @archived_sd DATETIME,
  @created_ed DATETIME,
  @modified_ed DATETIME,
  @activated_ed DATETIME,
  @archived_ed DATETIME
IF (@created_startdate = '') BEGIN
 SET @created_sd = '1753-01-01'
END ELSE BEGIN
 SET @created_sd = @created_startdate
END
IF (@modified_startdate = '') BEGIN
 SET @modified_sd = '1753-01-01'
END ELSE BEGIN
 SET @modified_sd = @modified_startdate
END
IF (@activated_startdate = '') BEGIN
 SET @activated_sd = '1753-01-01'
END ELSE BEGIN
 SET @activated_sd = @activated_startdate
END
IF (@archived_startdate = '') BEGIN
 SET @archived_sd = '1753-01-01'
END ELSE BEGIN
 SET @archived_sd = @archived_startdate
END
IF (@created_enddate = '') BEGIN
 IF (@created_startdate = '') BEGIN
  SET @created_ed = '1753-01-01'
 END ELSE BEGIN
  SET @created_ed = '9999-12-31'
 END
END ELSE BEGIN
 SET @created_ed = @created_enddate
END
IF (@modified_enddate = '') BEGIN
 IF (@modified_startdate = '') BEGIN
  SET @modified_ed = '1753-01-01'
 END ELSE BEGIN
  SET @modified_ed = '9999-12-31'
 END
END ELSE BEGIN
 SET @modified_ed = @modified_enddate
END
IF (@activated_enddate = '') BEGIN
 IF (@activated_startdate = '') BEGIN
  SET @activated_ed = '1753-01-01'
 END ELSE BEGIN
  SET @activated_ed = '9999-12-31'
 END
END ELSE BEGIN
 SET @activated_ed = @activated_enddate
END
IF (@archived_enddate = '') BEGIN
 IF (@archived_startdate = '') BEGIN
  SET @archived_ed = '1753-01-01'
 END ELSE BEGIN
  SET @archived_ed = '9999-12-31'
 END
END ELSE BEGIN
 SET @archived_ed = @archived_enddate
END
CREATE TABLE #doc_types (
 doc_type INT 
)
CREATE TABLE #keywords (
  keyword VARCHAR(30)
)
DECLARE @substring VARCHAR(30)
DECLARE @index INT
DECLARE @endindex INT
IF LEN(@doc_types_string) > 0 BEGIN
 SET @index = 1
 WHILE @index <= LEN(@doc_types_string) BEGIN
  SET @endindex = CHARINDEX(',',@doc_types_string,@index+1)
  IF @endindex = 0 BEGIN
   SET @endindex = LEN(@doc_types_string)+1
  END --IF
  SET @substring = SUBSTRING(@doc_types_string,@index,@endindex-@index)
  INSERT INTO #doc_types VALUES (@substring)
  SET @index = @endindex + 1
 END -- WHILE
END -- IF
IF LEN(@keyword_string) > 0 BEGIN
 SET @index = 1
 WHILE @index <= LEN(@keyword_string) BEGIN
  SET @endindex = CHARINDEX(CHAR(13),@keyword_string,@index+1)
  IF @endindex = 0 BEGIN
   SET @endindex = LEN(@keyword_string)+1
  END --IF
  SET @substring = SUBSTRING(@keyword_string,@index,@endindex-@index)
  INSERT INTO #keywords VALUES (@substring)
  SET @index = @endindex + 1
 END -- WHILE
END -- IF
DECLARE @num_keywords INT
SELECT @num_keywords = COUNT(keyword) from #keywords
/* A table to contain all the pages matched, one row per keyword matched */
CREATE TABLE #keywords_matched (
 meta_id INT,
 doc_type INT,
 meta_headline VARCHAR(256),
 meta_text VARCHAR(1024),
 date_created DATETIME,
 date_modified DATETIME,
 date_activated DATETIME,
 date_archived DATETIME,
 archive TINYINT,
 shared TINYINT,
 show_meta TINYINT,
 disable_search TINYINT,
 keyword VARCHAR(30) 
)
INSERT INTO #keywords_matched
SELECT  
  m.meta_id,
  m.doc_type,
  m.meta_headline,
  m.meta_text,
  m.date_created,
  m.date_modified,
  CAST(activated_date+' '+activated_time AS DATETIME),
  CAST(archived_date+' '+archived_time AS DATETIME),
  archive,
  shared,
  show_meta,
  disable_search,
  k.keyword
FROM
  meta m
JOIN
  #doc_types dt  ON m.doc_type = dt.doc_type
     AND activate = 1
     AND (
       (
        date_created >= @created_sd
       AND date_created <= @created_ed
      ) OR (
        date_modified >= @modified_sd
       AND date_modified <= @modified_ed
      ) OR (
        CAST(activated_date+' '+activated_time AS DATETIME) >= @activated_sd
       AND CAST(activated_date+' '+activated_time AS DATETIME) <= @activated_ed
      ) OR (
        CAST(archived_date+' '+archived_time AS DATETIME) >= @archived_sd
       AND CAST(archived_date+' '+archived_time AS DATETIME) <= @archived_ed
      ) OR (
        @created_startdate = ''
       AND @created_enddate = ''
       AND @modified_startdate = ''
       AND @modified_enddate = ''
       AND @activated_startdate = ''
       AND @activated_enddate = ''
       AND @archived_startdate = ''
       AND @archived_enddate = ''
      )
     )
LEFT JOIN
  roles_rights rr  ON rr.meta_id = m.meta_id
JOIN
  user_roles_crossref urc ON urc.user_id = @user_id
     AND (
       urc.role_id = 0   -- Superadmin may always see everything
      OR (
        rr.role_id = urc.role_id  -- As does a user...
       AND (
         rr.set_id < 3   -- ... with a privileged role
        OR (
          (
           rr.set_id = 3   -- ... or a user with read-rights
          OR show_meta != 0   -- ... or if the document lets anyone see
         )
         AND m.disable_search = 0   -- ... that is, if searching is not turned off for this document
         AND (
           m.shared != 0   -- ... and the document is shared
          OR @only_addable = 0  -- ... unless we've selected to only see addable (shared) documents.
         )
        )
       )
      )
     )
LEFT JOIN
  texts t   ON m.meta_id = t.meta_id
JOIN
  #keywords k  ON m.meta_headline  LIKE '%'+k.keyword+'%'
     OR m.meta_text  LIKE '%'+k.keyword+'%'
     OR t.text   LIKE '%'+k.keyword+'%'
GROUP BY
  m.meta_id,
  k.keyword,
  m.doc_type,
  m.meta_headline,
  m.meta_text,
  m.date_created,
  m.date_modified,
  CAST(activated_date+' '+activated_time AS DATETIME),
  CAST(archived_date+' '+archived_time AS DATETIME),
  archive,
  shared,
  show_meta,
  disable_search
DECLARE @doc_count INT
SET @doc_count = @@ROWCOUNT
IF @and_mode = 'AND' BEGIN
 DELETE FROM #keywords_matched
 WHERE meta_id IN (
  SELECT meta_id
  FROM #keywords_matched
  GROUP BY meta_id
  HAVING (COUNT(keyword) < @num_keywords)
 )
 SET @doc_count = @doc_count - @@ROWCOUNT
END
DECLARE @eval VARCHAR(2000)
SET @eval = ('
IF '+STR(@fromdoc)+' > 0 BEGIN
 DELETE FROM #keywords_matched
 WHERE meta_id IN (
  SELECT TOP '+STR(@fromdoc)+' meta_id FROM #keywords_matched
  ORDER BY meta_id
 )
END
SELECT TOP '+STR(@num_docs)+' 
  meta_id,
  doc_type,
  meta_headline,
  meta_text,
  date_created,
  date_modified,
  ISNULL(CONVERT(VARCHAR,NULLIF(date_activated,''''),121),'''') AS date_activated,
  ISNULL(CONVERT(VARCHAR,NULLIF(date_archived,''''),121),'''') AS date_archived,
  archive,
  shared,
  show_meta,
  disable_search,
  ' + STR(@doc_count) + ' AS doc_count
FROM
  #keywords_matched
GROUP BY
  meta_id,
  doc_type,
  meta_headline,
  meta_text,
  date_created,
  date_modified,
  date_activated,
  date_archived,
  archive,
  shared,
  show_meta,
  disable_search
ORDER BY 
  '+@sortorder)
EXEC (@eval)
DROP TABLE #keywords
DROP TABLE #doc_types
DROP TABLE #keywords_matched

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE ServerMasterGet AS
DECLARE @smname VARCHAR(80)
DECLARE @smaddress VARCHAR(80)
SELECT @smname = value FROM sys_data WHERE type_id = 4
SELECT @smaddress = value FROM sys_data WHERE type_id = 5
SELECT @smname,@smaddress

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE ServerMasterSet 
@smname VARCHAR(80), 
@smaddress VARCHAR(80)  AS
UPDATE sys_data SET value = @smname WHERE type_id = 4
UPDATE sys_data SET value = @smaddress WHERE type_id = 5

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE SetDocPermissionSet @meta_id INT, @set_id INT, @permission_id INT AS
/*
 Updates a permissionset for a document.
*/
-- Delete the previous value
DELETE FROM doc_permission_sets
WHERE meta_id = @meta_id
AND  set_id = @set_id
-- Insert new value
INSERT INTO doc_permission_sets
VALUES (@meta_id,@set_id,@permission_id)

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE SetDocPermissionSetEx @meta_id INT, @set_id INT, @permission_id INT, @permission_data INT AS
/*
 Updates an extended permissionset for a document.
*/
-- Insert new value
INSERT INTO doc_permission_sets_ex
VALUES (@meta_id,@set_id,@permission_id, @permission_data)

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  ON    SET ANSI_NULLS  OFF 
GO

CREATE PROCEDURE SetInclude @meta_id INT, @include_id INT, @included_meta_id INT AS

DELETE FROM	includes 
WHERE 	meta_id = @meta_id
	AND 	include_id = @include_id

INSERT INTO	includes	 (meta_id, include_id, included_meta_id)
VALUES	(@meta_id, @include_id, @included_meta_id)

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  ON    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE SetNewDocPermissionSet @meta_id INT, @set_id INT, @permission_id INT AS
/*
 Updates a permissionset for a document.
*/
-- Delete the previous value
DELETE FROM new_doc_permission_sets
WHERE meta_id = @meta_id
AND  set_id = @set_id
-- Insert new value
INSERT INTO new_doc_permission_sets
VALUES (@meta_id,@set_id,@permission_id)

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  ON    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE SetNewDocPermissionSetEx @meta_id INT, @set_id INT, @permission_id INT, @permission_data INT AS
/*
 Updates an extended permissionset for a document.
*/
-- Insert new value
INSERT INTO new_doc_permission_sets_ex
VALUES (@meta_id,@set_id,@permission_id, @permission_data)

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE SetRoleDocPermissionSetId @role_id INT, @meta_id INT, @set_id INT AS
-- First delete the previous set_id
DELETE FROM   roles_rights 
WHERE   meta_id = @meta_id
  AND  role_id = @role_id
-- Now insert the new one
IF @set_id < 4
BEGIN
 INSERT INTO roles_rights (role_id, meta_id, set_id)
 VALUES ( @role_id, @meta_id, @set_id )
END

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE SetSessionCounterDate
   @new_date varchar(20)
AS
      
 update sys_data
 set value = @new_date where type_id = 2
 
  return

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  OFF 
GO

CREATE PROCEDURE SortOrder_GetExistingDocs 

	@langPrefixString char(3)
AS
/*
This sproc is used by the GetExistingDoc servlet, it takes the lang id string as argument and returns 
the sortorder options  display text for that language. 
Example: SortOrder_GetExistingDocs 'se'.

*/

SELECT sType.sort_by_type , display.display_name
FROM lang_prefixes lang
INNER JOIN display_name display
	ON display.lang_id = lang.lang_id
	AND lang.lang_prefix = @langPrefixString
INNER JOIN sort_by sType
	ON sType.sort_by_id = display.sort_by_id
GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE SystemMessageGet AS
/*
 Used by the AdminSystemMessage servlet to retrieve the systemmessage
*/
SELECT s.value
FROM sys_data s
WHERE s.type_id = 3

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE SystemMessageSet
/*
Lets update the system message table. Used by the AdminSystemMessage servlet
*/
 @newMsg varchar(80)
AS
UPDATE sys_data
SET value = @newMsg
WHERE type_id = 3

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE [UpdateParentsDateModified] @meta_id INT AS
UPDATE meta
SET date_modified = GETDATE() 
FROM meta JOIN childs c
ON meta.meta_id = c.meta_id 
WHERE c.to_meta_id = @meta_id

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE WebMasterGet AS
DECLARE @wmname VARCHAR(80)
DECLARE @wmaddress VARCHAR(80)
SELECT @wmname = value FROM sys_data WHERE type_id = 6
SELECT @wmaddress = value FROM sys_data WHERE type_id = 7
SELECT @wmname,@wmaddress

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE WebMasterSet 
@wmname VARCHAR(80), 
@wmaddress VARCHAR(80)  AS
UPDATE sys_data SET value = @wmname WHERE type_id = 6
UPDATE sys_data SET value = @wmaddress WHERE type_id = 7

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

