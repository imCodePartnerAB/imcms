SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetTexts]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetTexts]
;


CREATE PROCEDURE GetTexts
@meta_id int AS
/**
	DOCME: Document me!
**/

select '#txt'+convert(varchar(5), name)+'#',name,type,text from texts where meta_id = @meta_id


;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

