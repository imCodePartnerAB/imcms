SET IDENTITY_INSERT sys_data ON 
INSERT INTO sys_data (sys_id, type_id, value) 
 VALUES(1, 1, 0)
INSERT INTO sys_data (sys_id, type_id, value) 
 VALUES(2, 2, convert(char(10),getDate(),120))
INSERT INTO sys_data (sys_id, type_id, value) 
 VALUES(3, 3, '')
INSERT INTO sys_data (sys_id, type_id, value) 
 VALUES(4, 4, '@servermaster-name@')
INSERT INTO sys_data (sys_id, type_id, value)
 VALUES(5, 5, '@servermaster-email@')
INSERT INTO sys_data (sys_id, type_id, value)
 VALUES(6, 6, '@webmaster-name@')
INSERT INTO sys_data (sys_id, type_id, value)
 VALUES(7, 7, '@webmaster-email@')
SET IDENTITY_INSERT sys_data OFF
INSERT INTO users 
 VALUES (1,'admin', 'admin', 'Admin', 'Super','','','','','','','','',0,1001,0,1,1,1,convert(char(10),getDate(),120))INSERT INTO users VALUES (2,'user', 'user', 'User', 'Extern','','','','','','','','',0,1001,0,1,1,1,convert(char(10),getDate(),120))
INSERT INTO roles 
 VALUES(0, 'Superadmin',0)
INSERT INTO roles 
 VALUES(1, 'Users',1)
INSERT INTO user_roles_crossref
 VALUES(1,0)
INSERT INTO user_roles_crossref
 VALUES(2,1)
SET IDENTITY_INSERT meta ON
INSERT INTO meta (meta_id, description, doc_type, meta_headline, meta_text, meta_image, owner_id, permissions, shared, expand, show_meta, help_text_id, archive, status_id, lang_prefix, classification, date_created, date_modified, sort_position, menu_position, disable_search, activated_datetime, archived_datetime, target,  frame_name, activate)
 VALUES           (1001,    '',          2,        'Startsidan',  '',        '',         1,        0,           0,      1,      0,         1,            0,       1,         'se',        '',             convert(char(10),getDate(),120),    convert(char(10),getDate(),120),     1,             1,             0,              convert(char(10),getDate(),120),   null,        '_self',  '',         1)
SET IDENTITY_INSERT meta OFF
INSERT INTO templates 
 VALUES (1,'start.html', 'Start', 'se', 1,1,1)
INSERT INTO templategroups
 VALUES (0, 'Start')
INSERT INTO templates_cref
 VALUES(0,1)
INSERT INTO text_docs
 VALUES (1001, 1, 0, 1, -1, -1)
INSERT INTO roles_rights
 VALUES (1,1001,3)
INSERT INTO texts
 VALUES( 1001, 1, '<h2>imCode imCMS</h2><br><a href="@loginurl@/">Logga in!</a>',1)
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
values (1001,100,29,0,0,0,3,'','_blank','','top','','','imCMSpower.gif','http://www.imcms.net')
