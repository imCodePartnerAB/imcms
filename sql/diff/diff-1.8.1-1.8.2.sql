-- Diff from 1_8_1-RELEASE up to 1_8_2-RELEASE

CREATE PROCEDURE MSSQL_DropConstraintLike @table VARCHAR(255), @like VARCHAR(255) AS

DECLARE @constraint VARCHAR(255)

SELECT @constraint = constraints.name
FROM sysconstraints c, sysobjects constraints, sysobjects tables
WHERE c.constid = constraints.id AND c.id = tables.id
	AND tables.name = @table
	AND constraints.name LIKE @like

EXEC ('ALTER TABLE '+@table+' DROP CONSTRAINT '+@constraint)
GO

update  languages set lang_prefix = 'swe' where lang_prefix = 'se'
update  languages set user_prefix = 'swe' where user_prefix = 'se'
update  languages set lang_prefix = 'eng' where lang_prefix = 'en'
update  languages set user_prefix = 'eng' where user_prefix = 'en'

update lang_prefixes set lang_prefix ='swe' where lang_prefix = 'se'
update lang_prefixes set lang_prefix ='eng' where lang_prefix = 'en'

update doc_types set lang_prefix ='swe' where lang_prefix = 'se'
update doc_types set lang_prefix ='eng' where lang_prefix = 'en'

update user_types set lang_prefix = 'swe' where lang_prefix = 'se'
update user_types set lang_prefix = 'eng' where lang_prefix = 'en'

update doc_permissions set lang_prefix = 'swe' where lang_prefix = 'se'
update doc_permissions set lang_prefix = 'eng' where lang_prefix = 'en'

update permissions set lang_prefix = 'swe' where lang_prefix = 'se'
update permissions set lang_prefix = 'eng' where lang_prefix = 'en'

update roles_permissions set lang_prefix = 'swe' where lang_prefix = 'se'
update roles_permissions set lang_prefix = 'eng' where lang_prefix = 'en'

update mime_types set lang_prefix = 'swe' where lang_prefix = 'se'
update mime_types set lang_prefix = 'eng' where lang_prefix = 'en'

-- 2004-01-20 / Lennart Å

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[readrunner_user_data]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[readrunner_user_data]
GO

-- 2004-01-21  /Hasse

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[getfilename]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetFileName]
GO

-- 2004-02-11 /Hasse

BEGIN TRANSACTION
SET QUOTED_IDENTIFIER ON
COMMIT
BEGIN TRANSACTION
ALTER TABLE dbo.categories ADD
	image varchar(255) NOT NULL DEFAULT ''
GO
COMMIT

-- 2004-02-23 / Lennart -  Add new column for path to category icon

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
ALTER TABLE dbo.meta
	DROP CONSTRAINT FK_meta_users
GO
COMMIT
BEGIN TRANSACTION
CREATE TABLE dbo.Tmp_meta
	(
	meta_id int NOT NULL IDENTITY (1, 1),
	description varchar(80) NOT NULL,
	doc_type int NOT NULL,
	meta_headline nvarchar(255) NOT NULL,
	meta_text nvarchar(1000) NOT NULL,
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
	disable_search int NOT NULL,
	target nvarchar(50) NOT NULL,
	frame_name varchar(20) NOT NULL,
	activate int NOT NULL,
	activated_datetime datetime NULL,
	archived_datetime datetime NULL,
	publisher_id int NULL
	)  ON [PRIMARY]
