INSERT INTO meta (meta_id, description, doc_type, meta_headline, meta_text, meta_image, owner_id, permissions, shared, expand, show_meta, help_text_id, archive, status_id, lang_prefix, classification, date_created, date_modified, sort_position, menu_position, disable_search, activated_datetime, archived_datetime, target,  frame_name, activate)
 VALUES           (9001,    '',          2,        'Text-dok',  '',        '',         1,        0,           0,      1,      0,         1,            0,       1,         'se',        '',             CURRENT_TIMESTAMP,    CURRENT_TIMESTAMP,     1,             1,             0,              CURRENT_TIMESTAMP,   null,        '_self',  '',         1);
INSERT INTO text_docs VALUES (9001, 1, 0, 1, -1, -1);
INSERT INTO texts VALUES( 9001, 1, 'Text 1 på text-dokument 9001',0,2);
INSERT INTO images VALUES (9001,100,29,0,0,0,3,'','_blank','','top','','','imCMSpower.gif','http://www.imcms.net');
INSERT INTO classification VALUES(1,'testsida');
INSERT INTO meta_classification VALUES(9001,1);

INSERT INTO meta (meta_id, description, doc_type, meta_headline, meta_text, meta_image, owner_id, permissions, shared, expand, show_meta, help_text_id, archive, status_id, lang_prefix, classification, date_created, date_modified, sort_position, menu_position, disable_search, activated_datetime, archived_datetime, target,  frame_name, activate)
 VALUES           (9002,    '',          2,        'Text-dok',  '',        '',         1,        0,           0,      1,      0,         1,            0,       1,         'se',        '',             CURRENT_TIMESTAMP,    CURRENT_TIMESTAMP,     1,             1,             0,              CURRENT_TIMESTAMP,   null,        '_self',  '',         1);
INSERT INTO text_docs VALUES (9002, 1, 0, 1, -1, -1);

INSERT INTO includes VALUES (9001,1,9002);

INSERT INTO childs (meta_id, to_meta_id, menu_sort, manual_sort_order)
 VALUES (9001, 9002,1,500);

INSERT INTO meta (meta_id, description, doc_type, meta_headline, meta_text, meta_image, owner_id, permissions, shared, expand, show_meta, help_text_id, archive, status_id, lang_prefix, classification, date_created, date_modified, sort_position, menu_position, disable_search, activated_datetime, archived_datetime, target,  frame_name, activate)
 VALUES           (9003,    '',          8,        'Fil-dok',   '',        '',         1,        0,           0,      1,      0,         1,            0,       1,         'se',        '',             CURRENT_TIMESTAMP,    CURRENT_TIMESTAMP,     1,             1,             0,              CURRENT_TIMESTAMP,   null,        '_self',  '',         1);
INSERT INTO fileupload_docs VALUES (9003, 'testfilename.txt', 'text/plain');

INSERT INTO meta (meta_id, description, doc_type, meta_headline, meta_text, meta_image, owner_id, permissions, shared, expand, show_meta, help_text_id, archive, status_id, lang_prefix, classification, date_created, date_modified, sort_position, menu_position, disable_search, activated_datetime, archived_datetime, target,  frame_name, activate)
 VALUES           (9004,    '',          5,        'URL-dok',   '',        '',         1,        0,           0,      1,      0,         1,            0,       1,         'se',        '',             CURRENT_TIMESTAMP,    CURRENT_TIMESTAMP,     1,             1,             0,              CURRENT_TIMESTAMP,   null,        '_self',  '',         1);
INSERT INTO url_docs VALUES(9004,'','','http://www.google.com/','','se');

INSERT INTO meta (meta_id, description, doc_type, meta_headline, meta_text, meta_image, owner_id, permissions, shared, expand, show_meta, help_text_id, archive, status_id, lang_prefix, classification, date_created, date_modified, sort_position, menu_position, disable_search, activated_datetime, archived_datetime, target,  frame_name, activate)
 VALUES           (9005,    '',          6,        'Browserdok','',        '',         1,        0,           0,      1,      0,         1,            0,       1,         'se',        '',             CURRENT_TIMESTAMP,    CURRENT_TIMESTAMP,     1,             1,             0,              CURRENT_TIMESTAMP,   null,        '_self',  '',         1);
INSERT INTO browser_docs VALUES(9005, 9001, 0);
INSERT INTO browser_docs VALUES(9005, 9002, 2);

INSERT INTO meta (meta_id, description, doc_type, meta_headline, meta_text, meta_image, owner_id, permissions, shared, expand, show_meta, help_text_id, archive, status_id, lang_prefix, classification, date_created, date_modified, sort_position, menu_position, disable_search, activated_datetime, archived_datetime, target,  frame_name, activate)
 VALUES           (9006,    '',          7,        'HTML-dok',  '',        '',         1,        0,           0,      1,      0,         1,            0,       1,         'se',        '',             CURRENT_TIMESTAMP,    CURRENT_TIMESTAMP,     1,             1,             0,              CURRENT_TIMESTAMP,   null,        '_self',  '',         1);
INSERT INTO frameset_docs VALUES (9006, '<html><head><title>HTML-dokument</title></head><body><h1>HTML-dokument</h1></body></html>');

INSERT INTO meta (meta_id, description, doc_type, meta_headline, meta_text, meta_image, owner_id, permissions, shared, expand, show_meta, help_text_id, archive, status_id, lang_prefix, classification, date_created, date_modified, sort_position, menu_position, disable_search, activated_datetime, archived_datetime, target,  frame_name, activate)
 VALUES           (9999,    '',          2,        'Testsida',  '',        '',         1,        0,           0,      1,      0,         1,            0,       1,         'se',        '',             CURRENT_TIMESTAMP,    CURRENT_TIMESTAMP,     1,             1,             0,              CURRENT_TIMESTAMP,   null,        '_self',  '',         1);
INSERT INTO text_docs VALUES (9999, 1, 0, 1, -1, -1);

INSERT INTO users VALUES (3,'TestUser', 'TestUser', 'TestUser', 'TestUser','','','','','','','','',0,1001,0,1,1,1,CURRENT_TIMESTAMP);
INSERT INTO roles VALUES(3, 'TestRole', 1, 0);
INSERT INTO phones VALUES (1, '666-666', 3, 0);
INSERT INTO user_roles_crossref VALUES(3,3);
INSERT INTO user_roles_crossref VALUES(3,2);

INSERT INTO roles_rights VALUES(3,9001,1);
INSERT INTO doc_permission_sets VALUES(9001,1,8);
INSERT INTO doc_permission_sets_ex VALUES(9001,1,8,2);
INSERT INTO new_doc_permission_sets VALUES(9001,1,8);
INSERT INTO new_doc_permission_sets_ex VALUES(9001,1,8,2);

