if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_AddNewConf]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_AddNewConf]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_AddNewDisc]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_AddNewDisc]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_AddNewForum]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_AddNewForum]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_AddNewTemplateLib]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_AddNewTemplateLib]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_AddReply]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_AddReply]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_AddTemplateLib]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_AddTemplateLib]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_AdminStatistics1]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_AdminStatistics1]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_AdminStatistics2]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_AdminStatistics2]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_CheckUserId]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_CheckUserId]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_ConfUsersAdd]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_ConfUsersAdd]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_ConfUsersGetReplyDate]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_ConfUsersGetReplyDate]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_ConfUsersGetReplyOrder]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_ConfUsersGetReplyOrder]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_ConfUsersGetReplyOrderSel]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_ConfUsersGetReplyOrderSel]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_ConfUsersGetUserLevel]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_ConfUsersGetUserLevel]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_ConfUsersSetReplyOrder]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_ConfUsersSetReplyOrder]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_ConfUsersSetUserLevel]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_ConfUsersSetUserLevel]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_ConfUsersUpdate]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_ConfUsersUpdate]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_ConfUsersUpdateLoginDate]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_ConfUsersUpdateLoginDate]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_ConvertReplyDate]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_ConvertReplyDate]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_DelForum]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_DelForum]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_DeleteDiscussion]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_DeleteDiscussion]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_DeleteForum]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_DeleteForum]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_DeleteReply]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_DeleteReply]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_FindForumName]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_FindForumName]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_FindMetaId]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_FindMetaId]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_FindTemplateLib]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_FindTemplateLib]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_GetAllConfUsersInList]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_GetAllConfUsersInList]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_GetAllDiscsInForum]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_GetAllDiscsInForum]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_GetAllDiscussions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_GetAllDiscussions]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_GetAllForum]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_GetAllForum]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_GetAllNbrOfDiscsToShow]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_GetAllNbrOfDiscsToShow]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_GetAllNewDiscussions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_GetAllNewDiscussions]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_GetAllOldDiscussions]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_GetAllOldDiscussions]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_GetAllRepliesInDisc]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_GetAllRepliesInDisc]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_GetAllRepliesInDiscAdmin]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_GetAllRepliesInDiscAdmin]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_GetAllTemplateLibs]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_GetAllTemplateLibs]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_GetAllUsersInList]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_GetAllUsersInList]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_GetConfLoginNames]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_GetConfLoginNames]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_GetDiscussionDate]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_GetDiscussionDate]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_GetDiscussionHeader]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_GetDiscussionHeader]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_GetFirstForum]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_GetFirstForum]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_GetForumName]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_GetForumName]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_GetLastDiscussionId]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_GetLastDiscussionId]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_GetLastLoginDate]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_GetLastLoginDate]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_GetLastLoginDate2]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_GetLastLoginDate2]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_GetNbrOfDiscs]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_GetNbrOfDiscs]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_GetNbrOfDiscsToShow]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_GetNbrOfDiscsToShow]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_GetTemplateIdFromName]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_GetTemplateIdFromName]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_GetTemplateLib]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_GetTemplateLib]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_GetTemplateLibName]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_GetTemplateLibName]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_GetTime]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_GetTime]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_GetUserNames]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_GetUserNames]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_InsertFirstReply]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_InsertFirstReply]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_LinkForumToConf]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_LinkForumToConf]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_MemberInConf]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_MemberInConf]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_RenameForum]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_RenameForum]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_SearchText]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_SearchText]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_SelfRegRoles_AddNew]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_SelfRegRoles_AddNew]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_SelfRegRoles_Delete]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_SelfRegRoles_Delete]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_SelfRegRoles_GetAll]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_SelfRegRoles_GetAll]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_SelfRegRoles_GetAll2]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_SelfRegRoles_GetAll2]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_SetNbrOfDiscsToShow]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_SetNbrOfDiscsToShow]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_SetTemplateLib]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_SetTemplateLib]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_TestConfDb]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_TestConfDb]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_UpdateDiscussionModifyDate]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_UpdateDiscussionModifyDate]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_UpdateReply]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[A_UpdateReply]
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;




CREATE PROCEDURE A_AddNewConf

/* Lets add a new Conference. Observe that were not using the sorttype for the moment,

so were just gonna add a default value*/

 @meta_id int,

 @confName varchar(255)

AS

-- Lets check if theres already such an meta_id in the conference

DECLARE @returnVal int

SELECT @returnVal = meta_id

FROM A_conference

WHERE meta_id = @meta_id

IF NOT @returnVal IS NULL BEGIN

	RETURN

END

-- Ok, there was no such meta_id in the database, Lets insert it

INSERT INTO A_conference (meta_id, name)

VALUES (@meta_id, @confName)

-- Lets create the templates library path as well

EXEC A_AddNewTemplateLib @meta_id



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER ON
;
SET ANSI_NULLS ON
;





CREATE PROCEDURE A_AddNewDisc

/*

Lets add a new discussion to  a forum

*/

 @forum_id int,

 @user_id int,

 @headline varchar(255),

 @text text ,

 @replyLevel int

AS

--  Lets create the first reply

DECLARE @firstReplyId int

Exec @firstReplyId = A_insertFirstReply @user_id,  @headline, @text, @replyLevel , @firstReplyId



-- Lets insert into discussion *

INSERT INTO A_discussion (forum_id, reply_id, create_date, last_mod_date, count_replies )

VALUES  (@forum_id, @firstReplyId , GETDATE() , GETDATE() , 1)

DECLARE @thisDiscussionId int

SELECT @thisDiscussionId = @@identity

-- Lets update the first reply, its parent_id shall point us out

UPDATE A_replies

SET A_replies.parent_id = @thisDiscussionId

WHERE A_replies.reply_id = @firstReplyId



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_AddNewForum

/*

Adds a new forum to a conferences

*/

 @meta_id int,

 @forum_name varchar(255),

 @archive_mode char,

 @archive_time int

AS

/* Lets insert into forum */

INSERT INTO A_forum (archive_time, forum_name, archive_mode )

VALUES  (@archive_time, @forum_name, @archive_mode )



/* Lets get the forum_id from the inserted forum */

DECLARE @newForumId int

SELECT @newForumId  = @@identity



EXEC A_linkForumToConf @meta_id, @newForumId



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;





CREATE PROCEDURE A_AddNewTemplateLib

/*

 Lets add the templatelibrary where all the templates is situated on hd. This

function is used when the administrator adds a new conference. Used from sp

AddNewConf

*/

	@meta_id int

AS

DECLARE @thisTemplateId int

DECLARE @template_lib varchar(50)

SET @template_lib = 'original'

INSERT INTO A_templates ( template_lib )

VALUES  ( @template_lib )

/* Lets get the template_id id for the just created template */

SELECT  @thisTemplateId = @@identity

INSERT INTO A_conf_templates ( conf_id , template_id )

VALUES (@meta_id, @thisTemplateId )



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;







CREATE PROCEDURE A_AddReply

/*
This procedure is the one which actually adds replies to a discussion.
*/
	@user_id int,
	@theDiscussionId int,
	@headline varchar(255),
	@text text ,
	@reply_level int
AS

