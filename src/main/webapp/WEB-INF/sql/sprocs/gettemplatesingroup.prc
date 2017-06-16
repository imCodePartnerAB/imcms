CREATE PROCEDURE getTemplatesInGroup @grp_id INT AS

SELECT template_name
FROM templates_cref c
WHERE group_id = @grp_id
ORDER BY template_name
