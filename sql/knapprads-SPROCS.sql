SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure imse.GetTexts    Script Date: 2000-10-26 11:27:36 ******/
setuser N'imse'
GO

CREATE PROCEDURE GetTexts
@meta_id int AS
select '#txt'+convert(varchar(5), name)+'#',name,type,text from texts where meta_id = @meta_id

GO

setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure imse.GetImgs    Script Date: 2000-10-26 11:27:36 ******/
setuser N'imse'
GO

CREATE PROCEDURE GetImgs
@meta_id int AS
select '#img'+convert(varchar(5), name)+'#',name,imgurl,linkurl,width,height,border,v_space,h_space,image_name,align,alt_text,low_scr,target,target_name from images where meta_id = @meta_id

GO

setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  ON    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure imse.GetTextDocData    Script Date: 2000-10-26 11:27:36 ******/
setuser N'imse'
GO

CREATE PROCEDURE GetTextDocData @meta_id INT AS

SELECT	t.template_id, simple_name, sort_order, t.group_id
FROM 		text_docs t 	
JOIN 		templates c 
					ON t.template_id = c.template_id
WHERE meta_id = @meta_id

GO

setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure imse.GetChilds    Script Date: 2000-10-26 11:27:38 ******/
setuser N'imse'
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
  archive,target, left(convert (varchar,date_created,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time,
  min(            -- This field will have 0 in it, if the user may change the document
   coalesce(urc.role_id,rr.permission_id-3,ur.permission_id-99)*
   coalesce(ur.permission_id-99,rr.permission_id-3,urc.role_id)*
   coalesce(rr.permission_id-3,urc.role_id,ur.permission_id-99)
  )
from   childs c
join   meta m    
     on   m.meta_id = c.to_meta_id    -- meta.meta_id corresponds to childs.to_meta_id
      and m.activate != 0      -- Only include the documents that are active in the meta table.
      and c.meta_id = @meta_id     -- Only include documents that are children to this particular meta_id
left join  roles_rights rr         -- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
     on  c.to_meta_id = rr.meta_id     -- Only include rows with the documents we are interested in
       --and (rr.permission_id & 1) != 0    -- Only include permissions where the first bit is set. "Read rights" (May skip this now, since right now, if we have anything, this matches.)
join  user_roles_crossref urc        -- This table tells us which users have which roles
     on  urc.user_id = @user_id     -- Only include the rows with the user we are interested in...
      and ( 
        rr.role_id = urc.role_id    -- Include rows where the users roles match the roles that have permissions on the documents
       or urc.role_id = 0     -- and also include the rows that tells us this user is a superadmin
       or ( 
         m.show_meta != 0   -- and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
        and rr.permission_id != 3   -- Don't include show_meta if it is not needed.
        ) 
       )
left join  user_rights ur         -- We may have permission, even though we don't own any documents... That is, if we're roleadmins or superadmin. When we lose ownership-based rights, this may be skipped.
     on  ur.meta_id = c.to_meta_id    -- Only include rows with the documents we are interested in
      and ur.user_id =  urc.user_id     -- Only include rows with the user  we are interested in
      and ur.permission_id = 99     -- Only include rows that mean "Ownership"
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time
order by  menu_sort,c.manual_sort_order desc
end
else if @sort_by = 3
begin
select  to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time,
  min(
   coalesce(urc.role_id,rr.permission_id-3,ur.permission_id-99)*
   coalesce(ur.permission_id-99,rr.permission_id-3,urc.role_id)*
   coalesce(rr.permission_id-3,urc.role_id,ur.permission_id-99)
  )
from   childs c
join   meta m    
     on   m.meta_id = c.to_meta_id    -- meta.meta_id corresponds to childs.to_meta_id
      and m.activate != 0      -- Only include the documents that are active in the meta table.
      and c.meta_id = @meta_id     -- Only include documents that are children to this particular meta_id
left join  roles_rights rr         -- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
     on  c.to_meta_id = rr.meta_id     -- Only include rows with the documents we are interested in
       --and (rr.permission_id & 1) != 0    -- Only include permissions where the first bit is set. "Read rights" (May skip this now, since right now, if we have anything, this matches.)
join  user_roles_crossref urc        -- This table tells us which users have which roles
     on  urc.user_id = @user_id     -- Only include the rows with the user we are interested in...
      and ( 
        rr.role_id = urc.role_id    -- Include rows where the users roles match the roles that have permissions on the documents
       or urc.role_id = 0     -- and also include the rows that tells us this user is a superadmin
       or ( 
         m.show_meta != 0   -- and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
        and rr.permission_id != 3   -- Don't include show_meta if it is not needed.
        ) 
       )
left join  user_rights ur         -- We may have permission, even though we don't own any documents... That is, if we're roleadmins or superadmin. When we lose ownership-based rights, this may be skipped.
     on  ur.meta_id = c.to_meta_id    -- Only include rows with the documents we are interested in
      and ur.user_id =  urc.user_id     -- Only include rows with the user  we are interested in
      and ur.permission_id = 99     -- Only include rows that mean "Ownership"
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time
order by  menu_sort,left(convert (varchar,date_created,120),10) desc
end
else if @sort_by = 1
begin
select  to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time,
  min(
   coalesce(urc.role_id,rr.permission_id-3,ur.permission_id-99)*
   coalesce(ur.permission_id-99,rr.permission_id-3,urc.role_id)*
   coalesce(rr.permission_id-3,urc.role_id,ur.permission_id-99)
  )
from   childs c
join   meta m    
     on   m.meta_id = c.to_meta_id    -- meta.meta_id corresponds to childs.to_meta_id
      and m.activate != 0      -- Only include the documents that are active in the meta table.
      and c.meta_id = @meta_id     -- Only include documents that are children to this particular meta_id
left join  roles_rights rr         -- We may have permission, even though we don't have anything in role-permissions... That is, if we're docowner or superadmin.
     on  c.to_meta_id = rr.meta_id     -- Only include rows with the documents we are interested in
       --and (rr.permission_id & 1) != 0    -- Only include permissions where the first bit is set. "Read rights" (May skip this now, since right now, if we have anything, this matches.)
join  user_roles_crossref urc        -- This table tells us which users have which roles
     on  urc.user_id = @user_id     -- Only include the rows with the user we are interested in...
      and ( 
        rr.role_id = urc.role_id    -- Include rows where the users roles match the roles that have permissions on the documents
       or urc.role_id = 0     -- and also include the rows that tells us this user is a superadmin
       or ( 
         m.show_meta != 0   -- and also include documents that are to be shown regardless of rights. (Visa även för obehöriga)
        and rr.permission_id != 3   -- Don't include show_meta if it is not needed.
        ) 
       )
left join  user_rights ur         -- We may have permission, even though we don't own any documents... That is, if we're roleadmins or superadmin. When we lose ownership-based rights, this may be skipped.
     on  ur.meta_id = c.to_meta_id    -- Only include rows with the documents we are interested in
      and ur.user_id =  urc.user_id     -- Only include rows with the user  we are interested in
      and ur.permission_id = 99     -- Only include rows that mean "Ownership"
group by to_meta_id, c.menu_sort,manual_sort_order, doc_type,
  archive,target, left(convert (varchar,date_created,120),10),
  meta_headline,meta_text,meta_image,frame_name,
  activated_date+activated_time,archived_date+archived_time
order by  menu_sort,meta_headline
end

GO

setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure imse.getTemplatesInGroup    Script Date: 2000-10-26 11:27:38 ******/
setuser N'imse'
GO

CREATE PROCEDURE getTemplatesInGroup @grp_id INT AS
SELECT t.template_id,simple_name
FROM  templates t JOIN
  templates_cref c
ON  t.template_id = c.template_id
WHERE c.group_id = @grp_id
ORDER BY simple_name

GO

setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

/****** Object:  Stored Procedure imse.getTemplategroups    Script Date: 2000-10-26 11:27:38 ******/
setuser N'imse'
GO

CREATE PROCEDURE getTemplategroups AS
select group_id,group_name from templategroups order by group_name

GO

setuser
GO

SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO

