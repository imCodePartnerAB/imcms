if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_AddChatParams]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_AddChatParams]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_AddMessageType]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_AddMessageType]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_AddNewChat]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_AddNewChat]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_AddNewChatMsg]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_AddNewChatMsg]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_AddNewMsgType]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_AddNewMsgType]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_AddTemplateLib]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_AddTemplateLib]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_ChatAutoTypes]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_ChatAutoTypes]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_DeleteAuthorizations]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_DeleteAuthorizations]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_DeleteConnections]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_DeleteConnections]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_DeleteMessage]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_DeleteMessage]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_Delete_MsgTypes]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_Delete_MsgTypes]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_FindMetaId]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_FindMetaId]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_FindTemplateLib]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_FindTemplateLib]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_GetAllTemplateLibs]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_GetAllTemplateLibs]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_GetAllTypes]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_GetAllTypes]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_GetAuthorizationTypes]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_GetAuthorizationTypes]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_GetBaseMsgTypes]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_GetBaseMsgTypes]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_GetChat]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_GetChat]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_GetChatAutoTypes]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_GetChatAutoTypes]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_GetChatName]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_GetChatName]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_GetChatNameAndPerm]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_GetChatNameAndPerm]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_GetChatParameters]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_GetChatParameters]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_GetChatParametersMini]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_GetChatParametersMini]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_GetMessageId]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_GetMessageId]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_GetMsgTypeId]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_GetMsgTypeId]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_GetMsgTypes]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_GetMsgTypes]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_GetTemplateIdFromName]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_GetTemplateIdFromName]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_GetTemplateLib]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_GetTemplateLib]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_GetTheMsgId]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_GetTheMsgId]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_GetTheMsgTypes]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_GetTheMsgTypes]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_GetTheMsgTypesBase]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_GetTheMsgTypesBase]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_MetaIdExists]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_MetaIdExists]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_SetNewTemplateLib]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_SetNewTemplateLib]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_UpdateChatParams]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_UpdateChatParams]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_deleteChatTemplateset]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_deleteChatTemplateset]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_getCheckboxText]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_getCheckboxText]
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_getParamsToCheckbox]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_getParamsToCheckbox]
;

SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_AddChatParams 
 @meta_id int,
 @updateTime int,
 @reload int,
 @inOut int,
 @privat int,
 @publik int,
 @dateTime int,
 @font int
 
 
AS
delete from C_chatParameters
where chatId = @meta_id
INSERT INTO C_chatParameters( chatId ,updateTime,reload ,inOut ,privat,publik,dateTime,font )
VALUES (@meta_id , @updateTime , @reload , @inOut, @privat, @publik, @dateTime , @font)

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

SET QUOTED_IDENTIFIER ON 
;
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_AddMessageType 
 @meta_id int,
 @msg_id int,
 @namn varchar(25)
 
AS
declare @retVal int
select @retVal = msg_id
from C_msg_type
where msg_string like @namn
if(@retVal is null) begin
	INSERT INTO C_msg_type(msg_string)
	VALUES (@namn)
	
	DECLARE @returnVal int
	SELECT @returnVal =  max(msg_id)
	FROM C_msg_type
	INSERT INTO C_chat_msg_type(msg_id, meta_id)
	VALUES (@returnVal, @meta_id)
end

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_AddNewChat
/*
Adds a new chat to the chat table
*/
 @meta_id int,
 @name varchar(255),
 @permType int
AS
INSERT INTO C_chat (meta_id,name,permType)
VALUES (@meta_id, @name, @permType)

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_AddNewChatMsg 
 
 @msg_id int,
 @meta_id int
 
AS
INSERT INTO C_chat_msg_type (msg_id,meta_id)
VALUES (@msg_id,@meta_id)

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_AddNewMsgType 
 @msg_id int,
 @msg_type varchar(25)
AS
INSERT INTO C_msg_type (msg_id,msg_string)
VALUES (@msg_id,@msg_type)

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_AddTemplateLib
/*
This function is used when an admin creates a new templateset.
*/
	@newTemplateLib varchar(50)
AS
INSERT INTO C_templates (template_lib)
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

CREATE PROCEDURE C_ChatAutoTypes 
 
 @autho_id int,
 @room_id int
 
AS
INSERT INTO C_chat_authorization (authorization_id,meta_id)
VALUES (@autho_id,@room_id)

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_DeleteAuthorizations
 @chatId int
AS
 
 DELETE
 FROM C_chat_authorization
 WHERE meta_id = @chatId

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_DeleteConnections
 @chatId int
AS
 
 DELETE
 FROM C_chat_room
 WHERE meta_id = @chatId
 DELETE 
 FROM C_chat_msg_type
 WHERE meta_id= @chatId

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

SET QUOTED_IDENTIFIER ON 
;
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_DeleteMessage
 @meta_id int,
 @msg_id int
AS
 DELETE
 FROM C_chat_msg_type
 WHERE msg_id = @msg_id
and meta_id=@meta_id
 --DELETE 
 --FROM C_msg_type
-- WHERE msg_id= @msg_id

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_Delete_MsgTypes
 @meta_id int
AS
 DELETE
 FROM C_chat_msg_type
 WHERE meta_id=@meta_id

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_FindMetaId
/*
This function is used by servlet ChatAdd to check if the meta_id argument
already exists in the database. Thas because a db can be used from
different servers, and a meta_id can be used twice to be added in the 
database
*/
	@newMetaId int
 AS
