
declare @webroot varchar(30)
set @webroot='/imcms' 

-- kollar att det inte redan finns mallar med id 2 eller 3
DECLARE @temp int 
declare @message varchar(100)
SET @temp = 0  
SELECT @temp = template_id
FROM templates
WHERE template_id=2 or template_id=3
IF @temp = 2 or @temp=3
	select 'Du har inte läst manualen för att installera hjälp! Läs manualen för att se hur ni ska göra!'as message
else
begin

-- ok vi kan börja ösa in i databasen

SET IDENTITY_INSERT meta ON 
INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (1,'',2,'Hj&auml;lpsidan','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (2,'',2,'Administrera filer','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (3,'',2,'Aktivera/avaktivera användare','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (4,'',2,'Administrera användarroller','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (5,'',2,'Administrationssida f&ouml;r Browserkontroll','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (6,'',2,'Förändrade dokument - bild 1','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (7,'',2,'Förändrade dokument - bild 2','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (8,'',2,'Administrera formatmallar/formatgrupper','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (9,'',2,'Byta namn på formatgrupp','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (10,'',2,'Lägga till/ta bort formatmallar','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (11,'',2,'Skapa formatgrupp','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (12,'',2,'Ta bort formatgrupp','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (13,'',2,'Ta bort formatgrupp - varning','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (14,'',2,'Administrationssida för IP-accesser - bild 1','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (15,'',2,'Lägga till ny IP-access ','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (16,'',2,'Ta bort IP-accesser - varning','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (17,'',2,'Administrera räknare','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (18,'',2,'Kontrollera internet-länkar','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (19,'',2,'L&auml;gga till l&auml;nk till ett befintligt dokument','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (20,'',2,'Byt namn på formatmall','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (21,'',2,'Hämta uppladdad formatmall','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (22,'',2,'Ladda upp ny formatmall','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (23,'',2,'Ladda upp ny exempelmall','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (24,'',2,'Ladda upp ny formatmall - klart!','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (25,'',2,'Ta bort formatmall','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (26,'',2,'Ta bort formatmall - varning!','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (27,'',2,'Visa formatmallar','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (28,'',2,'Administratörsmenyn','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (29,'',2,'Administrera användare och roller','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (30,'',2,'Administrera roller ','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (31,'',2,'Byt namn på roll','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (32,'',2,'Lägg till ny roll','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (33,'',2,'Redigera rättigheter för roll','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (34,'',2,'Ta bort roll - varning!','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (35,'',2,'Administrera systeminformation','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (36,'',2,'Lägga till/ändra text','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (37,'',2,'Visa alla dokument','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (38,'',2,'Ändra användaregenskaper','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (39,'',2,'Ändra dokumentinfo','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (40,'',2,'Rättigheter för begränsad behörighet 1','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (41,'',2,'Rättigheter för begränsad behörighet 1, för nya dokument','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (42,'',2,'Rättigheter för begränsad behörighet 2','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (43,'',2,'Rättigheter för begränsad behörighet 2, för nya dokument','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (44,'',2,'Lägga till bild - Bildarkiv','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (45,'',2,'Meddelande','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (46,'',2,'L&auml;gga till l&auml;nk till Browserkontroll - sida 2','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (47,'',2,'Lägga till diagram - bild 1','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (48,'',2,'Lägga till diagram - bild 2 - Skapa nytt diagram','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (49,'',2,'Lägga till diagram - bild 3 - Inmatningsformulär för diagram och tabeller','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (50,'',2,'Lägga till diagram - bild 4 - Nytt diagram meny','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (51,'',2,'Rättighet att få lösenord via e-post saknas','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (52,'',2,'Lösenord via e-post','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (53,'',2,'Inkludera en befintlig sida i en annan sida','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (54,'',2,'Inloggning','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (55,'',2,'Knappraden','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (56,'',2,'Konferens - ändra användare','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (57,'',2,'Konferens - administrera användardata','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (58,'',2,'Konferens - varning vid byte av mallset','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (59,'',2,'Konferens - administrera diskussion','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (60,'',2,'Konferens - administrera forum','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (61,'',2,'Konferens - administrera inlägg','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (62,'',2,'Konferens - administrera mallset','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (63,'',2,'Konferens - administrera självregistrering','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (64,'',2,'Konferens - ändra befintlig mallfil','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (65,'',2,'Konferens - inloggning','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (66,'',2,'Konferensvy','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (67,'',2,'Konferens - självregistrering','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (68,'',2,'Konferens - konferensdata','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (69,'',2,'Konferens - skapa en ny diskussion','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (70,'',2,'Konferens - skapa en ny kommentar','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (71,'',2,'Lägga till/redigera användare','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (72,'',2,'Lägga till bild','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (73,'',2,'Lägga till bild - Browse/Sök','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (74,'',2,'L&auml;gga till l&auml;nk till en fil - sida 2','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (75,'',2,'L&auml;gga till l&auml;nk till HTML-dokument - sida 1','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (76,'',2,'L&auml;gga till l&auml;nk till HTML-dokument - sida 2','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (77,'',2,'L&auml;gga till l&auml;nk till Text-dokument - sida 1','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (78,'',2,'L&auml;gga till l&auml;nk till Text-dokument - sida 2','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (79,'',2,'L&auml;gga till l&auml;nk - funktion','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (80,'',2,'L&auml;gga till l&auml;nk till en fil - sida 1','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (81,'',2,'L&auml;gga till l&auml;nk till URL-dokument - sida 1','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (82,'',2,'L&auml;gga till l&auml;nk till URL-dokument - sida 2','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (83,'',2,'Misslyckad inloggning','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (84,'',2,'Rättigheter','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (85,'',2,'Ta bort ett dokument','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (86,'',2,'Ta bort ett dokument - varning','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (87,'',2,'Ändra utseende på dokumentet','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (88,'',2,'Ta bort roll - varning','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (89,'',2,'Administrera roller - huvudsida','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (90,'',2,'L&auml;gga till l&auml;nk - sida 1','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (91,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (92,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (93,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (94,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (95,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (96,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (97,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (98,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (99,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (100,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (101,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (102,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (103,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (104,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (105,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (106,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (107,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (108,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (109,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (110,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (111,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (112,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (113,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (114,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (115,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (116,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (117,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (118,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (119,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (120,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (121,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (122,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (123,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (124,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (125,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (126,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (127,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (128,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (129,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (130,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (131,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (132,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (133,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (134,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (135,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (136,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (137,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (138,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (139,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (140,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (141,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (142,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (143,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (144,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (145,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (146,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (147,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (148,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (149,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (150,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (151,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (152,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (153,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (154,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (155,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (156,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (157,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (158,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (159,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (160,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (161,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (162,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (163,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (164,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (165,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (166,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (167,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (168,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (169,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (170,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (171,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (172,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (173,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (174,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (175,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (176,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (177,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (178,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (179,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (180,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (181,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (182,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (183,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (184,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (185,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (186,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (187,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (188,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (189,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (190,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (191,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (192,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (193,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (194,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (195,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (196,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (197,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (198,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (199,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_self','',1,'2001-09-14 00:00:00',NULL)
 INSERT INTO meta( meta_id , description , doc_type , meta_headline , meta_text , meta_image , owner_id , permissions , shared , expand , show_meta , help_text_id , archive , status_id , lang_prefix , classification , date_created , date_modified , sort_position , menu_position , disable_search , target , frame_name , activate , activated_datetime , archived_datetime )
values (200,'',2,'','','',1,0,0,1,0,1,0,1,'se','','2001-09-14 00:00:00','2001-09-14 00:00:00',1,1,0,'_blank','',1,'2001-09-14 00:00:00',NULL)
SET IDENTITY_INSERT meta off

INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (2,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-filadministration.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (3,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-roller-av-aktiv-anv.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (4,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-admin-anvoroller.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (5,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/AdminsidaBrowser2.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (6,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-forandrade-dok-bild1.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (6,0,0,0,0,0,2,'','_self','','top','','','se/helpimages/Admin-forandrade-dok-bild2.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (7,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-forandrade-dok-bild2.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (8,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-mallar.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (9,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-grupp-byt-namn.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (10,333,249,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-formatgrupp-tilldela-mall.gif','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (11,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-formatgrupp-skapa.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (12,331,85,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-formatgrupp-ta-bort.gif','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (13,303,137,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-formatgrupp-ta-bort-varning.gif','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (14,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-ip-access.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (14,0,0,0,0,0,2,'','_self','','top','','','se/helpimages/Admin-admin-ip-access.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (15,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-IP-access-ny.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (16,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-IP-access-varning.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (17,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-raknare.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (18,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-test-av-url.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (19,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-lank-bef-dok copy.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (19,0,0,0,0,0,2,'','_self','','top','','','','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (20,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-mallar-byt-namn-mall.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (21,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-mallar-hamta-ned.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (22,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-ladda-upp-ny-mall.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (23,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-mallar-ny-exempelmall.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (24,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-mallar-uppl-gickbra.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (25,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-ta-bort-mall.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (26,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-mallar-tabort-varning.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (27,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-mallar-visa-formatmallar.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (28,0,0,1,0,0,1,'','_self','','top','','','se/helpimages/Admin-meny.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (29,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-roller-adm-anvoroller.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (30,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-admin-roller.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (31,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-roller-bytnamn.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (32,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-roller-lagg-till-ny.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (33,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-roller-redigera.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (34,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-roller-ta-bort-varning.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (35,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-systeminfo.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (36,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Andra-text-html.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (36,0,0,0,0,0,2,'','_self','','top','','','se/helpimages/Admin_text_html.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (37,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-visa-alla-dok.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (37,0,0,0,0,0,2,'','_self','','top','','','se/helpimages/Admin-visa-dok2.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (38,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-andraanvegenskaper.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (39,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Dokumentinfo.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (40,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Rattigheter-behorighet1.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (41,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Rattigheter-behorighet1.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (42,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Behorighet2.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (43,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Behorighet2-nya-dok.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (44,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Bildarkiv.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (45,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Bild-finns-redan.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (46,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Valj-Browser.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (47,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Lagg-till-diagram.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (48,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Skapa-Nytt-Diagram.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (49,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Diagram-inmatningsformular.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (49,0,0,0,0,0,2,'','_self','','top','','','se/helpimages/Diagram-tabellinstallning.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (50,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Diagram-tillbaka-till-x.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (51,0,0,1,0,0,1,'','_self','','middle','','','se/helpimages/Login-ej-e-post.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (52,0,0,1,0,0,1,'','_self','','top','','','se/helpimages/Losen-via-e-post.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (53,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Include.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (54,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/login.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (55,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Knappar3.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (56,0,0,1,0,0,1,'','_self','','top','','','se/helpimages/Konf-admin-anvandare.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (57,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Konf-admin-anvandardata.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (58,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Konf-bytmallset-varning1.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (59,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Konf-admin-diskussion.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (60,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Konf-admin-forum.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (61,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Konf-admin-kommentar.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (62,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Konf-admin-mallset.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (63,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Konf-admin-sjalvreg.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (64,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Konf-ny-mallfil.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (65,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Konferens-login.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (66,940,452,0,0,0,1,'','_self','','top','','','se/helpimages/Konf-confViewer1.gif','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (67,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Konf-sjalvreg.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (68,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Konferens-data.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (69,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Konf-admin-ny-diskussion.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (70,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Konf-admin-ny-kommentar.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (71,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-lagg-till-anvandare.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (72,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Lagg-till-bild-m-bild.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (73,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Bild-Browse.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (74,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Valj-fil.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (74,0,0,0,0,0,2,'','_self','','top','','','se/helpimages/Valj-filtyp.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (75,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Lagg-till-dokument2.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (76,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/HTML-kod.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (77,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Lagg-till-dokument.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (78,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Andra-Text1.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (78,0,0,0,0,0,2,'','_self','','top','','','se/helpimages/Andra-text-html1.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (79,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Lank-valj.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (79,0,0,0,0,0,2,'','_self','','top','','','se/helpimages/Lank-Arkivera.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (80,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Lank-fil.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (81,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Lank-URL.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (82,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/NyURL.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (83,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Losen-felaktigt.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (84,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Rattigheter.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (85,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-ta-bort-dok.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (86,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-ta-bort-dok-varning.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (87,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/utseende3.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (88,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-ta-bort-roll-varning.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (89,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Admin-roller-huvudsida.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (90,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Lagg-till-dokument.GIF','')
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (1002,0,0,0,0,0,1,'','_self','','top','','','se/helpimages/Lagg-till-dokument.GIF','')














insert into texts(meta_id,name,text,type)
values(
2,1,'Administrera filer',0
)
insert into texts(meta_id,name,text,type)
values(
2,2,'Genom att markera en katalog och sedan klicka p&aring; &quot;Byt katalog&quot; visas inneh&aring;llet i den katalogen. Genom att markera ..\ och klicka p&aring; &quot;Byt katalog&quot; s&aring; tar man sig ett steg h&ouml;gre upp i hierarkin.
)
insert into texts(meta_id,name,text,type)
values(<BR>
<BR>F&ouml;r att ladda ner en fil till sin egen h&aring;rddisk/n&auml;tverk, leta fram filen och markera den, klicka sedan p&aring; &quot;Ladda ner&quot;. &quot;Ladda ner&quot; till v&auml;nster om filen finns i den v&auml;nstra rutan och &quot;Ladda ner&quot; till h&ouml;ger om filen finns i den h&ouml;gra rutan. Ett nytt f&ouml;nster &ouml;ppnas d&auml;r du f&aring;r v&auml;lja om du vill spara ned filen p&aring; din h&aring;rddisk/ditt n&auml;tverk eller om du vill &ouml;ppna filen. Spr&aring;ket i bilden &auml;r beroende p&aring; det spr&aring;k som din webbl&auml;sare har.
<BR>
<BR>F&ouml;r att ladda upp en fil fr&aring;n sin egen h&aring;rddisk/n&auml;tverk, klicka p&aring; &quot;Browse&quot; (mitt i bilden). Ett nytt f&ouml;nster &ouml;ppnas d&auml;r du f&aring;r leta reda p&aring; filen. Spr&aring;ket i bilden &auml;r beroende p&aring; det spr&aring;k som din webbl&auml;sare har. N&auml;r filen &auml;r framletad, skall den katalog d&auml;r filen skall l&auml;ggas in i markeras. Om katalogen &auml;r markerad i den v&auml;nstra rutan, klicka p&aring; &quot;Ladda upp&quot; till v&auml;nster. &Auml;r katalogen markerad i den h&ouml;gra rutan, klicka p&aring; &quot;Ladda upp&quot; till h&ouml;ger.
<BR>
<BR>F&ouml;r att kopiera en fil till en annan katalog,  leta fram filen i den v&auml;nstra rutan, markera filen och leta fram den katalog dit filen skall kopieras i den h&ouml;gra rutan och markera den. Klicka sedan p&aring; &quot;Kopiera -&gt;&quot;. Detta g&aring;r att g&ouml;ra tv&auml;rtom ocks&aring; - kopiera fil fr&aring;n den h&ouml;gra rutan till den v&auml;nstra. Klicka d&aring; p&aring; &quot;&lt;-Kopiera&quot; ist&auml;llet.
<BR>
<BR>F&ouml;r att flytta en fil fr&aring;n en katalog till en annan katalog,  leta fram filen i den v&auml;nstra rutan, markera filen och leta fram den katalog dit filen skall flyttas i den h&ouml;gra rutan och markera den. Klicka sedan p&aring; &quot;Flytta -&gt;&quot;. Detta g&aring;r att g&ouml;ra tv&auml;rtom ocks&aring; - flytta en fil fr&aring;n den h&ouml;gra rutan till den v&auml;nstra. Klicka d&aring; p&aring; &quot;&lt;-Flytta&quot; ist&auml;llet.
<BR>
<BR>F&ouml;r att byta namn p&aring; en katalog eller fil, markera den katalog/fil som skall bytas namn p&aring;. Skriv in det nya namnet i rutan under Nytt namn:. Klicka sedan p&aring; &quot;Byt namn&quot; till v&auml;nster eller h&ouml;ger beroende p&aring; om katalogen/filen finns i den v&auml;nstra eller h&ouml;gra rutan.
<BR>
<BR>F&ouml;r att skapa katalog, markera den katalog som den nya katalogen skall hamna under. Skriv in namnet p&aring; katalogen i rutan under Nytt namn:. Klicka sedan p&aring; &quot;Skapa katalog&quot; till v&auml;nster eller h&ouml;ger beroende p&aring; i vilken ruta du har markerat katalogen.
<BR>
<BR>F&ouml;r att radera en katalog eller fil, markera katalogen/filen och klicka sedan p&aring; &quot;Radera&quot;. Knappen till v&auml;nster om katalogen/filen finns i v&auml;nstra rutan, knappen till h&ouml;ger om katalogen/filen finns i h&ouml;gra rutan. En varningsruta d&auml;r hela s&ouml;kv&auml;gen till katalogen/filen finns visas. Klicka &quot;Ja&quot; om du &auml;r s&auml;ker p&aring; att katalogen/filen skall tas bort, annars p&aring; &quot;Nej&quot;.
<BR>
<BR>&quot;Tillbaka&quot; leder till Administrat&ouml;rsmenyn.
<BR>',0
)
insert into texts(meta_id,name,text,type)
values(
3,1,'Aktivera/avaktivera anv&auml;ndare',0
)
insert into texts(meta_id,name,text,type)
values(
3,2,'H&auml;r kan anv&auml;ndare aktiveras och avaktiveras. Avaktivering g&ouml;r att anv&auml;ndaren inte l&auml;ngre kan logga in i systemet. En anv&auml;ndare som &auml;r avaktiverad kan h&auml;r aktiveras igen.
<BR>
<BR>Markera den/de anv&auml;ndare som du vill aktivera och klicka sedan p&aring; &quot;Aktivera&quot;. Anv&auml;ndaren/anv&auml;ndarna &auml;r nu aktiverade och har tillg&aring;ng till systemet igen.
<BR>
<BR>Markera den/de anv&auml;ndare som du vill avaktivera och klicka sedan p&aring; &quot;Avktivera&quot;. Anv&auml;ndaren/anv&auml;ndarna &auml;r nu avaktiverade och har inte l&auml;ngre tillg&aring;ng till systemet.
<BR>',0
)
insert into texts(meta_id,name,text,type)
values(
4,1,'Administrera anv&auml;ndarroller',0
)
insert into texts(meta_id,name,text,type)
values(
4,2,'H&auml;r kan anv&auml;ndare tilldelas en ny roll, tas bort fr&aring;n en roll och flyttas fr&aring;n en roll till en annan.
<BR>
<BR>F&ouml;r att tilldela anv&auml;ndaren en ny roll - klicka f&ouml;rst p&aring; anv&auml;ndaren och sedan p&aring; den roll som anv&auml;ndaren skall tilldelas och till sist klicka p&aring; &quot;Tilldela&quot;. Anv&auml;ndaren tillh&ouml;r nu b&aring;de den aktuella rollen och den nya rollen.
<BR>
<BR>F&ouml;r att ta bort en anv&auml;ndare fr&aring;n en roll - klicka f&ouml;rst p&aring; anv&auml;ndaren som skall tas bort och sedan p&aring; &quot;Ta bort&quot;. Anv&auml;ndaren tas bort fr&aring;n rollen.
<BR>
<BR>F&ouml;r att flytta en anv&auml;ndare till en annan roll - klicka f&ouml;rst p&aring; anv&auml;ndaren och sedan p&aring; den roll som anv&auml;ndaren skall flyttas till och till sist klicka p&aring; &quot;Flytta&quot;. Anv&auml;ndaren flyttas fr&aring;n aktuell roll till den nya rollen.
<BR>
<BR>&quot;Avbryt&quot; leder tillbaka till f&ouml;reg&aring;ende sida.
<BR>',0
)
insert into texts(meta_id,name,text,type)
values(
5,1,'Administrationssida f&ouml;r Browserkontroll',0
)
insert into texts(meta_id,name,text,type)
values(
5,2,'<div align="center">
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
',1
)
insert into texts(meta_id,name,text,type)
values(
6,1,'F&ouml;r&auml;ndrade dokument - bild 1',0
)
insert into texts(meta_id,name,text,type)
values(
6,2,'H&auml;r kan man s&ouml;ka fram alla dokument som lagts till i systemet under en viss tidsperiod.
<BR>
<BR>',0
)
insert into texts(meta_id,name,text,type)
values(
6,3,'Markera den dokumenttyp som du vill se p&aring; genom att klicka p&aring; den. Dokumenttypen Alla visar alla dokument oavsett typ som skapats under perioden.
<BR>
<BR>Ange fr&aring;n och med-datum och till och med-datum. Datum anges i formatet &Aring;&Aring;&Aring;&Aring;-MM-DD.
<BR>
<BR>Klicka sedan p&aring; &quot;Visa&quot;.
<BR>
<BR>I ett nytt f&ouml;nster visas resultatet av s&ouml;kningen.
<BR>',0
)
insert into texts(meta_id,name,text,type)
values(
6,4,'Bilden visar alla dokument som skapats under perioden. Det g&aring;r att klicka p&aring; antingen Meta id eller Rubrik f&ouml;r att se sidan (dokumentet).',0
)
insert into texts(meta_id,name,text,type)
values(
7,1,'F&ouml;r&auml;ndrade dokument - bild 2',0
)
insert into texts(meta_id,name,text,type)
values(
7,2,'Bilden visar alla dokument som skapats under perioden. Det g&aring;r att klicka p&aring; antingen Meta id eller Rubrik f&ouml;r att se sidan (dokumentet).',0
)
insert into texts(meta_id,name,text,type)
values(
8,1,'Administrera formatmallar/formatgrupper',0
)
insert into texts(meta_id,name,text,type)
values(
8,2,'<p align="left">Detta &auml;r startsidan f&ouml;r administrationen av
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
',1
)
insert into texts(meta_id,name,text,type)
values(
9,1,'Byta namn p&aring; formatgrupp',0
)
insert into texts(meta_id,name,text,type)
values(
9,2,'V&auml;lj den befintliga formatgruppen genom att bl&auml;ddra fram den i rullgardinslistan. Skriv sedan in det nya namnet vid Nytt namn. Klicka p&aring; &quot;OK&quot;.
<BR>
<BR>&quot;Tillbaka&quot; leder till Administrera formatmallar/formatgrupper.
<BR>',0
)
insert into texts(meta_id,name,text,type)
values(
10,1,'L&auml;gga till/ta bort formatmallar',0
)
insert into texts(meta_id,name,text,type)
values(
10,2,'H&auml;r kan formatmallar l&auml;ggas till en grupp eller tas bort fr&aring;n en grupp.
<BR>V&auml;lj den formatgrupp du vill arbeta med genom att bl&auml;ddra fram den i rullgardinslistan. Klicka sedan p&aring; &quot;Visa mallar&quot;. Mallarna i den valda formatgruppen visas nu i rutan till h&ouml;ger p&aring; bilden.
<BR>
<BR>I rutan till v&auml;nster visas vilka mallar som finns tillg&auml;ngliga.
<BR>L&auml;gga till formatmall till en grupp: Markera mallen i rutan till v&auml;nster, klicka sedan p&aring; &quot;L&auml;gg till&quot; och mallen l&auml;ggs till gruppen och visas i det h&ouml;gra f&ouml;nstret..
<BR>
<BR>Ta bort formatmall fr&aring;n en grupp: Markera mallen i rutan till h&ouml;ger, klicka sedan p&aring; &quot;Ta bort&quot; och mallen tas bort fr&aring;n gruppen och visas i det v&auml;nstra f&ouml;nstret..
<BR>
<BR>&quot;Tillbaka&quot; leder till Administrera formatmallar/formatgrupper.
<BR>',0
)
insert into texts(meta_id,name,text,type)
values(
11,1,'Skapa formatgrupp',0
)
insert into texts(meta_id,name,text,type)
values(
11,2,'<center>Skriv in det namn som formatgruppen skall ha. Klicka sedan p&aring; "Skapa".</p>

"Tillbaka" leder till Administrera formatmallar/formatgrupper.
</center>',1
)
insert into texts(meta_id,name,text,type)
values(
12,1,'Ta bort formatgrupp',0
)
insert into texts(meta_id,name,text,type)
values(
12,2,'V&auml;lj den formatgrupp du vill ta bort genom att bl&auml;ddra fram den i rullgardinslistan. Klicka sedan p&aring; &quot;Ta bort&quot;.
<BR>
<BR>&quot;Tillbaka&quot; leder till Administrera formatmallar/formatgrupper.
<BR>',0
)
insert into texts(meta_id,name,text,type)
values(
13,1,'Ta bort formatgrupp - varning',0
)
insert into texts(meta_id,name,text,type)
values(
13,2,'Varning f&ouml;r att ta bort formatgrupp. Bilden visar vilka mallar som tillh&ouml;r den grupp som h&aring;ller p&aring; att tas bort. 
<BR>
<BR>Om du vill ta bort gruppen, flytta d&aring; f&ouml;rst &ouml;ver mallarna (om de inte redan tillh&ouml;r n&aring;gon annan grupp ocks&aring;) till n&aring;gon annan grupp. Detta g&ouml;rs via gr&auml;nssnittet f&ouml;r L&auml;gg till/Ta bort formatmallar. 
<BR>
<BR>Klicka sedan p&aring; &quot;OK&quot;.
<BR>
<BR>Om du inte vill ta bort gruppen klicka p&aring; &quot;Avbryt&quot;.
<BR>',0
)
insert into texts(meta_id,name,text,type)
values(
14,1,'Administrationssida f&ouml;r IP-accesser - bild 1',0
)
insert into texts(meta_id,name,text,type)
values(
14,2,'Genom att koppla anv&auml;ndarnamnet till anv&auml;ndarens dators IP-nr kan anv&auml;ndaren f&aring; direkt tillg&aring;ng till systemet utan att beh&ouml;va logga in. De f&aring;r ett anv&auml;ndarnamn som &auml;r kopplat till datorns IP-nr. Man kan ge flera anv&auml;ndare inom ett visst intervall av IP-nr ett gemensamt anv&auml;ndarnamn.&nbsp;',0
)
insert into texts(meta_id,name,text,type)
values(
14,3,'N&auml;r ingen IP-access &auml;r registrerad ser bilden ut som ovan. N&auml;r n&aring;gon IP-access har blivit registrerad, ser den ut som nedan.',0
)
insert into texts(meta_id,name,text,type)
values(
14,4,'F&ouml;r att l&auml;gga till en ny IP-access - klicka p&aring; &quot;L&auml;gg till&quot;.&nbsp;
<BR>
<BR>F&ouml;r att &auml;ndra p&aring; en befintlig uppgift - s&auml;tt en bock i rutan framf&ouml;r anv&auml;ndaren, g&ouml;r &auml;ndringen och klicka sedan p&aring; &quot;Spara om&quot;.
<BR>
<BR>F&ouml;r att ta bort en anv&auml;ndare - s&auml;tt en bock i rutan framf&ouml;r anv&auml;ndaren, och klicka sedan p&aring; &quot;Ta bort&quot;.
<BR>
<BR>F&ouml;r att &aring;terg&aring; till f&ouml;reg&aring;ende bild - klicka p&aring; &quot;Tillbaka&quot;.
<BR>',0
)
insert into texts(meta_id,name,text,type)
values(
15,1,'L&auml;gga till ny IP-access',0
)
insert into texts(meta_id,name,text,type)
values(
15,2,'V&auml;lja anv&auml;ndare genom att bl&auml;ddra i rullgardinslistan. Skriv in tillh&ouml;rande IP-nr eller intervall av IP-nr som g&auml;ller f&ouml;r anv&auml;ndaren. Klicka p&aring; &quot;Spara&quot;.
<BR>
<BR>F&ouml;r att &aring;terg&aring; till f&ouml;reg&aring;ende bild utan att l&auml;gga till ny IP-access - klicka p&aring; &quot;Avbryt&quot;.',0
)
insert into texts(meta_id,name,text,type)
values(
16,1,'Ta bort IP-accesser - varning',0
)
insert into texts(meta_id,name,text,type)
values(
16,2,'F&ouml;r att ta bort IP-accessen - klicka &quot;OK&quot;, f&ouml;r att &aring;terg&aring; till f&ouml;reg&aring;ende sida - klicka p&aring; &quot;Avbryt&quot;.',0
)
insert into texts(meta_id,name,text,type)
values(
17,1,'Administrera r&auml;knare',0
)
insert into texts(meta_id,name,text,type)
values(
17,2,'R&auml;kneverket &auml;ndras genom att ett nytt v&auml;rde skrivs in i den &ouml;versta vita rutan och sedan klicka p&aring; &quot;Uppdatera&quot;. Om r&auml;kneverket skall nollst&auml;llas skrivs en nolla in.
<BR>
<BR>F&ouml;r att &auml;ndra startdatum fyller man i ett nytt datum i den nedersta vita rutan och klickar sedan p&aring; &quot;Uppdatera&quot;. Det &auml;r det datum som sedan kommer att visas som sedan-datum.
<BR>
<BR>Ex Antal bes&ouml;kare &auml;r 6731 sedan 2000-01-01
<BR>',0
)
insert into texts(meta_id,name,text,type)
values(
18,1,'Kontrollera internet-l&auml;nkar',0
)
insert into texts(meta_id,name,text,type)
values(
18,2,'H&auml;r kan man v&auml;lja att klicka p&aring; meta-id-l&auml;nkarna och kommer d&aring; till adminl&auml;get d&auml;r l&auml;nken &auml;r. Klickar man p&aring; URL:erna s&aring; kommer man till den sidan dit l&auml;nken leder. Rutorna under rubrikerna: &quot;Servern hittades&quot;, &quot;Servern gick att n&aring;&quot; och &quot;Dokumentet hittades&quot; f&auml;rgas gr&ouml;na om s&aring; &auml;r fallet annars f&auml;rgas de r&ouml;da. P&aring; s&aring; s&auml;tt kan man se var felet ligger om man ej n&aring;r de externa l&auml;nkarna i fr&aring;n systemet.',0
)
insert into texts(meta_id,name,text,type)
values(
19,1,'L&auml;gga till l&auml;nk till ett befintligt dokument',0
)
insert into texts(meta_id,name,text,type)
values(
19,2,'Det finns tv&aring; s&auml;tt att l&auml;gga till en l&auml;nk till ett befintlig sida.
        Kan man sidans MetaID skriver man in det direkt annars finns m&ouml;jlighet
        att s&ouml;ka fram sidan.
        ',1
)
insert into texts(meta_id,name,text,type)
values(
19,3,'<p>I &ouml;versta delen - "<b><i>V&auml;lj befintligt dokument</i></b>"
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
        h&auml;r kan man begr&auml;nsa s&ouml;kningen genom att skriva in start- och
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
        s&ouml;kningen visas underst p&aring; sidan.</span>',1
)
insert into texts(meta_id,name,text,type)
values(
20,1,'Byt namn p&aring; formatmall',0
)
insert into texts(meta_id,name,text,type)
values(
20,2,'V&auml;lj vilken mall som skall bytas namn p&aring; genom att bl&auml;ddra fram namnet p&aring; mallen i rullgardinslistan. Skriv in det nya namnet. Klicka sedan p&aring; &quot;Byt namn&quot;.
<BR>
<BR>&quot;Tillbaka&quot; leder tillbaka till f&ouml;reg&aring;ende sida.
<BR>',0
)
insert into texts(meta_id,name,text,type)
values(
21,1,'H&auml;mta uppladdad formatmall',0
)
insert into texts(meta_id,name,text,type)
values(
21,3,'V&auml;lj vilken mall som skall h&auml;mtas genom bl&auml;ddra fram den i rullgardinslistan. Klicka sedan p&aring; &quot;H&auml;mta&quot;.
<BR>
<BR>En ny sida d&auml;r du f&aring;r v&auml;lja om du vill se p&aring; mallen eller om du vill spara den p&aring; disk visas. Bilden visas p&aring; det spr&aring;k som din webbl&auml;sare anv&auml;nder. Klicka p&aring; &quot;OK&quot; och v&auml;lj sedan var p&aring; din h&aring;rddisk/n&auml;tverk du vill spara mallen.
<BR>
<BR>&quot;Tillbaka&quot; (delvis dold under rullgardinslistan p&aring; bilden) leder till Administrera formatmallar/formatgrupper.
<BR>',0
)
insert into texts(meta_id,name,text,type)
values(
22,1,'Ladda upp ny formatmall',0
)
insert into texts(meta_id,name,text,type)
values(
22,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
                <blockquote>
                  <p align="left">Leta reda p&aring; den formatmall som du vill ladda
                  upp genom att anv&auml;nda "Browse/S&ouml;k" (namnet beror
                  p&aring; vilket spr&aring;k din webbl&auml;sare anv&auml;nder).</p>
                  <p align="left">Skriv in det namn du vill att formatmallen
                  skall ha. En <img border="0" src="'+@webroot+'/images/se/helpimages/Admin-4.GIF" width="13" height="14">
                  vid <i>"Skriv &ouml;ver existerande" </i>g&ouml;r att om en
                  formatmall med det namnet redan finns, skrivs den &ouml;ver. S&auml;tt
                  inte <img border="0" src="'+@webroot+'/images/se/helpimages/Admin-4.GIF" width="13" height="14">
                  om du inte &auml;r riktigt s&auml;ker p&aring; att du verkligen skall
                  skriva &ouml;ver den befintliga mallen.</p>
                  <p align="left">Markera i rutan till h&ouml;ger den/de
                  formatgrupper som skall ha tillg&aring;ng till mallen. Detta
                  beh&ouml;ver inte g&ouml;ras nu utan kan g&ouml;ras senare, men mallen kan
                  inte anv&auml;ndas om den inte tillh&ouml;r n&aring;gon grupp.</p>
                  <p align="left">Genom att klicka p&aring; "Ladda upp"
                  l&auml;ggs formatmallen till.</p>
                  <p align="left">&nbsp;"Tillbaka" leder till
                  Administrera formatmallar/formatgrupper.</p>
                </blockquote>
      </td>
    </tr>
  </table>
  </center>
</div>',1
)
insert into texts(meta_id,name,text,type)
values(
23,1,'Ladda upp ny exempelmall',0
)
insert into texts(meta_id,name,text,type)
values(
23,2,'<p align="left">F&ouml;r att ladda upp en ny exempelmall:</p>
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
                      <p align="left"><i>V&auml;lj mall:</i> V&auml;lj den mall (med *)
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
                  </ul>',1
)
insert into texts(meta_id,name,text,type)
values(
24,1,'Ladda upp ny formatmall - klart!',0
)
insert into texts(meta_id,name,text,type)
values(
24,2,'<center>Bilden visar att formatmallen lagts till i systemet. </p>&nbsp;

"Tillbaka" leder till Administrera formatmallar/formatgrupper.
</center>',1
)
insert into texts(meta_id,name,text,type)
values(
25,1,'Ta bort formatmall',0
)
insert into texts(meta_id,name,text,type)
values(
25,2,'V&auml;lj den formatmall du vill ta bort genom att bl&auml;ddra fram den i rullgardinslistan. Inom [ ] st&aring;r det hur m&aring;nga sidor som anv&auml;nder mallen.
<BR>
<BR>Genom att klicka p&aring; &quot;Ta bort&quot; tas mallen bort. Innan mallen tas bort visas en sida d&auml;r det g&aring;r att tilldela de dokument, som har den mallen som skall tas bort, n&aring;gon annan formatmall.
<BR>
<BR>&quot;Tillbaka&quot; leder till f&ouml;reg&aring;ende sida.
<BR>',0
)
insert into texts(meta_id,name,text,type)
values(
26,1,'Ta bort formatmall - varning!',0
)
insert into texts(meta_id,name,text,type)
values(
26,2,'V&auml;lj vilken ny mall som de dokument som anv&auml;nder mallen du t&auml;nker ta bort skall anv&auml;nda i forts&auml;ttningen. V&auml;lj genom att bl&auml;ddra fram mallen i rullgardinslistan. OBS att alla dokument kommer att tilldelas samma mall.
<BR>
<BR>Klicka sedan p&aring; &quot;OK&quot;.
<BR>
<BR>&quot;Avbryt&quot; leder tillbaka till f&ouml;reg&aring;ende sida.
<BR>',0
)
insert into texts(meta_id,name,text,type)
values(
27,1,'Visa formatmallar',0
)
insert into texts(meta_id,name,text,type)
values(
27,2,'Till v&auml;nster p&aring; bilden visas alla formatmallar. Inom [ ] st&aring;r det hur m&aring;nga dokument som anv&auml;nder mallen. Markera mallen och klicka p&aring; &quot;Lista dokument&quot;. Dokumenten visas nu till h&ouml;ger p&aring; bilden. Genom att markera ett dokument och sedan klicka p&aring; &quot;Visa dokument&quot; f&aring;r du se dokumentet.&nbsp;
<BR>&nbsp;
<BR>&quot;Tillbaka&quot; leder till Administrera formatmallar/formatgrupper.
<BR>',0
)
insert into texts(meta_id,name,text,type)
values(
28,1,'Administrat&ouml;rsmenyn',0
)
insert into texts(meta_id,name,text,type)
values(
28,2,'H&auml;r kan man:
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
                rullgardinslistan och sedan klicka p&aring; knappen "G&aring; till
                adminsida" (dold under rullgardinslistan p&aring; bilden).</p>
                <p>L&auml;nken "Tillbaka till startsidan" leder tillbaka
                till systemets f&ouml;rsta sida.',1
)
insert into texts(meta_id,name,text,type)
values(
29,1,'Administrera anv&auml;ndare och roller',0
)
insert into texts(meta_id,name,text,type)
values(
29,2,'&quot;Administrera&quot; leder till den sida d&auml;r anv&auml;ndare kan tilldelas en ny roll, tas bort fr&aring;n en roll och flyttas fr&aring;n en roll till en annan. Markera den roll du vill arbeta med.
<BR>
<BR>&quot;av/Aktivera&quot; leder till en sida d&auml;r anv&auml;ndare kan aktiveras eller avaktiveras. Avaktivering g&ouml;r att anv&auml;ndaren inte l&auml;ngre kan logga in i systemet. En anv&auml;ndare som &auml;r avaktiverad kan aktiveras igen. OBS att endast de anv&auml;ndare som tillh&ouml;r den roll som &auml;r vald visas. Om Alla markeras visas alla anv&auml;ndare.
<BR>
<BR>&quot;Tillbaka&quot; leder tillbaka till f&ouml;reg&aring;ende sida.
<BR>',0
)
insert into texts(meta_id,name,text,type)
values(
30,1,'Administrera roller ',0
)
insert into texts(meta_id,name,text,type)
values(
30,2,'Knappen &quot;Administrera roller&quot; leder till den sida d&auml;r nya roller kan l&auml;ggas till, namnet bytas p&aring; en roll, r&auml;ttigheterna f&ouml;r rollen kan redigeras eller rollen kan tas bort.
<BR>
<BR>Knappen &quot;Administrera anv&auml;ndar-roller&quot; leder till den sida d&auml;r administration av rollen sker. D&auml;r kan anv&auml;ndare l&auml;ggas till, tas bort och flyttas fr&aring;n en roll till en annan.
<BR>',0
)
insert into texts(meta_id,name,text,type)
values(
31,1,'Byt namn p&aring; roll',0
)
insert into texts(meta_id,name,text,type)
values(
31,2,'<center>Skriv in det nya rollnamnet och klicka p&aring; "Spara".</p>
"Avbryt" leder tillbaka till f&ouml;reg&aring;ende sida.</p></center>',1
)
insert into texts(meta_id,name,text,type)
values(
32,1,'L&auml;gg till ny roll',0
)
insert into texts(meta_id,name,text,type)
values(
32,2,'<center>
  <table border="0" cellpadding="0" cellspacing="0" width="400">
    <tr>
      <td>
                <p align="left">Skriv in namnet p&aring; den roll som skall l&auml;ggas
                till. </p>
                <p align="left">Genom att s&auml;tta en <img border="0" src="'+@webroot+'/images/se/helpimages/Admin-4.GIF" width="13" height="14">
                vid <i>"R&auml;tt att f&aring; l&ouml;senord per mail"</i> f&aring;r en
                anv&auml;ndare som tillh&ouml;r rollen m&ouml;jlighet att f&aring; sitt l&ouml;senord
                s&auml;nt till sig per mail om han/hon gl&ouml;mt bort det.
                (Best&auml;llningen av l&ouml;senordet g&ouml;rs p&aring; inloggningssidan).</p>
                <p align="left">Genom att s&auml;tta en <img border="0" src="'+@webroot+'/images/se/helpimages/Admin-4.GIF" width="13" height="14">
                vid <i>"Sj&auml;lvregistreringsr&auml;tt i konferens"</i> f&aring;r
                en anv&auml;ndare som tillh&ouml;r rollen r&auml;tt att registrera sig
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
</div>',1
)
insert into texts(meta_id,name,text,type)
values(
33,1,'Redigera r&auml;ttigheter f&ouml;r roll',0
)
insert into texts(meta_id,name,text,type)
values(
33,2,'<p align="left">Genom att s&auml;tta en bock vid <i>"R&auml;tt att f&aring; l&ouml;senord per mail"</i> f&aring;r en
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
              sida utan att n&aring;gon f&ouml;r&auml;ndring i rollens r&auml;ttigheter gjorts.</p>',1
)
insert into texts(meta_id,name,text,type)
values(
34,1,'Ta bort roll - varning!',0
)
insert into texts(meta_id,name,text,type)
values(
34,2,'H&auml;r visas vilka anv&auml;ndare (de 50 f&ouml;rsta) som &auml;r medlemmar i den roll som du t&auml;nker ta bort (om inga anv&auml;ndare visas har rollen inga anv&auml;ndare tilldelade till sig).&nbsp;
<BR>
<BR>Dessutom ser du vilka dokument som rollen har r&auml;ttigheter till. Att ta bort rollen g&ouml;r att anv&auml;ndarna inte kan se dessa dokument l&auml;ngre (om de inte ocks&aring; tillh&ouml;r n&aring;gon annan roll som har r&auml;ttigheter till dessa dokument).
<BR>
<BR>F&ouml;r att ta bort rollen, klicka p&aring; &quot;OK&quot;. Om du inte vill ta bort rollen, klicka p&aring; &quot;Avbryt&quot;.
<BR>',0
)
insert into texts(meta_id,name,text,type)
values(
35,1,'Administrera systeminformation',0
)
insert into texts(meta_id,name,text,type)
values(
35,2,'H&auml;r kan man skriva in systemmeddelanden, ange vem som &auml;r servermaster och webbmaster. OBS att f&ouml;r att dessa uppgifter skall visas p&aring; en sida m&aring;ste de &quot;imCMS-taggar&quot; som styr detta vara inlagda i den mall som styr utseendet p&aring; sidan (dokumentet).',0
)
insert into texts(meta_id,name,text,type)
values(
35,3,'<h3><b>Ange systemmeddelande </b></h3>
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
          &auml;r/skall vara ny webbmaster.',1
)
insert into texts(meta_id,name,text,type)
values(
36,1,'L&auml;gga till/&auml;ndra text',0
)
insert into texts(meta_id,name,text,type)
values(
36,2,'&Auml;ndra text Txt 10 visar vilket textf&auml;lt p&aring; sidan som l&auml;ggs
              till/&auml;ndras. MetaId visar vilken specifik sida som
              till&auml;gget/&auml;ndringen g&ouml;rs p&aring;.</p>
              <p>Den ursprungliga texten visas i textrutan.&nbsp; I textrutan
              kan redigering g&ouml;ras, text tas bort eller l&auml;ggas till. &Auml;r det
              vanlig text som skrivs i rutan skall <b>Format</b>: <i>Vanlig text</i>
              markeras.',1
)
insert into texts(meta_id,name,text,type)
values(
36,3,'Det g&aring;r &auml;ven att skriva text med HTML-formateringar, d&aring;
              skall <b>Format</b>: <i>HTML</i> markeras.',1
)
insert into texts(meta_id,name,text,type)
values(
37,1,'Visa alla dokument',0
)
insert into texts(meta_id,name,text,type)
values(
37,2,'',0
)
insert into texts(meta_id,name,text,type)
values(
37,3,'H&auml;r v&auml;ljer man f&ouml;rst dokumentnummer p&aring; start, vilket blir det f&ouml;rsta dokumentet som listas. N&auml;r man kommer in p&aring; sidan ligger det f&ouml;rsta  tillg&auml;ngliga dokumentet automatiskt p&aring; start. P&aring; intervall v&auml;ljer man sen inom vilket intervall dokumenten man vill se ska ligga. Slutligen trycker man p&aring; knappen &quot;Lista&quot; och f&aring;r d&aring; upp en lista av l&auml;nkar till alla de dokument som har dokumentnummer inom det intervall man valt.',0
)
insert into texts(meta_id,name,text,type)
values(
37,4,'H&auml;r visas alla sidor (dokument) i intervallet, med sina undersidor (dokument). Om man klickar p&aring; en sida som har en &#9679; framf&ouml;r visas sidan (dokumentet). Klickar man p&aring; en sida (dokument) med &deg; framf&ouml;r visas listan med det MetaId:et &ouml;verst. D&aring; g&aring;r det att se om undersidan i sin tur har undersidor. Undersidan har nu f&aring;tt en &#9679; framf&ouml;r sig och det g&aring;r att klicka p&aring; den f&ouml;r att f&aring; se sidan.',1
)
insert into texts(meta_id,name,text,type)
values(
38,1,'&Auml;ndra anv&auml;ndaregenskaper',0
)
insert into texts(meta_id,name,text,type)
values(
38,2,'Anv&auml;ndarkategori - v&auml;lj kategori och klicka p&aring; "Visa anv&auml;ndare" f&ouml;r att visa denna kategoris anv&auml;ndare i nedre delen av bilden.<br><br>

<li>Anonyma anv&auml;ndare: Anv&auml;ndare som inte loggar in sig i systemet.</li>
<li>Autentiserade anv&auml;ndare: Anv&auml;ndare som loggar in i systemet med hj&auml;lp av sitt anv&auml;ndarnamn och l&ouml;senord.</li>
<li>Konferensanv&auml;ndare: Anv&auml;ndare som l&auml;gger in sig sj&auml;lva i systemet.</li>

<br><br>F&ouml;r att l&auml;gga till en ny anv&auml;ndare - klicka p&aring; "L&auml;gg till".

<br><br>F&ouml;r att redigera en befintlig anv&auml;ndare - klicka p&aring; anv&auml;ndaren f&ouml;r att markera den och sedan p&aring; "Redigera".

F&ouml;r att &aring;terg&aring; till f&ouml;reg&aring;ende sida klicka p&aring; "Tillbaka".',1
)
insert into texts(meta_id,name,text,type)
values(
39,1,'&Auml;ndra dokumentinfo',0
)
insert into texts(meta_id,name,text,type)
values(
39,2,'<h3 align="center">H&auml;r &auml;ndras dokumentets grundinformation&nbsp;</h3>
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
        vid s&ouml;kning. En <img border="0" src="'+@webroot+'/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
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
        anges. En <img border="0" src="'+@webroot+'/images/se/helpimages/Lagg-t4.GIF" width="13" height="14"> i
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
            skall visas.',1
)
insert into texts(meta_id,name,text,type)
values(
40,1,'R&auml;ttigheter f&ouml;r begr&auml;nsad beh&ouml;righet 1',0
)
insert into texts(meta_id,name,text,type)
values(
40,2,'<center>
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
                    <td width="100%"><b>R&auml;tt att &auml;ndra avancerad
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
</div align="CENTER">',1
)
insert into texts(meta_id,name,text,type)
values(
41,1,'R&auml;ttigheter f&ouml;r begr&auml;nsad beh&ouml;righet 1, f&ouml;r nya dokument',0
)
insert into texts(meta_id,name,text,type)
values(
41,2,'<div align="center">
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
</div align="CENTER">',1
)
insert into texts(meta_id,name,text,type)
values(
42,1,'R&auml;ttigheter f&ouml;r begr&auml;nsad beh&ouml;righet 2',0
)
insert into texts(meta_id,name,text,type)
values(
42,2,'<div align="center">
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
                    <td width="100%"><b>R&auml;tt att &auml;ndra avancerad
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
</div align="CENTER">',1
)
insert into texts(meta_id,name,text,type)
values(
43,1,'R&auml;ttigheter f&ouml;r begr&auml;nsad beh&ouml;righet 2, f&ouml;r nya dokument',0
)
insert into texts(meta_id,name,text,type)
values(
43,2,'<div align="center">
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
</div align="CENTER">',1
)
insert into texts(meta_id,name,text,type)
values(
44,1,'L&auml;gga till bild - Bildarkiv',0
)
insert into texts(meta_id,name,text,type)
values(
44,2,'H&auml;r visas alla bilder som redan finns uppladdade i systemet. Klicka f&ouml;rst p&aring; en bild och sedan p&aring; &quot;F&ouml;rhandsgranska markerad bild&quot; f&ouml;r att f&aring; se bilden innan du l&auml;gger in den p&aring; din sida. N&auml;r du hittat den bild du s&ouml;ker klicka p&aring; &quot;Anv&auml;nd markerad bild&quot;.',0
)
insert into texts(meta_id,name,text,type)
values(
45,1,'Meddelande',0
)
insert into texts(meta_id,name,text,type)
values(
45,2,'En bild med samma filnamn finns redan. Klicka p&aring; &quot;OK&quot; och g&aring; sedan in i &quot;Bildarkivet&quot; f&ouml;r att se att det &auml;r samma bild som du f&ouml;rs&ouml;ker ladda upp. Om det inte &auml;r samma bild f&aring;r du byta namn p&aring; filen i ditt n&auml;tverk innan du laddar upp filen igen.',0
)
insert into texts(meta_id,name,text,type)
values(
46,1,'L&auml;gga till l&auml;nk till Browserkontroll - sida 2',0
)
insert into texts(meta_id,name,text,type)
values(
46,2,'Samma webbsida kan se olika ut beorende p&aring; vilken webbl&auml;sare (browser) som anv&auml;nds. D&auml;rf&ouml;r kan man g&ouml;ra flera alternativa sidor och styra vilken sida som skall visas i respektive webbl&auml;sare. Markera webbl&auml;saren och klicka p&aring; &quot;L&auml;gg till&quot;. Ett f&auml;lt kommer upp p&aring; h&ouml;ger sida. Skriv in MetaId f&ouml;r den sida som skall visas. Upprepa om det &auml;r flera olika webbl&auml;sare som skall visa olika sidor. Klicka sedan p&aring; &quot;OK&quot;.',0
)
insert into texts(meta_id,name,text,type)
values(
47,1,'L&auml;gga till diagram - bild 1',0
)
insert into texts(meta_id,name,text,type)
values(
47,2,'<div align="center">
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
        vid s&ouml;kning. En markering vid <i>Blockera s&ouml;kning</i> g&ouml;r att sidan inte kommer att ge n&aring;gon
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
</div>',1
)
insert into texts(meta_id,name,text,type)
values(
48,1,'L&auml;gga till diagram - bild 2 - Skapa nytt diagram',0
)
insert into texts(meta_id,name,text,type)
values(
48,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
<p align="center">H&auml;r v&auml;ljs vilken typ av diagram som skall skapas.</p>
<blockquote>
                <p align="left">V&auml;lj diagramtyp: Klicka p&aring; <img border="0" src="'+@webroot+'/images/se/helpimages/Pil.GIF" width="16" height="21">
                f&ouml;r att kunna markera den diagramtyp du vill ha. De olika
                alternativen visas p&aring; bildens nedersta del. Klicka sedan p&aring;
                "Skapa nytt diagram".</p>
</blockquote>

      </td>
    </tr>
  </table>
  </center>
</div>',1
)
insert into texts(meta_id,name,text,type)
values(
49,1,'L&auml;gga till diagram - bild 3
<BR>Inmatningsformul&auml;r f&ouml;r diagram och tabeller
<BR>',0
)
insert into texts(meta_id,name,text,type)
values(
49,2,'<h3 align="center">Diagraminst&auml;llningar:</h3>
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
</table>',1
)
insert into texts(meta_id,name,text,type)
values(
49,3,'<div align="CENTER">
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
    <td width="100%" height="19"><b>Delta x (x- och y-axel): </b>Autogenereras
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
    <td width="100%" height="19"><b>Ta bort rad: </b>Tar bort en rad. Vilken rad
      som skall tas bort v&auml;ljer man i rullgardingsmenyn <img border="0" src="'+@webroot+'/images/se/helpimages/Pil.GIF" width="16" height="21">,
      n&auml;r rullgardingsmenyn sl&auml;pps tas raden bort. </td>
  </tr>
  <tr>
    <td width="100%" height="19"><b>Ta bort kolumn: </b> Tar bort en
      kolumn. Vilken kolumn som skall tas bort v&auml;ljer man i rullgardingsmenyn <img border="0" src="'+@webroot+'/images/se/helpimages/Pil.GIF" width="16" height="21">,
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
  <center>
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
</div>',1
)
insert into texts(meta_id,name,text,type)
values(
49,4,'<div align="CENTER">
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
      som skall tas bort v&auml;ljer man i rullgardinsmenyn <img border="0" src="'+@webroot+'/images/se/helpimages/Pil.GIF" width="16" height="21">,
      n&auml;r rullgardinsmenyn sl&auml;pps tas raden bort. </td>
  </tr>
  <tr>
    <td width="100%" height="19"><b>Ta bort kolumn: </b> Tar bort en kolumn.
      Vilken kolumn som skall tas bort v&auml;ljer man i rullgardinsmenyn <img border="0" src="'+@webroot+'/images/se/helpimages/Pil.GIF" width="16" height="21">,
      n&auml;r rullgardinsmenyn sl&auml;pps tas kolumnen bort. </td>
  </tr>
  <tr>
    <td width="100%" height="18"><b>Klistra in diagramv&auml;rden h&auml;r: </b>Anv&auml;nds
      om man vill har v&auml;rden i Excel som man vill skapa diagram av. Kopiera de
      rader och kolumner som &ouml;nskas fr&aring;n Excelarket och klistra in dessa i
      rutan. Klicka sedan p&aring; "Skapa tabellv&auml;rden".</td>
  </tr>
</table>
</div align="CENTER">',1
)
insert into texts(meta_id,name,text,type)
values(
50,1,'L&auml;gga till diagram - bild 4
<BR>Nytt diagram meny',0
)
insert into texts(meta_id,name,text,type)
values(
50,2,'<div align="center">
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
</div>',1
)
insert into texts(meta_id,name,text,type)
values(
51,1,'R&auml;ttighet att f&aring; l&ouml;senord via e-post saknas
<BR>',0
)
insert into texts(meta_id,name,text,type)
values(
51,2,'<center>Du har inte beh&ouml;righet att f&aring; l&ouml;senordet s&auml;nt till dig via e-post. Var v&auml;nlig och kontakta Systemadministrat&ouml;ren f&ouml;r att f&aring; hj&auml;lp.</center>',1
)
insert into texts(meta_id,name,text,type)
values(
52,1,'L&ouml;senord via e-post',0
)
insert into texts(meta_id,name,text,type)
values(
52,2,'<p align="center">Ange anv&auml;ndarnamn och klicka p&aring; &quot;S&auml;nd&quot;.</p>
<p align="center">L&ouml;senordet skickas till den e-postadress som uppgavs vid
registreringen.</p>
',1
)
insert into texts(meta_id,name,text,type)
values(
53,1,'Inkludera en befintlig sida i en annan sida',0
)
insert into texts(meta_id,name,text,type)
values(
53,2,'<div align="center">
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
</div>',1
)
insert into texts(meta_id,name,text,type)
values(
54,1,'Inloggning',0
)
insert into texts(meta_id,name,text,type)
values(
54,2,'<div align="center">
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
</div>',1
)
insert into texts(meta_id,name,text,type)
values(
55,1,'Knappraden',0
)
insert into texts(meta_id,name,text,type)
values(
55,2,'<div align="center">
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
          <p style="text-align:justify">N&auml;r klick sker p&aring; "<b style="mso-bidi-font-weight:normal">Text</b>"
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
          ikonen eller p&aring; den lilla pilen p&aring; den plats d&auml;r bilden skall l&auml;ggas
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
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span>URL-dokument (Internetsida)</p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span>Browserkontroll</p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span>HTML-dokument</p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span>Fil</p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
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
          <h4 style="text-align:justify">Logga ut</h4>
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
            av anv&auml;ndare</span><span style="color: windowtext; text-decoration: none; text-underline: none">&nbsp;&nbsp;&nbsp;&nbsp;
            </span></a><span style="color: windowtext; text-decoration: none; text-underline: none">&nbsp;
            </span></p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span><a href="#_Administrera_roller"><span style="color:
windowtext;text-decoration:none;text-underline:none">Administration av roller</span></a></p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span><a href="#_Administrera_IP-accesser"><span style="color:windowtext;text-decoration:none;text-underline:none">Administration
            av IP-accesser</span></a></p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span><a href="#_Administrera_formatmallar/formatgrupper"><span style="color:windowtext;text-decoration:none;text-underline:none">Administration
            av mallar</span></a></p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span><a href="#_Visa_alla_dokument"><span style="color:windowtext;
text-decoration:none;text-underline:none">Visa alla dokument</span></a></p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span><a href="#_Ta_bort_ett"><span style="color:windowtext;
text-decoration:none;text-underline:none">Ta bort ett dokument</span></a></p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span><a href="#_Kontrollera_Internetl&auml;nkar"><span style="color:windowtext;text-decoration:none;text-underline:none">Kontrollera
            Internet-l&auml;nkar</span></a></p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span><a href="#_Administrera_r&auml;knare"><span style="color:windowtext;text-decoration:none;text-underline:none">Administrera
            r&auml;knare</span></a></p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span><a href="#_Administrera_systeminformation"><span style="color:windowtext;text-decoration:none;text-underline:none">Administrera
            systeminformation</span></a></p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span><a href="#_Administrera_filer"><span style="color:windowtext;
text-decoration:none;text-underline:none">Administrera filer</span></a></p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </span></span><a href="#_F&ouml;r&auml;ndrade_dokument"><span style="color:
windowtext;text-decoration:none;text-underline:none">F&ouml;r&auml;ndrade dokument</span></a></p>
            <p class="-49" style="text-align:justify"><span style="font-family:Symbol">&middot;<span style="font:7.0pt "Times New Roman"">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
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
',1
)
insert into texts(meta_id,name,text,type)
values(
56,1,'Konferens - &auml;ndra anv&auml;ndare',0
)
insert into texts(meta_id,name,text,type)
values(
56,2,'<div align="center">
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
</div>',1
)
insert into texts(meta_id,name,text,type)
values(
57,1,'Konferens - administrera anv&auml;ndardata',0
)
insert into texts(meta_id,name,text,type)
values(
57,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td><i>Expertanv&auml;ndare:</i> Genom att s&auml;tta en <img border="0" src="'+@webroot+'/images/se/helpimages/Konf-a1.GIF" width="13" height="14">
        i rutan efter Expertanv&auml;ndare kommer det att visas en&nbsp; <img border="0" src="'+@webroot+'/images/se/helpimages/Konf-a2.GIF" width="12" height="16">&nbsp;
        framf&ouml;r rubriken i de inl&auml;gg
        som anv&auml;ndaren g&ouml;r. Detta visar att anv&auml;ndaren &auml;r specialist i
        &auml;mnet. </td>
    </tr>
  </table>
  </center>
</div>
<p align="center">&nbsp;</p>',1
)
insert into texts(meta_id,name,text,type)
values(
58,1,'Konferens - varning vid byte av mallset',0
)
insert into texts(meta_id,name,text,type)
values(
58,2,'Om du &auml;r s&auml;ker p&aring; att du vill byta mallset, klicka &quot;OK&quot;. Om du &auml;r tveksam och vill kontrollera allt igen, klicka p&aring; &quot;Avbryt&quot;.
<BR>',0
)
insert into texts(meta_id,name,text,type)
values(
59,1,'Konferens - administrera diskussion',0
)
insert into texts(meta_id,name,text,type)
values(
59,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>F&ouml;r att ta bort en diskussion: <img border="0" src="'+@webroot+'/images/se/helpimages/Konf-a3.GIF" width="13" height="14">
        f&ouml;r den diskussion som du vill ta bort och klicka sedan p&aring; "TA
        BORT".
        <p>F&ouml;r att l&auml;mna administrationsl&auml;get: klicka p&aring; "Avsluta admin".</td>
    </tr>
  </table>
  </center>
</div>',1
)
insert into texts(meta_id,name,text,type)
values(
60,1,'Konferens - administrera forum',0
)
insert into texts(meta_id,name,text,type)
values(
60,2,'<div align="center">
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
        <p><i>Antalet diskussioner som visas:</i> H&auml;r anges hur m&aring;nga
        diskussioner som skall visas &aring;t g&aring;ngen i ett forum. Markera det forum
        du vill &auml;ndra antalet diskussioner i genom att klicka p&aring; det i listan
        till h&ouml;ger. Det befintliga antalet diskussioner som visas i forumet
        visas inom parentes i listan. V&auml;lj sedan det&nbsp; nya antalet
        diskussioner som skall visas genom att klicka p&aring; <img border="0" src="'+@webroot+'/images/se/helpimages/Pil.GIF" width="16" height="21">
        och markera det antalet. Klicka sedan p&aring; "Uppdatera".</td>
    </tr>
  </table>
  </center>
</div>',1
)
insert into texts(meta_id,name,text,type)
values(
61,1,'Konferens - administrera inl&auml;gg',0
)
insert into texts(meta_id,name,text,type)
values(
61,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td><b>Ta bort en kommentar:</b> <img border="0" src="'+@webroot+'/images/se/helpimages/Konf-a3.GIF" width="13" height="14">
        f&ouml;r den/de kommentarer du vill ta bort och klicka sedan p&aring; "TA
        BORT". En varningsbild visas d&auml;r du f&aring;r bekr&auml;fta att du
        verkligen vill ta bort inl&auml;gget. Inl&auml;gget kommer d&aring; att tas bort
        fr&aring;n diskussionen. OBS att diskussionens f&ouml;rsta inl&auml;gg, dvs, det som
        initierade diskussionen <b>inte</b> kan tas bort. Det g&aring;r dock att
        redigera inl&auml;gget alternativt ta bort hela diskussionen. Var d&aring;
        medveten om att samtliga inl&auml;gg i diskussionen f&ouml;rsvinner.
        <p><b>Spara om en kommentar: </b>F&ouml;r att &auml;ndra ett befintligt inl&auml;gg,
        &auml;ndra den text som skall uppdateras och markera inl&auml;gget genom att <img border="0" src="'+@webroot+'/images/se/helpimages/Konf-a3.GIF" width="13" height="14">
        f&ouml;r det. Klicka sedan p&aring; "SPARA OM".</p>
        <p>F&ouml;r att &aring;terg&aring; till anv&auml;ndarl&auml;ge, klicka p&aring; "Avsluta admin".</td>
    </tr>
  </table>
  </center>
</div>',1
)
insert into texts(meta_id,name,text,type)
values(
62,1,'Konferens - administrera mallset',0
)
insert into texts(meta_id,name,text,type)
values(
62,2,'<div align="center">
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
        befintligt mallset, v&auml;lj f&ouml;rst vilket set som skall uppdateras och
        sedan vilken typ av mall det &auml;r som skall uppdateras (Konferensen
        st&ouml;der tv&aring; typer av filer: bilder och html-filer ). N&auml;r dessa val &auml;r
        gjorda, klicka p&aring; &quot;Administrera&quot;.</p>
        <p>F&ouml;r att avsluta administrationen, klicka p&aring; &quot;Avsluta admin&quot;.</td>
    </tr>
  </table>
  </center>
</div>
<p align="center">&nbsp;</p>',1
)
insert into texts(meta_id,name,text,type)
values(
63,1,'Konferens - administrera sj&auml;lvregistrering',0
)
insert into texts(meta_id,name,text,type)
values(
63,2,'<div align="center">
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
        den rollen.</td>
    </tr>
  </table>
  </center>
</div>',1
)
insert into texts(meta_id,name,text,type)
values(
64,1,'Konferens - &auml;ndra befintlig mallfil',0
)
insert into texts(meta_id,name,text,type)
values(
64,2,'<div align="center">
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
</div>',1
)
insert into texts(meta_id,name,text,type)
values(
65,1,'Konferens - inloggning',0
)
insert into texts(meta_id,name,text,type)
values(
65,2,'<div align="center">
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
</div>',1
)
insert into texts(meta_id,name,text,type)
values(
66,1,'Konferensvy',0
)
insert into texts(meta_id,name,text,type)
values(
66,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>Varje konferens har minst ett forum d&auml;r olika diskussionen p&aring;g&aring;r.
        V&auml;lj vilket forum du vill genom att klicka p&aring; <img border="0" src="'+@webroot+'/images/se/helpimages/Pil.GIF" width="16" height="21">
        under rubriken <i>V&auml;lj forum, </i>markera forumet och klicka p&aring;
        "V&auml;lj". De diskussioner som visas i f&ouml;nstrets v&auml;nstra del &auml;r
        rubrikerna till de f&ouml;rsta inl&auml;ggen som skrivits i respektive
        diskussion. F&ouml;r att leta bland de diskussioner som inte visas anv&auml;nds
        knapparna "Tidigare inl&auml;gg" och "Senare inl&auml;gg. Om <img border="0" src="'+@webroot+'/images/se/helpimages/Konf-k4.GIF" width="21" height="15">&nbsp;
        (Ny-symbolen) visas framf&ouml;r en diskussion s&aring; inneb&auml;r det att antingen
        &auml;r detta en ny diskussion eller s&aring; har det tillkommit nya inl&auml;gg
        sedan du sist var inloggad i konferensen. F&ouml;r att visa de inl&auml;gg som
        tillkommit sedan du sist var inloggad, klicka p&aring; "Uppdatera".
        <p>Genom att klicka p&aring; diskussionsrubriken visas alla inl&auml;gg i
        diskussionen i f&ouml;nstrets h&ouml;gra del. Om <img border="0" src="'+@webroot+'/images/se/helpimages/Konf-k3.GIF" width="14" height="16">
        (specialist-symbolen) visas framf&ouml;r rubriken inneb&auml;r det att inl&auml;gget
        skrivits av en anv&auml;ndare som i den h&auml;r konferensen &auml;r n&aring;gon form av
        specialist. Efter den eventuella "specialistsymbolen" f&ouml;ljer
        inl&auml;ggets rubrik, inl&auml;ggstext, f&ouml;rfattare samt datum n&auml;r inl&auml;gget
        skapades.</p>
        <p>Det g&aring;r att ange hur kommentarerna skall sorteras genom att klicka i
        <i>"Stigande" </i>eller <i>"</i><i>F</i><i>allande"</i>.
        Klicka sedan p&aring; "Sortera". Kommentarerna sorteras d&aring; efter
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
        s&ouml;kordet skrivas in. Klicka sedan p&aring; "S&ouml;k". S&ouml;kningen kan
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
        <p>&nbsp;</td>
    </tr>
  </table>
  </center>
</div>',1
)
insert into texts(meta_id,name,text,type)
values(
67,1,'Konferens - sj&auml;lvregistrering',0
)
insert into texts(meta_id,name,text,type)
values(
67,2,'<div align="center">
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
</div>',1
)
insert into texts(meta_id,name,text,type)
values(
68,1,'Konferens - konferensdata',0
)
insert into texts(meta_id,name,text,type)
values(
68,2,'<div align="center">
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
        <p align="center"><i>Forum namn: </i>Skriv in det namn som konferensen
        skall ha.</p>
        <p align="center">Klicka sedan p&aring; &quot;OK&quot;.</td>
    </tr>
  </table>
</div>',1
)
insert into texts(meta_id,name,text,type)
values(
69,1,'Konferens - skapa en ny diskussion',0
)
insert into texts(meta_id,name,text,type)
values(
69,2,'<div align="center">
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
</div>',1
)
insert into texts(meta_id,name,text,type)
values(
70,1,'Konferens - skapa en ny kommentar',0
)
insert into texts(meta_id,name,text,type)
values(
70,2,'<div align="center">
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
</div>',1
)
insert into texts(meta_id,name,text,type)
values(
71,1,'L&auml;gga till/redigera anv&auml;ndare',0
)
insert into texts(meta_id,name,text,type)
values(
71,2,'<div align="center">
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
                    &auml;ndras/tas bort genom att klicka p&aring; <img border="0" src="'+@webroot+'/images/se/helpimages/Pil.GIF" width="17" height="22">.
                    N&auml;r telefonnr &auml;r markerat - klicka p&aring; den knapp som
                    g&auml;ller f&ouml;r det du vill g&ouml;ra. Om "&Auml;ndra" &auml;r
                    valet, kommer telefonnr att visas i rutorna och sedan kan
                    man &auml;ndra det som skall &auml;ndras och till sist klicka p&aring;
                    "OK". </li>
                  <li>
                    <p align="left"><i>Aktiverad - </i>en <img border="0" src="'+@webroot+'/images/se/helpimages/Lagg-t1.GIF" width="13" height="14">
                    g&ouml;r att anv&auml;ndaren &auml;r aktiverad och kan logga in. <img border="0" src="'+@webroot+'/images/se/helpimages/Lagg-t2.GIF" width="13" height="14">
                    kan tas bort om anv&auml;ndaren inte l&auml;ngre skall kunna logga
                    in i systemet.</li>
                  <li>
                    <p align="left"><i>Anv&auml;ndartyp </i>- autentiserade
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
                <p align="left">"Avbryt" - rensar alla uppgifter och
                f&ouml;reg&aring;ende sida visas.</p>
      </td>
    </tr>
  </table>
  </center>
</div>',1
)
insert into texts(meta_id,name,text,type)
values(
72,1,'L&auml;gga till bild',0
)
insert into texts(meta_id,name,text,type)
values(
72,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
<h3 align="center">L&auml;gg till bild</h3>
<blockquote>
  <blockquote>
      <p align="left">Antingen kan en bild l&auml;ggas in genom att anv&auml;nda&nbsp;
      "Browse"-knappen eller "S&ouml;k"-knappen, (vad knappen
      heter styrs av vilket spr&aring;k webbl&auml;saren&nbsp; anv&auml;nder) eller genom att
      klicka p&aring; "Bildarkiv"-knappen.</p>
        <ul>
          <li>
            <p align="left">Anv&auml;nds "Browse"-knappen eller "S&ouml;k"-knappen
            s&ouml;ks bilden fram p&aring; det egna n&auml;tverket. </li>
        </ul>
        <ul>
          <li>
            <p align="left">Anv&auml;nds "Bildarkivet" g&ouml;rs s&ouml;kningen
            bland de bilder som redan finns uppladdade i systemet. </li>
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
            <td width="100%"><b>Bild:</b> H&auml;r visas s&ouml;kv&auml;gen till bilden n&auml;r
              den h&auml;mtas fr&aring;n det egna n&auml;tverket.</td>
          </tr>
          <tr>
            <td width="100%"><b>F&auml;ltet under MetaId: </b>F&auml;ltet visas endast
              om det finns en bild inlagd p&aring; sidan (eller om man klickat p&aring;
              "F&ouml;rhandsgranska bilden"). Bilden visas d&aring; h&auml;r.</td>
          </tr>
          <tr>
            <td width="100%"><b>Bild 3: </b>H&auml;r visas s&ouml;kv&auml;gen till bilden
              n&auml;r bilden h&auml;mtas fr&aring;n bildarkivet. Klicka p&aring;
              "F&ouml;rhandsgranska bilden" s&aring; visas bilden. (Siffran
              anger vilket bildf&auml;lt p&aring; sidan som &aring;syftas).</td>
          </tr>
          <tr>
            <td width="100%"><b>Bildnamn: </b>H&auml;r kan ett namn p&aring; bilden
              anges, det visas dock ingenstans p&aring; sidan.</td>
          </tr>
          <tr>
            <td width="100%"><b>Format: </b>Bildens bredd, h&ouml;jd och om n&aring;gon
              ram (kant) skall finnas runt bilden. Alla m&aring;tt anges i pixlar.</td>
          </tr>
          <tr>
            <td width="100%"><b>N&auml;sta textf&auml;lts placering: </b>H&auml;r v&auml;ljs var
              texten kring bilden kommer att hamna. OBS att det beror p&aring;
              utseendemallen om dessa val fungerar eller ej. Klicka p&aring;<b> <img border="0" src="'+@webroot+'/images/se/helpimages/Lagg-t3.GIF">
              </b>f&ouml;r att v&auml;lja alternativ.
              <ul>
                <li><i>Ingen: </i>Textens placering styrs av webbl&auml;sarens
                  default-inst&auml;llning.</li>
                <li><i>Baslinjen: </i>Texten b&ouml;rjar vid nedre h&ouml;gra h&ouml;rnet av
                  bilden och forts&auml;tter sedan under bilden.</li>
                <li><i>Toppen: </i>Textens f&ouml;rsta rad b&ouml;rjar vid bildens &ouml;vre
                  h&ouml;gre h&ouml;rn och rad tv&aring; forts&auml;tter sedan under bilden.</li>
                <li><i>Mitten: </i>Textens f&ouml;rsta rad b&ouml;rjar vid bildens mitt
                  (till h&ouml;ger om bilden) och rad tv&aring; forts&auml;tter sedan under
                  bilden.</li>
                <li><i>Botten: </i>Texten b&ouml;rjar vid nedre h&ouml;gra h&ouml;rnet av
                  bilden och forts&auml;tter sedan under bilden.</li>
                <li><i>Texttoppen: </i>Textens h&ouml;gsta del placeras i h&ouml;jd med
                  bildens &ouml;vre kant (till h&ouml;ger om bilden).</li>
                <li><i>Exakt mitten: </i>Textens mittpunkt (h&ouml;jdm&auml;ssigt)
                  hamnar vid bildens mitt (till h&ouml;ger om bilden).&nbsp;</li>
                <li><i>L&auml;ngst ner: </i>Textens l&auml;gsta del (som bokstaven g)
                  placeras i h&ouml;jd med bildens nedre kant (till h&ouml;ger om
                  bilden).</li>
                <li><i>Bild v&auml;nster: </i>Texten placeras till h&ouml;ger om bilden.</li>
                <li><i>Bild h&ouml;ger: </i>Texten placeras till v&auml;nster om bilden.</li>
              </ul>
            </td>
          </tr>
          <tr>
            <td width="100%"><b>Bildtext under uppladdning: </b>Text som visas
              under tiden bilden laddas upp (l&auml;mpligt om det &auml;r en v&auml;ldigt
              stor bild som tar l&aring;ng tid att ladda upp).</td>
          </tr>
          <tr>
            <td width="100%"><b>Alt bild under uppladdning: </b>Bild som visas
              under tiden bilden laddas upp (l&auml;mpligt om det &auml;r en v&auml;ldigt
              stor bild som tar l&aring;ng tid att ladda upp).</td>
          </tr>
          <tr>
            <td width="100%"><b>"Luft" kring bilden: </b>H&auml;r anges om
              det skall finnas ett tomt utrymme (luft)&nbsp; kring bilden.
              Storleken p&aring; "luften" anges i pixlar. B&aring;de vertikal
              och horisontell "luft" anges.</td>
          </tr>
          <tr>
            <td width="100%"><b>L&auml;nkad till www: </b>Om bilden skall vara
              klickbar och l&auml;nka till en webbsida anges Internet-adressen
              h&auml;r.&nbsp;</td>
          </tr>
          <tr>
            <td width="100%"><b>L&auml;nk &ouml;ppnas i: </b>H&auml;r v&auml;ljs i vilket
              f&ouml;nster/frame l&auml;nken skall &ouml;ppnas. Klicka p&aring;<b> <img border="0" src="'+@webroot+'/images/se/helpimages/Lagg-t3.GIF">
              </b>f&ouml;r att v&auml;lja alternativ.
              <ul>
                <li><i>Aktuellt f&ouml;nster: </i>&Ouml;ppnar sidan som bilden l&auml;nkar
                  till i det &ouml;versta f&ouml;nstret (OBS ej frame)</li>
                <li><i>Nytt f&ouml;nster: </i>Sidan &ouml;ppnas i ett nytt f&ouml;nster.</li>
                <li><i>Moderram: </i>Sidan &ouml;ppnas i framen eller f&ouml;nstret som
                  inneh&aring;ller framesetet.</li>
                <li><i>Samma ram: </i>&Ouml;ppnar sidan i den nuvarande framen eller
                  f&ouml;nstret (som bilden ligger i).</li>
                <li><i>Annan ram: </i>&Ouml;ppnar sidan i en annan frame. Namnet p&aring;
                  framen anges i det vita f&auml;ltet till h&ouml;ger.</li>
              </ul>
            </td>
          </tr>
        </table>
</div align="Center">',1
)
insert into texts(meta_id,name,text,type)
values(
73,1,'L&auml;gga till bild - Browse/S&ouml;k',0
)
insert into texts(meta_id,name,text,type)
values(
73,2,'<div align="center">
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
<p align="center">&nbsp;&nbsp;&nbsp; </p>',1
)
insert into texts(meta_id,name,text,type)
values(
74,1,'L&auml;gga till l&auml;nk till en fil - sida 2',0
)
insert into texts(meta_id,name,text,type)
values(
74,3,'<div align="center">
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
        filtyp &auml;n de alternativen som g&aring;r att v&auml;lja i rullgardinslisten v&auml;ljer
        man alternativet "<b>Annan</b>" och skriver in i f&auml;ltet "<b style="mso-bidi-font-weight:normal">Annan</b>"
        vilken filtyp det &auml;r.</p>
        <p class="MsoBodyText" align="left">Klicka p&aring; "OK".</p>
        </blockquote>
  <center>',1
)
insert into texts(meta_id,name,text,type)
values(
75,1,'L&auml;gga till l&auml;nk till HTML-dokument - sida 1',0
)
insert into texts(meta_id,name,text,type)
values(
75,2,'<h3 align="center">L&auml;gga till dokument</h3>
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
      <td width="100%"><b>S&ouml;kord: </b>H&auml;r anges de s&ouml;kord som skall ge tr&auml;ff
        vid s&ouml;kning. En <img border="0" src="'+@webroot+'/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
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
        anges. En <img border="0" src="'+@webroot+'/images/se/helpimages/Lagg-t5.GIF" width="13" height="14"> i
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
<p align="center">&nbsp;</p>',1
)
insert into texts(meta_id,name,text,type)
values(
76,1,'L&auml;gga till l&auml;nk till HTML-dokument - sida 2',0
)
insert into texts(meta_id,name,text,type)
values(
76,2,'<div align="center">
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
</div>',1
)
insert into texts(meta_id,name,text,type)
values(
77,1,'L&auml;gga till l&auml;nk till Text-dokument - sida 1',0
)
insert into texts(meta_id,name,text,type)
values(
77,2,'<h3 align="center">L&auml;gga till dokument</h3>
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
      <td width="100%"><b>S&ouml;kord: </b>H&auml;r anges de s&ouml;kord som skall ge tr&auml;ff
        vid s&ouml;kning. En <img border="0" src="'+@webroot+'/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
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
        anges. En <img border="0" src="'+@webroot+'/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
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
<p align="center">&nbsp;</p>',1
)
insert into texts(meta_id,name,text,type)
values(
78,1,'L&auml;gga till l&auml;nk till Text-dokument - sida 2',0
)
insert into texts(meta_id,name,text,type)
values(
78,2,'<div align="center">
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
                <p> OBS fullst&auml;ndig HTML-kod med start- och sluttagg beh&ouml;ver
                inte skrivas.&nbsp;</p>
</td>
    </tr>
  </table>
  </center>
</div>',1
)
insert into texts(meta_id,name,text,type)
values(
78,3,'<br><h3 align="center">Exempel HTML-format</h3>',1
)
insert into texts(meta_id,name,text,type)
values(
79,1,'L&auml;gga till l&auml;nk - funktion',0
)
insert into texts(meta_id,name,text,type)
values(
79,3,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
<p align="center">Klicka p&aring; <img border="0" src="'+@webroot+'/images/se/helpimages/Pil.GIF" width="16" height="21">
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
      som &auml;r styrd av vilken webbl&auml;sare man har. Olika webbl&auml;sare kan g&ouml;ra
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
<h2 align="center">Administrera befintliga l&auml;nkar</h2>',1
)
insert into texts(meta_id,name,text,type)
values(
79,4,'<p align="center">&nbsp;</p>
        <blockquote>
            <p align="left">Genom att <img border="0" src="'+@webroot+'/images/se/helpimages/Lank.h6.GIF" width="13" height="14">
            f&ouml;r en l&auml;nk kan den tas bort, arkiveras eller kopieras genom att klicka p&aring;
            respektive knapp. N&auml;r en l&auml;nk &auml;r arkiverad visas det genom att
            l&auml;nken &auml;r &ouml;verstruken (se bilden - Dokument 1). Arkiveringen kan
            tas bort via Admin-knappen "Dokinfo". F&ouml;r att sortera
            l&auml;nkarna kan siffror anges i respektive ruta framf&ouml;r l&auml;nken.
            Numreringen &auml;r helt valfri. H&ouml;gst nummer hamnar h&ouml;gst upp. Klicka
            sedan p&aring; "Sortera".&nbsp;</p>
            <p align="left">N&auml;r man klickar p&aring; "Kopiera"-knappen
            skapas ett exakt likadant dokument som det som &auml;r markerat och det
            kommer s&aring;ledes att visas tv&aring; l&auml;nkar med samma l&auml;nkrubrik, den
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
<p align="center">&nbsp;</p>',1
)
insert into texts(meta_id,name,text,type)
values(
80,1,'L&auml;gga till l&auml;nk till en fil - sida 1',0
)
insert into texts(meta_id,name,text,type)
values(
80,2,'<h3 align="center">L&auml;gga till dokument</h3>
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
      <td width="100%"><b>S&ouml;kord: </b>H&auml;r anges de s&ouml;kord som skall ge tr&auml;ff
        vid s&ouml;kning. En <img border="0" src="'+@webroot+'/images/se/helpimages/Lank-U1.GIF" width="13" height="14">
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
        anges. En <img border="0" src="'+@webroot+'/images/se/helpimages/Lank-U1.GIF" width="13" height="14">
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
<p>&nbsp;</p>',1
)
insert into texts(meta_id,name,text,type)
values(
81,1,'L&auml;gga till l&auml;nk till URL-dokument - sida 1',0
)
insert into texts(meta_id,name,text,type)
values(
81,2,'<h3 align="center">L&auml;gga till dokument</h3>
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
        vid s&ouml;kning. En <img border="0" src="'+@webroot+'/images/se/helpimages/Lank-U1.GIF" width="13" height="14">
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
        anges. En <img border="0" src="'+@webroot+'/images/se/helpimages/Lank-U1.GIF" width="13" height="14">
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
sj&auml;lva Internet-adressen skrivs in.</b></p>
',1
)
insert into texts(meta_id,name,text,type)
values(
82,1,'L&auml;gga till l&auml;nk till URL-dokument - sida 2',0
)
insert into texts(meta_id,name,text,type)
values(
82,2,'<div align="center">
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
</div>',1
)
insert into texts(meta_id,name,text,type)
values(
83,1,'Misslyckad inloggning',0
)
insert into texts(meta_id,name,text,type)
values(
83,2,'<p align="center">Skriv in anv&auml;ndarnamn och l&ouml;senord igen.</p>
<p align="center">Om du gl&ouml;mt l&ouml;senordet - klicka p&aring; den bl&aring; l&auml;nken.</p>
<p align="center">Om du gl&ouml;mt anv&auml;ndarnamnet - ta kontakt med
systemadministrat&ouml;ren.</p>',1
)
insert into texts(meta_id,name,text,type)
values(
84,1,'R&auml;ttigheter',0
)
insert into texts(meta_id,name,text,type)
values(
84,2,'<p align="center">H&auml;r styrs vilken/vilka roller som skall f&aring; g&ouml;ra vad med
sidan. </p>
<div align="CENTER">
<table border="1" width="100%">
  <tr>
    <td width="100%"><b>Rubrik: </b>Visar dokumentets rubrik (l&auml;nktexten).
      Genom att <img border="0" src="'+@webroot+'/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
      f&ouml;r <i>Visa rubrik &auml;ven om anv&auml;ndaren &auml;r obeh&ouml;rig</i> till&aring;ts
      obeh&ouml;riga att se l&auml;nken men kan inte komma in p&aring; sj&auml;lva sidan.</td>
  </tr>
  <tr>
    <td width="100%"><b>Beh&ouml;righet: </b>Visar vilka roller som har n&aring;gon form
      av r&auml;ttighet p&aring; sidan. 
      <ul>
        <li><i>Ingen </i>betyder att l&auml;nken inte visas f&ouml;r rollen, om inte <i>Visa
          rubrik &auml;ven om anv&auml;ndaren &auml;r obeh&ouml;rig </i>&auml;r <img border="0" src="'+@webroot+'/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">.
        </li>
        <li><i>L&auml;sa</i> betyder att anv&auml;ndare med rollen kan l&auml;sa allt p&aring;
          sidan, men inte kan &auml;ndra n&aring;got p&aring; den.</li>
        <li><i>Begr.2 </i>betyder att anv&auml;ndare med rollen kan g&ouml;ra de saker
          som Begr.2 till&aring;ts g&ouml;ra. F&ouml;r att &auml;ndra r&auml;ttigheter f&ouml;r Begr.2 se
          <b>Definiera beh&ouml;righeter.</b></li>
        <li><i>Begr.1 </i>betyder att anv&auml;ndare med rollen kan g&ouml;ra de saker
          som Begr.1 till&aring;ts g&ouml;ra. F&ouml;r att &auml;ndra r&auml;ttigheter f&ouml;r Begr.2 se
          <b>Definiera beh&ouml;righeter.</b><i> </i></li>
        <li><i>Full </i>betyder att anv&auml;ndare med den rollen har fullst&auml;ndiga
          r&auml;ttigheter p&aring; sidan och kan &auml;ndra allting.</li>
      </ul>
      <p>R&auml;ttigheterna &auml;ndras genom att klicka i den vita cirkeln f&ouml;r
      l&auml;mplig r&auml;ttighet. Om Ingen markeras kommer rollen att flyttas ned till <i>Roller
      utan beh&ouml;righet</i> n&auml;r man klickar p&aring; "Spara".</p>
    </td>
  </tr>
  <tr>
    <td width="100%">
      <p align="left"><b>Roller utan beh&ouml;righet: </b>Genom att markera en roll
      och klicka p&aring; "L&auml;gg till" kan fler roller ges r&auml;ttigheter p&aring;
      sidan. Rollen flyttas upp till <b>Beh&ouml;righet </b>och r&auml;ttigheten kan
      markeras.</p>
    </td>
  </tr>
  <tr>
    <td width="100%"><b>Definiera beh&ouml;righet: </b>Genom att klicka p&aring;
      "Definiera" vid antingen <i>Begr&auml;nsad beh&ouml;righet 1 </i>eller <i>Begr&auml;nsad
      beh&ouml;righet 2 </i>f&aring;s den sida upp d&auml;r dessa r&auml;ttigheter kan &auml;ndras.
      En <img border="0" src="'+@webroot+'/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
      betyder att Begr&auml;nsad 1 &auml;r mer priviligerad &auml;n Begr&auml;nsad 2 f&ouml;r den
      h&auml;r sidan. Om den rutan skall bockas f&ouml;r beror p&aring; hur beh&ouml;righeterna
      f&ouml;r respektive &auml;r inst&auml;lld. Denna&nbsp; <img border="0" src="'+@webroot+'/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
      &auml;r en hj&auml;lp s&aring; att administrat&ouml;ren av sidan l&auml;tt skall kunna se
      vilken begr&auml;nsning som har mest r&auml;ttigheter att g&ouml;ra saker p&aring; sidan.
      "Definiera f&ouml;r nya dokument" betyder att r&auml;ttigheterna st&auml;lls
      in f&ouml;r sidor som skapas fr&aring;n aktuell sida. Default &auml;r att de &auml;rver de
      r&auml;ttigheter som ursprungssidan har. </td>
  </tr>
  <tr>
    <td width="100%"><b>Skapad av: </b>(namnet p&aring; den anv&auml;ndare som skapat
      sidan). Genom en <img border="0" src="'+@webroot+'/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
      vid <i>Dela ut dokumentet </i>ges m&ouml;jlighet f&ouml;r andra anv&auml;ndare att
      l&auml;nka till sidan fr&aring;n "sina" dokument.</td>
  </tr>
</table>
</div align="CENTER">',1
)
insert into texts(meta_id,name,text,type)
values(
85,1,'Ta bort ett dokument',0
)
insert into texts(meta_id,name,text,type)
values(
85,2,'<div align="center">
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
</div>',1
)
insert into texts(meta_id,name,text,type)
values(
86,1,'Ta bort ett dokument - varning',0
)
insert into texts(meta_id,name,text,type)
values(
86,2,'<p align="center">Klicka p&aring; &quot;OK&quot; om du &auml;r s&auml;ker p&aring; att du vill ta
bort dokumentet.</p>
<p align="center">&quot;Cancel&quot; leder tillbaka till f&ouml;reg&aring;ende sida utan
att dokumentet tas bort.</p>',1
)
insert into texts(meta_id,name,text,type)
values(
87,1,'&Auml;ndra utseende p&aring; dokumentet',0
)
insert into texts(meta_id,name,text,type)
values(
87,2,'<div align="center">
  <center>
  <table border="0" cellpadding="0" cellspacing="0" width="550">
    <tr>
      <td>
            <p align="left">Aktuell formatgrupp och mall visas. F&ouml;r att &auml;ndra
            utseendet - klicka p&aring; <img border="0" src="'+@webroot+'/images/se/helpimages/Pil.GIF" width="16" height="21">
            vid format-mallen. Om mallen som du vill ha ligger i en annan
            formatgrupp m&aring;ste du f&ouml;rst klicka p&aring; <img border="0" src="'+@webroot+'/images/se/helpimages/Pil.GIF" width="16" height="21">
            vid formatgrupp, v&auml;lja ny formatgrupp och klicka p&aring; "&Auml;ndra
            grupp". De mallar som tillh&ouml;r den nu valda formatgruppen visas
            vid klick p&aring; <img border="0" src="'+@webroot+'/images/se/helpimages/Pil.GIF" width="16" height="21">
            vid formatmall. Markera formatmallen och klicka sedan p&aring;
            "Spara". F&ouml;r att se hur mallen ser ut, klicka p&aring;
            "Visa mall". OBS fungerar bara om det finns en exempelmall
            kopplad till denna mall.</p>
      </td>
    </tr>
  </table>
  </center>
</div>',1
)
insert into texts(meta_id,name,text,type)
values(
88,1,'Ta bort roll - varning',0
)
insert into texts(meta_id,name,text,type)
values(
88,2,'Om du &auml;r s&auml;ker p&aring; att du vill ta bort rollen - klicka p&aring; &quot;OK&quot;, annars klicka p&aring; &quot;Avbryt&quot;.',0
)
insert into texts(meta_id,name,text,type)
values(
89,1,'Administrera roller - huvudsida',0
)
insert into texts(meta_id,name,text,type)
values(
89,2,'<div align="center">
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
  </center>',1
)
insert into texts(meta_id,name,text,type)
values(
90,1,'L&auml;gga till l&auml;nk - sida 1',0
)
insert into texts(meta_id,name,text,type)
values(
90,2,'<h3 align="center">L&auml;gga till dokument</h3>
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
      <td width="100%"><b>S&ouml;kord: </b>H&auml;r anges de s&ouml;kord som skall ge tr&auml;ff
        vid s&ouml;kning. En <img border="0" src="'+@webroot+'/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
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
        anges. En <img border="0" src="'+@webroot+'/images/se/helpimages/Lagg-t4.GIF" width="13" height="14">
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
Efter det att man fyllt i denna sida och man klickat "Ok", kommer n&auml;sta inst&auml;llningssida upp. Vad den inneh&aring;ller beror p&aring; vilken typ av l&auml;nk man valt att l&auml;gga till.<br><br>

<i>Klicka p&aring; hj&auml;lpknappen p&aring; n&auml;sta sida f&ouml;r att se hj&auml;lp om denna funktion.</i></b></p>',1
)




declare @groupId int
select @groupId = max(group_id)+1 from templategroups

--ok lets create the templategroup
insert into templategroups(group_id,group_name)
values(@groupId,'imCMShelp')

--ok lets add the templates
INSERT INTO templates ( template_id , template_name , simple_name , lang_prefix , no_of_txt , no_of_img , no_of_url )
values (2,'Helpmenu.html','Helpmenu','se',0,0,0)
INSERT INTO templates ( template_id , template_name , simple_name , lang_prefix , no_of_txt , no_of_img , no_of_url )
values (3,'Help.html','Help','se',4,2,0)

--ok lets relate templates and templategroups
insert into templates_cref(group_id,template_id)
values (@groupId,2)
insert into templates_cref(group_id,template_id)
values (@groupId,3)

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

--lets set all the rolerights



INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,1,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,2,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,3,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,4,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,5,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,6,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,7,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,8,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,9,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,10,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,11,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,12,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,13,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,14,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,15,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,16,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,17,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,18,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,19,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,20,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,21,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,22,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,23,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,24,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,25,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,26,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,27,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,28,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,29,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,30,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,31,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,32,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,33,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,34,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,35,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,36,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,37,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,38,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,39,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,40,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,41,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,42,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,43,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,44,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,45,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,46,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,47,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,48,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,49,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,50,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,51,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,52,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,53,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,54,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,55,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,56,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,57,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,58,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,59,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,60,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,61,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,62,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,63,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,64,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,65,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,66,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,67,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,68,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,69,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,70,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,71,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,72,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,73,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,74,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,75,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,76,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,77,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,78,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,79,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,80,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,81,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,82,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,83,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,84,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,85,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,86,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,87,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,88,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,89,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,90,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,91,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,92,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,93,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,94,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,95,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,96,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,97,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,98,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,99,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,100,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,101,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,102,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,103,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,104,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,105,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,106,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,107,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,108,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,109,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,110,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,111,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,112,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,113,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,114,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,115,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,116,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,117,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,118,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,119,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,120,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,121,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,122,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,123,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,124,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,125,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,126,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,127,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,128,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,129,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,130,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,131,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,132,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,133,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,134,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,135,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,136,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,137,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,138,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,139,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,140,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,141,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,142,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,143,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,144,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,145,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,146,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,147,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,148,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,149,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,150,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,151,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,152,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,153,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,154,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,155,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,156,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,157,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,158,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,159,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,160,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,161,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,162,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,163,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,164,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,165,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,166,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,167,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,168,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,169,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,170,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,171,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,172,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,173,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,174,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,175,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,176,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,177,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,178,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,179,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,180,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,181,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,182,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,183,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,184,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,185,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,186,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,187,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,188,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,189,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,190,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,191,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,192,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,193,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,194,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,195,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,196,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,197,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,198,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,199,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,199,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (1,200,3)

--sets superadmin rights


INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,1,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,2,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,3,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,4,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,5,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,6,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,7,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,8,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,9,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,10,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,11,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,12,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,13,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,14,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,15,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,16,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,17,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,18,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,19,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,20,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,21,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,22,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,23,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,24,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,25,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,26,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,27,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,28,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,29,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,30,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,31,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,32,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,33,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,34,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,35,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,36,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,37,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,38,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,39,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,40,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,41,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,42,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,43,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,44,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,45,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,46,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,47,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,48,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,49,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,50,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,51,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,52,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,53,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,54,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,55,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,56,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,57,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,58,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,59,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,60,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,61,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,62,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,63,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,64,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,65,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,66,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,67,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,68,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,69,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,70,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,71,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,72,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,73,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,74,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,75,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,76,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,77,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,78,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,79,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,80,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,81,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,82,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,83,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,84,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,85,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,86,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,87,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,88,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,89,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,90,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,91,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,92,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,93,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,94,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,95,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,96,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,97,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,98,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,99,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,100,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,101,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,102,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,103,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,104,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,105,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,106,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,107,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,108,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,109,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,110,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,111,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,112,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,113,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,114,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,115,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,116,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,117,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,118,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,119,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,120,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,121,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,122,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,123,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,124,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,125,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,126,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,127,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,128,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,129,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,130,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,131,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,132,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,133,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,134,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,135,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,136,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,137,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,138,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,139,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,140,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,141,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,142,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,143,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,144,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,145,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,146,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,147,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,148,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,149,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,150,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,151,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,152,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,153,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,154,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,155,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,156,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,157,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,158,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,159,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,160,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,161,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,162,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,163,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,164,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,165,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,166,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,167,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,168,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,169,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,170,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,171,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,172,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,173,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,174,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,175,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,176,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,177,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,178,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,179,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,180,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,181,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,182,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,183,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,184,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,185,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,186,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,187,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,188,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,189,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,190,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,191,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,192,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,193,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,194,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,195,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,196,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,197,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,198,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,199,3)
INSERT INTO roles_rights (role_id, meta_id ,set_id )
values (0,200,3)

END