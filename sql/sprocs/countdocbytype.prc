SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[CountDocByType]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[CountDocByType]
GO

create procedure CountDocByType

/* Counts all meta document off every type where meta > 1000 (not help document) and insert the result in table countDoc 
   If all values is the same as last insert no insert will be done except for
   day 28 in month when insert always will be done. 
   This procedure is executed by SQL Server Agent in job "countDoc" every day at 07:00		
*/
as

declare @text int, @file int, @conf int, @html int, @chat int, @billb int, @url int, @browser int, @chart int
declare @textLast int, @fileLast int, @confLast int, @htmlLast int, @chatLast int, @billbLast int, @urlLast int, @browsLast int, @chartLast int
declare @SumA_doc int, @SumB_doc int
declare @lastDate datetime


select @text=count(*) from meta where doc_type = 2 and activate=1 and meta_id > 1000

select @file=count(*) from meta where doc_type = 8 and activate=1 and meta_id > 1000

select @conf=count(*)from meta where doc_type = 102 and activate=1 and meta_id > 1000 

select @html=count(*) from meta where doc_type = 7 and activate=1 and meta_id > 1000 

select @chat=count(*)from meta where doc_type = 103 and activate=1 and meta_id > 1000

select @billb=count(*) from meta where doc_type = 104 and activate=1 and meta_id > 1000

select @url=count(*)from meta where doc_type = 5 and activate=1 and meta_id > 1000

select @browser=count(*) from meta where doc_type = 6 and activate=1 and meta_id > 1000

select @chart=count(*) from meta where doc_type = 101 and activate=1 and meta_id > 1000

select @lastDate=max(countDate) from countDoc



select  @textLast=Text_doc, @fileLast=File_doc, @confLast=Conf_doc, @htmlLast=Html_doc,
	@chatLast=Chat_doc, @billbLast=Billb_doc, @urlLast=Url_doc, @browsLast=Brows_doc, @chartLast=Chart_doc
from countDoc where countDate = @lastDate 


if @text<>@textLast or @fileLast<>@file or @confLast<>@conf or @htmlLast<>@html or 
	@chatLast<>@chat or @billbLast<>@billb or @urlLast<>@url or
	@browsLast<>@browser or @chartLast<>@chart or 
	(datepart(dd, getdate())='28') or @lastDate IS NULL
begin
	select @SumA_doc= @text + @file + @conf + @html + @chat + @billb
	
	select @SumB_doc= @url + @browser + @chart
	
	insert into countDoc	(CountDate, Text_doc, File_doc, Conf_doc, Html_doc,
				Chat_doc, Billb_doc, Url_doc, Brows_doc, Chart_doc,
				SumA_doc, SumB_doc  
				)
			
			values	(convert(char(10),getdate(),120), @text, @file, @conf, @html,
				 @chat, @billb, @url, @browser, @chart,
				 @SumA_doc, @SumB_doc  
				)

end

