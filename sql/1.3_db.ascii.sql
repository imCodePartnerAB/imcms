ALTER TABLE [dbo].[browser_docs] DROP CONSTRAINT FK_browser_docs_browsers
GO

ALTER TABLE [dbo].[meta_classification] DROP CONSTRAINT FK_meta_classification_classification
GO

ALTER TABLE [dbo].[childs] DROP CONSTRAINT FK_childs_meta
GO

ALTER TABLE [dbo].[childs] DROP CONSTRAINT FK_childs_meta1
GO

ALTER TABLE [dbo].[doc_permission_sets] DROP CONSTRAINT FK_doc_permission_sets_meta
GO

ALTER TABLE [dbo].[doc_permission_sets_ex] DROP CONSTRAINT FK_doc_permission_sets_ex_meta
GO

ALTER TABLE [dbo].[fileupload_docs] DROP CONSTRAINT FK_fileupload_docs_meta
GO

ALTER TABLE [dbo].[frameset_docs] DROP CONSTRAINT FK_frameset_docs_meta
GO

ALTER TABLE [dbo].[images] DROP CONSTRAINT FK_images_meta
GO

ALTER TABLE [dbo].[meta_classification] DROP CONSTRAINT FK_meta_classification_meta
GO

ALTER TABLE [dbo].[text_docs] DROP CONSTRAINT FK_text_docs_meta
GO

ALTER TABLE [dbo].[texts] DROP CONSTRAINT FK_texts_meta
GO

ALTER TABLE [dbo].[user_roles_crossref] DROP CONSTRAINT FK_user_roles_crossref_roles
GO

ALTER TABLE [dbo].[templates_cref] DROP CONSTRAINT FK_templates_cref_templates
GO

ALTER TABLE [dbo].[text_docs] DROP CONSTRAINT FK_text_docs_templates
GO

