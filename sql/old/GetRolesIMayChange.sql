DECLARE @meta_id INT
SET @meta_id = 2690
DECLARE @user_id INT
SET @user_id = 12006

/*
	List the roles, with their set_id and my set_id for this document.
	This is so i can know which i may change.
	If my set_id = 0, i may change set_id for all roles.
	If my set_id = 1, i may change set_id for all roles that have set_id 1 or higher, but i may only change roles with set_id 2 if my permission-bit 3 is set.
	If my set_id = 2, i may change set_id for all roles that have set_id 2 or higher.
	If my set_id >= 3, i may not change a thing.
*/

SELECT
		r.role_id,r.role_name,
		ISNULL(rr.set_id,4) AS role_set_id,									-- Each role's set_id
		MIN(ISNULL(rr2.set_id,4)) AS my_set_id,									-- My most privileged (lowest) set_id
		CAST(MAX(ISNULL(dps.permission_id&8,0)) AS BIT) AS my_permission_to_define_permission_set_2		-- Is bit 3 ( 2^3 = 8 ) set in my permissions for one of my roles for this document?
															-- (Do i have permission to define permission_set 2 for this document?)
															-- (Only meaningful if my set_id = 1)
FROM
		roles r
LEFT JOIN 
		roles_rights rr 							-- The roles_rights for each role
						ON 	rr.role_id = r.role_id			-- Select from roles_rights set_id for the roles we are interested in (all except superadmin)
						AND	rr.meta_id = @meta_id			-- Select from roles_rights set_id for this document
LEFT JOIN	user_roles_crossref urc
						ON	urc.user_id = @user_id			-- Now we need to find out which roles the user has
LEFT JOIN
		roles_rights rr2							-- The roles_rights for my roles. Note that my roles are independent of the roles listed here, though my roles are listed too.
						ON	urc.role_id = rr2.role_id			-- Just my roles
						AND	rr2.meta_id = @meta_id			-- And the set_ids for this document.
LEFT JOIN
		doc_permission_sets dps						-- My permission_set
						ON	dps.meta_id = @meta_id			-- For this document
						AND	dps.set_id = rr2.set_id			-- And my roles
WHERE	r.role_id > 0
GROUP BY	r.role_id	,r.role_name,rr.role_id,rr.meta_id,rr.set_id, ISNULL(rr.set_id,4)
ORDER BY	role_name


--select * from roles_rights where meta_id = 2690
--select * from user_roles_crossref where role_id = 5
