if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_AddNewBill]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_AddNewBill]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_AddNewBillBoard]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_AddNewBillBoard]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_AddNewSection]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_AddNewSection]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_AddNewTemplateLib]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_AddNewTemplateLib]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_AddReply]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_AddReply]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_AddTemplateLib]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_AddTemplateLib]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_AdminStatistics1]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_AdminStatistics1]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_ChangeSection]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_ChangeSection]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_DeleteBill]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_DeleteBill]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_DeleteSection]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_DeleteSection]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_FindMetaId]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_FindMetaId]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_FindSectionName]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_FindSectionName]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_FindTemplateLib]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_FindTemplateLib]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_GetAdminBill]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_GetAdminBill]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_GetAllBillsInSection]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_GetAllBillsInSection]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_GetAllBillsToShow]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_GetAllBillsToShow]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_GetAllNbrOfDaysToShow]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_GetAllNbrOfDaysToShow]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_GetAllNbrOfDiscsToShow]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_GetAllNbrOfDiscsToShow]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_GetAllOldBills]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_GetAllOldBills]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_GetAllSection]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_GetAllSection]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_GetAllTemplateLibs]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_GetAllTemplateLibs]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_GetBillHeader]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_GetBillHeader]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_GetCurrentBill]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_GetCurrentBill]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_GetEmail]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_GetEmail]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_GetFirstSection]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_GetFirstSection]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_GetLastDiscussionId]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_GetLastDiscussionId]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_GetNbrOfDiscs]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_GetNbrOfDiscs]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_GetNbrOfDiscsToShow]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_GetNbrOfDiscsToShow]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_GetSectionName]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_GetSectionName]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_GetSubjectStr]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_GetSubjectStr]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_GetTemplateIdFromName]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_GetTemplateIdFromName]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_GetTemplateLib]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_GetTemplateLib]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_LinkSectionToBillBoard]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_LinkSectionToBillBoard]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_RenameSection]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_RenameSection]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_SearchText]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_SearchText]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_SetNbrOfDaysToShow]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_SetNbrOfDaysToShow]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_SetNbrOfDiscsToShow]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_SetNbrOfDiscsToShow]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_SetTemplateLib]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_SetTemplateLib]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_UpdateBill]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[B_UpdateBill]
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_AddNewBill
/*
Lets add a new bill to  a section
*/
 @section_id int,
 @user_id int,
 @headline varchar(255),
 @text text ,
 @epost varchar(255),
 @ipadr varchar(255)
AS
INSERT INTO B_bill (section_id, create_date, ip_adress, headline, text, email, user_id )
VALUES ( @section_id, GETDATE(), @ipadr, @headline, @text, @epost,@user_id )
/*
--  Lets create the first reply 
DECLARE @firstReplyId int
Exec @firstReplyId = insertFirstReply @user_id,  @headline, @text, @replyLevel , @firstReplyId
 
-- Lets insert into discussion *
INSERT INTO discussion (forum_id, reply_id, create_date, last_mod_date, count_replies )
VALUES  (@forum_id, @firstReplyId , GETDATE() , GETDATE() , 1)
DECLARE @thisDiscussionId int
SELECT @thisDiscussionId = @@identity 
-- Lets update the first reply, its parent_id shall point us out 
UPDATE replies
SET replies.parent_id = @thisDiscussionId
WHERE replies.reply_id = @firstReplyId
*/

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_AddNewBillBoard
/* Lets add a new BillBoard.*/
 @meta_id int,
 @billBoardName varchar(255),
 @subject varchar(50)

AS
-- Lets check if theres already such an meta_id in the billboard
DECLARE @returnVal int
SELECT @returnVal = meta_id
FROM B_billboard
WHERE meta_id = @meta_id
IF NOT @returnVal IS NULL BEGIN
	RETURN
END
-- Ok, there was no such meta_id in the database, Lets insert it	
INSERT INTO B_billboard (meta_id, name,subject)
VALUES (@meta_id, @billBoardName, @subject)
-- Lets create the templates library path as well
EXEC B_AddNewTemplateLib @meta_id
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_AddNewSection
/*
Adds a new section to a billboard
*/
 @meta_id int,   
 @section_name varchar(255),
 @archive_mode char,
 @archive_time int,
 @days_to_show int
AS
/* Lets insert into B_section */
INSERT INTO B_section (archive_time, section_name, archive_mode, days_to_show )
VALUES  (@archive_time, @section_name, @archive_mode, @days_to_show )
/* Lets get the section_id from the inserted section */
DECLARE @newSectionId int
SELECT @newSectionId  = @@identity
EXEC B_LinkSectionToBillBoard @meta_id, @newSectionId

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_AddNewTemplateLib 
/*
 Lets add the templatelibrary where all the templates is situated on hd. This 
function is used when the administrator adds a new billboard. Used from sp
B_AddNewBillBoard
*/
	@meta_id int 
