SET QUOTED_IDENTIFIER OFF
GO
SET ANSI_NULLS ON
GO

/****** Object:  Stored Procedure imcms.CopyDocs    Script Date: 2002-09-24 12:03:12 ******/
if exists (select * from sysobjects where id = object_id('dbo.CopyDocs') and sysstat & 0xf = 4)
	drop procedure dbo.CopyDocs
GO







CREATE    PROCEDURE CopyDocs @documents_string VARCHAR(200), @parent_id INT, @menu_id INT, @user INT, @copyPrefix VARCHAR(20) AS
/**
   @documents_string =  commasepatated string with meta_id´s
   @parent_id = parent document
   @menu_id = menu-number in parent document
   @user = user id
   @copyPrefix = prefix added to meta_headline

   Make a copy off all meta passed in @documents_string and all data referensed to it.
**/

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
	doc_type,
	meta_headline,
	meta_text,
	meta_image,
	owner_id,
	permissions,
	shared,
	show_meta,
	lang_prefix,
	date_created,
	date_modified,
	disable_search,
	archived_datetime,
	target,
	activate,
	status,
	publication_start_datetime,
	publication_end_datetime
FROM	meta, #documents d
WHERE	meta.meta_id = d.meta_id
OPEN documents_cursor
DECLARE @meta_id int,
	@doc_type int,
	@meta_headline varchar(255),
	@meta_text varchar(1000),
	@meta_image varchar(255),
	@owner_id int,
	@permissions int,
	@shared int,
	@show_meta int,
	@lang_prefix varchar(3),
	@date_created datetime,
	@date_modified datetime,
	@disable_search int,
	@archived_datetime datetime,
	@target varchar(10),
	@activate int,
	@status int,
	@publication_start_datetime datetime,
	@publication_end_datetime datetime
FETCH NEXT FROM documents_cursor
INTO	@meta_id,
	@doc_type,
	@meta_headline,
	@meta_text,
	@meta_image,
	@owner_id,
	@permissions,
	@shared,
	@show_meta,
	@lang_prefix,
	@date_created,
	@date_modified,
	@disable_search,
	@archived_datetime,
	@target,
	@activate,
	@status,
	@publication_start_datetime,
	@publication_end_datetime
WHILE (@@FETCH_STATUS = 0) BEGIN
	INSERT INTO meta (
		doc_type,
		meta_headline,
		meta_text,
		meta_image,
		owner_id,
		permissions,
		shared,
		show_meta,
		lang_prefix,
		date_created,
		date_modified,
		disable_search,
		archived_datetime,
		target,
		activate,
		status,
		publication_start_datetime,
		publication_end_datetime
	) VALUES (
		@doc_type,
		@meta_headline + @copyPrefix,
		@meta_text,
		@meta_image,
		@owner_id,
		@permissions,
		@shared,
		@show_meta,
		@lang_prefix,
		@date_created,
		@date_modified,
		@disable_search,
		@archived_datetime,
		@target,
		@activate,
		0,
		GETDATE(),
		@publication_end_datetime
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
	INSERT INTO meta_section
	SELECT 	@copy_id,
       		section_id
	FROM   	meta_section
	WHERE  	meta_id = @meta_id
	INSERT INTO childs
	SELECT	@copy_id,
			to_meta_id,
			menu_sort,
			manual_sort_order,
			tree_sort_index
	FROM		childs
	WHERE	meta_id = @meta_id
	DECLARE @child_max INT
	-- FIXME: manual_sort_order should be an identity column
	SELECT @child_max = MAX(manual_sort_order)+10 FROM childs WHERE meta_id = @parent_id AND menu_sort = @menu_id
	INSERT INTO childs (meta_id, to_meta_id, menu_sort, manual_sort_order, tree_sort_index) VALUES(@parent_id, @copy_id, @menu_id, @child_max, '')
	FETCH NEXT FROM documents_cursor
	INTO	@meta_id,
		@doc_type,
		@meta_headline,
		@meta_text,
		@meta_image,
		@owner_id,
		@permissions,
		@shared,
		@show_meta,
		@lang_prefix,
		@date_created,
		@date_modified,
		@disable_search,
		@archived_datetime,
		@target,
		@activate,
		@status,
		@publication_start_datetime,
		@publication_end_datetime
END --WHILE
CLOSE documents_cursor
DEALLOCATE documents_cursor
DROP TABLE #documents


update meta
   set 	date_created=getDate(),
	date_modified=getDate()
   where meta_id=@copy_id




GO

SET QUOTED_IDENTIFIER OFF
GO
SET ANSI_NULLS ON
GO

