SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[ListDocsByDate]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[ListDocsByDate]
GO


CREATE PROCEDURE ListDocsByDate @listMod int,  @doc_type int, @startDate varchar(10), @endDate varchar(20), @lang_prefix varchar(3) AS
/*
 lists doctyps where activate = 1
 @listMod 0 = all date, 1 = only creatdat, 2 = only modifieddata
 @startDoc yyyy-mm-dd or 0 then not set
 @endDate yyyy-mm-dd or 0 then not set
*/
-- Listdate fix 
if ( @endDate <> '0') BEGIN
 SET @endDate = @endDate + ' 23:59:59'
 PRINT @endDate
END 
/* list all (not in use ) */
if ( @listMod = 0) begin
 if ( @startDate = '0' ) begin
  if ( @endDate = '0' ) begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and activate = 1 and dt.lang_prefix = @lang_prefix
   order by m.date_modified
  end
  else begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_modified <= @endDate and activate = 1 and dt.lang_prefix = @lang_prefix
   order by m.date_modified
  end
 end
 else if ( @startDate != '0' ) begin
  if ( @endDate = '0' ) begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_modified >= @startDate and activate = 1 and dt.lang_prefix = @lang_prefix
   order by m.date_modified
  end
  else
  begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_modified <= @endDate and m.date_modified >= @startDate and activate = 1 and dt.lang_prefix = @lang_prefix
   order by m.date_modified
  end
 end
end
/* list creatdate */
else if ( @listMod = 1) begin
 if ( @startDate = '0' ) begin
  if ( @endDate = '0' ) begin
   select m.meta_id, dt.type, m.meta_headline, m.date_created
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and activate = 1 and dt.lang_prefix = @lang_prefix
   order by m.date_created
  end
  else begin
   select m.meta_id, dt.type, m.meta_headline, m.date_created
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_created <= @endDate and activate = 1 and dt.lang_prefix = @lang_prefix
   order by m.date_created
  end
 end
 else if ( @startDate != '0' ) begin
  if ( @endDate = '0' ) begin
   select m.meta_id, dt.type, m.meta_headline, m.date_created
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_created >= @startDate and activate = 1 and dt.lang_prefix = @lang_prefix
   order by m.date_created
  end
  else
  begin
   select m.meta_id, dt.type, m.meta_headline, m.date_created
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_created <= @endDate and m.date_created >= @startDate and activate = 1 and dt.lang_prefix = @lang_prefix
   order by m.date_created
  end
 end
end
/* list only modified*/
else begin
 if ( @startDate = '0' ) begin
  if ( @endDate = '0' ) begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and activate = 1 and m.date_modified != m.date_created and dt.lang_prefix = @lang_prefix
   order by m.date_modified
  end
  else begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_modified <= @endDate and activate = 1 and m.date_modified != m.date_created and dt.lang_prefix = @lang_prefix
   order by m.date_modified
  end
 end
 else if ( @startDate != '0' ) begin
  if ( @endDate = '0' ) begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_modified >= @startDate and activate = 1 and m.date_modified != m.date_created and dt.lang_prefix = @lang_prefix
   order by m.date_modified
  end
  else
  begin
   select m.meta_id, dt.type, m.meta_headline, m.date_modified
 
   from meta m
   join doc_types dt
      on m.doc_type = dt.doc_type
   where m.doc_type = @doc_type and m.date_modified <= @endDate and m.date_modified >= @startDate and activate = 1 and m.date_modified != m.date_created and dt.lang_prefix = @lang_prefix
   order by m.date_modified
  end
 end
end


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

