CREATE PROCEDURE GetTexts
@meta_id int AS
/**
	DOCME: Document me!
**/

select '#txt'+convert(varchar(5), name)+'#',name,type,text from texts where meta_id = @meta_id