/* Lets get today's date */

DECLARE @toDay datetime
SELECT @toDay = GETDATE()
INSERT INTO A_replies (user_id, parent_id, create_date, headline, text, reply_level)
VALUES ( @user_id, @theDiscussionId, @toDay, @headline, @text, @reply_level )

/* Lets increment the discussions counter */
Declare @nbrOfRepliesInDisc int
SELECT @nbrOfRepliesInDisc  = max(count_replies) + 1 FROM A_discussion
WHERE A_discussion.discussion_id = @theDiscussionId

UPDATE A_discussion
SET count_replies =  @nbrOfRepliesInDisc
WHERE A_discussion.discussion_id = @theDiscussionId

/*Lets update the discussions lastModified date*/

EXEC A_UpdateDiscussionModifyDate @theDiscussionId


;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_AddTemplateLib

/*

This function is used when an admin creates a new templateset.

*/

	@newTemplateLib varchar(50)

AS

INSERT INTO A_TEMPLATES (template_lib)

VALUES  ( @newTemplateLib)



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS OFF
;




CREATE PROCEDURE A_AdminStatistics1

	@meta_id int,

	--@forum_id int,

	@fromDate varchar(10),

	@toDate varchar(20),

	@listMode int

AS



-- Get all forums in a conference which has discussions which modified date is in between two dates

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

	SELECT DISTINCT  A_forum.forum_id,  A_forum.forum_name

	FROM A_conf_forum, A_forum, A_discussion disc, A_replies, A_conf_users usr

	WHERE A_conf_forum.conf_id = @meta_id

	AND A_conf_forum.forum_id  = A_forum.forum_id

	-- AND A_forum.forum_id = @forum_id

	AND A_forum.forum_id = disc.forum_id

	AND disc.reply_id = A_replies.reply_id

	AND disc.create_date >@fromDate AND disc.create_date < @toDate

	GROUP BY A_forum.forum_id, A_forum.forum_name

END

-- LISTMODE = 1 , LOOK FOR THE DISCUSSIONS LAST MODIFIED DATE

IF ( @listMode  = 2 ) BEGIN

	-- SELECT DISTINCT disc.discussion_id, SUBSTRING( CONVERT(char(20), disc.last_mod_date,20),1, 20) AS 'updated_date', replies.headline

	SELECT DISTINCT  A_forum.forum_id,  A_forum.forum_name

	FROM A_conf_forum, A_forum, A_discussion disc, A_replies, A_conf_users usr

	WHERE A_conf_forum.conf_id = @meta_id

	AND A_conf_forum.forum_id  = A_forum.forum_id

	--AND A_forum.forum_id = @forum_id

	AND A_forum.forum_id = disc.forum_id

	AND disc.reply_id = A_replies.reply_id

	AND disc.last_mod_date >@fromDate AND disc.last_mod_date < @toDate

	GROUP BY A_forum.forum_id, A_forum.forum_name

END



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS OFF
;




CREATE PROCEDURE A_AdminStatistics2

	@meta_id int,

	@forum_id int,

	@fromDate varchar(10),

	@toDate varchar(20),

	@listMode int

AS





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

-- Get all discussions in a conference which modified date is in between two dates

--DECLARE @fromDate datetime , @toDate datetime

--SELECT @fromDate = '2000-10-31 09:17:30'

--SELECT @toDate = '2000-10-31 11:17:30'



-- LISTMODE = 1 , LOOK FOR THE DISCUSSIONS CREATE DATE

IF( @listMode = 1 ) BEGIN

	SELECT DISTINCT disc.discussion_id,  A_replies.headline, SUBSTRING( CONVERT(char(20), disc.last_mod_date,20),1, 20) AS 'Created_date'

	FROM A_conf_forum, A_forum, A_discussion disc, A_replies, A_conf_users usr

	WHERE A_conf_forum.conf_id = @meta_id

	AND A_conf_forum.forum_id  = A_forum.forum_id

	AND A_forum.forum_id = @forum_id

	AND A_forum.forum_id = disc.forum_id

	AND disc.reply_id = A_replies.reply_id

	AND disc.create_date >@fromDate AND disc.create_date < @toDate

END

-- LISTMODE = 1 , LOOK FOR THE DISCUSSIONS LAST MODIFIED DATE

IF ( @listMode  = 2 ) BEGIN

	SELECT DISTINCT disc.discussion_id,  A_replies.headline, SUBSTRING( CONVERT(char(20), disc.last_mod_date,20),1, 20) AS 'Created_date'

	FROM A_conf_forum, A_forum, A_discussion disc, A_replies, A_conf_users usr

	WHERE A_conf_forum.conf_id = @meta_id

	AND A_conf_forum.forum_id  = A_forum.forum_id

	AND A_forum.forum_id = @forum_id

	AND A_forum.forum_id = disc.forum_id

	AND disc.reply_id = A_replies.reply_id

	AND disc.last_mod_date >@fromDate AND disc.last_mod_date < @toDate

END



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






/*

Lets check if the user_id exists in the user_names

*/



CREATE PROCEDURE A_CheckUserId

	@meta_id int,

	@wanted_user_id int

AS

SELECT  usr.user_id

FROM A_CONF_USERS usr, A_conference c

WHERE usr.user_id = @wanted_user_id

--AND usr.conf_id = c.meta_id

--AND c.meta_id = @meta_id



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;


/*

This procedure is used when we shall add a new user to the  conf_users table

*/

CREATE   PROCEDURE A_ConfUsersAdd

	@user_id int,

	@conf_id int,

	@aFirstName char(25),

	@aLastName char(30)

AS

declare @lastLoginDate datetime

select @lastLoginDate =
	(select min(create_date)-1
 	from A_discussion , A_conf_forum
	where conf_id = @conf_id)

IF @lastLoginDate IS NULL
begin
	select @lastLoginDate = getdate()
end

-- Lets check if the user is a member of any other conference or if this is the first one

IF (NOT EXISTS 	( SELECT user_id

			FROM A_conf_users

			WHERE user_id = @user_id )   )

BEGIN

-- Ok, Lets add him to the conference

	INSERT INTO A_CONF_USERS(  user_id, first_name, last_name )

	VALUES (@user_id, @aFirstName, @aLastName )

END


-- Lets link tthe user to the conference in CONF_USERS_CROSSREF

INSERT INTO A_CONF_USERS_CROSSREF ( conf_id,  user_id, last_login_date )

VALUES ( @conf_id, @user_id,  @lastLoginDate )




;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_ConfUsersGetReplyDate AS





SELECT * FROM A_conf_users



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_ConfUsersGetReplyOrder

/*

This procedure is used when we shall get a users personal replyorder. Used when

we shall show the replies for the user. This function is used when executed from another

stored procedure. Takes a return value.

*/

	@meta_id int,

	@user_id int,

	@retVal int = 0 OUTPUT

AS

SELECT @retVal = crossref.replies_order

FROM A_conf_users_crossref crossref

WHERE crossref.user_id = @user_id

AND crossref.conf_id = @meta_id

return @retVal



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_ConfUsersGetReplyOrderSel

/*

This procedure is used when we shall get a users personal replylevelsetting.So we know

how were gonna present the replies for him

*/

	@meta_id int,

	@user_id int

