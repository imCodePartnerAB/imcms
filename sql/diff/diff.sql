-- diff.sql,v
-- Revision 1.2  2001/09/26 12:19:50  kreiger
-- Added default-templates to text_docs.
--


drop procedure [CheckDocSharePermissionForUser]
drop procedure [CheckUserDocSharePermission]

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  OFF 
GO
CREATE PROCEDURE CheckUserDocSharePermission @user_id INT, @meta_id INT AS

SELECT m.meta_id
FROM meta m
JOIN user_roles_crossref urc
				ON	urc.user_id = @user_id
				AND	m.meta_id = @meta_id
LEFT join roles_rights rr
				ON	rr.meta_id = m.meta_id
				AND	rr.role_id = urc.role_id
WHERE				(
						shared = 1
					OR	rr.set_id < 3
					OR	urc.role_id = 0
				)
GROUP BY m.meta_id
GO

-- 2001-09-19

-- Add columns for default-templates to text-docs.

alter table text_docs 
add default_template_1 INT DEFAULT -1 NOT NULL

alter table text_docs 
add default_template_2 INT DEFAULT -1 NOT NULL

-- The procedure to suport the default_templates
GO
CREATE PROCEDURE [UpdateDefaultTemplates] 
 @meta_id INT,
 @template1 int,
 @template2 int
 AS
UPDATE text_docs
SET default_template_1= @template1,
default_template_2=@template2 
WHERE meta_id = @meta_id
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO
-- The CopyDocs-procedure must also be updated

alter PROCEDURE CopyDocs @documents_string VARCHAR(200), @parent_id INT, @menu_id INT, @user INT AS

CREATE TABLE #documents (
  meta_id VARCHAR(10)
)

CREATE TABLE #documents2 (
  meta_id VARCHAR(10)
)

DECLARE @substring VARCHAR(30)
DECLARE @index INT
DECLARE @endindex INT

IF LEN(@documents_string) > 0 BEGIN
 SET @index = 1
 WHILE @index <= LEN(@documents_string) BEGIN
  SET @endindex = CHARINDEX(',',@documents_string,@index+1)
  IF @endindex = 0 BEGIN
   SET @endindex = LEN(@documents_string)+1
  END --IF
  SET @substring = SUBSTRING(@documents_string,@index,@endindex-@index)
  INSERT INTO #documents2 VALUES (@substring)
  SET @index = @endindex + 1
 END -- WHILE
END -- IF

INSERT INTO 	#documents
SELECT		t.meta_id
FROM		#documents2 t
JOIN		meta m
					ON	t.meta_id = m.meta_id
JOIN		user_roles_crossref urc
					ON	urc.user_id = @user
LEFT JOIN	roles_rights rr
					ON	rr.set_id < 3
					AND	rr.meta_id = @parent_id
					AND	rr.role_id = urc.role_id
LEFT JOIN	doc_permission_sets_ex dpse
					ON	dpse.meta_id = @parent_id
					AND	dpse.set_id = rr.set_id
					AND	permission_id = 8
					AND	permission_data = m.doc_type
					AND	(
							rr.set_id = 1
						OR	rr.set_id = 2
					)
WHERE 		urc.role_id = 0	
	OR	rr.set_id = 0
	OR	dpse.permission_id = 8
GROUP BY	t.meta_id

DROP TABLE #documents2

DECLARE documents_cursor CURSOR FOR
SELECT	meta.meta_id,
	description,
	doc_type,
	meta_headline,
	meta_text,
	meta_image,
	owner_id,
	permissions,
	shared,
	expand,
	show_meta,
	help_text_id,
	archive,
	status_id,
	lang_prefix,
	classification,
	date_created,
	date_modified,
	sort_position,
	menu_position,
	disable_search,
	activated_date,
	activated_time,
	archived_date,
	archived_time,
	target,
	frame_name,
	activate
FROM	meta, #documents d
WHERE	meta.meta_id = d.meta_id

OPEN documents_cursor