AS
DECLARE @thisTemplateId int
DECLARE @template_lib varchar(50)
SET @template_lib = 'Original'
INSERT INTO B_templates ( template_lib )
VALUES  ( @template_lib )
/* Lets get the template_id id for the just created template */ 
SELECT  @thisTemplateId = @@identity
INSERT INTO B_billboard_templates ( billboard_id , template_id )
VALUES (@meta_id, @thisTemplateId )

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_AddReply
/*
Lets add a new replie to  a bill
note that the user_id is the one in the imCMS user session object
if it's a annonymous user the id vill be 68 or what the number for User will be
*/
 @bill_id int,
 @user_id int,
 @headline varchar(255),
 @text text ,
 @epost varchar(255),
 @ipadr varchar(255)
AS
INSERT INTO B_replies (ip_adress,create_date, headline, text, parent_id, user_id, email )
VALUES (@ipadr, getdate(), @headline, @text, @bill_id, @user_id, @epost	)

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_AddTemplateLib
/*
This function is used when an admin creates a new templateset.
*/
	@newTemplateLib varchar(50)
AS
INSERT INTO B_templates (template_lib)
VALUES  ( @newTemplateLib)

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_AdminStatistics1
	@meta_id int,
	--@section_id int,
	@fromDate varchar(10),
	@toDate varchar(20),
	@listMode int
AS
-- Get all sections in a billboard which has discussions which create date is in between two dates
--DECLARE @fromDate datetime , @toDate datetime
--SELECT @fromDate = '2000-10-31 09:17:30'
--SELECT @toDate = '2000-10-31 11:17:30'
-- Lets verify date
IF ( @fromDate = '0' ) 
	SET @fromDate = '1990-01-01'
IF ( @toDate = '0' ) BEGIN
	SET @toDate = '2070-01-01'
END ELSE BEGIN
-- Listdate fix 
	SET @toDate = @toDate + ' 23:59:59'
	PRINT @toDate
END
-- LISTMODE = 1 , LOOK FOR THE DISCUSSIONS CREATE DATE
IF( @listMode = 1 ) BEGIN
	SELECT DISTINCT  B_section.section_id,  B_section.section_name
	FROM B_billboard_section, B_section, B_bill bill
	WHERE B_billboard_section.billboard_id = @meta_id 
	AND B_billboard_section.section_id  = B_section.section_id 
	-- AND B_section.section_id = @section_id
	AND B_section.section_id = bill.section_id
	AND bill.create_date >@fromDate AND bill.create_date < @toDate	
	GROUP BY B_section.section_id, B_section.section_name
END

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_ChangeSection
/*
 Changes the section for a bill. Used in adminmode
*/
	@bill_id int,
	@section_id int
AS
UPDATE B_bill
SET B_bill.section_id = @section_id
WHERE B_bill.bill_id = @bill_id

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_DeleteBill
	@aBillId int
/*
Lets delete from discussion. Observe that as conforum is the link between
conference and forum, that link must be removed before the forum can be deleted
This function is used when an admin wants to delete a discussion
*/
AS
-- Lets delete all replies for the bill
DELETE FROM B_replies
WHERE B_replies.parent_id = @aBillId
-- Lets delete the bill
DELETE FROM B_bill
WHERE B_bill.bill_id = @aBillId

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_DeleteSection
/*
Lets delete a section from the billboard
*/
 @aSectionId int
AS
 DELETE 
 FROM B_billboard_section
 WHERE section_id = @aSectionId
/*  Lets delete from section*/
 DELETE
 FROM B_section
 WHERE section_id = @aSectionId

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_FindMetaId
/*
This function is used by servlet ConfAdd to check if the meta_id argument
already exists in the database. Thas because a db can be used from
different servers, and a meta_id can be used twice to be added in the 
database
*/
	@newMetaId int
 AS
DECLARE @returnVal int
SELECT @returnVal = meta_id
FROM B_billboard
WHERE meta_id = @newMetaId
SELECT @returnVal = ISNULL(@returnVal, 1) 
SELECT @returnVal AS 'FoundMetaId'

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_FindSectionName
/*
Checks if a section  with such a name exists, if it exists, the
id for that section is returned. If not found -1 is returned. Used when an admin
shall add a new section Checks if there already exists such a section 
*/
	@meta_id int,
	@newSectionName varchar(50)
