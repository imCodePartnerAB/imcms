-- diff.sql
--

print' OBS!!!  Glöm inte att du MÅSTE köra hela sprocs.sql efter detta script vid uppgradering  OBS!!'

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

-- Insert default start document into the database.
SET IDENTITY_INSERT sys_types ON
INSERT INTO sys_types (type_id, name)
 VALUES(0, 'StartDocument')
SET IDENTITY_INSERT sys_types OFF
SET IDENTITY_INSERT sys_data ON
INSERT INTO sys_data (sys_id, type_id, value)
 VALUES(0, 0, 1001)
SET IDENTITY_INSERT sys_data OFF

--2002-07-29

-- Insert new user_agent values for browser
DELETE FROM browsers WHERE name = 'Windows Internet Explorer 5'
GO
DECLARE @temp int
SET @temp = (select max(browser_id) from browsers)
INSERT INTO browsers (browser_id, name, user_agent, value) VALUES (@temp +1, 'Windows Internet Explorer 5.0','%MSIE 5.0%win%',5)
INSERT INTO browsers (browser_id, name, user_agent, value) VALUES (@temp +2, 'Windows Internet Explorer 5.5','%MSIE 5.5%win%',5)
INSERT INTO browsers (browser_id, name, user_agent, value) VALUES (@temp +3, 'Internet Explorer 6','%MSIE 6%',4)
INSERT INTO browsers (browser_id, name, user_agent, value) VALUES (@temp +4, 'Windows Internet Explorer 6','%MSIE 6%win%',5)
INSERT INTO browsers (browser_id, name, user_agent, value) VALUES (@temp +5, 'Netscape 7','Mozilla%/5;%netscape/7%',4)
INSERT INTO browsers (browser_id, name, user_agent, value) VALUES (@temp +6, 'Windows Netscape 7','Mozilla/5%(%win%netscape/7%',5)

--2002-11-20
GO


CREATE TABLE [dbo].[readrunner_user_data] (
	[user_id] [int] NOT NULL ,
	[uses] [int] NULL ,
	[max_uses] [int] NULL ,
	[max_uses_warning_threshold] [int] NULL ,
	[expiry_date] [datetime] NULL ,
	[expiry_date_warning_threshold] [int] NULL ,
	[expiry_date_warning_sent] [int] NOT NULL
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[readrunner_user_data] WITH NOCHECK ADD
	 PRIMARY KEY  CLUSTERED
	(
		[user_id]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[readrunner_user_data] WITH NOCHECK ADD
	CONSTRAINT [DF_readrunner_user_data_expiry_date_warning_sent] DEFAULT (0) FOR [expiry_date_warning_sent]

-- 2002-12-16
GO


/*
  All user will have the role Users. Lets insert role ( Users ) for all user that
  not already have it.
*/
declare @role_id int
declare @user_id int
declare @count int

select @role_id = role_id from roles
where role_name like 'Users'

declare posCursor  Cursor scroll
for select user_id from users
open posCursor
fetch next from posCursor
into @user_id
while @@fetch_status = 0
begin
	select  @count = count(*) from user_roles_crossref where user_id = @user_id and role_id = @role_id
	if @count < 1 begin
		insert into user_roles_crossref
		values (@user_id, @role_id)
	end

	fetch next from posCursor
  	into @user_id
end
close posCursor
deallocate posCursor

-- 2002-12-18
GO



CREATE TABLE [dbo].[useradmin_role_crossref] (
	[user_id] [int] NOT NULL ,
	[role_id] [int] NOT NULL
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[useradmin_role_crossref] WITH NOCHECK ADD
	CONSTRAINT [PK_useradmin_role_crossref] PRIMARY KEY  CLUSTERED
	(
		[user_id],
		[role_id]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[useradmin_role_crossref] ADD
	CONSTRAINT [FK_useradmin_role_crossref_roles] FOREIGN KEY
	(
		[role_id]
	) REFERENCES [dbo].[roles] (
		[role_id]
	),
	CONSTRAINT [FK_useradmin_role_crossref_users] FOREIGN KEY
	(
		[user_id]
	) REFERENCES [dbo].[users] (
		[user_id]
	)

-- 2002-12-19
GO



/*
   den 9 januari 2003 17:44:40
   Application: MS SQLEM - Data Tools

   New column admin_role in table roles and
   one new role : Useradmin
   admin_role values: Superadmin = 1
   					  Useradmin = 2
*/


SET QUOTED_IDENTIFIER ON
BEGIN TRANSACTION


ALTER TABLE dbo.roles ADD
	admin_role int NOT NULL CONSTRAINT DF_roles_admin_role DEFAULT 0
GO
COMMIT

BEGIN TRANSACTION
	UPDATE roles set admin_role = 1
	WHERE role_id = 0

	DECLARE @maxId int
	SELECT @maxId = max(role_id) FROM roles

	INSERT INTO roles
	VALUES ( @maxId+1, 'Useradmin', 0, 2 )

GO
COMMIT
SET QUOTED_IDENTIFIER OFF

-- 2003-01-09


/*
	den 14 januari 2003 12:52:04
	Create table phonetypes and insert values
*/
if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[phonetypes]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[phonetypes]
GO

CREATE TABLE [dbo].[phonetypes] (
	[phonetype_id] [int] NOT NULL ,
	[typename] [varchar] (12) NOT NULL ,
	[lang_id] [int] NOT NULL
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[phonetypes] WITH NOCHECK ADD
	CONSTRAINT [PK_phonetypes] PRIMARY KEY  CLUSTERED
	(
		[phonetype_id],
		[lang_id]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[phonetypes] ADD
	CONSTRAINT [FK_phonetypes_lang_prefixes] FOREIGN KEY
	(
		[lang_id]
	) REFERENCES [dbo].[lang_prefixes] (
		[lang_id]
	)
GO


INSERT INTO phonetypes VALUES(0, 'Annat', 1 )
INSERT INTO phonetypes VALUES(1, 'Hem', 1 )
INSERT INTO phonetypes VALUES(2, 'Arbete', 1 )
INSERT INTO phonetypes VALUES(3, 'Mobil', 1 )
INSERT INTO phonetypes VALUES(4, 'Fax', 1 )
INSERT INTO phonetypes VALUES(0, 'Other', 2 )
INSERT INTO phonetypes VALUES(1, 'Home', 2 )
INSERT INTO phonetypes VALUES(2, 'Work', 2 )
INSERT INTO phonetypes VALUES(3, 'Mobile', 2 )
INSERT INTO phonetypes VALUES(4, 'Fax', 2 )
GO


/*
   den 14 januari 2003
   On table phones,
   add column phonetype_id int default 0 ( default typename = Other )
*/

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
ALTER TABLE dbo.phones ADD
	phonetype_id int NOT NULL CONSTRAINT DF_phones_phonetype_id DEFAULT 0
GO
COMMIT


/*
	On table phones,
	Change type for column phones.number från char(25) to varchar(25)
 */
BEGIN TRANSACTION
ALTER TABLE dbo.phones
	DROP CONSTRAINT FK_phones_users
GO
ALTER TABLE dbo.phones
	DROP CONSTRAINT DF_phones_phonetype_id
GO
CREATE TABLE dbo.Tmp_phones
	(
	phone_id int NOT NULL,
	country_code varchar(4) NOT NULL,
	area_code char(8)  NOT NULL,
	number varchar(25)  NOT NULL,
	user_id int NOT NULL,
	phonetype_id int NOT NULL
	)  ON [PRIMARY]
GO
ALTER TABLE dbo.Tmp_phones ADD CONSTRAINT
	DF_phones_phonetype_id DEFAULT (0) FOR phonetype_id
GO
IF EXISTS(SELECT * FROM dbo.phones)
	 EXEC('INSERT INTO dbo.Tmp_phones (phone_id, country_code, area_code, number, user_id, phonetype_id)
		SELECT phone_id, country_code, area_code, CONVERT(varchar(25), number), user_id, phonetype_id FROM dbo.phones TABLOCKX')
GO
DROP TABLE dbo.phones
GO
EXECUTE sp_rename N'dbo.Tmp_phones', N'phones', 'OBJECT'
GO
ALTER TABLE dbo.phones ADD CONSTRAINT
	PK_phones PRIMARY KEY NONCLUSTERED
	(
	phone_id,
	user_id
	) ON [PRIMARY]

GO
ALTER TABLE dbo.phones WITH NOCHECK ADD CONSTRAINT
	FK_phones_users FOREIGN KEY
	(
	user_id
	) REFERENCES dbo.users
	(
	user_id
	)
GO
COMMIT

/*
	On table phones,
	concat values in columns country_code, area_code, number and store result in column number
	then delete column country_code and area_code
*/


BEGIN TRANSACTION
declare @phone_id int
declare @country_code varchar(4)
declare @area_code varchar(8)
declare @number varchar(25)


declare posCursor  Cursor scroll
for select phone_id from phones
open posCursor
fetch next from posCursor
into @phone_id
while @@fetch_status = 0
begin
	select @country_code = rtrim(country_code), @area_code = rtrim(area_code), @number = rtrim(number)
	from phones
	where phone_id = @phone_id
	update phones set number = ltrim(ltrim(@country_code)+' '+ltrim(@area_code)+' '+ltrim(@number))
	where phone_id = @phone_id

	fetch next from posCursor
  	into @phone_id
end
close posCursor
deallocate posCursor
GO

ALTER TABLE dbo.phones
	DROP COLUMN country_code, area_code
GO
COMMIT

-- 2003-01-14



/*
   den 25 januari 2003 11:29:02
   User: sa
   Server: LENNART
   Database: allerannons
   Application: MS SQLEM - Data Tools
*/

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
ALTER TABLE dbo.users
	DROP CONSTRAINT DF_users_title
GO
ALTER TABLE dbo.users
	DROP CONSTRAINT DF_users_company
GO
ALTER TABLE dbo.users
	DROP CONSTRAINT DF_users_user_type
GO
ALTER TABLE dbo.users
	DROP CONSTRAINT DF_users_active
GO
CREATE TABLE dbo.Tmp_users
	(
	user_id int NOT NULL,
	login_name varchar(50) NOT NULL,
	login_password varchar(15) NOT NULL,
	first_name varchar(25) NOT NULL,
	last_name varchar(30) NOT NULL,
	title varchar(30) NOT NULL,
	company varchar(30) NOT NULL,
	address varchar(40) NOT NULL,
	city varchar(30) NOT NULL,
	zip varchar(15) NOT NULL,
	country varchar(30) NOT NULL,
	county_council varchar(30) NOT NULL,
	email varchar(50) NOT NULL,
	admin_mode int NOT NULL,
	last_page int NOT NULL,
	archive_mode int NOT NULL,
	lang_id int NOT NULL,
	user_type int NOT NULL,
	active int NOT NULL,
	create_date smalldatetime NOT NULL
	)  ON [PRIMARY]
GO
ALTER TABLE dbo.Tmp_users ADD CONSTRAINT
	DF_users_title DEFAULT ('') FOR title
GO
ALTER TABLE dbo.Tmp_users ADD CONSTRAINT
	DF_users_company DEFAULT ('') FOR company
GO
ALTER TABLE dbo.Tmp_users ADD CONSTRAINT
	DF_users_user_type DEFAULT (1) FOR user_type
GO
ALTER TABLE dbo.Tmp_users ADD CONSTRAINT
	DF_users_active DEFAULT (1) FOR active
GO
IF EXISTS(SELECT * FROM dbo.users)
	 EXEC('INSERT INTO dbo.Tmp_users (user_id, login_name, login_password, first_name, last_name, title, company, address, city, zip, country, county_council, email, admin_mode, last_page, archive_mode, lang_id, user_type, active, create_date)
		SELECT user_id, CONVERT(varchar(50), login_name), CONVERT(varchar(15), login_password), CONVERT(varchar(25), first_name), CONVERT(varchar(30), last_name), CONVERT(varchar(30), title), CONVERT(varchar(30), company), CONVERT(varchar(40), address), CONVERT(varchar(30), city), CONVERT(varchar(15), zip), CONVERT(varchar(30), country), CONVERT(varchar(30), county_council), CONVERT(varchar(50), email), admin_mode, last_page, archive_mode, lang_id, user_type, active, create_date FROM dbo.users TABLOCKX')
GO
ALTER TABLE dbo.useradmin_role_crossref
	DROP CONSTRAINT FK_useradmin_role_crossref_users
GO
ALTER TABLE dbo.phones
	DROP CONSTRAINT FK_phones_users
GO
ALTER TABLE dbo.user_rights
	DROP CONSTRAINT FK_user_rights_users
GO
ALTER TABLE dbo.user_roles_crossref
	DROP CONSTRAINT FK_user_roles_crossref_users
GO
DROP TABLE dbo.users
GO
EXECUTE sp_rename N'dbo.Tmp_users', N'users', 'OBJECT'
GO
ALTER TABLE dbo.users ADD CONSTRAINT
	PK_users PRIMARY KEY NONCLUSTERED
	(
	user_id
	) ON [PRIMARY]

GO
COMMIT
BEGIN TRANSACTION
ALTER TABLE dbo.user_roles_crossref WITH NOCHECK ADD CONSTRAINT
	FK_user_roles_crossref_users FOREIGN KEY
	(
	user_id
	) REFERENCES dbo.users
	(
	user_id
	)
GO
COMMIT
BEGIN TRANSACTION
ALTER TABLE dbo.user_rights WITH NOCHECK ADD CONSTRAINT
	FK_user_rights_users FOREIGN KEY
	(
	user_id
	) REFERENCES dbo.users
	(
	user_id
	)
GO
COMMIT
BEGIN TRANSACTION
ALTER TABLE dbo.phones WITH NOCHECK ADD CONSTRAINT
	FK_phones_users FOREIGN KEY
	(
	user_id
	) REFERENCES dbo.users
	(
	user_id
	)
GO
COMMIT
BEGIN TRANSACTION
ALTER TABLE dbo.useradmin_role_crossref WITH NOCHECK ADD CONSTRAINT
	FK_useradmin_role_crossref_users FOREIGN KEY
	(
	user_id
	) REFERENCES dbo.users
	(
	user_id
	)
GO
COMMIT
-- 2003-01-25

CREATE TABLE [dbo].[user_flags] (
	[user_flag_id] [int] NOT NULL ,
	[name] [varchar] (64) NOT NULL ,
	[type] [int] NOT NULL ,
	[description] [varchar] (256) NOT NULL ,
	CONSTRAINT [PK_user_flags] PRIMARY KEY  CLUSTERED
	(
		[user_flag_id]
	)  ON [PRIMARY] ,
	CONSTRAINT [IX_user_flags] UNIQUE  NONCLUSTERED
	(
		[name]
	)  ON [PRIMARY]
) ON [PRIMARY]
GO


CREATE TABLE [dbo].[user_flags_crossref] (
	[user_id] [int] NOT NULL ,
	[user_flag_id] [int] NOT NULL ,
	CONSTRAINT [PK_user_flags_crossref] PRIMARY KEY  CLUSTERED
	(
		[user_id],
		[user_flag_id]
	)  ON [PRIMARY] ,
	CONSTRAINT [FK_user_flags_crossref_user_flags] FOREIGN KEY
	(
		[user_flag_id]
	) REFERENCES [dbo].[user_flags] (
		[user_flag_id]
	),
	CONSTRAINT [FK_user_flags_crossref_users] FOREIGN KEY
	(
		[user_id]
	) REFERENCES [dbo].[users] (
		[user_id]
	)
) ON [PRIMARY]
GO

-- 2003-02-07 kreiger

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


--lets create new templategroup for Example-templates
declare @example_groupId int
select @example_groupId = max(group_id)+1 from templategroups

insert into templategroups(group_id, group_name)
values(@example_groupId,'Example-templates')


--lets add default example template and then connect it to a templategroup
declare @poll_form_templateId int
select @poll_form_templateId = max(template_id)+1 from templates
INSERT INTO templates ( template_id , template_name , simple_name , lang_prefix , no_of_txt , no_of_img , no_of_url )
	values (@poll_form_templateId,'poll_form_template.html','poll_form_template','se',0,0,0)
INSERT INTO templates_cref(group_id, template_id)
	values (@example_groupId, @poll_form_templateId)

--lets add default example template and then connect it to a templategroup
declare @poll_result_default_templateId int
select @poll_result_default_templateId = max(template_id)+1 from templates
INSERT INTO templates ( template_id , template_name , simple_name , lang_prefix , no_of_txt , no_of_img , no_of_url )
	values (@poll_result_default_templateId,'poll_result_default_template.html','poll_result_default_template','se',0,0,0)
INSERT INTO templates_cref(group_id, template_id)
	values (@example_groupId, @poll_result_default_templateId)

--lets add default example template and then connect it to a templategroup
declare @poll_confirmation_templateId int
select @poll_confirmation_templateId = max(template_id)+1 from templates
INSERT INTO templates ( template_id , template_name , simple_name , lang_prefix , no_of_txt , no_of_img , no_of_url )
	values (@poll_confirmation_templateId,'poll_confirmation_template.html','poll_confirmation_template','se',0,0,0)
INSERT INTO templates_cref(group_id, template_id)
	values (@example_groupId, @poll_confirmation_templateId)

print ' OBS !!!!! '
print 'Följande åtgärder behöver genomföras efter detta script '
print ''
print 'Kopiera poll templates från mappen /poll/templates till WEB-INF/templates/text'
print 'och byt namn på dem enligt följande:'
print 'poll_form_template.html  till ' + convert (varchar(5), @poll_form_templateId) + '.html'
print 'poll_result_default_template.html  till ' + convert (varchar(5), @poll_result_default_templateId) + '.html'
print 'poll_confirmation_template.html  till ' + convert (varchar(5), @poll_confirmation_templateId) + '.html'

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

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[AddPhoneNr]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[AddPhoneNr]
GO
-- 2003-11-28 Lennart

print ' OBS !!!!! '
print 'Följande åtgärder behöver genomföras efter detta script '
print ''
print '1. Du MÅSTE köra hela "sprocs.sql" som finns i "dist" katalogen'
print ''
print '2. Kopiera poll templates från mappen /poll/templates till WEB-INF/templates/text'
print 'och byt namn på dem enligt anvisningar, se tidigare print utskrift i detta resultat:'
GO