AS

DECLARE @retVal int

SELECT @retVal = crossref.replies_order

FROM A_conf_users_crossref crossref

WHERE crossref.user_id = @user_id

AND crossref.conf_id = @meta_id

SELECT @retVal =  ISNULL( @retVal , 0)

SELECT @retVal AS 'UserLevel'



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_ConfUsersGetUserLevel

/*

Returns the users user_level. 1 if he is an expert in this conference,

zero if hes an ordinary user.

Used when were adding a new reply to set the replies reply_level.

*/

	@meta_id int,

	@user_id int

AS

DECLARE @returnVal int

-- Lets get a users usertype, if hes an expert or not

SELECT @returnVal = userCross.user_level

FROM A_conf_users_crossref userCross

WHERE userCross.user_id = @user_id

AND userCross.conf_id = @meta_id

SELECT @returnVal =  ISNULL(@returnVal, -1)

SELECT @returnVal AS 'UserLevel'



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






/*

This procedure is used to update the users sortorder preference

*/

CREATE PROCEDURE A_ConfUsersSetReplyOrder

	@meta_id int,

	@user_id int,

	@newSortOrder int

AS

UPDATE A_CONF_USERS_CROSSREF

SET

	replies_order =  @newSortOrder

WHERE A_conf_users_crossref.user_id = @user_id

AND A_conf_users_crossref.conf_id = @meta_id



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_ConfUsersSetUserLevel

/*

Sets a users level. 1 if its an expert. 0 if its an normal user. Changes a users

replylevel to the appropriate level and updates all his current replies in the

conference to this new level

*/

	@meta_id int,

	@user_id int,

	@newLevel int

AS

-- Lets set a users usertype, if hes an expert or not

UPDATE A_conf_users_crossref

SET user_level = @newLevel

WHERE A_conf_users_crossref.user_id = @user_id

AND A_conf_users_crossref.conf_id = @meta_id

-- Ok, Lets update all his old replies, so that their level will be the right one

UPDATE A_replies

SET reply_level = @newLevel

FROM A_replies r, A_discussion d, A_forum f, A_conf_forum cf

WHERE r.user_id = @user_id

AND r.parent_id = d.discussion_id

AND d.forum_id = f.forum_id

AND f.forum_id = cf.forum_id

AND cf.conf_id  = @meta_id

-- conf_id = @meta_id



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_ConfUsersUpdate

/*

This procedure is used when we shall update a users name and last_logindate

to the  conf_users table. This function will run everytime a user comes in to the

conference

*/

	@meta_id int,

	@user_id int,

	@aFirstName char(25),

	@aLastName char(30)

AS

-- Ok, Lets update the name

UPDATE A_CONF_USERS

SET  first_name = @aFirstName,

         last_name = @aLastName

WHERE A_conf_users.user_id = @user_id

-- Lets update conf_users_crossref

UPDATE A_conf_users_crossref

	SET  A_conf_users_crossref.last_login_date = GETDATE()

WHERE A_conf_users_crossref.user_id = @user_id

AND A_conf_users_crossref.conf_id = @meta_id



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_ConfUsersUpdateLoginDate

/*

This procedure is used when we shall update a users last_logindate

to the  conf_users_crossref table. This function will run everytime a user starts a discussion /

or replies in a discussion

*/

 @meta_id int,

 @user_id int



AS

UPDATE A_CONF_USERS_CROSSREF

SET

 last_login_date = GETDATE()

FROM A_CONF_USERS, A_CONF_USERS_CROSSREF , A_CONFERENCE

WHERE A_conf_users_crossref.user_id = @user_id

AND A_conf_users_crossref.conf_id = A_conference.meta_id

AND A_conference.meta_id = @meta_id

--WHERE conf_users.user_id = @user_id

--AND conf_users.user_id = conf_users_crossref.user_id

--AND conf_users_crossref.conf_id = conference.meta_id

--AND conference.meta_id = @meta_id



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_ConvertReplyDate

/*

This func is used to convert a full date to the format wich will be shown

for when each reply were created

*/

	@aFullDate dateTime

AS

DECLARE @year varchar(4)

SELECT @year = DATEPART(yy, @aFullDate )

DECLARE @month varchar(2)

SELECT @month = DATEPART(mm, @aFullDate )

DECLARE @day varchar(2)

SELECT @day = DATEPART(dd, @aFullDate)

/* Lets get the TIME */

DECLARE @hh varchar(2)

SELECT @hh = DATEPART(hh, @aFullDate)

DECLARE @mm varchar(2)

SELECT @mm = DATEPART(mm, @aFullDate)

DECLARE @ss varchar(2)

SELECT @ss = DATEPART(ss, @aFullDate)

SELECT @year +'-'+ @month +'-'+ @day + ' ' +@hh + ':' + @mm + ':' + @ss

/* SELECT 'Datum: ', @year +'-'+ @month +'-'+ @day */



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_DelForum

 @aForumId int

AS

/*

Lets delete from conf_forum. Observe that as conforum is the link between

conference and forum, that link must be removed before the forum can be deleted

*/

 DELETE

 FROM A_conf_Forum

 WHERE forum_id = @aForumId

/*  Lets delete from forum*/

 DELETE

 FROM A_forum

 WHERE forum_id = @aForumId



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_DeleteDiscussion

	@aDiscId int

/*

Lets delete from discussion. Observe that as conforum is the link between

conference and forum, that link must be removed before the forum can be deleted

This function is used when an admin wants to delete a discussion

*/

AS

-- Lets delete the discussion

DELETE FROM A_DISCUSSION

WHERE A_discussion.discussion_id = @aDiscId

-- Lets delete all replies for the discussion

DELETE FROM A_REPLIES

WHERE A_replies.parent_id = @aDiscId



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_DeleteForum

/*

Lets delete a forum from the conference

*/

 @aForumId int

AS

 DELETE

 FROM A_conf_Forum

 WHERE forum_id = @aForumId

/*  Lets delete from forum*/

 DELETE

 FROM A_forum

 WHERE forum_id = @aForumId



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_DeleteReply

/*

 Deletes a reply  except if its the first reply in a discussion. Delete the discussion

instead if so. Admin function

*/

	@discId int,

	@replyId int

AS

-- Lets check how many replies in the discussion there are

DECLARE @nbrOfReplies int

SELECT @nbrOfReplies = COUNT(rep.reply_id) FROM A_replies rep WHERE rep.parent_id = @discId

-- Lets get the first reply_id in the discussion

DECLARE @firstReply int

SELECT @firstReply = 	r.reply_id

			FROM A_replies r

			WHERE r.parent_id = @discId

			ORDER BY r.create_date DESC

--PRINT 'ANTAL INLÄGG: ' + CONVERT(CHAR(3), @nbrOfReplies)

--PRINT 'Första inläggets id: ' + CONVERT(CHAR(3), @firstReply)

IF( @firstReply != @replyId AND @nbrOfReplies > 1 ) BEGIN

-- Ok, lets delete the reply

	DELETE FROM A_replies

	WHERE A_replies.reply_id = @replyId

	AND A_replies.parent_id = @discId



	-- Lets decrease the counter with one

	UPDATE A_discussion

	SET count_replies = count_replies -1

	WHERE A_discussion.discussion_id = @discId

	RETURN

