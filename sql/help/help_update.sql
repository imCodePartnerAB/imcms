-- Script name = help_update.sql
 
-- Run this script to update a database with the latest help-page 
 
-- This script is autocreated by script "create_update_help.sql"
 
-- Soures database = help
-- Server = LENNART
-- Create date = 2002-03-05
-- Update intervall = meta_id from 91 to 290
 
 
--Först kollar vi att det inte redan finns mallar med id 2 tom 5 för dessa skall vi använda till hjälpmallar
 
DECLARE @temp int 
declare @message varchar(100)
SET @temp = 0  
SELECT @temp = template_id
FROM templates
WHERE ( template_id > 1 and template_id < 6 ) and template_name not like 'Help%'
IF @temp > 0 
	select 'Det finns befintliga mallar som måste bytas namn på. Detta görs genom att köra script remove_templates.sql. Läs manualen för att se hur man ska göra!' as message
 
else
begin

-- ok vi kan börja ösa in i databasen

Begin Tran
-- drop constraints 

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_childs_meta1]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[childs] DROP CONSTRAINT FK_childs_meta1

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_roles_rights_meta]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[roles_rights] DROP CONSTRAINT FK_roles_rights_meta

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_text_docs_meta]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[text_docs] DROP CONSTRAINT FK_text_docs_meta

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_browser_docs_meta]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[browser_docs] DROP CONSTRAINT FK_browser_docs_meta

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_permission_sets_meta]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[doc_permission_sets] DROP CONSTRAINT FK_permission_sets_meta

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_frameset_docs_meta]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[frameset_docs] DROP CONSTRAINT FK_frameset_docs_meta

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_images_meta]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[images] DROP CONSTRAINT FK_images_meta

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_includes_meta]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[includes] DROP CONSTRAINT FK_includes_meta

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_includes_meta1]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[includes] DROP CONSTRAINT FK_includes_meta1

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_meta_classification_meta]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[meta_classification] DROP CONSTRAINT FK_meta_classification_meta

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_meta_section_meta]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[meta_section] DROP CONSTRAINT FK_meta_section_meta

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_new_doc_permission_sets_meta]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[new_doc_permission_sets] DROP CONSTRAINT FK_new_doc_permission_sets_meta

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_texts_meta]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[texts] DROP CONSTRAINT FK_texts_meta

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_url_docs_meta]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[url_docs] DROP CONSTRAINT FK_url_docs_meta

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_user_rights_meta]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[user_rights] DROP CONSTRAINT FK_user_rights_meta
--First delete all meta_id 
DELETE FROM meta WHERE meta_id >= 91 and meta_id <= 290
 
--Lets insert all meta_id
 
