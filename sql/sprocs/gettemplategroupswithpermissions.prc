SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetTemplateGroupsWithPermissions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetTemplateGroupsWithPermissions]
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
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

