declare @user_id int
declare @meta_id int

set @user_id = 105
set @meta_id = 2690

select distinct	to_meta_id, c.menu_sort,manual_sort_order,doc_type,archive,target,left(convert (varchar,date_created,120),10),meta_headline,meta_text,meta_image,frame_name
from 		childs c
join 		meta m 			
					on 		m.meta_id = c.to_meta_id				-- meta.meta_id corresponds to childs.to_meta_id
						and	m.activate != 0						-- Only include the documents that are active in the meta table.
						and	c.meta_id = @meta_id					-- Only include documents that are children to this particular meta_id
left join		roles_rights rr									-- We may have permission, even though we don't have anything in roles_rights... That is, if we're docowner or superadmin.
					on		c.to_meta_id = rr.meta_id					-- Only include rows in roles_rights where rr.meta_id is among the children to this meta_id
							and (rr.permission_id & 1) != 0				-- Only include permissions where the first bit is set. "Read rights"
join		user_roles_crossref urc
					on		rr.role_id = urc.role_id					-- Only include the user-roles crossref where the role_id is one of the roles selected from roles_rights
						and	urc.user_id = @user_id					-- Only include the user-roles crossref where the user_id is the one we are interested in...

left join		user_rights ur		on		ur.meta_id = c.to_meta_id
						and	ur.user_id = @user_id					-- Only include lines where the user_id is the one we are interested in
						and	ur.permission_id = 99					-- Only include lines that means "Ownership"

order by		menu_sort,c.manual_sort_order desc
