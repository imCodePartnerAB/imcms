SET QUOTED_IDENTIFIER ON 
;
SET ANSI_NULLS OFF 
;

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[SetInclude]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[SetInclude]
;


CREATE PROCEDURE SetInclude @meta_id INT, @include_id INT, @included_meta_id INT AS
/**
	DOCME: Document me!
**/

DELETE FROM	includes 
WHERE 	meta_id = @meta_id
	AND 	include_id = @include_id
INSERT INTO	includes	 (meta_id, include_id, included_meta_id)
VALUES	(@meta_id, @include_id, @included_meta_id)


;
SET QUOTED_IDENTIFIER OFF 
;
SET ANSI_NULLS ON 
;

