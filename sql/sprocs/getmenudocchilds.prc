SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[getMenuDocChilds]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[getMenuDocChilds]
GO


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
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

