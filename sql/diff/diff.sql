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

--Changed the procedure so it only gets the document ones

ALTER PROCEDURE ListDocsGetInternalDocTypesValue AS
/* selct all internal doc types */
select distinct doc_type
from doc_types
where doc_type <= 100
GO

-- 2001-11-12

--Changed the procedure so it only select stuff from one langue at the time

ALTER PROCEDURE [GetPermissionSet] @meta_id INT, @set_id INT, @lang_prefix VARCHAR(3) AS
/*
 Nice little query that returns which permissions a permissionset consists of.
 Column 1: The id of the permission
 Column 2: The description of the permission
 Column 3: Wether the permission is set. 0 or 1.
*/
SELECT p.permission_id AS p_id, p.description,CAST(ISNULL((p.permission_id & dps.permission_id),0) AS BIT)
FROM   doc_permission_sets dps
RIGHT JOIN permissions p
       ON (p.permission_id & dps.permission_id) > 0
       WHERE dps.meta_id = @meta_id
       AND dps.set_id = @set_id
       AND p.lang_prefix = @lang_prefix
UNION
SELECT dp.permission_id AS p_id, dp.description,CAST(ISNULL((dp.permission_id & dps.permission_id),0) AS BIT)
FROM   meta m
JOIN  doc_permissions dp
       ON dp.doc_type = m.doc_type
       AND m.meta_id = @meta_id
       AND dp.lang_prefix = @lang_prefix
LEFT JOIN doc_permission_sets dps
       ON (dp.permission_id & dps.permission_id) > 0
       AND dps.set_id = @set_id
       AND dps.meta_id = m.meta_id
GO

-- 2001-11-13

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
DROP PROCEDURE GetChilds
GO
--
-- Procedure Create
-- dbo.GetChilds
--
CREATE PROCEDURE GetChilds
 @meta_id int,
 @user_id int
AS
/*
Nice little query that lists the children of a document that a particular user may see, and includes a field that tells you wether he may do something to it or not.
*/
declare @sort_by int
select @sort_by = sort_order from text_docs where meta_id = @meta_id
-- Manual sort order
if @sort_by = 2
begin
select  to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, convert (varchar,date_created,120), convert (varchar,date_modified,120),
  meta_headline,meta_text,meta_image,frame_name,
  activated_datetime,archived_datetime,
  min(urc.role_id * ISNULL(~CAST(dps.permission_id AS BIT),1) * ISNULL(rr.set_id,1)),
  fd.filename
from   childs c
join   meta m    
     on    m.meta_id = c.to_meta_id     -- meta.meta_id corresponds to childs.to_meta_id
     and  m.activate > 0       -- Only include the documents that are active in the meta table.
     and  c.meta_id = @meta_id      -- Only include documents that are children to this particular meta_id
left join roles_rights rr            -- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
     on  c.to_meta_id = rr.meta_id      -- Only include rows with the documents we are interested in
left join doc_permission_sets dps           -- Include the permission_sets
     on  c.to_meta_id = dps.meta_id     -- for each document
     and dps.set_id = rr.set_id      -- and only the sets for the roles we are interested in
     and dps.permission_id > 0      -- and only the sets that have any permission
join user_roles_crossref urc           -- This table tells us which users have which roles
     on urc.user_id = @user_id      -- Only include the rows with the user we are interested in...
     and ( 
       rr.role_id = urc.role_id     -- Include rows where the users roles match the roles that have permissions on the documents
      or  urc.role_id = 0      -- and also include the rows that tells us this user is a superadmin
      or  (
        m.show_meta != 0    -- and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
       and ISNULL(~CAST(dps.permission_id AS BIT),1) != 1
      )
     )
left join fileupload_docs fd
     on  fd.meta_id = c.to_meta_id
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, convert (varchar,date_created,120), convert (varchar,date_modified,120),
  meta_headline,meta_text,meta_image,frame_name,
  activated_datetime,archived_datetime,
  fd.filename
order by  menu_sort,c.manual_sort_order desc
end
else if @sort_by = 3
begin
select  to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, convert (varchar,date_created,120), convert (varchar,date_modified,120),
  meta_headline,meta_text,meta_image,frame_name,
  activated_datetime,archived_datetime,
  min(urc.role_id * ISNULL(~CAST(dps.permission_id AS BIT),1) * ISNULL(rr.set_id,1)),
  fd.filename
