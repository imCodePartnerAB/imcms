
create table #tmptexts (

	meta_id int not null,
	name int not null,
        text ntext collate Finnish_Swedish_CS_AS, 
	newtext varchar(1000) collate Finnish_Swedish_CS_AS 
	

)


DECLARE @ptrval binary(16), @insert_offset int, @delete_length int 

declare @start int, @ink int, @oldtext varchar(1000), @newtext varchar(1000), @length int, @meta_id int, @name int
set @start = 1
set @ink =1000
--set @meta_id = 1036


declare posCursor  Cursor scroll 
for select meta_id, name
    from texts
    where meta_id > 1000  -- = @meta_id  

open posCursor
fetch next from posCursor 
into @meta_id, @name
while @@fetch_status = 0
   begin
        insert into #tmptexts (meta_id, name, text, newtext)
        select @meta_id, @name, text, '' from texts where meta_id =@meta_id and name = @name
               
	
        select @oldtext = substring(text,@start,1000), @length = len(substring(text,@start,1000))
       	from #tmptexts 
       	where meta_id = @meta_id and name = @name
       	
        update #tmptexts set newtext = @oldtext where meta_id =@meta_id and name = @name        
        
	update #tmptexts set newtext = replace((select newtext from #tmptexts where meta_id =@meta_id and name = @name),'&aring;', 'å')
        where meta_id = @meta_id and name = @name
	update #tmptexts set newtext = replace((select newtext from #tmptexts where meta_id =@meta_id and name = @name),'&Aring;', 'Å')
        where meta_id = @meta_id and name = @name
	update #tmptexts set newtext = replace((select newtext from #tmptexts where meta_id =@meta_id and name = @name),'&auml;', 'ä')
        where meta_id = @meta_id and name = @name
	update #tmptexts set newtext = replace((select newtext from #tmptexts where meta_id =@meta_id and name = @name),'&Auml;', 'Ä')
        where meta_id = @meta_id and name = @name
	update #tmptexts set newtext = replace((select newtext from #tmptexts where meta_id =@meta_id and name = @name),'&ouml;', 'ö')
        where meta_id = @meta_id and name = @name
	update #tmptexts set newtext = replace((select newtext from #tmptexts where meta_id =@meta_id and name = @name),'&Ouml;', 'Ö')
        where meta_id = @meta_id and name = @name 
	update #tmptexts set newtext = replace((select newtext from #tmptexts where meta_id =@meta_id and name = @name),'&amp;', '&')
        where meta_id = @meta_id and name = @name                
	update #tmptexts set newtext = replace((select newtext from #tmptexts where meta_id =@meta_id and name = @name),'&quot;', '"')
        where meta_id = @meta_id and name = @name                
	update #tmptexts set newtext = replace((select newtext from #tmptexts where meta_id =@meta_id and name = @name),'&lt;', '<')
        where meta_id = @meta_id and name = @name                
	update #tmptexts set newtext = replace((select newtext from #tmptexts where meta_id =@meta_id and name = @name),'&gt;', '>')
        where meta_id = @meta_id and name = @name                
	update #tmptexts set newtext = replace((select newtext from #tmptexts where meta_id =@meta_id and name = @name),'&eacute;', 'é')
        where meta_id = @meta_id and name = @name                
	update #tmptexts set newtext = replace((select newtext from #tmptexts where meta_id =@meta_id and name = @name),'&aacute;', 'á')
        where meta_id = @meta_id and name = @name                               


        select @newtext = newtext 
        from #tmptexts
        where meta_id = @meta_id and name = @name
        	
	SELECT @ptrval = TEXTPTR(text) 
   	FROM texts
      	WHERE meta_id = @meta_id and name = @name 
      	
        set @insert_offset = 0		--A value of 0 inserts the new data at the beginning of the existing data
        
        set @delete_length = NULL   	-- A value of NULL deletes all data from the insert_offset
					-- position to the end of the existing text or image column.
 
       
       /* update column text,
       deletes all data from the insert_offset position to the end of the existing text
       and inserts the new data at the beginning of the existing data */
       UPDATETEXT texts.text @ptrval @insert_offset @delete_length  @newtext  
						    
 
       	if @length = 1000
       	begin		
        while (@length > 0 )
          begin
	    select @oldtext = substring(text,@ink,1000), @length = len(substring(text,@ink,1000))
            from #tmptexts 
            where meta_id = @meta_id and name = @name
            
            update #tmptexts set newtext = @oldtext where meta_id =@meta_id and name = @name       
        
	    update #tmptexts set newtext = replace((select newtext from #tmptexts where meta_id =@meta_id and name = @name),'&aring;', 'å')
            where meta_id = @meta_id and name = @name
	    update #tmptexts set newtext = replace((select newtext from #tmptexts where meta_id =@meta_id and name = @name),'&Aring;', 'Å')
            where meta_id = @meta_id and name = @name
	    update #tmptexts set newtext = replace((select newtext from #tmptexts where meta_id =@meta_id and name = @name),'&auml;', 'ä')
            where meta_id = @meta_id and name = @name
	    update #tmptexts set newtext = replace((select newtext from #tmptexts where meta_id =@meta_id and name = @name),'&Auml;', 'Ä')
            where meta_id = @meta_id and name = @name
	    update #tmptexts set newtext = replace((select newtext from #tmptexts where meta_id =@meta_id and name = @name),'&ouml;', 'ö')
            where meta_id = @meta_id and name = @name
	    update #tmptexts set newtext = replace((select newtext from #tmptexts where meta_id =@meta_id and name = @name),'&Ouml;', 'Ö')
            where meta_id = @meta_id and name = @name                
	    update #tmptexts set newtext = replace((select newtext from #tmptexts where meta_id =@meta_id and name = @name),'&amp;', '&')
            where meta_id = @meta_id and name = @name                
	    update #tmptexts set newtext = replace((select newtext from #tmptexts where meta_id =@meta_id and name = @name),'&quot;', '"')
            where meta_id = @meta_id and name = @name                
	    update #tmptexts set newtext = replace((select newtext from #tmptexts where meta_id =@meta_id and name = @name),'&lt;', '<')
            where meta_id = @meta_id and name = @name                
	    update #tmptexts set newtext = replace((select newtext from #tmptexts where meta_id =@meta_id and name = @name),'&gt;', '>')
            where meta_id = @meta_id and name = @name                
	    update #tmptexts set newtext = replace((select newtext from #tmptexts where meta_id =@meta_id and name = @name),'&eacute;', 'é')
            where meta_id = @meta_id and name = @name                
	    update #tmptexts set newtext = replace((select newtext from #tmptexts where meta_id =@meta_id and name = @name),'&aacute;', 'á')
            where meta_id = @meta_id and name = @name                               


            select @newtext = newtext 
            from #tmptexts
            where meta_id = @meta_id and name = @name


	    SELECT @ptrval = TEXTPTR(text) 
   	    FROM texts
      	    WHERE meta_id = @meta_id and name = @name 
    
            set @insert_offset = NULL	--A value of NULL appends the new data to the existing data value.
        
            set @delete_length = 0   	-- A value of 0 deletes no data
	    	
      	    /* update column text,
            appends the new data to the existing data. */ 
            UPDATETEXT texts.text @ptrval @insert_offset @delete_length  @newtext  
            
	    set @ink = @ink + 1000
          end 
        end
        

       fetch next from posCursor 
       into @meta_id, @name
  end
