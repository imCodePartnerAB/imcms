-- create_help_update.sql
-- get all help-page data for meta_id >= @metaStart and  meta_id <= @metaMax and build the new script "help_update.sql" 
--
-- !!!! SET @metaStart and @metaMax before you run the script !!!! 

declare @metaStart varchar(4), @metaMax varchar(4), @serverName varchar(35)
set @metaStart = 1 --1
set @metaMax = 200 --200

select @serverName = srvname
from master.dbo.sysservers


print'-- Script name = help_update.sql'
print''
print'-- Run this script to update a database with the latest help-page '
print'' 
print'-- This script is autocreated by script "create_update_help.sql"'
print''
print'-- Soures database = ' + db_name() + ''
print'-- Server = ' + @serverName + ''
print'-- Create date = ' + convert(char(10),getDate(),120)+ ''
print'-- Update intervall = meta_id from ' + @metaStart + ' to ' + @metaMax + '' 
print''


-- Först måste vi kolla att det inte redan finns mallar med id 2 tom 5 för dessa skall vi använda till hjälpmallar'
print ''
print '--Först kollar vi att det inte redan finns mallar med id 2 tom 5 för dessa skall vi använda till hjälpmallar'
print ''
print 'DECLARE @temp int 
declare @message varchar(100)
SET @temp = 0  
SELECT @temp = template_id
FROM templates
WHERE ( template_id > 1 and template_id < 6 ) and template_name not like ''Help%''
IF @temp > 0 
	select ''Det finns befintliga mallar som måste bytas namn på. Detta görs genom att köra script remove_templates.sql. Läs manualen för att se hur man ska göra!'' as message'
print''
print'else
begin

-- ok vi kan börja ösa in i databasen'

declare @newstring varchar(8000), @meta_id varchar(4), @meta_headline varchar(255), @meta_text varchar(1000), @lang_prefix varchar(3), @date_created varchar(10), @date_modified varchar(10), @activated_datetime varchar(10)
set @date_modified = convert(char(10),getDate(),120)

declare posCursor Cursor scroll 
for select meta_id, meta_headline, meta_text, lang_prefix
    from meta 
    where meta_id >= @metaStart and meta_id <= @metaMax

print'
Begin Tran'
   

