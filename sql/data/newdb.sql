-- mssql SET IDENTITY_INSERT sys_data ON
INSERT INTO sys_data (sys_id, type_id, value) VALUES(0, 0, 1001);
INSERT INTO sys_data (sys_id, type_id, value) VALUES(1, 1, 0);
INSERT INTO sys_data (sys_id, type_id, value) VALUES(2, 2,
-- mysql NOW()
-- mssql CONVERT(CHAR(10),GETDATE(),120)
);
INSERT INTO sys_data (sys_id, type_id, value) VALUES(3, 3, '');
INSERT INTO sys_data (sys_id, type_id, value) VALUES(4, 4, '@servermaster-name@');
INSERT INTO sys_data (sys_id, type_id, value) VALUES(5, 5, '@servermaster-email@');
INSERT INTO sys_data (sys_id, type_id, value) VALUES(6, 6, '@webmaster-name@');
INSERT INTO sys_data (sys_id, type_id, value) VALUES(7, 7, '@webmaster-email@');
-- mssql SET IDENTITY_INSERT sys_data OFF

-- mssql SET IDENTITY_INSERT users ON
INSERT INTO users (user_id, login_name, login_password, first_name, last_name,
                   title, company, address, city, zip, country, county_council, email,
                   external, active, create_date, language)
VALUES (1,'admin', 'admin', 'Admin', 'Super','','','','','','','','@servermaster-email@',0,1,
-- mysql NOW()
-- mssql GETDATE()
,'<? sql/default_lang ?>');
INSERT INTO users (user_id, login_name, login_password, first_name, last_name,
                   title, company, address, city, zip, country, county_council, email,
                   external, active, create_date, language)
VALUES (2,'user', 'user', 'User', 'Extern','','','','','','','','',0,1,
-- mysql NOW()
-- mssql GETDATE()
,'<? sql/default_lang ?>');
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
 VALUES (1001,   2,        '<? sql/sql/newdb.sql/headline_1001 ?>',  '',        '',         1,        0,           0,      0,         '@language@',
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

INSERT INTO templates VALUES (1,'demo.html', 'demo', '<? sql/default_lang ?>', 1,1,1);

INSERT INTO templategroups VALUES (0, 'normal');

INSERT INTO templates_cref VALUES(0,1);

INSERT INTO text_docs VALUES (1001, 1, 0, -1, -1, NULL);

INSERT INTO roles_rights VALUES (2,1001,3);

INSERT INTO texts (meta_id, name, text, type) VALUES( 1001, 1, '<? sql/sql/newdb.sql/text_1001_1 ?>',1);
INSERT INTO texts (meta_id, name, text, type) VALUES( 1001, 2, '<? sql/sql/newdb.sql/text_1001_2 ?>',1);

INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , align , alt_text , low_scr , imgurl , linkurl, type )
values (1001,0,0,0,0,0,3,'','_blank','top','','','imCMSpower.gif','http://www.imcms.net','0');
