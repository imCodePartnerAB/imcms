SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[PermissionsGetPermission]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[PermissionsGetPermission]
GO


CREATE PROCEDURE PermissionsGetPermission @login_name varchar(15), @permission int AS
/*
*/
select login_password, first_name, last_name, email, min(permissions&@permission), lang_prefix 
from users u 
join lang_prefixes lp 
    on u.lang_id = lp.lang_id 
join user_roles_crossref urc 
    on u.user_id = urc.user_id left 
join roles r 
    on r.role_id = urc.role_id
where login_name = @login_name
group by login_password, first_name, last_name, email, lang_prefix 


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

