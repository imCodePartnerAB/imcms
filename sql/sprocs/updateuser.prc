SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[UpdateUser]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[UpdateUser]
GO


CREATE PROCEDURE UpdateUser
/*
usertype. 0=special, 1=default, 2=conferenceuser 
*/
 @user_id int,
 @login_name varchar(50),
 @login_password varchar(15),
 @first_name varchar(25),
 @last_name varchar(30),
 @title varchar(30),
 @company varchar(30),
 @address varchar(40),
 @city varchar(30),
 @zip varchar (15),
 @country varchar(30),
 @county_council varchar(30),
 @email varchar(50),
 @external int,
 @last_page int,
 @archive_mode int,
 @lang_id int,
 @user_type int,
 @active int
AS
UPDATE users 
SET login_name = @login_name,
login_password = @login_password,
first_name = @first_name,
last_name = @last_name,
title = @title,
company = @company,
address =  @address,
city = @city,
zip = @zip,
country = @country,
county_council = @county_council,
email = @email,
user_type = @user_type,
active = @active,
lang_id = @lang_id
WHERE user_id = @User_id 


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