GO
SET IDENTITY_INSERT dbo.Tmp_meta ON
GO
IF EXISTS(SELECT * FROM dbo.meta)
	 EXEC('INSERT INTO dbo.Tmp_meta (meta_id, description, doc_type, meta_headline, meta_text, meta_image, owner_id, permissions, shared, expand, show_meta, help_text_id, archive, status_id, lang_prefix, classification, date_created, date_modified, sort_position, menu_position, disable_search, target, frame_name, activate, activated_datetime, archived_datetime, publisher_id)
		SELECT meta_id, description, doc_type, meta_headline, meta_text, meta_image, owner_id, permissions, shared, expand, show_meta, help_text_id, archive, status_id, lang_prefix, classification, date_created, date_modified, sort_position, menu_position, disable_search, target, frame_name, activate, activated_datetime, archived_datetime, publisher_id FROM dbo.meta TABLOCKX')
GO
SET IDENTITY_INSERT dbo.Tmp_meta OFF
GO
ALTER TABLE dbo.meta_classification
	DROP CONSTRAINT FK_meta_classification_meta
GO
ALTER TABLE dbo.meta_section
	DROP CONSTRAINT FK_meta_section_meta
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
ALTER TABLE dbo.browser_docs
	DROP CONSTRAINT FK_browser_docs_meta
GO
ALTER TABLE dbo.childs
	DROP CONSTRAINT FK_childs_meta1
GO
ALTER TABLE dbo.doc_permission_sets
	DROP CONSTRAINT FK_permission_sets_meta
GO
EXECUTE MSSQL_DropConstraintLike 'document_categories', 'FK__document___meta___%'
GO
ALTER TABLE dbo.frameset_docs
	DROP CONSTRAINT FK_frameset_docs_meta
GO
ALTER TABLE dbo.images
	DROP CONSTRAINT FK_images_meta
GO
ALTER TABLE dbo.includes
	DROP CONSTRAINT FK_includes_meta1
GO
ALTER TABLE dbo.includes
	DROP CONSTRAINT FK_includes_meta
GO
DROP TABLE dbo.meta
GO
EXECUTE sp_rename N'dbo.Tmp_meta', N'meta', 'OBJECT'
GO
CREATE UNIQUE CLUSTERED INDEX meta_meta_id ON dbo.meta
	(
	meta_id,
	show_meta,
	activate
	) ON [PRIMARY]
GO
ALTER TABLE dbo.meta ADD CONSTRAINT
	PK_meta PRIMARY KEY NONCLUSTERED
	(
	meta_id
	) ON [PRIMARY]

GO
ALTER TABLE dbo.meta WITH NOCHECK ADD CONSTRAINT
	FK_meta_users FOREIGN KEY
	(
	publisher_id
	) REFERENCES dbo.users
	(
	user_id
	)
GO
COMMIT
BEGIN TRANSACTION
ALTER TABLE dbo.includes WITH NOCHECK ADD CONSTRAINT
	FK_includes_meta1 FOREIGN KEY
	(
	included_meta_id
	) REFERENCES dbo.meta
	(
	meta_id
	)
GO
ALTER TABLE dbo.includes WITH NOCHECK ADD CONSTRAINT
	FK_includes_meta FOREIGN KEY
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
ALTER TABLE dbo.document_categories WITH NOCHECK ADD CONSTRAINT
	FK__document_categories_meta FOREIGN KEY
	(
	meta_id
	) REFERENCES dbo.meta
	(
	meta_id
	)
GO
COMMIT
BEGIN TRANSACTION
ALTER TABLE [dbo].[document_categories] ADD CONSTRAINT
	[FK_document_categories_categories] FOREIGN KEY
	(
		[category_id]
	) REFERENCES [dbo].[categories] (
		[category_id]
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
ALTER TABLE dbo.meta_section WITH NOCHECK ADD CONSTRAINT
	FK_meta_section_meta FOREIGN KEY
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

-- 2004-02-26 /Hasse

DELETE FROM browsers WHERE browser_id = 0
GO

-- 2004-03-01 Kreiger

UPDATE meta SET archived_datetime = GETDATE() WHERE archive = 1 AND (archived_datetime IS NULL OR GETDATE() < archived_datetime )
GO

ALTER TABLE meta ADD
        status INT NOT NULL DEFAULT 0,
        publication_start_datetime DATETIME NULL,
        publication_end_datetime DATETIME NULL
GO

UPDATE meta SET status = 2
UPDATE meta SET publication_start_datetime = ISNULL(activated_datetime, date_created)
GO

ALTER TABLE meta DROP
        COLUMN description,
        COLUMN expand,
        COLUMN help_text_id,
        COLUMN archive,
        COLUMN classification,
        COLUMN sort_position,
        COLUMN menu_position,
        COLUMN frame_name,
        COLUMN status_id,
        COLUMN activated_datetime
GO

-- 2004-03-03 Kreiger

DROP PROCEDURE AddBrowserStatistics
DROP PROCEDURE AddScreenStatistics
DROP PROCEDURE AddStatistics
DROP PROCEDURE AddStatisticsCount
DROP PROCEDURE AddVersionStatistics
DROP PROCEDURE CheckExistsInMenu
DROP PROCEDURE Classification_Get_All
DROP PROCEDURE Classification_convert
DROP PROCEDURE Classification_fix
DROP PROCEDURE ClassificationAdd
DROP PROCEDURE CountDocByType
DROP PROCEDURE DelUser
DROP PROCEDURE ExistingDocsGetSelectedMetaIds
DROP PROCEDURE GetLangPrefix
DROP PROCEDURE GetLanguages
DROP PROCEDURE GetMetaPathInfo
DROP PROCEDURE GetNoOfTemplates
DROP PROCEDURE GetTextNumber
DROP PROCEDURE GetUserCreateDate
DROP PROCEDURE GetUserId
DROP PROCEDURE SearchDocs
DROP PROCEDURE SearchDocsIndex

-- 2004-03-05 Kreiger
-- 1_8_2-RELEASE

print ' PLEASE NOTE !!!!! '
print ''
print 'You have to run the sql script "imcms-sprocs-1.8.2.sql" on imCMS database '
print ''
GO