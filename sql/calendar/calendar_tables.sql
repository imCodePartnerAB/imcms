if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_D_logg_D_calender]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[D_logg] DROP CONSTRAINT FK_D_logg_D_calender
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_D_page_D_calender]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[D_page] DROP CONSTRAINT FK_D_page_D_calender
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_D_templates_crossref_D_calender]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[D_templates_crossref] DROP CONSTRAINT FK_D_templates_crossref_D_calender
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_D_appointment_D_page]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[D_appointment] DROP CONSTRAINT FK_D_appointment_D_page
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_D_page_D_players]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[D_page] DROP CONSTRAINT FK_D_page_D_players
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_D_templates_crossref_D_template_types]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[D_templates_crossref] DROP CONSTRAINT FK_D_templates_crossref_D_template_types
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[D_appointment]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[D_appointment]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[D_calender]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[D_calender]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[D_logg]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[D_logg]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[D_months]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[D_months]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[D_page]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[D_page]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[D_players]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[D_players]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[D_template_types]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[D_template_types]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[D_templates_crossref]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[D_templates_crossref]
GO

CREATE TABLE [dbo].[D_appointment] (
	[appointment_id] [int] IDENTITY (1, 1) NOT NULL ,
	[titel] [varchar] (255) COLLATE Finnish_Swedish_CI_AS NULL ,
	[startTime] [datetime] NOT NULL ,
	[endTime] [datetime] NULL ,
	[place] [varchar] (50) COLLATE Finnish_Swedish_CI_AS NULL ,
	[notes] [varchar] (255) COLLATE Finnish_Swedish_CI_AS NULL ,
	[page_id] [int] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[D_calender] (
	[meta_id] [int] NOT NULL ,
	[name] [varchar] (50) COLLATE Finnish_Swedish_CI_AS NOT NULL ,
	[titel] [varchar] (50) COLLATE Finnish_Swedish_CI_AS NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[D_logg] (
	[logg_id] [int] IDENTITY (1, 1) NOT NULL ,
	[logg_date] [datetime] NOT NULL ,
	[logg_action] [varchar] (50) COLLATE Finnish_Swedish_CI_AS NULL ,
	[logg_exep] [varchar] (50) COLLATE Finnish_Swedish_CI_AS NULL ,
	[player_id] [int] NULL ,
	[meta_id] [int] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[D_months] (
	[months_id] [int] NOT NULL ,
	[name_se] [char] (10) COLLATE Finnish_Swedish_CI_AS NULL ,
	[name_en] [char] (10) COLLATE Finnish_Swedish_CI_AS NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[D_page] (
	[page_id] [int] IDENTITY (1, 1) NOT NULL ,
	[meta_id] [int] NOT NULL ,
	[players_id] [int] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[D_players] (
	[players_id] [int] IDENTITY (1, 1) NOT NULL ,
	[user_id] [int] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[D_template_types] (
	[type_id] [int] NOT NULL ,
	[name] [char] (50) COLLATE Finnish_Swedish_CI_AS NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[D_templates_crossref] (
	[meta_id] [int] NOT NULL ,
	[template_id] [int] IDENTITY (1, 1) NOT NULL ,
	[type_id] [int] NOT NULL 
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[D_appointment] WITH NOCHECK ADD 
	CONSTRAINT [PK_D_appointment] PRIMARY KEY  CLUSTERED 
	(
		[appointment_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[D_calender] WITH NOCHECK ADD 
	CONSTRAINT [PK_D_calender] PRIMARY KEY  CLUSTERED 
	(
		[meta_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[D_logg] WITH NOCHECK ADD 
	CONSTRAINT [PK_D_logg] PRIMARY KEY  CLUSTERED 
	(
		[logg_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[D_months] WITH NOCHECK ADD 
	CONSTRAINT [PK_D_months] PRIMARY KEY  CLUSTERED 
	(
		[months_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[D_page] WITH NOCHECK ADD 
	CONSTRAINT [PK_D_page] PRIMARY KEY  CLUSTERED 
	(
		[page_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[D_players] WITH NOCHECK ADD 
	CONSTRAINT [PK_D_players] PRIMARY KEY  CLUSTERED 
	(
		[players_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[D_template_types] WITH NOCHECK ADD 
	CONSTRAINT [PK_D_template_types] PRIMARY KEY  CLUSTERED 
	(
		[type_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[D_templates_crossref] WITH NOCHECK ADD 
	CONSTRAINT [PK_D_templates_crossref] PRIMARY KEY  CLUSTERED 
	(
		[template_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[D_appointment] ADD 
	CONSTRAINT [FK_D_appointment_D_page] FOREIGN KEY 
	(
		[page_id]
	) REFERENCES [dbo].[D_page] (
		[page_id]
	) NOT FOR REPLICATION 
GO

alter table [dbo].[D_appointment] nocheck constraint [FK_D_appointment_D_page]
GO

ALTER TABLE [dbo].[D_logg] ADD 
	CONSTRAINT [FK_D_logg_D_calender] FOREIGN KEY 
	(
		[meta_id]
	) REFERENCES [dbo].[D_calender] (
		[meta_id]
	) NOT FOR REPLICATION 
GO

alter table [dbo].[D_logg] nocheck constraint [FK_D_logg_D_calender]
GO

ALTER TABLE [dbo].[D_page] ADD 
	CONSTRAINT [FK_D_page_D_calender] FOREIGN KEY 
	(
		[meta_id]
	) REFERENCES [dbo].[D_calender] (
		[meta_id]
	) NOT FOR REPLICATION ,
	CONSTRAINT [FK_D_page_D_players] FOREIGN KEY 
	(
		[players_id]
	) REFERENCES [dbo].[D_players] (
		[players_id]
	) NOT FOR REPLICATION 
GO

alter table [dbo].[D_page] nocheck constraint [FK_D_page_D_calender]
GO

alter table [dbo].[D_page] nocheck constraint [FK_D_page_D_players]
GO

ALTER TABLE [dbo].[D_templates_crossref] ADD 
	CONSTRAINT [FK_D_templates_crossref_D_calender] FOREIGN KEY 
	(
		[meta_id]
	) REFERENCES [dbo].[D_calender] (
		[meta_id]
	) NOT FOR REPLICATION ,
	CONSTRAINT [FK_D_templates_crossref_D_template_types] FOREIGN KEY 
	(
		[type_id]
	) REFERENCES [dbo].[D_template_types] (
		[type_id]
	)
GO

alter table [dbo].[D_templates_crossref] nocheck constraint [FK_D_templates_crossref_D_calender]
GO

alter table [dbo].[D_templates_crossref] nocheck constraint [FK_D_templates_crossref_D_template_types]
GO