AS
DECLARE @returnVal int
SELECT @returnVal = s.section_id
FROM B_section s , B_billboard_section bs
WHERE s.section_name = @newSectionName
AND s.section_id = bs.section_id
AND bs.billboard_id = @meta_id
SELECT @returnVal =  ISNULL(@returnVal, -1) 
SELECT @returnVal AS 'section'

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_FindTemplateLib
/*
Checks if a template set with such a name exists, if it exists, the
id for that template is returned. If not found -1 is returned. Used when an admin
shall add a new templatelib. Checks if there already exists such a templateset 
*/
	@newLibName varchar(50)
AS
DECLARE @returnVal int
SELECT @returnVal = template_id
FROM B_templates
WHERE template_lib = @newLibName
SELECT @returnVal =  ISNULL(@returnVal, -1) 
SELECT @returnVal AS 'TemplateLib'

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_GetAdminBill
/*
Get the bill with the suplied bill_id
*/
 
 @bill_id int
AS
SELECT B_bill.bill_id, B_bill.headline, text, (select count(reply_id)   from B_replies where parent_id=B_bill.bill_id) AS "repNr" , B_bill.email, (select convert (char(10),B_bill.create_date,20)),B_bill.ip_adress
FROM B_bill
WHERE B_bill.bill_id=@bill_id

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_GetAllBillsInSection
/*
Returns all discussions for a certain forum.
Used when an admin wants to get a list with all discussions in a forum. Used when
the admin wants to delete a forum but first needs to delete all discussions.
*/
	@aSectionId int
AS
SELECT b.bill_id
FROM B_bill b
WHERE b.section_id = @aSectionId

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_GetAllBillsToShow
/*
Get all bills that has a valid show date
*/
 @meta_id int,
 @section_id int
AS
SELECT B_bill.bill_id, B_bill.headline, (select count(reply_id)   from B_replies where parent_id=B_bill.bill_id) AS "repNr" , (select convert (char(10),B_bill.create_date,20))
FROM B_bill, B_section, B_billboard_section
WHERE B_billboard_section.billboard_id = @meta_id
AND B_billboard_section.section_id = B_section.section_id
AND B_section.section_id = B_bill.section_id
AND B_section.section_id=@section_id
AND DATEDIFF(dy,B_bill.create_date, GETDATE()) < B_section.days_to_show
order by B_bill.bill_id desc

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_GetAllNbrOfDaysToShow
/*
Returns the number of days to show for a certain section. Used in admin mode
to return the list with the nbr of days to show in a section inside parentheses.
*/
	@meta_id int
AS
SELECT s.section_id , s.section_name + ' ('+ RTRIM(CONVERT(char(10), s.days_to_show) ) +')' 
FROM B_section s,  B_billboard_section bs
WHERE s.section_id = bs.section_id
AND bs.billboard_id = @meta_id

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_GetAllNbrOfDiscsToShow
/*
Returns the number of discussions to show for a certain section. Used in admin mode
to return the list with the nbr of discs to show in a section inside parentheses.
*/
	@meta_id int
AS
SELECT s.section_id , s.section_name + ' ('+ RTRIM(CONVERT(char(10), s.discs_to_show) ) +')' 
FROM B_section s,  B_billboard_section bs
WHERE s.section_id = bs.section_id
AND bs.billboard_id = @meta_id

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_GetAllOldBills
/*
Get all old bills 
*/
 @meta_id int,
 @section_id int
AS
SELECT B_bill.bill_id, B_bill.headline, (select count(reply_id)   from B_replies where parent_id=B_bill.bill_id) AS "repNr" , (select convert (char(10),B_bill.create_date,20))
FROM B_bill, B_section, B_billboard_section
WHERE B_billboard_section.billboard_id = @meta_id
AND B_billboard_section.section_id = B_section.section_id
AND B_section.section_id = B_bill.section_id
AND B_section.section_id=@section_id
AND B_bill.bill_id not in(SELECT B_bill.bill_id 
			FROM B_bill, B_section, B_billboard_section
			WHERE B_billboard_section.billboard_id = @meta_id
			AND B_billboard_section.section_id = B_section.section_id
			AND B_section.section_id = B_bill.section_id
			AND B_section.section_id=@section_id
			AND DATEDIFF(dy,B_bill.create_date, GETDATE()) < B_section.days_to_show)
order by B_bill.bill_id desc

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_GetAllSection 
/*
Returns all sections for a certain meta id. Used almost everywhere
*/
 @meta_id int
