SET QUOTED_IDENTIFIER ON 
;
SET ANSI_NULLS ON 
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[CheckUserDocSharePermission]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[CheckUserDocSharePermission]
;


CREATE PROCEDURE CheckUserDocSharePermission @user_id INT, @meta_id INT AS
/**
    DOCME: Document me!
**/
SELECT m.meta_id
FROM meta m
JOIN user_roles_crossref urc
				ON	urc.user_id = @user_id
				AND	m.meta_id = @meta_id
LEFT join roles_rights rr
				ON	rr.meta_id = m.meta_id
				AND	rr.role_id = urc.role_id
WHERE				(
						shared = 1
					OR	rr.set_id < 3
					OR	urc.role_id = 0
				)
GROUP BY m.meta_id


;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