END ELSE  IF( @firstReply = @replyId AND @nbrOfReplies > 1 ) BEGIN

	PRINT 'VI KAN INTE TA BORT FÖRSTA INLÄGGET'

END ELSE BEGIN

	PRINT 'VI KAN INTE TA BORT FÖRSTA OCH ENDA INLÄGGET!'

END



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_FindForumName

/*

Checks if a forum  with such a name exists, if it exists, the

id for that forum is returned. If not found -1 is returned. Used when an admin

shall add a new forum Checks if there already exists such a forum

*/

	@meta_id int,

	@newforumName varchar(50)

AS

DECLARE @returnVal int

SELECT @returnVal = f.forum_id

FROM A_forum f , A_conf_forum cf

WHERE f.forum_name = @newforumName

AND f.forum_id = cf.forum_id

AND cf.conf_id = @meta_id

SELECT @returnVal =  ISNULL(@returnVal, -1)

SELECT @returnVal AS 'forum'



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_FindMetaId

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

FROM A_conference

WHERE meta_id = @newMetaId

SELECT @returnVal = ISNULL(@returnVal, 1)

SELECT @returnVal AS 'FoundMetaId'



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_FindTemplateLib

/*

Checks if a template set with such a name exists, if it exists, the

id for that template is returned. If not found -1 is returned. Used when an admin

shall add a new templatelib. Checks if there already exists such a templateset

*/

	@newLibName varchar(50)

AS

DECLARE @returnVal int

SELECT @returnVal = template_id

FROM A_templates

WHERE template_lib = @newLibName

SELECT @returnVal =  ISNULL(@returnVal, -1)

SELECT @returnVal AS 'TemplateLib'



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_GetAllConfUsersInList

/*

Get All Conference users in a list for a certain meta id. Used by the ConfAdminUsers

function to get all the users in a conference

*/

	@meta_id int

AS

SELECT cu.user_id, cu.last_name + ', ' + cu.first_name

FROM A_conf_users cu , A_conf_users_crossref crossref

WHERE cu.user_id = crossref.user_id

AND crossref.conf_id = @meta_id

ORDER BY last_name



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE [A_GetAllDiscsInForum]

/*

Returns all discussions for a certain forum.

Used when an admin wants to get a list with all discussions in a forum. Used when

the admin wants to delete a forum but first needs to delete all discussions.

*/

	@aForumId int

AS

SELECT d.discussion_id

FROM A_discussion d

WHERE d.forum_id = @aForumId



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_GetAllDiscussions

/*

Get all discussions, sorted by the the newest first, and the older later. Used

when the user shall see all the discussions in a forum

*/

	@meta_id int,

	@forum_id int,

	@lastLoginDate datetime

AS

-- GET ALL NEW DISCUSSIONS

SELECT '1' as 'newFlag', disc.discussion_id, SUBSTRING( CONVERT(char(16), A_replies.create_date,120), 6, 16) AS 'create_date' , A_replies.headline, disc.count_replies, usr.first_name, usr.last_name, SUBSTRING( CONVERT(char(20), disc.last_mod_date,20),1, 20) AS 'updated_date'

FROM A_conf_forum, A_forum, A_discussion disc, A_replies, A_conf_users usr

WHERE A_conf_forum.conf_id =  @meta_id

AND A_conf_forum.forum_id  = A_forum.forum_id

AND A_forum.forum_id = @forum_id

AND A_forum.forum_id = disc.forum_id

AND disc.reply_id = A_replies.reply_id

AND usr.user_id = A_replies.user_id

AND ( DATEDIFF( ss , disc.last_mod_date , @lastLoginDate ) < 0 )

UNION
-- GET OLD DISCUSSIONS

SELECT '0' as 'newFlag', disc.discussion_id, SUBSTRING( CONVERT(char(16), A_replies.create_date,120), 6, 16) AS 'create_date' , A_replies.headline, disc.count_replies, usr.first_name, usr.last_name, SUBSTRING( CONVERT(char(20), disc.last_mod_date,20), 1, 20) AS 'updated_date'

FROM A_conf_forum, A_forum, A_discussion disc, A_replies, A_conf_users usr

WHERE A_conf_forum.conf_id =  @meta_id

AND A_conf_forum.forum_id  = A_forum.forum_id

AND A_forum.forum_id = @forum_id

AND A_forum.forum_id = disc.forum_id

AND disc.reply_id = A_replies.reply_id

AND usr.user_id = A_replies.user_id

AND ( DATEDIFF( ss , disc.last_mod_date , @lastLoginDate ) > 0 )

ORDER BY disc.discussion_id DESC



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_GetAllForum

/*

Returns all forums for a certain meta id. Used almost everywhere

*/

 @meta_id int

AS

/* Lets get all forums for a certain conference meta_id*/

SELECT A_forum.forum_id, A_forum.forum_name

FROM A_forum, A_conf_forum

WHERE A_conf_forum.conf_id = @meta_id

AND A_conf_forum.forum_id  = A_forum.forum_id



ORDER BY A_forum.forum_name



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_GetAllNbrOfDiscsToShow

/*

Returns the number of discussions to show for a certain forum. Used in admin mode

to return the list with the nbr of discs to show in a forum inside parentheses.

*/

	@meta_id int

AS

SELECT f.forum_id , f.forum_name + ' ('+ RTRIM(CONVERT(char(10), f.discs_to_show) ) +')'

FROM A_forum f,  A_conf_forum cf

WHERE f.forum_id = cf.forum_id

AND cf.conf_id = @meta_id



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_GetAllNewDiscussions

/*

Returns all new discussions. Used in adminmode for retrieving all the new

discussions since The admin was logged in the last time. Used in combination

with GetallOldDiscussions to retrieve all discussions in a forum

*/

	@meta_id int,

	@forum_id int,

	@lastLoginDate datetime

	--@nbrOfRowsToGet int

AS

-- Lets check how many rows we should get, if null, then set 20 as default value

--SELECT ISNULL( @nbrOfRowsToGet  , 20 )

SELECT disc.discussion_id, SUBSTRING( CONVERT(char(16), A_replies.create_date,20), 6, 16) AS 'create_date' , A_replies.headline, disc.count_replies, usr.first_name, usr.last_name

--SELECT disc.discussion_id,  replies.create_date , replies.headline, disc.count_replies, usr.first_name, usr.last_name

FROM A_conf_forum, A_forum, A_discussion disc, A_replies, A_conf_users usr

WHERE A_conf_forum.conf_id =  @meta_id

AND A_conf_forum.forum_id  = A_forum.forum_id

AND A_forum.forum_id = @forum_id

AND A_forum.forum_id = disc.forum_id

AND disc.reply_id = A_replies.reply_id

AND usr.user_id = A_replies.user_id

AND ( DATEDIFF( ss , disc.last_mod_date , @lastLoginDate ) < 0 )

ORDER BY disc.create_date DESC



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_GetAllOldDiscussions

