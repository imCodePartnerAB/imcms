SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetDocTypesForUser]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetDocTypesForUser]
;


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


;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

