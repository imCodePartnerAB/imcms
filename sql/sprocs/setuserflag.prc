SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[SetUserFlag]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SetUserFlag]
;

CREATE PROCEDURE SetUserFlag @user_id INT, @flag_name VARCHAR(64) AS

INSERT INTO 	user_flags_crossref 
SELECT	@user_id, user_flags.user_flag_id
FROM 		user_flags
WHERE	name = @flag_name
;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

