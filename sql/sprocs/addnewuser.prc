SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[AddNewuser]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[AddNewuser]
GO


CREATE PROCEDURE AddNewuser
/*
Adds a new user to the user table
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
 @zip varchar(15),
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
INSERT INTO users (user_id,login_name,login_password,first_name,last_name, title, company, address,city,zip,country,county_council,email,external,last_page,archive_mode,lang_id, user_type, active, create_date)
VALUES (@user_id, @login_name, @login_password, @first_name, @last_name, @title, @company,  @address, @city, @zip, @country,
   @county_council, @email, @external, @last_page, @archive_mode, @lang_id ,@user_type, @active, getDate())


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

