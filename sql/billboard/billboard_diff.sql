
-- billboard_diff.sql,v
-- Revision 

--Changed name on the templatelib and the procedures that adds new one
GO
UPDATE B_templates
SET B_templates.template_lib = 'original'
WHERE B_templates.template_lib = 'Original'
GO

GO
ALTER PROCEDURE B_AddNewTemplateLib 
/*
 Lets add the templatelibrary where all the templates is situated on hd. This 
function is used when the administrator adds a new billboard. Used from sp
B_AddNewBillBoard
*/
	@meta_id int 
AS
DECLARE @thisTemplateId int
DECLARE @template_lib varchar(50)
SET @template_lib = 'original'
INSERT INTO B_templates ( template_lib )
VALUES  ( @template_lib )
/* Lets get the template_id id for the just created template */ 
SELECT  @thisTemplateId = @@identity
INSERT INTO B_billboard_templates ( billboard_id , template_id )
VALUES (@meta_id, @thisTemplateId )

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS OFF 
GO


GO
ALTER PROCEDURE B_GetTemplateLib
/*
 Lets get the folder library where all the templates are situated. if nothing is found for that
meta id , 'original' is returned instead
*/
	@meta_id int
AS
DECLARE @returnVal varchar(50)
SELECT @returnVal = t.template_lib
FROM B_billboard b , B_billboard_templates bt , B_templates t
WHERE t.template_id = bt.template_id
AND bt.billboard_id = b.meta_id
AND b.meta_id = @meta_id
SELECT @returnVal =  ISNULL(@returnVal, 'original') 
SELECT @returnVal AS 'TemplateLib'

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS OFF 
GO


GO
ALTER PROCEDURE B_AddNewTemplateLib 
/*
 Lets add the templatelibrary where all the templates is situated on hd. This 
function is used when the administrator adds a new billboard. Used from sp
B_AddNewBillBoard
*/
	@meta_id int 
AS
DECLARE @thisTemplateId int
DECLARE @template_lib varchar(50)
SET @template_lib = 'original'
INSERT INTO B_templates ( template_lib )
VALUES  ( @template_lib )
/* Lets get the template_id id for the just created template */ 
SELECT  @thisTemplateId = @@identity
INSERT INTO B_billboard_templates ( billboard_id , template_id )
VALUES (@meta_id, @thisTemplateId )

GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS OFF 
GO

-- 2001-10-25