SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetDocTypesWithNewPermissions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetDocTypesWithNewPermissions]
;


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


;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

