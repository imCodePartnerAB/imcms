if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_chat_authorization_authorization_types]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[C_chat_authorization] DROP CONSTRAINT FK_chat_authorization_authorization_types
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_chat_authorization_chat]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[C_chat_authorization] DROP CONSTRAINT FK_chat_authorization_chat
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_chat_msg_type_chat]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[C_chat_msg_type] DROP CONSTRAINT FK_chat_msg_type_chat
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_chat_msg_type_msg_type]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[C_chat_msg_type] DROP CONSTRAINT FK_chat_msg_type_msg_type
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_authorization_types]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[C_authorization_types]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_chat]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[C_chat]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_chatParameters]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[C_chatParameters]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_chat_authorization]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[C_chat_authorization]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_chat_msg_type]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[C_chat_msg_type]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_chat_selfreg_crossref]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[C_chat_selfreg_crossref]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_chat_templates]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[C_chat_templates]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_msg_type]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[C_msg_type]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_selfreg_roles]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[C_selfreg_roles]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[C_templates]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[C_templates]
GO

CREATE TABLE [dbo].[C_authorization_types] (
	[authorization_type] [varchar] (50) NOT NULL ,
	[authorization_id] [int] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[C_chat] (
	[meta_id] [int] NOT NULL ,
	[name] [varchar] (255) NULL ,
	[permType] [int] NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[C_chatParameters] (
	[chatId] [int] NOT NULL ,
	[updateTime] [int] NULL ,
	[reload] [int] NULL ,
	[inOut] [int] NULL ,
	[privat] [int] NULL ,
	[publik] [int] NULL ,
	[dateTime] [int] NULL ,
	[font] [int] NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[C_chat_authorization] (
	[meta_id] [int] NOT NULL ,
	[authorization_id] [int] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[C_chat_msg_type] (
	[msg_id] [int] NOT NULL ,
	[meta_id] [int] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[C_chat_selfreg_crossref] (
	[meta_id] [int] NOT NULL ,
	[selfreg_id] [int] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[C_chat_templates] (
	[chat_id] [int] NOT NULL ,
	[template_id] [int] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[C_msg_type] (
	[msg_id] [int] IDENTITY (1, 1) NOT NULL ,
	[msg_string] [varchar] (25) NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[C_selfreg_roles] (
	[selfreg_id] [int] IDENTITY (1, 1) NOT NULL ,
	[role_id] [int] NULL ,
	[role_name] [char] (25) NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[C_templates] (
	[template_id] [int] IDENTITY (1, 1) NOT NULL ,
	[template_lib] [varchar] (50) NOT NULL 
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[C_authorization_types] WITH NOCHECK ADD 
	CONSTRAINT [PK_authorization_types] PRIMARY KEY  CLUSTERED 
	(
		[authorization_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[C_chat] WITH NOCHECK ADD 
	CONSTRAINT [PK_chat] PRIMARY KEY  CLUSTERED 
	(
		[meta_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[C_chatParameters] WITH NOCHECK ADD 
	CONSTRAINT [PK_chatParameters] PRIMARY KEY  CLUSTERED 
	(
		[chatId]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[C_chat_authorization] WITH NOCHECK ADD 
	CONSTRAINT [PK_chat_authorization] PRIMARY KEY  CLUSTERED 
	(
		[meta_id],
		[authorization_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[C_chat_msg_type] WITH NOCHECK ADD 
	CONSTRAINT [PK_room_msg_type] PRIMARY KEY  CLUSTERED 
	(
		[msg_id],
		[meta_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[C_chat_selfreg_crossref] WITH NOCHECK ADD 
	CONSTRAINT [PK_chat_selfreg_crossref] PRIMARY KEY  CLUSTERED 
	(
		[meta_id],
		[selfreg_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[C_chat_templates] WITH NOCHECK ADD 
	CONSTRAINT [PK_chat_templates] PRIMARY KEY  CLUSTERED 
	(
		[chat_id],
		[template_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[C_msg_type] WITH NOCHECK ADD 
	CONSTRAINT [PK_msg_type] PRIMARY KEY  CLUSTERED 
	(
		[msg_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[C_chatParameters] ADD 
	CONSTRAINT [DF_chatParameters_updateTime] DEFAULT (30) FOR [updateTime],
	CONSTRAINT [DF_chatParameters_reload] DEFAULT (2) FOR [reload],
	CONSTRAINT [DF_chatParameters_inOut] DEFAULT (2) FOR [inOut],
	CONSTRAINT [DF_chatParameters_privat] DEFAULT (2) FOR [privat],
	CONSTRAINT [DF_chatParameters_publik] DEFAULT (2) FOR [publik],
	CONSTRAINT [DF_chatParameters_dateTime] DEFAULT (2) FOR [dateTime],
	CONSTRAINT [DF_chatParameters_font] DEFAULT (2) FOR [font]
GO

ALTER TABLE [dbo].[C_chat_authorization] ADD 
	CONSTRAINT [FK_chat_authorization_authorization_types] FOREIGN KEY 
	(
		[authorization_id]
	) REFERENCES [dbo].[C_authorization_types] (
		[authorization_id]
	),
	CONSTRAINT [FK_chat_authorization_chat] FOREIGN KEY 
	(
		[meta_id]
	) REFERENCES [dbo].[C_chat] (
		[meta_id]
	)
GO

ALTER TABLE [dbo].[C_chat_msg_type] ADD 
	CONSTRAINT [FK_chat_msg_type_chat] FOREIGN KEY 
	(
		[meta_id]
	) REFERENCES [dbo].[C_chat] (
		[meta_id]
	),
	CONSTRAINT [FK_chat_msg_type_msg_type] FOREIGN KEY 
	(
		[msg_id]
	) REFERENCES [dbo].[C_msg_type] (
		[msg_id]
	)
GO