/*

Returns all old discussions. Used in adminmode for retrieving all the old

discussions since The admin was logged in the last time. Used in combination

with GetallNewDiscussions to retrieve all discussions in a forum

*/

	@meta_id int,

	@forum_id int,

	@lastLoginDate datetime

AS

SELECT disc.discussion_id, SUBSTRING( CONVERT(char(16), A_replies.create_date,20), 6, 16) AS 'create_date', A_replies.headline, disc.count_replies, usr.first_name, usr.last_name

FROM A_conf_forum, A_forum, A_discussion disc, A_replies, A_conf_users usr

WHERE A_conf_forum.conf_id =  @meta_id

AND A_conf_forum.forum_id  = A_forum.forum_id

AND A_forum.forum_id = @forum_id

AND A_forum.forum_id = disc.forum_id

AND disc.reply_id = A_replies.reply_id

AND usr.user_id = A_replies.user_id

--AND ( DATEDIFF( ss , replies.create_date , @lastLoginDate ) > 0 )

AND ( DATEDIFF( ss , disc.last_mod_date , @lastLoginDate ) > 0 )

ORDER BY disc.create_date DESC



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_GetAllRepliesInDisc

/*

  This proc is used when you want to see all replies in a discussion. It asks

  the user what preferences he has for this conference and  returns the replies

  in the sortorder he wants it in

*/

 @discussion_id int,

 @user_id int

AS

/* Lets get the meta_id for this discussion id */

DECLARE @meta_id int

SELECT @meta_id = cf.conf_id

FROM A_CONF_FORUM cf, A_DISCUSSION d, A_FORUM f

WHERE d.discussion_id = @discussion_id

AND d.forum_id = f.forum_id

AND f.forum_id = cf.forum_id

/* Lets get the users sortorder preference*/

DECLARE @userOrder int

-- EXEC @userOrder = ConfUsersGetReplyOrder @meta_id, @user_id ,0

EXEC @userOrder = A_ConfUsersGetReplyOrder @meta_id, @user_id

IF( @userOrder = 1 )

BEGIN

	SELECT CONVERT(char(16), rep.create_date,20), usr.first_name, usr.last_name, rep.headline, rep.text, rep.reply_level

	--SELECT  rep.create_date, usr.first_name, usr.last_name, rep.headline, rep.text, rep.reply_level

	FROM A_REPLIES rep, A_DISCUSSION disc, A_CONF_USERS usr

	WHERE disc.discussion_id = @discussion_id

 	AND rep.parent_id = disc.discussion_id

 	AND usr.user_id = rep.user_id

	ORDER BY rep.create_date ASC

END

ELSE BEGIN

	 SELECT CONVERT(char(16), rep.create_date,20), usr.first_name, usr.last_name, rep.headline, rep.text, rep.reply_level

	--SELECT  rep.create_date, usr.first_name, usr.last_name, rep.headline, rep.text, rep.reply_level

	FROM A_REPLIES rep, A_DISCUSSION disc, A_CONF_USERS usr

	WHERE disc.discussion_id = @discussion_id

 	AND rep.parent_id = disc.discussion_id

 	AND usr.user_id = rep.user_id

	ORDER BY rep.create_date DESC

END

/*

CREATE PROCEDURE GetAllRepliesInDisc

--  This proc is used when you want to see all replies in a discussion. It asks

--  the user what preferences he has for this conference and  returns the replies

--  in the sortorder he wants it in

 @discussion_id int,

 @user_id int

AS

-- Lets get the meta_id for this discussion id

DECLARE @meta_id int

SELECT @meta_id = cf.conf_id

FROM CONF_FORUM cf, DISCUSSION d, FORUM f

WHERE d.discussion_id = @discussion_id

AND d.forum_id = f.forum_id

AND f.forum_id = cf.forum_id

-- Lets get the users sortorder preference

DECLARE @userOrder int

-- EXEC @userOrder = ConfUsersGetReplyOrder @meta_id, @user_id ,0

EXEC @userOrder = ConfUsersGetReplyOrder @meta_id, @user_id

IF( @userOrder = 1 )

BEGIN

	SELECT CONVERT(char(16), rep.create_date,20), usr.first_name, usr.last_name, rep.headline, rep.text, rep.reply_level

	--SELECT  rep.create_date, usr.first_name, usr.last_name, rep.headline, rep.text, rep.reply_level

	FROM REPLIES rep, DISCUSSION disc, CONF_USERS usr

	WHERE disc.discussion_id = @discussion_id

 	AND rep.parent_id = disc.discussion_id

 	AND usr.user_id = rep.user_id

	ORDER BY rep.create_date ASC

END

ELSE BEGIN

	 SELECT CONVERT(char(16), rep.create_date,20), usr.first_name, usr.last_name, rep.headline, rep.text, rep.reply_level

	--SELECT  rep.create_date, usr.first_name, usr.last_name, rep.headline, rep.text, rep.reply_level

	FROM REPLIES rep, DISCUSSION disc, CONF_USERS usr

	WHERE disc.discussion_id = @discussion_id

 	AND rep.parent_id = disc.discussion_id

 	AND usr.user_id = rep.user_id

	ORDER BY rep.create_date DESC

END

*/



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_GetAllRepliesInDiscAdmin

/*

  This proc is used when you want to see all replies in a discussion. It asks

  the user what preferences he has for this conference and  returns the replies

  in the sortorder he wants it in

*/

	@discussion_id int,

	@user_id int

AS

/* Lets get the meta_id for this discussion id */

DECLARE @meta_id int

SELECT @meta_id = cf.conf_id

FROM A_CONF_FORUM cf, A_DISCUSSION d, A_FORUM f

WHERE d.discussion_id = @discussion_id

AND d.forum_id = f.forum_id

AND f.forum_id = cf.forum_id

/* Lets get the users sortorder preference*/

DECLARE @userOrder int

EXEC @userOrder = A_ConfUsersGetReplyOrder @meta_id, @user_id , 0

IF( @userOrder = 1 )

BEGIN

	SELECT  CONVERT(char(16), rep.create_date,20) as 'create_date', usr.first_name, usr.last_name, rep.headline, rep.text, rep.reply_level, rep.reply_id

	--SELECT  rep.create_date, usr.first_name, usr.last_name, rep.headline, rep.text, rep.reply_level

	FROM A_REPLIES rep, A_DISCUSSION disc, A_CONF_USERS usr

	WHERE disc.discussion_id = @discussion_id

 	AND rep.parent_id = disc.discussion_id

 	AND usr.user_id = rep.user_id

	ORDER BY rep.create_date ASC

END

ELSE BEGIN

	 SELECT CONVERT(char(16), rep.create_date,20) as 'create_date', usr.first_name, usr.last_name, rep.headline, rep.text, rep.reply_level,  rep.reply_id

	--SELECT  rep.create_date, usr.first_name, usr.last_name, rep.headline, rep.text, rep.reply_level

	FROM A_REPLIES rep, A_DISCUSSION disc, A_CONF_USERS usr

	WHERE disc.discussion_id = @discussion_id

 	AND rep.parent_id = disc.discussion_id

 	AND usr.user_id = rep.user_id

	ORDER BY rep.create_date DESC

END



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_GetAllTemplateLibs  AS

/*

Returns all the templatelibs which exists for all conferences. Used in adminmode

to create the list where the admin can choose among current templatelibs

*/

