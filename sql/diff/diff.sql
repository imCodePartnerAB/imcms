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
-- Added collation type, to make SearchDocs procedure to work on SQL 2000

GO
alter PROCEDURE SearchDocs
  @user_id INT,
  @keyword_string VARCHAR(128),  -- Must be large enough to encompass an entire searchstring.
  @and_mode VARCHAR(3),  -- 'AND' or something else
  @doc_types_string VARCHAR(30), -- Must be large enough to encompass all possible doc_types, commaseparated and expressed in decimal notation.
  @fromdoc INT,
  @num_docs INT,
  @sortorder VARCHAR(256),  -- doc_type, date_modified, date_created, archived_datetime, activated_datetime, meta_id, meta_headline
  @created_startdate DATETIME,
  @created_enddate DATETIME,
  @modified_startdate DATETIME,
  @modified_enddate DATETIME,
  @activated_startdate DATETIME,
  @activated_enddate DATETIME,
  @archived_startdate DATETIME,
  @archived_enddate DATETIME,
  @only_addable TINYINT  -- 1 to show only documents the user may add.
AS
/*
SET @keyword_string = 'kreiger'+CHAR(13)+'test'
SET @doc_types_string = '2,5,6,7,8,101,102'
SET @created_startdate = '1999-09-01'
SET @created_enddate = '2000-01-01'
SET @modified_startdate = ''
SET @modified_enddate = ''
SET @activated_startdate = ''
SET @activated_enddate = ''
SET @archived_startdate = ''
SET @archived_enddate = ''
SET @and_mode = 'OR'
SET @user_id = 98
SET @fromdoc = 1
SET @num_docs = 10
SET @sortorder = 'doc_type,meta_id DESC'
*/
SET nocount on
SET @fromdoc = @fromdoc - 1
DECLARE @created_sd DATETIME,
  @modified_sd DATETIME,
  @activated_sd DATETIME,
  @archived_sd DATETIME,
  @created_ed DATETIME,
  @modified_ed DATETIME,
  @activated_ed DATETIME,
  @archived_ed DATETIME
IF (@created_startdate = '') BEGIN
 SET @created_sd = '1753-01-01'
END ELSE BEGIN
 SET @created_sd = @created_startdate
END
IF (@modified_startdate = '') BEGIN
 SET @modified_sd = '1753-01-01'
END ELSE BEGIN
 SET @modified_sd = @modified_startdate
END
IF (@activated_startdate = '') BEGIN
 SET @activated_sd = '1753-01-01'
END ELSE BEGIN
 SET @activated_sd = @activated_startdate
END
IF (@archived_startdate = '') BEGIN
 SET @archived_sd = '1753-01-01'
END ELSE BEGIN
 SET @archived_sd = @archived_startdate
END
IF (@created_enddate = '') BEGIN
 IF (@created_startdate = '') BEGIN
  SET @created_ed = '1753-01-01'
 END ELSE BEGIN
  SET @created_ed = '9999-12-31'
 END
END ELSE BEGIN
 SET @created_ed = @created_enddate
END
IF (@modified_enddate = '') BEGIN
 IF (@modified_startdate = '') BEGIN
  SET @modified_ed = '1753-01-01'
 END ELSE BEGIN
  SET @modified_ed = '9999-12-31'
 END
END ELSE BEGIN
 SET @modified_ed = @modified_enddate
END
IF (@activated_enddate = '') BEGIN
 IF (@activated_startdate = '') BEGIN
  SET @activated_ed = '1753-01-01'
 END ELSE BEGIN
  SET @activated_ed = '9999-12-31'
 END
END ELSE BEGIN
 SET @activated_ed = @activated_enddate
END
IF (@archived_enddate = '') BEGIN
 IF (@archived_startdate = '') BEGIN
  SET @archived_ed = '1753-01-01'
 END ELSE BEGIN
  SET @archived_ed = '9999-12-31'
 END
END ELSE BEGIN
 SET @archived_ed = @archived_enddate
END
CREATE TABLE #doc_types (
 doc_type INT
)
CREATE TABLE  #keywords (
  keyword VARCHAR(30)
)


DECLARE @substring VARCHAR(30)
DECLARE @index INT
DECLARE @endindex INT
IF LEN(@doc_types_string) > 0 BEGIN
 SET @index = 1
 WHILE @index <= LEN(@doc_types_string) BEGIN
  SET @endindex = CHARINDEX(',',@doc_types_string,@index+1)
  IF @endindex = 0 BEGIN
   SET @endindex = LEN(@doc_types_string)+1
  END --IF
  SET @substring = SUBSTRING(@doc_types_string,@index,@endindex-@index)
  INSERT INTO #doc_types VALUES (@substring)
  SET @index = @endindex + 1
 END -- WHILE
END -- IF
IF LEN(@keyword_string) > 0 BEGIN
 SET @index = 1
 WHILE @index <= LEN(@keyword_string) BEGIN
  SET @endindex = CHARINDEX(CHAR(13),@keyword_string,@index+1)
  IF @endindex = 0 BEGIN
   SET @endindex = LEN(@keyword_string)+1
  END --IF
  SET @substring = SUBSTRING(@keyword_string,@index,@endindex-@index)
  INSERT INTO #keywords VALUES (@substring)
  SET @index = @endindex + 1
 END -- WHILE
