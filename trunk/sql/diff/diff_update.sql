--Ett komplement till diffscriptet för riktigt gamla databaser
--För att kontrollera om detta script behöver köras kan ni kontrollera  
--meta tabellens meta_id, är satt som räknare behöver ni troligen inte köra scriptet
 

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
CREATE TABLE dbo.Tmp_meta
	(
	meta_id int NOT NULL IDENTITY (1, 1),
	description varchar(80) NOT NULL,
	doc_type int NOT NULL,
	meta_headline varchar(255) NOT NULL,
	meta_text varchar(1000) NOT NULL,
	meta_image varchar(255) NOT NULL,
	owner_id int NOT NULL,
	permissions int NOT NULL,
	shared int NOT NULL,
	expand int NOT NULL,
	show_meta int NOT NULL,
	help_text_id int NOT NULL,
	archive int NOT NULL,
	status_id int NOT NULL,
	lang_prefix varchar(3) NOT NULL,
	classification varchar(200) NOT NULL,
	date_created datetime NOT NULL,
	date_modified datetime NOT NULL,
	sort_position int NOT NULL,
	menu_position int NOT NULL,
	disable_search int NULL,
	target varchar(10) NULL,
	frame_name varchar(20) NULL,
	activate int NULL,
	activated_datetime datetime NULL,
	archived_datetime datetime NULL
	)  ON [PRIMARY]
