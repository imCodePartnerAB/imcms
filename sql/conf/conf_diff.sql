-- confdiff.sql,v
-- Revision 


--Changed name on the templatelib and the procedures that adds new one
GO
UPDATE A_templates
SET A_templates.template_lib = 'original'
WHERE A_templates.template_lib = 'Original'
GO
GO
ALTER PROCEDURE A_AddNewTemplateLib 

/*
 Lets add the templatelibrary where all the templates is situated on hd. This 
function is used when the administrator adds a new conference. Used from sp
AddNewConf
*/
	@meta_id int 
AS
DECLARE @thisTemplateId int
DECLARE @template_lib varchar(50)
SET @template_lib = 'original'
INSERT INTO A_templates ( template_lib )
VALUES  ( @template_lib )
/* Lets get the template_id id for the just created template */ 
SELECT  @thisTemplateId = @@identity
INSERT INTO A_conf_templates ( conf_id , template_id )
VALUES (@meta_id, @thisTemplateId )
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO
GO
ALTER PROCEDURE A_GetTemplateLib
/*
 Lets get the folder library where all the templates are situated. if nothing is found for that
meta id , 'original' is returned instead
*/
	@meta_id int
AS
DECLARE @returnVal varchar(50)
SELECT @returnVal = t.template_lib
FROM A_conference c , A_conf_templates ct , A_templates t
WHERE t.template_id = ct.template_id
AND ct.conf_id = c.meta_id
AND c.meta_id = @meta_id
SELECT @returnVal =  ISNULL(@returnVal, 'original') 
SELECT @returnVal AS 'TemplateLib'
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO
-- 2001-10-24

-- ************* Here is imCMS v1_5_0-pre10 tagged  *******************************

-- did changes to procedure so that it changes an conferences templateset to another one
ALTER PROCEDURE A_SetTemplateLib
/*
 Used when an admin wants to change the conferences template set to another one
*/
	@meta_id int , 
	 @new_lib_id varchar(50) 
AS
UPDATE A_conf_templates
SET template_id= @new_lib_id
WHERE conf_id = @meta_id
GO
-- 2002-01-23