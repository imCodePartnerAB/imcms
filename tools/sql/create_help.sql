-- create_help.sql
-- get all help data for meta_id < @metaMax and build the new script "help.sql" 
--
-- !!!! SET @metaMax before you run the script !!!! 

declare @metaMax varchar(4), @serverName varchar(35)
set @metaMax = 401  --201 

select @serverName = srvname
from master.dbo.sysservers

print '
-- Script name = "help.sql" 

-- Run this script to set upp all help-page on a new database 
  
-- This script is autocreated by script "create_help.sql"'

print'-- Soures database = ' + db_name() + ''
print'-- Server = ' + @serverName + ''
print'-- Create date = ' + convert(char(10),getDate(),120)+ ''
print'-- Included help-page =  meta_id < ' + @metaMax + '' 
print''
-- Här börjar vi att lägga in hjälp data
-- Först kollar vi att det inte redan finns mallar med id 2 tom 5 för dessa skall vi använda till hjälpmallar'
print ''
print 'GO
DECLARE @temp int 
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

-- ok vi kan börja ösa in i databasen

SET IDENTITY_INSERT meta ON'

-- get all item in table meta where meta_id < metaMax
print'-- lets insert new meta_id '
declare @newstring varchar(8000), @meta_id varchar(3), @meta_headline varchar(255), @meta_text varchar(1000), @lang_prefix varchar(3), @date_created varchar(10)

set @date_created = convert(char(10),getDate(),120)

declare posCursor  Cursor scroll 
for select meta_id, meta_headline, meta_text, lang_prefix
    from meta 
    where meta_id < @metaMax

open posCursor

fetch next from posCursor 
into @meta_id, @meta_headline, @meta_text, @lang_prefix
while @@fetch_status = 0
begin
  set @newstring =
 
  'INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (' + @meta_id + ','''',2,'''+ @meta_headline + ''',''' + @meta_text + ''','''',1,0,0,1,0,1,0,1,''' + @lang_prefix + ''','''',''' + @date_created + ''',''' + @date_created + ''',1,1,1,''_self'','''',1,''' + @date_created + ''',NULL)'

  print  @newstring

  fetch next from posCursor 
  into @meta_id, @meta_headline, @meta_text, @lang_prefix
end 
close posCursor
deallocate posCursor
print 'SET IDENTITY_INSERT meta OFF'
print ''

-- now get all images where meta_id < metaMax
print'-- now insert all images '
print''
declare @width varchar(4), @height varchar(4), @border char(1), @name varchar(3), @align varchar(15),  @imgurl varchar(255), @linkurl varchar(255) 

declare posCursor  Cursor scroll 
for select meta_id, width, height, border, name, align, imgurl, linkurl
    from images
    where meta_id < @metaMax
    order by meta_id

open posCursor
fetch next from posCursor 
into @meta_id, @width, @height, @border, @name, @align, @imgurl, @linkurl
while @@fetch_status = 0
begin
  
  set @newstring =
'INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values ('+ @meta_id + ',' + @width + ',' + @height + ',' + @border+ ',0,0,''' + @name + ''','''',''_self'','''',''' + @align + ''','''','''',''' + @imgurl+ ''','''+@linkurl+''')'

  print  @newstring
  fetch next from posCursor 
  into @meta_id, @width, @height, @border, @name, @align, @imgurl, @linkurl
end
print''
close posCursor
deallocate posCursor

-- now we get all data in texts where meta_id < metaMax
print'-- now insert all data in texts '

declare @type varchar(3)
declare @start int, @ink int, @text varchar(1000), @length int
set @start = 1

declare posCursor  Cursor scroll 
for select meta_id, name, type
    from texts
    where meta_id < @metaMax
    order by meta_id

open posCursor
fetch next from posCursor 
into @meta_id, @name, @type
while @@fetch_status = 0
   begin
       set @ink =1001
       select @text = replace( substring(text,@start,1000), '''', '"' ), @length = len(substring(text,@start,2000)) 
       from texts 
       where meta_id = @meta_id and name = @name

       print 'INSERT INTO texts( meta_id, name, text, type )
       values('+ @meta_id + ',' + @name + ',''' + @text  
 
       if @length > 1000
       begin		
          while (@length > 0 )
          begin
            	select @text = replace( substring(text,@ink,1000), '''', '"' ), @length = len(substring(text,@ink,1000))
            	from texts 
            	where meta_id = @meta_id and name = @name
            	print @text
            	set @ink = @ink + 1000
          end 
        end
        print ''',' + @type + ')'	
        print ''

       fetch next from posCursor 
       into @meta_id, @name, @type
  end
close posCursor
deallocate posCursor
print''

-- add templategroup and templates

print'

declare @groupId int
select @groupId = max(group_id)+1 from templategroups

--ok lets create the templategroup
insert into templategroups(group_id, group_name)
values(@groupId,''x_imCMShelp'')

--ok lets add the templates and relate templates and templategroups'
declare @template_id varchar(4), @template_name varchar(80), @simple_name varchar(80), @no_of_txt varchar(3), @no_of_img varchar(3), @no_of_url varchar(3)

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



-- set templates for all help meta_ids
print '
--ok lets set templates for all help meta_ids'

declare posCursor  Cursor scroll  
for select meta_id, template_id
    from text_docs
    where meta_id < @metaMax
    order by meta_id

open posCursor
fetch next from posCursor 
into @meta_id, @template_id
while @@fetch_status = 0
begin
    print 
    'INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values ('+ @meta_id + ',' + @template_id + ',@groupId,1,-1,-1)'
    fetch next from posCursor 
    into @meta_id, @template_id
end
print''
close posCursor
deallocate posCursor

-- get all childs to meta_id < @metaMax
print'-- get all childs to meta_id < @metaMax'
declare @to_meta_id varchar(4), @menu_sort varchar(2), @manual_sort_order varchar(4)

declare posCursor  Cursor scroll  
for select meta_id, to_meta_id , menu_sort , manual_sort_order
    from childs
    where meta_id < @metaMax
    order by meta_id, to_meta_id

open posCursor
fetch next from posCursor 
into @meta_id, @to_meta_id, @menu_sort, @manual_sort_order
while @@fetch_status = 0
begin

    print
    'INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (' + @meta_id+ ',' + @to_meta_id + ',' + @menu_sort + ',' + @manual_sort_order + ')'
    fetch next from posCursor 
    into @meta_id, @to_meta_id, @menu_sort, @manual_sort_order
end
close posCursor
deallocate posCursor
print''

--lets set all the role_rights
print'--lets set all the role_rights'
declare @user_role_id varchar(2), @role_id varchar(2), @set_id varchar(2)

select @user_role_id = role_id 
from roles 
where role_name = 'Users'

declare posCursor  Cursor scroll  
for select role_id, meta_id , set_id
    from roles_rights
    where meta_id < @metaMax and ( role_id = 0 or role_id = @user_role_id )

open posCursor
fetch next from posCursor 
into @role_id, @meta_id, @set_id
while @@fetch_status = 0
begin
    print
    'INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (' + @role_id + ',' + @meta_id + ',' + @set_id + ')'
    fetch next from posCursor 
    into @role_id, @meta_id, @set_id
end
print''
print'end'

close posCursor
deallocate posCursor