SET IDENTITY_INSERT meta ON
 

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (91,'',2,'Administrera avdelningar','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (92,'',2,'Lägg till ny avdelning','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (93,'',2,'Ta bort avdelning','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (94,'',2,'Varning - Ta bort avdelning','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (95,'',2,'Ändra namn på avdelning','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (96,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (97,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (98,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (99,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (100,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (101,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (102,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (103,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (104,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (105,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (106,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (107,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (108,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (109,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (110,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (111,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (112,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (113,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (114,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (115,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (116,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (117,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (118,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (119,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (120,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (121,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (122,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (123,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (124,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (125,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (126,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (127,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (128,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (129,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (130,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (131,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (132,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (133,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (134,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (135,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (136,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (137,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (138,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (139,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (140,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (141,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (142,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (143,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (144,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (145,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (146,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (147,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (148,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (149,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (150,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (151,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (152,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (153,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (154,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (155,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (156,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (157,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (158,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (159,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (160,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (161,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (162,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (163,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (164,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (165,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (166,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (167,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (168,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (169,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (170,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (171,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (172,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (173,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (174,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (175,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (176,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (177,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (178,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (179,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (180,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (181,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (182,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (183,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (184,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (185,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (186,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (187,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (188,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (189,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (190,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (191,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (192,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (193,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (194,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (195,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (196,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (197,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (198,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (199,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (200,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (201,'',2,'Help Page in English','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (202,'',2,'Administrate files&nbsp;','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (203,'',2,'De-/Activate user','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (204,'',2,'Administrate user roles','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (205,'',2,'Administration page for Browser-sensitive switch','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (206,'',2,'Page changes - picture 1','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (207,'',2,'Page changes - Picture 2','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (208,'',2,'Administrate format templates / template directories ','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (209,'',2,'Change name of template directory','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (210,'',2,'Add / Delete Design Templates','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (214,'',2,'Administration Page for IP Access - Image 1','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (215,'',2,'Add a new IP Access &nbsp;','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (217,'',2,'Administrate counter','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (218,'',2,'Check Internet links','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (219,'',2,'Add link to an existing page','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (220,'',2,'Change the name of a design template','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (221,'',2,'Get uploaded design template','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (222,'',2,'Upload a new design template','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (223,'',2,'Upload template model','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (224,'',2,'Upload a design template','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (228,'',2,'Administration menu','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (229,'',2,'Administrate users and roles','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (230,'',2,'Administrate roles &nbsp;','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (231,'',2,'Change name of a role','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (232,'',2,'Add a new role','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (233,'',2,'Edit authority / rights for roles','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (235,'',2,'Administrate system information','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (236,'',2,'Add / edit text','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (244,'',2,'Add image - Image Archive','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (245,'',2,'Message','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (246,'',2,'Add a Browser-sensitive switch - Page 2','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (247,'',2,'Create a diagram - Picture 1','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (248,'',2,'Create a diagram - Picture 2 - Creating the diagram','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (249,'',2,'Create a diagram - Picture 3 - Data entry form for graph/chart and tables','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (250,'',2,'Create a diagram - Picture 4 - New diagram menu','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (251,'',2,'Right to receive password via e-mail missing','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (252,'',2,'Password via e-mail','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (253,'',2,'Include an existing page in another page','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (254,'',2,'Log-in','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (255,'',2,'Admin buttons','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (256,'',2,'Conference - change user','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (257,'',2,'Conference - administrate user data','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (258,'',2,'Conference - warning about changing template set','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (259,'',2,'Conference - administrate discussion','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (260,'',2,'Conference - administrate forum','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (261,'',2,'Conference - administrate contributions','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (262,'',2,'Conference - administrate template directory','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (263,'',2,'Conference - administrate self-registration','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (264,'',2,'Conference - edit an existing template file','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (265,'',2,'Conference - log-in','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (266,'',2,'Conference view','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (267,'',2,'Conference - self-registration','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (268,'',2,'Conference - conference data','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (269,'',2,'Conference - create a new discussion','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (270,'',2,'Conference - create a new contribution','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (271,'',2,'Add / edit user','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (272,'',2,'Add an image','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (273,'',2,'Add an image- Browse/Search&nbsp;','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (274,'',2,'Add a file upload - Page 2','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (275,'',2,'Create a static HTML page - Page 1','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (276,'',2,'Create a static HTML page - Page 2','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (277,'',2,'Create a text page - Page 1','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (278,'',2,'Create a text page - Page 2','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (279,'',2,'Add a link - function','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (280,'',2,'Add a file upload - Page 1','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (281,'',2,'Create an Internet link - Page 1','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (282,'',2,'Create an Internet link- Page 2','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (283,'',2,'Failed Log-in','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (289,'',2,'Administrate roles - main page','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)

     INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
     values (290,'',2,'Add a link - Page 1','','',1,0,0,1,0,1,0,1,'en','','2002-03-05','2002-03-05',1,1,0,'_self','',1,'2002-03-05',NULL)
 
SET IDENTITY_INSERT meta OFF
 
-- Lets create constraints

ALTER TABLE [dbo].[childs] ADD 
	CONSTRAINT [FK_childs_meta1] FOREIGN KEY 
	(
		[meta_id]
	) REFERENCES [dbo].[meta] (
		[meta_id]
	)

ALTER TABLE [dbo].[roles_rights] ADD 
	CONSTRAINT [FK_roles_rights_meta] FOREIGN KEY 
	(
		[meta_id]
	) REFERENCES [dbo].[meta] (
		[meta_id]
	)

ALTER TABLE [dbo].[browser_docs] ADD 
	CONSTRAINT [FK_browser_docs_meta] FOREIGN KEY 

	(

		[meta_id]

	) REFERENCES [dbo].[meta] (

		[meta_id]

	)

ALTER TABLE [dbo].[doc_permission_sets] ADD 

	CONSTRAINT [FK_permission_sets_meta] FOREIGN KEY 

	(

		[meta_id]

	) REFERENCES [dbo].[meta] (

		[meta_id]

	)


ALTER TABLE [dbo].[frameset_docs] ADD 

	CONSTRAINT [FK_frameset_docs_meta] FOREIGN KEY 

	(

		[meta_id]

	) REFERENCES [dbo].[meta] (

		[meta_id]

	)


ALTER TABLE [dbo].[images] ADD 

	CONSTRAINT [FK_images_meta] FOREIGN KEY 

	(

		[meta_id]

	) REFERENCES [dbo].[meta] (

		[meta_id]

	)


ALTER TABLE [dbo].[includes] ADD 

	CONSTRAINT [FK_includes_meta] FOREIGN KEY 

	(

		[meta_id]

	) REFERENCES [dbo].[meta] (

		[meta_id]

	),

	CONSTRAINT [FK_includes_meta1] FOREIGN KEY 

	(

		[included_meta_id]

	) REFERENCES [dbo].[meta] (

		[meta_id]

	)


ALTER TABLE [dbo].[meta_classification] ADD 


	CONSTRAINT [FK_meta_classification_meta] FOREIGN KEY 

	(

		[meta_id]

	) REFERENCES [dbo].[meta] (

		[meta_id]

	)


ALTER TABLE [dbo].[meta_section] ADD 

	CONSTRAINT [FK_meta_section_meta] FOREIGN KEY 

	(

		[meta_id]

	) REFERENCES [dbo].[meta] (

		[meta_id]

	)


ALTER TABLE [dbo].[new_doc_permission_sets] ADD 

	CONSTRAINT [FK_new_doc_permission_sets_meta] FOREIGN KEY 

	(

		[meta_id]

	) REFERENCES [dbo].[meta] (

		[meta_id]

	)

ALTER TABLE [dbo].[text_docs] ADD 

	CONSTRAINT [FK_text_docs_meta] FOREIGN KEY 

	(

		[meta_id]

	) REFERENCES [dbo].[meta] (

		[meta_id]

	)


ALTER TABLE [dbo].[texts] ADD 

	CONSTRAINT [FK_texts_meta] FOREIGN KEY 

	(

		[meta_id]

	) REFERENCES [dbo].[meta] (

		[meta_id]

	)


ALTER TABLE [dbo].[url_docs] ADD 

	CONSTRAINT [FK_url_docs_meta] FOREIGN KEY 

	(

		[meta_id]

	) REFERENCES [dbo].[meta] (

		[meta_id]

	)


ALTER TABLE [dbo].[user_rights] ADD 

	CONSTRAINT [FK_user_rights_meta] FOREIGN KEY 

	(

		[meta_id]

	) REFERENCES [dbo].[meta] (

		[meta_id]

	)

-- get all images
 
--delete old
DELETE FROM images WHERE meta_id >= 91 and meta_id <= 290
 
--lets insert all new in images
 
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (91,238,134,0,0,0,'1','','_self','','top','','','se/helpimages/Index-val.gif','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (92,375,0,0,0,0,'1','','_self','','top','','','se/helpimages/Index-ny.gif','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (93,236,140,0,0,0,'1','','_self','','top','','','se/helpimages/Index-tabort.gif','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (94,348,216,0,0,0,'1','','_self','','top','','','se/helpimages/Index-tabort_varning.gif','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (95,348,145,0,0,0,'1','','_self','','top','','','se/helpimages/Index-byt-namn.gif','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (202,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Admin-filadministration.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (203,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Admin-roller-av-aktiv-anv.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (204,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Admin-admin-anvoroller.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (205,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/AdminsidaBrowser2.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (206,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Admin-forandrade-dok-bild1.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (206,0,0,0,0,0,'2','','_self','','top','','','en/helpimages/Admin-forandrade-dok-bild2.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (207,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Admin-forandrade-dok-bild2.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (208,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Admin-mallar.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (209,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Admin-grupp-byt-namn.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (210,333,249,0,0,0,'1','','_self','','top','','','en/helpimages/Admin-formatgrupp-tilldela-mall.gif','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (214,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Admin-ip-access.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (214,0,0,0,0,0,'2','','_self','','top','','','en/helpimages/Admin-admin-ip-access.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (215,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Admin-IP-access-ny.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (217,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Admin-raknare.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (218,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Admin-test-av-url.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (219,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Admin-lank-bef-dok copy.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (219,0,0,0,0,0,'2','','_self','','top','','','','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (220,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Admin-mallar-byt-namn-mall.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (221,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Admin-mallar-hamta-ned.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (222,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Admin-ladda-upp-ny-mall.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (223,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Admin-mallar-ny-exempelmall.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (224,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Admin-mallar-uppl-gickbra.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (228,0,0,1,0,0,'1','','_self','','top','','','en/helpimages/Admin-meny.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (229,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Admin-roller-adm-anvoroller.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (230,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Admin-admin-roller.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (231,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Admin-roller-bytnamn.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (232,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Admin-roller-lagg-till-ny.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (233,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Admin-roller-redigera.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (235,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Admin-systeminfo.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (236,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Andra-text-html.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (236,0,0,0,0,0,'2','','_self','','top','','','en/helpimages/Admin_text_html.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (244,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Bildarkiv.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (245,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Bild-finns-redan.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (246,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Valj-Browser.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (247,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Lagg-till-diagram.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (248,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Skapa-Nytt-Diagram.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (249,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Diagram-inmatningsformular.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (249,0,0,0,0,0,'2','','_self','','top','','','en/helpimages/Diagram-tabellinstallning.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (250,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Diagram-tillbaka-till-x.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (251,0,0,1,0,0,'1','','_self','','middle','','','en/helpimages/Login-ej-e-post.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (252,0,0,1,0,0,'1','','_self','','top','','','en/helpimages/Losen-via-e-post.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (253,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Include.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (254,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/login.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (255,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Knappar3.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (256,0,0,1,0,0,'1','','_self','','top','','','en/helpimages/Konf-admin-anvandare.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (257,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Konf-admin-anvandardata.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (258,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Konf-bytmallset-varning1.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (259,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Konf-admin-diskussion.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (260,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Konf-admin-forum.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (261,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Konf-admin-kommentar.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (262,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Konf-admin-mallset.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (263,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Konf-admin-sjalvreg.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (264,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Konf-ny-mallfil.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (265,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Konferens-login.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (266,940,452,0,0,0,'1','','_self','','top','','','en/helpimages/Konf-confViewer1.gif','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (267,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Konf-sjalvreg.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (268,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Konferens-data.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (269,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Konf-admin-ny-diskussion.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (270,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Konf-admin-ny-kommentar.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (271,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Admin-lagg-till-anvandare.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (272,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Lagg-till-bild-m-bild.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (273,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Bild-Browse.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (274,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Valj-fil.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (274,0,0,0,0,0,'2','','_self','','top','','','en/helpimages/Valj-filtyp.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (275,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Lagg-till-dokument2.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (276,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/HTML-kod.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (277,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Lagg-till-dokument.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (278,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Andra-Text1.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (278,0,0,0,0,0,'2','','_self','','top','','','en/helpimages/Andra-text-html1.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (279,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Lank-valj.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (279,0,0,0,0,0,'2','','_self','','top','','','en/helpimages/Lank-Arkivera.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (280,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Lank-fil.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (281,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Lank-URL.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (282,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/NyURL.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (283,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Losen-felaktigt.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (289,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Admin-roller-huvudsida.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
     values (290,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Lagg-till-dokument.GIF','')
 
-- now we get all data in texts 
--delete old 
DELETE FROM texts WHERE meta_id >= 91 and meta_id <= 290
 
-- insert new 
INSERT INTO texts( meta_id, name, text, type )
     values(91,1,'Administrera avdelningar
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(91,3,'
<br>
<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="360">
    <tr>
      <td width="360">
                  <p align="left"> För att lägga till en ny avdelning klicka
                  på &quot;<b> Lägg till </b>&quot;.</p>
                  <p align="left"> För att ta bort en avdelning klicka på
                  &quot;<b> Ta bort </b>&quot;.</p>
                  <p align="left"> För att ändra namn på en avdelning klicka
                  på &quot;<b> Editera </b>&quot;.&nbsp;</p>
                  <p align="left">&quot;<b> Tillbaka </b>&quot; leder till
                  Administratörsmenyn.</p>
      </td>
    </tr>
  </table>
  </center>
</div>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(92,1,'Lägg till ny avdelning
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(92,3,'




<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="400">
    <tr>
      <td>
                  <p align="left"> Skriv in namnet på den nya avdelningen vid <i> Ny avdelning.</i> Klicka på
                  &quot;<b> Lägg till </b>&quot;.</p>
                  <p align="left"><i> Lista över befintliga </i>: visar en lista
                  över alla avdelningar som finns registrerade. Inom [
                  ]&nbsp; visas hur många sidor som använder avdelningen.</p>
                  <p align="left">&nbsp;&quot;<b> Tillbaka </b>&quot; leder till
                  Administrera avdelningar.</p>
        <p>&nbsp;</td>
    </tr>
  </table>
  </center>
</div>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(93,1,'Ta bort avdelning
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(93,3,'

<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="385">
    <tr>
      <td width="383">
                  <p align="left"> Välj i rullgardinslistan vilken avdelning som skall tas bort. Inom [ ]&nbsp; visas hur många
                  sidor/dokument som använder avdelningen.</p>
                  <p align="left"> Klicka på &quot;<b> Ta bort </b>&quot;.</p>
                  <p align="left"><b> Tillbaka </b>&quot; leder till Administrera
                  avdelning.</p>
      </td>
    </tr>
  </table>
  </center>
</div>

',1)
INSERT INTO texts( meta_id, name, text, type )
     values(94,1,'Varning - Ta bort avdelning
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(94,3,'

<br>
<div align="center">
  <center>

  <table border="0" cellpadding="0" cellspacing="0" width="389">
    <tr>
      <td width="387">
                  <p align="left"> En varning visas där man ser hur många
                  dokument (sidor) som är kopplade till den avdelning
                  som du vill ta bort. Välj en ny avdelning som dessa
                  dokument skall kopplas till genom att markera det i
                  rullgardinslistan. Om de inte skall kopplas till någon avdelning - välj "ta bort kopplingar". </p>
                  <p align="left"> Klicka på &quot;<b> OK </b>&quot;.</p>
                  <p align="left">&quot;<b> Avbryt </b>&quot; leder till Ta bort avdelning.</p>
      </td>
    </tr>
  </table>
    </center>
</div>

',1)
INSERT INTO texts( meta_id, name, text, type )
     values(95,1,'Ändra namn på avdelning
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(95,3,'

<br><div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="404">
    <tr>
      <td width="402">
                  <p align="left"> Välj i rullgardinslisten vilken avdelning som skall bytas namn på. Inom [ ]&nbsp; visas hur
                  många dokument/sidor som använder avdelningen.</p>
                  <p align="left"> Skriv in det nya namnet på avdelningen och
                  klicka sedan på &quot;<b> Byt namn </b>&quot;.</p>
                  <p align="left">&quot;<b> Tillbaka </b>&quot; leder till
                  Administrera avdelning.</p>
      </td>
    </tr>
  </table>
  </center>
</div>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(202,2,'By first marking a directory with your mouse and then clicking &quot;Change directory&quot; the content of the directory will be shown. By marking &quot;..\&quot; and then clicking on &quot;Change directory&quot; you will be takenb up one level on the file directory hierarchy.
<BR>
<BR>To download a file to your own harddisk or network, find the file and mark it, and then click on &quot;Download&quot;. Use &quot;Download&quot; on the left if the file is marked in the lefthand box or use the right &quot;Download&quot; if the file is marked in the righthand box. A new window will open and you can choose to save the file to your disk / network or just open it. The language on the buttons will be in the language of you web browser.
<BR>
<BR>To upload a file from your harddisk or network, click on &quot;Browse&quot; (in the middle of the form). A new window will open which allows you to search for and select the file.  Once again the language here will be taken from the web browser you 


are using. When you have found the file and selected it, the directory where the file should be copied should be marked. If you mark the directory in the lefthand box, use the Upload button on the left. If you mark the directory in the righthand box, use the Upload button on the right.
<BR> 
<BR>To copy a file to another directory,  find the file in the left box, mark the file. Go then to the righthand box and find the directory you wish to copy the file to and mark the directory. Click thereafter on &quot;Copy -&gt;&quot;. You can as well do this the other way around and copy from the right to the left box. Click instead on &quot;&lt;- Copy&quot;.
<BR>
<BR>To move a file from a directory to another directory,  select the file in the lefthand box, mark the file and in the righthand box find the directory where you wish to move the file and mark that directory. Click thereafter on &quot;Move -&gt;&quot;. You can as well do this the other way around and copy from the right to the
 l
ef
t box. Click instead on &quot;&lt;- Move&quot;.
<BR>
<BR>To change the name of a directory or file, mark the directory or file in question. Enter the new name in the field &quot;New name:&quot;. Click thereafter on &quot;Change name&quot; on the left or right depending if the directory or file is marked in the lefthand or righthand box.
<BR>
<BR>To create a new directory, mark the directory under which the new directory should be located. Enter the name in the field &quot;New name:&quot;. Click thereafter on &quot;New name&quot; on the left or right depending if the directory is marked in the lefthand or righthand box.
<BR>
<BR>To delete a directory or file, mark that directory or file and then click on &quot;Delete&quot;. The botton on the left should be used if the directory or file is marked in the lefthand box and rhet right button should be used if the directory or file is marked in the righthand box. A warning showing the complete search root to the directory or fil
e wi
ll b
e displayed. Click &quot;Yes&quot; if you are sure that that directory or file should be removed, otherwise push &quot;No&quot;.
<BR>
<BR>&quot;Back&quot; leads to the System Administration menu.
<BR>
 
 
 
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(202,1,'Administrate files
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(203,2,'Here is where users can be activated and deactivated. Deactivation means the user is still registered but can no longer log into the system. A deactivated user can be reactivated.
<BR>
<BR>Mark that/those users you wish to activate and click on &quot;Activate&quot;. The user(s) are activated and now have access to the system again.
<BR>
<BR>Mark that/those users you wish to deactivate and click on &quot;Deactivate&quot;. The user(s) are now deactivated and do not have access to the system.
<BR>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(203,1,'De-/Activate user
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(204,1,'Administrate user roles
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(204,2,'Here is where users can be given new roles, get roles taken away or move members of a role to another role.
<BR>
<BR>To give a user a new role - click on the user to be given a role and then on the role to be received. Finally click on &quot;Add a new  role&quot;. The user has now both previous role(s) as well as the newly added role.
<BR>
<BR>To remove a user from a role - click on the user to be removed from the role&acute;s membership and then on &quot;Remove&quot;. The user no longer has this role.
<BR>
<BR>To move a user to a new role - click on the user to be given a role and then on the role to which the users is to be moved to. Finally click on &quot;Move&quot;. The user is moved from the previous role to the new role.
<BR>
<BR>&quot;Cancel&quot; leads back to the previous page.
<BR>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(205,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="450">
    <TR>
    <TD>
      <UL>
	<LI><P><STRONG>Normal </STRONG> allows you to go through the switch to the web page suited for your browser / platform combination.</P>
        <LI>
        <P><STRONG>Edit</STRONG>allows you to adjust the settings of the switch.</P>
        <LI>
        <P><STRONG>Back</STRONG>takes you to the most recent web page visited.</P></LI></UL>
      <P>The other buttons function as usual.</P>
      <P><STRONG>NB! If you wish to return to this administration page, click on "links" button on a page where there is a link to the relevant page, then click on the arrow by that link.</STRONG>
      <TABLE>
        </TABLE></P>
</TD></TR></TABLE></P>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(205,1,'Administration page for Browser-sensitive switch
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(206,4,'This picture shows all the web pages / documents created during a given period. You can click on either the Meta ID or Header to go directly to the page /document.
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(206,1,'Page changes - picture 1
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(206,2,'Here is the place where you can find all of the web pages created or modified during a specific period of time.
<BR>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(206,3,'Mark the type of document or web page you wish to check and click on it. Type &quot;All&quot;  will list all types of documents / web pages in the system created during the given period. 
<BR>
<BR>Enter a start and end date for your search. The date is to be given in a YYYY-MM-DD format.
<BR>
<BR>Then click on &quot;Show&quot;.
<BR>
<BR>A new window with the results will appear.
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(207,1,'Page changes - Picture 2
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(207,2,'The picture here shows all the pages created during a specific period. You can click on the Meta ID or Header to see the web page etc.
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(208,2,'<p align="left">This is the start page for the admiistration of design templates and template directories. H&auml;r v&auml;ljs vad som skall
                g&ouml;ras. </p>Here is where you choose what type of operation you wish to begin.
                <p align="left">Choices can be made here:p>
                <ul>
                  <li>
                    <p align="left"> <i>Add</i> a new design template to the system.</li>
                  <li>
                    <p align="left"><i>Remove</i> a design template from the system.</li>
                  <li>
                    <p align="left"><i>Rename</i> an existing design template that is already in the system.</li>
                  <li>
                    <p align="left"><i>Get template</i> that is currently uploaded into the system and down a copy of it on to your harddisk or network.</li>
                  <li>
                    <p align="left"><i>Load template model</i> in the form of a screen-dump image i


nto the system so administrators understand the intended use of the design template.</li>
                  <li>
                    <p align="left"><i>Show format template</i> shows all the design templates in the system and how many/which pages are using them.</li>
                  <li>
                    <p align="left"><i>Create</i> a new template directory(which design templates can be kept in).</li>
                  <li>
                    <p align="left"><i>Remove</i> a template directory.</li>
                  <li>
                    <p align="left"><i>Rename</i> a template directory.</li>
                  <li>
                    <p align="left"><i>Assign templates</i> to a particular template directory</li>
                </ul>
                <p align="left">"Back" leads back to the main administrator menu.</p>
 
 
 
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(208,1,'Administrate format templates / template directories 
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(209,2,'Select the current name by marking it in the roll-down menu. Enter the new name by &quot;New name:&quot; Click on &quot;OK&quot;.
<BR>
<BR>&quot;Back&quot; leads to Administrate templates.
<BR>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(209,1,'Change name of template directory
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(210,1,'Add / Delete Design Templates
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(210,2,'This is where design templates can be added to a template directory or removed from one.
<BR>
<BR> Select the template directory which you wish to work with by marking it in the roll-down menu. Then click on &quot;Show templates&quot;. The templates in the selected directory will be displayed in the righthand box (as seen below).In the lefthand box all available templates are shown.
<BR>
<BR> To add a template to a template directory: Mark the template seen in the lefthand box, click on &quot;Add&quot; and the template will be added to the list of templates in the box on the righthand side...
<BR>
<BR> To remove a template from a template directory: Mark the template you wish to remove in the righthand box, click on delete and the template will be removed from the directory and is shown in the lefthand box..
<BR>
<BR>&quot;Back&quot; takes you back to Administrate design templates/template directories.
<BR>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(214,2,'By connecting the user name to the IP number of the user&acute;s computer, a user can get direct authorised access to the system without having to log in. The user gets a user name connected to the IP number. Several users within a specific range of IP numbers can have a user name in common.&nbsp;
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(214,1,'Administration Page for IP Access - Image 1
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(214,3,'When no IP access is registered in a system, the IP access page looks as the picture above.  When an IP number is registered, the page looks like the picture below.
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(214,4,'To add a new IP access - click on &quot;Add&quot;.&nbsp;
<BR>
<BR>To edit existing information - tick the checkbox in front of the user, make the changes and then click on &quot;Resave&quot;.
<BR>
<BR>To remove a user - tick the checkbox in front of the user and then click on &quot;Delete&quot;.
<BR>
<BR>To return to the previous image - click on &quot;Back&quot;.
<BR>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(215,1,'Add a new IP Access  
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(215,2,'Select the user by pulling down the roll-down menu. Enter the corresponding IP number or range of IP numbers valid for the user. Click on &quot;Save&quot;.
<BR>
<BR>To return to the previous image without adding an IP number - click on &quot;Cancel&quot;.
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(217,1,'Administrate counter
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(217,2,'The counter can be modified by entering a new value in the top white box and then clicking on &quot;Update&quot;. If the counter is to be restarted, simply enter a zero in the box.
<BR>
<BR>To modify the starting date, fill in a new date in the lower white box and then click on &quot;Update&quot;. This will be used when publishing the number of visitors visitng your site since this date.
<BR>
<BR>Example below: The number of visitors to the site is 6731 since 2000-01-01.
<BR>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(218,1,'Check Internet links
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(218,2,'You can choose to click on the Meta ID by each link to come direct to the link&acute;s administration page, for example to modify the URL refernce or see the link&acute;s context. Click on the URL addresses themselves and you will come to the Internet site and page which the URL refers to, for example to check the actual status of the page or its content. Boxes under the headers: &quot;Server found&quot;, &quot;Server accessible&quot; and &quot;Page found&quot; are coloured green as confirmation or red for unconfirmed status requiring manual checking. This way can all external link be checked rapidly to identify any dead links for modification.
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(219,1,'Add link to an existing page
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(219,2,'There are two ways to add a link to an existing page.
If you know the page&acute;s Meta ID enter it directly into the Meta ID box and click "OK".  Otherwise ther is a possibility to seach for and link to the page you have in mind.
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(219,4,'
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(219,3,'<p>In the top section - "<b><i>select an existing page</i></b>"
        enter the page&acute;s Meta ID and push on "<b>Add link</b>".</p>
        <p>In the larger bottom section - "<b>Search for an existing page</b>"
        enter your search word(s) and push on the "Search" button. If you want to use several search words just leave a blank space between them (NB! no commas). By ticking "AND" the search will only identify pages containing all the search words. Ticking "OR" on the other hand will find all pages where one of the search words is found.<o:p>
        </o:p>
        </p>
        <p>"<b><i>Include type of page</i></b>" - here you can
        limit your searches by marking the types of pages you wish to find.</p>
        <p>"<b><i>Include pages between these dates</i></b>" -
        here you can limit your search to pages between the given start and end dates.</p>
        <p>By marking "<i><b>Created</b></i>" only pages created during the given period will be found.<


/p>
        <p>If you mark"<b><i>Modified</i></b>" only those pages modified during the period will be found.</p>
        <p>The results of a search can be listed by Header, Meta ID, Page Type, Date Modified, Date Created,Date Archived or Date Activated. Mark which sorting order you want.</p>
        <p><span style="font-size:12.0pt;font-family:"Times New Roman";
mso-fareast-font-family:"Times New Roman";mso-ansi-language:SV;mso-fareast-language:
SV;mso-bidi-language:AR-SA">Click on"<b style="mso-bidi-font-weight:
normal"><i style="mso-bidi-font-style:normal">Search</i></b>". The results of the search are found at the bottom of the page. Link any number of pages to your current page by ticking the checkbox by the page found and then pushing "Link ticked page(s)"</span>
 
 
 
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(220,1,'Change the name of a design template
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(220,2,'Select the template that you wish to rename in the roll-down menu. Enter the new name. The click on &quot;Change name&quot;.
<BR>
<BR>&quot;Back&quot; leads you back to the previous page.
<BR>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(221,1,'Get uploaded design template
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(221,3,'Select the template you wish to download by finding it in the roll-down menu. Then click on &quot;Get template&quot;.
<BR>
<BR>A new page appears asking if you wish to save it to your harddisk / network or just open it. The buttons will be in the language of your web browser. Click on &quot;OK&quot; and select where on your harddisk or network you wish to save the template.
<BR>
<BR>&quot;Back&quot; (partially hidden in the picture) leads to Administrate templates.
<BR>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(222,1,'Upload a new design template
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(222,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
                <blockquote>
                  <p align="left">Find the design template you wish to upload by using the "Browse/Search" button (the name depends
                  on the language used by your web browser).</p>
                  <p align="left">Enter the name you wish the template should have in the system. A <img border="0" src="/imcode/images/se/helpimages/Admin-4.GIF" width="13" height="14">
                  by <i> "Overwrite existing file" </i> will, if a template by that name is already in the system, erase and overwrite the template by that name which is already in the system. Do not put
                   <img border="0" src="/imcode/images/se/helpimages/Admin-4.GIF" width="13" height="14">
                   if you are not sure you wish to delete the existing template.</p>
                  <p align="left">Mark in the list to the right


 which template directories should have access to this template. This can be done at a later point in time but a template must be assigned to at least one directory to be used.</p>
                  <p align="left">By clicking on "Upload"
                  the design template will be copied into the directory/directories.</p>
                  <p align="left">&nbsp;"Back" leads to
                  Administrate templates.</p>
                </blockquote>
      </td>
    </tr>
  </table>
  </center>
</div>
 
 
 
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(223,1,'Upload template model
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(223,2,'A template model of a design template is an image of how the template is intended to be used, i e how a typical page using this template should look. <br>
<p align="left">To upload a new template model:</p>
                  <ul>
                    <li>
                      <p align="left"><i>Select the model: </i>Find the model (gif or jpeg) on your harddisk or network using the "Browse/Search" button (the name depends on which language your web browser uses).</li>
                    <li>
                      <p align="left"><i>Select template: </i>Select the design template that is exemplified by the model. Find it in the roll-down menu.
                      Templates marked with a * already have a model connected to them.</li>
                    <li>
                      <p align="left">Click on "Upload".</li>
                  </ul>
                  <p align="left">To display the model for the template:</p>
                  <ul>
                    <li>
     


                 <p align="left"><i>Select template:</i> Select the template (with *)
                      which you wish to see by finding it in the roll-down menu.
                      Click on "Show template model". The model usage of the template will be displayed in a new window. </li>
                  </ul>
                  <p align="left">To remove a template model:</p>
                  <ul>
                    <li>
                      <p align="left"><i>Select template:</i> Select athe template in question (with *)
                      which has the model you wish to delete. Find it in the roll-down menu.
                      Click on "Remove template model".
                      The template model is deleted (* by the design template disappears as well). </li>
                  </ul>
 
 
 
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(224,2,'<center>The picture here shows a design template loaded into the system. </p>&nbsp;

"Back" leads to Administrate templates.
</center>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(224,1,'Upload a design template
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(228,1,'Administration menu
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(228,2,'Here is where you can:
                <ul>
                  <li>
                    <p>Administrate users</li>
                  <li>
                    <p>Administrate roles</li>
                  <li>
                    <p>Administrate IP access</li>
                  <li>
                    <p>Administrate templates</li>
                  <li>
                    <p>Show all pages</li>
                  <li>
                    <p>Remove a page</li>
                  <li>
                    <p>Control Internet links</li>
                  <li>
                    <p>Administrate counter</li>
                  <li>
                    <p>Administrate system information</li>
                  <li>
                    <p>Administrate files</li>
<li>
                    <p>Page changes</li>
                  <li>
                    <p>Administrate conferences</li>
                
                </ul>
                <p>Make your choice by finding th


e area you wish to administrate and "Go to
                Admin page" (hidden in the picture here).</p>
                <p>The link "Back to start page" leads back
                StartDoc, the system´s first page.
 
 
 
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(229,2,'&quot;Administrate&quot; leads to the page where the user can be given new roles, have roles taken away or have members of a role moved to another role. Mark the role you wish to work with.
<BR>
<BR>&quot;De-/Activate&quot; leads to a page where users can be activated or deactivated.  Deactivation means that the user can no longer log into the system.  A deactivated user can be reactivated. NB! Only members of the selected role are shown. Select &quot;All&quot;, in other words all the  roles and all users will be listed. 
<BR>
<BR>&quot;Back&quot; leads back to the previous page.
<BR>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(229,1,'Administrate users and roles
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(230,1,'Administrate roles  
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(230,2,'The &quot;Administrate roles&quot; button leads to the page where new roles can be added, role names can be changed, rights inherent to roles can be edited and roles can be deleted.
<BR>
<BR>The &quot;Administrate users-roles&quot; button leads to the page where administration of roles takes place. Here is where users as new members can be added to the roles, removed from them och moved to another role.
<BR>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(231,2,'<center>Enter the new name of the role and click on "Save".</p>
"Cancel" leads back to the previous page.</p></center>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(231,1,'Change name of a role
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(232,1,'Add a new role
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(232,2,'<center>
  <table border="0" cellpadding="0" cellspacing="0" width="400">
    <tr>
      <td>
                <p align="left">Enter the name of the new role that is to be added. </p>
                <p align="left">By setting a <img border="0" src="/imcode/images/se/helpimages/Admin-4.GIF" width="13" height="14">
                by <i>"Permission to get password by e-mail "</i> the users with this role, having no other role prohibiting this right can be sent their password by e-mail
                (Ordering a password is done at the log-in page with the link "Forgotten your password?").</p>
                <p align="left">By setting a <img border="0" src="/imcode/images/se/helpimages/Admin-4.GIF" width="13" height="14">
                by <i>"Self-register rights in conference "</i> this role has the right to be distributed to users who register themselves on a self-registery page (for example, to participat in an online conference). Which role(s) are given to self-registered


 users are determined by the conference administrator who selects from a list of roles which permit self-registry.
             NB! Conferences are created with the extra module imConf and may not be available in your system.</p>
                <p align="left">When the selection has been made - click on
                "Save".</p>
                <p align="left">"Cancel" leads to tthe previous page.</p>
      </td>
    </tr>
  </table>
  </center>
</div>
 
 
 
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(233,1,'Edit authority / rights for roles
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(233,2,'<p align="left">By ticking<i>"Permission to get password by e-mail "</i> the users of this role, having no other role which prohibits this, can receive their password from the system per e-mail. (Ordering is done from the log-in page under the link "Forgotten your password?").</p>
              <p align="left">By ticking<i>"Self-register rights in conference "</i> this role can be given to users who register themselves on a self-registery page (for example, to participate in an online conference. The administrator of a conference can select which role(s) are to be automatically given to users among a list of roles with permit use for self-registry. NB! Conferences are created with extra module imConf and may not be avail&ouml;able in your system.</p>
              <p align="left">When the selection is made - click on
              "Save".</p>
              <p align="left">"cancel" leads to the previous page without changing the role´s rights.</p>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(235,1,'Administrate system information
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(235,2,'On this page information, such as the name and e-mail of the web master, can be entered and then made available on any / all pages in the system through the use of imCMS tags. NB!For this information to be presented imCMS tags have to be put into the design templates used.
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(235,3,'<h3><b>Enter system message </b></h3>
          <p><i><b>Current system message: </b></i>Displays the system messagethat is now active.<p><b><i>Change system message:</i></b>
          Enter the message you wish to display on the site. Use either plain text or HTML code.<h3>Enter server master</h3>
          <p><b><i>Current servermaster:</i></b> Shows the name of the person who is now responsible for the server.<b><i> </i></b><p><b><i>Change
          servermaster:</i></b> Here is where the name of the new servermaster can be entered.<i><b> </b></i><p><i><b>Current server master e-mail:</b></i>
          Shows the e-mail of the person now registered as servermaster<i><b> </b></i> <p><b><i>Change servermaster e-mail:</i></b>
          Here is the new e-mail address of the new servermaster can be entered.<h3><b>Enter web master</b></h3>
          <p><b><i>Current webmaster: </i></b>Shows the name of the person who is now responsible for managing the web site.<p><b><i>Change
    


      webmaster: </i></b>Here is where the name of the new webmaster can be entered.<p><b><i>Current webmaster e-mail: </i></b>Shows the e-mail of the person now registered as webmaster.<p><i><b>Change
          web master e-mail: </b></i>Here is the new e-mail address of the new webmaster can be entered.
 
 
 
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(236,1,'Add /edit text
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(236,3,'It is also possible to write text in HTML format.  Then you must ensure that <b>Format</b>: <i>HTML</i> is ticked.
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(236,2,'<i>Change text Txt 10 </i> shows one of the textboxs (text box no. 10) from a web page in edit mode. <i>Meta ID</i> underneath tells you that you are editing on a specific web page having this specific Meta ID number.</p>
              <p>The original text is shown in the textbox.&nbsp; In the text box you can edit, paste in or write text, copy or delete text, 
              If it is normal text check that <b>Format</b>: <i>Plain text</i>
              is ticked.
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(244,1,'Add image - Image Archive
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(244,2,'All of the images that have been uploaded to the system are shown in the Image Archive. Click on the image file and then on &quot;Preview selected image&quot; to view the picture before publishing it on the web page. When you have found the desired image, click on &quot;Insert selected image&quot;.
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(245,1,'Message
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(245,2,'An image with the same name already exists. Click on &quot;OK&quot; and go to the &quot;Image Archive&quot; to see if it is the same image that you are trying to upload. If it is not the same image, change the name of the file on your harddisk /network and then upload it.
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(246,1,'Add a Browser-sensitive switch - Page 2
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(246,2,'The same page can look different in different web browsers. If you find out that a page is distorted by a browser/platform combination you can create an alternative page for that/those combinations. The browser-sensitive switch will automatically lead those with a certain browser/platform combination to an alternative page. Mark the browser/platform for your alternative page and click on &quot;Add&quot;. A field will appear on the righthand side. Write the Meta ID for the page that is to be shown. Repeat the process if several different browsers/platforms are to be diverted to different alternative pages. Click on &quot;OK&quot;.
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(247,1,'Create a diagram - Picture 1
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(247,2,'<h3 align="center">Create a page</h3>
<div align="center">
  <table border="1" width="75%">
    <tr>
      <td width="100%"><b>Header:</b> This text will be the main link in itself.</td>
    </tr>
    <tr>
      <td width="100%"><b>Text:</b> Additional description shown by the link.(NB!
        This text is only displayed if the design template in use permits link texts). If you do not want any description of the link, leave this box blank.</td>
    </tr>
    <tr>
      <td width="100%"><b>Image icon:</b> Here is where you enter the path to the thumbnail image or icon symbol which is already loaded into the system such as "/images/picture1.gif" to the image to be displayed.  (NB!
        This image is only displayed if the design template in use permits link image icons). If you do not wish to have a small image by the link, leave this box blank.</td>
    </tr>
  </table>
  <h3 align="center">Advanced</h3>
  <table border="1" width="75%">
    <tr>
      <td width="100


%"><b>Key words (for searches): </b>These are additional words or codes (which are not already found on the page) that will help find this page in a site search. A <img border="0" src="/imcode/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
        in the checkbox <i>Block searches from finding this page </i> prevents the page from ever turning up in local site searches.
    </tr>
    <tr>
      <td width="100%"><b>Share: </b><i>Show this page link to unauthorised users
</i>means that this link can be seen by persons without the authority to view the page.  They will be denied access to the page if they select the link. <i>Allow unauthorised local links to this page </i> means that other web administrators in your system lacking editing rights on  this page are allowed to create links to this page.</td>
    </tr>
    <tr>
      <td width="100%"><b>Publish from: </b>A future time and date can be given as publishing date if the page is not to be published now.</td>

 
  
 </tr>
    <tr>
      <td width="100%"><b>To the archives: </b>A future time and date can be given as the date when this page is to be archived. The links to this page will be on the given date. A <img border="0" src="/imcode/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
        in the checkbox <i>Archive now</i> means that the page will be archived immediately.</td>
    </tr>
    <tr>
      <td width="100%"><b>Display in: </b>Here is where you can control how the page is displayed.
        <ul>
          <li><i>Same frame</i> means that the page will be opened in the same frame as where the link is found. </li>
          <li><i>New window</i> means that the page will be opened in a new browser window. </li>
          <li><i>Full window</i> means that the page will be opened in a new frame which replaces all other frames (if there are more than one)in the current window.</li>
          <li><i>Other frame</i> - if a frameset is being used here you can cont
rol 
in w
hich frame the page will appear.</li>
        </ul>
      </td>
    </tr>
  </table>
</div>
<p align="center"><b>
After having filled in this page (or at least filled in Header)and clicked on "OK", another page for diagram creation will appear.<br><br></p>
 
 
 
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(248,1,'Create a diagram - Picture 2
<BR>Creating the diagram
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(248,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
<p align="center">This is where you choose the type of diagram you want to create.</p>
<blockquote>
                <p align="left">V&auml;lj diagramtyp: Click on <img border="0" src="/imcode/images/se/helpimages/Pil.GIF" width="16" height="21">
                to mark the type of graph you wish to use to display your data. The available alternatives are shown in the lower portion of the picture. Click on
                "Create new diagram".</p>
</blockquote>

      </td>
    </tr>
  </table>
  </center>
</div>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(249,1,'Create a diagram - Picture 3 - 
<BR>Data entry form for graph/chart and tables
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(249,2,'<h3 align="center">Diagram settings:</h3>
<table border="0" width="100%">
  <tr>
    <td width="100%">There are two ways to create a diagram, either by taking the data from an Excel file or by entering the data straight into the diagram entry form. Whatever the type of diagram, all diagrams use the first table column for the data labels to be found under or next to each data bar. In the second column, the max values for bar 1 with label of series 1 should be entered. In the third column the max values and series label for the second series should be entered and so forth. Certain settings can be automatically generated but it is possible to write in specific values if desired.</td>
  </tr>
</table>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(249,3,'<div align="CENTER">
<table border="1" width="75%" height="323">
  <tr>
    <td width="100%" height="19"><b>Heading: </b>Enter the title of the chart/graph. This can be left blank if no title is desired above the diagram.</td>
  </tr>
  <tr>
    <td width="100%" height="19"><b>Width (x and y axes): </b>Automatically generated
      or can be given in pixels here.</td>
  </tr>
  <tr>
    <td width="100%" height="19"><b>Heading(x and y axes): </b>Enter the header labels for the two axes.</td>
  </tr>
  <tr>
    <td width="100%" height="19"><b>Max value (x and y axes): </b>Automatically generated
      or the highest value for each axis can be given here. NB: If the value given here is not an integer that text written in this field will not be displayed.</td>
  </tr>
  <tr>
    <td width="100%" height="19"><b>Delta x (x and y axes): </b>Automatically generated
      or can be given here as the distance between the data points on the respective axis. NB: If the value give


n here is not an integer that text written in this field will not be displayed.</td>
  </tr>
  <tr>
    <td width="100%" height="18"><b>Series title 1, 2 etc: </b>Enter here the text you would like displayed as an explaination to the colour scheme of the diagram. The number of series titles depends on the diagram type selected. </td>
  </tr>
  <tr>
    <td width="100%" height="19">
      <p align="left"><b>Add row: </b>Adds a new row to the table. The new rad is placed at the bottom.</td>
  </tr>
  <tr>
    <td width="100%" height="19"><b>Add column: </b>Adds a new column to the table. The new column is added on the far right of the table. </td>
  </tr>
  <tr>
    <td width="100%" height="19"><b>Delete row: </b>This removes a row. Which row is actually removed you determine in the roll-down menu. <img border="0" src="/imcode/images/se/helpimages/Pil.GIF" width="16" height="21">,
      When the roll-down menu is released the row is removed. </td>
  </tr>
  <tr>
    
<t
d 
width="100%" height="19"><b>Delete column: </b> This is to remove a column.  Which column is removed is determined by your selection in the roll-down menu.
      <img border="0" src="/imcode/images/se/helpimages/Pil.GIF" width="16" height="21">,
      When the roll-down menu is released,the column is removed. </td>
  </tr>
  <tr>
    <td width="100%" height="18"><b>Paste table values here: </b>This can be used if you have the table values in an Excel spreadsheet. Copy the rows and columns as desired from the Excel file and paste into this field. Click on "Create table values".</td>
  </tr>
</table>
</div align="CENTER">
<p> </p>
<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
<h3 align="center">Table settings:</h3>

<p align="center">If you would like a supporting table of data to be published under the graph/chart, fill in the values here or cut and paste from Excel.</p>
      </td>
    </t
r>
  </
table>
  </center>
</div>
 
 
 
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(249,4,'<div align="CENTER">
<table border="1" width="75%" height="323">
  <tr>
    <td width="100%" height="19"><b>Heading: </b>Enter the title of the chart/graph. This can be left blank if no title is desired above the diagram.</td>
  </tr>
  <tr>
    <td width="100%" height="19">
      <p align="left"><b>Add row: </b>Adds a new row to the table. The new rad is placed at the bottom.</td>
  </tr>
  <tr>
    <td width="100%" height="19"><b>Add column: </b>Adds a new column to the table. The new column is added on the far right of the table. </td>
  </tr>
  <tr>
    <td width="100%" height="19"><b>Delete row: </b>This removes a row. Which row is actually removed you determine in the roll-down menu. <img border="0" src="/imcode/images/se/helpimages/Pil.GIF" width="16" height="21">,
      When the roll-down menu is released the row is removed. </td>
  </tr>
  <tr>
    <td width="100%" height="19"><b>Delete column: </b> This is to remove a column.  Which column is removed is deter


mined by your selection in the roll-down menu.
      <img border="0" src="/imcode/images/se/helpimages/Pil.GIF" width="16" height="21">,
      When the roll-down menu is released,the column is removed. </td>
  </tr>
  <tr>
    <td width="100%" height="18"><b>Paste table values here: </b>This can be used if you have the table values in an Excel spreadsheet. Copy the rows and columns as desired from the Excel file and paste into this field. Click on "Create table values".</td>
  </tr>
</table>
</div align="CENTER">
 
 
 
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(250,1,'Create a diagram - Picture 4 
<BR>New diagram menu
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(250,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
                <p align="left">Click on "Meta data" to come to the form where the header (link header), link text and link icon
                can be entered.</p>
                <p align="left">Click on "Return" to return to the original page (the page to which the diagram is to be linked).</p>
      </td>
    </tr>
  </table>
  </center>
</div>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(251,1,'Right to receive password via e-mail missing
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(251,2,'<center>You do not have a role that permits the system to send you your password or you have other high authority roles that prohibit the sending of passwords for security reasons. Please contact the system administrator to get help.</center>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(252,1,'Password via e-mail
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(252,2,'<p align="center">Enter user name and click on "Send".</p>
<p align="center">The password will be sent to the e-mail address you gave when you registered.</p>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(253,1,'Include an existing page in another page
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(253,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" width="427" cellspacing="0">
    <tr>
      <td width="425">
                <p align="left">Enter the Meta ID of the page that is to be included into the present page in the white textbox. Click thereafter on
                "OK". The included page will be shown where the white textbox and OK button were located.</p>
                <p align="left">If you click on "<i>Edit</i>"
                the included page will appear in a new window and you can edit it.</p>
      </td>
    </tr>
  </table>
  </center>
</div>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(254,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
<p align="center">Enter your user name and password.</p>
<p align="center">Then click on "OK".</p>

      </td>
    </tr>
  </table>
  </center>
</div>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(254,1,'Log-in
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(255,2,'<div align="center">
          <table border="1" width="500">
            <tr>
              <td>
              
          <h4 style="text-align:justify">Previous</h4>
          <p class="MsoBodyText" style="text-align:justify">This button takes you back to the most recent page visited prior to the present one (NB: Browser "back" button on the other hand takes you chronologically backwards step-by-step in the editing process on the current page before returning to the most recently visited page).</p>
          <h4 style="text-align:justify">Normal</h4>
          <p class="MsoBodyText" style="text-align:justify">A click on "<b style="mso-bidi-font-weight:normal">Normal</b>"returns you to normal mode to view how your changes will be seen by visitors.
          If you have not made any changes you will just leave the page´s "admin mode" view.</p>
          <h4 style="text-align:justify">Text</h4>
          <p style="text-align:justify">When you click on "<b style="mso-bidi-font


-weight:normal">Text</b>"
          small arrows will appear on the page where it is possible to add text (or HTML code). To add text - click on one of these arrows
          at the location you wish you text to appear. If there are no arrows, the current template does not support text. In this case change the template if you need to insert text.</p>
          <h4 style="text-align:justify">Image</h4>
          <p class="MsoBodyText" style="text-align:justify">When you click on          "<b style="mso-bidi-font-weight:normal">Image</b>" a small green "portrait" <i style="mso-bidi-font-style:normal"> </i>appears along with an arrow at those locations where  you may add images. To load up an image - click on the portrait symbol or the arrow where you wish to put the image. If there is already an image on the page and a small arrow appears by it in the admin image mode, click on the arrow to be able to change images.</p>
          <p class="MsoBodyText" style="text-align:justify"
>I
f 
no symbol or arrows appear when you push "image", the cuttenr template does not support adding images.  Images that do not get arrows next to them in this mode are embedded in the template. In this case you must switch templates if images are to be added.</p>
          <h4 style="text-align:justify">Links</h4>
          <p style="text-align:justify">When you click on "<b style="mso-bidi-font-weight:normal">Links</b>" a link creation roll-down menu will appear whereever and if dynamic links are possible on the page. It is possible to make the following types of links:</p>
          <blockquote>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span>Text document</p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbs
p;&n
bsp;
&nbsp;&nbsp;
            </span></span>URL document (Internet web page)</p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span>Browser-sensitive switch</p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span>HTML document</p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span>File upload</p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span>Diagram (NB:
 requi
res an
 extra software module to
            imCMS)</p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span>Conference (NB: requires an extra software module to
            imCMS)</p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span>Link to existing page</p>
          </blockquote>
          <p class="MsoBodyText" style="text-align:justify">What these link types are is explained under the corresponding headers.</p>
          <h4 style="text-align:justify">Template</h4>
          <p class="MsoBodyText" style="text-align:justify">This is where you can switch the design template used by the current page.</p>
          <h4 style="text-align:justify">Include</h4>

       
   <p st
yle="text-align:justify">When you click on "<b style="mso-bidi-font-weight:normal">Include</b>"
          any Include functionality is displayed in the form of a white admin textbox with an "<b style="mso-bidi-font-weight:normal">OK</b><span style="mso-bidi-font-weight:bold">" button
          and a link marked "<i>Edit</i>".</span> </p>
          <p style="text-align:justify">If no include textbox appears, there is no dynamic include possibilities on this template. Switch templates if you need to insert a page within a page.</p>
          <h4 style="text-align:justify">Page info</h4>
          <p class="MsoBodyText" style="text-align:justify">This is where the data about the page and settings for the page are edited.</p>
          <h4 style="text-align:justify">Authority</h4>
          <p class="MsoBodyText" style="text-align:justify">Here is where you can decide which roles have which right on this page.</p>
          <h4 style="text-align:justify">Log out
</h4>
   
       <p 
class="MsoBodyText" style="text-align:justify">When you click on
          "<b style="mso-bidi-font-weight:normal">Log out</b>", you are logged out of this imCMS site.</p>
          <h4 style="text-align:justify">Super Admin</h4>
          <p class="MsoBodyText" style="text-align:justify">This button appears only for those with super admin authority and leads to the system adnministration menu where the following functions are found:</p>
          <blockquote>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span><a href="#_Administrera_anv&auml;ndare"><span style="color:windowtext;text-decoration:none;text-underline:none">Administrate
            users</span><span style="color: windowtext; text-decoration: none; text-underline: none">&nbsp;&nbsp;&nbsp;&nbsp;
            </span></a><span style="color: wi
ndowtext; te
xt-decoratio
n: none; text-underline: none">&nbsp;
            </span></p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span><a href="#_Administrera_roller"><span style="color:
windowtext;text-decoration:none;text-underline:none">Administrate roles</span></a></p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span><a href="#_Administrera_IP-accesser"><span style="color:windowtext;text-decoration:none;text-underline:none">Administrate
            IP access</span></a></p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        
    </span></s
pan><a href="#
_Administrera_formatmallar/formatgrupper"><span style="color:windowtext;text-decoration:none;text-underline:none">Administrate
            templates</span></a></p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span><a href="#_Visa_alla_dokument"><span style="color:windowtext;
text-decoration:none;text-underline:none">Show all pages</span></a></p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span><a href="#_Ta_bort_ett"><span style="color:windowtext;
text-decoration:none;text-underline:none">Remove a page</span></a></p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Ti
mes New Roman"">
&nbsp;&nbsp;&nbs
p;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span><a href="#_Kontrollera_Internetl&auml;nkar"><span style="color:windowtext;text-decoration:none;text-underline:none">Check
            Internet links</span></a></p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span><a href="#_Administrera_r&auml;knare"><span style="color:windowtext;text-decoration:none;text-underline:none">Administrate
            counter</span></a></p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span><a href="#_Administrera_systeminformation"><span style="color:windowtext;text-decoration:none;text-underline:none">Administrate
            system messages</span></a><
/p>
            <
p class="-49" styl
e="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span><a href="#_Administrera_filer"><span style="color:windowtext;
text-decoration:none;text-underline:none">Administrate files</span></a></p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span><a href="#_F&ouml;r&auml;ndrade_dokument"><span style="color:
windowtext;text-decoration:none;text-underline:none">Page changes</span></a></p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span>Administrate conferences (NB: Conferences require an extra software module to
   
         imCMS)</p>

          </blockqu
ote>
                <p>&nbsp;</td>
            </tr>
            <tr>
              <td>
              
              </td>
            </tr>
          </table>
        </div>
      </td>
    </tr>
  </table>
  </center>
</div>
<div align="center" class="unnamed1">
 
 
 
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(255,1,'Admin buttons
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(256,1,'Conference - change user
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(256,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
        <p align="center">Mark the user you wish to change. Then click on "Change".</p>
        <p align="center">To leave the administration mode - click on
        "End Admin".</p>
        <p align="center">"Back" leads back to Administrate
        conference.</td>
    </tr>
  </table>
  </center>
</div>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(257,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td><i>Expert users:</i> By putting a <img border="0" src="/imcode/images/se/helpimages/Konf-a1.GIF" width="13" height="14">
        in the Expert checkbox, the symbol<img border="0" src="/imcode/images/se/helpimages/Konf-a2.GIF" width="12" height="16">&nbsp;
        will be placed in front of the headers of all the contributions made by this user. This is to show that the user is a specialist in the subject matter. </td>
    </tr>
  </table>
  </center>
</div>
<p align="center">&nbsp;</p>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(257,1,'Conference - administrate user data
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(258,1,'Conference - warning about changing template set
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(258,2,'If you are certain you wish to switch the template set, click &quot;OK&quot;. If you are in doubt and wish to recheck, click on &quot;Cancel&quot;.
<BR>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(259,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>To delete a discussion: <img border="0" src="/imcode/images/se/helpimages/Konf-a3.GIF" width="13" height="14">
        by the discussion you wish to remove and click on "DELETE".
        <p>To leave the administration mode: click on "End Admin".</td>
    </tr>
  </table>
  </center>
</div>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(259,1,'Conference - administrate discussion
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(260,1,'Conference - administrate forum
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(260,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="500">
    <tr>
      <td><i>Create new forum: </i>Enter the name of the new forum shall have in the name field. Click thereafter on "Create new".<p><i>Remove a forum:
        </i>Mark the forum you wish to remove.&nbsp; Click then on "Delete". You will get a warning that the forum is about to be removed. Click on "OK" if you are sure you wish to remove the forum. Click on "Cancel" if you are unsure.</p>
        <p><i>Change name of a forum:</i> Mark the forum which has the name you wish to edit by clicking on the list of forums on the right. Enter the new name in the field under <i>New name</i>. Click thereafter on "Change". A warning will appear asking you to confirm that you wish to make this name change.</p>
        <p><i>Number of discussions shown:</i> Here is where you can determine how many discussions should be shown at any one time in this forum. Mark the forum
        for which 


you wish to alter the number of forums to be displayed by clicking on the list at the right. The current number of discussions allowed to be shown in this forum is displayed after the forum in parentheses. select the new number of discussions to be shown by clicking on <img border="0" src="/imcode/images/se/helpimages/Pil.GIF" width="16" height="21">
        and marking the desired number. Click then on "Update".</td>
    </tr>
  </table>
  </center>
</div>
 
 
 
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(261,1,'Conference - administrate contributions
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(261,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td><b>Remove a contribution:</b> <img border="0" src="/imcode/images/se/helpimages/Konf-a3.GIF" width="13" height="14">
        Tick the checkbox(es) of the contribution(s) you wish to remove and then push the "DELETE" button. You will get a warning asking if you really wish to remove the contribution. Once you OK the removal the contribution is deleted from the discussion. NB: the first contribution of a discussion, in other words, the contribution which initiated the discussion and its header<b>CANNOT</b> be removed here. It is however possible to alter/edit the initial contribution or you can remove the entire discussion on the Discussion Administration page. Note that when you remove a discussion, all its contributions are also deleted.
        <p><b>Resave a contribution: </b>To alter an existing contribution,
        change the text as desired and tick the check


box of that contribution with a tick<img border="0" src="/imcode/images/se/helpimages/Konf-a3.GIF" width="13" height="14">
        . Click thereafter on "RESAVE".</p>
        <p>To return to user mode in the conference, click on "End admin".</td>
    </tr>
  </table>
  </center>
</div>
 
 
 
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(262,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td><i>Register a new template directory: </i>To create a new template directory, enter the name of the new directory and push on "new directory". NB:
        The directory´s name cannot contain any special letters or symbols.
        When a new directory is created, all the files in the directory called "Original" will be copied into the new directory.
        <p><i>Change the template directory of a conference: </i> Mark  the template directory you wish to use for the conferece you are now administrating.
        Click thereafter on 
        "Change template directory". The directories are switched. The directory now in use is displayed with <b>bold</b>&nbsp;text on the  admin page.</p>
        <p><i>Update template file: </i>To update a template file in an existing directory, first select the directory to be updated. Select thereafter the type of file to be updat


ed(The conference
        supports two types of files: images and html files). When these selections have been made, click on "Administrate".</p>
        <p>To quit administration mode, click on "End admin".</td>
    </tr>
  </table>
  </center>
</div>
<p align="center">&nbsp;</p>
 
 
 
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(262,1,'Conference - administrate template directory
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(263,1,'Conference - administrate self-registration
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(263,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>This page is used to determine which role(s) will be given to conference users who register themselves. When visitors register themselves, they will receive teh /those roles listed under <i>Existing.</i>
        <p><b><i>&nbsp;</i>Add a new role:</b> mark the role in the lefthand checkbox, click thereafter on "-->". The role is then moved over the the righthand box and the self-registered user is given that role(and any other role found in the box). </p>
        <p><b>Remove a role: </b>mark the role in the righthand box and click on "<--". The role is moved to the lefthand window.
        och nya sj&auml;lvregistrerade anv&auml;ndare kommer <b>inte</b> att tilldelas
        den rollen.</td>
    </tr>
  </table>
  </center>
</div>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(264,1,'Conference - edit an existing template file
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(264,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>Look for the file you wish to add to the system by clicking the search/browse button.
        The name of this button depends on the browser you are using. Find the file on your hard drive or network, click thereafter on 
        "Upload". The selected file will be copied onto the server. NB:
        the existing file having the same name will be overwritten.</td>
    </tr>
  </table>
  </center>
</div>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(265,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
        <p align="center"><b>Previously registered user: </b>Enter your user name and password, then click "OK".</p>
        <p align="center"><b>New user: </b>Click on <i>"REGISTER".</i></td>
    </tr>
  </table>
  </center>
</div>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(265,1,'Conference - log-in
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(266,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>Each conference has at least one forum where various discussions can take place.
        Select which forum you wish to view by clicking on <img border="0" src="/imcode/images/se/helpimages/Pil.GIF" width="16" height="21">
        by the header <i>Select forum, </i> thus marking the forum and then click on "Select". Those discussions displayed in the windows´s lefthand side are the headers of the current discussions ( more perecisely, the header of the first contribution of each discussion.  To browse through the discussions, use the buttons "earlier discussions" and "later discussions". If<img border="0" src="/imcode/images/se/helpimages/Konf-k4.GIF" width="21" height="15">&nbsp;
        (NEW symbol) is shown before a discussion this is either a new discussion or there is a new contribution in this dicussion since you last logged into the conference. To see the co


ntributions made since your last visit, push on "Update".
        <p>By clicking on the header of a discussion, all the contributions in that discussion will be displayed on the window´s righthand side. If <img border="0" src="/imcode/images/se/helpimages/Konf-k3.GIF" width="14" height="16">
        (EXPERT symbol) is shown in front of the header, the author of this contribution is registered as a specialist. After the "specialist symbol" the contribution´s header, text, author and date created are presented.</p>
        <p>You can control how the contributions are presented in your computer by clicking on 
        <i>"ascending" </i>eller <i>"</i><i>F</i><i>descending"</i>.
        Click thereafter on "Sort". The contributions are sorted by the date created. </p>
        <p>A new discussion is created by clicking "New discussion".</p>
        <p>A new contribution is made by clicking on one of the "contribute" or comment button.
        The reason that there are several bu
tt
on
s for "contribute" is that some contributions can be very long and a user need not scroll up or down to submit the contribution.</p>
        <p>To search among the contributions there is an inbuilt search function normally displayed in the top righthand corner of the window. Searches are valid for all contributions in a forum. You can search for words in the header, body or by author. Select first which of these three you wish to search among and then enter the key words. Click thereafter on "Search". The search can be limited by giving a start and end date of the search. Dates must be given in the yyyy-mm-dd format, for example 2002-02-26 or in the preset special forms like "yesterday" or "today".</p>
        <h2 align="center"><b>Administration</b></h2>
        <p>To administrate a forum, click on the "Admin" button
        in the upper lefthand corner. You will then come to an administration page where a forum can be added, removed or have its name changed. You can also 
dete
rmin
e how many discussions should be displayed at one time in the forum.</p>
        <p>To administrate the discussion click on the Admin" button below "later contributions".
        On this administration page you can delete a discussion.</p>
        <p>To administrate contributions, click on the "Admin" button in the lower righthand side of the window. The administration page that appears allows you to erase or alter and resave contributions.
        </p>
        <p>&nbsp;</td>
    </tr>
  </table>
  </center>
</div>
 
 
 
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(266,1,'Conference view
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(267,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="500">
    <tr>
      <td><i>&nbsp;</i>Fill in your personal data below. Fields: User name,
        password, verify password, given name, family name and e-mail are required.<i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </i>
        <p><i>USER NAME: </i> Enter the user name you wish to go by<i><br>
        PASSWORD: </i>Enter the pasword you wish to use (4-15 normal letter/numbers).<i><br>
        VERIFY PASSWORD: </i>Enter once again the password you just wrote.<br>
        <i>OCCUPATION / TITLE:</i> Enter your job title or other relevant description<br>
        <i>GIVEN NAME: </i>Enter your first name<br>
        <i>FAMILY NAME: </i>Enter you last name<br>
        <i>WORK ADDRESS: </i>Enter your company, place of work, department as relevant<br>
        <i>TOWN: </i>Enter the location of your workplace or residence if relevant<br>
        <i>TELEPHONE (WORK): </i>Enter your work phone<b


r>
        <i>E-MAIL:</i> Enter the e-mail address where you wish to receive messages from/about this system/site.</td>
    </tr>
  </table>
  </center>
</div>
 
 
 
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(267,1,'Conference - self-registration
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(268,2,'<div align="center">
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
        <p align="center"><i>Forum name: </i>Enter the name that the forum or "room" should have</p>
        <p align="center">Click thereafter on "OK".</td>
    </tr>
  </table>
</div>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(268,1,'Conference - conference data
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(269,1,'Conference - create a new discussion
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(269,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td><b>Create a new discussion:</b> Enter the header and text, add
        Internet link and/or e-mail if desired by clicking on link respective mail.  Fill in the necessary data in the windows which open.
        Click on "Submit". "Reset" cancels the new discussion without saving it. NB: The header of the initial contribution in a new discussion becomes the name of the discussion. The header is also the header of your contribution.</td>
    </tr>
  </table>
  </center>
</div>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(270,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td><b>Skapa ett nytt inl&auml;gg/kommentar:</b> Enter the header and text. 
        Insert links or/and e-mail address if desired by clicking on the corresponding link and filling the infpormation required in the window that opens. Click then on "Send". "Cancel"
        stops the contribution from being created or saved. </td>
    </tr>
  </table>
  </center>
</div>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(270,1,'Conference - create a new contribution
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(271,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
                <p align="left">This is where you can add new users, give
                existing users new roles and other characteristics. The fields
                marked with an * are obligatory. </p>
                <ul>
                  <li>
                    <p align="left"><i>Language</i> - selected language
                    means that the administration templates will be in this language.</li>
                  <li>
                    <p align="left"><i>Telephone number</i> - To add a                     telephone number - enter the country code(without + ), local area code and finally
                    the telephone no.&nbsp; Click thereafter on OK. To remove or change
                    a telephone number: roll down to the telephone number which is to be 
                    edited/removed by clicking on <img border="0" src="/imcode


/images/se/helpimages/Pil.GIF" width="17" height="22">.
                    After the telephone number is marked - click on the corresponding button
                    for that which you wish to do. If you choose "Edit", the telephone no. will be displayed in the boxes and then you can edit the number.  When finised, Click on
                    "OK". </li>
                  <li>
                    <p align="left"><i>Activated - </i>a <img border="0" src="/imcode/images/se/helpimages/Lagg-t1.GIF" width="13" height="14">
                    actives a user so that they can log in. <img border="0" src="/imcode/images/se/helpimages/Lagg-t2.GIF" width="13" height="14">
                    can be removed so the user cannot log into the system but remains registered.</li>
                  <li>
                    <p align="left"><i>User type</i>- authenticated
                    users are users that log into the system with a user name and password. Conference users are user
s

 
                   which have registered themselves into the system.</li>
                  <li>
                    <p align="left"><i>Roles </i>- select that or those roles
                    the user shall have by clicking on the role.
                    To select several roles, hold down "Ctrl" key while
                    clicking on the desired roles.</li>
                </ul>
                <p align="left">"Save" - sees that the user´s detaails are saved.</p>
                <p align="left">"Reset" - re-inserts the user data the form had when last saved.</p>
                <p align="left">"Cancel" - removes any new unsaved data entered into the form and returns you to the previous page.</p>
      </td>
    </tr>
  </table>
  </center>
</div>
 
 
 
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(271,1,'Add /edit user
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(272,1,'Add an image
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(272,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
<h3 align="center">Add an image</h3>
<blockquote>
  <blockquote>
      <p align="left">An image can be added to the page by either clicking of the "Browse" or "Search" button(the name of this button depends on your browser and its selected language) or by clicking on the "Image Archive" button.</p>
        <ul>
          <li>
            <p align="left">If the "Browse" or "Search" button
            is used, you will be prompted to find the image on your computer´s hard disk or in your network. </li>
        </ul>
        <ul>
          <li>
            <p align="left">If the "Image Archive" button is used, you will come to the main directory of images already loaded into the system. </li>
        </ul>
  </blockquote>
</blockquote>
      </td>
    </tr>
  </table>
  </center>
</div>
<div align="Center">
        <table border="1" width="75%">
 


         <tr>
            <td width="100%"><b>Image:</b> Here is where the path to the file in your computer/network is displayed.</td>
          </tr>
          <tr>
            <td width="100%"><b>The field under Meta ID: </b>The field is only displayed if ther is an image on the page(or if you click on
              "Preview image"). The image is then displyed here.</td>
          </tr>
          <tr>
            <td width="100%"><b>Image 3: </b>Here is where the path to the image is displayed if the image was found in the Image Archive. Click on
              "Preview image" to see the picture. (The number refers to the image number/location on the specific page).</td>
          </tr>
          <tr>
            <td width="100%"><b>Image name: </b>Here is where the name of the image is given.  It is not shown at any other location.</td>
          </tr>
          <tr>
            <td width="100%"><b>Format: </b>The image´s width, height and border or frame (if any)
 a
re
 controlled here. All measurements given are in pixels.</td>
          </tr>
          <tr>
            <td width="100%"><b>Alignment of next text: </b>Here is where you select where the text following the image should begin. NB: The design template determines if this function is enabled on this page!. Click on<b> <img border="0" src="/imcode/images/se/helpimages/Lagg-t3.GIF">
              </b>to select an alternative.
              <ul>
                <li><i>None: </i>The placement of the text is determined by the visitor´s browsers default setting.</li>
                <li><i>Baseline: </i>The text begins at the bottom righthand corner of the image with the base of the text exactly at the baseline and continues directly under the image.</li>
                <li><i>Top: </i>The first row of text begins at the top righthand corner while the second row continues directly under the image.</li>
                <li><i>Middle: </i>The first row of text starts in the cente
r of
 th
e image
                  (to the right of the righthand edge) while row two continues under the image.</li>
                <li><i>Bottom: </i>The text begins at the bottom righthand corner of the image near at the baseline and continues directly under the image.</li>
                <li><i>Texttop: </i>The highest part of the text is flush with the image´s top corner (to the right of the image.</li>
                <li><i>AbsMiddle: </i>The middle point of the text(heightwise)
                  is placed at the middle point of the image(to the right of the image).&nbsp;</li>
                <li><i>AbsBottom: </i>The lowest point of the text(such as the tail of the letter g)
                  is placed exactly at the base of the image(to the right of the image).</li>
                <li><i>Pic left: </i>The text is to the RIGHT of the image.</li>
                <li><i>Pic right: </i>The text is to the LEFT of the image.</li>
              </ul>
            </td>

    
     
 </tr>
          <tr>
            <td width="100%"><b> 
Image text while loading: </b>Text shown while the main image is being loaded(appropriate when the main image file is large or the many visitors have slow Internet access). NB! Many visually-impaired visitors to your site are dependent on a good description here as they have browsers which only read this text, without displaying the image itself.</td>
          </tr>
          <tr>
            <td width="100%"><b> 
Alt image while loading: </b>Image shown while the main image is being loaded(appropriate when the main image file is large or the many visitors have slow Internet access).</td>
          </tr>
          <tr>
            <td width="100%"><b>Space around image: </b>Here is where you decide if there should be any empty space or "air" around the image.
              The size of this space is entered in pixels. Both vertical
              and horizontal space can be controlled.</td>
          </t
r>
    
      <
tr>
            <td width="100%"><b>Linkad to www: </b>If the image should be clickable to a web site, enter the URL web address here.</td>
          </tr>
          <tr>
            <td width="100%"><b>Link shown in : </b>Here is where you can control how the page is displayed. Click on<b> <img border="0" src="/imcode/images/se/helpimages/Lagg-t3.GIF">
              </b>to select an alternative.
        <ul>
          <li><i>Current window:</i> Opens the page in the window laying on top. NB: window, not frame. </li>
<li><i>New window:</i> Opens the page in a new browser window. </li>
          
<li><i>Parent frame:</i> Opens the page in the frame or window where the frame set is found.</li>
          <li><i>Same frame:</i> Opens the page in the same frame as where the link is found. </li>
          
          <li><i>Other frame:</i> - Opens the frame which you choose in the current frameset is being used. You control in which frame will appear by enteri
ng the nam
e in the 
field to the right.</li>
              <ul>
              </ul>
            </td>
          </tr>
        </table>
</div align="Center">
 
 
 
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(273,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
<p align="center"> Search for the file on your harddisk or network. Click on the file name so that it appears in the field by File name.</p>
<p align="center">Click on "Open".</p>

        <p>&nbsp;</td>
    </tr>
  </table>
  </center>
</div>
<p align="center">&nbsp;&nbsp;&nbsp; </p>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(273,1,'Add an image- Browse/Search 
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(274,1,'Add a file upload - Page 2
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(274,3,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
        <blockquote>
                  <p align="left">Browse to find the file you wish to upload from your computer or network by clicking on "Browse"- or "Search" button.
                  Exactly what the button is called depends on your browser/browser language selected.</p>
  </center>
<h3 align="left">File type</h3>
        <p class="MsoBodyText" align="left">File types (mime extension or ending) need only be entered if you are working from a MAC. If you are using a PC the system automatically
        senses the file type.</p>
        <p class="MsoBodyText" align="left">Using the roll-down menu, enter the type of file you wish to upload. If the file is <span style="mso-spacerun: yes">&nbsp; </span>other than the alternatives given, select "<b>Other</b>" and enter into the field"<b style="mso-bidi-font-weight:normal">Other</b>"
        the correct mime 


ending.</p>
        <p class="MsoBodyText" align="left">Click on "OK".</p>
        </blockquote>
  <center>
 
 
 
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(275,1,'Create a static HTML page - Page 1
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(275,2,'<h3 align="center">Create a page</h3>
<div align="center">
  <table border="1" width="75%">
    <tr>
      <td width="100%"><b>Header:</b> This text will be the main link in itself.</td>
    </tr>
    <tr>
      <td width="100%"><b>Text:</b> Additional description shown by the link.(NB!
        This text is only displayed if the design template in use permits link texts). If you do not want any description of the link, leave this box blank.</td>
    </tr>
    <tr>
      <td width="100%"><b>Image icon:</b> Here is where you enter the path to the thumbnail image or icon symbol which is already loaded into the system such as "/images/picture1.gif" to the image to be displayed.  (NB!
        This image is only displayed if the design template in use permits link image icons). If you do not wish to have a small image by the link, leave this box blank.</td>
    </tr>
  </table>
  <h3 align="center">Advanced</h3>
  <table border="1" width="75%">
    <tr>
      <td width="100


%"><b>Key words (for searches): </b>These are additional words or codes (which are not already found on the page) that will help find this page in a site search. A <img border="0" src="/imcode/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
        in the checkbox <i>Block searches from finding this page </i> prevents the page from ever turning up in local site searches.
    </tr>
    <tr>
      <td width="100%"><b>Share: </b><i>Show this page link to unauthorised users
</i>means that this link can be seen by persons without the authority to view the page.  They will be denied access to the page if they select the link. <i>Allow unauthorised local links to this page </i> means that other web administrators in your system lacking editing rights on  this page are allowed to create links to this page.</td>
    </tr>
    <tr>
      <td width="100%"><b>Publish from: </b>A future time and date can be given as publishing date if the page is not to be published now.</td>

 
  
 </tr>
    <tr>
      <td width="100%"><b>To the archives: </b>A future time and date can be given as the date when this page is to be archived. The links to this page will be on the given date. A <img border="0" src="/imcode/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
        in the checkbox <i>Archive now</i> means that the page will be archived immediately.</td>
    </tr>
    <tr>
      <td width="100%"><b>Display in: </b>Here is where you can control how the page is displayed.
        <ul>
          <li><i>Same frame</i> means that the page will be opened in the same frame as wehere the link is found. </li>
          <li><i>New window</i> means that the page will be opened in a new browser window. </li>
          <li><i>Full window</i> means that the page will be opened in a new frame which replaces all other frames (if there are more than one)in the current window.</li>
          <li><i>Other frame</i> - if a frameset is being used here you can con
trol
 in 
which frame the page will appear.</li>
        </ul>
      </td>
    </tr>
  </table>
</div>
<p align="center"><b>
After having filled in this page (or at least filled in Header)and clicked on "OK", another page for the HTML code will appear.<br><br></p>
 
 
 
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(276,1,'Create a static HTML page - Page 2
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(276,2,'<div align="center">
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
<p align="left"><span style="font-family: Times New Roman; mso-fareast-font-family: Times New Roman; mso-ansi-language: SV; mso-fareast-language: EN-US; mso-bidi-language: HE">"<b style="mso-bidi-font-weight:normal">Empty HTML document</b>" can be used to publish static web pages, forms and framesets. The HTML code can be edited later. When you finish pasting in or writing the HTML code push "<b style="mso-bidi-font-weight:normal">OK</b>".</span> NB! When using an empty HTML document, be sure that you always begin with <HTML> and always finish with </HTML>. Mistakes in the code can prevent the dialogue box from reopening.</p>

      </td>
    </tr>
  </table>
</div>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(277,1,'Create a text page - Page 1
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(277,2,'<h3 align="center">Create a page</h3>
<div align="center">
  <table border="1" width="75%">
    <tr>
      <td width="100%"><b>Header:</b> This text will be the main link in itself.</td>
    </tr>
    <tr>
      <td width="100%"><b>Text:</b> Additional description shown by the link.(NB!
        This text is only displayed if the design template in use permits link texts). If you do not want any description of the link, leave this box blank.</td>
    </tr>
    <tr>
      <td width="100%"><b>Image icon:</b> Here is where you enter the path to the thumbnail image or icon symbol which is already loaded into the system such as "/images/picture1.gif" to the image to be displayed.  (NB!
        This image is only displayed if the design template in use permits link image icons). If you do not wish to have a small image by the link, leave this box blank.</td>
    </tr>
  </table>
  <h3 align="center">Advanced</h3>
  <table border="1" width="75%">
    <tr>
      <td width="100


%"><b>Key words (for searches): </b>These are additional words or codes (which are not already found on the page) that will help find this page in a site search. A <img border="0" src="/imcode/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
        in the checkbox <i>Block searches from finding this page </i> prevents the page from ever turning up in local site searches.
    </tr>
    <tr>
      <td width="100%"><b>Share: </b><i>Show this page link to unauthorised users
</i>means that this link can be seen by persons without the authority to view the page.  They will be denied access to the page if they select the link. <i>Allow unauthorised local links to this page </i> means that other web administrators in your system lacking editing rights on  this page are allowed to create links to this page.</td>
    </tr>
    <tr>
      <td width="100%"><b>Publish from: </b>A future time and date can be given as publishing date if the page is not to be published now.</td>

 
  
 </tr>
    <tr>
      <td width="100%"><b>To the archives: </b>A future time and date can be given as the date when this page is to be archived. The links to this page will be on the given date. A <img border="0" src="/imcode/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
        in the checkbox <i>Archive now</i> means that the page will be archived immediately.</td>
    </tr>
    <tr>
      <td width="100%"><b>Display in: </b>Here is where you can control how the page is displayed.
        <ul>
          <li><i>Same frame</i> means that the page will be opened in the same frame as wehere the link is found. </li>
          <li><i>New window</i> means that the page will be opened in a new browser window. </li>
          <li><i>Full window</i> means that the page will be opened in a new frame which replaces all other frames (if there are more than one)in the current window.</li>
          <li><i>Other frame</i> - if a frameset is being used here you can con
trol
 in 
which frame the page will appear.</li>
        </ul>
      </td>
    </tr>
  </table>
</div>
<p align="center"><b>
</p>
 
 
 
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(278,3,'<br><h3 align="center">Example of HTML format</h3>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(278,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
                <p><i>Change text</i> shows the number of the textbox where text is being added or edited. <i> Meta ID</i> shows the number of the specific page being edited.</p>
                <p>The original text is shown in the white field of the text box.&nbsp; In the text box all editing or text entry can be done.</p>
                <p>Here you have the possibility to write in plain text or HTML code. By using HTML code you can affect the appearance of the text, despite what the design template dictates. Select format plain text or HTML as desired</p>
                <p> NB! Complete HTML page code need not be entered.&nbsp;</p>
</td>
    </tr>
  </table>
  </center>
</div>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(278,1,'Create a text page - Page 2
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(279,1,'Add a link - function
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(279,4,'<p align="center">&nbsp;</p>
        <blockquote>
            <p align="left">By setting a <img border="0" src="/imcode/images/se/helpimages/Lank.h6.GIF" width="13" height="14">
            in the checkbox in front of the link, you can remove, archive or copy a link by clicking on the corresponding button. When a link is archived it will be crossed over (see picture - Page 1). Archiving can
            changed through the page´s Admin button "Page info". To sort the links numbers are used in front of the links. The numbering made by the system is random and you may change any or all numbers to sort. The highest number will always move to the top. Click
            on "Sort" to get it to re-sort according to the numbers you entered.&nbsp;</p>
            <p align="left">When you click on the "copy" button
            an exact copy of the ticked page is made and marked as copy version with the word "copy" or "(2)" added(see picture - page 2 (2)). </p>
            <p align="left">


To return to the page click on "Normal".</p>
        </blockquote>
<p align="center">&nbsp;</p>
      </td>
    </tr>
  </table>
  </center>
</div>
<p align="center">&nbsp;</p>
 
 
 
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(279,3,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
<p align="center">Click on <img border="0" src="/imcode/images/se/helpimages/Pil.GIF" width="16" height="21">
to view all the types of pages/links that can be created</p>
<div align="center">
<table border="1" width="75%">
  <tr>
    <td width="100%"><b>Text page:</b> Creates a new web page which automatically inherits all the settings of the current page. A link from the curent page to this new page is created as well.  The settings of the new page can be changed later (as long as you have the system authority to do so).</td>
  </tr>
  <tr>
    <td width="100%"><b>Internet link(URL document): </b>Create an Internet link to another web site, such as to Yahoo or Hotmail, which the system administrator can easily check that it is functioning properly.</td>
  </tr>
  <tr>
    <td width="100%"><b>Browser-sensitive switch: </b>Creates links to alternative page


s diverting visitors automatically depending on what browser and or platform (PC/Mac)the visitor is using. As browser/platform combinations can display your pages differently, you may want to ensure that visitors see similar pages. This switch can also be used as an alternative link for all browser/platform cominations to a page allowing you to use different link header and text to the same page.</td>
  </tr>
  <tr>
    <td width="100%"><b>Empty HTML page: </b>Creates an empty page linked from the current page where you can paste in an HTML form or static HTML page.</td>
  </tr>
  <tr>
    <td width="100%"><b>File upload: </b>Allows you to upload a great number of documents/files onto the system and creates a link from the current page to the file. Visitors can download the file which uses that computer´s software to run the file.  </td>
  </tr>
  <tr>
    <td width="100%"><b>Diagram: </b>Creates a graph and  a link to it from the current page. NB! This function requires a
 e
xt
ra imCMS module.</td>
  </tr>
  <tr>
    <td width="100%"><b>Conference: </b>Creates a discussion forum and  a link to it from the current page. NB! This function requires a extra imCMS module.</td>
  </tr>
  <tr>
    <td width="100%"><b>Existing page: </b>creates a link to one or more existing pages in this system.</td>
  </tr>
    
</table>
</div align "center">
<hr>
<h2 align="center">Administrate existing links</h2>
 
 
 
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(280,1,'Add a file upload - Page 1
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(280,2,'<h3 align="center">Create a page</h3>
<div align="center">
  <table border="1" width="75%">
    <tr>
      <td width="100%"><b>Header:</b> This text will be the main link in itself.</td>
    </tr>
    <tr>
      <td width="100%"><b>Text:</b> Additional description shown by the link.(NB!
        This text is only displayed if the design template in use permits link texts). If you do not want any description of the link, leave this box blank.</td>
    </tr>
    <tr>
      <td width="100%"><b>Image icon:</b> Here is where you enter the path to the thumbnail image or icon symbol which is already loaded into the system such as "/images/picture1.gif" to the image to be displayed.  (NB!
        This image is only displayed if the design template in use permits link image icons). If you do not wish to have a small image by the link, leave this box blank.</td>
    </tr>
  </table>
  <h3 align="center">Advanced</h3>
  <table border="1" width="75%">
    <tr>
      <td width="100


%"><b>Key words (for searches): </b>These are additional words or codes (which are not already found on the page) that will help find this page in a site search. A <img border="0" src="/imcode/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
        in the checkbox <i>Block searches from finding this page </i> prevents the page from ever turning up in local site searches.
    </tr>
    <tr>
      <td width="100%"><b>Share: </b><i>Show this page link to unauthorised users
</i>means that this link can be seen by persons without the authority to view the page.  They will be denied access to the page if they select the link. <i>Allow unauthorised local links to this page </i> means that other web administrators in your system lacking editing rights on  this page are allowed to create links to this page.</td>
    </tr>
    <tr>
      <td width="100%"><b>Publish from: </b>A future time and date can be given as publishing date if the page is not to be published now.</td>

 
  
 </tr>
    <tr>
      <td width="100%"><b>To the archives: </b>A future time and date can be given as the date when this page is to be archived. The links to this page will be on the given date. A <img border="0" src="/imcode/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
        in the checkbox <i>Archive now</i> means that the page will be archived immediately.</td>
    </tr>
    <tr>
      <td width="100%"><b>Display in: </b>Here is where you can control how the page is displayed.
        <ul>
          <li><i>Same frame</i> means that the page will be opened in the same frame as wehere the link is found. </li>
          <li><i>New window</i> means that the page will be opened in a new browser window. </li>
          <li><i>Full window</i> means that the page will be opened in a new frame which replaces all other frames (if there are more than one)in the current window.</li>
          <li><i>Other frame</i> - if a frameset is being used here you can con
trol
 in 
which frame the page will appear.</li>
        </ul>
      </td>
    </tr>
  </table>
</div>
<p align="center"><b>
After having filled in this page (or at least filled in Header)and clicked on "OK", another page for file upload settings will appear. <br><br></p>
 
 
 
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(281,1,'Create an Internet link - Page 1
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(281,2,'<h3 align="center">Create a page</h3>
<div align="center">
  <table border="1" width="75%">
    <tr>
      <td width="100%"><b>Header:</b> This text will be the main link in itself.</td>
    </tr>
    <tr>
      <td width="100%"><b>Text:</b> Additional description shown by the link.(NB!
        This text is only displayed if the design template in use permits link texts). If you do not want any description of the link, leave this box blank.</td>
    </tr>
    <tr>
      <td width="100%"><b>Image icon:</b> Here is where you enter the path to the thumbnail image or icon symbol which is already loaded into the system such as "/images/picture1.gif" to the image to be displayed.  (NB!
        This image is only displayed if the design template in use permits link image icons). If you do not wish to have a small image by the link, leave this box blank.</td>
    </tr>
  </table>
  <h3 align="center">Advanced</h3>
  <table border="1" width="75%">
    <tr>
      <td width="100


%"><b>Key words (for searches): </b>These are additional words or codes (which are not already found on the page) that will help find this page in a site search. A <img border="0" src="/imcode/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
        in the checkbox <i>Block searches from finding this page </i> prevents the page from ever turning up in local site searches.
    </tr>
    <tr>
      <td width="100%"><b>Share: </b><i>Show this page link to unauthorised users
</i>means that this link can be seen by persons without the authority to view the page.  They will be denied access to the page if they select the link. <i>Allow unauthorised local links to this page </i> means that other web administrators in your system lacking editing rights on  this page are allowed to create links to this page.</td>
    </tr>
    <tr>
      <td width="100%"><b>Publish from: </b>A future time and date can be given as publishing date if the page is not to be published now.</td>

 
  
 </tr>
    <tr>
      <td width="100%"><b>To the archives: </b>A future time and date can be given as the date when this page is to be archived. The links to this page will be on the given date. A <img border="0" src="/imcode/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
        in the checkbox <i>Archive now</i> means that the page will be archived immediately.</td>
    </tr>
    <tr>
      <td width="100%"><b>Display in: </b>Here is where you can control how the page is displayed.
        <ul>
          <li><i>Same frame</i> means that the page will be opened in the same frame as where the link is found. </li>
          <li><i>New window</i> means that the page will be opened in a new browser window. </li>
          <li><i>Full window</i> means that the page will be opened in a new frame which replaces all other frames (if there are more than one)in the current window.</li>
          <li><i>Other frame</i> - if a frameset is being used here you can cont
rol 
in w
hich frame the page will appear.</li>
        </ul>
      </td>
    </tr>
  </table>
</div>
<p align="center"><b>
After having filled in this page (or at least filled in Header)and clicked on "OK", another page for Internet link settings will appear.<br><br></p>
 
 
 
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(282,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="450">
    <tr>
      <td>
   
<blockquote>
   
<p align="left">Here is where you can control how the page is displayed. </p>
</blockquote>
                  <ul>
                    <li>
                      <p align="left"><i>Same frame </i> means that the page will be opened in the same frame as wehere the link is found. </li>
                  </ul>
                  <ul>
                    <li>
                      <p align="left"><i>New window </i> means that the page will be opened in a new browser window. 
&nbsp;</li>
                  </ul>
                  <ul>
                    <li>
                      <p align="left"><i>Full window </i> means that the page will be opened in a new frame which replaces all other frames (if there are more than one)in the current window. 
</li>
                  </ul>
                  <ul>
                    <li>
       


               <p align="left"><i>Other frame</i> - if a frameset is being used here you can control in which frame the page will appear. 
</li>
                  </ul>
   
      </td>
    </tr>
  </table>
  </center>
</div>
 
 
 
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(282,1,'Create an Internet link - Page 2
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(283,2,'<p align="center">Enter the user name and password once again.</p>
<p align="center">If you have forgotten the password- click on the blue link.</p>
<p align="center">If you have forgotten your user name- contact the system administrator.</p>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(283,1,'Failed Log-in
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(289,1,'Administrate roles - main page
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(289,2,'<div align="center">
  <center>
  <table border="0" width="325">
    <tr>
      <td>
<p align="left">By clicking on the corresponding button you can:
<li><i>Add a new role</i></li>
<li><i>Change name</i> of a role</li>
<li><i>Edit</i> rights etc</li>
<li> <i>Delete role </i>from the system</li>


<p align="left">"Back" leads to the previous page.</p>

      </td>
    </tr>
  </table>
  </center>
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(290,1,'Add a link - Page 1
',1)
INSERT INTO texts( meta_id, name, text, type )
     values(290,2,'<h3 align="center">Create a page</h3>
<div align="center">
  <table border="1" width="75%">
    <tr>
      <td width="100%"><b>Header:</b> This text will be the main link in itself.</td>
    </tr>
    <tr>
      <td width="100%"><b>Text:</b> Additional description shown by the link.(NB!
        This text is only displayed if the design template in use permits link texts). If you do not want any description of the link, leave this box blank.</td>
    </tr>
    <tr>
      <td width="100%"><b>Image icon:</b> Here is where you enter the path to the thumbnail image or icon symbol which is already loaded into the system such as "/images/picture1.gif" to the image to be displayed.  (NB!
        This image is only displayed if the design template in use permits link image icons). If you do not wish to have a small image by the link, leave this box blank.</td>
    </tr>
  </table>
  <h3 align="center">Advanced</h3>
  <table border="1" width="75%">
    <tr>
      <td width="100


%"><b>Key words (for searches): </b>These are additional words or codes (which are not already found on the page) that will help find this page in a site search. A <img border="0" src="/imcode/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
        in the checkbox <i>Block searches from finding this page </i> prevents the page from ever turning up in local site searches.
    </tr>
    <tr>
      <td width="100%"><b>Share: </b><i>Show this page link to unauthorised users
</i>means that this link can be seen by persons without the authority to view the page.  They will be denied access to the page if they select the link. <i>Allow unauthorised local links to this page </i> means that other web administrators in your system lacking editing rights on  this page are allowed to create links to this page.</td>
    </tr>
    <tr>
      <td width="100%"><b>Publish from: </b>A future time and date can be given as publishing date if the page is not to be published now.</td>

 
  
 </tr>
    <tr>
      <td width="100%"><b>To the archives: </b>A future time and date can be given as the date when this page is to be archived. The links to this page will be on the given date. A <img border="0" src="/imcode/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
        in the checkbox <i>Archive now</i> means that the page will be archived immediately.</td>
    </tr>
    <tr>
      <td width="100%"><b>Display in: </b>Here is where you can control how the page is displayed.
        <ul>
          <li><i>Same frame</i> means that the page will be opened in the same frame as wehere the link is found. </li>
          <li><i>New window</i> means that the page will be opened in a new browser window. </li>
          <li><i>Full window</i> means that the page will be opened in a new frame which replaces all other frames (if there are more than one)in the current window.</li>
          <li><i>Other frame</i> - if a frameset is being used here you can con
trol
 in 
which frame the page will appear.</li>
        </ul>
      </td>
    </tr>
  </table>
</div>
<p align="center"><b>
After having filled in this page (or at least filled in Header)and clicked on "OK", another page for settings will appear. Which settings page that appears depends on the type of page you are creating.<br><br>

<i>Click on the help button on the next page to receive further help regarding that function.</i></b></p>
 
 
 
',1)
 
-- add the templates and relate templates and templategroups
-- drop constraints 

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_templates_cref_templates]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[templates_cref] DROP CONSTRAINT FK_templates_cref_templates

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_text_docs_templates]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[text_docs] DROP CONSTRAINT FK_text_docs_templates
-- insert new help_templates
-- first get group_id for 'imCMShelp' from templategroups
declare @groupId int
select @groupId = group_id from templategroups
where group_name = 'imCMShelp'
 
 
-- delete old help-template from templates and from templates_cref 
DELETE FROM templates 
WHERE template_id < 12 and simple_name like 'Help%'

DELETE FROM templates_cref 
WHERE template_id < 12 and group_id = @groupId 
 
-- insert new help_templates to templates and templates_cref
 
INSERT INTO templates ( template_id , template_name , simple_name , lang_prefix , no_of_txt , no_of_img , no_of_url )
	values (2,'Helpmenu_se.html','Helpmenu_se','se',0,0,0)
INSERT INTO templates_cref(group_id, template_id)
	values (@groupId,2)
INSERT INTO templates ( template_id , template_name , simple_name , lang_prefix , no_of_txt , no_of_img , no_of_url )
	values (3,'Help_se.html','Help_se','se',4,2,0)
INSERT INTO templates_cref(group_id, template_id)
	values (@groupId,3)
INSERT INTO templates ( template_id , template_name , simple_name , lang_prefix , no_of_txt , no_of_img , no_of_url )
	values (4,'Helpmenu_en.html','Helpmenu_en','se',0,0,0)
INSERT INTO templates_cref(group_id, template_id)
	values (@groupId,4)
INSERT INTO templates ( template_id , template_name , simple_name , lang_prefix , no_of_txt , no_of_img , no_of_url )
	values (5,'Help_en.html','Help_en','se',4,2,0)
INSERT INTO templates_cref(group_id, template_id)
	values (@groupId,5)
 
-- add constraints

ALTER TABLE [dbo].[text_docs] ADD 
	CONSTRAINT [FK_text_docs_templates] FOREIGN KEY 
	(
		[template_id]
	) REFERENCES [dbo].[templates] (
		[template_id]
	)


ALTER TABLE [dbo].[templates_cref] ADD 
	CONSTRAINT [FK_templates_cref_templates] FOREIGN KEY 
	(
		[template_id]
	) REFERENCES [dbo].[templates] (
		[template_id]
	)
-- lets set templates for all help meta_ids
-- delete old text_docs
DELETE FROM text_docs WHERE meta_id >= 91 and meta_id <= 290
 
-- insert new text_docs
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (91,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (92,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (93,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (94,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (95,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (96,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (97,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (98,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (99,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (100,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (101,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (102,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (103,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (104,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (105,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (106,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (107,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (108,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (109,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (110,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (111,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (112,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (113,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (114,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (115,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (116,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (117,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (118,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (119,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (120,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (121,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (122,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (123,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (124,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (125,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (126,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (127,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (128,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (129,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (130,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (131,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (132,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (133,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (134,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (135,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (136,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (137,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (138,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (139,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (140,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (141,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (142,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (143,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (144,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (145,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (146,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (147,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (148,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (149,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (150,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (151,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (152,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (153,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (154,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (155,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (156,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (157,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (158,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (159,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (160,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (161,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (162,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (163,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (164,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (165,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (166,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (167,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (168,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (169,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (170,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (171,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (172,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (173,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (174,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (175,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (176,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (177,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (178,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (179,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (180,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (181,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (182,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (183,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (184,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (185,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (186,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (187,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (188,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (189,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (190,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (191,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (192,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (193,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (194,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (195,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (196,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (197,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (198,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (199,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (200,3, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (201,4, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (202,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (203,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (204,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (205,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (206,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (207,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (208,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (209,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (210,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (214,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (215,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (217,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (218,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (219,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (220,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (221,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (222,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (223,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (224,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (228,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (229,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (230,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (231,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (232,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (233,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (235,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (236,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (244,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (245,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (246,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (247,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (248,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (249,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (250,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (251,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (252,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (253,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (254,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (255,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (256,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (257,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (258,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (259,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (260,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (261,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (262,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (263,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (264,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (265,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (266,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (267,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (268,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (269,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (270,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (271,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (272,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (273,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (274,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (275,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (276,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (277,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (278,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (279,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (280,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (281,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (282,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (283,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (289,5, @groupId, 1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
      values (290,5, @groupId, 1,-1,-1)
 
-- insert all meta_id in childs
--delete old
DELETE FROM childs WHERE to_meta_id >= 91 and to_meta_id <= 290
 
--insert new
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (1,91,1,1390)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (1,92,1,1400)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (1,93,1,1410)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (1,94,1,1420)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (1,95,1,1430)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (1,201,1,1440)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,202,1,500)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,203,1,510)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,204,1,520)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,205,1,530)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,206,1,540)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,207,1,550)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,208,1,560)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,209,1,570)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,210,1,580)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,214,1,620)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,215,1,630)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,217,1,650)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,218,1,660)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,219,1,670)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,220,1,680)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,221,1,690)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,222,1,700)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,223,1,710)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,224,1,720)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,228,1,760)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,229,1,770)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,230,1,780)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,231,1,790)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,232,1,800)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,233,1,810)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,235,1,830)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,236,1,840)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,244,1,920)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,245,1,930)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,246,1,940)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,247,1,950)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,248,1,960)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,249,1,970)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,250,1,980)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,251,1,990)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,252,1,1000)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,253,1,1010)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,254,1,1020)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,255,1,1030)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,256,1,1040)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,257,1,1050)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,258,1,1060)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,259,1,1070)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,260,1,1080)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,261,1,1090)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,262,1,1100)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,263,1,1110)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,264,1,1120)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,265,1,1130)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,266,1,1140)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,267,1,1150)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,268,1,1160)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,269,1,1170)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,270,1,1180)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,271,1,1190)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,272,1,1200)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,273,1,1210)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,274,1,1220)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,275,1,1230)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,276,1,1240)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,277,1,1250)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,278,1,1260)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,279,1,1270)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,280,1,1280)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,281,1,1290)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,282,1,1300)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,283,1,1310)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,289,1,1370)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
      values (201,290,1,1380)
 
--lets set all role_rights
 
--delete old
DELETE FROM roles_rights WHERE meta_id >= 91 and meta_id <= 290
 
-- we have to get the role_id for users and superadmin from the current database
 DECLARE @user_roleId varchar(1), @sa_roleId varchar(1)
SELECT @user_roleId = role_id FROM roles
WHERE role_name = 'Users'
SELECT @sa_roleId = role_id FROM roles
WHERE role_name = 'Superadmin'
--insert new roles_rights
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 91, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 91,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 92, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 92,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 93, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 93,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 94, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 94,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 95, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 95,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 96, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 96,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 97, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 97,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 98, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 98,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 99, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 99,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 100, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 100,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 101, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 101,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 102, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 102,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 103, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 103,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 104, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 104,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 105, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 105,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 106, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 106,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 107, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 107,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 108, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 108,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 109, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 109,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 110, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 110,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 111, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 111,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 112, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 112,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 113, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 113,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 114, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 114,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 115, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 115,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 116, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 116,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 117, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 117,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 118, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 118,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 119, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 119,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 120, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 120,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 121, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 121,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 122, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 122,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 123, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 123,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 124, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 124,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 125, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 125,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 126, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 126,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 127, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 127,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 128, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 128,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 129, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 129,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 130, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 130,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 131, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 131,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 132, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 132,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 133, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 133,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 134, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 134,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 135, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 135,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 136, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 136,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 137, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 137,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 138, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 138,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 139, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 139,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 140, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 140,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 141, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 141,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 142, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 142,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 143, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 143,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 144, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 144,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 145, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 145,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 146, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 146,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 147, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 147,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 148, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 148,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 149, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 149,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 150, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 150,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 151, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 151,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 152, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 152,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 153, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 153,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 154, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 154,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 155, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 155,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 156, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 156,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 157, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 157,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 158, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 158,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 159, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 159,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 160, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 160,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 161, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 161,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 162, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 162,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 163, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 163,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 164, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 164,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 165, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 165,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 166, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 166,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 167, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 167,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 168, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 168,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 169, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 169,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 170, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 170,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 171, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 171,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 172, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 172,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 173, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 173,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 174, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 174,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 175, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 175,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 176, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 176,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 177, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 177,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 178, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 178,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 179, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 179,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 180, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 180,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 181, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 181,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 182, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 182,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 183, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 183,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 184, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 184,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 185, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 185,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 186, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 186,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 187, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 187,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 188, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 188,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 189, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 189,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 190, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 190,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 191, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 191,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 192, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 192,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 193, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 193,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 194, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 194,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 195, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 195,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 196, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 196,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 197, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 197,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 198, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 198,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 199, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 199,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 200, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 200,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 201, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 201,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 202, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 202,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 203, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 203,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 204, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 204,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 205, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 205,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 206, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 206,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 207, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 207,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 208, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 208,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 209, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 209,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 210, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 210,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 214, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 214,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 215, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 215,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 217, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 217,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 218, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 218,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 219, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 219,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 220, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 220,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 221, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 221,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 222, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 222,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 223, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 223,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 224, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 224,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 228, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 228,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 229, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 229,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 230, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 230,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 231, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 231,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 232, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 232,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 233, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 233,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 235, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 235,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 236, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 236,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 244, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 244,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 245, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 245,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 246, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 246,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 247, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 247,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 248, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 248,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 249, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 249,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 250, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 250,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 251, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 251,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 252, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 252,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 253, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 253,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 254, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 254,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 255, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 255,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 256, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 256,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 257, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 257,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 258, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 258,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 259, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 259,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 260, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 260,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 261, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 261,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 262, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 262,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 263, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 263,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 264, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 264,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 265, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 265,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 266, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 266,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 267, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 267,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 268, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 268,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 269, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 269,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 270, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 270,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 271, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 271,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 272, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 272,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 273, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 273,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 274, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 274,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 275, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 275,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 276, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 276,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 277, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 277,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 278, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 278,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 279, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 279,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 280, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 280,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 281, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 281,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 282, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 282,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 283, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 283,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 289, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 289,3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@user_roleId, 290, 3)
INSERT INTO roles_rights (role_id, meta_id, set_id )
      values (@sa_roleId, 290,3)
 

If @@error = 0
	BEGIN
           Commit Tran
           Print 'Commit Tran'
	END    
Else
	BEGIN
            Rollback Tran
	    Print 'Rollback Tran'
	END
END
-- End off script
