SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[ExistingDocsGetSelectedMetaIds]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[ExistingDocsGetSelectedMetaIds]
GO


CREATE PROCEDURE ExistingDocsGetSelectedMetaIds
		@string varchar(1024) 
AS
/*
Creates a table with the meta id:s we are looking for.
ImcServices function ExistingDocsGetSelectedMetaIds is using this method
*/
SET NOCOUNT ON
-- Lets create the table where we gonna put the found products
CREATE TABLE #wanted_meta_id (
	meta_id INT PRIMARY KEY
)
DECLARE @substring VARCHAR(50)
DECLARE @index INT
DECLARE @endindex INT
IF LEN(@string) > 0 BEGIN
	SET @index = 1
	WHILE @index <= LEN(@string) BEGIN
		SET @endindex = CHARINDEX(',',@string,@index+1)
		IF @endindex = 0 BEGIN
			SET @endindex = LEN(@string)+1
		END -- IF
		SET @substring = SUBSTRING(@string,@index,@endindex-@index)
		SET @subString =  LTRIM (  RTRIM(@subString) )
		
		-- Lets check if the meta id already exists in the table
		DECLARE @foundMetaId int
		SET @foundMetaId = 0
		DECLARE @tmpId int
		SELECT @tmpId = CAST( @subString AS INT)
		SELECT @foundMetaId = meta_id 
		FROM #wanted_meta_id 
		WHERE #wanted_meta_id.meta_id  = @tmpId
		
		-- Lets insert the meta id:s into table
		IF ( @foundMetaId = 0 )
			INSERT INTO #wanted_meta_id  (meta_id ) VALUES (  @subString  ) 
		SET @index = @endindex + 1
	END -- WHILE
END -- IF
--SELECT * 
--FROM #wanted_meta_id
SELECT 	meta_id,
		doc_type,
		meta_headline,
		meta_text,
		date_created,
		date_modified,
		ISNULL(CONVERT(VARCHAR,NULLIF(activated_date,''''),121),'''') AS date_activated,
		ISNULL(CONVERT(VARCHAR,NULLIF(archived_date,''''),121),'''') AS date_archived,
		archive,
		shared,
		show_meta,
		disable_search
--		' + STR(@doc_count) + ' AS doc_count
FROM meta
WHERE meta_id IN 
	(	SELECT meta_id
		FROM #wanted_meta_id
	)
DROP TABLE #wanted_meta_id


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

