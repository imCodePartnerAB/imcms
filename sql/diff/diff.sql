-- diff.sql
-- 


-- drop old procedure
if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[CheckDocSharePermissionForUser]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [CheckDocSharePermissionForUser]


SET QUOTED_IDENTIFIER  OFF    
SET ANSI_NULLS  OFF 
GO

-- Add columns for default-templates to text-docs.

alter table text_docs 
add default_template_1 INT DEFAULT -1 NOT NULL

alter table text_docs 
add default_template_2 INT DEFAULT -1 NOT NULL

GO
SET QUOTED_IDENTIFIER OFF 
SET ANSI_NULLS ON 
GO
-- 2001-09-26


INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(1048576,2,'se','Ändra include')
INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(1048576,2,'uk','Change include')

-- 2001-10-08


-- Add proper fields for activated and archived date-times. 
ALTER TABLE meta ADD activated_datetime DATETIME 
ALTER TABLE meta ADD archived_datetime DATETIME  

-- Migrate the old ugly fields to the new nice ones 
UPDATE meta SET activated_datetime = NULLIF(activated_date+' '+activated_time,'') 
UPDATE meta SET archived_datetime = NULLIF(archived_date+' '+archived_time,'')  

-- Drop the old bastards. 
ALTER TABLE meta DROP COLUMN activated_date 
ALTER TABLE meta DROP COLUMN activated_time 
ALTER TABLE meta DROP COLUMN archived_date 
ALTER TABLE meta DROP COLUMN archived_time 
 
GO 

-- 2001-11-15
--here is the release of v1_5_0-pre8



--this is the stuff needed to create full-text index on the colums we want to search on
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
ALTER TABLE dbo.texts
	DROP CONSTRAINT FK_texts_meta
COMMIT

BEGIN TRANSACTION
CREATE TABLE dbo.Tmp_texts
	(
	meta_id int NOT NULL,
	name int NOT NULL,
	text text NOT NULL,
	type int NULL,
	counter int NOT NULL IDENTITY (1, 1)
	)  ON [PRIMARY]
	 TEXTIMAGE_ON [PRIMARY]