AS
/* Lets get all sections for a certain billBoard meta_id*/
SELECT B_section.section_id, B_section.section_name 
FROM B_section, B_billboard_section
WHERE B_billboard_section.billboard_id = @meta_id 
AND B_billboard_section.section_id  = B_section.section_id 
ORDER BY B_section.section_name

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_GetAllTemplateLibs  AS
/*
Returns all the templatelibs which exists for all conferences. Used in adminmode
to create the list where the admin can choose among current templatelibs
*/
-- Get all templatelibs for all confernences
SELECT DISTINCT t.template_lib , t.template_lib
FROM B_templates t

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_GetBillHeader
/*
Get the billheader with the suplied bill_id
*/
 
 @bill_id int
AS
SELECT B_bill.headline
FROM B_bill
WHERE B_bill.bill_id=@bill_id

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_GetCurrentBill
/*
Get the bill with the suplied bill_id
*/
 
 @bill_id int
AS
SELECT B_bill.bill_id, B_bill.headline, text, (select count(reply_id)   from B_replies where parent_id=B_bill.bill_id) AS "repNr" , (select convert (char(10),B_bill.create_date,20))
FROM B_bill
WHERE B_bill.bill_id=@bill_id

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_GetEmail
/*
Get the email adress to the suplied bill_id
*/
 
 @bill_id int
AS
SELECT B_bill.email
FROM B_bill
WHERE B_bill.bill_id=@bill_id

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_GetFirstSection
/*
 This method is used to get the first section id for the meta_id. It will be used
later to show all messages for this section by default
*/
	@meta_id int
AS
/* Lets get the section with the lowest id for this meta_id*/
DECLARE @returnVal int
SELECT TOP 1 s.section_id AS 'FirstSectionId'
FROM B_section s, B_billboard_section bs
WHERE bs.billboard_id = @meta_id  
AND bs.section_id  = s.section_id 
ORDER BY s.section_name
IF ( @@ROWCOUNT =  0 ) BEGIN
	SELECT -1 AS 'FirstSectionId'
END

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_GetLastDiscussionId
/*
This function is used the first time the user starts his session to the billboard.
It will fetch the latest discussionid and which will be put in the users session
object, so the latest replies can be retrieved later. if no discussion id is found, -1 will be returned
*/
	@meta_id int,
	@section_id int
AS
DECLARE @returnVal int
SELECT @returnVal=  MAX( B_bill.bill_id)
FROM B_billboard_section, B_section, B_bill
WHERE B_billboard_section.billboard_id =  @meta_id 
AND B_billboard_section.section_id  = B_section.section_id 
AND B_section.section_id = @section_id
AND B_section.section_id = B_bill.section_id
SELECT @returnVal =  ISNULL(@returnVal, -1) 
SELECT @returnVal AS 'LastBillId'

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_GetNbrOfDiscs 
/*
Returns the number of discussions in a forum. Used to determind how many
discussions to show
*/
	@section_id int
AS
SELECT COUNT (DISTINCT bill_id)
FROM B_bill
WHERE B_bill.section_id = @section_id

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_GetNbrOfDiscsToShow
/*
Returns the number of discussions to show for a certain section. By default 20 will be 
returned.
*/
	@section_id int
AS
DECLARE @returnVal int
SELECT @returnVal = s.discs_to_show
FROM B_section s
WHERE s.section_id = @section_id
SELECT @returnVal =  ISNULL(@returnVal, 20) 
SELECT @returnVal AS 'FirstSectionId'

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_GetSectionName
/*
Returns the name the section. Used in admin mode to display the currently
selected section
*/
	@section_id int
AS
DECLARE @returnVal varchar(255)
SELECT  @returnVal = section_name 
FROM B_section s
WHERE s.section_id = @section_id
SELECT @returnVal =  ISNULL(@returnVal, 'Not found') 
SELECT @returnVal AS 'Section_name'

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_GetSubjectStr
/*
Get the subject string to the mail
*/
 @bill_id int,
 @meta_id int,
 @section_id int
AS
SELECT distinct B_billboard.subject+" "+B_section.section_name+' [ '+B_bill.headline+' ]'
FROM B_bill, B_section, B_billboard
WHERE B_bill.bill_id=@bill_id
AND B_section.section_id=@section_id
AND B_billboard.meta_id=@meta_id

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_GetTemplateIdFromName
/*
 Lets get the folder library id for the passed name, if nothing is found, -1 will be returned.
This function is used when the admin wants change the templateset for a conference.
*/
	@aTemplateLibName varchar(50)
