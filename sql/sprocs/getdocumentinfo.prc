SET QUOTED_IDENTIFIER OFF
GO
SET ANSI_NULLS OFF
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[GetDocumentInfo]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)
drop procedure [dbo].[GetDocumentInfo]
GO



CREATE PROCEDURE GetDocumentInfo
 @meta_id int
AS
 SELECT meta_id,
	doc_type,
	meta_headline,
	meta_text,
	meta_image,
	owner_id,
	permissions,
	shared,
	show_meta,
	lang_prefix,
	date_created,
	date_modified,
	disable_search,
	target,
	archived_datetime,
	publisher_id,
	status,
	publication_start_datetime,
	publication_end_datetime
 FROM meta
 WHERE meta_id=@meta_id
GO
SET QUOTED_IDENTIFIER OFF
GO
SET ANSI_NULLS ON
GO

