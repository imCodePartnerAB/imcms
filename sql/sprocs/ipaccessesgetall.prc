SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[IPAccessesGetAll]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[IPAccessesGetAll]
GO


CREATE PROCEDURE IPAccessesGetAll AS
/*
Lets get all IPaccesses from db. Used  by the AdminIpAccesses
*/
SELECT ip.ip_access_id, ip.user_id, usr.login_name, ip.ip_start, ip.ip_end    
FROM IP_ACCESSES ip, USERS usr
WHERE ip.user_id = usr.user_id


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

