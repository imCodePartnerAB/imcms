if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[D_getAppointment]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[D_getAppointment]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[D_getCalender]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[D_getCalender]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[D_getDaysAppointments]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[D_getDaysAppointments]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[D_getLogg]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[D_getLogg]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[D_getMonthsAppointments]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[D_getMonthsAppointments]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[D_getPage]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[D_getPage]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[D_getPlayer]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[D_getPlayer]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[D_getPlayerId]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[D_getPlayerId]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[D_getPlayerNames]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[D_getPlayerNames]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[D_loggAction]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[D_loggAction]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[D_newAppointment]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[D_newAppointment]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[D_newCalender]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[D_newCalender]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[D_newPage]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[D_newPage]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[D_newPlayer]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[D_newPlayer]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[D_removeAppointment]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[D_removeAppointment]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[D_removeCalender]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[D_removeCalender]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[D_removePlayer]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[D_removePlayer]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[D_setTitle]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[D_setTitle]
GO

SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE D_getAppointment
	@app_id	int
 AS

SELECT * from D_appointment
WHERE appointment_id = @app_id
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE D_getCalender
	@meta_id	int
AS

SELECT *
FROM D_calender
WHERE meta_id = @meta_id
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE D_getDaysAppointments
	@date		datetime,
	@player_id	int,
	@meta_id	int

AS

SELECT * FROM D_appointment WHERE
DATEPART(year, @date) = DATEPART(year, startTime) AND
DATEPART(month, @date) = DATEPART(month, startTime) AND
DATEPART(day, @date) = DATEPART(day, startTime) AND
page_id IN
( SELECT page_id FROM D_page WHERE players_id = @player_id AND meta_id = @meta_id)
ORDER BY startTime
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE D_getLogg
	@startTime	datetime = null,
	@endTime	datetime = null
AS


IF @endTime = NULL BEGIN
	SELECT @endTime = DATEADD(MONTH, -1, GETDATE())
END
IF @startTime = NULL BEGIN
	SELECT @startTime = GETDATE()
	SELECT @endTime = DATEADD(MONTH, -1, GETDATE())
END

SELECT meta_id, player_id, logg_date, logg_action, logg_exep
FROM D_logg WHERE logg_date < @startTime AND logg_date > @endTime
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE D_getMonthsAppointments
	@year		int,
	@month	int,
	@player_id	int,
	@meta_id	int

AS

SELECT * FROM D_appointment WHERE YEAR(startTime) = @year AND MONTH(startTime) = @month AND page_id IN
( SELECT page_id FROM D_page WHERE players_id = @player_id AND meta_id = @meta_id)
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE D_getPage
	@players_id	int,
	@meta_id	int

AS

SELECT page_id FROM D_page WHERE players_id = @players_id AND meta_id = @meta_id
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE D_getPlayer
	@players_id	int

AS

DECLARE @user_id int
SELECT user_id FROM D_players WHERE players_id = @players_id
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE D_getPlayerId
	@userId	int,
	@meta_id	int
AS

--SELECT players_id FROM D_players WHERE user_id = @userId

select players_id from d_players where user_id = @userId AND players_id IN 
	(select players_id from d_page where meta_id = @meta_id)
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE D_getPlayerNames
	@meta_id	int

AS

DECLARE @user_id int
SELECT user_id, players_id FROM D_players WHERE players_id IN
	( SELECT players_id FROM D_page WHERE meta_id = @meta_id )
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS ON 
GO

/*
	Creates a logg entry.
	This procedure should be called when
	logging friendly actions, not errors
	or exceptions.
	PARAMS:
		@meta_id	the calender/document where the action occured. cannot be NULL
		@logg_action	text to describe the action
		@player_id	the player whos action it was
*/

CREATE PROCEDURE D_loggAction
	(@meta_id 	int,
	 @logg_action 	varchar(50) = NULL,
	 @logg_exep	varchar(50) = NULL,
	 @player_id 	int = 0)

AS INSERT INTO D_logg
	 (logg_date,
	 meta_id,
	 logg_action,
	 logg_exep,
	 player_id) 
 
VALUES 
	(GETDATE(),
	 @meta_id,
	 @logg_action,
	 @logg_exep,
	 @player_id)
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

/*
	Creates a new appointment.
	Enters a new row into D_appointment
	PARAMS:
		@startTime	start of the appointment. cannot be NULL
		@page_id	the page who created the appointment. a page is owned by a player. cannot be NULL
		@titel		the displayed titel
		@endTime	end of the appointment
		@place		place to meet
		@notes		users notes about the appointment

SET DATEFORMAT ymd -- Year, month, day

*/

CREATE PROCEDURE D_newAppointment
	(@startTime 	datetime,
	 @page_id 	int,
	 @endTime 	datetime = NULL,
	 @place 	varchar(50) = NULL,
	 @notes 	varchar(255) = NULL,
	 @titel 		varchar(255) = NULL)

AS

INSERT INTO D_appointment
	(startTime,
	 page_id,
	 titel,
	 endTime,
	 place,
	 notes)
 
VALUES 
	(@startTime,
	 @page_id,
	 @titel,
	 @endTime,
	 @place,
	 @notes)