DECLARE @meta_id int,
	@description varchar(80),
	@doc_type int,
	@meta_headline varchar(255),
	@meta_text varchar(1000),
	@meta_image varchar(255),
	@owner_id int,
	@permissions int,
	@shared int,
	@expand int,
	@show_meta int,
	@help_text_id int,
	@archive int,
	@status_id int,
	@lang_prefix varchar(3),
	@classification varchar(200),
	@date_created datetime,
	@date_modified datetime,
	@sort_position int,
	@menu_position int,
	@disable_search int,
	@activated_date varchar(10),
	@activated_time varchar(6),
	@archived_date varchar(10),
	@archived_time varchar(6),
	@target varchar(10),
	@frame_name varchar(20),
	@activate int

FETCH NEXT FROM documents_cursor
INTO	@meta_id,
 	@description,
	@doc_type,
	@meta_headline,
	@meta_text,
	@meta_image,
	@owner_id,
	@permissions,
	@shared,
	@expand,
	@show_meta,
	@help_text_id,
	@archive,
	@status_id,
	@lang_prefix,
	@classification,
	@date_created,
	@date_modified,
	@sort_position,
	@menu_position,
	@disable_search,
	@activated_date,
	@activated_time,
	@archived_date,
	@archived_time,
	@target,
	@frame_name,
	@activate

WHILE (@@FETCH_STATUS = 0) BEGIN
	INSERT INTO meta (
		description,
		doc_type,
		meta_headline,
		meta_text,
		meta_image,
		owner_id,
		permissions,
		shared,
		expand,
		show_meta,
		help_text_id,
		archive,
		status_id,
		lang_prefix,
		classification,
		date_created,
		date_modified,
		sort_position,
		menu_position,
		disable_search,
		activated_date,
		activated_time,
		archived_date,
		archived_time,
		target,
		frame_name,
		activate
	) VALUES (
		@description,
		@doc_type,
		@meta_headline + ' (2)',
		@meta_text,
		@meta_image,
		@owner_id,
		@permissions,
		@shared,
		@expand,
		@show_meta,
		@help_text_id,
		@archive,
		@status_id,
		@lang_prefix,
		@classification,
		@date_created,
		@date_modified,
		@sort_position,
		@menu_position,
		@disable_search,
		@activated_date,
		@activated_time,
		@archived_date,
		@archived_time,
		@target,
		@frame_name,
		@activate
	)

	DECLARE @copy_id INT
	SET @copy_id = @@IDENTITY

	INSERT INTO text_docs 
	SELECT	@copy_id,
		template_id,
		group_id,
		sort_order,
		default_template_1,
		default_template_2
	FROM	text_docs
	WHERE	meta_id = @meta_id

	INSERT INTO url_docs
	SELECT	@copy_id,
		frame_name,
		target,
		url_ref,
		url_txt,
		lang_prefix
	FROM	url_docs
	WHERE	meta_id = @meta_id

	INSERT INTO browser_docs
	SELECT	@copy_id,
		to_meta_id,
		browser_id
	FROM	browser_docs
	WHERE	meta_id = @meta_id

	INSERT INTO frameset_docs
	SELECT	@copy_id,
		frame_set
	FROM	frameset_docs
	WHERE	meta_id = @meta_id

	INSERT INTO fileupload_docs
	SELECT	@copy_id,
		filename,
		mime
	FROM	fileupload_docs
	WHERE	meta_id = @meta_id

	INSERT INTO texts
	SELECT	@copy_id,
		name,
		text,
		type
	FROM	texts
	WHERE	meta_id = @meta_id

	INSERT INTO images
	SELECT	@copy_id,
		width,
		height,
		border,
		v_space,
		h_space,
		name,
		image_name,
		target,
		target_name,
		align,
		alt_text,
		low_scr,
		imgurl,
		linkurl
	FROM	images
	WHERE	meta_id = @meta_id

	INSERT INTO includes
	SELECT	@copy_id,
		include_id,
		included_meta_id
	FROM	includes
	WHERE	meta_id = @meta_id

	INSERT INTO doc_permission_sets
	SELECT	@copy_id,
		set_id,
		permission_id
	FROM	doc_permission_sets
	WHERE	meta_id = @meta_id

	INSERT INTO new_doc_permission_sets
	SELECT	@copy_id,
		set_id,
		permission_id
	FROM	new_doc_permission_sets
	WHERE	meta_id = @meta_id

	INSERT INTO doc_permission_sets_ex
	SELECT	@copy_id,
		set_id,
		permission_id,
		permission_data
	FROM	doc_permission_sets_ex
	WHERE	meta_id = @meta_id

	INSERT INTO new_doc_permission_sets_ex
	SELECT	@copy_id,
		set_id,
		permission_id,
		permission_data
	FROM	new_doc_permission_sets_ex
	WHERE	meta_id = @meta_id

	INSERT INTO roles_rights
	SELECT	role_id,
		@copy_id,
		set_id
	FROM	roles_rights
	WHERE	meta_id = @meta_id

	INSERT INTO user_rights
	SELECT	user_id,
		@copy_id,
		permission_id
	FROM	user_rights
	WHERE	meta_id = @meta_id

	INSERT INTO meta_classification
	SELECT	@copy_id,
		class_id
	FROM	meta_classification
	WHERE	meta_id = @meta_id

	INSERT INTO childs
	SELECT	@copy_id,
			to_meta_id,
			menu_sort,
			manual_sort_order
	FROM		childs
	WHERE	meta_id = @meta_id

	DECLARE @child_max INT
	-- FIXME: manual_sort_order should be an identity column
	SELECT @child_max = MAX(manual_sort_order)+10 FROM childs WHERE meta_id = @parent_id AND menu_sort = @menu_id
	INSERT INTO childs VALUES(@parent_id, @copy_id, @menu_id, @child_max)

	FETCH NEXT FROM documents_cursor
	INTO	@meta_id,
 		@description,
		@doc_type,
		@meta_headline,
		@meta_text,
		@meta_image,
		@owner_id,
		@permissions,
		@shared,
		@expand,
		@show_meta,
		@help_text_id,
		@archive,
		@status_id,
		@lang_prefix,
		@classification,
		@date_created,
		@date_modified,
		@sort_position,
		@menu_position,
		@disable_search,
		@activated_date,
		@activated_time,
		@archived_date,
		@archived_time,
		@target,
		@frame_name,
		@activate