AS
DECLARE @returnVal int
SELECT @returnVal = t.template_id
FROM B_templates t
WHERE t.template_lib = @aTemplateLibName
-- Lets validate for null
SELECT @returnVal = ISNULL(  @returnVal , -1 )
SELECT @returnVal AS 'TemplateId'

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_GetTemplateLib
/*
 Lets get the folder library where all the templates are situated. if nothing is found for that
meta id , 'Original' is returned instead
*/
	@meta_id int
AS
DECLARE @returnVal varchar(50)
SELECT @returnVal = t.template_lib
FROM B_billboard b , B_billboard_templates bt , B_templates t
WHERE t.template_id = bt.template_id
AND bt.billboard_id = b.meta_id
AND b.meta_id = @meta_id
SELECT @returnVal =  ISNULL(@returnVal, 'Original') 
SELECT @returnVal AS 'TemplateLib'

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_LinkSectionToBillBoard
/* Links a section to a billboard. Used when a section is created
*/
 @billboard_id int,
 @section_id int
AS
INSERT INTO B_billboard_section (billboard_id, section_id)
VALUES  (@billboard_id, @section_id)

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_RenameSection
/*
 Changes the name on a section. Used through the admin interface
*/
	@section_id int,
	@section_name varchar(255)
AS
UPDATE B_section
SET B_section.section_name = @section_name
WHERE B_section.section_id = @section_id

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_SearchText
/* This procedure is the one which is used to search among the 
headlines, text, and user
The argument category will come as an integer. 0 = header, 1 = text
*/
	@meta_id int,
	@section_id int,
	@category int,
	@sWord char(100),
	@fromDate datetime,
	@toDate datetime
AS
-- Lets build a '%Computer%' string
SET @sWord =  '%' + RTRIM(@sWord) + '%' 
--prINT 'Sökord: ' + @sWord
IF( @category = 0)
BEGIN
	/* Lets search  among The replies HEADERS*/ 
	SELECT DISTINCT bill.bill_id, bill.headline,(select count(reply_id)   from B_replies where parent_id=bill.bill_id) AS "repNr" , SUBSTRING( CONVERT(char(10), bill.create_date,20), 6, 16) AS 'create_date' 
	FROM B_bill bill,  B_billboard_section bs, B_section
	WHERE bill.headline LIKE RTRIM(@sWord) 
	AND bill.section_id = B_section.section_id
	AND B_section.section_id = @section_id
	AND B_section.section_id = bs.section_id
	AND bs.billboard_id = @meta_id 	
	-- Lets check for the date
	AND bill.create_date >@fromDate AND bill.create_date < @toDate	
END
-- LETS SEARCH AMONG TEXT
ELSE IF( @category = 1 )
BEGIN
	/* Lets search  among The replies TEXT */ 
	SELECT DISTINCT bill.bill_id, bill.headline,(select count(reply_id)   from B_replies where parent_id=bill.bill_id) AS "repNr" , SUBSTRING( CONVERT(char(16), bill.create_date,20), 6, 16) AS 'create_date' 
	FROM B_bill bill,  B_billboard_section bs, B_section
	WHERE bill.text LIKE RTRIM(@sWord) 
	AND bill.section_id = B_section.section_id
	AND B_section.section_id = @section_id
	AND B_section.section_id = bs.section_id
	AND bs.billboard_id = @meta_id 	
-- Lets check for the date
	AND bill.create_date >@fromDate AND bill.create_date < @toDate	
END

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_SetNbrOfDaysToShow
/*
Sets the number of days to show a bill for a certain forum.
*/
	@section_id int,
	@newNbrToShow int
AS
UPDATE B_section 
SET days_to_show = @newNbrToShow
WHERE section_id = @section_id

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_SetNbrOfDiscsToShow
/*
Sets the number of discussions to show for a certain forum.
*/
	@section_id int,
	@newNbrToShow int
AS
UPDATE B_section 
SET discs_to_show = @newNbrToShow
WHERE section_id = @section_id

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_SetTemplateLib
/*
 Used when an admin wants to change the conferences template set to another one
Lets set a template lib name for a meta id
*/
	@meta_id int , 
	 @newLibName varchar(50) 
AS
UPDATE B_templates
SET template_lib = @newLibName
FROM B_billboard b , B_billboard_templates bt , B_templates t
WHERE t.template_id = bt.template_id
AND bt.billboard_id = b.meta_id
AND b.meta_id = @meta_id

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

CREATE PROCEDURE B_UpdateBill
/*
This function is used to update a bill header and text and email. used by  an administrator
*/
	@bill_id int,
	@header varchar(255),
	@text text,
	@email varchar(155)
AS
UPDATE B_bill
	SET headline = @header , text = @text, email=@email
WHERE B_bill.bill_id = @bill_id

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