SELECT appointment_id FROM D_appointment WHERE appointment_id = @@IDENTITY
DECLARE @players_id	int, @meta_id	int
SELECT @players_id = players_id, @meta_id = meta_id FROM D_page WHERE page_id = @page_id
EXEC D_loggAction @meta_id, 'New Appointment created ', NULL, @players_id
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS ON 
GO

/*
	Creates a new row in D_calender and inserts the three arguments.
	Gets all template types from D_template_types and uses them to get all the
	templates from D_templates.
	template_id from D_templates and the argument meta_id are inserted into
	D_templates_crossref.
	PARAMS:
		@meta_id	the documents meta_id. must be the same as in imCMS database, tabel meta. cannot be NULL
		@name		calenders name. cannot be NULL
		@titel		displayed titel
*/

CREATE PROCEDURE D_newCalender
	 @meta_id 	int,
	 @name 	varchar(50),
	 @titel		varchar(50) = NULL

AS 

DECLARE @type_id int, @template_id int

DECLARE type_cursor CURSOR FOR
SELECT type_id FROM D_template_types
OPEN type_cursor
FETCH NEXT FROM type_cursor INTO @type_id

WHILE @@FETCH_STATUS = 0
BEGIN
	--SELECT @template_id = MAX(template_id) FROM D_templates_crossref
	--SELECT @template_id
	--IF @template_id IS NULL BEGIN
		--SET @template_id = 0
	--END
	INSERT INTO D_templates_crossref (meta_id, type_id) VALUES (@meta_id, @type_id )
FETCH NEXT FROM type_cursor INTO @type_id
END

CLOSE type_cursor
DEALLOCATE type_cursor

INSERT INTO D_calender  (meta_id, name, titel)  VALUES (@meta_id, @name, @titel)

EXEC D_loggAction @meta_id, 'New Calender created '
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

/*
	Creates a new page.
	Creates a new row in D_page.
	Warning! Should not be called!
	D_newPlayer uses this stored procedure
	PARAMS:
		@meta_id	the calenders meta_id. cannot be NULL
		@players_id	the owners id. cannot be NULL
*/

CREATE PROCEDURE D_newPage
	 @meta_id 	int,
	 @players_id 	int

AS INSERT INTO D_page
	 (meta_id,
	 players_id) 
 
VALUES 
	(@meta_id,
	 @players_id)

EXEC D_loggAction @meta_id, 'New Page created', NULL, @players_id
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

/*
	Creates a new row in D_players.
	@user_id should come from the main imCMS databases.
	Creates a new page for the player using
	stored procedure D_newPage.
	PARAMS:
		@user_id	user_id from imCMS database, tabel user. cannot be NULL
		@meta_id	calenders meta_id. provided for the creation of a page. cannot be NULL
*/

CREATE PROCEDURE D_newPlayer
	@user_id 	int,
	@meta_id	int

AS INSERT INTO D_players
	 (user_id) 
VALUES 
	(@user_id)

DECLARE @temp int
SELECT @temp = @@IDENTITY
EXEC D_loggAction @meta_id, 'New Player created', NULL, @temp
EXEC D_newPage @meta_id, @temp
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

CREATE PROCEDURE D_removeAppointment
	(@app_id 	int)

AS

DECLARE @meta_id int, @page_id int, @players_id int

SELECT @page_id = page_id FROM D_appointment WHERE appointment_id = @app_id
SELECT @meta_id = meta_id, @players_id = players_id FROM D_page WHERE page_id = @page_id

DELETE D_appointment 
WHERE 
	( appointment_id	 = @app_id)

EXEC D_loggAction @meta_id, 'Appointment removed', NULL, @players_id
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE D_removeCalender
	@meta_id	int
AS

DECLARE @page_id int, @players_id int

WHILE ( SELECT TOP 1 page_id FROM D_page WHERE meta_id = @meta_id ) <> NULL
BEGIN
	SELECT @page_id = page_id, @players_id = players_id FROM D_page WHERE meta_id = @meta_id
	EXEC D_removePlayer @players_id, @meta_id
END

DELETE FROM D_calender WHERE meta_id = @meta_id
DELETE FROM D_templates_crossref WHERE meta_id = @meta_id

EXEC D_loggAction @meta_id, 'Calender removed'
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS OFF 
GO

/*
	Removes a player from a certain calender.
*/

CREATE PROCEDURE D_removePlayer
	@players_id 	int,
	@meta_id	int

AS

DECLARE @page_id int, @app_id int

SELECT @page_id = page_id FROM D_page WHERE players_id = @players_id AND meta_id = @meta_id

WHILE ( SELECT TOP 1 appointment_id FROM D_appointment WHERE page_id = @page_id ) <> NULL
BEGIN
	SELECT DISTINCT @app_id = appointment_id FROM D_appointment WHERE page_id = @page_id
	EXEC D_removeAppointment @app_id
END

DELETE FROM D_page WHERE players_id = @players_id AND meta_id = @meta_id

DELETE FROM D_players WHERE players_id = @players_id

EXEC D_loggAction @meta_id, 'Player removed', NULL, @players_id
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE D_setTitle
	@meta_id	int,
	@title		varchar(50)

AS

UPDATE D_calender
SET titel = @title
WHERE meta_id = @meta_id
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