END -- IF
DECLARE @num_keywords INT
SELECT @num_keywords = COUNT(keyword) from #keywords
/* A table to contain all the pages matched, one row per keyword matched */
CREATE TABLE #keywords_matched (
 meta_id INT,
 doc_type INT,
 meta_headline VARCHAR(256),
 meta_text VARCHAR(1024),
 date_created DATETIME,
 date_modified DATETIME,
 date_activated DATETIME,
 date_archived DATETIME,
 archive TINYINT,
 shared TINYINT,
 show_meta TINYINT,
 disable_search TINYINT,
 keyword VARCHAR(30) 
 
)
INSERT INTO #keywords_matched
SELECT  
  m.meta_id,
  m.doc_type,
  m.meta_headline,
  m.meta_text,
  m.date_created,
  m.date_modified,
  CAST(activated_date+' '+activated_time AS DATETIME),
  CAST(archived_date+' '+archived_time AS DATETIME),
  archive,
  shared,
  show_meta,
  disable_search,
  k.keyword
FROM
  meta m
JOIN
  #doc_types dt  ON m.doc_type = dt.doc_type
     AND activate = 1
     AND (
       (
        date_created >= @created_sd
       AND date_created <= @created_ed
      ) OR (
        date_modified >= @modified_sd
       AND date_modified <= @modified_ed
      ) OR (
        CAST(activated_date+' '+activated_time AS DATETIME) >= @activated_sd
       AND CAST(activated_date+' '+activated_time AS DATETIME) <= @activated_ed
      ) OR (
        CAST(archived_date+' '+archived_time AS DATETIME) >= @archived_sd
       AND CAST(archived_date+' '+archived_time AS DATETIME) <= @archived_ed
      ) OR (
        @created_startdate = ''
       AND @created_enddate = ''
       AND @modified_startdate = ''
       AND @modified_enddate = ''
       AND @activated_startdate = ''
       AND @activated_enddate = ''
       AND @archived_startdate = ''
       AND @archived_enddate = ''
      )
     )
LEFT JOIN
  roles_rights rr  ON rr.meta_id = m.meta_id
JOIN
  user_roles_crossref urc ON urc.user_id = @user_id
     AND (
       urc.role_id = 0   -- Superadmin may always see everything
      OR (
        rr.role_id = urc.role_id  -- As does a user...
       AND (
         rr.set_id < 3   -- ... with a privileged role
        OR (
          (
           rr.set_id = 3   -- ... or a user with read-rights
          OR show_meta != 0   -- ... or if the document lets anyone see
         )
         AND m.disable_search = 0   -- ... that is, if searching is not turned off for this document
         AND (
           m.shared != 0   -- ... and the document is shared
          OR @only_addable = 0  -- ... unless we've selected to only see addable (shared) documents.
         )
        )
       )
      )
     )
LEFT JOIN
  texts t   ON m.meta_id = t.meta_id
JOIN
  #keywords k  ON m.meta_headline COLLATE SQL_SwedishStd_Pref_Cp1_CI_AS  LIKE '%'+k.keyword+'%' COLLATE SQL_SwedishStd_Pref_Cp1_CI_AS
     OR m.meta_text COLLATE SQL_SwedishStd_Pref_Cp1_CI_AS LIKE '%'+k.keyword+'%' COLLATE SQL_SwedishStd_Pref_Cp1_CI_AS
     OR t.text COLLATE SQL_SwedishStd_Pref_Cp1_CI_AS   LIKE '%'+k.keyword+'%' COLLATE SQL_SwedishStd_Pref_Cp1_CI_AS
GROUP BY
  m.meta_id,
  k.keyword,
  m.doc_type,
  m.meta_headline,
  m.meta_text,
  m.date_created,
  m.date_modified,
  CAST(activated_date+' '+activated_time AS DATETIME),
  CAST(archived_date+' '+archived_time AS DATETIME),
  archive,
  shared,
  show_meta,
  disable_search
DECLARE @doc_count INT
SET @doc_count = @@ROWCOUNT
IF @and_mode = 'AND' BEGIN
 DELETE FROM #keywords_matched
 WHERE meta_id IN (
  SELECT meta_id
  FROM #keywords_matched
  GROUP BY meta_id
  HAVING (COUNT(keyword) < @num_keywords)
 )
 SET @doc_count = @doc_count - @@ROWCOUNT
END
DECLARE @eval VARCHAR(2000)
SET @eval = ('
IF '+STR(@fromdoc)+' > 0 BEGIN
 DELETE FROM #keywords_matched
 WHERE meta_id IN (
  SELECT TOP '+STR(@fromdoc)+' meta_id FROM #keywords_matched
  ORDER BY meta_id
 )
END
SELECT TOP '+STR(@num_docs)+' 
  meta_id,
  doc_type,
  meta_headline,
  meta_text,
  date_created,
  date_modified,
  ISNULL(CONVERT(VARCHAR,NULLIF(date_activated,''''),121),'''') AS date_activated,
  ISNULL(CONVERT(VARCHAR,NULLIF(date_archived,''''),121),'''') AS date_archived,
  archive,
  shared,
  show_meta,
  disable_search,
  ' + STR(@doc_count) + ' AS doc_count
FROM
  #keywords_matched
GROUP BY
  meta_id,
  doc_type,
  meta_headline,
  meta_text,
  date_created,
  date_modified,
  date_activated,
  date_archived,
  archive,
  shared,
  show_meta,
  disable_search
ORDER BY 
  '+@sortorder)
EXEC (@eval)
DROP TABLE #keywords
DROP TABLE #doc_types
DROP TABLE #keywords_matched
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

-- 2001-10-15