END --WHILE

CLOSE documents_cursor
DEALLOCATE documents_cursor

DROP TABLE #documents


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO



-- 2001-09-26
INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(1048576,2,'se','Ändra include')
INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(1048576,2,'uk','Change include')

-- 2001-09-28 
GO
CREATE PROCEDURE GetTemplateId
 @aTemplatename varchar(80)
 AS

SELECT template_id
FROM templates
WHERE simple_name = @aTemplatename
GO

-- 2001-10-08


ALTER PROCEDURE Classification_Fix
--peter har gjort om denna så att den numera inte använder semicolon som separator utan endast komma
 @meta_id int ,
 @string varchar(2000)
AS
declare @value varchar(50)
declare @pos int
-- Lets delete all current crossreferences, if any
DELETE 
FROM meta_classification 
WHERE meta_id = @meta_id
--SELECT @string = 'ett;två;tre;fyra;fem'
-- Lets search for semicolon, if not found then look for a , This is relevant 
-- when we convert the db. After convertion, only look for semicolons
--SELECT @pos = PATINDEX('%;%', @string)
--IF( @pos = 0 ) BEGIN
 SELECT @pos = PATINDEX('%,%', @string)
--END
WHILE @pos > 0
BEGIN
 SELECT @value = LEFT(@string,@pos-1)
 SELECT @pos = LEN(@string) - @pos
 SELECT @string = RIGHT(@string,@pos)
 SELECT  @value  = lTrim(rTrim( ( @value ) )) 
 EXEC ClassificationAdd @meta_id , @value
 --INSERT INTO data (value) VALUES (@value)
 SELECT @pos = PATINDEX('%,%', @string)
 -- PRINT @value
END
-- Lets get the last part of the string
--PRINT @string
SELECT @value = @string
SELECT  @value  = lTrim(rTrim( ( @value ) )) 
EXEC ClassificationAdd @meta_id , @value
-- INSERT INTO data (value) VALUES (@string)
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO


-- 2001-10-29


--Changed the procedure so the prefix who thells its a copy must bee supplied as a param

