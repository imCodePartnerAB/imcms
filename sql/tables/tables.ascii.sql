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
	[first_name] [varchar] (50) NULL ,
	[last_name] [varchar] (50) NULL ,
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
	[name] [varchar] (255) NULL ,
	[sort_type] [char] (1) NULL
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
	[forum_name] [varchar] (255) NULL ,
	[archive_mode] [char] (1) NOT NULL ,
	[discs_to_show] [int] NOT NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[A_replies] (
	[reply_id] [int] IDENTITY (1, 1) NOT NULL ,
	[user_id] [int] NULL ,
	[parent_id] [int] NULL ,
	[create_date] [datetime] NULL ,
	[headline] [varchar] (255) NULL ,
	[text] [text] NULL ,
	[reply_level] [int] NOT NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

CREATE TABLE [dbo].[A_selfreg_roles] (
	[selfreg_id] [int] IDENTITY (1, 1) NOT NULL ,
	[role_id] [int] NULL ,
	[role_name] [char] (25) NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[A_templates] (
	[template_id] [int] IDENTITY (1, 1) NOT NULL ,
	[template_lib] [varchar] (50) NOT NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[B_bill] (
	[bill_id] [int] IDENTITY (1, 1) NOT NULL ,
	[section_id] [int] NOT NULL ,
	[create_date] [datetime] NOT NULL ,
	[ip_adress] [varchar] (15) NOT NULL ,
	[headline] [varchar] (255) NOT NULL ,
	[text] [text] NOT NULL ,
	[email] [varchar] (155) NOT NULL ,
	[user_id] [int] NOT NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

CREATE TABLE [dbo].[B_billboard] (
	[meta_id] [int] NOT NULL ,
	[name] [varchar] (255) NULL ,
	[subject] [varchar] (50) NOT NULL
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
	[ip_adress] [varchar] (15) NOT NULL ,
	[create_date] [datetime] NOT NULL ,
	[headline] [varchar] (255) NOT NULL ,
	[text] [text] NOT NULL ,
	[parent_id] [int] NOT NULL ,
	[user_id] [int] NOT NULL ,
	[email] [varchar] (155) NOT NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

CREATE TABLE [dbo].[B_section] (
	[section_id] [int] IDENTITY (1, 1) NOT NULL ,
	[section_name] [varchar] (255) NULL ,
	[archive_time] [datetime] NOT NULL ,
	[archive_mode] [char] (1) NOT NULL ,
	[days_to_show] [int] NOT NULL ,
	[discs_to_show] [int] NOT NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[B_templates] (
	[template_id] [int] IDENTITY (1, 1) NOT NULL ,
	[template_lib] [varchar] (50) NOT NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[C_authorization_types] (
	[authorization_type] [varchar] (50) NOT NULL ,
	[authorization_id] [int] NOT NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[C_chat] (
	[meta_id] [int] NOT NULL ,
	[name] [varchar] (255) NULL ,
	[permType] [int] NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[C_chatParameters] (
	[chatId] [int] NOT NULL ,
	[updateTime] [int] NULL ,
	[reload] [int] NULL ,
	[inOut] [int] NULL ,
	[privat] [int] NULL ,
	[publik] [int] NULL ,
	[dateTime] [int] NULL ,
	[font] [int] NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[C_chat_authorization] (
	[meta_id] [int] NOT NULL ,
	[authorization_id] [int] NOT NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[C_chat_msg_type] (
	[msg_id] [int] NOT NULL ,
	[meta_id] [int] NOT NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[C_chat_selfreg_crossref] (
	[meta_id] [int] NOT NULL ,
	[selfreg_id] [int] NOT NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[C_chat_templates] (
	[chat_id] [int] NOT NULL ,
	[template_id] [int] NOT NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[C_msg_type] (
	[msg_id] [int] IDENTITY (1, 1) NOT NULL ,
	[msg_string] [varchar] (25) NOT NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[C_selfreg_roles] (
	[selfreg_id] [int] IDENTITY (1, 1) NOT NULL ,
	[role_id] [int] NULL ,
	[role_name] [char] (25) NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[C_templates] (
	[template_id] [int] IDENTITY (1, 1) NOT NULL ,
	[template_lib] [varchar] (50) NOT NULL
) ON [PRIMARY]
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

CREATE TABLE [dbo].[categories] (
	[category_id] [int] IDENTITY (1, 1) NOT NULL ,
	[category_type_id] [int] NOT NULL ,
	[name] [varchar] (50) NOT NULL ,
	[description] [varchar] (500) NULL ,
	[image] [varchar] (255) NOT NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[category_types] (
	[category_type_id] [int] IDENTITY (1, 1) NOT NULL ,
	[name] [varchar] (50) NOT NULL ,
	[max_choices] [int] NOT NULL ,
    [inherited] [bit] NOT NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[childs] (
	[to_meta_id] [int] NOT NULL ,
	[manual_sort_order] [int] NOT NULL ,
	[tree_sort_index] [varchar] (64) NOT NULL ,
	[menu_id] [int] NOT NULL
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

CREATE TABLE [dbo].[document_categories] (
	[meta_id] [int] NOT NULL ,
	[category_id] [int] NOT NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[fileupload_docs] (
	[meta_id] [int] NOT NULL ,
	[variant_name] [varchar] (100) NOT NULL ,
	[filename] [varchar] (255) NOT NULL ,
	[mime] [varchar] (50) NOT NULL ,
	[created_as_image] [int] NOT NULL ,
	[default_variant] [bit] NOT NULL DEFAULT (0)
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
	[align] [varchar] (15) NOT NULL ,
	[alt_text] [varchar] (255) NOT NULL ,
	[low_scr] [varchar] (255) NOT NULL ,
	[imgurl] [varchar] (255) NOT NULL ,
	[linkurl] [varchar] (255) NOT NULL,
	[type] [int] NOT NULL
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

CREATE TABLE [dbo].[menus] (
	[menu_id] [int] IDENTITY (1, 1) NOT NULL ,
	[meta_id] [int] NOT NULL ,
	[menu_index] [int] NOT NULL ,
	[sort_order] [int] NOT NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[meta] (
	[meta_id] [int] IDENTITY (1, 1) NOT NULL ,
	[doc_type] [int] NOT NULL ,
	[meta_headline] [nvarchar] (255) NOT NULL ,
	[meta_text] [nvarchar] (1000) NOT NULL ,
	[meta_image] [varchar] (255) NOT NULL ,
	[owner_id] [int] NOT NULL ,
	[permissions] [int] NOT NULL ,
	[shared] [int] NOT NULL ,
	[show_meta] [int] NOT NULL ,
	[lang_prefix] [varchar] (3) NOT NULL ,
	[date_created] [datetime] NOT NULL ,
	[date_modified] [datetime] NOT NULL ,
	[disable_search] [int] NOT NULL ,
	[target] [nvarchar] (10) NOT NULL ,
	[activate] [int] NOT NULL ,
	[archived_datetime] [datetime] NULL ,
	[publisher_id] [int] NULL ,
	[status] [int] NOT NULL ,
	[publication_start_datetime] [datetime] NULL ,
	[publication_end_datetime] [datetime] NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[meta_classification] (
	[meta_id] [int] NOT NULL ,
	[class_id] [int] NOT NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[meta_section] (
	[meta_id] [int] NOT NULL ,
	[section_id] [int] NOT NULL
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
	[number] [varchar] (25) NOT NULL ,
	[user_id] [int] NOT NULL ,
	[phonetype_id] [int] NOT NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[phonetypes] (
	[phonetype_id] [int] NOT NULL ,
	[typename] [varchar] (12) NOT NULL ,
	[lang_id] [int] NOT NULL
) ON [PRIMARY]
GO

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
	[email_from] [int] NULL ,
	[email_subject] [int] NULL ,
	[result_template] [int] NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[roles] (
	[role_id] [int] IDENTITY (1, 1) NOT NULL ,
	[role_name] [varchar] (60) NOT NULL ,
	[permissions] [int] NOT NULL ,
	[admin_role] [int] NOT NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[roles_rights] (
	[role_id] [int] NOT NULL ,
	[meta_id] [int] NOT NULL ,
	[set_id] [tinyint] NOT NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[sections] (
	[section_id] [int] IDENTITY (1, 1) NOT NULL ,
	[section_name] [varchar] (50) NOT NULL
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
	[default_template_1] [int] NOT NULL ,
	[default_template_2] [int] NOT NULL ,
	[default_template] [int] NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[texts] (
	[meta_id] [int] NOT NULL ,
	[name] [int] NOT NULL ,
	[text] [ntext] NOT NULL ,
	[type] [int] NULL ,
	[counter] [int] IDENTITY (1, 1) NOT NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
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

CREATE TABLE [dbo].[user_flags] (
	[user_flag_id] [int] NOT NULL ,
	[name] [varchar] (64) NOT NULL ,
	[type] [int] NOT NULL ,
	[description] [varchar] (256) NOT NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[user_flags_crossref] (
	[user_id] [int] NOT NULL ,
	[user_flag_id] [int] NOT NULL
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

CREATE TABLE [dbo].[useradmin_role_crossref] (
	[user_id] [int] NOT NULL ,
	[role_id] [int] NOT NULL
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[users] (
	[user_id] [int] NOT NULL ,
	[login_name] [varchar] (50) NOT NULL ,
	[login_password] [varchar] (15) NOT NULL ,
	[first_name] [varchar] (25) NOT NULL ,
	[last_name] [varchar] (30) NOT NULL ,
	[title] [varchar] (30) NOT NULL ,
	[company] [varchar] (30) NOT NULL ,
	[address] [varchar] (40) NOT NULL ,
	[city] [varchar] (30) NOT NULL ,
	[zip] [varchar] (15) NOT NULL ,
	[country] [varchar] (30) NOT NULL ,
	[county_council] [varchar] (30) NOT NULL ,
	[email] [varchar] (50) NOT NULL ,
	[external] [int] NOT NULL ,
	[active] [int] NOT NULL ,
	[create_date] [smalldatetime] NOT NULL ,
	[language] [varchar] (3) NOT NULL ,
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

ALTER TABLE [dbo].[C_authorization_types] WITH NOCHECK ADD
	CONSTRAINT [PK_authorization_types] PRIMARY KEY  CLUSTERED
	(
		[authorization_id]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[C_chat] WITH NOCHECK ADD
	CONSTRAINT [PK_chat] PRIMARY KEY  CLUSTERED
	(
		[meta_id]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[C_chatParameters] WITH NOCHECK ADD
	CONSTRAINT [PK_chatParameters] PRIMARY KEY  CLUSTERED
	(
		[chatId]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[C_chat_authorization] WITH NOCHECK ADD
	CONSTRAINT [PK_chat_authorization] PRIMARY KEY  CLUSTERED
	(
		[meta_id],
		[authorization_id]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[C_chat_msg_type] WITH NOCHECK ADD
	CONSTRAINT [PK_room_msg_type] PRIMARY KEY  CLUSTERED
	(
		[msg_id],
		[meta_id]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[C_chat_selfreg_crossref] WITH NOCHECK ADD
	CONSTRAINT [PK_chat_selfreg_crossref] PRIMARY KEY  CLUSTERED
	(
		[meta_id],
		[selfreg_id]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[C_chat_templates] WITH NOCHECK ADD
	CONSTRAINT [PK_chat_templates] PRIMARY KEY  CLUSTERED
	(
		[chat_id],
		[template_id]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[C_msg_type] WITH NOCHECK ADD
	CONSTRAINT [PK_msg_type] PRIMARY KEY  CLUSTERED
	(
		[msg_id]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[C_selfreg_roles] WITH NOCHECK ADD
	CONSTRAINT [PK_C_selfreg_roles] PRIMARY KEY  CLUSTERED
	(
		[selfreg_id]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[C_templates] WITH NOCHECK ADD
	CONSTRAINT [PK_C_templates] PRIMARY KEY  CLUSTERED
	(
		[template_id]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[categories] WITH NOCHECK ADD
	 PRIMARY KEY  CLUSTERED
	(
		[category_id]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[category_types] WITH NOCHECK ADD
	 PRIMARY KEY  CLUSTERED
	(
		[category_type_id]
	)  ON [PRIMARY] ,
    CONSTRAINT [UQ__category_types__name] UNIQUE  NONCLUSTERED
    (
        [name]
    )  ON [PRIMARY]
GO

ALTER TABLE [dbo].[childs] WITH NOCHECK ADD
	CONSTRAINT [PK_childs] PRIMARY KEY  CLUSTERED
	(
		[menu_id],
		[to_meta_id]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[display_name] WITH NOCHECK ADD
	CONSTRAINT [PK_display_name] PRIMARY KEY  CLUSTERED
	(
		[sort_by_id],
		[lang_id]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[document_categories] WITH NOCHECK ADD
	 PRIMARY KEY  CLUSTERED
	(
		[meta_id],
		[category_id]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[includes] WITH NOCHECK ADD
	CONSTRAINT [PK_includes] PRIMARY KEY  CLUSTERED
	(
		[meta_id],
		[include_id]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[ip_accesses] WITH NOCHECK ADD
	CONSTRAINT [PK_ip_accesses] PRIMARY KEY  CLUSTERED
	(
		[ip_access_id]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[menus] WITH NOCHECK ADD
	 PRIMARY KEY  CLUSTERED
	(
		[menu_id]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[meta_section] WITH NOCHECK ADD
	CONSTRAINT [PK_meta_section] PRIMARY KEY  CLUSTERED
	(
		[meta_id],
		[section_id]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[phonetypes] WITH NOCHECK ADD
	CONSTRAINT [PK_phonetypes] PRIMARY KEY  CLUSTERED
	(
		[phonetype_id],
		[lang_id]
	)  ON [PRIMARY]
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

ALTER TABLE [dbo].[sections] WITH NOCHECK ADD
	CONSTRAINT [PK_section] PRIMARY KEY  CLUSTERED
	(
		[section_id]
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

ALTER TABLE [dbo].[texts] WITH NOCHECK ADD
	CONSTRAINT [PK_texts] PRIMARY KEY  CLUSTERED
	(
		[counter]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[user_flags] WITH NOCHECK ADD
	CONSTRAINT [PK_user_flags] PRIMARY KEY  CLUSTERED
	(
		[user_flag_id]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[user_flags_crossref] WITH NOCHECK ADD
	CONSTRAINT [PK_user_flags_crossref] PRIMARY KEY  CLUSTERED
	(
		[user_id],
		[user_flag_id]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[useradmin_role_crossref] WITH NOCHECK ADD
	CONSTRAINT [PK_useradmin_role_crossref] PRIMARY KEY  CLUSTERED
	(
		[user_id],
		[role_id]
	)  ON [PRIMARY]
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

ALTER TABLE [dbo].[B_section] WITH NOCHECK ADD
	CONSTRAINT [DF_B_section_discs_to_show] DEFAULT (20) FOR [discs_to_show]
GO

ALTER TABLE [dbo].[C_chatParameters] WITH NOCHECK ADD
	CONSTRAINT [DF_chatParameters_updateTime] DEFAULT (30) FOR [updateTime],
	CONSTRAINT [DF_chatParameters_reload] DEFAULT (2) FOR [reload],
	CONSTRAINT [DF_chatParameters_inOut] DEFAULT (2) FOR [inOut],
	CONSTRAINT [DF_chatParameters_privat] DEFAULT (2) FOR [privat],
	CONSTRAINT [DF_chatParameters_publik] DEFAULT (2) FOR [publik],
	CONSTRAINT [DF_chatParameters_dateTime] DEFAULT (2) FOR [dateTime],
	CONSTRAINT [DF_chatParameters_font] DEFAULT (2) FOR [font]
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

ALTER TABLE [dbo].[categories] WITH NOCHECK ADD
	CONSTRAINT [DF__categorie__image__78B3EFCA] DEFAULT ('') FOR [image]
GO

ALTER TABLE [dbo].[category_types] WITH NOCHECK ADD
	CONSTRAINT [DF__category___max_c__5E54FF49] DEFAULT (0) FOR [max_choices]
GO

ALTER TABLE [dbo].[childs] WITH NOCHECK ADD
	CONSTRAINT [DF__childs__tree_sor__7B905C75] DEFAULT ('') FOR [tree_sort_index]
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
	CONSTRAINT [DF_doc_types_lang_prefix] DEFAULT ('swe') FOR [lang_prefix],
	CONSTRAINT [PK_doc_types] PRIMARY KEY  NONCLUSTERED
	(
		[doc_type],
		[lang_prefix]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[fileupload_docs] WITH NOCHECK ADD
	CONSTRAINT [PK_fileupload_docs] PRIMARY KEY  NONCLUSTERED
	(
		[meta_id],
		[variant_name]
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

ALTER TABLE [dbo].[menus] WITH NOCHECK ADD
	CONSTRAINT [UQ__menus__meta_id_menu_index] UNIQUE  NONCLUSTERED
	(
		[meta_id],
		[menu_index]
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
	CONSTRAINT [DF_mime_types_lang_prefix] DEFAULT ('swe') FOR [lang_prefix],
	CONSTRAINT [PK_mime_types] PRIMARY KEY  NONCLUSTERED
	(
		[mime_id],
		[lang_prefix]
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
	CONSTRAINT [DF_permissions_lang_prefix] DEFAULT ('swe') FOR [lang_prefix],
	CONSTRAINT [PK_permissions] PRIMARY KEY  NONCLUSTERED
	(
		[permission_id],
		[lang_prefix]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[phones] WITH NOCHECK ADD
	CONSTRAINT [DF_phones_phonetype_id] DEFAULT (0) FOR [phonetype_id],
	CONSTRAINT [PK_phones] PRIMARY KEY  NONCLUSTERED
	(
		[phone_id],
		[user_id]
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

ALTER TABLE [dbo].[roles] WITH NOCHECK ADD
	CONSTRAINT [DF_roles_permissions] DEFAULT (0) FOR [permissions],
	CONSTRAINT [DF_roles_admin_role] DEFAULT (0) FOR [admin_role],
	CONSTRAINT [PK_roles] PRIMARY KEY  NONCLUSTERED
	(
		[role_id]
	)  ON [PRIMARY] ,
	CONSTRAINT [roles_role_name] UNIQUE  NONCLUSTERED
	(
		[role_name]
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
	CONSTRAINT [DF__text_docs__defau__0D44F85C] DEFAULT ((-1)) FOR [default_template_1],
	CONSTRAINT [DF__text_docs__defau__0E391C95] DEFAULT ((-1)) FOR [default_template_2],
	CONSTRAINT FK_text_docs_default_template FOREIGN KEY ( default_template ) REFERENCES templates ( template_id ),
	CONSTRAINT [PK_text_docs] PRIMARY KEY  NONCLUSTERED
	(
		[meta_id]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[url_docs] WITH NOCHECK ADD
	CONSTRAINT [PK_url_docs] PRIMARY KEY  NONCLUSTERED
	(
		[meta_id],
		[lang_prefix]
	)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[user_flags] WITH NOCHECK ADD
	CONSTRAINT [IX_user_flags] UNIQUE  NONCLUSTERED
	(
		[name]
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

ALTER TABLE [dbo].[users] WITH NOCHECK ADD
	CONSTRAINT [DF_users_title] DEFAULT ('') FOR [title],
	CONSTRAINT [DF_users_company] DEFAULT ('') FOR [company],
	CONSTRAINT [DF_users_active] DEFAULT (1) FOR [active],
	CONSTRAINT [PK_users] PRIMARY KEY  NONCLUSTERED
	(
		[user_id]
	)  ON [PRIMARY] ,
	CONSTRAINT [users_login_name] UNIQUE  NONCLUSTERED
	(
		[login_name]
	)  ON [PRIMARY]
GO

CREATE UNIQUE NONCLUSTERED INDEX [IX_users_login_name] ON [dbo].[users]([login_name]) ON [PRIMARY]
GO

 CREATE  INDEX [IX_browsers] ON [dbo].[browsers]([value]) ON [PRIMARY]
GO

/****** The index created by the following statement is for internal use only. ******/
/****** It is not a real index but exists as statistics only. ******/
if (@@microsoftversion > 0x07000000 )
EXEC ('CREATE STATISTICS [Statistic_type] ON [dbo].[doc_types] ([type]) ')
GO

 CREATE  INDEX [roles_rights_role_id] ON [dbo].[roles_rights]([role_id]) ON [PRIMARY]
GO

 CREATE  INDEX [IX_texts] ON [dbo].[texts]([meta_id]) ON [PRIMARY]
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

ALTER TABLE [dbo].[C_chat_authorization] ADD
	CONSTRAINT [FK_chat_authorization_authorization_types] FOREIGN KEY
	(
		[authorization_id]
	) REFERENCES [dbo].[C_authorization_types] (
		[authorization_id]
	),
	CONSTRAINT [FK_chat_authorization_chat] FOREIGN KEY
	(
		[meta_id]
	) REFERENCES [dbo].[C_chat] (
		[meta_id]
	)
GO

ALTER TABLE [dbo].[C_chat_msg_type] ADD
	CONSTRAINT [FK_chat_msg_type_chat] FOREIGN KEY
	(
		[meta_id]
	) REFERENCES [dbo].[C_chat] (
		[meta_id]
	),
	CONSTRAINT [FK_chat_msg_type_msg_type] FOREIGN KEY
	(
		[msg_id]
	) REFERENCES [dbo].[C_msg_type] (
		[msg_id]
	)
GO

ALTER TABLE [dbo].[browser_docs] ADD
	CONSTRAINT [FK_browser_docs_meta] FOREIGN KEY
	(
		[meta_id]
	) REFERENCES [dbo].[meta] (
		[meta_id]
	)
GO

ALTER TABLE [dbo].[categories] ADD
	 FOREIGN KEY
	(
		[category_type_id]
	) REFERENCES [dbo].[category_types] (
		[category_type_id]
	)
GO

ALTER TABLE [dbo].[childs] ADD
	CONSTRAINT [FK__childs__menu_id] FOREIGN KEY
	(
		[menu_id]
	) REFERENCES [dbo].[menus] (
		[menu_id]
	),
	CONSTRAINT [PK_childs_meta] FOREIGN KEY
	(
		[to_meta_id]
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

ALTER TABLE [dbo].[document_categories] ADD
	 FOREIGN KEY
	(
		[meta_id]
	) REFERENCES [dbo].[meta] (
		[meta_id]
	),
	CONSTRAINT [FK_document_categories_categories] FOREIGN KEY
	(
		[category_id]
	) REFERENCES [dbo].[categories] (
		[category_id]
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

ALTER TABLE [dbo].[menus] ADD
	 FOREIGN KEY
	(
		[meta_id]
	) REFERENCES [dbo].[meta] (
		[meta_id]
	)
GO

ALTER TABLE [dbo].[meta] ADD
	CONSTRAINT [FK_meta_users] FOREIGN KEY
	(
		[publisher_id]
	) REFERENCES [dbo].[users] (
		[user_id]
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

ALTER TABLE [dbo].[meta_section] ADD
	CONSTRAINT [FK_meta_section_meta] FOREIGN KEY
	(
		[meta_id]
	) REFERENCES [dbo].[meta] (
		[meta_id]
	),
	CONSTRAINT [FK_meta_section_section] FOREIGN KEY
	(
		[section_id]
	) REFERENCES [dbo].[sections] (
		[section_id]
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

ALTER TABLE [dbo].[phonetypes] ADD
	CONSTRAINT [FK_phonetypes_lang_prefixes] FOREIGN KEY
	(
		[lang_id]
	) REFERENCES [dbo].[lang_prefixes] (
		[lang_id]
	)
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

ALTER TABLE [dbo].[user_flags_crossref] ADD
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
GO

