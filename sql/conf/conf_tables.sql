if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_conf_users_crossref_conf_users]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[A_conf_users_crossref] DROP CONSTRAINT FK_conf_users_crossref_conf_users
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_replies_conf_users]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[A_replies] DROP CONSTRAINT FK_replies_conf_users
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_conference_forum_conference]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[A_conf_forum] DROP CONSTRAINT FK_conference_forum_conference
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_conf_selfreg_crossref_conference]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[A_conf_selfreg_crossref] DROP CONSTRAINT FK_conf_selfreg_crossref_conference
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_conf_templates_crossref_conference]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[A_conf_templates] DROP CONSTRAINT FK_conf_templates_crossref_conference
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_conf_users_crossref_conference]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[A_conf_users_crossref] DROP CONSTRAINT FK_conf_users_crossref_conference
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_conference_forum_forum]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[A_conf_forum] DROP CONSTRAINT FK_conference_forum_forum
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_discussion_forum]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[A_discussion] DROP CONSTRAINT FK_discussion_forum
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_discussion_replies]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[A_discussion] DROP CONSTRAINT FK_discussion_replies
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_conf_selfreg_crossref_selfreg_roles]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[A_conf_selfreg_crossref] DROP CONSTRAINT FK_conf_selfreg_crossref_selfreg_roles
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_conf_templates_crossref_templates]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[A_conf_templates] DROP CONSTRAINT FK_conf_templates_crossref_templates
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_conf_forum]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[A_conf_forum]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_conf_selfreg_crossref]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[A_conf_selfreg_crossref]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_conf_templates]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[A_conf_templates]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_conf_users]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[A_conf_users]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_conf_users_crossref]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[A_conf_users_crossref]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_conference]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[A_conference]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_discussion]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[A_discussion]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_forum]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[A_forum]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_replies]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[A_replies]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_selfreg_roles]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[A_selfreg_roles]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[A_templates]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[A_templates]
GO