from   childs c
join   meta m    
     on    m.meta_id = c.to_meta_id     -- meta.meta_id corresponds to childs.to_meta_id
     and  m.activate > 0       -- Only include the documents that are active in the meta table.
     and  c.meta_id = @meta_id      -- Only include documents that are children to this particular meta_id
left join roles_rights rr            -- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
     on  c.to_meta_id = rr.meta_id      -- Only include rows with the documents we are interested in
left join doc_permission_sets dps           -- Include the permission_sets
     on  c.to_meta_id = dps.meta_id     -- for each document
     and dps.set_id = rr.set_id      -- and only the sets for the roles we are interested in
     and dps.permission_id > 0      -- and only the sets that have any permission
join user_roles_crossref urc           -- This table tells us which users have which roles
     on urc.user_id = @user_id      -- Only include the rows with the user we are interested in...
     and ( 
       rr.role_id = urc.role_id     -- Include rows where the users roles match the roles that have permissions on the documents
      or  urc.role_id = 0      -- and also include the rows that tells us this user is a superadmin
      or  (
        m.show_meta != 0    -- and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
       and ISNULL(~CAST(dps.permission_id AS BIT),1) != 1
      )
     )
left join fileupload_docs fd
     on  fd.meta_id = c.to_meta_id
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, convert (varchar,date_created,120), convert (varchar,date_modified,120),
  meta_headline,meta_text,meta_image,frame_name,
  activated_datetime,archived_datetime,
  fd.filename
order by  menu_sort,convert (varchar,date_created,120) desc
end
else if @sort_by = 1
begin
select  to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, convert (varchar,date_created,120), convert (varchar,date_modified,120),
  meta_headline,meta_text,meta_image,frame_name,
  activated_datetime,archived_datetime,
  min(urc.role_id * ISNULL(~CAST(dps.permission_id AS BIT),1) * ISNULL(rr.set_id,1)),
  fd.filename
from   childs c
join   meta m    
     on    m.meta_id = c.to_meta_id     -- meta.meta_id corresponds to childs.to_meta_id
     and  m.activate > 0       -- Only include the documents that are active in the meta table.
     and  c.meta_id = @meta_id      -- Only include documents that are children to this particular meta_id
left join roles_rights rr            -- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
     on  c.to_meta_id = rr.meta_id      -- Only include rows with the documents we are interested in
left join doc_permission_sets dps           -- Include the permission_sets
     on  c.to_meta_id = dps.meta_id     -- for each document
     and dps.set_id = rr.set_id      -- and only the sets for the roles we are interested in
     and dps.permission_id > 0      -- and only the sets that have any permission
join user_roles_crossref urc           -- This table tells us which users have which roles
     on urc.user_id = @user_id      -- Only include the rows with the user we are interested in...
     and ( 
       rr.role_id = urc.role_id     -- Include rows where the users roles match the roles that have permissions on the documents
      or  urc.role_id = 0      -- and also include the rows that tells us this user is a superadmin
      or  (
        m.show_meta != 0    -- and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
       and ISNULL(~CAST(dps.permission_id AS BIT),1) != 1
      )
     )
left join fileupload_docs fd
     on  fd.meta_id = c.to_meta_id
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, convert (varchar,date_created,120), convert (varchar,date_modified,120),
  meta_headline,meta_text,meta_image,frame_name,
  activated_datetime,archived_datetime,
  fd.filename
order by  menu_sort,meta_headline
end
GO

GO
DROP PROCEDURE CopyDocs
GO
CREATE PROCEDURE CopyDocs @documents_string VARCHAR(200), @parent_id INT, @menu_id INT, @user INT, @copyPrefix VARCHAR(20) AS
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
	activated_datetime,
	archived_datetime,
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
	@activated_datetime datetime,
	@archived_datetime datetime,
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
	@activated_datetime,
	@archived_datetime,
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
		activated_datetime,
		archived_datetime,
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
		@activated_datetime,
		@archived_datetime,
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
		@activated_datetime,
		@archived_datetime,
		@target,
		@frame_name,
		@activate
END --WHILE
CLOSE documents_cursor
DEALLOCATE documents_cursor
DROP TABLE #documents
GO


-- 2001-11-15