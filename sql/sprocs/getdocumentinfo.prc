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
	 description,
	doc_type,
	meta_headline,
	meta_text,
	meta_image,
	owner_id,
	permissions,
	shared,
	expand,
	show_meta,
	help_text_id,
	archive,
	status_id,
	lang_prefix,
	classification,
	date_created,
	date_modified,
	sort_position,
	menu_position,
	disable_search,
	target,
	frame_name,
	activated_datetime,
	archived_datetime,
	publisher_id
 FROM meta
 WHERE meta_id=@meta_id
GO
SET QUOTED_IDENTIFIER OFF
GO
SET ANSI_NULLS ON
GO

