CREATE PROCEDURE GetTemplateId
 @aTemplatename varchar(80)
 AS
/**
	DOCME: Document me!
**/

SELECT template_id
FROM templates
WHERE simple_name = @aTemplatename


;