CREATE TABLE [dbo].[A_conf_forum] (
	[conf_id] [int] NOT NULL ,
	[forum_id] [int] NOT NULL ,
	[sort_number] [int] NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[A_conf_selfreg_crossref] (
	[meta_id] [int] NOT NULL ,
	[selfreg_id] [int] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[A_conf_templates] (
	[conf_id] [int] NOT NULL ,
	[template_id] [int] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[A_conf_users] (
	[user_id] [int] NOT NULL ,
	[first_name] [varchar] (50)  NULL ,
	[last_name] [varchar] (50)  NULL ,
	[last_login_date_OLD] [datetime] NULL ,
	[replies_order_OLD] [int] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[A_conf_users_crossref] (
	[conf_id] [int] NOT NULL ,
	[user_id] [int] NOT NULL ,
	[user_level] [int] NOT NULL ,
	[last_login_date] [datetime] NULL ,
	[replies_order] [int] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[A_conference] (
	[meta_id] [int] NOT NULL ,
	[name] [varchar] (255)  NULL ,
	[sort_type] [char] (1)  NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[A_discussion] (
	[discussion_id] [int] IDENTITY (1, 1) NOT NULL ,
	[forum_id] [int] NOT NULL ,
	[reply_id] [int] NOT NULL ,
	[count_replies] [int] NULL ,
	[create_date] [datetime] NULL ,
	[last_mod_date] [datetime] NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[A_forum] (
	[forum_id] [int] IDENTITY (1, 1) NOT NULL ,
	[archive_time] [int] NOT NULL ,
	[forum_name] [varchar] (255)  NULL ,
	[archive_mode] [char] (1)  NOT NULL ,
	[discs_to_show] [int] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[A_replies] (
	[reply_id] [int] IDENTITY (1, 1) NOT NULL ,
	[user_id] [int] NULL ,
	[parent_id] [int] NULL ,
	[create_date] [datetime] NULL ,
	[headline] [varchar] (255)  NULL ,
	[text] [text]  NULL ,
	[reply_level] [int] NOT NULL 
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

CREATE TABLE [dbo].[A_selfreg_roles] (
	[selfreg_id] [int] IDENTITY (1, 1) NOT NULL ,
	[role_id] [int] NULL ,
	[role_name] [char] (25)  NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[A_templates] (
	[template_id] [int] IDENTITY (1, 1) NOT NULL ,
	[template_lib] [varchar] (50)  NOT NULL 
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[A_conf_forum] WITH NOCHECK ADD 
	CONSTRAINT [PK_conference_forum] PRIMARY KEY  NONCLUSTERED 
	(
		[conf_id],
		[forum_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[A_conf_selfreg_crossref] WITH NOCHECK ADD 
	CONSTRAINT [PK_conf_selfreg_crossref] PRIMARY KEY  NONCLUSTERED 
	(
		[meta_id],
		[selfreg_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[A_conf_templates] WITH NOCHECK ADD 
	CONSTRAINT [PK_conf_templates_crossref] PRIMARY KEY  NONCLUSTERED 
	(
		[conf_id],
		[template_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[A_conf_users] WITH NOCHECK ADD 
	CONSTRAINT [DF_conf_users_replies_order] DEFAULT (1) FOR [replies_order_OLD],
	CONSTRAINT [PK_systemusers] PRIMARY KEY  NONCLUSTERED 
	(
		[user_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[A_conf_users_crossref] WITH NOCHECK ADD 
	CONSTRAINT [DF_conf_users_crossref_user_type] DEFAULT (0) FOR [user_level],
	CONSTRAINT [DF_conf_users_crossref_replies_order] DEFAULT (1) FOR [replies_order],
	CONSTRAINT [PK_conf_users_crossref] PRIMARY KEY  NONCLUSTERED 
	(
		[conf_id],
		[user_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[A_conference] WITH NOCHECK ADD 
	CONSTRAINT [DF_conference_sort_type] DEFAULT ('B') FOR [sort_type],
	CONSTRAINT [PK_conference] PRIMARY KEY  NONCLUSTERED 
	(
		[meta_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[A_discussion] WITH NOCHECK ADD 
	CONSTRAINT [PK_discussion] PRIMARY KEY  NONCLUSTERED 
	(
		[discussion_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[A_forum] WITH NOCHECK ADD 
	CONSTRAINT [DF_forum_archive_time] DEFAULT (30) FOR [archive_time],
	CONSTRAINT [DF_forum_archive_mode] DEFAULT ('A') FOR [archive_mode],
	CONSTRAINT [DF_forum_discs_to_show] DEFAULT (20) FOR [discs_to_show],
	CONSTRAINT [PK_forum] PRIMARY KEY  NONCLUSTERED 
	(
		[forum_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[A_replies] WITH NOCHECK ADD 
	CONSTRAINT [DF_replies_reply_level] DEFAULT (0) FOR [reply_level],
	CONSTRAINT [PK_replies] PRIMARY KEY  NONCLUSTERED 
	(
		[reply_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[A_selfreg_roles] WITH NOCHECK ADD 
	CONSTRAINT [PK_selfreg_roles] PRIMARY KEY  NONCLUSTERED 
	(
		[selfreg_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[A_templates] WITH NOCHECK ADD 
	CONSTRAINT [PK_A_templates] PRIMARY KEY  NONCLUSTERED 
	(
		[template_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[A_conf_forum] ADD 
	CONSTRAINT [FK_conference_forum_conference] FOREIGN KEY 
	(
		[conf_id]
	) REFERENCES [dbo].[A_conference] (
		[meta_id]
	),
	CONSTRAINT [FK_conference_forum_forum] FOREIGN KEY 
	(
		[forum_id]
	) REFERENCES [dbo].[A_forum] (
		[forum_id]
	)
GO

ALTER TABLE [dbo].[A_conf_selfreg_crossref] ADD 
	CONSTRAINT [FK_conf_selfreg_crossref_conference] FOREIGN KEY 
	(
		[meta_id]
	) REFERENCES [dbo].[A_conference] (
		[meta_id]
	),
	CONSTRAINT [FK_conf_selfreg_crossref_selfreg_roles] FOREIGN KEY 
	(
		[selfreg_id]
	) REFERENCES [dbo].[A_selfreg_roles] (
		[selfreg_id]
	)
GO

ALTER TABLE [dbo].[A_conf_templates] ADD 
	CONSTRAINT [FK_conf_templates_crossref_conference] FOREIGN KEY 
	(
		[conf_id]
	) REFERENCES [dbo].[A_conference] (
		[meta_id]
	),
	CONSTRAINT [FK_conf_templates_crossref_templates] FOREIGN KEY 
	(
		[template_id]
	) REFERENCES [dbo].[A_templates] (
		[template_id]
	)
GO

ALTER TABLE [dbo].[A_conf_users_crossref] ADD 
	CONSTRAINT [FK_conf_users_crossref_conf_users] FOREIGN KEY 
	(
		[user_id]
	) REFERENCES [dbo].[A_conf_users] (
		[user_id]
	),
	CONSTRAINT [FK_conf_users_crossref_conference] FOREIGN KEY 
	(
		[conf_id]
	) REFERENCES [dbo].[A_conference] (
		[meta_id]
	)
GO

ALTER TABLE [dbo].[A_discussion] ADD 
	CONSTRAINT [FK_discussion_forum] FOREIGN KEY 
	(
		[forum_id]
	) REFERENCES [dbo].[A_forum] (
		[forum_id]
	),
	CONSTRAINT [FK_discussion_replies] FOREIGN KEY 
	(
		[reply_id]
	) REFERENCES [dbo].[A_replies] (
		[reply_id]
	)
GO

ALTER TABLE [dbo].[A_replies] ADD 
	CONSTRAINT [FK_replies_conf_users] FOREIGN KEY 
	(
		[user_id]
	) REFERENCES [dbo].[A_conf_users] (
		[user_id]
	)
GO

