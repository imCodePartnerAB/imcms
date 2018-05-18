CREATE PROCEDURE getTemplategroups AS
/**
	DOCME: Document me!
**/

select group_id,group_name from imcms_template_group order by group_name
