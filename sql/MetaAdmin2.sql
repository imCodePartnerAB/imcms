/****** Object:  Stored Procedure imse.getBrowserDocChilds    Script Date: 2000-10-30 15:27:28 ******/
if exists (select * from sysobjects where id = object_id(N'[imse].[getBrowserDocChilds]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[getBrowserDocChilds]
GO

/****** Object:  Stored Procedure imse.getDocs    Script Date: 2000-10-30 15:27:28 ******/
if exists (select * from sysobjects where id = object_id(N'[imse].[getDocs]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[getDocs]
GO

/****** Object:  Stored Procedure imse.getMenuDocChilds    Script Date: 2000-10-30 15:27:28 ******/
if exists (select * from sysobjects where id = object_id(N'[imse].[getMenuDocChilds]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [imse].[getMenuDocChilds]
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure imse.getBrowserDocChilds    Script Date: 2000-10-30 15:27:28 ******/
setuser N'imse'
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

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure imse.getDocs    Script Date: 2000-10-30 15:27:29 ******/
setuser N'imse'
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

/****** Object:  Stored Procedure imse.getMenuDocChilds    Script Date: 2000-10-30 15:27:29 ******/
setuser N'imse'
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

