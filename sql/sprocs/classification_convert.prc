SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[classification_convert]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[classification_convert]
GO


CREATE PROCEDURE classification_convert AS
-- Hämta alla klassificeringskoder och för över dem till nya systemet. Observera att denna inte fixar
-- de fält som har , tecken som delimiter
DECLARE @meta_id int
DECLARE @class varchar(200)
DECLARE tmpCursor CURSOR FOR
 SELECT meta_id, classification
 FROM meta
 WHERE classification IS NOT NULL
 and classification <> ''
 and classification NOT LIKE 'META NAME%'
 and classification NOT LIKE 'Test'
 --AND meta_id = 1014
OPEN tmpCursor
FETCH NEXT FROM tmpCursor INTO @meta_id, @class
WHILE @@fetch_status = 0 BEGIN
 PRINT 'Class: ' + @class 
 EXEC classification_fix @meta_id, @class 
 FETCH NEXT FROM tmpCursor INTO @meta_id, @class
END
CLOSE tmpCursor
DEALLOCATE tmpCursor


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

