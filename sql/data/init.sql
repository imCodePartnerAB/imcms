-- mysql SET sql_mode = 'NO_AUTO_VALUE_ON_ZERO';

INSERT INTO languages VALUES('swe','swe','Svenska');
INSERT INTO languages VALUES('swe','eng','Swedish');
INSERT INTO languages VALUES('eng','swe','Engelska');
INSERT INTO languages VALUES('eng','eng','English');

INSERT INTO lang_prefixes VALUES(1,'swe');
INSERT INTO lang_prefixes VALUES(2,'eng');

INSERT INTO doc_types VALUES(2, 'swe', 'Textsida');
INSERT INTO doc_types VALUES(5, 'swe', 'Extern länk');
INSERT INTO doc_types VALUES(6, 'swe', 'Browserkontroll');
INSERT INTO doc_types VALUES(7, 'swe', 'HTML-dokument');
INSERT INTO doc_types VALUES(8, 'swe', 'Fil');

INSERT INTO doc_types VALUES(2, 'eng', 'Text page');
INSERT INTO doc_types VALUES(5, 'eng', 'External link');
INSERT INTO doc_types VALUES(6, 'eng', 'Browser controlled link');
INSERT INTO doc_types VALUES(7, 'eng', 'HTML-document');
INSERT INTO doc_types VALUES(8, 'eng', 'File');

INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(65536,2,'swe','Ändra text');
INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(65536,5,'swe','Redigera');
INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(65536,6,'swe','Redigera');
INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(65536,7,'swe','Redigera');
INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(65536,8,'swe','Redigera');
INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(65536,101,'swe','Redigera');
INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(65536,102,'swe','Redigera');
INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(131072,2,'swe','Ändra bild');
INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(262144,2,'swe','Ändra meny');
INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(524288,2,'swe','Ändra utseende');
INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(1048576,2,'swe','Ändra include');

INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(65536,2,'eng','Edit texts');
INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(65536,5,'eng','Edit');
INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(65536,6,'eng','Edit');
INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(65536,7,'eng','Edit');
INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(65536,8,'eng','Edit');
INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(65536,101,'eng','Edit');
INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(65536,102,'eng','Edit');
INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(131072,2,'eng','Edit pictures');
INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(262144,2,'eng','Edit menus');
INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(524288,2,'eng','Change template');
INSERT INTO doc_permissions (permission_id, doc_type, lang_prefix, description) VALUES(1048576,2,'eng','Change include');

INSERT INTO permissions (permission_id, lang_prefix, description) VALUES(1,'swe','Ändra rubrik');
INSERT INTO permissions (permission_id, lang_prefix, description) VALUES(2,'swe','Ändra dokinfo');
INSERT INTO permissions (permission_id, lang_prefix, description) VALUES(4,'swe','Ändra rättigheter för roller');
INSERT INTO permissions (permission_id, lang_prefix, description) VALUES(8,'swe','Skapa dokument');

INSERT INTO permissions (permission_id, lang_prefix, description) VALUES(1,'eng','Edit headline');
INSERT INTO permissions (permission_id, lang_prefix, description) VALUES(2,'eng','Edit docinfo');
INSERT INTO permissions (permission_id, lang_prefix, description) VALUES(4,'eng','Edit permissions');
INSERT INTO permissions (permission_id, lang_prefix, description) VALUES(8,'eng','Create document');

INSERT INTO permission_sets (set_id, description) VALUES(0,'Full');
INSERT INTO permission_sets (set_id, description) VALUES(1,'Begränsad 1');
INSERT INTO permission_sets (set_id, description) VALUES(2,'Begränsad 2');
INSERT INTO permission_sets (set_id, description) VALUES(3,'Läs');

