SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[SearchDocs]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SearchDocs]
GO


CREATE PROCEDURE SearchDocs
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
  @only_addable TINYINT,  -- 1 to show only documents the user may add.
  @starts_with TINYINT -- 1 to search only on words that starts with the search-string
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
  @archived_ed DATETIME,
  @search_start VARCHAR(5)
if(@starts_with=1) Begin
	set @search_start='% '
end else begin
	set @search_start='%'
end
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
CREATE TABLE #keywords (
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
  activated_datetime,
  archived_datetime,
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
        activated_datetime >= @activated_sd
       AND activated_datetime <= @activated_ed
      ) OR (
        archived_datetime >= @archived_sd
       AND archived_datetime <= @archived_ed
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
  #keywords k  ON m.meta_headline  LIKE @search_start+k.keyword+'%'
     OR m.meta_text  LIKE @search_start+k.keyword+'%'
     OR t.text   LIKE @search_start+k.keyword+'%'


GROUP BY
  m.meta_id,
  k.keyword,
  m.doc_type,
  m.meta_headline,
  m.meta_text,
  m.date_created,
  m.date_modified,
  activated_datetime,
  archived_datetime,
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

