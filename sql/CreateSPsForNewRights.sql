if exists (select * from sysobjects where id = object_id(N'[dbo].[CheckAdminRights]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[CheckAdminRights]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[DeleteDocPermissionSetEx]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[DeleteDocPermissionSetEx]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[DeleteNewDocPermissionSetEx]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[DeleteNewDocPermissionSetEx]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetDocTypes]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetDocTypes]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetChilds]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetChilds]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetDocTypesWithNewPermissions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetDocTypesWithNewPermissions]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetDocTypesWithPermissions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetDocTypesWithPermissions]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetNewPermissionSet]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetNewPermissionSet]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[getMenuDocChilds]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[getMenuDocChilds]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[getBrowserDocChilds]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[getBrowserDocChilds]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetRolesDocPermissions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetRolesDocPermissions]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetPermissionSet]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetPermissionSet]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetTemplateGroupsWithNewPermissions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetTemplateGroupsWithNewPermissions]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetTemplateGroupsWithPermissions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetTemplateGroupsWithPermissions]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetUserPermissionSet]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserPermissionSet]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[GetUserRolesDocPermissions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetUserRolesDocPermissions]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[SetDocPermissionSet]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SetDocPermissionSet]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[SetDocPermissionSetEx]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SetDocPermissionSetEx]
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

if exists (select * from sysobjects where id = object_id(N'[dbo].[UpdateParentsDateModified]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[UpdateParentsDateModified]
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

CREATE PROCEDURE DeleteDocPermissionSetEx @meta_id INT, @set_id INT AS

/*
	Delete extended permissions for a permissionset for a document
*/

DELETE FROM		doc_permission_sets_ex
WHERE		meta_id = @meta_id
		AND	set_id = @set_id

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  ON    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE DeleteNewDocPermissionSetEx @meta_id INT, @set_id INT AS

/*
	Delete extended permissions for a permissionset for a document
*/

DELETE FROM		new_doc_permission_sets_ex
WHERE		meta_id = @meta_id
		AND	set_id = @set_id


GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE GetDocTypes AS

SELECT doc_type,type FROM doc_types
GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
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
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE GetDocTypesWithNewPermissions @meta_id INT,@set_id INT AS

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
ORDER	BY	CAST(ISNULL(dpse.permission_data,-1)+1  AS BIT) DESC,doc_type



GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE GetDocTypesWithPermissions @meta_id INT,@set_id INT AS

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
ORDER	BY	CAST(ISNULL(dpse.permission_data,-1)+1  AS BIT) DESC,doc_type


GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE GetNewPermissionSet @meta_id INT, @set_id INT, @lang_prefix VARCHAR(3) AS


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


GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
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
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
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
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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



GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  ON    SET ANSI_NULLS  ON 
GO

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
ORDER		BY CAST(ISNULL(dpse.permission_data,-1)+1  AS BIT) DESC, group_name



GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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
ORDER		BY CAST(ISNULL(dpse.permission_data,-1)+1  AS BIT) DESC, group_name


GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

CREATE PROCEDURE GetUserPermissionSet @meta_id INT, @user_id INT AS

/*
	Finds out what is the most privileged permission_set a user has for a document.

	Column 1:	The users most privileged set_id
	Column 2:	The users permission-set for this set_id
	Column 3:	The permissions for this document. ( At the time of this writing, the only permission there is wether or not set_id 1 is more privileged than set_id 2, and it's stored in bit 0 )

	set_id's:

	0 - most privileged (full rights)
	1 & 2 - misc. They may be equal, and 1 may have permission to modify 2.
	3 - only read rights
	4 - least privileged (no rights)
*/

SELECT TOP 1	(MIN(rr.set_id)*CAST(MIN(ISNULL(urc.role_id,1)) AS BIT)),
		ISNULL(dps.permission_id,0),
		m.permissions
FROM 		roles_rights rr
JOIN 		user_roles_crossref urc
						ON	urc.user_id = @user_id
						AND	(
								rr.role_id = urc.role_id
							OR	urc.role_id < 1
							)						
JOIN		meta m
						ON	m.meta_id = @meta_id
						AND	rr.meta_id = m.meta_id
LEFT JOIN	doc_permission_sets dps
						ON	dps.meta_id = m.meta_id
						AND	rr.set_id = dps.set_id
GROUP BY	ISNULL(dps.permission_id,0),m.permissions
ORDER BY	(MIN(rr.set_id)*CAST(MIN(ISNULL(urc.role_id,1)) AS BIT))




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
						ON 	rr.role_id = r.role_id
						AND	rr.meta_id = @meta_id
LEFT JOIN	user_roles_crossref urc
						ON	r.role_id = urc.role_id
						AND	urc.user_id = @user_id
WHERE	r.role_id > 0
ORDER BY	role_name


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
WHERE	meta_id = @meta_id
AND		set_id = @set_id

-- Insert new value
INSERT INTO	doc_permission_sets
VALUES	(@meta_id,@set_id,@permission_id)
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
INSERT INTO	doc_permission_sets_ex
VALUES	(@meta_id,@set_id,@permission_id, @permission_data)

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
WHERE	meta_id = @meta_id
AND		set_id = @set_id

-- Insert new value
INSERT INTO	new_doc_permission_sets
VALUES	(@meta_id,@set_id,@permission_id)

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
INSERT INTO	new_doc_permission_sets_ex
VALUES	(@meta_id,@set_id,@permission_id, @permission_data)



GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

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