INSERT INTO browsers (browser_id, name, user_agent, value) VALUES(1,'Internet Explorer','%MSIE%',2);
INSERT INTO browsers (browser_id, name, user_agent, value) VALUES(2,'Netscape','Mozilla%(%;%[UIN][);]%',2);
INSERT INTO browsers (browser_id, name, user_agent, value) VALUES(3,'Internet Explorer 3','%MSIE 3%',4);
INSERT INTO browsers (browser_id, name, user_agent, value) VALUES(4,'Internet Explorer 4','%MSIE 4%',4);
INSERT INTO browsers (browser_id, name, user_agent, value) VALUES(5,'Internet Explorer 5','%MSIE 5%',4);
INSERT INTO browsers (browser_id, name, user_agent, value) VALUES(6,'Internet Explorer 6','%MSIE 6%',4);
INSERT INTO browsers (browser_id, name, user_agent, value) VALUES(7,'Netscape 3','Mozilla/3%(%;%[UIN][ );]%',4);
INSERT INTO browsers (browser_id, name, user_agent, value) VALUES(8,'Netscape 4','Mozilla/4%(%;%[UIN][ );]%',4);
INSERT INTO browsers (browser_id, name, user_agent, value) VALUES(9,'Netscape 6','Mozilla/5%(%;%[UIN][ );]%',4);
INSERT INTO browsers (browser_id, name, user_agent, value) VALUES(10,'Netscape 7','Mozilla%/5;%netscape/7%',4);
INSERT INTO browsers (browser_id, name, user_agent, value) VALUES(11,'Windows','%win%',1);
INSERT INTO browsers (browser_id, name, user_agent, value) VALUES(12,'Macintosh','%mac%',1);

INSERT INTO browsers (browser_id, name, user_agent, value) VALUES(13,'Windows Internet Explorer','%MSIE%win%',3);
INSERT INTO browsers (browser_id, name, user_agent, value) VALUES(14,'Windows Internet Explorer 3','%MSIE 3%win%',5);
INSERT INTO browsers (browser_id, name, user_agent, value) VALUES(15,'Windows Internet Explorer 4','%MSIE 4%win%',5);
INSERT INTO browsers (browser_id, name, user_agent, value) VALUES(16,'Windows Internet Explorer 5.0','%MSIE 5.0%win%',5);
INSERT INTO browsers (browser_id, name, user_agent, value) VALUES(17,'Windows Internet Explorer 5.5','%MSIE 5.5%win%',5);
INSERT INTO browsers (browser_id, name, user_agent, value) VALUES(18,'Windows Internet Explorer 6','%MSIE 6%win%',5);

INSERT INTO browsers (browser_id, name, user_agent, value) VALUES(19,'Windows Netscape','Mozilla%(%win%;%[UIN][ );]%',3);
INSERT INTO browsers (browser_id, name, user_agent, value) VALUES(20,'Windows Netscape 3','Mozilla/3%(%win%;%[UIN][ );]%',5);
INSERT INTO browsers (browser_id, name, user_agent, value) VALUES(21,'Windows Netscape 4','Mozilla/4%(%win%;%[UIN][ );]%',5);
INSERT INTO browsers (browser_id, name, user_agent, value) VALUES(22,'Windows Netscape 6','Mozilla/5%(%win%;%[UIN][ );]%',5);
INSERT INTO browsers (browser_id, name, user_agent, value) VALUES(23,'Windows Netscape 7','Mozilla/5%(%win%netscape/7%',5);

INSERT INTO browsers (browser_id, name, user_agent, value) VALUES(24,'Macintosh Internet Explorer','%MSIE%mac%',3);
INSERT INTO browsers (browser_id, name, user_agent, value) VALUES(25,'Macintosh Internet Explorer 3','%MSIE 3%mac%',5);
INSERT INTO browsers (browser_id, name, user_agent, value) VALUES(26,'Macintosh Internet Explorer 4','%MSIE 4%mac%',5);
INSERT INTO browsers (browser_id, name, user_agent, value) VALUES(27,'Macintosh Internet Explorer 5','%MSIE 5%mac%',5);

INSERT INTO browsers (browser_id, name, user_agent, value) VALUES(28,'Macintosh Netscape','Mozilla%(%mac%;%[UIN][ );]%',3);
INSERT INTO browsers (browser_id, name, user_agent, value) VALUES(29,'Macintosh Netscape 3','Mozilla/3%(%mac%;%[UIN][ );]%',5);
INSERT INTO browsers (browser_id, name, user_agent, value) VALUES(30,'Macintosh Netscape 4','Mozilla/4%(%mac%;%[UIN][ );]%',5);
INSERT INTO browsers (browser_id, name, user_agent, value) VALUES(31,'Macintosh Netscape 6','Mozilla/5%(%mac%;%[UIN][ );]%',5);