/****** Object:  Stored Procedure dbo.Classification_Fix    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[Classification_Fix]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[Classification_Fix]
GO

/****** Object:  Stored Procedure dbo.AddBrowserStatistics    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[AddBrowserStatistics]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[AddBrowserStatistics]
GO

/****** Object:  Stored Procedure dbo.AddScreenStatistics    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[AddScreenStatistics]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[AddScreenStatistics]
GO

/****** Object:  Stored Procedure dbo.AddStatisticsCount    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[AddStatisticsCount]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[AddStatisticsCount]
GO

/****** Object:  Stored Procedure dbo.AddUserRole    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[AddUserRole]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[AddUserRole]
GO

/****** Object:  Stored Procedure dbo.AddVersionStatistics    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[AddVersionStatistics]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[AddVersionStatistics]
GO

/****** Object:  Stored Procedure dbo.CheckAdminRights    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[CheckAdminRights]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[CheckAdminRights]
GO

/****** Object:  Stored Procedure dbo.CheckExistsInMenu    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[CheckExistsInMenu]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[CheckExistsInMenu]
GO

/****** Object:  Stored Procedure dbo.ClassificationAdd    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[ClassificationAdd]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[ClassificationAdd]
GO

/****** Object:  Stored Procedure dbo.DeleteDocPermissionSetEx    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[DeleteDocPermissionSetEx]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[DeleteDocPermissionSetEx]
GO

/****** Object:  Stored Procedure dbo.DelUser    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[DelUser]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[DelUser]
GO

/****** Object:  Stored Procedure dbo.DelUserRoles    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[DelUserRoles]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[DelUserRoles]
GO

/****** Object:  Stored Procedure dbo.getBrowserDocChilds    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[getBrowserDocChilds]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[getBrowserDocChilds]
GO

/****** Object:  Stored Procedure dbo.GetChilds    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetChilds]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetChilds]
GO

/****** Object:  Stored Procedure dbo.getDocs    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[getDocs]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[getDocs]
GO

/****** Object:  Stored Procedure dbo.GetDocTypesForUser    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetDocTypesForUser]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetDocTypesForUser]
GO

/****** Object:  Stored Procedure dbo.GetDocTypesWithPermissions    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetDocTypesWithPermissions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetDocTypesWithPermissions]
GO

/****** Object:  Stored Procedure dbo.GetImgs    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetImgs]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetImgs]
GO

/****** Object:  Stored Procedure dbo.getMenuDocChilds    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[getMenuDocChilds]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[getMenuDocChilds]
GO

/****** Object:  Stored Procedure dbo.GetPermissionSet    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetPermissionSet]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetPermissionSet]
GO

/****** Object:  Stored Procedure dbo.GetTemplateGroupsForUser    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetTemplateGroupsForUser]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetTemplateGroupsForUser]
GO

/****** Object:  Stored Procedure dbo.GetTemplateGroupsWithPermissions    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetTemplateGroupsWithPermissions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetTemplateGroupsWithPermissions]
GO

/****** Object:  Stored Procedure dbo.getTemplatesInGroup    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[getTemplatesInGroup]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[getTemplatesInGroup]
GO

/****** Object:  Stored Procedure dbo.GetTextDocData    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetTextDocData]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetTextDocData]
GO

/****** Object:  Stored Procedure dbo.GetTexts    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetTexts]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetTexts]
GO

/****** Object:  Stored Procedure dbo.GetUserPermissionSet    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetUserPermissionSet]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserPermissionSet]
GO

/****** Object:  Stored Procedure dbo.GetUserPermissionSetEx    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetUserPermissionSetEx]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserPermissionSetEx]
GO

/****** Object:  Stored Procedure dbo.GetUserRoles    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetUserRoles]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserRoles]
GO

/****** Object:  Stored Procedure dbo.GetUserRolesDocPermissions    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetUserRolesDocPermissions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserRolesDocPermissions]
GO

/****** Object:  Stored Procedure dbo.GetUserRolesIds    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetUserRolesIds]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserRolesIds]
GO

/****** Object:  Stored Procedure dbo.GetUsersWhoBelongsToRole    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetUsersWhoBelongsToRole]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUsersWhoBelongsToRole]
GO

/****** Object:  Stored Procedure dbo.InheritPermissions    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[InheritPermissions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[InheritPermissions]
GO

/****** Object:  Stored Procedure dbo.PermissionsGetPermission    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[PermissionsGetPermission]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[PermissionsGetPermission]
GO

/****** Object:  Stored Procedure dbo.RemoveUserFromRole    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[RemoveUserFromRole]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RemoveUserFromRole]
GO

/****** Object:  Stored Procedure dbo.RoleCountAffectedUsers    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[RoleCountAffectedUsers]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleCountAffectedUsers]
GO

/****** Object:  Stored Procedure dbo.RoleDelete    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[RoleDelete]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleDelete]
GO

/****** Object:  Stored Procedure dbo.RoleDeleteViewAffectedUsers    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[RoleDeleteViewAffectedUsers]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleDeleteViewAffectedUsers]
GO

/****** Object:  Stored Procedure dbo.SetDocPermissionSet    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[SetDocPermissionSet]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SetDocPermissionSet]
GO

/****** Object:  Stored Procedure dbo.SetDocPermissionSetEx    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[SetDocPermissionSetEx]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SetDocPermissionSetEx]
GO

/****** Object:  Stored Procedure dbo.UpdateParentsDateModified    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[UpdateParentsDateModified]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[UpdateParentsDateModified]
GO

/****** Object:  Stored Procedure dbo.UpdateTemplateTextsAndImages    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[UpdateTemplateTextsAndImages]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[UpdateTemplateTextsAndImages]
GO

/****** Object:  Stored Procedure dbo.AddNewuser    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[AddNewuser]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[AddNewuser]
GO

/****** Object:  Stored Procedure dbo.AddStatistics    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[AddStatistics]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[AddStatistics]
GO

/****** Object:  Stored Procedure dbo.ChangeUserActiveStatus    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[ChangeUserActiveStatus]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[ChangeUserActiveStatus]
GO

/****** Object:  Stored Procedure dbo.Classification_Get_All    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[Classification_Get_All]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[Classification_Get_All]
GO

/****** Object:  Stored Procedure dbo.DeleteNewDocPermissionSetEx    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[DeleteNewDocPermissionSetEx]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[DeleteNewDocPermissionSetEx]
GO

/****** Object:  Stored Procedure dbo.FindMetaId    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[FindMetaId]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[FindMetaId]
GO

/****** Object:  Stored Procedure dbo.FindUserName    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[FindUserName]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[FindUserName]
GO

/****** Object:  Stored Procedure dbo.GetAllRoles    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetAllRoles]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetAllRoles]
GO

/****** Object:  Stored Procedure dbo.GetAllUsers    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetAllUsers]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetAllUsers]
GO

/****** Object:  Stored Procedure dbo.GetAllUsersInList    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetAllUsersInList]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetAllUsersInList]
GO

/****** Object:  Stored Procedure dbo.GetCategoryUsers    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetCategoryUsers]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetCategoryUsers]
GO

/****** Object:  Stored Procedure dbo.GetCurrentSessionCounter    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetCurrentSessionCounter]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetCurrentSessionCounter]
GO

/****** Object:  Stored Procedure dbo.GetCurrentSessionCounterDate    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetCurrentSessionCounterDate]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetCurrentSessionCounterDate]
GO

/****** Object:  Stored Procedure dbo.GetDocType    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetDocType]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetDocType]
GO

/****** Object:  Stored Procedure dbo.GetDocTypes    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetDocTypes]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetDocTypes]
GO

/****** Object:  Stored Procedure dbo.GetDocTypesWithNewPermissions    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetDocTypesWithNewPermissions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetDocTypesWithNewPermissions]
GO

/****** Object:  Stored Procedure dbo.GetHighestUserId    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetHighestUserId]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetHighestUserId]
GO

/****** Object:  Stored Procedure dbo.GetLangPrefix    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetLangPrefix]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetLangPrefix]
GO

/****** Object:  Stored Procedure dbo.GetLangPrefixFromId    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetLangPrefixFromId]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetLangPrefixFromId]
GO

/****** Object:  Stored Procedure dbo.GetLanguageList    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetLanguageList]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetLanguageList]
GO

/****** Object:  Stored Procedure dbo.getLanguages    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[getLanguages]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[getLanguages]
GO

/****** Object:  Stored Procedure dbo.GetMetaPathInfo    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetMetaPathInfo]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetMetaPathInfo]
GO

/****** Object:  Stored Procedure dbo.GetNewPermissionSet    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetNewPermissionSet]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetNewPermissionSet]
GO

/****** Object:  Stored Procedure dbo.GetNoOfTemplates    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetNoOfTemplates]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetNoOfTemplates]
GO

/****** Object:  Stored Procedure dbo.GetRolesDocPermissions    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetRolesDocPermissions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetRolesDocPermissions]
GO

/****** Object:  Stored Procedure dbo.getTemplategroups    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[getTemplategroups]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[getTemplategroups]
GO

/****** Object:  Stored Procedure dbo.GetTemplateGroupsWithNewPermissions    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetTemplateGroupsWithNewPermissions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetTemplateGroupsWithNewPermissions]
GO

/****** Object:  Stored Procedure dbo.getTemplates    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[getTemplates]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[getTemplates]
GO

/****** Object:  Stored Procedure dbo.GetUserCreateDate    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetUserCreateDate]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserCreateDate]
GO

/****** Object:  Stored Procedure dbo.GetUserId    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetUserId]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserId]
GO

/****** Object:  Stored Procedure dbo.GetUserIdFromName    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetUserIdFromName]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserIdFromName]
GO

/****** Object:  Stored Procedure dbo.GetUserInfo    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetUserInfo]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserInfo]
GO

/****** Object:  Stored Procedure dbo.GetUserNames    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetUserNames]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserNames]
GO

/****** Object:  Stored Procedure dbo.GetUserPassword    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetUserPassword]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserPassword]
GO

/****** Object:  Stored Procedure dbo.GetUserPhoneNumbers    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetUserPhoneNumbers]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserPhoneNumbers]
GO

/****** Object:  Stored Procedure dbo.GetUserPhones    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetUserPhones]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserPhones]
GO

/****** Object:  Stored Procedure dbo.GetUserType    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetUserType]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserType]
GO

/****** Object:  Stored Procedure dbo.GetUserTypes    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[GetUserTypes]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserTypes]
GO

/****** Object:  Stored Procedure dbo.IncSessionCounter    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[IncSessionCounter]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[IncSessionCounter]
GO

/****** Object:  Stored Procedure dbo.IPAccessAdd    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[IPAccessAdd]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[IPAccessAdd]
GO

/****** Object:  Stored Procedure dbo.IPAccessDelete    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[IPAccessDelete]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[IPAccessDelete]
GO

/****** Object:  Stored Procedure dbo.IPAccessesGetAll    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[IPAccessesGetAll]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[IPAccessesGetAll]
GO

/****** Object:  Stored Procedure dbo.IPAccessUpdate    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[IPAccessUpdate]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[IPAccessUpdate]
GO

/****** Object:  Stored Procedure dbo.ListConferences    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[ListConferences]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[ListConferences]
GO

/****** Object:  Stored Procedure dbo.ListDocsGetInternalDocTypes    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[ListDocsGetInternalDocTypes]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[ListDocsGetInternalDocTypes]
GO

/****** Object:  Stored Procedure dbo.ListDocsGetInternalDocTypesValue    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[ListDocsGetInternalDocTypesValue]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[ListDocsGetInternalDocTypesValue]
GO

/****** Object:  Stored Procedure dbo.phoneNbrAdd    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[phoneNbrAdd]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[phoneNbrAdd]
GO

/****** Object:  Stored Procedure dbo.PhoneNbrDelete    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[PhoneNbrDelete]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[PhoneNbrDelete]
GO

/****** Object:  Stored Procedure dbo.PhoneNbrUpdate    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[PhoneNbrUpdate]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[PhoneNbrUpdate]
GO

/****** Object:  Stored Procedure dbo.RoleAddNew    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[RoleAddNew]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleAddNew]
GO

/****** Object:  Stored Procedure dbo.RoleAdminGetAll    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[RoleAdminGetAll]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleAdminGetAll]
GO

/****** Object:  Stored Procedure dbo.RoleCount    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[RoleCount]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleCount]
GO

/****** Object:  Stored Procedure dbo.RoleDeleteViewAffectedMetaIds    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[RoleDeleteViewAffectedMetaIds]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleDeleteViewAffectedMetaIds]
GO

/****** Object:  Stored Procedure dbo.RoleFindName    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[RoleFindName]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleFindName]
GO

/****** Object:  Stored Procedure dbo.RoleGetAllApartFromRole    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[RoleGetAllApartFromRole]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleGetAllApartFromRole]
GO

/****** Object:  Stored Procedure dbo.RoleGetName    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[RoleGetName]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleGetName]
GO

/****** Object:  Stored Procedure dbo.RoleGetPermissionsByLanguage    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[RoleGetPermissionsByLanguage]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleGetPermissionsByLanguage]
GO

/****** Object:  Stored Procedure dbo.RoleGetPermissionsFromRole    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[RoleGetPermissionsFromRole]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleGetPermissionsFromRole]
GO

/****** Object:  Stored Procedure dbo.RolePermissionsAddNew    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[RolePermissionsAddNew]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RolePermissionsAddNew]
GO

/****** Object:  Stored Procedure dbo.RoleUpdateName    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[RoleUpdateName]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleUpdateName]
GO

/****** Object:  Stored Procedure dbo.RoleUpdatePermissions    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[RoleUpdatePermissions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[RoleUpdatePermissions]
GO

/****** Object:  Stored Procedure dbo.SetNewDocPermissionSet    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[SetNewDocPermissionSet]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SetNewDocPermissionSet]
GO

/****** Object:  Stored Procedure dbo.SetNewDocPermissionSetEx    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[SetNewDocPermissionSetEx]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SetNewDocPermissionSetEx]
GO

/****** Object:  Stored Procedure dbo.SetRoleDocPermissionSetId    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[SetRoleDocPermissionSetId]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SetRoleDocPermissionSetId]
GO

/****** Object:  Stored Procedure dbo.SetSessionCounterDate    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[SetSessionCounterDate]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SetSessionCounterDate]
GO

/****** Object:  Stored Procedure dbo.SystemMessageGet    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[SystemMessageGet]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SystemMessageGet]
GO

/****** Object:  Stored Procedure dbo.SystemMessageSet    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[SystemMessageSet]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SystemMessageSet]
GO

/****** Object:  Stored Procedure dbo.UpdateUser    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[UpdateUser]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[UpdateUser]
GO

/****** Object:  Stored Procedure dbo.UserPrefsChange    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[UserPrefsChange]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[UserPrefsChange]
GO

/****** Object:  Table [dbo].[browser_docs]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[browser_docs]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[browser_docs]
GO

/****** Object:  Table [dbo].[childs]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[childs]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[childs]
GO

/****** Object:  Table [dbo].[doc_permission_sets]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[doc_permission_sets]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[doc_permission_sets]
GO

/****** Object:  Table [dbo].[doc_permission_sets_ex]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[doc_permission_sets_ex]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[doc_permission_sets_ex]
GO

/****** Object:  Table [dbo].[fileupload_docs]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[fileupload_docs]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[fileupload_docs]
GO

/****** Object:  Table [dbo].[frameset_docs]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[frameset_docs]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[frameset_docs]
GO

/****** Object:  Table [dbo].[images]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[images]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[images]
GO

/****** Object:  Table [dbo].[meta_classification]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[meta_classification]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[meta_classification]
GO

/****** Object:  Table [dbo].[templates_cref]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[templates_cref]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[templates_cref]
GO

/****** Object:  Table [dbo].[text_docs]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[text_docs]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[text_docs]
GO

/****** Object:  Table [dbo].[texts]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[texts]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[texts]
GO

/****** Object:  Table [dbo].[user_roles_crossref]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[user_roles_crossref]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[user_roles_crossref]
GO

/****** Object:  Table [dbo].[browsers]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[browsers]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[browsers]
GO

/****** Object:  Table [dbo].[classification]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[classification]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[classification]
GO

/****** Object:  Table [dbo].[doc_permissions]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[doc_permissions]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[doc_permissions]
GO

/****** Object:  Table [dbo].[doc_types]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[doc_types]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[doc_types]
GO

/****** Object:  Table [dbo].[ip_accesses]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[ip_accesses]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[ip_accesses]
GO

/****** Object:  Table [dbo].[lang_prefixes]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[lang_prefixes]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[lang_prefixes]
GO

/****** Object:  Table [dbo].[languages]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[languages]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[languages]
GO

/****** Object:  Table [dbo].[main_log]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[main_log]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[main_log]
GO

/****** Object:  Table [dbo].[meta]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[meta]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[meta]
GO

/****** Object:  Table [dbo].[mime_types]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[mime_types]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[mime_types]
GO

/****** Object:  Table [dbo].[new_doc_permission_sets]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[new_doc_permission_sets]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[new_doc_permission_sets]
GO

/****** Object:  Table [dbo].[new_doc_permission_sets_ex]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[new_doc_permission_sets_ex]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[new_doc_permission_sets_ex]
GO

/****** Object:  Table [dbo].[permission_sets]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[permission_sets]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[permission_sets]
GO

/****** Object:  Table [dbo].[permissions]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[permissions]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[permissions]
GO

/****** Object:  Table [dbo].[phones]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[phones]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[phones]
GO

/****** Object:  Table [dbo].[roles]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[roles]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[roles]
GO

/****** Object:  Table [dbo].[roles_permissions]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[roles_permissions]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[roles_permissions]
GO

/****** Object:  Table [dbo].[roles_rights]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[roles_rights]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[roles_rights]
GO

/****** Object:  Table [dbo].[stats]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[stats]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[stats]
GO

/****** Object:  Table [dbo].[sys_data]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[sys_data]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[sys_data]
GO

/****** Object:  Table [dbo].[sys_types]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[sys_types]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[sys_types]
GO

/****** Object:  Table [dbo].[templategroups]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[templategroups]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[templategroups]
GO

/****** Object:  Table [dbo].[templates]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[templates]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[templates]
GO

/****** Object:  Table [dbo].[track_log]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[track_log]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[track_log]
GO

/****** Object:  Table [dbo].[url_docs]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[url_docs]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[url_docs]
GO

/****** Object:  Table [dbo].[user_rights]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[user_rights]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[user_rights]
GO

/****** Object:  Table [dbo].[user_types]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[user_types]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[user_types]
GO

/****** Object:  Table [dbo].[users]    Script Date: 2001-02-23 16:48:09 ******/
if exists (select * from sysobjects where id = object_id(N'[dbo].[users]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[users]
GO

/****** Object:  Table [dbo].[browsers]    Script Date: 2001-02-23 16:48:30 ******/
CREATE TABLE [dbo].[browsers] (
	[browser_id] [int] NOT NULL ,
	[name] [varchar] (50) NOT NULL ,
	[user_agent] [varchar] (50) NOT NULL ,
	[value] [tinyint] NOT NULL 
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[classification]    Script Date: 2001-02-23 16:48:31 ******/
CREATE TABLE [dbo].[classification] (
	[class_id] [int] IDENTITY (1, 1) NOT NULL ,
	[code] [varchar] (30) NOT NULL 
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[doc_permissions]    Script Date: 2001-02-23 16:48:31 ******/
CREATE TABLE [dbo].[doc_permissions] (
	[permission_id] [int] NOT NULL ,
	[doc_type] [int] NOT NULL ,
	[lang_prefix] [varchar] (3) NOT NULL ,
	[description] [varchar] (50) NOT NULL 
)
GO

/****** Object:  Table [dbo].[doc_types]    Script Date: 2001-02-23 16:48:31 ******/
CREATE TABLE [dbo].[doc_types] (
	[doc_type] [int] NOT NULL ,
	[lang_prefix] [varchar] (3) NOT NULL ,
	[type] [varchar] (50) NULL 
)
GO

/****** Object:  Table [dbo].[ip_accesses]    Script Date: 2001-02-23 16:48:31 ******/
CREATE TABLE [dbo].[ip_accesses] (
	[ip_access_id] [int] IDENTITY (1, 1) NOT NULL ,
	[user_id] [int] NOT NULL ,
	[ip_start] [decimal](18, 0) NOT NULL ,
	[ip_end] [decimal](18, 0) NOT NULL 
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[lang_prefixes]    Script Date: 2001-02-23 16:48:32 ******/
CREATE TABLE [dbo].[lang_prefixes] (
	[lang_id] [int] NOT NULL ,
	[lang_prefix] [char] (3) NULL 
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[languages]    Script Date: 2001-02-23 16:48:32 ******/
CREATE TABLE [dbo].[languages] (
	[lang_prefix] [varchar] (3) NOT NULL ,
	[user_prefix] [varchar] (3) NOT NULL ,
	[language] [varchar] (30) NULL 
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[main_log]    Script Date: 2001-02-23 16:48:32 ******/
CREATE TABLE [dbo].[main_log] (
	[log_datetime] [datetime] NULL ,
	[event] [varchar] (255) NULL 
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[meta]    Script Date: 2001-02-23 16:48:32 ******/
CREATE TABLE [dbo].[meta] (
	[meta_id] [int] NOT NULL ,
	[description] [varchar] (80) NOT NULL ,
	[doc_type] [int] NOT NULL ,
	[meta_headline] [varchar] (255) NOT NULL ,
	[meta_text] [varchar] (1000) NOT NULL ,
	[meta_image] [varchar] (255) NOT NULL ,
	[owner_id] [int] NOT NULL ,
	[permissions] [int] NOT NULL ,
	[shared] [int] NOT NULL ,
	[expand] [int] NOT NULL ,
	[show_meta] [int] NOT NULL ,
	[help_text_id] [int] NOT NULL ,
	[archive] [int] NOT NULL ,
	[status_id] [int] NOT NULL ,
	[lang_prefix] [varchar] (3) NOT NULL ,
	[classification] [varchar] (20) NOT NULL ,
	[date_created] [datetime] NOT NULL ,
	[date_modified] [datetime] NOT NULL ,
	[sort_position] [int] NOT NULL ,
	[menu_position] [int] NOT NULL ,
	[disable_search] [int] NULL ,
	[activated_date] [varchar] (10) NULL ,
	[activated_time] [varchar] (6) NULL ,
	[archived_date] [varchar] (10) NULL ,
	[archived_time] [varchar] (6) NULL ,
	[target] [varchar] (10) NULL ,
	[frame_name] [varchar] (20) NULL ,
	[activate] [int] NULL 
)
GO

/****** Object:  Table [dbo].[mime_types]    Script Date: 2001-02-23 16:48:33 ******/
CREATE TABLE [dbo].[mime_types] (
	[mime_id] [int] IDENTITY (1, 1) NOT NULL ,
	[mime_name] [varchar] (50) NOT NULL ,
	[mime] [varchar] (50) NOT NULL ,
	[lang_prefix] [varchar] (3) NOT NULL 
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[new_doc_permission_sets]    Script Date: 2001-02-23 16:48:33 ******/
CREATE TABLE [dbo].[new_doc_permission_sets] (
	[meta_id] [int] NOT NULL ,
	[set_id] [int] NOT NULL ,
	[permission_id] [int] NOT NULL 
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[new_doc_permission_sets_ex]    Script Date: 2001-02-23 16:48:33 ******/
CREATE TABLE [dbo].[new_doc_permission_sets_ex] (
	[meta_id] [int] NOT NULL ,
	[set_id] [int] NOT NULL ,
	[permission_id] [int] NOT NULL ,
	[permission_data] [int] NOT NULL 
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[permission_sets]    Script Date: 2001-02-23 16:48:33 ******/
CREATE TABLE [dbo].[permission_sets] (
	[set_id] [int] NOT NULL ,
	[description] [varchar] (30) NOT NULL 
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[permissions]    Script Date: 2001-02-23 16:48:33 ******/
CREATE TABLE [dbo].[permissions] (
	[permission_id] [tinyint] NOT NULL ,
	[lang_prefix] [varchar] (3) NOT NULL ,
	[description] [varchar] (50) NOT NULL 
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[phones]    Script Date: 2001-02-23 16:48:33 ******/
CREATE TABLE [dbo].[phones] (
	[phone_id] [int] NOT NULL ,
	[country_code] [varchar] (4) NOT NULL ,
	[area_code] [char] (8) NOT NULL ,
	[number] [char] (25) NOT NULL ,
	[user_id] [int] NOT NULL 
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[roles]    Script Date: 2001-02-23 16:48:34 ******/
CREATE TABLE [dbo].[roles] (
	[role_id] [int] NOT NULL ,
	[role_name] [char] (25) NOT NULL ,
	[permissions] [int] NOT NULL 
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[roles_permissions]    Script Date: 2001-02-23 16:48:34 ******/
CREATE TABLE [dbo].[roles_permissions] (
	[permission_id] [int] NOT NULL ,
	[lang_prefix] [varchar] (3) NOT NULL ,
	[description] [varchar] (40) NOT NULL 
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[roles_rights]    Script Date: 2001-02-23 16:48:34 ******/
CREATE TABLE [dbo].[roles_rights] (
	[role_id] [int] NOT NULL ,
	[meta_id] [int] NOT NULL ,
	[set_id] [tinyint] NOT NULL 
)
GO

/****** Object:  Table [dbo].[stats]    Script Date: 2001-02-23 16:48:34 ******/
CREATE TABLE [dbo].[stats] (
	[name] [varchar] (120) NOT NULL ,
	[num] [int] NOT NULL 
)
GO

/****** Object:  Table [dbo].[sys_data]    Script Date: 2001-02-23 16:48:34 ******/
CREATE TABLE [dbo].[sys_data] (
	[sys_id] [tinyint] IDENTITY (1, 1) NOT NULL ,
	[type_id] [tinyint] NOT NULL ,
	[value] [varchar] (80) NULL 
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[sys_types]    Script Date: 2001-02-23 16:48:34 ******/
CREATE TABLE [dbo].[sys_types] (
	[type_id] [tinyint] IDENTITY (1, 1) NOT NULL ,
	[name] [varchar] (50) NULL 
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[templategroups]    Script Date: 2001-02-23 16:48:35 ******/
CREATE TABLE [dbo].[templategroups] (
	[group_id] [int] NOT NULL ,
	[group_name] [varchar] (50) NOT NULL 
)
GO

/****** Object:  Table [dbo].[templates]    Script Date: 2001-02-23 16:48:35 ******/
CREATE TABLE [dbo].[templates] (
	[template_id] [int] NOT NULL ,
	[template_name] [varchar] (80) NOT NULL ,
	[simple_name] [varchar] (80) NOT NULL ,
	[lang_prefix] [varchar] (3) NOT NULL ,
	[no_of_txt] [int] NULL ,
	[no_of_img] [int] NULL ,
	[no_of_url] [int] NULL 
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[track_log]    Script Date: 2001-02-23 16:48:35 ******/
CREATE TABLE [dbo].[track_log] (
	[user_id] [smallint] NULL ,
	[log_datetime] [datetime] NULL ,
	[from_meta_id] [int] NULL ,
	[to_meta_id] [int] NULL ,
	[cookie_id] [int] NULL 
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[url_docs]    Script Date: 2001-02-23 16:48:35 ******/
CREATE TABLE [dbo].[url_docs] (
	[meta_id] [int] NOT NULL ,
	[frame_name] [varchar] (80) NOT NULL ,
	[target] [varchar] (15) NOT NULL ,
	[url_ref] [varchar] (255) NOT NULL ,
	[url_txt] [varchar] (255) NOT NULL ,
	[lang_prefix] [varchar] (3) NOT NULL 
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[user_rights]    Script Date: 2001-02-23 16:48:35 ******/
CREATE TABLE [dbo].[user_rights] (
	[user_id] [int] NOT NULL ,
	[meta_id] [int] NOT NULL ,
	[permission_id] [tinyint] NOT NULL 
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[user_types]    Script Date: 2001-02-23 16:48:35 ******/
CREATE TABLE [dbo].[user_types] (
	[user_type] [int] NOT NULL ,
	[type_name] [char] (30) NULL 
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[users]    Script Date: 2001-02-23 16:48:36 ******/
CREATE TABLE [dbo].[users] (
	[user_id] [int] NOT NULL ,
	[login_name] [char] (15) NOT NULL ,
	[login_password] [char] (15) NOT NULL ,
	[first_name] [char] (25) NOT NULL ,
	[last_name] [char] (30) NOT NULL ,
	[title] [char] (30) NOT NULL ,
	[company] [char] (30) NOT NULL ,
	[address] [char] (40) NOT NULL ,
	[city] [char] (30) NOT NULL ,
	[zip] [char] (15) NOT NULL ,
	[country] [char] (30) NOT NULL ,
	[county_council] [char] (30) NOT NULL ,
	[email] [char] (50) NOT NULL ,
	[admin_mode] [int] NOT NULL ,
	[last_page] [int] NOT NULL ,
	[archive_mode] [int] NOT NULL ,
	[lang_id] [int] NOT NULL ,
	[user_type] [int] NOT NULL ,
	[active] [int] NOT NULL ,
	[create_date] [smalldatetime] NOT NULL 
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[browser_docs]    Script Date: 2001-02-23 16:48:36 ******/
CREATE TABLE [dbo].[browser_docs] (
	[meta_id] [int] NOT NULL ,
	[to_meta_id] [int] NOT NULL ,
	[browser_id] [int] NOT NULL 
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[childs]    Script Date: 2001-02-23 16:48:36 ******/
CREATE TABLE [dbo].[childs] (
	[meta_id] [int] NOT NULL ,
	[to_meta_id] [int] NOT NULL ,
	[menu_sort] [int] NOT NULL ,
	[manual_sort_order] [int] NOT NULL 
)
GO

/****** Object:  Table [dbo].[doc_permission_sets]    Script Date: 2001-02-23 16:48:36 ******/
CREATE TABLE [dbo].[doc_permission_sets] (
	[meta_id] [int] NOT NULL ,
	[set_id] [int] NOT NULL ,
	[permission_id] [int] NOT NULL 
)
GO

/****** Object:  Table [dbo].[doc_permission_sets_ex]    Script Date: 2001-02-23 16:48:36 ******/
CREATE TABLE [dbo].[doc_permission_sets_ex] (
	[meta_id] [int] NOT NULL ,
	[set_id] [int] NOT NULL ,
	[permission_id] [int] NOT NULL ,
	[permission_data] [int] NOT NULL 
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[fileupload_docs]    Script Date: 2001-02-23 16:48:37 ******/
CREATE TABLE [dbo].[fileupload_docs] (
	[meta_id] [int] NOT NULL ,
	[filename] [varchar] (50) NOT NULL ,
	[mime] [varchar] (50) NOT NULL 
)
GO

/****** Object:  Table [dbo].[frameset_docs]    Script Date: 2001-02-23 16:48:37 ******/
CREATE TABLE [dbo].[frameset_docs] (
	[meta_id] [int] NOT NULL ,
	[frame_set] [text] NULL 
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

/****** Object:  Table [dbo].[images]    Script Date: 2001-02-23 16:48:37 ******/
CREATE TABLE [dbo].[images] (
	[meta_id] [int] NOT NULL ,
	[width] [int] NOT NULL ,
	[height] [int] NOT NULL ,
	[border] [int] NOT NULL ,
	[v_space] [int] NOT NULL ,
	[h_space] [int] NOT NULL ,
	[name] [int] NOT NULL ,
	[image_name] [varchar] (40) NOT NULL ,
	[target] [varchar] (15) NOT NULL ,
	[target_name] [varchar] (80) NOT NULL ,
	[align] [varchar] (15) NOT NULL ,
	[alt_text] [varchar] (255) NOT NULL ,
	[low_scr] [varchar] (255) NOT NULL ,
	[imgurl] [varchar] (255) NOT NULL ,
	[linkurl] [varchar] (255) NOT NULL 
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[meta_classification]    Script Date: 2001-02-23 16:48:37 ******/
CREATE TABLE [dbo].[meta_classification] (
	[meta_id] [int] NOT NULL ,
	[class_id] [int] NOT NULL 
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[templates_cref]    Script Date: 2001-02-23 16:48:37 ******/
CREATE TABLE [dbo].[templates_cref] (
	[group_id] [int] NOT NULL ,
	[template_id] [int] NOT NULL 
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[text_docs]    Script Date: 2001-02-23 16:48:38 ******/
CREATE TABLE [dbo].[text_docs] (
	[meta_id] [int] NOT NULL ,
	[template_id] [int] NOT NULL ,
	[group_id] [int] NOT NULL ,
	[sort_order] [int] NOT NULL 
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[texts]    Script Date: 2001-02-23 16:48:38 ******/
CREATE TABLE [dbo].[texts] (
	[meta_id] [int] NOT NULL ,
	[name] [int] NOT NULL ,
	[text] [text] NOT NULL ,
	[type] [int] NULL 
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

/****** Object:  Table [dbo].[user_roles_crossref]    Script Date: 2001-02-23 16:48:38 ******/
CREATE TABLE [dbo].[user_roles_crossref] (
	[user_id] [int] NOT NULL ,
	[role_id] [int] NOT NULL 
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[stats] WITH NOCHECK ADD 
	CONSTRAINT [stats_pk] PRIMARY KEY  CLUSTERED 
	(
		[name]
	)  ON [PRIMARY] 
GO

 CREATE  CLUSTERED  INDEX [IX_doc_permissions] ON [dbo].[doc_permissions]([permission_id]) ON [PRIMARY]
GO

 CREATE  CLUSTERED  INDEX [IX_doc_types] ON [dbo].[doc_types]([lang_prefix], [doc_type]) ON [PRIMARY]
GO

 CREATE  CLUSTERED  INDEX [meta_meta_id] ON [dbo].[meta]([meta_id], [show_meta], [activate]) ON [PRIMARY]
GO

 CREATE  CLUSTERED  INDEX [roles_rights_meta_id] ON [dbo].[roles_rights]([meta_id], [role_id], [set_id]) ON [PRIMARY]
GO

 CREATE  UNIQUE  CLUSTERED  INDEX [IX_tg] ON [dbo].[templategroups]([group_id]) ON [PRIMARY]
GO

 CREATE  CLUSTERED  INDEX [childs_meta_id] ON [dbo].[childs]([meta_id]) ON [PRIMARY]
GO

 CREATE  CLUSTERED  INDEX [IX_doc_permission_sets] ON [dbo].[doc_permission_sets]([meta_id], [set_id]) ON [PRIMARY]
GO

 CREATE  CLUSTERED  INDEX [fileupload_docs_meta_id] ON [dbo].[fileupload_docs]([meta_id]) ON [PRIMARY]
GO

ALTER TABLE [dbo].[browsers] WITH NOCHECK ADD 
	CONSTRAINT [DF_browsers_value] DEFAULT (1) FOR [value],
	CONSTRAINT [PK_browsers] PRIMARY KEY  NONCLUSTERED 
	(
		[browser_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[classification] WITH NOCHECK ADD 
	CONSTRAINT [PK_classification] PRIMARY KEY  NONCLUSTERED 
	(
		[class_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[doc_permissions] WITH NOCHECK ADD 
	CONSTRAINT [PK_doc_permissions] PRIMARY KEY  NONCLUSTERED 
	(
		[permission_id],
		[doc_type],
		[lang_prefix]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[doc_types] WITH NOCHECK ADD 
	CONSTRAINT [DF__doc_types__lang___55009F39] DEFAULT ('se') FOR [lang_prefix],
	CONSTRAINT [PK_doc_types] PRIMARY KEY  NONCLUSTERED 
	(
		[doc_type],
		[lang_prefix]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[ip_accesses] WITH NOCHECK ADD 
	CONSTRAINT [PK_ip_accesses] PRIMARY KEY  NONCLUSTERED 
	(
		[ip_access_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[lang_prefixes] WITH NOCHECK ADD 
	CONSTRAINT [PK_lang_prefixes] PRIMARY KEY  NONCLUSTERED 
	(
		[lang_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[languages] WITH NOCHECK ADD 
	CONSTRAINT [PK_languages] PRIMARY KEY  NONCLUSTERED 
	(
		[lang_prefix],
		[user_prefix]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[meta] WITH NOCHECK ADD 
	CONSTRAINT [PK_meta] PRIMARY KEY  NONCLUSTERED 
	(
		[meta_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[mime_types] WITH NOCHECK ADD 
	CONSTRAINT [DF__mime_type__lang___5D95E53A] DEFAULT ('se') FOR [lang_prefix],
	CONSTRAINT [PK_mime_types] PRIMARY KEY  NONCLUSTERED 
	(
		[mime_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[new_doc_permission_sets] WITH NOCHECK ADD 
	CONSTRAINT [PK_new_doc_permission_sets] PRIMARY KEY  NONCLUSTERED 
	(
		[meta_id],
		[set_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[new_doc_permission_sets_ex] WITH NOCHECK ADD 
	CONSTRAINT [PK_new_doc_permission_sets_ex] PRIMARY KEY  NONCLUSTERED 
	(
		[meta_id],
		[set_id],
		[permission_id],
		[permission_data]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[permission_sets] WITH NOCHECK ADD 
	CONSTRAINT [PK_permission_types] PRIMARY KEY  NONCLUSTERED 
	(
		[set_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[permissions] WITH NOCHECK ADD 
	CONSTRAINT [DF__permissio__lang___662B2B3B] DEFAULT ('se') FOR [lang_prefix],
	CONSTRAINT [PK_permissions] PRIMARY KEY  NONCLUSTERED 
	(
		[permission_id],
		[lang_prefix]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[phones] WITH NOCHECK ADD 
	CONSTRAINT [PK_phones] PRIMARY KEY  NONCLUSTERED 
	(
		[phone_id],
		[user_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[roles] WITH NOCHECK ADD 
	CONSTRAINT [DF_roles_permissions] DEFAULT (0) FOR [permissions],
	CONSTRAINT [PK_roles] PRIMARY KEY  NONCLUSTERED 
	(
		[role_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[roles_permissions] WITH NOCHECK ADD 
	CONSTRAINT [PK_roles_permissions] PRIMARY KEY  NONCLUSTERED 
	(
		[permission_id],
		[lang_prefix]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[roles_rights] WITH NOCHECK ADD 
	CONSTRAINT [PK_roles_rights] PRIMARY KEY  NONCLUSTERED 
	(
		[role_id],
		[meta_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[sys_data] WITH NOCHECK ADD 
	CONSTRAINT [PK_sys_data] PRIMARY KEY  NONCLUSTERED 
	(
		[sys_id],
		[type_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[sys_types] WITH NOCHECK ADD 
	CONSTRAINT [PK_sys_types] PRIMARY KEY  NONCLUSTERED 
	(
		[type_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[templategroups] WITH NOCHECK ADD 
	CONSTRAINT [PK_templategroups] PRIMARY KEY  NONCLUSTERED 
	(
		[group_id]
	)  ON [PRIMARY] ,
	CONSTRAINT [IX_templategroups] UNIQUE  NONCLUSTERED 
	(
		[group_name]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[templates] WITH NOCHECK ADD 
	CONSTRAINT [PK_templates] PRIMARY KEY  NONCLUSTERED 
	(
		[template_id]
	)  ON [PRIMARY] ,
	CONSTRAINT [IX_templates] UNIQUE  NONCLUSTERED 
	(
		[simple_name],
		[lang_prefix]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[url_docs] WITH NOCHECK ADD 
	CONSTRAINT [PK_url_docs] PRIMARY KEY  NONCLUSTERED 
	(
		[meta_id],
		[lang_prefix]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[user_rights] WITH NOCHECK ADD 
	CONSTRAINT [PK_user_rights] PRIMARY KEY  NONCLUSTERED 
	(
		[user_id],
		[meta_id],
		[permission_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[user_types] WITH NOCHECK ADD 
	CONSTRAINT [PK_user_types] PRIMARY KEY  NONCLUSTERED 
	(
		[user_type]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[users] WITH NOCHECK ADD 
	CONSTRAINT [DF__users__title__7C1A6C5A] DEFAULT ('') FOR [title],
	CONSTRAINT [DF__users__company__7D0E9093] DEFAULT ('') FOR [company],
	CONSTRAINT [DF__users__user_type__7E02B4CC] DEFAULT (1) FOR [user_type],
	CONSTRAINT [DF__users__active__7EF6D905] DEFAULT (1) FOR [active],
	CONSTRAINT [PK_users] PRIMARY KEY  NONCLUSTERED 
	(
		[user_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[browser_docs] WITH NOCHECK ADD 
	CONSTRAINT [DF__browser_d__brows__4B7734FF] DEFAULT (0) FOR [browser_id],
	CONSTRAINT [PK_browser_docs] PRIMARY KEY  NONCLUSTERED 
	(
		[meta_id],
		[to_meta_id],
		[browser_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[childs] WITH NOCHECK ADD 
	CONSTRAINT [PK_childs] PRIMARY KEY  NONCLUSTERED 
	(
		[meta_id],
		[to_meta_id],
		[menu_sort]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[doc_permission_sets] WITH NOCHECK ADD 
	CONSTRAINT [PK_doc_permission_sets] PRIMARY KEY  NONCLUSTERED 
	(
		[meta_id],
		[set_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[doc_permission_sets_ex] WITH NOCHECK ADD 
	CONSTRAINT [PK_permission_sets_ex] PRIMARY KEY  NONCLUSTERED 
	(
		[meta_id],
		[set_id],
		[permission_id],
		[permission_data]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[fileupload_docs] WITH NOCHECK ADD 
	CONSTRAINT [PK_fileupload_docs] PRIMARY KEY  NONCLUSTERED 
	(
		[meta_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[frameset_docs] WITH NOCHECK ADD 
	CONSTRAINT [PK_frameset_docs] PRIMARY KEY  NONCLUSTERED 
	(
		[meta_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[images] WITH NOCHECK ADD 
	CONSTRAINT [PK_images] PRIMARY KEY  NONCLUSTERED 
	(
		[meta_id],
		[name]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[meta_classification] WITH NOCHECK ADD 
	CONSTRAINT [PK_meta_classification] PRIMARY KEY  NONCLUSTERED 
	(
		[meta_id],
		[class_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[templates_cref] WITH NOCHECK ADD 
	CONSTRAINT [PK_templates_cref] PRIMARY KEY  NONCLUSTERED 
	(
		[group_id],
		[template_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[text_docs] WITH NOCHECK ADD 
	CONSTRAINT [DF__text_docs__group__72910220] DEFAULT (1) FOR [group_id],
	CONSTRAINT [PK_text_docs] PRIMARY KEY  NONCLUSTERED 
	(
		[meta_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[texts] WITH NOCHECK ADD 
	CONSTRAINT [PK_texts] PRIMARY KEY  NONCLUSTERED 
	(
		[meta_id],
		[name]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[user_roles_crossref] WITH NOCHECK ADD 
	CONSTRAINT [PK_user_roles_crossref] PRIMARY KEY  NONCLUSTERED 
	(
		[user_id],
		[role_id]
	)  ON [PRIMARY] 
GO

 CREATE  INDEX [IX_browsers] ON [dbo].[browsers]([value]) ON [PRIMARY]
GO

 CREATE  INDEX [roles_rights_role_id] ON [dbo].[roles_rights]([role_id]) ON [PRIMARY]
GO

ALTER TABLE [dbo].[browser_docs] ADD 
	CONSTRAINT [FK_browser_docs_browsers] FOREIGN KEY 
	(
		[browser_id]
	) REFERENCES [dbo].[browsers] (
		[browser_id]
	)
GO

ALTER TABLE [dbo].[childs] ADD 
	CONSTRAINT [FK_childs_meta] FOREIGN KEY 
	(
		[to_meta_id]
	) REFERENCES [dbo].[meta] (
		[meta_id]
	),
	CONSTRAINT [FK_childs_meta1] FOREIGN KEY 
	(
		[meta_id]
	) REFERENCES [dbo].[meta] (
		[meta_id]
	)
GO

ALTER TABLE [dbo].[doc_permission_sets] ADD 
	CONSTRAINT [FK_doc_permission_sets_meta] FOREIGN KEY 
	(
		[meta_id]
	) REFERENCES [dbo].[meta] (
		[meta_id]
	)
GO

ALTER TABLE [dbo].[doc_permission_sets_ex] ADD 
	CONSTRAINT [FK_doc_permission_sets_ex_meta] FOREIGN KEY 
	(
		[meta_id]
	) REFERENCES [dbo].[meta] (
		[meta_id]
	)
GO

ALTER TABLE [dbo].[fileupload_docs] ADD 
	CONSTRAINT [FK_fileupload_docs_meta] FOREIGN KEY 
	(
		[meta_id]
	) REFERENCES [dbo].[meta] (
		[meta_id]
	)
GO

ALTER TABLE [dbo].[frameset_docs] ADD 
	CONSTRAINT [FK_frameset_docs_meta] FOREIGN KEY 
	(
		[meta_id]
	) REFERENCES [dbo].[meta] (
		[meta_id]
	)
GO

ALTER TABLE [dbo].[images] ADD 
	CONSTRAINT [FK_images_meta] FOREIGN KEY 
	(
		[meta_id]
	) REFERENCES [dbo].[meta] (
		[meta_id]
	)
GO

ALTER TABLE [dbo].[meta_classification] ADD 
	CONSTRAINT [FK_meta_classification_classification] FOREIGN KEY 
	(
		[class_id]
	) REFERENCES [dbo].[classification] (
		[class_id]
	),
	CONSTRAINT [FK_meta_classification_meta] FOREIGN KEY 
	(
		[meta_id]
	) REFERENCES [dbo].[meta] (
		[meta_id]
	)
GO

ALTER TABLE [dbo].[templates_cref] ADD 
	CONSTRAINT [FK_templates_cref_templates] FOREIGN KEY 
	(
		[template_id]
	) REFERENCES [dbo].[templates] (
		[template_id]
	)
GO

ALTER TABLE [dbo].[text_docs] ADD 
	CONSTRAINT [FK_text_docs_meta] FOREIGN KEY 
	(
		[meta_id]
	) REFERENCES [dbo].[meta] (
		[meta_id]
	),
	CONSTRAINT [FK_text_docs_templates] FOREIGN KEY 
	(
		[template_id]
	) REFERENCES [dbo].[templates] (
		[template_id]
	)
GO

ALTER TABLE [dbo].[texts] ADD 
	CONSTRAINT [FK_texts_meta] FOREIGN KEY 
	(
		[meta_id]
	) REFERENCES [dbo].[meta] (
		[meta_id]
	)
GO

ALTER TABLE [dbo].[user_roles_crossref] ADD 
	CONSTRAINT [FK_user_roles_crossref_roles] FOREIGN KEY 
	(
		[role_id]
	) REFERENCES [dbo].[roles] (
		[role_id]
	)
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.UpdateUser    Script Date: 2001-02-23 16:48:38 ******/

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

/****** Object:  Stored Procedure dbo.UserPrefsChange    Script Date: 2001-02-23 16:48:38 ******/

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

/****** Object:  Stored Procedure dbo.AddNewuser    Script Date: 2001-02-23 16:48:38 ******/

/****** Object:  Stored Procedure dbo.AddNewuser    Script Date: 2001-02-23 09:18:39 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.AddStatistics    Script Date: 2001-02-23 16:48:38 ******/

/****** Object:  Stored Procedure dbo.AddStatistics    Script Date: 2001-02-23 09:18:39 ******/
--
-- Procedure Create
-- dbo.AddStatistics
--
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

/****** Object:  Stored Procedure dbo.ChangeUserActiveStatus    Script Date: 2001-02-23 16:48:38 ******/

/****** Object:  Stored Procedure dbo.ChangeUserActiveStatus    Script Date: 2001-02-23 09:18:39 ******/
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

/****** Object:  Stored Procedure dbo.Classification_Get_All    Script Date: 2001-02-23 16:48:38 ******/

/****** Object:  Stored Procedure dbo.Classification_Get_All    Script Date: 2001-02-23 09:18:39 ******/
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

/****** Object:  Stored Procedure dbo.DeleteNewDocPermissionSetEx    Script Date: 2001-02-23 16:48:39 ******/

/****** Object:  Stored Procedure dbo.DeleteNewDocPermissionSetEx    Script Date: 2001-02-23 09:18:39 ******/
--
-- Procedure Create
-- dbo.DeleteNewDocPermissionSetEx
--
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

/****** Object:  Stored Procedure dbo.FindMetaId    Script Date: 2001-02-23 16:48:39 ******/

/****** Object:  Stored Procedure dbo.FindMetaId    Script Date: 2001-02-23 09:18:40 ******/
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

/****** Object:  Stored Procedure dbo.FindUserName    Script Date: 2001-02-23 16:48:39 ******/

/****** Object:  Stored Procedure dbo.FindUserName    Script Date: 2001-02-23 09:18:40 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.GetAllRoles    Script Date: 2001-02-23 16:48:39 ******/

/****** Object:  Stored Procedure dbo.GetAllRoles    Script Date: 2001-02-23 09:18:40 ******/
--
-- Procedure Create
-- dbo.GetAllRoles
--
CREATE PROCEDURE GetAllRoles AS
SELECT role_id, role_name
FROM roles
 
ORDER BY role_name


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.GetAllUsers    Script Date: 2001-02-23 16:48:39 ******/

/****** Object:  Stored Procedure dbo.GetAllUsers    Script Date: 2001-02-23 09:18:40 ******/
--
-- Procedure Create
-- dbo.GetAllUsers
--
CREATE PROCEDURE [GetAllUsers] AS
  select *
 from USERS
 
 order by  last_name


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.GetAllUsersInList    Script Date: 2001-02-23 16:48:39 ******/

/****** Object:  Stored Procedure dbo.GetAllUsersInList    Script Date: 2001-02-23 09:18:40 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.GetCategoryUsers    Script Date: 2001-02-23 16:48:39 ******/

/****** Object:  Stored Procedure dbo.GetCategoryUsers    Script Date: 2001-02-23 09:18:40 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.GetCurrentSessionCounter    Script Date: 2001-02-23 16:48:39 ******/

/****** Object:  Stored Procedure dbo.GetCurrentSessionCounter    Script Date: 2001-02-23 09:18:40 ******/
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

/****** Object:  Stored Procedure dbo.GetCurrentSessionCounterDate    Script Date: 2001-02-23 16:48:39 ******/

/****** Object:  Stored Procedure dbo.GetCurrentSessionCounterDate    Script Date: 2001-02-23 09:18:40 ******/
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

/****** Object:  Stored Procedure dbo.GetDocType    Script Date: 2001-02-23 16:48:39 ******/

/****** Object:  Stored Procedure dbo.GetDocType    Script Date: 2001-02-23 09:18:40 ******/
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

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.GetDocTypes    Script Date: 2001-02-23 16:48:39 ******/

/****** Object:  Stored Procedure dbo.GetDocTypes    Script Date: 2001-02-23 09:18:40 ******/
--
-- Procedure Create
-- dbo.GetDocTypes
--
CREATE PROCEDURE GetDocTypes @lang_prefix VARCHAR(3) AS
SELECT doc_type,type FROM doc_types
WHERE lang_prefix = @lang_prefix
ORDER BY doc_type


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.GetDocTypesWithNewPermissions    Script Date: 2001-02-23 16:48:39 ******/

/****** Object:  Stored Procedure dbo.GetDocTypesWithNewPermissions    Script Date: 2001-02-23 09:18:40 ******/
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

/****** Object:  Stored Procedure dbo.GetHighestUserId    Script Date: 2001-02-23 16:48:39 ******/

/****** Object:  Stored Procedure dbo.GetHighestUserId    Script Date: 2001-02-23 09:18:40 ******/
--
-- Procedure Create
-- dbo.GetHighestUserId
--
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

/****** Object:  Stored Procedure dbo.GetLangPrefix    Script Date: 2001-02-23 16:48:39 ******/

/****** Object:  Stored Procedure dbo.GetLangPrefix    Script Date: 2001-02-23 09:18:40 ******/
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

/****** Object:  Stored Procedure dbo.GetLangPrefixFromId    Script Date: 2001-02-23 16:48:39 ******/

/****** Object:  Stored Procedure dbo.GetLangPrefixFromId    Script Date: 2001-02-23 09:18:40 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.GetLanguageList    Script Date: 2001-02-23 16:48:39 ******/

/****** Object:  Stored Procedure dbo.GetLanguageList    Script Date: 2001-02-23 09:18:40 ******/
CREATE PROCEDURE GetLanguageList AS
/*
 Returns all 
*/
SELECT lp.lang_id , lang.language
FROM lang_prefixes lp, languages lang
WHERE lp.lang_prefix = lang.lang_prefix


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.getLanguages    Script Date: 2001-02-23 16:48:39 ******/

/****** Object:  Stored Procedure dbo.getLanguages    Script Date: 2001-02-23 09:18:40 ******/
CREATE PROCEDURE getLanguages AS
select lang_prefix,language from languages order by language


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.GetMetaPathInfo    Script Date: 2001-02-23 16:48:39 ******/

/****** Object:  Stored Procedure dbo.GetMetaPathInfo    Script Date: 2001-02-23 09:18:41 ******/
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

/****** Object:  Stored Procedure dbo.GetNewPermissionSet    Script Date: 2001-02-23 16:48:39 ******/

/****** Object:  Stored Procedure dbo.GetNewPermissionSet    Script Date: 2001-02-23 09:18:41 ******/
--
-- Procedure Create
-- dbo.GetNewPermissionSet
--
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

/****** Object:  Stored Procedure dbo.GetNoOfTemplates    Script Date: 2001-02-23 16:48:39 ******/

/****** Object:  Stored Procedure dbo.GetNoOfTemplates    Script Date: 2001-02-23 09:18:41 ******/
--
-- Procedure Create
-- dbo.GetNoOfTemplates
--
CREATE PROCEDURE GetNoOfTemplates AS
select count(*) from templates


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.GetRolesDocPermissions    Script Date: 2001-02-23 16:48:39 ******/

/****** Object:  Stored Procedure dbo.GetRolesDocPermissions    Script Date: 2001-02-23 09:18:41 ******/
--
-- Procedure Create
-- dbo.GetRolesDocPermissions
--
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

/****** Object:  Stored Procedure dbo.getTemplategroups    Script Date: 2001-02-23 16:48:39 ******/

/****** Object:  Stored Procedure dbo.getTemplategroups    Script Date: 2001-02-23 09:18:41 ******/
--
-- Procedure Create
-- dbo.getTemplategroups
--
CREATE PROCEDURE getTemplategroups AS
select group_id,group_name from templategroups order by group_name


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.GetTemplateGroupsWithNewPermissions    Script Date: 2001-02-23 16:48:39 ******/

/****** Object:  Stored Procedure dbo.GetTemplateGroupsWithNewPermissions    Script Date: 2001-02-23 09:18:41 ******/
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

/****** Object:  Stored Procedure dbo.getTemplates    Script Date: 2001-02-23 16:48:39 ******/

/****** Object:  Stored Procedure dbo.getTemplates    Script Date: 2001-02-23 09:18:41 ******/
--
-- Procedure Create
-- dbo.getTemplates
--
CREATE PROCEDURE getTemplates AS
select template_id, simple_name from templates


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.GetUserCreateDate    Script Date: 2001-02-23 16:48:39 ******/

/****** Object:  Stored Procedure dbo.GetUserCreateDate    Script Date: 2001-02-23 09:18:41 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.GetUserId    Script Date: 2001-02-23 16:48:39 ******/

/****** Object:  Stored Procedure dbo.GetUserId    Script Date: 2001-02-23 09:18:41 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.GetUserIdFromName    Script Date: 2001-02-23 16:48:40 ******/

/****** Object:  Stored Procedure dbo.GetUserIdFromName    Script Date: 2001-02-23 09:18:41 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.GetUserInfo    Script Date: 2001-02-23 16:48:40 ******/

/****** Object:  Stored Procedure dbo.GetUserInfo    Script Date: 2001-02-23 09:18:41 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.GetUserNames    Script Date: 2001-02-23 16:48:40 ******/

/****** Object:  Stored Procedure dbo.GetUserNames    Script Date: 2001-02-23 09:18:41 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.GetUserPassword    Script Date: 2001-02-23 16:48:40 ******/

/****** Object:  Stored Procedure dbo.GetUserPassword    Script Date: 2001-02-23 09:18:41 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.GetUserPhoneNumbers    Script Date: 2001-02-23 16:48:40 ******/

/****** Object:  Stored Procedure dbo.GetUserPhoneNumbers    Script Date: 2001-02-23 09:18:41 ******/
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

/****** Object:  Stored Procedure dbo.GetUserPhones    Script Date: 2001-02-23 16:48:40 ******/

/****** Object:  Stored Procedure dbo.GetUserPhones    Script Date: 2001-02-23 09:18:41 ******/
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

/****** Object:  Stored Procedure dbo.GetUserType    Script Date: 2001-02-23 16:48:40 ******/

/****** Object:  Stored Procedure dbo.GetUserType    Script Date: 2001-02-23 09:18:42 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.GetUserTypes    Script Date: 2001-02-23 16:48:40 ******/

/****** Object:  Stored Procedure dbo.GetUserTypes    Script Date: 2001-02-23 09:18:42 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.IncSessionCounter    Script Date: 2001-02-23 16:48:40 ******/

/****** Object:  Stored Procedure dbo.IncSessionCounter    Script Date: 2001-02-23 09:18:42 ******/
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

/****** Object:  Stored Procedure dbo.IPAccessAdd    Script Date: 2001-02-23 16:48:40 ******/

/****** Object:  Stored Procedure dbo.IPAccessAdd    Script Date: 2001-02-23 09:18:42 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.IPAccessDelete    Script Date: 2001-02-23 16:48:40 ******/

/****** Object:  Stored Procedure dbo.IPAccessDelete    Script Date: 2001-02-23 09:18:42 ******/
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

/****** Object:  Stored Procedure dbo.IPAccessesGetAll    Script Date: 2001-02-23 16:48:40 ******/

/****** Object:  Stored Procedure dbo.IPAccessesGetAll    Script Date: 2001-02-23 09:18:42 ******/
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

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.IPAccessUpdate    Script Date: 2001-02-23 16:48:40 ******/

/****** Object:  Stored Procedure dbo.IPAccessUpdate    Script Date: 2001-02-23 09:18:43 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.ListConferences    Script Date: 2001-02-23 16:48:40 ******/

/****** Object:  Stored Procedure dbo.ListConferences    Script Date: 2001-02-23 09:18:43 ******/
CREATE PROCEDURE ListConferences AS
select meta_id, meta_headline 
from meta 
where doc_type = 102


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.ListDocsGetInternalDocTypes    Script Date: 2001-02-23 16:48:40 ******/

/****** Object:  Stored Procedure dbo.ListDocsGetInternalDocTypes    Script Date: 2001-02-23 09:18:43 ******/
CREATE PROCEDURE ListDocsGetInternalDocTypes AS
/* selct all internal doc types */
select doc_type, type 
from doc_types
where doc_type <= 100


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.ListDocsGetInternalDocTypesValue    Script Date: 2001-02-23 16:48:40 ******/

/****** Object:  Stored Procedure dbo.ListDocsGetInternalDocTypesValue    Script Date: 2001-02-23 09:18:43 ******/
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

/****** Object:  Stored Procedure dbo.phoneNbrAdd    Script Date: 2001-02-23 16:48:40 ******/

/****** Object:  Stored Procedure dbo.phoneNbrAdd    Script Date: 2001-02-23 09:18:43 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.PhoneNbrDelete    Script Date: 2001-02-23 16:48:40 ******/

/****** Object:  Stored Procedure dbo.PhoneNbrDelete    Script Date: 2001-02-23 09:18:43 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.PhoneNbrUpdate    Script Date: 2001-02-23 16:48:40 ******/

/****** Object:  Stored Procedure dbo.PhoneNbrUpdate    Script Date: 2001-02-23 09:18:43 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.RoleAddNew    Script Date: 2001-02-23 16:48:40 ******/

/****** Object:  Stored Procedure dbo.RoleAddNew    Script Date: 2001-02-23 09:18:43 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.RoleAdminGetAll    Script Date: 2001-02-23 16:48:40 ******/

/****** Object:  Stored Procedure dbo.RoleAdminGetAll    Script Date: 2001-02-23 09:18:43 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.RoleCount    Script Date: 2001-02-23 16:48:40 ******/

/****** Object:  Stored Procedure dbo.RoleCount    Script Date: 2001-02-23 09:18:43 ******/
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

/****** Object:  Stored Procedure dbo.RoleDeleteViewAffectedMetaIds    Script Date: 2001-02-23 16:48:40 ******/

/****** Object:  Stored Procedure dbo.RoleDeleteViewAffectedMetaIds    Script Date: 2001-02-23 09:18:43 ******/
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

/****** Object:  Stored Procedure dbo.RoleFindName    Script Date: 2001-02-23 16:48:40 ******/

/****** Object:  Stored Procedure dbo.RoleFindName    Script Date: 2001-02-23 09:18:43 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.RoleGetAllApartFromRole    Script Date: 2001-02-23 16:48:41 ******/

/****** Object:  Stored Procedure dbo.RoleGetAllApartFromRole    Script Date: 2001-02-23 09:18:43 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.RoleGetName    Script Date: 2001-02-23 16:48:41 ******/

/****** Object:  Stored Procedure dbo.RoleGetName    Script Date: 2001-02-23 09:18:43 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.RoleGetPermissionsByLanguage    Script Date: 2001-02-23 16:48:41 ******/

/****** Object:  Stored Procedure dbo.RoleGetPermissionsByLanguage    Script Date: 2001-02-23 09:18:43 ******/
--
-- Procedure Create
-- dbo.RoleGetPermissionsByLanguage
--
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

/****** Object:  Stored Procedure dbo.RoleGetPermissionsFromRole    Script Date: 2001-02-23 16:48:41 ******/

/****** Object:  Stored Procedure dbo.RoleGetPermissionsFromRole    Script Date: 2001-02-23 09:18:43 ******/
--
-- Procedure Create
-- dbo.RoleGetPermissionsFromRole
--
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

/****** Object:  Stored Procedure dbo.RolePermissionsAddNew    Script Date: 2001-02-23 16:48:41 ******/

/****** Object:  Stored Procedure dbo.RolePermissionsAddNew    Script Date: 2001-02-23 09:18:43 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.RoleUpdateName    Script Date: 2001-02-23 16:48:41 ******/

/****** Object:  Stored Procedure dbo.RoleUpdateName    Script Date: 2001-02-23 09:18:44 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.RoleUpdatePermissions    Script Date: 2001-02-23 16:48:41 ******/

/****** Object:  Stored Procedure dbo.RoleUpdatePermissions    Script Date: 2001-02-23 09:18:44 ******/
--
-- Procedure Create
-- dbo.RoleUpdatePermissions
--
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

/****** Object:  Stored Procedure dbo.SetNewDocPermissionSet    Script Date: 2001-02-23 16:48:41 ******/

/****** Object:  Stored Procedure dbo.SetNewDocPermissionSet    Script Date: 2001-02-23 09:18:44 ******/
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
WHERE meta_id = @meta_id
AND  set_id = @set_id
-- Insert new value
INSERT INTO new_doc_permission_sets
VALUES (@meta_id,@set_id,@permission_id)


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.SetNewDocPermissionSetEx    Script Date: 2001-02-23 16:48:41 ******/

/****** Object:  Stored Procedure dbo.SetNewDocPermissionSetEx    Script Date: 2001-02-23 09:18:44 ******/
--
-- Procedure Create
-- dbo.SetNewDocPermissionSetEx
--
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

/****** Object:  Stored Procedure dbo.SetRoleDocPermissionSetId    Script Date: 2001-02-23 16:48:41 ******/

/****** Object:  Stored Procedure dbo.SetRoleDocPermissionSetId    Script Date: 2001-02-23 09:18:44 ******/
--
-- Procedure Create
-- dbo.SetRoleDocPermissionSetId
--
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

/****** Object:  Stored Procedure dbo.SetSessionCounterDate    Script Date: 2001-02-23 16:48:41 ******/

/****** Object:  Stored Procedure dbo.SetSessionCounterDate    Script Date: 2001-02-23 09:18:44 ******/
CREATE PROCEDURE SetSessionCounterDate
   @new_date varchar(20)
AS
      
 update sys_data
 set value = @new_date where type_id = 2
 
  return


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.SystemMessageGet    Script Date: 2001-02-23 16:48:41 ******/

/****** Object:  Stored Procedure dbo.SystemMessageGet    Script Date: 2001-02-23 09:18:44 ******/
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

/****** Object:  Stored Procedure dbo.SystemMessageSet    Script Date: 2001-02-23 16:48:41 ******/

/****** Object:  Stored Procedure dbo.SystemMessageSet    Script Date: 2001-02-23 09:18:44 ******/
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

/****** Object:  Stored Procedure dbo.AddBrowserStatistics    Script Date: 2001-02-23 16:48:41 ******/

/****** Object:  Stored Procedure dbo.AddBrowserStatistics    Script Date: 2001-02-23 09:18:44 ******/
--
-- Procedure Create
-- dbo.AddBrowserStatistics
--
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

/****** Object:  Stored Procedure dbo.AddScreenStatistics    Script Date: 2001-02-23 16:48:41 ******/

/****** Object:  Stored Procedure dbo.AddScreenStatistics    Script Date: 2001-02-23 09:18:44 ******/
--
-- Procedure Create
-- dbo.AddScreenStatistics
--
CREATE PROCEDURE AddScreenStatistics @width INT, @height INT, @bits INT AS
DECLARE @screen VARCHAR(20) 
SET @screen = 'Screen: '+LTRIM(STR(@width))+'x'+LTRIM(STR(@height))+'x'+LTRIM(STR(@bits))
EXEC AddStatistics @screen


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.AddStatisticsCount    Script Date: 2001-02-23 16:48:41 ******/

/****** Object:  Stored Procedure dbo.AddStatisticsCount    Script Date: 2001-02-23 09:18:44 ******/
--
-- Procedure Create
-- dbo.AddStatisticsCount
--
CREATE PROCEDURE AddStatisticsCount AS
EXEC AddStatistics 'Count'


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.AddUserRole    Script Date: 2001-02-23 16:48:41 ******/

/****** Object:  Stored Procedure dbo.AddUserRole    Script Date: 2001-02-23 09:18:39 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.AddVersionStatistics    Script Date: 2001-02-23 16:48:41 ******/

/****** Object:  Stored Procedure dbo.AddVersionStatistics    Script Date: 2001-02-23 09:18:44 ******/
--
-- Procedure Create
-- dbo.AddVersionStatistics
--
CREATE PROCEDURE AddVersionStatistics @name VARCHAR(30), @version VARCHAR(30) AS
DECLARE @string VARCHAR(62)
SET @string = @name+': '+@version
EXEC AddStatistics @string


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.CheckAdminRights    Script Date: 2001-02-23 16:48:41 ******/

/****** Object:  Stored Procedure dbo.CheckAdminRights    Script Date: 2001-02-23 09:18:39 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.CheckExistsInMenu    Script Date: 2001-02-23 16:48:41 ******/

/****** Object:  Stored Procedure dbo.CheckExistsInMenu    Script Date: 2001-02-23 09:18:39 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.ClassificationAdd    Script Date: 2001-02-23 16:48:41 ******/

/****** Object:  Stored Procedure dbo.ClassificationAdd    Script Date: 2001-02-23 09:18:39 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.DeleteDocPermissionSetEx    Script Date: 2001-02-23 16:48:41 ******/

/****** Object:  Stored Procedure dbo.DeleteDocPermissionSetEx    Script Date: 2001-02-23 09:18:39 ******/
--
-- Procedure Create
-- dbo.DeleteDocPermissionSetEx
--
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

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.DelUser    Script Date: 2001-02-23 16:48:41 ******/

/****** Object:  Stored Procedure dbo.DelUser    Script Date: 2001-02-23 09:18:40 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.DelUserRoles    Script Date: 2001-02-23 16:48:41 ******/

/****** Object:  Stored Procedure dbo.DelUserRoles    Script Date: 2001-02-23 09:18:40 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.getBrowserDocChilds    Script Date: 2001-02-23 16:48:42 ******/

/****** Object:  Stored Procedure dbo.getBrowserDocChilds    Script Date: 2001-02-23 09:18:40 ******/
--
-- Procedure Create
-- dbo.getBrowserDocChilds
--
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

/****** Object:  Stored Procedure dbo.GetChilds    Script Date: 2001-02-23 16:48:42 ******/

/****** Object:  Stored Procedure dbo.GetChilds    Script Date: 2001-02-23 09:18:40 ******/
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
--  min(urc.role_id * ISNULL(~CAST(dps.permission_id AS BIT),1) * ISNULL(rr.set_id,1)),
  min(urc.role_id * ISNULL(dps.permission_id&~1,1) * ISNULL(rr.set_id,1)),
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
       and ISNULL(dps.permission_id&~1,1) != 1
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
       and ISNULL(dps.permission_id&~1,1) != 1
      )
     )
left join fileupload_docs fd
     on  fd.meta_id = c.to_meta_id
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time,
  fd.filename
order by  menu_sort,left(convert (varchar,date_created,120),10) desc
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
       and ISNULL(dps.permission_id&~1,1) != 1
      )
     )
left join fileupload_docs fd
     on  fd.meta_id = c.to_meta_id
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10),
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

/****** Object:  Stored Procedure dbo.getDocs    Script Date: 2001-02-23 16:48:42 ******/

/****** Object:  Stored Procedure dbo.getDocs    Script Date: 2001-02-23 09:18:40 ******/
--
-- Procedure Create
-- dbo.getDocs
--
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

/****** Object:  Stored Procedure dbo.GetDocTypesForUser    Script Date: 2001-02-23 16:48:42 ******/

/****** Object:  Stored Procedure dbo.GetDocTypesForUser    Script Date: 2001-02-23 09:18:40 ******/
--
-- Procedure Create
-- dbo.GetDocTypesForUser
--
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

/****** Object:  Stored Procedure dbo.GetDocTypesWithPermissions    Script Date: 2001-02-23 16:48:42 ******/

/****** Object:  Stored Procedure dbo.GetDocTypesWithPermissions    Script Date: 2001-02-23 09:18:40 ******/
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

/****** Object:  Stored Procedure dbo.GetImgs    Script Date: 2001-02-23 16:48:42 ******/

/****** Object:  Stored Procedure dbo.GetImgs    Script Date: 2001-02-23 09:18:40 ******/
--
-- Procedure Create
-- dbo.GetImgs
--
CREATE PROCEDURE GetImgs
@meta_id int AS
select '#img'+convert(varchar(5), name)+'#',name,imgurl,linkurl,width,height,border,v_space,h_space,image_name,align,alt_text,low_scr,target,target_name from images where meta_id = @meta_id


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.getMenuDocChilds    Script Date: 2001-02-23 16:48:42 ******/

/****** Object:  Stored Procedure dbo.getMenuDocChilds    Script Date: 2001-02-23 09:18:41 ******/
--
-- Procedure Create
-- dbo.getMenuDocChilds
--
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

/****** Object:  Stored Procedure dbo.GetPermissionSet    Script Date: 2001-02-23 16:48:42 ******/

/****** Object:  Stored Procedure dbo.GetPermissionSet    Script Date: 2001-02-23 09:18:41 ******/
--
-- Procedure Create
-- dbo.GetPermissionSet
--
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

/****** Object:  Stored Procedure dbo.GetTemplateGroupsForUser    Script Date: 2001-02-23 16:48:42 ******/

/****** Object:  Stored Procedure dbo.GetTemplateGroupsForUser    Script Date: 2001-02-23 09:18:41 ******/
--
-- Procedure Create
-- dbo.GetTemplateGroupsForUser
--
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

/****** Object:  Stored Procedure dbo.GetTemplateGroupsWithPermissions    Script Date: 2001-02-23 16:48:42 ******/

/****** Object:  Stored Procedure dbo.GetTemplateGroupsWithPermissions    Script Date: 2001-02-23 09:18:41 ******/
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

/****** Object:  Stored Procedure dbo.getTemplatesInGroup    Script Date: 2001-02-23 16:48:42 ******/

/****** Object:  Stored Procedure dbo.getTemplatesInGroup    Script Date: 2001-02-23 09:18:41 ******/
--
-- Procedure Create
-- dbo.getTemplatesInGroup
--
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

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.GetTextDocData    Script Date: 2001-02-23 16:48:42 ******/

/****** Object:  Stored Procedure dbo.GetTextDocData    Script Date: 2001-02-23 09:18:41 ******/
--
-- Procedure Create
-- dbo.GetTextDocData
--
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

/****** Object:  Stored Procedure dbo.GetTexts    Script Date: 2001-02-23 16:48:42 ******/

/****** Object:  Stored Procedure dbo.GetTexts    Script Date: 2001-02-23 09:18:41 ******/
--
-- Procedure Create
-- dbo.GetTexts
--
CREATE PROCEDURE GetTexts
@meta_id int AS
select '#txt'+convert(varchar(5), name)+'#',name,type,text from texts where meta_id = @meta_id


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.GetUserPermissionSet    Script Date: 2001-02-23 16:48:42 ******/

/****** Object:  Stored Procedure dbo.GetUserPermissionSet    Script Date: 2001-02-23 09:18:41 ******/
--
-- Procedure Create
-- dbo.GetUserPermissionSet
--
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
RIGHT JOIN  user_roles_crossref urc
      ON urc.user_id = @user_id
      AND rr.meta_id = @meta_id
      AND (
        rr.role_id = urc.role_id
       OR urc.role_id < 1
       )      
JOIN  meta m
      ON m.meta_id = @meta_id
      AND urc.user_id = @user_id
      AND (
        rr.meta_id = @meta_id
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

/****** Object:  Stored Procedure dbo.GetUserPermissionSetEx    Script Date: 2001-02-23 16:48:42 ******/

/****** Object:  Stored Procedure dbo.GetUserPermissionSetEx    Script Date: 2001-02-23 09:18:41 ******/
--
-- Procedure Create
-- dbo.GetUserPermissionSetEx
--
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

SET QUOTED_IDENTIFIER  ON    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.GetUserRoles    Script Date: 2001-02-23 16:48:42 ******/

/****** Object:  Stored Procedure dbo.GetUserRoles    Script Date: 2001-02-23 09:18:44 ******/
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

/****** Object:  Stored Procedure dbo.GetUserRolesDocPermissions    Script Date: 2001-02-23 16:48:42 ******/

/****** Object:  Stored Procedure dbo.GetUserRolesDocPermissions    Script Date: 2001-02-23 09:18:44 ******/
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
					ON	rr.role_id = r.role_id
					AND	rr.meta_id = @meta_id
LEFT JOIN 	user_roles_crossref urc
					ON	r.role_id = urc.role_id
      					AND	urc.user_id = @user_id
WHERE r.role_id > 0
ORDER BY role_name



GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.GetUserRolesIds    Script Date: 2001-02-23 16:48:42 ******/

/****** Object:  Stored Procedure dbo.GetUserRolesIds    Script Date: 2001-02-23 09:18:42 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.GetUsersWhoBelongsToRole    Script Date: 2001-02-23 16:48:42 ******/

/****** Object:  Stored Procedure dbo.GetUsersWhoBelongsToRole    Script Date: 2001-02-23 09:18:42 ******/
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

/****** Object:  Stored Procedure dbo.InheritPermissions    Script Date: 2001-02-23 16:48:42 ******/

/****** Object:  Stored Procedure dbo.InheritPermissions    Script Date: 2001-02-23 09:18:42 ******/
--
-- Procedure Create
-- dbo.InheritPermissions
--
CREATE PROCEDURE InheritPermissions @new_meta_id INT, @parent_meta_id INT, @doc_type INT AS
INSERT INTO doc_permission_sets
SELECT  @new_meta_id,
  ndps.set_id,
  ndps.permission_id | (ISNULL(CAST(permission_data AS BIT),0) * 65536)
FROM   new_doc_permission_sets ndps
LEFT JOIN  new_doc_permission_sets_ex ndpse ON ndps.meta_id = ndpse.meta_id
       AND ndps.set_id = ndpse.set_id
       AND ndpse.permission_id = 8
       AND ndpse.permission_data = @doc_type
       AND @doc_type != 2
WHERE ndps.meta_id = @parent_meta_id
GROUP BY ndps.meta_id,
  ndps.set_id,
  ndps.permission_id,
  ndpse.permission_id,
  ndpse.permission_data
INSERT INTO doc_permission_sets_ex
SELECT @new_meta_id,
  ndpse.set_id,
  ndpse.permission_id,
  ndpse.permission_data
FROM  new_doc_permission_sets_ex ndpse
WHERE ndpse.meta_id = @parent_meta_id
 AND @doc_type = 2
INSERT INTO new_doc_permission_sets
SELECT @new_meta_id,
  ndps.set_id,
  ndps.permission_id
FROM  new_doc_permission_sets ndps
WHERE ndps.meta_id = @parent_meta_id
 AND @doc_type = 2
INSERT INTO new_doc_permission_sets_ex
SELECT @new_meta_id,
  ndpse.set_id,
  ndpse.permission_id,
  ndpse.permission_data
FROM  new_doc_permission_sets_ex ndpse
WHERE ndpse.meta_id = @parent_meta_id
 AND @doc_type = 2
INSERT INTO roles_rights
SELECT role_id, @new_meta_id, set_id
FROM  roles_rights
WHERE meta_id = @parent_meta_id


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.PermissionsGetPermission    Script Date: 2001-02-23 16:48:42 ******/

/****** Object:  Stored Procedure dbo.PermissionsGetPermission    Script Date: 2001-02-23 09:18:43 ******/
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

/****** Object:  Stored Procedure dbo.RemoveUserFromRole    Script Date: 2001-02-23 16:48:42 ******/

/****** Object:  Stored Procedure dbo.RemoveUserFromRole    Script Date: 2001-02-23 09:18:43 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.RoleCountAffectedUsers    Script Date: 2001-02-23 16:48:42 ******/

/****** Object:  Stored Procedure dbo.RoleCountAffectedUsers    Script Date: 2001-02-23 09:18:43 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.RoleDelete    Script Date: 2001-02-23 16:48:42 ******/

/****** Object:  Stored Procedure dbo.RoleDelete    Script Date: 2001-02-23 09:18:43 ******/
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

/****** Object:  Stored Procedure dbo.RoleDeleteViewAffectedUsers    Script Date: 2001-02-23 16:48:43 ******/

/****** Object:  Stored Procedure dbo.RoleDeleteViewAffectedUsers    Script Date: 2001-02-23 09:18:43 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.SetDocPermissionSet    Script Date: 2001-02-23 16:48:43 ******/

/****** Object:  Stored Procedure dbo.SetDocPermissionSet    Script Date: 2001-02-23 09:18:44 ******/
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

/****** Object:  Stored Procedure dbo.SetDocPermissionSetEx    Script Date: 2001-02-23 16:48:43 ******/

/****** Object:  Stored Procedure dbo.SetDocPermissionSetEx    Script Date: 2001-02-23 09:18:44 ******/
--
-- Procedure Create
-- dbo.SetDocPermissionSetEx
--
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

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.UpdateParentsDateModified    Script Date: 2001-02-23 16:48:43 ******/

/****** Object:  Stored Procedure dbo.UpdateParentsDateModified    Script Date: 2001-02-23 09:18:44 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.UpdateTemplateTextsAndImages    Script Date: 2001-02-23 16:48:43 ******/

/****** Object:  Stored Procedure dbo.UpdateTemplateTextsAndImages    Script Date: 2001-02-23 09:18:44 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure dbo.Classification_Fix    Script Date: 2001-02-23 16:48:43 ******/

/****** Object:  Stored Procedure dbo.Classification_Fix    Script Date: 2001-02-23 09:18:44 ******/
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


GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET IDENTITY_INSERT sys_data ON
INSERT INTO sys_data (sys_id, type_id, value)  VALUES(1, 1, 0)
INSERT INTO sys_data (sys_id, type_id, value)  VALUES(2, 2, GETDATE())
INSERT INTO sys_data (sys_id, type_id, value) VALUES(3, 3, '')
SET IDENTITY_INSERT sys_data OFF

INSERT INTO users VALUES (1,'admin', 'admin', 'Admin', 'Super','','','','','','','','',0,1001,0,1,1,1,GETDATE())
INSERT INTO users VALUES (2,'user', 'user', 'User', 'Extern','','','','','','','','',0,1001,0,1,1,1,GETDATE())

INSERT INTO roles VALUES(0, 'Superadmin',0)
INSERT INTO roles VALUES(1, 'Users',1)

INSERT INTO user_roles_crossref VALUES(1,0)
INSERT INTO user_roles_crossref VALUES(2,1)

INSERT INTO meta VALUES (1001, '', 2, 'Startsidan', '','', 1, 0, 0, 1, 0, 1, 0, 1, 'se','', GETDATE(), GETDATE(), 1, 1, 0,'20010101','0000','','','_self','', 1)

INSERT INTO templates VALUES (1,'start.html', 'Start', 'se', 1,1,1)

INSERT INTO templategroups VALUES (0, 'Start')

INSERT INTO templates_cref VALUES(0,1)

INSERT INTO text_docs VALUES (1001, 1, 0, 1)

INSERT INTO roles_rights VALUES (1,1001,3)

INSERT INTO languages VALUES('se','','Svenska')

INSERT INTO lang_prefixes VALUES(1,'se')

INSERT INTO doc_types VALUES(2, 'se', 'Text-dokument')
INSERT INTO doc_types VALUES(5, 'se', 'URL-dokument')
INSERT INTO doc_types VALUES(6, 'se', 'Browserkontroll')
INSERT INTO doc_types VALUES(7, 'se', 'HTML-dokument')
INSERT INTO doc_types VALUES(8, 'se', 'Fil')
INSERT INTO doc_types VALUES(101, 'se', 'Diagram')
INSERT INTO doc_types VALUES(102, 'se', 'Konferens')

INSERT INTO texts VALUES( 1001, 1, '<h2>Startsidan.</h2><br><a href="/login/">Logga in!</a>',1)

INSERT INTO user_types VALUES(0, 'Anonyma användare')
INSERT INTO user_types VALUES(1, 'Autentiserde användare')
INSERT INTO user_types VALUES(2, 'Konferensanvändare')

