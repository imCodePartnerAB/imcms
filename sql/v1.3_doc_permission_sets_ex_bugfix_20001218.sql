
if exists (select * from sysobjects where id = object_id(N'[GetDocTypesWithNewPermissions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [GetDocTypesWithNewPermissions]
GO

if exists (select * from sysobjects where id = object_id(N'[GetDocTypesWithPermissions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [GetDocTypesWithPermissions]
GO

if exists (select * from sysobjects where id = object_id(N'[GetTemplateGroupsWithNewPermissions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [GetTemplateGroupsWithNewPermissions]
GO

if exists (select * from sysobjects where id = object_id(N'[GetTemplateGroupsWithPermissions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [GetTemplateGroupsWithPermissions]
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
							AND	dpse.permission_id = 524288
ORDER		BY CAST(ISNULL(dpse.permission_data,-1)+1  AS BIT) DESC, group_name


GO