ALTER PROCEDURE CopyDocs @documents_string VARCHAR(200), @parent_id INT, @menu_id INT, @user INT, @copyPrefix VARCHAR(20) AS
CREATE TABLE #documents (
  meta_id VARCHAR(10)
)
CREATE TABLE #documents2 (
  meta_id VARCHAR(10)
)
DECLARE @substring VARCHAR(30)
DECLARE @index INT
DECLARE @endindex INT
IF LEN(@documents_string) > 0 BEGIN
 SET @index = 1
 WHILE @index <= LEN(@documents_string) BEGIN
  SET @endindex = CHARINDEX(',',@documents_string,@index+1)
  IF @endindex = 0 BEGIN
   SET @endindex = LEN(@documents_string)+1
  END --IF
  SET @substring = SUBSTRING(@documents_string,@index,@endindex-@index)
  INSERT INTO #documents2 VALUES (@substring)
  SET @index = @endindex + 1
 END -- WHILE
END -- IF
INSERT INTO 	#documents
SELECT		t.meta_id
FROM		#documents2 t
JOIN		meta m
					ON	t.meta_id = m.meta_id
JOIN		user_roles_crossref urc
					ON	urc.user_id = @user
LEFT JOIN	roles_rights rr
					ON	rr.set_id < 3
					AND	rr.meta_id = @parent_id
					AND	rr.role_id = urc.role_id
LEFT JOIN	doc_permission_sets_ex dpse
					ON	dpse.meta_id = @parent_id
					AND	dpse.set_id = rr.set_id
					AND	permission_id = 8
					AND	permission_data = m.doc_type
					AND	(
							rr.set_id = 1
						OR	rr.set_id = 2
					)
WHERE 		urc.role_id = 0	
	OR	rr.set_id = 0
	OR	dpse.permission_id = 8
GROUP BY	t.meta_id
DROP TABLE #documents2
DECLARE documents_cursor CURSOR FOR
SELECT	meta.meta_id,
	description,
	doc_type,
	meta_headline,
	meta_text,
	meta_image,
	owner_id,
	permissions,
	shared,
	expand,
	show_meta,
	help_text_id,
	archive,
	status_id,
	lang_prefix,
	classification,
	date_created,
	date_modified,
	sort_position,
	menu_position,
	disable_search,
	activated_date,
	activated_time,
	archived_date,
	archived_time,
	target,
	frame_name,
	activate
FROM	meta, #documents d
WHERE	meta.meta_id = d.meta_id
OPEN documents_cursor
DECLARE @meta_id int,
	@description varchar(80),
	@doc_type int,
	@meta_headline varchar(255),
	@meta_text varchar(1000),
	@meta_image varchar(255),
	@owner_id int,
	@permissions int,
	@shared int,
	@expand int,
	@show_meta int,
	@help_text_id int,
	@archive int,
	@status_id int,
	@lang_prefix varchar(3),
	@classification varchar(200),
	@date_created datetime,
	@date_modified datetime,
	@sort_position int,
	@menu_position int,
	@disable_search int,
	@activated_date varchar(10),
	@activated_time varchar(6),
	@archived_date varchar(10),
	@archived_time varchar(6),
	@target varchar(10),
	@frame_name varchar(20),
	@activate int
FETCH NEXT FROM documents_cursor
INTO	@meta_id,
 	@description,
	@doc_type,
	@meta_headline,
	@meta_text,
	@meta_image,
	@owner_id,
	@permissions,
	@shared,
	@expand,
	@show_meta,
	@help_text_id,
	@archive,
	@status_id,
	@lang_prefix,
	@classification,
	@date_created,
	@date_modified,
	@sort_position,
	@menu_position,
	@disable_search,
	@activated_date,
	@activated_time,
	@archived_date,
	@archived_time,
	@target,
	@frame_name,
	@activate