GO
SET IDENTITY_INSERT dbo.Tmp_texts OFF
GO
IF EXISTS(SELECT * FROM dbo.texts)
	 EXEC('INSERT INTO dbo.Tmp_texts (meta_id, name, text, type)
		SELECT meta_id, name, text, type FROM dbo.texts TABLOCKX')
GO
DROP TABLE dbo.texts
GO

EXECUTE sp_rename N'dbo.Tmp_texts', N'texts', 'OBJECT'
GO

CREATE NONCLUSTERED INDEX IX_texts ON dbo.texts
	(
	meta_id
	) ON [PRIMARY]
GO
ALTER TABLE dbo.texts ADD CONSTRAINT
	PK_texts PRIMARY KEY CLUSTERED 
	(
	counter
	) ON [PRIMARY]

GO
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


-- 2001-12-20
--end of v1_5_0-pre10



--this will create 2 tables to handle sections
--*** *** start of creating tables *** ***
CREATE TABLE [meta_section] (
	[meta_id] [int] NOT NULL ,
	[section_id] [int] NOT NULL 
) ON [PRIMARY]
GO

ALTER TABLE [meta_section] WITH NOCHECK ADD 
	CONSTRAINT [PK_meta_section] PRIMARY KEY  CLUSTERED 
	(
		[meta_id],
		[section_id]
	)  ON [PRIMARY] 
GO

CREATE TABLE [sections] (
	[section_id] [int] IDENTITY (1, 1) NOT NULL ,
	[section_name] [varchar] (50)  NOT NULL 
) ON [PRIMARY]
GO

ALTER TABLE [sections] WITH NOCHECK ADD 
	CONSTRAINT [PK_section] PRIMARY KEY  CLUSTERED 
	(
		[section_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [meta_section] ADD 
	CONSTRAINT [FK_meta_section_meta] FOREIGN KEY 
	(
		[meta_id]
	) REFERENCES [meta] (
		[meta_id]
	),
	CONSTRAINT [FK_meta_section_section] FOREIGN KEY 
	(
		[section_id]
	) REFERENCES [sections] (
		[section_id]
	)
GO
--*** *** end of creating tables *** ***
--2002-02-01


-- The following changes the column 'text' in the table 'texts' from type 'text' to type 'ntext'.

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
ALTER TABLE dbo.texts 	
DROP CONSTRAINT FK_texts_meta 
COMMIT 

BEGIN TRANSACTION 
CREATE TABLE dbo.Tmp_texts ( meta_id int NOT NULL,
				name int NOT NULL, 	
				text ntext NOT NULL, 	
				type int NULL, 	
				counter int NOT NULL IDENTITY (1, 1) 	) 
				ON [PRIMARY]
			 	TEXTIMAGE_ON [PRIMARY] 
SET IDENTITY_INSERT dbo.Tmp_texts ON 

IF EXISTS(SELECT * FROM dbo.texts)
 	EXEC('INSERT INTO dbo.Tmp_texts (meta_id, name, text, type, counter)
 		SELECT meta_id, name, text, type, counter FROM dbo.texts TABLOCKX') 
	SET IDENTITY_INSERT dbo.Tmp_texts OFF 
	DROP TABLE dbo.texts 
	EXECUTE sp_rename N'dbo.Tmp_texts', N'texts', 'OBJECT' 
	ALTER TABLE dbo.texts 
		ADD CONSTRAINT 	PK_texts PRIMARY KEY CLUSTERED ( counter ) ON [PRIMARY]  
		CREATE NONCLUSTERED INDEX IX_texts ON dbo.texts ( meta_id ) ON [PRIMARY] 
	
	ALTER TABLE dbo.texts 
		WITH NOCHECK ADD CONSTRAINT FK_texts_meta 
		FOREIGN KEY ( meta_id ) 
		REFERENCES dbo.meta ( meta_id )
COMMIT 
--
-- 2002-02-08
--

--renamed all the procedures to handle the section stuff

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[add_section]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[add_section]
GO


if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[add_section_crossref]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[add_section_crossref]
GO


if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[change_and_delete_section_crossref]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[change_and_delete_section_crossref]
GO


if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[change_section_name]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[change_section_name]
GO


if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[get_sections_count]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[get_sections_count]
GO


if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[delete_section]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[delete_section]
GO


if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[get_all_sections]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[get_all_sections]
GO


if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[get_all_sections_count]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[get_all_sections_count]
GO


if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[get_inherit_section_id]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[get_inherit_section_id]
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

--2002-02-11


-- set type=1 (html) on every helptext to have html-escaped on the way out from db
GO
update texts 
set type = 1
where meta_id < 1001
GO
--2002-02-14


-- Changed the lang prefix uk to en
update roles_permissions 
set lang_prefix='en'
where lang_prefix='uk'

update doc_permissions 
set lang_prefix='en'
where lang_prefix='uk'

update permissions 
set lang_prefix='en'
where lang_prefix='uk'

update doc_types 
set lang_prefix='en'
where lang_prefix='uk'

update user_types 
set lang_prefix='en'
where lang_prefix='uk'

update languages 
set lang_prefix='en'
where lang_prefix='uk'

update languages 
set user_prefix='en'
where user_prefix='uk'
GO
-- 2002-03-07


--
-- Change primary key of table mime_types from
-- mime_id, to mime_id and lang_prefix combined.
-- 2002-03-12
--

 -- Drop the primary key
ALTER TABLE mime_types DROP CONSTRAINT PK_mime_types
GO

 -- Add a new primary key
ALTER TABLE mime_types ADD CONSTRAINT PK_mime_types PRIMARY KEY NONCLUSTERED ( mime_id, lang_prefix )
GO



--Update mime_types 
--
DELETE FROM mime_types WHERE mime_id < 17

SET IDENTITY_INSERT mime_types ON
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(0,'Annan... (Skriv in korrekt mime-typ nedan.)','other','se')
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(1,'Vanlig text (text/plain)','text/plain','se')
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(2,'HTML-dokument (text/html)','text/html','se')
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(3,'Binärfil (application/octet-stream)','application/octet-stream','se')
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(4,'Shockwave Flash (application/x-shockwave-flash)','application/x-shockwave-flash','se')
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(5,'Shockwave Director (application/x-director)','application/x-director','se')
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(6,'PNG-bild (image/png)','image/png','se')
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(7,'GIF-bild (image/gif)','image/gif','se')
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(8,'JPEG-bild (image/gif)','image/jpeg','se')
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(9,'Adobe Acrobat-dokument (application/pdf)','application/pdf','se')
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(10,'Wav-ljud (audio/x-wav)','audio/x-wav','se')
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(11,'Zip-fil (application/zip)','application/zip','se')
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(12,'AVI-film (video/x-msvideo)','video/x-msvideo','se')
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(13,'Quicktime-film (video/quicktime)','video/quicktime','se')
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(14,'MPEG-film (video/mpeg)','video/mpeg','se')
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(15,'MS Word-dokument (application/msword)','application/msword','se')
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(16,'MS Excel-dokument (application/vnd.ms-excel)','application/vnd.ms-excel','se')
SET IDENTITY_INSERT mime_types OFF

SET IDENTITY_INSERT mime_types ON
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(0,'Other... (Type correct mime-type below.)','other','en')
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(1,'Plain text (text/plain)','text/plain','en')
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(2,'HTML-document (text/html)','text/html','en')
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(3,'Binary file (application/octet-stream)','application/octet-stream','en')
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(4,'Shockwave Flash (application/x-shockwave-flash)','application/x-shockwave-flash','en')
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(5,'Shockwave Director (application/x-director)','application/x-director','en')
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(6,'PNG-image (image/png)','image/png','en')
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(7,'GIF-image (image/gif)','image/gif','en')
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(8,'JPEG-image (image/jpeg)','image/jpeg','en')
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(9,'Adobe Acrobat-document (application/pdf)','application/pdf','en')
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(10,'Wav-sound (audio/x-wav)','audio/x-wav','en')
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(11,'Zip-file (application/zip)','application/zip','en')
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(12,'AVI-movie (video/x-msvideo)','video/x-msvideo','en')
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(13,'Quicktime-movie (video/quicktime)','video/quicktime','en')
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(14,'MPEG-movie (video/mpeg)','video/mpeg','en')
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(15,'MS Word-document (application/msword)','application/msword','en')
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(16,'MS Excel-document (application/vnd.ms-excel)','application/vnd.ms-excel','en')
SET IDENTITY_INSERT mime_types OFF

--2002-03-26


--Enable full-text searching in the database.
EXEC sp_fulltext_database 'enable'
GO
-- Create a new full-text catalog.
EXEC sp_fulltext_catalog 'full_text_index',
			'create'
GO
--Register the new tales and colums in it
--then activate the table
--classification-table
EXEC sp_fulltext_table  'classification',
			'create',
			'full_text_index',
			'PK_classification'
EXEC sp_fulltext_column 'classification',
			'code',
			'add',
			0x041d
EXEC sp_fulltext_table  'classification',
			'activate'
GO
--meta-table
EXEC sp_fulltext_table  'meta',
			'create',
			'full_text_index',
			'PK_meta'
EXEC sp_fulltext_column 'meta',
			'meta_headline',
			'add',
			0x041d
EXEC sp_fulltext_column 'meta',
			'meta_text',
			'add',
			0x041d
EXEC sp_fulltext_table  'meta',
			'activate'
GO
--texts-table
EXEC sp_fulltext_table  'texts',
			'create',
			'full_text_index',
			'PK_texts'
EXEC sp_fulltext_column 'texts',
			'text',
			'add',
			0x041d
EXEC sp_fulltext_table  'texts',
			'activate'
GO

--start the population
EXEC sp_fulltext_catalog 'full_text_index',
			 'start_full'
while (SELECT fulltextcatalogproperty('full_text_index','populatestatus')) <> 0
	BEGIN
	WAITFOR DELAY '00:00:02'
	CONTINUE
	END
GO

--start the trace of changes
--classification table
EXEC sp_fulltext_table classification, 'Start_change_tracking'
EXEC sp_fulltext_table classification, 'Start_background_updateindex'
Go
--meta-table
EXEC sp_fulltext_table meta, 'Start_change_tracking'
EXEC sp_fulltext_table meta, 'Start_background_updateindex'
GO
-- texts-table
EXEC sp_fulltext_table texts, 'Start_change_tracking'
EXEC sp_fulltext_table texts, 'Start_background_updateindex'

GO
--2002-04-04

print' OBS!!!  Glöm inte att du MÅSTE köra hela sprocs.sql efter detta script vid uppgradering  OBS!!'



