SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetPermissionSet]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetPermissionSet]
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
       WHERE dps.meta_id = @meta_id
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
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