WHILE (@@FETCH_STATUS = 0) BEGIN
	INSERT INTO meta (
		description,
		doc_type,
		meta_headline,
		meta_text,
		meta_image,
		owner_id,
		permissions,
		shared,
		expand,
		show_meta,
		help_text_id,
		archive,
		status_id,
		lang_prefix,
		classification,
		date_created,
		date_modified,
		sort_position,
		menu_position,
		disable_search,
		activated_date,
		activated_time,
		archived_date,
		archived_time,
		target,
		frame_name,
		activate
	) VALUES (
		@description,
		@doc_type,
		@meta_headline + @copyPrefix,
		@meta_text,
		@meta_image,
		@owner_id,
		@permissions,
		@shared,
		@expand,
		@show_meta,
		@help_text_id,
		@archive,
		@status_id,
		@lang_prefix,
		@classification,
		@date_created,
		@date_modified,
		@sort_position,
		@menu_position,
		@disable_search,
		@activated_date,
		@activated_time,
		@archived_date,
		@archived_time,
		@target,
		@frame_name,
		@activate
	)
	DECLARE @copy_id INT
	SET @copy_id = @@IDENTITY
	INSERT INTO text_docs 
	SELECT	@copy_id,
		template_id,
		group_id,
		sort_order,
		default_template_1,
		default_template_2
	FROM	text_docs
	WHERE	meta_id = @meta_id
	INSERT INTO url_docs
	SELECT	@copy_id,
		frame_name,
		target,
		url_ref,
		url_txt,
		lang_prefix
	FROM	url_docs
	WHERE	meta_id = @meta_id
	INSERT INTO browser_docs
	SELECT	@copy_id,
		to_meta_id,
		browser_id
	FROM	browser_docs
	WHERE	meta_id = @meta_id
	INSERT INTO frameset_docs
	SELECT	@copy_id,
		frame_set
	FROM	frameset_docs
	WHERE	meta_id = @meta_id
	INSERT INTO fileupload_docs
	SELECT	@copy_id,
		filename,
		mime
	FROM	fileupload_docs
	WHERE	meta_id = @meta_id
	INSERT INTO texts
	SELECT	@copy_id,
		name,
		text,
		type
	FROM	texts
	WHERE	meta_id = @meta_id
	INSERT INTO images
	SELECT	@copy_id,
		width,
		height,
		border,
		v_space,
		h_space,
		name,
		image_name,
		target,
		target_name,
		align,
		alt_text,
		low_scr,
		imgurl,
		linkurl
	FROM	images
	WHERE	meta_id = @meta_id
	INSERT INTO includes
	SELECT	@copy_id,
		include_id,
		included_meta_id
	FROM	includes
	WHERE	meta_id = @meta_id
	INSERT INTO doc_permission_sets
	SELECT	@copy_id,
		set_id,
		permission_id
	FROM	doc_permission_sets
	WHERE	meta_id = @meta_id
	INSERT INTO new_doc_permission_sets
	SELECT	@copy_id,
		set_id,
		permission_id
	FROM	new_doc_permission_sets
	WHERE	meta_id = @meta_id
	INSERT INTO doc_permission_sets_ex
	SELECT	@copy_id,
		set_id,
		permission_id,
		permission_data
	FROM	doc_permission_sets_ex
	WHERE	meta_id = @meta_id
	INSERT INTO new_doc_permission_sets_ex
	SELECT	@copy_id,
		set_id,
		permission_id,
		permission_data
	FROM	new_doc_permission_sets_ex
	WHERE	meta_id = @meta_id
	INSERT INTO roles_rights
	SELECT	role_id,
		@copy_id,
		set_id
	FROM	roles_rights
	WHERE	meta_id = @meta_id
	INSERT INTO user_rights
	SELECT	user_id,
		@copy_id,
		permission_id
	FROM	user_rights
	WHERE	meta_id = @meta_id
	INSERT INTO meta_classification
	SELECT	@copy_id,
		class_id
	FROM	meta_classification
	WHERE	meta_id = @meta_id
	INSERT INTO childs
	SELECT	@copy_id,
			to_meta_id,
			menu_sort,
			manual_sort_order
	FROM		childs
	WHERE	meta_id = @meta_id
	DECLARE @child_max INT
	-- FIXME: manual_sort_order should be an identity column
	SELECT @child_max = MAX(manual_sort_order)+10 FROM childs WHERE meta_id = @parent_id AND menu_sort = @menu_id
	INSERT INTO childs VALUES(@parent_id, @copy_id, @menu_id, @child_max)
	FETCH NEXT FROM documents_cursor
	INTO	@meta_id,
 		@description,
		@doc_type,
		@meta_headline,
		@meta_text,
		@meta_image,
		@owner_id,
		@permissions,
		@shared,
		@expand,
		@show_meta,
		@help_text_id,
		@archive,
		@status_id,
		@lang_prefix,
		@classification,
		@date_created,
		@date_modified,
		@sort_position,
		@menu_position,
		@disable_search,
		@activated_date,
		@activated_time,
		@archived_date,
		@archived_time,
		@target,
		@frame_name,
		@activate
END --WHILE
CLOSE documents_cursor
DEALLOCATE documents_cursor
DROP TABLE #documents
GO

-- 2001-10-30