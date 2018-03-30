CREATE PROCEDURE getTemplategroups AS
/**
	DOCME: Document me!
**/

select group_id,group_name from templategroups order by group_name
