ALTER TABLE [dbo].[doc_permission_sets_ex] DROP CONSTRAINT FK_doc_permission_sets_ex_doc_permission_sets1
GO

ALTER TABLE [dbo].[new_doc_permission_sets_ex] DROP CONSTRAINT FK_new_doc_permission_sets_ex_new_doc_permission_sets
GO

ALTER TABLE [dbo].[doc_permission_sets] DROP CONSTRAINT FK_doc_permission_sets_permission_types
GO

ALTER TABLE [dbo].[doc_permission_sets_ex] DROP CONSTRAINT FK_doc_permission_sets_ex_permission_sets
GO

ALTER TABLE [dbo].[new_doc_permission_sets] DROP CONSTRAINT FK_new_doc_permission_sets_permission_sets
GO

ALTER TABLE [dbo].[new_doc_permission_sets_ex] DROP CONSTRAINT FK_new_doc_permission_sets_ex_permission_sets
GO

ALTER TABLE [dbo].[roles_rights] DROP CONSTRAINT FK_r_r_permission_sets
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[doc_permission_sets]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[doc_permission_sets]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[doc_permission_sets_ex]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[doc_permission_sets_ex]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[doc_permissions]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[doc_permissions]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[new_doc_permission_sets_ex]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[new_doc_permission_sets_ex]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[new_doc_permission_sets]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[new_doc_permission_sets]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[permission_sets]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[permission_sets]
GO

CREATE TABLE [dbo].[doc_permission_sets] (
	[meta_id] [int] NOT NULL ,
	[set_id] [int] NOT NULL ,
	[permission_id] [int] NOT NULL 
)
GO

CREATE TABLE [dbo].[doc_permission_sets_ex] (
	[meta_id] [int] NOT NULL ,
	[set_id] [int] NOT NULL ,
	[permission_id] [int] NOT NULL ,
	[permission_data] [int] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[doc_permissions] (
	[permission_id] [int] NOT NULL ,
	[doc_type] [int] NOT NULL ,
	[lang_prefix] [varchar] (3) NOT NULL ,
	[description] [varchar] (50) NOT NULL 
)
GO

CREATE TABLE [dbo].[new_doc_permission_sets_ex] (
	[meta_id] [int] NOT NULL ,
	[set_id] [int] NOT NULL ,
	[permission_id] [int] NOT NULL ,
	[permission_data] [int] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[new_doc_permission_sets] (
	[meta_id] [int] NOT NULL ,
	[set_id] [int] NOT NULL ,
	[permission_id] [int] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[permission_sets] (
	[set_id] [int] NOT NULL ,
	[description] [varchar] (30) NOT NULL 
) ON [PRIMARY]
GO

 CREATE  CLUSTERED  INDEX [IX_doc_permission_sets] ON [dbo].[doc_permission_sets]([meta_id], [set_id]) ON [PRIMARY]
GO

 CREATE  CLUSTERED  INDEX [IX_doc_permissions] ON [dbo].[doc_permissions]([permission_id]) ON [PRIMARY]
GO

ALTER TABLE [dbo].[doc_permission_sets] WITH NOCHECK ADD 
	CONSTRAINT [PK_doc_permission_sets] PRIMARY KEY  NONCLUSTERED 
	(
		[meta_id],
		[set_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[doc_permission_sets_ex] WITH NOCHECK ADD 
	CONSTRAINT [PK_permission_sets_ex] PRIMARY KEY  NONCLUSTERED 
	(
		[meta_id],
		[set_id],
		[permission_id],
		[permission_data]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[doc_permissions] WITH NOCHECK ADD 
	CONSTRAINT [PK_doc_permissions] PRIMARY KEY  NONCLUSTERED 
	(
		[permission_id],
		[doc_type],
		[lang_prefix]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[new_doc_permission_sets_ex] WITH NOCHECK ADD 
	CONSTRAINT [PK_new_doc_permission_sets_ex] PRIMARY KEY  NONCLUSTERED 
	(
		[meta_id],
		[set_id],
		[permission_id],
		[permission_data]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[new_doc_permission_sets] WITH NOCHECK ADD 
	CONSTRAINT [PK_new_doc_permission_sets] PRIMARY KEY  NONCLUSTERED 
	(
		[meta_id],
		[set_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[permission_sets] WITH NOCHECK ADD 
	CONSTRAINT [PK_permission_types] PRIMARY KEY  NONCLUSTERED 
	(
		[set_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[doc_permission_sets] ADD 
	CONSTRAINT [FK_doc_permission_sets_permission_types] FOREIGN KEY 
	(
		[set_id]
	) REFERENCES [dbo].[permission_sets] (
		[set_id]
	),
	CONSTRAINT [FK_permission_sets_meta] FOREIGN KEY 
	(
		[meta_id]
	) REFERENCES [dbo].[meta] (
		[meta_id]
	)
GO

ALTER TABLE [dbo].[doc_permission_sets_ex] ADD 
	CONSTRAINT [FK_doc_permission_sets_ex_doc_permission_sets1] FOREIGN KEY 
	(
		[meta_id],
		[set_id]
	) REFERENCES [dbo].[doc_permission_sets] (
		[meta_id],
		[set_id]
	),
	CONSTRAINT [FK_doc_permission_sets_ex_permission_sets] FOREIGN KEY 
	(
		[set_id]
	) REFERENCES [dbo].[permission_sets] (
		[set_id]
	)
GO

ALTER TABLE [dbo].[doc_permissions] ADD 
	CONSTRAINT [FK_doc_permissions_doc_types] FOREIGN KEY 
	(
		[doc_type]
	) REFERENCES [dbo].[doc_types] (
		[doc_type]
	)
GO

ALTER TABLE [dbo].[new_doc_permission_sets_ex] ADD 
	CONSTRAINT [FK_new_doc_permission_sets_ex_new_doc_permission_sets] FOREIGN KEY 
	(
		[meta_id],
		[set_id]
	) REFERENCES [dbo].[new_doc_permission_sets] (
		[meta_id],
		[set_id]
	),
	CONSTRAINT [FK_new_doc_permission_sets_ex_permission_sets] FOREIGN KEY 
	(
		[set_id]
	) REFERENCES [dbo].[permission_sets] (
		[set_id]
	)
GO

ALTER TABLE [dbo].[new_doc_permission_sets] ADD 
	CONSTRAINT [FK_new_doc_permission_sets_meta] FOREIGN KEY 
	(
		[meta_id]
	) REFERENCES [dbo].[meta] (
		[meta_id]
	),
	CONSTRAINT [FK_new_doc_permission_sets_permission_sets] FOREIGN KEY 
	(
		[set_id]
	) REFERENCES [dbo].[permission_sets] (
		[set_id]
	)
GO

