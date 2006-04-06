CREATE PROCEDURE getTemplatesInGroup @grp_id INT AS
/**
	DOCME: Document me!
**/

SELECT t.template_id,simple_name
FROM  templates t JOIN
  templates_cref c
ON  t.template_id = c.template_id
WHERE c.group_id = @grp_id
ORDER BY simple_name