-- Get all templatelibs for all confernences

SELECT DISTINCT t.template_lib , t.template_lib

FROM A_templates t



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_GetAllUsersInList AS

/*

This function is used from AdminIpAcces servlet to generate a list

*/

SELECT user_id, last_name + ', ' + first_name from A_users

ORDER BY last_name



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_GetConfLoginNames

/*

Returns different variants on the Users name depending on the third argument.

Valid parameters are:

	1 = the firstname

	2= the lastname

	all else returns the firstname + the lastname

*/

	@meta_Id int,

	@user_Id int,

	@argument int

AS

-- Lets get the firstname

IF (@argument = 1 ) BEGIN

	SELECT cu.first_name

	FROM A_conf_users cu, A_conf_users_crossref crossref, A_conference c

	WHERE cu.user_id = @user_id

	AND cu.user_id = crossref.user_id

	AND crossref.conf_id = @meta_id

END

-- Lets get the lastname

ELSE IF (@argument = 2 ) BEGIN

	SELECT cu.last_name

	FROM A_conf_users cu, A_conf_users_crossref crossref, A_conference c

	WHERE cu.user_id = @user_id

	AND cu.user_id = crossref.user_id

	AND crossref.conf_id = @meta_id

END

-- Lets get both firstname and lastname

ELSE IF (@argument = 3 ) BEGIN

	SELECT cu.first_Name + ' ' + cu.last_name

	FROM A_conf_users cu, A_conf_users_crossref crossref, A_conference c

	WHERE cu.user_id = @user_id

	AND cu.user_id = crossref.user_id

	AND crossref.conf_id = @meta_id

END



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_GetDiscussionDate

AS

DECLARE @year varchar(4)

SELECT @year = DATEPART(yy,GETDATE())

DECLARE @month varchar(2)

SELECT @month = DATEPART(mm,GETDATE())

DECLARE @day varchar(2)

SELECT @day = DATEPART(dd,GETDATE())

/* Lets get the TIME */

DECLARE @hh varchar(2)

SELECT @hh = DATEPART(hh,GETDATE())

DECLARE @mm varchar(2)

SELECT @mm = DATEPART(mm,GETDATE())

DECLARE @ss varchar(2)

SELECT @ss = DATEPART(ss,GETDATE())

SELECT @year +'-'+ @month +'-'+ @day + ' ' +@hh + ':' + @mm + ':' + @ss

/* SELECT 'Datum: ', @year +'-'+ @month +'-'+ @day */



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;







/****** Object:  Stored Procedure dbo.GetDiscussionHeader    Script Date: 2000-12-05 09:40:09 ******/



/****** Object:  Stored Procedure dbo.GetDiscussionHeader    Script Date: 2000-10-27 17:39:26 ******/

CREATE PROCEDURE A_GetDiscussionHeader

/*

  This proc is used when the user should add a reply to an existing discussion.

  The proc returns the headers for the discussion and suggest it to the user in

  the form where you post your commment. The servlet grabs the first header.

*/

	@discussion_id int

AS



-- Bugfix, to prevent when were creating a new conference and the sent discussion_id is -1, then return an empty string

IF (@discussion_id = -1 ) BEGIN

	SELECT  ' '

END ELSE BEGIN

	SELECT r.headline

	FROM A_REPLIES r

	WHERE r.parent_id = @discussion_id

	ORDER BY reply_id

END



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_GetFirstForum

/*

 This method is used to get the first conference id for the meta_id. It will be used

later to show all messages for this conference by default

*/

	@meta_id int

AS

/* Lets get the forum with the lowest id for this meta_id*/

DECLARE @returnVal int



--SELECT  @returnVal  = min( f.forum_id )

SELECT TOP 1 f.forum_id AS 'FirstForumId'

FROM A_forum f, A_conf_forum cf

WHERE cf.conf_id = @meta_id

AND cf.forum_id  = f.forum_id

ORDER BY f.forum_name



IF ( @@ROWCOUNT =  0 ) BEGIN

	SELECT -1 AS 'FirstForumId'

END



















/*CREATE PROCEDURE GetFirstForum



 This method is used to get the first conference id for the meta_id. It will be used

later to show all messages for this conference by default



	@meta_id int

AS

-- Lets get the forum with the lowest id for this meta_id

DECLARE @returnVal int

SELECT  @returnVal  = min( f.forum_id )

FROM forum f, conf_forum cf

WHERE cf.conf_id = @meta_id

AND cf.forum_id  = f.forum_id

SELECT @returnVal =  ISNULL(@returnVal, -1)

SELECT @returnVal AS 'FirstForumId'

*/



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_GetForumName

/*

Returns the name the forum. Used in admin mode to display the currently

selected forum

*/

	@forum_id int

AS

DECLARE @returnVal varchar(255)

SELECT  @returnVal = forum_name

FROM A_forum f

WHERE f.forum_id = @forum_id

SELECT @returnVal =  ISNULL(@returnVal, 'Not found')

SELECT @returnVal AS 'Forum_name'



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_GetLastDiscussionId

/*

This function is used the first time the user starts his session to the conference.

It will fetch the latest discussionid and which will be put in the users session

object, so the latest replies can be retrieved later. if no discussion id is found, -1 will be returned

*/

	@meta_id int,

	@forum_id int

AS

DECLARE @returnVal int

SELECT @returnVal=  MAX( disc.discussion_id)

FROM A_conf_forum, A_forum, A_discussion disc, A_replies, A_conf_users usr

WHERE A_conf_forum.conf_id =  @meta_id

AND A_conf_forum.forum_id  = A_forum.forum_id

AND A_forum.forum_id = @forum_id

AND A_forum.forum_id = disc.forum_id

AND disc.reply_id = A_replies.reply_id

SELECT @returnVal =  ISNULL(@returnVal, -1)

SELECT @returnVal AS 'LastDiscussionId'



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_GetLastLoginDate

/*

This procedure is used when we shall get a users FULL last logindate

*/

	@meta_id int,

	@user_id int

AS

SELECT DISTINCT crossref.last_login_date

FROM A_CONF_USERS cu, A_CONF_USERS_CROSSREF crossref

WHERE crossref.user_id = @user_id

AND crossref.conf_id = @meta_id

-- The old style

--SELECT DISTINCT cu.last_login_date

--FROM CONF_USERS cu, CONFERENCE c , CONF_USERS_CROSSREF crossref

--WHERE cu.user_id = @user_id

--AND cu.user_id = crossref.user_id

--AND crossref.conf_id = @meta_id



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_GetLastLoginDate2

/*

This procedure is used when we shall get a users last logindate in the format

as it will appear in the discussion list

*/

	@meta_id int,

	@user_id int

AS

SELECT  DISTINCT CONVERT(char(19), crossref.last_login_date ,20)

FROM A_CONF_USERS cu,  A_CONF_USERS_CROSSREF crossref

WHERE crossref.user_id = @user_id

AND crossref.conf_id = @meta_id

--SELECT  DISTINCT CONVERT(char(16), cu.last_login_date ,20)

--FROM CONF_USERS cu, CONFERENCE c, CONF_USERS_CROSSREF crossref

