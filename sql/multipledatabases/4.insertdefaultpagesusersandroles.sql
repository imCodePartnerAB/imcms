INSERT INTO sys_data (sys_id, type_id, sysdata_value) VALUES(0, 0, '1001');
INSERT INTO sys_data (sys_id, type_id, sysdata_value) VALUES(1, 1, '0');
INSERT INTO sys_data (sys_id, type_id, sysdata_value) VALUES(8, 2, CAST(CURRENT_TIMESTAMP AS CHAR(80)));
INSERT INTO sys_data (sys_id, type_id, sysdata_value) VALUES(3, 3, '');
INSERT INTO sys_data (sys_id, type_id, sysdata_value) VALUES(4, 4, '@servermaster-name@');
INSERT INTO sys_data (sys_id, type_id, sysdata_value) VALUES(5, 5, '@servermaster-email@');
INSERT INTO sys_data (sys_id, type_id, sysdata_value) VALUES(6, 6, '@webmaster-name@');
INSERT INTO sys_data (sys_id, type_id, sysdata_value) VALUES(7, 7, '@webmaster-email@');

INSERT INTO users VALUES (1,'admin', 'admin', 'Admin', 'Super','','','','','','','','',0,1001,0,1,1,1,CURRENT_TIMESTAMP);
INSERT INTO users VALUES (2,'user', 'user', 'User', 'User','','','','','','','','',0,1001,0,1,1,1,CURRENT_TIMESTAMP);

INSERT INTO roles VALUES(0, 'Superadmin', 0, 1);
INSERT INTO roles VALUES(1, 'Useradmin', 0, 2);
INSERT INTO roles VALUES(2, 'Users', 1, 0);

INSERT INTO user_roles_crossref VALUES(1,0);
INSERT INTO user_roles_crossref VALUES(2,2);

INSERT INTO meta (meta_id, description, doc_type, meta_headline, meta_text, meta_image, owner_id, permissions, shared, expand, show_meta, help_text_id, archive, status_id, lang_prefix, classification, date_created, date_modified, sort_position, menu_position, disable_search, activated_datetime, archived_datetime, target,  frame_name, activate)
 VALUES           (1001,    '',          2,        'Startsidan',  '',        '',         1,        0,           0,      1,      0,         1,            0,       1,         'se',        '',             CURRENT_TIMESTAMP,    CURRENT_TIMESTAMP,     1,             1,             0,              CURRENT_TIMESTAMP,   null,        '_self',  '',         1);

INSERT INTO templates VALUES (1,'start.html', 'Start', 'se', 1,1,1);
INSERT INTO templategroups VALUES (0, 'Start');
INSERT INTO templates_cref VALUES(0,1);
INSERT INTO templates VALUES (6,'rr_start.html', 'ReadrunnerStart', 'se', 1,1,1);
INSERT INTO templategroups VALUES (1, 'Readrunner');
INSERT INTO templates_cref VALUES(1,6);
INSERT INTO templates  VALUES (7,'poll_form_template.html', 'poll_form_template', 'se', 0,0,0);
INSERT INTO templategroups VALUES (2, 'Example-templates');
INSERT INTO templates_cref VALUES(2,7);
INSERT INTO templates  VALUES (8,'poll_result_default_template.html', 'poll_result_default_template', 'se', 0,0,0);
INSERT INTO templates_cref VALUES(2,8);
INSERT INTO templates  VALUES (9,'poll_confirmation_template.html', 'poll_confirmation_template', 'se', 0,0,0);
INSERT INTO templates_cref VALUES(2,9);

INSERT INTO text_docs VALUES (1001, 1, 0, 1, -1, -1);
INSERT INTO roles_rights VALUES (2,1001,3);
INSERT INTO texts VALUES( 1001, 1, '<h2>imCode imCMS</h2><br><a href="@loginurl@/">Logga in!</a>',1,1);
INSERT INTO images ( meta_id , width , height , border , v_space , h_space , name , image_name , target , target_name , align , alt_text , low_scr , imgurl , linkurl )
VALUES (1001,100,29,0,0,0,3,'','_blank','','top','','','imCMSpower.gif','http://www.imcms.net');

UPDATE unique_keys SET key_value = 1001 WHERE table_name = 'meta' AND column_name = 'meta_id';
UPDATE unique_keys SET key_value = 2 WHERE table_name = 'users' AND column_name = 'user_id';
UPDATE unique_keys SET key_value = 2 WHERE table_name = 'roles' AND column_name = 'role_id';