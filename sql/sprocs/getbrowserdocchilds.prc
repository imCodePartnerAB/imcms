SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[getBrowserDocChilds]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[getBrowserDocChilds]
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
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