--WHERE cu.user_id = @user_id

--AND cu.user_id = crossref.user_id

--AND crossref.conf_id = @meta_id



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_GetNbrOfDiscs

/*

Returns the number of discussions in a forum. Used to determind how many

discussions to show

*/

	@forum_id int

AS

SELECT COUNT (DISTINCT discussion_id)

FROM A_discussion

WHERE A_discussion.forum_id = @forum_id



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_GetNbrOfDiscsToShow

/*

Returns the number of discussions to show for a certain forum. By default 20 will be

returned.

*/

	@forum_id int

AS

DECLARE @returnVal int

SELECT @returnVal = f.discs_to_show

FROM A_forum f

WHERE f.forum_id = @forum_id

SELECT @returnVal =  ISNULL(@returnVal, 20)

SELECT @returnVal AS 'FirstForumId'



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_GetTemplateIdFromName

/*

 Lets get the folder library id for the passed name, if nothing is found, -1 will be returned.

This function is used when the admin wants change the templateset for a conference.

*/

	@aTemplateLibName varchar(50)

AS

DECLARE @returnVal int

SELECT @returnVal = t.template_id

FROM A_templates t

WHERE t.template_lib = @aTemplateLibName

-- Lets validate for null

SELECT @returnVal = ISNULL(  @returnVal , -1 )

SELECT @returnVal AS 'TemplateId'



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_GetTemplateLib

/*

 Lets get the folder library where all the templates are situated. if nothing is found for that

meta id , 'original' is returned instead

*/

	@meta_id int

AS

DECLARE @returnVal varchar(50)

SELECT @returnVal = t.template_lib

FROM A_conference c , A_conf_templates ct , A_templates t

WHERE t.template_id = ct.template_id

AND ct.conf_id = c.meta_id

AND c.meta_id = @meta_id

SELECT @returnVal =  ISNULL(@returnVal, 'original')

SELECT @returnVal AS 'TemplateLib'



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_GetTemplateLibName

/*

 Lets get the folder library name for a certain templateId

*/

	@meta_id int,

	@template_id int

AS

SELECT  t.template_lib

FROM A_conference c , A_conf_templates ct , A_templates t

WHERE t.template_id = @template_id

AND t.template_id = ct.template_id

AND ct.conf_id = c.meta_id

AND c.meta_id = @meta_id



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS OFF
;





/****** Object:  Stored Procedure dbo.GetTime    Script Date: 2000-10-27 17:39:27 ******/

CREATE PROCEDURE A_GetTime AS



SELECT SUBSTRING( CONVERT(char(20),GETDATE(),20),1, 20) AS 'Now'



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_GetUserNames

/*

This procedure is used to retrieve a users full name (first name + last name

concateneted.

*/

	@user_id int,

	@what int

AS

	DECLARE @returnVal char(25)

IF(@what = 1) BEGIN

	SELECT @returnVal = RTRIM(first_name)

	FROM A_conf_users

	WHERE A_conf_users.user_id = @user_id

END ELSE BEGIN

	SELECT @returnVal =  RTRIM(last_name)

	FROM A_conf_users

	WHERE A_conf_users.user_id = @user_id

END

SELECT @returnVal =  ISNULL(@returnVal, -1)

SELECT @returnVal AS 'UserName'



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_InsertFirstReply

/*

Inserts the first reply into the newly created discussion

*/

 @user_id int,

 @headline varchar(255),

 @text text,

 @reply_level int,

 @returnReplyId int



AS

/* Lets get today's date */

DECLARE @toDay datetime

SELECT @toDay = GETDATE()

/* Lets insert into replies */

INSERT INTO A_replies (user_id, create_date, headline, text, reply_level)

VALUES ( @user_id, @toDay, @headline, @text, @reply_level)

/* Lets get the reply id for just inserted reply */

SELECT  @returnReplyId = @@identity

/* Lets return the reply_id */

RETURN @returnReplyId



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;





CREATE PROCEDURE A_LinkForumToConf

/* Links a forum to a conference. Used when a forum is created

*/

 @conf_id int,

 @forum_id int

AS

INSERT INTO A_conf_forum (conf_id, forum_id, sort_number )

VALUES  (@conf_id, @forum_id, 1 )



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;










CREATE PROCEDURE A_MemberInConf

/* Used to check if a user exists as a member in a conference.

Used when a user tries to log in to a conference

*/

	@meta_id int,

	@user_id int

AS

-- Lets check if a user is a member of a particular conference

SELECT DISTINCT cu.user_id

FROM A_conf_users cu, A_conf_users_crossref crossref, A_conference c

WHERE cu.user_id = @user_id

AND cu.user_id = crossref.user_id

AND crossref.conf_id = @meta_id



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_RenameForum

/*

 Changes the name on a forum. Used through the admin interface

*/

	@forum_id int,

	@forum_name varchar(255)

AS

UPDATE A_forum

SET A_forum.forum_name = @forum_name

WHERE A_forum.forum_id = @forum_id



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_SearchText

/* This procedure is the one which is used to search among the

headlines, text, and user

The argument category will come as an integer. 0 = header, 1 = text

*/

	@meta_id int,

	@forum_id int,

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

	SELECT DISTINCT '0' as 'newflag' , disc.discussion_id, SUBSTRING( CONVERT(char(16), rep.create_date,20), 6, 16) AS 'create_date' , rep.headline, disc.count_replies, usr.first_name, usr.last_name, SUBSTRING( CONVERT(char(20), disc.last_mod_date,20),1, 20) AS 'updated_date'

	FROM A_replies rep, A_discussion disc, A_conf_users usr, A_conference conf, A_conf_forum cf, A_forum, A_conf_users_crossref crossref

	WHERE rep.headline LIKE RTRIM(@sWord)

	AND cf.conf_id = @meta_id

	AND cf.forum_id = A_forum.forum_id

	AND A_forum.forum_id = @forum_id

	AND A_forum.forum_id = disc.forum_id

	AND disc.reply_id = rep.reply_id

-- Lets check for the date

	AND rep.create_date >@fromDate AND rep.create_date < @toDate

-- Lets tie to the meta_id

	AND rep.user_id = usr.user_id

	AND usr.user_id = crossref.user_id

	AND crossref.conf_id = @meta_id

END

-- LETS SEARCH AMONG TEXT

ELSE IF( @category = 1 )

BEGIN

	/* Lets search  among The replies TEXT */

	SELECT DISTINCT '0' as 'newflag' ,disc.discussion_id, SUBSTRING( CONVERT(char(16), rep.create_date,20), 6, 16) AS 'create_date' , rep.headline, disc.count_replies, usr.first_name, usr.last_name , SUBSTRING( CONVERT(char(20), disc.last_mod_date,20),1, 20) AS 'updated_date'

	FROM A_replies rep, A_discussion disc, A_conf_users usr, A_conference conf, A_conf_forum cf, A_forum,  A_conf_users_crossref crossref

	WHERE rep.text LIKE RTRIM(@sWord)

	AND rep.parent_id = disc.discussion_id

	AND disc.forum_id = A_forum.forum_id

	AND A_forum.forum_id = @forum_id

	AND A_forum.forum_id = cf.forum_id

	AND cf.conf_id = @meta_id

