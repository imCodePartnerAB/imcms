SET QUOTED_IDENTIFIER ON 
;
SET ANSI_NULLS ON 
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[IPAccessAdd]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[IPAccessAdd]
;


CREATE PROCEDURE IPAccessAdd
/*
This function adds a new ip-access to the db. Used by AdminManager
*/
 @user_id int,
 @ip_start DECIMAL , 
 @ip_end DECIMAL
AS
INSERT INTO IP_ACCESSES ( user_id , ip_start , ip_end )
VALUES ( @user_id , @ip_start , @ip_end )


;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

