-- Diff from 1_7_3-RELEASE up to 1_7_4-RELEASE
-- 1_7_3-RELEASE

CREATE TABLE [dbo].[shopping_order_item_descriptions] (
	[item_id] [int] NOT NULL ,
	[number] [int] NOT NULL ,
	[description] [varchar] (100) COLLATE Finnish_Swedish_CI_AS NOT NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[shopping_order_items] (
	[item_id] [int] IDENTITY (1, 1) NOT NULL ,
	[order_id] [int] NOT NULL ,
	[quantity] [int] NOT NULL ,
	[price] [money] NOT NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[shopping_orders] (
	[order_id] [int] IDENTITY (1, 1) NOT NULL ,
	[order_datetime] [datetime] NOT NULL ,
	[user_id] [int] NOT NULL
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[shopping_order_item_descriptions] WITH NOCHECK ADD
	CONSTRAINT [PK_shopping_order_item_descriptions] PRIMARY KEY  CLUSTERED
	(
		[item_id],
		[number]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[shopping_order_items] WITH NOCHECK ADD
	CONSTRAINT [PK_shopping_order_items] PRIMARY KEY  CLUSTERED
	(
		[item_id]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[shopping_orders] WITH NOCHECK ADD
	CONSTRAINT [PK_shopping_orders] PRIMARY KEY  CLUSTERED
	(
		[order_id]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[shopping_order_item_descriptions] ADD
	CONSTRAINT [FK_shopping_order_item_descriptions_shopping_order_items] FOREIGN KEY
	(
		[item_id]
	) REFERENCES [dbo].[shopping_order_items] (
		[item_id]
	)
GO

ALTER TABLE [dbo].[shopping_order_items] ADD
	CONSTRAINT [FK_shopping_order_items_shopping_orders] FOREIGN KEY
	(
		[order_id]
	) REFERENCES [dbo].[shopping_orders] (
		[order_id]
	)
GO

-- 2003-03-12 kreiger



CREATE TABLE [dbo].[poll_answers] (
	[id] [int] IDENTITY (1, 1) NOT NULL ,
	[question_id] [int] NOT NULL ,
	[text_id] [int] NOT NULL ,
	[option_number] [int] NOT NULL ,
	[answer_count] [int] NOT NULL ,
	[option_point] [int] NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[poll_questions] (
	[id] [int] IDENTITY (1, 1) NOT NULL ,
	[poll_id] [int] NOT NULL ,
	[question_number] [int] NOT NULL ,
	[text_id] [int] NOT NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[polls] (
	[id] [int] IDENTITY (1, 1) NOT NULL ,
	[name] [int] NULL ,
	[description] [int] NULL ,
	[meta_id] [int] NOT NULL ,
	[popup_freq] [int] NOT NULL ,
	[set_cookie] [bit] NOT NULL ,
	[hide_result] [bit] NOT NULL ,
	[confirmation_text] [int] NULL ,
	[email_recipients] [int] NULL ,
	[result_template] [int] NULL
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[poll_answers] WITH NOCHECK ADD
	CONSTRAINT [PK_poll_answers] PRIMARY KEY  CLUSTERED
	(
		[id]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[poll_questions] WITH NOCHECK ADD
	CONSTRAINT [PK_poll_questions] PRIMARY KEY  CLUSTERED
	(
		[id]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[polls] WITH NOCHECK ADD
	CONSTRAINT [PK_polls] PRIMARY KEY  CLUSTERED
	(
		[id]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[poll_answers] WITH NOCHECK ADD
	CONSTRAINT [DF_poll_answers_ans_count] DEFAULT (0) FOR [answer_count],
	CONSTRAINT [IX_poll_answers] UNIQUE  NONCLUSTERED
	(
		[question_id],
		[text_id]
	)  ON [PRIMARY] ,
	CONSTRAINT [IX_poll_answers_1] UNIQUE  NONCLUSTERED
	(
		[question_id],
		[option_number]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[poll_questions] WITH NOCHECK ADD
	CONSTRAINT [IX_poll_questions] UNIQUE  NONCLUSTERED
	(
		[poll_id],
		[question_number]
	)  ON [PRIMARY] ,
	CONSTRAINT [IX_poll_questions_1] UNIQUE  NONCLUSTERED
	(
		[poll_id],
		[text_id]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[polls] WITH NOCHECK ADD
	CONSTRAINT [DF_polls_popup_freq] DEFAULT (0) FOR [popup_freq],
	CONSTRAINT [DF_polls_enable_cookie] DEFAULT (0) FOR [set_cookie],
	CONSTRAINT [DF_polls_showresult] DEFAULT (0) FOR [hide_result]
GO

ALTER TABLE [dbo].[poll_answers] ADD
	CONSTRAINT [FK_poll_answers_poll_questions] FOREIGN KEY
	(
		[question_id]
	) REFERENCES [dbo].[poll_questions] (
		[id]
	)
GO

ALTER TABLE [dbo].[poll_questions] ADD
	CONSTRAINT [FK_poll_questions_polls] FOREIGN KEY
	(
		[poll_id]
	) REFERENCES [dbo].[polls] (
		[id]
	)
GO
-- 2003-03-13  Lennart Å


/* Add columns email_from and email_subject to polls table */

BEGIN TRANSACTION
SET QUOTED_IDENTIFIER ON
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE
SET ARITHABORT ON
SET NUMERIC_ROUNDABORT OFF
SET CONCAT_NULL_YIELDS_NULL ON
SET ANSI_NULLS ON
SET ANSI_PADDING ON
SET ANSI_WARNINGS ON
COMMIT
BEGIN TRANSACTION
ALTER TABLE dbo.polls
	DROP CONSTRAINT DF_polls_popup_freq
GO
ALTER TABLE dbo.polls
	DROP CONSTRAINT DF_polls_enable_cookie
GO
ALTER TABLE dbo.polls
	DROP CONSTRAINT DF_polls_showresult
GO
CREATE TABLE dbo.Tmp_polls
	(
	id int NOT NULL IDENTITY (1, 1),
	name int NULL,
	description int NULL,
	meta_id int NOT NULL,
	popup_freq int NOT NULL,
	set_cookie bit NOT NULL,
	hide_result bit NOT NULL,
	confirmation_text int NULL,
	email_recipients int NULL,
	email_from int NULL,
	email_subject int NULL,
	result_template int NULL
	)  ON [PRIMARY]
GO
ALTER TABLE dbo.Tmp_polls ADD CONSTRAINT
	DF_polls_popup_freq DEFAULT (0) FOR popup_freq
GO
ALTER TABLE dbo.Tmp_polls ADD CONSTRAINT
	DF_polls_enable_cookie DEFAULT (0) FOR set_cookie
GO
ALTER TABLE dbo.Tmp_polls ADD CONSTRAINT
	DF_polls_showresult DEFAULT (0) FOR hide_result
GO
SET IDENTITY_INSERT dbo.Tmp_polls ON
GO
IF EXISTS(SELECT * FROM dbo.polls)
	 EXEC('INSERT INTO dbo.Tmp_polls (id, name, description, meta_id, popup_freq, set_cookie, hide_result, confirmation_text, email_recipients, result_template)
		SELECT id, name, description, meta_id, popup_freq, set_cookie, hide_result, confirmation_text, email_recipients, result_template FROM dbo.polls TABLOCKX')
GO
SET IDENTITY_INSERT dbo.Tmp_polls OFF
GO
ALTER TABLE dbo.poll_questions
	DROP CONSTRAINT FK_poll_questions_polls
GO
DROP TABLE dbo.polls
GO
EXECUTE sp_rename N'dbo.Tmp_polls', N'polls', 'OBJECT'
GO
ALTER TABLE dbo.polls ADD CONSTRAINT
	PK_polls PRIMARY KEY CLUSTERED
	(
	id
	) ON [PRIMARY]

GO
COMMIT
BEGIN TRANSACTION
ALTER TABLE dbo.poll_questions WITH NOCHECK ADD CONSTRAINT
	FK_poll_questions_polls FOREIGN KEY
	(
	poll_id
	) REFERENCES dbo.polls
	(
	id
	)
GO
COMMIT

-- 2003-05-20 Kreiger

BEGIN TRANSACTION
SET QUOTED_IDENTIFIER ON
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE
SET ARITHABORT ON
SET NUMERIC_ROUNDABORT OFF
SET CONCAT_NULL_YIELDS_NULL ON
SET ANSI_NULLS ON
SET ANSI_PADDING ON
SET ANSI_WARNINGS ON
COMMIT
BEGIN TRANSACTION
EXECUTE sp_rename N'dbo.users.admin_mode', N'Tmp_external', 'COLUMN'
GO
EXECUTE sp_rename N'dbo.users.Tmp_external', N'external', 'COLUMN'
GO
DECLARE @v sql_variant
SET @v = N'Used to determine if this user is handled by Imcms instead of an external directory'
EXECUTE sp_addextendedproperty N'MS_Description', @v, N'user', N'dbo', N'table', N'users', N'column', N'external'
GO
COMMIT

-- 2003-06-13 Hasse och Kreiger

CREATE TABLE [dbo].[category_types] (
	[category_type_id] [int] NOT NULL ,
	[name] [varchar] (50) NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[categories] (
	[category_id] [int] NOT NULL ,
	[category_type_id] [int] NULL ,
	[name] [varchar] (50) NULL ,
	[description] [varchar] (500) NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[document_categories] (
	[meta_id] [int] NOT NULL ,
	[category_id] [int] NOT NULL
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[category_types] WITH NOCHECK ADD
	 PRIMARY KEY  CLUSTERED
	(
		[category_type_id]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[categories] WITH NOCHECK ADD
	 PRIMARY KEY  CLUSTERED
	(
		[category_id]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[document_categories] WITH NOCHECK ADD
	 PRIMARY KEY  CLUSTERED
	(
		[meta_id],
		[category_id]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[categories] ADD
	 FOREIGN KEY
	(
		[category_type_id]
	) REFERENCES [dbo].[category_types] (
		[category_type_id]
	)
GO

ALTER TABLE [dbo].[document_categories] ADD
	 FOREIGN KEY
	(
		[meta_id]
	) REFERENCES [dbo].[meta] (
		[meta_id]
	)
GO

-- 2003-10-20 Kreiger

ALTER TABLE [dbo].category_types ADD max_choices INT NOT NULL DEFAULT 0
GO

-- 2003-10-21 Kreiger

ALTER TABLE meta ADD publisher_id INT NULL

ALTER TABLE dbo.meta ADD CONSTRAINT
	FK_meta_users FOREIGN KEY
	(
	publisher_id
	) REFERENCES dbo.users
	(
	user_id
	)
GO

-- 2003-10-22 Hasse

BEGIN TRANSACTION
ALTER TABLE dbo.childs
	DROP CONSTRAINT FK_childs_meta1
GO
COMMIT
BEGIN TRANSACTION
CREATE TABLE dbo.Tmp_childs
	(
	meta_id int NOT NULL,
	to_meta_id int NOT NULL,
	menu_sort int NOT NULL,
	manual_sort_order int NOT NULL,
	tree_sort_index varchar(64) NOT NULL DEFAULT ''
	)  ON [PRIMARY]
GO
IF EXISTS(SELECT * FROM dbo.childs)
	 EXEC('INSERT INTO dbo.Tmp_childs (meta_id, to_meta_id, menu_sort, manual_sort_order)
		SELECT meta_id, to_meta_id, menu_sort, manual_sort_order FROM dbo.childs TABLOCKX')
GO
DROP TABLE dbo.childs
GO
EXECUTE sp_rename N'dbo.Tmp_childs', N'childs', 'OBJECT'
GO
CREATE CLUSTERED INDEX childs_meta_id ON dbo.childs
	(
	meta_id
	) ON [PRIMARY]
GO
ALTER TABLE dbo.childs ADD CONSTRAINT
	PK_childs PRIMARY KEY NONCLUSTERED
	(
	meta_id,
	to_meta_id,
	menu_sort
	) ON [PRIMARY]

GO
ALTER TABLE dbo.childs WITH NOCHECK ADD CONSTRAINT
	FK_childs_meta1 FOREIGN KEY
	(
	meta_id
	) REFERENCES dbo.meta
	(
	meta_id
	)
GO
COMMIT

-- 2003-10-29 Hasse / Kreiger
-- 1_7_4-RELEASE

print ' PLEASE NOTE !!!!! '
print ''
print 'You have to run the sql script "sprocs.sql" on imCMS database '
print ''

GO