close posCursor
deallocate posCursor

drop table #tmptexts


create table #tmpmeta(

	meta_id int not null,
	meta_headline varchar (255) collate Finnish_Swedish_CS_AS ,
        meta_text varchar(1000) collate Finnish_Swedish_CS_AS 

)

declare posCursor  Cursor scroll 
for select meta_id 
    from meta
    where meta_id > 1000  

open posCursor
fetch next from posCursor 
into @meta_id
while @@fetch_status = 0
   begin
        insert into #tmpmeta (meta_id, meta_headline, meta_text)
        select meta_id, meta_headline, meta_text from meta where meta_id =@meta_id 
               
       
	update #tmpmeta set meta_headline = replace((select meta_headline from #tmpmeta where meta_id =@meta_id ),'&aring;', 'å'),
			       meta_text =  replace((select meta_text from #tmpmeta where meta_id =@meta_id ),'&aring;', 'å')	
        where meta_id = @meta_id 
	update #tmpmeta set meta_headline = replace((select meta_headline from #tmpmeta where meta_id =@meta_id),'&Aring;', 'Å'),
        			meta_text = replace((select meta_text from #tmpmeta where meta_id =@meta_id),'&Aring;', 'Å')
	where meta_id = @meta_id 
	update #tmpmeta set meta_headline = replace((select meta_headline from #tmpmeta where meta_id =@meta_id),'&auml;', 'ä'),
        			meta_text = replace((select meta_text from #tmpmeta where meta_id =@meta_id),'&auml;', 'ä')
	where meta_id = @meta_id 
	update #tmpmeta set meta_headline = replace((select meta_headline from #tmpmeta where meta_id =@meta_id ),'&Auml;', 'Ä'),
       				meta_text = replace((select meta_text from #tmpmeta where meta_id =@meta_id),'&Auml;', 'Ä')
	where meta_id = @meta_id
	update #tmpmeta set meta_headline = replace((select meta_headline from #tmpmeta where meta_id =@meta_id ),'&ouml;', 'ö'),
 				meta_text = replace((select meta_text from #tmpmeta where meta_id =@meta_id),'&ouml;', 'ö')
        where meta_id = @meta_id
	update #tmpmeta set meta_headline = replace((select meta_headline from #tmpmeta where meta_id =@meta_id ),'&Ouml;', 'Ö'),
        			meta_text = replace((select meta_text from #tmpmeta where meta_id =@meta_id),'&Ouml;', 'Ö')
	where meta_id = @meta_id               
	update #tmpmeta set meta_headline = replace((select meta_headline from #tmpmeta where meta_id =@meta_id ),'&amp;', '&'),
        			meta_text = replace((select meta_text from #tmpmeta where meta_id =@meta_id),'&amp;', '&')
	where meta_id = @meta_id               
	update #tmpmeta set meta_headline = replace((select meta_headline from #tmpmeta where meta_id =@meta_id ),'&quot;', '"'),
        			meta_text = replace((select meta_text from #tmpmeta where meta_id =@meta_id),'&quot;', '"')
	where meta_id = @meta_id               
	update #tmpmeta set meta_headline = replace((select meta_headline from #tmpmeta where meta_id =@meta_id ),'&lt;', '<'),
        			meta_text = replace((select meta_text from #tmpmeta where meta_id =@meta_id),'&lt;', '<')
	where meta_id = @meta_id               
	update #tmpmeta set meta_headline = replace((select meta_headline from #tmpmeta where meta_id =@meta_id ),'&gt;', '>'),
        			meta_text = replace((select meta_text from #tmpmeta where meta_id =@meta_id),'&gt;', '>')
	where meta_id = @meta_id               
	update #tmpmeta set meta_headline = replace((select meta_headline from #tmpmeta where meta_id =@meta_id ),'&eacute;', 'é'),
        			meta_text = replace((select meta_text from #tmpmeta where meta_id =@meta_id),'&eacute;', 'é')
	where meta_id = @meta_id               
	update #tmpmeta set meta_headline = replace((select meta_headline from #tmpmeta where meta_id =@meta_id ),'&aacute;', 'á'),
        			meta_text = replace((select meta_text from #tmpmeta where meta_id =@meta_id),'&aacute;', 'á')
	where meta_id = @meta_id               


       
        update meta set meta_headline = (select meta_headline from #tmpmeta where meta_id =@meta_id),
			meta_text = (select meta_text from #tmpmeta where meta_id =@meta_id)
	where meta_id = @meta_id
						    
   	fetch next from posCursor 
   	into @meta_id
   end
close posCursor
deallocate posCursor

drop table #tmpmeta
GO 