INSERT INTO sys_types (type_id,name) VALUES(0,'StartDocument');
INSERT INTO sys_types (type_id,name) VALUES(1,'SessionCounter');
INSERT INTO sys_types (type_id,name) VALUES(2,'SessionCounterDate');
INSERT INTO sys_types (type_id,name) VALUES(3,'SystemMessage');
INSERT INTO sys_types (type_id,name) VALUES(4,'ServerMaster');
INSERT INTO sys_types (type_id,name) VALUES(5,'ServerMasterAddress');
INSERT INTO sys_types (type_id,name) VALUES(6,'WebMaster');
INSERT INTO sys_types (type_id,name) VALUES(7,'WebMasterAddress');

INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(0,'Annan...','other','swe');
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(1,'Vanlig text','text/plain','swe');
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(2,'HTML-dokument','text/html','swe');
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(3,'Binärfil','application/octet-stream','swe');
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(4,'Shockwave Flash','application/x-shockwave-flash','swe');
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(5,'Shockwave Director','application/x-director','swe');
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(6,'PNG-bild','image/png','swe');
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(7,'GIF-bild','image/gif','swe');
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(8,'JPEG-bild','image/jpeg','swe');
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(9,'Adobe Acrobat-dokument','application/pdf','swe');
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(10,'Wav-ljud','audio/x-wav','swe');
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(11,'Zip-fil','application/zip','swe');
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(12,'AVI-film','video/x-msvideo','swe');
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(13,'Quicktime-film','video/quicktime','swe');
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(14,'MPEG-film','video/mpeg','swe');
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(15,'MS Word-dokument','application/msword','swe');
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(16,'MS Excel-dokument','application/vnd.ms-excel','swe');
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(17,'MS Powerpoint-dokument','application/vnd.ms-powerpoint','swe');

INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(0,'Other...','other','eng');
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(1,'Plain text','text/plain','eng');
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(2,'HTML-document','text/html','eng');
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(3,'Binary file','application/octet-stream','eng');
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(4,'Shockwave Flash','application/x-shockwave-flash','eng');
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(5,'Shockwave Director','application/x-director','eng');
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(6,'PNG-image','image/png','eng');
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(7,'GIF-image','image/gif','eng');
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(8,'JPEG-image','image/jpeg','eng');
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(9,'Adobe Acrobat-document','application/pdf','eng');
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(10,'Wav-sound','audio/x-wav','eng');
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(11,'Zip-file','application/zip','eng');
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(12,'AVI-movie','video/x-msvideo','eng');
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(13,'Quicktime-movie','video/quicktime','eng');
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(14,'MPEG-movie','video/mpeg','eng');
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(15,'MS Word-document','application/msword','eng');
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(16,'MS Excel-document','application/vnd.ms-excel','eng');
INSERT INTO mime_types (mime_id, mime_name, mime, lang_prefix) VALUES(17,'MS Powerpoint-document','application/vnd.ms-powerpoint','eng');

INSERT INTO phonetypes VALUES(0, 'Annat', 1 );
INSERT INTO phonetypes VALUES(1, 'Bostad', 1 );
INSERT INTO phonetypes VALUES(2, 'Arbete', 1 );
INSERT INTO phonetypes VALUES(3, 'Mobil', 1 );
INSERT INTO phonetypes VALUES(4, 'Fax', 1 );
INSERT INTO phonetypes VALUES(0, 'Other', 2 );
INSERT INTO phonetypes VALUES(1, 'Home', 2 );
INSERT INTO phonetypes VALUES(2, 'Work', 2 );
INSERT INTO phonetypes VALUES(3, 'Mobile', 2 );
INSERT INTO phonetypes VALUES(4, 'Fax', 2 );

