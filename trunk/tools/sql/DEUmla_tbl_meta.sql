

/*
Change extended characters in column meta.meta_headline and meta.meta_text where meta_id > 1000
*/

-- table meta
declare @meta_id int

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
	              
	update #tmpmeta set meta_headline = replace((select meta_headline from #tmpmeta where meta_id =@meta_id ),'&eacute;', 'é'),
        			meta_text = replace((select meta_text from #tmpmeta where meta_id =@meta_id),'&eacute;', 'é')
	where meta_id = @meta_id 
        update #tmpmeta set meta_headline = replace((select meta_headline from #tmpmeta where meta_id =@meta_id ),'&acute;', '´'),
        			meta_text = replace((select meta_text from #tmpmeta where meta_id =@meta_id),'&acute;', '´')
	where meta_id = @meta_id
        update #tmpmeta set meta_headline = replace((select meta_headline from #tmpmeta where meta_id =@meta_id ),'&Eacute;', 'É'),
        			meta_text = replace((select meta_text from #tmpmeta where meta_id =@meta_id),'&Eacute;', 'É')
	where meta_id = @meta_id               
	update #tmpmeta set meta_headline = replace((select meta_headline from #tmpmeta where meta_id =@meta_id ),'&aacute;', 'á'),
        			meta_text = replace((select meta_text from #tmpmeta where meta_id =@meta_id),'&aacute;', 'á')
	where meta_id = @meta_id               
	update #tmpmeta set meta_headline = replace((select meta_headline from #tmpmeta where meta_id =@meta_id ),'&Aacute;', 'Á'),
        			meta_text = replace((select meta_text from #tmpmeta where meta_id =@meta_id),'&Aacute;', 'Á')
	where meta_id = @meta_id               
	update #tmpmeta set meta_headline = replace((select meta_headline from #tmpmeta where meta_id =@meta_id ),'&oslash;', 'ø'),
        			meta_text = replace((select meta_text from #tmpmeta where meta_id =@meta_id),'&oslash;', 'ø')
	where meta_id = @meta_id               
	update #tmpmeta set meta_headline = replace((select meta_headline from #tmpmeta where meta_id =@meta_id ),'&Oslash;', 'Ø'),
        			meta_text = replace((select meta_text from #tmpmeta where meta_id =@meta_id),'&Oslash;', 'Ø')
	where meta_id = @meta_id               
	update #tmpmeta set meta_headline = replace((select meta_headline from #tmpmeta where meta_id =@meta_id ),'&agrave;', 'à'),
        			meta_text = replace((select meta_text from #tmpmeta where meta_id =@meta_id),'&agrave;', 'à')
	where meta_id = @meta_id               
	update #tmpmeta set meta_headline = replace((select meta_headline from #tmpmeta where meta_id =@meta_id ),'&Agrave;', 'À'),
        			meta_text = replace((select meta_text from #tmpmeta where meta_id =@meta_id),'&Agrave;', 'À')
	where meta_id = @meta_id               
	update #tmpmeta set meta_headline = replace((select meta_headline from #tmpmeta where meta_id =@meta_id ),'&egrave;', 'è'),
        			meta_text = replace((select meta_text from #tmpmeta where meta_id =@meta_id),'&egrave;', 'è')
	where meta_id = @meta_id               
	update #tmpmeta set meta_headline = replace((select meta_headline from #tmpmeta where meta_id =@meta_id ),'&Egrave;', 'È'),
        			meta_text = replace((select meta_text from #tmpmeta where meta_id =@meta_id),'&Egrave;', 'È')
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



	

