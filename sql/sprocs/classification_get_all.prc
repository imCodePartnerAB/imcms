SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[Classification_Get_All]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[Classification_Get_All]
GO


CREATE PROCEDURE Classification_Get_All AS
/*
Get the meta_id and classifcation so we can start convert them
*/
SELECT meta_id, classification
 FROM meta
 WHERE classification IS NOT NULL
 and classification <> ''
 and classification NOT LIKE 'META NAME%'
 and classification NOT LIKE 'Test'


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

