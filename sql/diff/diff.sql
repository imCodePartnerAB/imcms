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

-- The procedure to suport the default_templates

CREATE PROCEDURE [UpdateDefaultTemplates] 
 @meta_id INT,
 @template1 int,
 @template2 int
 AS
UPDATE text_docs
SET default_template_1= @template1,
default_template_2=@template2 
WHERE meta_id = @meta_id
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

-- 2001-09-26
INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(1048576,2,'se','Ändra include')
INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(1048576,2,'uk','Change include')

-- 2001-09-28 
