if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[SearchDocsIndex]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SearchDocsIndex]
GO

SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS OFF 
GO


CREATE PROCEDURE SearchDocsIndex
  @user_id INT,
  @serchString varchar (180),  -- Must be large enough to encompass an entire searchstring.
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
          SET @archived_sd = '1753-01-01'
         END
       SET @archived_ed = '9999-12-31'
    END 
ELSE BEGIN
   SET @archived_ed = @archived_enddate
END

/* start setup table that contains the docctypes to search on */
CREATE TABLE #doc_types ( doc_type INT )

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
/*end doctype setup*/


/* here have we the start of the search engine stuff */
 create table #temp (meta_id int)
 create table #and ( meta_id int )
 create table #or    (meta_id int )
 create table #not  ( meta_id int )
 DECLARE @sWord varchar(180)
 DECLARE @start int
 DECLARE @stop int
 Declare @doneAnAnd int
 set @doneAnAnd = 0

 DECLARE @counter int
 declare @orCounter int
 set @orCounter = 0
 set @counter = 0

 IF LEN( @serchString ) > 0  BEGIN
	set @start = 1
	while @start <= LEN( @serchString ) BEGIN
		set @stop = charindex(',' , @serchString , @start + 1 )
		if  @stop = 0
		 begin
			set @stop =  len( @serchString ) +1
		 end --if 
		set @sWord = substring( @serchString , @start , @stop - @start )
		if @sWord like '"and"'  begin
			set @doneAnAnd = @doneAnAnd +1
			set @start = @stop +1
			set @stop = charindex(',' , @serchString , @start + 1 )
			if @stop = 0 begin
				set @stop =  len( @serchString ) +1
			end 
			if   @start <= LEN( @serchString ) BEGIN
				
				
				set @sWord = substring( @serchString , @start , @stop - @start )
				insert into #temp select meta_id from meta where contains(meta_headline,  @sWord )or contains(meta_text,  @sWord )
				insert into #temp select meta_id from texts where contains(text,  @sWord )
				insert into #temp select meta_id from classification, meta_classification where contains(code,  @sWord ) and meta_classification.class_id=classification.class_id
				select @counter = count(meta_id) from #and
				if @counter  > 0 begin
					delete from #temp where meta_id not in (select meta_id from #and) 
					delete from #and
				end
				insert into #and select * from #temp		
			end
		end 
		else if @sWord = '"not"'  begin
			set @start = @stop +1
			set @stop = charindex(',' , @serchString , @start + 1 )
			if @stop = 0 begin
				set @stop =  len( @serchString ) +1
			end 
			if   @start <= LEN( @serchString ) BEGIN
				set @sWord = substring( @serchString , @start , @stop - @start )
				insert into #not select meta_id from meta where contains(meta_headline,  @sWord )or contains(meta_text,  @sWord )
				insert into #not select meta_id from texts where contains(text,  @sWord )
				insert into #not select meta_id from classification, meta_classification where contains(code,  @sWord ) and meta_classification.class_id=classification.class_id
			end
		end
		else begin
			set @orCounter = @orCounter + 1
			if @stop = 0 begin
				set @stop =  len( @serchString ) +1
			end 
			if   @start <= LEN( @serchString ) BEGIN
				set @sWord = substring( @serchString , @start , @stop - @start )
				insert into #or select meta_id from meta where contains(meta_headline,  @sWord ) or contains(meta_text,  @sWord )
				insert into #or select meta_id from texts where contains(text,  @sWord )
				insert into #or select meta_id from classification, meta_classification where contains(code,  @sWord ) and meta_classification.class_id=classification.class_id
			end
		end	
		set @start = @stop + 1
	end --while
 END --begin

/*plockar ut de meta idn som ska vara kvar*/
if @orCounter = 0 begin
  insert into #or select meta_id from #and	
end
if @doneAnAnd  > 0 begin
	delete from #or where meta_id not in (select meta_id from #and) 
end
select @counter = count(meta_id) from #not
if @counter > 0 begin
	select @counter = count(meta_id) from #or
	if @counter > 0 begin
		delete from #or  where meta_id in(select meta_id from #not)
--		#or.meta_id = #not.meta_id
	end
end

--select  * from #or
/* end of the search engine*/

/*lets get the pages to return*/

SELECT  distinct
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
  meta_image
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

left  JOIN
  roles_rights rr  ON rr.meta_id = m.meta_id and m.meta_id != null
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
JOIN 
#or on #or.meta_id=m.meta_id and m.meta_id != null

GROUP BY
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
  meta_image
order by m.meta_headline


DROP TABLE #doc_types
drop table #temp
drop table #and
drop table #or
drop table #not
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

