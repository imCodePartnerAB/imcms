
-- Script name = "help.sql" 

-- Run this script to set upp all help-page on a new database 
  
-- This script is autocreated by script "create_help.sql"
-- Soures database = help
-- Server = LENNART
-- Create date = 2002-03-07
-- Included help-page =  meta_id < 401
 
 
GO
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

SET IDENTITY_INSERT meta ON
-- lets insert new meta_id 
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (1,'',2,'Hj&auml;lpsidan','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (2,'',2,'Administrera filer','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (3,'',2,'Aktivera/avaktivera användare','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (4,'',2,'Administrera användarroller','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (5,'',2,'Administrationssida f&ouml;r Browserkontroll','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (6,'',2,'Förändrade dokument - bild 1','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (7,'',2,'Förändrade dokument - bild 2','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (8,'',2,'Administrera formatmallar/formatgrupper','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (9,'',2,'Byta namn på formatgrupp','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (10,'',2,'Lägga till/ta bort formatmallar','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (11,'',2,'Skapa formatgrupp','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (12,'',2,'Ta bort formatgrupp','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (13,'',2,'Ta bort formatgrupp - varning','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (14,'',2,'Administrationssida för IP-accesser - bild 1','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (15,'',2,'Lägga till ny IP-access ','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (16,'',2,'Ta bort IP-accesser - varning','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (17,'',2,'Administrera räknare','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (18,'',2,'Kontrollera internet-länkar','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (19,'',2,'L&auml;gga till l&auml;nk till ett befintligt dokument','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (20,'',2,'Byt namn på formatmall','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (21,'',2,'Hämta uppladdad formatmall','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (22,'',2,'Ladda upp ny formatmall','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (23,'',2,'Ladda upp ny exempelmall','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (24,'',2,'Ladda upp ny formatmall - klart!','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (25,'',2,'Ta bort formatmall','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (26,'',2,'Ta bort formatmall - varning!','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (27,'',2,'Visa formatmallar','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (28,'',2,'Administratörsmenyn','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (29,'',2,'Administrera användare och roller','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (30,'',2,'Administrera roller ','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (31,'',2,'Byt namn på roll','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (32,'',2,'Lägg till ny roll','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (33,'',2,'Redigera rättigheter för roll','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (34,'',2,'Ta bort roll - varning!','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (35,'',2,'Administrera systeminformation','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (36,'',2,'Lägga till/ändra text','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (37,'',2,'Visa alla dokument','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (38,'',2,'Ändra användaregenskaper','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (39,'',2,'Ändra dokumentinfo','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (40,'',2,'Rättigheter för begränsad behörighet 1','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (41,'',2,'Rättigheter för begränsad behörighet 1, för nya dokument','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (42,'',2,'Rättigheter för begränsad behörighet 2','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (43,'',2,'Rättigheter för begränsad behörighet 2, för nya dokument','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (44,'',2,'Lägga till bild - Bildarkiv','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (45,'',2,'Meddelande','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (46,'',2,'L&auml;gga till l&auml;nk till Browserkontroll - sida 2','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (47,'',2,'Lägga till diagram - bild 1','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (48,'',2,'Lägga till diagram - bild 2 - Skapa nytt diagram','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (49,'',2,'Lägga till diagram - bild 3 - Inmatningsformulär för diagram och tabeller','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (50,'',2,'Lägga till diagram - bild 4 - Nytt diagram meny','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (51,'',2,'Rättighet att få lösenord via e-post saknas','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (52,'',2,'Lösenord via e-post','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (53,'',2,'Inkludera en befintlig sida i en annan sida','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (54,'',2,'Inloggning','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (55,'',2,'Knappraden','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (56,'',2,'Konferens - ändra användare','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (57,'',2,'Konferens - administrera användardata','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (58,'',2,'Konferens - varning vid byte av mallset','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (59,'',2,'Konferens - administrera diskussion','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (60,'',2,'Konferens - administrera forum','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (61,'',2,'Konferens - administrera inlägg','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (62,'',2,'Konferens - administrera mallset','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (63,'',2,'Konferens - administrera självregistrering','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (64,'',2,'Konferens - ändra befintlig mallfil','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (65,'',2,'Konferens - inloggning','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (66,'',2,'Konferensvy','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (67,'',2,'Konferens - självregistrering','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (68,'',2,'Konferens - konferensdata','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (69,'',2,'Konferens - skapa en ny diskussion','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (70,'',2,'Konferens - skapa en ny kommentar','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (71,'',2,'Lägga till/redigera användare','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (72,'',2,'Lägga till bild','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (73,'',2,'Lägga till bild - Browse/Sök','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (74,'',2,'L&auml;gga till l&auml;nk till en fil - sida 2','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (75,'',2,'L&auml;gga till l&auml;nk till HTML-dokument - sida 1','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (76,'',2,'L&auml;gga till l&auml;nk till HTML-dokument - sida 2','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (77,'',2,'L&auml;gga till l&auml;nk till Text-dokument - sida 1','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (78,'',2,'L&auml;gga till l&auml;nk till Text-dokument - sida 2','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (79,'',2,'L&auml;gga till l&auml;nk - funktion','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (80,'',2,'L&auml;gga till l&auml;nk till en fil - sida 1','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (81,'',2,'L&auml;gga till l&auml;nk till URL-dokument - sida 1','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (82,'',2,'L&auml;gga till l&auml;nk till URL-dokument - sida 2','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (83,'',2,'Misslyckad inloggning','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (84,'',2,'Rättigheter','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (85,'',2,'Ta bort ett dokument','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (86,'',2,'Ta bort ett dokument - varning','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (87,'',2,'Ändra utseende på dokumentet','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (88,'',2,'Ta bort roll - varning','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (89,'',2,'Administrera roller - huvudsida','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (90,'',2,'L&auml;gga till l&auml;nk - sida 1','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (91,'',2,'Administrera avdelningar','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (92,'',2,'Lägg till ny avdelning','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (93,'',2,'Ta bort avdelning','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (94,'',2,'Varning - Ta bort avdelning','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (95,'',2,'Ändra namn på avdelning','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (96,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (97,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (98,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (99,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (100,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (101,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (102,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (103,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (104,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (105,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (106,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (107,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (108,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (109,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (110,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (111,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (112,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (113,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (114,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (115,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (116,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (117,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (118,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (119,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (120,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (121,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (122,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (123,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (124,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (125,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (126,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (127,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (128,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (129,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (130,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (131,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (132,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (133,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (134,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (135,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (136,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (137,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (138,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (139,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (140,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (141,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (142,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (143,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (144,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (145,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (146,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (147,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (148,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (149,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (150,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (151,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (152,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (153,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (154,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (155,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (156,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (157,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (158,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (159,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (160,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (161,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (162,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (163,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (164,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (165,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (166,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (167,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (168,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (169,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (170,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (171,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (172,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (173,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (174,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (175,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (176,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (177,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (178,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (179,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (180,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (181,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (182,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (183,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (184,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (185,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (186,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (187,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (188,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (189,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (190,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (191,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (192,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (193,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (194,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (195,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (196,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (197,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (198,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (199,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (200,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (201,'',2,'Help Page in English','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (202,'',2,'Administrate files&nbsp;','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (203,'',2,'De-/Activate user','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (204,'',2,'Administrate user roles','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (205,'',2,'Administration page for Browser-sensitive switch','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (206,'',2,'Page changes - picture 1','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (207,'',2,'Page changes - Picture 2','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (208,'',2,'Administrate format templates / template directories ','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (209,'',2,'Change name of template directory','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (210,'',2,'Add / Delete Design Templates','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (211,'',2,'Create template directory','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (212,'',2,'Delete a template directory','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (213,'',2,'Delete template directory - Warning','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (214,'',2,'Administration Page for IP Access - Image 1','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (215,'',2,'Add a new IP Access &nbsp;','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (216,'',2,'Delete IP access - Warning','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (217,'',2,'Administrate counter','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (218,'',2,'Check Internet links','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (219,'',2,'Add link to an existing page','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (220,'',2,'Change the name of a design template','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (221,'',2,'Get uploaded design template','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (222,'',2,'Upload a new design template','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (223,'',2,'Upload template model','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (224,'',2,'Upload a design template','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (225,'',2,'Delete a design template','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (226,'',2,'Delete a design template - Warning!','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (227,'',2,'Show design templates','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (228,'',2,'Administration menu','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (229,'',2,'Administrate users and roles','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (230,'',2,'Administrate roles &nbsp;','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (231,'',2,'Change name of a role','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (232,'',2,'Add a new role','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (233,'',2,'Edit authority / rights for roles','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (234,'',2,'Delete a role - Warning!','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (235,'',2,'Administrate system information','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (236,'',2,'Add / edit text','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (237,'',2,'Show all pages','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (238,'',2,'Change user preferences','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (239,'',2,'Change page information','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (240,'',2,'Rights for Dynamic Authority 1','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (241,'',2,'Rights for Dynamic Authority 1, for new sub-pages','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (242,'',2,'Rights for Dynamic Authority 2','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (243,'',2,'Rights for Dynamic Authority 2, for new sub-pages  ','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (244,'',2,'Add image - Image Archive','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (245,'',2,'Message','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (246,'',2,'Add a Browser-sensitive switch - Page 2','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (247,'',2,'Create a diagram - Picture 1','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (248,'',2,'Create a diagram - Picture 2 - Creating the diagram','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (249,'',2,'Create a diagram - Picture 3 - Data entry form for graph/chart and tables','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (250,'',2,'Create a diagram - Picture 4 - New diagram menu','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (251,'',2,'Right to receive password via e-mail missing','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (252,'',2,'Password via e-mail','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (253,'',2,'Include an existing page in another page','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (254,'',2,'Log-in','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (255,'',2,'Admin buttons','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (256,'',2,'Conference - change user','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (257,'',2,'Conference - administrate user data','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (258,'',2,'Conference - warning about changing template set','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (259,'',2,'Conference - administrate discussion','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (260,'',2,'Conference - administrate forum','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (261,'',2,'Conference - administrate contributions','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (262,'',2,'Conference - administrate template directory','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (263,'',2,'Conference - administrate self-registration','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (264,'',2,'Conference - edit an existing template file','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (265,'',2,'Conference - log-in','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (266,'',2,'Conference view','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (267,'',2,'Conference - self-registration','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (268,'',2,'Conference - conference data','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (269,'',2,'Conference - create a new discussion','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (270,'',2,'Conference - create a new contribution','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (271,'',2,'Add / edit user','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (272,'',2,'Add an image','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (273,'',2,'Add an image- Browse/Search&nbsp;','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (274,'',2,'Add a file upload - Page 2','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (275,'',2,'Create a static HTML page - Page 1','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (276,'',2,'Create a static HTML page - Page 2','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (277,'',2,'Create a text page - Page 1','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (278,'',2,'Create a text page - Page 2','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (279,'',2,'Add a link - function','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (280,'',2,'Add a file upload - Page 1','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (281,'',2,'Create an Internet link - Page 1','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (282,'',2,'Create an Internet link- Page 2','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (283,'',2,'Failed Log-in','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (284,'',2,'Authority','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (285,'',2,'Delete a page','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (286,'',2,'Remove a page - Warning','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (287,'',2,'Change page appearance','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (288,'',2,'Delete  a role - Warning','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (289,'',2,'Administrate roles - main page','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (290,'',2,'Add a link - Page 1','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (291,'',2,'Administrate sections','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (292,'',2,'Add site section','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (293,'',2,'Delete Site Section','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (294,'',2,'Warning - Delete Site Section','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (295,'',2,'Change name of site section','','',1,0,0,1,0,1,0,1,'se','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (296,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (297,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (298,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (299,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (300,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (301,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (302,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (303,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (304,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (305,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (306,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (307,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (308,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (309,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (310,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (311,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (312,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (313,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (314,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (315,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (316,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (317,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (318,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (319,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (320,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (321,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (322,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (323,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (324,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (325,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (326,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (327,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (328,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (329,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (330,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (331,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (332,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (333,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (334,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (335,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (336,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (337,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (338,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (339,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (340,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (341,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (342,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (343,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (344,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (345,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (346,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (347,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (348,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (349,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (350,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (351,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (352,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (353,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (354,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (355,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (356,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (357,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (358,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (359,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (360,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (361,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (362,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (363,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (364,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (365,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (366,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (367,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (368,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (369,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (370,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (371,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (372,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (373,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (374,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (375,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (376,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (377,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (378,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (379,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (380,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (381,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (382,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (383,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (384,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (385,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (386,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (387,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (388,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (389,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (390,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (391,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (392,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (393,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (394,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (395,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (396,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (397,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (398,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (399,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
  values (400,'',2,'','','',1,0,0,1,0,1,0,1,'en','','2002-03-07','2002-03-07',1,1,0,'_self','',1,'2002-03-07',NULL)
SET IDENTITY_INSERT meta OFF
 
-- now insert all images 
 
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (2,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-filadministration.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (3,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-roller-av-aktiv-anv.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (4,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-admin-anvoroller.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (5,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/AdminsidaBrowser2.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (6,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-forandrade-dok-bild1.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (6,0,0,0,0,0,'2','','_self','','top','','','se/helpimages/Admin-forandrade-dok-bild2.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (7,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-forandrade-dok-bild2.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (8,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-mallar.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (9,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-grupp-byt-namn.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (10,333,249,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-formatgrupp-tilldela-mall.gif','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (11,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-formatgrupp-skapa.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (12,331,85,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-formatgrupp-ta-bort.gif','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (13,303,137,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-formatgrupp-ta-bort-varning.gif','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (14,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-ip-access.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (14,0,0,0,0,0,'2','','_self','','top','','','se/helpimages/Admin-admin-ip-access.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (15,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-IP-access-ny.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (16,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-IP-access-varning.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (17,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-raknare.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (18,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-test-av-url.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (19,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-lank-bef-dok copy.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (19,0,0,0,0,0,'2','','_self','','top','','','','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (20,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-mallar-byt-namn-mall.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (21,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-mallar-hamta-ned.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (22,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-ladda-upp-ny-mall.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (23,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-mallar-ny-exempelmall.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (24,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-mallar-uppl-gickbra.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (25,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-ta-bort-mall.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (26,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-mallar-tabort-varning.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (27,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-mallar-visa-formatmallar.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (28,0,0,1,0,0,'1','','_self','','top','','','se/helpimages/Admin-meny.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (29,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-roller-adm-anvoroller.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (30,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-admin-roller.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (31,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-roller-bytnamn.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (32,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-roller-lagg-till-ny.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (33,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-roller-redigera.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (34,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-roller-ta-bort-varning.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (35,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-systeminfo.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (36,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Andra-text-html.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (36,0,0,0,0,0,'2','','_self','','top','','','se/helpimages/Admin_text_html.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (37,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-visa-alla-dok.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (37,0,0,0,0,0,'2','','_self','','top','','','se/helpimages/Admin-visa-dok2.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (38,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-andraanvegenskaper.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (39,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Dokumentinfo.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (40,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Rattigheter-behorighet1.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (41,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Rattigheter-behorighet1.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (42,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Behorighet2.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (43,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Behorighet2-nya-dok.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (44,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Bildarkiv.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (45,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Bild-finns-redan.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (46,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Valj-Browser.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (47,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Lagg-till-diagram.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (48,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Skapa-Nytt-Diagram.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (49,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Diagram-inmatningsformular.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (49,0,0,0,0,0,'2','','_self','','top','','','se/helpimages/Diagram-tabellinstallning.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (50,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Diagram-tillbaka-till-x.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (51,0,0,1,0,0,'1','','_self','','middle','','','se/helpimages/Login-ej-e-post.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (52,0,0,1,0,0,'1','','_self','','top','','','se/helpimages/Losen-via-e-post.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (53,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Include.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (54,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/login.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (55,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Knappar3.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (56,0,0,1,0,0,'1','','_self','','top','','','se/helpimages/Konf-admin-anvandare.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (57,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Konf-admin-anvandardata.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (58,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Konf-bytmallset-varning1.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (59,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Konf-admin-diskussion.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (60,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Konf-admin-forum.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (61,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Konf-admin-kommentar.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (62,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Konf-admin-mallset.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (63,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Konf-admin-sjalvreg.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (64,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Konf-ny-mallfil.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (65,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Konferens-login.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (66,940,452,0,0,0,'1','','_self','','top','','','se/helpimages/Konf-confViewer1.gif','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (67,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Konf-sjalvreg.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (68,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Konferens-data.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (69,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Konf-admin-ny-diskussion.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (70,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Konf-admin-ny-kommentar.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (71,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-lagg-till-anvandare.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (72,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Lagg-till-bild-m-bild.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (73,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Bild-Browse.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (74,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Valj-fil.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (74,0,0,0,0,0,'2','','_self','','top','','','se/helpimages/Valj-filtyp.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (75,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Lagg-till-dokument2.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (76,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/HTML-kod.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (77,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Lagg-till-dokument.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (78,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Andra-Text1.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (78,0,0,0,0,0,'2','','_self','','top','','','se/helpimages/Andra-text-html1.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (79,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Lank-valj.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (79,0,0,0,0,0,'2','','_self','','top','','','se/helpimages/Lank-Arkivera.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (80,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Lank-fil.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (81,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Lank-URL.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (82,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/NyURL.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (83,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Losen-felaktigt.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (84,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Rattigheter.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (85,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-ta-bort-dok.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (86,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-ta-bort-dok-varning.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (87,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/utseende3.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (88,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-ta-bort-roll-varning.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (89,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-roller-huvudsida.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (90,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Lagg-till-dokument.GIF','')
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
values (211,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-formatgrupp-skapa.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (212,331,85,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-formatgrupp-ta-bort.gif','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (213,303,137,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-formatgrupp-ta-bort-varning.gif','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (214,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Admin-ip-access.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (214,0,0,0,0,0,'2','','_self','','top','','','en/helpimages/Admin-admin-ip-access.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (215,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Admin-IP-access-ny.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (216,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-IP-access-varning.GIF','')
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
values (225,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-ta-bort-mall.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (226,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-mallar-tabort-varning.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (227,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-mallar-visa-formatmallar.GIF','')
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
values (234,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-roller-ta-bort-varning.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (235,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Admin-systeminfo.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (236,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Andra-text-html.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (236,0,0,0,0,0,'2','','_self','','top','','','en/helpimages/Admin_text_html.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (237,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-visa-alla-dok.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (237,0,0,0,0,0,'2','','_self','','top','','','se/helpimages/Admin-visa-dok2.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (238,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-andraanvegenskaper.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (239,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Dokumentinfo.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (240,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Rattigheter-behorighet1.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (241,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Rattigheter-behorighet1.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (242,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Behorighet2.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (243,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Behorighet2-nya-dok.GIF','')
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
values (284,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Rattigheter.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (285,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-ta-bort-dok.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (286,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-ta-bort-dok-varning.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (287,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/utseende3.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (288,0,0,0,0,0,'1','','_self','','top','','','se/helpimages/Admin-ta-bort-roll-varning.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (289,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Admin-roller-huvudsida.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (290,0,0,0,0,0,'1','','_self','','top','','','en/helpimages/Lagg-till-dokument.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (291,238,134,0,0,0,'1','','_self','','top','','','se/helpimages/Index-val.gif','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (292,375,0,0,0,0,'1','','_self','','top','','','se/helpimages/Index-ny.gif','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (293,236,140,0,0,0,'1','','_self','','top','','','se/helpimages/Index-tabort.gif','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (294,348,216,0,0,0,'1','','_self','','top','','','se/helpimages/Index-tabort_varning.gif','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (295,348,145,0,0,0,'1','','_self','','top','','','se/helpimages/Index-byt-namn.gif','')
 
-- now insert all data in texts 
INSERT INTO texts( meta_id, name, text, type )
       values(2,1,'Administrera filer
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(2,2,'Genom att markera en katalog och sedan klicka p&aring; &quot;Byt katalog&quot; visas inneh&aring;llet i den katalogen. Genom att markera ..\ och klicka p&aring; &quot;Byt katalog&quot; s&aring; tar man sig ett steg h&ouml;gre upp i hierarkin.
<BR>
<BR> F&ouml;r att ladda ner en fil till sin egen h&aring;rddisk/n&auml;tverk, leta fram filen och markera den, klicka sedan p&aring; &quot;Ladda ner&quot;. &quot;Ladda ner&quot; till v&auml;nster om filen finns i den v&auml;nstra rutan och &quot;Ladda ner&quot; till h&ouml;ger om filen finns i den h&ouml;gra rutan. Ett nytt f&ouml;nster &ouml;ppnas d&auml;r du f&aring;r v&auml;lja om du vill spara ned filen p&aring; din h&aring;rddisk/ditt n&auml;tverk eller om du vill &ouml;ppna filen. Spr&aring;ket i bilden &auml;r beroende p&aring; det spr&aring;k som din webbl&auml;sare har.
<BR>
<BR> F&ouml;r att ladda upp en fil fr&aring;n sin egen h&aring;rddisk/n&auml;tverk, klicka p&aring; &quot;Browse&quot; (mitt i bilden). Ett nytt f&ouml;nster
 &ouml;ppnas d&auml;r du f&aring;r leta reda p&aring; filen. Spr&aring;ket i bilden &auml;r beroende p&aring; det spr&aring;k som din webbl&auml;sare har. N&auml;r filen &auml;r framletad, skall den katalog d&auml;r filen skall l&auml;ggas in i markeras. Om katalogen &auml;r markerad i den v&auml;nstra rutan, klicka p&aring; &quot;Ladda upp&quot; till v&auml;nster. &Auml;r katalogen markerad i den h&ouml;gra rutan, klicka p&aring; &quot;Ladda upp&quot; till h&ouml;ger.
<BR>
<BR> F&ouml;r att kopiera en fil till en annan katalog,  leta fram filen i den v&auml;nstra rutan, markera filen och leta fram den katalog dit filen skall kopieras i den h&ouml;gra rutan och markera den. Klicka sedan p&aring; &quot;Kopiera -&gt;&quot;. Detta g&aring;r att g&ouml;ra tv&auml;rtom ocks&aring; - kopiera fil fr&aring;n den h&ouml;gra rutan till den v&auml;nstra. Klicka d&aring; p&aring; &quot;&lt;-Kopiera&quot; ist&auml;llet.
<BR>
<BR> F&ouml;r att flytta en fil fr&aring;n en katalog till en annan ka
talog,  leta fram filen i den v&auml;nstra rutan, markera filen och leta fram den katalog dit filen skall flyttas i den h&ouml;gra rutan och markera den. Klicka sedan p&aring; &quot;Flytta -&gt;&quot;. Detta g&aring;r att g&ouml;ra tv&auml;rtom ocks&aring; - flytta en fil fr&aring;n den h&ouml;gra rutan till den v&auml;nstra. Klicka d&aring; p&aring; &quot;&lt;-Flytta&quot; ist&auml;llet.
<BR>
<BR> F&ouml;r att byta namn p&aring; en katalog eller fil, markera den katalog/fil som skall bytas namn p&aring;. Skriv in det nya namnet i rutan under Nytt namn:. Klicka sedan p&aring; &quot;Byt namn&quot; till v&auml;nster eller h&ouml;ger beroende p&aring; om katalogen/filen finns i den v&auml;nstra eller h&ouml;gra rutan.
<BR>
<BR> F&ouml;r att skapa katalog, markera den katalog som den nya katalogen skall hamna under. Skriv in namnet p&aring; katalogen i rutan under Nytt namn:. Klicka sedan p&aring; &quot;Skapa katalog&quot; till v&auml;nster eller h&ouml;ger beroende p&aring; i vilken r
uta du har markerat katalogen.
<BR>
<BR> F&ouml;r att radera en katalog eller fil, markera katalogen/filen och klicka sedan p&aring; &quot;Radera&quot;. Knappen till v&auml;nster om katalogen/filen finns i v&auml;nstra rutan, knappen till h&ouml;ger om katalogen/filen finns i h&ouml;gra rutan. En varningsruta d&auml;r hela s&ouml;kv&auml;gen till katalogen/filen finns visas. Klicka &quot;Ja&quot; om du &auml;r s&auml;ker p&aring; att katalogen/filen skall tas bort, annars p&aring; &quot;Nej&quot;.
<BR>
<BR>&quot;Tillbaka&quot; leder till Administrat&ouml;rsmenyn.
<BR>

 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(3,1,'Aktivera/avaktivera anv&auml;ndare
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(3,2,'H&auml;r kan anv&auml;ndare aktiveras och avaktiveras. Avaktivering g&ouml;r att anv&auml;ndaren inte l&auml;ngre kan logga in i systemet. En anv&auml;ndare som &auml;r avaktiverad kan h&auml;r aktiveras igen.
<BR>
<BR>Markera den/de anv&auml;ndare som du vill aktivera och klicka sedan p&aring; &quot;Aktivera&quot;. Anv&auml;ndaren/anv&auml;ndarna &auml;r nu aktiverade och har tillg&aring;ng till systemet igen.
<BR>
<BR>Markera den/de anv&auml;ndare som du vill avaktivera och klicka sedan p&aring; &quot;Avktivera&quot;. Anv&auml;ndaren/anv&auml;ndarna &auml;r nu avaktiverade och har inte l&auml;ngre tillg&aring;ng till systemet.
<BR>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(4,1,'Administrera anv&auml;ndarroller
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(4,2,'H&auml;r kan anv&auml;ndare tilldelas en ny roll, tas bort fr&aring;n en roll och flyttas fr&aring;n en roll till en annan.
<BR>
<BR>F&ouml;r att tilldela anv&auml;ndaren en ny roll - klicka f&ouml;rst p&aring; anv&auml;ndaren och sedan p&aring; den roll som anv&auml;ndaren skall tilldelas och till sist klicka p&aring; &quot;Tilldela&quot;. Anv&auml;ndaren tillh&ouml;r nu b&aring;de den aktuella rollen och den nya rollen.
<BR>
<BR>F&ouml;r att ta bort en anv&auml;ndare fr&aring;n en roll - klicka f&ouml;rst p&aring; anv&auml;ndaren som skall tas bort och sedan p&aring; &quot;Ta bort&quot;. Anv&auml;ndaren tas bort fr&aring;n rollen.
<BR>
<BR>F&ouml;r att flytta en anv&auml;ndare till en annan roll - klicka f&ouml;rst p&aring; anv&auml;ndaren och sedan p&aring; den roll som anv&auml;ndaren skall flyttas till och till sist klicka p&aring; &quot;Flytta&quot;. Anv&auml;ndaren flyttas fr&aring;n aktuell roll till den nya rollen.
<BR>
<BR>&quot;Avbryt&quot; leder tillbaka till f&ouml
;reg&aring;ende sida.
<BR>
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(5,1,'Administrationssida f&ouml;r Browserkontroll
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(5,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="450">
    <TR>
    <TD>
      <UL>
	<LI><P><STRONG>Normal </STRONG> till&aring;ter Dig att g&aring; genom Browser-Control dokumentet till 
  den sidan Du definerade f&ouml;r Din webbl&auml;sare.</P>
        <LI>
        <P><STRONG>Redigera </STRONG>till&aring;ter Dig att redigera Browser-Control dokumentet</P>
        <LI>
        <P><STRONG>Tillbaka </STRONG>tar Dig tillbaka till senast bes&ouml;kt 
        sida.</P></LI></UL>
      <P>De andra knapparna fungerar som vanligt.</P>
      <P><STRONG>OBS! Om du &ouml;nskar &aring;terbes&ouml;ka denna administrationssida, klicka 
      p&aring; l&auml;nk-knappen och sedan p&aring; den r&ouml;da pilen bredvid den aktuella 
      l&auml;nken.</STRONG>
      <TABLE>
        </TABLE></P>
</TD></TR></TABLE></P>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(6,1,'F&ouml;r&auml;ndrade dokument - bild 1
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(6,2,'H&auml;r kan man s&ouml;ka fram alla dokument som lagts till i systemet under en viss tidsperiod.
<BR>
<BR>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(6,3,'Markera den dokumenttyp som du vill se p&aring; genom att klicka p&aring; den. Dokumenttypen Alla visar alla dokument oavsett typ som skapats under perioden.
<BR>
<BR>Ange fr&aring;n och med-datum och till och med-datum. Datum anges i formatet &Aring;&Aring;&Aring;&Aring;-MM-DD.
<BR>
<BR>Klicka sedan p&aring; &quot;Visa&quot;.
<BR>
<BR>I ett nytt f&ouml;nster visas resultatet av s&ouml;kningen.
<BR>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(6,4,'Bilden visar alla dokument som skapats under perioden. Det g&aring;r att klicka p&aring; antingen Meta id eller Rubrik f&ouml;r att se sidan (dokumentet).
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(7,1,'F&ouml;r&auml;ndrade dokument - bild 2
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(7,2,'Bilden visar alla dokument som skapats under perioden. Det g&aring;r att klicka p&aring; antingen Meta id eller Rubrik f&ouml;r att se sidan (dokumentet).
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(8,1,'Administrera formatmallar/formatgrupper
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(8,2,'<p align="left">Detta &auml;r startsidan f&ouml;r administrationen av
                formatmallar och formatgrupper. H&auml;r v&auml;ljs vad som skall
                g&ouml;ras. </p>
                <p align="left">Val som kan g&ouml;ras:</p>
                <ul>
                  <li>
                    <p align="left">l&auml;gga till en ny formatmall till systemet</li>
                  <li>
                    <p align="left">ta bort en formatmall fr&aring;n systemet</li>
                  <li>
                    <p align="left">byta namn p&aring; en befintlig formatmall i
                    systemet</li>
                  <li>
                    <p align="left">h&auml;mta en uppladdad formatmall fr&aring;n
                    systemet och kopiera den till sin egen h&aring;rddisk/eget
                    n&auml;tverk</li>
                  <li>
                    <p align="left">ladda upp exempelmallar till systemet</li>
                  <li>
             
       <p align="left">visa de formatmallar som finns i systemet
                    och se vilka dokument de anv&auml;nds i</li>
                  <li>
                    <p align="left">skapa en ny formatgrupp (som sedan
                    formatmallarna kan l&auml;ggas in i)</li>
                  <li>
                    <p align="left">ta bort en formatgrupp</li>
                  <li>
                    <p align="left">byta namn p&aring; en formatgrupp</li>
                  <li>
                    <p align="left">tilldela formatmallar till formatgrupper</li>
                </ul>
                <p align="left">&quot;Tillbaka&quot; leder till
                Administrat&ouml;rsmenyn.</p>
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(9,1,'Byta namn p&aring; formatgrupp
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(9,2,'V&auml;lj den befintliga formatgruppen genom att bl&auml;ddra fram den i rullgardinslistan. Skriv sedan in det nya namnet vid Nytt namn. Klicka p&aring; &quot;OK&quot;.
<BR>
<BR>&quot;Tillbaka&quot; leder till Administrera formatmallar/formatgrupper.
<BR>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(10,1,'L&auml;gga till/ta bort formatmallar
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(10,2,'H&auml;r kan formatmallar l&auml;ggas till en grupp eller tas bort fr&aring;n en grupp.
<BR>V&auml;lj den formatgrupp du vill arbeta med genom att bl&auml;ddra fram den i rullgardinslistan. Klicka sedan p&aring; &quot;Visa mallar&quot;. Mallarna i den valda formatgruppen visas nu i rutan till h&ouml;ger p&aring; bilden.
<BR>
<BR>I rutan till v&auml;nster visas vilka mallar som finns tillg&auml;ngliga.
<BR>L&auml;gga till formatmall till en grupp: Markera mallen i rutan till v&auml;nster, klicka sedan p&aring; &quot;L&auml;gg till&quot; och mallen l&auml;ggs till gruppen och visas i det h&ouml;gra f&ouml;nstret..
<BR>
<BR>Ta bort formatmall fr&aring;n en grupp: Markera mallen i rutan till h&ouml;ger, klicka sedan p&aring; &quot;Ta bort&quot; och mallen tas bort fr&aring;n gruppen och visas i det v&auml;nstra f&ouml;nstret..
<BR>
<BR>&quot;Tillbaka&quot; leder till Administrera formatmallar/formatgrupper.
<BR>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(11,1,'Skapa formatgrupp
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(11,2,'<center>Skriv in det namn som formatgruppen skall ha. Klicka sedan p&aring; "Skapa".</p>

"Tillbaka" leder till Administrera formatmallar/formatgrupper.
</center>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(12,1,'Ta bort formatgrupp
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(12,2,'V&auml;lj den formatgrupp du vill ta bort genom att bl&auml;ddra fram den i rullgardinslistan. Klicka sedan p&aring; &quot;Ta bort&quot;.
<BR>
<BR>&quot;Tillbaka&quot; leder till Administrera formatmallar/formatgrupper.
<BR>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(13,1,'Ta bort formatgrupp - varning
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(13,2,'Varning f&ouml;r att ta bort formatgrupp. Bilden visar vilka mallar som tillh&ouml;r den grupp som h&aring;ller p&aring; att tas bort. 
<BR>
<BR>Om du vill ta bort gruppen, flytta d&aring; f&ouml;rst &ouml;ver mallarna (om de inte redan tillh&ouml;r n&aring;gon annan grupp ocks&aring;) till n&aring;gon annan grupp. Detta g&ouml;rs via gr&auml;nssnittet f&ouml;r L&auml;gg till/Ta bort formatmallar. 
<BR>
<BR>Klicka sedan p&aring; &quot;OK&quot;.
<BR>
<BR>Om du inte vill ta bort gruppen klicka p&aring; &quot;Avbryt&quot;.
<BR>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(14,1,'Administrationssida f&ouml;r IP-accesser - bild 1
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(14,2,'Genom att koppla anv&auml;ndarnamnet till anv&auml;ndarens dators IP-nr kan anv&auml;ndaren f&aring; direkt tillg&aring;ng till systemet utan att beh&ouml;va logga in. De f&aring;r ett anv&auml;ndarnamn som &auml;r kopplat till datorns IP-nr. Man kan ge flera anv&auml;ndare inom ett visst intervall av IP-nr ett gemensamt anv&auml;ndarnamn.&nbsp;
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(14,3,'N&auml;r ingen IP-access &auml;r registrerad ser bilden ut som ovan. N&auml;r n&aring;gon IP-access har blivit registrerad, ser den ut som nedan.
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(14,4,'F&ouml;r att l&auml;gga till en ny IP-access - klicka p&aring; &quot;L&auml;gg till&quot;.&nbsp;
<BR>
<BR>F&ouml;r att &auml;ndra p&aring; en befintlig uppgift - s&auml;tt en bock i rutan framf&ouml;r anv&auml;ndaren, g&ouml;r &auml;ndringen och klicka sedan p&aring; &quot;Spara om&quot;.
<BR>
<BR>F&ouml;r att ta bort en anv&auml;ndare - s&auml;tt en bock i rutan framf&ouml;r anv&auml;ndaren, och klicka sedan p&aring; &quot;Ta bort&quot;.
<BR>
<BR>F&ouml;r att &aring;terg&aring; till f&ouml;reg&aring;ende bild - klicka p&aring; &quot;Tillbaka&quot;.
<BR>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(15,1,'L&auml;gga till ny IP-access
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(15,2,'V&auml;lja anv&auml;ndare genom att bl&auml;ddra i rullgardinslistan. Skriv in tillh&ouml;rande IP-nr eller intervall av IP-nr som g&auml;ller f&ouml;r anv&auml;ndaren. Klicka p&aring; &quot;Spara&quot;.
<BR>
<BR>F&ouml;r att &aring;terg&aring; till f&ouml;reg&aring;ende bild utan att l&auml;gga till ny IP-access - klicka p&aring; &quot;Avbryt&quot;.
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(16,1,'Ta bort IP-accesser - varning
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(16,2,'F&ouml;r att ta bort IP-accessen - klicka &quot;OK&quot;, f&ouml;r att &aring;terg&aring; till f&ouml;reg&aring;ende sida - klicka p&aring; &quot;Avbryt&quot;.
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(17,1,'Administrera r&auml;knare
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(17,2,'R&auml;kneverket &auml;ndras genom att ett nytt v&auml;rde skrivs in i den &ouml;versta vita rutan och sedan klicka p&aring; &quot;Uppdatera&quot;. Om r&auml;kneverket skall nollst&auml;llas skrivs en nolla in.
<BR>
<BR>F&ouml;r att &auml;ndra startdatum fyller man i ett nytt datum i den nedersta vita rutan och klickar sedan p&aring; &quot;Uppdatera&quot;. Det &auml;r det datum som sedan kommer att visas som sedan-datum.
<BR>
<BR>Ex Antal bes&ouml;kare &auml;r 6731 sedan 2000-01-01
<BR>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(18,1,'Kontrollera internet-l&auml;nkar
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(18,2,'H&auml;r kan man v&auml;lja att klicka p&aring; meta-id-l&auml;nkarna och kommer d&aring; till adminl&auml;get d&auml;r l&auml;nken &auml;r. Klickar man p&aring; URL:erna s&aring; kommer man till den sidan dit l&auml;nken leder. Rutorna under rubrikerna: &quot;Servern hittades&quot;, &quot;Servern gick att n&aring;&quot; och &quot;Dokumentet hittades&quot; f&auml;rgas gr&ouml;na om s&aring; &auml;r fallet annars f&auml;rgas de r&ouml;da. P&aring; s&aring; s&auml;tt kan man se var felet ligger om man ej n&aring;r de externa l&auml;nkarna i fr&aring;n systemet.
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(19,1,'L&auml;gga till l&auml;nk till ett befintligt dokument
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(19,2,'Det finns tv&aring; s&auml;tt att l&auml;gga till en l&auml;nk till ett befintlig sida.
        Kan man sidans MetaID skriver man in det direkt annars finns m&ouml;jlighet
        att s&ouml;ka fram sidan.
        
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(19,3,'<p>I &ouml;versta delen - "<b><i>V&auml;lj befintligt dokument</i></b>"
        skrivs sidans MetaId in och sen klickar man p&aring; "<b>L&auml;gg till</b>".</p>
        <p>I understa delen - "<b>S&ouml;k befintligt dokument</b>"
        skriver man in det/de s&ouml;kord som man vill anv&auml;nda sig av. Om fler
        s&ouml;kord skrivs in skall det vara mellanslag mellan dessa ord (OBS inget
        kommatecken). Genom att pricka f&ouml;r AND s&ouml;ker den endast fram sidor som
        inneh&aring;ller alla s&ouml;kord, OR s&ouml;ker fram sidor som inneh&aring;ller n&aring;got av
        s&ouml;korden.<o:p>
        </o:p>
        </p>
        <p>"<b><i>Inkludera dokument av typen</i></b>" - h&auml;r kan man
        begr&auml;nsa s&ouml;kningen genom att bara markera den/de typer av sidor som
        man vill s&ouml;ka efter.</p>
        <p>"<b><i>Inkludera dokument mellan dessa datum</i></b>" -
        h&auml;r kan man begr&auml;nsa s&ouml;kningen genom att skriva in 
start- och
        slutdatum.</p>
        <p>Genom att markera "<i><b>Skapat</b></i>" visas de sidor som
        &auml;r skapade under perioden.</p>
        <p>Markeras "<b><i>&Auml;ndrat</i></b>" visas de sidor som
        &auml;ndrats under perioden.</p>
        <p>Resultatet av s&ouml;kningen kan sorteras antingen efter Rubrik, MetaId,
        Dokumenttyp, &Auml;ndrat datum, Skapat datum, Arkiverat datum eller
        Aktiverat datum. Markera den sorterings- ordning du vill ha.</p>
        <p><span style="font-size:12.0pt;font-family:"Times New Roman";
mso-fareast-font-family:"Times New Roman";mso-ansi-language:SV;mso-fareast-language:
SV;mso-bidi-language:AR-SA">Klicka sedan p&aring; "<b style="mso-bidi-font-weight:
normal"><i style="mso-bidi-font-style:normal">S&ouml;k</i></b>". Resultatet av
        s&ouml;kningen visas underst p&aring; sidan.</span>
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(20,1,'Byt namn p&aring; formatmall
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(20,2,'V&auml;lj vilken mall som skall bytas namn p&aring; genom att bl&auml;ddra fram namnet p&aring; mallen i rullgardinslistan. Skriv in det nya namnet. Klicka sedan p&aring; &quot;Byt namn&quot;.
<BR>
<BR>&quot;Tillbaka&quot; leder tillbaka till f&ouml;reg&aring;ende sida.
<BR>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(21,1,'H&auml;mta uppladdad formatmall
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(21,3,'V&auml;lj vilken mall som skall h&auml;mtas genom bl&auml;ddra fram den i rullgardinslistan. Klicka sedan p&aring; &quot;H&auml;mta&quot;.
<BR>
<BR>En ny sida d&auml;r du f&aring;r v&auml;lja om du vill se p&aring; mallen eller om du vill spara den p&aring; disk visas. Bilden visas p&aring; det spr&aring;k som din webbl&auml;sare anv&auml;nder. Klicka p&aring; &quot;OK&quot; och v&auml;lj sedan var p&aring; din h&aring;rddisk/n&auml;tverk du vill spara mallen.
<BR>
<BR>&quot;Tillbaka&quot; (delvis dold under rullgardinslistan p&aring; bilden) leder till Administrera formatmallar/formatgrupper.
<BR>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(22,1,'Ladda upp ny formatmall
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(22,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
                <blockquote>
                  <p align="left"> Leta reda p&aring; den formatmall som du vill ladda
                  upp genom att anv&auml;nda "Browse/S&ouml;k" (namnet beror
                  p&aring; vilket spr&aring;k din webbl&auml;sare anv&auml;nder).</p>
                  <p align="left"> Skriv in det namn du vill att formatmallen
                  skall ha. Observera att mallnamnet inte får innehålla å, ä eller ö. En <img border="0" src="/images/se/helpimages/Admin-4.GIF" width="13" height="14">
                  vid <i>"Skriv &ouml;ver existerande" </i> g&ouml;r att om en
                  formatmall med det namnet redan finns, skrivs den &ouml;ver. S&auml;tt
                  inte <img border="0" src="/images/se/helpimages/Admin-4.GIF" width="13" height="14">
                  om du inte &auml;r riktigt s&auml;ker p&aring; att du 
verkligen skall
                  skriva &ouml;ver den befintliga mallen.</p>
                  <p align="left"> Markera i rutan till h&ouml;ger den/de
                  formatgrupper som skall ha tillg&aring;ng till mallen. Detta
                  beh&ouml;ver inte g&ouml;ras nu utan kan g&ouml;ras senare, men mallen kan
                  inte anv&auml;ndas om den inte tillh&ouml;r n&aring;gon grupp.</p>
                  <p align="left"> Genom att klicka p&aring; "Ladda upp"
                  l&auml;ggs formatmallen till.</p>
                  <p align="left">&nbsp;"Tillbaka" leder till
                  Administrera formatmallar/formatgrupper.</p>
                </blockquote>
      </td>
    </tr>
  </table>
  </center>
</div>
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(23,1,'Ladda upp ny exempelmall
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(23,2,'<p align="left">F&ouml;r att ladda upp en ny exempelmall:</p>
                  <ul>
                    <li>
                      <p align="left"><i>Ange exempelmall: </i>Leta reda p&aring; den formatmall som du vill ladda
                  upp genom att anv&auml;nda "Browse/S&ouml;k" (namnet beror
                  p&aring; vilket spr&aring;k din webbl&auml;sare anv&auml;nder).</li>
                    <li>
                      <p align="left"><i>V&auml;lj mall: </i>V&auml;lj den mall som
                      exempelmallen skall h&ouml;ra ihop med genom att bl&auml;ddra i rullgardinslistan.
                      Mallar m&auml;rkta med en * har redan en exempelmall knuten
                      till sig.</li>
                    <li>
                      <p align="left">Klicka p&aring; "Ladda upp".</li>
                  </ul>
                  <p align="left">F&ouml;r att visa exempelmall:</p>
                  <ul>
                    <li>
                      <
p align="left"><i>V&auml;lj mall:</i> V&auml;lj den mall (med *)
                      som du vill se p&aring; genom att bl&auml;ddra fram den i rullgardinslistan.
                      Klicka sedan p&aring; "Visa exempelmall". Mallen
                      visas p&aring; en ny sida. </li>
                  </ul>
                  <p align="left">F&ouml;r att ta bort exempelmall:</p>
                  <ul>
                    <li>
                      <p align="left"><i>V&auml;lj mall:</i> V&auml;lj den mall (med *)
                      vars exempelmall som du vill ta bort genom att bl&auml;ddra i rullgardinslistan.
                      Klicka sedan p&aring; "Ta bort exempelmall".
                      Exempelmallen tas bort (* vid mallen tas ocks&aring; bort). </li>
                  </ul>
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(24,1,'Ladda upp ny formatmall - klart!
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(24,2,'<center>Bilden visar att formatmallen lagts till i systemet. </p>&nbsp;

"Tillbaka" leder till Administrera formatmallar/formatgrupper.
</center>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(25,1,'Ta bort formatmall
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(25,2,'V&auml;lj den formatmall du vill ta bort genom att bl&auml;ddra fram den i rullgardinslistan. Inom [ ] st&aring;r det hur m&aring;nga sidor som anv&auml;nder mallen.
<BR>
<BR>Genom att klicka p&aring; &quot;Ta bort&quot; tas mallen bort. Innan mallen tas bort visas en sida d&auml;r det g&aring;r att tilldela de dokument, som har den mallen som skall tas bort, n&aring;gon annan formatmall.
<BR>
<BR>&quot;Tillbaka&quot; leder till f&ouml;reg&aring;ende sida.
<BR>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(26,1,'Ta bort formatmall - varning!
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(26,2,'V&auml;lj vilken ny mall som de dokument som anv&auml;nder mallen du t&auml;nker ta bort skall anv&auml;nda i forts&auml;ttningen. V&auml;lj genom att bl&auml;ddra fram mallen i rullgardinslistan. OBS att alla dokument kommer att tilldelas samma mall.
<BR>
<BR>Klicka sedan p&aring; &quot;OK&quot;.
<BR>
<BR>&quot;Avbryt&quot; leder tillbaka till f&ouml;reg&aring;ende sida.
<BR>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(27,1,'Visa formatmallar
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(27,2,'Till v&auml;nster p&aring; bilden visas alla formatmallar. Inom [ ] st&aring;r det hur m&aring;nga dokument som anv&auml;nder mallen. Markera mallen och klicka p&aring; &quot;Lista dokument&quot;. Dokumenten visas nu till h&ouml;ger p&aring; bilden. Genom att markera ett dokument och sedan klicka p&aring; &quot;Visa dokument&quot; f&aring;r du se dokumentet.&nbsp;
<BR>&nbsp;
<BR>&quot;Tillbaka&quot; leder till Administrera formatmallar/formatgrupper.
<BR>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(28,1,'Administrat&ouml;rsmenyn
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(28,2,'H&auml;r kan man:
                <ul>
                  <li>
                    <p>Administrera anv&auml;ndare</li>
                  <li>
                    <p>Administrera roller</li>
                  <li>
                    <p>Administrera IP-accesser</li>
                  <li>
                    <p>Administrera mallar</li>
                  <li>
                    <p>Visa alla dokument</li>
                  <li>
                    <p>Ta bort ett dokument</li>
                  <li>
                    <p>Kontrollera Internetl&auml;nkar</li>
                  <li>
                    <p>Administrera r&auml;knare</li>
                  <li>
                    <p>Administrera systeminformation</li>
                  <li>
                    <p>Administrera filer</li>
                </ul>
                <p>Valet g&ouml;rs genom att bl&auml;ddra fram det man vill g&ouml;ra i
                rullgardinslistan och sedan klicka p&aring; knappen "G&aring
; till
                adminsida" (dold under rullgardinslistan p&aring; bilden).</p>
                <p>L&auml;nken "Tillbaka till startsidan" leder tillbaka
                till systemets f&ouml;rsta sida.
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(29,1,'Administrera anv&auml;ndare och roller
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(29,2,'&quot;Administrera&quot; leder till den sida d&auml;r anv&auml;ndare kan tilldelas en ny roll, tas bort fr&aring;n en roll och flyttas fr&aring;n en roll till en annan. Markera den roll du vill arbeta med.
<BR>
<BR>&quot;av/Aktivera&quot; leder till en sida d&auml;r anv&auml;ndare kan aktiveras eller avaktiveras. Avaktivering g&ouml;r att anv&auml;ndaren inte l&auml;ngre kan logga in i systemet. En anv&auml;ndare som &auml;r avaktiverad kan aktiveras igen. OBS att endast de anv&auml;ndare som tillh&ouml;r den roll som &auml;r vald visas. Om Alla markeras visas alla anv&auml;ndare.
<BR>
<BR>&quot;Tillbaka&quot; leder tillbaka till f&ouml;reg&aring;ende sida.
<BR>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(30,1,'Administrera roller 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(30,2,'Knappen &quot;Administrera roller&quot; leder till den sida d&auml;r nya roller kan l&auml;ggas till, namnet bytas p&aring; en roll, r&auml;ttigheterna f&ouml;r rollen kan redigeras eller rollen kan tas bort.
<BR>
<BR>Knappen &quot;Administrera anv&auml;ndar-roller&quot; leder till den sida d&auml;r administration av rollen sker. D&auml;r kan anv&auml;ndare l&auml;ggas till, tas bort och flyttas fr&aring;n en roll till en annan.
<BR>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(31,1,'Byt namn p&aring; roll
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(31,2,'<center>Skriv in det nya rollnamnet och klicka p&aring; "Spara".</p>
"Avbryt" leder tillbaka till f&ouml;reg&aring;ende sida.</p></center>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(32,1,'L&auml;gg till ny roll
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(32,2,'<center>
  <table border="0" cellpadding="0" cellspacing="0" width="400">
    <tr>
      <td>
                <p align="left">Skriv in namnet p&aring; den roll som skall l&auml;ggas
                till. </p>
                <p align="left">Genom att s&auml;tta en <img border="0" src="/images/se/helpimages/Admin-4.GIF" width="13" height="14">
                vid <i>"R&auml;tt att f&aring; l&ouml;senord per mail"</i> f&aring;r en
                anv&auml;ndare som tillh&ouml;r rollen m&ouml;jlighet att f&aring; sitt l&ouml;senord
                s&auml;nt till sig per mail om han/hon gl&ouml;mt bort det.
                (Best&auml;llningen av l&ouml;senordet g&ouml;rs p&aring; inloggningssidan).</p>
                <p align="left">Genom att s&auml;tta en <img border="0" src="/images/se/helpimages/Admin-4.GIF" width="13" height="14">
                vid <i>"Sj&auml;lvregistreringsr&auml;tt i konferens"</i> f&aring;r
                en anv&auml;ndare som tillh&ouml;r rollen r
&auml;tt att registrera sig
                sj&auml;lv f&ouml;r att kunna delta i en konferens. OBS att Konferens
                &auml;r en till&auml;ggsmodul till imCMS.</p>
                <p align="left">N&auml;r valen &auml;r gjorda - klicka p&aring;
                "Spara".</p>
                <p align="left">"Avbryt" leder tillbaka till
                f&ouml;reg&aring;ende sida.</p>
      </td>
    </tr>
  </table>
  </center>
</div>
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(33,1,'Redigera r&auml;ttigheter f&ouml;r roll
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(33,2,'<p align="left">Genom att s&auml;tta en bock vid <i>"R&auml;tt att f&aring; l&ouml;senord per mail"</i> f&aring;r en
              anv&auml;ndare som tillh&ouml;r rollen m&ouml;jlighet att f&aring; sitt l&ouml;senord s&auml;nt
              till sig per mail om han/hon gl&ouml;mt bort det. (Best&auml;llningen av l&ouml;senordet
              g&ouml;rs p&aring; inloggningssidan).</p>
              <p align="left">Genom att s&auml;tta en bock vid <i>"Sj&auml;lvregistreringsr&auml;tt i konferens"</i> f&aring;r
              en anv&auml;ndare som tillh&ouml;r rollen r&auml;tt att registrera sig sj&auml;lv
              f&ouml;r att kunna delta i en konferens. OBS att Konferens &auml;r en till&auml;ggsmodul
              till imCMS.</p>
              <p align="left">N&auml;r valen &auml;r gjorda - klicka p&aring;
              "Spara".</p>
              <p align="left">"Avbryt" leder tillbaka till f&ouml;reg&aring;ende
              sida utan att n&aring;gon f&ouml;r&auml;ndring 
i rollens r&auml;ttigheter gjorts.</p>
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(34,1,'Ta bort roll - varning!
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(34,2,'H&auml;r visas vilka anv&auml;ndare (de 50 f&ouml;rsta) som &auml;r medlemmar i den roll som du t&auml;nker ta bort (om inga anv&auml;ndare visas har rollen inga anv&auml;ndare tilldelade till sig).&nbsp;
<BR>
<BR>Dessutom ser du vilka dokument som rollen har r&auml;ttigheter till. Att ta bort rollen g&ouml;r att anv&auml;ndarna inte kan se dessa dokument l&auml;ngre (om de inte ocks&aring; tillh&ouml;r n&aring;gon annan roll som har r&auml;ttigheter till dessa dokument).
<BR>
<BR>F&ouml;r att ta bort rollen, klicka p&aring; &quot;OK&quot;. Om du inte vill ta bort rollen, klicka p&aring; &quot;Avbryt&quot;.
<BR>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(35,1,'Administrera systeminformation
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(35,2,'H&auml;r kan man skriva in systemmeddelanden, ange vem som &auml;r servermaster och webbmaster. OBS att f&ouml;r att dessa uppgifter skall visas p&aring; en sida m&aring;ste de &quot;imCMS-taggar&quot; som styr detta vara inlagda i den mall som styr utseendet p&aring; sidan (dokumentet).
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(35,3,'<h3><b>Ange systemmeddelande </b></h3>
          <p><i><b>Aktuellt systemmeddelande: </b></i>Visar det systemmeddelande
          som &auml;r aktuellt f&ouml;r n&auml;rvarande.<p><b><i>&Auml;ndra systemmeddelande:</i></b>
          Skriv in det nya meddelande som skall visas. Det g&aring;r bra att anv&auml;nda
          sig av HTML-formatering.<h3>Ange servermaster</h3>
          <p><b><i>Aktuell servermaster:</i></b> Visar namnet p&aring; den person som
          &auml;r registrerad som servermaster f&ouml;r n&auml;rvarande.<b><i> </i></b><p><b><i>&Auml;ndra
          servermaster:</i></b> H&auml;r anges namnet p&aring; den person som skall vara
          ny servermaster.<i><b> </b></i><p><i><b>Aktuell servermaster email:</b></i>
          Visar e-postadressen till den person som &auml;r registrerad som
          servermaster. <i><b> </b></i> <p><b><i>&Auml;ndra servermaster email:</i></b>
          H&auml;r anges e-postadressen till den person som &auml;r/skall vara ny
  
        servermaster.<h3><b>Ange webmaster</b></h3>
          <p><b><i>Aktuell webmaster: </i></b>Visar namnet p&aring; den person som
          &auml;r registrerad som webbmaster f&ouml;r n&auml;rvarande.<p><b><i>&Auml;ndra
          webmaster: </i></b>H&auml;r anges namnet p&aring; den person som skall vara ny
          webbmaster.<p><b><i>Aktuell webmaster email: </i></b>Visar
          e-postadressen till den person som &auml;r registrerad som webbmaster.<p><i><b>&Auml;ndra
          webmaster email: </b></i>H&auml;r anges e-postadressen till den person som
          &auml;r/skall vara ny webbmaster.
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(36,1,'L&auml;gga till/&auml;ndra text
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(36,2,'&Auml;ndra text Txt 10 visar vilket textf&auml;lt p&aring; sidan som l&auml;ggs
              till/&auml;ndras. MetaId visar vilken specifik sida som
              till&auml;gget/&auml;ndringen g&ouml;rs p&aring;.</p>
              <p>Den ursprungliga texten visas i textrutan.&nbsp; I textrutan
              kan redigering g&ouml;ras, text tas bort eller l&auml;ggas till. &Auml;r det
              vanlig text som skrivs i rutan skall <b>Format</b>: <i>Vanlig text</i>
              markeras.
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(36,3,'Det g&aring;r &auml;ven att skriva text med HTML-formateringar, d&aring;
              skall <b>Format</b>: <i>HTML</i> markeras.
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(37,1,'Visa alla dokument
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(37,2,'
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(37,3,'H&auml;r v&auml;ljer man f&ouml;rst dokumentnummer p&aring; start, vilket blir det f&ouml;rsta dokumentet som listas. N&auml;r man kommer in p&aring; sidan ligger det f&ouml;rsta  tillg&auml;ngliga dokumentet automatiskt p&aring; start. P&aring; intervall v&auml;ljer man sen inom vilket intervall dokumenten man vill se ska ligga. Slutligen trycker man p&aring; knappen &quot;Lista&quot; och f&aring;r d&aring; upp en lista av l&auml;nkar till alla de dokument som har dokumentnummer inom det intervall man valt.
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(37,4,'H&auml;r visas alla sidor (dokument) i intervallet, med sina undersidor (dokument). Om man klickar p&aring; en sida som har en &#9679; framf&ouml;r visas sidan (dokumentet). Klickar man p&aring; en sida (dokument) med &deg; framf&ouml;r visas listan med det MetaId:et &ouml;verst. D&aring; g&aring;r det att se om undersidan i sin tur har undersidor. Undersidan har nu f&aring;tt en &#9679; framf&ouml;r sig och det g&aring;r att klicka p&aring; den f&ouml;r att f&aring; se sidan.
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(38,1,'&Auml;ndra anv&auml;ndaregenskaper
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(38,2,'Anv&auml;ndarkategori - v&auml;lj kategori och klicka p&aring; "Visa anv&auml;ndare" f&ouml;r att visa denna kategoris anv&auml;ndare i nedre delen av bilden.<br><br>

<li>Anonyma anv&auml;ndare: Anv&auml;ndare som inte loggar in sig i systemet.</li>
<li>Autentiserade anv&auml;ndare: Anv&auml;ndare som loggar in i systemet med hj&auml;lp av sitt anv&auml;ndarnamn och l&ouml;senord.</li>
<li>Konferensanv&auml;ndare: Anv&auml;ndare som l&auml;gger in sig sj&auml;lva i systemet.</li>

<br><br>F&ouml;r att l&auml;gga till en ny anv&auml;ndare - klicka p&aring; "L&auml;gg till".

<br><br>F&ouml;r att redigera en befintlig anv&auml;ndare - klicka p&aring; anv&auml;ndaren f&ouml;r att markera den och sedan p&aring; "Redigera".

F&ouml;r att &aring;terg&aring; till f&ouml;reg&aring;ende sida klicka p&aring; "Tillbaka".
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(39,1,'&Auml;ndra dokumentinfo
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(39,2,'<h3 align="center">H&auml;r &auml;ndras dokumentets grundinformation&nbsp;</h3>
<div align="center">
  <table border="1" width="100%">
    <tr>
      <td width="100%"><b>Rubrik:</b>  Den text som kommer att vara sj&auml;lva l&auml;nktexten.</td>
    </tr>
    <tr>
      <td width="100%"><b>Text:</b>  F&ouml;rklarande text som visas vid l&auml;nken (OBS
        visas endast om utseendemallen till&aring;ter detta).</td>
    </tr>
    <tr>
      <td width="100%"><b>Bild:</b>  H&auml;r anges s&ouml;kv&auml;gen till den bild som
        skall visas som en ikon under l&auml;nken och den f&ouml;rklarande texten (OBS
        visas endast om utseendemallen till&aring;ter detta). Om ingen bild skall
        visas l&auml;mnas f&auml;ltet tomt.</td>
    </tr>
  </table>
  <h3 align="center">Avancerat</h3>
  <table border="1" width="100%">
    <tr>
      <td width="100%"><b>S&ouml;kord: </b>H&auml;r anges de s&ouml;kord som skall ge tr&auml;ff
        vid s&ouml;kning. En <img 
border="0" src="/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
        i rutan vid <i>Blockera s&ouml;kning</i> g&ouml;r att sidan inte kommer att ge n&aring;gon
        tr&auml;ff vid s&ouml;kningar.</td>
    </tr>
    <tr>
      <td width="100%"><b>Dela ut: </b><i>Visa dokumentet &auml;ven f&ouml;r obeh&ouml;riga
        anv&auml;ndare</i> betyder att l&auml;nken till sidan visas men de kan inte
        komma in och se sj&auml;lva sidan. <i>Dela ut dokument f&ouml;r andra
        administrat&ouml;rer</i> g&ouml;r att administrat&ouml;rerna kan l&auml;nka till denna
        sida.</td>
    </tr>
    <tr>
      <td width="100%"><b>Publicera: </b>H&auml;r anges datum och klockslag om sidan
        inte skall visas omedelbart utan vid ett senare tillf&auml;lle.</td>
    </tr>
    <tr>
      <td width="100%"><b>Arkivera: </b>H&auml;r anges det datum d&aring; sidan skall
        arkiveras. L&auml;nken kommer att tas bort vid det datum och klockslag som
        
anges. En <img border="0" src="/images/se/helpimages/Lagg-t4.GIF" width="13" height="14"> i
        rutan vid <i>Arkivera nu</i> g&ouml;r att sidan arkiveras direkt.</td>
    </tr>
    <tr>
      <td width="100%"><b>Visa: </b>H&auml;r anges var sidan skall visas.&nbsp;
        <ul>
          <li><i>Samma frame</i> betyder att sidan &ouml;ppnas i den frame d&auml;r l&auml;nken
            finns.&nbsp;</li>
          <li><i>Nytt f&ouml;nster</i> anger att sidan &ouml;ppnas i ett nytt webbl&auml;sarf&ouml;nster.&nbsp;</li>
          <li><i>Ers&auml;tt allt</i> betyder att alla frames p&aring; sidan kommer att
            ers&auml;ttas med en ny frame d&auml;r den nya sidan visas.</li>
          <li><i>Annan frame</i> - h&auml;r anges namnet p&aring; den frame d&auml;r sidan
            skall visas.
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(40,1,'R&auml;ttigheter f&ouml;r begr&auml;nsad beh&ouml;righet 1
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(40,2,'<center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
                <p align="left">Genom att markera respektive r&auml;ttighet och sedan klicka p&aring; "OK" f&aring;r
                anv&auml;ndare som tillh&ouml;r roller med <i>Begr&auml;nsad beh&ouml;righet 1 </i>dessa
                r&auml;ttigheter.</p>
                <p align="left">&nbsp;</p>
      </td>
    </tr>
  </table>
  </center>
</div>
<div align="CENTER">
                <table border="1" width="75%">
                  <tr>
                    <td width="100%"><b>R&auml;tt att &auml;ndra dokumentinformation: </b>Inneb&auml;r
                      att man f&aring;r lov att &auml;ndra p&aring; Rubrik (texten p&aring;
                      l&auml;nken), den f&ouml;rklarande texten till l&auml;nken och
                      ikon-bilden vid l&auml;nken.</td>
                  </tr>
                  <tr>
                    <td width="100%"><b>R&auml;tt att &
auml;ndra avancerad
                      dokumentinformation: </b>Inneb&auml;r att man f&aring;r lov att
                      &auml;ndra p&aring; Rubrik (texten p&aring; l&auml;nken), den f&ouml;rklarande
                      texten till l&auml;nken, ikon-bilden vid l&auml;nken, s&ouml;kord,
                      publicera, arkivera, dela ut sidan och best&auml;mma om andra
                      administrat&ouml;rer skall f&aring; l&auml;nka til sidan.</td>
                  </tr>
                  <tr>
                    <td width="100%"><b>R&auml;tt att &auml;ndra r&auml;ttigheter f&ouml;r
                      roller: </b>Inneb&auml;r att man kan g&aring; in och &auml;ndra, l&auml;gga
                      till och ta bort roller och &auml;ndra beh&ouml;righeter f&ouml;r
                      dessa roller p&aring; sidan. </td>
                  </tr>
                  <tr>
                    <td width="100%"><b>R&auml;tt att &auml;ndra texter: </b>Inneb&auml;r
  
                    att man f&aring;r lov att &auml;ndra textinneh&aring;llet p&aring; sidan.</td>
                  </tr>
                  <tr>
                    <td width="100%"><b>R&auml;tt att &auml;ndra bilder: </b>Inneb&auml;r
                      att man f&aring;r lov att &auml;ndra p&aring; bilderna p&aring; sidan.</td>
                  </tr>
                  <tr>
                    <td width="100%"><b>R&auml;tt att &auml;ndra menyer: </b>Inneb&auml;r
                      att man f&aring;r lov att skapa undersidor till sidan. Vilka
                      typer av sidor best&auml;ms av vilka som markeras i listan
                      (klicka p&aring; namnet s&aring; blir den bl&aring;markerad, f&ouml;r att
                      markera flera, h&aring;ll ned "Ctrl" och klicka p&aring;
                      respektive typ).</td>
                  </tr>
                  <tr>
                    <td width="100%"><b>R&auml;tt att &auml;ndra mallar: </b>Inneb&a
uml;r
                      att man f&aring;r lov att byta utseende p&aring; sidan. Vilken/vilka
                      mallgrupper best&auml;ms av vilka som markeras i listan
                      (klicka p&aring; namnet s&aring; blir den bl&aring;markerad, f&ouml;r att
                      markera flera, h&aring;ll ned "Ctrl" och klicka p&aring;
                      respektive typ).</td>
                  </tr>
                </table>
</div align="CENTER">
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(41,1,'R&auml;ttigheter f&ouml;r begr&auml;nsad beh&ouml;righet 1, f&ouml;r nya dokument
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(41,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
<p align="center">
      "Definiera f&ouml;r nya dokument" betyder att r&auml;ttigheterna st&auml;lls
      in f&ouml;r sidor som skapas fr&aring;n aktuell sida.</p>
                <p align="left">Genom att markera respektive r&auml;ttighet och sedan klicka p&aring; "OK" f&aring;r
                anv&auml;ndare som tillh&ouml;r roller med <i>Begr&auml;nsad beh&ouml;righet 1 </i>dessa
                r&auml;ttigheter.</p>
        <p>&nbsp;</td>
    </tr>
  </table>
  </center>
</div>
<div align="CENTER">
                <table border="1" width="75%">
                  <tr>
                    <td width="100%"><b>R&auml;tt att &auml;ndra dokumentinformation: </b>Inneb&auml;r
                      att man f&aring;r lov att &auml;ndra p&aring; Rubrik (texten p&aring;
                      l&auml;nken), den f&ouml;rklarande texten till l&auml;nken och
 
                     ikon-bilden vid l&auml;nken.</td>
                  </tr>
                  <tr>
                    <td width="100%"><b>R&auml;tt att &auml;ndra avancerad
                      dokumentinformation: </b>Inneb&auml;r att man f&aring;r lov att
                      &auml;ndra p&aring; Rubrik (texten p&aring; l&auml;nken), den f&ouml;rklarande
                      texten till l&auml;nken, ikon-bilden vid l&auml;nken, s&ouml;kord,
                      publicera, arkivera, dela ut sidan och best&auml;mma om andra
                      administrat&ouml;rer skall f&aring; l&auml;nka till sidan.</td>
                  </tr>
                  <tr>
                    <td width="100%"><b>R&auml;tt att &auml;ndra r&auml;ttigheter f&ouml;r
                      roller: </b>Inneb&auml;r att man kan g&aring; in och &auml;ndra, l&auml;gga
                      till och ta bort roller och &auml;ndra beh&ouml;righeter f&ouml;r
                      dessa roller p&ar
ing; sidan. </td>
                  </tr>
                  <tr>
                    <td width="100%"><b>R&auml;tt att &auml;ndra texter: </b>Inneb&auml;r
                      att man f&aring;r lov att &auml;ndra textinneh&aring;llet p&aring; sidan.</td>
                  </tr>
                  <tr>
                    <td width="100%"><b>R&auml;tt att &auml;ndra bilder: </b>Inneb&auml;r
                      att man f&aring;r lov att &auml;ndra p&aring; bilderna p&aring; sidan.</td>
                  </tr>
                  <tr>
                    <td width="100%"><b>R&auml;tt att &auml;ndra menyer: </b>Inneb&auml;r
                      att man f&aring;r lov att skapa undersidor till sidan. Vilka
                      typer av sidor best&auml;ms av vilka som markeras i listan
                      (klicka p&aring; namnet s&aring; blir den bl&aring;markerad, f&ouml;r att
                      markera flera, h&aring;ll ned "Ctrl" och klicka p&aring;
                 
     respektive typ).</td>
                  </tr>
                  <tr>
                    <td width="100%"><b>R&auml;tt att &auml;ndra mallar: </b>Inneb&auml;r
                      att man f&aring;r lov att byta utseende p&aring; sidan. Vilken/vilka
                      mallgrupper best&auml;ms av vilka som markeras i listan
                      (klicka p&aring; namnet s&aring; blir den bl&aring;markerad, f&ouml;r att
                      markera flera, h&aring;ll ned "Ctrl" och klicka p&aring;
                      respektive typ).</td>
                  </tr>
                </table>
</div align="CENTER">
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(42,1,'R&auml;ttigheter f&ouml;r begr&auml;nsad beh&ouml;righet 2
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(42,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
                <p align="left">Genom att markera respektive r&auml;ttighet och sedan klicka p&aring; "OK" f&aring;r
                anv&auml;ndare som tillh&ouml;r roller med <i>Begr&auml;nsad beh&ouml;righet 2 </i>dessa
                r&auml;ttigheter.</p>
        <p>&nbsp;</td>
    </tr>
  </table>
  </center>
</div>
<div align="CENTER">
                <table border="1" width="75%">
                  <tr>
                    <td width="100%"><b>R&auml;tt att &auml;ndra dokumentinformation: </b>Inneb&auml;r
                      att man f&aring;r lov att &auml;ndra p&aring; Rubrik (texten p&aring;
                      l&auml;nken), den f&ouml;rklarande texten till l&auml;nken och
                      ikon-bilden vid l&auml;nken.</td>
                  </tr>
                  <tr>
                    <td width="100%"><b>R&auml;tt att &auml;ndra
 avancerad
                      dokumentinformation: </b>Inneb&auml;r att man f&aring;r lov att
                      &auml;ndra p&aring; Rubrik (texten p&aring; l&auml;nken), den f&ouml;rklarande
                      texten till l&auml;nken, ikon-bilden vid l&auml;nken, s&ouml;kord,
                      publicera, arkivera, dela ut sidan och best&auml;mma om andra
                      administrat&ouml;rer skall f&aring; l&auml;nka til sidan.</td>
                  </tr>
                  <tr>
                    <td width="100%"><b>R&auml;tt att &auml;ndra r&auml;ttigheter f&ouml;r
                      roller: </b>Inneb&auml;r att man kan g&aring; in och &auml;ndra, l&auml;gga
                      till och ta bort roller och &auml;ndra beh&ouml;righeter f&ouml;r
                      dessa roller p&aring; sidan. </td>
                  </tr>
                  <tr>
                    <td width="100%"><b>R&auml;tt att &auml;ndra texter: </b>Inneb&auml;r
           
           att man f&aring;r lov att &auml;ndra textinneh&aring;llet p&aring; sidan.</td>
                  </tr>
                  <tr>
                    <td width="100%"><b>R&auml;tt att &auml;ndra bilder: </b>Inneb&auml;r
                      att man f&aring;r lov att &auml;ndra p&aring; bilderna p&aring; sidan.</td>
                  </tr>
                  <tr>
                    <td width="100%"><b>R&auml;tt att &auml;ndra menyer: </b>Inneb&auml;r
                      att man f&aring;r lov att skapa undersidor till sidan. Vilka
                      typer av sidor best&auml;ms av vilka som markeras i listan
                      (klicka p&aring; namnet s&aring; blir den bl&aring;markerad, f&ouml;r att
                      markera flera, h&aring;ll ned "Ctrl" och klicka p&aring;
                      respektive typ).</td>
                  </tr>
                  <tr>
                    <td width="100%"><b>R&auml;tt att &auml;ndra mallar: </b>Inneb&auml;r
  
                    att man f&aring;r lov att byta utseende p&aring; sidan. Vilken/vilka
                      mallgrupper best&auml;ms av vilka som markeras i listan
                      (klicka p&aring; namnet s&aring; blir den bl&aring;markerad, f&ouml;r att
                      markera flera, h&aring;ll ned "Ctrl" och klicka p&aring;
                      respektive typ).</td>
                  </tr>
                </table>
</div align="CENTER">
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(43,1,'R&auml;ttigheter f&ouml;r begr&auml;nsad beh&ouml;righet 2, f&ouml;r nya dokument
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(43,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
<p align="center">
      "Definiera f&ouml;r nya dokument" betyder att r&auml;ttigheterna st&auml;lls
      in f&ouml;r sidor som skapas fr&aring;n aktuell sida.</p>
                <p align="left">Genom att markera respektive r&auml;ttighet och sedan klicka p&aring; "OK" f&aring;r
                anv&auml;ndare som tillh&ouml;r roller med <i>Begr&auml;nsad beh&ouml;righet 2 </i>dessa
                r&auml;ttigheter.</p>
        <p>&nbsp;</td>
    </tr>
  </table>
  </center>
</div>
<div align="CENTER">
                <table border="1" width="75%">
                  <tr>
                    <td width="100%"><b>R&auml;tt att &auml;ndra dokumentinformation: </b>Inneb&auml;r
                      att man f&aring;r lov att &auml;ndra p&aring; Rubrik (texten p&aring;
                      l&auml;nken), den f&ouml;rklarande texten till l&auml;nken och
 
                     ikon-bilden vid l&auml;nken.</td>
                  </tr>
                  <tr>
                    <td width="100%"><b>R&auml;tt att &auml;ndra avancerad
                      dokumentinformation: </b>Inneb&auml;r att man f&aring;r lov att
                      &auml;ndra p&aring; Rubrik (texten p&aring; l&auml;nken), den f&ouml;rklarande
                      texten till l&auml;nken, ikon-bilden vid l&auml;nken, s&ouml;kord,
                      publicera, arkivera, dela ut sidan och best&auml;mma om andra
                      administrat&ouml;rer skall f&aring; l&auml;nka till sidan.</td>
                  </tr>
                  <tr>
                    <td width="100%"><b>R&auml;tt att &auml;ndra r&auml;ttigheter f&ouml;r
                      roller: </b>Inneb&auml;r att man kan g&aring; in och &auml;ndra, l&auml;gga
                      till och ta bort roller och &auml;ndra beh&ouml;righeter f&ouml;r
                      dessa roller p&ar
ing; sidan. </td>
                  </tr>
                  <tr>
                    <td width="100%"><b>R&auml;tt att &auml;ndra texter: </b>Inneb&auml;r
                      att man f&aring;r lov att &auml;ndra textinneh&aring;llet p&aring; sidan.</td>
                  </tr>
                  <tr>
                    <td width="100%"><b>R&auml;tt att &auml;ndra bilder: </b>Inneb&auml;r
                      att man f&aring;r lov att &auml;ndra p&aring; bilderna p&aring; sidan.</td>
                  </tr>
                  <tr>
                    <td width="100%"><b>R&auml;tt att &auml;ndra menyer: </b>Inneb&auml;r
                      att man f&aring;r lov att skapa undersidor till sidan. Vilka
                      typer av sidor best&auml;ms av vilka som markeras i listan
                      (klicka p&aring; namnet s&aring; blir den bl&aring;markerad, f&ouml;r att
                      markera flera, h&aring;ll ned "Ctrl" och klicka p&aring;
                 
     respektive typ).</td>
                  </tr>
                  <tr>
                    <td width="100%"><b>R&auml;tt att &auml;ndra mallar: </b>Inneb&auml;r
                      att man f&aring;r lov att byta utseende p&aring; sidan. Vilken/vilka
                      mallgrupper best&auml;ms av vilka som markeras i listan
                      (klicka p&aring; namnet s&aring; blir den bl&aring;markerad, f&ouml;r att
                      markera flera, h&aring;ll ned "Ctrl" och klicka p&aring;
                      respektive typ).</td>
                  </tr>
                </table>
</div align="CENTER">
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(44,1,'L&auml;gga till bild - Bildarkiv
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(44,2,'H&auml;r visas alla bilder som redan finns uppladdade i systemet. Klicka f&ouml;rst p&aring; en bild och sedan p&aring; &quot;F&ouml;rhandsgranska markerad bild&quot; f&ouml;r att f&aring; se bilden innan du l&auml;gger in den p&aring; din sida. N&auml;r du hittat den bild du s&ouml;ker klicka p&aring; &quot;Anv&auml;nd markerad bild&quot;.
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(45,1,'Meddelande
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(45,2,'En bild med samma filnamn finns redan. Klicka p&aring; &quot;OK&quot; och g&aring; sedan in i &quot;Bildarkivet&quot; f&ouml;r att se att det &auml;r samma bild som du f&ouml;rs&ouml;ker ladda upp. Om det inte &auml;r samma bild f&aring;r du byta namn p&aring; filen i ditt n&auml;tverk innan du laddar upp filen igen.
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(46,1,'L&auml;gga till l&auml;nk till Browserkontroll - sida 2
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(46,2,'Samma webbsida kan se olika ut beorende p&aring; vilken webbl&auml;sare (browser) som anv&auml;nds. D&auml;rf&ouml;r kan man g&ouml;ra flera alternativa sidor och styra vilken sida som skall visas i respektive webbl&auml;sare. Markera webbl&auml;saren och klicka p&aring; &quot;L&auml;gg till&quot;. Ett f&auml;lt kommer upp p&aring; h&ouml;ger sida. Skriv in MetaId f&ouml;r den sida som skall visas. Upprepa om det &auml;r flera olika webbl&auml;sare som skall visa olika sidor. Klicka sedan p&aring; &quot;OK&quot;.
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(47,1,'L&auml;gga till diagram - bild 1
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(47,2,'<div align="center">
  <table border="1" width="75%">
    <tr>
      <td width="100%"><b>Rubrik:</b> Den text som kommer att vara sj&auml;lva l&auml;nktexten.</td>
    </tr>
    <tr>
      <td width="100%"><b>Text:</b> F&ouml;rklarande text som visas vid l&auml;nken (OBS
        visas endast om utseendemallen till&aring;ter detta).</td>
    </tr>
    <tr>
      <td width="100%"><b>Bild:</b> H&auml;r anges s&ouml;kv&auml;gen till den bild som
        skall visas som en ikon under l&auml;nken och den f&ouml;rklarande texten (OBS
        visas endast om utseendemallen till&aring;ter detta). Om ingen bild skall
        visas l&auml;mnas f&auml;ltet tomt.</td>
    </tr>
  </table>
  <h3 align="center">Avancerat</h3>
  <table border="1" width="75%">
    <tr>
      <td width="100%"><b>S&ouml;kord: </b>H&auml;r anges de s&ouml;kord som skall ge tr&auml;ff
        vid s&ouml;kning. En markering vid <i>Blockera s&ouml;kning</i> g&ouml;r att sidan inte kommer att ge n&aring;go
n
        tr&auml;ff vid s&ouml;kningar.</td>
    </tr>
    <tr>
      <td width="100%"><b>Dela ut: </b><i>Visa dokumentet &auml;ven f&ouml;r obeh&ouml;riga
        anv&auml;ndare</i> betyder att l&auml;nken till sidan visas men de kan inte
        komma in och se sj&auml;lva sidan. <i>Dela ut dokument f&ouml;r andra
        administrat&ouml;rer</i> g&ouml;r att administrat&ouml;rerna kan l&auml;nka till denna
        sida.</td>
    </tr>
    <tr>
      <td width="100%"><b>Publicera: </b>H&auml;r anges datum och klockslag om sidan
        inte skall visas omedelbart utan vid ett senare tillf&auml;lle.</td>
    </tr>
    <tr>
      <td width="100%"><b>Arkivera: </b>H&auml;r anges det datum d&aring; sidan skall
        arkiveras. L&auml;nken kommer att tas bort vid det datum och klockslag som
        anges. En markering vid <i>Arkivera nu</i> g&ouml;r att sidan arkiveras direkt.</td>
    </tr>
    <tr>
      <td width="100%"><b>Visa: </b>H&auml;r anges var sidan skall
 visas.&nbsp;
        <ul>
          <li><i>Samma frame</i> betyder att sidan &ouml;ppnas i den frame d&auml;r l&auml;nken
            finns.&nbsp;</li>
          <li><i>Nytt f&ouml;nster</i> anger att sidan &ouml;ppnas i ett nytt webbl&auml;sarf&ouml;nster.&nbsp;</li>
          <li><i>Ers&auml;tt allt</i> betyder att alla frames p&aring; sidan kommer att
            ers&auml;ttas med en ny frame d&auml;r den nya sidan visas.</li>
          <li><i>Annan frame</i> - h&auml;r anges namnet p&aring; den frame d&auml;r sidan
            skall visas.</li>
        </ul>
      </td>
    </tr>
  </table>
</div>
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(48,1,'L&auml;gga till diagram - bild 2 - Skapa nytt diagram
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(48,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
<p align="center">H&auml;r v&auml;ljs vilken typ av diagram som skall skapas.</p>
<blockquote>
                <p align="left">V&auml;lj diagramtyp: Klicka p&aring; <img border="0" src="/images/se/helpimages/Pil.GIF" width="16" height="21">
                f&ouml;r att kunna markera den diagramtyp du vill ha. De olika
                alternativen visas p&aring; bildens nedersta del. Klicka sedan p&aring;
                "Skapa nytt diagram".</p>
</blockquote>

      </td>
    </tr>
  </table>
  </center>
</div>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(49,1,'L&auml;gga till diagram - bild 3
<BR>Inmatningsformul&auml;r f&ouml;r diagram och tabeller
<BR>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(49,2,'<h3 align="center">Diagraminst&auml;llningar:</h3>
<table border="0" width="100%">
  <tr>
    <td width="100%">Det finns tv&aring; s&auml;tt att skapa diagram, antingen s&aring;
        h&auml;mtas v&auml;rdena fr&aring;n Excel eller s&aring; skriver man in v&auml;rdena direkt i
        formul&auml;ret. F&ouml;r samtliga diagram g&auml;ller det att i f&ouml;rsta kolumnen
        skall alltid det namn/siffra som skall st&aring; under/bredvid varje stapel i
        diagrammet anges. I andra kolumnen skall maxv&auml;rdena f&ouml;r stapel 1 med
        serietitel 1 st&aring; och i tredje kolumnen ska maxv&auml;rdena f&ouml;r stapel 2
        med serietitel 2 st&aring; osv.&nbsp;Vissa inst&auml;llningar autogenereras, men
        det g&aring;r att skriva in specifika v&auml;rden om man s&aring; &ouml;nskar.</td>
  </tr>
</table>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(49,3,'<div align="CENTER">
<table border="1" width="75%" height="323">
  <tr>
    <td width="100%" height="19"><b>Rubrik: </b>Anger rubriken f&ouml;r diagrammet
      som kommer att visas ovanf&ouml;r diagrammet. Kan utel&auml;mnas om ingen rubrik
      &ouml;nskas.</td>
  </tr>
  <tr>
    <td width="100%" height="19"><b>Bredd (x- och y-axel): </b>Autogenereras
      eller s&aring; kan h&auml;r anges bredden p&aring; diagrammet i pixlar r&auml;knat.</td>
  </tr>
  <tr>
    <td width="100%" height="19"><b>Rubrik (x- och y-axel): </b>Anger den rubrik
      som skall st&aring; p&aring; respektive axel.</td>
  </tr>
  <tr>
    <td width="100%" height="19"><b>Maxv&auml;rde (x- och y-axel): </b>Autogenereras
      eller s&aring; kan h&auml;r anges det h&ouml;gsta v&auml;rde som diagrammet skall ha p&aring;
      respektive axel. OBS att om axelns v&auml;rden ej &auml;r siffror kommer det som
      skrivs i detta f&auml;lt inte att visas.</td>
  </tr>
  <tr>
    <td width="100
%" height="19"><b>Delta x (x- och y-axel): </b>Autogenereras
      eller s&aring; kan h&auml;r anges det avst&aring;nd som skall vara mellan m&auml;tpunkterna
      p&aring; respektive axel. OBS att om axelns v&auml;rden ej &auml;r siffror kommer det
      som skrivs i detta f&auml;lt inte att visas.</td>
  </tr>
  <tr>
    <td width="100%" height="18"><b>Serietitel 1, 2 osv: </b>H&auml;r anges den text
      som man vill skall visas som f&ouml;rklaring till respektive f&auml;rg i
      diagrammet. Antal serietitlar &auml;r beroende av vilken diagramtyp som &auml;r
      vald. </td>
  </tr>
  <tr>
    <td width="100%" height="19">
      <p align="left"><b>L&auml;gg till rad: </b>L&auml;gger till en ny rad. Den nya
      raden l&auml;ggs till sist.</td>
  </tr>
  <tr>
    <td width="100%" height="19"><b>L&auml;gg till kolumn: </b>L&auml;gger till en ny
      kolumn. Den nya kolumnen l&auml;ggs till sist. </td>
  </tr>
  <tr>
    <td width="100%" height="19"><b>Ta bo
rt rad: </b>Tar bort en rad. Vilken rad
      som skall tas bort v&auml;ljer man i rullgardingsmenyn <img border="0" src="/images/se/helpimages/Pil.GIF" width="16" height="21">,
      n&auml;r rullgardingsmenyn sl&auml;pps tas raden bort. </td>
  </tr>
  <tr>
    <td width="100%" height="19"><b>Ta bort kolumn: </b> Tar bort en
      kolumn. Vilken kolumn som skall tas bort v&auml;ljer man i rullgardingsmenyn <img border="0" src="/images/se/helpimages/Pil.GIF" width="16" height="21">,
      n&auml;r rullgardingsmenyn sl&auml;pps tas kolumnen bort. </td>
  </tr>
  <tr>
    <td width="100%" height="18"><b>Klistra in diagramv&auml;rden h&auml;r: </b>Anv&auml;nds
      om man vill har v&auml;rden i Excel som man vill skapa diagram av. Kopiera de
      rader och kolumner som &ouml;nskas fr&aring;n Excelarket och klistra in dessa i
      rutan. Klicka sedan p&aring; "Skapa diagramv&auml;rden".</td>
  </tr>
</table>
</div align="CENTER">
<p> </p>
<div align="center">
  <cent
er>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
<h3 align="center">Tabellinst&auml;llningar:</h3>

<p align="center">Om man vill ha en tillh&ouml;rande tabell som visas vid diagrammet
fyller man i v&auml;rdena h&auml;r eller h&auml;mtar dem fr&aring;n Excel..</p>
      </td>
    </tr>
  </table>
  </center>
</div>
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(49,4,'<div align="CENTER">
<table border="1" width="75%" height="323">
  <tr>
    <td width="100%" height="19"><b>Rubrik: </b>Anger rubriken f&ouml;r tabellen som kommer att visas ovanf&ouml;r diagrammet. Kan utel&auml;mnas om ingen rubrik
      &ouml;nskas.</td>
  </tr>
  <tr>
    <td width="100%" height="19">
      <p align="left"><b>L&auml;gg till rad: </b>L&auml;gger till en ny rad. Den nya
      raden l&auml;ggs till sist.</td>
  </tr>
  <tr>
    <td width="100%" height="19"><b>L&auml;gg till kolumn: </b>L&auml;gger till en ny
      kolumn. Den nya kolumnen l&auml;ggs till sist. </td>
  </tr>
  <tr>
    <td width="100%" height="19"><b>Ta bort rad: </b>Tar bort en rad. Vilken rad
      som skall tas bort v&auml;ljer man i rullgardinsmenyn <img border="0" src="/images/se/helpimages/Pil.GIF" width="16" height="21">,
      n&auml;r rullgardinsmenyn sl&auml;pps tas raden bort. </td>
  </tr>
  <tr>
    <td width="100%" height="19"><b>Ta bort kolumn: </b> Tar bort en kolum
n.
      Vilken kolumn som skall tas bort v&auml;ljer man i rullgardinsmenyn <img border="0" src="/images/se/helpimages/Pil.GIF" width="16" height="21">,
      n&auml;r rullgardinsmenyn sl&auml;pps tas kolumnen bort. </td>
  </tr>
  <tr>
    <td width="100%" height="18"><b>Klistra in diagramv&auml;rden h&auml;r: </b>Anv&auml;nds
      om man vill har v&auml;rden i Excel som man vill skapa diagram av. Kopiera de
      rader och kolumner som &ouml;nskas fr&aring;n Excelarket och klistra in dessa i
      rutan. Klicka sedan p&aring; "Skapa tabellv&auml;rden".</td>
  </tr>
</table>
</div align="CENTER">
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(50,1,'L&auml;gga till diagram - bild 4
<BR>Nytt diagram meny
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(50,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
                <p align="left">Klicka p&aring; &quot;Metadata&quot; f&ouml;r att komma
                till den bild d&auml;r rubrik (l&auml;nktext), undertext och eventuell
                ikonbild skrivs in.</p>
                <p align="left">Klicka p&aring; &quot;&Aring;terg&aring;&quot; f&ouml;r att komma
                tillbaka till ursprungsdokumentet (det dokument som l&auml;nken till
                diagrammet &auml;r p&aring;).</p>
      </td>
    </tr>
  </table>
  </center>
</div>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(51,1,'R&auml;ttighet att f&aring; l&ouml;senord via e-post saknas
<BR>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(51,2,'<center>Du har inte beh&ouml;righet att f&aring; l&ouml;senordet s&auml;nt till dig via e-post. Var v&auml;nlig och kontakta Systemadministrat&ouml;ren f&ouml;r att f&aring; hj&auml;lp.</center>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(52,1,'L&ouml;senord via e-post
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(52,2,'<p align="center">Ange anv&auml;ndarnamn och klicka p&aring; &quot;S&auml;nd&quot;.</p>
<p align="center">L&ouml;senordet skickas till den e-postadress som uppgavs vid
registreringen.</p>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(53,1,'Inkludera en befintlig sida i en annan sida
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(53,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" width="427" cellspacing="0">
    <tr>
      <td width="425">
                <p align="left">Skriv in MetaId f&ouml;r den sida som skall
                inkluderas i den vita textboxen och klicka sedan p&aring;
                "OK". Den inkluderade sidan kommer nu att visas p&aring;
                den plats d&auml;r textboxen och "OK"-knappen tidigare visades.</p>
                <p align="left">Vid klick p&aring; l&auml;nken "<i>Redigera</i>"
                kommer den inkluderade sidan att visas i ett nytt f&ouml;nster och
                den kan redigeras.</p>
      </td>
    </tr>
  </table>
  </center>
</div>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(54,1,'Inloggning
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(54,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
<p align="center">Skriv in anv&auml;ndarnamn och l&ouml;senord.</p>
<p align="center">Klicka sedan p&aring; &quot;OK&quot;.</p>

      </td>
    </tr>
  </table>
  </center>
</div>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(55,1,'Knappraden
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(55,2,'<div align="center">
          <table border="1" width="500">
            <tr>
              <td>
              
          <h4 style="text-align:justify">F&ouml;reg&aring;ende</h4>
          <p class="MsoBodyText" style="text-align:justify">Leder tillbaka till
          den sida som &auml;r huvudsida f&ouml;r den sida du befinner dig p&aring;.</p>
          <h4 style="text-align:justify">Normal</h4>
          <p class="MsoBodyText" style="text-align:justify">Ett klick p&aring; "<b style="mso-bidi-font-weight:normal">Normal</b>"
          g&ouml;r att systemet &aring;terg&aring;r till ursprungssidan med de gjorda &auml;ndringarna.
          Om inga &auml;ndringar gjorts d&aring; administrat&ouml;ren g&aring;r ur admin-l&auml;get &aring;terg&aring;r
          systemet till ursprungssidan utan att f&ouml;r&auml;ndra inneh&aring;llet.</p>
          <h4 style="text-align:justify">Text</h4>
          <p style="text-align:justify">N&auml;r klick sker p&aring; "<b style="mso-bidi
-font-weight:normal">Text</b>"
          visas sm&aring; pilar p&aring; de platser p&aring; sidan d&auml;r det g&aring;r att l&auml;gga in
          text. F&ouml;r att l&auml;gga till sj&auml;lva texten - klicka p&aring; den r&ouml;da pilen
          p&aring; den plats d&auml;r texten skall l&auml;ggas till. Om ingen pil visas finns
          ingen plats f&ouml;r att l&auml;gga till text i den mall som styr sidan
          utseende.</p>
          <h4 style="text-align:justify">Bild</h4>
          <p class="MsoBodyText" style="text-align:justify">N&auml;r klick sker p&aring;
          "<b style="mso-bidi-font-weight:normal">Bild</b>" visas en liten
          ikon som det st&aring;r <i style="mso-bidi-font-style:normal">Bild </i>p&aring;
          och en liten pil p&aring; de platser p&aring; sidan d&auml;r det g&aring;r att l&auml;gga in
          bilder. F&ouml;r att l&auml;gga till sj&auml;lva bilden - klicka antingen p&aring;
          ikonen eller p&aring; den lilla pile
n p&aring; den plats d&auml;r bilden skall l&auml;ggas
          till. Om den redan finns en bild inlagd p&aring; sidan och den skall bytas
          ut, klicka p&aring; den lilla pilen bredvid bilden..</p>
          <p class="MsoBodyText" style="text-align:justify">Om ingen ikon eller
          pil visas finns ingen plats f&ouml;r att l&auml;gga till bild i den mall som
          styr sidan utseende.</p>
          <h4 style="text-align:justify">L&auml;nk</h4>
          <p style="text-align:justify">N&auml;r klick sker p&aring;
          "<b style="mso-bidi-font-weight:normal">L&auml;nkar</b>" visas en sida
          d&auml;r l&auml;nkar/menyer skapas. Det g&aring;r att l&auml;gga till l&auml;nkar till:</p>
          <blockquote>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span>Textdokument</p>
            <p class="-
49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span>URL-dokument (Internetsida)</p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span>Browserkontroll</p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span>HTML-dokument</p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span>Fil</p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="f
ont:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span>Diagram (OBS till&auml;ggsmodul till imCMS)</p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span>Konferens (OBS till&auml;ggsmodul till imCMS)</p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span>Befintligt dokument</p>
          </blockquote>
          <p class="MsoBodyText" style="text-align:justify">Vad varje l&auml;nk
          betyder, f&ouml;rklaras under respektive rubrik.</p>
          <h4 style="text-align:justify">Utseende</h4>
          <p class="MsoBodyText" style="text-align:justify">H&auml;r byts den mall
          som styr utseendet p&aring; sidan.</p>
          <h4 style="text-align:justify">Include</h4>
          <p style="text-align:justify">N&auml;r klick sker p&aring; "<b style="mso-bidi-font-weight:normal">Include</b>"
          visas admindelen f&ouml;r Include. Den best&aring;r av en vit textbox, "<b style="mso-bidi-font-weight:normal">OK</b><span style="mso-bidi-font-weight:bold">"-knapp
          och en l&auml;nk "<i>Redigera</i>".</span> </p>
          <p style="text-align:justify">Om ingen textruta visas finns ingen
          plats f&ouml;r att infoga befintlig sida i den mall som styr sidans
          utseende. </p>
          <h4 style="text-align:justify">Dok.info</h4>
          <p class="MsoBodyText" style="text-align:justify">H&auml;r skrivs sidans
          grundinformation in.</p>
          <h4 style="text-align:justify">R&auml;ttigheter</h4>
          <p class="MsoBodyText" style="text-align:justify">H&auml;r styrs de r&auml;ttigheter
          som anv&auml;ndarna har p&aring; sidan.</p>
          <h4 
style="text-align:justify">Logga ut</h4>
          <p class="MsoBodyText" style="text-align:justify">N&auml;r klick sker p&aring;
          "<b style="mso-bidi-font-weight:normal">Logga ut</b>", loggas anv&auml;ndaren
          ut fr&aring;n imCMS.</p>
          <h4 style="text-align:justify">Admin</h4>
          <p class="MsoBodyText" style="text-align:justify">Denna knapp visas
          endast f&ouml;r den som &auml;r super-admin och ett klick leder till
          administrationsl&auml;get f&ouml;r att:</p>
          <blockquote>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span><a href="#_Administrera_anv&auml;ndare"><span style="color:windowtext;text-decoration:none;text-underline:none">Administration
            av anv&auml;ndare</span><span style="color: windowtext; text-decoration: none; text-underline: none">&nb
sp;&nbsp;&nbsp;&nbsp;
            </span></a><span style="color: windowtext; text-decoration: none; text-underline: none">&nbsp;
            </span></p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span><a href="#_Administrera_roller"><span style="color:
windowtext;text-decoration:none;text-underline:none">Administration av roller</span></a></p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span><a href="#_Administrera_IP-accesser"><span style="color:windowtext;text-decoration:none;text-underline:none">Administration
            av IP-accesser</span></a></p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:
7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span><a href="#_Administrera_formatmallar/formatgrupper"><span style="color:windowtext;text-decoration:none;text-underline:none">Administration
            av mallar</span></a></p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span><a href="#_Visa_alla_dokument"><span style="color:windowtext;
text-decoration:none;text-underline:none">Visa alla dokument</span></a></p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span><a href="#_Ta_bort_ett"><span style="color:windowtext;
text-decoration:none;text-underline:none">Ta bort ett dokument</span></a></p>
            <p class="-49" style="te
xt-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span><a href="#_Kontrollera_Internetl&auml;nkar"><span style="color:windowtext;text-decoration:none;text-underline:none">Kontrollera
            Internet-l&auml;nkar</span></a></p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span><a href="#_Administrera_r&auml;knare"><span style="color:windowtext;text-decoration:none;text-underline:none">Administrera
            r&auml;knare</span></a></p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span><a href="#_Administrera_systeminformation"><span style="color:windowt
ext;text-decoration:none;text-underline:none">Administrera
            systeminformation</span></a></p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span><a href="#_Administrera_filer"><span style="color:windowtext;
text-decoration:none;text-underline:none">Administrera filer</span></a></p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span><a href="#_F&ouml;r&auml;ndrade_dokument"><span style="color:
windowtext;text-decoration:none;text-underline:none">F&ouml;r&auml;ndrade dokument</span></a></p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&n
bsp;
            </span></span>Administrera konferenser (OBS konferenser &auml;r en till&auml;ggsmodul
            till imCMS)</p>
          </blockquote>
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
       values(56,1,'Konferens - &auml;ndra anv&auml;ndare
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(56,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
        <p align="center">Markera den anv&auml;ndare du vill &auml;ndra. Klicka sedan
        p&aring; "&Auml;ndra".</p>
        <p align="center">F&ouml;r att avsluta administrationen - klicka p&aring;
        "Avsluta admin".</p>
        <p align="center">"&Aring;terg&aring;" leder tillbaka till Administrera
        konferens.</td>
    </tr>
  </table>
  </center>
</div>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(57,1,'Konferens - administrera anv&auml;ndardata
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(57,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td><i>Expertanv&auml;ndare:</i> Genom att s&auml;tta en <img border="0" src="/images/se/helpimages/Konf-a1.GIF" width="13" height="14">
        i rutan efter Expertanv&auml;ndare kommer det att visas en&nbsp; <img border="0" src="/images/se/helpimages/Konf-a2.GIF" width="12" height="16">&nbsp;
        framf&ouml;r rubriken i de inl&auml;gg
        som anv&auml;ndaren g&ouml;r. Detta visar att anv&auml;ndaren &auml;r specialist i
        &auml;mnet. </td>
    </tr>
  </table>
  </center>
</div>
<p align="center">&nbsp;</p>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(58,1,'Konferens - varning vid byte av mallset
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(58,2,'Om du &auml;r s&auml;ker p&aring; att du vill byta mallset, klicka &quot;OK&quot;. Om du &auml;r tveksam och vill kontrollera allt igen, klicka p&aring; &quot;Avbryt&quot;.
<BR>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(59,1,'Konferens - administrera diskussion
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(59,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>F&ouml;r att ta bort en diskussion: <img border="0" src="/images/se/helpimages/Konf-a3.GIF" width="13" height="14">
        f&ouml;r den diskussion som du vill ta bort och klicka sedan p&aring; "TA
        BORT".
        <p>F&ouml;r att l&auml;mna administrationsl&auml;get: klicka p&aring; "Avsluta admin".</td>
    </tr>
  </table>
  </center>
</div>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(60,1,'Konferens - administrera forum
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(60,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="500">
    <tr>
      <td><i>L&auml;gg till forum: </i>Ange det namn det nya forumet skall ha i
        f&auml;ltet namn. Klicka sedan p&aring; "L&auml;gg till".<p><i>Radera forum:
        </i>Markera det forum som skall tas bort.&nbsp; Klicka sedan p&aring; "Radera". En
        varningsbild dyker upp. Klicka p&aring; "OK" om du &auml;r s&auml;ker p&aring;
        att du vill ta bort forumet. Klicka p&aring; "Avbryt" om du &auml;r
        os&auml;ker.</p>
        <p><i>&Auml;ndra namn p&aring; forum:</i> Markera det forum du vill byta namn p&aring;
        genom att klicka p&aring; det i listan till h&ouml;ger. Ange det nya namnet i
        f&auml;ltet under <i>Nytt namn</i>. Klicka sedan p&aring; "&Auml;ndra". En
        varningsbild dyker upp d&auml;r du f&aring;r bekr&auml;fta att du vill byta namn.</p>
        <p><i>Antalet diskussioner som visas:</i> H&auml;r anges hur m&aring;n
ga
        diskussioner som skall visas &aring;t g&aring;ngen i ett forum. Markera det forum
        du vill &auml;ndra antalet diskussioner i genom att klicka p&aring; det i listan
        till h&ouml;ger. Det befintliga antalet diskussioner som visas i forumet
        visas inom parentes i listan. V&auml;lj sedan det&nbsp; nya antalet
        diskussioner som skall visas genom att klicka p&aring; <img border="0" src="/images/se/helpimages/Pil.GIF" width="16" height="21">
        och markera det antalet. Klicka sedan p&aring; "Uppdatera".</td>
    </tr>
  </table>
  </center>
</div>
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(61,1,'Konferens - administrera inl&auml;gg
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(61,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td><b>Ta bort en kommentar:</b> <img border="0" src="/images/se/helpimages/Konf-a3.GIF" width="13" height="14">
        f&ouml;r den/de kommentarer du vill ta bort och klicka sedan p&aring; "TA
        BORT". En varningsbild visas d&auml;r du f&aring;r bekr&auml;fta att du
        verkligen vill ta bort inl&auml;gget. Inl&auml;gget kommer d&aring; att tas bort
        fr&aring;n diskussionen. OBS att diskussionens f&ouml;rsta inl&auml;gg, dvs, det som
        initierade diskussionen <b>inte</b> kan tas bort. Det g&aring;r dock att
        redigera inl&auml;gget alternativt ta bort hela diskussionen. Var d&aring;
        medveten om att samtliga inl&auml;gg i diskussionen f&ouml;rsvinner.
        <p><b>Spara om en kommentar: </b>F&ouml;r att &auml;ndra ett befintligt inl&auml;gg,
        &auml;ndra den text som skall uppdateras och markera inl&auml;gget genom att
 <img border="0" src="/images/se/helpimages/Konf-a3.GIF" width="13" height="14">
        f&ouml;r det. Klicka sedan p&aring; "SPARA OM".</p>
        <p>F&ouml;r att &aring;terg&aring; till anv&auml;ndarl&auml;ge, klicka p&aring; "Avsluta admin".</td>
    </tr>
  </table>
  </center>
</div>
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(62,1,'Konferens - administrera mallset
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(62,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td><i>Registrera nytt mallset: </i>F&ouml;r att skapa ett nytt mallset, ange
        namnet p&aring; det nya mallsetet och tryck p&aring; &quot;Nytt mallset&quot;. OBS
        att mallsetets namn inte f&aring;r inneh&aring;lla n&aring;gra specialtecken som &aring;&auml;&ouml;.
        N&auml;r ett nytt mallset skapas, kommer mallsetet &quot;Original&quot;:s
        mallfiler att kopieras till det nya mallsetet.
        <p><i>&Auml;ndra mallset f&ouml;r en konferens: </i> Markera det mallset du vill
        anv&auml;nda f&ouml;r den konferens du administrerar. Klicka sedan p&aring;
        &quot;Byta mallset&quot;. Mallsetet &auml;ndras. Det mallset som anv&auml;nds i
        konferensen visas i <b>fetstil</b>&nbsp; i dialogen.</p>
        <p><i>Uppdatera malfil: </i>F&ouml;r att uppdatera en mallfil till ett
        befintligt mallset, v&auml;lj f&ouml;rst vilk
et set som skall uppdateras och
        sedan vilken typ av mall det &auml;r som skall uppdateras (Konferensen
        st&ouml;der tv&aring; typer av filer: bilder och html-filer ). N&auml;r dessa val &auml;r
        gjorda, klicka p&aring; &quot;Administrera&quot;.</p>
        <p>F&ouml;r att avsluta administrationen, klicka p&aring; &quot;Avsluta admin&quot;.</td>
    </tr>
  </table>
  </center>
</div>
<p align="center">&nbsp;</p>
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(63,1,'Konferens - administrera sj&auml;lvregistrering
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(63,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>Sidan anv&auml;nds f&ouml;r att tilldela sj&auml;lvregistrerade anv&auml;ndare i
        konferensen en viss roll. N&auml;r anv&auml;ndaren registrerar sig tilldelas
        han/hon den/de roller som st&aring;r i rutan under <i>Befintliga.</i>
        <p><b><i>&nbsp;</i>L&auml;gga till en roll:</b> markera den i det v&auml;nstra
        rutan, klicka sedan p&aring; &quot;--&gt;&quot;. Rollen flyttas &ouml;ver i det
        h&ouml;gra rutan och nya sj&auml;lvregistrerade anv&auml;ndare kommer att tilldelas
        den rollen (och &ouml;vriga ocks&aring; om det finns fler i rutan). </p>
        <p><b>Ta bort en roll: </b>markera rollen i den h&ouml;gra rutan och klicka
        p&aring; &quot;&lt;--&quot;. Rollen flyttas &ouml;ver till det v&auml;nstra f&ouml;nstret
        och nya sj&auml;lvregistrerade anv&auml;ndare kommer <b>inte</b> att tilldelas
        d
en rollen.</td>
    </tr>
  </table>
  </center>
</div>
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(64,1,'Konferens - &auml;ndra befintlig mallfil
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(64,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>Leta reda p&aring; den fil som skall laddas upp genom att klicka p&aring; &quot;Browse/S&ouml;k&quot;.
        Namnet p&aring; knappen &auml;r beroende av vilket spr&aring;k din webbl&auml;sare
        anv&auml;nder. Leta reda p&aring; filen p&aring; din h&aring;rddisk, klicka sedan p&aring;
        &quot;Ladda upp&quot;. Den valda mallfilen kopieras till servern. OBS
        att den befintliga mallfilen kommer att skrivas &ouml;ver.</td>
    </tr>
  </table>
  </center>
</div>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(65,1,'Konferens - inloggning
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(65,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
        <p align="center"><b>Redan registrerad anv&auml;ndare: </b>skriv in namn och
        l&ouml;senord och klicka p&aring; "OK".</p>
        <p align="center"><b>Ny anv&auml;ndare: </b>klicka p&aring; <i>"REGISTRERA".</i></td>
    </tr>
  </table>
  </center>
</div>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(66,1,'Konferensvy
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(66,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>Varje konferens har minst ett forum d&auml;r olika diskussionen p&aring;g&aring;r.
        V&auml;lj vilket forum du vill genom att klicka p&aring; <img border="0" src="/images/se/helpimages/Pil.GIF" width="16" height="21">
        under rubriken <i>V&auml;lj forum, </i>markera forumet och klicka p&aring;
        "V&auml;lj". De diskussioner som visas i f&ouml;nstrets v&auml;nstra del &auml;r
        rubrikerna till de f&ouml;rsta inl&auml;ggen som skrivits i respektive
        diskussion. F&ouml;r att leta bland de diskussioner som inte visas anv&auml;nds
        knapparna "Tidigare inl&auml;gg" och "Senare inl&auml;gg. Om <img border="0" src="/images/se/helpimages/Konf-k4.GIF" width="21" height="15">&nbsp;
        (Ny-symbolen) visas framf&ouml;r en diskussion s&aring; inneb&auml;r det att antingen
        &auml;r detta en ny diskussion eller s&aring; har de
t tillkommit nya inl&auml;gg
        sedan du sist var inloggad i konferensen. F&ouml;r att visa de inl&auml;gg som
        tillkommit sedan du sist var inloggad, klicka p&aring; "Uppdatera".
        <p>Genom att klicka p&aring; diskussionsrubriken visas alla inl&auml;gg i
        diskussionen i f&ouml;nstrets h&ouml;gra del. Om <img border="0" src="/images/se/helpimages/Konf-k3.GIF" width="14" height="16">
        (specialist-symbolen) visas framf&ouml;r rubriken inneb&auml;r det att inl&auml;gget
        skrivits av en anv&auml;ndare som i den h&auml;r konferensen &auml;r n&aring;gon form av
        specialist. Efter den eventuella "specialistsymbolen" f&ouml;ljer
        inl&auml;ggets rubrik, inl&auml;ggstext, f&ouml;rfattare samt datum n&auml;r inl&auml;gget
        skapades.</p>
        <p>Det g&aring;r att ange hur kommentarerna skall sorteras genom att klicka i
        <i>"Stigande" </i>eller <i>"</i><i>F</i><i>allande"</i>.
        Klicka sedan p&aring; "Sortera". 
Kommentarerna sorteras d&aring; efter
        det datum de lades in. </p>
        <p>Ny diskussion skapas genom att klicka p&aring; "Ny diskussion".</p>
        <p>Nytt inl&auml;gg skapas genom att klicka p&aring; n&aring;gon av "Kommentera"-knapparna.
        Anledningen till att det finns tv&aring; &auml;r att om det &auml;r m&aring;nga inl&auml;gg
        &auml;r det bra att ha en knapp h&ouml;gst upp och en l&auml;ngst ned f&ouml;r d&aring;
        beh&ouml;ver man inte bl&auml;ddra upp och ned n&auml;r nya inl&auml;gg skall skapas.</p>
        <p>F&ouml;r att s&ouml;ka bland inl&auml;ggen finns en s&ouml;kfunktion som visas h&ouml;gst
        upp till h&ouml;ger i f&ouml;nstret. S&ouml;kning kan g&ouml;ras bland ett forums
        inl&auml;gg. Det g&aring;r att s&ouml;ka p&aring; antingen rubriker, inneh&aring;ll eller
        f&ouml;rfattare. F&ouml;rst v&auml;ljs vad du vill s&ouml;ka bland, sedan skall
        s&ouml;kordet skrivas in. Klicka sedan p&aring; "S&ouml;k
". S&ouml;kningen kan
        begr&auml;nsas genom att ange start- och slutdatum f&ouml;r s&ouml;kningen. Ett
        datum m&aring;ste anges i formen yyyy-mm-dd eller i specialformerna
        "ig&aring;r" och "idag".</p>
        <h2 align="center"><b>Administration</b></h2>
        <p>F&ouml;r att administrera ett forum, klicka p&aring; "Admin" uppe
        till v&auml;nster i f&ouml;nstret. D&aring; visas en sida d&auml;r forum kan l&auml;ggas
        till, tas bort, bytas namn p&aring; och d&auml;r det g&aring;r att &auml;ndra hur m&aring;nga
        diskussioner som skall visas &aring;t g&aring;ngen i ett forum.</p>
        <p>F&ouml;r att administrera diskussionen klicka p&aring; "Admin" under
        "Senare inl&auml;gg". D&auml;r kan diskussionen tas bort.</p>
        <p>F&ouml;r att administrera kommentarer, klicka p&aring; "Admin" i
        f&ouml;nstrets nedre del. D&aring; visas en bild d&auml;r kommentarer kan tas bort
        eller &auml;ndras.</p>
        <
p>&nbsp;</td>
    </tr>
  </table>
  </center>
</div>
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(67,1,'Konferens - sj&auml;lvregistrering
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(67,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="500">
    <tr>
      <td><i>&nbsp;</i>Fyll i nedanst&aring;ende uppgifter. F&auml;lten: Anv&auml;ndarnamn,
        l&ouml;senord, verifiera l&ouml;senord, f&ouml;rnamn, efternamn och email &auml;r
        obligatoriska.<i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </i>
        <p><i>ANV&Auml;NDARNAMN: </i>ange det namn du vill ha som anv&auml;ndarnamn<i><br>
        L&Ouml;SENORD: </i>skriv in det l&ouml;senord som du vill ha<i><br>
        VERIFIERA L&Ouml;SENORD: </i>skriv in l&ouml;senordet en g&aring;ng till<br>
        <i>YRKE/TITEL:</i> skriv in namnet p&aring; det yrke du har eller den titel
        du har<br>
        <i>F&Ouml;RNAMN: </i>skriv in ditt f&ouml;rnamn<br>
        <i>EFTERNAMN: </i>skriv in ditt efternamn<br>
        <i>ARBETSPLATS: </i>skriv in namnet p&aring; din arbetsplats<br>
        <i>ORT: </i>skriv in den ort d&auml;r din arbetsplats &auml;r bel&auml;gen<br>
        <i>TELEFON ARBETE: </i>skriv in ditt arbetstelefonnummer<br>
        <i>EMAIL:</i> skriv in den e-postadress du vill f&aring; e-post till</td>
    </tr>
  </table>
  </center>
</div>
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(68,1,'Konferens - konferensdata
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(68,2,'<div align="center">
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
        <p align="center"><i>Forum namn: </i>Skriv in det namn som konferensen
        skall ha.</p>
        <p align="center">Klicka sedan p&aring; &quot;OK&quot;.</td>
    </tr>
  </table>
</div>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(69,1,'Konferens - skapa en ny diskussion
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(69,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td><b>Skapa en ny diskussion:</b> Skriv in rubrik och text, infoga
        eventuell l&auml;nk och/eller e-post (mail) genom att klicka p&aring; respektive
        l&auml;nk och fylla i de uppgifter som beh&ouml;vs p&aring; de f&ouml;nster som &ouml;ppnas.
        Klicka sedan p&aring; &quot;Skicka&quot;. &quot;&Aring;ngra&quot; avbryter utan
        att spara diskussionen. OBS att det du anger i rubriken blir namnet p&aring;
        sj&auml;lva diskussionen. Rubrik och den text du anger blir det f&ouml;rsta
        inl&auml;gget i diskussionen.</td>
    </tr>
  </table>
  </center>
</div>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(70,1,'Konferens - skapa en ny kommentar
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(70,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td><b>Skapa ett nytt inl&auml;gg/kommentar:</b> Skriv in rubrik och text,
        infoga eventuell l&auml;nk och/eller e-post (mail) genom att klicka p&aring;
        respektive l&auml;nk och fylla i de uppgifter som beh&ouml;vs p&aring; de f&ouml;nster
        som &ouml;ppnas. Klicka sedan p&aring; "Skicka". "&Aring;ngra"
        avbryter utan att spara inl&auml;gget/kommentaren. </td>
    </tr>
  </table>
  </center>
</div>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(71,1,'L&auml;gga till/redigera anv&auml;ndare
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(71,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
                <p align="left">H&auml;r kan man l&auml;gga till nya anv&auml;ndare, ge
                befintliga anv&auml;ndare nya roller och andra egenskaper. De f&auml;lt
                som &auml;r markerade med * &auml;r obligatoriska. </p>
                <ul>
                  <li>
                    <p align="left"><i>Spr&aring;k</i> - valt spr&aring;k visar
                    administrationsmallar p&aring; detta spr&aring;k.</li>
                  <li>
                    <p align="left"><i>Telefonnummer</i> - f&ouml;r att l&auml;gga till
                    telefonnummer - ange landskod (inget + framf&ouml;r), riktnr och
                    telefonnr.&nbsp; Klicka sedan p&aring; OK. F&ouml;r att ta bort eller
                    &auml;ndra ett telefonnr: bl&auml;ddra fram det telefonnr som skall
                    &auml;ndras/tas bort geno
m att klicka p&aring; <img border="0" src="/images/se/helpimages/Pil.GIF" width="17" height="22">.
                    N&auml;r telefonnr &auml;r markerat - klicka p&aring; den knapp som
                    g&auml;ller f&ouml;r det du vill g&ouml;ra. Om "&Auml;ndra" &auml;r
                    valet, kommer telefonnr att visas i rutorna och sedan kan
                    man &auml;ndra det som skall &auml;ndras och till sist klicka p&aring;
                    "OK". </li>
                  <li>
                    <p align="left"><i>Aktiverad - </i>en <img border="0" src="/images/se/helpimages/Lagg-t1.GIF" width="13" height="14">
                    g&ouml;r att anv&auml;ndaren &auml;r aktiverad och kan logga in. <img border="0" src="/images/se/helpimages/Lagg-t2.GIF" width="13" height="14">
                    kan tas bort om anv&auml;ndaren inte l&auml;ngre skall kunna logga
                    in i systemet.</li>
                  <li>
                    <p align="left">
<i>Anv&auml;ndartyp </i>- autentiserade
                    anv&auml;ndare &auml;r anv&auml;ndare som loggar in i systemet med
                    anv&auml;ndarnamn och l&ouml;senord. Konferensanv&auml;ndare &auml;r
                    anv&auml;ndare som kan registrera sig sj&auml;lva i en konferens.</li>
                  <li>
                    <p align="left"><i>Roller </i>- v&auml;lj vilken/vilka roller
                    anv&auml;ndaren skall tillh&ouml;ra genom att klicka p&aring; rollen.
                    F&ouml;r att v&auml;lja fler roller h&aring;ll Ctrl nedtryckt samtidigt
                    som du klickar p&aring; rollen.</li>
                </ul>
                <p align="left">"Spara" - g&ouml;r att anv&auml;ndarens
                uppgifter sparas.</p>
                <p align="left">"&Aring;terst&auml;ll" - g&ouml;r att formul&auml;ret
                visar senast sparade uppgifter.</p>
                <p align="left">"Avbryt" - rensar alla uppgifter 
och
                f&ouml;reg&aring;ende sida visas.</p>
      </td>
    </tr>
  </table>
  </center>
</div>
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(72,1,'L&auml;gga till bild
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(72,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
<h3 align="center"> L&auml;gg till bild </h3>
<blockquote>
  <blockquote>
      <p align="left"> Antingen kan en bild l&auml;ggas in genom att anv&auml;nda&nbsp;
      "Browse"-knappen eller "S&ouml;k"-knappen, (vad knappen
      heter styrs av vilket spr&aring;k webbl&auml;saren&nbsp; anv&auml;nder) eller genom att
      klicka p&aring; "Bildarkiv"-knappen.</p>
        <ul>
          <li>
            <p align="left"> Anv&auml;nds "Browse"-knappen eller "S&ouml;k"-knappen
            s&ouml;ks bilden fram p&aring; det egna n&auml;tverket. För att ladda upp en bild - klicka på knappen och leta fram bilden på din hårddisk/nätverk. Klicka sedan på "Open". Tänk på att bildnamnet inte får innehålla mellanslag. Skriv inte "pig let", skriv "piglet" eller "pig_let". Om det redan finns en bild med samma namn visas ett meddelande.</li>
        </ul>
        <ul>
          <li>
            <p align="left"> Anv&auml;nds "Bildarkivet" g&ouml;rs s&ouml;kningen
            bland de bilder som redan finns uppladdade i systemet. </li>
        </ul>
  </blockquote>
</blockquote>
      </td>
    </tr>
  </table>
  </center>
</div>
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(72,3,'<div align="Center">
        <table border="1" width="75%">
          <tr>
            <td width="100%"><b> Bild:</b> H&auml;r visas s&ouml;kv&auml;gen till bilden n&auml;r
              den h&auml;mtas fr&aring;n det egna n&auml;tverket.</td>
          </tr>
          <tr>
            <td width="100%"><b> F&auml;ltet under MetaId: </b> F&auml;ltet visas endast
              om det finns en bild inlagd p&aring; sidan (eller om man klickat p&aring;
              "F&ouml;rhandsgranska bilden"). Bilden visas d&aring; h&auml;r.</td>
          </tr>
          <tr>
            <td width="100%"><b> Bild 1: </b> H&auml;r visas s&ouml;kv&auml;gen till bilden
              n&auml;r bilden h&auml;mtas fr&aring;n bildarkivet. Klicka p&aring;
              "F&ouml;rhandsgranska bilden" s&aring; visas bilden. (Siffran
              anger vilket bildf&auml;lt p&aring; sidan som &aring;syftas).</td>
          </tr>
          <tr>
            <td width="100%"><b> Bildnamn: </b> H&auml
;r kan ett namn p&aring; bilden
              anges, det visas dock ingenstans p&aring; sidan.</td>
          </tr>
          <tr>
            <td width="100%"><b> Format: </b> Bildens bredd, h&ouml;jd och om n&aring;gon
              ram (kant) skall finnas runt bilden. Alla m&aring;tt anges i pixlar.</td>
          </tr>
          <tr>
            <td width="100%"><b> N&auml;sta textf&auml;lts placering: </b> H&auml;r v&auml;ljs var
              texten kring bilden kommer att hamna. OBS att det beror p&aring;
              utseendemallen om dessa val fungerar eller ej. Klicka p&aring;<b> <img border="0" src="/images/se/helpimages/Lagg-t3.GIF">
              </b> f&ouml;r att v&auml;lja alternativ.
              <ul>
                <li><i> Ingen: </i> Textens placering styrs av webbl&auml;sarens
                  default-inst&auml;llning.</li>
                <li><i> Baslinjen: </i> Texten b&ouml;rjar vid nedre h&ouml;gra h&ouml;rnet av
                  bilden och f
orts&auml;tter sedan under bilden.</li>
                <li><i> Toppen: </i> Textens f&ouml;rsta rad b&ouml;rjar vid bildens &ouml;vre
                  h&ouml;gre h&ouml;rn och rad tv&aring; forts&auml;tter sedan under bilden.</li>
                <li><i> Mitten: </i> Textens f&ouml;rsta rad b&ouml;rjar vid bildens mitt
                  (till h&ouml;ger om bilden) och rad tv&aring; forts&auml;tter sedan under
                  bilden.</li>
                <li><i> Botten: </i> Texten b&ouml;rjar vid nedre h&ouml;gra h&ouml;rnet av
                  bilden och forts&auml;tter sedan under bilden.</li>
                <li><i> Texttoppen: </i> Textens h&ouml;gsta del placeras i h&ouml;jd med
                  bildens &ouml;vre kant (till h&ouml;ger om bilden).</li>
                <li><i> Exakt mitten: </i> Textens mittpunkt (h&ouml;jdm&auml;ssigt)
                  hamnar vid bildens mitt (till h&ouml;ger om bilden).&nbsp;</li>
                <li><i> L&auml;ngst ner: </i> Te
xtens l&auml;gsta del (som bokstaven g)
                  placeras i h&ouml;jd med bildens nedre kant (till h&ouml;ger om
                  bilden).</li>
                <li><i> Bild v&auml;nster: </i> Texten placeras till h&ouml;ger om bilden.</li>
                <li><i> Bild h&ouml;ger: </i> Texten placeras till v&auml;nster om bilden.</li>
              </ul>
            </td>
          </tr>
          <tr>
            <td width="100%"><b> Bildtext under uppladdning: </b> Text som visas
              under tiden bilden laddas upp (l&auml;mpligt om det &auml;r en v&auml;ldigt
              stor bild som tar l&aring;ng tid att ladda upp).</td>
          </tr>
          <tr>
            <td width="100%"><b> Alt bild under uppladdning: </b> Bild som visas
              under tiden bilden laddas upp (l&auml;mpligt om det &auml;r en v&auml;ldigt
              stor bild som tar l&aring;ng tid att ladda upp).</td>
          </tr>
          <tr>
            <td width="100
%"><b>"Luft" kring bilden: </b> H&auml;r anges om
              det skall finnas ett tomt utrymme (luft)&nbsp; kring bilden.
              Storleken p&aring; "luften" anges i pixlar. B&aring;de vertikal
              och horisontell "luft" anges.</td>
          </tr>
          <tr>
            <td width="100%"><b> L&auml;nkad till www: </b> Om bilden skall vara
              klickbar och l&auml;nka till en webbsida anges Internet-adressen
              h&auml;r.&nbsp;</td>
          </tr>
          <tr>
            <td width="100%"><b> L&auml;nk &ouml;ppnas i: </b> H&auml;r v&auml;ljs i vilket
              f&ouml;nster/frame l&auml;nken skall &ouml;ppnas. Klicka p&aring;<b> <img border="0" src="/images/se/helpimages/Lagg-t3.GIF">
              </b> f&ouml;r att v&auml;lja alternativ.
              <ul>
                <li><i> Aktuellt f&ouml;nster: </i>&Ouml;ppnar sidan som bilden l&auml;nkar
                  till i det &ouml;versta f&ouml;nstret (OBS ej frame)</li>
                <li><i> Nytt f&ouml;nster: </i> Sidan &ouml;ppnas i ett nytt f&ouml;nster.</li>
                <li><i> Moderram: </i> Sidan &ouml;ppnas i framen eller f&ouml;nstret som
                  inneh&aring;ller framesetet.</li>
                <li><i> Samma ram: </i>&Ouml;ppnar sidan i den nuvarande framen eller
                  f&ouml;nstret (som bilden ligger i).</li>
                <li><i> Annan ram: </i>&Ouml;ppnar sidan i en annan frame. Namnet p&aring;
                  framen anges i det vita f&auml;ltet till h&ouml;ger.</li>
              </ul>
            </td>
          </tr>
        </table>
</div align="Center">
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(73,1,'L&auml;gga till bild - Browse/S&ouml;k
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(73,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
<p align="center"> S&ouml;k fram filen p&aring; n&auml;tverket. Klicka p&aring;
den s&aring; att filnamnet visas i rutan f&ouml;r File name.</p>
<p align="center">Klicka sedan p&aring; &quot;Open&quot;.</p>

        <p>&nbsp;</td>
    </tr>
  </table>
  </center>
</div>
<p align="center">&nbsp;&nbsp;&nbsp; </p>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(74,1,'L&auml;gga till l&auml;nk till en fil - sida 2
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(74,3,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
        <blockquote>
                  <p align="left">S&ouml;k fram filen p&aring; ditt n&auml;tverk genom att
                  anv&auml;nda "Browse"- eller "S&ouml;k"-knappen.
                  Vad knappen heter beror p&aring; vilket spr&aring;k <br>
                  webbl&auml;saren &auml;r p&aring;.</p>
  </center>
<h3 align="left">Filtyp</h3>
        <p class="MsoBodyText" align="left">Filtyp beh&ouml;ver bara v&auml;ljas om det
        &auml;r en MAC som anv&auml;nds. Anv&auml;nds PC k&auml;nner systemet automatiskt av
        vilken filtyp filen har.</p>
        <p class="MsoBodyText" align="left">Med hj&auml;lp av rullgardinslisten
        anger man vad f&ouml;r slags fil det &auml;r som ska laddas upp. Om filen som
        ska laddas upp &auml;r<span style="mso-spacerun: yes">&nbsp; </span>av annan
        filtyp &auml;n de alternativen som g
&aring;r att v&auml;lja i rullgardinslisten v&auml;ljer
        man alternativet "<b>Annan</b>" och skriver in i f&auml;ltet "<b style="mso-bidi-font-weight:normal">Annan</b>"
        vilken filtyp det &auml;r.</p>
        <p class="MsoBodyText" align="left">Klicka p&aring; "OK".</p>
        </blockquote>
  <center>
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(75,1,'L&auml;gga till l&auml;nk till HTML-dokument - sida 1
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(75,2,'<h3 align="center">L&auml;gga till dokument</h3>
<div align="center">
  <table border="1" width="100%">
    <tr>
      <td width="100%"><b>Rubrik:</b> Den text som kommer att vara sj&auml;lva l&auml;nktexten.</td>
    </tr>
    <tr>
      <td width="100%"><b>Text:</b> F&ouml;rklarande text som visas vid l&auml;nken (OBS
        visas endast om utseendemallen till&aring;ter detta). Om ingen f&ouml;rklarande
        text skall visa l&auml;mnas f&auml;ltet tomt.</td>
    </tr>
    <tr>
      <td width="100%"><b>Bild:</b> H&auml;r anges s&ouml;kv&auml;gen till den bild som
        skall visas som en ikon under l&auml;nken och den f&ouml;rklarande texten (OBS
        visas endast om utseendemallen till&aring;ter detta). Om ingen bild skall
        visas l&auml;mnas f&auml;ltet tomt.</td>
    </tr>
  </table>
  <h3 align="center">Avancerat</h3>
  <table border="1" width="100%">
    <tr>
      <td width="100%"><b>S&ouml;kord: </b>H&auml;r anges de s&ouml;kord som skall ge
 tr&auml;ff
        vid s&ouml;kning. En <img border="0" src="/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
        i rutan vid <i>Blockera s&ouml;kning</i> g&ouml;r att sidan inte kommer att ge n&aring;gon
        tr&auml;ff vid s&ouml;kningar.</td>
    </tr>
    <tr>
      <td width="100%"><b>Dela ut: </b><i>Visa dokumentet &auml;ven f&ouml;r obeh&ouml;riga
        anv&auml;ndare</i> betyder att l&auml;nken till sidan visas men de kan inte
        komma in och se sj&auml;lva sidan. <i>Dela ut dokument f&ouml;r andra
        administrat&ouml;rer</i> g&ouml;r att administrat&ouml;rerna kan l&auml;nka till denna
        sida.</td>
    </tr>
    <tr>
      <td width="100%"><b>Publicera: </b>H&auml;r anges datum och klockslag om sidan
        inte skall visas omedelbart utan vid ett senare tillf&auml;lle.</td>
    </tr>
    <tr>
      <td width="100%"><b>Arkivera: </b>H&auml;r anges det datum d&aring; sidan skall
        arkiveras. L&auml;nken kommer att tas
 bort vid det datum och klockslag som
        anges. En <img border="0" src="/images/se/helpimages/Lagg-t5.GIF" width="13" height="14"> i
        rutan vid <i>Arkivera nu</i> g&ouml;r att sidan arkiveras direkt.</td>
    </tr>
    <tr>
      <td width="100%"><b>Visa: </b>H&auml;r anges var sidan skall visas.&nbsp;
        <ul>
          <li><i>Samma frame</i> betyder att sidan &ouml;ppnas i den frame d&auml;r l&auml;nken
            finns.&nbsp;</li>
          <li><i>Nytt f&ouml;nster</i> anger att sidan &ouml;ppnas i ett nytt webbl&auml;sarf&ouml;nster.&nbsp;</li>
          <li><i>Ers&auml;tt allt</i> betyder att alla frames p&aring; sidan kommer att
            ers&auml;ttas med en ny frame d&auml;r den nya sidan visas.</li>
          <li><i>Annan frame</i> - h&auml;r anges namnet p&aring; den frame d&auml;r sidan
            skall visas.</li>
        </ul>
      </td>
    </tr>
  </table>
</div>
<p align="center">&nbsp;</p>
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(76,1,'L&auml;gga till l&auml;nk till HTML-dokument - sida 2
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(76,2,'<div align="center">
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
<p align="left"><span style="font-family: Times New Roman; mso-fareast-font-family: Times New Roman; mso-ansi-language: SV; mso-fareast-language: EN-US; mso-bidi-language: HE">"<b style="mso-bidi-font-weight:normal">Tomt
Htmldokument</b>" anv&auml;nds om antalet frames p&aring; dokumentet ska f&ouml;r&auml;ndras. N&auml;r
man gjort detta kommer en sida upp med en tom textruta d&auml;r man kan skriva
Htmlkod f&ouml;r ett nytt frameset. N&auml;r koden &auml;r f&auml;rdigskriven trycker man p&aring;
knappen "<b style="mso-bidi-font-weight:normal">OK</b>".</span> OBS att koden m&aring;ste inledas med
&lt;HTML&gt; och avslutas med &lt;/HTML&gt;.</p>

      </td>
    </tr>
  </table>
</div>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(77,1,'L&auml;gga till l&auml;nk till Text-dokument - sida 1
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(77,2,'<h3 align="center">L&auml;gga till dokument</h3>
<div align="center">
  <table border="1" width="75%">
    <tr>
      <td width="100%"><b>Rubrik:</b> Den text som kommer att vara sj&auml;lva l&auml;nktexten.</td>
    </tr>
    <tr>
      <td width="100%"><b>Text:</b> F&ouml;rklarande text som visas vid l&auml;nken (OBS
        visas endast om utseendemallen till&aring;ter detta). Om ingen f&ouml;rklarande
        text skall visa l&auml;mnas f&auml;ltet tomt.</td>
    </tr>
    <tr>
      <td width="100%"><b>Bild:</b> H&auml;r anges s&ouml;kv&auml;gen till den bild som
        skall visas som en ikon under l&auml;nken och den f&ouml;rklarande texten (OBS
        visas endast om utseendemallen till&aring;ter detta). Om ingen bild skall
        visas l&auml;mnas f&auml;ltet tomt.</td>
    </tr>
  </table>
  <h3 align="center">Avancerat</h3>
  <table border="1" width="75%">
    <tr>
      <td width="100%"><b>S&ouml;kord: </b>H&auml;r anges de s&ouml;kord som skall ge t
r&auml;ff
        vid s&ouml;kning. En <img border="0" src="/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
        i rutan vid <i>Blockera s&ouml;kning</i> g&ouml;r att sidan inte kommer att ge n&aring;gon
        tr&auml;ff vid s&ouml;kningar.</td>
    </tr>
    <tr>
      <td width="100%"><b>Dela ut: </b><i>Visa dokumentet &auml;ven f&ouml;r obeh&ouml;riga
        anv&auml;ndare</i> betyder att l&auml;nken till sidan visas men de kan inte
        komma in och se sj&auml;lva sidan. <i>Dela ut dokument f&ouml;r andra
        administrat&ouml;rer</i> g&ouml;r att administrat&ouml;rerna kan l&auml;nka till denna
        sida.</td>
    </tr>
    <tr>
      <td width="100%"><b>Publicera: </b>H&auml;r anges datum och klockslag om sidan
        inte skall visas omedelbart utan vid ett senare tillf&auml;lle.</td>
    </tr>
    <tr>
      <td width="100%"><b>Arkivera: </b>H&auml;r anges det datum d&aring; sidan skall
        arkiveras. L&auml;nken kommer att tas b
ort vid det datum och klockslag som
        anges. En <img border="0" src="/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
        i rutan vid <i>Arkivera nu</i> g&ouml;r att sidan arkiveras direkt.</td>
    </tr>
    <tr>
      <td width="100%"><b>Visa: </b>H&auml;r anges var sidan skall visas.&nbsp;
        <ul>
          <li><i>Samma frame</i> betyder att sidan &ouml;ppnas i den frame d&auml;r l&auml;nken
            finns.&nbsp;</li>
          <li><i>Nytt f&ouml;nster</i> anger att sidan &ouml;ppnas i ett nytt webbl&auml;sarf&ouml;nster.&nbsp;</li>
          <li><i>Ers&auml;tt allt</i> betyder att alla frames p&aring; sidan kommer att
            ers&auml;ttas med en ny frame d&auml;r den nya sidan visas.</li>
          <li><i>Annan frame</i> - h&auml;r anges namnet p&aring; den frame d&auml;r sidan
            skall visas.</li>
        </ul>
      </td>
    </tr>
  </table>
</div>
<p align="center">&nbsp;</p>
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(78,1,'L&auml;gga till l&auml;nk till Text-dokument - sida 2
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(78,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
                <p><i>&Auml;ndra text</i> visar vilket textf&auml;lt p&aring; sidan som l&auml;ggs
                till/&auml;ndras. <i> MetaId</i> visar vilken specifik sida som till&auml;gget/&auml;ndringen
                g&ouml;rs p&aring;.</p>
                <p>Den ursprungliga texten visas i textrutan.&nbsp; I textrutan
                kan redigering g&ouml;ras, text tas bort eller l&auml;ggas till.&nbsp;</p>
                <p>H&auml;r finns valm&ouml;jlighet att antingen skriva
                vanlig text eller att anv&auml;nda HTML-kod. Genom att anv&auml;nda HTML-kod kan man sj&auml;lv
                p&aring;verka utseendet p&aring; texten ut&ouml;ver det som mallen styr.Valet g&ouml;rs genom ett
                klick i cirkeln framf&ouml;r det alternativ man vill ha.&nbsp;</p>
                <p> OBS fullst&auml;ndig HTML-kod med start- och slu
ttagg beh&ouml;ver
                inte skrivas.&nbsp;</p>
</td>
    </tr>
  </table>
  </center>
</div>
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(78,3,'<br><h3 align="center">Exempel HTML-format</h3>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(79,1,'L&auml;gga till l&auml;nk - funktion
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(79,3,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
<p align="center">Klicka p&aring; <img border="0" src="/images/se/helpimages/Pil.GIF" width="16" height="21">
f&ouml;r att f&aring; se hela listan p&aring; vilka olika typer av l&auml;nkar det g&aring;r att l&auml;gga
till.</p>
<div align="center">
<table border="1" width="75%">
  <tr>
    <td width="100%"><b>Text-dokument:</b> L&auml;gger till en l&auml;nk till en ny sida d&auml;r alla egenskaper fr&aring;n
      ursprungssidan &auml;rvs. Dessa egenskaper g&aring;r att &auml;ndra senare (om man har beh&ouml;righet att
g&ouml;ra detta).</td>
  </tr>
  <tr>
    <td width="100%"><b>URL-dokument: </b>L&auml;gger till en l&auml;nk till en
      befintlig Internet-sida, t ex Spray eller DN.</td>
  </tr>
  <tr>
    <td width="100%"><b>Browserkontroll: </b>L&auml;gger till en l&auml;nk till en sida
      som &auml;r styrd av vilken webbl&auml;sare man har. 
Olika webbl&auml;sare kan g&ouml;ra
      att en sida ser annorlunda ut. H&auml;r kan man styra vilken sida som skall
      visas.</td>
  </tr>
  <tr>
    <td width="100%"><b>HTML-dokument: </b>L&auml;gger till en l&auml;nk till en sida
      som anv&auml;ndaren sj&auml;lv f&aring;r skapa genom att skriva HTML-kod.</td>
  </tr>
  <tr>
    <td width="100%"><b>Fil: </b>L&auml;gger till en l&auml;nk till en befintlig fil som
      laddas upp i systemet.</td>
  </tr>
  <tr>
    <td width="100%"><b>Diagram: </b>L&auml;gger till en l&auml;nk till ett diagram. OBS
      till&auml;ggsmodul till imCMS.</td>
  </tr>
  <tr>
    <td width="100%"><b>Konferens: </b>L&auml;gger till en l&auml;nk till en konferens.
      OBS till&auml;ggsmodul till imCMS.</td>
  </tr>
  <tr>
    <td width="100%"><b>Befintligt dokument: </b>L&auml;gger till en l&auml;nk till en
      befintlig sida i systemet.</td>
  </tr>
    
</table>
</div align "center">
<hr>
<h2 align="center">Administr
era befintliga l&auml;nkar</h2>
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(79,4,'<p align="center">&nbsp;</p>
        <blockquote>
            <p align="left">Genom att <img border="0" src="/images/se/helpimages/Lank.h6.GIF" width="13" height="14">
            f&ouml;r en l&auml;nk kan den tas bort, arkiveras eller kopieras genom att klicka p&aring;
            respektive knapp. N&auml;r en l&auml;nk &auml;r arkiverad visas det genom att
            l&auml;nken &auml;r &ouml;verstruken (se bilden - Dokument 1). Arkiveringen kan
            tas bort via Admin-knappen "Dokinfo". F&ouml;r att sortera
            l&auml;nkarna kan siffror anges i respektive ruta framf&ouml;r l&auml;nken.
            Numreringen &auml;r helt valfri. H&ouml;gst nummer hamnar h&ouml;gst upp. Klicka
            sedan p&aring; "Sortera".&nbsp;</p>
            <p align="left">N&auml;r man klickar p&aring; "Kopiera"-knappen
            skapas ett exakt likadant dokument som det som &auml;r markerat och det
            kommer s&aring;ledes att visas tv&aring; l&auml;nkar med samma 
l&auml;nkrubrik, den
            nya l&auml;nken f&aring;r dock (2) som till&auml;gg (se bilden - Dokument 2 (2)). .</p>
            <p align="left"> F&ouml;r att &aring;terg&aring; till sidan klicka
            p&aring; "Normal".</p>
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
       values(80,1,'L&auml;gga till l&auml;nk till en fil - sida 1
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(80,2,'<h3 align="center">L&auml;gga till dokument</h3>
<div align="center">
  <table border="1" width="75%">
    <tr>
      <td width="100%"><b>Rubrik:</b> Den text som kommer att vara sj&auml;lva l&auml;nktexten.</td>
    </tr>
    <tr>
      <td width="100%"><b>Text:</b> F&ouml;rklarande text som visas vid l&auml;nken (OBS
        visas endast om utseendemallen till&aring;ter detta). Om ingen f&ouml;rklarande
        text skall visa l&auml;mnas f&auml;ltet tomt.</td>
    </tr>
    <tr>
      <td width="100%"><b>Bild:</b> H&auml;r anges s&ouml;kv&auml;gen till den bild som
        skall visas som en ikon under l&auml;nken och den f&ouml;rklarande texten (OBS
        visas endast om utseendemallen till&aring;ter detta). Om ingen bild skall
        visas l&auml;mnas f&auml;ltet tomt.</td>
    </tr>
  </table>
  <h3 align="center">Avancerat</h3>
  <table border="1" width="75%">
    <tr>
      <td width="100%"><b>S&ouml;kord: </b>H&auml;r anges de s&ouml;kord som skall ge t
r&auml;ff
        vid s&ouml;kning. En <img border="0" src="/images/se/helpimages/Lank-U1.GIF" width="13" height="14">
        i rutan vid <i>Blockera s&ouml;kning</i> g&ouml;r att sidan inte kommer att ge n&aring;gon
        tr&auml;ff vid s&ouml;kningar.</td>
    </tr>
    <tr>
      <td width="100%"><b>Dela ut: </b><i>Visa dokumentet &auml;ven f&ouml;r obeh&ouml;riga
        anv&auml;ndare</i> betyder att l&auml;nken till sidan visas men de kan inte
        komma in och se sj&auml;lva sidan. <i>Dela ut dokument f&ouml;r andra
        administrat&ouml;rer</i> g&ouml;r att administrat&ouml;rerna kan l&auml;nka till denna
        sida.</td>
    </tr>
    <tr>
      <td width="100%"><b>Publicera: </b>H&auml;r anges datum och klockslag om sidan
        inte skall visas omedelbart utan vid ett senare tillf&auml;lle.</td>
    </tr>
    <tr>
      <td width="100%"><b>Arkivera: </b>H&auml;r anges det datum d&aring; sidan skall
        arkiveras. L&auml;nken kommer att tas b
ort vid det datum och klockslag som
        anges. En <img border="0" src="/images/se/helpimages/Lank-U1.GIF" width="13" height="14">
        i rutan vid <i>Arkivera nu</i> g&ouml;r att sidan arkiveras direkt.</td>
    </tr>
    <tr>
      <td width="100%"><b>Visa: </b>H&auml;r anges var sidan skall visas.&nbsp;
        <ul>
          <li><i>Samma frame</i> betyder att sidan &ouml;ppnas i den frame d&auml;r l&auml;nken
            finns.&nbsp;</li>
          <li><i>Nytt f&ouml;nster</i> anger att sidan &ouml;ppnas i ett nytt webbl&auml;sarf&ouml;nster.&nbsp;</li>
          <li><i>Ers&auml;tt allt</i> betyder att alla frames p&aring; sidan kommer att
            ers&auml;ttas med en ny frame d&auml;r den nya sidan visas.</li>
          <li><i>Annan frame</i> - h&auml;r anges namnet p&aring; den frame d&auml;r sidan
            skall visas.</li>
        </ul>
      </td>
    </tr>
  </table>
</div>
<p align="center">&nbsp;</p>
<p>&nbsp;</p>
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(81,1,'L&auml;gga till l&auml;nk till URL-dokument - sida 1
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(81,2,'<h3 align="center">L&auml;gga till dokument</h3>
<div align="center">
  <table border="1" width="75%">
    <tr>
      <td width="100%"><b>Rubrik:</b> Den text som kommer att vara sj&auml;lva l&auml;nktexten.</td>
    </tr>
    <tr>
      <td width="100%"><b>Text:</b> F&ouml;rklarande text som visas vid l&auml;nken om utseendemallen till&aring;ter
        detta. Om ingen f&ouml;rklarande text skall visa l&auml;mnas f&auml;ltet tomt.</td>
    </tr>
    <tr>
      <td width="100%"><b>Bild:</b> H&auml;r anges s&ouml;kv&auml;gen till den bild som
        skall visas som en ikon under l&auml;nken och den f&ouml;rklarande texten (OBS
        visas endast om utseendemallen till&aring;ter detta). Om ingen bild skall
        visas l&auml;mnas f&auml;ltet tomt.</td>
    </tr>
  </table>
  <h3 align="center">Avancerat</h3>
  <table border="1" width="75%">
    <tr>
      <td width="100%"><b>S&ouml;kord: </b>H&auml;r anges de s&ouml;kord som skall ge tr&auml;ff
        vid s&oum
l;kning. En <img border="0" src="/images/se/helpimages/Lank-U1.GIF" width="13" height="14">
        i rutan vid <i>Blockera s&ouml;kning</i> g&ouml;r att sidan inte kommer att ge n&aring;gon
        tr&auml;ff vid s&ouml;kningar.</td>
    </tr>
    <tr>
      <td width="100%"><b>Dela ut: </b><i>Visa dokumentet &auml;ven f&ouml;r obeh&ouml;riga
        anv&auml;ndare</i> betyder att l&auml;nken till sidan visas men de kan inte
        komma in och se sj&auml;lva sidan. <i>Dela ut dokument f&ouml;r andra
        administrat&ouml;rer</i> g&ouml;r att administrat&ouml;rerna kan l&auml;nka till denna
        sida.</td>
    </tr>
    <tr>
      <td width="100%"><b>Publicera: </b>H&auml;r anges datum och klockslag om sidan
        inte skall visas omedelbart utan vid ett senare tillf&auml;lle.</td>
    </tr>
    <tr>
      <td width="100%"><b>Arkivera: </b>H&auml;r anges det datum d&aring; sidan skall
        arkiveras. L&auml;nken kommer att tas bort vid det datum och klocks
lag som
        anges. En <img border="0" src="/images/se/helpimages/Lank-U1.GIF" width="13" height="14">
        i rutan vid <i>Arkivera nu</i> g&ouml;r att sidan arkiveras direkt.</td>
    </tr>
    <tr>
      <td width="100%"><b>Visa: </b>H&auml;r anges var sidan skall visas.&nbsp;
        <ul>
          <li><i>Samma frame</i> betyder att sidan &ouml;ppnas i den frame d&auml;r l&auml;nken
            finns.&nbsp;</li>
          <li><i>Nytt f&ouml;nster</i> anger att sidan &ouml;ppnas i ett nytt webbl&auml;sarf&ouml;nster.&nbsp;</li>
          <li><i>Ers&auml;tt allt</i> betyder att alla frames p&aring; sidan kommer att
            ers&auml;ttas med en ny frame d&auml;r den nya sidan visas.</li>
          <li><i>Annan frame</i> - h&auml;r anges namnet p&aring; den frame d&auml;r sidan
            skall visas.</li>
        </ul>
      </td>
    </tr>
  </table>
</div>

<p align="center"><b>N&auml;r man klickat p&aring; "OK" visas den sida d&auml;r
sj&auml;lva Inte
rnet-adressen skrivs in.</b></p>
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(82,1,'L&auml;gga till l&auml;nk till URL-dokument - sida 2
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(82,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="450">
    <tr>
      <td>
   
<blockquote>
   
<p align="left">H&auml;r anges hela Internet-adressen till den sida som skall
visas.</p>
</blockquote>
                  <ul>
                    <li>
                      <p align="left"><i>Samma frame</i> betyder att sidan &ouml;ppnas i den frame d&auml;r l&auml;nken
            finns.&nbsp;</li>
                  </ul>
                  <ul>
                    <li>
                      <p align="left"><i>Nytt f&ouml;nster</i> anger att sidan &ouml;ppnas i ett nytt webbl&auml;sarf&ouml;nster.&nbsp;</li>
                  </ul>
                  <ul>
                    <li>
                      <p align="left"><i>Ers&auml;tt allt</i> betyder att alla frames p&aring; sidan kommer att
            ers&auml;ttas med en ny frame d&auml;r den nya sidan visas.</li>
                  </ul>
                  <ul>
              
      <li>
                      <p align="left"><i>Annan frame</i> - h&auml;r anges namnet p&aring; den frame d&auml;r sidan
            skall visas.</li>
                  </ul>
   
      </td>
    </tr>
  </table>
  </center>
</div>
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(83,1,'Misslyckad inloggning
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(83,2,'<p align="center">Skriv in anv&auml;ndarnamn och l&ouml;senord igen.</p>
<p align="center">Om du gl&ouml;mt l&ouml;senordet - klicka p&aring; den bl&aring; l&auml;nken.</p>
<p align="center">Om du gl&ouml;mt anv&auml;ndarnamnet - ta kontakt med
systemadministrat&ouml;ren.</p>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(84,1,'R&auml;ttigheter
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(84,2,'<p align="center"> H&auml;r styrs vilken/vilka roller som skall f&aring; g&ouml;ra vad med
sidan. </p>
<div align="CENTER">
<table border="1" width="100%">
  <tr>
    <td width="100%"><b> Rubrik: </b> Visar dokumentets rubrik (l&auml;nktexten).
      Genom att <img border="0" src="/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
      f&ouml;r <i> Visa rubrik &auml;ven om anv&auml;ndaren &auml;r obeh&ouml;rig </i> till&aring;ts
      obeh&ouml;riga att se l&auml;nken men kan inte komma in p&aring; sj&auml;lva sidan.</td>
  </tr>
  <tr>
    <td width="100%"><b> Beh&ouml;righet: </b> Visar vilka roller som har n&aring;gon form
      av r&auml;ttighet p&aring; sidan. 
      <ul>
        <li><i> Ingen </i> betyder att l&auml;nken inte visas f&ouml;r rollen, om inte <i> Visa
          rubrik &auml;ven om anv&auml;ndaren &auml;r obeh&ouml;rig </i>&auml;r <img border="0" src="/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">.
        </li>
        <li><i> L
&auml;sa </i> betyder att anv&auml;ndare med rollen kan l&auml;sa allt p&aring;
          sidan, men inte kan &auml;ndra n&aring;got p&aring; den.</li>
        <li><i> Begr.2 </i> betyder att anv&auml;ndare med rollen kan g&ouml;ra de saker
          som Begr.2 till&aring;ts g&ouml;ra. F&ouml;r att &auml;ndra r&auml;ttigheter f&ouml;r Begr.2 se
          <b> Definiera beh&ouml;righeter.</b></li>
        <li><i> Begr.1 </i> betyder att anv&auml;ndare med rollen kan g&ouml;ra de saker
          som Begr.1 till&aring;ts g&ouml;ra. F&ouml;r att &auml;ndra r&auml;ttigheter f&ouml;r Begr.2 se
          <b> Definiera beh&ouml;righeter.</b><i> </i></li>
        <li><i> Full </i> betyder att anv&auml;ndare med den rollen har fullst&auml;ndiga
          r&auml;ttigheter p&aring; sidan och kan &auml;ndra allting.</li>
      </ul>
      <p> R&auml;ttigheterna &auml;ndras genom att klicka i den vita cirkeln f&ouml;r
      l&auml;mplig r&auml;ttighet. Om Ingen markeras kommer rollen att 
flyttas ned till <i> Roller
      utan beh&ouml;righet </i> n&auml;r man klickar p&aring; "Spara".</p>
    </td>
  </tr>
  <tr>
    <td width="100%">
      <p align="left"><b> Roller utan beh&ouml;righet: </b> Genom att markera en roll
      och klicka p&aring; "L&auml;gg till" kan fler roller ges r&auml;ttigheter p&aring;
      sidan. Rollen flyttas upp till <b> Beh&ouml;righet </b> och r&auml;ttigheten kan
      markeras.</p>
    </td>
  </tr>
  <tr>
    <td width="100%"><b> Definiera beh&ouml;righet: </b> Genom att klicka p&aring;
      "Definiera" vid antingen <i> Begr&auml;nsad beh&ouml;righet 1 </i> eller <i> Begr&auml;nsad
      beh&ouml;righet 2 </i> f&aring;s den sida upp d&auml;r dessa r&auml;ttigheter kan &auml;ndras.
      En <img border="0" src="/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
      betyder att Begr&auml;nsad 1 &auml;r mer priviligerad &auml;n Begr&auml;nsad 2 f&ouml;r den
      h&auml;r sidan och därmed kan &auml;ndra r&auml;tt
igheter f&ouml;r Begr&auml;nsad 2.
      "Definiera f&ouml;r nya dokument" betyder att r&auml;ttigheterna st&auml;lls
      in f&ouml;r sidor som skapas fr&aring;n aktuell sida. Default &auml;r att de &auml;rver de
      r&auml;ttigheter som ursprungssidan har. </td>
  </tr>
  <tr>
    <td width="100%"><b> Skapad av: </b>(namnet p&aring; den anv&auml;ndare som skapat
      sidan). Genom en <img border="0" src="/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
      vid <i> Dela ut dokumentet </i> ges m&ouml;jlighet f&ouml;r andra anv&auml;ndare att
      l&auml;nka till sidan fr&aring;n "sina" dokument.</td>
  </tr>
</table>
</div align="CENTER">
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(85,1,'Ta bort ett dokument
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(85,2,'<div align="center">
  <center>
  <table border="0" width="480">
    <tr>
      <td>
<p align="left">Skriv in MetaId f&ouml;r det dokument som skall tas bort. Klicka
p&aring; "Ta bort" och en kontrollruta kommer upp innan dokumentet raderas fr&aring;n databasen.&nbsp;</p>
<p align="left">"Tillbaka" leder till f&ouml;reg&aring;ende sida.</p>

      </td>
    </tr>
  </table>
  </center>
</div>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(86,1,'Ta bort ett dokument - varning
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(86,2,'<p align="center">Klicka p&aring; &quot;OK&quot; om du &auml;r s&auml;ker p&aring; att du vill ta
bort dokumentet.</p>
<p align="center">&quot;Cancel&quot; leder tillbaka till f&ouml;reg&aring;ende sida utan
att dokumentet tas bort.</p>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(87,1,'&Auml;ndra utseende p&aring; dokumentet
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(87,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
            <p align="left">Aktuell formatgrupp och mall visas. F&ouml;r att &auml;ndra
            utseendet - klicka p&aring; <img border="0" src="/images/se/helpimages/Pil.GIF" width="16" height="21">
            vid format-mallen. Om mallen som du vill ha ligger i en annan
            formatgrupp m&aring;ste du f&ouml;rst klicka p&aring; <img border="0" src="/images/se/helpimages/Pil.GIF" width="16" height="21">
            vid formatgrupp, v&auml;lja ny formatgrupp och klicka p&aring; "&Auml;ndra
            grupp". De mallar som tillh&ouml;r den nu valda formatgruppen visas
            vid klick p&aring; <img border="0" src="/images/se/helpimages/Pil.GIF" width="16" height="21">
            vid formatmall. Markera formatmallen och klicka sedan p&aring;
            "Spara". F&ouml;r att se hur mallen ser ut, klicka p&aring;
            "Visa mall". OBS
 fungerar bara om det finns en exempelmall
            kopplad till denna mall.</p>
      </td>
    </tr>
  </table>
  </center>
</div>
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(88,1,'Ta bort roll - varning
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(88,2,'Om du &auml;r s&auml;ker p&aring; att du vill ta bort rollen - klicka p&aring; &quot;OK&quot;, annars klicka p&aring; &quot;Avbryt&quot;.
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(89,1,'Administrera roller - huvudsida
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(89,2,'<div align="center">
  <center>
  <table border="0" width="325">
    <tr>
      <td>
<p align="left">Genom att klicka p&aring; respektive knapp kan man:
<li>L&auml;gga till en roll</li>
<li>Byta namn p&aring; en roll</li>
<li>Redigera en roll</li>
<li>Ta bort en roll</li>


<p align="left">"Avbryt" leder tillbaka till f&ouml;reg&aring;ende sida".</p>

      </td>
    </tr>
  </table>
  </center>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(90,1,'L&auml;gga till l&auml;nk - sida 1
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(90,2,'<h3 align="center">L&auml;gga till dokument</h3>
<div align="center">
  <table border="1" width="75%">
    <tr>
      <td width="100%"><b>Rubrik:</b> Den text som kommer att vara sj&auml;lva l&auml;nktexten.</td>
    </tr>
    <tr>
      <td width="100%"><b>Text:</b> F&ouml;rklarande text som visas vid l&auml;nken (OBS
        visas endast om utseendemallen till&aring;ter detta). Om ingen f&ouml;rklarande
        text skall visa l&auml;mnas f&auml;ltet tomt.</td>
    </tr>
    <tr>
      <td width="100%"><b>Bild:</b> H&auml;r anges s&ouml;kv&auml;gen till den bild som
        skall visas som en ikon under l&auml;nken och den f&ouml;rklarande texten (OBS
        visas endast om utseendemallen till&aring;ter detta). Om ingen bild skall
        visas l&auml;mnas f&auml;ltet tomt.</td>
    </tr>
  </table>
  <h3 align="center">Avancerat</h3>
  <table border="1" width="75%">
    <tr>
      <td width="100%"><b>S&ouml;kord: </b>H&auml;r anges de s&ouml;kord som skall ge t
r&auml;ff
        vid s&ouml;kning. En <img border="0" src="/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
        i rutan vid <i>Blockera s&ouml;kning</i> g&ouml;r att sidan inte kommer att ge n&aring;gon
        tr&auml;ff vid s&ouml;kningar.</td>
    </tr>
    <tr>
      <td width="100%"><b>Dela ut: </b><i>Visa dokumentet &auml;ven f&ouml;r obeh&ouml;riga
        anv&auml;ndare</i> betyder att l&auml;nken till sidan visas men de kan inte
        komma in och se sj&auml;lva sidan. <i>Dela ut dokument f&ouml;r andra
        administrat&ouml;rer</i> g&ouml;r att administrat&ouml;rerna kan l&auml;nka till denna
        sida.</td>
    </tr>
    <tr>
      <td width="100%"><b>Publicera: </b>H&auml;r anges datum och klockslag om sidan
        inte skall visas omedelbart utan vid ett senare tillf&auml;lle.</td>
    </tr>
    <tr>
      <td width="100%"><b>Arkivera: </b>H&auml;r anges det datum d&aring; sidan skall
        arkiveras. L&auml;nken kommer att tas b
ort vid det datum och klockslag som
        anges. En <img border="0" src="/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
        i rutan vid <i>Arkivera nu</i> g&ouml;r att sidan arkiveras direkt.</td>
    </tr>
    <tr>
      <td width="100%"><b>Visa: </b>H&auml;r anges var sidan skall visas. 
        <ul>
          <li><i>Samma frame</i> betyder att sidan &ouml;ppnas i den frame d&auml;r l&auml;nken
            finns. </li>
          <li><i>Nytt f&ouml;nster</i> anger att sidan &ouml;ppnas i ett nytt webbl&auml;sarf&ouml;nster. </li>
          <li><i>Ers&auml;tt allt</i> betyder att alla frames p&aring; sidan kommer att
            ers&auml;ttas med en ny frame d&auml;r den nya sidan visas.</li>
          <li><i>Annan frame</i> - h&auml;r anges namnet p&aring; den frame d&auml;r sidan
            skall visas.</li>
        </ul>
      </td>
    </tr>
  </table>
</div>
<p align="center"><b>
Efter det att man fyllt i denna sida och man klickat "Ok", komm
er n&auml;sta inst&auml;llningssida upp. Vad den inneh&aring;ller beror p&aring; vilken typ av l&auml;nk man valt att l&auml;gga till.<br><br>

<i>Klicka p&aring; hj&auml;lpknappen p&aring; n&auml;sta sida f&ouml;r att se hj&auml;lp om denna funktion.</i></b></p>
 
',1)
 
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
<BR>To move a file from a directory to another directory,  select the file in the lefthand box, mark the file and in the righthand box find the directory where you wish to move the file and mark that directory. Click thereafter on &quot;Move -&gt;&quot;. You can as well do this the other way around and copy from the right to
 t
he
 l
ef
t box. Click instead on &quot;&lt;- Move&quot;.
<BR>
<BR>To change the name of a directory or file, mark the directory or file in question. Enter the new name in the field &quot;New name:&quot;. Click thereafter on &quot;Change name&quot; on the left or right depending if the directory or file is marked in the lefthand or righthand box.
<BR>
<BR>To create a new directory, mark the directory under which the new directory should be located. Enter the name in the field &quot;New name:&quot;. Click thereafter on &quot;New name&quot; on the left or right depending if the directory is marked in the lefthand or righthand box.
<BR>
<BR>To delete a directory or file, mark that directory or file and then click on &quot;Delete&quot;. The botton on the left should be used if the directory or file is marked in the lefthand box and rhet right button should be used if the directory or file is marked in the righthand box. A warning showing the complete search root to the director
y or
 fil
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
       values(211,1,'Create template directory
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(211,2,'<center> Enter the name of the new directory. Click thereafter on "Create".</p>

"Back" takes you to Administrate templates/template directories.
</center>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(212,1,'Delete a template directory
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(212,2,'Select the template directory you wish to remove by marking it in the roll-down menu. Click thereafter on &quot;Delete&quot;. <BR>&quot;Back&quot; takes you to Administrate templates/template directories.
<BR>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(213,1,'Delete template directory - Warning
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(213,2,'Warning that a directory is about to be removed. The picture shows the templates that are connected to the directory that is to be removed.<BR>
<BR> If you wish to remove the directory and not lose these templates, move the named templates to another directory first (if they do not already belong to another directory). Do this by canceling this directory removal and going to the Add/Remove Template function. 
<BR>
<BR> Click thereafter on &quot;OK&quot;.
<BR>
<BR> If you do not want to remove a directory click on &quot;Cancel&quot;.
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
       values(216,1,'Delete IP access - Warning
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(216,2,'To remove an IP access - click on &quot;OK&quot;, to return to the previous page - click on &quot;Cancel&quot;.
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
       values(225,1,'Delete a design template
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(225,2,'Select the template you wish to remove by marking it in the roll-down menu. Between the parentheses [ ] is the number of pages using the template.
<BR>
<BR> By clicking on &quot;Delete&quot; the template is removed. Before the template is removed a page appears allowing you to select another template for the pages using the template to be removed.
<BR>
<BR>&quot;Back&quot; takes you to the previous page.
<BR>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(226,1,'Delete a design template - Warning!
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(226,2,'Select the alternative template that the affected pages should use once you remove the template. Select this new template by marking it in the roll-down menu. NB: All these pages will be assigned the same template.<BR>
<BR> Click thereafter on &quot;OK&quot;.
<BR>
<BR>&quot;Cancel&quot; takes you back to the previous page.
<BR>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(227,1,'Show design templates
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(227,2,'On the lefthand side of the picture you see all of the templates available. The number of pages using each template is displayed between parentheses [ ]. Mark the template and click on &quot;List pages&quot;. The pages are listed on the righthand side of the picture. By marking a page and then clicking on &quot;Show page&quot; you will open the page in question.&nbsp;
<BR>&nbsp;
<BR>&quot;Back&quot; takes you to Administrate templates/template directories.
<BR>
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
       values(234,1,'Delete a role - Warning!
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(234,2,'The users who are a member of the role you wish to remove are displayed here(the first 50). If no users are displayed the role has no members.&nbsp;
<BR>
<BR> You can see as well those pages where the role has been given page authority. removing the role may mean that these pages will no longer be accessible to the removed role´s members(if they do not have another role giving them access to these pages).
<BR>
<BR> To remove the role, click on &quot;OK&quot;. If you do not wish to remove the role, click on &quot;Cancel&quot;.
<BR>
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
       values(237,1,'Show all pages
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(237,2,'
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(237,3,'First, select the page range to be displayed by selecting the first number to be listed. The default setting is always the first available page. You can as well select the interval to be displayed. Finally push on the &quot;List&quot; button to display the numerical list of pages according to the start page and interval size you requested.
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(237,4,'All the pages in the interval are displayed with their sub-pages (pages linked from that page). If you click on a page that is preceded by a &#9679; the page will be shown. Click on a page preceded with a &deg; the list with that Meta ID at the top is displayed. It is then possible to inspect the links on the page and see if these lisked pages have sub-pages themselves. Sub-pages preceded by a &#9679; can be clicked on to display the actual page.
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(238,1,'Change user preferences
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(238,2,'User category- Select a category and click on "Show users" to display the users in this category.<br><br>

<li> Anonymous users: Users who log into the system with IP number and without identifying themselves.</li>
<li> Authenticated users: Users who log into the system with user name and password.</li>
<li> Conference users: Users who register themselves into the system.</li>

<br><br> To register a new user - click on "Add user".

<br><br> To edit a user - click on the user to mark that user and then click on "Edit".

To return to the previous page push on "Back".
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(239,1,'Change page information
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(239,2,'<h3 align="center"> Basic page information and settings can be edited here </h3>
<div align="center">
  <table border="1" width="75%">
    <tr>
      <td width="100%"><b> Header:</b> This text is the main link in itself.</td>
    </tr>
    <tr>
      <td width="100%"><b> Text:</b> Additional description shown by the link.(NB!
        This text is only displayed if the design template in use permits link texts). If you do not want any description of the link, leave this box blank.</td>
    </tr>
    <tr>
      <td width="100%"><b> Image icon:</b> Here is where you enter the path to the thumbnail image or icon symbol which is already loaded into the system such as "/images/picture1.gif" to the image to be displayed.  (NB!
        This image is only displayed if the design template in use permits link image icons). If you do not wish to have a small image by the link, leave this box blank.</td>
    </tr>
  </table>
  <h3 align="center"> Advanced </h3>
  <table border="1" w


idth="75%">
    <tr>
      <td width="100

%"><b> Key words (for searches): </b> These are additional words or codes (which are not already found on the page) that will help find this page in a site search. A <img border="0" src="/imcode/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
        in the checkbox <i> Block searches from finding this page </i> prevents the page from ever turning up in local site searches.
    </tr>
    <tr>
      <td width="100%"><b> Share: </b><i> Show this page link to unauthorised users
</i> means that this link can be seen by persons without the authority to view the page.  They will be denied access to the page if they select the link. <i> Allow unauthorised local links to this page </i> means that other web administrators in your system lacking editing rights on  this page are allowed to create links to this page.</td>
    </tr>
    <tr>
      <td width="100%"><b> Publish from: </b> A future time and date can be given as publi
sh
in
g date if the page is not to be published now.</td>
 
  
 </tr>
    <tr>
      <td width="100%"><b> To the archives: </b> A future time and date can be given as the date when this page is to be archived. The links to this page will be on the given date. A <img border="0" src="/imcode/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
        in the checkbox <i> Archive now </i> means that the page will be archived immediately.</td>
    </tr>
    <tr>
      <td width="100%"><b> Display in: </b> Here is where you can control how the page is displayed.
        <ul>
          <li><i> Same frame </i> means that the page will be opened in the same frame as where the link is found. </li>
          <li><i> New window </i> means that the page will be opened in a new browser window. </li>
          <li><i> Full window </i> means that the page will be opened in a new frame which replaces all other frames (if there are more than one)in the current window.</li>
        
  <l
i><i
> Other frame </i> - if a frameset is being used here you can control 
in w
hich frame the page will appear.</li>
        </ul>
      </td>
    </tr>
  </table>
</div>
<p align="center"><b>
<br><br></p>
 
 
 
 
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(240,1,'Rights for Dynamic Authority 1
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(240,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
<p align="center"></p>
                <p align="left"> By ticking one or more boxes below and pushing "OK", all members of roles having the <i> Dynamic Authority  1 </i> level of authority on this page will be granted these rights on this specific page.
</p>
        <p>&nbsp;</td>
    </tr>
  </table>
  </center>
</div>
<div align="CENTER">
                <table border="1" width="75%">
                  <tr>
                    <td width="100%"><b> Right to edit page information: </b> This means that the role-member has the right to change header(main link text), additional descriptive link text and the icon image symbol by the links.</td>
                  </tr>
                  <tr>
                    <td width="100%"><b> Right to edit advanced page  information: </b> This means that the role-member has the right to change header(main link text),


 additional descriptive link text, icon image by the links PLUS key words for searches,
                      date of publication and archivation and permit unauthorised linking to the page by other site administrators.</td>
                  </tr>
                  <tr>
                    <td width="100%"><b> Right to change rights for roles: </b> This means that the role-member has the right to change, add, remove rights and roles on this page. </td>
                  </tr>
                  <tr>
                    <td width="100%"><b> Right to edit text: </b> This means that the role-member has the right to add, remove and edit texts on the  page.</td>
                  </tr>
                  <tr>
                    <td width="100%"><b> Right to edit images
: </b> This means that the role-member has the right to add, remove and edit images on the  page.</td>
                  </tr>
                  <tr>
                    <td width="100%"><b> Right to edit li
nk
s

: </b> This means that the role-member has the right to create links and thus new sub-pages to the  page. Permitted document types that can be created are determined by those marked in this list.
                      (Click on the type, for more than one name hold down the "Ctrl" key while clicking on the types).</td>
                  </tr>
                  <tr>
                    <td width="100%"><b> Right to switch templates: </b> This means that the role-member can change the graphic appearance of the page. Permitted template directories that can be accessed are those marked in the roll-down menu
                      (Click on the name, for more than one name hold down the "Ctrl" key while clicking on the names).</td>
                  </tr>
                </table>
</div align="CENTER">
 
 
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(241,1,'Rights for Dynamic Authority 1, for new sub-pages
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(241,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
<p align="center">
      "Define for new sub-pages" means that the authority of pages created from this page can be predefined.</p>
                <p align="left"> By ticking one or more boxes below and pushing "OK", all members of roles having the <i> Dynamic Authority  1 </i> level of authority on new sub-pages created from this page will be granted these rights on those pages from now on.
</p>
        <p>&nbsp;</td>
    </tr>
  </table>
  </center>
</div>
<div align="CENTER">
                <table border="1" width="75%">
                  <tr>
                    <td width="100%"><b> Right to edit page information: </b> This means that the role-member has the right to change header(main link text), additional descriptive link text and the icon image symbol by the links.</td>
                  </tr>
                  <tr>
                    <td w


idth="100%"><b> Right to edit advanced page  information: </b> This means that the role-member has the right to change header(main link text), additional descriptive link text, icon image by the links PLUS key words for searches,
                      date of publication and archivation and permit unauthorised linking to the page by other site administrators.</td>
                  </tr>
                  <tr>
                    <td width="100%"><b> Right to change rights for roles: </b> This means that the role-member has the right to change, add, remove rights and roles on this page. </td>
                  </tr>
                  <tr>
                    <td width="100%"><b> Right to edit text: </b> This means that the role-member has the right to add, remove and edit texts on the  page.</td>
                  </tr>
                  <tr>
                    <td width="100%"><b> Right to edit images
: </b> This means that the role-member has the right to add, remove 
an
d 
edit images on the  page.</td>
                  </tr>
                  <tr>
                    <td width="100%"><b> Right to edit links
: </b> This means that the role-member has the right to create links and thus new sub-pages to the  page. Permitted document types that can be created are determined by those marked in this list.
                      (Click on the type, for more than one name hold down the "Ctrl" key while clicking on the types).</td>
                  </tr>
                  <tr>
                    <td width="100%"><b> Right to switch templates: </b> This means that the role-member can change the graphic appearance of the page. Permitted template directories that can be accessed are those marked in the roll-down menu
                      (Click on the name, for more than one name hold down the "Ctrl" key while clicking on the names).</td>
                  </tr>
                </table>
</div align="CENTER">
 
 
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(242,1,'Rights for Dynamic Authority 2
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(242,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
<p align="center"></p>
                <p align="left"> By ticking one or more boxes below and pushing "OK", all members of roles having the <i> Dynamic Authority  2 </i> level of authority on this page will be granted these rights on this specific page.
</p>
        <p>&nbsp;</td>
    </tr>
  </table>
  </center>
</div>
<div align="CENTER">
                <table border="1" width="75%">
                  <tr>
                    <td width="100%"><b> Right to edit page information: </b> This means that the role-member has the right to change header(main link text), additional descriptive link text and the icon image symbol by the links.</td>
                  </tr>
                  <tr>
                    <td width="100%"><b> Right to edit advanced page  information: </b> This means that the role-member has the right to change header(main link text),


 additional descriptive link text, icon image by the links PLUS key words for searches,
                      date of publication and archivation and permit unauthorised linking to the page by other site administrators.</td>
                  </tr>
                  <tr>
                    <td width="100%"><b> Right to change rights for roles: </b> This means that the role-member has the right to change, add, remove rights and roles on this page. </td>
                  </tr>
                  <tr>
                    <td width="100%"><b> Right to edit text: </b> This means that the role-member has the right to add, remove and edit texts on the  page.</td>
                  </tr>
                  <tr>
                    <td width="100%"><b> Right to edit images
: </b> This means that the role-member has the right to add, remove and edit images on the  page.</td>
                  </tr>
                  <tr>
                    <td width="100%"><b> Right to edit li
nk
s

: </b> This means that the role-member has the right to create links and thus new sub-pages to the  page. Permitted document types that can be created are determined by those marked in this list.
                      (Click on the type, for more than one name hold down the "Ctrl" key while clicking on the types).</td>
                  </tr>
                  <tr>
                    <td width="100%"><b> Right to switch templates: </b> This means that the role-member can change the graphic appearance of the page. Permitted template directories that can be accessed are those marked in the roll-down menu
                      (Click on the name, for more than one name hold down the "Ctrl" key while clicking on the names).</td>
                  </tr>
                </table>
</div align="CENTER">
 
 
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(243,1,'Rights for Dynamic Authority 2, for new sub-pages  
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(243,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
<p align="center">
      "Define for new sub-pages" means that the authority of pages created from this page can be predefined.</p>
                <p align="left"> By ticking one or more boxes below and pushing "OK", all members of roles having the <i> Dynamic Authority  2 </i> level of authority on new sub-pages created from this page will be granted these rights on those pages from now on.
</p>
        <p>&nbsp;</td>
    </tr>
  </table>
  </center>
</div>
<div align="CENTER">
                <table border="1" width="75%">
                  <tr>
                    <td width="100%"><b> Right to edit page information: </b> This means that the role-member has the right to change header(main link text), additional descriptive link text and the icon image symbol by the links.</td>
                  </tr>
                  <tr>
                    <td w


idth="100%"><b> Right to edit advanced page  information: </b> This means that the role-member has the right to change header(main link text), additional descriptive link text, icon image by the links PLUS key words for searches,
                      date of publication and archivation and permit unauthorised linking to the page by other site administrators.</td>
                  </tr>
                  <tr>
                    <td width="100%"><b> Right to change rights for roles: </b> This means that the role-member has the right to change, add, remove rights and roles on this page. </td>
                  </tr>
                  <tr>
                    <td width="100%"><b> Right to edit text: </b> This means that the role-member has the right to add, remove and edit texts on the  page.</td>
                  </tr>
                  <tr>
                    <td width="100%"><b> Right to edit images
: </b> This means that the role-member has the right to add, remove 
an
d 
edit images on the  page.</td>
                  </tr>
                  <tr>
                    <td width="100%"><b> Right to edit links
: </b> This means that the role-member has the right to create links and thus new sub-pages to the  page. Permitted document types that can be created are determined by those marked in this list.
                      (Click on the type, for more than one name hold down the "Ctrl" key while clicking on the types).</td>
                  </tr>
                  <tr>
                    <td width="100%"><b> Right to switch templates: </b> This means that the role-member can change the graphic appearance of the page. Permitted template directories that can be accessed are those marked in the roll-down menu
                      (Click on the name, for more than one name hold down the "Ctrl" key while clicking on the names).</td>
                  </tr>
                </table>
</div align="CENTER">
 
 
 
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
       values(247,2,'<h3 align="center"> Create a page </h3>
<div align="center">
  <table border="1" width="75%">
    <tr>
      <td width="100%"><b> Header:</b> This text will be the main link in itself.</td>
    </tr>
    <tr>
      <td width="100%"><b> Text:</b> Additional description shown by the link.(NB!
        This text is only displayed if the design template in use permits link texts). If you do not want any description of the link, leave this box blank.</td>
    </tr>
    <tr>
      <td width="100%"><b> Image icon:</b> Here is where you enter the path to the thumbnail image or icon symbol which is already loaded into the system such as "/images/picture1.gif" to the image to be displayed.  (NB!
        This image is only displayed if the design template in use permits link image icons). If you do not wish to have a small image by the link, leave this box blank.</td>
    </tr>
  </table>
  <h3 align="center"> Advanced </h3>
  <table border="1" width="75%">
    <tr>
      <td wid


th="100

%"><b> Key words (for searches): </b> These are additional words or codes (which are not already found on the page) that will help find this page in a site search. A <img border="0" src="/imcode/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
        in the checkbox <i> Block searches from finding this page </i> prevents the page from ever turning up in local site searches.
    </tr>
    <tr>
      <td width="100%"><b> Share: </b><i> Show this page link to unauthorised users
</i> means that this link can be seen by persons without the authority to view the page.  They will be denied access to the page if they select the link. <i> Allow unauthorised local links to this page </i> means that other web administrators in your system lacking editing rights on  this page are allowed to create links to this page.</td>
    </tr>
    <tr>
      <td width="100%"><b> Publish from: </b> A future time and date can be given as publishing date if the page is not to be 
pu
bl
ished now.</td>
 
  
 </tr>
    <tr>
      <td width="100%"><b> To the archives: </b> A future time and date can be given as the date when this page is to be archived. The links to this page will be on the given date. A <img border="0" src="/imcode/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
        in the checkbox <i> Archive now </i> means that the page will be archived immediately.</td>
    </tr>
    <tr>
      <td width="100%"><b> Display in: </b> Here is where you can control how the page is displayed.
        <ul>
          <li><i> Same frame </i> means that the page will be opened in the same frame as where the link is found. </li>
          <li><i> New window </i> means that the page will be opened in a new browser window. </li>
          <li><i> Full window </i> means that the page will be opened in a new frame which replaces all other frames (if there are more than one)in the current window.</li>
          <li><i> Other frame </i> - if a fr
ames
et i
s being used here you can control 
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
          <p class="MsoBodyText" style="text-align:just
if
y"
>I
f 
no symbol or arrows appear when you push "image", the cuttenr template does not support adding images.  Images that do not get arrows next to them in this mode are embedded in the template. In this case you must switch templates if images are to be added.</p>
          <h4 style="text-align:justify">Links</h4>
          <p style="text-align:justify">When you click on "<b style="mso-bidi-font-weight:normal">Links</b>" a link creation roll-down menu will appear whereever and if dynamic links are possible on the page. It is possible to make the following types of links:</p>
          <blockquote>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span>Text document</p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&n
bsp;
&nbs
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
            </span></span>
Diagra
m (NB:
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
          <h4 style="text-align:justif
y">Inclu
de</h4>

       
   <p st
yle="text-align:justify">When you click on "<b style="mso-bidi-font-weight:normal">Include</b>"
          any Include functionality is displayed in the form of a white admin textbox with an "<b style="mso-bidi-font-weight:normal">OK</b><span style="mso-bidi-font-weight:bold">" button
          and a link marked "<i>Edit</i>".</span> </p>
          <p style="text-align:justify">If no include textbox appears, there is no dynamic include possibilities on this template. Switch templates if you need to insert a page within a page.</p>
          <h4 style="text-align:justify">Page info</h4>
          <p class="MsoBodyText" style="text-align:justify">This is where the data about the page and settings for the page are edited.</p>
          <h4 style="text-align:justify">Authority</h4>
          <p class="MsoBodyText" style="text-align:justify">Here is where you can decide which roles have which right on this page.</p>
          <h4 style="text-al
ign:justif
y">Log out
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
            </span></
a><span styl
e="color: wi
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
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;
&nbsp;&nbsp;&n
bsp;
        
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
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&mi
ddot;<span style
="font:7.0pt "Ti
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
   
         system me
ssages</span></a><
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
            </span></span>Administrate conferences (NB: Conferences 
require an extra sof
tware module to
   
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
        The reason that there are severa
l 
bu
tt
on
s for "contribute" is that some contributions can be very long and a user need not scroll up or down to submit the contribution.</p>
        <p>To search among the contributions there is an inbuilt search function normally displayed in the top righthand corner of the window. Searches are valid for all contributions in a forum. You can search for words in the header, body or by author. Select first which of these three you wish to search among and then enter the key words. Click thereafter on "Search". The search can be limited by giving a start and end date of the search. Dates must be given in the yyyy-mm-dd format, for example 2002-02-26 or in the preset special forms like "yesterday" or "today".</p>
        <h2 align="center"><b>Administration</b></h2>
        <p>To administrate a forum, click on the "Admin" button
        in the upper lefthand corner. You will then come to an administration page where a forum can be added, removed or have its name changed. You c
an a
lso 
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
                    users are users that log into the system with a user name and password. Conference users are 
us
er
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
            <td width="100%"><b>Format: </b>The image´s width, height and border or frame (if 
an
y)
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
                <li><i>Middle: </i>The first row of text starts in t
he c
ente
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
              and horizontal space can be controlled.</td
>
     
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
          
          <li><i>Other frame:</i> - Opens the frame which you choose in the current frameset is being used. You control in which frame w
ill appear
 by enteri
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
      <td width="100%"><b>Publish from: </b>A future time and date can be given as publishing date if the page is not to be published now.</
td
>

 
  
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
          <li><i>Other frame</i> - if a frameset is being used here you
 can
 con
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
      <td width="100%"><b>Publish from: </b>A future time and date can be given as publishing date if the page is not to be published now.</
td
>

 
  
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
          <li><i>Other frame</i> - if a frameset is being used here you
 can
 con
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
    <td width="100%"><b>Diagram: </b>Creates a graph and  a link to it from the current page. NB! This function requir
es
 a
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
      <td width="100%"><b>Publish from: </b>A future time and date can be given as publishing date if the page is not to be published now.</
td
>

 
  
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
          <li><i>Other frame</i> - if a frameset is being used here you
 can
 con
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
      <td width="100%"><b>Publish from: </b>A future time and date can be given as publishing date if the page is not to be published now.</
td
>

 
  
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
          <li><i>Other frame</i> - if a frameset is being used here you 
can 
cont
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
       values(284,1,'Authority
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(284,2,'<p align="center"> This is where you can control which roles can do what on each page.</p>
<div align="CENTER">
<table border="1" width="100%">
  <tr>
    <td width="100%"><b> Header: </b> The page title to be displayed(main link text).
      By ticking <img border="0" src="/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
      the checkbox <i> Show page link to unauthorised users </i> visitors without page authority are allowed to see the link but prevented from visiting the page.</td>
  </tr>
  <tr>
    <td width="100%"><b> Authority: </b> Displays the roles that have some level of authority on the page in question. 
      <ul>
        <li><i> None </i> means that not even the link is seen by this role unless <i> Show page link to unauthorised users </i> is ticked <img border="0" src="/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">.
        </li>
        <li><i> Read </i> betyder att anv&auml;ndare med rollen kan l&auml;sa allt p&aring;
          si


dan, men inte kan &auml;ndra n&aring;got p&aring; den.</li>
        <li><i> Dynamic Authority 2 </i> means users with this role can do what Dynamic Authority 2 is allowed on this page. To edit the authority of Dynamic Authority 2 see
          <b> Define authority.</b></li>
        <li><i> Dynamic Authority 1 </i> means users with this role can do what Dynamic Authority 1 is allowed on this page. To edit the authority of Dynamic Authority 1 see
          <b> Define authority.</b><i> </i></li>
        <li><i> Full </i> means that the users having this authority can make any change on this page.</li>
      </ul>
      <p> The level of authority is changed for a role by clicking on a new white radio button. If none is marked the role will be moved to <i> Roles without authority </i> once you click on "Save".</p>
    </td>
  </tr>
  <tr>
    <td width="100%">
      <p align="left"><b> Roles without authority: </b> By marking a role
      and clicking on "Add" can new roles
 a
lr
eady in the system be brought up onto the page´s role authority table. The role is moved up to <b> Page Authority </b> an a level of authority can be assigned.</p>
    </td>
  </tr>
  <tr>
    <td width="100%"><b> Define authority: </b> By clicking on "Define authority" either by <i> Dynamic Authority 1 </i> or <i> Dynamic Authority 2 </i> a new settings page will appear where you can define in detail the authority to be given.  A <img border="0" src="/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
      means that Dynamic Authority 1 has more authority than Dynamic Authority 2 on this specific page.  This checkbox should be ticked if you wish to strongly differenciate the two dynamic levels of authority. The tick <img border="0" src="/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
      means that Dynamic Authority 1 is allows to redefine the authority of dynamic authority 2 and change the roles assigned to the lower levels of the page.
      "Defi
ne f
or n
ew sub-pages" means that you can define the authority configuration of future sub-pages created from this page and this configuration can be different than those on this page. Default settings for new sub-pages will otherwise be those found on this page. </td>
  </tr>
  <tr>
    <td width="100%"><b> Created by: </b>(the name of the user that created this page). By ticking the checkbox <img border="0" src="/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
      next to <i> Allow unauthorised local links to this page </i> you can allow other site administrators who do not have authority on this page to link to it.</td>
  </tr>
</table>
</div align="CENTER">
 
 
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(285,1,'Delete a page
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(285,2,'<div align="center">
  <center>
  <table border="0" width="480">
    <tr>
      <td>
<p align="left"> Enter the Meta ID of the page to be removed. Click
on "Delete". A warning will appear requiring confirmation prior to the page being deleted from the database.</p>
<p align="left">"Back" takes you back to the previous page.</p>

      </td>
    </tr>
  </table>
  </center>
</div>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(286,1,'Remove a page - Warning
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(286,2,'<p align="center"> Click on &quot;OK&quot; if you are sure you wish to remove the page.</p>
<p align="center">&quot;Cancel&quot; brings you to the previous page without removing any pages.</p>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(287,1,'Change page appearance
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(287,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
            <p align="left"> Current template directory and template is shown. To change the appearance - click on <img border="0" src="/images/se/helpimages/Pil.GIF" width="16" height="21">
            the template. If the template you wish  to use is in a different directory, click on <img border="0" src="/images/se/helpimages/Pil.GIF" width="16" height="21">
            by template directory, select the new template directory and click on "Change directory". The templates in the directory can be seen by clicking on <img border="0" src="/images/se/helpimages/Pil.GIF" width="16" height="21">
            by "templates". Mark the template and then click on     "Save". To see the template, click on 
            "Preveiw template". NB: This works only if a model of the template (i.e. gif image from a screen dump) has been loaded.</p>
      </td>
    </tr>
  </ta


ble>
  </center>
</div>
 
 
 
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(288,1,'Delete  a role - Warning
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(288,2,'If you are certain you wish to remove this role, click on  &quot;OK&quot;, otherwise click on &quot;Cancel&quot;.
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(288,3,'
',0)
 
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
      <td width="100%"><b>Publish from: </b>A future time and date can be given as publishing date if the page is not to be published now.</
td
>

 
  
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
          <li><i>Other frame</i> - if a frameset is being used here you
 can
 con
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
 
INSERT INTO texts( meta_id, name, text, type )
       values(291,1,'Administrate sections
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(291,3,'
<br>
<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="360">
    <tr>
      <td width="360">
                  <p align="left"> To create a new site section click on &quot;<b> Add </b>&quot;.</p>
                  <p align="left"> To remove a site section click on &quot;<b> Delete </b>&quot;.</p>
                  <p align="left"> To change the name of a section click on &quot;<b> Edit </b>&quot;.&nbsp;</p>
                  <p align="left">&quot;<b> Back </b>&quot; takes you to the main Adminstration Menu.</p>
      </td>
    </tr>
  </table>
  </center>
</div>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(292,1,'Add site section
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(292,3,'




<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="400">
    <tr>
      <td>
                  <p align="left"> Enter the name of the new section in <i> New section.</i> <br> Click on
                  &quot;<b> Add </b>&quot;.</p>
                  <p align="left"><i> List of existing </i>: shows a list
                  of all the registered sections. The number of pages in each section is shown between parentheses [
                  ]&nbsp;.</p>
                  <p align="left">&nbsp;&quot;<b> Back </b>&quot; takes you to Administrate sections.</p>
        <p>&nbsp;</td>
    </tr>
  </table>
  </center>
</div>
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(293,1,'Delete Site Section
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(293,3,'

<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="385">
    <tr>
      <td width="383">
                  <p align="left"> Select the setion to be removed from the roll-down menu. The number of pages connected to the section are shown between the parentheses, [ ]&nbsp;.</p>
                  <p align="left"> Click on &quot;<b> Delete </b>&quot;.</p>
                  <p align="left"><b> Back </b>&quot; takes you to Administrate
                  sections.</p>
      </td>
    </tr>
  </table>
  </center>
</div>

',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(294,1,'Warning - Delete Site Section
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(294,3,'

<br>
<div align="center">
  <center>

  <table border="0" cellpadding="0" cellspacing="0" width="389">
    <tr>
      <td width="387">
                  <p align="left"> A warning appears to show how many pages are connected to the section you wish to remove. Select a new section for these pages by marking the new section in the roll-down menu. If they are not to be connected to a section - select "Delete connections". </p>
                  <p align="left"> Click on &quot;<b> OK </b>&quot;.</p>
                  <p align="left">&quot;<b> Cancel </b>&quot; takes you to Delete Site Section.</p>
      </td>
    </tr>
  </table>
    </center>
</div>

',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(295,1,'Change name of site section
',1)
 
INSERT INTO texts( meta_id, name, text, type )
       values(295,3,'

<br><div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="404">
    <tr>
      <td width="402">
                  <p align="left"> Select the section to be renamed from the roll-down menu. The number of pages connected to the section are displayed between the parentheses, [ ]&nbsp; .</p>
                  <p align="left"> Enter the new name of the section and click thereafter on <br>&quot;<b> Change name </b>&quot;.</p>
                  <p align="left">&quot;<b> Back </b>&quot; takes you back to 
                  Administrate site section.</p>
      </td>
    </tr>
  </table>
  </center>
</div>
',1)
 
 


declare @groupId int
select @groupId = max(group_id)+1 from templategroups

--ok lets create the templategroup
insert into templategroups(group_id, group_name)
values(@groupId,'imCMShelp')

--ok lets add the templates and relate templates and templategroups
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

--ok lets set templates for all help meta_ids
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (1,2,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (2,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (3,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (4,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (5,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (6,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (7,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (8,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (9,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (10,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (11,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (12,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (13,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (14,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (15,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (16,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (17,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (18,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (19,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (20,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (21,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (22,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (23,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (24,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (25,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (26,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (27,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (28,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (29,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (30,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (31,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (32,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (33,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (34,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (35,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (36,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (37,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (38,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (39,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (40,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (41,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (42,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (43,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (44,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (45,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (46,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (47,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (48,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (49,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (50,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (51,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (52,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (53,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (54,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (55,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (56,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (57,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (58,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (59,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (60,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (61,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (62,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (63,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (64,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (65,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (66,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (67,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (68,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (69,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (70,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (71,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (72,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (73,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (74,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (75,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (76,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (77,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (78,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (79,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (80,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (81,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (82,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (83,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (84,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (85,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (86,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (87,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (88,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (89,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (90,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (91,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (92,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (93,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (94,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (95,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (96,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (97,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (98,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (99,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (100,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (101,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (102,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (103,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (104,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (105,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (106,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (107,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (108,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (109,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (110,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (111,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (112,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (113,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (114,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (115,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (116,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (117,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (118,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (119,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (120,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (121,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (122,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (123,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (124,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (125,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (126,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (127,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (128,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (129,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (130,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (131,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (132,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (133,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (134,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (135,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (136,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (137,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (138,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (139,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (140,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (141,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (142,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (143,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (144,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (145,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (146,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (147,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (148,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (149,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (150,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (151,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (152,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (153,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (154,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (155,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (156,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (157,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (158,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (159,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (160,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (161,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (162,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (163,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (164,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (165,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (166,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (167,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (168,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (169,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (170,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (171,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (172,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (173,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (174,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (175,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (176,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (177,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (178,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (179,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (180,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (181,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (182,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (183,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (184,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (185,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (186,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (187,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (188,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (189,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (190,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (191,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (192,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (193,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (194,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (195,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (196,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (197,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (198,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (199,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (200,3,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (201,4,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (202,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (203,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (204,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (205,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (206,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (207,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (208,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (209,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (210,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (211,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (212,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (213,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (214,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (215,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (216,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (217,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (218,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (219,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (220,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (221,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (222,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (223,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (224,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (225,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (226,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (227,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (228,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (229,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (230,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (231,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (232,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (233,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (234,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (235,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (236,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (237,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (238,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (239,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (240,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (241,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (242,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (243,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (244,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (245,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (246,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (247,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (248,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (249,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (250,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (251,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (252,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (253,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (254,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (255,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (256,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (257,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (258,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (259,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (260,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (261,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (262,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (263,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (264,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (265,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (266,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (267,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (268,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (269,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (270,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (271,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (272,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (273,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (274,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (275,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (276,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (277,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (278,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (279,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (280,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (281,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (282,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (283,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (284,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (285,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (286,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (287,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (288,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (289,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (290,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (291,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (292,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (293,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (294,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (295,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (296,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (297,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (298,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (299,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (300,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (301,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (302,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (303,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (304,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (305,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (306,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (307,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (308,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (309,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (310,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (311,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (312,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (313,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (314,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (315,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (316,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (317,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (318,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (319,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (320,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (321,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (322,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (323,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (324,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (325,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (326,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (327,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (328,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (329,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (330,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (331,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (332,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (333,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (334,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (335,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (336,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (337,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (338,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (339,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (340,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (341,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (342,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (343,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (344,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (345,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (346,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (347,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (348,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (349,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (350,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (351,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (352,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (353,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (354,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (355,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (356,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (357,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (358,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (359,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (360,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (361,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (362,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (363,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (364,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (365,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (366,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (367,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (368,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (369,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (370,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (371,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (372,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (373,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (374,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (375,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (376,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (377,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (378,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (379,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (380,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (381,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (382,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (383,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (384,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (385,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (386,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (387,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (388,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (389,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (390,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (391,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (392,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (393,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (394,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (395,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (396,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (397,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (398,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (399,5,@groupId,1,-1,-1)
INSERT INTO text_docs ( meta_id , template_id , group_id , sort_order , default_template_1 , default_template_2 )
    values (400,5,@groupId,1,-1,-1)
 
-- get all childs to meta_id < @metaMax
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,2,1,500)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,3,1,510)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,4,1,520)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,5,1,530)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,6,1,540)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,7,1,550)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,8,1,560)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,9,1,570)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,10,1,580)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,11,1,590)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,12,1,600)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,13,1,610)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,14,1,620)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,15,1,630)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,16,1,640)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,17,1,650)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,18,1,660)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,19,1,670)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,20,1,680)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,21,1,690)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,22,1,700)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,23,1,710)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,24,1,720)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,25,1,730)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,26,1,740)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,27,1,750)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,28,1,760)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,29,1,770)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,30,1,780)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,31,1,790)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,32,1,800)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,33,1,810)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,34,1,820)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,35,1,830)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,36,1,840)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,37,1,850)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,38,1,860)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,39,1,870)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,40,1,880)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,41,1,890)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,42,1,900)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,43,1,910)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,44,1,920)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,45,1,930)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,46,1,940)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,47,1,950)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,48,1,960)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,49,1,970)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,50,1,980)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,51,1,990)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,52,1,1000)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,53,1,1010)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,54,1,1020)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,55,1,1030)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,56,1,1040)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,57,1,1050)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,58,1,1060)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,59,1,1070)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,60,1,1080)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,61,1,1090)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,62,1,1100)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,63,1,1110)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,64,1,1120)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,65,1,1130)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,66,1,1140)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,67,1,1150)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,68,1,1160)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,69,1,1170)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,70,1,1180)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,71,1,1190)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,72,1,1200)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,73,1,1210)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,74,1,1220)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,75,1,1230)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,76,1,1240)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,77,1,1250)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,78,1,1260)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,79,1,1270)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,80,1,1280)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,81,1,1290)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,82,1,1300)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,83,1,1310)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,84,1,1320)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,85,1,1330)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,86,1,1340)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,87,1,1350)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,88,1,1360)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,89,1,1370)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (1,90,1,1380)
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
    values (201,211,1,590)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,212,1,600)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,213,1,610)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,214,1,620)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,215,1,630)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,216,1,640)
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
    values (201,225,1,730)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,226,1,740)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,227,1,750)
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
    values (201,234,1,820)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,235,1,830)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,236,1,840)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,237,1,850)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,238,1,860)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,239,1,870)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,240,1,880)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,241,1,890)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,242,1,900)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,243,1,910)
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
    values (201,284,1,1320)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,285,1,1330)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,286,1,1340)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,287,1,1350)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,288,1,1360)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,289,1,1370)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,290,1,1380)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,291,1,1390)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,292,1,1400)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,293,1,1410)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,294,1,1420)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,295,1,1430)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,1002,1,1640)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,1003,1,1650)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,1004,1,1660)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,1005,1,1670)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,1006,1,1680)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,1007,1,1690)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,1008,1,1700)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,1009,1,1710)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,1010,1,1720)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,1011,1,1730)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,1012,1,1740)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,1013,1,1750)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,1014,1,1760)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,1015,1,1770)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,1016,1,1780)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,1017,1,1790)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,1018,1,1800)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,1019,1,1810)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,1020,1,1820)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,1021,1,1830)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,1022,1,1840)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,1023,1,1850)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,1024,1,1860)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,1025,1,1870)
INSERT INTO childs ( meta_id , to_meta_id , menu_sort , manual_sort_order )
    values (201,1026,1,1880)
 
--lets set all the role_rights
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,1,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,1,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,2,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,2,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,3,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,3,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,4,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,4,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,5,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,5,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,6,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,6,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,7,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,7,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,8,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,8,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,9,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,9,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,10,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,10,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,11,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,11,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,12,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,12,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,13,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,13,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,14,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,14,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,15,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,15,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,16,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,16,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,17,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,17,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,18,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,18,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,19,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,19,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,20,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,20,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,21,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,21,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,22,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,22,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,23,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,23,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,24,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,24,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,25,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,25,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,26,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,26,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,27,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,27,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,28,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,28,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,29,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,29,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,30,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,30,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,31,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,31,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,32,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,32,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,33,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,33,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,34,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,34,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,35,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,35,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,36,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,36,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,37,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,37,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,38,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,38,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,39,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,39,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,40,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,40,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,41,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,41,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,42,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,42,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,43,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,43,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,44,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,44,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,45,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,45,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,46,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,46,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,47,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,47,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,48,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,48,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,49,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,49,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,50,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,50,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,51,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,51,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,52,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,52,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,53,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,53,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,54,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,54,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,55,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,55,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,56,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,56,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,57,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,57,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,58,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,58,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,59,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,59,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,60,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,60,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,61,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,61,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,62,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,62,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,63,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,63,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,64,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,64,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,65,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,65,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,66,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,66,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,67,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,67,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,68,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,68,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,69,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,69,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,70,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,70,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,71,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,71,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,72,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,72,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,73,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,73,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,74,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,74,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,75,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,75,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,76,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,76,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,77,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,77,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,78,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,78,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,79,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,79,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,80,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,80,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,81,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,81,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,82,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,82,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,83,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,83,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,84,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,84,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,85,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,85,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,86,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,86,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,87,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,87,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,88,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,88,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,89,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,89,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,90,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,90,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,91,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,91,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,92,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,92,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,93,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,93,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,94,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,94,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,95,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,95,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,96,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,96,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,97,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,97,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,98,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,98,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,99,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,99,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,100,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,100,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,101,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,101,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,102,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,102,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,103,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,103,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,104,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,104,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,105,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,105,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,106,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,106,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,107,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,107,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,108,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,108,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,109,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,109,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,110,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,110,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,111,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,111,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,112,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,112,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,113,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,113,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,114,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,114,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,115,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,115,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,116,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,116,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,117,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,117,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,118,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,118,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,119,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,119,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,120,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,120,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,121,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,121,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,122,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,122,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,123,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,123,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,124,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,124,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,125,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,125,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,126,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,126,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,127,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,127,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,128,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,128,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,129,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,129,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,130,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,130,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,131,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,131,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,132,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,132,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,133,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,133,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,134,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,134,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,135,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,135,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,136,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,136,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,137,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,137,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,138,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,138,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,139,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,139,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,140,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,140,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,141,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,141,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,142,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,142,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,143,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,143,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,144,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,144,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,145,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,145,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,146,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,146,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,147,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,147,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,148,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,148,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,149,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,149,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,150,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,150,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,151,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,151,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,152,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,152,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,153,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,153,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,154,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,154,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,155,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,155,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,156,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,156,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,157,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,157,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,158,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,158,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,159,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,159,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,160,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,160,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,161,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,161,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,162,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,162,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,163,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,163,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,164,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,164,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,165,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,165,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,166,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,166,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,167,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,167,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,168,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,168,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,169,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,169,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,170,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,170,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,171,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,171,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,172,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,172,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,173,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,173,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,174,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,174,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,175,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,175,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,176,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,176,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,177,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,177,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,178,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,178,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,179,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,179,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,180,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,180,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,181,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,181,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,182,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,182,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,183,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,183,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,184,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,184,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,185,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,185,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,186,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,186,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,187,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,187,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,188,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,188,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,189,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,189,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,190,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,190,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,191,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,191,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,192,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,192,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,193,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,193,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,194,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,194,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,195,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,195,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,196,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,196,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,197,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,197,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,198,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,198,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,199,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,199,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,200,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,200,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,201,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,201,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,202,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,202,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,203,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,203,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,204,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,204,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,205,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,205,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,206,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,206,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,207,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,207,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,208,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,208,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,209,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,209,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,210,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,210,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,211,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,211,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,212,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,212,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,213,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,213,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,214,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,214,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,215,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,215,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,216,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,216,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,217,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,217,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,218,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,218,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,219,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,219,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,220,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,220,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,221,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,221,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,222,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,222,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,223,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,223,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,224,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,224,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,225,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,225,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,226,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,226,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,227,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,227,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,228,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,228,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,229,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,229,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,230,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,230,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,231,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,231,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,232,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,232,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,233,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,233,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,234,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,234,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,235,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,235,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,236,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,236,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,237,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,237,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,238,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,238,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,239,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,239,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,240,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,240,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,241,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,241,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,242,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,242,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,243,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,243,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,244,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,244,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,245,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,245,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,246,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,246,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,247,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,247,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,248,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,248,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,249,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,249,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,250,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,250,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,251,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,251,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,252,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,252,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,253,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,253,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,254,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,254,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,255,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,255,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,256,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,256,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,257,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,257,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,258,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,258,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,259,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,259,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,260,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,260,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,261,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,261,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,262,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,262,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,263,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,263,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,264,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,264,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,265,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,265,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,266,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,266,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,267,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,267,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,268,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,268,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,269,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,269,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,270,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,270,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,271,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,271,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,272,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,272,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,273,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,273,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,274,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,274,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,275,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,275,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,276,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,276,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,277,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,277,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,278,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,278,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,279,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,279,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,280,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,280,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,281,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,281,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,282,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,282,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,283,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,283,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,284,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,284,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,285,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,285,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,286,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,286,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,287,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,287,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,288,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,288,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,289,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,289,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,290,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,290,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,291,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,291,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,292,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,292,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,293,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,293,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,294,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,294,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,295,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,295,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,296,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,296,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,297,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,297,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,298,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,298,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,299,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,299,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,300,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,300,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,301,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,301,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,302,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,302,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,303,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,303,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,304,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,304,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,305,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,305,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,306,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,306,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,307,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,307,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,308,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,308,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,309,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,309,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,310,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,310,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,311,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,311,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,312,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,312,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,313,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,313,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,314,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,314,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,315,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,315,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,316,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,316,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,317,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,317,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,318,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,318,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,319,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,319,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,320,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,320,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,321,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,321,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,322,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,322,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,323,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,323,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,324,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,324,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,325,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,325,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,326,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,326,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,327,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,327,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,328,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,328,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,329,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,329,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,330,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,330,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,331,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,331,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,332,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,332,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,333,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,333,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,334,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,334,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,335,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,335,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,336,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,336,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,337,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,337,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,338,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,338,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,339,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,339,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,340,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,340,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,341,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,341,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,342,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,342,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,343,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,343,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,344,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,344,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,345,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,345,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,346,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,346,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,347,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,347,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,348,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,348,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,349,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,349,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,350,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,350,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,351,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,351,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,352,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,352,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,353,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,353,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,354,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,354,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,355,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,355,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,356,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,356,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,357,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,357,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,358,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,358,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,359,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,359,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,360,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,360,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,361,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,361,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,362,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,362,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,363,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,363,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,364,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,364,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,365,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,365,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,366,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,366,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,367,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,367,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,368,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,368,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,369,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,369,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,370,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,370,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,371,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,371,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,372,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,372,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,373,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,373,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,374,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,374,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,375,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,375,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,376,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,376,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,377,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,377,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,378,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,378,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,379,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,379,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,380,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,380,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,381,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,381,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,382,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,382,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,383,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,383,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,384,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,384,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,385,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,385,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,386,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,386,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,387,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,387,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,388,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,388,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,389,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,389,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,390,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,390,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,391,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,391,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,392,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,392,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,393,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,393,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,394,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,394,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,395,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,395,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,396,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,396,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,397,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,397,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,398,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,398,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,399,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,399,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (0,400,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
    values (1,400,3)
 
end
