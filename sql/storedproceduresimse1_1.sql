if exists (select * from sysobjects where id = object_id(N'[imse].[CheckExistsInMenu]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[CheckExistsInMenu]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[DelUser]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[DelUser]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[DelUserRoles]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[DelUserRoles]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[FindMetaId]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[FindMetaId]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[FindUserName]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[FindUserName]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[GetAdminChilds]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[GetAdminChilds]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[GetAllRoles]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[GetAllRoles]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[GetAllUsers]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[GetAllUsers]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[GetAllUsersInList]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[GetAllUsersInList]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[getBrowserDocChilds]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[getBrowserDocChilds]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[GetCategoryUsers]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[GetCategoryUsers]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[GetChilds]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[GetChilds]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[GetCurrentSessionCounter]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[GetCurrentSessionCounter]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[GetCurrentSessionCounterDate]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[GetCurrentSessionCounterDate]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[getDocs]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[getDocs]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[GetDocType]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[GetDocType]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[GetHighestUserId]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[GetHighestUserId]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[GetImgs]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[GetImgs]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[GetLangPrefix]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[GetLangPrefix]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[GetLangPrefixFromId]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[GetLangPrefixFromId]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[getLanguages]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[getLanguages]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[getMenuDocChilds]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[getMenuDocChilds]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[GetMetaPathInfo]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[GetMetaPathInfo]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[GetNoOfTemplates]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[GetNoOfTemplates]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[getTemplategroups]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[getTemplategroups]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[getTemplates]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[getTemplates]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[getTemplatesInGroup]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[getTemplatesInGroup]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[GetTexts]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[GetTexts]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[GetUserCreateDate]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[GetUserCreateDate]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[GetUserId]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[GetUserId]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[GetUserIdFromName]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[GetUserIdFromName]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[GetUserInfo]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[GetUserInfo]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[GetUserNames]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[GetUserNames]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[GetUserPassword]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[GetUserPassword]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[GetUserRoles]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[GetUserRoles]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[GetUserRolesIds]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[GetUserRolesIds]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[GetUserType]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[GetUserType]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[GetUserTypes]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[GetUserTypes]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[getUserWriteRights]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[getUserWriteRights]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[IMC_AddChild]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[IMC_AddChild]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[IMC_AddImage]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[IMC_AddImage]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[IMC_AddImageRef]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[IMC_AddImageRef]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[IMC_AddOwnerRights]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[IMC_AddOwnerRights]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[IMC_AddRole]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[IMC_AddRole]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[IMC_AddTextDoc]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[IMC_AddTextDoc]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[IMC_AddTexts]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[IMC_AddTexts]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[IMC_AddUserRights]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[IMC_AddUserRights]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[IMC_CheckMenuSort]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[IMC_CheckMenuSort]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[IMC_CreateNewMeta]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[IMC_CreateNewMeta]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[IMC_ExecuteExample]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[IMC_ExecuteExample]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[IMC_GetMaxMetaID]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[IMC_GetMaxMetaID]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[IMC_GetNbrOfText]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[IMC_GetNbrOfText]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[IMC_GetSortOrderNum]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[IMC_GetSortOrderNum]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[IMC_GetTemplateId]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[IMC_GetTemplateId]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[IncSessionCounter]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[IncSessionCounter]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[IPAccessAdd]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[IPAccessAdd]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[IPAccessDelete]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[IPAccessDelete]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[IPAccessesGetAll]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[IPAccessesGetAll]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[IPAccessUpdate]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[IPAccessUpdate]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[magnustest]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[magnustest]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[meta_select]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[meta_select]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[RoleAddNew]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[RoleAddNew]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[RoleAdminGetAll]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[RoleAdminGetAll]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[RoleCount]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[RoleCount]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[RoleCountAffectedUsers]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[RoleCountAffectedUsers]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[RoleDelete]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[RoleDelete]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[RoleDeleteViewAffectedMetaIds]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[RoleDeleteViewAffectedMetaIds]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[RoleDeleteViewAffectedUsers]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[RoleDeleteViewAffectedUsers]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[RoleFindName]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[RoleFindName]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[RoleGetName]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[RoleGetName]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[RoleUpdateName]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[RoleUpdateName]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[SetSessionCounterDate]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[SetSessionCounterDate]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[SystemMessageGet]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[SystemMessageGet]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[SystemMessageSet]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[SystemMessageSet]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[test]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[test]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[TestJanusDB]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[TestJanusDB]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[UpdateTemplateTextsAndImages]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[UpdateTemplateTextsAndImages]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[UpdateUser]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[UpdateUser]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[AddNewuser]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[AddNewuser]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[AddUserRole]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[AddUserRole]
GO

if exists (select * from sysobjects where id = object_id(N'[imse].[CheckAdminRights]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[CheckAdminRights]
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
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
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
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
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE DelUserRoles
 @aUserId int
AS
 DELETE 
 FROM user_roles_crossref
 WHERE user_id = @aUserId

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE [FindMetaId]
 @meta_id int
 AS
SELECT meta_id 
FROM meta
WHERE meta_id = @meta_id

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
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
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

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

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE GetAllRoles AS
  SELECT *
 FROM roles
 
 ORDER BY role_id

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE [GetAllUsers] AS
  select *
 from USERS
 
 order by  last_name

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE GetAllUsersInList AS
/*
This function is used from AdminIpAcces servlet to generate a list
*/
SELECT user_id, last_name + ', ' + first_name from users
ORDER BY last_name

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

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

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  ON    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

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
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

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
  min(            -- This field will have 0 in it, if the user may change the document
   coalesce(urc.role_id,rr.permission_id-3,ur.permission_id-99)*
   coalesce(ur.permission_id-99,rr.permission_id-3,urc.role_id)*
   coalesce(rr.permission_id-3,urc.role_id,ur.permission_id-99)
  )
from   childs c
join   meta m    
     on   m.meta_id = c.to_meta_id    -- meta.meta_id corresponds to childs.to_meta_id
      and m.activate != 0      -- Only include the documents that are active in the meta table.
      and c.meta_id = @meta_id     -- Only include documents that are children to this particular meta_id
left join  roles_rights rr         -- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
     on  c.to_meta_id = rr.meta_id     -- Only include rows with the documents we are interested in
       --and (rr.permission_id & 1) != 0    -- Only include permissions where the first bit is set. "Read rights" (May skip this now, since right now, if we have anything, this matches.)
join  user_roles_crossref urc        -- This table tells us which users have which roles
     on  urc.user_id = @user_id     -- Only include the rows with the user we are interested in...
      and ( 
        rr.role_id = urc.role_id    -- Include rows where the users roles match the roles that have permissions on the documents
       or urc.role_id = 0     -- and also include the rows that tells us this user is a superadmin
       or ( 
         m.show_meta != 0   -- and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
        and rr.permission_id != 3   -- Don't include show_meta if it is not needed.
        ) 
       )
left join  user_rights ur         -- We may have permission, even though we don't own any documents... That is, if we're roleadmins or superadmin. When we lose ownership-based rights, this may be skipped.
     on  ur.meta_id = c.to_meta_id    -- Only include rows with the documents we are interested in
      and ur.user_id =  urc.user_id     -- Only include rows with the user  we are interested in
      and ur.permission_id = 99     -- Only include rows that mean "Ownership"
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time
order by  menu_sort,c.manual_sort_order desc
end
else if @sort_by = 3
begin
select  to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time,
  min(
   coalesce(urc.role_id,rr.permission_id-3,ur.permission_id-99)*
   coalesce(ur.permission_id-99,rr.permission_id-3,urc.role_id)*
   coalesce(rr.permission_id-3,urc.role_id,ur.permission_id-99)
  )
from   childs c
join   meta m    
     on   m.meta_id = c.to_meta_id    -- meta.meta_id corresponds to childs.to_meta_id
      and m.activate != 0      -- Only include the documents that are active in the meta table.
      and c.meta_id = @meta_id     -- Only include documents that are children to this particular meta_id
left join  roles_rights rr         -- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
     on  c.to_meta_id = rr.meta_id     -- Only include rows with the documents we are interested in
       --and (rr.permission_id & 1) != 0    -- Only include permissions where the first bit is set. "Read rights" (May skip this now, since right now, if we have anything, this matches.)
join  user_roles_crossref urc        -- This table tells us which users have which roles
     on  urc.user_id = @user_id     -- Only include the rows with the user we are interested in...
      and ( 
        rr.role_id = urc.role_id    -- Include rows where the users roles match the roles that have permissions on the documents
       or urc.role_id = 0     -- and also include the rows that tells us this user is a superadmin
       or ( 
         m.show_meta != 0   -- and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
        and rr.permission_id != 3   -- Don't include show_meta if it is not needed.
        ) 
       )
left join  user_rights ur         -- We may have permission, even though we don't own any documents... That is, if we're roleadmins or superadmin. When we lose ownership-based rights, this may be skipped.
     on  ur.meta_id = c.to_meta_id    -- Only include rows with the documents we are interested in
      and ur.user_id =  urc.user_id     -- Only include rows with the user  we are interested in
      and ur.permission_id = 99     -- Only include rows that mean "Ownership"
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time
order by  menu_sort,left(convert (varchar,date_created,120),10) desc
end
else if @sort_by = 1
begin
select  to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time,
  min(
   coalesce(urc.role_id,rr.permission_id-3,ur.permission_id-99)*
   coalesce(ur.permission_id-99,rr.permission_id-3,urc.role_id)*
   coalesce(rr.permission_id-3,urc.role_id,ur.permission_id-99)
  )
from   childs c
join   meta m    
     on   m.meta_id = c.to_meta_id    -- meta.meta_id corresponds to childs.to_meta_id
      and m.activate != 0      -- Only include the documents that are active in the meta table.
      and c.meta_id = @meta_id     -- Only include documents that are children to this particular meta_id
left join  roles_rights rr         -- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
     on  c.to_meta_id = rr.meta_id     -- Only include rows with the documents we are interested in
       --and (rr.permission_id & 1) != 0    -- Only include permissions where the first bit is set. "Read rights" (May skip this now, since right now, if we have anything, this matches.)
join  user_roles_crossref urc        -- This table tells us which users have which roles
     on  urc.user_id = @user_id     -- Only include the rows with the user we are interested in...
      and ( 
        rr.role_id = urc.role_id    -- Include rows where the users roles match the roles that have permissions on the documents
       or urc.role_id = 0     -- and also include the rows that tells us this user is a superadmin
       or ( 
         m.show_meta != 0   -- and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
        and rr.permission_id != 3   -- Don't include show_meta if it is not needed.
        ) 
       )
left join  user_rights ur         -- We may have permission, even though we don't own any documents... That is, if we're roleadmins or superadmin. When we lose ownership-based rights, this may be skipped.
     on  ur.meta_id = c.to_meta_id    -- Only include rows with the documents we are interested in
      and ur.user_id =  urc.user_id     -- Only include rows with the user  we are interested in
      and ur.permission_id = 99     -- Only include rows that mean "Ownership"
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time
order by  menu_sort,meta_headline
end

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE GetCurrentSessionCounter 
 
AS
 SELECT value 
 FROM sys_data
 WHERE type_id  = 1

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE GetCurrentSessionCounterDate
 
AS
 SELECT value 
 FROM sys_data
 WHERE type_id  = 2

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

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

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
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
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE GetHighestUserId
AS
--DECLARE @retVal int
SELECT MAX(user_id) +1
FROM users

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE GetImgs
@meta_id int AS
select '#img'+convert(varchar(5), name)+'#',name,imgurl,linkurl,width,height,border,v_space,h_space,image_name,align,alt_text,low_scr,target,target_name from images where meta_id = @meta_id

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
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
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
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
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE getLanguages AS
select lang_prefix,language from languages order by language

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

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

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
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
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE GetNoOfTemplates AS
select count(*) from templates

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE getTemplategroups AS
select group_id,group_name from templategroups order by group_name

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE getTemplates AS
select template_id, simple_name from templates

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE getTemplatesInGroup @grp_id INT AS
SELECT t.template_id,simple_name
FROM  templates t JOIN
  templates_cref c
ON  t.template_id = c.template_id
WHERE c.group_id = @grp_id
ORDER BY simple_name

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE GetTexts
@meta_id int AS
select '#txt'+convert(varchar(5), name)+'#',name,type,text from texts where meta_id = @meta_id

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  ON    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
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
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE GetUserId 
 @aUserId int
AS
 SELECT user_id 
 FROM users
 WHERE user_id  = @aUserId

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
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
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  ON    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
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
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
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
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
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
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
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
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
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
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
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
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  ON    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE GetUserTypes
/*
Used to generate a list with all type of users. Used from AdminUserProps
*/
 AS
 SELECT DISTINCT user_type, type_name 
 FROM user_types

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE getUserWriteRights AS
DECLARE @user int
DECLARE @doc int
select user_id from user_roles_crossref where user_id = @user and role_id = 0 -- Returnerar en rad om användare 1 är superadmin
select user_id from user_rights where meta_id = @doc and user_id = @user and permission_id = 99 -- Returnerar en rad om användare 1 skapade dokument 1351
select user_id from roles_rights join user_roles_crossref on roles_rights.role_id = user_roles_crossref.role_id where meta_id = @doc and user_id = @doc and permission_id = 3 -- Returnerar en rad om användare 1 är medlem i en grupp som har skrivrättigheter i dokument 1351

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE IMC_AddChild 
 @meta_id int , 
 @newMetaId int ,
 @doc_menu_no int,
 @newSortNo int
AS
INSERT INTO childs(meta_id,to_meta_id,menu_sort,manual_sort_order)
VALUES ( @meta_id , @newMetaId , @doc_menu_no , @newSortNo )

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE IMC_AddImage
 @newMetaId int ,
 @t int
 AS
/*
INSERT INTO images( meta_id, width, height, border, v_space, h_space,
 name, image_name,target,target_name,align,alt_text,low_scr,imgurl,linkurl)
VALUES ( @newMetaId, 0, 0, 0, 0, 0, @t, '_self' ,  '_top', '' , '' ) 
*/

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE IMC_AddImageRef
 @newMetaId int ,
 @t int
 AS
-- add imageref to database      
INSERT INTO images (meta_id, width, height, border, v_space, h_space,
name,image_name,target,target_name,align,alt_text,low_scr,imgurl,linkurl)
VALUES ( @newMetaId, 0, 0, 0, 0, 0,  @t, '',  '_self' , '',  '_top',  '','' ,'' , '' )

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE IMC_AddOwnerRights
 @metaId int,
 @userId int
AS
INSERT INTO user_rights
VALUES ( @userId , @metaId , 99 ) 

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

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

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE IMC_AddTextDoc
 @meta_id int ,
 @template_id int ,
 @sort_order int
AS
INSERT INTO text_docs ( meta_id,template_id,sort_order )
VALUES ( @meta_id , @template_id , @sort_order ) 

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

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

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

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

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE IMC_CheckMenuSort
 @meta_id int,
 @doc_menu_no int
AS
-- test if this is the first child with this  menusort
SELECT to_meta_id 
FROM childs
WHERE meta_id =  @meta_id 
AND menu_sort = @doc_menu_no

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

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
  
  

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE [IMC_ExecuteExample] AS
-- Lets create the templates library path as well
EXEC AddNewTemplateLib 1

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE IMC_GetMaxMetaID AS
/* get max meta id */
select max(meta_id) from meta

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE IMC_GetNbrOfText
 @meta_id int 
 AS
-- find no_of_txt for the template
SELECT no_of_txt,no_of_img,no_of_url 
FROM text_docs,templates
WHERE meta_id = @meta_id
AND templates.template_id = text_docs.template_id ;

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

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

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE IMC_GetTemplateId
 @meta_id int
AS
SELECT template_id
FROM text_docs
WHERE meta_id = @meta_id    

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
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
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

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
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
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
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE IPAccessesGetAll AS
/*
Lets get all IPaccesses from db. Used  by the AdminIpAccesses
*/
SELECT ip.ip_access_id, ip.user_id, usr.login_name, ip.ip_start, ip.ip_end    
FROM IP_ACCESSES ip, USERS usr
WHERE ip.user_id = usr.user_id

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

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
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE magnustest 
@text varchar(80)
AS 
update texts
set text = @text
where meta_id = 4260
and name = 1

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

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

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
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
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE RoleAdminGetAll AS
/*
 Used by AdminRoles servlet to retrieve all roles except the Superadmin role
*/
SELECT role_id , role_name FROM ROLES
WHERE role_id != 0
ORDER BY role_id

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
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
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
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
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
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
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
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
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
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
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
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
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
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
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
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
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE SetSessionCounterDate
   @new_date varchar(20)
AS
      
 update sys_data
 set value = @new_date where type_id = 2
 
  return

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE SystemMessageGet AS
/*
 Used by the AdminSystemMessage servlet to retrieve the systemmessage
*/
SELECT s.value
FROM sys_data s
WHERE s.type_id = 3

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
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
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE test AS
SELECT COUNT(usr.role_id) , (RTRIM(last_name) + ', ' + RTRIM(first_name))   
FROM user_roles_crossref usr, roles r, users u
WHERE usr.ROLE_ID = 5 
AND usr.user_id = u.user_id
GROUP BY (RTRIM(last_name) + ', ' + RTRIM(first_name)), usr.role_id

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE TestJanusDB
AS
select 'Hurra!'

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
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
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
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
address =  @address,
city = @city,
zip = @zip,
country = @country,
county_council = @county_council,
email = @email,
user_type = @user_type,
active = @active
WHERE user_id = @User_id 

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  ON    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
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

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
GO

CREATE PROCEDURE AddUserRole
/* Adds a role to a particular user
*/
 @aUser_id int,
 @aRole_id int
AS
 INSERT INTO  user_roles_crossref(user_id, role_id)
 VALUES( @aUser_id , @aRole_id)
 

GO
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

setuser N'dbo'
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
setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

