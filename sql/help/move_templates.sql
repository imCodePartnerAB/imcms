
--OBS! detta script flyttar mallarna 2 och 3 till de sista 2 lediga nummren
--så när ni kört scriptet får ni reda på vilka de nya nummren är
--om det står 0 har inte mallen flyttats efter som den inte fanns
--men annars är det bara att gå in i templates/text och byta namn på mallarna till det nya
--kopiera sedan in hjälpmallarna 2.html och 3.html i templates/text och kör sedan
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
declare @change1 int
declare @change2 int

declare @templateMax int
declare @mall2 int
declare @mall3 int


select @mall2 = template_id from templates
where template_id=2

select @mall3 = template_id from templates
where template_id=3

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
	
	set @change1 = @templateMax
	
END
else BEGIN
	set @change1 = 0

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
	
	set @change2 = @templateMax
	
END
else BEGIN
	set @change2 = 0


END

select @change1 as mall2, @change2 as mall3