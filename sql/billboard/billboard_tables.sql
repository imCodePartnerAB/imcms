if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_B_replies_B_bill]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[B_replies] DROP CONSTRAINT FK_B_replies_B_bill
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_B_billboard_section_B_billboard]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[B_billboard_section] DROP CONSTRAINT FK_B_billboard_section_B_billboard
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_B_billboard_templates_B_billboard]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[B_billboard_templates] DROP CONSTRAINT FK_B_billboard_templates_B_billboard
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_B_bill_B_section]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[B_bill] DROP CONSTRAINT FK_B_bill_B_section
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_B_billboard_section_B_section]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[B_billboard_section] DROP CONSTRAINT FK_B_billboard_section_B_section
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_B_billboard_templates_B_templates]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[B_billboard_templates] DROP CONSTRAINT FK_B_billboard_templates_B_templates
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_bill]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[B_bill]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_billboard]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[B_billboard]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_billboard_section]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[B_billboard_section]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_billboard_templates]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[B_billboard_templates]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_replies]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[B_replies]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_section]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[B_section]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[B_templates]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[B_templates]
GO

CREATE TABLE [dbo].[B_bill] (
	[bill_id] [int] IDENTITY (1, 1) NOT NULL ,
	[section_id] [int] NOT NULL ,
	[create_date] [datetime] NOT NULL ,
	[ip_adress] [varchar] (15) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
	[headline] [varchar] (255) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
	[text] [text] COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
	[email] [varchar] (155) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
	[user_id] [int] NOT NULL 
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

CREATE TABLE [dbo].[B_billboard] (
	[meta_id] [int] NOT NULL ,
	[name] [varchar] (255) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ,
	[subject] [varchar] (50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[B_billboard_section] (
	[billboard_id] [int] NOT NULL ,
	[section_id] [int] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[B_billboard_templates] (
	[billboard_id] [int] NOT NULL ,
	[template_id] [int] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[B_replies] (
	[reply_id] [int] IDENTITY (1, 1) NOT NULL ,
	[ip_adress] [varchar] (15) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
	[create_date] [datetime] NOT NULL ,
	[headline] [varchar] (255) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
	[text] [text] COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
	[parent_id] [int] NOT NULL ,
	[user_id] [int] NOT NULL ,
	[email] [varchar] (155) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL 
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

CREATE TABLE [dbo].[B_section] (
	[section_id] [int] IDENTITY (1, 1) NOT NULL ,
	[section_name] [varchar] (255) COLLATE SQL_Latin1_General_CP1_CI_AS NULL ,
	[archive_time] [datetime] NOT NULL ,
	[archive_mode] [char] (1) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
	[days_to_show] [int] NOT NULL ,
	[discs_to_show] [int] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[B_templates] (
	[template_id] [int] IDENTITY (1, 1) NOT NULL ,
	[template_lib] [varchar] (50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL 
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[B_bill] WITH NOCHECK ADD 
	CONSTRAINT [PK_B_bill] PRIMARY KEY  CLUSTERED 
	(
		[bill_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[B_billboard] WITH NOCHECK ADD 
	CONSTRAINT [PK_B_billboard] PRIMARY KEY  CLUSTERED 
	(
		[meta_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[B_billboard_section] WITH NOCHECK ADD 
	CONSTRAINT [PK_B_billboard_section] PRIMARY KEY  CLUSTERED 
	(
		[billboard_id],
		[section_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[B_billboard_templates] WITH NOCHECK ADD 
	CONSTRAINT [PK_B_billboard_templates] PRIMARY KEY  CLUSTERED 
	(
		[billboard_id],
		[template_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[B_replies] WITH NOCHECK ADD 
	CONSTRAINT [PK_B_replies] PRIMARY KEY  CLUSTERED 
	(
		[reply_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[B_section] WITH NOCHECK ADD 
	CONSTRAINT [PK_B_section] PRIMARY KEY  CLUSTERED 
	(
		[section_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[B_templates] WITH NOCHECK ADD 
	CONSTRAINT [PK_B_templates] PRIMARY KEY  CLUSTERED 
	(
		[template_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[B_section] WITH NOCHECK ADD 
	CONSTRAINT [DF_B_section_discs_to_show] DEFAULT (20) FOR [discs_to_show]
GO

ALTER TABLE [dbo].[B_bill] ADD 
	CONSTRAINT [FK_B_bill_B_section] FOREIGN KEY 
	(
		[section_id]
	) REFERENCES [dbo].[B_section] (
		[section_id]
	)
GO

ALTER TABLE [dbo].[B_billboard_section] ADD 
	CONSTRAINT [FK_B_billboard_section_B_billboard] FOREIGN KEY 
	(
		[billboard_id]
	) REFERENCES [dbo].[B_billboard] (
		[meta_id]
	),
	CONSTRAINT [FK_B_billboard_section_B_section] FOREIGN KEY 
	(
		[section_id]
	) REFERENCES [dbo].[B_section] (
		[section_id]
	)
GO

ALTER TABLE [dbo].[B_billboard_templates] ADD 
	CONSTRAINT [FK_B_billboard_templates_B_billboard] FOREIGN KEY 
	(
		[billboard_id]
	) REFERENCES [dbo].[B_billboard] (
		[meta_id]
	),
	CONSTRAINT [FK_B_billboard_templates_B_templates] FOREIGN KEY 
	(
		[template_id]
	) REFERENCES [dbo].[B_templates] (
		[template_id]
	)
GO

ALTER TABLE [dbo].[B_replies] ADD 
	CONSTRAINT [FK_B_replies_B_bill] FOREIGN KEY 
	(
		[parent_id]
	) REFERENCES [dbo].[B_bill] (
		[bill_id]
	)
GO

