
--OBS! detta script flyttar mallarna 2 tom 5 till de sista 4 lediga nummren om 'simple_name' heter något med Helpmenu eller Help
--mallarna döps även om så att språkprefix adderas, ex Helpmenu_se  och Help_se
--så när ni kört scriptet får ni reda på vilka de nya nummren är
--om det står 0 har inte mallen flyttats efter som den inte fanns
--men annars är det bara att gå in i templates/text och byta namn på mallarna till det nya
--kopiera sedan in hjälpmallarna 2.html tom 5.html i templates/text och kör sedan
--scriptet help.sql

--variabler för att kopiera mallinfon för den mall som ska flyttas
declare @template_id int
declare @template_name varchar(80)
declare @simple_name varchar(80)
declare @lang_prefix varchar(3)
declare @no_of_txt int
declare @no_of_img int
declare @no_of_url int

-- variabler
declare @change2 int, @change3 int, @change4 int, @change5 int

declare @templateMax int
declare @mall2 int, @mall3 int, @mall4 int, @mall5 int



select @mall2 = template_id from templates
where template_id=2 and simple_name not like 'Helpmenu%' 

select @mall3 = template_id from templates
where template_id=3 and simple_name not like 'Help%'

select @mall4 = template_id from templates
where template_id=4 and simple_name not like 'Helpmenu%'

select @mall5 = template_id from templates
where template_id=5 and simple_name not like 'Help%'

if(@mall2 = 2) BEGIN
	--lets get all info
	select 	@template_id   = template_id, 
			@template_name = template_name,
			@simple_name   = simple_name,
			@lang_prefix   = lang_prefix,
			@no_of_txt	   = no_of_txt,
			@no_of_img 	   = no_of_img,
			@no_of_url     = no_of_url
	from templates
	where template_id = 2
				
	--lets get the new number
	select @templateMax = max(template_id)+1 from templates
	
	--lets create a new template
	INSERT INTO templates (template_id,template_name,simple_name,lang_prefix,no_of_txt,no_of_img,no_of_url )
	values (@templateMax,'','','',@no_of_txt,@no_of_img,@no_of_url )
	
	update text_docs
	set template_id = @templateMax
	where template_id = 2
	
	UPDATE templates_cref
	set template_id = @templateMax
	where template_id = 2
	
	--lets delete the old template
	delete from templates
	where template_id = 2
	
	--lets update the new template
	update templates
	set 	template_name = @template_name,
		simple_name 	= @simple_name,
		lang_prefix 	= @lang_prefix
	where template_id 	= @templateMax
	
	set @change2 = @templateMax
	
END
else BEGIN
	set @change2 = 0

	select @mall2 = template_id from templates
	where template_id=2
	if ( @mall2=2 ) BEGIN
		update templates set template_name = 'Helpmenu_se.html',
				     simple_name = 'Helpmenu_se'
        	where template_id=2 and simple_name like 'Helpmenu'
        END
END
  
if(@mall3 = 3) BEGIN
	--lets get all info
	select 	@template_id   = template_id, 
			@template_name = template_name,
			@simple_name   = simple_name,
			@lang_prefix   = lang_prefix,
			@no_of_txt	   = no_of_txt,
			@no_of_img 	   = no_of_img,
			@no_of_url     = no_of_url
	from templates
	where template_id = 3
				
	--lets get the new number
	select @templateMax = max(template_id)+1 from templates
	
	--lets create a new template
	INSERT INTO templates (template_id,template_name,simple_name,lang_prefix,no_of_txt,no_of_img,no_of_url )
	values (@templateMax,'','','',@no_of_txt,@no_of_img,@no_of_url )
	
	update text_docs
	set template_id = @templateMax
	where template_id = 3
	
	update templates_cref
	set template_id = @templateMax
	where template_id = 3
	
	--lets delete the old template
	delete from templates
	where template_id = 3
	
	--lets update the new template
	update templates
	set 	template_name = @template_name,
		simple_name 	= @simple_name,
		lang_prefix 	= @lang_prefix
	where template_id 	= @templateMax
	
	set @change3 = @templateMax
	
END
else BEGIN
	set @change3 = 0
	
	select @mall3 = template_id from templates
	where template_id=3
	if ( @mall3=3 ) BEGIN
		update templates set template_name = 'Help_se.html',
				     simple_name = 'Help_se'
        	where template_id=3 and simple_name like 'Help'
        END

END

if(@mall4 = 4) BEGIN
	--lets get all info
	select 	@template_id   = template_id, 
			@template_name = template_name,
			@simple_name   = simple_name,
			@lang_prefix   = lang_prefix,
			@no_of_txt	   = no_of_txt,
			@no_of_img 	   = no_of_img,
			@no_of_url     = no_of_url
	from templates
	where template_id = 4
				
	--lets get the new number
	select @templateMax = max(template_id)+1 from templates
	
	--lets create a new template
	INSERT INTO templates (template_id,template_name,simple_name,lang_prefix,no_of_txt,no_of_img,no_of_url )
	values (@templateMax,'','','',@no_of_txt,@no_of_img,@no_of_url )
	
	update text_docs
	set template_id = @templateMax
	where template_id = 4
	
	UPDATE templates_cref
	set template_id = @templateMax
	where template_id = 4
	
	--lets delete the old template
	delete from templates
	where template_id = 4
	
	--lets update the new template
	update templates
	set 	template_name = @template_name,
		simple_name 	= @simple_name,
		lang_prefix 	= @lang_prefix
	where template_id 	= @templateMax
	
	set @change4 = @templateMax
	
END
else BEGIN
	set @change4 = 0

	select @mall4 = template_id from templates
	where template_id=4
	if ( @mall4=4 ) BEGIN
		update templates set template_name = 'Helpmenu_en.html',
				     simple_name = 'Helpmenu_en'
        	where template_id=4 and simple_name like 'Helpmenu'
        END

END
  
if(@mall5 = 5) BEGIN
	--lets get all info
	select 	@template_id   = template_id, 
			@template_name = template_name,
			@simple_name   = simple_name,
			@lang_prefix   = lang_prefix,
			@no_of_txt	   = no_of_txt,
			@no_of_img 	   = no_of_img,
			@no_of_url     = no_of_url
	from templates
	where template_id = 5
				
	--lets get the new number
	select @templateMax = max(template_id)+1 from templates
	
	--lets create a new template
	INSERT INTO templates (template_id,template_name,simple_name,lang_prefix,no_of_txt,no_of_img,no_of_url )
	values (@templateMax,'','','',@no_of_txt,@no_of_img,@no_of_url )
	
	update text_docs
	set template_id = @templateMax
	where template_id = 5
	
	update templates_cref
	set template_id = @templateMax
	where template_id = 5
	
	--lets delete the old template
	delete from templates
	where template_id = 5
	
	--lets update the new template
	update templates
	set 	template_name = @template_name,
		simple_name 	= @simple_name,
		lang_prefix 	= @lang_prefix
	where template_id 	= @templateMax
	
	set @change5 = @templateMax
	
END
else BEGIN
	set @change5 = 0
	
	select @mall5 = template_id from templates
	where template_id=5
	if ( @mall5=5 ) BEGIN
		update templates set template_name = 'Helpm_en.html',
				     simple_name = 'Help_en'
        	where template_id=5 and simple_name like 'Helpmenu'
        END

END


select @change2 as mall2, @change3 as mall3, @change4 as mall4, @change5 as mall5