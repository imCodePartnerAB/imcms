SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

/****** Object:  Stored Procedure dbo.CheckForFileDocs    Script Date: 2002-09-24 12:08:20 ******/
if exists (select * from sysobjects where id = object_id('dbo.CheckForFileDocs') and sysstat & 0xf = 4)
	drop procedure dbo.CheckForFileDocs
GO



CREATE     PROCEDURE CheckForFileDocs @documents_string VARCHAR(200) AS
/**
	This procedure takes a list of document-ids (meta_ids)
	and returns a list of which of those are file-docs.
**/

CREATE TABLE #documents (
  meta_id INT
)
DECLARE @substring VARCHAR(30);
DECLARE @index INT;
DECLARE @endindex INT;
IF LEN(@documents_string) > 0 BEGIN
 SET @index = 1
 WHILE @index <= LEN(@documents_string) BEGIN
  SET @endindex = CHARINDEX(',',@documents_string,@index+1)
  IF @endindex = 0 BEGIN
   SET @endindex = LEN(@documents_string)+1
  END --IF
  SET @substring = SUBSTRING(@documents_string,@index,@endindex-@index)
  INSERT INTO #documents VALUES (CAST(@substring AS INT))
  SET @index = @endindex + 1
 END -- WHILE
END -- IF


SELECT meta.meta_id FROM meta
JOIN #documents
ON meta.meta_id = #documents.meta_id
WHERE doc_type = 8
ORDER BY meta.meta_headline;

drop table #documents;


GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

