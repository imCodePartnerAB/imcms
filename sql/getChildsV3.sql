declare @meta_id int
declare @user_id int

set @meta_id = 2690
set @user_id = 98

select		to_meta_id, c.menu_sort,manual_sort_order, doc_type,
		archive,target, left(convert (varchar,date_created,120),10),
		meta_headline,meta_text,meta_image,frame_name,
		activated_date+activated_time,archived_date+archived_time,
		urc.role_id,rr.permission_id,ur.permission_id,
		min(coalesce(urc.role_id,rr.permission_id-3,ur.permission_id-99)*
		coalesce(ur.permission_id-99,rr.permission_id-3,urc.role_id)*
		coalesce(rr.permission_id-3,urc.role_id,ur.permission_id-99)
		)
		

from 		childs c
join 		meta m 			
					on 		m.meta_id = c.to_meta_id				-- meta.meta_id corresponds to childs.to_meta_id
						and	m.activate != 0						-- Only include the documents that are active in the meta table.
						and	c.meta_id = @meta_id					-- Only include documents that are children to this particular meta_id
left join		roles_rights rr									-- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
					on		c.to_meta_id = rr.meta_id					-- Only include rows with the documents we are interested in
							--and (rr.permission_id & 1) != 0				-- Only include permissions where the first bit is set. "Read rights" (May skip this now, since right now, if we have anything, this matches.)
join		user_roles_crossref urc								-- This table tells us which users have which roles
					on		urc.user_id = @user_id					-- Only include the rows with the user we are interested in...
						and	(	rr.role_id = urc.role_id				-- Include rows where the users roles match the roles that have permissions on the documents
							or	urc.role_id = 0					-- and also include the rows that tells us this user is a superadmin
							or	(
									m.show_meta != 0			-- and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
								and	rr.permission_id is null			-- if the permission_id for this row is 3, it has already been included.
								)	
							)
left join		user_rights ur									-- We may have permission, even though we don't own any documents... That is, if we're roleadmins or superadmin. When we lose ownership-based rights, this may be skipped.
					on		ur.meta_id = c.to_meta_id				-- Only include rows with the documents we are interested in
						and	ur.user_id =  urc.user_id					-- Only include rows with the user  we are interested in
						and	ur.permission_id = 99					-- Only include rows that mean "Ownership"
						and	m.show_meta = 0

group by	to_meta_id, c.menu_sort,manual_sort_order, doc_type,
		archive,target, left(convert (varchar,date_created,120),10),
		meta_headline,meta_text,meta_image,frame_name,
		activated_date+activated_time,archived_date+archived_time,
		urc.role_id,rr.permission_id,ur.permission_id
order by		menu_sort,c.manual_sort_order desc