print'-- drop constraints '
print'
if exists (select * from dbo.sysobjects where id = object_id(N''[dbo].[FK_childs_meta1]'') and OBJECTPROPERTY(id, N''IsForeignKey'') = 1)
ALTER TABLE [dbo].[childs] DROP CONSTRAINT FK_childs_meta1'
print'
if exists (select * from dbo.sysobjects where id = object_id(N''[dbo].[FK_roles_rights_meta]'') and OBJECTPROPERTY(id, N''IsForeignKey'') = 1)
ALTER TABLE [dbo].[roles_rights] DROP CONSTRAINT FK_roles_rights_meta'
print'
if exists (select * from dbo.sysobjects where id = object_id(N''[dbo].[FK_text_docs_meta]'') and OBJECTPROPERTY(id, N''IsForeignKey'') = 1)
ALTER TABLE [dbo].[text_docs] DROP CONSTRAINT FK_text_docs_meta'
print'
if exists (select * from dbo.sysobjects where id = object_id(N''[dbo].[FK_browser_docs_meta]'') and OBJECTPROPERTY(id, N''IsForeignKey'') = 1)
ALTER TABLE [dbo].[browser_docs] DROP CONSTRAINT FK_browser_docs_meta'
print'
if exists (select * from dbo.sysobjects where id = object_id(N''[dbo].[FK_permission_sets_meta]'') and OBJECTPROPERTY(id, N''IsForeignKey'') = 1)
ALTER TABLE [dbo].[doc_permission_sets] DROP CONSTRAINT FK_permission_sets_meta'
print'
if exists (select * from dbo.sysobjects where id = object_id(N''[dbo].[FK_frameset_docs_meta]'') and OBJECTPROPERTY(id, N''IsForeignKey'') = 1)
ALTER TABLE [dbo].[frameset_docs] DROP CONSTRAINT FK_frameset_docs_meta'
print'
if exists (select * from dbo.sysobjects where id = object_id(N''[dbo].[FK_images_meta]'') and OBJECTPROPERTY(id, N''IsForeignKey'') = 1)
ALTER TABLE [dbo].[images] DROP CONSTRAINT FK_images_meta'
print'
if exists (select * from dbo.sysobjects where id = object_id(N''[dbo].[FK_includes_meta]'') and OBJECTPROPERTY(id, N''IsForeignKey'') = 1)
ALTER TABLE [dbo].[includes] DROP CONSTRAINT FK_includes_meta'
print'
if exists (select * from dbo.sysobjects where id = object_id(N''[dbo].[FK_includes_meta1]'') and OBJECTPROPERTY(id, N''IsForeignKey'') = 1)
ALTER TABLE [dbo].[includes] DROP CONSTRAINT FK_includes_meta1'
print'
if exists (select * from dbo.sysobjects where id = object_id(N''[dbo].[FK_meta_classification_meta]'') and OBJECTPROPERTY(id, N''IsForeignKey'') = 1)
ALTER TABLE [dbo].[meta_classification] DROP CONSTRAINT FK_meta_classification_meta'
print'
if exists (select * from dbo.sysobjects where id = object_id(N''[dbo].[FK_meta_section_meta]'') and OBJECTPROPERTY(id, N''IsForeignKey'') = 1)
ALTER TABLE [dbo].[meta_section] DROP CONSTRAINT FK_meta_section_meta'
print'
if exists (select * from dbo.sysobjects where id = object_id(N''[dbo].[FK_new_doc_permission_sets_meta]'') and OBJECTPROPERTY(id, N''IsForeignKey'') = 1)
ALTER TABLE [dbo].[new_doc_permission_sets] DROP CONSTRAINT FK_new_doc_permission_sets_meta'
print'
if exists (select * from dbo.sysobjects where id = object_id(N''[dbo].[FK_texts_meta]'') and OBJECTPROPERTY(id, N''IsForeignKey'') = 1)
ALTER TABLE [dbo].[texts] DROP CONSTRAINT FK_texts_meta'
print'
if exists (select * from dbo.sysobjects where id = object_id(N''[dbo].[FK_url_docs_meta]'') and OBJECTPROPERTY(id, N''IsForeignKey'') = 1)
ALTER TABLE [dbo].[url_docs] DROP CONSTRAINT FK_url_docs_meta'
print'
if exists (select * from dbo.sysobjects where id = object_id(N''[dbo].[FK_user_rights_meta]'') and OBJECTPROPERTY(id, N''IsForeignKey'') = 1)
ALTER TABLE [dbo].[user_rights] DROP CONSTRAINT FK_user_rights_meta'


-- get all meta_id 
print'--First delete all meta_id '
print'DELETE FROM meta WHERE meta_id >= ' + @metaStart + ' and meta_id <= ' + @metaMax + ''
print''
print'--Lets insert all meta_id'
print''
print'SET IDENTITY_INSERT meta ON'
print''

open posCursor

fetch next from posCursor 
into @meta_id, @meta_headline, @meta_text, @lang_prefix

