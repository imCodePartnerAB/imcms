ALTER TABLE [dbo].[meta_classification] DROP CONSTRAINT FK_meta_classification_classification
GO

ALTER TABLE [dbo].[doc_permission_sets_ex] DROP CONSTRAINT FK_doc_permission_sets_ex_doc_permission_sets1
GO

ALTER TABLE [dbo].[display_name] DROP CONSTRAINT FK_display_name_lang_prefixes
GO

ALTER TABLE [dbo].[browser_docs] DROP CONSTRAINT FK_browser_docs_meta
GO

ALTER TABLE [dbo].[childs] DROP CONSTRAINT FK_childs_meta1
GO

ALTER TABLE [dbo].[doc_permission_sets] DROP CONSTRAINT FK_permission_sets_meta
GO

ALTER TABLE [dbo].[frameset_docs] DROP CONSTRAINT FK_frameset_docs_meta
GO

ALTER TABLE [dbo].[images] DROP CONSTRAINT FK_images_meta
GO

ALTER TABLE [dbo].[includes] DROP CONSTRAINT FK_includes_meta
GO

ALTER TABLE [dbo].[includes] DROP CONSTRAINT FK_includes_meta1
GO

ALTER TABLE [dbo].[meta_classification] DROP CONSTRAINT FK_meta_classification_meta
GO

ALTER TABLE [dbo].[new_doc_permission_sets] DROP CONSTRAINT FK_new_doc_permission_sets_meta
GO

ALTER TABLE [dbo].[roles_rights] DROP CONSTRAINT FK_roles_rights_meta
GO

ALTER TABLE [dbo].[text_docs] DROP CONSTRAINT FK_text_docs_meta
GO

ALTER TABLE [dbo].[texts] DROP CONSTRAINT FK_texts_meta
GO

ALTER TABLE [dbo].[url_docs] DROP CONSTRAINT FK_url_docs_meta
GO

ALTER TABLE [dbo].[user_rights] DROP CONSTRAINT FK_user_rights_meta
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

ALTER TABLE [dbo].[roles_rights] DROP CONSTRAINT FK_roles_rights_roles
GO

ALTER TABLE [dbo].[user_roles_crossref] DROP CONSTRAINT FK_user_roles_crossref_roles
GO

ALTER TABLE [dbo].[display_name] DROP CONSTRAINT FK_display_name_sort_by
GO

ALTER TABLE [dbo].[sys_data] DROP CONSTRAINT FK_sys_data_sys_types
GO

ALTER TABLE [dbo].[templates_cref] DROP CONSTRAINT FK_templates_cref_templategroups
GO

ALTER TABLE [dbo].[templates_cref] DROP CONSTRAINT FK_templates_cref_templates
GO

ALTER TABLE [dbo].[text_docs] DROP CONSTRAINT FK_text_docs_templates
GO

ALTER TABLE [dbo].[phones] DROP CONSTRAINT FK_phones_users
GO

ALTER TABLE [dbo].[user_rights] DROP CONSTRAINT FK_user_rights_users
GO

ALTER TABLE [dbo].[user_roles_crossref] DROP CONSTRAINT FK_user_roles_crossref_users
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[browser_docs]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[browser_docs]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[browsers]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[browsers]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[childs]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[childs]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[classification]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[classification]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[display_name]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[display_name]
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

