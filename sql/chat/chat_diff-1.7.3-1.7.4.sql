-- chat_diff.sql
-- 1_7_3-RELEASE


-- Remove selfregistration function from chat
if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_chat_authorization_authorization_types]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[C_chat_authorization] DROP CONSTRAINT FK_chat_authorization_authorization_types
GO

delete from c_authorization_types where authorization_id = 2
delete from c_chat_authorization where authorization_id = 2

update c_authorization_types set authorization_id = 2 where authorization_id = 3
update c_chat_authorization set authorization_id = 2 where authorization_id = 3


ALTER TABLE [dbo].[C_chat_authorization] ADD
	CONSTRAINT [FK_chat_authorization_authorization_types] FOREIGN KEY
	(
		[authorization_id]
	) REFERENCES [dbo].[C_authorization_types] (
		[authorization_id]
	)
-- 2003-10-28  Lennart

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_AddChatRoomConnection]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_AddChatRoomConnection]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_AddNewRoom]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_AddNewRoom]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_AddNewRoomMsg]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_AddNewRoomMsg]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_DeleteChatRoom]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_DeleteChatRoom]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_DeleteChatRooms]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_DeleteChatRooms]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_DeleteRoom]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_DeleteRoom]
GO
if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_GetAllRooms]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_GetAllRooms]
GO
if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_GetMaxRoomId]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_GetMaxRoomId]
GO
if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_GetRoomId]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_GetRoomId]
GO
if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_GetRoomIds]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_GetRoomIds]
GO
if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_GetRoomName]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_GetRoomName]
GO
if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_GetRooms]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_GetRooms]
GO
if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_GetTheRoomId]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_GetTheRoomId]
GO
if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_AddNewChatRoom]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[C_AddNewChatRoom]
GO

drop table C_room
-- Remove function "chat rooms"
--03-10-30 Lennart Å

-- 1_7_4-RELEASE