GO
SET IDENTITY_INSERT dbo.Tmp_meta ON
GO
IF EXISTS(SELECT * FROM dbo.meta)
	 EXEC('INSERT INTO dbo.Tmp_meta (meta_id, description, doc_type, meta_headline, meta_text, meta_image, owner_id, permissions, shared, expand, show_meta, help_text_id, archive, status_id, lang_prefix, classification, date_created, date_modified, sort_position, menu_position, disable_search, target, frame_name, activate, activated_datetime, archived_datetime)
		SELECT meta_id, description, doc_type, meta_headline, meta_text, meta_image, owner_id, permissions, shared, expand, show_meta, help_text_id, archive, status_id, lang_prefix, classification, date_created, date_modified, sort_position, menu_position, disable_search, target, frame_name, activate, activated_datetime, archived_datetime FROM dbo.meta TABLOCKX')
GO
SET IDENTITY_INSERT dbo.Tmp_meta OFF
GO
ALTER TABLE dbo.browser_docs
	DROP CONSTRAINT FK_browser_docs_meta
GO
ALTER TABLE dbo.childs
	DROP CONSTRAINT FK_childs_meta1
GO
ALTER TABLE dbo.doc_permission_sets
	DROP CONSTRAINT FK_permission_sets_meta
GO
ALTER TABLE dbo.frameset_docs
	DROP CONSTRAINT FK_frameset_docs_meta
GO
ALTER TABLE dbo.images
	DROP CONSTRAINT FK_images_meta
GO
ALTER TABLE dbo.meta_classification
	DROP CONSTRAINT FK_meta_classification_meta
GO
ALTER TABLE dbo.new_doc_permission_sets
	DROP CONSTRAINT FK_new_doc_permission_sets_meta
GO
ALTER TABLE dbo.roles_rights
	DROP CONSTRAINT FK_roles_rights_meta
GO
ALTER TABLE dbo.text_docs
	DROP CONSTRAINT FK_text_docs_meta
GO
ALTER TABLE dbo.texts
	DROP CONSTRAINT FK_texts_meta
GO
ALTER TABLE dbo.url_docs
	DROP CONSTRAINT FK_url_docs_meta
GO
ALTER TABLE dbo.user_rights
	DROP CONSTRAINT FK_user_rights_meta
GO
DROP TABLE dbo.meta
GO
EXECUTE sp_rename N'dbo.Tmp_meta', N'meta', 'OBJECT'
GO
CREATE CLUSTERED INDEX meta_meta_id ON dbo.meta
	(
	meta_id,
	show_meta,
	activate
	) WITH FILLFACTOR = 90 ON [PRIMARY]
GO
ALTER TABLE dbo.meta ADD CONSTRAINT
	PK_meta PRIMARY KEY NONCLUSTERED 
	(
	meta_id
	) WITH FILLFACTOR = 90 ON [PRIMARY]

GO
COMMIT
BEGIN TRANSACTION
ALTER TABLE dbo.user_rights WITH NOCHECK ADD CONSTRAINT
	FK_user_rights_meta FOREIGN KEY
	(
	meta_id
	) REFERENCES dbo.meta
	(
	meta_id
	)
GO
COMMIT
BEGIN TRANSACTION
ALTER TABLE dbo.url_docs WITH NOCHECK ADD CONSTRAINT
	FK_url_docs_meta FOREIGN KEY
	(
	meta_id
	) REFERENCES dbo.meta
	(
	meta_id
	)
GO
COMMIT
BEGIN TRANSACTION
ALTER TABLE dbo.texts WITH NOCHECK ADD CONSTRAINT
	FK_texts_meta FOREIGN KEY
	(
	meta_id
	) REFERENCES dbo.meta
	(
	meta_id
	)
GO
COMMIT
BEGIN TRANSACTION
ALTER TABLE dbo.text_docs WITH NOCHECK ADD CONSTRAINT
	FK_text_docs_meta FOREIGN KEY
	(
	meta_id
	) REFERENCES dbo.meta
	(
	meta_id
	)
GO
COMMIT
BEGIN TRANSACTION
ALTER TABLE dbo.roles_rights WITH NOCHECK ADD CONSTRAINT
	FK_roles_rights_meta FOREIGN KEY
	(
	meta_id
	) REFERENCES dbo.meta
	(
	meta_id
	)
GO
COMMIT
BEGIN TRANSACTION
ALTER TABLE dbo.new_doc_permission_sets WITH NOCHECK ADD CONSTRAINT
	FK_new_doc_permission_sets_meta FOREIGN KEY
	(
	meta_id
	) REFERENCES dbo.meta
	(
	meta_id
	)
GO
COMMIT
BEGIN TRANSACTION
ALTER TABLE dbo.meta_classification WITH NOCHECK ADD CONSTRAINT
	FK_meta_classification_meta FOREIGN KEY
	(
	meta_id
	) REFERENCES dbo.meta
	(
	meta_id
	)
GO
COMMIT
BEGIN TRANSACTION
ALTER TABLE dbo.images WITH NOCHECK ADD CONSTRAINT
	FK_images_meta FOREIGN KEY
	(
	meta_id
	) REFERENCES dbo.meta
	(
	meta_id
	)
GO
COMMIT
BEGIN TRANSACTION
ALTER TABLE dbo.frameset_docs WITH NOCHECK ADD CONSTRAINT
	FK_frameset_docs_meta FOREIGN KEY
	(
	meta_id
	) REFERENCES dbo.meta
	(
	meta_id
	)
GO
COMMIT
BEGIN TRANSACTION
ALTER TABLE dbo.doc_permission_sets WITH NOCHECK ADD CONSTRAINT
	FK_permission_sets_meta FOREIGN KEY
	(
	meta_id
	) REFERENCES dbo.meta
	(
	meta_id
	)
GO
COMMIT
BEGIN TRANSACTION
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
BEGIN TRANSACTION
ALTER TABLE dbo.browser_docs WITH NOCHECK ADD CONSTRAINT
	FK_browser_docs_meta FOREIGN KEY
	(
	meta_id
	) REFERENCES dbo.meta
	(
	meta_id
	)
GO
COMMIT


--*******************************************************************************************
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
ALTER TABLE dbo.user_types ADD
	lang_prefix char(3) NOT NULL CONSTRAINT DF_user_types_lang_prefix DEFAULT 'se'
GO
COMMIT

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
ALTER TABLE dbo.user_types
	DROP CONSTRAINT PK_user_types
GO
ALTER TABLE dbo.user_types ADD CONSTRAINT
	PK_user_types PRIMARY KEY NONCLUSTERED 
	(
	user_type,
	lang_prefix
	) WITH FILLFACTOR = 90 ON [PRIMARY]

GO
COMMIT


INSERT INTO user_types (user_type,type_name,lang_prefix) VALUES(0,'Anonymous users','uk')
INSERT INTO user_types (user_type,type_name,lang_prefix) VALUES(1,'Authenticated users','uk')
INSERT INTO user_types (user_type,type_name,lang_prefix) VALUES(2,'Conference users','uk')

--*******************************************************************************************

CREATE TABLE [dbo].[includes] (
	[meta_id] [int] NOT NULL ,
	[include_id] [int] NOT NULL ,
	[included_meta_id] [int] NOT NULL 
) ON [PRIMARY]
GO
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
ALTER TABLE dbo.includes ADD CONSTRAINT
	PK_includes PRIMARY KEY CLUSTERED 
	(
	meta_id,
	include_id
	) ON [PRIMARY]

GO
COMMIT


--*******************************************************************************************

CREATE TABLE [dbo].[display_name] (
	[sort_by_id] [int] NOT NULL ,
	[lang_id] [int] NOT NULL ,
	[display_name] [varchar] (30) NOT NULL 
) ON [PRIMARY]
GO
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
ALTER TABLE dbo.display_name ADD CONSTRAINT
	PK_display_name PRIMARY KEY CLUSTERED 
	(
	sort_by_id,
	lang_id
	) ON [PRIMARY]

GO
COMMIT


INSERT INTO display_name ( sort_by_id, lang_id, display_name ) VALUES(1,1,'Rubrik')
INSERT INTO display_name ( sort_by_id, lang_id, display_name ) VALUES(1,2,'Meta headline')
INSERT INTO display_name ( sort_by_id, lang_id, display_name ) VALUES(2,1,'Meta ID')
INSERT INTO display_name ( sort_by_id, lang_id, display_name ) VALUES(2,2,'Meta ID')
INSERT INTO display_name ( sort_by_id, lang_id, display_name ) VALUES(3,1,'Dokument typ')
INSERT INTO display_name ( sort_by_id, lang_id, display_name ) VALUES(3,2,'Document type')
INSERT INTO display_name ( sort_by_id, lang_id, display_name ) VALUES(4,1,'Ändrat datum')
INSERT INTO display_name ( sort_by_id, lang_id, display_name ) VALUES(4,2,'Date modified')
INSERT INTO display_name ( sort_by_id, lang_id, display_name ) VALUES(5,1,'Skapat datum')
INSERT INTO display_name ( sort_by_id, lang_id, display_name ) VALUES(5,2,'Date created')
INSERT INTO display_name ( sort_by_id, lang_id, display_name ) VALUES(6,1,'Arkiverat datum')
INSERT INTO display_name ( sort_by_id, lang_id, display_name ) VALUES(6,2,'Archived date')
INSERT INTO display_name ( sort_by_id, lang_id, display_name ) VALUES(7,1,'Aktiverat datum')
INSERT INTO display_name ( sort_by_id, lang_id, display_name ) VALUES(7,2,'Activated date')

--********************************************************************************************

CREATE TABLE [dbo].[Results] (
	[user_type] [int] NOT NULL ,
	[type_name] [char] (30) NULL ,
	[lang_prefix] [char] (3) NOT NULL 
) ON [PRIMARY]
GO

--********************************************************************************************
CREATE TABLE [dbo].[sort_by] (
	[sort_by_id] [int] IDENTITY (1, 1) NOT NULL ,
	[sort_by_type] [varchar] (30) NOT NULL 
) ON [PRIMARY]
GO

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
ALTER TABLE dbo.sort_by ADD CONSTRAINT
	PK_sort_by PRIMARY KEY CLUSTERED 
	(
	sort_by_id
	) ON [PRIMARY]

GO
COMMIT

SET IDENTITY_INSERT SORT_BY ON
INSERT INTO SORT_BY ( sort_by_id, sort_by_type ) VALUES (1, 'meta_headline')
INSERT INTO SORT_BY ( sort_by_id, sort_by_type ) VALUES (2, 'meta_id')
INSERT INTO SORT_BY ( sort_by_id, sort_by_type ) VALUES (3, 'doc_type')
INSERT INTO SORT_BY ( sort_by_id, sort_by_type ) VALUES (4, 'date_modified')
INSERT INTO SORT_BY ( sort_by_id, sort_by_type ) VALUES (5, 'date_created')
INSERT INTO SORT_BY ( sort_by_id, sort_by_type ) VALUES (6, 'date_archived')
INSERT INTO SORT_BY ( sort_by_id, sort_by_type ) VALUES (7, 'date_activated')
INSERT INTO SORT_BY ( sort_by_id, sort_by_type ) VALUES (8, 'meta_text')
INSERT INTO SORT_BY ( sort_by_id, sort_by_type ) VALUES (9, 'archive')
SET IDENTITY_INSERT SORT_BY OFF


--********************************************************************************************