if exists (select * from sysobjects where id = object_id(N'[dbo].[doc_types]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[doc_types]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[fileupload_docs]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[fileupload_docs]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[frameset_docs]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[frameset_docs]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[images]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[images]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[includes]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[includes]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[ip_accesses]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[ip_accesses]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[lang_prefixes]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[lang_prefixes]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[languages]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[languages]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[main_log]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[main_log]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[meta]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[meta]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[meta_classification]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[meta_classification]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[mime_types]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[mime_types]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[new_doc_permission_sets]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[new_doc_permission_sets]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[new_doc_permission_sets_ex]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[new_doc_permission_sets_ex]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[permission_sets]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[permission_sets]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[permissions]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[permissions]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[phones]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[phones]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[roles]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[roles]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[roles_permissions]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[roles_permissions]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[roles_rights]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[roles_rights]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[sort_by]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[sort_by]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[stats]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[stats]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[sys_data]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[sys_data]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[sys_types]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[sys_types]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[templategroups]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[templategroups]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[templates]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[templates]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[templates_cref]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[templates_cref]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[text_docs]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[text_docs]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[texts]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[texts]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[track_log]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[track_log]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[url_docs]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[url_docs]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[user_rights]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[user_rights]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[user_roles_crossref]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[user_roles_crossref]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[user_types]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[user_types]
GO

if exists (select * from sysobjects where id = object_id(N'[dbo].[users]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[users]
GO

CREATE TABLE [dbo].[browser_docs] (
	[meta_id] [int] NOT NULL ,
	[to_meta_id] [int] NOT NULL ,
	[browser_id] [int] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[browsers] (
	[browser_id] [int] NOT NULL ,
	[name] [varchar] (50) NOT NULL ,
	[user_agent] [varchar] (50) NOT NULL ,
	[value] [tinyint] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[childs] (
	[meta_id] [int] NOT NULL ,
	[to_meta_id] [int] NOT NULL ,
	[menu_sort] [int] NOT NULL ,
	[manual_sort_order] [int] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[classification] (
	[class_id] [int] IDENTITY (1, 1) NOT NULL ,
	[code] [varchar] (30) NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[display_name] (
	[sort_by_id] [int] NOT NULL ,
	[lang_id] [int] NOT NULL ,
	[display_name] [varchar] (30) NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[doc_permission_sets] (
	[meta_id] [int] NOT NULL ,
	[set_id] [int] NOT NULL ,
	[permission_id] [int] NOT NULL 
) ON [PRIMARY]
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
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[doc_types] (
	[doc_type] [int] NOT NULL ,
	[lang_prefix] [varchar] (3) NOT NULL ,
	[type] [varchar] (50) NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[fileupload_docs] (
	[meta_id] [int] NOT NULL ,
	[filename] [varchar] (50) NOT NULL ,
	[mime] [varchar] (50) NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[frameset_docs] (
	[meta_id] [int] NOT NULL ,
	[frame_set] [text] NULL 
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

CREATE TABLE [dbo].[images] (
	[meta_id] [int] NOT NULL ,
	[width] [int] NOT NULL ,
	[height] [int] NOT NULL ,
	[border] [int] NOT NULL ,
	[v_space] [int] NOT NULL ,
	[h_space] [int] NOT NULL ,
	[name] [int] NOT NULL ,
	[image_name] [varchar] (40) NOT NULL ,
	[target] [varchar] (15) NOT NULL ,
	[target_name] [varchar] (80) NOT NULL ,
	[align] [varchar] (15) NOT NULL ,
	[alt_text] [varchar] (255) NOT NULL ,
	[low_scr] [varchar] (255) NOT NULL ,
	[imgurl] [varchar] (255) NOT NULL ,
	[linkurl] [varchar] (255) NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[includes] (
	[meta_id] [int] NOT NULL ,
	[include_id] [int] NOT NULL ,
	[included_meta_id] [int] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[ip_accesses] (
	[ip_access_id] [int] IDENTITY (1, 1) NOT NULL ,
	[user_id] [int] NOT NULL ,
	[ip_start] [decimal](18, 0) NOT NULL ,
	[ip_end] [decimal](18, 0) NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[lang_prefixes] (
	[lang_id] [int] NOT NULL ,
	[lang_prefix] [char] (3) NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[languages] (
	[lang_prefix] [varchar] (3) NOT NULL ,
	[user_prefix] [varchar] (3) NOT NULL ,
	[language] [varchar] (30) NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[main_log] (
	[log_datetime] [datetime] NULL ,
	[event] [varchar] (255) NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[meta] (
	[meta_id] [int] IDENTITY (1, 1) NOT NULL ,
	[description] [varchar] (80) NOT NULL ,
	[doc_type] [int] NOT NULL ,
	[meta_headline] [varchar] (255) NOT NULL ,
	[meta_text] [varchar] (1000) NOT NULL ,
	[meta_image] [varchar] (255) NOT NULL ,
	[owner_id] [int] NOT NULL ,
	[permissions] [int] NOT NULL ,
	[shared] [int] NOT NULL ,
	[expand] [int] NOT NULL ,
	[show_meta] [int] NOT NULL ,
	[help_text_id] [int] NOT NULL ,
	[archive] [int] NOT NULL ,
	[status_id] [int] NOT NULL ,
	[lang_prefix] [varchar] (3) NOT NULL ,
	[classification] [varchar] (200) NOT NULL ,
	[date_created] [datetime] NOT NULL ,
	[date_modified] [datetime] NOT NULL ,
	[sort_position] [int] NOT NULL ,
	[menu_position] [int] NOT NULL ,
	[disable_search] [int] NOT NULL ,
	[activated_date] [varchar] (10) NOT NULL ,
	[activated_time] [varchar] (6) NOT NULL ,
	[archived_date] [varchar] (10) NOT NULL ,
	[archived_time] [varchar] (6) NOT NULL ,
	[target] [varchar] (10) NOT NULL ,
	[frame_name] [varchar] (20) NOT NULL ,
	[activate] [int] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[meta_classification] (
	[meta_id] [int] NOT NULL ,
	[class_id] [int] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[mime_types] (
	[mime_id] [int] IDENTITY (0, 1) NOT NULL ,
	[mime_name] [varchar] (50) NOT NULL ,
	[mime] [varchar] (50) NOT NULL ,
	[lang_prefix] [varchar] (3) NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[new_doc_permission_sets] (
	[meta_id] [int] NOT NULL ,
	[set_id] [int] NOT NULL ,
	[permission_id] [int] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[new_doc_permission_sets_ex] (
	[meta_id] [int] NOT NULL ,
	[set_id] [int] NOT NULL ,
	[permission_id] [int] NOT NULL ,
	[permission_data] [int] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[permission_sets] (
	[set_id] [int] NOT NULL ,
	[description] [varchar] (30) NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[permissions] (
	[permission_id] [tinyint] NOT NULL ,
	[lang_prefix] [varchar] (3) NOT NULL ,
	[description] [varchar] (50) NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[phones] (
	[phone_id] [int] NOT NULL ,
	[country_code] [varchar] (4) NOT NULL ,
	[area_code] [char] (8) NOT NULL ,
	[number] [char] (25) NOT NULL ,
	[user_id] [int] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[roles] (
	[role_id] [int] NOT NULL ,
	[role_name] [char] (25) NOT NULL ,
	[permissions] [int] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[roles_permissions] (
	[permission_id] [int] NOT NULL ,
	[lang_prefix] [varchar] (3) NOT NULL ,
	[description] [varchar] (40) NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[roles_rights] (
	[role_id] [int] NOT NULL ,
	[meta_id] [int] NOT NULL ,
	[set_id] [tinyint] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[sort_by] (
	[sort_by_id] [int] IDENTITY (1, 1) NOT NULL ,
	[sort_by_type] [varchar] (30) NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[stats] (
	[name] [varchar] (120) NOT NULL ,
	[num] [int] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[sys_data] (
	[sys_id] [tinyint] IDENTITY (1, 1) NOT NULL ,
	[type_id] [tinyint] NOT NULL ,
	[value] [varchar] (80) NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[sys_types] (
	[type_id] [tinyint] IDENTITY (1, 1) NOT NULL ,
	[name] [varchar] (50) NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[templategroups] (
	[group_id] [int] NOT NULL ,
	[group_name] [varchar] (50) NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[templates] (
	[template_id] [int] NOT NULL ,
	[template_name] [varchar] (80) NOT NULL ,
	[simple_name] [varchar] (80) NOT NULL ,
	[lang_prefix] [varchar] (3) NOT NULL ,
	[no_of_txt] [int] NULL ,
	[no_of_img] [int] NULL ,
	[no_of_url] [int] NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[templates_cref] (
	[group_id] [int] NOT NULL ,
	[template_id] [int] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[text_docs] (
	[meta_id] [int] NOT NULL ,
	[template_id] [int] NOT NULL ,
	[group_id] [int] NOT NULL ,
	[sort_order] [int] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[texts] (
	[meta_id] [int] NOT NULL ,
	[name] [int] NOT NULL ,
	[text] [text] NOT NULL ,
	[type] [int] NULL 
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

CREATE TABLE [dbo].[track_log] (
	[user_id] [smallint] NULL ,
	[log_datetime] [datetime] NULL ,
	[from_meta_id] [int] NULL ,
	[to_meta_id] [int] NULL ,
	[cookie_id] [int] NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[url_docs] (
	[meta_id] [int] NOT NULL ,
	[frame_name] [varchar] (80) NOT NULL ,
	[target] [varchar] (15) NOT NULL ,
	[url_ref] [varchar] (255) NOT NULL ,
	[url_txt] [varchar] (255) NOT NULL ,
	[lang_prefix] [varchar] (3) NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[user_rights] (
	[user_id] [int] NOT NULL ,
	[meta_id] [int] NOT NULL ,
	[permission_id] [tinyint] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[user_roles_crossref] (
	[user_id] [int] NOT NULL ,
	[role_id] [int] NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[user_types] (
	[user_type] [int] NOT NULL ,
	[type_name] [char] (30) NULL ,
	[lang_prefix] [char] (3) NOT NULL 
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[users] (
	[user_id] [int] NOT NULL ,
	[login_name] [char] (15) NOT NULL ,
	[login_password] [char] (15) NOT NULL ,
	[first_name] [char] (25) NOT NULL ,
	[last_name] [char] (30) NOT NULL ,
	[title] [char] (30) NOT NULL ,
	[company] [char] (30) NOT NULL ,
	[address] [char] (40) NOT NULL ,
	[city] [char] (30) NOT NULL ,
	[zip] [char] (15) NOT NULL ,
	[country] [char] (30) NOT NULL ,
	[county_council] [char] (30) NOT NULL ,
	[email] [char] (50) NOT NULL ,
	[admin_mode] [int] NOT NULL ,
	[last_page] [int] NOT NULL ,
	[archive_mode] [int] NOT NULL ,
	[lang_id] [int] NOT NULL ,
	[user_type] [int] NOT NULL ,
	[active] [int] NOT NULL ,
	[create_date] [smalldatetime] NOT NULL 
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[display_name] WITH NOCHECK ADD 
	CONSTRAINT [PK_display_name] PRIMARY KEY  CLUSTERED 
	(
		[sort_by_id],
		[lang_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[includes] WITH NOCHECK ADD 
	CONSTRAINT [PK_includes] PRIMARY KEY  CLUSTERED 
	(
		[meta_id],
		[include_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[sort_by] WITH NOCHECK ADD 
	CONSTRAINT [PK_sort_by] PRIMARY KEY  CLUSTERED 
	(
		[sort_by_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[stats] WITH NOCHECK ADD 
	CONSTRAINT [stats_pk] PRIMARY KEY  CLUSTERED 
	(
		[name]
	)  ON [PRIMARY] 
GO

 CREATE  CLUSTERED  INDEX [childs_meta_id] ON [dbo].[childs]([meta_id]) ON [PRIMARY]
GO

 CREATE  CLUSTERED  INDEX [IX_doc_permission_sets] ON [dbo].[doc_permission_sets]([meta_id], [set_id]) ON [PRIMARY]
GO

 CREATE  CLUSTERED  INDEX [IX_doc_permissions] ON [dbo].[doc_permissions]([permission_id]) ON [PRIMARY]
GO

 CREATE  CLUSTERED  INDEX [IX_doc_types] ON [dbo].[doc_types]([lang_prefix], [doc_type]) ON [PRIMARY]
GO

 CREATE  CLUSTERED  INDEX [fileupload_docs_meta_id] ON [dbo].[fileupload_docs]([meta_id]) ON [PRIMARY]
GO

 CREATE  UNIQUE  CLUSTERED  INDEX [meta_meta_id] ON [dbo].[meta]([meta_id], [show_meta], [activate]) ON [PRIMARY]
GO

 CREATE  CLUSTERED  INDEX [roles_rights_meta_id] ON [dbo].[roles_rights]([meta_id], [role_id], [set_id]) ON [PRIMARY]
GO

ALTER TABLE [dbo].[browser_docs] WITH NOCHECK ADD 
	CONSTRAINT [DF_browser_docs_browser_id] DEFAULT (0) FOR [browser_id],
	CONSTRAINT [PK_browser_docs] PRIMARY KEY  NONCLUSTERED 
	(
		[meta_id],
		[to_meta_id],
		[browser_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[browsers] WITH NOCHECK ADD 
	CONSTRAINT [DF_browsers_value] DEFAULT (1) FOR [value],
	CONSTRAINT [PK_browsers] PRIMARY KEY  NONCLUSTERED 
	(
		[browser_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[childs] WITH NOCHECK ADD 
	CONSTRAINT [PK_childs] PRIMARY KEY  NONCLUSTERED 
	(
		[meta_id],
		[to_meta_id],
		[menu_sort]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[classification] WITH NOCHECK ADD 
	CONSTRAINT [PK_classification] PRIMARY KEY  NONCLUSTERED 
	(
		[class_id]
	)  ON [PRIMARY] 
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

ALTER TABLE [dbo].[doc_types] WITH NOCHECK ADD 
	CONSTRAINT [DF_doc_types_lang_prefix] DEFAULT ('se') FOR [lang_prefix],
	CONSTRAINT [PK_doc_types] PRIMARY KEY  NONCLUSTERED 
	(
		[doc_type],
		[lang_prefix]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[fileupload_docs] WITH NOCHECK ADD 
	CONSTRAINT [PK_fileupload_docs] PRIMARY KEY  NONCLUSTERED 
	(
		[meta_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[frameset_docs] WITH NOCHECK ADD 
	CONSTRAINT [PK_frameset_docs] PRIMARY KEY  NONCLUSTERED 
	(
		[meta_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[images] WITH NOCHECK ADD 
	CONSTRAINT [PK_images] PRIMARY KEY  NONCLUSTERED 
	(
		[meta_id],
		[name]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[lang_prefixes] WITH NOCHECK ADD 
	CONSTRAINT [PK_lang_prefixes] PRIMARY KEY  NONCLUSTERED 
	(
		[lang_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[languages] WITH NOCHECK ADD 
	CONSTRAINT [PK_languages] PRIMARY KEY  NONCLUSTERED 
	(
		[lang_prefix],
		[user_prefix]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[meta] WITH NOCHECK ADD 
	CONSTRAINT [PK_meta] PRIMARY KEY  NONCLUSTERED 
	(
		[meta_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[meta_classification] WITH NOCHECK ADD 
	CONSTRAINT [PK_meta_classification] PRIMARY KEY  NONCLUSTERED 
	(
		[meta_id],
		[class_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[mime_types] WITH NOCHECK ADD 
	CONSTRAINT [DF_mime_types_lang_prefix] DEFAULT ('se') FOR [lang_prefix],
	CONSTRAINT [PK_mime_types] PRIMARY KEY  NONCLUSTERED 
	(
		[mime_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[new_doc_permission_sets] WITH NOCHECK ADD 
	CONSTRAINT [PK_new_doc_permission_sets] PRIMARY KEY  NONCLUSTERED 
	(
		[meta_id],
		[set_id]
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

ALTER TABLE [dbo].[permission_sets] WITH NOCHECK ADD 
	CONSTRAINT [PK_permission_types] PRIMARY KEY  NONCLUSTERED 
	(
		[set_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[permissions] WITH NOCHECK ADD 
	CONSTRAINT [DF_permissions_lang_prefix] DEFAULT ('se') FOR [lang_prefix],
	CONSTRAINT [PK_permissions] PRIMARY KEY  NONCLUSTERED 
	(
		[permission_id],
		[lang_prefix]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[phones] WITH NOCHECK ADD 
	CONSTRAINT [PK_phones] PRIMARY KEY  NONCLUSTERED 
	(
		[phone_id],
		[user_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[roles] WITH NOCHECK ADD 
	CONSTRAINT [DF_roles_permissions] DEFAULT (0) FOR [permissions],
	CONSTRAINT [PK_roles] PRIMARY KEY  NONCLUSTERED 
	(
		[role_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[roles_permissions] WITH NOCHECK ADD 
	CONSTRAINT [PK_roles_permissions] PRIMARY KEY  NONCLUSTERED 
	(
		[permission_id],
		[lang_prefix]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[roles_rights] WITH NOCHECK ADD 
	CONSTRAINT [PK_roles_rights] PRIMARY KEY  NONCLUSTERED 
	(
		[role_id],
		[meta_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[sys_data] WITH NOCHECK ADD 
	CONSTRAINT [PK_sys_data] PRIMARY KEY  NONCLUSTERED 
	(
		[sys_id],
		[type_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[sys_types] WITH NOCHECK ADD 
	CONSTRAINT [PK_sys_types] PRIMARY KEY  NONCLUSTERED 
	(
		[type_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[templategroups] WITH NOCHECK ADD 
	CONSTRAINT [PK_templategroups] PRIMARY KEY  NONCLUSTERED 
	(
		[group_id]
	)  ON [PRIMARY] ,
	CONSTRAINT [IX_templategroups] UNIQUE  NONCLUSTERED 
	(
		[group_name]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[templates] WITH NOCHECK ADD 
	CONSTRAINT [PK_templates] PRIMARY KEY  NONCLUSTERED 
	(
		[template_id]
	)  ON [PRIMARY] ,
	CONSTRAINT [IX_templates] UNIQUE  NONCLUSTERED 
	(
		[simple_name],
		[lang_prefix]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[templates_cref] WITH NOCHECK ADD 
	CONSTRAINT [PK_templates_cref] PRIMARY KEY  NONCLUSTERED 
	(
		[group_id],
		[template_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[text_docs] WITH NOCHECK ADD 
	CONSTRAINT [DF_text_docs_group_id] DEFAULT (1) FOR [group_id],
	CONSTRAINT [PK_text_docs] PRIMARY KEY  NONCLUSTERED 
	(
		[meta_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[texts] WITH NOCHECK ADD 
	CONSTRAINT [PK_texts] PRIMARY KEY  NONCLUSTERED 
	(
		[meta_id],
		[name]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[url_docs] WITH NOCHECK ADD 
	CONSTRAINT [PK_url_docs] PRIMARY KEY  NONCLUSTERED 
	(
		[meta_id],
		[lang_prefix]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[user_rights] WITH NOCHECK ADD 
	CONSTRAINT [PK_user_rights] PRIMARY KEY  NONCLUSTERED 
	(
		[user_id],
		[meta_id],
		[permission_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[user_roles_crossref] WITH NOCHECK ADD 
	CONSTRAINT [PK_user_roles_crossref] PRIMARY KEY  NONCLUSTERED 
	(
		[user_id],
		[role_id]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[user_types] WITH NOCHECK ADD 
	CONSTRAINT [PK_user_types] PRIMARY KEY  NONCLUSTERED 
	(
		[user_type],
		[lang_prefix]
	)  ON [PRIMARY] 
GO

ALTER TABLE [dbo].[users] WITH NOCHECK ADD 
	CONSTRAINT [DF_users_title] DEFAULT ('') FOR [title],
	CONSTRAINT [DF_users_company] DEFAULT ('') FOR [company],
	CONSTRAINT [DF_users_user_type] DEFAULT (1) FOR [user_type],
	CONSTRAINT [DF_users_active] DEFAULT (1) FOR [active],
	CONSTRAINT [PK_users] PRIMARY KEY  NONCLUSTERED 
	(
		[user_id]
	)  ON [PRIMARY] 
GO

 CREATE  INDEX [IX_browsers] ON [dbo].[browsers]([value]) ON [PRIMARY]
GO

if (@@microsoftversion > 0x07000000 )
EXEC ('CREATE STATISTICS [Statistic_type] ON [dbo].[doc_types] ([type]) ')
GO

 CREATE  INDEX [roles_rights_role_id] ON [dbo].[roles_rights]([role_id]) ON [PRIMARY]
GO

ALTER TABLE [dbo].[browser_docs] ADD 
	CONSTRAINT [FK_browser_docs_meta] FOREIGN KEY 
	(
		[meta_id]
	) REFERENCES [dbo].[meta] (
		[meta_id]
	)
GO

ALTER TABLE [dbo].[childs] ADD 
	CONSTRAINT [FK_childs_meta1] FOREIGN KEY 
	(
		[meta_id]
	) REFERENCES [dbo].[meta] (
		[meta_id]
	)
GO

ALTER TABLE [dbo].[display_name] ADD 
	CONSTRAINT [FK_display_name_lang_prefixes] FOREIGN KEY 
	(
		[lang_id]
	) REFERENCES [dbo].[lang_prefixes] (
		[lang_id]
	),
	CONSTRAINT [FK_display_name_sort_by] FOREIGN KEY 
	(
		[sort_by_id]
	) REFERENCES [dbo].[sort_by] (
		[sort_by_id]
	)
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

ALTER TABLE [dbo].[frameset_docs] ADD 
	CONSTRAINT [FK_frameset_docs_meta] FOREIGN KEY 
	(
		[meta_id]
	) REFERENCES [dbo].[meta] (
		[meta_id]
	)
GO

ALTER TABLE [dbo].[images] ADD 
	CONSTRAINT [FK_images_meta] FOREIGN KEY 
	(
		[meta_id]
	) REFERENCES [dbo].[meta] (
		[meta_id]
	)
GO

ALTER TABLE [dbo].[includes] ADD 
	CONSTRAINT [FK_includes_meta] FOREIGN KEY 
	(
		[meta_id]
	) REFERENCES [dbo].[meta] (
		[meta_id]
	),
	CONSTRAINT [FK_includes_meta1] FOREIGN KEY 
	(
		[included_meta_id]
	) REFERENCES [dbo].[meta] (
		[meta_id]
	)
GO

ALTER TABLE [dbo].[meta_classification] ADD 
	CONSTRAINT [FK_meta_classification_classification] FOREIGN KEY 
	(
		[class_id]
	) REFERENCES [dbo].[classification] (
		[class_id]
	),
	CONSTRAINT [FK_meta_classification_meta] FOREIGN KEY 
	(
		[meta_id]
	) REFERENCES [dbo].[meta] (
		[meta_id]
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

ALTER TABLE [dbo].[phones] ADD 
	CONSTRAINT [FK_phones_users] FOREIGN KEY 
	(
		[user_id]
	) REFERENCES [dbo].[users] (
		[user_id]
	)
GO

ALTER TABLE [dbo].[roles_rights] ADD 
	CONSTRAINT [FK_roles_rights_meta] FOREIGN KEY 
	(
		[meta_id]
	) REFERENCES [dbo].[meta] (
		[meta_id]
	),
	CONSTRAINT [FK_roles_rights_roles] FOREIGN KEY 
	(
		[role_id]
	) REFERENCES [dbo].[roles] (
		[role_id]
	)
GO

ALTER TABLE [dbo].[sys_data] ADD 
	CONSTRAINT [FK_sys_data_sys_types] FOREIGN KEY 
	(
		[type_id]
	) REFERENCES [dbo].[sys_types] (
		[type_id]
	)
GO

ALTER TABLE [dbo].[templates_cref] ADD 
	CONSTRAINT [FK_templates_cref_templategroups] FOREIGN KEY 
	(
		[group_id]
	) REFERENCES [dbo].[templategroups] (
		[group_id]
	),
	CONSTRAINT [FK_templates_cref_templates] FOREIGN KEY 
	(
		[template_id]
	) REFERENCES [dbo].[templates] (
		[template_id]
	)
GO

ALTER TABLE [dbo].[text_docs] ADD 
	CONSTRAINT [FK_text_docs_meta] FOREIGN KEY 
	(
		[meta_id]
	) REFERENCES [dbo].[meta] (
		[meta_id]
	),
	CONSTRAINT [FK_text_docs_templates] FOREIGN KEY 
	(
		[template_id]
	) REFERENCES [dbo].[templates] (
		[template_id]
	)
GO

ALTER TABLE [dbo].[texts] ADD 
	CONSTRAINT [FK_texts_meta] FOREIGN KEY 
	(
		[meta_id]
	) REFERENCES [dbo].[meta] (
		[meta_id]
	)
GO

ALTER TABLE [dbo].[url_docs] ADD 
	CONSTRAINT [FK_url_docs_meta] FOREIGN KEY 
	(
		[meta_id]
	) REFERENCES [dbo].[meta] (
		[meta_id]
	)
GO

ALTER TABLE [dbo].[user_rights] ADD 
	CONSTRAINT [FK_user_rights_meta] FOREIGN KEY 
	(
		[meta_id]
	) REFERENCES [dbo].[meta] (
		[meta_id]
	),
	CONSTRAINT [FK_user_rights_users] FOREIGN KEY 
	(
		[user_id]
	) REFERENCES [dbo].[users] (
		[user_id]
	)
GO

ALTER TABLE [dbo].[user_roles_crossref] ADD 
	CONSTRAINT [FK_user_roles_crossref_roles] FOREIGN KEY 
	(
		[role_id]
	) REFERENCES [dbo].[roles] (
		[role_id]
	),
	CONSTRAINT [FK_user_roles_crossref_users] FOREIGN KEY 
	(
		[user_id]
	) REFERENCES [dbo].[users] (
		[user_id]
	)
GO

