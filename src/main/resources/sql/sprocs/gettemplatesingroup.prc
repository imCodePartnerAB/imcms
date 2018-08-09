CREATE PROCEDURE getTemplatesInGroup @grp_id INT AS

SELECT template_name
FROM imcms_template_group_crossref c
WHERE group_id = @grp_id
ORDER BY template_name
