SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS OFF 
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetText]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetText]
;

CREATE PROCEDURE GetText @meta_id INT, @no INT AS
/*
	Retrieve a text with type
*/
SELECT  text, type FROM texts WHERE meta_id = @meta_id AND name = @no

;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

