declare @meta_id int
declare @user_id int
declare @sort_by int
set @meta_id = 4456
set @user_id = 99

select @sort_by = sort_order from text_docs where meta_id = @meta_id

if @sort_by = 2
begin
select distinct	to_meta_id, c.menu_sort,manual_sort_order,doc_type,archive,target,status_id,meta_headline,meta_text,date_created
from 		meta m 
join 		childs c 			on 	m.meta_id = c.to_meta_id 
join		roles_rights rr		on	m.meta_id = rr.meta_id
join		user_roles_crossref urc	on	rr.role_id = urc.role_id
where 		c.meta_id = @meta_id					-- Insert meta_id here
and		activate = 1
and		(show_meta = 1
or		(rr.permission_id & 1 = 1 or urc.role_id = 0) and urc.user_id = @user_id)	-- Insert user_id here
order by		menu_sort,c.manual_sort_order desc
end
else if @sort_by = 3
begin
select distinct	to_meta_id, c.menu_sort,manual_sort_order,doc_type,archive,target,meta_headline,meta_text,date_created
from 		meta m 
join 		childs c 			on 	m.meta_id = c.to_meta_id 
join		roles_rights rr		on	m.meta_id = rr.meta_id
join		user_roles_crossref urc	on	rr.role_id = urc.role_id
where 		c.meta_id = @meta_id					-- Insert meta_id here
and		activate = 1
and		(show_meta = 1
or		(rr.permission_id & 1 = 1 or urc.role_id = 0) and urc.user_id = @user_id)	-- Insert user_id here
order by		menu_sort,date_created desc
end
else if @sort_by = 1
begin
select distinct	to_meta_id, c.menu_sort,manual_sort_order,doc_type,archive,target,meta_headline,meta_text,date_created
from 		meta m 
join 		childs c 			on 	m.meta_id = c.to_meta_id 
join		roles_rights rr		on	m.meta_id = rr.meta_id
join		user_roles_crossref urc	on	rr.role_id = urc.role_id
where 		c.meta_id = @meta_id					-- Insert meta_id here
and		activate = 1
and		(show_meta = 1
or		(rr.permission_id & 1 = 1 or urc.role_id = 0) and urc.user_id = @user_id)	-- Insert user_id here
order by		menu_sort,meta_headline
end