while @@fetch_status = 0
begin
     print'
     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (' + @meta_id + ','''',2,'''+ @meta_headline + ''',''' + @meta_text + ''','''',1,0,0,1,0,1,0,1,''' + @lang_prefix + ''','''',''' + @date_modified + ''',''' + @date_modified + ''',1,1,0,''_self'','''',1,''' + @date_modified + ''',NULL)'
     
     fetch next from posCursor 
     into @meta_id, @meta_headline, @meta_text, @lang_prefix
    
end
close posCursor
deallocate posCursor
print''
print 'SET IDENTITY_INSERT meta OFF'
print ''

print'-- Lets create constraints'
print'
ALTER TABLE [dbo].[childs] ADD 
	CONSTRAINT [FK_childs_meta1] FOREIGN KEY 
	(
		[meta_id]
	) REFERENCES [dbo].[meta] (
		[meta_id]
	)
'
print'
ALTER TABLE [dbo].[roles_rights] ADD 
	CONSTRAINT [FK_roles_rights_meta] FOREIGN KEY 
	(
		[meta_id]
	) REFERENCES [dbo].[meta] (
		[meta_id]
	)
'
print'
ALTER TABLE [dbo].[browser_docs] ADD 
	CONSTRAINT [FK_browser_docs_meta] FOREIGN KEY 

	(

		[meta_id]

	) REFERENCES [dbo].[meta] (

		[meta_id]

	)
'

print'
ALTER TABLE [dbo].[doc_permission_sets] ADD 

	CONSTRAINT [FK_permission_sets_meta] FOREIGN KEY 

	(

		[meta_id]

	) REFERENCES [dbo].[meta] (

		[meta_id]

	)

'
print'
ALTER TABLE [dbo].[frameset_docs] ADD 

	CONSTRAINT [FK_frameset_docs_meta] FOREIGN KEY 

	(

		[meta_id]

	) REFERENCES [dbo].[meta] (

		[meta_id]

	)

'
print'
ALTER TABLE [dbo].[images] ADD 

	CONSTRAINT [FK_images_meta] FOREIGN KEY 

	(

		[meta_id]

	) REFERENCES [dbo].[meta] (

		[meta_id]

	)

'
print'
ALTER TABLE [dbo].[includes] ADD 

	CONSTRAINT [FK_includes_meta] FOREIGN KEY 

	(

		[meta_id]

	) REFERENCES [dbo].[meta] (

		[meta_id]

	),

	CONSTRAINT [FK_includes_meta1] FOREIGN KEY 

	(

		[included_meta_id]

	) REFERENCES [dbo].[meta] (

		[meta_id]

	)

'
print'
ALTER TABLE [dbo].[meta_classification] ADD 


	CONSTRAINT [FK_meta_classification_meta] FOREIGN KEY 

	(

		[meta_id]

	) REFERENCES [dbo].[meta] (

		[meta_id]

	)

'
print'
ALTER TABLE [dbo].[meta_section] ADD 

	CONSTRAINT [FK_meta_section_meta] FOREIGN KEY 

	(

		[meta_id]

	) REFERENCES [dbo].[meta] (

		[meta_id]

	)

'
print'
ALTER TABLE [dbo].[new_doc_permission_sets] ADD 

	CONSTRAINT [FK_new_doc_permission_sets_meta] FOREIGN KEY 

	(

		[meta_id]

	) REFERENCES [dbo].[meta] (

		[meta_id]

	)
'

print'
ALTER TABLE [dbo].[text_docs] ADD 

	CONSTRAINT [FK_text_docs_meta] FOREIGN KEY 

	(

		[meta_id]

	) REFERENCES [dbo].[meta] (

		[meta_id]

	)

'
print'
ALTER TABLE [dbo].[texts] ADD 

	CONSTRAINT [FK_texts_meta] FOREIGN KEY 

	(

		[meta_id]

	) REFERENCES [dbo].[meta] (

		[meta_id]

	)

'
print'
ALTER TABLE [dbo].[url_docs] ADD 

	CONSTRAINT [FK_url_docs_meta] FOREIGN KEY 

	(

		[meta_id]

	) REFERENCES [dbo].[meta] (

		[meta_id]

	)

'
print'
ALTER TABLE [dbo].[user_rights] ADD 

	CONSTRAINT [FK_user_rights_meta] FOREIGN KEY 

	(

		[meta_id]

	) REFERENCES [dbo].[meta] (

		[meta_id]

	)

'


-- get all images
print'-- get all images'
print''
declare @width varchar(4), @height varchar(4), @border char(1),  @v_space varchar(3), @h_space varchar(3), @name varchar(3), @align varchar(15),  @imgurl varchar(255), @linkurl varchar(255) 

declare posCursor  Cursor scroll 
for select meta_id, width, height, border, v_space, h_space, name, align, imgurl, linkurl
from images
where meta_id >= @metaStart and meta_id <= @metaMax
order by meta_id

print'--delete old'
print'DELETE FROM images WHERE meta_id >= ' + @metaStart + ' and meta_id <= ' + @metaMax + ''
print''
print'--lets insert all new in images'
print''		
open posCursor
fetch next from posCursor 
into @meta_id, @width, @height, @border, @v_space, @h_space, @name, @align, @imgurl, @linkurl
while @@fetch_status = 0
begin
     print
     'INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values ('+ @meta_id + ',' + @width + ',' + @height + ',' + @border+ ',' + @v_space + ',' + @h_space + ',''' + @name + ''','''',''_self'','''',''' + @align + ''','''','''',''' + @imgurl+ ''','''+@linkurl+''')'

     fetch next from posCursor 
     into @meta_id, @width, @height, @border, @v_space, @h_space, @name, @align, @imgurl, @linkurl
end
close posCursor
deallocate posCursor
print ''


-- now we get all data in texts 
print'-- now we get all data in texts '
declare @type varchar(3)
declare @start int, @ink int, @text varchar(1000), @length int
set @start = 1


declare posCursor  Cursor scroll 
for select meta_id, name, type
from texts
where meta_id >= @metaStart and meta_id <= @metaMax
order by meta_id

print'--delete old '
print'DELETE FROM texts WHERE meta_id >= ' + @metaStart + ' and meta_id <= ' + @metaMax + ''
print''
print '-- insert new '

open posCursor
fetch next from posCursor 
into @meta_id, @name, @type
while @@fetch_status = 0
begin 
     set @ink =1001
     select @text = substring(text,@start,1000), @length = len(substring(text,@start,2000))
     from texts 
     where meta_id = @meta_id and name = @name

     print 'INSERT INTO texts( meta_id, name, text, type )
     values('+ @meta_id + ',' + @name + ',''' + @text  
 
     if @length > 1000
     begin		
           while (@length > 0 )
           begin
                select @text = substring(text,@ink,1000), @length = len(substring(text,@ink,1000))
                from texts 
                where meta_id = @meta_id and name = @name
                print @text
                set @ink = @ink + 1000
           end 
     end
     print ''',' + @type + ')'	
        
     fetch next from posCursor 
     into @meta_id, @name, @type
end
close posCursor
deallocate posCursor
print''

-- add the templates and relate templates and templategroups'
print'-- add the templates and relate templates and templategroups'
declare @template_id varchar(4), @template_name varchar(80), @simple_name varchar(80), @no_of_txt varchar(3), @no_of_img varchar(3), @no_of_url varchar(3)

print'-- drop constraints '
print'
if exists (select * from dbo.sysobjects where id = object_id(N''[dbo].[FK_templates_cref_templates]'') and OBJECTPROPERTY(id, N''IsForeignKey'') = 1)
ALTER TABLE [dbo].[templates_cref] DROP CONSTRAINT FK_templates_cref_templates

if exists (select * from dbo.sysobjects where id = object_id(N''[dbo].[FK_text_docs_templates]'') and OBJECTPROPERTY(id, N''IsForeignKey'') = 1)
ALTER TABLE [dbo].[text_docs] DROP CONSTRAINT FK_text_docs_templates'

print'-- insert new help_templates'

print'-- first get group_id for ''imCMShelp'' from templategroups'
print'declare @groupId int
select @groupId = group_id from templategroups
where group_name = ''imCMShelp'''
print''

print''
print'-- delete old help-template from templates and from templates_cref '
print'DELETE FROM templates 
WHERE template_id < 12 and simple_name like ''Help%''

DELETE FROM templates_cref 
WHERE template_id < 12 and group_id = @groupId '
print''

print'-- insert new help_templates to templates and templates_cref'
print''

declare posCursor  Cursor scroll 
for select template_id, template_name, simple_name, lang_prefix, no_of_txt, no_of_img, no_of_url
    from templates
    where template_id < 12 and simple_name like 'Help%'
    order by template_id

open posCursor
fetch next from posCursor 
into @template_id, @template_name, @simple_name, @lang_prefix, @no_of_txt, @no_of_img, @no_of_url
while @@fetch_status = 0
begin
    	print'INSERT INTO templates ( template_id , template_name , simple_name , lang_prefix , no_of_txt , no_of_img , no_of_url )
	values (' + @template_id+ ',''' + @template_name + ''',''' + @simple_name+ ''',''' + @lang_prefix + ''',' + @no_of_txt + ',' + @no_of_img + ',' + @no_of_url + ')'
	print'INSERT INTO templates_cref(group_id, template_id)
	values (@groupId,' +  @template_id + ')'
   fetch next from posCursor 
   into @template_id, @template_name, @simple_name, @lang_prefix, @no_of_txt, @no_of_img, @no_of_url
end
close posCursor
deallocate posCursor

print''
print'-- add constraints

ALTER TABLE [dbo].[text_docs] ADD 
	CONSTRAINT [FK_text_docs_templates] FOREIGN KEY 
	(
		[template_id]
	) REFERENCES [dbo].[templates] (
		[template_id]
	)


ALTER TABLE [dbo].[templates_cref] ADD 
	CONSTRAINT [FK_templates_cref_templates] FOREIGN KEY 
	(
		[template_id]
	) REFERENCES [dbo].[templates] (
		[template_id]
	)
'

-- lets set templates for all help meta_ids

print'-- lets set templates for all help meta_ids'
declare @group_id varchar(3)

declare posCursor  Cursor scroll  
for select meta_id, template_id
from text_docs
where meta_id >= @metaStart and meta_id <= @metaMax
order by meta_id

print'-- delete old text_docs'
print'DELETE FROM text_docs WHERE meta_id >= ' + @metaStart + ' and meta_id <= ' + @metaMax + ''
print''
print'-- insert new text_docs'

open posCursor
fetch next from posCursor 
into @meta_id, @template_id
while @@fetch_status = 0
begin
     print 
     'INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values ('+ @meta_id + ',' + @template_id + ', @groupId, 1,-1,-1)'
     fetch next from posCursor 
     into @meta_id, @template_id
end
close posCursor
deallocate posCursor
print''


-- insert all meta_id in childs
print'-- insert all meta_id in childs'
declare @to_meta_id varchar(4), @menu_sort varchar(2), @manual_sort_order varchar(4)

declare posCursor  Cursor scroll  
for select meta_id , to_meta_id, menu_sort , manual_sort_order
from childs
where to_meta_id >= @metaStart and to_meta_id <= @metaMax
order by meta_id, to_meta_id

print'--delete old'
print'DELETE FROM childs WHERE to_meta_id >= ' + @metaStart + ' and to_meta_id <= ' + @metaMax + ''
print''

print'--insert new'
open posCursor
fetch next from posCursor 
into @meta_id, @to_meta_id, @menu_sort, @manual_sort_order
while @@fetch_status = 0
begin
     print
     'INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (' + @meta_id + ',' + @to_meta_id + ',' + @menu_sort + ',' + @manual_sort_order + ')'
     fetch next from posCursor 
     into @meta_id, @to_meta_id, @menu_sort, @manual_sort_order
end
close posCursor
deallocate posCursor
print''


--lets get all role_rights
print'--lets set all role_rights'
print''
declare @role_id varchar(2), @set_id varchar(2)

print'--delete old'
print'DELETE FROM roles_rights WHERE meta_id >= ' + @metaStart + ' and meta_id <= ' + @metaMax + ''
print''


print'-- we have to get the role_id for users and superadmin from the current database'
print' DECLARE @user_roleId varchar(1), @sa_roleId varchar(1)
SELECT @user_roleId = role_id FROM roles
WHERE role_name = ''Users''
SELECT @sa_roleId = role_id FROM roles
WHERE role_name = ''Superadmin'''

print'--insert new roles_rights'

declare posCursor  Cursor scroll  
for select distinct meta_id, set_id
from roles_rights
where meta_id >= @metaStart and meta_id <= @metaMax

open posCursor
fetch next from posCursor 
into @meta_id, @set_id
while @@fetch_status = 0
begin
     print
     'INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, ' + @meta_id + ', ' + @set_id + ')'
     print
     'INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, ' + @meta_id + ',' + @set_id + ')'	

     fetch next from posCursor 
     into @meta_id, @set_id
end
close posCursor
deallocate posCursor
print''
print'
If @@error = 0
	BEGIN
           Commit Tran
           Print ''Commit Tran''
	END    
Else
	BEGIN
            Rollback Tran
	    Print ''Rollback Tran''
	END
'
print'END'

print'-- End off script'













