-- confdiff.sql,v
-- Revision 


--Changed name on the templatelib and the procedures that adds new one
GO
UPDATE A_templates
SET A_templates.template_lib = 'original'
WHERE A_templates.template_lib = 'Original'
GO
GO
ALTER PROCEDURE A_AddNewTemplateLib 

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
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO
GO
ALTER PROCEDURE A_GetTemplateLib
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
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO
-- 2001-10-24

-- ************* Here is imCMS v1_5_0-pre10 tagged  *******************************

-- did changes to procedure so that it changes an conferences templateset to another one
ALTER PROCEDURE A_SetTemplateLib
/*
 Used when an admin wants to change the conferences template set to another one
*/
	@meta_id int , 
	 @new_lib_id varchar(50) 
AS
UPDATE A_conf_templates
SET template_id= @new_lib_id
WHERE conf_id = @meta_id
GO

-- 2002-01-23


-- update because off spelling mistake in A_discussion
GO
ALTER PROCEDURE A_AddReply
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
/* Lets update the discussions lastModified date */

EXEC A_UpdateDiscussionModifyDate @theDiscussionId

GO


--Update A_discussion.count_replies and last_mod_date 
declare @discId int, @nbrOfReplies int, @lastReplyDate datetime, @lastMod datetime
declare posCursor  Cursor scroll
for 	SELECT	discussion_id
FROM    A_discussion
open posCursor
fetch next from posCursor
into @discId
while @@fetch_status = 0
begin 
	SELECT @nbrOfReplies = COUNT(rep.reply_id) 
	FROM A_replies rep WHERE rep.parent_id = @discId
	-- Lets update the counter 
	UPDATE A_discussion 
	SET count_replies = @nbrOfReplies 
	WHERE discussion_id = @discId

	-- Lets get the last reply date in the discussion
	
	
	SELECT @lastReplyDate = r.create_date
	FROM A_replies r
	WHERE r.parent_id = @discId
	ORDER BY r.create_date 
	
	-- Lets update discussion.last_mod_date
	select @lastMod = last_mod_date
	from A_discussion
	where discussion_id = @discId
	if  @lastMod < @lastReplyDate
	begin
		UPDATE A_discussion
		SET last_mod_date = @lastReplyDate
		WHERE discussion_id = @discId
	end 
	fetch next from posCursor
	into @discId
END
close posCursor
deallocate posCursor		
GO
-- 2002-01-28

