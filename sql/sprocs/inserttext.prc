if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[InsertText]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[InsertText]
GO

SET QUOTED_IDENTIFIER ON 
GO
SET ANSI_NULLS ON 
GO

CREATE PROCEDURE InsertText
 @meta_id int,
 @name char(15),
 @type int,
 @text ntext

AS

declare  @number int 

 select @number=counter from texts
 where meta_id = @meta_id
 and name = @name

if(@number is null) begin
     insert into texts (meta_id,name,type,text)
     values(@meta_id,@name,@type,@text)
  end
else begin
  update texts
  set text = @text
  where counter=@number
  update texts
  set type=@type
  where counter=@number
end
GO
SET QUOTED_IDENTIFIER OFF 
GO
SET ANSI_NULLS ON 
GO