-- mssql SET IDENTITY_INSERT sys_data ON
INSERT INTO sys_data (sys_id, type_id, value) VALUES(0, 0, 1001);
INSERT INTO sys_data (sys_id, type_id, value) VALUES(1, 1, 0);
INSERT INTO sys_data (sys_id, type_id, value) VALUES(2, 2,
-- mysql NOW()
-- mssql CONVERT(CHAR(10),GETDATE(),120)
);
INSERT INTO sys_data (sys_id, type_id, value) VALUES(3, 3, '');
INSERT INTO sys_data (sys_id, type_id, value) VALUES(4, 4, '');
INSERT INTO sys_data (sys_id, type_id, value) VALUES(5, 5, '');
INSERT INTO sys_data (sys_id, type_id, value) VALUES(6, 6, '');
INSERT INTO sys_data (sys_id, type_id, value) VALUES(7, 7, '');
-- mssql SET IDENTITY_INSERT sys_data OFF

-- mssql SET IDENTITY_INSERT users ON
INSERT INTO users (user_id, login_name, login_password, first_name, last_name,
                   title, company, address, city, zip, country, county_council, email,
                   external, active, create_date, language)
VALUES (1,'admin', 'admin', 'Admin', 'Super','','','','','','','','',0,1,
-- mysql NOW()
-- mssql GETDATE()
,'@language@');
INSERT INTO users (user_id, login_name, login_password, first_name, last_name,
                   title, company, address, city, zip, country, county_council, email,
                   external, active, create_date, language)
VALUES (2,'user', 'user', 'User', 'Extern','','','','','','','','',0,1,
-- mysql NOW()
-- mssql GETDATE()
,'@language@');
-- mssql SET IDENTITY_INSERT users OFF

-- mssql SET IDENTITY_INSERT roles ON
INSERT INTO roles (role_id, role_name, permissions, admin_role) VALUES(0, 'Superadmin', 0, 1);
INSERT INTO roles (role_id, role_name, permissions, admin_role) VALUES(1, 'Useradmin', 0, 2);
INSERT INTO roles (role_id, role_name, permissions, admin_role) VALUES(2, 'Users', 1, 0);
-- mssql SET IDENTITY_INSERT roles OFF

INSERT INTO user_roles_crossref VALUES(1,0);
INSERT INTO user_roles_crossref VALUES(2,2);

-- mssql SET IDENTITY_INSERT meta ON
INSERT INTO meta (meta_id, doc_type, meta_headline,                meta_text, meta_image, owner_id, permissions, shared, show_meta, lang_prefix,         date_created,                    date_modified,                   disable_search, archived_datetime, target,  activate, status, publication_start_datetime,      publication_end_datetime)
 VALUES (1001,   2,        '@headline@',  '',        '',         1,        0,           0,      0,         '@language@',
-- mysql NOW()
-- mssql GETDATE()
,
-- mysql NOW()
-- mssql GETDATE()
, 0,              null,              '_self', 1,        2,
-- mysql NOW()
-- mssql GETDATE()
, null);
-- mssql SET IDENTITY_INSERT meta OFF

-- mssql SET IDENTITY_INSERT templategroups ON
INSERT INTO templategroups (group_id, group_name) VALUES (0, 'normal');
-- mssql SET IDENTITY_INSERT templategroups OFF

INSERT INTO templates_cref VALUES(0,'demo');

INSERT INTO text_docs VALUES (1001, 'demo', 0, NULL, NULL, NULL);

INSERT INTO roles_rights VALUES (2,1001,3);

INSERT INTO texts (meta_id, name, text, type) VALUES( 1001, 1, '@text1@',1);
INSERT INTO texts (meta_id, name, text, type) VALUES( 1001, 2, '@text2@',1);

INSERT INTO texts_history (meta_id, name, text, type, modified_datetime, user_id) VALUES( 1001, 1, '@text1@',1,
-- mysql NOW()
-- mssql GETDATE()
,1);
INSERT INTO texts_history (meta_id, name, text, type, modified_datetime, user_id) VALUES( 1001, 2, '@text2@',1,
-- mysql NOW()
-- mssql GETDATE()
,1);

INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , align , alt_text , low_scr , imgurl , linkurl, type )
values (1001,0,0,0,0,0,3,'','_blank','top','','','imCMSpower.gif','http://www.imcms.net','0');