-- Lets check for the date

	AND rep.create_date >@fromDate AND rep.create_date < @toDate

-- Lets tie to the meta_id

	AND rep.user_id = usr.user_id

	AND usr.user_id = crossref.user_id

	AND crossref.conf_id = @meta_id

END

/*

CREATE PROCEDURE SearchText

 This procedure is the one which is used to search among the

headlines, text, and user

The argument category will come as an integer. 0 = header, 1 = text

	@meta_id int,

	@forum_id int,

	@category int,

	@sWord char(100),

	@fromDate datetime,

	@toDate datetime

AS

-- Lets build a '%Computer%' string

SET @sWord =  '%' + RTRIM(@sWord) + '%'

PRINT 'Sökord: ' + @sWord

IF( @category = 0)

BEGIN

	-- Lets search  among The replies HEADERS

	SELECT DISTINCT '0' as 'newflag' , disc.discussion_id, SUBSTRING( CONVERT(char(16), rep.create_date,20), 6, 16) AS 'create_date' , rep.headline, disc.count_replies, usr.first_name, usr.last_name

	FROM replies rep, discussion disc, conf_users usr, conference conf, conf_forum cf, forum, conf_users_crossref crossref

	WHERE rep.headline LIKE RTRIM(@sWord)

	AND cf.conf_id = @meta_id

	AND cf.forum_id = forum.forum_id

	AND forum.forum_id = @forum_id

	AND Forum.forum_id = disc.forum_id

	AND disc.reply_id = rep.reply_id

-- Lets check for the date

	AND rep.create_date >@fromDate AND rep.create_date < @toDate

-- Lets tie to the meta_id

	AND rep.user_id = usr.user_id

	AND usr.user_id = crossref.user_id

	AND crossref.conf_id = @meta_id

END

-- LETS SEARCH AMONG TEXT

ELSE IF( @category = 1 )

BEGIN

	-- Lets search  among The replies TEXT

	SELECT DISTINCT '0' as 'newflag' ,disc.discussion_id, SUBSTRING( CONVERT(char(16), rep.create_date,20), 6, 16) AS 'create_date' , rep.headline, disc.count_replies, usr.first_name, usr.last_name

	FROM replies rep, discussion disc, conf_users usr, conference conf, conf_forum cf, forum,  conf_users_crossref crossref

	WHERE rep.text LIKE RTRIM(@sWord)

	AND rep.parent_id = disc.discussion_id

	AND disc.forum_id = forum.forum_id

	AND forum.forum_id = @forum_id

	AND forum.forum_id = cf.forum_id

	AND cf.conf_id = @meta_id

-- Lets check for the date

	AND rep.create_date >@fromDate AND rep.create_date < @toDate

-- Lets tie to the meta_id

	AND rep.user_id = usr.user_id

	AND usr.user_id = crossref.user_id

	AND crossref.conf_id = @meta_id

END

*/



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_SelfRegRoles_AddNew

	@theMetaId int ,

	@new_role_id int ,

	@role_name char(25)



/*

-- Lets add a new role which a member will have when he is selfregistereing.

*/



AS



-- Lets check if the role already exists in our selfreg role list

DECLARE @foundRole int

SELECT @foundRole = 0



SELECT @foundRole = sr.selfreg_id

FROM 	A_selfreg_roles sr , A_conf_selfreg_crossref ref, A_conference c

WHERE sr.role_id = @new_role_id

	AND sr.selfreg_id = ref.selfreg_id

	AND ref.meta_id = c.meta_id

	AND c.meta_id = @theMetaId





-- PRINT @foundRole

IF ( @foundRole = 0  ) BEGIN

	 PRINT 'Rollen fanns inte'

	-- Lets start to add the classification

	INSERT INTO A_selfreg_roles( role_id , role_name )

	VALUES ( @new_role_id , @role_name )

	SELECT @foundRole = @@identity



	-- Lets insert the new crossreferences to the new role

	INSERT INTO A_conf_selfreg_crossref (meta_id,selfreg_id)

	VALUES (  @theMetaId , @foundRole )

END ELSE BEGIN

	PRINT 'Rollen fanns - gör ingenting'

END



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_SelfRegRoles_Delete

	@meta_id int ,

	@selfRegId int



/*

Deletes a selfregistered role in a conference. Used by ConfAdmin servlet

*/



AS





DELETE

FROM A_conf_selfreg_crossref

WHERE A_conf_selfreg_crossref.meta_id = @meta_id

AND A_conf_selfreg_crossref.selfreg_id = @selfRegId



DELETE

FROM A_selfreg_roles

WHERE A_selfreg_roles.selfreg_id = @selfRegId



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_SelfRegRoles_GetAll

	@theMetaId int

AS



-- Lets get all roles in a conference a selfregistered member will get when his selfregistering



SELECT sr.selfreg_id, sr.role_name

FROM A_selfreg_roles sr , A_conf_selfreg_crossref ref, A_conference c

WHERE sr.selfreg_id = ref.selfreg_id

AND ref.meta_id = c.meta_id

AND c.meta_id = @theMetaId



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_SelfRegRoles_GetAll2

	@theMetaId int

AS



-- Lets get all roles a user will get when he is selfregistereing

-- Returns the ROLE_ID instead of selfregId which the original does

-- Used by the ConfLogin servlet



SELECT sr.role_id, sr.role_name

FROM A_selfreg_roles sr , A_conf_selfreg_crossref ref, A_conference c

WHERE sr.selfreg_id = ref.selfreg_id

AND ref.meta_id = c.meta_id

AND c.meta_id = @theMetaId



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_SetNbrOfDiscsToShow

/*

Sets the number of discussions to show for a certain forum.

*/

	@forum_id int,

	@newNbrToShow int

AS

UPDATE A_forum

SET discs_to_show = @newNbrToShow

WHERE forum_id = @forum_id



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_SetTemplateLib

/*

 Used when an admin wants to change the conferences template set to another one

*/

	@meta_id int ,

	 @new_lib_id varchar(50)

AS

UPDATE A_conf_templates

SET template_id= @new_lib_id

WHERE conf_id = @meta_id

;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_TestConfDb

AS

select 'Hurra Conferensen db svarar!'



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;






CREATE PROCEDURE A_UpdateDiscussionModifyDate

/* Updates the date when a discussion has had a new reply added to it.

*/

	@theDiscussionId int

AS

-- Lets start with calculate the archive date

DECLARE @archive_days int

DECLARE @toDay datetime

DECLARE @archiveDay datetime

SELECT @toDay = GETDATE()

-- Lets get the nbr of days to archive

UPDATE A_DISCUSSION

SET  A_discussion.last_mod_Date = GETDATE()

WHERE A_discussion.discussion_id =  @theDiscussionId



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;








CREATE PROCEDURE A_UpdateReply

/*

This function is used to update a replys header and text. used by  an administrator

*/

	@reply_id int,

	@header varchar(255),

	@text text

AS

UPDATE A_replies

	SET headline = @header , text = @text

	FROM A_replies rep

WHERE rep.reply_id = @reply_id



;
SET QUOTED_IDENTIFIER OFF
;
SET ANSI_NULLS ON
;

