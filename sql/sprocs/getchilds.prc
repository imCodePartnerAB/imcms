SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetChilds]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetChilds]
GO


CREATE PROCEDURE GetChilds
 @meta_id int,
 @user_id int
AS
/*
Nice little query that lists the children of a document that a particular user may see, and includes a field that tells you wether he may do something to it or not.
*/
declare @sort_by int
select @sort_by = sort_order from text_docs where meta_id = @meta_id
-- Manual sort order
if @sort_by = 2
begin
select  to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, convert (varchar,date_created,120), convert (varchar,date_modified,120),
  meta_headline,meta_text,meta_image,frame_name,
  activated_datetime,archived_datetime,
  min(urc.role_id * ISNULL(~CAST(dps.permission_id AS BIT),1) * ISNULL(rr.set_id,1)),
  fd.filename
from   childs c
join   meta m    
     on    m.meta_id = c.to_meta_id     -- meta.meta_id corresponds to childs.to_meta_id
     and  m.activate > 0       -- Only include the documents that are active in the meta table.
     and  c.meta_id = @meta_id      -- Only include documents that are children to this particular meta_id
left join roles_rights rr            -- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
     on  c.to_meta_id = rr.meta_id      -- Only include rows with the documents we are interested in
left join doc_permission_sets dps           -- Include the permission_sets
     on  c.to_meta_id = dps.meta_id     -- for each document
     and dps.set_id = rr.set_id      -- and only the sets for the roles we are interested in
     and dps.permission_id > 0      -- and only the sets that have any permission
join user_roles_crossref urc           -- This table tells us which users have which roles
     on urc.user_id = @user_id      -- Only include the rows with the user we are interested in...
     and ( 
       rr.role_id = urc.role_id     -- Include rows where the users roles match the roles that have permissions on the documents
      or  urc.role_id = 0      -- and also include the rows that tells us this user is a superadmin
      or  (
        m.show_meta != 0    -- and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
       and ISNULL(~CAST(dps.permission_id AS BIT),1) != 1
      )
     )
left join fileupload_docs fd
     on  fd.meta_id = c.to_meta_id
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, convert (varchar,date_created,120), convert (varchar,date_modified,120),
  meta_headline,meta_text,meta_image,frame_name,
  activated_datetime,archived_datetime,
  fd.filename
order by  menu_sort,c.manual_sort_order desc
end
else if @sort_by = 3
begin
select  to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, convert (varchar,date_created,120), convert (varchar,date_modified,120),
  meta_headline,meta_text,meta_image,frame_name,
  activated_datetime,archived_datetime,
  min(urc.role_id * ISNULL(~CAST(dps.permission_id AS BIT),1) * ISNULL(rr.set_id,1)),
  fd.filename
from   childs c
join   meta m    
     on    m.meta_id = c.to_meta_id     -- meta.meta_id corresponds to childs.to_meta_id
     and  m.activate > 0       -- Only include the documents that are active in the meta table.
     and  c.meta_id = @meta_id      -- Only include documents that are children to this particular meta_id
left join roles_rights rr            -- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
     on  c.to_meta_id = rr.meta_id      -- Only include rows with the documents we are interested in
left join doc_permission_sets dps           -- Include the permission_sets
     on  c.to_meta_id = dps.meta_id     -- for each document
     and dps.set_id = rr.set_id      -- and only the sets for the roles we are interested in
     and dps.permission_id > 0      -- and only the sets that have any permission
join user_roles_crossref urc           -- This table tells us which users have which roles
     on urc.user_id = @user_id      -- Only include the rows with the user we are interested in...
     and ( 
       rr.role_id = urc.role_id     -- Include rows where the users roles match the roles that have permissions on the documents
      or  urc.role_id = 0      -- and also include the rows that tells us this user is a superadmin
      or  (
        m.show_meta != 0    -- and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
       and ISNULL(~CAST(dps.permission_id AS BIT),1) != 1
      )
     )
left join fileupload_docs fd
     on  fd.meta_id = c.to_meta_id
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, convert (varchar,date_created,120), convert (varchar,date_modified,120),
  meta_headline,meta_text,meta_image,frame_name,
  activated_datetime,archived_datetime,
  fd.filename
order by  menu_sort,convert (varchar,date_created,120) desc
end
else if @sort_by = 1
begin
select  to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, convert (varchar,date_created,120), convert (varchar,date_modified,120),
  meta_headline,meta_text,meta_image,frame_name,
  activated_datetime,archived_datetime,
  min(urc.role_id * ISNULL(~CAST(dps.permission_id AS BIT),1) * ISNULL(rr.set_id,1)),
  fd.filename
from   childs c
join   meta m    
     on    m.meta_id = c.to_meta_id     -- meta.meta_id corresponds to childs.to_meta_id
     and  m.activate > 0       -- Only include the documents that are active in the meta table.
     and  c.meta_id = @meta_id      -- Only include documents that are children to this particular meta_id
left join roles_rights rr            -- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
     on  c.to_meta_id = rr.meta_id      -- Only include rows with the documents we are interested in
left join doc_permission_sets dps           -- Include the permission_sets
     on  c.to_meta_id = dps.meta_id     -- for each document
     and dps.set_id = rr.set_id      -- and only the sets for the roles we are interested in
     and dps.permission_id > 0      -- and only the sets that have any permission
join user_roles_crossref urc           -- This table tells us which users have which roles
     on urc.user_id = @user_id      -- Only include the rows with the user we are interested in...
     and ( 
       rr.role_id = urc.role_id     -- Include rows where the users roles match the roles that have permissions on the documents
      or  urc.role_id = 0      -- and also include the rows that tells us this user is a superadmin
      or  (
        m.show_meta != 0    -- and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
       and ISNULL(~CAST(dps.permission_id AS BIT),1) != 1
      )
     )
left join fileupload_docs fd
     on  fd.meta_id = c.to_meta_id
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, convert (varchar,date_created,120), convert (varchar,date_modified,120),
  meta_headline,meta_text,meta_image,frame_name,
  activated_datetime,archived_datetime,
  fd.filename
order by  menu_sort,meta_headline
end


GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