DECLARE @returnVal int
SELECT @returnVal = meta_id
FROM C_chat
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
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_FindTemplateLib
/*
Checks if a template set with such a name exists, if it exists, the
id for that template is returned. If not found -1 is returned. Used when an admin
shall add a new templatelib. Checks if there already exists such a templateset 
*/
	@newLibName varchar(50)
AS
DECLARE @returnVal int
SELECT @returnVal = template_id
FROM C_templates
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
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_GetAllTemplateLibs  AS
/*
Returns all the templatelibs which exists for all conferences. Used in adminmode
to create the list where the admin can choose among current templatelibs
*/
-- Get all templatelibs for all confernences
SELECT DISTINCT t.template_lib , t.template_lib
FROM C_templates t

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_GetAllTypes
 AS
select msg_string from C_msg_type

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_GetAuthorizationTypes
 AS
select authorization_id, authorization_type from C_authorization_types

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_GetBaseMsgTypes 
AS
select msg_string
from C_msg_type
where msg_Id >=100 and msg_Id < 104

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_GetChat
@chat_id int
AS
select * from C_chat
where meta_id=@chat_id

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_GetChatAutoTypes 
 @meta_id int
AS
select authorization_id
from C_chat_authorization
where meta_id = @meta_id

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_GetChatName
@chat_id int
AS
select name from C_chat
where meta_id=@chat_id

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_GetChatNameAndPerm
@meta_id int
AS
select name, permType from C_chat
where meta_id=@meta_id

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_GetChatParameters
 @meta_id int
 AS
select updateTime,reload,inOut,privat,publik,dateTime,font 
from C_chatParameters
where chatId = @meta_id

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_GetChatParametersMini
 @meta_id int
 AS
select updateTime,reload,inOut,privat,publik,dateTime,font from C_chatParameters
where chatId = @meta_id

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

SET QUOTED_IDENTIFIER ON 
;
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_GetMessageId
@string varchar(25)
AS
select msg_id  from C_msg_type
where msg_string =@string

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_GetMsgTypeId
 @msgType varchar(255)
AS
select msg_Id
from C_msg_type
where  msg_string=@msgType

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_GetMsgTypes 
 @meta_id int
AS
select t.msg_id, msg_string
from C_msg_type t, C_Chat_msg_type ct
where meta_id = @meta_id
and ct.msg_id = t.msg_id

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_GetTemplateIdFromName
/*
 Lets get the folder library id for the passed name, if nothing is found, -1 will be returned.
This function is used when the admin wants change the templateset for a conference.
*/
	@aTemplateLibName varchar(50)
AS
DECLARE @returnVal int
SELECT @returnVal = t.template_id
FROM C_templates t
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
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_GetTemplateLib
/*
 Lets get the folder library where all the templates are situated. if nothing is found for that
meta id , 'Original' is returned instead
*/
	@meta_id int
AS
DECLARE @returnVal varchar(50)
SELECT @returnVal = template_lib
FROM C_chat c , C_chat_templates ct , C_templates t
WHERE t.template_id = ct.template_id
AND ct.chat_id = c.meta_id
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
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_GetTheMsgId
 @meta_id int,
 @name varChar(255)
AS
 select distinct r.msg_id from C_msg_type r, C_chat_msg_type cr
 where r.msg_string like(@name)
and r.msg_id=cr.msg_id
and cr.meta_id=@meta_id

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_GetTheMsgTypes
 @meta_id int
AS
select m.msg_Id, m.msg_string
from C_msg_type m, C_chat_msg_type cm
where  meta_id=@meta_id
AND cm.msg_id=m.msg_id

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_GetTheMsgTypesBase 
AS
select *
from C_msg_type
where msg_Id >=100 and msg_Id < 104

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_MetaIdExists
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
FROM C_chat
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
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_SetNewTemplateLib
/*
 Used when an admin wants to change the conferences template set to another one
Lets set a template lib name for a meta id
*/
	@meta_id int , 
	 @newLibName varchar(50) 
AS
DECLARE @returnVal int
SELECT @returnVal =  C_templates.template_id
from C_templates
where template_lib = @newLibName
INSERT INTO C_chat_templates(chat_id, template_id)
values(@meta_id,@returnVal)

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_UpdateChatParams
 @chatId int,
 @updateTime int,
 @reload int,
 @inOut int,
 @privat int,
 @publik int,
 @dateTime int,
 @font int
 
AS
UPDATE C_chatParameters 
SET updateTime = @updateTime,
reload = @reload,
inOut = @inOut,
publik = @publik,
privat = @privat,
font = @font,
dateTime = @dateTime
WHERE chatId = @chatId

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_deleteChatTemplateset
	@meta_id int 
AS
Delete
FROM C_chat_templates
WHERE chat_id = @meta_id

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_getCheckboxText
 @lang_id int
 AS
select boxName, text_string  from C_chat_checkbox_text
where lang_id=@lang_id

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

CREATE PROCEDURE C_getParamsToCheckbox
 @chatId int
 AS
select reload,inOut,privat,publik,dateTime,font,updateTime,chatId from C_chatParameters
where chatId = @chatId

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

