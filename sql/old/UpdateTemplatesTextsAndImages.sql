declare @t_id int
set @t_id = 
declare @new_no_txt int
declare @new_no_img int

select @new_no_txt = no_of_txt, @new_no_img = no_of_img from templates where template_id = @t_id

declare tmp cursor for
select td.meta_id,max(t.name),max(i.name) from text_docs td
left join texts t on td.meta_id = t.meta_id
left join images i on td.meta_id = i.meta_id
where td.template_id = @t_id
group by td.meta_id
having max(t.name) < @new_no_txt
or max(i.name) < @new_no_img

open tmp
declare @meta_id int
declare @max_txt int
declare @max_img int
fetch next from tmp
into @meta_id,@max_txt,@max_img

while @@fetch_status = 0
begin
	declare @no_txt int	
	declare @no_img int
	set @no_txt = @max_txt
	set @no_img = @max_img
	while @no_txt < @new_no_txt
	begin
		set @no_txt = @no_txt + 1
		insert into texts values (@meta_id,@no_txt,'',1)
	end
	while @no_img < @new_no_img
	begin
		set @no_img = @no_img + 1
		insert into images values (@meta_id,0,0,0,0,0,@no_img,'','_self','','_top','','','','')
	end
	fetch next from tmp
	into @meta_id,@max_txt,@max_img
end

close tmp
deallocate tmp