SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetTemplateGroupsForUser]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetTemplateGroupsForUser]
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
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

