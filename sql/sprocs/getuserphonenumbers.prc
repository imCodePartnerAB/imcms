SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

/****** Object:  Stored Procedure dbo.GetUserPhoneNumbers    Script Date: 2003-01-15 14:15:52 ******/
if exists (select * from sysobjects where id = object_id('dbo.GetUserPhoneNumbers') and sysstat & 0xf = 4)
	drop procedure dbo.GetUserPhoneNumbers
GO


CREATE  PROCEDURE GetUserPhoneNumbers
/*
 Return a users phonenumbers. Used by AdminUserProps servlet
*/
 @user_id int
AS

SELECT	phones.phone_id, phones.number, phones.user_id, phones.phonetype_id, phonetypes.typename
FROM	phones INNER JOIN users 
			ON phones.user_id = users.user_id INNER JOIN phonetypes 
			ON phones.phonetype_id = phonetypes.phonetype_id AND users.lang_id = phonetypes.lang_id
WHERE     (phones.user_id = @user_id)


GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO



