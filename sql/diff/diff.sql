-- diff.sql,v
-- Revision 1.2  2001/09/26 12:19:50  kreiger
-- Added default-templates to text_docs.
--


drop procedure [dbo].[CheckDocSharePermissionForUser]

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  OFF 
GO

CREATE PROCEDURE CheckUserDocSharePermission @user_id INT, @meta_id INT AS

SELECT m.meta_id
FROM meta m
JOIN user_roles_crossref urc
				ON	urc.user_id = @user_id
				AND	m.meta_id = @meta_id
LEFT join roles_rights rr
				ON	rr.meta_id = m.meta_id
				AND	rr.role_id = urc.role_id
WHERE				(
						shared = 1
					OR	rr.set_id < 3
					OR	urc.role_id = 0
				)
GROUP BY m.meta_id
GO

-- 2001-09-19

-- Add columns for default-templates to text-docs.

alter table text_docs 
add default_template_1 INT DEFAULT -1 NOT NULL

alter table text_docs 
add default_template_2 INT DEFAULT -1 NOT NULL


-- 2001/09/26 12:19:50
-- 2001-09-26
