SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FindUserName]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[FindUserName]
;


CREATE PROCEDURE FindUserName
 @userName varchar(50)
AS
SELECT  u.login_name
FROM users u
WHERE u.login_name = @userName


;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

